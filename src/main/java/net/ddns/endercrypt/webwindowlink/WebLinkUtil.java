package net.ddns.endercrypt.webwindowlink;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import net.ddns.endercrypt.webwindowlink.server.web.WebLinkWebServer;

public class WebLinkUtil
{
	private final static Logger LOGGER = Logger.getLogger(WebLinkUtil.class.getName());

	public static int findAvailablePort() throws IOException
	{
		try (ServerSocket serverSocket = new ServerSocket(0))
		{
			return serverSocket.getLocalPort();
		}
	}

	public static String readResourceAsString(String file) throws IOException
	{
		byte[] bytes = readResource(file);
		return new String(bytes);
	}

	public static byte[] readResource(String file) throws IOException
	{
		LOGGER.fine("reading resource: " + file + " ...");
		try (InputStream input = WebLinkWebServer.class.getClassLoader().getResourceAsStream(file))
		{
			if (input == null)
			{
				throw new FileNotFoundException("couldnt not find: " + file);
			}
			byte[] bytes = IOUtils.toByteArray(input);
			return bytes;
		}
		catch (FileNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, "Failed to locate " + file);
			throw new RuntimeException(e);
		}
	}
}
