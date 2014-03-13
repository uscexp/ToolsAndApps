
package haui.dbtool;

import haui.components.JExDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RepaintManager;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.UIDefaults;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.TableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Module:					TablePanel.java <p> Description:		Panel which implements a table view to a DB. </p><p> Created:				27.03.1998	by	AE </p><p> Last Modified:	08.09.2000	by	AE </p><p> history					14.10.1998 - 15.10.1998	by	AE:	DbTableAdapter functions added<br> 19.10.1998	by	AE:	JTable functions added<br> 19.10.1998	by	AE:	setColNonEditable functions added<br> 20.10.1998	by	AE:	setColCellRenderer function added<br> 20.10.1998	by	AE:	setColCellEditor function added<br> 23.10.1998	by	AE:	Error eliminated in setColCellRenderer and setColCellEditor functions<br> 28.10.1998	by	AE:	setColCellEditor function changed in setTextCellEditor and setComboCellEditor, setTableCellEditor function added<br> 29.10.1998	by	AE:	changed interface of setTableCellEditor function<br> 29.10.1998	by	AE:	isColNullable function added<br> 30.10.1998	by	AE:	addRow, insertRow, moveRow, removeRow function added<br> 30.10.1998	by	AE:	getTableAdapter function added<br> 30.10.1998	by	AE:	changed interface of setTableCellEditor function<br> 02.11.1998	by	AE:	changed return value of setTextCellEditor and setComboCellEditor, setTableCellEditor function<br> 03.11.1998	by	AE:	changed interface of setTableCellEditor function (strDlgTitle added)<br> 04.11.1998	by	AE:	getTableName and isEditable function added<br> 10.11.1998	by	AE:	getUsePrimKey and setUsePrimKey function added<br> 10.11.1998	by	AE:	setValueAt function with additional strPrimKey added<br> 26.11.1998	by	AE:	setCellEditor function added<br> 26.11.1998	by	AE:	Return value changed in setComboCellEditor and setTextCellEditor function<br> 26.11.1998	by	AE:	stopCellEditing function added<br> 15.04.1999	by	AE:	Converted to JDK v1.2<br> 07.03.2000	by	AE:	setCalcColMinWidth function added<br> 09.03.2000	by	AE:	JDK repaint bug workaround within scrollpanes (paint())<br> 04.08.2000	by	AE:	Added m_blConnect to control auto connection dialog<br> 07.09.2000	by	AE: Printing functionality added.<br> 08.09.2000	by	AE: Limit rows of result set added.<br> 13.08.2001	by	AE: setAutoCommit, getAutoCommit, commit, rollback added.<br> 03.09.2001	by	AE: getColumns, getTables, getSchemas,getcatalogs added.<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999,2000  </p><p>
 * @since  					JDK1.2  </p>
 */
