package net.ddns.endercrypt.tech.activateble;

public class StopException extends RuntimeException
{

	public StopException()
	{
	}

	public StopException(String message)
	{
		super(message);
	}

	public StopException(Throwable cause)
	{
		super(cause);
	}

	public StopException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StopException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
