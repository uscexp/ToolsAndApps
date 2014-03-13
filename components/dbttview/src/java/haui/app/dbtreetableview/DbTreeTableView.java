package haui.app.dbtreetableview;

//import java.awt.*;
import haui.components.desktop.JDesktopFrame;
import haui.components.desktop.JExInternalFrame;
import haui.dbtool.TablePanel;
import haui.dbtool.TreePanel;
import haui.io.MultiFileFilter;
import haui.resource.ResouceManager;
import haui.sqlpanel.SQLPanel;
import haui.tool.AniGifPanel;
import haui.tool.InternalCancelDlg;
import haui.util.AppProperties;
import haui.util.GlobalAppProperties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 *
 *		Module:					DbTreeTableView.java
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\DbTreeTableView.java,v $
 *<p>
 *		Description:
 *</p><p>
 *		Created:				??.07.1998	by	AE
 *</p><p>
 *		@history				23.04.1999	by	AE: Ported to Jdk 1.2 and the ported haui package.<br>
 *										06.05.1999	by	AE: JSlitPanes implemented.<br>
 *										12.05.1999	by	AE: JToolBar added.<br>
 *										18.05.1999	by	AE: JToolBar Icons added.<br>
 *										07.03.2000	by	AE: JCheckBoxMenuItem "Fixed Columns" added.<br>
 *										08.03.2000	by	AE: Adapted to changed haui.dbtool.TreePanel and read and write dimension props to file.<br>
 *										21.08.2000	by	AE: Functions and classes for SQL statement list added.<br>
 *										22.08.2000	by	AE: Input variables (= ?) for SQL statements added.<br>
 *										22.08.2000	by	AE: Bugfixes, saveProperties added.<br>
 *										04.09.2000	by	AE: Multible row selection and delete added.<br>
 *										04.09.2000	by	AE: Order by popup and functionality added.<br>
 *										07.09.2000	by	AE: Printing functionality added.<br>
 *										08.09.2000	by	AE: Copy selected rows to insert dialog functionality added.<br>
 *										08.09.2000	by	AE: Limit rows of result set added.<br>
 *										12.09.2000	by	AE: HelpViewer added.<br>
 *										25.09.2000	by	AE: Splashscreen added.<br>
 *										17.10.2000	by	AE: Select from multible drivers and connection URLs added.<br>
 *										19.10.2000	by	AE: Run SQL statement history list added.<br>
 *										03.08.2001	by	AE: Changed SqlEditDlg and QueryArea in JEditTextArea.<br>
 *										03.08.2001	by	AE: SQLStatements can be saved with '\n's.<br>
 *										13.08.2001	by	AE: Auto commit true/false added.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: DbTreeTableView.java,v $
 *		Revision 1.4  2004-08-31 16:03:04+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.3  2004-06-22 14:08:53+02  t026843
 *		bigger changes
 *
 *		Revision 1.2  2004-02-17 15:53:34+01  t026843
 *		permit submenus in sql parts menu
 *
 *		Revision 1.1  2003-05-28 14:19:45+02  t026843
 *		reorganisations
 *
 *		Revision 1.0  2003-05-21 16:25:26+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999,2000,2001,2002; $Revision: 1.4 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\DbTreeTableView.java,v 1.4 2004-08-31 16:03:04+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DbTreeTableView
  extends JExInternalFrame
{
  // Constants
  public static final String PROPSHEADER = "DbTreeTableView application properties";
  public static final String DBTTV = "DbTreeTableView";
  public static final int MAXHIST = 50;
  static int MAXUNDO = 20;

  //{{DECLARE_CONTROLS
  private BorderLayout m_borderLayoutBase = new BorderLayout();
  private JSplitPane jSplitPaneMain;
  private JSplitPane jSplitPaneQuery;
  private JTabbedPane m_jTabbedPane;
  private JScrollPane ListScrollPane;
  private JScrollPane HistListScrollPane;
  //private JEditTextArea QueryStatArea;
  //private JSyntaxEditPanel QueryStatArea;
  private JScrollPane QueryScrollPane;
  private SQLPanel QueryStatArea;
  private TablePanel DbTablePanel;
  //private JToolBar TreeToolBar;
  private TreePanel DbTreePanel;
  private JList SQLList;
  private JList HistList;
  private JMenuBar JMenuBarMain;
  private JMenu JMenuFile;
  private JMenuItem JMenuItemPrint;
  private JSeparator JSeparatorPrint;
  private JMenuItem JMenuItemLoad;
  private JMenuItem JMenuItemAppend;
  private JSeparator JSeparatorLoad;
  private JMenuItem JMenuItemExit;
  private JSeparator JSeparatorExit;
  private JMenu JMenuDb;
  private JMenuItem JMenuItemReconnect;
  private JSeparator JSeparatorSql;
  private JMenuItem JMenuItemSql;
  private JMenuItem JMenuItemExec;
  private JMenuItem JMenuItemBatchExec;
  private JSeparator JSeparatorTable;
  private JMenuItem JMenuItemAdd;
  private JMenuItem JMenuItemDelete;
  private JSeparator JSeparatorCommit;
  private JMenuItem JMenuItemCommit;
  private JMenuItem JMenuItemRollback;
  //private JMenuItem JMenuItemTmpView;
  private JMenu JMenuEdit;
  private JMenuItem JMenuItemUndo;
  private JMenuItem JMenuItemRedo;
  private JSeparator JSeparatorUndo;
  private JMenuItem JMenuItemCut;
  private JMenuItem JMenuItemCopy;
  private JMenuItem JMenuItemPaste;
  private JMenu JMenuView;
  private JRadioButtonMenuItem JRadioButtonMenuItemDBTree;
  private JRadioButtonMenuItem JRadioButtonMenuItemSQLList;
  private JRadioButtonMenuItem JRadioButtonMenuItemHistList;
  private JMenuItem JMenuItemDetail;
  private ButtonGroup buttonGroup;
  private JMenu JMenuOptions;
  private JCheckBoxMenuItem JCheckBoxMenuItemFixedColumns;
  private JCheckBoxMenuItem JCheckBoxMenuItemLimitRows;
  private JCheckBoxMenuItem JCheckBoxMenuItemAutoCommit;
  private JCheckBoxMenuItem JCheckBoxMenuItemDebug;
  private JMenu JMenuSQLList;
  private JMenuItem JMenuItemSQLAdd;
  private JMenuItem JMenuItemSQLEdit;
  private JMenuItem JMenuItemSQLDelete;
  private JMenuItem JMenuItemSQLUp;
  private JMenuItem JMenuItemSQLDown;
  private JMenuItem JMenuItemSQLCrearHist;

  private JPopupMenu jPopup;
  private JMenuItem JMenuItemOrderAsc;
  private JMenuItem JMenuItemOrderDesc;
  private JSeparator JSeparatorOrder;
  // Used for addNotify check.
  private boolean fComponentsAdjusted = false;

  private SymAction m_lSymAction;

  private AppProperties m_appProps;
  private Vector m_vecSql = new Vector();
  private Vector m_vecSqlPartSubMenu = new Vector();
  private Vector m_vecSqlPartMenu = new Vector();
  private String m_strDriver;
  private String m_strUrl;
  private String m_strUsr;
  private String m_strPwd;
  private String m_strTypes[];
  private Dimension m_wsize;
  private Point m_wlocation;
  private int m_spwidth;
  private JButton JButtonDel;
  private JButton JButtonAdd;
  private JButton JButtonExec;
  private JButton JButtonSql;
  private JButton JButtonCommit;
  private JButton JButtonRollback;
  private JButton JButtonExit;
  private JButton JButtonDetail;
  private JToolBar JToolBarMain;
  private AniGifPanel m_aniGifPanel;
  private Component m_coFocus;
  private DBAlias m_dba;
  private boolean m_blConnected = false;
  private boolean m_blOldTransPend = false;

  public DbTreeTableView( JDesktopFrame df, DBAlias dba, Vector vecSql, AppProperties appProps)
  {
    super( df, DbTtvDesktop.DBTTVDT, DBTTV, true, true, true, true);
    if( GlobalAppProperties.instance().getRootComponent( DbTtvDesktop.DBTTVDT) != null)
      GlobalAppProperties.instance().getRootComponent( DbTtvDesktop.DBTTVDT).setCursor(new Cursor( Cursor.WAIT_CURSOR));
    m_dba = dba;
    m_vecSql = vecSql;
    if( appProps == null)
      m_appProps = new AppProperties();
    else
      m_appProps = appProps;

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    //QueryStatArea.setStyle( JSyntaxEditPanel.SQL_STYLE);

    JMenuItemOrderAsc.setText("ORDER BY (ASC)");
    JMenuItemOrderAsc.setActionCommand("ascorder");
    JMenuItemOrderAsc.setMnemonic((int)'A');
    jPopup.add(JMenuItemOrderAsc);
    JMenuItemOrderDesc.setText("ORDER BY (DESC)");
    JMenuItemOrderDesc.setActionCommand("descorder");
    JMenuItemOrderDesc.setMnemonic((int)'D');
    jPopup.add(JMenuItemOrderDesc);
    jPopup.add(JSeparatorOrder);

    m_jTabbedPane.addChangeListener( new ChangeListener()
    {
      public void stateChanged( ChangeEvent e )
      {
        int iIdx = m_jTabbedPane.getSelectedIndex();
        switch( iIdx)
        {
          case 0:
            dbtree_actionPerformed( null);
            break;

          case 1:
            sqllist_actionPerformed( null);
            break;

          case 2:
            histlist_actionPerformed( null);
            break;
        }
      }
    } );

    AdpInternalFrame adpIntFrame = new AdpInternalFrame();
    this.addInternalFrameListener( adpIntFrame);

    //AdpFocus adpFoc = new AdpFocus();
    //this.addFocusListener( adpFoc);

    m_lSymAction = new SymAction();
    initSqlPartMenu();
    JMenuItemAdd.addActionListener(m_lSymAction);
    JMenuItemDelete.addActionListener(m_lSymAction);
    //JMenuItemTmpView.addActionListener(m_lSymAction);
    JMenuItemCommit.addActionListener(m_lSymAction);
    JMenuItemRollback.addActionListener(m_lSymAction);
    JMenuItemPrint.addActionListener(m_lSymAction);
    JMenuItemLoad.addActionListener(m_lSymAction);
    JMenuItemAppend.addActionListener(m_lSymAction);
    JMenuItemExit.addActionListener(m_lSymAction);
    JMenuItemSql.addActionListener(m_lSymAction);
    JMenuItemExec.addActionListener(m_lSymAction);
    JMenuItemBatchExec.addActionListener(m_lSymAction);
    JMenuItemUndo.addActionListener(m_lSymAction);
    JMenuItemRedo.addActionListener(m_lSymAction);
    JMenuItemCut.addActionListener(m_lSymAction);
    JMenuItemCopy.addActionListener(m_lSymAction);
    JMenuItemPaste.addActionListener(m_lSymAction);
    JRadioButtonMenuItemDBTree.addActionListener(m_lSymAction);
    JRadioButtonMenuItemSQLList.addActionListener(m_lSymAction);
    JRadioButtonMenuItemHistList.addActionListener(m_lSymAction);
    JMenuItemDetail.addActionListener(m_lSymAction);
    JMenuItemReconnect.addActionListener(m_lSymAction);
    JCheckBoxMenuItemFixedColumns.addActionListener(m_lSymAction);
    JCheckBoxMenuItemLimitRows.addActionListener(m_lSymAction);
    JCheckBoxMenuItemAutoCommit.addActionListener(m_lSymAction);
    JCheckBoxMenuItemDebug.addActionListener(m_lSymAction);
    JMenuItemSQLAdd.addActionListener(m_lSymAction);
    JMenuItemSQLEdit.addActionListener(m_lSymAction);
    JMenuItemSQLDelete.addActionListener(m_lSymAction);
    JMenuItemSQLUp.addActionListener(m_lSymAction);
    JMenuItemSQLDown.addActionListener(m_lSymAction);
    JMenuItemSQLCrearHist.addActionListener(m_lSymAction);
    JMenuItemOrderAsc.addActionListener(m_lSymAction);
    JMenuItemOrderDesc.addActionListener(m_lSymAction);
    SymComponent aSymComponent = new SymComponent();
    JButtonSql.addActionListener(m_lSymAction);
    JButtonCommit.addActionListener(m_lSymAction);
    JButtonRollback.addActionListener(m_lSymAction);
    JButtonExit.addActionListener(m_lSymAction);
    JButtonExec.addActionListener(m_lSymAction);
    JButtonAdd.addActionListener(m_lSymAction);
    JButtonDel.addActionListener(m_lSymAction);
    JButtonDetail.addActionListener(m_lSymAction);
    this.addComponentListener(aSymComponent);
    //QueryStatArea.setPreferredSize( new Dimension( 200, 40));
    //QueryStatArea.setMinimumSize( new Dimension( 200, 40));
    //pack();
    DbTreePanel.setAutoConnect( false);
    DbTreePanel.setToolBarVisible( false);
    DbTablePanel.setAutoConnect( false);
    DbTablePanel.setPopup( jPopup);
    DbTablePanel.setOutStream( GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT));
    DbTablePanel.setErrStream( GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT));
    DbTreePanel.setTablePanel( DbTablePanel);
    DbTreePanel.setOutStream( GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT));
    DbTreePanel.setErrStream( GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT));
    setFixedColumns();
    setVisible(true);

    // set default row limit
    Integer iRowLimit = m_appProps.getIntegerProperty( DbTtvDesktop.ROWLIMIT);
    if( iRowLimit != null)
    {
      JCheckBoxMenuItemLimitRows.setState( true);
      DbTablePanel.setMaxRows( iRowLimit.intValue());
    }
    else
      JCheckBoxMenuItemLimitRows.setState( false);

    // set default autocommit
    boolean bl = m_appProps.getBooleanProperty( DbTtvDesktop.AUTOCOMMIT);
    JCheckBoxMenuItemAutoCommit.setState( bl);
    autocommit_actionPerformed(null);

    // set default debug output
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.FIXCOLUMNS);
    JCheckBoxMenuItemFixedColumns.setState( bl);
    setFixedColumns();

    // set default debug output
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.SQLDEBUG);
    JCheckBoxMenuItemDebug.setState( bl);

    if( !connect_actionPerformed( null))
    {
      m_blConnected = false;
      if( GlobalAppProperties.instance().getRootComponent( DbTtvDesktop.DBTTVDT) != null)
        GlobalAppProperties.instance().getRootComponent( DbTtvDesktop.DBTTVDT).setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
      return;
    }
    else
      m_blConnected = true;

    adjustComponent();

    // start check thread which checks for pending transactions
    CheckThread ct = new CheckThread();
    ct.start();

    if( GlobalAppProperties.instance().getRootComponent( DbTtvDesktop.DBTTVDT) != null)
      GlobalAppProperties.instance().getRootComponent( DbTtvDesktop.DBTTVDT).setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
  }

  //Initialisierung der Komponente
  private void jbInit() throws Exception
  {
    this.setTitle(DBTTV);
    this.setSize(640,480);
    this.getContentPane().setLayout(m_borderLayoutBase);
    this.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
    SQLList = new JList()
    {
      public String getToolTipText( MouseEvent me)
      {
        Point pLoc = me.getPoint();
        int iIdx = locationToIndex( pLoc);
        String strT = null;

        if( iIdx != -1)
        {
          strT = ((ListElem)m_dba.getSqlListModel().get( iIdx)).getSqlStatement();
          strT = strT.replace( '\n', ' ');
          strT = strT.replace( '\t', ' ');
          int idx = strT.length() > 80 ? 80 : strT.length();
          strT = strT.substring( 0, idx);
        }

        return strT;
      }
    };
    HistList = new JList()
    {
      public String getToolTipText( MouseEvent me)
      {
        Point pLoc = me.getPoint();
        int iIdx = locationToIndex( pLoc);
        String strT = null;

        if( iIdx != -1)
        {
          strT = ((ListElem)m_dba.getHistListModel().get( iIdx)).getSqlStatement();
          strT = strT.replace( '\n', ' ');
          strT = strT.replace( '\t', ' ');
          int idx = strT.length() > 80 ? 80 : strT.length();
          strT = strT.substring( 0, idx);
        }
        return strT;
      }
    };
    JMenuBarMain = new JMenuBar();
    JMenuFile = new JMenu();
    JMenuItemReconnect = new JMenuItem();
    JSeparatorSql = new JSeparator();
    JMenuItemPrint = new JMenuItem();
    JSeparatorPrint = new JSeparator();
    JMenuItemLoad = new JMenuItem();
    JMenuItemAppend = new JMenuItem();
    JSeparatorLoad = new JSeparator();
    JMenuItemExit = new JMenuItem();
    JSeparatorExit = new JSeparator();
    //JMenuItemTmpView = new JMenuItem();
    JMenuDb = new JMenu();
    JMenuItemSql = new JMenuItem();
    JMenuItemExec = new JMenuItem();
    JMenuItemBatchExec = new JMenuItem();
    JSeparatorTable = new JSeparator();
    JMenuItemAdd = new JMenuItem();
    JMenuItemDelete = new JMenuItem();
    JSeparatorCommit = new JSeparator();
    JMenuItemCommit = new JMenuItem();
    JMenuItemRollback = new JMenuItem();
    JMenuEdit = new JMenu();
    JMenuItemUndo = new JMenuItem();
    JMenuItemRedo = new JMenuItem();
    JSeparatorUndo = new JSeparator();
    JMenuItemCut = new JMenuItem();
    JMenuItemCopy = new JMenuItem();
    JMenuItemPaste = new JMenuItem();
    JMenuView = new JMenu();
    JRadioButtonMenuItemDBTree = new JRadioButtonMenuItem();
    JRadioButtonMenuItemSQLList = new JRadioButtonMenuItem();
    JRadioButtonMenuItemHistList = new JRadioButtonMenuItem();
    JMenuItemDetail = new JMenuItem();
    buttonGroup = new ButtonGroup();
    JMenuOptions = new JMenu();
    JCheckBoxMenuItemFixedColumns = new JCheckBoxMenuItem();
    JCheckBoxMenuItemLimitRows = new JCheckBoxMenuItem();
    JCheckBoxMenuItemAutoCommit = new JCheckBoxMenuItem();
    JCheckBoxMenuItemDebug = new JCheckBoxMenuItem();
    JMenuSQLList = new JMenu();
    JMenuItemSQLAdd = new JMenuItem();
    JMenuItemSQLEdit = new JMenuItem();
    JMenuItemSQLDelete = new JMenuItem();
    JMenuItemSQLUp = new JMenuItem();
    JMenuItemSQLDown = new JMenuItem();
    JMenuItemSQLCrearHist = new JMenuItem();

    jPopup = new JPopupMenu();
    JMenuItemOrderAsc = new JMenuItem();
    JMenuItemOrderDesc = new JMenuItem();
    JSeparatorOrder = new JSeparator();
    JButtonDel = new JButton();
    JButtonAdd = new JButton();
    JButtonExec = new JButton();
    JButtonSql = new JButton();
    JButtonCommit = new JButton();
    JButtonRollback = new JButton();
    JButtonExit = new JButton();
    JButtonDetail = new JButton();
    JToolBarMain = new JToolBar();
    this.setJMenuBar(JMenuBarMain);
    JMenuFile.setText("File");
    JMenuFile.setActionCommand("file");
    JMenuFile.setMnemonic((int)'F');
    JMenuDb.setText("Database");
    JMenuDb.setActionCommand("database");
    JMenuDb.setMnemonic((int)'D');
    JButtonDel.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "remove.gif"));
    JButtonDel.setMaximumSize(new Dimension(18, 18));
    JButtonDel.setMinimumSize(new Dimension(18, 18));
    JButtonDel.setPreferredSize(new Dimension(18, 18));
    JButtonDel.setToolTipText("Delete row");
    JButtonDel.setActionCommand("delete");
    JButtonAdd.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "add.gif"));
    JButtonAdd.setMaximumSize(new Dimension(18, 18));
    JButtonAdd.setMinimumSize(new Dimension(18, 18));
    JButtonAdd.setPreferredSize(new Dimension(18, 18));
    JButtonAdd.setToolTipText("Add row");
    JButtonAdd.setActionCommand("add");
    JButtonExec.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "exec.gif"));
    JButtonExec.setMaximumSize(new Dimension(18, 18));
    JButtonExec.setMinimumSize(new Dimension(18, 18));
    JButtonExec.setPreferredSize(new Dimension(18, 18));
    JButtonExec.setToolTipText("Execute SQL-Statement");
    JButtonExec.setActionCommand("exec");
    JButtonExec.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "lightning16.gif"));
    JButtonSql.setMaximumSize(new Dimension(18, 18));
    JButtonSql.setMinimumSize(new Dimension(18, 18));
    JButtonSql.setPreferredSize(new Dimension(18, 18));
    JButtonSql.setToolTipText("Set & Exec Select Statement");
    JButtonSql.setActionCommand("setsel");
    JButtonSql.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "sqlexec16.gif"));
    JButtonCommit.setMaximumSize(new Dimension(18, 18));
    JButtonCommit.setMinimumSize(new Dimension(18, 18));
    JButtonCommit.setPreferredSize(new Dimension(18, 18));
    JButtonCommit.setToolTipText("Commit");
    JButtonCommit.setActionCommand("commit");
    JButtonCommit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "greenflag16.gif"));
    JButtonRollback.setMaximumSize(new Dimension(18, 18));
    JButtonRollback.setMinimumSize(new Dimension(18, 18));
    JButtonRollback.setPreferredSize(new Dimension(18, 18));
    JButtonRollback.setToolTipText("Rollback");
    JButtonRollback.setActionCommand("rollback");
    JButtonRollback.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "rollback16.gif"));
    JButtonExit.setMaximumSize(new Dimension(18, 18));
    JButtonExit.setMinimumSize(new Dimension(18, 18));
    JButtonExit.setPreferredSize(new Dimension(18, 18));
    JButtonExit.setToolTipText("Exit");
    JButtonExit.setActionCommand("exit");
    JButtonExit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "exit16.gif"));
    JMenuBarMain.add(JMenuFile);
    JMenuItemReconnect.setText("Reconnect");
    JMenuItemReconnect.setActionCommand("connect");
    JMenuItemReconnect.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, Event.CTRL_MASK));
    //JMenuItemReconnect.setMnemonic((int)'R');
    JMenuItemReconnect.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "refresh16.gif"));
    JMenuDb.add(JMenuItemReconnect);
    JMenuDb.add(JSeparatorSql);
    JMenuItemSql.setText("Set SQL & exec");
    JMenuItemSql.setActionCommand("setsel");
    JMenuItemSql.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, Event.CTRL_MASK));
    JMenuItemSql.setMnemonic((int)'S');
    JMenuItemSql.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "sqlexec16.gif"));
    JMenuDb.add(JMenuItemSql);
    JMenuItemExec.setText("Execute");
    JMenuItemExec.setActionCommand("exec");
    JMenuItemExec.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_E, Event.CTRL_MASK));
    JMenuItemExec.setMnemonic((int)'E');
    JMenuItemExec.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "lightning16.gif"));
    JMenuDb.add(JMenuItemExec);
    JMenuItemBatchExec.setText("Execute as batch");
    JMenuItemBatchExec.setActionCommand("batchexec");
    JMenuItemBatchExec.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_B, Event.CTRL_MASK));
    JMenuItemBatchExec.setMnemonic((int)'b');
    JMenuItemBatchExec.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "lightning16.gif"));
    JMenuDb.add(JMenuItemBatchExec);
    JMenuDb.add(JSeparatorTable);
    JMenuItemAdd.setText("Add row");
    JMenuItemAdd.setActionCommand("add");
    JMenuItemAdd.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A, Event.CTRL_MASK));
    JMenuItemAdd.setMnemonic((int)'A');
    JMenuItemAdd.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "add.gif"));
    JMenuDb.add(JMenuItemAdd);
    JMenuItemDelete.setText("Delete row");
    JMenuItemDelete.setActionCommand("delete");
    JMenuItemDelete.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, Event.CTRL_MASK));
    JMenuItemDelete.setMnemonic((int)'D');
    JMenuItemDelete.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "remove.gif"));
    JMenuDb.add(JMenuItemDelete);
    //JMenuItemTmpView.setText("Temp. view");
    //JMenuItemTmpView.setActionCommand("tmpview");
    //JMenuItemTmpView.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_T, Event.CTRL_MASK));
    //JMenuItemTmpView.setMnemonic((int)'T');
    //JMenuFile.add(JMenuItemTmpView);
    JMenuDb.add(JSeparatorCommit);
    JMenuItemCommit.setText("Commit");
    JMenuItemCommit.setActionCommand("commit");
    JMenuItemCommit.setMnemonic((int)'C');
    JMenuItemCommit.setEnabled( false);
    JMenuItemCommit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "greenflag16.gif"));
    JMenuDb.add(JMenuItemCommit);
    JMenuItemRollback.setText("Rollback");
    JMenuItemRollback.setActionCommand("rollback");
    JMenuItemRollback.setMnemonic((int)'R');
    JMenuItemRollback.setEnabled( false);
    JMenuItemRollback.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "rollback16.gif"));
    JMenuDb.add(JMenuItemRollback);
    JMenuFile.add(JSeparatorPrint);
    JMenuItemPrint.setText("Print");
    JMenuItemPrint.setActionCommand("print");
    JMenuItemPrint.setMnemonic((int)'P');
    JMenuItemPrint.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Print16.gif"));
    JMenuFile.add(JMenuItemPrint);
    JMenuFile.add(JSeparatorLoad);
    JMenuItemLoad.setText("Load Sql-List");
    JMenuItemLoad.setActionCommand("load");
    JMenuItemLoad.setMnemonic((int)'L');
    JMenuItemLoad.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Open16.gif"));
    JMenuFile.add(JMenuItemLoad);
    JMenuItemAppend.setText("Append Sql-List");
    JMenuItemAppend.setActionCommand("append");
    JMenuItemAppend.setMnemonic((int)'A');
    JMenuItemAppend.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Open16.gif"));
    JMenuFile.add(JMenuItemAppend);
    JMenuFile.add(JSeparatorExit);
    JMenuItemExit.setText("Exit");
    JMenuItemExit.setActionCommand("exit");
    JMenuItemExit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, Event.ALT_MASK));
    JMenuItemExit.setMnemonic((int)'x');
    JMenuItemExit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "exit16.gif"));
    JMenuFile.add(JMenuItemExit);
    JMenuEdit.setText("Edit");
    JMenuEdit.setActionCommand("edit");
    JMenuEdit.setMnemonic((int)'E');
    JMenuBarMain.add(JMenuEdit);
    JMenuBarMain.add(JMenuDb);
    JMenuItemUndo.setText("Undo");
    JMenuItemUndo.setActionCommand("undo");
    JMenuItemUndo.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, Event.CTRL_MASK));
    JMenuItemUndo.setMnemonic((int)'U');
    JMenuItemUndo.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Undo16.gif"));
    JMenuEdit.add(JMenuItemUndo);
    JMenuItemRedo.setText("Redo");
    JMenuItemRedo.setActionCommand("redo");
    JMenuItemRedo.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, Event.ALT_MASK));
    JMenuItemRedo.setMnemonic((int)'R');
    JMenuItemRedo.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Redo16.gif"));
    JMenuEdit.add(JMenuItemRedo);
    JMenuEdit.add(JSeparatorUndo);
    JMenuItemCut.setText("Cut");
    JMenuItemCut.setActionCommand("cut");
    JMenuItemCut.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, Event.CTRL_MASK));
    JMenuItemCut.setMnemonic((int)'t');
    JMenuItemCut.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Cut16.gif"));
    JMenuEdit.add(JMenuItemCut);
    JMenuItemCopy.setText("Copy");
    JMenuItemCopy.setActionCommand("copy");
    JMenuItemCopy.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, Event.CTRL_MASK));
    JMenuItemCopy.setMnemonic((int)'C');
    JMenuItemCopy.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Copy16.gif"));
    JMenuEdit.add(JMenuItemCopy);
    JMenuItemPaste.setText("Paste");
    JMenuItemPaste.setActionCommand("paste");
    JMenuItemPaste.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, Event.CTRL_MASK));
    JMenuItemPaste.setMnemonic((int)'P');
    JMenuItemPaste.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Paste16.gif"));
    JMenuEdit.add(JMenuItemPaste);
    JMenuView.setText("View");
    JMenuView.setActionCommand("view");
    JMenuView.setMnemonic((int)'V');
    JMenuBarMain.add(JMenuView);
    JRadioButtonMenuItemDBTree.setText("DB Tree");
    JRadioButtonMenuItemDBTree.setActionCommand("dbtree");
    JRadioButtonMenuItemDBTree.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_T, Event.CTRL_MASK));
    JRadioButtonMenuItemDBTree.setMnemonic((int)'T');
    JRadioButtonMenuItemDBTree.setSelected( true);
    JMenuView.add(JRadioButtonMenuItemDBTree);
    JRadioButtonMenuItemSQLList.setText("Sql List");
    JRadioButtonMenuItemSQLList.setActionCommand("sqllist");
    JRadioButtonMenuItemSQLList.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_L, Event.CTRL_MASK));
    JRadioButtonMenuItemSQLList.setMnemonic((int)'L');
    JMenuView.add(JRadioButtonMenuItemSQLList);
    JRadioButtonMenuItemHistList.setText("History List");
    JRadioButtonMenuItemHistList.setActionCommand("histlist");
    JRadioButtonMenuItemHistList.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H, Event.CTRL_MASK));
    JRadioButtonMenuItemHistList.setMnemonic((int)'H');
    JMenuView.add(JRadioButtonMenuItemHistList);
    buttonGroup.add(JRadioButtonMenuItemDBTree);
    buttonGroup.add(JRadioButtonMenuItemSQLList);
    buttonGroup.add(JRadioButtonMenuItemHistList);
    JMenuView.addSeparator();
    JMenuItemDetail.setText("Show rec. detail");
    JMenuItemDetail.setActionCommand("detail");
    JMenuItemDetail.setMnemonic((int)'S');
    JMenuItemDetail.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "read.gif"));
    JMenuView.add(JMenuItemDetail);
    JMenuOptions.setText("Options");
    JMenuOptions.setActionCommand("options");
    JMenuOptions.setMnemonic((int)'O');
    JMenuBarMain.add(JMenuOptions);
    JCheckBoxMenuItemFixedColumns.setText("Fixed Columns");
    JCheckBoxMenuItemFixedColumns.setActionCommand("fixed");
    JCheckBoxMenuItemFixedColumns.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F, Event.CTRL_MASK));
    JCheckBoxMenuItemFixedColumns.setMnemonic((int)'F');
    JCheckBoxMenuItemFixedColumns.setState( true);
    JMenuOptions.add(JCheckBoxMenuItemFixedColumns);
    JCheckBoxMenuItemLimitRows.setText("Limit Rows");
    JCheckBoxMenuItemLimitRows.setActionCommand("limit");
    JCheckBoxMenuItemLimitRows.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, Event.CTRL_MASK));
    JCheckBoxMenuItemLimitRows.setMnemonic((int)'R');
    JCheckBoxMenuItemLimitRows.setState( false);
    JMenuOptions.add(JCheckBoxMenuItemLimitRows);
    JCheckBoxMenuItemAutoCommit.setText("Auto Commit");
    JCheckBoxMenuItemAutoCommit.setActionCommand("autocommit");
    JCheckBoxMenuItemAutoCommit.setMnemonic((int)'A');
    JCheckBoxMenuItemAutoCommit.setState( true);
    JMenuOptions.add(JCheckBoxMenuItemAutoCommit);
    JCheckBoxMenuItemDebug.setText("Debug output");
    JCheckBoxMenuItemDebug.setActionCommand("debug");
    JCheckBoxMenuItemDebug.setMnemonic((int)'D');
    JCheckBoxMenuItemDebug.setState( false);
    JMenuOptions.add(JCheckBoxMenuItemDebug);
    JMenuBarMain.add(JMenuOptions);
    JMenuSQLList.setText("SQL List");
    JMenuSQLList.setActionCommand("sqllistmenu");
    JMenuSQLList.setMnemonic((int)'S');
    JMenuBarMain.add(JMenuSQLList);
    JMenuSQLList.setEnabled( false);
    JMenuItemSQLAdd.setText("Add SQL");
    JMenuItemSQLAdd.setActionCommand("addsql");
    JMenuItemSQLAdd.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q, Event.CTRL_MASK));
    JMenuItemSQLAdd.setMnemonic((int)'A');
    JMenuSQLList.add(JMenuItemSQLAdd);
    JMenuItemSQLEdit.setText("Edit SQL");
    JMenuItemSQLEdit.setActionCommand("editsql");
    JMenuItemSQLEdit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_I, Event.CTRL_MASK));
    JMenuItemSQLEdit.setMnemonic((int)'E');
    JMenuSQLList.add(JMenuItemSQLEdit);
    JMenuItemSQLDelete.setText("Del SQL");
    JMenuItemSQLDelete.setActionCommand("delsql");
    JMenuItemSQLDelete.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_D, Event.CTRL_MASK));
    JMenuItemSQLDelete.setMnemonic((int)'D');
    JMenuSQLList.add(JMenuItemSQLDelete);
    JMenuItemSQLUp.setText("SQL up");
    JMenuItemSQLUp.setActionCommand("upsql");
    JMenuItemSQLUp.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_UP, Event.CTRL_MASK));
    JMenuItemSQLUp.setMnemonic((int)'U');
    JMenuSQLList.add(JMenuItemSQLUp);
    JMenuItemSQLDown.setText("SQL down");
    JMenuItemSQLDown.setActionCommand("downsql");
    JMenuItemSQLDown.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, Event.CTRL_MASK));
    JMenuItemSQLDown.setMnemonic((int)'O');
    JMenuSQLList.add(JMenuItemSQLDown);
    JMenuItemSQLCrearHist.setText("Clear History");
    JMenuItemSQLCrearHist.setActionCommand("clearhist");
    JMenuItemSQLCrearHist.setMnemonic((int)'C');
    JMenuSQLList.add(JMenuItemSQLCrearHist);
    //JButtonSql.setText( ">>");
    //JButtonExec.setText( "Exec");
    //JButtonAdd.setText( "Add");
    //JButtonDel.setText( "Del");
    //JButtonHelp.setText( "Abo");
    jSplitPaneMain = new JSplitPane();
    jSplitPaneQuery = new JSplitPane( JSplitPane.VERTICAL_SPLIT);
    //QueryStatArea = new JSyntaxEditPanel( MAXUNDO);
    QueryStatArea = new SQLPanel( DbTtvDesktop.DBTTVDT, MAXUNDO);
    QueryScrollPane = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    QueryScrollPane.setViewportView( QueryStatArea);
    QueryScrollPane.setOpaque(true);
    ListScrollPane = new JScrollPane();
    HistListScrollPane = new JScrollPane();
    DbTablePanel = new TablePanel( DbTtvDesktop.DBTTVDT);
    DbTreePanel = new TreePanel( DbTtvDesktop.DBTTVDT);
    m_jTabbedPane = new JTabbedPane( JTabbedPane.BOTTOM);
    this.getContentPane().add("Center", jSplitPaneMain);
    //QueryStatArea.setTokenMarker( new TSQLTokenMarker());
    QueryStatArea.setToolTipText("Query Statement");
    jSplitPaneQuery.setTopComponent( QueryScrollPane);
    DbTablePanel.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    DbTablePanel.setQuery("");
    jSplitPaneQuery.setBottomComponent( DbTablePanel);
    SQLList.setModel( m_dba.getSqlListModel());
    SQLList.setToolTipText( "SQL Query");
    SQLList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
    ListScrollPane.setViewportView( SQLList);
    ListScrollPane.setOpaque(true);
    HistList.setModel( m_dba.getHistListModel());
    HistList.setToolTipText( "SQL Query");
    HistList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
    HistListScrollPane.setViewportView( HistList);
    HistListScrollPane.setOpaque(true);
    //jSplitPaneMain.setLeftComponent( DbTreePanel);
    m_jTabbedPane.addTab( "DB Tree", DbTreePanel);
    m_jTabbedPane.addTab( "Sql List", ListScrollPane);
    m_jTabbedPane.addTab( "History", HistListScrollPane);
    jSplitPaneMain.setLeftComponent( m_jTabbedPane);
    jSplitPaneMain.setRightComponent( jSplitPaneQuery);
    JToolBarMain.add(JButtonSql);
    JToolBarMain.addSeparator();
    JToolBarMain.add(JButtonExec);
    JToolBarMain.addSeparator();
    JToolBarMain.add(JButtonAdd);
    JToolBarMain.add(JButtonDel);
    JToolBarMain.addSeparator();
    JToolBarMain.add(JButtonCommit);
    JToolBarMain.addSeparator();
    JToolBarMain.add(JButtonRollback);
    JToolBarMain.addSeparator();
    JToolBarMain.add(JButtonExit);
    JToolBarMain.addSeparator();
    m_aniGifPanel = new AniGifPanel( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "alert.gif"));
    m_aniGifPanel.setPreferredSize( new Dimension( 18, 18));
    m_aniGifPanel.setMaximumSize( new Dimension( 18, 18));
    m_aniGifPanel.setToolTipText( "Transaction pending!");
    JToolBarMain.add( m_aniGifPanel);
    m_aniGifPanel.setVisible( false);
    this.getContentPane().add(JToolBarMain, BorderLayout.NORTH);
  }

  public boolean isConnected()
  {
    return m_blConnected;
  }

  public void setSqlPartVector( Vector vecSql)
  {
    m_vecSql = vecSql;
    eraseSqlPartMenu();
    initSqlPartMenu();
  }

  private void initSqlPartMenu()
  {
    int i;
    int iSize = m_vecSql.size();
    SqlClass sql;
    Vector vecSubMenus = new Vector();
    if( iSize <= 0)
      return;

    for( i = 0; i < iSize; i++)
    {
      sql = (SqlClass)m_vecSql.elementAt( i);
      JMenu jSubMenu = null;
      boolean blFound = false;

      JMenuItem JMenuItemSql = new JMenuItem();
      JMenuItemSql.setText( sql.getIdentifier() );
      JMenuItemSql.setActionCommand( sql.getIdentifier() );
      JMenuItemSql.addActionListener(m_lSymAction);
      if( sql.getSubMenu() != null && !sql.getSubMenu().equals( ""))
      {
        for( int ii = 0; ii < vecSubMenus.size(); ++ii)
        {
          JMenu jMenu = (JMenu)vecSubMenus.elementAt( ii);
          if( jMenu.getText().equals( sql.getSubMenu()))
          {
            blFound = true;
            jSubMenu = jMenu;
            break;
          }
        }
        if( !blFound)
        {
          jSubMenu = new JMenu();
          jSubMenu.setText( sql.getSubMenu() );
          jSubMenu.add( JMenuItemSql );
          jPopup.add( jSubMenu);
          m_vecSqlPartSubMenu.add( jSubMenu);
          vecSubMenus.add( jSubMenu);
        }
        else
        {
          jSubMenu.add( JMenuItemSql );
        }
      }
      else
      {
        jPopup.add( JMenuItemSql );
      }
      m_vecSqlPartMenu.add( JMenuItemSql);
    }
  }

  private void eraseSqlPartMenu()
  {
    int i;
    int iSize = m_vecSqlPartMenu.size();
    if( iSize <= 0)
      return;

    for( i = 0; i < iSize; i++)
    {
      JMenuItem JMenuItemSql = (JMenuItem)m_vecSqlPartMenu.elementAt( i);
      JMenuItemSql.removeActionListener(m_lSymAction);
      if( JMenuItemSql.getParent() != null)
        JMenuItemSql.getParent().remove( JMenuItemSql);
    }
    iSize = m_vecSqlPartSubMenu.size();
    if( iSize <= 0)
      return;

    for( i = 0; i < iSize; i++)
    {
      JMenu jMenu = (JMenu)m_vecSqlPartSubMenu.elementAt( i);
      jMenu.removeActionListener(m_lSymAction);
      jPopup.remove( jMenu);
    }
  }

  public DbTreeTableView( JDesktopFrame df, String sTitle, DBAlias dba, Vector vecSql, AppProperties appProps)
  {
    this( df, dba, vecSql, appProps);
    setTitle(sTitle);
  }

  public void setVisible(boolean b)
  {
    super.setVisible(b);
  }

  public void addNotify()
  {
    // Record the size of the window prior to calling parents addNotify.
    Dimension d = getSize();

    super.addNotify();

    if (fComponentsAdjusted)
      return;
    // Adjust components according to the insets
    setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
    Component components[] = getContentPane().getComponents();
    for (int i = 0; i < components.length; i++)
    {
      Point p = components[i].getLocation();
      p.translate(getInsets().left, getInsets().top);
      components[i].setLocation(p);
    }
    fComponentsAdjusted = true;
  }

  private class CheckThread
    extends Thread
  {
    public void run()
    {
      try
      {
        while( m_blConnected)
        {
          if( DbTablePanel.getAutoCommit())
          {
            Thread.sleep( 500);
          }
          else
          {
            checkTransactionStatus();
            Thread.sleep( 500);
          }
          focusSet();
        }
      }
      catch( InterruptedException iex)
      {
      }
    }
  }

  private class AdpInternalFrame
    extends InternalFrameAdapter
  {
    public void internalFrameClosing( InternalFrameEvent event)
    {
      Object object = event.getSource();
      if (object == DbTreeTableView.this)
      {
        onExit();
      }
    }
  }

  private class AdpFocus
    extends FocusAdapter
  {
    public void focusGained( FocusEvent event)
    {
      Object object = event.getSource();
      if (object == DbTreeTableView.this)
      {
        toFront();
      }
    }
  }

  private class SymAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "connect")
        connect_actionPerformed(event);
      else if (cmd == "fixed")
        fixed_actionPerformed(event);
      else if (cmd == "limit")
        limit_actionPerformed(event);
      else if (cmd == "autocommit")
        autocommit_actionPerformed(event);
      else if (cmd == "exec")
        exec_actionPerformed(event);
      else if (cmd == "batchexec")
        batchexec_actionPerformed(event);
      else if (cmd == "setsel")
        setsel_actionPerformed(event);
      else if (cmd == "add")
        add_actionPerformed(event);
      else if (cmd == "delete")
        delete_actionPerformed(event);
      else if (cmd == "tmpview")
        tmpview_actionPerformed(event);
      else if (cmd == "exit")
        onExit();
      else if (cmd == "load")
        load_actionPerformed(event, false);
      else if (cmd == "append")
        load_actionPerformed(event, true);
      else if (cmd == "undo")
        undo_actionPerformed(event);
      else if (cmd == "redo")
        redo_actionPerformed(event);
      else if (cmd == "cut")
        cut_actionPerformed(event);
      else if (cmd == "copy")
        copy_actionPerformed(event);
      else if (cmd == "paste")
        paste_actionPerformed(event);
      else if (cmd == "dbtree")
        dbtree_actionPerformed(event);
      else if (cmd == "sqllist")
        sqllist_actionPerformed(event);
      else if (cmd == "histlist")
        histlist_actionPerformed(event);
      else if (cmd == "addsql")
        addsql_actionPerformed(event);
      else if (cmd == "editsql")
        editsql_actionPerformed(event);
      else if (cmd == "delsql")
        delsql_actionPerformed(event);
      else if (cmd == "upsql")
        upsql_actionPerformed(event);
      else if (cmd == "downsql")
        downsql_actionPerformed(event);
      else if (cmd == "clearhist")
        clearhist_actionPerformed(event);
      else if (cmd == "commit")
        commit_actionPerformed(event);
      else if (cmd == "rollback")
        rollback_actionPerformed(event);
      else if (cmd == "ascorder" || cmd == "descorder")
        order_actionPerformed(event, cmd);
      else if (cmd == "print")
        print_actionPerformed(event);
      else if (cmd == "detail")
        detail_actionPerformed(event);
      else if (cmd == "dbinfo")
      {
        Component c = (Component)event.getSource();
        int x = (int)c.getBounds().getX();
        int y = (int)c.getBounds().getY();
        DbTreePanel.showPopup( x, y, false);
      }
      else
      {
        for( int i = 0; i < m_vecSql.size(); i++)
        {
          SqlClass sql = (SqlClass)m_vecSql.elementAt( i);
          if( cmd.equals( sql.getIdentifier()))
          {
            setSqlPart( sql);
            break;
          }
        }
      }
    }
  }

  private void limit_actionPerformed( ActionEvent event)
  {
    setLimitRows();
  }

  private void setLimitRows()
  {
    if( JCheckBoxMenuItemLimitRows.getState())
    {
      String tmp;
      int limit = 0;
      tmp = DbTablePanel.inputMsgDlg( "Rows limit?");
      if( tmp != null && !tmp.equals( ""))
      {
        try
        {
          limit = (new Integer( tmp)).intValue();
        }
        catch( NumberFormatException nex)
        {
          limit = 0;
        }
      }
      if( limit == 0)
        JCheckBoxMenuItemLimitRows.setState( false);
      DbTablePanel.setMaxRows( limit);
    }
    else
    {
      DbTablePanel.setMaxRows( 0);
    }
  }

  private void add_actionPerformed( ActionEvent event)
  {
    addRows( null);
  }

  private void commit_actionPerformed( ActionEvent event)
  {
    try
    {
      DbTablePanel.commit();
      GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT).println( "Commit executed!");
      //System.out.println( "Commit executed!");
    }
    catch( SQLException sqlex)
    {
      sqlex.printStackTrace();
      try
      {
        DbTablePanel.rollback();
      }
      catch( SQLException sqlrollex)
      {
        sqlrollex.printStackTrace();
      }
    }
    checkTransactionStatus();
  }

  private void rollback_actionPerformed( ActionEvent event)
  {
    try
    {
      DbTablePanel.rollback();
      GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT).println( "Rollback executed!");
      //System.out.println( "Rollback executed!");
    }
    catch( SQLException sqlex)
    {
      sqlex.printStackTrace();
    }
    checkTransactionStatus();
  }

  private void addRows( Vector data)
  {
    Vector vecValues = null;
    Vector newData;
    jDialogTable dlg = new jDialogTable( (Frame)null, "Add row", null);
    dlg.setFrame( this);

    if( data == null)
    {
      newData = new Vector();
      if( DbTablePanel.getSelectedRowCount() != 0)
      {
        int idx[] = DbTablePanel.getSelectedRows();
        for( int i = 0; i < DbTablePanel.getSelectedRowCount(); i++)
        {
          newData.addElement( DbTablePanel.getTableAdapter().getRow(idx[i]));
        }
      }
      else
        newData = null;
    }
    else
    {
      newData = data;
    }
    dlg.setModel( DbTablePanel.getTableAdapter(), newData);
    dlg.pack();
    dlg.setSize( new Dimension(dlg.getSize().width, 150));
    dlg.getTable().setAutoResizeMode( DbTablePanel.getAutoResizeMode());
    dlg.setVisible( true);

    vecValues = dlg.getValues();
    if( vecValues != null)
    {
      if( DbTreePanel.getTableName() == null)
      {
        DbTablePanel.setTableName( DbTablePanel.inputMsgDlg( "Insert tablenames related to your SELECT statement:"));
      }
      DbTablePanel.setEditable( false);
      for( int i = 0; i < vecValues.size(); i++)
        DbTablePanel.addRow( (Vector)vecValues.elementAt( i));
      DbTablePanel.setTableName( DbTreePanel.getTableName());
      DbTablePanel.setEditable( true);
    }
    dlg.dispose();
    repaint();
  }

  private void print_actionPerformed( ActionEvent event)
  {
    PrinterJob pj = PrinterJob.getPrinterJob();
    pj.setPrintable(DbTablePanel);
    pj.printDialog();
    try
    {
      setCursor(new Cursor( Cursor.WAIT_CURSOR));
      pj.print();
    }
    catch( PrinterException pex)
    {
      DbTablePanel.errorMsgDlg( pex.getMessage());
      pex.printStackTrace();
    }
    setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
  }

  private void delete_actionPerformed( ActionEvent event)
  {
    //DbTablePanel.removeRow( DbTablePanel.getSelectedRow());
    final int idx[] = DbTablePanel.getSelectedRows();

    if( DbTablePanel.getSelectedRowCount() > 1)
    {
      if( !DbTablePanel.questionMsgDlg( "Delete selected rows from DB?"))
        return;
    }

    final InternalCancelDlg cdlg = new InternalCancelDlg( this, DbTtvDesktop.DBTTVDT, "Executing ...", null);
    Runnable runner = new Runnable()
    {
      public void run()
      {
        for( int i = DbTablePanel.getSelectedRowCount()-1; i >= 0; i--)
        {
          DbTablePanel.removeRow( idx[i]);
        }
      }
    };
    setFixedColumns();
    repaint();
    cdlg.setRunner( runner);
    cdlg.start();
    if( cdlg.isCanceled())
      DbTablePanel.resetTableAdapter();
  }

  private void order_actionPerformed( ActionEvent event, String cmd)
  {
    int i = -1;
    String column = DbTablePanel.getSelColumnName();
    jPopup.setVisible( false);

    String strQuery = QueryStatArea.getText();

    if( (((i = strQuery.lastIndexOf( "\nORDER ")) != -1)
        || ((i = strQuery.lastIndexOf( " ORDER ")) != -1)
        || ((i = strQuery.lastIndexOf( "\norder ")) != -1)
        || ((i = strQuery.lastIndexOf( " order ")) != -1)
        || ((i = strQuery.lastIndexOf( "\nOrder ")) != -1)
        || ((i = strQuery.lastIndexOf( " Order ")) != -1)
        || ((i = strQuery.lastIndexOf( "\nordeR ")) != -1)
        || ((i = strQuery.lastIndexOf( " ordeR ")) != -1))
        && ((strQuery.lastIndexOf( " BY\n\t") != -1)
        || (strQuery.lastIndexOf( " BY\n") != -1)
        || (strQuery.lastIndexOf( " BY ") != -1)
        || (strQuery.lastIndexOf( " By\n\t") != -1)
        || (strQuery.lastIndexOf( " By\n") != -1)
        || (strQuery.lastIndexOf( " By ") != -1)
        || (strQuery.lastIndexOf( " bY\n\t") != -1)
        || (strQuery.lastIndexOf( " bY\n") != -1)
        || (strQuery.lastIndexOf( " bY ") != -1))
    )
    {
      strQuery = strQuery.substring( 0, i);
    }

    if( column != null)
    {
      strQuery += "\nORDER BY " + column;
      if( cmd == "descorder")
        strQuery += " DESC";
    }

    QueryStatArea.setText( strQuery);
    //QueryStatArea.updateScrollBars();
    if( DbTablePanel.getTableName() != null && !DbTablePanel.getTableName().equals(""))
      DbTablePanel.setEditable( false);
    exec( strQuery);
    DbTablePanel.setEditable( true);
  }

  private void setSqlPart( SqlClass sql)
  {
    String column = DbTablePanel.getSelColumnName();
    String tab = null;
    try
    {
      ResultSetMetaData rsma = DbTablePanel.getResultSet().getMetaData();
      tab = DbTablePanel.getSelectedColumnTableName();
    }
    catch( Exception ex){}
    if( tab == null || tab.equals( ""))
    {
      tab = DbTablePanel.getTableName();
      tab = tab.trim();
      int i = tab.lastIndexOf( '.');
      if( i != -1 && i+1 < tab.length())
        tab = tab.substring( i+1);
    }
    String schema = DbTablePanel.getSchemaName();

    jPopup.setVisible( false);

    if( sql.getSql() != null)
    {
      //QueryStatArea.setSelectedText( sql.getCompleteSql( column, tab, schema));
      QueryStatArea.replaceSelection( sql.getCompleteSql( column, tab, schema));
    }
    //QueryStatArea.updateScrollBars();
  }

  private void setsel_actionPerformed( ActionEvent event)
  {
    String strQuery = "";
    String strTable;
    boolean blExec = true;

    if( JRadioButtonMenuItemDBTree.isSelected())
    {
      strQuery = DbTreePanel.getSelectStatement();
      if( strQuery == null || strQuery.equals( ""))
      {
        JOptionPane.showMessageDialog( this, "From this point it is not possible to construct a statement!", "SQL-Execution",
                                       JOptionPane.ERROR_MESSAGE);
        return;
      }
      QueryStatArea.setText( strQuery);
      //QueryStatArea.updateScrollBars();
    }
    else
    {
      if( JRadioButtonMenuItemSQLList.isSelected())
        strQuery = m_dba.getSqlListModel().get( SQLList.getSelectedIndex()).toString();
      else
        strQuery = m_dba.getHistListModel().get( HistList.getSelectedIndex()).toString();

      QueryStatArea.setText( strQuery);
      blExec = !isMultipleStatement( strQuery);
      strQuery = parseVariables( strQuery, true);
      //QueryStatArea.updateScrollBars();
    }

    if( JRadioButtonMenuItemDBTree.isSelected())
      strTable = DbTreePanel.getTableName();
    else
    {
      if( JRadioButtonMenuItemSQLList.isSelected())
        strTable = ((ListElem)m_dba.getSqlListModel().get( SQLList.getSelectedIndex())).getTableNames();
      else
        strTable = ((ListElem)m_dba.getHistListModel().get( HistList.getSelectedIndex())).getTableNames();
    }
    DbTablePanel.setEditable( true);
    if( strTable != null && !strTable.equals(""))
      DbTablePanel.setEditable( false);
    if( blExec)
      exec( strQuery);
    DbTablePanel.setTableName( strTable);
    DbTablePanel.setEditable( true);
  }

  private boolean isMultipleStatement( String strQuery)
  {
    boolean blRet = false;
    int iIdx = -1;
    strQuery = strQuery.trim();
    int iLen = strQuery.length();

    if( ( iIdx = strQuery.indexOf( ';')) > -1)
    {
      if( iIdx+1 < iLen)
        blRet = true;
    }
    return blRet;
  }

  private String getCurrentSql()
  {
    String strQuery = QueryStatArea.getText();
    int iLen = strQuery.length();
    int iCarPos = QueryStatArea.getCaretPosition();
    int iEndPos = -1;
    if( iCarPos > 0 && (iCarPos == iLen || strQuery.charAt( iCarPos-1) == ';'))
      --iCarPos;
    int iStartPos = strQuery.substring( 0, iCarPos).lastIndexOf( ';');
    if( iCarPos+1 < iLen)
    {
      iEndPos = strQuery.substring( iCarPos+1).indexOf( ';');
      if( iEndPos > -1)
        iEndPos += iCarPos+1;
    }
    if( iStartPos > -1 && iEndPos > -1)
      strQuery = strQuery.substring( iStartPos+1, iEndPos);
    else if( iStartPos > -1 && iEndPos == -1)
      strQuery = strQuery.substring( iStartPos+1);
    else if( iStartPos == -1 && iEndPos > -1)
      strQuery = strQuery.substring( 0, iEndPos);

    return strQuery;
  }

  private String parseVariables( String strQuery, boolean blShow)
  {
    int i = 0;

    // parse -- comments
    try
    {
      StringReader sr = new StringReader( strQuery);
      BufferedReader br = new BufferedReader( sr);
      String strLine;
      strQuery = "";

      for( i = 0; ( strLine = br.readLine()) != null; ++i)
      {
        if( !strLine.startsWith( "--"))
        {
          if( i == 0)
            strQuery = strLine;
          else
            strQuery += "\n" + strLine;
        }
      }
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }

    // parse $n variables
    int iLen = strQuery.length();
    int iIdx = -1;
    int iOldIdx = 0;
    int iValIdx = -1;
    char c;
    String strVar = "";
    String strVal = null;
    Vector vecVar = new Vector();
    Vector vecVal = new Vector();
    String strNewQuery = "";
    i = 0;
    while( ( iIdx = strQuery.indexOf( '$', iOldIdx)) > -1)
    {
      strVar = "";
      if( iLen > iIdx)
      {
        for( i = 1; iIdx+i < iLen && (c = strQuery.charAt( iIdx+i)) != ' ' && c != '\n' && c != '\r' && c != '\t'; ++i)
        {
          strVar += c;
        }
        try
        {
          Integer.parseInt( strVar);
          strVar = "$" + strVar;
          if( ( iValIdx = vecVar.indexOf( strVar)) == -1)
          {
            vecVar.add( strVar);
            strVal = DbTablePanel.inputMsgDlg( "Value for variable " + strVar + ":");
            vecVal.add( strVal);
          }
          else
          {
            strVal = (String)vecVal.elementAt( iValIdx);
          }
          if( strVal != null)
          {
            strNewQuery += strQuery.substring( iOldIdx, iIdx) + strVal;
            iOldIdx = iIdx+strVal.length()+1;
          }
        }
        catch( NumberFormatException nfex)
        {
          if( strVar != null)
          {
            strVar = "$" + strVar;
            strNewQuery += strQuery.substring( iOldIdx, iIdx) + strVar;
            iOldIdx = iIdx+strVar.length()+1;
          }
          else
            iOldIdx = iIdx+1;
          continue;
        }
      }
    }
    if( iLen > iOldIdx)
      strNewQuery += strQuery.substring( iOldIdx);

    // parse ? replacements
    i = 0;
    String strtmp = "";
    StringTokenizer strtok = null;
    strtok = new StringTokenizer( strNewQuery, "?", true);
    strNewQuery = "";

    while( strtok.hasMoreTokens())
    {
      strtmp = strtok.nextToken();
      if( strtmp.indexOf( "?") == -1)
      {
        strNewQuery += strtmp;
        if( blShow)
          QueryStatArea.setText( strNewQuery);
      }
      else
      {
        strtmp = DbTablePanel.inputMsgDlg( new Integer( ++i).toString() + ". parameter:");
        if( strtmp != null)
          strNewQuery += strtmp;
        if( blShow)
          QueryStatArea.setText( strNewQuery);
      }
    }
    return strNewQuery;
  }

  private void dbtree_actionPerformed( ActionEvent event)
  {
    JMenuItemSQLAdd.setEnabled( true);
    JMenuItemSQLEdit.setEnabled( true);
    JMenuItemSQLCrearHist.setEnabled( false);
    JMenuSQLList.setEnabled( false);
    JRadioButtonMenuItemDBTree.setSelected( true);
    m_jTabbedPane.setSelectedComponent( DbTreePanel);
    /*
    int loc =  jSplitPaneMain.getDividerLocation();
    jSplitPaneMain.setLeftComponent( DbTreePanel);
    jSplitPaneMain.setDividerLocation( loc);
    */
  }

  private void sqllist_actionPerformed( ActionEvent event)
  {
    JMenuItemSQLAdd.setEnabled( true);
    JMenuItemSQLEdit.setEnabled( true);
    JMenuItemSQLCrearHist.setEnabled( false);
    JMenuSQLList.setEnabled( true);
    JRadioButtonMenuItemSQLList.setSelected( true);
    m_jTabbedPane.setSelectedComponent( ListScrollPane);
    /*
    int loc =  jSplitPaneMain.getDividerLocation();
    jSplitPaneMain.setLeftComponent( ListScrollPane);
    jSplitPaneMain.setDividerLocation( loc);
    */
  }

  private void histlist_actionPerformed( ActionEvent event)
  {
    JMenuItemSQLAdd.setEnabled( false);
    JMenuItemSQLEdit.setEnabled( true);
    JMenuItemSQLCrearHist.setEnabled( true);
    JMenuSQLList.setEnabled( true);
    JRadioButtonMenuItemHistList.setSelected( true);
    m_jTabbedPane.setSelectedComponent( HistListScrollPane);
    /*
    int loc =  jSplitPaneMain.getDividerLocation();
    jSplitPaneMain.setLeftComponent( HistListScrollPane);
    jSplitPaneMain.setDividerLocation( loc);;
    */
  }

  private void addSqlHist()
  {
    ListElem sqlStat = new ListElem();
    sqlStat.setSqlStatement( QueryStatArea.getText());
    if( sqlStat != null && !sqlStat.equals("") && !m_dba.getHistListModel().contains(sqlStat))
    {
      sqlStat.setTableNames( DbTablePanel.getTableName());

      if( m_dba.getHistListModel().size() > MAXHIST)
        m_dba.getHistListModel().remove( m_dba.getHistListModel().size()-1);

      m_dba.getHistListModel().add(0, sqlStat);
    }
  }

  private void addsql_actionPerformed( ActionEvent event)
  {
    ListElem sqlStat = new ListElem();
    sqlStat.setSqlStatement( QueryStatArea.getText());
    if( sqlStat != null && !sqlStat.equals("") && !m_dba.getSqlListModel().contains(sqlStat))
    {
      InputDlg dialog = new InputDlg( (Frame)null, "Input!", "Tablename(s) for the SQL statement?", DbTablePanel.getTableName());
      dialog.setFrame( this);

      String tables = dialog.getValue();
      dialog.dispose();
      sqlStat.setTableNames( tables);
      m_dba.getSqlListModel().add(0, sqlStat);
    }
  }

  private void editsql_actionPerformed( ActionEvent event)
  {
    if( JRadioButtonMenuItemSQLList.isSelected())
    {
      int idx = SQLList.getSelectedIndex();
      if( idx < 0)
        return;
      ListElem sqlStat = (ListElem)m_dba.getSqlListModel().get( idx);
      if( sqlStat != null && !sqlStat.equals(""))
      {
        SqlStatEditDlg dlg = new SqlStatEditDlg( (Frame)null, "Edit SQL statement", true);
        dlg.setFrame( this);
        dlg.setListElem( sqlStat);
        dlg.setVisible(true);
        sqlStat = dlg.getListElem();
        m_dba.getSqlListModel().set( idx, sqlStat);
      }
    }
    else
    {
      int idx = HistList.getSelectedIndex();
      if( idx < 0)
        return;
      ListElem sqlStat = (ListElem)m_dba.getHistListModel().get( idx);
      if( sqlStat != null && !sqlStat.equals(""))
      {
        SqlStatEditDlg dlg = new SqlStatEditDlg( (Frame)null, "Edit SQL statement", true);
        dlg.setFrame( this);
        dlg.setListElem( sqlStat);
        dlg.setVisible(true);
        sqlStat = dlg.getListElem();
        m_dba.getHistListModel().set( idx, sqlStat);
      }
    }
  }

  private void delsql_actionPerformed( ActionEvent event)
  {
    if( JRadioButtonMenuItemSQLList.isSelected())
      m_dba.getSqlListModel().removeElementAt( SQLList.getSelectedIndex());
    else
      m_dba.getHistListModel().removeElementAt( HistList.getSelectedIndex());
  }

  private void upsql_actionPerformed( ActionEvent event)
  {
    int iSize = -1;
    int iSel = -1;
    ListElem sqlStat = null;
    ListElem sqlStatEx = null;
    if( JRadioButtonMenuItemSQLList.isSelected())
    {
      iSize = m_dba.getSqlListModel().size();
      iSel = SQLList.getSelectedIndex();
      if( iSel > 0 && iSel < iSize)
      {
        sqlStat = (ListElem)m_dba.getSqlListModel().get( iSel);
        sqlStatEx = (ListElem)m_dba.getSqlListModel().get( iSel-1);
        m_dba.getSqlListModel().setElementAt( sqlStat, iSel-1);
        m_dba.getSqlListModel().setElementAt( sqlStatEx, iSel);
        SQLList.setSelectedIndex( iSel-1);
      }
    }
    else
    {
      iSize = m_dba.getHistListModel().size();
      iSel = HistList.getSelectedIndex();
      if( iSel > 0 && iSel < iSize)
      {
        sqlStat = (ListElem)m_dba.getHistListModel().get( iSel);
        sqlStatEx = (ListElem)m_dba.getHistListModel().get( iSel-1);
        m_dba.getHistListModel().setElementAt( sqlStat, iSel-1);
        m_dba.getHistListModel().setElementAt( sqlStatEx, iSel);
        HistList.setSelectedIndex( iSel-1);
      }
    }
  }

  private void downsql_actionPerformed( ActionEvent event)
  {
    int iSize = -1;
    int iSel = -1;
    ListElem sqlStat = null;
    ListElem sqlStatEx = null;
    if( JRadioButtonMenuItemSQLList.isSelected())
    {
      iSize = m_dba.getSqlListModel().size();
      iSel = SQLList.getSelectedIndex();
      if( iSel > -1 && iSel < iSize-1)
      {
        sqlStat = (ListElem)m_dba.getSqlListModel().get( iSel);
        sqlStatEx = (ListElem)m_dba.getSqlListModel().get( iSel+1);
        m_dba.getSqlListModel().setElementAt( sqlStat, iSel+1);
        m_dba.getSqlListModel().setElementAt( sqlStatEx, iSel);
        SQLList.setSelectedIndex( iSel+1);
      }
    }
    else
    {
      iSize = m_dba.getHistListModel().size();
      iSel = HistList.getSelectedIndex();
      if( iSel > -1 && iSel < iSize-1)
      {
        sqlStat = (ListElem)m_dba.getHistListModel().get( iSel);
        sqlStatEx = (ListElem)m_dba.getHistListModel().get( iSel+1);
        m_dba.getHistListModel().setElementAt( sqlStat, iSel+1);
        m_dba.getHistListModel().setElementAt( sqlStatEx, iSel);
        HistList.setSelectedIndex( iSel+1);
      }
    }
  }

  private void clearhist_actionPerformed( ActionEvent event)
  {
    if( DbTablePanel.questionMsgDlg( "Clear complete history?"))
      m_dba.getHistListModel().removeAllElements();
  }

  private boolean connect_actionPerformed( ActionEvent event)
  {
    dbtree_actionPerformed( null);
    JRadioButtonMenuItemDBTree.setSelected( true);
    JRadioButtonMenuItemSQLList.setSelected( false);
    JRadioButtonMenuItemHistList.setSelected( false);
    if( m_dba != null)
    {
      DbTreePanel.Close();
      DbTreePanel.Reset();
      DbTreePanel.clearTree();
      DbTablePanel.close();
      //DbTablePanel.Reset();
      m_strDriver = m_dba.getDBDriverClass();
      m_strUrl = m_dba.getFullDBUrl();
      m_strUsr = m_dba.getDBUser();
      m_strTypes = new String[1];
      m_strTypes[0] = "TABLE";
      DbTablePanel.setDriver( m_strDriver);
      DbTablePanel.setUrl( m_strUrl);
      DbTreePanel.setDriver( m_strDriver);
      DbTreePanel.setUrl( m_strUrl);
      DbTreePanel.setTypes( m_strTypes);
      if( m_strPwd == null || m_strPwd.equals( ""))
      {
        DbTreePanel.setUsr( m_strUsr);
        //DbTreePanel.LoginDlg();
        ConnectDialog dlgConn = new ConnectDialog( this, m_dba);
        dlgConn.setVisible( true);
        if( dlgConn.isCancelled())
        {
          onExit();
          return false;
        }
        m_strUsr = dlgConn.getUser();
        m_strPwd = dlgConn.getPassword();
      }
      DbTablePanel.setUsr( m_strUsr);
      DbTablePanel.setPwd( m_strPwd);
      DbTreePanel.setUsr( m_strUsr);
      DbTreePanel.setPwd( m_strPwd);
      if( !DbTreePanel.Connect())
      {
        onExit();
        return false;
      }
      DbTablePanel.connect();
      QueryStatArea.activateSQLStatementAid( m_df, this, m_strDriver, m_strUrl, m_strUsr, m_strPwd, DbTablePanel.getSchemaName());
      setTitle( m_dba.getAliasName() + " as " + DbTreePanel.getUsr());
      return true;
    }
    return false;
  }

  private void fixed_actionPerformed( ActionEvent event)
  {
    setFixedColumns();
  }

  private void setFixedColumns()
  {
    if( JCheckBoxMenuItemFixedColumns.getState())
    {
      DbTablePanel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      //DbTablePanel.setCalcColMinWidth();
      DbTablePanel.sizeColumnsToFit();
    }
    else
    {
      DbTablePanel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      //DbTablePanel.setColMinWidth(10);
      DbTablePanel.sizeColumnsToFit();
    }
  }

  private void autocommit_actionPerformed( ActionEvent event)
  {
    DbTablePanel.setAutoCommit( JCheckBoxMenuItemAutoCommit.getState());
    JMenuItemCommit.setEnabled( !JCheckBoxMenuItemAutoCommit.getState());
    JMenuItemRollback.setEnabled( !JCheckBoxMenuItemAutoCommit.getState());
    JButtonCommit.setEnabled( !JCheckBoxMenuItemAutoCommit.getState());
    JButtonRollback.setEnabled( !JCheckBoxMenuItemAutoCommit.getState());
  }

  private void exec_actionPerformed( ActionEvent event)
  {
    String strTableName = null;
    //String strSql = QueryStatArea.getText();
    String strSql = getCurrentSql();
    int iIdx = checkWholeWord( "SELECT", strSql);
    if( ( iIdx) != -1 && iIdx < 5)
    {
      int iIdxEnd = -1;
      iIdx = checkWholeWord( "FROM", strSql);
      if( iIdx != -1)
      {
        iIdxEnd = checkWholeWord( "WHERE", strSql);
        if( iIdxEnd == -1)
          iIdxEnd = checkWholeWord( "ORDER", strSql);
        if( iIdxEnd == -1)
          iIdxEnd = checkWholeWord( "GROUP", strSql);
        if( iIdxEnd == -1)
          iIdxEnd = checkWholeWord( "HAVING", strSql);
        if( iIdxEnd == -1)
          strTableName = strSql.substring( iIdx+5);
        else
          strTableName = strSql.substring( iIdx+5, iIdxEnd-1);
        if( strTableName != null)
        {
          DbTablePanel.setTableName( strTableName);
          DbTablePanel.setEditable( false);
        }
        else
          DbTablePanel.setEditable( true);
      }
    }
    strSql = parseVariables( strSql, false);
    exec( strSql);
    if( strTableName != null)
    {
      DbTablePanel.setTableName( strTableName);
      DbTablePanel.setEditable( true);
    }
    addSqlHist();
  }

  private int checkWholeWord( String strWord, String strQuery)
  {
    strWord = strWord.toUpperCase();
    int iIdx = strQuery.toUpperCase().indexOf( strWord);
    char c1 = ' ';
    if( iIdx > 0)
    {
      c1 = strQuery.charAt( iIdx - 1 );
    }
    char c2 = ' ';
    if( iIdx + strWord.length() < strQuery.length())
    {
      c2 = strQuery.charAt( iIdx + strWord.length() );
    }
    if( ( iIdx > 0 && c1 != ' ' && c1 != '\t' && c1 != '\n' )
        || ( iIdx + strWord.length() < strQuery.length() && c2 != ' ' && c2 != '\t' && c2 != '\n' ) )
    {
      iIdx = -1;
    }
    return iIdx;
  }

  private void batchexec_actionPerformed( ActionEvent event)
  {
    if( DbTablePanel.getEditingColumn() != -1)
    {
      DbTablePanel.getTable().getCellEditor(DbTablePanel.getEditingRow(), DbTablePanel.getEditingColumn()).cancelCellEditing();
    }
    String strTableName = null;
    String strSql = QueryStatArea.getText();
    int iIdx = -1;
    strSql = parseVariables( strSql, false);
    DbTablePanel.setDbg( JCheckBoxMenuItemDebug.getState());
    final String strQuery = strSql;
    final InternalCancelDlg cdlg = new InternalCancelDlg( this, DbTtvDesktop.DBTTVDT, "Executing batch ...", null);
    Runnable runner = new Runnable()
    {
      public void run()
      {
        DbTablePanel.queryBatch( strQuery);
      }
    };
    cdlg.setRunner( runner);
    cdlg.start();
    checkTransactionStatus();
    //if( cdlg.isCanceled())
      //DbTablePanel.resetTableAdapter();
    addSqlHist();
  }

  private void exec( String strQuery)
  {
    if( DbTablePanel.getEditingColumn() != -1)
    {
      DbTablePanel.getTable().getCellEditor(DbTablePanel.getEditingRow(), DbTablePanel.getEditingColumn()).cancelCellEditing();
    }
    DbTablePanel.setQuery( strQuery);
    DbTablePanel.setDbg( JCheckBoxMenuItemDebug.getState());
    final InternalCancelDlg cdlg = new InternalCancelDlg( this, DbTtvDesktop.DBTTVDT, "Executing ...", null);
    Runnable runner = new Runnable()
    {
      public void run()
      {
        DbTablePanel.query();
      }
    };
    setFixedColumns();
    repaint();
    cdlg.setRunner( runner);
    cdlg.start();
    if( cdlg.isCanceled())
      DbTablePanel.resetTableAdapter();
    checkTransactionStatus();
  }

  private void checkTransactionStatus()
  {
    boolean blTransPend = DbTablePanel.isTransactionPending();
    if( blTransPend != m_blOldTransPend)
    {
      if( blTransPend)
      {
        m_aniGifPanel.setVisible( true);
      }
      else
      {
        m_aniGifPanel.setVisible( false);
      }
    }
    m_blOldTransPend = blTransPend;
  }

  private void tmpview_actionPerformed( ActionEvent event)
  {
    DbTreePanel.addTmpView();
  }

  private void load_actionPerformed( ActionEvent event, boolean blAppend)
  {
    String strFile = null;
    JFileChooser chooser = new JFileChooser();
    String strExt[] = new String[1];
    strExt[0] = DBAlias.SQLFILEEXT;
    chooser.setFileFilter( new MultiFileFilter( strExt));
    int returnVal = chooser.showOpenDialog( getParent());
    if(returnVal == JFileChooser.APPROVE_OPTION)
    {
      if( chooser.getSelectedFile() != null && chooser.getSelectedFile().getAbsolutePath() != null)
      {
        strFile = chooser.getSelectedFile().getAbsolutePath();
        m_dba.readSqlListElems( strFile, blAppend);
      }
    }
  }

  private void focusSet()
  {
    if( QueryStatArea.hasFocus() )
      m_coFocus = QueryStatArea;
    else if( DbTablePanel.hasFocus())
      m_coFocus = DbTablePanel;
    else if( DbTreePanel.hasFocus())
      m_coFocus = DbTreePanel;
  }

  private void detail_actionPerformed( ActionEvent event)
  {
    RecordDetailDialog rdd = new RecordDetailDialog( this, "Record details", true, m_appProps, DbTablePanel);
    rdd.setVisible( true);
    rdd.dispose();
  }

  private void undo_actionPerformed( ActionEvent event)
  {
    if( QueryStatArea.hasFocus() || m_coFocus == QueryStatArea)
    {
      QueryStatArea.undo();
    }
  }

  private void redo_actionPerformed( ActionEvent event)
  {
    if( QueryStatArea.hasFocus() || m_coFocus == QueryStatArea)
    {
      QueryStatArea.redo();
    }
  }

  private void cut_actionPerformed( ActionEvent event)
  {
    if( QueryStatArea.hasFocus() || m_coFocus == QueryStatArea)
    {
      QueryStatArea.cut();
    }
    else if( DbTablePanel.hasFocus() || m_coFocus == DbTablePanel)
    {
      rowCopy();
      delete_actionPerformed(null);
    }
  }

  private void copy_actionPerformed( ActionEvent event)
  {
    if( QueryStatArea.hasFocus() || m_coFocus == QueryStatArea)
    {
      QueryStatArea.copy();
    }
    else if( DbTablePanel.hasFocus() || m_coFocus == DbTablePanel)
    {
      rowCopy();
    }
  }

  private void rowCopy()
  {
    if( DbTablePanel.getSelectedRowCount() > 0)
    {
      Clipboard clipboard = getToolkit().getSystemClipboard();
      StringBuffer buf = new StringBuffer();

      int idx[] = DbTablePanel.getSelectedRows();
      for( int i = 0; i < DbTablePanel.getSelectedRowCount(); i++)
      {
        buf.append( DbTablePanel.toString( idx[i]) + "\n");
      }
      clipboard.setContents( new StringSelection(buf.toString()), null);
    }
  }

  private void paste_actionPerformed( ActionEvent event)
  {
    if( QueryStatArea.hasFocus() || m_coFocus == QueryStatArea)
    {
      QueryStatArea.paste();
    }
    else if( DbTablePanel.hasFocus() || m_coFocus == DbTablePanel)
    {
      Clipboard clipboard = getToolkit().getSystemClipboard();

      try
      {
        Transferable tr = clipboard.getContents( null);
        String str = (String)tr.getTransferData( DataFlavor.stringFlavor);
        Vector vecRows = new Vector();

        if( str != null)
        {
          String strRow;
          StringTokenizer stokRows = new StringTokenizer( str, "\n", false);

          while( stokRows.hasMoreTokens())
          {
            Vector vecCols = new Vector();
            strRow = stokRows.nextToken();
            if( strRow != null)
            {
              StringTokenizer stokCols = new StringTokenizer( strRow, "\t", true);
              String strPrev = null;
              while( stokCols.hasMoreTokens())
              {
                String strCol = stokCols.nextToken();
                if( strCol != null && !strCol.equals( "\t"))
                {
                  if( vecCols.size() < DbTablePanel.getColumnCount())
                    vecCols.add( strCol);
                }
                else if( strCol != null && strCol.equals( "\t") && strPrev != null && strPrev.equals( "\t"))
                {
                  if( vecCols.size() < DbTablePanel.getColumnCount())
                    vecCols.add( null);
                }
                strPrev = strCol;
              }
              if( vecCols.size() < DbTablePanel.getColumnCount())
              {
                int iStart = 0;
                int iEnd = DbTablePanel.getColumnCount();

                for( iStart = vecCols.size()-1; iStart < iEnd; iStart++)
                {
                  vecCols.add( null);
                }
              }
              vecRows.add( vecCols);
            }
          }
        }
        if( vecRows.size() > 0)
        {
          addRows( vecRows);
        }
      }
      catch( IOException ioex)
      {
        ioex.printStackTrace();
      }
      catch( UnsupportedFlavorException ufex)
      {
        ufex.printStackTrace();
      }
    }
  }

  private class SymComponent extends ComponentAdapter
  {
    public void componentResized( ComponentEvent event)
    {
      Object object = event.getSource();
      if (object == DbTreeTableView.this)
        DbTreeTableView_componentResized();
    }

    public void componentMoved( ComponentEvent event)
    {
      Object object = event.getSource();
      if (object == DbTreeTableView.this)
        ;
    }
  }

  public void adjustComponent()
  {
    jSplitPaneQuery.setDividerLocation( 0.2);
    jSplitPaneMain.setDividerLocation( 0.25);
  }

  private void DbTreeTableView_componentResized()
  {
    jSplitPaneQuery.setDividerLocation( 0.2);
  }

  public void onExit()
  {
    if( m_blConnected && !JCheckBoxMenuItemAutoCommit.getState() && DbTablePanel.isTransactionPending())
    {
      int iRet = JOptionPane.showConfirmDialog( this, "Commit open DB-transaction?", "Confirmation for " + getTitle(),
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if( iRet == JOptionPane.YES_OPTION)
        commit_actionPerformed( null);
      else
        rollback_actionPerformed( null);
    }
    if( DbTreePanel.getUsr() != null)
    {
      m_dba.saveSqlListElems();
      m_dba.saveHistListElems();
    }
    DbTablePanel.close();
    DbTreePanel.Close();
    QueryStatArea.closeSQLStatementAid();
    jPopup.setVisible( false);
    super.onExit();
  }
}
