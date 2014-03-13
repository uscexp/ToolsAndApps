package haui.io;

import java.io.*;

/**
 * This class allows an application to create an input stream in which the bytes read are supplied by the contents of a string. <p>This <code>InputStream</code> provides proper <a href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">character encoding</a>.</p> <p>There is a <code>StringBufferInputStream</code> in the <code>java.io</code> package as well, but that one does not provide proper character encoding and therefore it is deprecated.</p>
 * @version  1.0
 */
public class StringBufferInputStream
  extends InputStream
{

  /** the delegate input stream */
  private ByteArrayInputStream in;
  /** the canonical name of the encoding */
  private String encoding;

  /**
   * Creates a new <code>StringBufferInputStream</code> to read
   * data from the specified string. The system's default character
   * encoding is used to translate the characters of the string into
   * bytes.
   *
   * @param str the underlying input buffer
   */
  public StringBufferInputStream( String str )
    throws IOException
  {
    this.init( str, null );
  }

  /**
   * Creates a new <code>StringBufferInputStream</code> to read
   * data from the specified string. This constructor sets the
   * character encoding to be used to translate the characters
   * of the string into bytes.
   *
   * @param str the underlying input buffer
   * @param encoding the name of a supported
   *        <a href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">character
   *        encoding</a>
   */
  public StringBufferInputStream( String str, String encoding )
    throws UnsupportedEncodingException, IOException
  {
    this.init( str, encoding );
  }

  /**
   * Reads the next byte of data from this input stream.
   * The value byte is returned as an <code>int</code>
   * in the range <code>0</code> to <code>255</code>.
   * If no byte is available because the end of the stream
   * has been reached, the value <code>-1</code> is returned.
   */
  public int read()
    throws IOException
  {
    return in.read();
  }

  /**
   * Reads some number of bytes from the input stream and stores
   * them into the buffer array <code>b</code>. The number of bytes
   * actually read is returned as an integer.
   */
  public int read( byte[] b )
    throws IOException
  {
    return in.read( b );
  }

  /**
   * Reads up to <code>len</code> bytes of data into
   * an array of bytes from this input stream.
   *
   * @param b the buffer into which the data is read
   * @param off the start offset of the data
   * @param len the maximum number of bytes read
   * @return the total number of bytes read into the buffer,
   *         or <code>-1</code> if there is no more data because
   *         the end of the stream has been reached
   */
  public int read( byte[] b, int off, int len )
    throws IOException
  {
    return in.read( b, off, len );
  }

  /**
   * Skips <code>n</code> bytes of input from this input stream.
   *
   * @param n the number of bytes to be skipped
   * @return the actual number of bytes skipped
   */
  public long skip( long n )
    throws IOException
  {
    return in.skip( n );
  }

  /**
   * Returns the number of bytes that can be read from this
   * input stream without blocking.
   */
  public int available()
    throws IOException
  {
    return in.available();
  }

  /** Close the stream. */
  public void close()
    throws IOException
  {
    in.close();
  }

  /**
   * Marks the current position in this input stream.
   * This method only works properly if the method
   * <code>markSupported()</code> returns <code>true</code>.
   *
   * @param readlimit the maximum limit of bytes that can be
   *        read before the mark position becomes invalid.
   */
  public void mark( int readlimit )
  {
    in.mark( readlimit );
  }

  /**
   * Repositions this stream to the position at the time
   * the <code>mark()</code> method was last called on this
   * input stream.
   * This method only works properly if the method
   * <code>markSupported()</code> returns <code>true</code>.
   */
  public void reset()
    throws IOException
  {
    in.reset();
  }

  /**
   * Tell whether this stream supports the <code>mark()</code> operation.
   */
  public boolean markSupported()
  {
    return in.markSupported();
  }

  /**
   * Returns the canonical name of the <a href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">character encoding</a> being used by this stream. If this <code>StringBufferInputStream</code> was created with the <code>StringBufferInputStream(String,String)</code> constructor then the returned encoding name, being canonical, may differ from the encoding name passed to the constructor.
   * @return  a <code>String<code> representing the encoding name
   * @uml.property  name="encoding"
   */
  public String getEncoding()
  {
    return this.encoding;
  }

  private void init( String str, String encoding )
    throws IOException
  {
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    OutputStreamWriter w;
    if( encoding == null )
    {
      w = new OutputStreamWriter( b );
    }
    else
    {
      w = new OutputStreamWriter( b, encoding );
    }
    w.write( str );
    w.flush();
    this.in = new ByteArrayInputStream( b.toByteArray() );
    this.encoding = w.getEncoding();
    w.close();
  }
}