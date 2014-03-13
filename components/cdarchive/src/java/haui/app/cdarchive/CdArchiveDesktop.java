package haui.app.cdarchive;

import haui.components.AboutDialog;
import haui.components.HelpDialog;
import haui.components.SplashWindow;
import haui.components.desktop.JDesktopFrame;
import haui.resource.ResouceManager;
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
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


/**
 * Module:      CdArchiveDesktop.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchiveDesktop.java,v $
 *<p>
 * Description: CdArchiveDesktop.<br>
 *</p><p>
 * Created:     23.04.2003	by	AE
 *</p><p>
 *
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     23. April 2003
 * @history     23.04.2003	by	AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: CdArchiveDesktop.java,v $
 * Revision 1.4  2004-08-31 16:03:02+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.3  2004-06-22 14:08:49+02  t026843
 * bigger changes
 *
 * Revision 1.2  2004-02-17 16:07:00+01  t026843
 * <>
 *
 * Revision 1.1  2003-05-28 14:19:48+02  t026843
 * reorganisations
 *
 * Revision 1.0  2003-05-21 16:24:38+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.4 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchiveDesktop.java,v 1.4 2004-08-31 16:03:02+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class CdArchiveDesktop
  extends JDesktopFrame
{
  // Constants
  public static final String VERSION = "0.1";
  public static final String CDARCHDT = "CdArchive";
  public static final String PROPSFILE = "cdarchdt.ppr";
  public static final String PROPSHEADER = "CdArchive application properties";

  // Default properties constants
  public static final String DBDRIVER = "DbDriver";
  public static final String DBURL = "DbUrl";
  public static final String FILTERS = "Filters";
  public static final String CATEGORIES = "Categories";
  public static final String PRINTERADJUST = "PrinterAdjust";

  // member variables
  protected static final int m_xOffset = 25, m_yOffset = 25;
  protected AppAction m_appAction = new AppAction();
  protected String m_strDbDriver = "jdbc.SimpleText.SimpleTextDriver";
  protected String m_strDbUrl = "jdbc:SimpleText:.";
  protected Vector m_vecFilters;
  protected static Vector m_vecCategories;
  protected IdManager m_idManager = IdManager.instance();
  protected CdArchSearchDialog m_cdArchSearchDlg;
  protected String m_strSearchString;
  private int m_iPrinterAdjust;

  // GUI member variables
  private AboutDialog m_aboutDlg;
  private HelpDialog m_helpViewer;
  private BorderLayout m_borderLayoutMain;
  private JScrollPane m_jScrollPaneDp;
  private JMenuBar m_jMenuBarMain;
  private JMenu m_jMenuFile;
  private JMenuItem m_jMenuItemNew;
  private JMenuItem m_jMenuItemSearch;
  private JMenuItem m_jMenuItemSaveDb;
  private JSeparator m_jSeparatorNew;
  private JMenuItem m_jMenuItemExit;
  private JSeparator m_jSeparatorExit;
  private JMenu m_jMenuOption;
  private JMenuItem m_jMenuItemProperties;
  private JMenu m_jMenuWindow;
  private JMenu m_jMenuHelp;
  private JMenuItem m_jMenuItemHelp;
  private JMenuItem m_jMenuItemAbout;
  private JToolBar m_jToolBarMain;
  private JButton m_jButtonNew;
  private JButton m_jButtonSearch;
  private JButton m_jButtonSaveDb;
  private JButton m_jButtonProperties;
  private JButton m_jButtonExit;

  public CdArchiveDesktop()
  {
    super( CDARCHDT, CDARCHDT);
    APPICON = CDARCHDT + ".gif";
    SplashWindow sw = null;
    JPanel jPanelPb = new JPanel();
    jPanelPb.setLayout( new BorderLayout());
    JLabel jLabelPb = new JLabel( "Loading archive data ...");
    JProgressBar pb = new JProgressBar();
    jPanelPb.add( jLabelPb, BorderLayout.NORTH);
    jPanelPb.add( pb, BorderLayout.CENTER);
    setIconImage( (new ImageIcon( getClass().getResource(APPICON))).getImage());
    try
    {
      sw = new SplashWindow( CDARCHDT, "CD Catalog Archive", "Version 1.0 by A.Eisenhauer",
        new ImageIcon( this.getClass().getResource( "Splash.gif")));
      //sw.setSize( 370,144);
      sw.getContentPane().add( jPanelPb, BorderLayout.SOUTH);
      sw.pack();
      sw.setVisible( true);
    }
    catch( Exception ex)
    {
      sw = null;
    }

    readProperties( pb);

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    createToolFrames();
    m_jToolBarMain.add(m_jButtonNew, null);
    m_jToolBarMain.add(m_jButtonSearch, null);
    m_jToolBarMain.addSeparator();
    m_jToolBarMain.add(m_jButtonSaveDb, null);
    m_jToolBarMain.addSeparator();
    createToolbarEntries( m_appAction, m_jToolBarMain);
    m_jToolBarMain.add(m_jButtonProperties, null);
    m_jToolBarMain.addSeparator();
    m_jToolBarMain.add(m_jButtonExit, null);
    //createFrame( m_dp);

    AdpMenu adpMenu = new AdpMenu();
    m_jMenuWindow.addMenuListener( adpMenu);

    // add action handler
    m_jMenuItemNew.addActionListener( m_appAction);
    m_jMenuItemSearch.addActionListener( m_appAction);
    m_jMenuItemSaveDb.addActionListener( m_appAction);
    m_jMenuItemExit.addActionListener( m_appAction);
    m_jMenuItemProperties.addActionListener( m_appAction);
    m_jMenuItemHelp.addActionListener( m_appAction);
    m_jMenuItemAbout.addActionListener( m_appAction);
    m_jButtonNew.addActionListener( m_appAction);
    m_jButtonSearch.addActionListener( m_appAction);
    m_jButtonSaveDb.addActionListener( m_appAction);
    m_jButtonProperties.addActionListener( m_appAction);
    m_jButtonExit.addActionListener( m_appAction);

    GlobalAppProperties.instance().addAppClass( CDARCHDT, getClass());
    sw.close();
  }

  //Initialisierung der Komponente
  private void jbInit()
    throws Exception
  {
    m_jMenuItemNew = new JMenuItem();
    m_jMenuItemSearch = new JMenuItem();
    m_jMenuItemSaveDb = new JMenuItem();
    m_dp.setBackground(Color.black);
    m_dp.setDesktopManager( m_dpManager);
    m_borderLayoutMain = new BorderLayout();
    m_jMenuBarMain = new JMenuBar();
    m_jMenuFile = new JMenu();
    m_jSeparatorNew = new JSeparator();
    m_jSeparatorExit = new JSeparator();
    m_jMenuItemExit = new JMenuItem();
    m_jMenuOption = new JMenu();
    m_jMenuItemProperties = new JMenuItem();
    m_jMenuWindow = new JMenu();
    m_jMenuHelp = new JMenu();
    m_jMenuItemHelp = new JMenuItem();
    m_jMenuItemAbout = new JMenuItem();
    m_jButtonNew = new JButton();
    m_jButtonSearch = new JButton();
    m_jButtonSaveDb = new JButton();
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
    m_jMenuItemNew.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "new.gif"));
    m_jMenuItemNew.setMnemonic((int)'N');
    m_jMenuItemNew.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_N, Event.CTRL_MASK));
    m_jMenuItemNew.setActionCommand("new");
    m_jMenuItemNew.setText("New");
    m_jMenuFile.add(m_jMenuItemNew);
    m_jMenuItemSearch.setIcon(ResouceManager.getCommonImageIcon( CDARCHDT, "search.gif"));
    m_jMenuItemSearch.setMnemonic((int)'c');
    m_jMenuItemSearch.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_S, Event.ALT_MASK));
    m_jMenuItemSearch.setActionCommand("search");
    m_jMenuItemSearch.setText("Search");
    m_jMenuFile.add(m_jMenuItemSearch);
    m_jMenuItemSaveDb.setIcon(ResouceManager.getCommonImageIcon( CDARCHDT, "save.gif"));
    m_jMenuItemSaveDb.setMnemonic((int)'S');
    m_jMenuItemSaveDb.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_S, Event.CTRL_MASK));
    m_jMenuItemSaveDb.setActionCommand("savedb");
    m_jMenuItemSaveDb.setText("Save");
    m_jMenuFile.add(m_jMenuItemSaveDb);
    m_jMenuFile.add( m_jSeparatorNew);
    createFileMenuEntries( m_appAction, m_jMenuFile);
    m_jMenuFile.add( m_jSeparatorExit);
    m_jMenuItemExit.setText( "Exit");
    m_jMenuItemExit.setActionCommand( "exit");
    m_jMenuItemExit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, Event.ALT_MASK));
    m_jMenuItemExit.setMnemonic( (int)'x');
    m_jMenuItemExit.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "exit16.gif"));
    m_jMenuFile.add( m_jMenuItemExit);
    m_jMenuOption.setText( "Options");
    m_jMenuOption.setActionCommand( "options");
    m_jMenuOption.setMnemonic( (int)'O');
    m_jMenuBarMain.add( m_jMenuOption);
    m_jMenuItemProperties.setText( "Global properties");
    m_jMenuItemProperties.setActionCommand( "globprops");
    m_jMenuItemProperties.setMnemonic( (int)'G');
    m_jMenuItemProperties.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "preferences16.gif"));
    m_jMenuOption.add( m_jMenuItemProperties);
    m_jMenuWindow.setText( "Windows");
    m_jMenuWindow.setActionCommand( "windows");
    m_jMenuWindow.setMnemonic( (int)'W');
    m_jMenuBarMain.add( m_jMenuWindow);
    createWindowMenuEntries( m_appAction, m_jMenuWindow);
    m_jMenuHelp.setText( "Help");
    m_jMenuHelp.setActionCommand( "help");
    m_jMenuHelp.setMnemonic( (int)'H');
    m_jMenuBarMain.add( m_jMenuHelp);
    m_jMenuItemHelp.setText( "Help Contents");
    m_jMenuItemHelp.setActionCommand( "helpcontents");
    m_jMenuItemHelp.setMnemonic( (int)'C');
    m_jMenuItemHelp.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "help.gif"));
    m_jMenuHelp.add( m_jMenuItemHelp);
    m_jMenuItemAbout.setText( "About");
    m_jMenuItemAbout.setActionCommand( "about");
    m_jMenuItemAbout.setMnemonic( (int)'A');
    m_jMenuHelp.add( m_jMenuItemAbout);
    m_jButtonNew.setMaximumSize(new Dimension(18, 18));
    m_jButtonNew.setMinimumSize(new Dimension(18, 18));
    m_jButtonNew.setPreferredSize(new Dimension(18, 18));
    m_jButtonNew.setToolTipText("New");
    m_jButtonNew.setActionCommand("new");
    m_jButtonNew.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "new.gif"));
    m_jButtonSearch.setMaximumSize(new Dimension(18, 18));
    m_jButtonSearch.setMinimumSize(new Dimension(18, 18));
    m_jButtonSearch.setPreferredSize(new Dimension(18, 18));
    m_jButtonSearch.setToolTipText("Search");
    m_jButtonSearch.setActionCommand("search");
    m_jButtonSearch.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "search.gif"));
    m_jButtonSaveDb.setMaximumSize(new Dimension(18, 18));
    m_jButtonSaveDb.setMinimumSize(new Dimension(18, 18));
    m_jButtonSaveDb.setPreferredSize(new Dimension(18, 18));
    m_jButtonSaveDb.setToolTipText("Save");
    m_jButtonSaveDb.setActionCommand("savedb");
    m_jButtonSaveDb.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "save.gif"));
    m_jButtonProperties.setMaximumSize(new Dimension(18, 18));
    m_jButtonProperties.setMinimumSize(new Dimension(18, 18));
    m_jButtonProperties.setPreferredSize(new Dimension(18, 18));
    m_jButtonProperties.setToolTipText("Global properties");
    m_jButtonProperties.setActionCommand("globprops");
    m_jButtonProperties.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "preferences16.gif"));
    m_jButtonExit.setMaximumSize(new Dimension(18, 18));
    m_jButtonExit.setMinimumSize(new Dimension(18, 18));
    m_jButtonExit.setPreferredSize(new Dimension(18, 18));
    m_jButtonExit.setToolTipText("Exit");
    m_jButtonExit.setActionCommand("exit");
    m_jButtonExit.setIcon( ResouceManager.getCommonImageIcon( CDARCHDT, "exit16.gif"));
  }

  public void createFrame( CdObject cdObj, boolean blNew)
  {
    CdArchiveFrame cdArchFrame = new CdArchiveFrame( this, cdObj, m_appProps, blNew);
    m_dp.add( cdArchFrame);
    cdArchFrame.setSearchString( m_strSearchString);
    cdArchFrame.setVisible( true);
    super.createFrame( cdArchFrame);
    cdArchFrame.adjustComponent();
  }

  protected void createToolFrames()
  {
    super.createToolFrames();
  }

  public String getSearchString()
  {
    return m_strSearchString;
  }

  public void setSearchString( String strSearch)
  {
    m_strSearchString = strSearch;
  }

  public static Vector getCategories()
  {
    return m_vecCategories;
  }

  public static boolean addCategory( String strCat)
  {
    boolean blRet = false;
    if( m_vecCategories == null)
      m_vecCategories = new Vector();
    if( !m_vecCategories.contains( strCat))
    {
      if( m_vecCategories.size() == 0)
      {
        m_vecCategories.add( strCat );
        blRet = true;
      }
      else
      {
        for( int i = 0; i < m_vecCategories.size(); ++ i)
        {
          String str = (String)m_vecCategories.elementAt( i);
          if( str.compareTo( strCat) > 0)
          {
            m_vecCategories.insertElementAt( strCat, i );
            blRet = true;
            break;
          }
        }
        if( !blRet)
        {
          m_vecCategories.add( strCat );
          blRet = true;
        }
      }
    }
    else
      blRet = true;
    return blRet;
  }

  public static boolean removeCategory( String strCat)
  {
    boolean blRet = false;
    if( m_vecCategories == null)
      return blRet;
    if( m_vecCategories.contains( strCat))
    {
      CdObject.removeCategory( strCat);
      m_vecCategories.remove( strCat);
      blRet = true;
    }
    return blRet;
  }

  public static boolean renameCategory( String strOldCat, String strNewCat)
  {
    boolean blRet = false;
    if( m_vecCategories == null)
      return blRet;
    if( m_vecCategories.contains( strOldCat))
    {
      CdObject.renameCategory( strOldCat, strNewCat);
      addCategory( strNewCat);
      m_vecCategories.remove( strOldCat);
      blRet = true;
    }
    return blRet;
  }

  /**
  Read properties from file.
  */
  protected void readProperties( JProgressBar pb)
  {
    super.readProperties( ConfigPathUtil.getCurrentReadPath( CDARCHDT, PROPSFILE));
    String str = m_appProps.getProperty( DBDRIVER);
    if( str != null)
      m_strDbDriver = str;
    str = m_appProps.getProperty( DBURL);
    if( str != null)
      m_strDbUrl = str;
    m_vecFilters = (Vector)m_appProps.getObjectProperty( FILTERS);
    Integer iPa = m_appProps.getIntegerProperty( PRINTERADJUST);
    if( iPa != null)
      m_iPrinterAdjust = iPa.intValue();
    else
      m_iPrinterAdjust = 0;
    CdObject.connect( pb);
    m_vecCategories = CdObject.getAllCategories();
  }

  /**
  Save properties from file.
  */
  protected void saveProperties()
  {
    super.saveProperties();

    m_appProps.setProperty( DBDRIVER, m_strDbDriver);
    m_appProps.setProperty( DBURL, m_strDbUrl);
    if( m_vecFilters != null)
      m_appProps.setObjectProperty( FILTERS, m_vecFilters);
    m_appProps.setIntegerProperty( PRINTERADJUST, new Integer( m_iPrinterAdjust));
    m_appProps.store( ConfigPathUtil.getCurrentSavePath( CDARCHDT, PROPSFILE), PROPSHEADER);
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
      CdArchiveDesktop.this.menuSelected( event, 11);
    }
  }

  class AppAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if( CdArchiveDesktop.this.actionPerformed( event, cmd))
        ;
      else if( cmd == "new")
        onNew();
      else if( cmd == "search")
        onSearch();
      else if( cmd == "savedb")
        onSaveDb();
      else if( cmd == "exit")
        onExit();
      else if( cmd == "save")
        onSave();
      else if( cmd == "globprops")
        onGlobalProperties();
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
            JInternalFrame[] ifArray = getRelevantInternalFrames();
            for( i = 0; i < Array.getLength( ifArray); ++i)
            {
              if( i == iIdx)
              {
                JInternalFrame cdarchdt = ((JInternalFrame)ifArray[i]);
                if( cdarchdt.isIcon())
                {
                  try
                  {
                    cdarchdt.setIcon( false);
                  }
                  catch( PropertyVetoException e)
                  {}
                }
                cdarchdt.toFront();
                try
                {
                  cdarchdt.setSelected(true);
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

  protected void onNew()
  {
    createFrame( new CdObject(), true);
  }

  protected void onSearch()
  {
    if( m_cdArchSearchDlg == null)
      m_cdArchSearchDlg = new CdArchSearchDialog( this, "archive search", false, m_vecCategories);
    else
      m_cdArchSearchDlg._init();
    m_cdArchSearchDlg.setVisible( true);
    if( m_cdArchSearchDlg.isIcon())
    {
      try
      {
        m_cdArchSearchDlg.setIcon( false);
      }
      catch( PropertyVetoException e)
      {}
    }
    m_cdArchSearchDlg.toFront();
    try
    {
      m_cdArchSearchDlg.setSelected(true);
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
    CdArchDtPropertiesDialog dlg = new CdArchDtPropertiesDialog( this, "Global properties", true, m_appProps, m_vecCategories);
    dlg.setVisible( true);
    m_vecFilters = (Vector)m_appProps.getObjectProperty( FILTERS);
  }

  protected void onExit()
  {
    super.onExit();
    JInternalFrame[] ifArray = getRelevantInternalFrames();
    for( int i = 0; i < Array.getLength( ifArray); ++i)
    {
      if( ifArray[i] instanceof CdArchiveFrame)
        ((CdArchiveFrame)ifArray[i]).onExit();
    }
    onSaveDb();
    saveProperties();
    setVisible(false); // hide the Frame
    dispose();	     // free the system resources
    System.exit(0);    // close the application
  }

  protected void onSaveDb()
  {
    IdManager.instance()._save();
    CdObject._save();
  }

  protected void onSave()
  {
    super.onSave();
    saveProperties();
  }

  void onAbout()
  {
    m_aboutDlg = new AboutDialog( this, "About", true, CDARCHDT, "CD Catalog Archive", "Version " + VERSION + " by A.Eisenhauer"
                                  , new ImageIcon( getClass().getResource( "Splash.gif")), CDARCHDT);
    m_aboutDlg.setVisible( true);
    m_aboutDlg.dispose();
    m_aboutDlg = null;
  }

  void onHelpcontents()
  {
    if( m_helpViewer == null)
    {
      m_helpViewer = new HelpDialog( this, "Help", false, CDARCHDT);
      try
      {
        m_helpViewer.getHtmlPanel().setSearchDir( m_strDocPath);
        m_helpViewer.getHtmlPanel().setPage( new URL( new URL( "file:"), m_strDocPath + "/" + m_strDocIndex), null);
      }
      catch (MalformedURLException e)
      {
        GlobalAppProperties.instance().getPrintStreamOutput( CdArchiveDesktop.CDARCHDT).println( "Malformed URL: " + e);
        //System.out.println("Malformed URL: " + e);
      }
    }
    m_helpViewer.setVisible( true);
  }

  // static initializer for setting look & feel
  static
  {
    try
    {
      GlobalAppProperties.instance().addAppClass( CDARCHDT, Class.forName( "haui.app.cdarchive.CdArchiveDesktop" ) );
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch( Exception e )
    {}
  }

  static public void main( String args[] )
  {
    CdArchiveDesktop desktop = new CdArchiveDesktop();
    desktop.setVisible( true );
  }
}