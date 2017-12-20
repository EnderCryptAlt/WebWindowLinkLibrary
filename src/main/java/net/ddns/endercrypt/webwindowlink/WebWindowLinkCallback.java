package net.ddns.endercrypt.webwindowlink;

import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebRequest;
import net.ddns.endercrypt.webwindowlink.server.web.transfer.WebResponse;

public interface WebWindowLinkCallback
{
	public void onReady();

	public WebResponse handleHtmlRequest(WebRequest request);

	public void onShutdown();
}
