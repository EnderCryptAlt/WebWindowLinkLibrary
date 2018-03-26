package net.ddns.endercrypt.webwindowlink;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import net.ddns.endercrypt.tech.activateble.ActivatableClass;
import net.ddns.endercrypt.tech.activateble.ActiveatableInstance;
import net.ddns.endercrypt.webwindowlink.exception.MissingDesktopSupport;
import net.ddns.endercrypt.webwindowlink.exception.NotConfiguredException;
import net.ddns.endercrypt.webwindowlink.server.socket.WebLinkSocketServer;
import net.ddns.endercrypt.webwindowlink.server.socket.transfer.SocketEvent;
import net.ddns.endercrypt.webwindowlink.server.socket.transfer.SocketMessage;
import net.ddns.endercrypt.webwindowlink.server.socket.transfer.SocketMessageType;
import net.ddns.endercrypt.webwindowlink.server.web.WebLinkWebServer;

public class WebWindowLinkApplication extends ActivatableClass<WebWindowLinkApplication.Instance>
{
	private final static Logger LOGGER = Logger.getLogger(WebWindowLinkApplication.class.getName());

	private Optional<WebWindowLinkCallback> callbackOptional = Optional.empty();

	private Map<String, JsonEventListener> listeners = new HashMap<>();

	public WebWindowLinkApplication()
	{

	}

	public void setCallback(WebWindowLinkCallback callback)
	{
		callbackOptional = Optional.of(callback);
		LOGGER.info("callback set to " + callback);
	}

	public WebWindowLinkCallback getCallback()
	{
		return callbackOptional.orElseThrow(() -> new NotConfiguredException("Callbacks are not set"));
	}

	public void addListener(String name, JsonEventListener listener)
	{
		LOGGER.info("added event listener " + listener + " for event " + name);
		listeners.put(name.toLowerCase(), listener);
	}

	public boolean triggerListener(String name, JSONObject jsonObject)
	{
		JsonEventListener listener = listeners.get(name.toLowerCase());
		if (listener != null)
		{
			LOGGER.fine("triggered event listener " + name + " with json: " + jsonObject);
			listener.trigger(jsonObject);
			return true;
		}
		else
		{
			return false;
		}
	}

	public void sendEvent(String name, JSONObject jsonData)
	{
		SocketEvent event = new SocketEvent(name, jsonData);
		send(new SocketMessage(SocketMessageType.EVENT, event.getJson()));
	}

	private void send(SocketMessageType type, JSONObject jsonData)
	{
		send(new SocketMessage(type, jsonData));
	}

	private void send(SocketMessage socketMessage)
	{
		getInstance().webSocketServer.send(socketMessage);
	}

	public void start() throws IOException
	{
		start(new Instance());
	}

	public int getWebPort()
	{
		return getInstance().webPort;
	}

	public int getSocketPort()
	{
		return getInstance().socketPort;
	}

	private URL assembleUrl(String protocol, int port)
	{
		requireRunning();
		try
		{
			return new URL(protocol + "://localhost:" + port);
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public URL getWebUrl()
	{
		return assembleUrl("http", getWebPort());
	}

	public URL getSocketUrl()
	{
		return assembleUrl("ws", getSocketPort());
	}

	public void openBrowser() throws IOException
	{
		if (Desktop.isDesktopSupported() == false)
		{
			throw new MissingDesktopSupport("Desktop support is missing on this device");
		}
		Desktop desktop = Desktop.getDesktop();
		URI uri;
		try
		{
			uri = getWebUrl().toURI();
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
		desktop.browse(uri);
	}

	public class Instance implements ActiveatableInstance
	{
		public int webPort;
		public WebLinkWebServer webLinkWebserver;
		public int socketPort;
		public WebLinkSocketServer webSocketServer;

		public String key;

		public List<WebSocket> webSockets = new ArrayList<>();

		private Thread pingService;

		private Instance()
		{
			pingService = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						while (Thread.currentThread().isInterrupted() == false)
						{
							Thread.sleep(50);
							webSocketServer.send(new SocketMessage(SocketMessageType.PING, "{}"));
						}
					}
					catch (InterruptedException e)
					{
						return;
					}
				}
			});
			/*
			pingService.addListener(3, TimeUnit.SECONDS, false, new PingListener()
			{
				@Override
				public void pingTimeout()
				{
					WebWindowLinkApplication.this.stop();
				}
			});
			*/
		}

		@Override
		public void start() throws Exception
		{
			// webserver
			webPort = WebLinkUtil.findAvailablePort();
			LOGGER.info("Starting WEB on port: " + webPort);
			webLinkWebserver = new WebLinkWebServer(WebWindowLinkApplication.this, this, webPort);
			webLinkWebserver.start();

			//websocket
			socketPort = WebLinkUtil.findAvailablePort();
			LOGGER.info("Starting SOCKET on port: " + socketPort);
			webSocketServer = new WebLinkSocketServer(WebWindowLinkApplication.this, this, socketPort);
			webSocketServer.start();
		}

		@Override
		public void stop() throws IOException, InterruptedException
		{
			// callback
			getCallback().onShutdown();
			// webserver
			webLinkWebserver.stop();
			//websocket
			try
			{
				webSocketServer.stop();
			}
			catch (IOException e)
			{
				// ignore
			}
			// ping service
			//pingService.stop();
			pingService.interrupt();
		}
	}
}
