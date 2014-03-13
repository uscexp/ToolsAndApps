package haui.app.cdarchive;

import haui.asynch.Mutex;
import haui.util.GlobalAppProperties;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Module:      ContentTextArea.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\ContentTextArea.java,v $
 *<p>
 * Description: ContentTextArea.<br>
 *</p><p>
 * Created:     08.05.2003	by	AE
 *</p><p>
 *
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     08. May 2003
 * @history     08.05.2003	by	AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: ContentTextArea.java,v $
 * Revision 1.3  2004-08-31 16:03:05+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.2  2004-06-22 14:08:52+02  t026843
 * bigger changes
 *
 * Revision 1.1  2004-02-17 16:05:12+01  t026843
 * <>
 *
 * Revision 1.0  2003-05-21 16:24:40+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.3 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\ContentTextArea.java,v 1.3 2004-08-31 16:03:05+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class ContentTextArea
  extends JTextPane
  implements Printable
{
  // constants
  protected static final short WARP = 100;
  protected static final int NORMAL = 0;
  protected static final int BOLD = 1;
  protected static final int BOLDITALIC = 2;
  protected static final int MAXSTYLES = 3;

  // member variables
  protected String m_strAppName;
  protected SimpleAttributeSet[] m_sas;
  protected StyledDocument m_doc;
  protected String m_strText;
  protected String m_strSearch;
  protected Mutex m_mutLock = new Mutex();
  private Vector m_vecUndo = new Vector();
  private int m_iPosUndo = -1;
  private int m_iMaxUndo = 0;
  private int m_iPosRedo = -1;
  private Vector m_vecRedo = new Vector();
  private int m_iPrinterAdjust;
  protected CdArchiveDesktop m_dp;
  protected CdArchiveFrame m_af;

  public ContentTextArea( CdArchiveDesktop dp, CdArchiveFrame af, int iMaxUndo, int iPrinterAdjust, String strAppName)
  {
    super();
    m_dp = dp;
    m_af = af;
    m_strAppName = strAppName;
    m_iMaxUndo = iMaxUndo;
    m_iPrinterAdjust = iPrinterAdjust;
    m_sas = new SimpleAttributeSet[MAXSTYLES];
    for( int i = 0; i < MAXSTYLES; ++i)
    {
      m_sas[i] = new SimpleAttributeSet();
    }

    // NORMAL
    StyleConstants.setForeground( m_sas[NORMAL], Color.black);
    // BOLD
    StyleConstants.setBold( m_sas[BOLD], true);
    StyleConstants.setForeground( m_sas[BOLD], Color.black);
    // BOLD ITALIC
    StyleConstants.setBold( m_sas[BOLDITALIC], true);
    StyleConstants.setItalic( m_sas[BOLDITALIC], true);
    StyleConstants.setForeground( m_sas[BOLDITALIC], Color.black);
    StyleConstants.setFontSize( m_sas[BOLDITALIC], 14);

    m_doc = getStyledDocument();
    setVisible( true);
  }

  public void find( boolean blNew)
  {
    if( blNew)
    {
      FindDialog dlg = new FindDialog( m_af, "Find", true);
      dlg.setFindString( m_strSearch);
      dlg.setMessageString( "Find text?");
      dlg.pack();
      dlg.setVisible( true);
      if( !dlg.isCanceled())
      {
        m_strSearch = dlg.getFindString();
      }
      else
        return;
      //m_strSearch = JOptionPane.showInputDialog( AppProperties.m_rootComp, "Find text?"
      //                                           , "Find", JOptionPane.QUESTION_MESSAGE );
    }

    if( m_strSearch != null && !m_strSearch.equals( ""))
    {
      int iCurPos = getCaret().getDot();
      int iLen = getText().length();
      int iLenWord = m_strSearch.length();
      int iPos = 0;

      if( iCurPos >= iLen-1)
      {
        iCurPos = 0;
        setCaretPosition( 0);
      }

      if( (iPos = getText().toLowerCase().indexOf( m_strSearch.toLowerCase(), iCurPos)) != -1)
      {
        requestFocus();
        setCaretPosition( iPos+iLenWord);
        setSelectionStart( iPos);
        setSelectionEnd( iPos+iLenWord);
      }
      else
      {
        JOptionPane.showMessageDialog( GlobalAppProperties.instance().getRootComponent( CdArchiveDesktop.CDARCHDT), "Nothing found!"
                                       , "Info", JOptionPane.INFORMATION_MESSAGE );
      }
    }
  }

  public void cut()
  {
    super.cut();
    SwingUtilities.invokeLater( new TextParser());
  }

  public void paste()
  {
    /*
    if( m_iMaxUndo > 0)
    {
      String strOld = "";
      strOld = super.getText();
      strOld = strOld.substring( getSelectionStart(), getSelectionEnd());
      UndoData u = new UndoData( UndoData.INSERT, getSelectionStart(), getSelectionStart(), strOld);
      addUndo( u);
    }
    */
    super.paste();
    SwingUtilities.invokeLater( new TextParser());
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
        GlobalAppProperties.instance().getPrintStreamOutput( CdArchiveDesktop.CDARCHDT).println( "replaceSelection workaround used!!!");
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
    SwingUtilities.invokeLater( new TextParser() );
  }

  public void replaceSelectionWithoutUndo( String str)
  {
    super.replaceSelection( str);
    SwingUtilities.invokeLater( new TextParser() );
  }

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

  private void setRedo( UndoData u)
  {
    if( m_iMaxUndo <= 0)
      return;

    if( m_iPosRedo < m_vecRedo.size())
    {
      m_vecRedo.setElementAt( u, m_iPosRedo);
    }
  }

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
    SwingUtilities.invokeLater( new TextParser() );
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
    SwingUtilities.invokeLater( new TextParser() );
  }

  protected void setTextStyle( int iOffset, int iLen, int iStyle)
  {
    if( iStyle >= MAXSTYLES || iStyle < 0)
    {
      GlobalAppProperties.instance().getPrintStreamOutput( m_strAppName).println( "Bad style index");
      //System.err.println( "Bad style index" );
      return;
    }
    m_doc.setCharacterAttributes( iOffset, iLen, m_sas[iStyle], true);
  }

  protected void appendText( String str, int iStyle)
  {
    try
    {
      if( str == null || str.equals( ""))
        return;

      if( iStyle >= MAXSTYLES || iStyle < 0)
      {
        GlobalAppProperties.instance().getPrintStreamOutput( m_strAppName).println( "Bad style index");
        //System.err.println( "Bad style index" );
        return;
      }
      m_doc.insertString( m_doc.getLength(), str, m_sas[iStyle] );
    }
    catch( BadLocationException e )
    {
      GlobalAppProperties.instance().getPrintStreamOutput( m_strAppName).println( "Bad location");
      //System.err.println( "Bad location" );
      return;
    }
  }

  public void setText( String strText)
  {
    try
    {
      m_mutLock.acquire( IdManager.LOCKTIMEOUT);
      m_strText = strText;

      if( m_iMaxUndo > 0)
      {
        String strOld = super.getText();
        UndoData u = new UndoData( UndoData.NEW, 0, 0, strOld,
          UndoData.NEW, 0, 0, strText);
        addUndo( u);
      }
      super.setText( m_strText);
      setCaretPosition( 0 );
      SwingUtilities.invokeLater( new TextParser());
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
  }

  public void setSearchString( String strSearch)
  {
    m_strSearch = strSearch;
  }

  public String getSearchString()
  {
    return m_strSearch;
  }

  public void setText( File dir, String strLabel, Vector vecFilters)
  {
    if( !dir.isDirectory() )
    {
      GlobalAppProperties.instance().getPrintStreamOutput( m_strAppName).println( "Error: File is not a directory!");
      //System.err.println( "Error: File is not a directory!" );
      return;
    }
    String strText = parseFileStructure( dir, strLabel, vecFilters );

    setText( strText);
  }

  private void parseAndSetTextStyle()
  {
    try
    {
      m_mutLock.acquire( IdManager.LOCKTIMEOUT);
      m_strText = getText();
      int iStyle = NORMAL;
      int iPosStart = 0;
      int iLength = 0;
      if( m_strText == null || m_strText.equals( ""))
        return;
      int iLen = m_strText.length();
      for( int i = 0; i < iLen; ++i)
      {
        char c = m_strText.charAt( i);
        switch( c)
        {
          case '{':
            setTextStyle( iPosStart, iLength, iStyle);
            iPosStart += iLength;
            iLength = 1;
            iStyle = BOLDITALIC;
            break;

          case '}':
            ++iLength;
            setTextStyle( iPosStart, iLength, iStyle);
            iPosStart += iLength;
            iLength = 0;
            iStyle = NORMAL;
            break;

          case '[':
            setTextStyle( iPosStart, iLength, iStyle);
            iPosStart += iLength;
            iLength = 1;
            iStyle = BOLD;
            break;

          case ']':
            ++iLength;
            setTextStyle( iPosStart, iLength, iStyle);
            iPosStart += iLength;
            iLength = 0;
            iStyle = NORMAL;
            break;

          default:
            ++iLength;
            break;
        }
      }
      setTextStyle( iPosStart, iLength, iStyle);
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
  }

  public String parseFileStructure( File dir, String strLabel, Vector vecFilters)
  {
    String strRet = parseFileStructure( dir, 0, "", vecFilters);
    if( strRet == null)
      return strRet;
    if( strLabel != null && !strLabel.equals( ""))
    {
      if( strRet.startsWith( "\n"))
        strRet = "{" + strLabel + "}" + strRet;
      else
        strRet = "{" + strLabel + "}\n" + strRet;
    }
    return strRet;
  }

  private String parseFileStructure( File dir, int iLevel, String strParent, Vector vecFilters)
  {
    String strRet = "";

    File[] files = dir.listFiles();

    if( files != null)
    {
      Vector vecPrevNames = new Vector();

      int iLen = Array.getLength( files);
      if( iLen > 0)
      {
        Arrays.sort( files, new DefaultComparator() );
        strRet = "";

        for( int i = 0; i < iLen; ++i)
        {
          if( files[i].isDirectory())
          {
            if( !strRet.equals( ""))
            {
              strRet += ";";
            }
            strRet += "\n[";
            String strTmpParent = strParent + File.separatorChar + files[i].getName();
            if( strParent == null || strParent.equals( ""))
              strTmpParent = files[i].getName();
            else
              strTmpParent = strParent + File.separatorChar + files[i].getName();
            strRet += strTmpParent + "] ";
            strRet += parseFileStructure( files[i], iLevel+1, strTmpParent, vecFilters);
          }
          else
          {
            String strFileName = "";
            if( !strRet.equals( ""))
            {
                strFileName += ", ";
            }
            String strTmpName = files[i].getName();
            if( vecFilters != null)
            {
              boolean blBreak = false;
              for( int iV = 0; iV < vecFilters.size(); ++iV)
              {
                String str = (String)vecFilters.elementAt( iV);
                if( strTmpName.endsWith( str))
                {
                  blBreak = true;
                  break;
                }
              }
              if( blBreak)
                continue;
            }
            int iIdx = strTmpName.lastIndexOf( '.');
            if( iIdx > 0)
              strTmpName = strTmpName.substring( 0, iIdx);

            if( vecPrevNames.contains( strTmpName))
            {
              continue;
            }
            vecPrevNames.add( strTmpName);
            strFileName += strTmpName;
            int iNameLen = strFileName.length();
            int iLineLen = 0;
            int iLastIdx = strRet.lastIndexOf( '\n');
            if( iLastIdx == -1)
              iLineLen = strRet.length() + strParent.length() + 2;
            else
              iLineLen = strRet.length() - iLastIdx;
            iLineLen += iNameLen;
            if( iLineLen >= WARP)
            {
              strRet += ",\n";
              strFileName = strTmpName;
            }
            strRet += strFileName;
          }
        }
      }
    }
    if( iLevel == 0 && strRet != null && !strRet.equals( ""))
      strRet += ";";
    return strRet;
  }

  public int print(Graphics g, PageFormat pageFormat, int iPageIndex)
    throws PrinterException
  {
    RepaintManager.currentManager( this).setDoubleBufferingEnabled(false);

    Graphics2D  g2 = (Graphics2D) g;
    g2.setColor( Color.black);
    int iFontHeight = g2.getFontMetrics().getHeight();
    int iFontDesent = g2.getFontMetrics().getDescent();
    int iFontAscent = g2.getFontMetrics().getAscent();

    //leave room for page number
    //double dPageHeight = pageFormat.getImageableHeight() - iFontHeight * 2;
    //double dPageWidth = pageFormat.getImageableWidth();
    //double dPageHeight = pageFormat.getImageableHeight();
    double dPageWidth = 340;
    double dPageHeight = 665;
    double dLineWidth = (double)(getFontMetrics( m_doc.getFont( m_sas[BOLD])).charWidth( 'W') * WARP);
    double dCharHeight = ((double)getFontMetrics( m_doc.getFont( m_sas[BOLD])).getHeight()) + m_iPrinterAdjust;
    StringTokenizer st = new StringTokenizer( m_strText, "\n", false);
    int iRows = st.countTokens();
    double scale = 1;

    if (dLineWidth >= dPageWidth)
    {
      scale =  dPageWidth / dLineWidth;
    }

    //double headerHeightOnPage = this.getTableHeader().getHeight() * scale;
    double dLineWidthOnPage = dLineWidth * scale;

    double oneRowHeight = dCharHeight * scale;
    int numRowsOnAPage = (int)( (dPageHeight) / oneRowHeight);
    double dPageHeightForTable = oneRowHeight * numRowsOnAPage;
    int totalNumPages = (int)Math.ceil( ((double)iRows) / numRowsOnAPage);

    if(iPageIndex>=totalNumPages)
    {
      return NO_SUCH_PAGE;
    }
    g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY());

    //g2.translate( 0f, headerHeightOnPage);
    g2.translate( 0f, iFontAscent*2);
    g2.translate( 0f, -iPageIndex * dPageHeightForTable);

    //If this piece of the table is smaller than the size available,
    //clip to the appropriate bounds.
    if( iPageIndex + 1 == totalNumPages)
    {
      int lastRowPrinted = numRowsOnAPage * iPageIndex;
      int numRowsLeft = iRows - lastRowPrinted;
      g2.setClip( 0, (int)( dPageHeightForTable * iPageIndex), (int)Math.ceil( dLineWidthOnPage),
        (int)Math.ceil( oneRowHeight * numRowsLeft));
    }
    //else clip to the entire area available.
    else
    {
      g2.setClip( 0, (int)( dPageHeightForTable * iPageIndex), (int)Math.ceil(dLineWidthOnPage),
        (int)Math.ceil( dPageHeightForTable));
    }

    g2.scale( scale, scale);

    //g2.translate( 0f, -iPageIndex);
    g2.draw3DRect( 0, (int)(iPageIndex*2*(dLineWidthOnPage*2)), (int)(dLineWidthOnPage*2.05), (int)(dLineWidthOnPage*2), true);
    if( iRows > (numRowsOnAPage/2)*((iPageIndex*2)+1))
      g2.draw3DRect( 0, (int)((iPageIndex*2+1)*(dLineWidthOnPage*2)), (int)(dLineWidthOnPage*2.05), (int)(dLineWidthOnPage*2), true);
    try
    {
      this.paint( g2);
    }
    catch( IllegalArgumentException iaex)
    {
      iaex.printStackTrace();
    }
    //g2.translate( 0f, -iPageIndex);
    g2.draw3DRect( 0, (int)(iPageIndex*2*(dLineWidthOnPage*2)), (int)(dLineWidthOnPage*2.05), (int)(dLineWidthOnPage*2), true);
    if( iRows > (numRowsOnAPage/2)*((iPageIndex*2)+1))
      g2.draw3DRect( 0, (int)((iPageIndex*2+1)*(dLineWidthOnPage*2)), (int)(dLineWidthOnPage*2.05), (int)(dLineWidthOnPage*2), true);

    return Printable.PAGE_EXISTS;
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
          case KeyEvent.VK_F3:
            find( false);
            break;

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
              strOld = strOld.substring( iSelStart, iSelEnd);
              UndoData u = new UndoData( UndoData.INSERT, iSelStart, iSelStart, strOld,
                UndoData.REPLACE, iSelStart, iSelEnd, "");
              addUndo( u);
            }
            break;
        }
        break;
    }
    super.processComponentKeyEvent( e);
    String strText = getText();
    if( !strText.equals( m_strText))
    {
      SwingUtilities.invokeLater( new TextParser() );
    }
  }

  class TextParser
    extends Thread
  {
    public void run()
    {
      try
      {
        parseAndSetTextStyle();
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  public static class DefaultComparator
    implements Comparator
  {
    public int compare (Object o1, Object o2)
    {
      File f1 = (File)o1;
      File f2 = (File)o2;

      if (f1.isDirectory() && !f2.isDirectory())
        return 1;

      if (!f1.isDirectory() && f2.isDirectory())
        return -1;

      return f1.getName().compareTo(f2.getName());
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
                     int iActionRedo, int iStartPosRedo, int iEndPosRedo, String strRedo )
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

    public void undo( ContentTextArea pane )
    {
      int iLen = 0;
      switch( m_iActionUndo )
      {
        case NEW:
          pane.selectAll();
          pane.replaceSelectionWithoutUndo( m_strUndo );
          break;

        case REPLACE:
          iLen = pane.getText().length();
          if( m_iStartPosUndo <= iLen && m_iEndPosUndo <= iLen )
          {
            pane.setSelectionStart( m_iStartPosUndo );
            pane.setSelectionEnd( m_iEndPosUndo );
            pane.replaceSelectionWithoutUndo( m_strUndo );
          }
          break;

        case INSERT:
          iLen = pane.getText().length();
          if( m_iStartPosUndo <= iLen )
          {
            pane.setSelectionStart( m_iStartPosUndo );
            pane.setSelectionEnd( m_iStartPosUndo );
            pane.replaceSelectionWithoutUndo( m_strUndo );
          }
          break;

        default:
          break;
      }
    }

    public void redo( ContentTextArea pane )
    {
      int iLen = 0;
      switch( m_iActionRedo )
      {
        case NEW:
          pane.selectAll();
          pane.replaceSelectionWithoutUndo( m_strRedo );
          break;

        case REPLACE:
          iLen = pane.getText().length();
          if( m_iStartPosRedo <= iLen && m_iEndPosRedo <= iLen )
          {
            pane.setSelectionStart( m_iStartPosRedo );
            pane.setSelectionEnd( m_iEndPosRedo );
            pane.replaceSelectionWithoutUndo( m_strRedo );
          }
          break;

        case INSERT:
          iLen = pane.getText().length();
          if( m_iStartPosRedo <= iLen )
          {
            pane.setSelectionStart( m_iStartPosRedo );
            pane.setSelectionEnd( m_iStartPosRedo );
            pane.replaceSelectionWithoutUndo( m_strRedo );
          }
          break;

        default:
          break;
      }
    }

    public boolean equals( Object obj )
    {
      if( obj instanceof UndoData )
      {
        UndoData u = ( UndoData )obj;
        return( ( ( getUndoString() == null && u.getUndoString() == null )
                  || ( ( getUndoString() != null && u.getUndoString() != null )
                       && getUndoString().equals( u.getUndoString() ) ) )
                && ( ( getRedoString() == null && u.getRedoString() == null )
                     || ( ( getRedoString() != null && u.getRedoString() != null )
                          && getRedoString().equals( u.getRedoString() ) ) )
                && u.getUndoStartPos() == getUndoStartPos() && u.getUndoEndPos() == getUndoEndPos()
                && u.getRedoStartPos() == getRedoStartPos() && u.getRedoEndPos() == getRedoEndPos()
                && u.getUndoAction() == getUndoAction() );
      }
      return false;
    }
  }
}

