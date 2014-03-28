package haui.app.fm;

import haui.app.external.jdiff.Diff;
import haui.app.filesync.FileSyncPanel;
import haui.components.CommPortTypeServerDialog;
import haui.components.JExDialog;
import haui.components.SocketTypeServerDialog;
import haui.components.shelltextarea.ShellTextArea;
import haui.io.FileConnector;
import haui.io.FileInterface.CommPortTypeServer;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.NormalFile;
import haui.io.FileInterface.SocketTypeServer;
import haui.io.FileInterface.configuration.CommPortTypeServerFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.GeneralFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.SocketTypeServerFileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.resource.ResouceManager;
import haui.tool.SplashDlg;
import haui.tool.shell.JShell;
import haui.tool.shell.engine.JShellEngine;
import haui.util.AppProperties;
import haui.util.CommandClass;
import haui.util.ConfigPathUtil;
import haui.util.GlobalApplicationContext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Module: FileManager.java<br>
 * $Source:
 * M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\
 * FileManager.java,v $
 * <p>
 * Description: JApplet (Application) to manage files and directories local
 * and/or on a remote machine via a cgi script, a ftp connection, ip-socket
 * connection, ...<br>
 * </p>
 * <p>
 * Created: 10.11.2000 by AE
 * </p>
 * <p>
 * 
 * @history 10.11.2000 by AE: Created.<br>
 *          </p>
 *          <p>
 *          Modification:<br>
 *          $Log: FileManager.java,v $ Revision 1.5 2004-08-31 16:03:01+02
 *          t026843 Large redesign for application dependent outputstreams,
 *          mainframes, AppProperties! Bugfixes to DbTreeTableView, additional
 *          features for jDirWork. Revision 1.4 2004-06-22 14:08:49+02 t026843
 *          bigger changes Revision 1.3 2003-06-06 10:04:00+02 t026843
 *          modifications because of the moving the 'TypeFile's to haui.io
 *          package Revision 1.2 2003-06-04 15:35:57+02 t026843 bugfixes
 *          Revision 1.1 2003-05-28 14:19:45+02 t026843 reorganisations Revision
 *          1.0 2003-05-21 16:25:48+02 t026843 Initial revision Revision 1.11
 *          2003-02-26 09:24:03+01 t026843 Changes due to the changes in
 *          SplashDlg Revision 1.10 2002-09-27 15:28:47+02 t026843 Dialogs
 *          extended from JExDialog Revision 1.9 2002-09-18 11:16:21+02 t026843
 *          - changes to fit extended filemanager.pl - logon and logoff moved to
 *          'TypeFile's - startTerminal() added to 'TypeFile's, but only
 *          CgiTypeFile (until now) starts the LRShell as terminal - LRShell
 *          changed to work with filemanager.pl Revision 1.8 2002-09-03
 *          17:08:00+02 t026843 - CgiTypeFile is now full functional. - Migrated
 *          to the extended filemanager.pl script. Revision 1.7 2002-08-07
 *          15:25:25+02 t026843 Ftp support via filetype added. Some bugfixes.
 *          Revision 1.6 2002-06-27 16:44:17+02 t026843 saves current dir and
 *          connection on exit Revision 1.5 2002-06-26 12:06:46+02 t026843
 *          History extended, simple bookmark system added. Revision 1.4
 *          2002-06-17 17:19:19+02 t026843 Zip and Jar filetype read only
 *          support. Revision 1.3 2002-06-11 09:32:52+02 t026843 bugfixes
 *          Revision 1.2 2002-06-06 16:06:07+02 t026843 bugfix with starter menu
 *          Revision 1.1 2002-05-29 11:18:15+02 t026843 Added: - starter menu -
 *          config starter dialog - file extensions configurable for right,
 *          middle and left mouse button Changed: - icons minimized - changed
 *          layout of file ex. and button cmd config dialog - output area
 *          hideable - other minor changes bugfixes: - some minor bugfixes
 *          Revision 1.0 2001-07-20 16:34:24+02 t026843 Initial revision
 *          </p>
 *          <p>
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2002; $Revision: 1.5 $<br>
 *          $Header:
 *          M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\
 *          app\\fm\\FileManager.java,v 1.5 2004-08-31 16:03:01+02 t026843 Exp
 *          t026843 $
 *          </p>
 *          <p>
 * @since JDK1.2
 *        </p>
 */
public class FileManager extends JApplet {
	private static final long serialVersionUID = -5336213527966914967L;

	// property constants
	public final static String APPNAME = "jDirWork";
	public final static String PROPFILENAME = "app.pps";
	public final static String DIMFILENAME = "dim.pps";
	public final static String PROPFILEHEADER = "FileManager application properties";
	public final static String DIMFILEHEADER = "FileManager dimension and position properties";
	public final static String STARTER = "Starter.";
	public final static String DIMLOCX = "LocX";
	public final static String DIMLOCY = "LocY";
	public final static String DIMWIDTH = "Width";
	public final static String DIMHEIGHT = "Height";
	public final static String DIMOUTVIS = "OutputVis";
	public final static String LEFTDIR = "LeftDir";
	public final static String LEFTCONNID = "LeftConnId";
	public final static String RIGHTDIR = "RightDir";
	public final static String RIGHTCONNID = "RightConnId";
	public final static String BOOKMARK = "Bookmark";

	// static member variables
	static public AppProperties m_appProps = new AppProperties();

	// member variables
	boolean isStandalone = false;
	// OutputThread m_ot;
	List<BookmarkInfo> m_vecBookmarks = new ArrayList<>();
	// AppProperties m_appProps = new AppProperties();
	AppProperties m_dimProps = new AppProperties();
	List<CommandClass> m_starterCmd = new ArrayList<>();
	List<JMenuItem> m_starterMenuItems = new ArrayList<>();
	HashMap<String, JMenuItem> m_bookmarkMenuItems = new HashMap<>();
	HashMap<String, JMenu> m_bookmarkSubMenuItems = new HashMap<>();

	// JShell member variable
	ShellTextArea m_jTextAreaOut = new ShellTextArea(APPNAME);
	JShell m_jshell = new JShell(APPNAME);

	// GUI member variables
	BorderLayout m_borderLayoutBase = new BorderLayout();
	JDialog m_outDlg;
	PropertyDialog m_propsDlg;
	// JTextArea m_jTextAreaOutLarge = new JTextArea();
	// JTextArea m_jTextAreaOut = new JTextArea();
	JMenuBar m_jMenuBar = new JMenuBar();
	JMenu m_jMenuFile = new JMenu();
	JMenuItem m_jMenuItemExit = new JMenuItem();
	JMenu m_jMenuView = new JMenu();
	JMenuItem m_jMenuItemClear = new JMenuItem();
	JMenuItem m_jMenuItemLarge = new JMenuItem();
	JMenuItem m_jMenuItemOutput = new JMenuItem();
	JMenu m_jMenuExtra = new JMenu();
	JToolBar m_jToolBar = new JToolBar();
	JPanel m_jPanelMainView = new JPanel();
	FileInfoPanel m_jPanelFileViewLeft = null;
	FileInfoPanel m_jPanelFileViewRight = null;
	JPanel m_jPanelFileView = new JPanel();
	GridLayout gridLayoutFileView = new GridLayout();
	BorderLayout m_borderLayoutMainView = new BorderLayout();
	JScrollPane m_jScrollPaneOut = new JScrollPane();
	JScrollPane m_jScrollPaneOutLarge = new JScrollPane();
	JPanel m_jPanelOut = new JPanel();
	BorderLayout m_borderLayoutOut = new BorderLayout();
	JPanel m_jPanelOutHead = new JPanel();
	FlowLayout m_flowLayoutOutHead = new FlowLayout();
	JButton m_jButtonOutClear = new JButton();
	JButton m_jButtonOutLarge = new JButton();
	JButton m_jButtonOutput = new JButton();
	JButton m_jButtonProps = new JButton();
	JButton m_jButtonExit = new JButton();
	JMenuItem m_jMenuItemSave = new JMenuItem();
	JMenuItem m_jMenuItemProps = new JMenuItem();
	// JCheckBoxMenuItem m_jCheckBoxMenuItemAutoLogon = new JCheckBoxMenuItem();
	// JCheckBoxMenuItem m_jCheckBoxMenuItemDiff = new JCheckBoxMenuItem();
	JMenuItem m_jMenuItemConfigStarter = new JMenuItem();
	JMenu m_jMenuStarter = new JMenu();
	JSeparator m_starterSeparator = new JSeparator();
	LisAction m_actionlistener;
	SplashDlg m_sd = null;
	JMenu m_jMenuConnect = new JMenu();
	JButton m_jButtonConfigAll = new JButton();
	JButton m_jButtonConfig = new JButton();
	JButton m_jButtonHistLeft = new JButton();
	JButton m_jButtonHistRight = new JButton();
	JButton m_jButtonCopy = new JButton();
	JButton m_jButtonMove = new JButton();
	JButton m_jButtonMkDir = new JButton();
	JButton m_jButtonDelete = new JButton();
	JButton m_jButtonRefresh = new JButton();
	JButton m_jButtonTerm = new JButton();
	JButton m_jButtonBsh = new JButton();
	JMenu m_jMenuServer = new JMenu();
	JMenuItem m_jMenuItemServerSocket = new JMenuItem();
	JMenu m_jMenuClient = new JMenu();
	JMenuItem m_jMenuItemClientSocket = new JMenuItem();
	JMenuItem m_jMenuItemClientCommPort = new JMenuItem();
	JMenuItem m_jMenuItemRemote = new JMenuItem();
	JMenuItem m_jMenuItemServerCommPort = new JMenuItem();
	JMenu m_jMenuBookmark = new JMenu();
	JMenuItem m_jMenuItemAddBookmark = new JMenuItem();
	JMenuItem m_jMenuItemEditBookmark = new JMenuItem();
	JMenuItem m_jMenuItemConfigAll = new JMenuItem();
	JMenuItem m_jMenuItemConfig = new JMenuItem();
	JMenuItem m_jMenuItemCopy = new JMenuItem();
	JMenuItem m_jMenuItemMove = new JMenuItem();
	JMenuItem m_jMenuItemMkDir = new JMenuItem();
	JMenuItem m_jMenuItemDelete = new JMenuItem();
	JMenuItem m_jMenuItemRefresh = new JMenuItem();
	JMenuItem m_jMenuItemTerm = new JMenuItem();
	JMenuItem m_jMenuItemAll = new JMenuItem();
	JMenuItem m_jMenuItemFilter = new JMenuItem();
	JMenuItem m_jMenuItemSearch = new JMenuItem();
	JMenuItem m_jMenuItemCompare = new JMenuItem();
	JMenuItem m_jMenuItemCalcSize = new JMenuItem();
	JMenu m_jMenuNewConnect = new JMenu();
	JMenuItem m_jMenuItemNewCgi = new JMenuItem();
	JMenuItem m_jMenuItemNewFtp = new JMenuItem();
	JMenuItem m_jMenuItemDisconnect = new JMenuItem();
	JMenuItem m_jMenuItemSync = new JMenuItem();

