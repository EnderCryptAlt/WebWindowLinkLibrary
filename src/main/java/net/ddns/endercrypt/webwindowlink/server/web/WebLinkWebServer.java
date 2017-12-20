package net.ddns.endercrypt.webwindowlink.server.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;

import fi.iki.elonen.NanoHTTPD;
import net.ddns.endercrypt.webwindowlink.WebLinkUtil;
import net.ddns.endercrypt.webwindowlink.WebWindowLinkApplication;
import net.ddns.endercrypt.webwindowlink.WebWindowLinkCallback;
import net.ddns.endercrypt.webwindowlink.server.UriArray;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebRequest;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebResponse;

public class WebLinkWebServer extends NanoHTTPD
{
	private final static Logger LOGGER = Logger.getLogger(WebLinkWebServer.class.getName());

	private static Map<UriArray, String> resources = new HashMap<>();
	static
	{
		resources.put(new UriArray("/"), "default/index.html");
		resources.put(new UriArray("/WebEngine/index.js"), "default/index.js");
	}

	private WebWindowLinkApplication application;
	private WebWindowLinkApplication.Instance instance;

	public WebLinkWebServer(WebWindowLinkApplication application, WebWindowLinkApplication.Instance instance, int port)
	{
		super(port);
		this.application = application;
		this.instance = instance;
		this.instance.key = UUID.randomUUID().toString();
	}

	@Override
	public Response serve(IHTTPSession session)
	{
		try
		{
			Method method = session.getMethod();
			UriArray uriArray = new UriArray(session.getUri());
			LOGGER.info("recieved HTTP request METHOD: " + method + " at " + uriArray);
			String resourcePath = resources.get(uriArray);
			if (resourcePath == null)
			{
				return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "the uri " + uriArray.getFullPath() + " was not found!");
			}
			LOGGER.fine("preparing to respond with resource: " + resourcePath);
			String output = WebLinkUtil.readResourceAsString(resourcePath);
			LOGGER.fine("modifying html for sending...");
			output = assembleOutput(output, new WebRequest().new Builder()
					.importFrom(session)
					.get());
			LOGGER.fine("assembling response object...");
			Response response = NanoHTTPD.newFixedLengthResponse(output);
			LOGGER.fine("preparing headers...");
			String headers = WebLinkUtil.readResourceAsString("headers.txt");
			for (String headerEntry : headers.split("\n"))
			{
				if (headerEntry.length() > 0)
				{
					String[] split = headerEntry.split(":");
					if (split.length == 2)
					{
						String name = split[0];
						String value = split[1].trim();
						if (name.length() > 0 && value.length() > 0)
						{
							LOGGER.finer("Adding header: " + name + " = " + value);
							response.addHeader(name, value);
						}
						else
						{
							LOGGER.warning("Ignoring header: \"" + headerEntry + "\" (reason: name/value too short)");
						}
					}
					else
					{
						LOGGER.warning("Ignoring header: \"" + headerEntry + "\" (reason: bad \":\" count)");
					}
				}
			}
			LOGGER.fine("responding!");
			return response;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Exception occured while trying to build a web response", e);
			return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, ExceptionUtils.getFullStackTrace(e));
		}
	}

	private String assembleOutput(String text, WebRequest webRequest)
	{
		// callback
		WebWindowLinkCallback callback = application.getCallback();
		Objects.requireNonNull(callback);

		// handle
		WebResponse webResponse = callback.handleHtmlRequest(webRequest);

		// key
		text = text.replace("[META_WS_KEY]", instance.key);
		text = text.replace("[META_WS_PORT]", String.valueOf(instance.socketPort));

		// head
		text = text.replace("[HEAD]", webResponse.getHead());

		// body
		text = text.replace("[BODY]", webResponse.getBody());

		// return
		return text;
	}
}
