package haui.components;

import haui.util.GlobalApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 *		Module:					ProcessInputThread.java<br>
 *										$Source: $
 *<p>
 *		Description:    Reads process output and/or error output in a thread and prints it to System.out .<br>
 *</p><p>
 *		Created:				28.06.2002	by	AE
 *</p><p>
 *		@history				28.06.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ProcessInputThread
extends Thread
{
  Process m_proc;
  String m_strAppName;
  BufferedInputStream m_bis = null;
  BufferedOutputStream m_bos = null;

  public ProcessInputThread( String strAppName, Process proc)
  {
    m_proc = proc;
    m_strAppName = strAppName;
    try
    {
      if( m_bis == null)
        m_bis = new BufferedInputStream( System.in);
      else
      {
        m_bis.close();
        m_bis = new BufferedInputStream( System.in);
      }
      if( m_bos == null)
        m_bos = new BufferedOutputStream( proc.getOutputStream());
      else
      {
        m_bos.close();
        m_bos = new BufferedOutputStream( proc.getOutputStream());
      }
    }
    catch( FileNotFoundException ex)
    {
      ex.printStackTrace();
    }
    catch( IOException ex)
    {
      ex.printStackTrace();
    }
  }

  public BufferedInputStream getInputStream()
  {
    return m_bis;
  }

  public BufferedOutputStream getOutputStream()
  {
    return m_bos;
  }

  public void send( int c)
  {
    try
    {
      if( m_bos != null)
      {
        GlobalApplicationContext.instance().getOutputPrintStream().write( c);
        //System.out.write( c);
        m_bos.write( c);
      }
    }
    catch( java.io.IOException ioex)
    {
      ioex.printStackTrace();
    }
  }

  public void close()
  {
    try
    {
      if( m_bis != null)
      {
        m_bis.close();
        m_bis = null;
      }
      if( m_bos != null)
      {
        m_bos.close();
        m_bos = null;
      }
    }
    catch( java.io.IOException ioex)
    {
      ioex.printStackTrace();
    }
  }

  public void run()
  {
    try
    {
      byte[] b = new byte[256];
      int iLen = 0;

      try
      {
        for( int i = 1; true; i++)
        {
          if( m_bis == null && m_bos == null)
            break;
          try
          {
            //int iexit = m_proc.exitValue();
            break;
          }
          catch( IllegalThreadStateException itsex)
          {
          }
          if( (m_bis != null && m_bis.available() <= 0))
            Thread.sleep(10);
          if( m_bis != null && m_bis.available() > 0)
          {
            iLen = m_bis.read( b, 0, 256);
            if( iLen > 0)
            {
              GlobalApplicationContext.instance().getOutputPrintStream().write( b, 0, iLen);
              //System.out.write( b, 0, iLen);
              m_bos.write( b, 0, iLen);
            }
          }
          if( i%50 == 0)
          {
            Thread.sleep( 5);
            i = 1;
          }
        }
        close();
      }
      catch( java.io.IOException ioex)
      {
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