public class TablePanel extends JPanel
  implements Constant, Printable
{
  private static final long serialVersionUID = 4474663455818408575L;

  /** CellRenderer/CellEditor constant: TextArea */
  public static final int CELLTEXTAREA = 1;

  String m_strAppName;
  JLabel m_labelErg;
  DbTableMap m_dbtabmap;
  public JExTable m_tableView;
  public DbTableAdapter m_dbtabadp;
  public JScrollPane m_scrollpaneTab;
  boolean m_blDbg;
  boolean m_blExDbg;
  boolean m_blLogMode;
  boolean m_blEditable;
  String m_strQuery;
  String m_strTable;
  String m_strDateFormat;
  static String m_strUsr = "";
  static String m_strPwd = "";
  static String m_strDriver = "";
  static String m_strUrl = "";
  JTextField m_textUsr;
  JPasswordField m_passwordPwd;
  JDialog m_dlgProBar;
  JDialog m_dlgLogin;
  JProgressBar m_probar;
  OutputStream m_printsOut;
  OutputStream m_printsErr;
  String m_strPrimKey;
  boolean m_blUsePrimKey;
  boolean m_blConnect;
  int m_maxNumPage;
  int m_iMaxRows;
  boolean m_blAutoCommit = true;
  DbQuery m_dbquery = null;

  public TablePanel( String strAppName)
  {
    m_strAppName = strAppName;
    m_labelErg = null;
    m_dbtabmap = null;
    m_tableView = null;
    m_dbtabadp = null;
    m_scrollpaneTab = null;
    m_blDbg = false;
    m_blExDbg = false;
    m_blLogMode = false;
    m_blEditable = true;
    m_blUsePrimKey = false;
    m_blConnect = true;
    m_strDateFormat = null;
    m_strQuery = "SELECT * FROM table";
    //setToolTipText("table: result set");
    setBackground( Color.lightGray); // set color because of problems with internal frames
    createPanel();
  }

  void createPanel()
  {
    m_labelErg = new JLabel("Results: ");
    m_scrollpaneTab = createTable();
    super.setLayout(new BorderLayout());
    add("North", m_labelErg);
    add("Center", m_scrollpaneTab);
  }

  JScrollPane createTable()
  {
    m_dbtabmap = new DbTableMap();
    m_tableView = new JExTable(m_dbtabmap);
    m_tableView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane jscrollpane = new JScrollPane(m_tableView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jscrollpane.getViewport().setBackground(new Color(255,255,255));
    return jscrollpane;
  }

  public JTable getTable()
  {
    return m_tableView;
  }

  public JPopupMenu getPopup()
  {
    return m_tableView.getPopup();
  }

  public void setPopup( JPopupMenu popup)
  {
    m_tableView.setPopup( popup);;
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex)
    throws PrinterException
  {
    RepaintManager.currentManager( m_tableView).setDoubleBufferingEnabled(false);

    Graphics2D  g2 = (Graphics2D) g;
    g2.setColor( Color.black);
    int fontHeight = g2.getFontMetrics().getHeight();
    int fontDesent = g2.getFontMetrics().getDescent();
    int fontAscent = g2.getFontMetrics().getAscent();

    //leave room for page number
    double pageHeight = pageFormat.getImageableHeight() - fontHeight * 2;
    double pageWidth = pageFormat.getImageableWidth();
    double tableWidth = (double)m_tableView.getColumnModel().getTotalColumnWidth();
    double scale = 1;

    if (tableWidth >= pageWidth)
    {
      scale =  pageWidth / tableWidth;
    }

    double headerHeightOnPage = m_tableView.getTableHeader().getHeight() * scale;
    double tableWidthOnPage = tableWidth * scale;

    double oneRowHeight = (m_tableView.getRowHeight() + m_tableView.getRowMargin()) * scale;
    int numRowsOnAPage = (int)( (pageHeight-headerHeightOnPage-fontAscent*2) / oneRowHeight);
    double pageHeightForTable = oneRowHeight * numRowsOnAPage;
    int totalNumPages = (int)Math.ceil( ((double)m_tableView.getRowCount()) / numRowsOnAPage);

    if(pageIndex>=totalNumPages)
    {
      return NO_SUCH_PAGE;
    }

    g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY());
    g2.drawString( "Page: " + ( pageIndex+1), (int)pageWidth / 2 - 35, (int)( pageHeight + fontHeight - fontDesent));//bottom center
    g2.drawString( (getTableName() == null) ? " " : getTableName(), 0, fontAscent);

    g2.translate( 0f, headerHeightOnPage);
    g2.translate( 0f, fontAscent*2);
    g2.translate( 0f, -pageIndex * pageHeightForTable);

    //If this piece of the table is smaller than the size available,
    //clip to the appropriate bounds.
    if( pageIndex + 1 == totalNumPages)
    {
      int lastRowPrinted = numRowsOnAPage * pageIndex;
      int numRowsLeft = m_tableView.getRowCount() - lastRowPrinted;
      g2.setClip( 0, (int)( pageHeightForTable * pageIndex), (int)Math.ceil( tableWidthOnPage),
        (int)Math.ceil( oneRowHeight * numRowsLeft));
    }
    //else clip to the entire area available.
    else
    {
      g2.setClip( 0, (int)( pageHeightForTable * pageIndex), (int)Math.ceil(tableWidthOnPage),
        (int)Math.ceil( pageHeightForTable));
    }

    g2.scale( scale, scale);
    try
    {
      m_tableView.setPrinting( true);
      m_tableView.paint( g2);
    }
    catch( IllegalArgumentException iaex)
    {
      m_tableView.setPrinting( false);
      iaex.printStackTrace();
    }
    m_tableView.setPrinting( false);
    g2.scale( 1 / scale, 1 / scale);
    g2.translate( 0f, pageIndex * pageHeightForTable);
    g2.translate( 0f, -headerHeightOnPage);
    g2.setClip( 0, 0, (int)Math.ceil( tableWidthOnPage), (int)Math.ceil( headerHeightOnPage));
    g2.scale( scale, scale);
    m_tableView.getTableHeader().paint( g2);//paint header at top

    return Printable.PAGE_EXISTS;
  }

  public DbTableAdapter getTableAdapter()
  {
    return m_dbtabadp;
  }

  public void resetTableAdapter()
  {
    m_dbtabadp = null;
  }

  public JScrollPane getScrollPane()
  {
    return m_scrollpaneTab;
  }

  public boolean hasFocus()
  {
    return m_tableView.hasFocus() || m_scrollpaneTab.hasFocus();
  }

  /**
    * Set primary key to use (true) or not (false) with the setValueAt function.
    *
    * @param strPrimKey:	Column name of the primary key.
    * @param blUsePrimKey:	true to use the primary key.
    */
  public void setUsePrimKey( String strPrimKey, boolean blUsePrimKey)
  {
    m_strPrimKey = strPrimKey;
    m_blUsePrimKey = blUsePrimKey;
    if( m_dbtabadp != null)
      m_dbtabadp.setUsePrimKey( m_strPrimKey, m_blUsePrimKey);
  }

  /**
    * Get DbQuery class in class DbTableAdapter.
    */
  public DbQuery getDbQuery()
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getDbQuery();
    else
      return m_dbquery;
  }

  /**
    * Get DbQuery class in class DbTableAdapter.
    */
  public void setDbQuery( DbQuery dbquery)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setDbQuery( dbquery);
    m_dbquery = dbquery;
  }

  /**
    * Get if the primary key is in use (true) or not (false) with the setValueAt function.
    */
  public boolean getUsePrimKey()
  {
    return m_blUsePrimKey;
  }

  public String getQuery()
  {
    return m_strQuery;
  }

  public void setQuery(String s)
  {
    m_strQuery = s;
  }

  public boolean getDbg()
  {
    return m_blDbg;
  }

  public boolean getExtDbg()
  {
    return m_blExDbg;
  }

  /**
    * Set debug output mode.
    *
    * @param flag:		If true sets the debug output to on.
    */
  public void setDbg(boolean flag)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setDebugMode(flag);
    m_blDbg = flag;
  }

  /**
    * Set extended debug output mode.
    *
    * @param flag:		If true sets the extended debug output to on.
    */
  public void setExtDbg(boolean flag)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setExtDebugMode(flag);
    m_blExDbg = flag;
  }

  public boolean getEditable()
  {
    return m_blEditable;
  }

  /**
    * Set auto commit mode.
    *
    * @param blLog:				Auto commit mode true or false
    */
  public void setAutoCommit( boolean blAutoCommit)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setAutoCommit( blAutoCommit);
    m_blAutoCommit = blAutoCommit;
  }

  /**
    * Set auto commit mode.
    *
    * @return				Auto commit mode
    */
  public boolean getAutoCommit()
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getAutoCommit();
    else
      return m_blAutoCommit;
  }

  /**
   * Get transaction status.
   *
   * @return				true if a transaction is pending
   */
  public boolean isTransactionPending()
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.isTransactionPending();
    else
      return false;
  }

  /**
    * Execute commit on DB.
    */
  public void commit()
    throws SQLException
  {
    if( m_dbtabadp != null)
      m_dbtabadp.commit();
  }

  /**
    * Execute rollback on DB.
    */
  public void rollback()
    throws SQLException
  {
    if( m_dbtabadp != null)
      m_dbtabadp.rollback();
  }

  /**
    * Set table editable.
    *
    * @param flag:		If true sets the table to editable.
    */
  public void setEditable(boolean flag)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setEditable(flag);
    m_blEditable = flag;
  }

  /**
    * Is table editable?
    */
  public boolean isEditable()
  {
    return m_blEditable;
  }

  public void setLayout(LayoutManager mgr)
  {
    return;
  }

  /**
  Set max number of rows in result sets. If 0 result set is not limited

  @param iMaxRows:		Limit to max number of rows in result set.
  */
  public void setMaxRows( int iMaxRows)
  {
    m_iMaxRows = iMaxRows;
  }

  /**
  Get max number of rows in result sets. If 0 result set is not limited

  @return		Max number of rows in result sets.
  */
  public int getMaxRows()
  {
    return m_iMaxRows;
  }

  /**
    * Set column minimum width.
    *
    * @param strColName:	Name of the column to set the minimum width of the cell.
    * @param iMinWidth:		Minimun width of the column.
    *
    * @return Error code (0 = success (OK)).
    */
  public int setColMinWidth( String strColName, int iMinWidth)
  {
    int iRet = IDXERR;

    if( m_dbtabadp != null)
    {
      int iIdx = getColumnIndex( strColName);
      return setColMinWidth( iIdx, iMinWidth);
    }
    return iRet;
  }

  /**
    * Set column minimum width.
    *
    * @param iIdx:				Index of the column to set the minimum width of the cell.
    * @param iMinWidth:		Minimun width of the column.
    *
    * @return Error code (0 = success (OK)).
    */
  public int setColMinWidth( int iIdx, int iMinWidth)
  {
    int iRet = IDXERR;

    if( m_dbtabadp != null)
    {
      if( iIdx == IDXERR)
        return iRet;
      TableColumn column = getColumnModel().getColumn( iIdx);
      column.setMinWidth( iMinWidth);
      iRet = OK;
    }
    return iRet;
  }

  /**
    * Set column minimum width for all columns calculated due to column headers.
    */
  public void setCalcColMinWidth()
  {
    if( m_dbtabadp != null)
    {
      for( int i = 0; i < getColumnCount(); i++)
      {
        TableColumn column = getColumnModel().getColumn(i);
        column.sizeWidthToFit();
        column.setMinWidth( column.getWidth());
      }
    }
  }

  /**
    * Set column minimum width for all columns.
    *
    * @param iMinWidth:			Minimun width of the column.
    */
  public void setColMinWidth( int iMinWidth)
  {
    if( m_dbtabadp != null)
    {
      for( int i = 0; i < getColumnCount(); i++)
      {
        TableColumn column = getColumnModel().getColumn(i);
        column.setMinWidth( iMinWidth);
      }
    }
  }

  /**
    * Set column cell renderer.
    *
    * @param strColName:		Name of the column to set the cell renderer.
    * @param iMinWidth:			Minimun width of the column.
    * @param iCellRenderer:	Constant of the cell renderer ( e.g. CELLTEXTAREA).
    *
    * @return Error code (0 = success (OK)).
    */
  public int setColCellRenderer( String strColName, int iMinWidth, int iCellRenderer)
  {
    int iRet = IDXERR;

    if( m_dbtabadp != null)
    {
      int iIdx = getColumnIndex( strColName);
      if( iIdx == IDXERR)
        return iRet;
      TableColumn column = getColumnModel().getColumn( iIdx);
      switch( iCellRenderer)
      {
      case CELLTEXTAREA:
        column.setCellRenderer(new TextCellRenderer());
        break;

      default:
        column.setCellRenderer(new DefaultTableCellRenderer());
        break;
      }
      column.setMinWidth( iMinWidth);
      iRet = OK;
    }
    return iRet;
  }

  /**
    * Set column cell editor.
    *
    * @param strColName:		Name of the column to set the cell editor.
    * @param tce:						Table cell editor.
    */
  public void setCellEditor( String strColName, TableCellEditor tce)
  {
    if( m_dbtabadp != null)
    {
      int iIdx = getColumnIndex( strColName);
      if( iIdx == IDXERR)
        return;
      TableColumn column = getColumnModel().getColumn( iIdx);
      column.setCellEditor( tce);
    }
  }

  /**
    * Set column text area cell editor.
    *
    * @param strColName:		Name of the column to set the cell editor.
    *
    * @return TextCellEditor or null if not successful.
    */
  public TextCellEditor setTextCellEditor( String strColName)
  {
    TextCellEditor tce = null;

    if( m_dbtabadp != null)
    {
      int iIdx = getColumnIndex( strColName);
      if( iIdx == IDXERR)
        return null;
      TableColumn column = getColumnModel().getColumn( iIdx);
      tce = new TextCellEditor();
      column.setCellEditor( tce);
    }
    return tce;
  }

  /**
    * Set column combo box cell editor.
    *
    * @param strColName:		Name of the column to set the cell editor.
    * @param strTable:			DB Table
    * @param strUseColumn:	Column to use in ComboBox
    * @param strDriver:			JDBC Driver for the DB connection
    * @param strUrl:				JDBC Url for the DB connection
    * @param strUid:				JDBC login for the DB connection
    * @param strPwd:				JDBC pssword for the DB connection
    *
    * @return ComboCellEditor or null if not successful.
    */
  public ComboCellEditor setComboCellEditor( String strColName, String strTable, String strUseColumn, boolean blEditable,
    String strDriver, String strUrl, String strUid, String strPwd)
  {
    ComboCellEditor cce = null;

    if( m_dbtabadp != null)
    {
      int iIdx = getColumnIndex( strColName);
      if( iIdx == IDXERR)
        return null;
      TableColumn column = getColumnModel().getColumn( iIdx);
      cce = new ComboCellEditor( strTable, strUseColumn, blEditable,
        strDriver, strUrl, strUid, strPwd);
      column.setCellEditor( cce);
    }
    return cce;
  }

  /**
    * Set column table cell editor.
    *
    * @param strDlgTitle:			Title of the DbTableDialog.
    * @param strColName:			Name of the column to set the cell editor.
    * @param strTable:				DB Table
    * @param strQuery:				Constant of the cell renderer ( e.g. CELLCOMBOBOX).
    * @param strResultColumn:	Name of the column from which to return the value of the cell editor.
    * @param blEditable:			Set table editable (true) or non editable (false).
    * @param blOk:						Show Ok button.
    * @param blClear:					Show Clear button.
    * @param blCancel:				Show Cancel button.
    * @param blNew:						Show New button.
    * @param blDelete:				Show Delete button.
    * @param strDriver:				JDBC Driver for the DB connection
    * @param strUrl:					JDBC Url for the DB connection
    * @param strUid:					JDBC login for the DB connection
    * @param strPwd:					JDBC pssword for the DB connection
    *
    * @return DbTableDialog or null if not successful.
    */
  public DbTableDialog setTableCellEditor( String strDlgTitle, String strColName, String strTable, String strQuery, String strResultColumn, boolean blEditable, boolean blOk, boolean blClear, boolean blCancel, boolean blNew, boolean blDelete,
    String strDriver, String strUrl, String strUid, String strPwd, String strAppName)
  {
    DbTableDialog td = null;

    if( m_dbtabadp != null)
    {
      int iIdx = getColumnIndex( strColName);
      if( iIdx == IDXERR)
        return td;
      TableColumn column = getColumnModel().getColumn( iIdx);
      DbTableCellEditor tce = new DbTableCellEditor( strDlgTitle, strQuery, strTable, strResultColumn, blEditable, blOk, blClear, blCancel, blNew, blDelete,
        strDriver, strUrl, strUid, strPwd, strAppName);
      tce.setParentTablePanel( this);
      td = tce.getTableDialog();
      td.setTableCellEditor( tce);
      column.setCellEditor( tce);
    }
    return td;
  }

  public boolean stopCellEditing()
  {
    boolean blRet = false;
    if( isEditing())
    {
      blRet = getCellEditor().stopCellEditing();
    }
    return blRet;
  }

  /**
    * Set OutputStream for the output of the class DbTableAdapter.
    *
    * @param stream:			OutputStream.
    */
  public void setOutStream(OutputStream stream)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setOutStream(stream);
    m_printsOut = stream;
  }

  /**
    * Set OutputStream for the error output of the class DbTableAdapter.
    *
    * @param stream:			OutputStream.
    */
  public void setErrStream(OutputStream stream)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setErrStream(stream);
    m_printsErr = stream;
  }

  /**
    * Get OutputStream of the output of the class DbTableAdapter.
    */
  public OutputStream getOutStream()
  {
    if(m_dbtabadp != null)
      return m_dbtabadp.getOutStream();
    else
      return null;
  }

  /**
    * Get OutputStream of the error output of the class DbTableAdapter.
    */
  public OutputStream getErrStream()
  {
    if(m_dbtabadp != null)
      return m_dbtabadp.getErrStream();
    else
      return null;
  }

  /**
    * Set table name(s) necessary for the update and delete SQL statements.
    *
    * @param strTable:	Table name(s) e.g.: "table11,table2"
    */
  public void setTableName( String strTable)
  {
    m_strTable = strTable;
    if(m_dbtabadp != null)
      m_dbtabadp.setTableName( strTable);
  }

  /**
    * Get table name(s) of the TablePanel.
    */
  public String getTableName()
  {
    return m_strTable;
  }

  public void query(ActionEvent actionevent)
  {
    query();
  }

  public boolean connect()
  {
    boolean blRet = false;
    if( m_dbtabadp != null)
      close();

    m_dbtabadp = new DbTableAdapter();
    if( m_printsOut != null)
      m_dbtabadp.setOutStream(m_printsOut);
    if( m_printsErr != null)
      m_dbtabadp.setErrStream(m_printsErr);
    m_dbtabadp.setAutoCommit( m_blAutoCommit);
    //if( m_dbquery != null)
      //m_dbtabadp.setDbQuery( m_dbquery);
    if(m_dbtabadp.dbConnect(getDriver(), getUrl(), getUsr(), getPwd()) != null)
    {
      blRet = true;
      m_dbtabadp.setDebugMode(m_blDbg);
      m_dbtabadp.setExtDebugMode(m_blExDbg);
      m_dbtabadp.setLogMode(m_blLogMode);
      m_dbtabadp.setEditable(m_blEditable);
      m_dbtabadp.setDateFormat(m_strDateFormat);
      m_dbtabadp.setTableName(m_strTable);
      m_dbtabadp.setUsePrimKey( m_strPrimKey, m_blUsePrimKey);
      m_dbtabmap.setModel(m_dbtabadp);
      // Limit result set to max rows
      m_dbtabadp.setMaxRows( getMaxRows());
    }
    else
      blRet = false;
    return blRet;
  }

  /**
    * Execute a DB query and creates a result set.
    *
    * @return Error code (0 = success (OK)).
    */
  public int query()
  {
    int iRet = ERR;
    if(m_dbtabadp != null)
    {
      m_dbtabadp.setDebugMode(m_blDbg);
      m_dbtabadp.setExtDebugMode(m_blExDbg);
      m_dbtabadp.setLogMode(m_blLogMode);
      m_dbtabadp.setEditable(m_blEditable);
      m_dbtabadp.setDateFormat(m_strDateFormat);
      m_dbtabadp.setTableName(m_strTable);
      m_dbtabadp.setUsePrimKey( m_strPrimKey, m_blUsePrimKey);
      // Limit result set to max rows
      m_dbtabadp.setMaxRows( getMaxRows());

      if(( iRet = m_dbtabadp.query(m_strQuery)) == OK)
      {
        m_labelErg.setText( m_dbtabadp.getRowCount() + " found:");
        tablesDlg();
      }
      else if( iRet  == OKNORSET)
      {
        m_labelErg.setText( m_dbtabadp.getTreatedRows() + " treated:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
      return iRet;
    }
    if( m_blConnect && (getUsr() == null || ( getUsr() != null && getUsr().equals(""))))
      loginDlg();
//		if(getUsr() != null && !getUsr().equals(""))
    {
      m_dbtabadp = new DbTableAdapter();
      if( m_printsOut != null)
        m_dbtabadp.setOutStream(m_printsOut);
      if( m_printsErr != null)
        m_dbtabadp.setErrStream(m_printsErr);
      m_dbtabadp.setAutoCommit( m_blAutoCommit);
      //if( m_dbquery != null)
        //m_dbtabadp.setDbQuery( m_dbquery);
      if(m_dbtabadp.dbConnect(getDriver(), getUrl(), getUsr(), getPwd()) != null)
      {
        m_dbtabadp.setDebugMode(m_blDbg);
        m_dbtabadp.setExtDebugMode(m_blExDbg);
        m_dbtabadp.setLogMode(m_blLogMode);
        m_dbtabadp.setEditable(m_blEditable);
        m_dbtabadp.setDateFormat(m_strDateFormat);
        m_dbtabadp.setTableName(m_strTable);
        m_dbtabadp.setUsePrimKey( m_strPrimKey, m_blUsePrimKey);
        m_dbtabmap.setModel(m_dbtabadp);
        // Limit result set to max rows
        m_dbtabadp.setMaxRows( getMaxRows());

        if(( iRet = m_dbtabadp.query(m_strQuery)) == OK)
        {
          m_labelErg.setText( m_dbtabadp.getRowCount() + " found:");
          tablesDlg();
        }
        else if( iRet  == OKNORSET)
        {
          m_labelErg.setText( m_dbtabadp.getTreatedRows() + " treated:");
        }
        else
        {
          m_labelErg.setText("Wrong SQL statement:");
        }
        return iRet;
      }
      else
      {
        errorMsgDlg("Could not connect to database!");
        reset();
        m_dbtabadp = null;
        return iRet;
      }
    }
/*		else
    {
      ErrorMsgDlg("Wrong login name!");
      Reset();
      m_dbtabadp = null;
      return;
    }
*/
  }

  public void tablesDlg()
  {
    if(m_strQuery.toUpperCase().indexOf("SELECT") != -1 && m_blEditable)
    {
      m_dbtabadp.setTableName(inputMsgDlg("Insert tablenames related to your SELECT statement:"));
    }
  }

  /**
    * Close the result set, statementent and DB connection.
    */
  public void close()
  {
    if(m_dbtabadp != null)
    {
      m_dbtabadp.close();
      m_dbtabadp = null;
    }
  }

// DbTableAdapter functions

  /**
  Prepares Db for ResultSet queries

  @return Error code (0 = success (OK)).
  */
  public int prepareDB()
  {
    int iRet = ERR;
    if(m_dbtabadp != null)
    {
      m_dbtabadp.setDebugMode(m_blDbg);
      m_dbtabadp.setExtDebugMode(m_blExDbg);
      m_dbtabadp.setLogMode(m_blLogMode);
      m_dbtabadp.setEditable(m_blEditable);
      m_dbtabadp.setDateFormat(m_strDateFormat);
      m_dbtabadp.setTableName(m_strTable);
      m_dbtabadp.setUsePrimKey( m_strPrimKey, m_blUsePrimKey);
      // Limit result set to max rows
      m_dbtabadp.setMaxRows( getMaxRows());
      iRet = OK;
    }
    if( m_blConnect && (getUsr() == null || ( getUsr() != null && getUsr().equals(""))))
      loginDlg();
//		if(getUsr() != null && !getUsr().equals(""))
    {
      m_dbtabadp = new DbTableAdapter();
      if( m_printsOut != null)
        m_dbtabadp.setOutStream(m_printsOut);
      if( m_printsErr != null)
        m_dbtabadp.setErrStream(m_printsErr);
      m_dbtabadp.setAutoCommit( m_blAutoCommit);
      //if( m_dbquery != null)
        //m_dbtabadp.setDbQuery( m_dbquery);
      if(m_dbtabadp.dbConnect(getDriver(), getUrl(), getUsr(), getPwd()) != null)
      {
        m_dbtabadp.setDebugMode(m_blDbg);
        m_dbtabadp.setExtDebugMode(m_blExDbg);
        m_dbtabadp.setLogMode(m_blLogMode);
        m_dbtabadp.setEditable(m_blEditable);
        m_dbtabadp.setDateFormat(m_strDateFormat);
        m_dbtabadp.setTableName(m_strTable);
        m_dbtabadp.setUsePrimKey( m_strPrimKey, m_blUsePrimKey);
        m_dbtabmap.setModel(m_dbtabadp);
        // Limit result set to max rows
        m_dbtabadp.setMaxRows( getMaxRows());
        iRet = OK;
      }
      else
      {
        errorMsgDlg("Could not connect to database!");
        reset();
        m_dbtabadp = null;
        iRet = ERR;
      }
    }
    return iRet;
  }

  /**
  Get the RS of given index

  @param iIdx:				Index of the result set

  @return the RS
  */
  public ResultSet getResultSet()
  {
    if(m_dbtabadp != null)
      return m_dbtabadp.getResultSet( 0);
    return( null);
  }

  /**
  Gets the schema names available in this database.
  The results are ordered by schema name.

  @return Error code (0 = success (OK)).
  */
  public int getSchemas( )
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getSchemas( )) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets the catalog names available in this database.
  The results are ordered by catalog name.

  @return Error code (0 = success (OK)).
  */
  public int getCatalogs( )
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getCatalogs( )) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of table columns available in the specified catalog.
  Only column descriptions matching the catalog, schema, table and column name criteria are returned.
  They are ordered by TABLE_SCHEM, TABLE_NAME and ORDINAL_POSITION.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param column:    a column name pattern

  @return Error code (0 = success (OK)).
  */
  public int getColumns( String catalog, String schema, String table, String column)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getColumns( catalog, schema, table, column)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of the access rights for a table's columns.
  Only privileges matching the column name criteria are returned. They are ordered by COLUMN_NAME and PRIVILEGE.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name
  @param column:    a column name pattern

  @return Error code (0 = success (OK)).
  */
  public int getColumnPrivileges( String catalog, String schema, String table, String column)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getColumnPrivileges( catalog, schema, table, column)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of a catalog's stored procedure parameters and result columns.
  Only descriptions matching the schema, procedure and parameter name criteria are returned.
  They are ordered by PROCEDURE_SCHEM and PROCEDURE_NAME.
  Within this, the return value, if any, is first. Next are the parameter descriptions in call order.
  The column descriptions follow in column number order.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param procedure: a procedure name pattern
  @param column:    a column name pattern

  @return Error code (0 = success (OK)).
  */
  public int getProcedureColumns( String catalog, String schema, String procedure, String column)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getProcedureColumns( catalog, schema, procedure, column)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of the stored procedures available in a catalog.
  Only procedure descriptions matching the schema and procedure name criteria are returned.
  They are ordered by PROCEDURE_SCHEM, and PROCEDURE_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param procedure: a procedure name pattern

  @return Error code (0 = success (OK)).
  */
  public int getProcedures( String catalog, String schema, String procedure)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getProcedures( catalog, schema, procedure)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Only table descriptions matching the catalog, schema, table name and type criteria are returned.
  They are ordered by TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getTables( String catalog, String schema, String table)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getTables( catalog, schema, table)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of the access rights for each table available in a catalog.
  Note that a table privilege applies to one or more columns in the table.
  It would be wrong to assume that this priviledge applies to all columns
  (this may be true for some systems but is not true for all.)
  Only privileges matching the schema and table name criteria are returned.
  They are ordered by TABLE_SCHEM, TABLE_NAME, and PRIVILEGE.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getTablePrivileges( String catalog, String schema, String table)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getTablePrivileges( catalog, schema, table)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of a table's primary key columns. They are ordered by COLUMN_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getPrimaryKeys( String catalog, String schema, String table)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getPrimaryKeys( catalog, schema, table)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of the primary key columns that are referenced by a table's foreign key columns
  (the primary keys imported by a table).
  They are ordered by PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getImportedKeys( String catalog, String schema, String table)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getImportedKeys( catalog, schema, table)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of the foreign key columns that reference a table's primary key columns
  (the foreign keys exported by a table).
  They are ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and KEY_SEQ.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getExportedKeys( String catalog, String schema, String table)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getExportedKeys( catalog, schema, table)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
  Gets a description of a table's indices and statistics.
  They are ordered by NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param unique:    when true, return only indices for unique values;
                    when false, return indices regardless of whether unique or not
  @param approximate: when true, result is allowed to reflect approximate or out of data values;
                    when false, results are requested to be accurate

  @return Error code (0 = success (OK)).
  */
  public int getIndexInfo( String catalog, String schema, String table, boolean unique, boolean approximate)
  {
    int iRet = ERR;
    if( prepareDB() == OK)
    {
      if(( iRet = m_dbtabadp.getIndexInfo( catalog, schema, table, unique, approximate)) == 0)
      {
        m_labelErg.setText(m_dbtabadp.getRowCount() + " found:");
      }
      else
      {
        m_labelErg.setText("Wrong SQL statement:");
      }
    }
    return iRet;
  }

  /**
    * Set date datatype format.
    *
    * @param strDateFormat:		Format of the date datatype (default: "DD.MM.YYYY").
    */
  public void setDateFormat(String strDateFormat)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setDateFormat( strDateFormat);
    m_strDateFormat = strDateFormat;
  }

  /**
    * Get date datatype format.
    */
  public String getDateFormat()
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getDateFormat();
    return null;
  }

  /**
    * Set log mode.
    *
    * @param blLog:				Log mode true or false
    */
  public void setLogMode(boolean blLog)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setLogMode(blLog);
    m_blLogMode = blLog;
  }

  /**
    * Execute a DB query and creates a result set.
    *
    * @param strQuery:		DB query string e.g.: "SELECT * FROM tablename"
    *
    * @return Error code (0 = success (OK)).
    */
  public int query(String strQuery)
  {
    setQuery( strQuery);
    return query();
  }

  /**
    * Execute a DB query and without result set.
    *
    * @param strQuery:		DB query string e.g.: "USE tablename"
    *
    * @return Error code (0 = success (OK)).
    */
  public int queryNoReturn(String strQuery)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.queryNoReturn(strQuery);
    return ERR;
  }

  /**
   * Execute a DB query batch without result set.
   *
   * @param strQuery:		DB query string
   *
   * @return Error code (0 = success (OK)).
   */
  public int queryBatch( String strQuery)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.queryBatch(strQuery);
    return ERR;
  }

  /**
    * Deletes a Row in the database.
    *
    * <BR><BR>Comment:<BR>
    * Only for result sets within one table.
    *
    * @param iIdx:			index of the row
    * @param strPrim:		Primary Key field name of the table (can be empty).
    *
    * @return Error code (0 = success (OK)).
    */
  public int deleteRecord(int iIdx, String strPrim)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.deleteRecord( iIdx, strPrim);
    return ERR;
  }

  /**
    * Find a row in the database table view.
    *
    * @param obj:			Object to be found.
    * @param iIdx:		Index of the column in the table view to be search in
    *
    * @return Number of the found row or Constant.IDXERR if nothing was found.
    */
  public int findRow(Object obj, int iIdx)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.findRow( obj, iIdx);
    return IDXERR;
  }

  /**
    * Find a column in the database table view.
    *
    * @param obj:			Object to be found.
    * @param iIdx:		Index of the row in the table view to be search in
    *
    * @return Number of the found column or Constant.IDXERR if nothing was found.
    */
  public int findColumn(Object obj, int iIdx)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.findColumn( obj, iIdx);
    return IDXERR;
  }

  /**
    * Get the type of the columns in the result set.
    *
    * @param iCIdx:				Index of the column
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int getColType( int iCIdx)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getColType( iCIdx, 0);
    return IDXERR;
  }

  /**
    * Get the type of the columns in the result set.
    *
    * @param strColName:	name of the column
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int getColType( String strColName)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getColType( strColName, 0);
    return IDXERR;
  }

  /**
    * Get the type of the columns in the result set.
    *
    * @param iType:				Type of the column
    * @param iCIdx:				Index of the column
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int setColType( int iType, int iCIdx)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.setColType( iType, iCIdx, 0);
    return IDXERR;
  }

  /**
    * Set the type of the columns in the result set.
    *
    * @param iType:				Type of the column
    * @param strColName:	name of the column
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int setColType( int iType, String strColName)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.setColType( iType, strColName, 0);
    return IDXERR;
  }

  /**
    * Can you put a NULL in the secified column?
    *
    * @param iCIdx:				Index of the column
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int isColNullable(int iCIdx)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.isColNullable( iCIdx, 0);
    return IDXERR;
  }

  /**
    * Can you put a NULL in the secified column?
    *
    * @param strColName:	name of the column
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int isColNullable( String strColName)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.isColNullable( strColName, 0);
    return IDXERR;
  }

  /**
    * Get the index of a column.
    *
    * @param strColName:	name of the column
    *
    * @return Index of the found column or Constant.IDXERR if nothing was found.
    */
  public int getColumnIndex(String strColName)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getColumnIndex( strColName);
    return IDXERR;
  }

  /**
    * Get the name of a column index.
    *
    * @param iIdx:	index of the column
    *
    * @return Name of the found column or null if nothing was found.
    */
  public String getColumnName(int iIdx)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getColumnName( iIdx);
    return null;
  }

  /**
    * Get the name of the shema.
    *
    * @return Name of the shema.
    */
  public String getSchemaName()
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getSchemaName();
    return null;
  }

  /**
    * Get the name of the column selected by an tableheader popup.
    *
    * @return Name of column or null if nothing was selected.
    */
  public String getSelColumnName()
  {
    return m_tableView.getSelColumnName();
  }

  /**
    * Get cell editable status.
    *
    * @param iX:	index of the row
    * @param iY:	index of the column
    *
    * @return true if the cell is editable.
    */
  public boolean isCellEditable(int iX, int iY)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.isCellEditable( iX, iY);
    return false;
  }

  /**
    * Sets a column in the result set to non editable. Must be set after a Query().
    *
    * @param strColName:	name of the column
    *
    * @return Error code ( Success = Constant.OK).
    */
  public int setColNonEditable( String strColName)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.setColNonEditable( strColName);
    return IDXERR;
  }

  /**
    * Sets a column in the result set to non editable. Must be set after a Query().
    *
    * @param iIdx:	index of the column
    *
    * @return Error code ( Success = Constant.OK).
    */
  public int setColNonEditable( int iIdx)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.setColNonEditable( iIdx);
    return IDXERR;
  }

  /**
    * Get the column count in the table view.
    *
    * @return Number of columns.
    */
  public int getColumnCount()
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getColumnCount();
    return IDXERR;
  }

  /**
    * Get the row count in the table view.
    *
    * @return Number of rows.
    */
  public int getRowCount()
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getRowCount();
    return IDXERR;
  }

  /**
    * Gives the values in string representation
    *
    * @param iX:	index of the row
    *
    * @return string which represents the row values as string.
    */
  public String toString( int iX)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.toString( iX);
    return null;
  }

  /**
    * Get the object of a cell.
    *
    * @param iX:	index of the row
    * @param iY:	index of the column
    *
    * @return object of the cell, or null if the cell doesn't exist.
    */
  public Object getValueAt(int iX, int iY)
    throws ArrayIndexOutOfBoundsException
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getValueAt( iX, iY);
    return null;
  }

  /**
    * Set the object of a cell.
    *
    * @param obj:	Object to be set
    * @param iX:	index of the row
    * @param iY:	index of the column
    */
  public void setValueAt(Object obj, int iX, int iY)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setValueAt( obj, iX, iY);
  }

  /**
    * Set the object of a cell.
    *
    * @param obj:	Object to be set
    * @param iX:	index of the row
    * @param iY:	index of the column
    * @param strPrimKey: Primary key of the table
    */
  public void setValueAt(Object obj, int iX, int iY, String strPrimKey)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.setValueAt( obj, iX, iY, strPrimKey);
  }

  /**
    * Get the class representation of a column.
    *
    * @param iY:	index of the column
    *
    * @return Class representation of a column.
    */
  public Class getColumnClass(int iY)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getColumnClass( iY);
    return null;
  }

  /**
    * Get the string representation of a column value.
    *
    * @param column:	index of the column
    * @param value:		object of the column
    *
    * @return String representation of the column values.
    */
  public String dbRepresentation(int column, Object value)
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.dbRepresentation( column, value);
    return null;
  }

    /**
     *  Add a row to the end of the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
  public void addRow(Vector rowData)
  {
    if( m_dbtabadp != null)
    {
      BeginWait();
      m_dbtabadp.addRow( rowData);
      query();
      endWait();
    }
  }

    /**
     *  Add a row to the end of the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
  public void addRow(Object[] rowData)
  {
    if( m_dbtabadp != null)
    {
      BeginWait();
      m_dbtabadp.addRow( rowData);
      query();
      endWait();
    }
  }

    /**
     *  Insert a row at <i>row</i> in the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   row             the row index of the row to be inserted
     * @param   rowData         optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
  public void insertRow(int row, Vector rowData)
  {
    if( m_dbtabadp != null)
    {
      BeginWait();
      m_dbtabadp.insertRow( row, rowData);
      query();
      endWait();
    }
  }

    /**
     *  Insert a row at <i>row</i> in the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   row      the row index of the row to be inserted
     * @param   rowData          optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
  public void insertRow(int row, Object[] rowData)
  {
    if( m_dbtabadp != null)
    {
      BeginWait();
      m_dbtabadp.insertRow( row, rowData);
      query();
      endWait();
    }
  }

    /**
     *  Moves one or more rows starting at <i>startIndex</i> to <i>endIndex</i>
     *  in the model to the <i>toIndex</i>.    This method will send a
     *  tableChanged() notification message to all the listeners. <p>
     *
     *  Examples of moves:<p>
     *  1. moveRow(1,3,5);<p>
     *          a|B|C|D|e|f|g|h|i|j|k   - before
     *          a|e|f|B|C|D|g|h|i|j|k   - after
     *  2. moveRow(6,7,1);<p>
     *          a|b|c|d|e|f|G|H|i|j|k   - before
     *          a|G|H|b|c|d|e|f|i|j|k   - after
     *
     * @param   startIndex       the starting row index to be moved
     * @param   endIndex         the ending row index to be moved
     * @param   toIndex          the destination of the rows to be moved
     * @exception  ArrayIndexOutOfBoundsException  if any of the indices are out of
     *                           range.  Or if endIndex is less than startIndex.
     */
  public void moveRow(int startIndex, int endIndex, int toIndex)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.moveRow( startIndex, endIndex, toIndex);
  }

    /**
     *  Remove the row at <i>row</i> from the model and database.  Notification
     *  of the row being removed will be sent to all the listeners.
     *
     * @param   row      the row index of the row to be removed
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
  public void removeRow(int row)
  {
    if( m_dbtabadp != null)
      m_dbtabadp.removeRow( row);
  }

// End DbTableAdapter functions

// jTable functions

//
// Table Attributes
//

    /**
     * Sets the tableHeader working with this JTable to <I>newHeader</I>.
     * It is legal to have a <B>null</B> tableHeader.
     *
     * @param   newHeader                       new tableHeader
     * @see     #getTableHeader()
     * @beaninfo
     * description: The JTableHeader instance which renders the column headers.
     */
  public void setTableHeader(JTableHeader newHeader)
  {
    m_tableView.setTableHeader( newHeader);
  }

    /**
     * Returns the tableHeader working with this JTable.
     *
     * @return  the tableHeader working with the receiver
     * @see     #setTableHeader()
     */
  public JTableHeader getTableHeader()
  {
    return m_tableView.getTableHeader();
  }

    /**
     * Sets the height for rows to <I>newRowHeight</I> and invokes tile
     *
     * @param   newRowHeight                    new row height
     * @exception IllegalArgumentException      If <I>newRowHeight</I> is
     *                                          less than 1.
     * @see     #getRowHeight()
     * @beaninfo
     * description: The height of the cells including the inter-cell spacing.
     */
  public void setRowHeight(int newHeight)
  {
    m_tableView.setRowHeight( newHeight);
  }

    /**
     * Returns the height of a table row in the receiver.
     * The default row height is 16.0.
     *
     * @return  the height of each row in the receiver
     * @see     #setRowHeight()
     */
  public int getRowHeight()
  {
    return m_tableView.getRowHeight();
  }

    /**
     * Sets the width and height between cells to <I>newSpacing</I> and
     * redisplays the receiver.
     *
     * @param   newSpacing              The new width and height intercellSpacing
     * @see     #getIntercellSpacing()
     * @beaninfo
     * description: The spacing between the cells, drawn in the background color of the JTable.
     */
  public void setIntercellSpacing(Dimension newSpacing)
  {
    m_tableView.setIntercellSpacing( newSpacing);
  }

    /**
     * Returns the horizontal and vertical spacing between cells.
     * The default spacing is (3, 2).
     *
     * @return  the horizontal and vertical spacing between cells
     * @see     #setIntercellSpacing()
     */
  public Dimension getIntercellSpacing()
  {
    return m_tableView.getIntercellSpacing();
  }

    /**
     * Sets the color used to draw grid lines to <I>color</I> and redisplays
     * the receiver.
     * The default color is gray.
     *
     * @param   color                           new color of the grid
     * @exception IllegalArgumentException      if <I>color</I> is null
     * @see     #getGridColor()
     */
  public void setGridColor(Color newColor)
  {
    m_tableView.setGridColor( newColor);
  }

    /**
     * Returns the color used to draw grid lines. The default color is gray.
     *
     * @return  the color used to draw grid lines
     * @see     #setGridColor()
     */
  public Color getGridColor()
  {
    return m_tableView.getGridColor();
  }

    /**
     *  Sets whether the receiver draws grid lines around cells.
     *  If <I>flag</I> is true it does; if it is false it doesn't.
     *  There is no getShowGrid() method as the this state is held
     *  in two variables: showHorizontalLines and showVerticalLines
     *  each of which may be queried independently.
     *
     * @param   flag                    true if table view should draw grid lines
     *
     * @see     #setShowVerticalLines
     * @see     #setShowHorizontalLines
     * @beaninfo
     * description: The color used to draw the grid lines.
     */
  public void setShowGrid(boolean b)
  {
    m_tableView.setShowGrid( b);
  }

    /**
     *  Sets whether the receiver draws horizontal lines between cells.
     *  If <I>flag</I> is true it does; if it is false it doesn't.
     *
     * @param   flag                    true if table view should draw horizontal lines
     * @see     #getShowHorizontalLines
     * @see     #setShowGrid
     * @see     #setShowVerticalLines
     * @beaninfo
     * description: Whether horizontal lines should be drawn in between the cells.
     */
  public void setShowHorizontalLines(boolean b)
  {
    m_tableView.setShowHorizontalLines( b);
  }

    /**
     *  Sets whether the receiver draws vertical lines between cells.
     *  If <I>flag</I> is true it does; if it is false it doesn't.
     *
     * @param   flag                    true if table view should draw vertical lines
     * @see     #getShowVerticalLines
     * @see     #setShowGrid
     * @see     #setShowHorizontalLines
     * @beaninfo
     * description: Whether vertical lines should be drawn in between the cells.
     */
  public void setShowVerticalLines(boolean b)
  {
    m_tableView.setShowVerticalLines( b);
  }

    /**
     * Returns true if the receiver draws horizontal lines between cells, false if it
     * doesn't. The default is true.
     *
     * @return  true if the receiver draws horizontal lines between cells, false if it
     *          doesn't
     * @see     #setShowHorizontalLines
     */
  public boolean getShowHorizontalLines()
  {
    return m_tableView.getShowHorizontalLines();
  }

    /**
     * Returns true if the receiver draws vertical lines between cells, false if it
     * doesn't. The default is true.
     *
     * @return  true if the receiver draws vertical lines between cells, false if it
     *          doesn't
     * @see     #setShowVerticalLines
     */
  public boolean getShowVerticalLines()
  {
    return m_tableView.getShowVerticalLines();
  }

    /**
     * Sets the table's auto resize mode when the table is resized.
     *
     * @param   mode            One of 3 legal values: AUTO_RESIZE_OFF,
     *                          AUTO_RESIZE_LAST_COLUMN, AUTO_RESIZE_ALL_COLUMNS
     *
     * @see     #getAutoResizeMode()
     * @see     #sizeColumnsToFit()
     * @beaninfo
     * description: Whether the columns should adjust themselves automatically to accomodate changes.
     *        enum: AUTO_RESIZE_OFF          JTable.AUTO_RESIZE_OFF
     *              AUTO_RESIZE_LAST_COLUMN  JTable.AUTO_RESIZE_LAST_COLUMN
     *              AUTO_RESIZE_ALL_COLUMNS  JTable.AUTO_RESIZE_ALL_COLUMNS
     */
  public void setAutoResizeMode(int mode)
  {
    m_tableView.setAutoResizeMode( mode);
  }

    /**
     * Returns auto resize mode of the table.  The default is
     * AUTO_RESIZE_ALL_COLUMNS.
     *
     * @return  the autoResizeMode of the table
     *
     * @see     #setAutoResizeMode()
     * @see     #sizeColumnsToFit()
     */
  public int getAutoResizeMode()
  {
    return m_tableView.getAutoResizeMode();
  }

    /**
     * Sets the table's autoCreateColumnsFromModel flag.  This method
     * will call createDefaultColumnsFromModel() if <i>createColumns</i>
     * is true.
     *
     * @param   createColumns   true if JTable should auto create columns
     * @see     #getAutoCreateColumnsFromModel()
     * @see     #createDefaultColumnsFromModel()
     * @beaninfo
     * description: Automatically populate the columnModel when a new TableModel is submitted.
     */
  public void setAutoCreateColumnsFromModel(boolean createColumns)
  {
    m_tableView.setAutoCreateColumnsFromModel( createColumns);
  }

    /**
     * Returns whether the table will create default columns from the model.
     * If this is true, setModel() will clear any existing columns and
     * create new columns from the new model.  Also if the event in the
     * the tableChanged() notification specified the entired table changed
     * then the columns will be rebuilt.  The default is true.
     *
     * @return  the autoCreateColumnsFromModel of the table
     * @see     #setAutoCreateColumnsFromModel()
     * @see     #createDefaultColumnsFromModel()
     */
  public boolean getAutoCreateColumnsFromModel()
  {
    return m_tableView.getAutoCreateColumnsFromModel();
  }

    /**
     * This method will create default columns for the table from
     * the data model using the getColumnCount() and getColumnType() methods
     * defined in the TableModel interface.
     * <p>
     * This method will clear any exsiting columns before creating the
     * new columns based on information from the model.
     *
     * @see     #getAutoCreateColumnsFromModel()
     */
  public void createDefaultColumnsFromModel()
  {
    m_tableView.createDefaultColumnsFromModel();
  }

    /**
     * Set a default renderer to be used if no renderer has been set in
     * a TableColumn.
     *
     * @see     #getDefaultRenderer
     * @see     #setDefaultEditor
     */
  public void setDefaultRenderer(Class columnClass, TableCellRenderer renderer)
  {
    m_tableView.setDefaultRenderer( columnClass, renderer);
  }

    /**
     * Returns the renderer to be used when no renderer has been set in
     * a TableColumn. During the rendering of cells the renderer is fetched from
     * a Hashtable of entries according to the class of the cells in the column. If
     * there is no entry for this <I>columnClass</I> the method returns
     * the entry for the most specific superclass. The JTable installs entries
     * for <I>Object</I>, <I>Number</I> and <I>Boolean</I> all which can be modified
     * or replaced.
     *
     * @see     #setDefaultRenderer
     * @see     #getColumnClass
     */
  public TableCellRenderer getDefaultRenderer(Class columnClass)
  {
    return m_tableView.getDefaultRenderer( columnClass);
  }

    /**
     * Set a default editor to be used if no editor has been set in
     * a TableColumn. If no editing is required in a table or a
     * particular column in a table use the isCellEditable()
     * method in the TableModel interface to ensure that the
     * JTable will not start an editor in these columns.
     *
     * @see     TableModel#isCellEditable
     * @see     #getDefaultEditor
     * @see     #setDefaultRenderer
     */
  public void setDefaultEditor(Class columnClass, TableCellEditor editor)
  {
    m_tableView.setDefaultEditor( columnClass, editor);
  }

    /**
     * Returns the editor to be used when no editor has been set in
     * a TableColumn. During the editing of cells the editor is fetched from
     * a Hashtable of entries according to the class of the cells in the column. If
     * there is no entry for this <I>columnClass</I> the method returns
     * the entry for the most specific superclass. The JTable installs entries
     * for <I>Object</I>, <I>Number</I> and <I>Boolean</I> all which can be modified
     * or replaced.
     *
     * @see     #setDefaultEditor
     * @see     #getColumnClass
     */
  public TableCellEditor getDefaultEditor(Class columnClass)
  {
    return m_tableView.getDefaultEditor( columnClass);
  }

