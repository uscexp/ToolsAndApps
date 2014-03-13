package haui.components;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Module:					ConnectionManagerUploadOutputStream.java<br> $Source: $ <p> Description:    ConnectionManagerUploadOutputStream hides http multipart/form-data stuff.<br> >>>>>>>>>>>>>>> completely untested!!!!!!!!!!!!!!!!!!!!!! </p><p> Created:				22.08.2002	by	AE </p><p>
 * @author      Andreas Eisenhauer  </p><p>
 * @created     23. August 2002
 * @history     23.08.2002	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @version     v1.0, 2002; $Revision: $<br>  $Header: $  </p><p>
 * @since       JDK1.2  </p>
 */
public class ConnectionManagerUploadOutputStream
  extends OutputStream
{
  // member variables
  ConnectionManager m_cm = null;
  BufferedOutputStream m_bos = null;

  /**
   * Constructor for the ConnectionManagerUploadOutputStream object
   *
   * @param  cm  ConnectionManager!
   */
  public ConnectionManagerUploadOutputStream( ConnectionManager cm )
  {
    m_cm = cm;
  }


  /**
   * Writes the specified byte to this file output stream. Implements
   * the <code>write</code> method of <code>OutputStream</code>.
   *
   * @param  b                the byte to be written.
   * @exception  IOException  if an I/O error occurs.
   */
  public void write( int b )
    throws IOException
  {
    byte by[] = new byte[1];
    by[0] = (byte)b;
    writeBytes( by, 0, 1);
  }


  /**
   * Writes <code>b.length</code> bytes from the specified byte array
   * to this file output stream.
   *
   * @param  b                the data.
   * @exception  IOException  if an I/O error occurs.
   */
  public void write( byte b[] )
    throws IOException
  {
    writeBytes( b, 0, b.length );
  }


  /**
   * Writes <code>len</code> bytes from the specified byte array
   * starting at offset <code>off</code> to this file output stream.
   *
   * @param  b                the data.
   * @param  off              the start offset in the data.
   * @param  len              the number of bytes to write.
   * @exception  IOException  if an I/O error occurs.
   */
  public void write( byte b[], int off, int len )
    throws IOException
  {
    writeBytes( b, off, len );
  }

  /**
   * Closes this file output stream and releases any system resources
   * associated with this stream. This file output stream may no longer
   * be used for writing bytes.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void close()
    throws IOException
  {
    m_cm.postEndUpload( m_bos);
    m_bos.close();
  }

  /**
   * Opens a file, with the specified name, for writing.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private void open( String name)
    throws IOException
  {
    m_cm.openConnection();
    m_bos = m_cm.postInitialUpload( name);
  }

  /**
   * Writes a sub array as a sequence of bytes.
   *
   * @param  b                the data to be written
   * @param  off              the start offset in the data
   * @param  len              the number of bytes that are written
   * @exception  IOException  If an I/O error has occurred.
   */
  private void writeBytes( byte b[], int off, int len)
    throws IOException
  {
    m_bos.write( b, off, len);
  }
}

