package net.ddns.endercrypt.webwindowlink.server.web.transfer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import net.ddns.endercrypt.webwindowlink.server.UriArray;

public class WebRequest
{
	private UriArray uriArray;
	private Method method;

	public UriArray getUriArray()
	{
		return uriArray;
	}

	public Method getMethod()
	{
		return method;
	}

	public class Builder
	{
		public Builder setUriArray(UriArray uriArray)
		{
			WebRequest.this.uriArray = uriArray;
			return this;
		}

		public Builder setMethod(Method method)
		{
			WebRequest.this.method = method;
			return this;
		}

		public Builder importFrom(IHTTPSession session)
		{
			setUriArray(new UriArray(session.getUri()));
			setMethod(session.getMethod());
			return this;
		}

		public WebRequest get()
		{
			return WebRequest.this;
		}
	}
}
