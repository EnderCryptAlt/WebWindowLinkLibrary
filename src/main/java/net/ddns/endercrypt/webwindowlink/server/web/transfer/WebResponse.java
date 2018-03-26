package net.ddns.endercrypt.webwindowlink.server.web.transfer;

import java.util.Objects;

import fi.iki.elonen.NanoHTTPD.Response;

public class WebResponse
{
	private String head = "";

	private String body = "";

	private Response response;

	// stringbuilder //

	public void setHead(StringBuilder head)
	{
		setHead(head.toString());
	}

	public void setBody(StringBuilder body)
	{
		setBody(body.toString());
	}

	// set //

	public void setHead(String head)
	{
		Objects.requireNonNull(head);
		this.head = head;
	}

	public void setBody(String body)
	{
		Objects.requireNonNull(body);
		this.body = body;
	}

	/**
	 * Response will override any body/head
	 * @param response
	 */
	public void setResponse(Response response)
	{
		this.response = response;
	}

	// get //

	public String getHead()
	{
		return head;
	}

	public String getBody()
	{
		return body;
	}

	public Response getResponse()
	{
		return response;
	}
}
