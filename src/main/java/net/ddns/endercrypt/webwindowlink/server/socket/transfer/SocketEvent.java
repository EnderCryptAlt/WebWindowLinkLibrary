package net.ddns.endercrypt.webwindowlink.server.socket.transfer;

import org.json.JSONObject;

public class SocketEvent
{
	private JSONObject json;

	private String name;
	private JSONObject jsonData;

	public SocketEvent(String name, JSONObject jsonData)
	{
		this(new JSONObject().put("name", name).put("data", jsonData));
	}

	public SocketEvent(JSONObject json)
	{
		this.json = json;
		this.name = json.getString("name");
		this.jsonData = json.getJSONObject("data");
	}

	public JSONObject getJson()
	{
		return json;
	}

	public String getName()
	{
		return name;
	}

	public JSONObject getJsonData()
	{
		return jsonData;
	}
}
