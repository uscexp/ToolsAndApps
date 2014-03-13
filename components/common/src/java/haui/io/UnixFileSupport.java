
package haui.io;

import java.util.*;
import java.io.*;

/** Provides a means for getting and setting UNIX file permissions for a file. */

public class UnixFileSupport
{
	private static boolean g_bEnabled;

	static
	{
		g_bEnabled = true;

		try
		{
			System.loadLibrary("unixfilesupport");
		}

		catch (Throwable e)
		{
			System.err.println ("Warning: Unix file support is not enabled: " + e);
			g_bEnabled = false;
		}
	}

	/** Checks if support for UNIX file handling is enabled on the host operating system.
	 This is determined by whether the native library was available for the OS or not. */

	public static boolean isEnabled () {return g_bEnabled;}

	/** Gets the Unix file permissions for the specified file. If the native library could not be loaded
	 an IOException is thrown indicating that Unix file support is not enabled. */

	public static UnixFilePermissions getPermissions (File file)
		throws IOException
	{
		if (g_bEnabled)
			return new UnixFilePermissions(getPermissionsNative(file.getPath()));

		throw new IOException ("Cannot get permissions for file because Unix file support is not enabled on this OS");
	}

	public static void setPermissions (File file, UnixFilePermissions permissions)
		throws IOException
	{
		if (g_bEnabled)
			setPermissionsNative(file.getPath(), permissions.getPermissions());

		else throw new IOException ("Cannot set permissions for file because Unix file support is not enabled on this OS");
	}

	public static void setModificationTime (File file, Date date)
		throws IOException
	{
		if (g_bEnabled)
			setModificationTimeNative (file.getPath(), date.getTime() / 1000);

		else throw new IOException ("Cannot set modification time for file because Unix file support is not enabled on this OS");
	}

	/** Gets the permissions for the specified file. */

	private static native int getPermissionsNative (String sFilePath) throws IOException;

	private static native void setPermissionsNative (String sFilePath, int nPermissions) throws IOException;

	private static native void setModificationTimeNative (String sFilePath, long time) throws IOException;

	/*public static void main (String args[])
		throws IOException
	{
		UnixFileSupport.setModificationTime(new File (args[0]), new Date());
	 }*/
}
