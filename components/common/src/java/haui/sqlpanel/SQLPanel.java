package haui.sqlpanel;

import haui.components.desktop.JDesktopFrame;
import haui.util.GlobalApplicationContext;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JInternalFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Description of the Class
 * @author      t026843
 * @created     12. Februar 2003
 */
public class SQLPanel
  extends JTextPane
{
  private static final long serialVersionUID = -4762823148077134752L;
  
  // constants
  public static final String COMPNAME = "SQLPanel";
  public static final String PROPSFILE = "SQLPanel.ppr";

  // member variables
  String m_strAppName;
  Vector m_vecUndo = new Vector();
  int m_iPosUndo = -1;
  int m_iMaxUndo = 0;
  int m_iPosRedo = -1;
  Vector m_vecRedo = new Vector();

  protected SQLInternalStatAid m_sqlStAid = null;
  boolean m_blStatementAidActive = false;
  public SQLParser sqlParser;
  Options options;


  /**
   *Constructor for the SQLPanel object
   *
   * @param  explorermainwindow  Description of Parameter
   */
  public SQLPanel( String strAppName)
  {
    this( strAppName, 0);
  }

  /**
   *Constructor for the SQLPanel object
   *
   * @param  explorermainwindow  Description of Parameter
   */
  public SQLPanel( String strAppName, int iMaxUndo)
  {
    m_strAppName = strAppName;
    m_iMaxUndo = iMaxUndo;
    options = new Options( m_strAppName, PROPSFILE);
    sqlParser = new SQLParser( this, options );
    setStyledDocument( sqlParser.doc );
    setBackground( (Color)options.get( "PARSER", "BACKGROUND", "COLORS" ) );
    // add mouse handler to list
    MouseHandler mh = new MouseHandler();
    addMouseListener( mh);
  }

  public void activateSQLStatementAid( JDesktopFrame df, JInternalFrame frame,
                                       String strDriver, String strUrl, String strUid, String strPwd, String strSchema)
  {
    m_sqlStAid = new SQLInternalStatAid( df, frame, this, "", strDriver, strUrl, strUid, strPwd, strSchema);
    m_blStatementAidActive = true;
  }

  public void setText( String str)
  {
    if( m_iMaxUndo > 0)
    {
      String strOld = super.getText();
      UndoData u = new UndoData( UndoData.NEW, 0, 0, strOld,
        UndoData.NEW, 0, 0, str);
      addUndo( u);
    }
    super.setText( str);
  }

  public void closeSQLStatementAid()
  {
    if( m_blStatementAidActive)
    {
      m_sqlStAid.close();
      m_blStatementAidActive = false;
    }
  }

  public void replaceSelection( String str)
  {
    if( m_iMaxUndo > 0)
    {
      String strOld = "";
      strOld = super.getText();
      int iRealSelEnd = getSelectionEnd();
      if( strOld.length() < iRealSelEnd)
      {
        iRealSelEnd = strOld.length();
        GlobalApplicationContext.instance().getOutputPrintStream().println( "replaceSelection workaround used!!!");
        //System.out.println( "replaceSelection workaround used!!!");
      }
      strOld = strOld.substring( getSelectionStart(), iRealSelEnd);
      int iSelEnd = getSelectionStart();
      if( str != null)
        iSelEnd += str.length();
      UndoData u = new UndoData( UndoData.REPLACE, getSelectionStart(), iSelEnd, strOld,
        UndoData.REPLACE, getSelectionStart(), iRealSelEnd, str);
      addUndo( u);
    }
    super.replaceSelection( str);
  }

  public void replaceSelectionWithoutUndo( String str)
  {
    super.replaceSelection( str);
  }

  public void cut()
  {
    if( m_iMaxUndo > 0)
    {
      String strOld = "";
      strOld = super.getText();
      strOld = strOld.substring( getSelectionStart(), getSelectionEnd());
      UndoData u = new UndoData( UndoData.INSERT, getSelectionStart(), getSelectionStart(), strOld,
        UndoData.REPLACE, getSelectionStart(), getSelectionEnd(), "");
      addUndo( u);
    }
    super.cut();
  }

  /*
  public void paste()
  {
    if( m_iMaxUndo > 0)
    {
      String strOld = "";
      strOld = super.getText();
      strOld = strOld.substring( getSelectionStart(), getSelectionEnd());
      UndoData u = new UndoData( UndoData.INSERT, getSelectionStart(), getSelectionStart(), strOld);
      addUndo( u);
    }
    super.paste();
  }
  */

  private void addUndo( UndoData u)
  {
    if( m_iMaxUndo <= 0)
      return;

    if( ++m_iPosUndo < m_iMaxUndo)
    {
      if( m_iPosUndo < m_vecUndo.size())
      {
        m_vecUndo.setElementAt( u, m_iPosUndo);
      }
      else
        m_vecUndo.add( u);
    }
    else
    {
      --m_iPosUndo;
      m_vecUndo.removeElementAt( 0);
      m_vecUndo.add( u);
    }
  }

//  private void setRedo( UndoData u)
//  {
//    if( m_iMaxUndo <= 0)
//      return;
//
//    if( m_iPosRedo < m_vecRedo.size())
//    {
//      m_vecRedo.setElementAt( u, m_iPosRedo);
//    }
//  }

  private void addRedo( UndoData u)
  {
    if( m_iMaxUndo <= 0)
      return;

    if( ++m_iPosRedo < m_iMaxUndo)
    {
      if( m_iPosRedo < m_vecRedo.size())
      {
        m_vecRedo.setElementAt( u, m_iPosRedo);
      }
      else
        m_vecRedo.add( u);
    }
    else
    {
      --m_iPosRedo;
      m_vecRedo.removeElementAt( 0);
      m_vecRedo.add( u);
    }
  }

  public void undo()
  {
    if( m_iMaxUndo <= 0)
      return;

    if( m_iPosUndo < 0)
    {
      m_iPosUndo = -1;
      return;
    }
    else
    {
      UndoData u = (UndoData)m_vecUndo.elementAt( m_iPosUndo);
      u.undo( this);
      addRedo( u);
      m_vecUndo.removeElementAt( m_iPosUndo);
      --m_iPosUndo;
    }
  }

  public void redo()
  {
    if( m_iMaxUndo <= 0)
      return;

    if( m_iPosRedo < 0)
    {
      m_iPosRedo = -1;
      return;
    }
    else
    {
      UndoData u = (UndoData)m_vecRedo.elementAt( m_iPosRedo);
      u.redo( this);
      addUndo( u);
      m_vecRedo.removeElementAt( m_iPosRedo);
      --m_iPosRedo;
    }
  }

  /**
   * Process specific events
   */
  protected void processComponentKeyEvent( KeyEvent e)
  {
    int id = e.getID();
    //int len = 0;
    boolean blConsumed = false;
    switch( id)
    {
      case KeyEvent.KEY_PRESSED:
        switch( e.getKeyCode())
        {
          case KeyEvent.VK_BACK_SPACE:
            if( m_iMaxUndo > 0)
            {
              int iSelStart = getSelectionStart();
              int iSelEnd = getSelectionEnd();
              if( iSelStart == iSelEnd)
                iSelStart = iSelEnd-1;
              if( iSelEnd <= 0)
              {
                iSelStart = 0;
                return;
              }
              String strOld = "";
              strOld = super.getText();
              strOld = strOld.substring( iSelStart, iSelEnd);
              UndoData u = new UndoData( UndoData.INSERT, iSelStart, iSelStart, strOld,
                UndoData.REPLACE, iSelStart, iSelEnd, "");
              addUndo( u);
            }
            break;

          case KeyEvent.VK_DELETE:
            if( m_iMaxUndo > 0)
            {
              int iSelStart = getSelectionStart();
              int iSelEnd = getSelectionEnd();
              if( iSelStart == iSelEnd)
                ++iSelEnd;
              if( iSelEnd <= 0)
              {
                iSelStart = 0;
                return;
              }
              String strOld = "";
              strOld = super.getText();
              if( strOld.length() < iSelEnd)
              {
                iSelEnd = 0;
                return;
              }
              strOld = strOld.substring( iSelStart, iSelEnd);
              UndoData u = new UndoData( UndoData.INSERT, iSelStart, iSelStart, strOld,
                UndoData.REPLACE, iSelStart, iSelEnd, "");
              addUndo( u);
            }
            break;

          // KeyEvents for SQLStatementAid
          case KeyEvent.VK_SPACE:
            if( m_blStatementAidActive && e.getModifiers() == KeyEvent.CTRL_MASK)
            {
              e.consume();
              m_sqlStAid.show( this);
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_PERIOD:
            if( m_blStatementAidActive)
            {
              super.processComponentKeyEvent( e);
              try
              {
                Runnable doRepaint = new Runnable()
                {
                  public void run()
                  {
                    repaint();
                  }
                };
                SwingUtilities.invokeLater( doRepaint );
                Thread.sleep( 10);
                Runnable doShow = new Runnable()
                {
                  public void run()
                  {
                    m_sqlStAid.show( SQLPanel.this );
                  }
                };
                SwingUtilities.invokeLater( doShow );
                blConsumed = true;
              }
              catch( Exception ex)
              {
                ex.printStackTrace();
              }
            }
            break;

          case KeyEvent.VK_ENTER:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              m_sqlStAid.replaceText();
              m_sqlStAid.setVisible( false );
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_ESCAPE:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              m_sqlStAid.setVisible( false );
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_LEFT:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_RIGHT:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_HOME:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              m_sqlStAid.moveSelectionHome();
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_END:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              m_sqlStAid.moveSelectionEnd();
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_UP:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              m_sqlStAid.moveSelectionUp();
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_DOWN:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              m_sqlStAid.moveSelectionDown();
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_PAGE_UP:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              m_sqlStAid.moveSelectionPageUp();
              blConsumed = true;
            }
            break;

          case KeyEvent.VK_PAGE_DOWN:
            if( m_sqlStAid != null && m_sqlStAid.isVisible())
            {
              e.consume();
              m_sqlStAid.moveSelectionPageDown();
              blConsumed = true;
            }
            break;
        }
        break;
    }
    super.processComponentKeyEvent( e);
    if( m_sqlStAid != null && !blConsumed && id == KeyEvent.KEY_PRESSED && m_sqlStAid.isVisible())
    {
      try
      {
        Runnable doRepaint = new Runnable()
        {
          public void run()
          {
            repaint();
          }
        };
        SwingUtilities.invokeLater( doRepaint );
        Thread.sleep( 10);
        Runnable doSelection = new Runnable()
        {
          public void run()
          {
            m_sqlStAid.setSelection();
          }
        };
        SwingUtilities.invokeLater( doSelection );
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      //Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2)
        onLeftMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 1)
        onLeftMousePressed( event);
    }
  }

  void onLeftMouseDoublePressed( MouseEvent event)
  {
    if( m_sqlStAid != null && m_sqlStAid.isVisible())
    {
      m_sqlStAid.replaceText();
      m_sqlStAid.setVisible( false );
    }
  }

  void onLeftMousePressed( MouseEvent event)
  {
    if( m_sqlStAid != null && m_sqlStAid.isVisible())
    {
      int iCurStartPos = getSelectionStart();
      if( iCurStartPos < m_sqlStAid.getStartPosition() || iCurStartPos > m_sqlStAid.getEndPosition())
      {
        m_sqlStAid.setVisible( false);
      }
      else if( iCurStartPos > m_sqlStAid.getStartPosition() || iCurStartPos < m_sqlStAid.getEndPosition())
      {
        m_sqlStAid.setSelection();
      }
    }
  }

  private class UndoData
    extends Object
  {
    // constants
    public static final int UNDEF = -1;
    public static final int NEW = 0;
    public static final int REPLACE = 1;
    public static final int INSERT = 2;

    // member variables
    int m_iActionUndo = UNDEF;
    int m_iStartPosUndo = 0;
    int m_iEndPosUndo = 0;
    String m_strUndo = "";
    int m_iActionRedo = UNDEF;
    int m_iStartPosRedo = 0;
    int m_iEndPosRedo = 0;
    String m_strRedo = "";

    public UndoData( int iActionUndo, int iStartPosUndo, int iEndPosUndo, String strUndo,
      int iActionRedo, int iStartPosRedo, int iEndPosRedo, String strRedo)
    {
      m_iActionUndo = iActionUndo;
      m_iStartPosUndo = iStartPosUndo;
      m_iEndPosUndo = iEndPosUndo;
      m_strUndo = strUndo;
      m_iActionRedo = iActionRedo;
      m_iStartPosRedo = iStartPosRedo;
      m_iEndPosRedo = iEndPosRedo;
      m_strRedo = strRedo;
    }

    public int getUndoAction()
    {
      return m_iActionUndo;
    }

    public int getRedoAction()
    {
      return m_iActionRedo;
    }

    public int getUndoStartPos()
    {
      return m_iStartPosUndo;
    }

    public int getRedoStartPos()
    {
      return m_iStartPosRedo;
    }

    public int getUndoEndPos()
    {
      return m_iEndPosUndo;
    }

    public int getRedoEndPos()
    {
      return m_iEndPosRedo;
    }

    public String getUndoString()
    {
      return m_strUndo;
    }

    public String getRedoString()
    {
      return m_strRedo;
    }

    public void undo( SQLPanel pane)
    {
      int iLen = 0;
      switch( m_iActionUndo)
      {
        case NEW:
          pane.selectAll();
          pane.replaceSelectionWithoutUndo( m_strUndo);
          break;

        case REPLACE:
          iLen = pane.getText().length();
          if( m_iStartPosUndo <= iLen && m_iEndPosUndo <= iLen)
          {
            pane.setSelectionStart( m_iStartPosUndo);
            pane.setSelectionEnd( m_iEndPosUndo);
            pane.replaceSelectionWithoutUndo( m_strUndo);
          }
          break;

        case INSERT:
          iLen = pane.getText().length();
          if( m_iStartPosUndo <= iLen)
          {
            pane.setSelectionStart( m_iStartPosUndo);
            pane.setSelectionEnd( m_iStartPosUndo);
            pane.replaceSelectionWithoutUndo( m_strUndo);
          }
          break;

        default:
          break;
      }
    }

    public void redo( SQLPanel pane)
    {
      int iLen = 0;
      switch( m_iActionRedo)
      {
        case NEW:
          pane.selectAll();
          pane.replaceSelectionWithoutUndo( m_strRedo);
          break;

        case REPLACE:
          iLen = pane.getText().length();
          if( m_iStartPosRedo <= iLen && m_iEndPosRedo <= iLen)
          {
            pane.setSelectionStart( m_iStartPosRedo);
            pane.setSelectionEnd( m_iEndPosRedo);
            pane.replaceSelectionWithoutUndo( m_strRedo);
          }
          break;

        case INSERT:
          iLen = pane.getText().length();
          if( m_iStartPosRedo <= iLen)
          {
            pane.setSelectionStart( m_iStartPosRedo);
            pane.setSelectionEnd( m_iStartPosRedo);
            pane.replaceSelectionWithoutUndo( m_strRedo);
          }
          break;

        default:
          break;
      }
    }

    public boolean equals( Object obj)
    {
      if( obj instanceof UndoData)
      {
        UndoData u = (UndoData) obj;
        return ( ( ( getUndoString() == null && u.getUndoString() == null)
          || ( ( getUndoString() != null && u.getUndoString() != null) && getUndoString().equals( u.getUndoString())))
          && ( ( getRedoString() == null && u.getRedoString() == null)
          || ( ( getRedoString() != null && u.getRedoString() != null) && getRedoString().equals( u.getRedoString())))
          && u.getUndoStartPos() == getUndoStartPos() && u.getUndoEndPos() == getUndoEndPos()
          && u.getRedoStartPos() == getRedoStartPos() && u.getRedoEndPos() == getRedoEndPos()
          && u.getUndoAction() == getUndoAction());
      }
      return false;
    }
  }
}