//
// Selection methods
//
    /**
     * Sets the table's selection mode to allow only single selections, a single
     * contiguous interval, or multiple intervals.
     *
     * NOTE:<br>
     * JTable provides all the methods for handling column and row selection.
     * When setting states, such as setSelectionMode, it not only
     * updates the mode for the row selection model but also sets similar
     * values in the selection model of the columnModel.
     * If you want to have states that is different between rows and columns
     * you can get the columnModel and change that directly.
     * <p>
     * Both the row and column selection models for the JTable default
     * to using a DefaultListSelectionModel so that JTable works the same
     * way as the JList. See setSelectionMode() in JList for details
     * about the modes.
     *
     * @see JList#setSelectionMode
     * @beaninfo
     * description: The selection mode used by the row and column selection models.
     *        enum: SINGLE_SELECTION            ListSelectionModel.SINGLE_SELECTION
     *              SINGLE_INTERVAL_SELECTION   ListSelectionModel.SINGLE_INTERVAL_SELECTION
     *              MULTIPLE_INTERVAL_SELECTION ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
     */
  public void setSelectionMode(int selectionMode)
  {
    m_tableView.setSelectionMode( selectionMode);
  }

    /**
     * Sets whether the rows in this model can be selected.
     *
     * @see #getRowSelectionAllowed()
     * @beaninfo
     * description: If true, an entire row is selected for each selected cell.
     */
  public void setRowSelectionAllowed(boolean flag)
  {
    m_tableView.setRowSelectionAllowed( flag);
  }

    /**
     * Returns true if rows can be selected.
     *
     * @return true if rows can be selected
     * @see #setRowSelectionAllowed()
     */
  public boolean getRowSelectionAllowed()
  {
    return m_tableView.getRowSelectionAllowed();
  }

    /**
     * Sets whether the columns in this model can be selected.
     *
     * @see #getColumnSelectionAllowed()
     * @beaninfo
     * description: If true, an entire column is selected for each selected cell.
     */
  public void setColumnSelectionAllowed(boolean flag)
  {
    m_tableView.setColumnSelectionAllowed( flag);
  }

    /**
     * Returns true if columns can be selected.
     *
     * @return true if columns can be selected.
     * @see #setColumnSelectionAllowed
     */
  public boolean getColumnSelectionAllowed()
  {
    return m_tableView.getColumnSelectionAllowed();
  }

    /**
     * Sets whether this table allows both a column selection and a
     * row selection to exist at the same time. When set, this results
     * in a facility to select a rectangular region of cells in the display.
     * This flag over-rides the row and column selection
     * modes ensuring that cell selection is possible whenever this flag is set.

     * @see #getCellSelectionEnabled
     * @beaninfo
     * description: Select a rectangular region of cells rather than rows or columns.
     */
  public void setCellSelectionEnabled(boolean flag)
  {
    m_tableView.setCellSelectionEnabled( flag);
  }

    /**
     * Returns true if simultaneous row and column selections are allowed
     *
     * @return true if simultaneous row and column selections are allowed
     * @see #setCellSelectionEnabled
     */
  public boolean getCellSelectionEnabled()
  {
    return m_tableView.getCellSelectionEnabled();
  }

    /**
     *  If a column is selected, then this selects all columns.  Similarly,
     *  if a row is selected, then, this selects all rows.  If both a column
     *  and a row are selected at the time this method is invoked, then
     *  all columns and rows are selected.
     */
  public void selectAll()
  {
    m_tableView.selectAll();
  }

    /**
     * Deselects all selected columns and rows.  If empty selection is not
     * allowed, then it leaves the first row selected.
     */
  public void clearSelection()
  {
    m_tableView.clearSelection();
  }

    /**
     * Selects the rows from <i>index0</i> to <i>index1</i> inclusive.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
  public void setRowSelectionInterval(int index0, int index1)
  {
    m_tableView.setRowSelectionInterval( index0, index1);
  }

    /**
     * Selects the columns from <i>index0</i> to <i>index1</i> inclusive.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
  public void setColumnSelectionInterval(int index0, int index1)
  {
    m_tableView.setColumnSelectionInterval( index0, index1);
  }

    /**
     * Adds the rows from <i>index0</i> to <i>index0</i> inclusive to
     * the current selection.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
  public void addRowSelectionInterval(int index0, int index1)
  {
    m_tableView.addRowSelectionInterval( index0, index1);
  }

    /**
     * Adds the columns from <i>index0</i> to <i>index0</i> inclusive to
     * the current selection.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
  public void addColumnSelectionInterval(int index0, int index1)
  {
    m_tableView.addColumnSelectionInterval( index0, index1);
  }

    /**
     * Deselects the rows from <i>index0</i> to <i>index0</i> inclusive.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
  public void removeRowSelectionInterval(int index0, int index1)
  {
    m_tableView.removeRowSelectionInterval( index0, index1);
  }

    /**
     * Deselects the columns from <i>index0</i> to <i>index0</i> inclusive.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
  public void removeColumnSelectionInterval(int index0, int index1)
  {
    m_tableView.removeColumnSelectionInterval( index0, index1);
  }

    /**
     * Returns the index of the last row selected or added to the selection.
     *
     * @return the index of the last row selected or added to the selection,
     *         (lead selection) or -1 if no row is selected.
     * @see #getSelectedRows()
     */
  public int getSelectedRow()
  {
    return m_tableView.getSelectedRow();
  }

    /**
     * Returns the index of the last column selected or added to the selection.
     *
     * @return the index of the last column selected or added to the selection,
     *         (lead selection) or -1 if no column is selected.
     * @see #getSelectedColumns()
     */
  public int getSelectedColumn()
  {
    return m_tableView.getSelectedColumn();
  }

    /**
     * Returns the table name of the last column selected or added to the selection.
     *
     * @return the table name of the last column selected or added to the selection,
     *         (lead selection) or null if no column is selected.
     */
  public String getSelectedColumnTableName()
  {
    if( m_dbtabadp != null)
      return m_dbtabadp.getColumnTableName( getSelColumnName());
    return null;
  }

    /**
     * Returns the indices of all selected rows.
     *
     * @return an array of ints containing the indices of all selected rows,
     *         or an empty array if no row is selected.
     * @see #getSelectedRow()
     */
  public int[] getSelectedRows()
  {
    return m_tableView.getSelectedRows();
  }

    /**
     * Returns the indices of all selected columns.
     *
     * @return an array of ints containing the indices of all selected columns,
     *         or an empty array if no column is selected.
     * @see #getSelectedColumn()
     */
  public int[] getSelectedColumns()
  {
    return m_tableView.getSelectedColumns();
  }

    /**
     * Returns the number of selected rows.
     *
     * @return the number of selected rows, 0 if no columns are selected
     */
  public int getSelectedRowCount()
  {
    return m_tableView.getSelectedRowCount();
  }

    /**
     * Returns the number of selected columns.
     *
     * @return the number of selected columns, 0 if no columns are selected
     */
  public int getSelectedColumnCount()
  {
    return m_tableView.getSelectedColumnCount();
  }

    /**
     * Returns true if the row at the specified index is selected
     *
     * @return true if the row at index <I>row</I> is selected, where 0 is the
     *              first row
     * @exception IllegalArgumentException      if <I>row</I> is not in the
     *                                          valid range
     */
  public boolean isRowSelected(int row)
  {
    return m_tableView.isRowSelected( row);
  }

    /**
     * Returns true if the column at the specified index is selected
     *
     * @return true if the column at index <I>column</I> is selected, where
     *              0 is the first column
     * @exception IllegalArgumentException      if <I>column</I> is not in the
     *                                          valid range
     */
  public boolean isColumnSelected(int column)
  {
    return m_tableView.isColumnSelected( column);
  }

    /**
     * Returns true if the cell at the specified position is selected.
     *
     * @return true if the cell at index <I>(row, column)</I> is selected,
     *              where the first row and first column are at index 0
     * @exception IllegalArgumentException      if <I>row</I> or <I>column</I>
     *                                          are not in the valid range
     */
  public boolean isCellSelected(int row, int column)
  {
    return m_tableView.isCellSelected( row, column);
  }

    /**
     * Returns the foreground color for selected cells.
     *
     * @return the Color object for the foreground property
     * @see #setSelectionForeground
     * @see #setSelectionBackground
     */
  public Color getSelectionForeground()
  {
    return m_tableView.getSelectionForeground();
  }

    /**
     * Set the foreground color for selected cells.  Cell renderers
     * can use this color to render text and graphics for selected
     * cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionForeground  the Color to use in the foreground
     *                             for selected list items
     * @see #getSelectionForeground
     * @see #setSelectionBackground
     * @see #setForeground
     * @see #setBackground
     * @see #setFont
     * @beaninfo
     *       bound: true
     * description: A default foreground color for selected cells.
     */
  public void setSelectionForeground(Color selectionForeground)
  {
    m_tableView.setSelectionForeground( selectionForeground);
  }

    /**
     * Returns the background color for selected cells.
     *
     * @return the Color used for the background of selected list items
     * @see #setSelectionBackground
     * @see #setSelectionForeground
     */
  public Color getSelectionBackground()
  {
    return m_tableView.getSelectionBackground();
  }

    /**
     * Set the background color for selected cells.  Cell renderers
     * can use this color to the fill selected cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionBackground  the Color to use for the background
     *                             of selected cells
     * @see #getSelectionBackground
     * @see #setSelectionForeground
     * @see #setForeground
     * @see #setBackground
     * @see #setFont
     * @beaninfo
     *       bound: true
     * description: A default background color for selected cells.
     */
  public void setSelectionBackground(Color selectionBackground)
  {
    m_tableView.setSelectionBackground( selectionBackground);
  }

    /**
     * Returns the <B>TableColumn</B> object for the column in the table
     * whose identifier is equal to <I>identifier</I>, when compared using
     * <I>equals()</I>.
     *
     * @return  the TableColumn object with matching identifier
     * @exception IllegalArgumentException      if <I>identifier</I> is null or no TableColumn has this identifier
     *
     * @param   identifier                      the identifier object
     */
  public TableColumn getColumn(Object identifier)
  {
    return m_tableView.getColumn( identifier);
  }

