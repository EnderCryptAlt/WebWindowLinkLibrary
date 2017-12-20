package net.ddns.endercrypt.tech.activateble;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ActivatableClass<T extends ActiveatableInstance>
{
	private final static Logger LOGGER = Logger.getLogger(ActivatableClass.class.getName());

	private Optional<T> activity = Optional.empty();

	protected void start(T instance)
	{
		logInfo("attempting to start...");
		// check
		if (isRunning())
		{
			logInfo("is already running");
			throw new IllegalStateException(getClass().getSimpleName() + " is already running");
		}
		// start
		logInfo("launching start callback...");
		try
		{
			instance.start();
		}
		catch (Exception e)
		{
			logThrowable("failed to start due to exception: ", e);
			throw new StartFailedException(e);
		}
		activity = Optional.of(instance);
		logInfo("started!");
	}

	public boolean isRunning()
	{
		return (activity.isPresent());
	}

	protected void requireRunning()
	{
		if (isRunning() == false)
		{
			throw new IllegalStateException(getClass().getSimpleName() + " is not running");
		}
	}

	protected T getInstance()
	{
		requireRunning();
		return activity.get();
	}

	public void stop()
	{
		logInfo("attempting to stop instance...");
		// check
		if (isRunning() == false)
		{
			logInfo("instance was already stopped already stopped!");
			throw new IllegalStateException(getClass().getSimpleName() + " was already stopped already stopped!");
		}
		// stop
		try
		{
			logInfo("stopping...");
			getInstance().stop();
		}
		catch (Exception e)
		{
			logThrowable("caught exception during stop attempt", e);
			throw new StopException(e);
		}
		finally
		{
			activity = Optional.empty();
			logInfo("stopped!");
		}
	}

	// LOGGING //

	private void logInfo(String text)
	{
		LOGGER.log(Level.INFO, generateLogString(text));
	}

	private void logThrowable(String text, Throwable thrown)
	{
		LOGGER.log(Level.SEVERE, generateLogString(text), thrown);
	}

	private String generateLogString(String text)
	{
		return getClass().getSimpleName() + ": " + text;
	}
}
