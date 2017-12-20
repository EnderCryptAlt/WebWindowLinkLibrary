package net.ddns.endercrypt.webwindowlink.exception;

public class NotConfiguredException extends RuntimeException
{

	public NotConfiguredException()
	{
	}

	public NotConfiguredException(String message)
	{
		super(message);
	}

	public NotConfiguredException(Throwable cause)
	{
		super(cause);
	}

	public NotConfiguredException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NotConfiguredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