//
// Informally implement the TableModel interface.
//

    /**
     * Return the index of the column in the model whose data is being displayed in
     * the column <I>viewColumnIndex</I> in the display. Returns <I>viewColumnIndex</I>
     * unchanged when <I>viewColumnIndex</I> is less than zero.
     *
     * @see #convertColumnIndexToView
     */
  public int convertColumnIndexToModel(int viewColumnIndex)
  {
    return m_tableView.convertColumnIndexToModel( viewColumnIndex);
  }

    /**
     * Return the index of the column in the view which is displaying the
     * data from the column <I>modelColumnIndex</I> in the model. Returns
     * -1 if this column is not being displayed. Returns <I>modelColumnIndex</I>
     * unchanged when <I>modelColumnIndex</I> is less than zero.
     *
     * @see #convertColumnIndexToModel
     */
  public int convertColumnIndexToView(int modelColumnIndex)
  {
    return m_tableView.convertColumnIndexToView( modelColumnIndex);
  }

//
// Adding and removing columns in the view
//

    /**
     *  Appends <I>aColumn</I> to the end of the array of columns held by
     *  the JTable's column model.
     *  If the header value of <I>aColumn</I> is <I>null</I>,
     *  sets the header value of <I>aColumn</I> to the name
     *  returned by <code>getModel().getColumnName()</code>.
     *  <p>
     *  To add a column to the JTable to display the <I>modelColumn</I>'th column of
     *  data in the model, with a given <I>width</I>,
     *  <I>cellRenderer</I> and <I>cellEditor</I> you can use:
     *  <pre>
     *
     *      addColumn(new TableColumn(modelColumn, width, cellRenderer, cellEditor));
     *
     *  </pre>
     *  [All of the other constructors in the TableColumn can be used in place of
     *  this one.] The model column is stored inside the TableColumn and is used during
     *  rendering and editing to locate the appropriate data values in the
     *  model. The model column does not change when columns are reordered
     *  in the view.
     *
     *  @param  aColumn         The <B>TableColumn</B> to be added
     *  @see    #removeColumn
     */
  public void addColumn(TableColumn aColumn)
  {
    m_tableView.addColumn( aColumn);
  }

    /**
     *  Removes <I>aColumn</I> from the JTable's array of columns.
     *  Note: this method does not remove the column of data from the
     *  model it just removes the TableColumn that was displaying it.
     *
     *  @param  aColumn         The <B>TableColumn</B> to be removed
     *  @see    #addColumn
     */
  public void removeColumn(TableColumn aColumn)
  {
    m_tableView.removeColumn( aColumn);
  }

    /**
     * Moves the column <I>column</I> to the position currently occupied by the
     * column <I>targetColumn</I>.  The old column at <I>targetColumn</I> is
     * shifted left or right to make room.
     *
     * @param   column                  the index of column to be moved
     * @param   targetColumn            the new index of the column
     */
  public void moveColumn(int column, int targetColumn)
  {
    m_tableView.moveColumn( column, targetColumn);
  }