	// Das Applet konstruieren
	public FileManager() {
		ConfigPathUtil.init(APPNAME);
		try {
			m_sd = new SplashDlg(APPNAME, APPNAME
					+ ".\n\nVersion 0.1 (alpha) by A.Eisenhauer",
					new ImageIcon(this.getClass().getResource("splash.gif")));
			m_sd.setHeadFont(new Font("DialogInput", Font.BOLD | Font.ITALIC,
					24));
			m_sd.setTextFont(new Font("Dialog", Font.ITALIC, 12));
			m_sd.setSize(370, 120);
			m_sd.setVisible(true);
		} catch (Exception ex) {
			m_sd = null;
		}
	}

	static public AppProperties getAppPropperties() {
		return m_appProps;
	}

	// Das Applet initialisieren
	public void init() {
		try {
			// m_ot = new OutputThread( m_jTextAreaOut, true, true);
			// m_ot.start();
			JShell.setMultiAppMode(true);
			m_jshell.getShellEnv().setStreamContainer(
					m_jTextAreaOut.getStreamContainer());
			m_jPanelFileViewLeft = new FileInfoPanel(m_jshell, false,
					m_appProps);
			m_jPanelFileViewRight = new FileInfoPanel(m_jshell, true,
					m_appProps);
			m_jPanelFileViewLeft.setActive();
			m_jPanelFileViewRight.setInactive();
			// m_jshell.init();
			jbInit();
			ButtonGroup bg = new ButtonGroup();
			bg.add(m_jMenuItemAll);
			bg.add(m_jMenuItemFilter);
			GlobalApplicationContext.instance().setOutputPrintStream(
					m_jshell.getOut());
			GlobalApplicationContext.instance().setErrorPrintStream(
					m_jshell.getOut());
		} catch (Exception e) {
			e.printStackTrace(m_jshell.getShellEnv().getErr());
		}
		m_jPanelFileViewLeft.setTargetFileInfoTable(m_jPanelFileViewRight);
		m_jPanelFileViewRight.setTargetFileInfoTable(m_jPanelFileViewLeft);

		m_actionlistener = new LisAction();
		m_jButtonOutClear.addActionListener(m_actionlistener);
		m_jButtonOutLarge.addActionListener(m_actionlistener);
		m_jButtonOutput.addActionListener(m_actionlistener);
		m_jButtonProps.addActionListener(m_actionlistener);
		m_jButtonExit.addActionListener(m_actionlistener);
		m_jMenuItemProps.addActionListener(m_actionlistener);
		m_jMenuItemCopy.addActionListener(m_actionlistener);
		m_jMenuItemMove.addActionListener(m_actionlistener);
		m_jMenuItemMkDir.addActionListener(m_actionlistener);
		m_jMenuItemDelete.addActionListener(m_actionlistener);
		m_jMenuItemRefresh.addActionListener(m_actionlistener);
		m_jMenuItemTerm.addActionListener(m_actionlistener);
		m_jMenuItemCalcSize.addActionListener(m_actionlistener);
		m_jMenuItemCompare.addActionListener(m_actionlistener);
		m_jMenuItemExit.addActionListener(m_actionlistener);
		m_jMenuItemClear.addActionListener(m_actionlistener);
		m_jMenuItemLarge.addActionListener(m_actionlistener);
		m_jMenuItemOutput.addActionListener(m_actionlistener);
		m_jMenuItemSave.addActionListener(m_actionlistener);
		m_jMenuItemSearch.addActionListener(m_actionlistener);
		m_jMenuItemServerSocket.addActionListener(m_actionlistener);
		m_jMenuItemServerCommPort.addActionListener(m_actionlistener);
		m_jMenuItemClientSocket.addActionListener(m_actionlistener);
		m_jMenuItemClientCommPort.addActionListener(m_actionlistener);
		m_jMenuItemNewCgi.addActionListener(m_actionlistener);
		m_jMenuItemNewFtp.addActionListener(m_actionlistener);
		m_jMenuItemRemote.addActionListener(m_actionlistener);
		m_jMenuItemDisconnect.addActionListener(m_actionlistener);
		m_jMenuItemConfigStarter.addActionListener(m_actionlistener);
		m_jMenuItemAddBookmark.addActionListener(m_actionlistener);
		m_jMenuItemEditBookmark.addActionListener(m_actionlistener);
		m_jMenuItemConfig.addActionListener(m_actionlistener);
		m_jMenuItemConfigAll.addActionListener(m_actionlistener);
		// m_jCheckBoxMenuItemAutoLogon.addActionListener( m_actionlistener);
		// m_jCheckBoxMenuItemDiff.addActionListener( m_actionlistener);
		m_jButtonHistLeft.addActionListener(m_actionlistener);
		m_jButtonHistRight.addActionListener(m_actionlistener);
		m_jButtonCopy.addActionListener(m_actionlistener);
		m_jButtonMove.addActionListener(m_actionlistener);
		m_jButtonMkDir.addActionListener(m_actionlistener);
		m_jButtonDelete.addActionListener(m_actionlistener);
		m_jButtonRefresh.addActionListener(m_actionlistener);
		m_jButtonTerm.addActionListener(m_actionlistener);
		m_jButtonBsh.addActionListener(m_actionlistener);
		m_jButtonConfig.addActionListener(m_actionlistener);
		m_jButtonConfigAll.addActionListener(m_actionlistener);
		m_jMenuItemAll.addActionListener(m_actionlistener);
		m_jMenuItemFilter.addActionListener(m_actionlistener);
		m_jMenuItemSync.addActionListener(m_actionlistener);

		_load();
		_init();
		initStarterMenus();
		initBookmarkMenus();
		if (m_sd != null) {
			m_sd.close();
			m_sd.dispose();
			m_sd = null;
		}
	}

	public void setRootComponent(Component comp) {
		GlobalApplicationContext.instance().addRootComponent(comp);
	}

