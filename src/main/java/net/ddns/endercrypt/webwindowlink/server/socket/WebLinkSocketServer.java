package net.ddns.endercrypt.webwindowlink.server.socket;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import net.ddns.endercrypt.webwindowlink.WebWindowLinkApplication;
import net.ddns.endercrypt.webwindowlink.server.socket.transfer.SocketEvent;
import net.ddns.endercrypt.webwindowlink.server.socket.transfer.SocketMessage;
import net.ddns.endercrypt.webwindowlink.server.web.WebLinkWebServer;

public class WebLinkSocketServer extends WebSocketServer
{
	private final static Logger LOGGER = Logger.getLogger(WebLinkWebServer.class.getName());

	private final static ExecutorService executor = Executors.newCachedThreadPool();

	private WebWindowLinkApplication application;
	private WebWindowLinkApplication.Instance instance;

	private List<WebSocket> unAuthorizedWebSockets = new ArrayList<>();

	public WebLinkSocketServer(WebWindowLinkApplication application, WebWindowLinkApplication.Instance instance, int port)
	{
		super(new InetSocketAddress("localhost", port));
		this.application = application;
		this.instance = instance;
	}

	public void send(SocketMessage socketMessage)
	{
		instance.webSockets.forEach(ws -> ws.send(socketMessage.getJson().toString()));
	}

	@Override
	public void onStart()
	{
		// ignore
	}

	@Override
	public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake)
	{
		LOGGER.info("recieved web socket connection from " + webSocket.getLocalSocketAddress().getAddress().getHostAddress());
		unAuthorizedWebSockets.add(webSocket);
	}

	private boolean isAuthorized(WebSocket webSocket)
	{
		return instance.webSockets.contains(webSocket);
	}

	@Override
	public void onMessage(WebSocket webSocket, String message)
	{
		LOGGER.finer("Recieved message: " + message);
		// message
		SocketMessage socketMessage = new SocketMessage(new JSONObject(message));
		// auth
		if (socketMessage.getType().isRequireAuthorized())
		{ // unauthorized request
			boolean isAuthorized = isAuthorized(webSocket);
			if (isAuthorized == false)
			{ // close
				webSocket.close();
				return;
			}
		}
		// handle data
		switch (socketMessage.getType())
		{
		case PING:
			// ignore data
			break;
		case AUTH:
			String key = socketMessage.getJsonData().getString("key");
			// check
			if (instance.key.equals(key))
			{
				// auth websocket
				instance.webSockets.add(webSocket);
				unAuthorizedWebSockets.remove(webSocket);
				// ready
				if (instance.webSockets.size() == 1) // was 0, is now 1
				{
					application.getCallback().onReady();
				}
				// start ping if needed
				/*
				if (instance.pingService.isRunning() == false)
				{
					instance.pingService.start();
				}
				*/
			}
			break;
		case EVENT:
			SocketEvent socketEvent = socketMessage.deriveSocketEvent();
			executor.execute(new Runnable()
			{
				@Override
				public void run()
				{
					application.triggerListener(socketEvent.getName(), socketEvent.getJsonData());
				}
			});
			break;
		}
	}

	@Override
	public void onError(WebSocket webSocket, Exception e)
	{
		LOGGER.log(Level.SEVERE, getClass().getSimpleName() + " onError", e);
	}

	@Override
	public void onClose(WebSocket webSocket, int code, String reason, boolean remove)
	{
		LOGGER.info("disconnected web socket connection from " + webSocket.getLocalSocketAddress().getAddress().getHostAddress());
		instance.webSockets.remove(webSocket);
		if (instance.webSockets.size() == 0)
		{
			// application.stop();
			application.getCallback().onDisconnect();
		}
	}
}