//
// Cover methods for various models and helper methods
//

    /**
     * Returns the index of the column that <I>point</I> lies in, or -1 if it
     * lies outside the receiver's bounds.
     *
     * @return  the index of the column that <I>point</I> lies in, or -1 if it
     *          lies outside the receiver's bounds
     * @see     #rowAtPoint
     */
  public int columnAtPoint(Point point)
  {
    return m_tableView.columnAtPoint( point);
  }

    /**
     * Returns the index of the row that <I>point</I> lies in, or -1 if is
     * not in the range [0, getRowCount()-1].
     *
     * @return  the index of the row that <I>point</I> lies in, or -1 if it
     *          is not in the range [0, getRowCount()-1]
     * @see     #columnAtPoint()
     */
  public int rowAtPoint(Point point)
  {
    return m_tableView.rowAtPoint( point);
  }

    /**
     * Returns a rectangle locating the cell that lies at the intersection of
     * <I>row</I> and <I>column</I>.   If <I>includeSpacing</I> is true then
     * the value returned includes the intercellSpacing margin.  If it is false,
     * then the returned rect is inset by half of intercellSpacing.
     * (This is the true frame of the cell)
     *
     * @param   row                             the row to compute
     * @param   column                          the column to compute
     * @param   includeSpacing                  if true, the rect returned will
     *                                          include the correct
     *                                          intercellSpacing
     * @return  the rectangle containing the cell at index
     *          <I>row</I>,<I>column</I>
     * @exception IllegalArgumentException      If <I>row</I> or <I>column</I>
     *                                          are not in the valid range.
     */
  public Rectangle getCellRect(int row, int column, boolean includeSpacing)
  {
    return m_tableView.getCellRect( row, column, includeSpacing);
  }

    /**
     * This method will resize one or more columns of the table
     * so that the sum width of all columns will equal to the
     * width of the table.  It will spread the
     * size delta proportionately to all the columns, while respecting
     * each column's max and min size limits.  Also, notifications of each
     * column width change will be sent out as they are resized.  <p>
     *
     * Note: It is possible that even after this method is called,
     *   the total width of the columns is still not be equal to the width
     *   of the table.  eg. A table with a single column, the column has a
     *   minimum width of 20, and the tableView has a width of 10.  And there
     *   is nothing I can do about that.
     */
  public void sizeColumnsToFit()
  {
    m_tableView.sizeColumnsToFit( -1);
  }

  public void setToolTipText(String text)
  {
    m_tableView.setToolTipText( text);
  }

    /**
     * Overrides JComponent's setToolTipText method to allow use of the
     * renderer's tips (if the renderer has text set).
     * <p>
     * NOTE: For JTable to properly display tooltips of its renderers
     *       JTable must be a registered component with the ToolTipManager.
     *       This is done automatically in initializeLocalVars(), but
     *       if at a later point JTable is told setToolTipText(null)
     *       it will unregister the table component, and no tips from
     *       renderers will display anymore.
     *
     * @see JComponent#getToolTipText
     */
  public String getToolTipText(MouseEvent event)
  {
    return m_tableView.getToolTipText( event);
  }

