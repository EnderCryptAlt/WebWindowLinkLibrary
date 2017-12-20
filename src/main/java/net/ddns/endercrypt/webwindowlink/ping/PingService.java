package net.ddns.endercrypt.webwindowlink.ping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.ddns.endercrypt.tech.activateble.ActivatableClass;
import net.ddns.endercrypt.tech.activateble.ActiveatableInstance;

@Deprecated
public class PingService extends ActivatableClass<PingService.Instance>
{
	private final static Logger LOGGER = Logger.getLogger(PingService.class.getName());

	private static ExecutorService executorService = Executors.newCachedThreadPool();
	private static int PRECISION = 10;

	private List<PingEntry> listeners = new ArrayList<>();

	public PingService()
	{
		// TODO Auto-generated constructor stub
	}

	public void start()
	{
		start(new Instance());
	}

	public void ping()
	{
		getInstance().ping();
	}

	public void addListener(long time, TimeUnit timeUnit, boolean removeOnTrigger, PingListener listener)
	{
		LOGGER.info("added listener " + listener + " to run every " + time + " " + timeUnit + (removeOnTrigger ? " and only activate once" : "and continuesly trigger"));
		long millis = timeUnit.toMillis(time);
		listeners.add(new PingEntry(millis, removeOnTrigger, listener));
	}

	public void clearListeners()
	{
		listeners.clear();
	}

	public class Instance implements ActiveatableInstance
	{
		private long time;
		private Future<Void> future;

		private Instance()
		{
			ping();
		}

		public void ping()
		{
			if (time > 0)
			{
				LOGGER.fine("ping: " + getTime() + "ms");
			}
			time = System.currentTimeMillis();
		}

		public long getTime()
		{
			return (System.currentTimeMillis() - time);
		}

		@Override
		public void start() throws Exception
		{
			ping();
			future = executorService.submit(new PingTimer());
		}

		@Override
		public void stop() throws Exception
		{
			future.cancel(true);
		}
	}

	public class PingTimer implements Callable<Void>
	{
		@Override
		public Void call() throws Exception
		{
			while (Thread.currentThread().isInterrupted() == false)
			{
				Thread.sleep(PRECISION);
				Iterator<PingEntry> iterator = listeners.iterator();
				while (iterator.hasNext())
				{
					PingEntry pingEntry = iterator.next();
					// check
					if (getInstance().getTime() >= pingEntry.getTime())
					{
						LOGGER.info("timeout at:" + getInstance().getTime() + "ms, activating listener " + pingEntry.getListener());
						// remove
						if (pingEntry.isRemoveOnTrigger())
						{
							LOGGER.info("listener " + pingEntry.getListener() + " was removed due to removeOnTrigger=true");
							iterator.remove();
						}
						// listener
						pingEntry.triggerListener();
					}
				}
			}
			return null;
		}
	}
}
