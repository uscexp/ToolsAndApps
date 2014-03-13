package haui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.Document;

/**
 * Module:					ProcessTextArea.java<br> $Source: $ <p> Description:    Extended JTextArea.<br> </p><p> Created:				27.06.2002	by	AE </p><p>
 * @history  				27.06.2002	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2002; $Revision: $<br>  $Header: $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ProcessTextArea
extends JTextArea
{
  private static final long serialVersionUID = 4716939726095501643L;

  // other constants

  // member variables
  String m_strAppName;
  ProcessOutputThread m_pot = null;
  ProcessInputThread m_pit = null;

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
    super.paste();
  }

  public void exec( String cmd)
  {
    try
    {
      Process proc = null;
      if( cmd != null && !cmd.equals( "") && !cmd.equals( " "))
        proc = Runtime.getRuntime().exec( cmd);
      if( proc != null)
      {
        m_pot = new ProcessOutputThread( m_strAppName, proc, true, true, this);
        m_pot.start();
        m_pit = new ProcessInputThread( m_strAppName, proc);
        //m_pit.start();
      }
    }
    catch( IOException ioEx)
    {
      ioEx.printStackTrace();
    }
  }

  /**
   * Process teminal emulation specific events
   */
  protected void processComponentKeyEvent( KeyEvent e)
  {
    int c = e.getKeyChar();
    int id = e.getID();
    if( id == KeyEvent.KEY_PRESSED && !e.isConsumed())
      m_pit.send( c);
    e.consume();
  }

  /**
   * Constructs a new TextArea.  A default model is set, the initial string
   * is null, and rows/columns are set to 0.
   */
  public ProcessTextArea( String strAppName)
  {
    this( strAppName, null, null, 0, 0);
  }

  /**
   * Constructs a new TextArea with the specified text displayed.
   * A default model is created and rows/columns are set to 0.
   *
   * @param text the text to be displayed, or null
   */
  public ProcessTextArea( String strAppName, String text)
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
  public ProcessTextArea( String strAppName, int rows, int columns)
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
  public ProcessTextArea( String strAppName, String text, int rows, int columns)
  {
      this( strAppName, null, text, rows, columns);
  }

  /**
   * Constructs a new JTextArea with the given document model, and defaults
   * for all of the other arguments (null, 0, 0).
   *
   * @param doc  the model to use
   */
  public ProcessTextArea( String strAppName, Document doc)
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
  public ProcessTextArea( String strAppName, Document doc, String text, int rows, int columns)
  {
    super( doc, text, rows, columns);
    m_strAppName = strAppName;
    setPreferredSize( new Dimension( 640, 480));
    //super.setEditable( false);
  }

  //Main-Methode
  public static void main(String[] args)
  {
    ProcessTextArea pta = new ProcessTextArea( null);
    JFrame frame = new JFrame();
    frame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        //applet.exitApp();
        System.exit(0);
      }
    });
    frame.setTitle("Process control");
    frame.getContentPane().add( pta, BorderLayout.CENTER);
    pta.exec( "tzshnt");
    frame.pack();
    frame.setVisible(true);
  }

  // static initializer for setting look & feel
  static {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (Exception e) {}
  }
}
