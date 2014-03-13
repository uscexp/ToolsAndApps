
package haui.io;

import java.io.*;

/**
 * A file filter which will only accept files that exist which are also accepted by another filter.
 */
public class ExistsFileFilter implements FileFilter
{
	public ExistsFileFilter (FileFilter filter)
	{
		m_Filter = filter;
	}

	public boolean accept (File file) {return file.exists() && m_Filter.accept(file);}

	private FileFilter m_Filter;
}
