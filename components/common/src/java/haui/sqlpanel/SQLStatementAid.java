package haui.sqlpanel;

import haui.dbtool.DbQuery;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;

/**
 * Module:      SQLStatementAid.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\sqlpanel\\SQLStatementAid.java,v $ <p> Description: SQLStatementAid.<br> </p><p> Created:     17.02.2004 by AE </p><p>
 * @history  	    17.02.2004 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: SQLStatementAid.java,v $  Revision 1.0  2004-06-22 14:07:00+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: 1.0 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\sqlpanel\\SQLStatementAid.java,v 1.0 2004-06-22 14:07:00+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.3  </p>
 */
public class SQLStatementAid
  extends JScrollPane
{
  private static final long serialVersionUID = -6529951750226912017L;
  
  // constants
  protected final static short ERROR = 0;
  protected final static short SCHEMA = 1;
  protected final static short TABLE = 2;
  protected final static short COLUMN = 3;

  // GUI member variables
  JComboBox m_jComboBoxMain = new JComboBox();
  JTextComponent m_jTextComponent;
  JList m_jList = new JList();
  DefaultListModel m_dlm = new DefaultListModel();


  // member variables
  Component m_compParent;
  DbQuery m_dbQuery;
  String m_strDriver;
  String m_strUrl;
  String m_strUid;
  String m_strPwd;
  String m_strSchema;
  String m_strSchemaCurrent;
  String m_strTableCurrent;
  boolean m_blTabInitialized = false;
  String m_strText = "";
  int m_iStartPos = 0;
  boolean m_blCont = false;
  HashMap m_hmAlias = new HashMap();

  /**
   * Constructor.
   *
   * @param strDriver:      DB Driver e.g.: "sun.jdbc.odbc.JdbcOdbcDriver"
   * @param strUrl:         DB URL e.g.: "jdbc:odbc:SBV"
   * @param strUid:         User id to connect the DB.
   * @param strPwd:         Password to connect the DB.
   *
   */
  public SQLStatementAid( Component compParent, String strDriver, String strUrl, String strUid, String strPwd, String strSchema)
  {
    m_compParent = compParent;
    m_strDriver = strDriver;
    m_strUrl = strUrl;
    m_strUid = strUid;
    m_strPwd = strPwd;
    m_strSchema = strSchema;
    m_dbQuery = new DbQuery();
    m_dbQuery.dbConnect( strDriver, strUrl, strUid, strPwd);

    m_jList.setModel( m_dlm);
    m_jList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
    setWheelScrollingEnabled( true);
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    // add mouse handler to list
    //MouseHandler mh = new MouseHandler();
    //m_jList.addMouseListener( mh);
  }

  private void jbInit() throws Exception
  {
    setViewportView( m_jList);
  }

  public JList getList()
  {
    return m_jList;
  }

  protected void parse()
  {
    String strComplText = m_jTextComponent.getText();
    strComplText = strComplText.replaceAll( "\r\n", "\n");
    int iComplLen = strComplText.length();
    int iIdx = iComplLen;
    m_hmAlias.clear();

    while( iIdx == iComplLen || iIdx > -1)
    {
      char c;
      boolean blEnd = false;
      int i = 0;
      short sLevel = 0;
      String strLevel0 = "";
      String strLevel1 = "";
      String strText = "";
      try
      {
        for( i = iIdx - 1; i >= 0 && !blEnd; --i )
        {
          c = m_jTextComponent.getText( i, 1 ).charAt( 0 );
          switch( c )
          {
            case ' ':
            case '\n':
            case '\t':
            case ',':
              switch( sLevel )
              {
                case 0:
                  ++sLevel;
                  strLevel0 = strText;
                  strText = "";
                  if( c == '\n')
                    blEnd = true;
                  break;

                case 1:
                  ++sLevel;
                  strLevel1 = strText;
                  strText = "";
                  blEnd = true;
                  break;
              }
              break;

            case '.':
              switch( sLevel )
              {
                case 0:
                  strLevel0 = "";
                  strText = "";
                  blEnd = true;
                  break;

                case 1:
                  ++sLevel;
                  strLevel1 = strText;
                  strText = "";
                  blEnd = true;
                  continue;

                default:
                  blEnd = true;
                  break;
              }
              break;

            case '(':
            case ')':
              blEnd = true;
              break;

            default:
              strText = c + strText;
              break;
          }
        }
        if( i < 0 )
        {
          switch( sLevel )
          {
            case 0:
              strLevel0 = strText;
              strText = "";
              break;

            case 1:
              strLevel1 = strText;
              strText = "";
              break;
          }
        }
        if( !strLevel0.equals( "") && !strLevel1.equals( "") && !isKeyword( strLevel0) && !isKeyword( strLevel1)
             && !hasOperator( strLevel0) && !hasOperator( strLevel1))
        {
          m_hmAlias.put( strLevel0, strLevel1);
        }
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
      int iIdx1 = strComplText.toLowerCase().lastIndexOf( ",", iIdx - 1);
      int iIdx2 = strComplText.toLowerCase().lastIndexOf( "\n", iIdx - 1);
      int iIdx3 = strComplText.toLowerCase().lastIndexOf( " ", iIdx - 1);
      if( iIdx1 < iIdx2)
      {
        if( iIdx2 < iIdx3)
          iIdx = iIdx3;
        else
          iIdx = iIdx2;
      }
      else
      {
        if( iIdx1 < iIdx3)
          iIdx = iIdx3;
        else
          iIdx = iIdx1;
      }
    }
  }

  protected boolean isKeyword( String str)
  {
    String[] strKeys = SQLParser.getKeywords();
    boolean blRet = false;

    for( int i = 0; i < strKeys.length; ++i)
    {
      if( strKeys[i].equalsIgnoreCase( str))
      {
        blRet = true;
        break;
      }
    }

    return blRet;
  }

  protected boolean hasOperator( String str)
  {
    char[] cOps = SQLParser.getOperators();
    boolean blRet = false;

    for( int i = 0; i < cOps.length; ++i)
    {
      if( str.indexOf( String.valueOf( cOps[i])) != -1)
      {
        blRet = true;
        break;
      }
    }

    return blRet;
  }

  public Rectangle show( JTextComponent jTextComponent)
  {
    Rectangle rec = null;
    m_hmAlias.clear();
    m_jTextComponent = jTextComponent;
    m_dlm.removeAllElements();
    m_blCont = false;

    if( m_jTextComponent == null)
      return rec;
    try
    {
      m_strSchemaCurrent = m_strSchema;
      m_strTableCurrent = "";
      int iPos = m_jTextComponent.getSelectionStart();
      short sLevel = 0;
      String strText = m_jTextComponent.getSelectedText();
      String strLevel0 = "";
      String strLevel1 = "";
      String strLevel2 = "";

      if( strText == null)
        strText = "";

      boolean blEnd = false;
      int iPosPopup = 0;
      int i = iPos;
      int iLen = m_jTextComponent.getText().length();
      for( i = iPos; i < iLen && !blEnd; ++i)
      {
        char c = m_jTextComponent.getText( i, 1).charAt( 0);
        switch( c)
        {
          case ' ':
          case '\n':
          case '\t':
          case ',':
            blEnd = true;
            break;

          case '.':
            blEnd = true;
            break;

          default:
            strText += c;
            break;
        }
      }
      blEnd = false;
      for( i = iPos-1; i >= 0 && !blEnd; --i)
      {
        char c = m_jTextComponent.getText( i, 1).charAt( 0);
        switch( c)
        {
          case ' ':
          case '\n':
          case '\t':
          case ',':
            switch( sLevel)
            {
              case 0:
                ++sLevel;
                strLevel0 = strText;
                strText = "";
                iPosPopup = i+1;
                break;

              case 1:
                ++sLevel;
                strLevel1 = strText;
                strText = "";
                break;

              case 2:
                ++sLevel;
                strLevel2 = strText;
                strText = "";
                break;
            }
            blEnd = true;
            break;

          case '.':
            switch( sLevel)
            {
              case 0:
                m_blCont = true;
                ++sLevel;
                strLevel0 = strText;
                strText = "";
                iPosPopup = i+1;
                continue;

              case 1:
                ++sLevel;
                strLevel1 = strText;
                strText = "";
                continue;

              case 2:
                ++sLevel;
                strLevel2 = strText;
                strText = "";
                blEnd = true;
                break;

              default:
                blEnd = true;
                break;
            }
            break;

          default:
            strText = c + strText;
            break;
        }
      }
      if( i < 0)
      {
        switch( sLevel)
        {
          case 0:
            strLevel0 = strText;
            strText = "";
            iPosPopup = i+1;
            break;

          case 1:
            strLevel1 = strText;
            strText = "";
            break;

          case 2:
            strLevel2 = strText;
            strText = "";
            break;
        }
      }
      m_iStartPos = iPosPopup;
      rec = m_jTextComponent.modelToView( iPosPopup );

      short sState = getState( strLevel0, strLevel1, strLevel2);

      switch( sState)
      {
        case SCHEMA:
          setSchemaNames();
          break;

        case TABLE:
          setTableNames();
          break;

        case COLUMN:
          setColumnNames();   // m_strText is set in function getState
          break;

        case ERROR:
          rec = null;
          break;
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    return rec;
  }

  public void close()
  {
    m_dbQuery.close( 0);
    m_dbQuery.closeConnection();
  }

  public int getStartPosition()
  {
    return m_iStartPos;
  }

  public int getEndPosition()
  {
    int iRet = m_iStartPos;
    int i = iRet;
    boolean blEnd = false;
    try
    {
      for( i = iRet; i < m_jTextComponent.getText().length() && !blEnd; ++i )
      {
        char c = m_jTextComponent.getText( i, 1 ).charAt( 0 );
        switch( c )
        {
          case ' ':
          case '\n':
          case '\t':
          case '.':
          case ',':
            blEnd = true;
            break;
        }
      }
      if( !blEnd )
        ++i;
      iRet = i - 1;
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    return iRet;
  }

  protected void setSchemaNames()
  {
    int iCount = m_dbQuery.getSchemaCount();

    for( int i = 0; i < iCount; ++i)
    {
      m_dlm.addElement( m_dbQuery.getSchemaName( i));
    }
    if( m_dlm.size() > 0)
      m_jList.setSelectedIndex( 0);
    ((JInternalFrame)m_compParent).setTitle( "Schema");
  }

  protected void setTableNames()
  {
    String[] strTypes = new String[2];
    strTypes[0] = "TABLE";
    strTypes[1] = "VIEW";
    String strSchema = m_strSchemaCurrent;
    if( strSchema == null || strSchema.equals( ""))
      strSchema = m_strSchema;

    if( m_blTabInitialized || m_dbQuery.getTabs( strTypes, strSchema) == DbQuery.OK)
    {
      m_blTabInitialized = true;
      int iCount = m_dbQuery.getTabCount();

      for( int i = 0; i < iCount; ++i)
      {
        m_dlm.addElement( m_dbQuery.getTabName( i));
      }
      if( m_dlm.size() > 0)
        m_jList.setSelectedIndex( 0);
      ((JInternalFrame)m_compParent).setTitle( "Table");
    }
  }

  protected void setColumnNames()
  {
    String strSchema = m_strSchemaCurrent;
    if( strSchema == null || strSchema.equals( ""))
      strSchema = m_strSchema;
    String strTable = m_strTableCurrent;
    if( strTable == null || strTable.equals( ""))
      strTable = "%";
    String strColumn = "%";

    if( m_dbQuery.getColumns( null, strSchema, strTable, strColumn, 0) == DbQuery.OK)
    {
      while( m_dbQuery.next( 0))
      {
        String strCol = (String)m_dbQuery.getColValue( "COLUMN_NAME", 0);

        if( strCol != null && !strCol.equals( ""))
        {
          if( !m_dlm.contains( strCol))
          {
            boolean blInserted = false;
            for( int j = 0; j < m_dlm.size(); ++j )
            {
              String str = ( String )m_dlm.elementAt( j );

              if( str.compareTo( strCol ) > 0 )
              {
                m_dlm.insertElementAt( strCol, j );
                blInserted = true;
                break;
              }
            }
            if( !blInserted )
              m_dlm.addElement( strCol );
          }
        }
      }
      if( m_dlm.size() > 0)
        m_jList.setSelectedIndex( 0);
      ((JInternalFrame)m_compParent).setTitle( "Column");
    }
  }

  protected String getTextBefore()
  {
    boolean blEnd = false;
    int iPos = m_jTextComponent.getSelectionStart();
    String strText = m_jTextComponent.getSelectedText();
    if( strText == null)
      strText = "";

    try
    {
      for( int i = iPos - 1; i >= 0 && !blEnd; --i )
      {
        char c = m_jTextComponent.getText( i, 1 ).charAt( 0 );
        switch( c )
        {
          case ' ':
          case '\n':
          case '\t':
          case ',':
            blEnd = true;
            break;

          case '.':
            blEnd = true;
            break;

          default:
            strText = c + strText;
            break;
        }
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    return strText;
  }

  public void moveSelectionHome()
  {
    int i = 0;
    m_jList.setSelectedIndex( i );
    moveScrollbarToSelection( i );
  }

  public void moveSelectionEnd()
  {
    int i = m_dlm.size()-1;
    m_jList.setSelectedIndex( i );
    moveScrollbarToSelection( i );
  }

  public void moveSelectionUp()
  {
    int i = m_jList.getSelectedIndex()-1;
    if( i >= 0)
    {
      m_jList.setSelectedIndex( i );
      moveScrollbarToSelection( i );
    }
  }

  public void moveSelectionDown()
  {
    int i = m_jList.getSelectedIndex()+1;
    if( i < m_dlm.size())
    {
      m_jList.setSelectedIndex( i );
      moveScrollbarToSelection( i );
    }
  }

  public void moveSelectionPageUp()
  {
    int i = m_jList.getSelectedIndex();
    //JScrollBar jsb = getVerticalScrollBar();
    int iStep = m_jList.getLastVisibleIndex() - m_jList.getFirstVisibleIndex();
    i -= iStep;
    if( i < 0)
      i = 0;
    m_jList.setSelectedIndex( i );
    moveScrollbarToSelection( i );
  }

  public void moveSelectionPageDown()
  {
    int i = m_jList.getSelectedIndex();
    //JScrollBar jsb = getVerticalScrollBar();
    int iStep = m_jList.getLastVisibleIndex() - m_jList.getFirstVisibleIndex();
    i += iStep;
    if( i >= m_dlm.size()-1)
      i = m_dlm.size()-1;
    m_jList.setSelectedIndex( i );
    moveScrollbarToSelection( i );
  }

  public void setSelection()
  {
    String strText = getTextBefore();

    if( strText != null && !strText.equals(""))
    {
      for( int i = 0; i < m_dlm.size(); ++i )
      {
        String str = ( String )m_dlm.elementAt( i );
        if( str.toLowerCase().indexOf( strText.toLowerCase() ) == 0 )
        {
          m_jList.setSelectedIndex( i );
          break;
        }
      }
    }
    int i = m_jList.getSelectedIndex();
    if( i >= 0)
      moveScrollbarToSelection( i);
  }

  protected void setSelectionInternalText()
  {
    if( m_strText != null && !m_strText.equals(""))
    {
      for( int i = 0; i < m_dlm.size(); ++i )
      {
        String str = ( String )m_dlm.elementAt( i );
        if( str.toLowerCase().indexOf( m_strText.toLowerCase() ) == 0 )
        {
          m_jList.setSelectedIndex( i );
          break;
        }
      }
    }
    int i = m_jList.getSelectedIndex();
    if( i >= 0)
      moveScrollbarToSelection( i);
  }

  private void moveScrollbarToSelection( int i)
  {
    JScrollBar jsb = getVerticalScrollBar();
    int iMax = jsb.getMaximum();
    int iLVal = i*100/m_dlm.size();
    int iValue = iLVal*iMax/100;
    jsb.setValue( iValue);
  }

  protected short getState( String strLevel0, String strLevel1, String strLevel2)
  {
    short sState = ERROR;

    if( strLevel0.equals( "") && strLevel1.equals( "") && strLevel2.equals( ""))
    {
      sState = TABLE;
      return sState;
    }
    else
    {
      //strLevel0 = getText( strLevel0);
      strLevel1 = getText( strLevel1);
      strLevel2 = getText( strLevel2);
      boolean blSchema = false;
      boolean blTable = false;

      if( !strLevel2.equals( ""))
      {
        if( isSchema( strLevel2, true))
        {
          m_strSchemaCurrent = strLevel2;
          blSchema = true;
        }
      }

      if( !strLevel1.equals( ""))
      {
        if( !blSchema && isSchema( strLevel1, true))
        {
          m_strSchemaCurrent = strLevel1;
          blSchema = true;
        }
        else if( isTable( strLevel1, true))
        {
          m_strTableCurrent = strLevel1;
          blTable = true;
        }
      }

      if( !strLevel0.equals( ""))
      {
        m_strText = strLevel0;
        String strLowText = strLevel0.toLowerCase();
        if( !blSchema && isSchema( strLevel0, false))
        {
          sState = SCHEMA;
        }
        else if( !blTable && isTable( strLevel0, false))
        {
          sState = TABLE;
        }
        else
        {
          String strSchema = m_strSchemaCurrent;
          if( strSchema == null || strSchema.equals( ""))
            strSchema = m_strSchema;
          String strTable = m_strTableCurrent;
          if( strTable == null || strTable.equals( ""))
            strTable = "%";

          if( m_dbQuery.getColumns( null, strSchema, strTable, "%", 0) == DbQuery.OK)
          {
            while( m_dbQuery.next( 0))
            {
              String strCol = (String)m_dbQuery.getColValue( "COLUMN_NAME", 0);

              if( strCol.toLowerCase().indexOf( strLowText) != -1)
              {
                sState = COLUMN;
              }
            }
          }
          if( sState == ERROR)
          {
            if( blTable)
              sState = COLUMN;
            else if( blSchema)
              sState = TABLE;
            m_strText = "";
          }
        }
      }
      else
      {
        if( blSchema && blTable)
          sState = COLUMN;
        else if( blSchema)
          sState = TABLE;
        else if( blTable)
          sState = COLUMN;
        else
          sState = ERROR;
      }
    }
    return sState;
  }

  protected boolean isSchema( String str, boolean blExact)
  {
    boolean blRet = false;
    int iSchCount = m_dbQuery.getSchemaCount();

    for( int i = 0; i < iSchCount; ++i )
    {
      if( blExact)
      {
        if( m_dbQuery.getSchemaName( i ).equalsIgnoreCase( str ))
        {
          blRet = true;
          break;
        }
      }
      else
      {
        if( m_dbQuery.getSchemaName( i ).toLowerCase().indexOf( str.toLowerCase() ) != -1 )
        {
          blRet = true;
          break;
        }
      }
    }
    return blRet;
  }

  protected boolean isTable( String str, boolean blExact)
  {
    boolean blRet = false;
    String[] strTypes = new String[2];
    strTypes[0] = "TABLE";
    strTypes[1] = "VIEW";

    if( m_blTabInitialized || m_dbQuery.getTabs( strTypes, m_strSchema ) == DbQuery.OK )
    {
      m_blTabInitialized = true;
      int iCount = m_dbQuery.getTabCount();

      for( int i = 0; i < iCount; ++i )
      {
        if( blExact)
        {
          if( m_dbQuery.getTabName( i ).equalsIgnoreCase( str ))
          {
            blRet = true;
            break;
          }
        }
        else
        {
          if( m_dbQuery.getTabName( i ).toLowerCase().indexOf( str.toLowerCase() ) != -1 )
          {
            blRet = true;
            break;
          }
        }
      }
    }
    return blRet;
  }

  protected String getText( String strText)
  {
    boolean blParsed = false;
    if( strText == null || strText.equals( ""))
      return strText;

    if( m_hmAlias.size() <= 0)
    {
      parse();
      blParsed = true;
    }
    String strRet = (String)m_hmAlias.get( strText);
    if( !blParsed && strRet == null)
      parse();

    if( strRet == null)
      strRet = strText;

    return strRet;
  }

  /*
  protected String getText( String strText)
  {
    if( strText == null || strText.equals( ""))
      return strText;
    String strRet = strText;
    int iLen = strRet.length();
    int iComplLen = m_jTextComponent.getText().length();
    String strComplText = "";
    boolean blFound = false;

    try
    {
      strComplText = m_jTextComponent.getText( 0, iComplLen-iLen-1);
    }
    catch( BadLocationException blex)
    {
      blex.printStackTrace();
    }

    int iIdx = -1;
    while( (iIdx = strComplText.toLowerCase().indexOf( strRet, iIdx + 1)) > -1)
    {
      char c;
      if( iIdx - 1 > 0 )
      {
        c = strComplText.charAt( iIdx - 1 );
        if( c == ' ' || c == '\n' || c == '\t' || c == ',' )
          blFound = true;
        else
          blFound = false;
      }
      else if( iIdx - 1 == 0)
        blFound = true;

      if( blFound && iIdx + iLen < iComplLen )
      {
        c = strComplText.charAt( iIdx + iLen);
        if( c == ' ' || c == '\n' || c == '\t' || c == ',' )
          blFound = true;
        else
          blFound = false;
      }
      if( blFound)
        break;
    }

    if( blFound)
    {
      boolean blEnd = false;
      int iPos = iIdx;
      String strRealText = "";
      boolean blStart = false;

      try
      {
        for( int i = iPos - 1; i >= 0 && !blEnd; --i )
        {
          char c = m_jTextComponent.getText( i, 1 ).charAt( 0 );
          switch( c )
          {
            case ' ':
            case '\n':
            case '\t':
              if( blStart)
                blEnd = true;
              break;

            case '.':
              if( blStart)
                blEnd = true;
              break;

            default:
              blStart = true;
              strRealText = c + strRealText;
              break;
          }
        }
        if( !strRealText.equals( ""))
          strRet = strRealText;
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }

    return strRet;
  }
  */

  public void replaceText()
  {
    String str = getListSelection();
    if( str != null && !str.equals( ""))
    {
      m_jTextComponent.setSelectionStart( m_iStartPos );
      m_jTextComponent.setSelectionEnd( getEndPosition() );
      m_jTextComponent.replaceSelection( str);
    }
  }

  public String getListSelection()
  {
    String strRet = null;
    strRet = (String)m_jList.getSelectedValue();
    return strRet;
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      //Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2)
        onLeftMouseDoublePressed( event);
    }
  }

  void onLeftMouseDoublePressed( MouseEvent event)
  {
    replaceText();
    ((JInternalFrame)m_compParent).setVisible( false);
  }
}