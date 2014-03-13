package haui.components;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 *		Module:					TerminalTextArea.java<br>
 *										$Source: $
 *<p>
 *		Description:    Extended JTextArea.<br>
 *</p><p>
 *		Created:				13.10.2000	by	AE
 *</p><p>
 *		@history				13.10.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class TerminalTextArea
extends JTextArea
{
  private static final long serialVersionUID = -5680382720935477724L;

  // other constants
  final static String PROMPT = "LRS> ";

  String m_strPrompt = PROMPT;
  Vector m_cmdHist = new Vector();
  StringBuffer m_curCmd = new StringBuffer();
  int m_curStartLinePos;
  int m_curLineIdx;
  int m_curLinePos;
  int m_histIdx;

  /**
   * Transfers the currently selected range in the associated
   * text model to the system clipboard, removing the contents
   * from the model.  The current selection is reset.  Does nothing
   * for null selections. Only copies the content if the selection
   * extends the current command line.
   */
  public void cut()
  {
    if( getSelectedText() != null)
    {
      if( getSelectionStart() > m_curStartLinePos && getSelectionEnd() > m_curStartLinePos)
      {
        super.cut();
        m_curLinePos = getCaretPosition() - m_curStartLinePos-1;
        m_curLineIdx = getText().length() - m_curStartLinePos-1;
      }
      else
      {
        super.copy();
        getToolkit().beep();
      }
    }
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
    setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
    super.paste();
    m_curLinePos = getCaretPosition() - m_curStartLinePos-1;
    m_curLineIdx = getText().length() - m_curStartLinePos-1;
  }

  /**
   * Process teminal emulation specific events
   */
  protected void processComponentKeyEvent( KeyEvent e)
  {
    int id = e.getID();
    int len = 0;
    switch( id)
    {
      case KeyEvent.KEY_PRESSED:
        switch( e.getKeyCode())
        {
          case KeyEvent.VK_ENTER:
            len = getText().length();
            try
            {
              m_curCmd.append( getText( m_curStartLinePos+1, m_curLineIdx));
            }
            catch( BadLocationException blex)
            {
              blex.printStackTrace();
            }
            setCursor( new Cursor( Cursor.WAIT_CURSOR));
            onEnter();
            setPrompt();
            e.consume();
            setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
            break;

          case KeyEvent.VK_BACK_SPACE:
            if( getSelectedText() != null)
            {
              if( getSelectionStart() > m_curStartLinePos && getSelectionEnd() > m_curStartLinePos)
              {
                len = getSelectedText().length();
                m_curLineIdx -= getSelectionEnd() - getSelectionStart();
                m_curLinePos = getSelectionStart() - m_curStartLinePos-1;
                replaceSelection( "");
              }
              else
              {
                if( getSelectionEnd() > m_curStartLinePos)
                {
                  m_curLinePos = getSelectionEnd() - m_curStartLinePos-1;
                }
                else
                  m_curLinePos = 0;
                getToolkit().beep();
                replaceSelection( getSelectedText());
              }
              e.consume();
              setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
              break;
            }
            if( m_curStartLinePos + 1 >= getCaretPosition())
            {
              e.consume();
              getToolkit().beep();
              break;
            }
            super.processComponentKeyEvent( e);
            m_curLinePos = getCaretPosition() - m_curStartLinePos-1;
            m_curLineIdx = getText().length() - m_curStartLinePos-1;
            break;

          case KeyEvent.VK_DELETE:
            if( getSelectedText() != null)
            {
              if( getSelectionStart() > m_curStartLinePos && getSelectionEnd() > m_curStartLinePos)
              {
                m_curLinePos = getSelectionStart() - m_curStartLinePos-1;
                m_curLineIdx -= getSelectionEnd() - getSelectionStart();
                replaceSelection( "");
              }
              else
              {
                if( getSelectionEnd() > m_curStartLinePos)
                {
                  m_curLinePos = getSelectionEnd() - m_curStartLinePos-1;
                }
                else
                  m_curLinePos = 0;
                getToolkit().beep();
                replaceSelection( getSelectedText());
              }
              e.consume();
              setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
              break;
            }
            if( m_curStartLinePos >= getCaretPosition())
            {
              e.consume();
              getToolkit().beep();
              break;
            }
            super.processComponentKeyEvent( e);
            m_curLinePos = getCaretPosition() - m_curStartLinePos-1;
            m_curLineIdx = getText().length() - m_curStartLinePos-1;
            break;

          case KeyEvent.VK_UP:
            if( getSelectedText() != null && e.getModifiers() != KeyEvent.SHIFT_MASK)
              replaceSelection( getSelectedText());
            len = getText().length();
            if( m_histIdx >= 0 && m_cmdHist.size() != 0)
            {
              m_histIdx = (m_histIdx < 1) ? 0 : m_histIdx-1;
              String strHist = ((StringBuffer)m_cmdHist.elementAt( m_histIdx)).toString();
              replaceRange( strHist, m_curStartLinePos+1, len);
              m_curLineIdx = strHist.length();
              m_curLinePos = m_curLineIdx;
              e.consume();
              setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
            }
            break;

          case KeyEvent.VK_DOWN:
            if( getSelectedText() != null && e.getModifiers() != KeyEvent.SHIFT_MASK)
              replaceSelection( getSelectedText());
            len = getText().length();
            if( m_histIdx < m_cmdHist.size())
            {
              m_histIdx = (m_histIdx >= m_cmdHist.size()-1) ? m_cmdHist.size()-1 : m_histIdx+1;
              String strHist = ((StringBuffer)m_cmdHist.elementAt( m_histIdx)).toString();
              replaceRange( strHist, m_curStartLinePos+1, len);
              m_curLineIdx = strHist.length();
              m_curLinePos = m_curLineIdx;
              e.consume();
              setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
            }
            break;

          case KeyEvent.VK_LEFT:
            if( getSelectedText() != null && e.getModifiers() != KeyEvent.SHIFT_MASK)
            {
              if( getSelectionStart() > m_curStartLinePos)
                m_curLinePos = getSelectionStart() - m_curStartLinePos-1;
              else
                m_curLinePos = 0;
              replaceSelection( getSelectedText());
              e.consume();
              setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
              break;
            }
            if( m_curLinePos > 0)
              m_curLinePos--;
            e.consume();
            setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
            break;

          case KeyEvent.VK_RIGHT:
            if( getSelectedText() != null && e.getModifiers() != KeyEvent.SHIFT_MASK)
            {
              if( getSelectionEnd() > m_curStartLinePos)
                m_curLinePos = getSelectionEnd() - m_curStartLinePos-1;
              else
                m_curLinePos = 0;
              replaceSelection( getSelectedText());
              e.consume();
              setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
              break;
            }
            if( m_curLinePos < m_curLineIdx)
              m_curLinePos++;
            e.consume();
            setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
            break;

          default:
            if( !(e.getKeyCode() == KeyEvent.VK_ALT || e.getKeyCode() == KeyEvent.VK_ALT_GRAPH || e.getKeyCode() == KeyEvent.VK_CANCEL
                || e.getKeyCode() == KeyEvent.VK_CAPS_LOCK || e.getKeyCode() == KeyEvent.VK_CLEAR || e.getKeyCode() == KeyEvent.VK_CONTROL
                || e.getKeyCode() == KeyEvent.VK_COPY || e.getKeyCode() == KeyEvent.VK_CUT || e.getKeyCode() == KeyEvent.VK_ESCAPE
                || e.getKeyCode() == KeyEvent.VK_F1 || e.getKeyCode() == KeyEvent.VK_F10 || e.getKeyCode() == KeyEvent.VK_F11
                || e.getKeyCode() == KeyEvent.VK_F12 || e.getKeyCode() == KeyEvent.VK_F2 || e.getKeyCode() == KeyEvent.VK_F3
                || e.getKeyCode() == KeyEvent.VK_F4 || e.getKeyCode() == KeyEvent.VK_F5 || e.getKeyCode() == KeyEvent.VK_F6
                || e.getKeyCode() == KeyEvent.VK_F7 || e.getKeyCode() == KeyEvent.VK_F8 || e.getKeyCode() == KeyEvent.VK_HELP
                || e.getKeyCode() == KeyEvent.VK_HOME || e.getKeyCode() == KeyEvent.VK_INSERT || e.getKeyCode() == KeyEvent.VK_META
                || e.getKeyCode() == KeyEvent.VK_NUM_LOCK || e.getKeyCode() == KeyEvent.VK_PASTE || e.getKeyCode() == KeyEvent.VK_PAUSE
                || e.getKeyCode() == KeyEvent.VK_PRINTSCREEN || e.getKeyCode() == KeyEvent.VK_SCROLL_LOCK || e.getKeyCode() == KeyEvent.VK_SHIFT
                || e.getKeyCode() == KeyEvent.VK_UNDO || ( e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_X)
                || ( e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_C)
                || ( e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_V)))
            {
              if( getSelectedText() == null)
              {
                setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
              }
              else
              {
                replaceSelection( getSelectedText());
                setCaretPosition( m_curStartLinePos + m_curLinePos + 1);
                getToolkit().beep();
              }
            }
            super.processComponentKeyEvent( e);
            if( e.getKeyCode() == KeyEvent.VK_ALT || e.getKeyCode() == KeyEvent.VK_ALT_GRAPH || e.getKeyCode() == KeyEvent.VK_CANCEL
                || e.getKeyCode() == KeyEvent.VK_CAPS_LOCK || e.getKeyCode() == KeyEvent.VK_CLEAR || e.getKeyCode() == KeyEvent.VK_CONTROL
                || e.getKeyCode() == KeyEvent.VK_COPY || e.getKeyCode() == KeyEvent.VK_CUT || e.getKeyCode() == KeyEvent.VK_ESCAPE
                || e.getKeyCode() == KeyEvent.VK_F1 || e.getKeyCode() == KeyEvent.VK_F10 || e.getKeyCode() == KeyEvent.VK_F11
                || e.getKeyCode() == KeyEvent.VK_F12 || e.getKeyCode() == KeyEvent.VK_F2 || e.getKeyCode() == KeyEvent.VK_F3
                || e.getKeyCode() == KeyEvent.VK_F4 || e.getKeyCode() == KeyEvent.VK_F5 || e.getKeyCode() == KeyEvent.VK_F6
                || e.getKeyCode() == KeyEvent.VK_F7 || e.getKeyCode() == KeyEvent.VK_F8 || e.getKeyCode() == KeyEvent.VK_HELP
                || e.getKeyCode() == KeyEvent.VK_HOME || e.getKeyCode() == KeyEvent.VK_INSERT || e.getKeyCode() == KeyEvent.VK_META
                || e.getKeyCode() == KeyEvent.VK_NUM_LOCK || e.getKeyCode() == KeyEvent.VK_PASTE || e.getKeyCode() == KeyEvent.VK_PAUSE
                || e.getKeyCode() == KeyEvent.VK_PRINTSCREEN || e.getKeyCode() == KeyEvent.VK_SCROLL_LOCK || e.getKeyCode() == KeyEvent.VK_SHIFT
                || e.getKeyCode() == KeyEvent.VK_UNDO || ( e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_X)
                || ( e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_C)
                || ( e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_V))
              break;
            m_curLinePos = getCaretPosition() - m_curStartLinePos;
            m_curLineIdx = getText().length() - m_curStartLinePos;
            break;
        }
        break;

      default:
        super.processComponentKeyEvent( e);
        //m_curLinePos = getCaretPosition() - m_curStartLinePos;
        //m_curLineIdx = getText().length() - m_curStartLinePos;
        break;
    }
  }

  /**
   * This method is called when enter was pressed.
   * Overwrite this method to do what you like.
   */
  public void onEnter()
  {
  }

  /**
   * Constructs a new TextArea.  A default model is set, the initial string
   * is null, and rows/columns are set to 0.
   */
  public TerminalTextArea()
  {
    this( null, null, 0, 0);
  }

  /**
   * Constructs a new TextArea with the specified text displayed.
   * A default model is created and rows/columns are set to 0.
   *
   * @param text the text to be displayed, or null
   */
  public TerminalTextArea( String text)
  {
    this( null, text, 0, 0);
  }

  /**
   * Constructs a new empty TextArea with the specified number of
   * rows and columns.  A default model is created, and the initial
   * string is null.
   *
   * @param rows the number of rows >= 0
   * @param columns the number of columns >= 0
   */
  public TerminalTextArea( int rows, int columns)
  {
      this( null, null, rows, columns);
  }

  /**
   * Constructs a new TextArea with the specified text and number
   * of rows and columns.  A default model is created.
   *
   * @param text the text to be displayed, or null
   * @param rows the number of rows >= 0
   * @param columns the number of columns >= 0
   */
  public TerminalTextArea( String text, int rows, int columns)
  {
      this( null, text, rows, columns);
  }

  /**
   * Constructs a new JTextArea with the given document model, and defaults
   * for all of the other arguments (null, 0, 0).
   *
   * @param doc  the model to use
   */
  public TerminalTextArea( Document doc)
  {
    this( doc, null, 0, 0);
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
  public TerminalTextArea( Document doc, String text, int rows, int columns)
  {
    super( doc, text, rows, columns);
    setPrompt();

  }


  public String getCommand()

  {

    return m_curCmd.toString();

  }

  public void setPromptLabel( String strPrompt)
  {
    m_strPrompt = strPrompt;
  }

  public String getPromptLabel()
  {
    return m_strPrompt;
  }

  /**
   * Set teminal prompt
   */
  void setPrompt()
  {
    if( m_curCmd.length() != 0)
      m_cmdHist.add( m_curCmd);
    append( m_strPrompt);
    m_curLineIdx = 0;
    m_curLinePos = 0;
    int len = getText().length();
    setCaretPosition( len);
    m_histIdx = m_cmdHist.size();
    m_curCmd = new StringBuffer();
    m_curStartLinePos = len-1;
    setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }
}


