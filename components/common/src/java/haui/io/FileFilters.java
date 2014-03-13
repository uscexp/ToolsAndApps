
package haui.io;

import java.io.*;

/** A collection of useful file filters. */

public final class FileFilters
{
	public static final FileFilter ALL = new FileFilter ()
	{
		public boolean accept (File f) {return true;}
	};

	public static final FileFilter DIRECTORIES_ONLY = new FileFilter ()
	{
		public boolean accept (File f) {return f.isDirectory();}
	};

	public static final FileFilter FILES_ONLY = new FileFilter ()
	{
		public boolean accept (File f) {return f.isFile();}
	};

	public static final FileFilter EXISTS = new FileFilter ()
	{
		public boolean accept (File f) {return f.exists();}
	};
}
