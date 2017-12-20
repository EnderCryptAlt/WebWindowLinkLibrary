package test;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONObject;

import net.ddns.endercrypt.webwindowlink.JsonEventListener;
import net.ddns.endercrypt.webwindowlink.WebWindowLinkApplication;
import net.ddns.endercrypt.webwindowlink.WebWindowLinkCallback;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebRequest;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebResponse;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		WebWindowLinkApplication webWindowLinkApplication = new WebWindowLinkApplication();

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
		public void onShutdown()
		{
			System.out.println("WebLink was exited, shutting down app");
			System.exit(0);
		}
	}
}
