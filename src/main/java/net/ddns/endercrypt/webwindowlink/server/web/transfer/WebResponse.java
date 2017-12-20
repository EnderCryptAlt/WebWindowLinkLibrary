package net.ddns.endercrypt.webwindowlink.server.web.transfer;

import java.util.Objects;

public class WebResponse
{
	private String head = "";

	private String body = "";

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

	// get //

	public String getHead()
	{
		return head;
	}

	public String getBody()
	{
		return body;
	}
}