//
// Editing Support
//

    /**
     * Programmatically starts editing the cell at <I>row</I> and
     * <I>column</I>, if the cell is editable.
     *
     * @param   row                             the row to be edited
     * @param   column                          the column to be edited
     * @exception IllegalArgumentException      If <I>row</I> or <I>column</I>
     *                                          are not in the valid range
     * @return  false if for any reason the cell cannot be edited.
     */
  public boolean editCellAt(int row, int column)
  {
    return m_tableView.editCellAt( row, column);
  }

    /**
     * Programmatically starts editing the cell at <I>row</I> and
     * <I>column</I>, if the cell is editable.
     * To prevent the JTable from editing a particular table, column or
     * cell value, return false from the isCellEditable() method in the
     * TableModel interface.
     *
     * @param   row                             the row to be edited
     * @param   column                          the column to be edited
     * @param   e                               event to pass into
     *                                          shouldSelectCell
     * @exception IllegalArgumentException      If <I>row</I> or <I>column</I>
     *                                          are not in the valid range
     * @return  false if for any reason the cell cannot be edited.
     */
  public boolean editCellAt(int row, int column, EventObject e)
  {
    return m_tableView.editCellAt( row, column, e);
  }

    /**
     * Returns  true is the table is editing a cell.
     *
     * @return  true is the table is editing a cell
     * @see     #editingColumn()
     * @see     #editingRow()
     */
  public boolean isEditing()
  {
    return m_tableView.isEditing();
  }

    /**
     * If the receiver is currently editing this will return the Component
     * that was returned from the CellEditor.
     *
     * @return  Component handling editing session
     */
  public Component getEditorComponent()
  {
    return m_tableView.getEditorComponent();
  }

    /**
     * This returns the index of the editing column.
     *
     * @return  the index of the column being edited
     * @see #editingRow()
     */
  public int getEditingColumn()
  {
    return m_tableView.getEditingColumn();
  }

    /**
     * Returns the index of the editing row.
     *
     * @return  the index of the row being edited
     * @see #editingColumn()
     */
  public int getEditingRow()
  {
    return m_tableView.getEditingRow();
  }

