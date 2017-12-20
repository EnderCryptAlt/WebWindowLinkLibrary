package net.ddns.endercrypt.webwindowlink.ping;

@Deprecated
public class PingEntry
{
	private long time;
	private boolean removeOnTrigger;
	private PingListener listener;

	public PingEntry(long time, boolean removeOnTrigger, PingListener listener)
	{
		this.time = time;
		this.removeOnTrigger = removeOnTrigger;
		this.listener = listener;
	}

	public long getTime()
	{
		return time;
	}

	public boolean isRemoveOnTrigger()
	{
		return removeOnTrigger;
	}

	public void triggerListener()
	{
		getListener().pingTimeout();
	}

	public PingListener getListener()
	{
		return listener;
	}
}
