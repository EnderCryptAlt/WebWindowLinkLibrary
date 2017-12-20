package net.ddns.endercrypt.webwindowlink.server.socket.transfer;

import org.json.JSONObject;

public class SocketMessage
{
	private final JSONObject json;

	private final SocketMessageType type;
	private final JSONObject jsonData;

	public SocketMessage(SocketMessageType type, String jsonData)
	{
		this(type, new JSONObject(jsonData));
	}

	public SocketMessage(SocketMessageType type, JSONObject jsonData)
	{
		this(new JSONObject().put("type", type.ordinal()).put("data", jsonData));
	}

	public SocketMessage(JSONObject json)
	{
		this.json = json;
		this.type = SocketMessageType.values()[json.getInt("type")];
		this.jsonData = json.getJSONObject("data");
	}

	public JSONObject getJson()
	{
		return json;
	}

	public SocketMessageType getType()
	{
		return type;
	}

	public JSONObject getJsonData()
	{
		return jsonData;
	}

	public SocketEvent deriveSocketEvent()
	{
		if (getType() != SocketMessageType.EVENT)
		{
			throw new IllegalArgumentException("cannot derive EVENT type from " + getType());
		}
		System.out.println(json);
		String name = getJsonData().getString("name");
		JSONObject jsonEventObject = getJsonData().getJSONObject("data");
		return new SocketEvent(name, jsonEventObject);
	}
}