//
// Managing TableUI
//

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the TableUI object that renders this component
     */
  public TableUI getTableUI()
  {
    return m_tableView.getUI();
  }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the TableUI L&F object
     * @see UIDefaults#getUI
     */
  public void setTableUI(TableUI ui)
  {
    m_tableView.setUI( ui);
  }

    /**
     * Notification from the UIManager that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * UIManager.
     *
     * @see JComponent#updateUI
     */
  public void updateUI()
  {
    if( m_tableView != null)
      m_tableView.updateUI();
  }

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "TableUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
  public String getUIClassID()
  {
    return m_tableView.getUIClassID();
  }


//
// Managing models
//

    /**
     * Sets the data model for this table to <I>newModel</I> and registers
     * with for listner notifications from the new data model.
     *
     * @param   newModel        the new data source for this table
     * @exception IllegalArgumentException      if <I>newModel</I> is null
     * @see     #getModel()
     * @beaninfo
     * description: The model that is the source of the data for this view.
     */
  public void setModel(TableModel newModel)
  {
    m_tableView.setModel( newModel);
  }

    /**
     * Returns the <B>TableModel</B> that provides the data displayed by
     * the receiver.
     *
     * @return  the object that provides the data displayed by the receiver
     * @see     #setModel()
     */
  public TableModel getModel()
  {
    return m_tableView.getModel();
  }

    /**
     * Sets the column model for this table to <I>newModel</I> and registers
     * with for listner notifications from the new column model. Also sets
     * the column model of the JTableHeader to <I>newModel</I>.
     *
     * @param   newModel        the new data source for this table
     * @exception IllegalArgumentException      if <I>newModel</I> is null
     * @see     #getColumnModel()
     * @beaninfo
     * description: The object governing the way columns appear in the view.
     */
  public void setColumnModel(TableColumnModel newModel)
  {
    m_tableView.setColumnModel( newModel);
  }

    /**
     * Returns the <B>TableColumnModel</B> that contains all column inforamtion
     * of this table.
     *
     * @return  the object that provides the column state of the table
     * @see     #setColumnModel()
     */
  public TableColumnModel getColumnModel()
  {
    return m_tableView.getColumnModel();
  }

    /**
     * Sets the row selection model for this table to <I>newModel</I>
     * and registers with for listner notifications from the new selection model.
     *
     * @param   newModel        the new selection model
     * @exception IllegalArgumentException      if <I>newModel</I> is null
     * @see     #getSelectionModel()
     * @beaninfo
     * description: The selection model for rows.
     */
  public void setSelectionModel(ListSelectionModel newModel)
  {
    m_tableView.setSelectionModel( newModel);
  }

    /**
     * Returns the <B>ListSelectionModel</B> that is used to maintain row
     * selection state.
     *
     * @return  the object that provides row selection state.  Or <B>null</B>
     *          if row selection is not allowed.
     * @see     #setSelectionModel()
     */
  public ListSelectionModel getSelectionModel()
  {
    return m_tableView.getSelectionModel();
  }

//
// Implementing TableModelListener interface
//

    /**
     * The TableModelEvent should be constructed in the co-ordinate system
     * of the model, the appropriate mapping to the view co-ordinate system
     * is performed by the JTable when it recieves the event.
     */
  public void tableChanged(TableModelEvent e)
  {
    m_tableView.tableChanged( e);
  }

//
// Implementing TableColumnModelListener interface
//

    /**
     * Tells listeners that a column was added to the model.
     *
     * @see TableColumnModelListener
     */
  public void columnAdded(TableColumnModelEvent e)
  {
    m_tableView.columnAdded( e);
  }

    /**
     * Tells listeners that a column was removed from the model.
     *
     * @see TableColumnModelListener
     */
  public void columnRemoved(TableColumnModelEvent e)
  {
    m_tableView.columnRemoved( e);
  }

    /**
     * Tells listeners that a column was repositioned.
     *
     * @see TableColumnModelListener
     */
  public void columnMoved(TableColumnModelEvent e)
  {
    m_tableView.columnMoved( e);
  }

    /**
     * Tells listeners that a column was moved due to a margin change.
     *
     * @see TableColumnModelListener
     */
  public void columnMarginChanged(ChangeEvent e)
  {
    m_tableView.columnMarginChanged( e);
  }

    /**
     * Tells listeners that the selection model of the
     * TableColumnModel changed.
     *
     * @see TableColumnModelListener
     */
  public void columnSelectionChanged(ListSelectionEvent e)
  {
    m_tableView.columnSelectionChanged( e);
  }

//
// Implementing ListSelectionListener interface
//

    /**
     * Tells listeners that the selection changed.
     *
     * @see ListSelectionListener
     */
  public void valueChanged(ListSelectionEvent e)
  {
    m_tableView.valueChanged( e);
  }

//
// Implementing the CellEditorListener interface
//

    /**
     * Invoked when editing is finished. The changes are saved, the
     * editor object is discarded, and the cell is rendered once again.
     *
     * @see CellEditorListener
     */
  public void editingStopped(ChangeEvent e)
  {
    m_tableView.editingStopped( e);
  }

    /**
     * Invoked when editing is canceled. The editor object is discarded
     * and the cell is rendered once again.
     *
     * @see CellEditorListener
     */
  public void editingCanceled(ChangeEvent e)
  {
    m_tableView.editingCanceled( e);
  }

