
package haui.io;

import java.io.*;

/** Takes a reader and converts it to an input stream which reads in ASCII characters.
    If any characters on not in the ASCII range then an IOException will be thrown to prevent corrupted values
    being read in. This is needed
 by a lot of stupid APIs that use InputStreams for reading text files (eg Properties and XML API) instead of Readers. */

public class ASCIIInputStream extends InputStream
{
	public ASCIIInputStream (Reader in)
	{
		m_In = in;
	}

	public int read ()
		throws IOException
	{
		int n = m_In.read();

		if (n == -1)
			return -1;

		if (n > 255)
			throw new IOException ("Non ASCII character read");

		return n;
	}

	private Reader m_In;
}
