
package haui.components;

import javax.swing.JTextArea;
import java.io.*;

/**
 *
 *		Module:					OutputThread.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\OutputThread.java,v $
 *<p>
 *		Description:    Reads output and/or error output in a thread and prints it to a JTextArea.<br>
 *</p><p>
 *		Created:				13.11.2000	by	AE
 *</p><p>
 *		@history				13.11.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: OutputThread.java,v $
 *		Revision 1.2  2004-08-31 16:03:17+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.1  2004-06-22 14:08:52+02  t026843
 *		bigger changes
 *
 *		Revision 1.0  2002-06-10 14:35:42+02  t026843
 *		Initial revision
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision: 1.2 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\OutputThread.java,v 1.2 2004-08-31 16:03:17+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class OutputThread
extends Thread
{
  private static int TIMEOUT = 10000;
  boolean m_blOut = false;
  boolean m_blErr = false;
  JTextArea m_jTextArea;
  PipedInputStream m_pis;
  PipedOutputStream m_pos;
  BufferedReader m_br;

  public OutputThread( JTextArea jTextArea, boolean blOut, boolean blErr)
  {
    m_jTextArea = jTextArea;
    m_blOut = blOut;
    m_blErr = blErr;
    init();
  }

  public void init()
  {
    try
    {
      if( m_pis == null)
        m_pis = new PipedInputStream();
      else
      {
        m_pis.close();
        m_pis = new PipedInputStream();
      }
      if( m_pos == null)
        m_pos = new PipedOutputStream( m_pis);
      else
      {
        m_pos.close();
        m_pos = new PipedOutputStream( m_pis);
      }
      if( m_blOut)
        System.setOut( new PrintStream( m_pos));
      if( m_blErr)
        System.setErr( new PrintStream( m_pos));
      if( m_br == null)
        m_br = new BufferedReader( new InputStreamReader( m_pis));
      else
      {
        m_br.close();
        m_br = new BufferedReader( new InputStreamReader( m_pis));
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

  public void close()
  {
    try
    {
      try
      {
        if( isAlive())
        {
          interrupt();
          if( isAlive())
          {
            sleep( 300);
            if( isAlive())
              stop();
          }
        }
      }
      catch (Exception ex)
      {
      }
      if( m_pis == null )
      {
        m_pis.close();
        m_pis = null;
      }
      if( m_pos == null)
      {
        m_pos.close();
        m_pos = null;
      }
      if( m_br != null)
      {
        m_br.close();
        m_br = null;
      }
    }
    catch( Exception ignored )
    {
    }

  }

  public void setTextArea( JTextArea jTextArea)
  {
    m_jTextArea = jTextArea;
  }

  public JTextArea getTextArea()
  {
    return m_jTextArea;
  }

  public PipedOutputStream getPipedOutputStream()
  {
    return m_pos;
  }

  public PipedInputStream getPipedInputStream()
  {
    return m_pis;
  }

  public void run()
  {
    try
    {
      String str = "";
      boolean blRun = true;

      try
      {
        for( int i = 1; blRun; i++)
        {
          int iTOut = 0;
          while( !m_br.ready())
          {
            Thread.sleep( 10 );
            iTOut += 10;
            if( iTOut > TIMEOUT)
            {
              //blRun = false;
              System.err.println( "Error: Output-Thread timeout!");
              break;
            }
          }
          if( !blRun || (str = m_br.readLine()) == null)
            continue;
          m_jTextArea.append( str + "\n");
          m_jTextArea.setCaretPosition( m_jTextArea.getText().length());
          if( i%50 == 0)
          {
            Thread.sleep( 5);
            i = 1;
          }
        }
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