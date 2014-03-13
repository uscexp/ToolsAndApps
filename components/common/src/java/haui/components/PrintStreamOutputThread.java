package haui.components;

import haui.util.GlobalApplicationContext;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 *
 * Module:      PrintStreamOutputThread.java<br>
 *              $Source: $
 *<p>
 * Description: Reads output and/or error output in a thread and prints it to a JTextArea.<br>
 *</p><p>
 * Created:     12.08.2004 by AE
 *</p><p>
 * @history     12.08.2004 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since					JDK1.3
 *</p>
 */
public class PrintStreamOutputThread
extends Thread
{
  private static int TIMEOUT = 10000;
  JTextArea m_jTextArea;
  PrintStream m_psOut;
  PipedInputStream m_pis;
  PipedOutputStream m_pos;
  String m_strAppName;

  BufferedReader m_br;

  public PrintStreamOutputThread( String strAppName, JTextArea jTextArea)
  {
    m_strAppName = strAppName;
    m_jTextArea = jTextArea;
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
      m_psOut = new PrintStream( m_pos);
      if( m_br == null)
        m_br = new BufferedReader( new InputStreamReader( m_pis));
      else
      {
        m_br.close();
        m_br = new BufferedReader( new InputStreamReader( m_pis));
      }
      GlobalApplicationContext.instance().setOutputPrintStream(m_psOut);
      GlobalApplicationContext.instance().setErrorPrintStream(m_psOut);
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

  public PrintStream getOutputPrintStream()
  {
    return m_psOut;
  }

  public PrintStream getErrorPrintStream()
  {
    return m_psOut;
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
              //AppProperties.getPrintStreamOutput( m_strAppName).println( "Error: Output-Thread timeout!");
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
