package net.ddns.endercrypt.webwindowlink;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD.Response;
import net.ddns.endercrypt.webwindowlink.server.UriArray;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebRequest;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebResponse;

public interface WebWindowLinkCallback
{
	public void onReady();

	public WebResponse handleHtmlRequest(WebRequest request);

	public Response handleRawRequest(WebRequest request, UriArray uriArray) throws IOException;

	public void onDisconnect();

	public void onShutdown();
}