	// Initialisierung der Komponente
	private void jbInit() throws Exception {
		this.setSize(new Dimension(629, 537));
		this.getContentPane().setLayout(m_borderLayoutBase);
		this.setJMenuBar(m_jMenuBar);
		m_jMenuFile.setText("File");
		m_jMenuFile.setActionCommand("file");
		m_jMenuFile.setMnemonic((int) 'F');
		m_jMenuView.setText("View");
		m_jMenuView.setActionCommand("view");
		m_jMenuView.setMnemonic((int) 'V');
		m_jMenuExtra.setText("Extra");
		m_jMenuExtra.setActionCommand("extra");
		m_jMenuExtra.setMnemonic((int) 'E');
		m_jMenuStarter.setText("Starter");
		m_jMenuStarter.setActionCommand("starter");
		m_jMenuStarter.setMnemonic('S');
		m_jPanelMainView.setLayout(m_borderLayoutMainView);
		m_jPanelFileViewLeft.setBorder(BorderFactory.createEtchedBorder());
		m_jPanelFileViewRight.setBorder(BorderFactory.createEtchedBorder());
		m_jPanelFileView.setLayout(gridLayoutFileView);
		gridLayoutFileView.setColumns(2);
		m_jScrollPaneOut.setPreferredSize(new Dimension(74, 100));
		m_jPanelOut.setLayout(m_borderLayoutOut);
		m_jPanelOutHead.setLayout(m_flowLayoutOutHead);
		m_flowLayoutOutHead.setAlignment(FlowLayout.LEFT);
		m_flowLayoutOutHead.setHgap(0);
		m_flowLayoutOutHead.setVgap(0);
		m_jTextAreaOut.setEditable(false);
		m_jButtonOutClear.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"clearout.gif"));
		m_jButtonOutClear.setMaximumSize(new Dimension(22, 20));
		m_jButtonOutClear.setMinimumSize(new Dimension(22, 20));
		m_jButtonOutClear.setPreferredSize(new Dimension(22, 20));
		m_jButtonOutClear.setToolTipText("Clear output");
		m_jButtonOutClear.setActionCommand("Clear");
		m_jButtonOutLarge.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"largeout.gif"));
		m_jButtonOutLarge.setMaximumSize(new Dimension(22, 20));
		m_jButtonOutLarge.setMinimumSize(new Dimension(22, 20));
		m_jButtonOutLarge.setPreferredSize(new Dimension(22, 20));
		m_jButtonOutLarge.setToolTipText("Show large output window");
		m_jButtonOutLarge.setActionCommand("Large");
		m_jButtonOutput.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"hideout.gif"));
		m_jButtonOutput.setMaximumSize(new Dimension(22, 20));
		m_jButtonOutput.setMinimumSize(new Dimension(22, 20));
		m_jButtonOutput.setPreferredSize(new Dimension(22, 20));
		m_jButtonOutput.setToolTipText("show/hide output");
		m_jButtonOutput.setActionCommand("output");
		m_jButtonProps.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"preferences16.gif"));
		m_jButtonProps.setMaximumSize(new Dimension(22, 20));
		m_jButtonProps.setMinimumSize(new Dimension(22, 20));
		m_jButtonProps.setPreferredSize(new Dimension(22, 20));
		m_jButtonProps.setToolTipText("Properties");
		m_jButtonProps.setActionCommand("properties");
		m_jButtonExit.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"exit16.gif"));
		m_jButtonExit.setMaximumSize(new Dimension(22, 20));
		m_jButtonExit.setMinimumSize(new Dimension(22, 20));
		m_jButtonExit.setPreferredSize(new Dimension(22, 20));
		m_jButtonExit.setToolTipText("Exit");
		m_jButtonExit.setActionCommand("exit");
		m_jMenuItemProps.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"preferences16.gif"));
		m_jMenuItemProps.setMnemonic('P');
		m_jMenuItemProps.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				Event.CTRL_MASK));
		m_jMenuItemProps.setActionCommand("properties");
		m_jMenuItemProps.setText("Properties");
		m_jMenuItemCalcSize.setMnemonic('s');
		m_jMenuItemCalcSize.setActionCommand("calcsize");
		m_jMenuItemCalcSize.setText("Calc size ...");
		m_jMenuItemCompare.setMnemonic('C');
		m_jMenuItemCompare.setActionCommand("compare");
		m_jMenuItemCompare.setText("Compare by content");
		m_jMenuItemCompare.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"compare.gif"));
		// m_jCheckBoxMenuItemAutoLogon.setText("Auto logon");
		// m_jCheckBoxMenuItemAutoLogon.setActionCommand("autologon");
		// m_jCheckBoxMenuItemAutoLogon.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_A, Event.CTRL_MASK));
		// m_jCheckBoxMenuItemAutoLogon.setMnemonic((int)'A');
		// m_jCheckBoxMenuItemAutoLogon.setState( false);
		// m_jCheckBoxMenuItemDiff.setState(false);
		// m_jCheckBoxMenuItemDiff.setMnemonic((int)'d');
		// m_jCheckBoxMenuItemDiff.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_D, Event.CTRL_MASK));
		// m_jCheckBoxMenuItemDiff.setActionCommand("treatdiff");
		// m_jCheckBoxMenuItemDiff.setText("Treat as different");
		m_jMenuItemConfigStarter.setText("Config starter");
		m_jMenuItemConfigStarter.setActionCommand("configstarter");
		m_jMenuItemConfigStarter.setMnemonic('C');
		m_jMenuConnect.setMnemonic((int) 'D');
		m_jMenuConnect.setActionCommand("direct");
		m_jMenuConnect.setText("Connect");
		m_jButtonConfigAll.setMaximumSize(new Dimension(21, 21));
		m_jButtonConfigAll.setMinimumSize(new Dimension(21, 21));
		m_jButtonConfigAll.setPreferredSize(new Dimension(21, 21));
		m_jButtonConfigAll.setToolTipText("Configure connetions");
		m_jButtonConfigAll.setActionCommand("ConfigAll");
		m_jButtonConfigAll.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"confconnect.gif"));
		m_jButtonConfig.setMaximumSize(new Dimension(21, 21));
		m_jButtonConfig.setMinimumSize(new Dimension(21, 21));
		m_jButtonConfig.setPreferredSize(new Dimension(21, 21));
		m_jButtonConfig.setToolTipText("Configure file extension bindings");
		m_jButtonConfig.setActionCommand("ConfigCurrent");
		m_jButtonConfig.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"config.gif"));
		m_jButtonHistLeft.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"lbutakt.gif"));
		m_jButtonHistLeft.setActionCommand("back");
		m_jButtonHistLeft.setToolTipText("previous");
		m_jButtonHistLeft.setPreferredSize(new Dimension(21, 21));
		m_jButtonHistLeft.setMinimumSize(new Dimension(21, 21));
		m_jButtonHistLeft.setMaximumSize(new Dimension(21, 21));
		m_jButtonHistRight.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"rbutakt.gif"));
		m_jButtonHistRight.setActionCommand("next");
		m_jButtonHistRight.setToolTipText("next");
		m_jButtonHistRight.setPreferredSize(new Dimension(21, 21));
		m_jButtonHistRight.setMinimumSize(new Dimension(21, 21));
		m_jButtonHistRight.setMaximumSize(new Dimension(21, 21));
		m_jButtonCopy.setMaximumSize(new Dimension(21, 21));
		m_jButtonCopy.setMinimumSize(new Dimension(21, 21));
		m_jButtonCopy.setPreferredSize(new Dimension(21, 21));
		m_jButtonCopy.setToolTipText("Copy file 'F5'");
		m_jButtonCopy.setActionCommand("Copy");
		m_jButtonCopy.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"copy.gif"));
		m_jButtonMove.setMaximumSize(new Dimension(21, 21));
		m_jButtonMove.setMinimumSize(new Dimension(21, 21));
		m_jButtonMove.setPreferredSize(new Dimension(21, 21));
		m_jButtonMove.setToolTipText("Move file 'F6'");
		m_jButtonMove.setActionCommand("Move");
		m_jButtonMove.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"move.gif"));
		m_jButtonMkDir.setMaximumSize(new Dimension(21, 21));
		m_jButtonMkDir.setMinimumSize(new Dimension(21, 21));
		m_jButtonMkDir.setPreferredSize(new Dimension(21, 21));
		m_jButtonMkDir.setToolTipText("Make directory 'F7'");
		m_jButtonMkDir.setActionCommand("MkDir");
		m_jButtonMkDir.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"newfolder.gif"));
		m_jButtonDelete.setMaximumSize(new Dimension(21, 21));
		m_jButtonDelete.setMinimumSize(new Dimension(21, 21));
		m_jButtonDelete.setPreferredSize(new Dimension(21, 21));
		m_jButtonDelete.setToolTipText("Delete file 'F8'");
		m_jButtonDelete.setActionCommand("Delete");
		m_jButtonDelete.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"delete.gif"));
		m_jButtonRefresh.setMaximumSize(new Dimension(21, 21));
		m_jButtonRefresh.setMinimumSize(new Dimension(21, 21));
		m_jButtonRefresh.setPreferredSize(new Dimension(21, 21));
		m_jButtonRefresh.setToolTipText("Reread source");
		m_jButtonRefresh.setActionCommand("Refresh");
		m_jButtonRefresh.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"refresh.gif"));
		m_jButtonTerm.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"term.gif"));
		m_jButtonTerm.setActionCommand("Terminal");
		m_jButtonTerm.setToolTipText("Start terminal");
		m_jButtonTerm.setPreferredSize(new Dimension(21, 21));
		m_jButtonTerm.setMinimumSize(new Dimension(21, 21));
		m_jButtonTerm.setMaximumSize(new Dimension(21, 21));
		m_jButtonBsh.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"bshterm.gif"));
		m_jButtonBsh.setActionCommand("Bsh");
		m_jButtonBsh.setToolTipText("Start bsh desktop");
		m_jButtonBsh.setPreferredSize(new Dimension(21, 21));
		m_jButtonBsh.setMinimumSize(new Dimension(21, 21));
		m_jButtonBsh.setMaximumSize(new Dimension(21, 21));
		m_jMenuServer.setText("Direct link server");
		m_jMenuServer.setActionCommand("direct");
		m_jMenuServer.setMnemonic((int) 'D');
		m_jMenuItemServerSocket.setText("Socket");
		m_jMenuItemServerSocket.setActionCommand("serversocket");
		m_jMenuItemServerSocket.setMnemonic((int) 'S');
		m_jMenuClient.setMnemonic((int) 'D');
		m_jMenuClient.setActionCommand("direct");
		m_jMenuClient.setText("Direct link client");
		m_jMenuItemClientSocket.setMnemonic((int) 'S');
		m_jMenuItemClientSocket.setActionCommand("clientsocket");
		m_jMenuItemClientSocket.setText("Socket");
		m_jMenuItemClientCommPort.setMnemonic((int) 'C');
		m_jMenuItemClientCommPort.setActionCommand("clientcommport");
		m_jMenuItemClientCommPort.setText("CommPort");
		m_jMenuItemRemote.setMnemonic((int) 'C');
		m_jMenuItemRemote.setActionCommand("Connect");
		m_jMenuItemRemote.setText("Connect");
		m_jMenuItemServerCommPort.setText("CommPort");
		m_jMenuItemServerCommPort.setActionCommand("servercommport");
		m_jMenuItemServerCommPort.setMnemonic((int) 'C');
		m_jMenuBookmark.setMnemonic((int) 'B');
		m_jMenuBookmark.setActionCommand("bookmark");
		m_jMenuBookmark.setText("Bookmark");
		m_jMenuItemAddBookmark.setText("Add bookmark");
		m_jMenuItemAddBookmark.setActionCommand("addbookmark");
		m_jMenuItemAddBookmark.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_B, Event.CTRL_MASK));
		m_jMenuItemAddBookmark.setMnemonic((int) 'A');
		m_jMenuItemEditBookmark.setText("Edit bookmarks");
		m_jMenuItemEditBookmark.setActionCommand("editbookmarks");
		m_jMenuItemEditBookmark.setMnemonic((int) 'E');
		m_jMenuItemConfigAll.setText("Configure connetions");
		m_jMenuItemConfigAll.setActionCommand("ConfigAll");
		m_jMenuItemConfigAll.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"confconnect.gif"));
		m_jMenuItemConfig.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"config.gif"));
		m_jMenuItemConfig.setActionCommand("ConfigCurrent");
		m_jMenuItemConfig.setText("Configure file extension bindings");
		m_jMenuItemCopy.setText("Copy");
		m_jMenuItemCopy.setActionCommand("Copy");
		m_jMenuItemCopy.setAccelerator(KeyStroke
				.getKeyStroke(KeyEvent.VK_F5, 0));
		m_jMenuItemCopy.setMnemonic('C');
		m_jMenuItemCopy.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"copy.gif"));
		m_jMenuItemMove.setText("Move");
		m_jMenuItemMove.setActionCommand("Move");
		m_jMenuItemMove.setAccelerator(KeyStroke
				.getKeyStroke(KeyEvent.VK_F6, 0));
		m_jMenuItemMove.setMnemonic('M');
		m_jMenuItemMove.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"move.gif"));
		m_jMenuItemMkDir.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"newfolder.gif"));
		m_jMenuItemMkDir.setMnemonic('k');
		m_jMenuItemMkDir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7,
				0));
		m_jMenuItemMkDir.setActionCommand("MkDir");
		m_jMenuItemMkDir.setText("Make directory");
		m_jMenuItemDelete.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"delete.gif"));
		m_jMenuItemDelete.setMnemonic('D');
		m_jMenuItemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8,
				0));
		m_jMenuItemDelete.setActionCommand("Delete");
		m_jMenuItemDelete.setText("Delete");
		m_jMenuItemRefresh.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"refresh.gif"));
		m_jMenuItemRefresh.setMnemonic('R');
		m_jMenuItemRefresh.setActionCommand("Refresh");
		m_jMenuItemRefresh.setText("Reread source");
		m_jMenuItemTerm.setText("Terminal");
		m_jMenuItemTerm.setActionCommand("Terminal");
		m_jMenuItemTerm.setMnemonic('R');
		m_jMenuItemTerm.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"term.gif"));
		m_jMenuItemAll.setMnemonic((int) 'A');
		m_jMenuItemAll.setActionCommand("all");
		m_jMenuItemAll.setText("All");
		m_jMenuItemAll.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"clearout.gif"));
		m_jMenuItemFilter.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"clearout.gif"));
		m_jMenuItemFilter.setText("Filter");
		m_jMenuItemFilter.setActionCommand("filter");
		m_jMenuItemFilter.setMnemonic((int) 'F');
		m_jMenuItemSearch.setText("Search files");
		m_jMenuItemSearch.setActionCommand("search");
		m_jMenuItemSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				Event.CTRL_MASK));
		m_jMenuItemSearch.setMnemonic((int) 'c');
		m_jMenuNewConnect.setText("New connection");
		m_jMenuNewConnect.setActionCommand("newconnect");
		m_jMenuNewConnect.setMnemonic((int) 'D');
		m_jMenuItemNewCgi.setMnemonic((int) 'S');
		m_jMenuItemNewCgi.setActionCommand("newcgi");
		m_jMenuItemNewCgi.setText("Cgi");
		m_jMenuItemNewFtp.setMnemonic((int) 'C');
		m_jMenuItemNewFtp.setActionCommand("newftp");
		m_jMenuItemNewFtp.setText("Ftp");
		m_jMenuItemDisconnect.setText("Disconnect");
		m_jMenuItemDisconnect.setActionCommand("Disconnect");
		m_jMenuItemDisconnect.setMnemonic((int) 'D');
		m_jMenuItemSync.setMnemonic((int) 'S');
		m_jMenuItemSync.setActionCommand("sync");
		m_jMenuItemSync.setText("Synchronize");
		m_jMenuBar.add(m_jMenuFile);
		m_jMenuBar.add(m_jMenuView);
		m_jMenuBar.add(m_jMenuBookmark);
		m_jMenuBar.add(m_jMenuExtra);
		m_jMenuBar.add(m_jMenuStarter);
		m_jMenuItemExit.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"exit16.gif"));
		m_jMenuItemExit.setText("Exit");
		m_jMenuItemExit.setActionCommand("exit");
		m_jMenuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				Event.CTRL_MASK));
		m_jMenuItemExit.setMnemonic((int) 'E');
		m_jMenuFile.add(m_jMenuItemCopy);
		m_jMenuFile.add(m_jMenuItemMove);
		m_jMenuFile.add(m_jMenuItemMkDir);
		m_jMenuFile.add(m_jMenuItemDelete);
		m_jMenuFile.add(m_jMenuItemRefresh);
		m_jMenuFile.add(m_jMenuItemTerm);
		m_jMenuFile.addSeparator();
		m_jMenuFile.add(m_jMenuItemProps);
		m_jMenuFile.add(m_jMenuItemConfigAll);
		m_jMenuFile.add(m_jMenuItemConfig);
		m_jMenuFile.addSeparator();
		m_jMenuFile.add(m_jMenuItemCompare);
		m_jMenuFile.add(m_jMenuItemCalcSize);
		m_jMenuFile.addSeparator();
		m_jMenuFile.add(m_jMenuItemExit);
		m_jMenuItemClear.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"clearout.gif"));
		m_jMenuItemClear.setText("Clear output");
		m_jMenuItemClear.setActionCommand("Clear");
		m_jMenuItemClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				Event.CTRL_MASK));
		m_jMenuItemClear.setMnemonic((int) 'C');
		m_jMenuView.add(m_jMenuItemClear);
		m_jMenuItemLarge.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"largeout.gif"));
		m_jMenuItemLarge.setText("Large output window");
		m_jMenuItemLarge.setActionCommand("Large");
		m_jMenuItemLarge.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				Event.CTRL_MASK));
		m_jMenuItemLarge.setMnemonic((int) 'L');
		m_jMenuView.add(m_jMenuItemLarge);
		m_jMenuItemOutput.setIcon(ResouceManager.getCommonImageIcon(APPNAME,
				"hideout.gif"));
		m_jMenuItemOutput.setText("hide/show output window");
		m_jMenuItemOutput.setActionCommand("output");
		m_jMenuItemOutput.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				Event.CTRL_MASK));
		m_jMenuItemOutput.setMnemonic((int) 'O');
		m_jMenuView.add(m_jMenuItemOutput);
		m_jMenuView.addSeparator();
		m_jMenuView.add(m_jMenuItemAll);
		m_jMenuView.add(m_jMenuItemFilter);
		m_jMenuItemSave.setMnemonic((int) 'S');
		m_jMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Event.CTRL_MASK));
		m_jMenuItemSave.setActionCommand("Save");
		m_jMenuItemSave.setText("Save config");
		m_jMenuExtra.add(m_jMenuItemSave);
		m_jMenuExtra.add(m_jMenuItemSearch);
		m_jMenuExtra.add(m_jMenuItemSync);
		m_jMenuExtra.addSeparator();
		m_jMenuExtra.add(m_jMenuConnect);
		m_jMenuExtra.add(m_jMenuItemDisconnect);
		// m_jMenuExtra.addSeparator();
		// m_jMenuExtra.add(m_jCheckBoxMenuItemAutoLogon);
		// m_jMenuExtra.add(m_jCheckBoxMenuItemDiff);
		this.getContentPane().add(m_jToolBar, BorderLayout.NORTH);
		this.getContentPane().add(m_jPanelMainView, BorderLayout.CENTER);
		m_jPanelMainView.add(m_jPanelFileView, BorderLayout.CENTER);
		m_jPanelFileView.add(m_jPanelFileViewLeft, null);
		m_jPanelFileView.add(m_jPanelFileViewRight, null);
		this.getContentPane().add(m_jPanelOut, BorderLayout.SOUTH);
		m_jPanelOut.add(m_jScrollPaneOut, BorderLayout.CENTER);
		m_jPanelOut.add(m_jPanelOutHead, BorderLayout.NORTH);
		// m_jPanelOutHead.add(m_jButtonOutput, null);
		m_jScrollPaneOut.getViewport().add(m_jTextAreaOut, null);
		m_jToolBar.add(m_jButtonCopy, null);
		m_jToolBar.add(m_jButtonMove, null);
		m_jToolBar.add(m_jButtonMkDir, null);
		m_jToolBar.add(m_jButtonDelete, null);
		m_jToolBar.add(m_jButtonRefresh, null);
		m_jToolBar.addSeparator();
		m_jToolBar.add(m_jButtonTerm, null);
		m_jToolBar.add(m_jButtonBsh, null);
		m_jToolBar.addSeparator();
		m_jToolBar.add(m_jButtonHistLeft, null);
		m_jToolBar.add(m_jButtonHistRight, null);
		m_jToolBar.addSeparator();
		m_jToolBar.add(m_jButtonOutClear, null);
		m_jToolBar.add(m_jButtonOutLarge, null);
		m_jToolBar.add(m_jButtonOutput, null);
		m_jToolBar.addSeparator();
		m_jToolBar.add(m_jButtonConfigAll, null);
		m_jToolBar.add(m_jButtonConfig, null);
		m_jToolBar.add(m_jButtonProps, null);
		m_jToolBar.addSeparator();
		m_jToolBar.add(m_jButtonExit, null);
		m_jMenuStarter.add(m_jMenuItemConfigStarter);
		m_jMenuStarter.add(m_starterSeparator);
		m_jMenuConnect.add(m_jMenuServer);
		m_jMenuConnect.add(m_jMenuClient);
		m_jMenuConnect.add(m_jMenuNewConnect);
		m_jMenuConnect.add(m_jMenuItemRemote);
		m_jMenuServer.add(m_jMenuItemServerSocket);
		m_jMenuServer.add(m_jMenuItemServerCommPort);
		m_jMenuClient.add(m_jMenuItemClientSocket);
		m_jMenuClient.add(m_jMenuItemClientCommPort);
		m_jMenuBookmark.add(m_jMenuItemAddBookmark);
		m_jMenuBookmark.add(m_jMenuItemEditBookmark);
		m_jMenuBookmark.addSeparator();
		m_jMenuNewConnect.add(m_jMenuItemNewCgi);
		m_jMenuNewConnect.add(m_jMenuItemNewFtp);
	}

	// Das Applet starten
	public void start() {
	}

	// Das Applet anhalten
	public void stop() {
		getRight().getFileInfoTable().getFileInterface().logoff();
		getLeft().getFileInfoTable().getFileInterface().logoff();
	}

	// Das Applet löschen
	public void destroy() {
	}

	// Applet-Information holen
	public String getAppletInfo() {
		return "File Manager to manage files and directories local and/or on a remote machine via a cgi script.";
	}

	// Parameter-Infos holen
	public String[][] getParameterInfo() {
		return null;
	}

	public FileInfoPanel getActiveFileInfoPanel() {
		if (m_jPanelFileViewLeft != null && m_jPanelFileViewLeft.isActive())
			return m_jPanelFileViewLeft;
		else if (m_jPanelFileViewRight != null
				&& m_jPanelFileViewRight.isActive())
			return m_jPanelFileViewRight;
		return m_jPanelFileViewLeft;
	}

	public FileInfoPanel getInactiveFileInfoPanel() {
		if (m_jPanelFileViewLeft != null && !m_jPanelFileViewLeft.isActive())
			return m_jPanelFileViewLeft;
		else if (m_jPanelFileViewRight != null
				&& !m_jPanelFileViewRight.isActive())
			return m_jPanelFileViewRight;
		return m_jPanelFileViewRight;
	}

	class LisAction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			getRootPane().getParent().setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String cmd = event.getActionCommand();
			FileInfoPanel fip = getActiveFileInfoPanel();
			if (cmd.equals("Clear")) {
				m_jTextAreaOut.setText("");
			} else if (cmd.equals("Large"))
				outlarge_actionPerformed(event);
			else if (cmd.equals("output")) {
				m_jPanelOut.setVisible(false);
				if (m_jScrollPaneOut.isVisible())
					m_jScrollPaneOut.setVisible(false);
				else
					m_jScrollPaneOut.setVisible(true);
				m_jPanelOut.setVisible(true);
			} else if (cmd.equals("properties"))
				props_actionPerformed(event);
			else if (cmd.equals("calcsize"))
				calcsize_actionPerformed(event);
			else if (cmd.equals("compare"))
				compare_actionPerformed(event);
			else if (cmd.equals("exit"))
				exit_actionPerformed(event);
			else if (cmd.equals("Save"))
				_save();
			else if (cmd.equals("serversocket"))
				socketServer();
			else if (cmd.equals("servercommport"))
				commPortServer();
			else if (cmd.equals("autologon"))
				_autoLogon();
			else if (cmd.equals("treatdiff"))
				_treatAsDiff();
			else if (cmd.equals("configstarter"))
				configStarterCommands();
			else if (cmd.equals("back")) {
				fip.getFileInfoTable().back();
				fip.updateRootCombo();
				fip.updateRootSelection();
			} else if (cmd.equals("next")) {
				fip.getFileInfoTable().next();
				fip.updateRootCombo();
				fip.updateRootSelection();
			} else if (cmd.equals("Copy"))
				fip._copy(false, false);
			else if (cmd.equals("Move"))
				fip._move();
			else if (cmd.equals("MkDir"))
				fip.mkdir_actionPerformed(event);
			else if (cmd.equals("Delete"))
				fip.delete_actionPerformed(event, false);
			else if (cmd.equals("Refresh"))
				fip.refresh();
			else if (cmd.equals("Terminal"))
				fip.onTerminal();
			else if (cmd.equals("Bsh"))
				fip.onBsh();
			else if (cmd.equals("ConfigCurrent"))
				fip.configFileExtCommands();
			else if (cmd.equals("ConfigAll"))
				fip.configAllFileExtCommands();
			else if (cmd.equals("sync"))
				onSync();
			else if (cmd.equals("Connect"))
				fip.connect(null);
			else if (cmd.equals("Disconnect"))
				fip.connect(FileInterface.LOCAL);
			else if (cmd.equals("newcgi"))
				fip.configAllFileExtCommands();
			else if (cmd.equals("newftp"))
				fip.configAllFileExtCommands();
			else if (cmd.equals("clientsocket"))
				fip.connect(FileInterface.SOCKET);
			else if (cmd.equals("clientcommport"))
				fip.connect(FileInterface.COMMPORT);
			else if (cmd.equals("addbookmark"))
				addBookmark();
			else if (cmd.equals("editbookmarks"))
				editBookmarks();
			else if (cmd.equals("all")) {
				fip.setFileInterfaceFilter(null);
				fip.refresh();
			} else if (cmd.equals("filter"))
				setFilter();
			else if (cmd.equals("search"))
				search();
			else {
				CommandClass cmdCls = CommandClass.searchCommand(m_starterCmd,
						cmd, CommandClass.NO);
				boolean blSuccess = false;
				int i = 0;

				if (cmdCls != null) {
					blSuccess = true;
					_exec(cmdCls);
				}
				if (!blSuccess) {
					for (i = 0; i < m_vecBookmarks.size(); i++) {
						if (m_vecBookmarks.get(i) == null)
							break;
						if (cmd.equals((m_vecBookmarks.get(i)).toString())) {
							BookmarkInfo bi = m_vecBookmarks.get(i);
							fip.connect(bi.getConnectionId(), bi.getPath());
							blSuccess = true;
							break;
						}
					}
				}
			}
			getRootPane().getParent().setCursor(
					new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void socketServer() {
		SocketTypeServerFileInterfaceConfiguration configuration = new SocketTypeServerFileInterfaceConfiguration(
				SocketTypeServerFileInterfaceConfiguration.DEFAULTPORT, null,
				FileManager.APPNAME, m_appProps, true);
		GeneralFileInterfaceConfiguration generalFileInterfaceConfiguration = new GeneralFileInterfaceConfiguration(
				APPNAME, m_appProps, true);
		SocketTypeServer sts = new SocketTypeServer(new NormalFile(".", null,
				generalFileInterfaceConfiguration), configuration);
		sts.setShellEnv(m_jshell.getShellEnv());
		SocketTypeServerDialog ftsd = new SocketTypeServerDialog(this,
				"Direct link socket server", false, sts, APPNAME);
		configuration.setDialog(ftsd);
		ftsd.setVisible(true);
	}

	public void commPortServer() {
		CommPortTypeServerFileInterfaceConfiguration commPortTypeServerFileInterfaceConfiguration = new CommPortTypeServerFileInterfaceConfiguration(
				null, null, APPNAME, m_appProps, true);
		GeneralFileInterfaceConfiguration generalFileInterfaceConfiguration = new GeneralFileInterfaceConfiguration(
				APPNAME, m_appProps, true);
		CommPortTypeServer cpts = new CommPortTypeServer(new NormalFile(".",
				null, generalFileInterfaceConfiguration),
				commPortTypeServerFileInterfaceConfiguration);
		cpts.setShellEnv(m_jshell.getShellEnv());
		CommPortTypeServerDialog ftsd = new CommPortTypeServerDialog(this,
				"Direct link comm port server", false, cpts, APPNAME);
		commPortTypeServerFileInterfaceConfiguration.setDialog(ftsd);
		ftsd.setVisible(true);
	}

	public void removeStarterMenus() {
		for (int i = 0; i < m_starterMenuItems.size(); i++) {
			JMenuItem mi = m_starterMenuItems.get(i);
			m_jMenuStarter.remove(mi);
		}
		m_starterMenuItems.clear();
	}

	public void initStarterMenus() {
		for (int i = 0; i < m_starterCmd.size(); i++) {
			CommandClass cmd = m_starterCmd.get(i);
			JMenuItem mi = new JMenuItem(cmd.getIdentifier());
			mi.setActionCommand(cmd.getIdentifier());
			mi.addActionListener(m_actionlistener);
			m_starterMenuItems.add(mi);
			m_jMenuStarter.add(mi);
		}
	}

	public void setFilter() {
		FileFilterDialog ffd = new FileFilterDialog(this, "Filefilter", true,
				m_appProps);
		ffd.setVisible(true);
		if (!ffd.isCanceled()) {
			FileInfoPanel fip = getActiveFileInfoPanel();
			FileInterfaceFilter fif = ffd.getSelection();
			fip.setFileInterfaceFilter(fif);
			fip.refresh();
		}
	}

	public void search() {
		FileInfoPanel fip = getActiveFileInfoPanel();
		FileSearchDialog fsd = new FileSearchDialog(this, "File search", false,
				APPNAME, fip, m_appProps);
		fsd.setVisible(true);
	}

	public void addBookmarkMenu(BookmarkInfo bi) {
		JMenu sub = (JMenu) m_bookmarkSubMenuItems.get(bi.getConnectionId());
		if (sub == null) {
			sub = new JMenu(bi.getConnectionId());
			m_bookmarkSubMenuItems.put(bi.getConnectionId(), sub);
			m_jMenuBookmark.add(sub);
		}
		JMenuItem mi = new JMenuItem(bi.getPath());
		mi.setActionCommand(bi.toString());
		mi.addActionListener(m_actionlistener);
		m_bookmarkMenuItems.put(bi.toString(), mi);
		sub.add(mi);
	}

	public void removeBookmarkMenu(String strBookmark, boolean blSingle) {
		JMenuItem mi = (JMenuItem) m_bookmarkMenuItems.get(strBookmark);
		mi.removeActionListener(m_actionlistener);
		StringTokenizer st = new StringTokenizer(strBookmark, ";", false);
		if (st.hasMoreTokens()) {
			String strConnId = st.nextToken().trim();
			JMenu sub = (JMenu) m_bookmarkSubMenuItems.get(strConnId);
			sub.remove(mi);
			if (sub.getItemCount() == 0) {
				m_jMenuBookmark.remove(sub);
				m_bookmarkSubMenuItems.remove(strConnId);
			}
		}
		if (blSingle)
			m_bookmarkMenuItems.remove(strBookmark);
	}

	public void removeBookmarkMenus() {
		Set<String> sKeys = m_bookmarkMenuItems.keySet();
		Iterator<String> it = sKeys.iterator();
		while (it.hasNext()) {
			String strKey = it.next();
			removeBookmarkMenu(strKey, false);
		}
		m_bookmarkMenuItems.clear();
	}

	public void initBookmarkMenus() {
		List<BookmarkInfo> vecConnIds = getConnectionIdsFromBookmarks();
		for (int i = 0; i < vecConnIds.size(); i++) {
			BookmarkInfo biConnId = vecConnIds.get(i);
			JMenu sub = new JMenu(biConnId.getConnectionId());
			m_bookmarkSubMenuItems.put(biConnId.getConnectionId(), sub);
			m_jMenuBookmark.add(sub);
			List<BookmarkInfo> vecBookms = getBookmarksWithConnectionId(biConnId
					.getConnectionId());
			for (int j = 0; j < vecBookms.size(); ++j) {
				BookmarkInfo biBookm = vecBookms.get(j);
				JMenuItem mi = new JMenuItem(biBookm.getPath());
				mi.setActionCommand(biBookm.toString());
				mi.addActionListener(m_actionlistener);
				m_bookmarkMenuItems.put(biBookm.toString(), mi);
				sub.add(mi);
			}
		}
	}

	protected List<BookmarkInfo> getConnectionIdsFromBookmarks() {
		List<BookmarkInfo> vecRet = new ArrayList<>();
		for (int i = 0; i < m_vecBookmarks.size(); i++) {
			BookmarkInfo bi = m_vecBookmarks.get(i);
			boolean blFound = false;
			for (int j = 0; j < vecRet.size(); ++j) {
				BookmarkInfo biTmp = (BookmarkInfo) vecRet.get(j);
				if (biTmp.getConnectionId().equalsIgnoreCase(
						bi.getConnectionId())) {
					blFound = true;
					break;
				}
			}
			if (!blFound)
				vecRet.add((BookmarkInfo) bi.clone());
		}
		return vecRet;
	}

	protected List<BookmarkInfo> getBookmarksWithConnectionId(String strConnId) {
		List<BookmarkInfo> vecRet = new ArrayList<>();
		BookmarkInfo bi = null;
		boolean blFound = false;
		for (int i = 0; i < m_vecBookmarks.size(); i++) {
			bi = (BookmarkInfo) m_vecBookmarks.get(i);
			blFound = false;
			if (bi.getConnectionId().equalsIgnoreCase(strConnId)) {
				blFound = true;
			}
			if (blFound)
				vecRet.add((BookmarkInfo) bi.clone());
		}
		return vecRet;
	}

	public void setBookmarks(List<BookmarkInfo> vecBookmarks) {
		m_vecBookmarks = vecBookmarks;
		removeBookmarkMenus();
		initBookmarkMenus();
	}

	public void addBookmark() {
		FileInfoPanel fip = getActiveFileInfoPanel();
		if (fip == null)
			return;
		String strConnId = fip.getConnectionName();
		int idx = strConnId.lastIndexOf(FileInfoPanel.FILEEXT);
		if (idx != -1)
			strConnId = strConnId.substring(0, idx);
		BookmarkInfo bi = new BookmarkInfo(strConnId, fip.getFileInfoTable()
				.getFileInterface().getAbsolutePath());
		if (m_vecBookmarks.contains(bi))
			return;
		addBookmark(bi);
		addBookmarkMenu(bi);
	}

	public void addBookmark(BookmarkInfo bi) {
		m_vecBookmarks.add(bi);
	}

	public void editBookmarks() {
		List<BookmarkInfo> bookmarkInfos = new ArrayList<>();
		for (BookmarkInfo bookmarkInfo : m_vecBookmarks) {
			bookmarkInfos.add((BookmarkInfo) bookmarkInfo.clone());
		}
		EditBookmarkInfoDialog dlg = new EditBookmarkInfoDialog(this,
				"Edit Bookmarks", true, bookmarkInfos);
		dlg.setVisible(true);
		if (dlg.isOk()) {
			removeBookmarkMenus();
			m_vecBookmarks = dlg.getBookmark();
			initBookmarkMenus();
		}
		dlg.dispose();
	}

	/**
	 * get file extension command Vector
	 */
	public List<BookmarkInfo> getBookmarkVector() {
		return m_vecBookmarks;
	}

	public void configStarterCommands() {
		removeStarterMenus();
		ConfigFileExtCommandDialog dlg = new ConfigFileExtCommandDialog(this,
				"Config starter commands", true, m_starterCmd);
		dlg.hideForStarter();
		dlg.setVisible(true);
		m_starterCmd = dlg.getCommand();
		dlg.showAfterStarter();
		dlg.dispose();
		initStarterMenus();
	}

	public void _initDim() {
		if (m_dimProps.load(ConfigPathUtil.getCurrentReadPath(APPNAME,
				DIMFILENAME))) {
			int iLocX = m_dimProps.getProperty(DIMLOCX, 0);
			int iLocY = m_dimProps.getProperty(DIMLOCY, 0);
			int iWidth = m_dimProps.getProperty(DIMWIDTH, 300);
			int iHeight = m_dimProps.getProperty(DIMHEIGHT, 200);
			boolean blOutVis = m_dimProps.getProperty(DIMOUTVIS, false);
			GlobalApplicationContext.instance().getRootComponent()
					.setSize(iWidth, iHeight);
			GlobalApplicationContext.instance().getRootComponent()
					.setLocation(iLocX, iLocY);
			String strLeftDir = m_dimProps.getProperty(LEFTDIR);
			String strLeftConnId = m_dimProps.getProperty(LEFTCONNID);
			String strRightDir = m_dimProps.getProperty(RIGHTDIR);
			String strRightConnId = m_dimProps.getProperty(RIGHTCONNID);
			if (strLeftConnId != null && strLeftDir != null)
				m_jPanelFileViewLeft.connect(strLeftConnId, strLeftDir);
			if (strRightConnId != null && strRightDir != null)
				m_jPanelFileViewRight.connect(strRightConnId, strRightDir);
			if (!blOutVis) {
				m_jScrollPaneOut.setVisible(false);
			}
		} else {
			GlobalApplicationContext.instance().getRootComponent()
					.setSize(800, 600);
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			GlobalApplicationContext
					.instance()
					.getRootComponent()
					.setLocation(
							(d.width - GlobalApplicationContext.instance()
									.getRootComponent().getSize().width) / 2,
							(d.height - GlobalApplicationContext.instance()
									.getRootComponent().getSize().height) / 2);
		}
	}

	public void _saveDim() {
		int iLocX = GlobalApplicationContext.instance().getRootComponent()
				.getX();
		int iLocY = GlobalApplicationContext.instance().getRootComponent()
				.getY();
		int iWidth = GlobalApplicationContext.instance().getRootComponent()
				.getWidth();
		int iHeight = GlobalApplicationContext.instance().getRootComponent()
				.getHeight();
		String strLeftConnId = m_jPanelFileViewLeft.getFileInfoTable()
				.getFileInterface().getId();
		String strLeftDir = m_jPanelFileViewLeft.getFileInfoTable()
				.getAbsolutePath();
		String strRightConnId = m_jPanelFileViewRight.getFileInfoTable()
				.getFileInterface().getId();
		String strRightDir = m_jPanelFileViewRight.getFileInfoTable()
				.getAbsolutePath();
		m_dimProps.setProperty(DIMLOCX, new Integer(iLocX));
		m_dimProps.setProperty(DIMLOCY, new Integer(iLocY));
		m_dimProps.setProperty(DIMWIDTH, new Integer(iWidth));
		m_dimProps.setProperty(DIMHEIGHT, new Integer(iHeight));
		m_dimProps.setProperty(DIMOUTVIS, m_jScrollPaneOut.isVisible());
		m_dimProps.setProperty(LEFTCONNID, strLeftConnId);
		m_dimProps.setProperty(LEFTDIR, strLeftDir);
		m_dimProps.setProperty(RIGHTCONNID, strRightConnId);
		m_dimProps.setProperty(RIGHTDIR, strRightDir);
		m_dimProps.store(
				ConfigPathUtil.getCurrentSavePath(APPNAME, DIMFILENAME),
				DIMFILEHEADER);
	}

	public void _init() {
		// int i = 0;
		// String strId;
		// String strPar;
		m_starterCmd = CommandClass.initCommandVector(STARTER, APPNAME,
				m_appProps);
		m_vecBookmarks = BookmarkInfo.initBookmarkVector(BOOKMARK, m_appProps);

		FileConnector.setTempDirectory(m_appProps
				.getProperty(FileInterface.TEMPDIR));
		FileConnector.setExtPath(m_appProps
				.getProperty(FileInterface.EXTENDEDPATH));
		Integer iSd = m_appProps.getProperty(FileInterface.SEARCHDEPTH, 0);
		FileConnector.setSearchDepth(iSd.intValue());
	}

	/**
	 * erase starter command and bookmark properties
	 */
	public void _erase() {
		CommandClass.eraseCommands(STARTER, m_appProps);
		BookmarkInfo.eraseBookmarks(BOOKMARK, m_appProps);
	}

	/**
	 * enlage output area handler
	 */
	void outlarge_actionPerformed(ActionEvent e) {
		if (m_outDlg == null) {
			m_outDlg = new JDialog((Frame) null, "Output:", false);
			m_outDlg.getContentPane().add(m_jScrollPaneOutLarge);
			m_outDlg.setSize(getSize());
			Point p = this.getLocation();
			m_outDlg.setLocation(p);
			m_outDlg.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					m_jScrollPaneOutLarge.getViewport().remove(m_jTextAreaOut);
					m_jScrollPaneOut.getViewport().add(m_jTextAreaOut, null);
					m_jPanelOut.setVisible(true);
					super.windowClosing(e);
				}
			});
		}
		if (m_outDlg != null && !m_outDlg.isVisible()) {
			m_jPanelOut.setVisible(false);
			m_jScrollPaneOut.getViewport().remove(m_jTextAreaOut);
			m_jScrollPaneOutLarge.getViewport().add(m_jTextAreaOut, null);
			m_outDlg.toFront();
			m_outDlg.setVisible(true);
		}
		/*
		 * if( m_outDlg == null) { m_outDlg = new JDialog( (Frame)null,
		 * "Output:", false); JScrollPane jScrollPaneOutLarge = new
		 * JScrollPane(); if( m_jTextAreaOutLarge == null) m_jTextAreaOutLarge =
		 * new JTextArea(); m_jTextAreaOutLarge.setEditable(false);
		 * jScrollPaneOutLarge.getViewport().add( m_jTextAreaOutLarge, null);
		 * m_outDlg.getContentPane().add( jScrollPaneOutLarge);
		 * m_outDlg.setSize( getSize()); Point p = this.getLocation();
		 * m_outDlg.setLocation( p); m_outDlg.addWindowListener(new
		 * WindowAdapter() { public void windowClosing(WindowEvent e) {
		 * m_jTextAreaOut.setText( m_jTextAreaOutLarge.getText());
		 * m_jTextAreaOut.setCaretPosition( m_jTextAreaOut.getText().length());
		 * if( m_ot != null) m_ot.setTextArea( m_jTextAreaOut);
		 * super.windowClosing( e); } }); } if( m_outDlg != null &&
		 * !m_outDlg.isVisible()) { m_jTextAreaOutLarge.setText(
		 * m_jTextAreaOut.getText()); m_jTextAreaOutLarge.setCaretPosition(
		 * m_jTextAreaOutLarge.getText().length()); m_outDlg.toFront(); if( m_ot
		 * != null) m_ot.setTextArea( m_jTextAreaOutLarge);
		 * m_jTextAreaOut.setText( ""); m_outDlg.setVisible( true); }
		 */
	}

	void onSync() {
		JExDialog dlg = FileSyncPanel.createSyncDialog(this,
				m_jPanelFileViewLeft, m_jPanelFileViewRight, APPNAME,
				m_appProps);
		dlg.setModal(false);
		dlg.setVisible(true);
	}

	/**
	 * properties menu handler
	 */
	void props_actionPerformed(ActionEvent e) {
		if (m_propsDlg == null)
			m_propsDlg = new PropertyDialog(this, "Properties", true,
					m_appProps);
		m_propsDlg.setVisible(true);
	}

	void calcsize_actionPerformed(ActionEvent e) {
		FileInfoPanel fip = getActiveFileInfoPanel();
		if (fip != null) {
			FileInterface[] fis = fip.getFileInfoTable()
					.getSelectedFileInterfaces();
			long[] lFiSel = FileConnector
					.lengthAndCountOfFiles(fis, null, true);
			NumberFormat nf = NumberFormat.getInstance();
			StringBuffer strbuf = new StringBuffer("<HTML>Total size:<BR>");
			strbuf.append(nf.format(lFiSel[0]));
			strbuf.append(" bytes in ");
			strbuf.append(nf.format(lFiSel[1]));
			strbuf.append(" file(s).</HTML>");
			JOptionPane.showMessageDialog(fip, strbuf.toString(),
					"Calc. size ...", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	void compare_actionPerformed(ActionEvent e) {
		FileInterface fi1 = getInactiveFileInfoPanel().getFileInfoTable()
				.getSelectedFileInterface();
		FileInterface fi2 = getActiveFileInfoPanel().getFileInfoTable()
				.getSelectedFileInterface();

		if (fi1 != null && fi2 != null && !fi1.isDirectory()
				&& !fi2.isDirectory()) {
			Diff diff = new Diff(this, fi1, fi2, false, APPNAME, m_appProps);
			diff.setSize(700, 500);
			diff.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "Please select two files!",
					"Compare", JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * exit menu handler
	 */
	void exit_actionPerformed(ActionEvent e) {
		exitApp();
	}

	/**
	 * exit application
	 */
	public void exitApp() {
		_save();
		_saveDim();
		stop();
		Component co = GlobalApplicationContext.instance().getRootComponent();
		if (co != null && co instanceof JFrame)
			((JFrame) co).dispose();
		JShell.setMultiAppMode(false);
		System.exit(0);
	}

	public FileInfoPanel getLeft() {
		return m_jPanelFileViewLeft;
	}

	public FileInfoPanel getRight() {
		return m_jPanelFileViewRight;
	}

	/**
	 * handler to set auto logon flag
	 */
	public void _autoLogon() {
		boolean blState = false; // m_jCheckBoxMenuItemAutoLogon.getState();
		if (blState) {
			// m_jCheckBoxMenuItemAutoLogon.setState( true);
			getRight().setAutoLogon(true);
			getLeft().setAutoLogon(true);
		} else {
			// m_jCheckBoxMenuItemAutoLogon.setState( false);
			getRight().setAutoLogon(false);
			getLeft().setAutoLogon(false);
		}
	}

	/**
	 * handler to set treas as different flag
	 */
	public void _treatAsDiff() {
		boolean blState = false; // m_jCheckBoxMenuItemDiff.getState();
		if (blState) {
			// m_jCheckBoxMenuItemDiff.setState( true);
			getRight().setTreatAsDiff(true);
			getLeft().setTreatAsDiff(true);
		} else {
			// m_jCheckBoxMenuItemDiff.setState( false);
			getRight().setTreatAsDiff(false);
			getLeft().setTreatAsDiff(false);
		}
	}

	/**
	 * save config
	 */
	public void _save() {
		if (m_appProps != null) {
			// remove passwords and URL authorization
			String strProxyPwd = m_appProps
					.getProperty(GlobalApplicationContext.PROXYPASSWORD);
			m_appProps.remove(GlobalApplicationContext.PROXYPASSWORD);
			String strUrlUsr = m_appProps
					.getProperty(GlobalApplicationContext.URLUSER);
			m_appProps.remove(GlobalApplicationContext.URLUSER);
			String strUrlPwd = m_appProps
					.getProperty(GlobalApplicationContext.URLPASSWORD);
			m_appProps.remove(GlobalApplicationContext.URLPASSWORD);

			_erase();
			BookmarkInfo.storeBookmarks(BOOKMARK, m_vecBookmarks, m_appProps);
			CommandClass.storeCommands(STARTER, m_starterCmd, m_appProps);

			m_appProps.store(
					ConfigPathUtil.getCurrentSavePath(APPNAME, PROPFILENAME),
					PROPFILEHEADER);
			// set passwords and URL authorization
			if (strProxyPwd != null)
				m_appProps.setProperty(GlobalApplicationContext.PROXYPASSWORD,
						strProxyPwd);
			if (strUrlUsr != null)
				m_appProps.setProperty(GlobalApplicationContext.URLUSER,
						strUrlUsr);
			if (strUrlPwd != null)
				m_appProps.setProperty(GlobalApplicationContext.URLPASSWORD,
						strUrlPwd);
		}
		// System.out.println( "save config");
		m_jshell.getShellEnv().getOut().println("save config");
		getRight()._save();
		if (!getLeft()
				.getFileInfoTable()
				.getFileInterface()
				.getId()
				.equals(getRight().getFileInfoTable().getFileInterface()
						.getId()))
			getLeft()._save();
	}

	/**
	 * load config
	 */
	public void _load() {
		if (m_appProps != null)
			m_appProps.load(ConfigPathUtil.getCurrentReadPath(APPNAME,
					PROPFILENAME));
		// System.out.println( "load config");
		m_jshell.getShellEnv().getOut().println("load config");
	}

	/**
	 * ececute command
	 * 
	 * @param cmd
	 *            command string
	 */
	public void _exec(CommandClass cmd) {
		String strCmd = "";
		if (cmd == null)
			return;
		FileInfoPanel fip = getActiveFileInfoPanel();
		FileInfoPanel fipTarget = null;
		if (fip == m_jPanelFileViewLeft)
			fipTarget = m_jPanelFileViewRight;
		else
			fipTarget = m_jPanelFileViewLeft;
		FileInterface fiSource = fip.getFileInfoTable()
				.getSelectedFileInterface();
		FileInterface fiStartDir = null;
		String strStartDir = cmd.getCompleteStartDir(fiSource, fipTarget
				.getFileInfoTable().getAbsolutePath());
		FileInterfaceConfiguration fic = FileConnector
				.createFileInterfaceConfiguration(null, 0, null, null, 0, 0,
						APPNAME, m_appProps, true);
		if (strStartDir != null && !strStartDir.equals(""))
			fiStartDir = FileConnector.createFileInterface(strStartDir, null,
					false, fic);
		else
			fiStartDir = FileConnector.createFileInterface(".", null, false,
					fic);
		strCmd = cmd.getCompleteCommand(fiSource, fipTarget.getFileInfoTable()
				.getAbsolutePath());
		if (strCmd != null) {
			strCmd = strCmd.trim();
			// System.out.println( "starting ...: " + strCmd );
			m_jshell.getShellEnv().getOut().println("starting ...: " + strCmd);
		}
		try {
			if (strCmd != null) {
				if (!strCmd.endsWith("&"))
					strCmd += "&";
				m_jshell.getShellEnv().setFileInterface(fiStartDir);
				JShellEngine.processCommands(strCmd, m_jshell.getShellEnv(),
						false);
			} else {
				fiStartDir.exec(null, cmd, m_jshell.getShellEnv().getOut(),
						m_jshell.getShellEnv().getErr(), null);
				// fiStartDir.exec( null, cmd, System.out, System.err, null );
				// m_jshell.getShellEnv().addProcess( new BackgroundThread(
				// strCmd, strTargetPath, fi));
			}
		} catch (Exception ex) {
			ex.printStackTrace(m_jshell.getShellEnv().getErr());
		}
		/*
		 * if( cmd != null) System.out.println( "starting ...: " + cmd); try {
		 * Process proc = null; if( cmd != null && !cmd.equals( "") &&
		 * !cmd.equals( " ")) proc = Runtime.getRuntime().exec( cmd); if( proc
		 * != null) { ProcessOutputThread pot = new ProcessOutputThread( proc,
		 * true, true); pot.start(); } } catch( IOException ioEx) {
		 * ioEx.printStackTrace(m_jshell.getShellEnv().getErr()); }
		 */
	}

	/*
	 * public void resetInputOutputStreams() { synchronized( m_jshell) {
	 * JShell.setMultiAppMode( false); m_ot.close(); m_ot.init(); m_ot.start();
	 * m_jshell.init(); JShell.setMultiAppMode( true); } }
	 */

	// Main-Methode
	public static void main(String[] args) {
		final FileManager applet = new FileManager();
		applet.isStandalone = true;
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				applet.exitApp();
				// System.exit(0);
			}
		});
		frame.setTitle("jDirWork");
		frame.getContentPane().add(applet, BorderLayout.CENTER);
		frame.setIconImage((new ImageIcon(haui.app.fm.FileInfoPanel.class
				.getResource("jdw.gif"))).getImage());
		applet.init();
		applet.setRootComponent(frame);
		// applet.getLeft().init( "C:\\temp", true, null);
		// applet.getRight().init( "C:\\temp", true, null);
		applet.getLeft().connect(FileInterface.LOCAL);
		applet.getRight().connect(FileInterface.LOCAL);
		applet.start();
		applet._initDim();
		frame.setVisible(true);
	}

	// static initializer for setting look & feel
	static {
		try {
			GlobalApplicationContext.instance().addApplicationClass(
					Class.forName("haui.app.fm.FileManager"));

			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
		}
	}

}