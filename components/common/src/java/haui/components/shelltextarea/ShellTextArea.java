package haui.components.shelltextarea;

import haui.asynch.Mutex;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * Module:       ShellTextArea.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ShellTextArea.java,v $ <p> Description:  Extended JTextArea.<br> </p><p> Created:	    16.03.2004  by AE </p><p>
 * @history       16.03.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: ShellTextArea.java,v $  Revision 1.1  2004-08-31 16:03:14+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.0  2004-06-22 14:06:53+02  t026843  Initial revision  </p><p>
 * @author        Andreas Eisenhauer  </p><p>
 * @version       v1.0, 2004; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ShellTextArea.java,v 1.1 2004-08-31 16:03:14+02 t026843 Exp t026843 $  </p><p>
 * @since         JDK1.3  </p>
 */
public class ShellTextArea
  extends JTextArea
{
  private static final long serialVersionUID = 1641481337563309231L;

  // constants
  public final static int LOCKTIMEOUT = 1000;

  // member variables
  protected String m_strAppName;
  private StreamContainer m_sc = new StreamContainer();
  private String m_strCmd;

  private int m_iCurStartLinePos;
  private Mutex m_mutLock = new Mutex();
  private boolean m_blCorrection = false;
  private boolean m_blPasswordInput = false;
  private boolean m_blInput = false;
  StringBuffer strbufPassword = null;
  Context stateContext = new Context( new NormalInputState());

  int m_histIdx;
  Vector m_cmdHist = new Vector();

  public void setPasswordInputMode( boolean blMode)
  {
    if( blMode)
      strbufPassword = new StringBuffer();
    else
      strbufPassword = null;
    m_blPasswordInput = blMode;
  }

  public boolean isInPasswordInputMode()
  {
    return m_blPasswordInput;
  }

  public void setInputMode( boolean blMode)
  {
    m_blInput = blMode;
  }

  public boolean isInInputMode()
  {
    return m_blInput;
  }

  public void setCurrentStartLinePos( int iCurStartLinePos)
  {
    try
    {
      m_mutLock.acquire( LOCKTIMEOUT );
      m_iCurStartLinePos = iCurStartLinePos;
    }
    catch( InterruptedException ex )
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
  }

  public int getCurrentStartLinePos()
  {
    return m_iCurStartLinePos;
  }

  public StreamContainer getStreamContainer()
  {
    return m_sc;
  }

  public void setStreamContainer( StreamContainer sc)
  {
    this.m_sc = sc;
  }

  public String getAppName()
  {
    return m_strAppName;
  }

  public void setAppName( String appName)
  {
    m_strAppName = appName;
  }

  public String getCommand()
  {
    return m_strCmd;
  }

  public void setCommnand( String cmd)
  {
    m_strCmd = cmd;
  }

  public StringBuffer getPassword()
  {
    return strbufPassword;
  }

  public void setPassword( StringBuffer strbufPassword)
  {
    this.strbufPassword = strbufPassword;
  }

  public boolean isCorrection()
  {
    return m_blCorrection;
  }

  public void setCorrection( boolean correction)
  {
    m_blCorrection = correction;
  }

  public Vector getCommandHistory()
  {
    return m_cmdHist;
  }

  public void setCommandHistory( Vector hist)
  {
    m_cmdHist = hist;
  }

  public int getHistoryIndex()
  {
    return m_histIdx;
  }

  public void setHistoryIndex( int idx)
  {
    m_histIdx = idx;
  }

  /**
   * Transfers the currently selected range in the associated
   * text model to the system clipboard, removing the contents
   * from the model.  The current selection is reset.  Does nothing
   * for null selections. Only copies the content if the selection
   * extends the current command line.
   */
  public void cut()
  {
    super.cut();
  }

   /**
   * Transfers the currently selected range in the associated
   * text model to the system clipboard, leaving the contents
   * in the text model.  The current selection is remains intact.
   * Does nothing for null selections.
   */
  public void copy()
  {
    super.copy();
  }

 /**
    * Transfers the contents of the system clipboard into the
    * JTextArea in the current command line position.
    */
  public void paste()
  {
    /*
    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();

    Transferable content = cb.getContents (this);

    String str = null;

    try
    {
      str = (String)content.getTransferData (DataFlavor.stringFlavor);
      byte[] b = str.getBytes();
      m_sc.getShellOutputStream().write( b, 0, b.length );
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    */
    super.paste();
  }

  /**
   * Process shell emulation specific events
   */
  protected void processComponentKeyEvent( KeyEvent e)
  {
    stateContext.request( this, e);
  }
  
  /**
   * Process shell emulation specific events
   */
/*  protected void processComponentKeyEvent( KeyEvent e)
  {
    int id = e.getID();
    int iCode = e.getKeyCode();
    int iMod = e.getModifiers();
    byte[] b;
    String c = String.valueOf( e.getKeyChar());
    int iLen = 0;
    if( m_blPasswordInput)
    {
      switch( id )
      {
        case KeyEvent.KEY_PRESSED:
          m_blCorrection = false;
          setCaretPosition( getCurrentStartLinePos());
          switch( iCode )
          {
            case KeyEvent.VK_ENTER:
              m_strCmd = strbufPassword.toString();
              if( m_strCmd != null )
              {
                b = m_strCmd.getBytes();
                try
                {
                  m_sc.getShellOutputStream().write( b, 0, b.length );
                  b = c.getBytes();
                  m_sc.getShellOutputStream().write( b, 0, b.length );
                }
                catch( IOException ioex )
                {
                  ioex.printStackTrace();
                  GlobalAppProperties.instance().getPrintStreamOutput( m_strAppName).println( "Reconnect Input/Output Streams!");
                  //System.out.println( "Reconnect Input/Output Streams!" );
                  m_sc.newConnection();
                }
              }
              setPasswordInputMode( false);
              break;

            default:
              if( m_blCorrection )
                e.consume();
              break;
          }
          break;

        case KeyEvent.KEY_TYPED:
          strbufPassword.append( c);
          e.consume();
          break;

        case KeyEvent.KEY_RELEASED:
          if( m_blCorrection )
            e.consume();
          break;
      }
    }
    else
    {
      switch( id )
      {
        case KeyEvent.KEY_PRESSED:
          m_blCorrection = false;
          if( getCaretPosition() < getCurrentStartLinePos()
              || getSelectionStart() < getCurrentStartLinePos()
              || getSelectionEnd() < getCurrentStartLinePos() )
            m_blCorrection = true;

          switch( iCode )
          {
            case KeyEvent.VK_UP:
              if( getSelectedText() != null && e.getModifiers() != KeyEvent.SHIFT_MASK )
                replaceSelection( getSelectedText() );
              iLen = getText().length();
              if( !m_blInput && m_histIdx >= 0 && m_cmdHist.size() != 0 )
              {
                m_histIdx = ( m_histIdx < 1 ) ? 0 : m_histIdx - 1;
                String strHist = ( String )m_cmdHist.elementAt( m_histIdx );
                replaceRange( strHist, getCurrentStartLinePos(), iLen );
                e.consume();
                setCaretPosition( getCurrentStartLinePos() + strHist.length() );
              }
              break;

            case KeyEvent.VK_DOWN:
              if( getSelectedText() != null && e.getModifiers() != KeyEvent.SHIFT_MASK )
                replaceSelection( getSelectedText() );
              iLen = getText().length();
              if( !m_blInput && m_histIdx < m_cmdHist.size() )
              {
                m_histIdx = ( m_histIdx >= m_cmdHist.size() - 1 ) ? m_cmdHist.size() - 1
                            : m_histIdx + 1;
                String strHist = ( String )m_cmdHist.elementAt( m_histIdx );
                replaceRange( strHist, getCurrentStartLinePos(), iLen );
                e.consume();
                setCaretPosition( getCurrentStartLinePos() + strHist.length() );
              }
              break;

            case KeyEvent.VK_ENTER:
              try
              {
                setCaretPosition( getText().length() );
                m_strCmd = getText( getCurrentStartLinePos(),
                                    getText().length() - getCurrentStartLinePos() );
                if( m_strCmd.length() != 0 && !m_blInput )
                  m_cmdHist.add( m_strCmd );
                m_histIdx = m_cmdHist.size();
              }
              catch( BadLocationException ex )
              {
                ex.printStackTrace();
              }
              if( m_strCmd != null )
              {
                b = m_strCmd.getBytes();
                try
                {
                  m_sc.getShellOutputStream().write( b, 0, b.length );
                  b = c.getBytes();
                  m_sc.getShellOutputStream().write( b, 0, b.length );
                }
                catch( IOException ioex )
                {
                  ioex.printStackTrace();
                  GlobalAppProperties.instance().getPrintStreamOutput( m_strAppName).println( "Reconnect Input/Output Streams!");
                  //System.out.println( "Reconnect Input/Output Streams!" );
                  m_sc.newConnection();
                }
              }
              setInputMode( false);
              break;

            case KeyEvent.VK_C:
              if( iMod != KeyEvent.CTRL_MASK )
              {
                if( m_blCorrection )
                  e.consume();
              }
              break;

            case KeyEvent.VK_X:
              if( iMod == KeyEvent.CTRL_MASK )
                copy();
              if( m_blCorrection )
                e.consume();
              break;

            case KeyEvent.VK_BACK_SPACE:
              if( getCaretPosition() <= getCurrentStartLinePos()
                  || getSelectionStart() <= getCurrentStartLinePos()
                  || getSelectionEnd() <= getCurrentStartLinePos() )
                m_blCorrection = true;
              if( m_blCorrection )
                e.consume();
              break;

            default:
              if( m_blCorrection )
                e.consume();
              break;
          }
          break;

        case KeyEvent.KEY_TYPED:
          if( m_blCorrection )
            e.consume();
          break;

        case KeyEvent.KEY_RELEASED:
          if( m_blCorrection )
            e.consume();
          break;
      }
    }
  }
*/
  /**
   * Constructs a new TextArea.  A default model is set, the initial string
   * is null, and rows/columns are set to 0.
   */
  public ShellTextArea( String strAppName)
  {
    this( strAppName, null, null, 0, 0);
  }

  /**
   * Constructs a new TextArea with the specified text displayed.
   * A default model is created and rows/columns are set to 0.
   *
   * @param text the text to be displayed, or null
   */
  public ShellTextArea( String strAppName, String text)
  {
    this( strAppName, null, text, 0, 0);
  }

  /**
   * Constructs a new empty TextArea with the specified number of
   * rows and columns.  A default model is created, and the initial
   * string is null.
   *
   * @param rows the number of rows >= 0
   * @param columns the number of columns >= 0
   */
  public ShellTextArea( String strAppName, int rows, int columns)
  {
      this( strAppName, null, null, rows, columns);
  }

  /**
   * Constructs a new TextArea with the specified text and number
   * of rows and columns.  A default model is created.
   *
   * @param text the text to be displayed, or null
   * @param rows the number of rows >= 0
   * @param columns the number of columns >= 0
   */
  public ShellTextArea( String strAppName, String text, int rows, int columns)
  {
      this( strAppName, null, text, rows, columns);
  }

  /**
   * Constructs a new JTextArea with the given document model, and defaults
   * for all of the other arguments (null, 0, 0).
   *
   * @param doc  the model to use
   */
  public ShellTextArea( String strAppName, Document doc)
  {
    this( strAppName, doc, null, 0, 0);
  }

  /**
   * Constructs a new JTextArea with the specified number of rows
   * and columns, and the given model.  All of the constructors
   * feed through this constructor.
   *
   * @param doc the model to use, or create a default one if null
   * @param text the text to be displayed, null if none
   * @param rows the number of rows >= 0
   * @param columns the number of columns >= 0
   */
  public ShellTextArea( String strAppName, Document doc, String text, int rows, int columns)
  {
    super( doc, text, rows, columns);
    m_strAppName = strAppName;
    m_iCurStartLinePos = 0;
    OutputThread ot = new OutputThread();
    ot.start();
  }

  public void clear()
  {
    setText( "");
  }

  public class OutputThread
    extends Thread
  {
    public OutputThread()
    {
    }

    public void run()
    {
      byte[] buf = new byte[256];
      int iBufSize = 256;
      int iRead = 0;
      boolean blRun = true;

      while( blRun)
      {
        try
        {
          String str = "";
          while( ( iRead = m_sc.getShellInputStream().read( buf, 0, iBufSize ) ) != -1 )
          {
            str = new String( buf, 0, iRead );
            //ShellTextArea.this.replaceSelection( str);
            ShellTextArea.this.append( str );
            int iPos = ShellTextArea.this.getText().length();
            setCurrentStartLinePos( iPos );
            ShellTextArea.this.setCaretPosition( iPos );
          }
        }
        catch( IOException ioex )
        {
          //AppProperties.getPrintStreamOutput( m_strAppName).println( ioex.toString());
          //System.err.println( "Reconnect Input/Output Streams!");
          System.err.println( "...");
          m_sc.newConnection();
          continue;
        }
        catch( Exception ex )
        {
          ex.printStackTrace();
        }
        blRun = false;
      }
    }
  }

  public class StreamContainer
  {
    private PipedInputStream m_pisFromExtern;
    private PipedOutputStream m_posFromExtern;
    private PipedInputStream m_pisToExtern;
    private PipedOutputStream m_posToExtern;
    private PrintStream m_ps;

    public StreamContainer()
    {
      newConnection();
    }

    public void newConnection()
    {
      try
      {
        close();
        m_posFromExtern = new PipedOutputStream();
        m_pisFromExtern = new PipedInputStream( m_posFromExtern );
        m_pisToExtern = new PipedInputStream();
        m_posToExtern = new PipedOutputStream( m_pisToExtern );
        m_ps = new PrintStream( getOutputStream());
      }
      catch( IOException ex )
      {
        ex.printStackTrace();
      }
    }

    public void close()
    {
      try
      {
        if( m_posFromExtern != null )
          m_posFromExtern.close();
        if( m_pisFromExtern != null)
          m_pisFromExtern.close();
        if( m_posToExtern != null)
          m_posToExtern.close();
        if( m_posToExtern != null)
          m_posToExtern.close();
      }
      catch( IOException ex )
      {
        ex.printStackTrace();
      }
    }

    public InputStream getInputStream()
    {
      return m_pisToExtern;
    }

    public OutputStream getOutputStream()
    {
      return m_posFromExtern;
    }

    public PrintStream getOutputPrintStream()
    {
      return m_ps;
    }

    public InputStream getShellInputStream()
    {
      return m_pisFromExtern;
    }

    public OutputStream getShellOutputStream()
    {
      return m_posToExtern;
    }
  }
}


