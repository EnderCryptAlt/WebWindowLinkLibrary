package net.ddns.endercrypt.webwindowlink.server.socket.transfer;

public enum SocketMessageType
{
	PING(false), // empty ping message
	AUTH(false), // key strig value to auth the socket
	EVENT(true); // general purpose user data

	private boolean requireAuthorized;

	private SocketMessageType(boolean requireAuthorized)
	{
		this.requireAuthorized = requireAuthorized;
	}

	public boolean isRequireAuthorized()
	{
		return requireAuthorized;
	}
}