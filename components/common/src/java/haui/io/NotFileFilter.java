
package haui.io;

import java.io.*;

/**
 * A file filter that accepts files that aren't accepted by a specified file filter and vice versa.
 */
public class NotFileFilter implements FileFilter
{
	public NotFileFilter (FileFilter filter)
	{
		m_Filter = filter;
	}

	public boolean accept (File file) {return !m_Filter.accept(file);}

	private FileFilter m_Filter;
}

