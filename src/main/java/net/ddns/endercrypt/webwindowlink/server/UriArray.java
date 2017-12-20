package net.ddns.endercrypt.webwindowlink.server;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UriArray
{
	private static final char SEPARATOR_CHAR = '/';
	private static final String SEPARATOR_STRING = String.valueOf(SEPARATOR_CHAR);

	private final boolean root;
	private final List<String> folders;
	private final Optional<String> file; // is a file, otherwise empty
	private final boolean outside;

	public UriArray(URI uri)
	{
		this(uri.toString());
	}

	public UriArray(URL url)
	{
		this(url.toString());
	}

	public UriArray(String url)
	{
		url = url.trim();
		root = url.startsWith(SEPARATOR_STRING);
		List<String> rawUriArray = Arrays.stream(url.split(SEPARATOR_STRING)).map(u -> u.trim()).collect(Collectors.toList());

		// traverse
		boolean modifiableOutside = false;
		List<String> modifiableFolders = new ArrayList<>();
		for (String uri : rawUriArray)
		{
			// no change
			if (uri.equals("") || url.equals("."))
			{
				continue;
			}
			// move up
			if (uri.equals(".."))
			{
				if (modifiableFolders.size() == 0)
				{
					modifiableOutside = true;
					modifiableFolders.add("..");
				}
				else
				{
					modifiableFolders.remove(modifiableFolders.size() - 1);
				}
				continue;
			}
			// otherwise folder
			modifiableFolders.add(uri);
		}

		//  check if ends with file (choose one of these methods)

		// check if ends with /
		if (url.endsWith(SEPARATOR_STRING))
		{ // folder
			file = Optional.empty();
		}
		else
		{ // file
			file = Optional.of(modifiableFolders.remove(modifiableFolders.size() - 1));
		}

		// check if last has dot and extension
		/* DO NOT REMOVE
		String last = modifiableFolders.remove(modifiableFolders.size() - 1);
		if (last.indexOf('.') < last.length())
		{ // file
			file = Optional.of(last);
		}
		else
		{ // folder
			file = Optional.empty();
			modifiableFolders.add(last);
		}
		*/

		// finalize
		folders = Collections.unmodifiableList(modifiableFolders);
		outside = modifiableOutside;
	}

	public boolean isRoot()
	{
		return root;
	}

	public boolean isFile()
	{
		return file.isPresent();
	}

	public String getFile()
	{
		return file.get();
	}

	public List<String> getFolders()
	{
		return folders;
	}

	public boolean isOutside()
	{
		return outside;
	}

	public String getFolderPath()
	{
		StringBuilder sb = new StringBuilder();
		// add root /
		if (isRoot())
		{
			sb.append(SEPARATOR_CHAR);
		}
		// add all folders
		Iterator<String> folderIterator = folders.iterator();
		while (folderIterator.hasNext())
		{
			String folder = folderIterator.next();
			sb.append(folder);
			sb.append(SEPARATOR_CHAR);
			/*
			if (folderIterator.hasNext())
			{
				sb.append(SEPARATOR_CHAR);
			}
			*/
		}
		// sb.append(SEPARATOR_CHAR); // adds a last / on every folder path
		return sb.toString();
	}

	public String getFullPath()
	{
		String path = getFolderPath();
		if (isFile())
		{
			if (isRoot() == false)
			{
				path = path + SEPARATOR_CHAR;
			}
			path = path + getFile();
		}
		return path;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		if (isFile())
		{
			result += getFile().hashCode();
		}
		for (String folder : getFolders())
		{
			result += folder.hashCode();
		}
		result = prime * (isRoot() ? 6 : 1);
		result = prime * (isFile() ? 7 : 1);
		result = prime * (isOutside() ? 8 : 1);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		UriArray other = (UriArray) obj;
		if (isRoot() != other.isRoot())
			return false;
		if (isFile() != other.isFile())
		{
			return false;
		}
		if (isFile())
		{
			if (getFile().equals(other.getFile()) == false)
			{
				return false;
			}
		}
		if (isOutside() != other.isOutside())
			return false;
		if (folders.size() != other.folders.size())
		{
			return false;
		}
		if (folders.equals(other.folders) == false)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		// marks
		int flags = 0;
		StringBuilder sbMarks = new StringBuilder();
		sbMarks.append(" (");
		if (isRoot())
		{
			flags++;
			sbMarks.append("ROOT");
			sbMarks.append(',');
		}
		if (isFile())
		{
			flags++;
			sbMarks.append("FILE");
			sbMarks.append(',');
		}
		if (isOutside())
		{
			flags++;
			sbMarks.append("OUTSIDE");
			sbMarks.append(',');
		}
		if (flags > 0)
		{
			sbMarks.setLength(sbMarks.length() - 1);
			sbMarks.append(')');
		}
		else
		{
			sbMarks.setLength(0);
		}
		// texts
		String txtPath = "Path: '" + getFolderPath() + "'";
		String txtFile = isFile() ? "File: '" + getFile() + "'" : "";
		return getClass().getSimpleName() + " [" + txtPath + " " + txtFile + "" + sbMarks.toString() + "]";
	}
}
