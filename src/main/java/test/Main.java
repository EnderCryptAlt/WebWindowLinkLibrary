package test;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import net.ddns.endercrypt.webwindowlink.JsonEventListener;
import net.ddns.endercrypt.webwindowlink.WebWindowLinkApplication;
import net.ddns.endercrypt.webwindowlink.WebWindowLinkCallback;
import net.ddns.endercrypt.webwindowlink.server.UriArray;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebRequest;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebResponse;

public class Main
{
	private static WebWindowLinkApplication webWindowLinkApplication;

	public static void main(String[] args) throws IOException
	{
		webWindowLinkApplication = new WebWindowLinkApplication();

		webWindowLinkApplication.setCallback(new WebApp());

		webWindowLinkApplication.start();

		webWindowLinkApplication.addListener("popup", new JsonEventListener()
		{
			@Override
			public void trigger(JSONObject json)
			{
				JOptionPane.showMessageDialog(null, json.getString("text"));
			}
		});

		webWindowLinkApplication.openBrowser();
	}

	private static class WebApp implements WebWindowLinkCallback
	{
		@Override
		public void onReady()
		{

		}

		@Override
		public WebResponse handleHtmlRequest(WebRequest request)
		{
			WebResponse response = new WebResponse();
			response.setHead("<title>HAI</title>");
			response.setBody("<input type='button' value='popup!' onClick=\"sendEvent('popup',{text: document.getElementById('in').value})\"></input><input id='in' value='put text here'></input>");
			return response;
		}

		@Override
		public Response handleRawRequest(WebRequest request, UriArray uriArray)
		{
			// TODO Auto-generated method stub
			return NanoHTTPD.newFixedLengthResponse("request: " + uriArray.getFullPath());
		}

		@Override
		public void onDisconnect()
		{
			System.out.println("WebLink was exited, shutting down app");
			webWindowLinkApplication.stop();
		}

		@Override
		public void onShutdown()
		{
			System.exit(0);
		}
	}
}
