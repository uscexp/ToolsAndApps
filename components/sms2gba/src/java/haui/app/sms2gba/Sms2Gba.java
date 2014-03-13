package haui.app.sms2gba;

import haui.components.*;
import haui.resource.ResouceManager;
import haui.util.*;
import haui.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Array;

/**
 * Module:      sms2gba.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\sms2gba\\Sms2Gba.java,v $
 *<p>
 * Description: sms rom injector for DrSMS.<br>
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     22.05.2003
 *</p><p>
 * Modification:<br>
 * $Log: Sms2Gba.java,v $
 * Revision 1.5  2004-08-31 16:03:01+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.4  2004-06-22 14:08:54+02  t026843
 * bigger changes
 *
 * Revision 1.3  2003-07-11 17:20:43+02  t026843
 * - real first public release
 * - rom type flag added (Game Gear support)
 *
 * Revision 1.2  2003-06-06 10:04:48+02  t026843
 * first public release
 *
 * Revision 1.1  2003-06-04 15:35:23+02  t026843
 * zip, tar, gz, bz2, jar support added.
 *
 * Revision 1.0  2003-05-28 14:21:27+02  t026843
 * Initial revision
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.5 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\sms2gba\\Sms2Gba.java,v 1.5 2004-08-31 16:03:01+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class Sms2Gba
  extends JFrame
{
  // constants
  public static final String VERSION = "0.4";
  public static final String APPICON = "gba.gif";
  public static final String APPTITLE = "Sms2Gba";
  public static final String PROPSFILE = "sms2gba.ppr";
  public static final String PROPSHEADER = "Sms2Gba application properties";
  public static final String[] GBAFILTER = { "gba", "bin" };
  public static final String[] SMSFILTER = { "sms", "gg", "zip", "jar", "gz", "tar", "bz2" };
  public static final String[] EMUFILTER = { "exe" };
  public static final String[] PURESMSFILTER = { "sms" };
  public static final String[] PUREGGFILTER = { "gg" };
  public static final String[] DRSMSFILTERS = { "sms", "gg" };
  public static final int NAMELENGTH = 28;

  // Default properties constants
  public static final String DRSMSROM = "DrSmsRom";
  public static final String SMSROMNAME = "SmsRomName";
  public static final String SMSROMS = "SmsRoms";
  public static final String EMUPATH = "EmuPath";
  public static final String SMSROMSPATH = "SmsRomsPath";
  public static final String SMSROMPATHS = "SmsRomPaths";
  public static final String SMSROMNAMES = "SmsRomNames";
  public static final String WLOCX = "WinLocationX";
  public static final String WLOCY = "WinLocationY";
  public static final String WWIDTH = "WinWidth";
  public static final String WHEIGHT = "WinHeight";

  // member Variables
  protected AppProperties m_appProps = new AppProperties();
  protected String m_strDrsms;
  protected String m_strRomName;
  protected String m_strSmsRomsPath;
  protected String m_strEmuPath;
  protected Vector m_vecSmsRoms;

  // GUI member variables
  private AboutDialog m_aboutDlg;
  private JPopupMenu m_popup = new JPopupMenu();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JMenuBar m_jMenuBar = new JMenuBar();
  JMenu m_jMenuFile = new JMenu();
  JMenuItem m_jMenuItemAdd = new JMenuItem();
  JMenuItem m_jMenuItemRemove = new JMenuItem();
  JMenuItem m_jMenuItemCreate = new JMenuItem();
  JMenuItem m_jMenuItemExec = new JMenuItem();
  JMenuItem m_jMenuItemExit = new JMenuItem();
  JMenu m_jMenuEdit = new JMenu();
  JMenuItem m_jMenuItemProperties = new JMenuItem();
  JMenuItem m_jMenuItemUp = new JMenuItem();
  JMenuItem m_jMenuItemDown = new JMenuItem();
  JMenu m_jMenuHelp = new JMenu();
  JMenuItem m_jMenuItemAbout = new JMenuItem();
  JToolBar m_jToolBar = new JToolBar();
  JButton m_jButtonAdd = new JButton();
  JButton m_jButtonRemove = new JButton();
  JButton m_jButtonCreate = new JButton();
  JButton m_jButtonUp = new JButton();
  JButton m_jButtonDown = new JButton();
  JButton m_jButtonProperties = new JButton();
  JButton m_jButtonExec = new JButton();
  JButton m_jButtonExit = new JButton();
  JPanel m_jPanelMain = new JPanel();
  JLabel m_jLabelDrsmsRom = new JLabel();
  BorderLayout m_borderLayoutDrsmsRom = new BorderLayout();
  JTextField m_jTextFieldDrsmsRom = new JTextField();
  JPanel m_jPanelDrsmsRom = new JPanel();
  JButton m_jButtonDrsmsRom = new JButton();
  BorderLayout m_borderLayoutMain = new BorderLayout();
  BorderLayout m_borderLayoutCreateRom = new BorderLayout();
  JPanel m_jPanelCreateRom = new JPanel();
  BorderLayout m_borderLayoutEmu = new BorderLayout();
  JPanel m_jPanelEmu = new JPanel();
  JScrollPane m_jScrollPaneRoms = new JScrollPane();
  JList m_jListRoms = new JList();
  JTextField m_jTextFieldEmu = new JTextField();
  JButton m_jButtonEmuBrowse = new JButton();
  JLabel m_jLabelEmu = new JLabel();
  JLabel m_jLabelCreateRom = new JLabel();
  JTextField m_jTextFieldCreateRom = new JTextField();
  JButton m_jButtonCreateRomBrowse = new JButton();
  JMenuItem m_jMenuItemPuRemove = new JMenuItem();
  JMenuItem m_jMenuItemPuProperties = new JMenuItem();
  JMenuItem m_jMenuItemPuUp = new JMenuItem();
  JMenuItem m_jMenuItemPuDown = new JMenuItem();

  public Sms2Gba()
  {
    super( APPTITLE);
    AppProperties.addRootComponent( APPTITLE, this);
    setIconImage( (new ImageIcon( getClass().getResource(APPICON))).getImage());

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    setSize( 480, 320);
    _init();

    //Quit this app when the big window closes.
    AdpWindow adpWin = new AdpWindow();
    addWindowListener( adpWin);

    MouseHandler mouseHandler = new MouseHandler();
    m_jListRoms.addMouseListener( mouseHandler);

    AppAction appAct = new AppAction();
    m_jMenuItemAdd.addActionListener( appAct);
    m_jMenuItemRemove.addActionListener( appAct);
    m_jMenuItemCreate.addActionListener( appAct);
    m_jMenuItemExec.addActionListener( appAct);
    m_jMenuItemExit.addActionListener( appAct);
    m_jMenuItemProperties.addActionListener( appAct);
    m_jMenuItemUp.addActionListener( appAct);
    m_jMenuItemDown.addActionListener( appAct);
    m_jMenuItemAbout.addActionListener( appAct);
    m_jMenuItemPuRemove.addActionListener( appAct);
    m_jMenuItemPuProperties.addActionListener( appAct);
    m_jMenuItemPuUp.addActionListener( appAct);
    m_jMenuItemPuDown.addActionListener( appAct);
    m_jButtonExit.addActionListener( appAct);
    m_jButtonDrsmsRom.addActionListener( appAct);
    m_jButtonAdd.addActionListener( appAct);
    m_jButtonRemove.addActionListener( appAct);
    m_jButtonCreate.addActionListener( appAct);
    m_jButtonProperties.addActionListener( appAct);
    m_jButtonUp.addActionListener( appAct);
    m_jButtonDown.addActionListener( appAct);
    m_jButtonExec.addActionListener( appAct);
    m_jButtonCreateRomBrowse.addActionListener( appAct);
    m_jButtonEmuBrowse.addActionListener( appAct);
  }

  private void jbInit() throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutBase);
    this.setJMenuBar( m_jMenuBar);
    m_jMenuFile.setText("File");
    m_jMenuFile.setMnemonic((int)'F');
    m_jMenuItemAdd.setText( "Add SMS rom(s)");
    m_jMenuItemAdd.setActionCommand( "add");
    m_jMenuItemAdd.setMnemonic( (int)'A');
    m_jMenuItemAdd.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "add.gif"));
    m_jMenuItemAdd.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, 0));
    m_jMenuItemRemove.setText( "Remove SMS rom(s)");
    m_jMenuItemRemove.setActionCommand( "remove");
    m_jMenuItemRemove.setMnemonic( (int)'R');
    m_jMenuItemRemove.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "remove.gif"));
    m_jMenuItemRemove.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0));
    m_jMenuItemCreate.setText( "Create GBA rom");
    m_jMenuItemCreate.setActionCommand( "createrom");
    m_jMenuItemCreate.setMnemonic( (int)'C');
    m_jMenuItemCreate.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "create.gif"));
    m_jMenuItemCreate.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, Event.CTRL_MASK));
    m_jMenuItemExec.setText( "Execute emulator");
    m_jMenuItemExec.setActionCommand( "exec");
    m_jMenuItemExec.setMnemonic( (int)'E');
    m_jMenuItemExec.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "lightning16.gif"));
    m_jMenuItemExec.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_E, Event.CTRL_MASK));
    m_jMenuItemExit.setText( "Exit");
    m_jMenuItemExit.setActionCommand( "exit");
    m_jMenuItemExit.setMnemonic( (int)'C');
    m_jMenuItemExit.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "exit16.gif"));
    m_jMenuItemExit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, Event.ALT_MASK));
    m_jMenuEdit.setText("Edit");
    m_jMenuEdit.setMnemonic((int)'E');
    m_jMenuItemUp.setText( "SMS rom move up");
    m_jMenuItemUp.setActionCommand( "up");
    m_jMenuItemUp.setMnemonic( (int)'U');
    m_jMenuItemUp.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "upbutakt.gif"));
    m_jMenuItemUp.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_U, Event.CTRL_MASK));
    m_jMenuItemDown.setText( "SMS rom move down");
    m_jMenuItemDown.setActionCommand( "down");
    m_jMenuItemDown.setMnemonic( (int)'D');
    m_jMenuItemDown.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "downbutakt.gif"));
    m_jMenuItemDown.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_D, Event.CTRL_MASK));
    m_jMenuItemProperties.setText( "SMS rom properties");
    m_jMenuItemProperties.setActionCommand( "itemproperties");
    m_jMenuItemProperties.setMnemonic( (int)'P');
    m_jMenuItemProperties.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "properties16.gif"));
    m_jMenuItemProperties.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, Event.CTRL_MASK));
    m_jMenuHelp.setText( "Help");
    m_jMenuHelp.setActionCommand( "help");
    m_jMenuHelp.setMnemonic( (int)'H');
    m_jMenuItemAbout.setText( "About");
    m_jMenuItemAbout.setActionCommand( "about");
    m_jMenuItemAbout.setMnemonic( (int)'A');
    m_jTextFieldEmu.setText("");
    m_jTextFieldEmu.setPreferredSize(new Dimension(200, 21));
    m_jTextFieldEmu.setMinimumSize(new Dimension(150, 21));
    m_jButtonEmuBrowse.setToolTipText("Choose your emulator");
    m_jButtonEmuBrowse.setActionCommand("emubrowse");
    m_jButtonEmuBrowse.setText("Browse");
    m_jLabelEmu.setText("Emu Path:  ");
    m_jLabelCreateRom.setText("Rom Name:");
    m_jTextFieldCreateRom.setText("");
    m_jTextFieldCreateRom.setPreferredSize(new Dimension(200, 21));
    m_jTextFieldCreateRom.setMinimumSize(new Dimension(150, 21));
    m_jButtonCreateRomBrowse.setToolTipText("Choose path and name for the GBA rom");
    m_jButtonCreateRomBrowse.setActionCommand("createrombrowse");
    m_jButtonCreateRomBrowse.setText("Browse");
    m_jMenuItemPuRemove.setActionCommand("remove");
    m_jMenuItemPuRemove.setText("Remove");
    m_jMenuItemPuRemove.setMnemonic( (int)'R');
    m_jMenuItemPuRemove.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "remove.gif"));
    m_jMenuItemPuProperties.setActionCommand("itemproperties");
    m_jMenuItemPuProperties.setText("Properties");
    m_jMenuItemPuProperties.setMnemonic( (int)'P');
    m_jMenuItemPuProperties.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "properties16.gif"));
    m_jMenuItemPuUp.setText( "Up");
    m_jMenuItemPuUp.setActionCommand( "up");
    m_jMenuItemPuUp.setMnemonic( (int)'U');
    m_jMenuItemPuUp.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "upbutakt.gif"));
    m_jMenuItemPuDown.setText( "Down");
    m_jMenuItemPuDown.setActionCommand( "down");
    m_jMenuItemPuDown.setMnemonic( (int)'D');
    m_jMenuItemPuDown.setIcon( ResouceManager.getCommonImageIcon( APPTITLE, "downbutakt.gif"));
    m_jMenuHelp.add( m_jMenuItemAbout);
    m_jMenuFile.add(m_jMenuItemAdd);
    m_jMenuFile.add(m_jMenuItemRemove);
    m_jMenuFile.add(m_jMenuItemCreate);
    m_jMenuFile.addSeparator();
    m_jMenuFile.add(m_jMenuItemExec);
    m_jMenuFile.addSeparator();
    m_jMenuFile.add(m_jMenuItemExit);
    m_jMenuEdit.add(m_jMenuItemProperties);
    m_jMenuEdit.add(m_jMenuItemUp);
    m_jMenuEdit.add(m_jMenuItemDown);
    m_jMenuBar.add(m_jMenuFile);
    m_jMenuBar.add(m_jMenuEdit);
    m_jMenuBar.add( m_jMenuHelp);
    this.getContentPane().add(m_jToolBar, BorderLayout.NORTH);
    m_jButtonAdd.setMaximumSize(new Dimension(20, 18));
    m_jButtonAdd.setMinimumSize(new Dimension(20, 18));
    m_jButtonAdd.setPreferredSize(new Dimension(20, 18));
    m_jButtonAdd.setToolTipText("Add SMS rom(s)");
    m_jButtonAdd.setActionCommand("add");
    m_jButtonAdd.setIcon( ResouceManager.getCommonImageIcon( APPTITLE,"add.gif"));
    m_jButtonRemove.setMaximumSize(new Dimension(20, 18));
    m_jButtonRemove.setMinimumSize(new Dimension(20, 18));
    m_jButtonRemove.setPreferredSize(new Dimension(20, 18));
    m_jButtonRemove.setToolTipText("Remove SMS rom(s)");
    m_jButtonRemove.setActionCommand("remove");
    m_jButtonRemove.setIcon( ResouceManager.getCommonImageIcon( APPTITLE,"remove.gif"));
    m_jButtonCreate.setMaximumSize(new Dimension(20, 18));
    m_jButtonCreate.setMinimumSize(new Dimension(20, 18));
    m_jButtonCreate.setPreferredSize(new Dimension(20, 18));
    m_jButtonCreate.setToolTipText("Create GBA rom");
    m_jButtonCreate.setActionCommand("createrom");
    m_jButtonCreate.setIcon( ResouceManager.getCommonImageIcon( APPTITLE,"create.gif"));
    m_jButtonExec.setMaximumSize(new Dimension(20, 18));
    m_jButtonExec.setMinimumSize(new Dimension(20, 18));
    m_jButtonExec.setPreferredSize(new Dimension(20, 18));
    m_jButtonExec.setToolTipText("Execute emulator");
    m_jButtonExec.setActionCommand("exec");
    m_jButtonExec.setIcon( ResouceManager.getCommonImageIcon( APPTITLE,"lightning16.gif"));
    m_jButtonProperties.setMaximumSize(new Dimension(20, 18));
    m_jButtonProperties.setMinimumSize(new Dimension(20, 18));
    m_jButtonProperties.setPreferredSize(new Dimension(20, 18));
    m_jButtonProperties.setToolTipText("SMS rom properties");
    m_jButtonProperties.setActionCommand("itemproperties");
    m_jButtonProperties.setIcon( ResouceManager.getCommonImageIcon( APPTITLE,"properties16.gif"));
    m_jButtonUp.setMaximumSize(new Dimension(20, 18));
    m_jButtonUp.setMinimumSize(new Dimension(20, 18));
    m_jButtonUp.setPreferredSize(new Dimension(20, 18));
    m_jButtonUp.setToolTipText("SMS rom move up");
    m_jButtonUp.setActionCommand("up");
    m_jButtonUp.setIcon( ResouceManager.getCommonImageIcon( APPTITLE,"upbutakt.gif"));
    m_jButtonDown.setMaximumSize(new Dimension(20, 18));
    m_jButtonDown.setMinimumSize(new Dimension(20, 18));
    m_jButtonDown.setPreferredSize(new Dimension(20, 18));
    m_jButtonDown.setToolTipText("SMS rom move down");
    m_jButtonDown.setActionCommand("down");
    m_jButtonDown.setIcon( ResouceManager.getCommonImageIcon( APPTITLE,"downbutakt.gif"));
    m_jButtonExit.setMaximumSize(new Dimension(20, 18));
    m_jButtonExit.setMinimumSize(new Dimension(20, 18));
    m_jButtonExit.setPreferredSize(new Dimension(20, 18));
    m_jButtonExit.setToolTipText("Exit");
    m_jButtonExit.setActionCommand("exit");
    m_jButtonExit.setIcon( ResouceManager.getCommonImageIcon( APPTITLE,"exit16.gif"));
    m_jToolBar.add( m_jButtonAdd);
    m_jToolBar.add( m_jButtonRemove);
    m_jToolBar.add( m_jButtonCreate);
    m_jToolBar.addSeparator();
    m_jToolBar.add( m_jButtonProperties);
    m_jToolBar.add( m_jButtonUp);
    m_jToolBar.add( m_jButtonDown);
    m_jToolBar.addSeparator();
    m_jToolBar.add( m_jButtonExec);
    m_jToolBar.addSeparator();
    m_jToolBar.add( m_jButtonExit);
    m_jLabelDrsmsRom.setText("DrSMS Rom:");
    m_jTextFieldDrsmsRom.setMinimumSize(new Dimension(150, 21));
    m_jTextFieldDrsmsRom.setPreferredSize(new Dimension(200, 21));
    m_jTextFieldDrsmsRom.setText("");
    m_jPanelDrsmsRom.setLayout(m_borderLayoutDrsmsRom);
    m_jButtonDrsmsRom.setToolTipText("Browse for DrSMS rom");
    m_jButtonDrsmsRom.setActionCommand("drsmsrom");
    m_jButtonDrsmsRom.setText("Browse");
    m_jPanelMain.setLayout(m_borderLayoutMain);
    m_jPanelCreateRom.setLayout(m_borderLayoutCreateRom);
    this.getContentPane().add(m_jPanelMain,  BorderLayout.CENTER);
    m_jPanelEmu.setLayout(m_borderLayoutEmu);
    m_jPanelDrsmsRom.add(m_jLabelDrsmsRom, BorderLayout.WEST);
    m_jPanelDrsmsRom.add(m_jTextFieldDrsmsRom, BorderLayout.CENTER);
    m_jPanelDrsmsRom.add(m_jButtonDrsmsRom, BorderLayout.EAST);
    m_jPanelMain.add(m_jPanelDrsmsRom,  BorderLayout.NORTH);
    m_jPanelMain.add(m_jPanelCreateRom,  BorderLayout.SOUTH);
    m_jPanelCreateRom.add(m_jLabelCreateRom, BorderLayout.WEST);
    m_jPanelCreateRom.add(m_jTextFieldCreateRom, BorderLayout.CENTER);
    m_jPanelCreateRom.add(m_jButtonCreateRomBrowse,  BorderLayout.EAST);
    m_jPanelMain.add(m_jScrollPaneRoms,  BorderLayout.CENTER);
    m_jScrollPaneRoms.getViewport().add(m_jListRoms, null);
    m_jPanelEmu.add(m_jTextFieldEmu, BorderLayout.CENTER);
    m_jPanelEmu.add(m_jButtonEmuBrowse,  BorderLayout.EAST);
    m_jPanelEmu.add(m_jLabelEmu,  BorderLayout.WEST);
    this.getContentPane().add(m_jPanelEmu,  BorderLayout.SOUTH);
    m_popup.add(m_jMenuItemPuProperties);
    m_popup.add(m_jMenuItemPuUp);
    m_popup.add(m_jMenuItemPuDown);
    m_popup.add(m_jMenuItemPuRemove);
    m_jListRoms.add( m_popup);
  }

  protected void _init()
  {
    File file = new File( ConfigPathUtil.getCurrentReadPath( DRSMSROM, PROPSFILE));
    if( !file.exists())
    {
      m_vecSmsRoms = new Vector();
      return;
    }
    m_appProps.load( ConfigPathUtil.getCurrentReadPath( DRSMSROM, PROPSFILE));

    m_strDrsms = m_appProps.getProperty( DRSMSROM);
    if( m_strDrsms != null && !m_strDrsms.equals( ""))
      m_jTextFieldDrsmsRom.setText( m_strDrsms);

    m_strRomName = m_appProps.getProperty( SMSROMNAME);
    if( m_strRomName != null && !m_strRomName.equals( ""))
      m_jTextFieldCreateRom.setText( m_strRomName);

    m_strEmuPath = m_appProps.getProperty( EMUPATH);
    if( m_strEmuPath != null && !m_strEmuPath.equals( ""))
      m_jTextFieldEmu.setText( m_strEmuPath);

    m_strSmsRomsPath = m_appProps.getProperty( SMSROMSPATH);

    String strPaths = m_appProps.getProperty( SMSROMPATHS);
    String strNames = m_appProps.getProperty( SMSROMNAMES);
    if( strPaths != null && strNames != null)
    {
      StringTokenizer stPath = new StringTokenizer( strPaths, ",", false );
      StringTokenizer stName = new StringTokenizer( strNames, ",", false );
      String strPath;
      String strName;

      try
      {
        if( stPath.countTokens() == stName.countTokens() )
        {
          m_vecSmsRoms = new Vector();
          SmsRom smsrom;
          while( stPath.hasMoreTokens() && stName.hasMoreTokens() )
          {
            strPath = stPath.nextToken();
            strName = stName.nextToken();
            FileInterface fileInt = FileConnector.createFileInterface( strPath, null, false, null, APPTITLE, m_appProps );
            smsrom = new SmsRom( fileInt );
            smsrom.setSmsName( strName );
            m_vecSmsRoms.add( smsrom );
          }
        }
      }
      catch( Exception ex)
      {
        //ex.printStackTrace();
        m_vecSmsRoms = new Vector();
      }
    }
    //m_vecSmsRoms = (Vector)m_appProps.getObjectProperty( SMSROMS);
    if( m_vecSmsRoms == null)
      m_vecSmsRoms = new Vector();
    else if( m_vecSmsRoms.size() > 0)
      m_jListRoms.setListData( m_vecSmsRoms);

    // Set stored dimension
    Integer wx = m_appProps.getIntegerProperty( WLOCX );
    Integer wy = m_appProps.getIntegerProperty( WLOCY );
    Integer wwidth = m_appProps.getIntegerProperty( WWIDTH );
    Integer wheight = m_appProps.getIntegerProperty( WHEIGHT );

    if( wx != null && wy != null )
      setLocation( wx.intValue(), wy.intValue() );

    if( wwidth != null && wheight != null )
      setSize( wwidth.intValue(), wheight.intValue() );
  }

  protected void _save()
  {
    File file = new File( ConfigPathUtil.getCurrentSavePath( DRSMSROM, PROPSFILE));
    if( !file.exists())
    {
      try
      {
        if( !file.createNewFile())
          return;
      }
      catch( IOException ioex)
      {
        ioex.printStackTrace();
        return;
      }
    }
    m_appProps.remove( DRSMSROM);
    m_appProps.remove( SMSROMNAME);
    m_appProps.remove( SMSROMSPATH);
    m_appProps.remove( EMUPATH);
    m_appProps.remove( SMSROMPATHS);
    m_appProps.remove( SMSROMNAMES);

    m_strDrsms = m_jTextFieldDrsmsRom.getText();
    if( m_strDrsms != null && !m_strDrsms.equals( ""))
      m_appProps.setProperty( DRSMSROM, m_strDrsms);

    m_strRomName = m_jTextFieldCreateRom.getText();
    if( m_strRomName != null && !m_strRomName.equals( ""))
      m_appProps.setProperty( SMSROMNAME, m_strRomName);

    m_strEmuPath = m_jTextFieldEmu.getText();
    if( m_strEmuPath != null && !m_strEmuPath.equals( ""))
      m_appProps.setProperty( EMUPATH, m_strEmuPath);

    if( m_strSmsRomsPath != null && !m_strSmsRomsPath.equals( ""))
      m_appProps.setProperty( SMSROMSPATH, m_strSmsRomsPath);

    if( m_vecSmsRoms != null && m_vecSmsRoms.size() > 0)
    {
      String strPath = null;
      String strName = null;
      for( int i = 0; i < m_vecSmsRoms.size(); ++i)
      {
        SmsRom sr = (SmsRom)m_vecSmsRoms.elementAt( i);
        if( i == 0)
        {
          strPath = sr.getSmsPath();
          strName = sr.getSmsName();
        }
        else
        {
          strPath += "," + sr.getSmsPath();
          strName += "," + sr.getSmsName();
        }
      }
      if( strPath != null && strName != null)
      {
        m_appProps.setProperty( SMSROMPATHS, strPath);
        m_appProps.setProperty( SMSROMNAMES, strName);
      }
      //m_appProps.setObjectProperty( SMSROMS, m_vecSmsRoms);
    }

    // Window location and dimension
    m_appProps.setIntegerProperty( WLOCX, new Integer( getX() ) );
    m_appProps.setIntegerProperty( WLOCY, new Integer( getY() ) );
    m_appProps.setIntegerProperty( WWIDTH, new Integer( getWidth() ) );
    m_appProps.setIntegerProperty( WHEIGHT, new Integer( getHeight() ) );

    m_appProps.store( ConfigPathUtil.getCurrentSavePath( DRSMSROM, PROPSFILE), PROPSHEADER);
  }

  class AppAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "drsmsrom")
        onDrsmsRom();
      else if (cmd == "add")
        onAdd();
      else if (cmd == "remove")
        onRemove();
      else if (cmd == "createrom")
        onCreateRom();
      else if (cmd == "createrombrowse")
        onCreateRomBrowse();
      else if (cmd == "emubrowse")
        onEmuBrowse();
      else if (cmd == "itemproperties")
        onSmsProperties();
      else if (cmd == "up")
        onUp();
      else if (cmd == "down")
        onDown();
      else if (cmd == "exec")
        onExec();
      else if (cmd == "exit")
        onExit();
      else if( cmd == "about")
        onAbout();
    }
  }

  protected void onAdd()
  {
    File filePath = null;
    if( m_strSmsRomsPath != null && !m_strSmsRomsPath.equals( ""))
    {
      filePath = new File( m_strSmsRomsPath);
      filePath = filePath.getParentFile();
    }
    MultiFileFilter mffSms = new MultiFileFilter( SMSFILTER);
    JFileChooser fc = new JFileChooser( filePath);
    fc.setFileFilter( mffSms);
    fc.setMultiSelectionEnabled( true);
    fc.showOpenDialog( this);
    File[] files = fc.getSelectedFiles();
    if( files != null && Array.getLength( files) > 0)
    {
      /*
      if( files[0].isFile())
        m_strSmsRomsPath = files[0].getParentFile().getAbsolutePath();
      else if( files[0].isDirectory())
      */
      m_strSmsRomsPath = files[0].getAbsolutePath();
      int iLen = Array.getLength( files);
      for( int i = 0; i < iLen; ++i)
      {
        FileInterface file = FileConnector.createFileInterface( files[i].getAbsolutePath(), null, false, null, APPTITLE, m_appProps);
        if( file.isArchive())
        {
          if( file.getName().lastIndexOf( FileConnector.SUPPORTEDFILES[4]) == -1
              && file.getName().lastIndexOf( FileConnector.SUPPORTEDFILES[6]) == -1)
            extractRoms( file );
          else
          {
            file = file._listFiles()[0];
            file = FileConnector.createFileInterface( file.getAbsolutePath(), null, true, null, APPTITLE, m_appProps);
            extractRoms( file );
          }
        }
        else if( file.isFile())
          m_vecSmsRoms.add( new SmsRom( file));
      }
      m_jListRoms.setListData( m_vecSmsRoms);
    }
  }

  protected void extractRoms( FileInterface file)
  {
    MultiFileInterfaceFilter mfifSms = new MultiFileInterfaceFilter( DRSMSFILTERS, APPTITLE);
    FileInterface[] files = file._listFiles( mfifSms);

    for( int i = 0; i < files.length; ++i)
    {
      if( files[i].isArchive() || files[i].isDirectory())
        extractRoms( files[i]);
      else if( files[i].isFile())
        m_vecSmsRoms.add( new SmsRom( files[i]));
    }
  }

  protected void onSmsProperties()
  {
    int iIdx = m_jListRoms.getSelectedIndex();
    if( iIdx < 0)
    {
      JOptionPane.showMessageDialog( AppProperties.getRootComponent( APPTITLE)
                                     , "No SMS rom selected!"
                                     , "Error", JOptionPane.ERROR_MESSAGE );
      return;
    }
    SmsRom smsRom = (SmsRom)m_vecSmsRoms.elementAt( iIdx);
    SmsRomPropertiesDialog dlg = new SmsRomPropertiesDialog( this, "SMS Rom Properties", true, smsRom);
    dlg.setVisible( true);
    dlg.dispose();
    m_jListRoms.setListData( m_vecSmsRoms);
  }

  protected void onUp()
  {
    int iIdx = m_jListRoms.getSelectedIndex();
    if( iIdx < 0)
    {
      JOptionPane.showMessageDialog( AppProperties.getRootComponent( APPTITLE)
                                     , "No SMS rom selected!"
                                     , "Error", JOptionPane.ERROR_MESSAGE );
      return;
    }
    SmsRom smsRom = (SmsRom)m_vecSmsRoms.elementAt( iIdx);
    if( iIdx > 0)
    {
      m_vecSmsRoms.remove( iIdx);
      m_vecSmsRoms.insertElementAt( smsRom, iIdx-1);
      m_jListRoms.setListData( m_vecSmsRoms);
      m_jListRoms.setSelectedIndex( iIdx-1);
    }
  }

  protected void onDown()
  {
    int iIdx = m_jListRoms.getSelectedIndex();
    if( iIdx < 0)
    {
      JOptionPane.showMessageDialog( AppProperties.getRootComponent( APPTITLE)
                                     , "No SMS rom selected!"
                                     , "Error", JOptionPane.ERROR_MESSAGE );
      return;
    }
    SmsRom smsRom = (SmsRom)m_vecSmsRoms.elementAt( iIdx);
    if( iIdx < m_vecSmsRoms.size()-1)
    {
      m_vecSmsRoms.remove( iIdx);
      m_vecSmsRoms.insertElementAt( smsRom, iIdx+1);
      m_jListRoms.setListData( m_vecSmsRoms);
      m_jListRoms.setSelectedIndex( iIdx+1);
    }
  }

  protected void onRemove()
  {
    int[] iIdx = m_jListRoms.getSelectedIndices();
    if( iIdx != null)
    {
      int iLen = Array.getLength( iIdx);
      for( int i = iLen-1; i >= 0; --i)
      {
        m_vecSmsRoms.removeElementAt( iIdx[i]);
      }
      m_jListRoms.setListData( m_vecSmsRoms);
    }
  }

  protected void onCreateRom()
  {
    if( m_strRomName != null && !m_strRomName.equals( "") && m_strDrsms != null
        && !m_strDrsms.equals( "") && m_vecSmsRoms.size() > 0)
    {
      try
      {
        File file = new File( m_strRomName );
        File fileDrsms = new File( m_strDrsms );
        if( !fileDrsms.exists() )
        {
          JOptionPane.showMessageDialog( AppProperties.getRootComponent( APPTITLE)
                                         , "DrSMS rom file dosn't exists!"
                                         , "Error", JOptionPane.ERROR_MESSAGE );
          return;
        }
        if( file.exists() )
        {
          int iRet = JOptionPane.showConfirmDialog( AppProperties.getRootComponent( APPTITLE)
            , "Rom file already exists, overwrite anyway?"
            , "Alert", JOptionPane.YES_NO_OPTION
            , JOptionPane.QUESTION_MESSAGE );
          if( iRet == JOptionPane.NO_OPTION )
          {
            AppProperties.getPrintStreamOutput( APPTITLE).println( "...cancelled");
            //System.out.println( "...cancelled" );
            return;
          }
        }
        else
        {
          file.createNewFile();
        }
        setCursor( new Cursor( Cursor.WAIT_CURSOR));
        BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( file));
        BufferedInputStream bis = new BufferedInputStream( new FileInputStream( fileDrsms));
        byte buf[] = new byte[256];
        int iLen;

        // write DrSMS rom
        while( ( iLen = bis.read( buf, 0, 256)) > 0)
        {
          bos.write( buf, 0, iLen);
        }
        bis.close();
        // write sms rom count
        bos.write( NumberConverter.toByteArray( m_vecSmsRoms.size()));
        // write sms rom sizes
        for( int i = 0; i < m_vecSmsRoms.size(); ++i)
        {
          SmsRom smsrom = (SmsRom)m_vecSmsRoms.elementAt( i);
          bos.write( smsrom.sizeBytes());
        }
        // write sms or gg flag
        for( int i = 0; i < m_vecSmsRoms.size(); ++i)
        {
          SmsRom smsrom = (SmsRom)m_vecSmsRoms.elementAt( i);
          bos.write( smsrom.getTypeFlag());
        }
        // write sms rom names
        for( int i = 0; i < m_vecSmsRoms.size(); ++i)
        {
          SmsRom smsrom = (SmsRom)m_vecSmsRoms.elementAt( i);
          bos.write( smsrom.getName());
        }
        // write sms roms
        for( int i = 0; i < m_vecSmsRoms.size(); ++i)
        {
          SmsRom smsrom = (SmsRom)m_vecSmsRoms.elementAt( i);
          bis = smsrom.getBufferedInputStream();
          if( bis != null)
          {
            while( ( iLen = bis.read( buf, 0, NAMELENGTH)) > 0)
            {
              bos.write( buf, 0, iLen);
            }
            bis.close();
          }
        }
        bos.flush();
        bos.close();
      }
      catch( IOException ioex)
      {
        ioex.printStackTrace();
      }
      setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
    }
  }

  protected void onDrsmsRom()
  {
    File file = null;
    if( m_strDrsms != null && !m_strDrsms.equals( ""))
    {
      file = new File( m_strDrsms);
      file = file.getParentFile();
    }
    MultiFileFilter mffGba = new MultiFileFilter( GBAFILTER);
    JFileChooser fc = new JFileChooser( file);
    fc.setFileFilter( mffGba);
    fc.showOpenDialog( this);
    file = fc.getSelectedFile();
    if( file != null)
    {
      m_strDrsms = file.getAbsolutePath();
      m_jTextFieldDrsmsRom.setText( m_strDrsms);
    }
  }

  protected void onCreateRomBrowse()
  {
    File file = null;
    if( m_strRomName != null && !m_strRomName.equals( ""))
    {
      file = new File( m_strRomName);
      file = file.getParentFile();
    }
    MultiFileFilter mffGba = new MultiFileFilter( GBAFILTER);
    JFileChooser fc = new JFileChooser( file);
    fc.setFileFilter( mffGba);
    fc.showOpenDialog( this);
    file = fc.getSelectedFile();
    if( file != null)
    {
      m_strRomName = file.getAbsolutePath();
      m_jTextFieldCreateRom.setText( m_strRomName);
    }
  }

  protected void onEmuBrowse()
  {
    File file = null;
    if( m_strEmuPath != null && !m_strEmuPath.equals( ""))
    {
      file = new File( m_strEmuPath);
      file = file.getParentFile();
    }
    MultiFileFilter mffEmu = new MultiFileFilter( EMUFILTER);
    JFileChooser fc = new JFileChooser( file);
    fc.setFileFilter( mffEmu);
    fc.showOpenDialog( this);
    file = fc.getSelectedFile();
    if( file != null)
    {
      m_strEmuPath = file.getAbsolutePath();
      m_jTextFieldEmu.setText( m_strEmuPath);
    }
  }

  protected void onExec()
  {
    if( m_strEmuPath != null && !m_strEmuPath.equals( "") && m_strRomName != null && !m_strRomName.equals( ""))
    {
      try
      {
        String strCmd = m_strEmuPath + " " + m_strRomName;
        FileInterface fi = FileConnector.createFileInterface( m_strEmuPath, null, false, null, APPTITLE, null );
        fi.exec( strCmd, null, AppProperties.getPrintStreamOutput( APPTITLE),
                 AppProperties.getPrintStreamError( APPTITLE), null );
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  class AdpWindow extends WindowAdapter
  {
    public void windowClosing( WindowEvent event)
    {
      Object object = event.getSource();
      if( object == Sms2Gba.this)
      {
        onExit();
      }
    }
  }

  protected void onExit()
  {
    setVisible(false);	// hide the Frame
    _save();
    dispose();
    System.exit(0);			// close the application
  }

  protected void onAbout()
  {
    m_aboutDlg = new AboutDialog( this, "About", true, APPTITLE, "DrSMS Rom Injector"
                                  , "&copy; 2003  by M.o.E (sms2gba@moe99.cjb.net)<BR>Version " + VERSION
                                  , new ImageIcon( getClass().getResource( "splash.gif")), Sms2Gba.APPTITLE);
    m_aboutDlg.setVisible( true);
    m_aboutDlg.dispose();
    m_aboutDlg = null;
  }

  public static void main( String[] args )
  {
    Sms2Gba sms2gba1 = new Sms2Gba();
    sms2gba1.setVisible( true );
  }

  public class SmsRom
    implements Serializable
  {
    // constants
    public static final int SMSTYPE = 0;
    public static final int GGTYPE = 1;

    // member variables
    protected String m_strSmsPath;
    protected String m_strSmsName;
    protected FileInterface m_file;

    public SmsRom()
    {
      m_strSmsPath = null;
      m_strSmsName = null;
    }

    public SmsRom( FileInterface file)
    {
      m_strSmsPath = file.getAbsolutePath();
      m_strSmsName = file.getName();
      int iIdx = m_strSmsName.lastIndexOf( '.');
      if( iIdx > -1)
        m_strSmsName = m_strSmsName.substring( 0, iIdx);
    }

    public BufferedInputStream getBufferedInputStream()
    {
      BufferedInputStream bis = null;
      try
      {
        bis = m_file.getBufferedInputStream();
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
      return bis;
    }

    public int size()
    {
      int iRet = -1;
      if( m_file == null)
      {
        m_file = FileConnector.createFileInterface( m_strSmsPath, null, false, null, APPTITLE, m_appProps );
      }

      iRet = (int)m_file.length();
      return iRet;
    }

    public byte[] getTypeFlag()
    {
      int iType = SMSTYPE;

      if( getSmsName().toLowerCase().endsWith( "." + PUREGGFILTER))
        iType = GGTYPE;

      return NumberConverter.toByteArray( iType);
    }

    public byte[] sizeBytes()
    {
      return NumberConverter.toByteArray( size());
    }

    public byte[] getName()
    {
      byte[] bRet = null;
      int iLen = m_strSmsName.length();
      if( iLen == NAMELENGTH)
        bRet = m_strSmsName.getBytes();
      else if( iLen > NAMELENGTH)
        bRet = m_strSmsName.substring( 0, NAMELENGTH).getBytes();
      else
      {
        bRet = new byte[NAMELENGTH];
        byte[] b = m_strSmsName.getBytes();
        for( int i = 0; i < NAMELENGTH; ++i)
        {
          if( i < iLen)
            bRet[i] = b[i];
          else
            bRet[i] = 0;
        }
      }
      return bRet;
    }

    public void setSmsPath( String strSmsPath)
    {
      m_strSmsPath = strSmsPath;
    }

    public String getSmsPath()
    {
      return m_strSmsPath;
    }

    public void setSmsName( String strSmsName)
    {
      m_strSmsName = strSmsName;
    }

    public String getSmsName()
    {
      return m_strSmsName;
    }

    public String toString()
    {
      return getSmsName();
    }

    private void writeObject(java.io.ObjectOutputStream out)
      throws IOException
    {
      out.defaultWriteObject();

      out.writeObject( getSmsName());
      out.writeObject( getSmsPath());
    }

    private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException
    {
      in.defaultReadObject();

      setSmsName( (String)in.readObject());
      setSmsPath( (String)in.readObject());
    }
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 2)
        rightMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 1)
        rightMousePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON2_MASK && event.getClickCount() == 2)
        middleMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON2_MASK && event.getClickCount() == 1)
        middleMousePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2)
        leftMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 1)
        leftMousePressed( event);
    }

    public void mouseReleased(MouseEvent event)
    {
      Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON1_MASK)
        leftMouseReleased( event);
      else if( (event.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && ((event.getModifiers() & InputEvent.SHIFT_MASK) != 0
              || (event.getModifiers() & InputEvent.CTRL_MASK) != 0))
        leftMouseReleased( event);
    }
  }

  void leftMousePressed( MouseEvent event)
  {
  }

  void leftMouseDoublePressed( MouseEvent event)
  {
  }

  void middleMousePressed( MouseEvent event)
  {
    setSelectionAtMousePos( event.getX(), event.getY());
  }

  void middleMouseDoublePressed( MouseEvent event)
  {
    setSelectionAtMousePos( event.getX(), event.getY());
  }

  void rightMousePressed( MouseEvent event)
  {
    setSelectionAtMousePos( event.getX(), event.getY());
    showPopup( event.getX(), event.getY());
  }

  void rightMouseDoublePressed( MouseEvent event)
  {
    setSelectionAtMousePos( event.getX(), event.getY());
  }

  void leftMouseReleased( MouseEvent event)
  {
  }

  protected void setSelectionAtMousePos( int x, int y)
  {
    int idx = m_jListRoms.locationToIndex( new Point( x, y));
    if( idx != -1 && idx < m_jListRoms.getModel().getSize())
    {
      m_jListRoms.setSelectedIndex( idx);
    }
  }

  public void showPopup( int x, int y)
  {
    if( m_popup != null)
    {
      m_popup.show( m_jListRoms, x, y);
      m_popup.setVisible( true);
    }
  }

  // static initializer for setting look & feel
  static
  {
    try
    {
      AppProperties.addAppClass( APPTITLE, Class.forName( "haui.app.sms2gba.Sms2Gba" ) );
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch( Exception e )
    {}
  }
}