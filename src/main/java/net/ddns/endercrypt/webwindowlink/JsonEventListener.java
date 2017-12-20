package net.ddns.endercrypt.webwindowlink;

import org.json.JSONObject;

public interface JsonEventListener
{
	public void trigger(JSONObject json);
}
