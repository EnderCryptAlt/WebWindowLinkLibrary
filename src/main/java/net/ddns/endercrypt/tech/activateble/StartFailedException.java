package net.ddns.endercrypt.tech.activateble;

public class StartFailedException extends RuntimeException
{

	public StartFailedException()
	{
	}

	public StartFailedException(String message)
	{
		super(message);
	}

	public StartFailedException(Throwable cause)
	{
		super(cause);
	}

	public StartFailedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StartFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
