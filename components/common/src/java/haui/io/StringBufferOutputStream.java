package haui.io;

import java.io.*;

/**
 * This class allows an application to create an output stream which stores the bytes written into it and provides them as a <code>String</code>. <p>Use the <code>toString()</code> method of this class to get a <code>String</code> representation of the content of this <code>OutputStream</code>.</p> <p>This <code>OutputStream</code> provides proper <a href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">character encoding</a>.</p>
 * @version  1.0
 */
public class StringBufferOutputStream
  extends OutputStream
{

  /** the delegate */
  private ByteArrayOutputStream out;
  /** the used name of the encoding */
  private String encoding;
  /** the canonical name of the encoding */
  private String realEncoding;

  /**
   * Creates a new <code>StringBufferOutputStream</code> to write
   * data into. The system's default character
   * encoding is used to translate the bytes written into characters
   * of the string returned by the <code>toString()</code> method.
   */
  public StringBufferOutputStream()
    throws IOException
  {
    out = new ByteArrayOutputStream();
    OutputStreamWriter o;
    o = new OutputStreamWriter( new ByteArrayOutputStream() );
    this.realEncoding = o.getEncoding();
    this.encoding = null;
    o.close();
  }

  /**
   * Creates a new <code>StringBufferOutputStream</code> to write
   * data into. This constructor sets the character
   * encoding to be used to translate the bytes written into characters
   * of the string returned by the <code>toString()</code> method.
   *
   * @param encoding the name of a supported
   *        <a href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">character
   *        encoding</a>
   */
  public StringBufferOutputStream( String encoding )
    throws UnsupportedEncodingException, IOException
  {
    out = new ByteArrayOutputStream();
    OutputStreamWriter o;
    o = new OutputStreamWriter( new ByteArrayOutputStream(), encoding );
    this.realEncoding = o.getEncoding();
    this.encoding = encoding;
    o.close();
  }

  /**
   * Returns the canonical name of the <a href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">character encoding</a> being used by this stream. If this <code>StringBufferOutputStream</code> was created with the <code>StringBufferOutputStream(String)</code> constructor then the returned encoding name, being canonical, may differ from the encoding name passed to the constructor.
   * @return  a <code>String<code> representing the encoding name
   * @uml.property  name="encoding"
   */
  public String getEncoding()
    throws UnsupportedEncodingException
  {
    return this.realEncoding;
  }

  /**
   * Writes the specified byte to this output stream.
   *
   * @param b the byte to be written.
   */
  public void write( int b )
    throws java.io.IOException
  {
    out.write( b );
  }

  /**
   * Writes <code>b.length</code> bytes from the specified byte array to
   * this output stream.
   *
   * @param b the data.
   */
  public void write( byte[] b )
    throws IOException
  {
    out.write( b );
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at
   * offset <code>off</code> to this output stream.
   *
   * @param b the data.
   * @param off the start offset in the data.
   * @param len the number of bytes to write.
   */
  public void write( byte[] b, int off, int len )
    throws IOException
  {
    out.write( b, off, len );
  }

  /**
   * Flushes this output stream and forces any buffered output bytes to be
   * written out. The general contract of <code>flush()</code> is that
   * calling it is an indication that, if any bytes previously written have
   * been buffered by the implementation of the output stream, such bytes
   * should immediately be written to their intended destination.
   */
  public void flush()
    throws IOException
  {
    out.flush();
  }

  /**
   * Closes this output stream and releases any system resources associated
   * with this stream. The general contract of <code>close()</code> is that
   * it closes the output stream. A closed stream cannot perform output
   * operations and cannot be reopened.
   */
  public void close()
    throws IOException
  {
    out.close();
  }

  /**
   * Returns a <code>String</code> representation of the content of this
   * output stream.
   *
   * <p>This method provides proper <a
   * href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">character
   * encoding</a> of the bytes written to this output stream.</p>
   */
  public String toString()
  {
    if( this.encoding == null )
    {
      return out.toString();
    }
    else
    {
      try
      {
        return out.toString( this.encoding );
      }
      catch( UnsupportedEncodingException e )
      {
        // this exception is already thrown in contructor
        // and this point should never be reached
        return null;
      }
    }
  }
}