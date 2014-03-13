package haui.app.HttpTunnel;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Array;
import javax.swing.JTextArea;
import haui.components.ConnectionManager;
import haui.asynch.Mutex;
import java.util.zip.*;

/**
 *		Module:					InputReader.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\HttpTunnel\\InputReader.java,v $
 *<p>
 *		Description:    Reads the input stream of a socket connection and saves the data in a buffer.<br>
 *</p><p>
 *		Created:				11.12.2001	by	AE
 *</p><p>
 *		@history				11.12.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: InputReader.java,v $
 *		Revision 1.0  2003-05-28 14:21:25+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2001; $Revision: 1.0 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\HttpTunnel\\InputReader.java,v 1.0 2003-05-28 14:21:25+02 t026843 Exp $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class InputReader
  extends Thread
{
  // constants
  public final static int LOCKTIMEOUT = 1000;

  // member variables
  private Mutex m_mutLock = new Mutex();
  private byte[] m_reqData;
  private int m_reqSize;
  private byte m_buf[] = new byte[ServerConnection.BUFFERSIZE];
  private JTextArea m_jTextAreaOut;
  private boolean m_blCompressed = false;
  private InputStream m_is = null;

  public InputReader( InputStream is, JTextArea ta, boolean compressed)
  {
    m_is = is;
    m_jTextAreaOut = ta;
    m_blCompressed = compressed;
  }

  public void setInputStream( InputStream is)
  {
    m_is = is;
  }

  public InputStream getInputStream()
  {
    return m_is;
  }

  public void closeAll()
  {
    try
    {
      if( m_is != null)
      {
        m_is.close();
        m_is = null;
      }
    }
    catch( IOException e)
    {
      if( m_jTextAreaOut != null)
        m_jTextAreaOut.append( "ERROR: " + e.toString() + "\n");
      e.printStackTrace();
    }
  }

  public void stopIt()
  {
    closeAll();
    stop();
  }

  public void interrupt()
  {
    closeAll();
    super.interrupt();
    if( isAlive())
    {
      try
      {
        Thread.sleep( 3000);
        if( isAlive())
          super.stop();
      }
      catch( InterruptedException iex)
      {
        iex.printStackTrace();
      }
    }
  }

  public boolean hasMoreData()
  {
    if( m_reqSize > 0)
      return true;
    else
      return false;
  }

  public int available()
  {
    return m_reqSize;
  }

  public byte[] getBytes()
  {
    if( isAlive())
      return null;

    byte[] b = null;
    int iLen;

    try
    {
      m_mutLock.acquire( LOCKTIMEOUT );
      b = m_reqData;
      iLen = m_reqSize;
      m_reqData = null;
      m_reqSize = 0;
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }

    return b;
  }

  public int read( byte[] b, int iSize)
  {
    if( iSize == 0)
      return 0;
    int iLen = 0;

    try
    {
      m_mutLock.acquire( LOCKTIMEOUT );
      int i = 0;
      byte[] oldData = m_reqData;

      if( m_reqSize <= iSize )
        iLen = m_reqSize;
      else
        iLen = iSize;
      for( i = 0; i < iLen; i++ )
      {
        b[i] = m_reqData[i];
      }
      if( m_reqSize - iLen > 0 )
      {
        m_reqData = new byte[m_reqSize - iLen];
        for( int j = 0; i < m_reqSize; i++, j++ )
        {
          m_reqData[j] = oldData[i];
        }
        m_reqSize = m_reqSize - iLen;
      }
      else
      {
        m_reqData = null;
        m_reqSize = 0;
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }

    return iLen;
  }

  public void run()
  {
    try
    {
      int iLen;

      while( m_is != null && (iLen = m_is.read( m_buf, 0, ServerConnection.BUFFERSIZE)) >= 0)
      {
        if( iLen == 0)
        {
          sleep(100);
          continue;
        }
        //System.out.print( ".");
        //System.out.write( m_buf);
        appendBytes( m_buf, iLen );
      }
    }
    catch( Exception e)
    {
      if( e.toString().indexOf( "10004") == -1 && e.toString().indexOf( "socket closed") == -1)
      {
        if( m_jTextAreaOut != null)
          m_jTextAreaOut.append( "ERROR: " + e.toString() + "\n");
        e.printStackTrace();
      }
    }
    closeAll();
  }

  private void appendBytes( byte[] b, int inSize)
  {
    try
    {
      m_mutLock.acquire( LOCKTIMEOUT );
      int i = 0;
      if( m_reqData == null )
      {
        m_reqData = new byte[inSize];
        for( i = 0; i < inSize; i++ )
          m_reqData[i] = b[i];
        m_reqSize = inSize;
      }
      else
      {
        int size = Array.getLength( m_reqData );
        byte[] oldBytes = m_reqData;
        m_reqData = new byte[m_reqSize + inSize];
        for( i = 0; i < size; i++ )
          m_reqData[i] = oldBytes[i];
        for( int j = 0; j < inSize; i++, j++ )
          m_reqData[i] = b[j];
        m_reqSize = m_reqSize + inSize;
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
  }
}
