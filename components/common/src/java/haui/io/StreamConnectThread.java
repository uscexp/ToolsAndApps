package haui.io;

import java.io.*;

/**
 *    Module:       StreamConnectThread.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\StreamConnectThread.java,v $
 *<p>
 *    Description:  stream connect thread.<br>
 *</p><p>
 *    Created:	    24.03.2004  by AE
 *</p><p>
 *    @history      24.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: StreamConnectThread.java,v $
 *    Revision 1.0  2004-06-22 14:06:58+02  t026843
 *    Initial revision
 *
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: 1.0 $<br>
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\StreamConnectThread.java,v 1.0 2004-06-22 14:06:58+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class StreamConnectThread
  extends Thread
{
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  private InputStream m_isIn = null;
  private OutputStream m_osOut = null;
  private int m_iResetSize = DEFAULT_BUFFER_SIZE;

  private boolean m_blClosing = false;

  public StreamConnectThread( InputStream inStream, OutputStream outStream, int iResetSize )
  {
    m_isIn = inStream;
    m_osOut = outStream;
    m_iResetSize = iResetSize;
  }

  public StreamConnectThread( InputStream inStream, OutputStream outStream )
  {
    this( inStream, outStream, DEFAULT_BUFFER_SIZE );
  }

  public void run()
  {
    byte[] byteBuf = new byte[m_iResetSize];
    while( !m_blClosing)
    {
      // This is the point where it is very important to synchronize.
      synchronized( m_isIn )
      {
        if( copyTransaction( byteBuf ) )
        {
          return;
        }
      }
    }
  }

  /**
   * stop the thread.
   */
  public void close()
  {
    m_blClosing = true;

    // This isn't effective while read() is in blocking state.
    interrupt();
    try
    {
      sleep( 100 );
      if( isAlive())
        stop();
    }
    catch( InterruptedException ex )
    {
      ex.printStackTrace();
    }
  }

  /**
   * please syncronize input stream before this method call.
   * @return true = terminating
   */
  private boolean copyTransaction( byte[] byteBuf )
  {
    beginTransactionStream();

    int iRet = ( -1 );
    try
    {
      iRet = m_isIn.read( byteBuf );
      if( iRet < 0 )
      {
        return true;
      }
    }
    catch( IOException ex )
    {
      //System.out.println("Input stream is blocked: "+ex.toString());
      return true;
    }

    if( m_blClosing )
    {
      rollbackTransactionStream();
      return true;
    }

    try
    {
      m_osOut.write( byteBuf, 0, iRet );
      m_osOut.flush();
    }
    catch( IOException ex )
    {
      rollbackTransactionStream();
      return true;
    }

    return false;
  }

  /**
   * begin input stream transaction.
   */
  private void beginTransactionStream()
  {
    if( m_isIn.markSupported())
      m_isIn.mark( m_iResetSize );
  }

  /**
   * rollback input stream transaction.
   */
  private void rollbackTransactionStream()
  {
    if( m_isIn.markSupported())
    {
      try
      {
        m_isIn.reset();
      }
      catch( IOException ex )
      {
        System.out.println( ex.toString() );
        ex.printStackTrace();
      }
    }
  }
}