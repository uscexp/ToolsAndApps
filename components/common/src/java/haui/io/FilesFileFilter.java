
package haui.io;

import java.io.*;

/**
 * A file filter which will only accept files which are also accepted by another filter.
 */
public class FilesFileFilter implements FileFilter
{
	public FilesFileFilter (FileFilter filter)
	{
		m_Filter = filter;
	}

	public boolean accept (File file) {return file.isFile() && m_Filter.accept(file);}

	private FileFilter m_Filter;
}

