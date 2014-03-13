package haui.app.dbtreetableview;

import haui.components.AboutDialog;
import haui.components.HelpDialog;
import haui.components.SplashWindow;
import haui.components.desktop.ExDesktopManager;
import haui.components.desktop.JDesktopFrame;
import haui.resource.ResouceManager;
import haui.util.AppProperties;
import haui.util.ConfigPathUtil;
import haui.util.GlobalAppProperties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Module:      DbTtvDesktop.java<br>
 *              $Source: $
 *<p>
 * Description: Desktop for the DbTreeTableView.<br>
 *</p><p>
 * Created:     19.09.2002 by AE
 *</p><p>
 * @history     19.09.2002 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2002; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since	      JDK1.2
 *</p>
 */
public class DbTtvDesktop
  extends JDesktopFrame
{
  // Constants
  public static final String VERSION = "1.0";
  public static final String DBTTVDT = "DbTreeTableView";
  public static final String PROPSFILE = "dbttvdt.ppr";
  public static final String PROPSHEADER = "DbTreeTableView application properties";
  public static final String DBURL = "DBUrl";
  public static final String SQLPART = "SqlPart";

  // Default properties constants
  public static final String AUTOCOMMIT = "AutoCommit";
  public static final String FIXCOLUMNS = "FixColumns";
  public static final String SQLDEBUG = "SqlDebug";
  public static final String ROWLIMIT = "RowLimit";

  // member variables
  AppAction m_appAction = new AppAction();
  Vector m_vecUrl = new Vector();
  Vector m_vecDrv = new Vector();
  private Vector m_vecSql = new Vector();

  // GUI member variables
  AboutDialog m_aboutDlg;
  HelpDialog m_helpViewer;
  DBDriverFrame m_dbd;
  DBAliasFrame m_dba;
  BorderLayout m_borderLayoutMain;
  JScrollPane m_jScrollPaneDp;
  JMenuBar m_jMenuBarMain;
  JMenu m_jMenuFile;
  JMenuItem m_jMenuItemExit;
  JSeparator m_jSeparatorExit;
  JMenu m_jMenuDBDriver;
  JMenuItem m_jMenuItemDBDNew;
  JMenuItem m_jMenuItemDBDEdit;
  JMenuItem m_jMenuItemDBDCopy;
  JMenuItem m_jMenuItemDBDDelete;
  JMenu m_jMenuDBAlias;
  JMenuItem m_jMenuItemDBAConnect;
  JMenuItem m_jMenuItemDBANew;
  JMenuItem m_jMenuItemDBAEdit;
  JMenuItem m_jMenuItemDBACopy;
  JMenuItem m_jMenuItemDBADelete;
  JMenu m_jMenuOption;
  JMenuItem m_jMenuItemProperties;
  JMenuItem m_jMenuItemConstrEdit;
  JMenu m_jMenuWindow;
  JMenuItem m_jMenuItemWDBD;
  JMenuItem m_jMenuItemWDBA;
  JMenu m_jMenuHelp;
  JMenuItem m_jMenuItemHelp;
  JMenuItem m_jMenuItemAbout;
  JToolBar m_jToolBarMain;
  JButton m_jButtonProperties;
  JButton m_jButtonExit;

  public DbTtvDesktop()
  {
    super( DBTTVDT, DBTTVDT);
    ConfigPathUtil.init( DBTTVDT);
    APPICON = DBTTVDT + ".gif";
    SplashWindow sd = null;
    setIconImage( (new ImageIcon( getClass().getResource(APPICON))).getImage());
    try
    {
      sd = new SplashWindow( DBTTVDT, "Database Tree View with editable Database Table View"
                             , "Version " + VERSION + " by A.Eisenhauer"
                             , new ImageIcon( this.getClass().getResource( "Splash.gif")));
      sd.setVisible( true);
    }
    catch( Exception ex)
    {
      sd = null;
    }

    readProperties();

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    initSqlParts();
    createToolFrames();
    m_jToolBarMain.add( m_dba.getAliasComboBox());
    m_jToolBarMain.addSeparator();
    createToolbarEntries( m_appAction, m_jToolBarMain);
    m_jToolBarMain.add(m_jButtonProperties, null);
    m_jToolBarMain.addSeparator();
    m_jToolBarMain.add(m_jButtonExit, null);
    //createFrame( m_dp);

    AdpMenu adpMenu = new AdpMenu();
    m_jMenuWindow.addMenuListener( adpMenu);

    // add action handler
    m_jMenuItemExit.addActionListener( m_appAction);
    m_jMenuItemProperties.addActionListener( m_appAction);
    m_jMenuItemConstrEdit.addActionListener( m_appAction);
    m_jMenuItemDBDNew.addActionListener( m_appAction);
    m_jMenuItemDBDEdit.addActionListener( m_appAction);
    m_jMenuItemDBDCopy.addActionListener( m_appAction);
    m_jMenuItemDBDDelete.addActionListener( m_appAction);
    m_jMenuItemDBAConnect.addActionListener( m_appAction);
    m_jMenuItemDBANew.addActionListener( m_appAction);
    m_jMenuItemDBAEdit.addActionListener( m_appAction);
    m_jMenuItemDBACopy.addActionListener( m_appAction);
    m_jMenuItemDBADelete.addActionListener( m_appAction);
    m_jMenuItemWDBD.addActionListener( m_appAction);
    m_jMenuItemWDBA.addActionListener( m_appAction);
    m_jMenuItemHelp.addActionListener( m_appAction);
    m_jMenuItemAbout.addActionListener( m_appAction);
    m_jButtonProperties.addActionListener( m_appAction);
    m_jButtonExit.addActionListener( m_appAction);

    sd.close();
  }

  //Initialisierung der Komponente
  private void jbInit()
    throws Exception
  {
    m_dp = new JDesktopPane();
    m_dpManager = new ExDesktopManager( m_dp);
    m_dp.setBackground(Color.black);
    m_dp.setDesktopManager( m_dpManager);
    m_borderLayoutMain = new BorderLayout();
    m_jMenuBarMain = new JMenuBar();
    m_jMenuFile = new JMenu();
    m_jSeparatorExit = new JSeparator();
    m_jMenuItemExit = new JMenuItem();
    m_jMenuDBDriver = new JMenu();
    m_jMenuItemDBDNew = new JMenuItem();
    m_jMenuItemDBDEdit = new JMenuItem();
    m_jMenuItemDBDCopy = new JMenuItem();
    m_jMenuItemDBDDelete = new JMenuItem();
    m_jMenuDBAlias = new JMenu();
    m_jMenuItemDBAConnect = new JMenuItem();
    m_jMenuItemDBANew = new JMenuItem();
    m_jMenuItemDBAEdit = new JMenuItem();
    m_jMenuItemDBACopy = new JMenuItem();
    m_jMenuItemDBADelete = new JMenuItem();
    m_jMenuOption = new JMenu();
    m_jMenuItemProperties = new JMenuItem();
    m_jMenuItemConstrEdit = new JMenuItem();
    m_jMenuWindow = new JMenu();
    m_jMenuItemWDBD = new JMenuItem();
    m_jMenuItemWDBA = new JMenuItem();
    m_jMenuHelp = new JMenu();
    m_jMenuItemHelp = new JMenuItem();
    m_jMenuItemAbout = new JMenuItem();
    m_jButtonProperties = new JButton();
    m_jButtonExit = new JButton();
    m_jToolBarMain = new JToolBar();
    m_jScrollPaneDp = new JScrollPane( m_dp,
      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    getContentPane().setLayout( m_borderLayoutMain);
    getContentPane().add( m_jScrollPaneDp, BorderLayout.CENTER);
    setJMenuBar( m_jMenuBarMain);
    getContentPane().add( m_jToolBarMain, BorderLayout.NORTH);
    m_jMenuFile.setText( "File");
    m_jMenuFile.setActionCommand( "file");
    m_jMenuFile.setMnemonic( (int)'F');
    m_jMenuBarMain.add( m_jMenuFile);
    createFileMenuEntries( m_appAction, m_jMenuFile);
    m_jMenuFile.add( m_jSeparatorExit);
    m_jMenuItemExit.setText( "Exit");
    m_jMenuItemExit.setActionCommand( "exit");
    m_jMenuItemExit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, Event.ALT_MASK));
    m_jMenuItemExit.setMnemonic( (int)'x');
    m_jMenuItemExit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "exit16.gif"));
    m_jMenuFile.add( m_jMenuItemExit);
    m_jMenuDBDriver.setText( "DBDriver");
    m_jMenuDBDriver.setActionCommand( "dbdriver");
    m_jMenuDBDriver.setMnemonic( (int)'D');
    m_jMenuBarMain.add( m_jMenuDBDriver);
    m_jMenuItemDBDNew.setText( "Add");
    m_jMenuItemDBDNew.setActionCommand( "dbdnew");
    m_jMenuItemDBDNew.setMnemonic( (int)'A');
    m_jMenuItemDBDNew.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Add16.gif"));
    m_jMenuDBDriver.add( m_jMenuItemDBDNew);
    m_jMenuItemDBDEdit.setText( "Edit");
    m_jMenuItemDBDEdit.setActionCommand( "dbdedit");
    m_jMenuItemDBDEdit.setMnemonic( (int)'E');
    m_jMenuItemDBDEdit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Edit16.gif"));
    m_jMenuDBDriver.add( m_jMenuItemDBDEdit);
    m_jMenuItemDBDCopy.setText( "Copy");
    m_jMenuItemDBDCopy.setActionCommand( "dbdcopy");
    m_jMenuItemDBDCopy.setMnemonic( (int)'C');
    m_jMenuItemDBDCopy.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Copy16.gif"));
    m_jMenuDBDriver.add( m_jMenuItemDBDCopy);
    m_jMenuItemDBDDelete.setText( "Delete");
    m_jMenuItemDBDDelete.setActionCommand( "dbddelete");
    m_jMenuItemDBDDelete.setMnemonic( (int)'D');
    m_jMenuItemDBDDelete.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Delete16.gif"));
    m_jMenuDBDriver.add( m_jMenuItemDBDDelete);
    m_jMenuDBAlias.setText( "DBAlias");
    m_jMenuDBAlias.setActionCommand( "dbalias");
    m_jMenuDBAlias.setMnemonic( (int)'A');
    m_jMenuBarMain.add( m_jMenuDBAlias);
    m_jMenuItemDBAConnect.setText( "Connect");
    m_jMenuItemDBAConnect.setActionCommand( "dbaconnect");
    m_jMenuItemDBAConnect.setMnemonic( (int)'C');
    m_jMenuItemDBAConnect.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "connect16.gif"));
    m_jMenuDBAlias.add( m_jMenuItemDBAConnect);
    m_jMenuItemDBANew.setText( "Add");
    m_jMenuItemDBANew.setActionCommand( "dbanew");
    m_jMenuItemDBANew.setMnemonic( (int)'A');
    m_jMenuItemDBANew.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Add16.gif"));
    m_jMenuDBAlias.add( m_jMenuItemDBANew);
    m_jMenuItemDBAEdit.setText( "Edit");
    m_jMenuItemDBAEdit.setActionCommand( "dbaedit");
    m_jMenuItemDBAEdit.setMnemonic( (int)'E');
    m_jMenuItemDBAEdit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Edit16.gif"));
    m_jMenuDBAlias.add( m_jMenuItemDBAEdit);
    m_jMenuItemDBACopy.setText( "Copy");
    m_jMenuItemDBACopy.setActionCommand( "dbacopy");
    m_jMenuItemDBACopy.setMnemonic( (int)'C');
    m_jMenuItemDBACopy.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Copy16.gif"));
    m_jMenuDBAlias.add( m_jMenuItemDBACopy);
    m_jMenuItemDBADelete.setText( "Delete");
    m_jMenuItemDBADelete.setActionCommand( "dbadelete");
    m_jMenuItemDBADelete.setMnemonic( (int)'D');
    m_jMenuItemDBADelete.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Delete16.gif"));
    m_jMenuDBAlias.add( m_jMenuItemDBADelete);
    m_jMenuOption.setText( "Options");
    m_jMenuOption.setActionCommand( "options");
    m_jMenuOption.setMnemonic( (int)'O');
    m_jMenuBarMain.add( m_jMenuOption);
    m_jMenuItemProperties.setText( "Global properties");
    m_jMenuItemProperties.setActionCommand( "globprops");
    m_jMenuItemProperties.setMnemonic( (int)'G');
    m_jMenuItemProperties.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "preferences16.gif"));
    m_jMenuOption.add( m_jMenuItemProperties);
    m_jMenuItemConstrEdit.setText( "Conf. SQL parts menu");
    m_jMenuItemConstrEdit.setActionCommand( "editsqlpart");
    m_jMenuItemConstrEdit.setMnemonic( (int)'C');
    m_jMenuOption.add( m_jMenuItemConstrEdit);
    m_jMenuWindow.setText( "Windows");
    m_jMenuWindow.setActionCommand( "windows");
    m_jMenuWindow.setMnemonic( (int)'W');
    m_jMenuBarMain.add( m_jMenuWindow);
    createWindowMenuEntries( m_appAction, m_jMenuWindow);
    m_jMenuItemWDBD.setText( "View DBDriver");
    m_jMenuItemWDBD.setActionCommand( "viewdbd");
    m_jMenuWindow.add( m_jMenuItemWDBD);
    m_jMenuItemWDBA.setText( "View DBAlias");
    m_jMenuItemWDBA.setActionCommand( "viewdba");
    m_jMenuWindow.add( m_jMenuItemWDBA);
    m_jMenuWindow.add( m_jSeparatorFixWin);
    m_jMenuHelp.setText( "Help");
    m_jMenuHelp.setActionCommand( "help");
    m_jMenuHelp.setMnemonic( (int)'H');
    m_jMenuBarMain.add( m_jMenuHelp);
    m_jMenuItemHelp.setText( "Help Contents");
    m_jMenuItemHelp.setActionCommand( "helpcontents");
    m_jMenuItemHelp.setMnemonic( (int)'C');
    m_jMenuItemHelp.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "help.gif"));
    m_jMenuHelp.add( m_jMenuItemHelp);
    m_jMenuItemAbout.setText( "About");
    m_jMenuItemAbout.setActionCommand( "about");
    m_jMenuItemAbout.setMnemonic( (int)'A');
    m_jMenuHelp.add( m_jMenuItemAbout);
    m_jButtonProperties.setMaximumSize(new Dimension(18, 18));
    m_jButtonProperties.setMinimumSize(new Dimension(18, 18));
    m_jButtonProperties.setPreferredSize(new Dimension(18, 18));
    m_jButtonProperties.setToolTipText("Global properties");
    m_jButtonProperties.setActionCommand("globprops");
    m_jButtonProperties.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "preferences16.gif"));
    m_jButtonExit.setMaximumSize(new Dimension(18, 18));
    m_jButtonExit.setMinimumSize(new Dimension(18, 18));
    m_jButtonExit.setPreferredSize(new Dimension(18, 18));
    m_jButtonExit.setToolTipText("Exit");
    m_jButtonExit.setActionCommand("exit");
    m_jButtonExit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "exit16.gif"));
  }

  public void createFrame( DBAlias dba)
  {
    AppProperties dbttvProps = new AppProperties();
    boolean bl = m_appProps.getBooleanProperty( DbTtvDesktop.AUTOCOMMIT);
    dbttvProps.setBooleanProperty( DbTtvDesktop.AUTOCOMMIT, bl);
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.FIXCOLUMNS);
    dbttvProps.setBooleanProperty( DbTtvDesktop.FIXCOLUMNS, bl);
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.SQLDEBUG);
    dbttvProps.setBooleanProperty( DbTtvDesktop.SQLDEBUG, bl);
    Integer iRowLimit = m_appProps.getIntegerProperty( DbTtvDesktop.ROWLIMIT);
    if( iRowLimit != null)
      dbttvProps.setIntegerProperty( DbTtvDesktop.ROWLIMIT, iRowLimit);
    DbTreeTableView dbttv = new DbTreeTableView( this, dba, m_vecSql, dbttvProps);
    if( !dbttv.isConnected())
      return;
    dbttv.setVisible(true); //necessary as of kestrel
    m_dp.add( dbttv);
    bl = m_appProps.getBooleanProperty( WINMAX);
    if( bl)
    {
      try
      {
        dbttv.setMaximum( true);
      }
      catch( PropertyVetoException e)
      {}
    }
    bl = m_appProps.getBooleanProperty( WINMAXTOOP);
    if( bl)
    {
      onFixOp();
      int iWidth = m_dp.getWidth();
      int iHeight = m_dp.getHeight()-m_ofOutput.getHeight()+m_yOffset;
      dbttv.setSize( iWidth, iHeight);
      dbttv.setLocation( 0, 0);
    }
    try
    {
      dbttv.setSelected(true);
      dbttv.adjustComponent();
    }
    catch( PropertyVetoException e)
    {}
  }

  protected void createToolFrames()
  {
    m_dbd = new DBDriverFrame();
    m_dbd.setVisible(true); //necessary as of kestrel
    m_dp.add( m_dbd);
    m_dba = new DBAliasFrame( m_dbd, this);
    m_dba.setVisible(true); //necessary as of kestrel
    m_dp.add( m_dba);
    super.createToolFrames();
  }

  public JDesktopPane getDesktopPane()
  {
    return m_dp;
  }

  /**
  Read properties from file.
  */
  protected void readProperties()
  {
    super.readProperties( ConfigPathUtil.getCurrentReadPath( DBTTVDT, PROPSFILE));
    // DB url information
    String strUrl;
    int i = 0;
    String strDbUrl = DBURL + (new Integer( i)).toString();
    while( (strUrl = m_appProps.getProperty( strDbUrl)) != null)
    {
      m_vecUrl.add( strUrl);
      m_appProps.remove( strUrl);
      i++;
      strDbUrl = DBURL + (new Integer( i)).toString();
    }
  }

  /**
  Save properties from file.
  */
  protected void saveProperties()
  {
    super.saveProperties();
    // DB url information
    for( int i = 0; m_appProps.getProperty(DBURL + (new Integer( i)).toString()) != null; i++)
    {
      String strDbUrl = DBURL + (new Integer( i)).toString();
      m_appProps.remove( strDbUrl);
    }
    for( int i = 0; i < m_vecUrl.size(); i++)
    {
      String strDbUrl = DBURL + (new Integer( i)).toString();
      m_appProps.setProperty( strDbUrl, (String)m_vecUrl.elementAt( i));
    }

    m_appProps.store( ConfigPathUtil.getCurrentSavePath( DBTTVDT, PROPSFILE), PROPSHEADER);
  }

  private void initSqlParts()
  {
    int i;
    SqlClass sql;
    String strId;
    String strSubM;
    String strSql;

    for( i = 0; (strId = m_appProps.getProperty( SQLPART + String.valueOf( i) + "." + "Identifier")) != null
      && (strSql = m_appProps.getProperty( SQLPART + String.valueOf( i) + "." + "SqlPart")) != null;
      i++)
    {
      strSubM = m_appProps.getProperty( SQLPART + String.valueOf( i) + "." + "SubMenu");
      sql = new SqlClass();
      sql.setIdentifier( strId);
      sql.setSubMenu( strSubM);
      sql.setSql( strSql);
      m_vecSql.add( sql);
    }
  }

  private void saveSqlParts()
  {
    int i;
    SqlClass sql;

    for( i = 0; i < m_vecSql.size(); i++)
    {
      sql = (SqlClass)m_vecSql.elementAt( i);
      m_appProps.setProperty( SQLPART + String.valueOf( i) + "." + "Identifier", sql.getIdentifier());
      if( sql.getSubMenu() != null)
        m_appProps.setProperty( SQLPART + String.valueOf( i) + "." + "SubMenu", sql.getSubMenu());
      m_appProps.setProperty( SQLPART + String.valueOf( i) + "." + "SqlPart", sql.getSql());
    }
  }

  private void eraseSqlParts()
  {
    int i;
    SqlClass sql;
    String strId;
    String strSql;

    for( i = 0; m_appProps.getProperty( SQLPART + String.valueOf( i) + "." + "Identifier") != null; i++)
    {
      m_appProps.remove( SQLPART + String.valueOf( i) + "." + "Identifier");
      m_appProps.remove( SQLPART + String.valueOf( i) + "." + "SubMenu");
      m_appProps.remove( SQLPART + String.valueOf( i) + "." + "SqlPart");
    }
  }

  public DbTreeTableView[] getAllDbTtvFrames()
  {
    int iWin = 0;
    DbTreeTableView[] dbttv = new DbTreeTableView[0];;
    JInternalFrame[] ifArray = m_dp.getAllFrames();
    int iCount = Array.getLength( ifArray);
    int iDbttvCount = iCount-3;
    if( iDbttvCount > 0)
    {
      for( int i = 0; i < iCount; ++i)
      {
        if( ifArray[i] instanceof DbTreeTableView)
        {
          ++iWin;
        }
      }
      dbttv = new DbTreeTableView[iWin];
      iWin = 0;
      for( int i = 0; i < iCount; ++i)
      {
        if( ifArray[i] instanceof DbTreeTableView)
        {
          dbttv[iWin] = (DbTreeTableView)ifArray[i];
          ++iWin;
        }
      }
    }
    return dbttv;
  }

  public JInternalFrame[] getRelevantInternalFrames()
  {
    return getAllDbTtvFrames();
  }

  class AdpMenu
    implements MenuListener
  {
    public void menuCanceled( MenuEvent event)
    {
    }

    public void menuDeselected( MenuEvent event)
    {
    }

    public void menuSelected( MenuEvent event)
    {
      DbTtvDesktop.this.menuSelected( event, 13);
    }
  }

  class AppAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if( DbTtvDesktop.this.actionPerformed( event, cmd))
        ;
      else if( cmd == "exit")
        onExit();
      else if( cmd == "dbdnew")
        onNewDriver();
      else if( cmd == "dbdedit")
        onEditDriver();
      else if( cmd == "dbdcopy")
        onCopyDriver();
      else if( cmd == "dbddelete")
        onDeleteDriver();
      else if( cmd == "dbaconnect")
        onConnectAlias();
      else if( cmd == "dbanew")
        onNewAlias();
      else if( cmd == "dbaedit")
        onEditAlias();
      else if( cmd == "dbacopy")
        onCopyAlias();
      else if( cmd == "dbadelete")
        onDeleteAlias();
      else if( cmd == "viewdbd")
        onViewDbd();
      else if( cmd == "viewdba")
        onViewDba();
      else if( cmd == "globprops")
        onGlobalProperties();
      else if( cmd == "editsqlpart")
        onConfigSqlPart();
      else if( cmd == "about")
        onAbout();
      else if( cmd == "helpcontents")
        onHelpcontents();
      else
      {
        int iIdx = -1;
        try
        {
          iIdx = Integer.parseInt( cmd);
          int i = 0;
          int iWin = -1;
          if( iIdx > -1)
          {
            JInternalFrame[] ifArray = getAllDbTtvFrames();
            for( i = 0; i < Array.getLength( ifArray); ++i)
            {
              if( i == iIdx)
              {
                DbTreeTableView dbttv = ((DbTreeTableView)ifArray[i]);
                if( dbttv.isIcon())
                {
                  try
                  {
                    dbttv.setIcon( false);
                  }
                  catch( PropertyVetoException e)
                  {}
                }
                dbttv.toFront();
                try
                {
                  dbttv.setSelected(true);
                }
                catch( PropertyVetoException e)
                {}
              }
            }
          }
        }
        catch( NumberFormatException nfex)
        {
        }
      }
    }
  }

  void onNewDriver()
  {
    m_dbd.onAdd();
  }

  void onEditDriver()
  {
    m_dbd.onEdit();
  }

  void onCopyDriver()
  {
    m_dbd.onCopy();
  }

  void onDeleteDriver()
  {
    m_dbd.onDelete();
  }

  void onConnectAlias()
  {
    m_dba.onConnect();
  }

  void onNewAlias()
  {
    m_dba.onAdd();
  }

  void onEditAlias()
  {
    m_dba.onEdit();
  }

  void onCopyAlias()
  {
    m_dba.onCopy();
  }

  void onDeleteAlias()
  {
    m_dba.onDelete();
  }

  void onViewDbd()
  {
    if( m_dbd.isIcon())
    {
      try
      {
        m_dbd.setIcon( false);
      }
      catch( PropertyVetoException e)
      {}
    }
    m_dbd.toFront();
    try
    {
      m_dbd.setSelected(true);
    }
    catch( PropertyVetoException e)
    {}
  }

  void onViewDba()
  {
    if( m_dba.isIcon())
    {
      try
      {
        m_dba.setIcon( false);
      }
      catch( PropertyVetoException e)
      {}
    }
    m_dba.toFront();
    try
    {
      m_dba.setSelected(true);
    }
    catch( PropertyVetoException e)
    {}
  }

  protected void onCascadeWin()
  {
    super.onCascadeWin();
  }

  protected void onTileWin()
  {
    super.onTileWin();
  }

  protected void onMaxWin()
  {
    super.onMaxWin();
  }

  protected void onMaxToOpWin()
  {
    super.onMaxToOpWin();
  }

  protected void onDeiconWin()
  {
    super.onDeiconWin();
  }

  private void onGlobalProperties()
  {
    DbTtvPropertiesDialog dlg = new DbTtvPropertiesDialog( this, "Global properties", true, m_appProps);
    dlg.setVisible( true);
    dlg.dispose();
  }

  private void onConfigSqlPart()
  {
    eraseSqlParts();
    ConfigSqlConstrDialog dlg = new ConfigSqlConstrDialog( this, m_vecSql);
    dlg.setVisible( true);
    dlg.dispose();
    JInternalFrame[] ifArray = m_dp.getAllFrames();
    for( int i = 0; i < Array.getLength( ifArray); ++i)
    {
      if( ifArray[i] instanceof DbTreeTableView)
        ((DbTreeTableView)ifArray[i]).setSqlPartVector( m_vecSql);
    }
  }

  protected void onExit()
  {
    super.onExit();
    JInternalFrame[] ifArray = m_dp.getAllFrames();
    for( int i = 0; i < Array.getLength( ifArray); ++i)
    {
      if( ifArray[i] instanceof DbTreeTableView)
        ((DbTreeTableView)ifArray[i]).onExit();
      else if( ifArray[i] instanceof DBDriverFrame)
        ((DBDriverFrame)ifArray[i]).onExit();
      else if( ifArray[i] instanceof DBAliasFrame)
        ((DBAliasFrame)ifArray[i]).onExit();
    }
    eraseSqlParts();
    saveSqlParts();
    saveProperties();
    setVisible(false); // hide the Frame
    dispose();	     // free the system resources
    System.exit(0);    // close the application
  }

  protected void onSave()
  {
    m_dbd._save();
    m_dba._save();
    eraseSqlParts();
    saveSqlParts();
    saveProperties();
  }

  void onAbout()
  {
    m_aboutDlg = new AboutDialog( this, "About", true, DBTTVDT, "Database Tree View with editable Database Table View"
                                  , "Version " + VERSION + " by A.Eisenhauer"
                                  , new ImageIcon( getClass().getResource( "Splash.gif")), DBTTVDT);
    m_aboutDlg.setVisible( true);
    m_aboutDlg.dispose();
    m_aboutDlg = null;
  }

  void onHelpcontents()
  {
    if( m_helpViewer == null)
    {
      m_helpViewer = new HelpDialog( this, "Help", false, DBTTVDT);
      try
      {
        m_helpViewer.getHtmlPanel().setSearchDir( m_strDocPath);
        m_helpViewer.getHtmlPanel().setPage( new URL( new URL( "file:"), m_strDocPath + "/" + m_strDocIndex), null);
      }
      catch (MalformedURLException e)
      {
        GlobalAppProperties.instance().getPrintStreamOutput( DBTTVDT).println( "Malformed URL: " + e);
        //System.out.println("Malformed URL: " + e);
      }
    }
    m_helpViewer.setVisible( true);
  }

  static public void main(String args[])
  {
    DbTtvDesktop dbttvdt = new DbTtvDesktop();
    dbttvdt.setVisible(true);
  }

  public void addNotify()
  {
    super.addNotify();
  }

  // static initializer for setting look & feel
  static
  {
    try
    {
      GlobalAppProperties.instance().addAppClass( DBTTVDT, Class.forName( "haui.app.dbtreetableview.DbTtvDesktop" ) );
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch( Exception e )
    {}
  }
}