//
// Implementing the Scrollable interface
//

    /**
     * Sets the preferred size of the viewport for this table.
     *
     * @param size  a Dimension object specifying the preferredSize of a
     *              JViewport whose view is this table
     * @see Scrollable#getPreferredScrollableViewportSize
     * @beaninfo
     * description: The preferred size of the viewport.
     */
  public void setPreferredScrollableViewportSize(Dimension size)
  {
    m_tableView.setPreferredScrollableViewportSize( size);
  }

    /**
     * Returns the preferred size of the viewport for this table.
     *
     * @return a Dimension object containing the preferredSize of the JViewport
     *         which displays this table
     * @see Scrollable#getPreferredScrollableViewportSize
     */
  public Dimension getPreferredScrollableViewportSize()
  {
    return m_tableView.getPreferredScrollableViewportSize();
  }

    /**
     * Returns the scroll increment that completely exposes one new row
     * or column (depending on the orientation).
     * <p>
     * This method is called each time the user requests a unit scroll.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction
     * @see Scrollable#getScrollableUnitIncrement
     */
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,	int direction)
  {
    return m_tableView.getScrollableUnitIncrement( visibleRect, orientation, direction);
  }

    /**
     * Returns The visibleRect.height or visibleRect.width, depending on the
     * table's orientation.
     *
     * @return The visibleRect.height or visibleRect.width per the orientation.
     * @see Scrollable#getScrollableBlockIncrement
     */
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return m_tableView.getScrollableBlockIncrement( visibleRect, orientation, direction);
  }

    /**
     * Returns false to indicate that the width of the viewport does not
     * determine the width of the table.
     *
     * @return false
     * @see Scrollable#getScrollableTracksViewportWidth
     */
  public boolean getScrollableTracksViewportWidth()
  {
    return m_tableView.getScrollableTracksViewportWidth();
  }

    /**
     * Returns false to indicate that the height of the viewport does not
     * determine the height of the table.
     *
     * @return false
     * @see Scrollable#getScrollableTracksViewportHeight
     */
  public boolean getScrollableTracksViewportHeight()
  {
    return m_tableView.getScrollableTracksViewportHeight();
  }

    /**
     * Return the cellEditor.
     *
     * @return the TableCellEditor that does the editing
     * @see #cellEditor
     */
  public TableCellEditor getCellEditor()
  {
    return m_tableView.getCellEditor();
  }

    /**
     * Set the cellEditor variable.
     *
     * @param anEditor  the TableCellEditor that does the editing
     * @see #cellEditor
     */
  public void setCellEditor(TableCellEditor anEditor)
  {
    m_tableView.setCellEditor( anEditor);
  }

    /**
     * Set the editingColumn variable.
     *
     * @see #editingColumn
     */
  public void setEditingColumn(int aColumn)
  {
    m_tableView.setEditingColumn( aColumn);
  }

    /**
     * Set the editingRow variable.
     *
     * @see #editingRow
     */
  public void setEditingRow(int aRow)
  {
    m_tableView.setEditingRow( aRow);
  }

    /**
     * Returns true to indicate that this component paints every pixel
     * in its range. (In other words, it does not have a transparent
     * background or foreground.)
     *
     * @return true
     * @see JComponent#isOpaque
     */
  public boolean isOpaque()
  {
    return m_tableView.isOpaque();
  }

    /**
     * Sets up the specified editor using the value at the specified cell.
     *
     * @param editor  the TableCellEditor to set up
     * @param row     the row of the cell to edit, where 0 is the first
     * @param column  the column of the cell to edit, where 0 is the first
     */
  public Component prepareEditor(TableCellEditor editor, int row, int column)
  {
    return m_tableView.prepareEditor( editor, row, column);
  }

    /**
     * Discard the editor object and return the real estate it used to
     * cell rendering.
     */
  public void removeEditor()
  {
    m_tableView.removeEditor();
  }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
  public AccessibleContext getAccessibleContext()
  {
    return m_tableView.getAccessibleContext();
  }

// End jTable functions

  public String getUsr()
  {
    return m_strUsr;
  }

  public void setUsr(String s)
  {
    m_strUsr = s;
  }

  public String getPwd()
  {
    return m_strPwd;
  }

  public void setPwd(String s)
  {
    m_strPwd = s;
  }

  public String getDriver()
  {
    return m_strDriver;
  }

  public void setDriver(String s)
  {
    m_strDriver = s;
  }

  public String getUrl()
  {
    return m_strUrl;
  }

  public void setUrl(String s)
  {
    m_strUrl = s;
  }

  public void reset()
  {
    m_strUsr = null;
    m_strPwd = null;
  }

  public void loginDlg()
  {
    if(m_dlgLogin == null)
    {
      m_dlgLogin = new JExDialog( m_strAppName);
      m_dlgLogin.setModal( true);
      m_dlgLogin.setTitle( "Database login:");
      m_dlgLogin.getContentPane().setLayout(new BorderLayout());
      m_dlgLogin.setResizable(false);
      //Point point = getParent().getLocation();
      m_textUsr = new JTextField(15);
      m_passwordPwd = new JPasswordField(15);
      JPanel jpanel = new JPanel();
      jpanel.setLayout(new GridLayout(4, 1));
      JLabel jlabel = new JLabel("Login:");
      JLabel jlabel1 = new JLabel("Password:");
      jpanel.add(jlabel);
      jpanel.add(m_textUsr);
      jpanel.add(jlabel1);
      jpanel.add(m_passwordPwd);
      m_dlgLogin.getContentPane().add("Center", jpanel);
      JPanel jpanel1 = new JPanel();
      jpanel1.setLayout(new FlowLayout(1));
      JButton jbutton = new JButton("Ok");
      jbutton.setMnemonic('O');
      jbutton.setActionCommand("Ok");
      jbutton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent actionevent)
        {
          String s = actionevent.getActionCommand();
          if(s.equals("Ok"))
          {
            TablePanel.m_strUsr = m_textUsr.getText();
            TablePanel.m_strPwd = String.valueOf( m_passwordPwd.getPassword());
            m_dlgLogin.setVisible(false);
          }
        }

      });
      jpanel1.add(jbutton);
      m_dlgLogin.getContentPane().add("South", jpanel1);
      m_dlgLogin.pack();
      if( m_strUsr != null)
        m_textUsr.setText( m_strUsr);
      if( m_strPwd != null)
        m_passwordPwd.setText( m_strPwd);
      m_dlgLogin.setVisible(true);
      getRootPane().setDefaultButton( jbutton);
      if( m_strUsr != null)
        m_passwordPwd.requestFocus();
      else
        m_textUsr.requestFocus();
      return;
    }
    else
    {
      if( m_strUsr == null || m_strUsr.equals( ""))
        m_textUsr.setText( "");
      if( m_strPwd == null || m_strPwd.equals( ""))
        m_passwordPwd.setText( "");
      m_dlgLogin.setVisible(true);
      if( m_strUsr != null)
        m_passwordPwd.requestFocus();
      else
        m_textUsr.requestFocus();
      return;
    }
  }

  public void errorMsgDlg(String s)
  {
    JOptionPane.showConfirmDialog(getParent(), s, "Error!", -1, 0);
  }

  public boolean questionMsgDlg(String s)
  {
    return JOptionPane.showConfirmDialog(getParent(), s, "Question!", 0, 3) == 0;
  }

  public void infoMsgDlg(String s)
  {
    JOptionPane.showConfirmDialog(getParent(), s, "Info!", -1, 1);
  }

  public boolean warnMsgDlg(String s)
  {
    return JOptionPane.showConfirmDialog(getParent(), s, "Warning!", 2, 2) == 0;
  }

  public String inputMsgDlg(String s)
  {
    String s1 = null;
    s1 = JOptionPane.showInputDialog(getParent(), s, "Input!", 1);
    return s1;
  }

  public void progressDlg(int i)
  {
    m_dlgProBar = new JDialog();
    m_dlgProBar.setTitle( "Progress");
    m_dlgProBar.setDefaultCloseOperation(0);
    Point point = getParent().getLocation();
    m_dlgProBar.setLocation(point.x + 30, point.y + 30);
    if(m_probar == null)
      m_probar = new JProgressBar();
    m_probar.getAccessibleContext().setAccessibleName("Fortschritt");
    m_probar.setMinimum(0);
    m_probar.setMaximum(i);
    m_probar.setValue(0);
    m_dlgProBar.getContentPane().setLayout(new FlowLayout(1));
    m_dlgProBar.getContentPane().add(m_probar);
    m_dlgProBar.pack();
    m_dlgProBar.setVisible(true);
  }

  public void setProBarLimits(int i, int j)
  {
    if(m_probar != null)
    {
      m_probar.setMinimum(0);
      m_probar.setMaximum(j);
    }
  }

  public void stepProgress(int i)
  {
    if(m_probar != null)
    {
      if(i == -1)
      {
        m_dlgProBar.setVisible(false);
        m_dlgProBar.dispose();
        m_dlgProBar = null;
        m_probar = null;
        return;
      }
      if(m_probar.getMaximum() > m_probar.getValue() + i)
      {
        m_probar.setValue(m_probar.getValue() + i);
        return;
      }
      m_probar.setValue(m_probar.getMaximum());
      try
      {
        Thread.sleep(500L);
      }
      catch(InterruptedException ex) {}
      m_dlgProBar.setVisible(false);
      m_dlgProBar.dispose();
      m_dlgProBar = null;
      m_probar = null;
    }
  }

  public boolean getAutoConnect()
  {
    return m_blConnect;
  }

  public void setAutoConnect( boolean blConnect)
  {
    m_blConnect = blConnect;
  }

  public void BeginWait()
  {
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    m_tableView.setCursor(new Cursor(Cursor.WAIT_CURSOR));
  }

  public void endWait()
  {
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    m_tableView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  public void paint(Graphics g)
  {
    try
    {
      Rectangle r = g.getClipBounds();
      g.setColor( getBackground() );
      g.fillRect( r.x, r.y, r.width, r.height );
      super.paint( g );
    }
    catch( NullPointerException nex)
    {
      //System.err.println( "null");
    }
    catch( Exception ex )
    {
      errPrintln( ex.toString());
    }
  }

//  private void errPrint( String str)
//  {
//    errWrite( str);
//  }
//
//  private void print( String str)
//  {
//    write( str);
//  }

  private void errPrintln( String str)
  {
    errWrite( str + "\n");
  }

//  private void println( String str)
//  {
//    write( str + "\n");
//  }

  private void errWrite( String str)
  {
    try
    {
      for( int i = 0; i < str.length(); i++)
      {
        m_printsErr.write( str.charAt( i));
      }
    }
    catch( java.io.IOException ex)
    {
      ex.printStackTrace();
    }
  }

//  private void write( String str)
//  {
//    try
//    {
//      for( int i = 0; i < str.length(); i++)
//      {
//        m_printsOut.write( str.charAt( i));
//      }
//    }
//    catch( java.io.IOException ex)
//    {
//      ex.printStackTrace();
//    }
//  }
}
