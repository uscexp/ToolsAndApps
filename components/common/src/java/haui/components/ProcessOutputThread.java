
package haui.components;

import haui.util.GlobalApplicationContext;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JTextArea;

/**
 *
 *		Module:					ProcessOutputThread.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ProcessOutputThread.java,v $
 *<p>
 *		Description:    Reads process output and/or error output in a thread and prints it to System.out .<br>
 *</p><p>
 *		Created:				10.06.2002	by	AE
 *</p><p>
 *		@history				10.06.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: ProcessOutputThread.java,v $
 *		Revision 1.2  2004-08-31 16:03:19+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.1  2002-07-01 16:02:55+02  t026843
 *		optional TextArea output
 *
 *		Revision 1.0  2002-06-10 16:54:21+02  t026843
 *		Initial revision
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: 1.2 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ProcessOutputThread.java,v 1.2 2004-08-31 16:03:19+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ProcessOutputThread
extends Thread
{
  Process m_proc;
  String m_strAppName;
  BufferedInputStream m_bes = null;
  BufferedInputStream m_bis = null;
  JTextArea m_jTextArea = null;

  public ProcessOutputThread( String strAppName, Process proc, boolean blOut, boolean blErr)
  {
    this( strAppName, proc, blOut, blErr, null);
  }

  public ProcessOutputThread( String strAppName, Process proc, boolean blOut, boolean blErr, JTextArea jTextArea)
  {
    m_proc = proc;
    m_strAppName = strAppName;
    m_jTextArea = jTextArea;
    try
    {
      if( m_bis == null && blOut)
        m_bis = new BufferedInputStream( proc.getInputStream());
      else
      {
        m_bis.close();
        m_bis = new BufferedInputStream( proc.getInputStream());
      }
      if( m_bes == null && blErr)
        m_bes = new BufferedInputStream( proc.getErrorStream());
      else
      {
        m_bes.close();
        m_bes = new BufferedInputStream( proc.getErrorStream());
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

  public BufferedInputStream getErrorStream()
  {
    return m_bes;
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
          if( m_bis == null && m_bes == null)
            break;
          try
          {
            //int iexit = m_proc.exitValue();
            break;
          }
          catch( IllegalThreadStateException itsex)
          {
          }
          if( (m_bis != null && m_bis.available() <= 0) && (m_bes != null && m_bes.available() <= 0))
            Thread.sleep(10);
          if( m_bis != null && m_bis.available() > 0)
          {
            iLen = m_bis.read( b, 0, 256);
            if( iLen > 0)
            {
              if( m_jTextArea == null)
                GlobalApplicationContext.instance().getOutputPrintStream().write( b, 0, iLen);
                //System.out.write( b, 0, iLen);
              else
              {
                m_jTextArea.append( new String( b));
                String str = m_jTextArea.getText();
                if( str != null)
                  m_jTextArea.getCaret().setDot( str.length());
              }
            }
          }
          if( m_bes != null && m_bes.available() > 0)
          {
            iLen = m_bes.read( b, 0, 256);
            if( iLen > 0)
            {
              if( m_jTextArea == null)
                GlobalApplicationContext.instance().getOutputPrintStream().write( b, 0, iLen);
                //System.out.write( b, 0, iLen);
              else
              {
                m_jTextArea.append( new String( b));
                String str = m_jTextArea.getText();
                if( str != null)
                  m_jTextArea.getCaret().setDot( str.length());
              }
            }
          }
          if( i%50 == 0)
          {
            Thread.sleep( 5);
            i = 1;
          }
        }
        GlobalApplicationContext.instance().getOutputPrintStream().print( "...end\n");
        //System.out.print( "...end\n");
        if( m_bis != null)
          m_bis.close();
        if( m_bes != null)
          m_bes.close();
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