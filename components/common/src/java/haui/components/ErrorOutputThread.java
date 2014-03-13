
package haui.components;

import javax.swing.JTextArea;
import java.io.*;

/**
 *
 *		Module:					ErrorOutputThread.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ErrorOutputThread.java,v $
 *<p>
 *		Description:    Reads error output in a thread and prints it to a JTextArea.<br>
 *</p><p>
 *		Created:				06.10.2000	by	AE
 *</p><p>
 *		@history				06.10.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: ErrorOutputThread.java,v $
 *		Revision 1.1  2002-09-27 15:29:43+02  t026843
 *		Dialogs extended from JExDialog
 *
 *		Revision 1.0  2000-10-13 09:14:14+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision: 1.1 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ErrorOutputThread.java,v 1.1 2002-09-27 15:29:43+02 t026843 Exp $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ErrorOutputThread
extends Thread
{
  JTextArea m_jTextArea;
  PipedInputStream m_pis;
  PipedOutputStream m_pos;
  BufferedReader m_br;

  public ErrorOutputThread( JTextArea jTextArea)
  {
    try
    {
      m_jTextArea = jTextArea;
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

      try
      {
        for( int i = 1; true; i++)
        {
          while( !m_br.ready())
            Thread.sleep(10);
          if( (str = m_br.readLine()) == null)
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