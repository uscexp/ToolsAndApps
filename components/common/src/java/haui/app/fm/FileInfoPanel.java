package haui.app.fm;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import cz.dhl.ftp.Ftp;
import cz.dhl.ftp.FtpConnect;
import haui.components.AuthorizationDialog;
import haui.components.CancelProgressDialog;
import haui.components.ConnectionManager;
import haui.components.JExDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.CgiTypeFile;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.FtpTypeFile;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.SocketTypeServerFileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.remote.CommPortConnection;
import haui.io.FileInterface.remote.SocketConnection;
import haui.io.StringBufferOutputStream;
import haui.resource.ResouceManager;
import haui.tool.CancelDlg;
import haui.tool.shell.JShell;
import haui.tool.shell.engine.JShellEngine;
import haui.util.AppProperties;
import haui.util.CommandClass;
import haui.util.ConfigPathUtil;
import haui.util.GlobalApplicationContext;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.Vector;
import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * Module:      FileInfoPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoPanel.java,v $ <p> Description: JPanel which contains a FileInfoTable with various buttons.<br> </p><p> Created:     30.10.2000 by AE </p><p>
 * @history      30.10.2000 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileInfoPanel.java,v $  Revision 1.4  2004-08-31 16:03:00+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.3  2004-06-22 14:08:48+02  t026843  bigger changes  Revision 1.2  2003-06-06 10:04:00+02  t026843  modifications because of the moving the 'TypeFile's to haui.io package  Revision 1.1  2003-05-28 14:19:46+02  t026843  reorganisations  Revision 1.14  2002-09-18 11:16:17+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.13  2002-09-03 17:07:55+02  t026843  - CgiTypeFile is now full functional.  - Migrated to the extended filemanager.pl script.  Revision 1.12  2002-08-28 14:22:44+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.11  2002-08-07 15:25:24+02  t026843  Ftp support via filetype added.  Some bugfixes.  Revision 1.10  2002-07-03 14:14:44+02  t026843  File context menu added  Revision 1.9  2002-07-01 16:01:20+02  t026843  _exec bugfix  Revision 1.8  2002-06-27 16:44:17+02  t026843  saves current dir and connection on exit  Revision 1.7  2002-06-27 15:51:42+02  t026843  sorting expanded and added as 'allways aktivated'  Revision 1.6  2002-06-26 12:06:45+02  t026843  History extended, simple bookmark system added.  Revision 1.5  2002-06-21 11:00:18+02  t026843  Added gzip and bzip2 file support  Revision 1.4  2002-06-19 16:13:53+02  t026843  Zip file support; writing doesn't work yet!  Revision 1.3  2002-06-17 17:19:18+02  t026843  Zip and Jar filetype read only support.  Revision 1.2  2002-06-11 09:32:52+02  t026843  bugfixes  Revision 1.1  2002-05-29 11:18:15+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.0  2001-07-20 16:34:22+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2000; $Revision: 1.4 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoPanel.java,v 1.4 2004-08-31 16:03:00+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.2  </p>
 */
public class FileInfoPanel extends JPanel
{
  private static final long serialVersionUID = 3109285813558758807L;
  
  // property constants
  final static String LOGONUSER = "LogonUser";
  final static String LOGONPASSWORD = "LogonPassword";
  final static String FILEMANAGERCGIURL = "FileManagerCgiUrl";
  final static String FTPURL = "FtpUrl";
  final static String FTPUSER = "FtpUser";
  final static String FTPPASSWORD = "FtpPassword";
  public final static String FIP = "Fip.";
  public final static String FIPPATH = "Path";
  final static String SOCKETHOST = "SocketHost";
  final static String SOCKETPORT = "SocketPort";
  final static String COMMPORT = "CommPort";

  // constants
  public final static int MAXBUTTONCOLUMNS = 5;
  public final static int MAXBUTTONROWS = 4;
  public final static String FILEEXT = ".fmc";
  public final static String NEWLOC = "new location";

  // member variables
  AppProperties m_appProps;
  AppProperties m_fipProps;
  boolean m_blInit = false;
  boolean m_blAutoLogon = false;
  boolean m_blTreatAsDiff = false;
  boolean m_blOverwriteAll = false;
  boolean m_blCloning = false;
  boolean m_blActive = false;
  String m_strConnName = null;
  FileInfoPanel m_fipTarget;
  ConnectionManager m_connLogon;
  ConnectionManager m_connLogoff;
  ConnectionManager m_connCmd;
  ConnectionManager m_connUp;
  Vector m_buttonCmd = new Vector();
  Vector m_fileCmd = new Vector();
  Object m_connObj;
  protected boolean m_blSuccess = false;

  // JShell member variable
  JShell m_jshell = null;

  // GUI
  AdpFocus m_adpFoc;
  LisAction m_actionlistener = new LisAction();
  MouseHandler m_mouseHandler = new MouseHandler();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  FileInfoTable m_fit = new FileInfoTable(null);
  JPanel m_jPanelButton = new JPanel();
  GridLayout m_gridLayoutButton = new GridLayout();
  JScrollPane m_jScrollPaneFit = new JScrollPane();
  JPanel m_jPanelFileView = new JPanel();
  BorderLayout m_borderLayoutFileView = new BorderLayout();
  JPanel m_jPanelFViewTop = new JPanel();
  GridLayout m_gridLayoutFViewTop = new GridLayout();
  JPanel m_jPanelRoots = new JPanel();
  JLabel m_jLabelInfo = new JLabel();
  JComboBox m_jComboBoxRoots = new JComboBox();
  JPanel m_jPanelRootsLeft = new JPanel();
  JPanel m_jPanelRootsRight = new JPanel();
  FlowLayout m_flowLayoutRootsRight = new FlowLayout();
  JButton m_jButtonRoot = new JButton();
  JButton m_jButtonParent = new JButton();
  JButton m_jButtonCmds[] = new JButton[MAXBUTTONCOLUMNS*MAXBUTTONROWS];
  JPanel m_jPanelCenter = new JPanel();
  BorderLayout m_borderLayoutCenter = new BorderLayout();
  CancelDlg m_cdlg;
  JPopupMenu m_jPopupMenuFileContext = new JPopupMenu();
  JMenuItem m_jMenuItemOpenWith = new JMenuItem();
  JMenuItem m_jMenuItemProperties = new JMenuItem();
  JMenuItem m_jMenuItemSplit = new JMenuItem();
  JMenuItem m_jMenuItemConcat = new JMenuItem();
  BorderLayout m_borderLayoutRootsLeft = new BorderLayout();
  BorderLayout m_borderLayoutRoots = new BorderLayout();
  JButton m_jButtonLabelPath = new JButton();
  JPopupMenu m_jPopupMenuButtonContext = new JPopupMenu();
  JMenuItem m_jMenuItemButEdit = new JMenuItem();
  JMenuItem m_jMenuItemButCopy = new JMenuItem();
  JMenuItem m_jMenuItemButPaste = new JMenuItem();
  JMenuItem m_jMenuItemButDelete = new JMenuItem();
  JButton m_jButtonCurrent;

  public FileInfoPanel( JShell jshell, boolean blToolBarLeft, AppProperties appProps)
  {
    try
    {
      m_appProps = appProps;
      m_fipProps = new AppProperties();
      m_jshell = jshell;
      for( int i = 0; i < MAXBUTTONCOLUMNS*MAXBUTTONROWS; i++) {
        FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
            FileManager.APPNAME, m_appProps, true);
        m_buttonCmd.add( new CommandClass( (new Integer( i)).toString(), "", "", "", -1, CommandClass.NO, true,
                this, fic));
      }
      m_fit = new FileInfoTable(m_fipProps)
      {
        private static final long serialVersionUID = 3109285813558758806L;
        
        void leftMousePressed( MouseEvent event)
        {
          super.leftMousePressed( event);
        }

        void leftMouseDoublePressed( MouseEvent event)
        {
          getRootPane().getParent().setCursor(new Cursor( Cursor.WAIT_CURSOR));
          if( m_blAutoLogon)
            m_fit.getFileInterface().logon();
          FileInterface fiSelected = m_fit.getSelectedFileInterface();
          if( fiSelected != null && fiSelected.isFile())
          {
            CommandClass cmd = CommandClass.searchCommand( m_fileCmd, fiSelected.getName(), CommandClass.LEFT);

            if( cmd == null && !fiSelected.isArchive())
              _exec( fiSelected, m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath(), null);

            _execFileExtCommand( cmd);
          }
          if( fiSelected != null && (fiSelected.isDirectory() || fiSelected.isArchive()))
          {
            if( fiSelected.getName().equals( "."))
            {
              getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
              return;
            }
            else if( fiSelected.getName().equals( ".."))
            {
              m_fit.parent();
              getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
              return;
            }
          }
          super.leftMouseDoublePressed( event);
          //m_fit.getFinfoHistory().add( m_fit.getFileInterface());
          updateLabels( true);
          //updateInfo();
          getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
        }

        void middleMousePressed( MouseEvent event)
        {
          super.middleMousePressed( event);
        }

        void middleMouseDoublePressed( MouseEvent event)
        {
          getRootPane().getParent().setCursor(new Cursor( Cursor.WAIT_CURSOR));
          setSelectionAtMousePos( event.getX(), event.getY());
          if( m_blAutoLogon)
            m_fit.getFileInterface().logon();
          FileInterface fiSelected = m_fit.getSelectedFileInterface();
          if( fiSelected != null && fiSelected.isFile())
          {
            CommandClass cmd = CommandClass.searchCommand( m_fileCmd, fiSelected.getName(), CommandClass.MIDDLE);

            if( cmd == null)
              _exec( fiSelected, fiSelected.getAbsolutePath(), null);

            _execFileExtCommand( cmd);
          }
          super.middleMouseDoublePressed( event);
          getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
        }

        void rightMousePressed( MouseEvent event)
        {
          super.rightMousePressed( event);
        }

        void rightMouseDoublePressed( MouseEvent event)
        {
          getRootPane().getParent().setCursor(new Cursor( Cursor.WAIT_CURSOR));
          setSelectionAtMousePos( event.getX(), event.getY());
          if( m_blAutoLogon)
            m_fit.getFileInterface().logon();
          FileInterface fiSelected = m_fit.getSelectedFileInterface();
          if( fiSelected != null && fiSelected.isFile())
          {
            CommandClass cmd = CommandClass.searchCommand( m_fileCmd, fiSelected.getName(), CommandClass.RIGHT);

            if( cmd == null)
              _exec( fiSelected, fiSelected.getAbsolutePath(), null);

            _execFileExtCommand( cmd);
          }
          super.rightMouseDoublePressed( event);
          getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
        }

        void leftMouseReleased( MouseEvent event)
        {
          super.leftMouseReleased( event);
          updateInfo();
        }

        public void back()
        {
          super.back();
          updateLabels( true);
        }

        public void next()
        {
          super.next();
          updateLabels( true);
        }

        public void parent()
        {
          super.parent();
          updateLabels( true);
        }

        public void root()
        {
          super.root();
          this.init( (String)m_jComboBoxRoots.getSelectedItem(), null, m_fit.getConnectionObject(), false);
          //m_fit.repaint();
          updateLabels( true);
        }

        public void setValueAt( Object aValue, int row, int column)
        {
          super.setValueAt( aValue, row, column);
          //super.repaint();
          refresh();
          if( m_fipTarget != null && m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath().equals( m_fit.getFileInterface().getAbsolutePath()))
          {
            m_fipTarget.refresh();
          }
        }
      };
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace(m_jshell.getShellEnv().getErr());
    }
    m_gridLayoutButton.setColumns( MAXBUTTONCOLUMNS);
    m_gridLayoutButton.setRows( MAXBUTTONROWS);

    for( int i = 0; i < MAXBUTTONCOLUMNS*MAXBUTTONROWS; i++)
    {
        m_jButtonCmds[i] = new JButton( (new Integer( i)).toString());
        m_jButtonCmds[i].setFont(new java.awt.Font("Dialog", 1, 11));
        m_jButtonCmds[i].setPreferredSize(new Dimension(64, 18));
        m_jButtonCmds[i].setMinimumSize(new Dimension(18, 18));
        m_jButtonCmds[i].setMaximumSize(new Dimension(256, 18));
        m_jButtonCmds[i].addMouseListener( m_mouseHandler);
        m_jButtonCmds[i].addActionListener( m_actionlistener);
        m_jPanelButton.add( m_jButtonCmds[i]);
    }

    m_jMenuItemOpenWith.addActionListener( m_actionlistener);
    m_jMenuItemSplit.addActionListener( m_actionlistener);
    m_jMenuItemConcat.addActionListener( m_actionlistener);
    m_jMenuItemProperties.addActionListener( m_actionlistener);
    m_jMenuItemButEdit.addActionListener( m_actionlistener);
    m_jMenuItemButCopy.addActionListener( m_actionlistener);
    m_jMenuItemButPaste.addActionListener( m_actionlistener);
    m_jMenuItemButDelete.addActionListener( m_actionlistener);
    m_jButtonParent.addActionListener( m_actionlistener);
    m_jButtonRoot.addActionListener( m_actionlistener);

    m_fit.setPopup( m_jPopupMenuFileContext);

    LisItem itemlistener = new LisItem();
    m_jComboBoxRoots.addItemListener( itemlistener);

    m_adpFoc = new AdpFocus();
    m_fit.addFocusListener( m_adpFoc);
    m_jScrollPaneFit.addFocusListener( m_adpFoc);
    m_jButtonLabelPath.addFocusListener( m_adpFoc);
    m_jLabelInfo.addFocusListener( m_adpFoc);
    m_jComboBoxRoots.addFocusListener( m_adpFoc);
    //m_jLabelRoots.addFocusListener( m_adpFoc);
    m_jButtonRoot.addFocusListener( m_adpFoc);
    m_jButtonParent.addFocusListener( m_adpFoc);
    for( int i = 0; i < MAXBUTTONCOLUMNS*MAXBUTTONROWS; i++)
    {
        m_jButtonCmds[i].addFocusListener( m_adpFoc);
    }
  }

  //Initialisation of the component
  private void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_gridLayoutButton);
    m_gridLayoutButton.setColumns(4);
    m_gridLayoutButton.setRows(3);
    m_jPanelCenter.setLayout(m_borderLayoutCenter);
    m_jButtonRoot.setMaximumSize(new Dimension(21, 21));
    m_jButtonRoot.setMinimumSize(new Dimension(21, 21));
    m_jButtonRoot.setPreferredSize(new Dimension(21, 21));
    m_jButtonRoot.setToolTipText("Root directory");
    m_jButtonRoot.setActionCommand("/");
    m_jButtonRoot.setIcon(ResouceManager.getCommonImageIcon( FileManager.APPNAME, "slash.gif"));
    m_jButtonParent.setMaximumSize(new Dimension(21, 21));
    m_jButtonParent.setMinimumSize(new Dimension(21, 21));
    m_jButtonParent.setPreferredSize(new Dimension(21, 21));
    m_jButtonParent.setToolTipText("Parent directory");
    m_jButtonParent.setActionCommand("..");
    m_jButtonParent.setIcon(ResouceManager.getCommonImageIcon( FileManager.APPNAME, "up.gif"));
    m_jMenuItemSplit.setMnemonic((int)'S');
    m_jMenuItemSplit.setActionCommand("split");
    m_jMenuItemSplit.setText("Split");
    m_jMenuItemConcat.setText("Concatenate");
    m_jMenuItemConcat.setActionCommand("concat");
    m_jMenuItemConcat.setMnemonic((int)'C');
    m_gridLayoutFViewTop.setRows(1);
    m_jPanelRootsRight.setBackground(Color.blue);
    m_jButtonLabelPath.setBackground(Color.blue);
    m_jButtonLabelPath.setForeground(Color.white);
    m_jButtonLabelPath.setAlignmentY((float) 0.0);
    m_jButtonLabelPath.setPreferredSize(new Dimension(55, 20));
    m_jButtonLabelPath.setBorderPainted(false);
    m_jButtonLabelPath.setContentAreaFilled(true);
    m_jButtonLabelPath.setHorizontalAlignment(SwingConstants.LEFT);
    m_jButtonLabelPath.setHorizontalTextPosition(SwingConstants.LEFT);
    m_jButtonLabelPath.setText("Path");
    m_jMenuItemButEdit.setMnemonic((int)'O');
    m_jMenuItemButEdit.setActionCommand("butedit");
    m_jMenuItemButEdit.setText("Edit");
    m_jMenuItemButCopy.setMnemonic((int)'O');
    m_jMenuItemButCopy.setActionCommand("butcopy");
    m_jMenuItemButCopy.setText("Copy");
    m_jMenuItemButPaste.setText("Paste");
    m_jMenuItemButPaste.setActionCommand("butpaste");
    m_jMenuItemButPaste.setMnemonic((int)'O');
    m_jMenuItemButDelete.setText("Delete");
    m_jMenuItemButDelete.setActionCommand("butdelete");
    m_jMenuItemButDelete.setMnemonic((int)'O');
    this.add(m_jPanelButton, BorderLayout.SOUTH);
    m_jComboBoxRoots.setPreferredSize(new Dimension(45, 20));
    m_jPanelFileView.setLayout(m_borderLayoutFileView);
    m_jPanelFViewTop.setLayout(m_gridLayoutFViewTop);
    m_gridLayoutFViewTop.setColumns(1);
    m_jPanelRoots.setLayout(m_borderLayoutRoots);
    m_jLabelInfo.setText("Info");
    m_jPanelRootsLeft.setLayout(m_borderLayoutRootsLeft);
    m_jPanelRootsRight.setLayout(m_flowLayoutRootsRight);
    m_flowLayoutRootsRight.setAlignment(FlowLayout.RIGHT);
    m_flowLayoutRootsRight.setHgap(0);
    m_flowLayoutRootsRight.setVgap(0);
    this.add(m_jPanelFileView, BorderLayout.CENTER);
    m_jPanelFileView.add(m_jPanelFViewTop, BorderLayout.NORTH);
    m_jPanelFViewTop.add(m_jPanelRoots, null);
    m_jPanelRoots.add(m_jPanelRootsLeft, BorderLayout.CENTER);
    m_jPanelRootsLeft.add(m_jComboBoxRoots, BorderLayout.WEST);
    m_jPanelRootsLeft.add(m_jButtonLabelPath,  BorderLayout.CENTER);
    m_jPanelRoots.add(m_jPanelRootsRight,  BorderLayout.EAST);
    m_jPanelRootsRight.add(m_jButtonRoot, null);
    m_jPanelRootsRight.add(m_jButtonParent, null);
    m_jPanelFileView.add(m_jLabelInfo, BorderLayout.SOUTH);
    m_jPanelFileView.add(m_jPanelCenter, BorderLayout.CENTER);
    m_jPanelCenter.add(m_jScrollPaneFit, BorderLayout.CENTER);
    m_jScrollPaneFit.getViewport().add(m_fit, null);
    m_jMenuItemOpenWith.setText("Open with");
    m_jMenuItemOpenWith.setActionCommand("openwith");
    m_jMenuItemOpenWith.setMnemonic((int)'O');
    m_jPopupMenuFileContext.add(m_jMenuItemOpenWith);
    m_jMenuItemProperties.setText("Properties");
    m_jMenuItemProperties.setActionCommand("fileproperties");
    m_jMenuItemProperties.setMnemonic((int)'P');
    m_jPopupMenuFileContext.add(m_jMenuItemSplit);
    m_jPopupMenuFileContext.add(m_jMenuItemConcat);
    m_jPopupMenuFileContext.add(m_jMenuItemProperties);
    m_jPopupMenuButtonContext.add(m_jMenuItemButEdit);
    m_jPopupMenuButtonContext.add(m_jMenuItemButCopy);
    m_jPopupMenuButtonContext.add(m_jMenuItemButPaste);
    m_jPopupMenuButtonContext.add(m_jMenuItemButDelete);
  }

  public void cloneTargetFip()
  {
    m_blCloning = true;
    setButtonCommandVector( (Vector)m_fipTarget.getButtonCommandVector().clone());
    setFileExtCommandVector( (Vector)m_fipTarget.getFileExtCommandVector().clone());
    if( m_fipTarget.getCmdCM() != null)
      m_connCmd = (ConnectionManager)m_fipTarget.getCmdCM().clone();
    if( m_fipTarget.getLogOnCM() != null)
      m_connLogon = (ConnectionManager)m_fipTarget.getLogOnCM().clone();
    if( m_fipTarget.getLogOffCM() != null)
      m_connLogoff = (ConnectionManager)m_fipTarget.getLogOffCM().clone();
    m_fipProps = m_fipTarget.getFipProps();
    m_fit.setFileInterface( (FileInterface)m_fipTarget.getFileInfoTable().getFileInterface().duplicate());
    m_fit.setPopup( m_fit.getPopup());
    m_fit.setHeaderPopup( m_fit.getHeaderPopup());
    FileInfoTableModel fitm = (FileInfoTableModel)m_fit.getModel();
    fitm.setFileInterfaceVector( m_fit.getFileInterface()._listFiles( m_fit.getFileInterfaceFilter()));
    m_fit.getColumnModel().getColumn( 0).setCellRenderer( m_fit.getFIICR());
    m_fit.getColumnModel().getColumn( 1).setCellEditor( m_fit.getFINCE());
    m_fit.getColumnModel().getColumn( 3).setCellRenderer( m_fit.getFINFCR());
    m_fit.setToolTipText( "FIView");
    updateRootCombo();
    setRootSelection( m_fipTarget.getRootSelection());
    fitm.sort(-1);
    updateLabels( true);
    fitm.forceRedraw();
    m_blCloning = false;
  }

  public void init( String strPath, String strParentPath, Object connObj, boolean blUpdate)
  {
    //if( m_blAutoLogon)
      //m_fit.getFileInterface().logon();
    if( !strPath.endsWith( "..") && strPath.endsWith( ".") && !strPath.equals( "."))
      strPath = strPath.substring( 0, strPath.length()-1);
    m_fit.setAppProperties( m_fipProps);
    m_fit.init( strPath, strParentPath, connObj, blUpdate);
    //String strRoots[] = null;
    m_blInit = true;
    updateRootCombo();
    updateRootSelection();
    updateLabels( true);
    m_blInit = false;
  }

  public void init( FileInterface fi)
  {
    m_fit.init( fi);
    //String strRoots[] = null;
    m_blInit = true;
    updateRootCombo();
    updateRootSelection();
    updateLabels( true);
    m_blInit = false;
  }

  public FinfoHistory getFinfoHistory()
  {
    return m_fit.getFinfoHistory();
  }

  public void updateRootCombo()
  {
    FileInterface[] fiRoots = null;
    m_blCloning = true;
    if( (fiRoots = m_fit.getFileInterface()._listRoots()) != null)
    {
      m_jComboBoxRoots.removeAllItems();
      for( int i = 0; i < fiRoots.length; i++)
      {
        m_jComboBoxRoots.addItem( fiRoots[i].getAbsolutePath());
      }
    }
    m_blCloning = false;
  }

  public void updateRootSelection()
  {
    FileInterface[] fiRoots = null;
    m_blCloning = true;
    if( (fiRoots = m_fit.getFileInterface()._listRoots()) != null)
    {
      int iLen = fiRoots[0].getAbsolutePath().length();
      String strParent = m_fit.getFileInterface().getAbsolutePath();
      if( strParent != null)
      {
        if( strParent.indexOf( m_fit.getFileInterface().separatorChar()) == -1)
          strParent += m_fit.getFileInterface().separatorChar();
        strParent = strParent.substring( 0, iLen);
        m_jComboBoxRoots.setSelectedItem( strParent);
        //m_jLabelRoots.setText( strParent);
      }
    }
    m_blCloning = false;
  }

  public int getRootSelection()
  {
    return m_jComboBoxRoots.getSelectedIndex();
  }

  public void setRootSelection( int i)
  {
    m_jComboBoxRoots.setSelectedIndex( i);
  }

  public void updateInfo()
  {
    long[] lDirCombi = FileConnector.lengthAndCountOfFilesOfDir( m_fit.getFileInterface(),
      m_fit.getFileInterfaceFilter(), false);
    long[] lSelCombi = FileConnector.lengthAndCountOfFiles( m_fit.getSelectedFileInterfaces(),
      m_fit.getFileInterfaceFilter(), false);
    NumberFormat nf = NumberFormat.getInstance();
    StringBuffer strbufLabel = new StringBuffer( nf.format( lSelCombi[0]));
    strbufLabel.append( " of ");
    strbufLabel.append( nf.format( lDirCombi[0]));
    strbufLabel.append( " in ");
    strbufLabel.append( lSelCombi[1]);
    strbufLabel.append( " of ");
    strbufLabel.append( lDirCombi[1]);
    strbufLabel.append( " files selected");
    m_jLabelInfo.setText( strbufLabel.toString());
  }

  public void updateLabels( boolean blUpdInfo)
  {
    String strFilter = "";

    if( m_fit.getFileInterfaceFilter() != null)
      strFilter = " (" + m_fit.getFileInterfaceFilter().getDescription() + ")";
    String strText = m_fit.getAbsolutePath() + strFilter;
    m_jButtonLabelPath.setText( strText);
    m_jButtonLabelPath.setToolTipText( strText);
    if( blUpdInfo)
      updateInfo();
  }

  public void setActive()
  {
    setActive( true);
  }

  public void setActive( boolean blUpdateTarget)
  {
    if( m_fipTarget != null && blUpdateTarget)
      m_fipTarget.setInactive( false);
    m_blActive = true;
    m_jPanelRootsRight.setBackground( Color.blue );
    m_jButtonLabelPath.setBackground( Color.blue );
  }

  public void setInactive()
  {
    setInactive( true);
  }

  public void setInactive(  boolean blUpdateTarget)
  {
    if( m_fipTarget != null && blUpdateTarget)
      m_fipTarget.setActive( false);
    m_blActive = false;
    m_jPanelRootsRight.setBackground( Color.gray );
    m_jButtonLabelPath.setBackground( Color.gray );
  }

  public boolean isActive()
  {
    return m_blActive;
  }

  public FileInfoTable getFileInfoTable()
  {
    return m_fit;
  }

  public AppProperties getFipProps()
  {
    return m_fipProps;
  }

  public JShell getShell()
  {
    return m_jshell;
  }

  public ConnectionManager getCmdCM()
  {
    return m_connCmd;
  }

  public ConnectionManager getLogOnCM()
  {
    return m_connLogon;
  }

  public ConnectionManager getLogOffCM()
  {
    return m_connLogoff;
  }

  public String getConnectionName()
  {
    return m_strConnName;
  }

  public void refresh()
  {
    init( m_fit.getAbsolutePath(), m_fit.getParentPath(), m_fit.getConnectionObject(), true);
  }

  public void connect( String strId)
  {
    connect( strId, ".");
  }

  public void connect( String strId, String strPath)
  {
    String strFile;
    int idx = -1;
    String strOldConnId = m_strConnName;
    if( strOldConnId != null)
      idx = strOldConnId.lastIndexOf( FILEEXT);
    if( idx != -1)
      strOldConnId = strOldConnId.substring( 0, idx);
    ConnectionManager cmFm = null;
    String strPrefix = FIP + strId + FILEEXT + ".";
    if( strId == null)
    {
      ConnectDialog dlg = new ConnectDialog( this, "Select location", true, null);
      dlg.setVisible( true);

      if( dlg.isCanceled())
        return;
      strId = dlg.getSelection();
      if( strId.equals( NEWLOC))
      {
        if( !configFileExtCommands())
          return;
      }
      if( strId == null)
        return;
      m_strConnName = null;
    }
    strPrefix = FIP + strId + FILEEXT + ".";

    String strConnId = strId;
    if( strConnId != null)
      idx = strConnId.lastIndexOf( FILEEXT);
    if( idx != -1)
      strConnId = strConnId.substring( 0, idx);
    if( m_fit.getFileInterface() != null)
    {
      String strSourceId = m_fit.getFileInterface().getId();
      String strTargetId = m_fipTarget.getFileInfoTable().getFileInterface().getId();
      if( !strSourceId.equals( strTargetId )
           || m_blTreatAsDiff )
        m_fit.getFileInterface().logoff();
    }
    //boolean blLocal = true;
    m_connObj = null;
    m_connCmd = null;
    m_connLogoff = null;
    m_connLogon = null;
    m_connUp = null;
    _erase( strConnId);
    AppProperties fipProps = new AppProperties();

    if( strId.equals( FileInterface.LOCAL))
    {
      //blLocal = true;
      strFile = strId + FILEEXT;
      File file = new File( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFile));
      if( file.exists() && fipProps.load( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFile)))
      {
        if( strPath.equals( "."))
          strPath = fipProps.getProperty( strPrefix + FIPPATH);
      }
      m_strConnName = strFile;
    }
    else if( strId.startsWith( FileInterface.SOCKET))
    {
      //blLocal = false;
      String strHost = null;
      int iPort = -1;
      if( strId.equals( FileInterface.SOCKET))
      {
        SocketConnectDialog scd = new SocketConnectDialog( ( Frame )null, "Insert socket connection parameter", true);
        scd.setFrame( this);
        scd.setPort( SocketTypeServerFileInterfaceConfiguration.DEFAULTPORT );
        scd.setVisible( true );
        if( scd.isCanceled() )
        {
          scd.dispose();
          return;
        }
        strHost = scd.getHost();
        iPort = scd.getPort();
        fipProps.setProperty( SOCKETHOST, strHost);
        fipProps.setIntegerProperty( SOCKETPORT, new Integer( iPort));
        strId = FileInterface.SOCKET + "-" + strHost;
        strConnId = strId;
        strFile = strId + FILEEXT;
        scd.dispose();
      }
      else
      {
        idx = -1;
        if( strId != null)
          idx = strId.lastIndexOf( FILEEXT);
        if( idx != -1)
          strFile = strId;
        else
          strFile = strId + FILEEXT;
      }
      File file = new File( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFile));
      if( file.exists() && fipProps.load( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFile)))
      {
        if( strPath.equals( "."))
          strPath = fipProps.getProperty( strPrefix + FIPPATH);
        if( strHost == null)
        {
          strHost = fipProps.getProperty( SOCKETHOST );
          iPort = fipProps.getIntegerProperty( SOCKETPORT ).intValue();
        }
      }
      m_strConnName = strFile;
      if( iPort == -1)
        iPort = SocketTypeServerFileInterfaceConfiguration.DEFAULTPORT;
      SocketConnection sc = new SocketConnection( strHost, iPort);
      m_connObj = sc;
    }
    else if( strId.startsWith( FileInterface.COMMPORT))
    {
      //blLocal = false;
      CommPortIdentifier portId = null;
      int iBaud = 9600;
      int iMode = ParallelPort.LPT_MODE_ECP;
      if( strId.equals( FileInterface.COMMPORT))
      {
        CommPortConnectDialog ccd = new CommPortConnectDialog( ( Frame )null, "Select comm port", true );
        ccd.setFrame( this);
        ccd.setVisible( true );
        if( ccd.isCanceled() )
        {
          ccd.dispose();
          return;
        }
        portId = ccd.getCommPortId();
        iMode = ccd.getMode();
        iBaud = ccd.getBaud();
        fipProps.setProperty( COMMPORT, portId.getName());
        strId = FileInterface.COMMPORT + "-" + portId.getName();
        strConnId = strId;
        strFile = strId + FILEEXT;
        ccd.dispose();
      }
      else
      {
        idx = -1;
        if( strId != null)
          idx = strId.lastIndexOf( FILEEXT);
        if( idx != -1)
          strFile = strId;
        else
          strFile = strId + FILEEXT;
      }
      String strPort = null;
      File file = new File( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFile));
      if( file.exists() && fipProps.load( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFile)))
      {
        if( strPath.equals( "."))
          strPath = fipProps.getProperty( strPrefix + FIPPATH);
        if( strPort == null)
        {
          strPort = fipProps.getProperty( COMMPORT );
          try
          {
            portId = CommPortIdentifier.getPortIdentifier( strPort );
          }
          catch( Exception ex )
          {
            ex.printStackTrace(m_jshell.getShellEnv().getErr());
          }
        }
      }
      m_strConnName = strFile;
      if( portId == null)
        portId = (CommPortIdentifier)CommPortIdentifier.getPortIdentifiers().nextElement();
      CommPortConnection cc = new CommPortConnection( FileManager.APPNAME, portId, iMode, iBaud);
      m_connObj = cc;
    }
    else
    {
      if( !strId.equals( NEWLOC))
      {
        strFile = strId + FILEEXT;
        File file = new File( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFile));
        if( file.exists() && fipProps.load( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFile)))
        {
          if( strPath.equals( "."))
            strPath = fipProps.getProperty( strPrefix + FIPPATH);
          if( strPath == null)
            strPath = ".";
        }
        m_strConnName = strFile;
      }
      else
      {
        //strPath = ".";
        m_strConnName = null;
      }
      if( fipProps.getProperty( FileInfoPanel.FTPURL) != null)
      {
        String url = fipProps.getProperty( FileInfoPanel.FTPURL);
        String user = fipProps.getProperty( FileInfoPanel.FTPUSER);
        String pwd = fipProps.getProperty( FileInfoPanel.FTPPASSWORD);
        if( url == null)
        {
          //System.err.println( "Ftp url not defined!");
          m_jshell.getShellEnv().getOut().println( "Ftp url not defined!");
          strId = null;
        }
        if( user == null || pwd == null)
        {
          AppProperties tmpProps = new AppProperties();
          if( user != null)
            tmpProps.setProperty( GlobalApplicationContext.URLUSER, user);
          if( pwd != null)
            tmpProps.setProperty( GlobalApplicationContext.URLPASSWORD, pwd);
          AuthorizationDialog dlg = new AuthorizationDialog( null, "Ftp authorization", true, tmpProps,
                                        AuthorizationDialog.URLAUTH, FileManager.APPNAME);
          dlg.setFrame( this);
          dlg.setVisible( true);
          user = tmpProps.getProperty( GlobalApplicationContext.URLUSER);
          pwd = tmpProps.getProperty( GlobalApplicationContext.URLPASSWORD);
        }
        String[] args;
        if( user != null)
        {
          args = new String[3];
          args[0] = url;
          args[1] = "-user";
          args[2] = user;
        }
        else
        {
          args = new String[1];
          args[0] = url;
        }
        FtpConnect fc = FtpConnect.newConnect( args);
        if( pwd != null)
        {
          fc.setPassWord( pwd);
          fipProps.setProperty( FileInfoPanel.FTPPASSWORD, pwd);
        }
        Ftp f = new Ftp();
        try
        { /* connect & login to host */
          f.setContextOutputStream( m_jshell.getShellEnv().getOut());
          f.connect( fc);
          if( strPath == null || strPath.equals( "."))
            strPath = f.pwd();
        }
        catch (IOException ioex)
        {
          ioex.printStackTrace(m_jshell.getShellEnv().getErr());
        }
        m_connObj = f;
        //blLocal = false;
      }
      else if( fipProps.getProperty( FileInfoPanel.FILEMANAGERCGIURL) != null)
      {
        cmFm = new ConnectionManager( fipProps.getProperty( FILEMANAGERCGIURL), m_appProps, FileManager.APPNAME);
        m_connObj = cmFm;
        //blLocal = false;
      }
      else
      {
        //System.err.println( "Neither FileManager cgi url nor Ftp url defined!");
        m_jshell.getShellEnv().getErr().println( "Neither FileManager cgi url nor Ftp url defined!");
        strId = null;
      }
      //}
      if( m_fipTarget.getFileInfoTable().getFileInterface() != null && m_fipTarget.getFileInfoTable().getFileInterface().getId().equals( strId)
         && !m_blTreatAsDiff /*&& m_fit.getFileInterface().getId().equals( FileInterface.LOCAL)*/)
      {
        cloneTargetFip();
        return;
      }
    }
    if( strOldConnId != null && !strOldConnId.equalsIgnoreCase( strConnId))
    {
      m_fit.getFinfoHistory().removeAll();
      m_fit.clearRootHist();
    }
    m_fipProps = fipProps;
    if( strPath != null)
    {
      init( strPath, null, m_connObj, false);
      fipProps = m_fit.getFileInterface().getAppProperties();
      if( fipProps != null)
        m_fipProps = fipProps;
      strId = this.getFileInfoTable().getFileInterface().getId();
      if( strOldConnId == null || (strOldConnId != null && !strOldConnId.equals( strId)))
      {
        FipConnection fipCon = new FipConnection( strId + FILEEXT, FileManager.APPNAME, m_fipProps);
        fipCon._init();
        if( fipCon.getFileExtCommands() != null && fipCon.getFileExtCommands().size() > 0)
          setFileExtCommandVector( fipCon.getFileExtCommands());
        if( fipCon.getButtonCommands() != null && fipCon.getButtonCommands().size() == MAXBUTTONCOLUMNS*MAXBUTTONROWS)
          setButtonCommandVector( fipCon.getButtonCommands());
      }
    }
    else
    {
      //m_fit.getFileInterface().logon();
      init( ".", null, m_connObj, false);
    }
    updateLabels( true);
  }

  /**
   * read remote std out (temp output file) and print it to system out
   *
   * @param cm ConnectionManager to read from
   */
  public void readToStdOut( ConnectionManager cm)
  {
    // Read lines from cgi-script.
    String line = null;
    int iIdx = -1;
    while( !(line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    while((line = cm.readLine()) != null)
    {
      if( ( iIdx = line.indexOf( "<EOF>")) != -1)
      {
        line = line.substring( 0, iIdx);
        //System.out.println( line);
        m_jshell.getShellEnv().getOut().println( line);
        break;
      }
      //System.out.println( line);
      m_jshell.getShellEnv().getOut().println( line);
    }
  }

  public void setTargetFileInfoTable( FileInfoPanel fipTarget)
  {
    m_fipTarget = fipTarget;
  }

  public FileInterfaceFilter getFileInterfaceFilter()
  {
    return m_fit.getFileInterfaceFilter();
  }

  public void setFileInterfaceFilter( FileInterfaceFilter fif)
  {
    m_fit.setFileInterfaceFilter( fif);
  }

  public boolean getAutoLogon()
  {
    return m_blAutoLogon;
  }

  public void setAutoLogon( boolean bl)
  {
    m_blAutoLogon = bl;
  }

  public void setTreatAsDiff( boolean bl)
  {
    m_blTreatAsDiff = bl;
  }

  /**
   * set file extension command Vector
   *
   * @param cmd Vector of CommandClass(es)
   */
  public void setFileExtCommandVector( Vector cmd)
  {
    m_fileCmd = cmd;
  }

  /**
   * get file extension command Vector
   */
  public Vector getFileExtCommandVector()
  {
    return m_fileCmd;
  }

  /**
   * set button command Vector
   *
   * @param cmd Vector of CommandClass(es)
   */
  public void setButtonCommandVector( Vector cmd)
  {
    m_buttonCmd = cmd;
    for( int i = 0; i < MAXBUTTONCOLUMNS*MAXBUTTONROWS; i++)
    {
      m_jButtonCmds[i].setText( ((CommandClass)m_buttonCmd.elementAt(i)).getIdentifier());
      m_jButtonCmds[i].setToolTipText( ((CommandClass)m_buttonCmd.elementAt(i)).toString());
      m_jButtonCmds[i].setMnemonic( ((CommandClass)m_buttonCmd.elementAt(i)).getMnemonic());
    }
  }

  /**
   * get button command Vector
   */
  public Vector getButtonCommandVector()
  {
    return m_buttonCmd;
  }

  /**
   * add a file extension command
   *
   * @param cmd CommandClass
   */
  public void addFileExtCommand( CommandClass cmd)
  {
    m_fileCmd.add( cmd);
  }

  /**
   * remove a file extension command
   *
   * @param cmd CommandClass
   */
  public void removeFileExtCommand( CommandClass cmd)
  {
    m_fileCmd.remove( cmd);
  }

  /**
   * assign command to a button
   *
   * @param cmd CommandClass
   * @param iIdx index of the button
   * @param blSync syncronize flag
   *
   * @throws IndexOutOfBoundsException if iIdx < 0 || iIdx > MAXBUTTONCOLUMNS*MAXBUTTONROWS
   */
  public void setButtonCommand( CommandClass cmd, int iIdx, boolean blSync)
  {
    if( iIdx < 0 || iIdx > MAXBUTTONCOLUMNS*MAXBUTTONROWS)
      throw new IndexOutOfBoundsException();

    m_buttonCmd.setElementAt( cmd, iIdx);
    m_jButtonCmds[iIdx].setText( cmd.getIdentifier());
    m_jButtonCmds[iIdx].setToolTipText( cmd.toString());
    m_jButtonCmds[iIdx].setMnemonic( cmd.getMnemonic());

    if( blSync)
      synchronizeButton( m_buttonCmd);
  }

  private class AdpFocus
    extends FocusAdapter
  {
    public void focusGained( FocusEvent event)
    {
      //Object object = event.getSource();
      setActive();
    }
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      getRootPane().getParent().setCursor( new Cursor( Cursor.WAIT_CURSOR));
      String cmd = event.getActionCommand();
      if (cmd.equals( ".."))
        m_fit.parent();
      else if (cmd.equals( "/"))
        m_fit.root();
      else if (cmd.equals( "fileproperties"))
        showFileProperties();
      else if (cmd.equals( "openwith"))
        showOpenWith();
      else if (cmd.equals( "split"))
        onSplit();
      else if (cmd.equals( "concat"))
        onConcat();
      else if (cmd.equals( "butedit"))
        onButEdit();
      else if (cmd.equals( "butcopy"))
        onButCopy();
      else if (cmd.equals( "butpaste"))
        onButPaste();
      else if (cmd.equals( "butdelete"))
        onButDelete();
      else
      {
        //boolean blSuccess = false;
        int i = 0;
        for( i = 0; i < m_buttonCmd.size(); i++)
        {
          if( m_buttonCmd.elementAt( i) == null)
            break;
          if( cmd.equals( ((CommandClass)m_buttonCmd.elementAt( i)).getIdentifier()))
          {
            String strDest = null;
            if( m_fipTarget != null && m_fipTarget.getFileInfoTable() != null)
              strDest = m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath();
            _exec( m_fit.getSelectedFileInterface(), strDest, (CommandClass)m_buttonCmd.elementAt( i));
            //blSuccess = true;
            break;
          }
        }
      }
      getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
    }
  }

  protected void onTerminal()
  {
    String strProxyHost = m_appProps.getProperty( GlobalApplicationContext.PROXYHOST);
    String strProxyList = m_appProps.getProperty( GlobalApplicationContext.PROXYNOPROXYLIST);
    String strProxyUsr = m_appProps.getProperty( GlobalApplicationContext.PROXYUSER);
    String strProxyPwd = m_appProps.getProperty( GlobalApplicationContext.PROXYPASSWORD);
    boolean blProxyEnable = m_appProps.getBooleanProperty( GlobalApplicationContext.PROXYENABLE);
    Integer iProxyPort = m_appProps.getIntegerProperty( GlobalApplicationContext.PROXYPORT);
    if( strProxyHost != null)
      m_fipProps.setProperty( GlobalApplicationContext.PROXYHOST, strProxyHost);
    if( strProxyList != null)
      m_fipProps.setProperty( GlobalApplicationContext.PROXYNOPROXYLIST, strProxyList);
    if( strProxyUsr != null)
      m_fipProps.setProperty( GlobalApplicationContext.PROXYUSER, strProxyUsr);
    if( strProxyPwd != null)
      m_fipProps.setProperty( GlobalApplicationContext.PROXYPASSWORD, strProxyPwd);
    m_fipProps.setBooleanProperty( GlobalApplicationContext.PROXYENABLE, blProxyEnable);
    if( iProxyPort != null)
      m_fipProps.setIntegerProperty( GlobalApplicationContext.PROXYPORT, iProxyPort);
    m_fit.getFileInterface().startTerminal();
  }

  protected void onBsh()
  {
    final JExDialog dlg = new JExDialog( null, "Bsh console", false, FileManager.APPNAME);
    final JConsole console = new JConsole();
    console.setPreferredSize( new Dimension( 640, 480));
    final Interpreter interpreter = new Interpreter(console);
    try
    {
      // do not allow System.exit() with exit() command
      interpreter.set( "bsh.system.shutdownOnExit", false);
    }
    catch( EvalError ex)
    {
      ex.printStackTrace();
    }
    dlg.addWindowListener( new WindowAdapter()
    {
      public void windowClosing( WindowEvent event )
      {
        Object object = event.getSource();
        if( object == dlg )
        {
          super.windowClosing( event);
          dlg.dispose();
        }
      }
    } );
    dlg.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE);
    dlg.getContentPane().add("Center", console);
    dlg.pack();
    dlg.setVisible( true);
    (new Thread( interpreter)).start();
  }

  protected void onSplit()
  {
    FileSplitDialog fspDlg = new FileSplitDialog( (Frame)null, "Split file", true, m_fit.getSelectedFileInterface());
    fspDlg.setVisible( true);
    if( m_fipTarget != null && m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath().equals( m_fit.getFileInterface().getAbsolutePath()))
    {
      m_fipTarget.refresh();
    }
    refresh();
  }

  protected void onConcat()
  {
    FileConcatDialog fcDlg = new FileConcatDialog( (Frame)null, "Concatenate files", true, m_fit.getSelectedFileInterfaces(), m_appProps);
    fcDlg.setVisible( true);
    if( m_fipTarget != null && m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath().equals( m_fit.getFileInterface().getAbsolutePath()))
    {
      m_fipTarget.refresh();
    }
    refresh();
  }

  protected void onButEdit()
  {
    if( m_jButtonCurrent != null)
    {
      for( int i = 0; i < MAXBUTTONCOLUMNS * MAXBUTTONROWS; i++ )
      {
        if( m_jButtonCurrent.equals( m_jButtonCmds[i] ) )
        {
          CommandClass cmd = ( ( CommandClass )m_buttonCmd.elementAt( i ) );
          if( cmd == null ) {
            FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
                FileManager.APPNAME, m_appProps, true);
            cmd = new CommandClass( "New", "", "New command", "", -1, CommandClass.NO, true,
                                    this, fic);
          }
          ConfigButtonCommandDialog dlg = new ConfigButtonCommandDialog( this, cmd );
          dlg.setVisible( true );
          if( dlg.getCommand() != null )
          {
            setButtonCommand( dlg.getCommand(), i, true );
            if( ( ( CommandClass )m_buttonCmd.elementAt( i ) ).getComponent() == null )
              ( ( CommandClass )m_buttonCmd.elementAt( i ) ).setComponent( this );
          }
          dlg.dispose();
          break;
        }
      }
    }
  }

  protected void onButCopy()
  {
    if( m_jButtonCurrent != null )
    {
      for( int i = 0; i < MAXBUTTONCOLUMNS * MAXBUTTONROWS; i++ )
      {
        if( m_jButtonCurrent.equals( m_jButtonCmds[i] ) )
        {
          CommandClass cmd = ( ( CommandClass )m_buttonCmd.elementAt( i ) );
          if( cmd == null )
            return;
          Clipboard clipboard = getToolkit().getSystemClipboard();
          String str = null;
          try
          {
            StringBufferOutputStream sbos = new StringBufferOutputStream( "ISO-8859-1");
            ObjectOutputStream oos = new ObjectOutputStream( sbos );
            oos.writeObject( cmd);
            oos.flush();
            oos.close();
            str = sbos.toString();
          }
          catch( Exception ex)
          {
            ex.printStackTrace();
            m_jshell.getErr().println( ex.toString());
          }
          if( str != null)
            clipboard.setContents( new StringSelection(str), null);
          break;
        }
      }
    }
  }

  protected void onButPaste()
  {
    if( m_jButtonCurrent != null )
    {
      for( int i = 0; i < MAXBUTTONCOLUMNS * MAXBUTTONROWS; i++ )
      {
        if( m_jButtonCurrent.equals( m_jButtonCmds[i] ) )
        {
          CommandClass cmd = ( ( CommandClass )m_buttonCmd.elementAt( i ) );
          if( cmd == null )
            return;
          Clipboard clipboard = getToolkit().getSystemClipboard();

          try
          {
            Transferable tr = clipboard.getContents( null);
            String str = (String)tr.getTransferData( DataFlavor.stringFlavor);

            if( str != null)
            {
              haui.io.StringBufferInputStream sbis = new haui.io.StringBufferInputStream( str, "ISO-8859-1");
              ObjectInputStream ois = new ObjectInputStream( sbis );
              cmd = ( CommandClass )ois.readObject();
              setButtonCommand( cmd, i, true);
            }
          }
          catch( Exception ex)
          {
            ex.printStackTrace();
            m_jshell.getErr().println( ex.toString());
          }
          break;
        }
      }
    }
  }

  protected void onButDelete()
  {
    if( m_jButtonCurrent != null )
    {
      for( int i = 0; i < MAXBUTTONCOLUMNS * MAXBUTTONROWS; i++ )
      {
        if( m_jButtonCurrent.equals( m_jButtonCmds[i] ) )
        {
          CommandClass cmd = ( ( CommandClass )m_buttonCmd.elementAt( i ) );
          if( cmd == null )
            return;
          FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
              FileManager.APPNAME, m_appProps, true);
          setButtonCommand( new CommandClass( (new Integer( i)).toString(), "", "", "", -1, CommandClass.NO, true,
                                              this, fic), i, true);
          break;
        }
      }
    }
  }

  public void showFileProperties()
  {
    FilePropertiesDialog fpd = new FilePropertiesDialog( m_fit.getSelectedFileInterface(), this, "File Properties", false);
    fpd.setVisible( true);
  }

  public void showOpenWith()
  {
    JFileChooser chooser = new JFileChooser();
    FileInterface fi = m_fit.getSelectedFileInterface();
    int returnVal = chooser.showOpenDialog( this);
    if(returnVal == JFileChooser.APPROVE_OPTION)
    {
      if( chooser.getSelectedFile() != null && chooser.getSelectedFile().getAbsolutePath() != null)
      {
        CommandClass cmd = null;
        String strExec = chooser.getSelectedFile().getAbsolutePath();
        //File file = new File( strExec);
        String strTargetPath= m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath();
        if( strExec == null || strExec.equals( ""))
          return;
        if( cmd == null) {
          FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
              FileManager.APPNAME, m_appProps, true);
          cmd = new CommandClass( "tmp", fi.getParent(), strExec, "%p%n", 0, CommandClass.NO, true, null, fic);
        }
        String strCmd = cmd.getCompleteCommand( fi, strTargetPath);
        if( strCmd != null)
          //System.out.println( "starting ...: " + strCmd);
          m_jshell.getShellEnv().getOut().println( "starting ...: " + strCmd);
        try
        {
          fi.exec( strTargetPath, cmd, m_jshell.getShellEnv().getOut(), m_jshell.getShellEnv().getErr(), null);
          //fi.exec( strTargetPath, cmd, System.out, System.err, null);
        }
        catch( Exception ex)
        {
          ex.printStackTrace( m_jshell.getShellEnv().getErr());
        }
      }
    }
  }

  /**
   * ececute command for file extensions
   */
  public void _execFileExtCommand( CommandClass cmd)
  {
    if( cmd != null)
      _exec( m_fit.getSelectedFileInterface(), m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath(), cmd);
  }

  /**
   * ececute command
   *
   * @param fi selected FileInterface
   * @param strTargetPath traget path
   * @param cmd CommandClass
   */
  public void _exec( FileInterface fi, String strTargetPath, CommandClass cmd)
  {
    String strCmd = "";
    if( cmd == null) {
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
          FileManager.APPNAME, m_appProps, true);
      cmd = new CommandClass( "tmp", fi.getParent(), fi.getAbsolutePath(), "", 0, CommandClass.NO, true, null, fic);
    }
    if( cmd.getExecLocal())
        fi = fi.getDirectAccessFileInterface();
    strCmd = cmd.getCompleteCommand( fi, strTargetPath);
    if( strCmd != null)
    {
      strCmd = strCmd.trim();
      //System.out.println( "starting ...: " + strCmd );
      m_jshell.getShellEnv().getOut().println( "starting ...: " + strCmd);
    }
    try
    {
      if( m_blAutoLogon)
        m_fit.getFileInterface().logon();
      if( strCmd != null)
      {
        if( !strCmd.endsWith( "&"))
          strCmd += "&";
        //m_jshell.getShellEnv().setFileInterface( m_fit.getFileInterface().getFileInterface().duplicate() );
        m_jshell.getShellEnv().setFileInterface( fi );
        m_jshell.getShellEnv().setTargetPath( strTargetPath);
        m_jshell.getShellEnv().setExecLocal( cmd.getExecLocal());
        JShellEngine.processCommands( strCmd, m_jshell.getShellEnv(), false);
      }
      else
      {
        FileInterface fiDup = fi.duplicate();
        fiDup.exec( strTargetPath, cmd, m_jshell.getShellEnv().getOut(), m_jshell.getShellEnv().getErr(), null );
        //fi.exec( strTargetPath, cmd, System.out, System.err, null );
        //m_jshell.getShellEnv().addProcess( new BackgroundThread( strCmd, strTargetPath, fi));
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace(m_jshell.getShellEnv().getErr());
    }
  }

  /**
   * copy files
   *
   * @return true if the file or directory is successfully copied; false otherwise
   */
  public boolean _copy( boolean blWait, boolean blDelete)
  {
    boolean blRet = false;
    final boolean blDeleting = blDelete;
    // if it is the same host and the same path -> cancel
    if( m_fit.getFileInterface().getId().equals( m_fipTarget.getFileInfoTable().getFileInterface().getId())
      && m_fit.getFileInterface().getAbsolutePath().equals( m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath()))
    {
      return blRet;
    }
    m_blOverwriteAll = false;
    final FileInfoTable fitOrg = m_fit;
    final FileInfoTable fitDest = m_fipTarget.getFileInfoTable();
    final FileInterface[] fisOrg = m_fit.getSelectedFileInterfaces();
    //final int[] iIdx = m_fit.getSelectedRows();
    if( fisOrg != null)
    {
      long[] lFiCombi = FileConnector.lengthAndCountOfFiles( fisOrg, null, true);
      StringBuffer strbufMsg = null;
      if( blDeleting)
        strbufMsg = new StringBuffer( "Moving ");
      else
        strbufMsg = new StringBuffer( "Copying ");
      strbufMsg.append( lFiCombi[1]);
      strbufMsg.append( " file(s)");
      final CancelProgressDialog cpdlg = new CancelProgressDialog( this, strbufMsg.toString(),
        null, true, FileManager.APPNAME);
      cpdlg.getMainProgressbar().setMinimum( 0);
      cpdlg.getMainProgressbar().setMaximum( (int)lFiCombi[1]);
      cpdlg.getMainProgressbar().setValue( 0);
      Thread th = new Thread( "copythread")
      {
        public void run()
        {
          if( cpdlg != null )
          {
            cpdlg.setVisible( false );
            cpdlg.setModal( false );
            cpdlg.setVisible( true );
            cpdlg.setModal( true );
          }
          for( int i = 0; i < fisOrg.length; ++i)
          {
            String strDest = m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath();
            String strNewPath = strDest;
            if( strNewPath.charAt( strNewPath.length()-1) != m_fipTarget.getFileInfoTable().getFileInterface().separatorChar())
              strNewPath += m_fipTarget.getFileInfoTable().getFileInterface().separatorChar();
            strNewPath += fisOrg[i].getName();
            if( m_blAutoLogon && m_fit.getConnectionObject() != null)
              m_fit.getFileInterface().logon();
            FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
                m_fipTarget.getFileInfoTable().getConnectionObject(), 0, 0,
                FileManager.APPNAME,
                m_fipTarget.getFileInfoTable().getFileInterface().getAppProperties(),
                m_fipTarget.getFileInfoTable().getFileInterface().isCached());
            boolean blExist = FileConnector.exists( strNewPath, fic);
            m_blSuccess = copy( fisOrg[i], m_fipTarget.getFileInfoTable().getFileInterface(), cpdlg, blDeleting);
            if( !m_blSuccess)
              m_jshell.getErr().println( "Error while copying " + fisOrg[i].getAbsolutePath());
            if( blDeleting && m_blSuccess)
            {
              m_blSuccess = FileConnector.deleteRecusively( fisOrg[i] );
              if( !m_blSuccess)
                m_jshell.getErr().println( "Error while deleting " + fisOrg[i].getAbsolutePath());
            }
            //System.out.println( "\n");
            //m_jshell.getShellEnv().getOut().println( "\n");
            if( m_blAutoLogon && m_fit.getConnectionObject() != null)
              m_fit.getFileInterface().logon();
            boolean blExistAfter = FileConnector.exists( strNewPath, fic);
            // add FileInterface if not exist before
            if( blExistAfter && !blExist )
            {
              FileInterface fi = FileConnector.createFileInterface( strNewPath, strDest, false, fic);
              ( ( FileInfoTableModel ) ( fitDest.getModel() ) ).addRow( fi );
            }
          }
          if( blDeleting && fitOrg.getFileInterface().equals( m_fit.getFileInterface() ) )
            refresh();
          if( fitDest.getFileInterface().equals( m_fipTarget.getFileInfoTable().getFileInterface() ) )
            m_fipTarget.refresh();
        }
      };
      cpdlg.setRunnable( th);
      cpdlg.start();
      if( blWait)
      {
        try
        {
          th.join();
        }
        catch( InterruptedException ex )
        {
          ex.printStackTrace( m_jshell.getErr());
        }
        blRet = m_blSuccess;
      }
      else
        blRet = true;
    }
    //m_fipTarget.refresh();
    return blRet;
  }

  /**
   * copy files
   *
   * @return true if the file or directory is successfully copied; false otherwise
   */
  boolean copy( FileInterface fiOrg, FileInterface fiDest, CancelProgressDialog cpd, boolean blSetOrgDate)
  {
    boolean blRet = false;
    // do not copy spezal dirs . and ..
    if( fiOrg.getName().equals( ".") || fiOrg.getName().equals( ".."))
      return true;
    final String strDest = fiDest.getAbsolutePath();
    String strNewPath = strDest;
    // check if the last char in path is the seperator
    if( strNewPath.charAt( strNewPath.length()-1) != fiDest.separatorChar())
      strNewPath += fiDest.separatorChar();
    strNewPath += fiOrg.getName();
    if( m_blAutoLogon && fiOrg.getConnObj() != null)
      m_fit.getFileInterface().logon();
    if( m_blAutoLogon && fiDest.getConnObj() != null
      && !fiOrg.getId().equals( fiDest.getId()))
      m_fipTarget.getFileInfoTable().getFileInterface().logon();
    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
        fiDest.getConnObj(), 0, 0,
        FileManager.APPNAME,
        fiDest.getAppProperties(),
        fiDest.isCached());
    if( fiOrg.isFile() || fiOrg.isArchive())
    { // copy a file
      if( !m_blOverwriteAll && FileConnector.exists( strNewPath, fic))
      {
        // file exists allready -> ask to overwrite it
        CopyFileDialog dlg = new CopyFileDialog( this, "Confirmation", true, "Overwrite file " + fiOrg.getName() + "?");
        dlg.setVisible( true);
        int iRes = dlg.getValue();
        if( iRes == CopyFileDialog.CANCEL_OPTION)
        {
          cpd.getMainProgressbar().setValue( cpd.getMainProgressbar().getValue()+1);
          return blRet;
        }
        else if( iRes == CopyFileDialog.OVALL_OPTION)
          m_blOverwriteAll = true;
        else if( iRes == CopyFileDialog.NO_OPTION)
        {
          cpd.getMainProgressbar().setValue( cpd.getMainProgressbar().getValue()+1);
          return true;
        }
        if( m_blAutoLogon && fiOrg.getConnObj() != null)
          m_fit.getFileInterface().logon();
        if( m_blAutoLogon && fiDest.getConnObj() != null
          && !fiOrg.getId().equals( fiDest.getId()))
          m_fipTarget.getFileInfoTable().getFileInterface().logon();
      }
      try
      {
        cpd.getDetailProgressbar().setMinimum( 0);
        cpd.getDetailProgressbar().setMaximum( (int)fiOrg.length());
        cpd.getDetailProgressbar().setValue( 0);
        StringBuffer strbuf = new StringBuffer( "from: ");
        strbuf.append( fiOrg.getAbsolutePath());
        cpd.setFromLabelText( strbuf.toString());
        strbuf = new StringBuffer( "to: ");
        strbuf.append( strNewPath);
        cpd.setToLabelText( strbuf.toString());
        BufferedOutputStream bos = fiDest.getBufferedOutputStream( strNewPath );
        blRet = fiOrg.copyFile( bos, cpd);
        bos.flush();
        bos.close();
        if( blSetOrgDate)
        {
          FileInterface fiTarget = FileConnector.createFileInterface( strNewPath, null, false, fic);
          fiTarget.setLastModified( fiOrg.lastModified());
        }
        cpd.getMainProgressbar().setValue( cpd.getMainProgressbar().getValue()+1);
      }
      catch( Exception ex )
      {
        ex.printStackTrace( m_jshell.getShellEnv().getErr());
      }
    }
    else if( fiOrg.isDirectory())
    { // copy dir recursively
      blRet = FileConnector.exists( strNewPath, fic);
      if( !blRet ) // create the copied dir if not exists on dest
      {
        FileInterface fiNewDir = FileConnector.createFileInterface( strNewPath, null, false, fic);
        blRet = fiNewDir.mkdir();
      }
      if( blRet )
      { // dir exists
        // create FileInterface for dest dir
        FileInterface fi = FileConnector.createFileInterface( strNewPath, strDest, false, fic);
        FileInterface fiFiles[] = fiOrg._listFiles();

        // copy all files and dirs from source to dest
        for( int i = 0; i < fiFiles.length; i++ )
        {
          blRet = copy( fiFiles[i], fi, cpd, blSetOrgDate);
          if( !blRet )
            return blRet;
        }
      }
    }
    return blRet;
  }

  /**
   * move files
   *
   * @return true if the file or directory is successfully copied; false otherwise
   */
  public boolean _move()
  {
    boolean blRet = false;
    boolean blWait = false;
    FileInterface[] fis = m_fit.getSelectedFileInterfaces();
    //int[] idxs = m_fit.getSelectedRows();
    FileInterface fiOrg = m_fit.getFileInterface();
    FileInterface fiDest = m_fipTarget.getFileInfoTable().getFileInterface();
    if( fis != null)
    {
      for( int i = fis.length-1; i >= 0; --i)
      {
        if( (fiOrg.getId().equals( fiDest.getId())
          && !m_blTreatAsDiff)) // move from local to local or remote to remote
        {
          StringBuffer strbufNewPath = new StringBuffer( fiDest.getAbsolutePath());
          if( strbufNewPath.charAt( strbufNewPath.length()-1) != fiDest.separatorChar())
              strbufNewPath.append( fiDest.separatorChar());
          strbufNewPath.append( fis[i].getName());
          FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
              m_connObj, 0, 0, FileManager.APPNAME, m_appProps, true);
          FileInterface fNew = FileConnector.createFileInterface( strbufNewPath.toString(), null, false, fic);
          if( fNew != null)
            blRet = fis[i].renameTo( fNew);
          if( blRet)
          {
            refresh();
            m_fipTarget.refresh();
          }
        }
      }
      if( !blRet)
      { // move from local to remote or remote to local
        blRet = _copy( blWait, true);
      }
    }
    return blRet;
  }

  /**
   * save current connection, FileExt and Button command properties
   */
  public void _save()
  {
    String strName = m_strConnName;
    String strHeader = "Connection, FileExt and Button command properties";
    if( strName == null)
    {
      strName = m_fit.getFileInterface().getId();
      if( strName == null || strName.equals( ""))
        strName = FileInterface.LOCAL;
      strName += FILEEXT;
      File f = new File( strName);
      if( f.exists())
      {
        int iRet;
        iRet = JOptionPane.showConfirmDialog( this, "Overwrite " + strName + "?", "Confirmation", JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);
        if( iRet == JOptionPane.NO_OPTION)
        {
          strName = JOptionPane.showInputDialog( this, "New config file name?", "Input", JOptionPane.QUESTION_MESSAGE);
          if( strName == null || strName.equals( ""))
          {
            //System.out.println( "Aborted saving config file!");
            m_jshell.getShellEnv().getOut().println( "Aborted saving config file!");
            return;
          }
          if( strName.lastIndexOf( FILEEXT) == -1)
            strName += FILEEXT;
        }
      }
    }
    String strPrefix = FIP + strName + ".";
    m_fipProps.setProperty( strPrefix + FIPPATH, m_fit.getFileInterface().getAbsolutePath());
    String strLoginPwd = m_fipProps.getProperty( GlobalApplicationContext.LOGONPASSWORD);
    String strFtpPwd = m_fipProps.getProperty( FileInfoPanel.FTPPASSWORD);
    m_fipProps.remove( GlobalApplicationContext.LOGONPASSWORD);
    m_fipProps.remove( FileInfoPanel.FTPPASSWORD);
    // remove keys for terminal execution
    m_fipProps.remove( GlobalApplicationContext.PROXYHOST);
    m_fipProps.remove( GlobalApplicationContext.PROXYNOPROXYLIST);
    m_fipProps.remove( GlobalApplicationContext.PROXYUSER);
    m_fipProps.remove( GlobalApplicationContext.PROXYPASSWORD);
    m_fipProps.remove( GlobalApplicationContext.PROXYENABLE);
    m_fipProps.remove( GlobalApplicationContext.PROXYPORT);

    FipConnection fipCon = new FipConnection( strName, FileManager.APPNAME, m_fipProps);
    fipCon.setFileExtCommands( m_fileCmd);
    fipCon.setButtonCommands( m_buttonCmd);
    fipCon._save();
    m_fipProps.store( ConfigPathUtil.getCurrentSavePath( FileManager.APPNAME, strName), strHeader);
    if( strLoginPwd != null)
      m_fipProps.setProperty( GlobalApplicationContext.LOGONPASSWORD, strLoginPwd);
    if( strFtpPwd != null)
      m_fipProps.setProperty( FileInfoPanel.FTPPASSWORD, strFtpPwd);
  }

  /**
   * erase current connection, FileExt and Button command properties
   */
  public void _erase( String strNewConnId)
  {
    String strConnId;
    String strName;
    if( m_fit.getFileInterface() == null)
      return;
    strName = m_fit.getFileInterface().getId();
    if( strName == null || strName.equals( ""))
      strName = FileInterface.LOCAL;
    strConnId = strName;
    strName += FILEEXT;
    String strPrefix = FIP + strName + ".";
    m_fipProps.remove( strPrefix + FIPPATH);

    if( strNewConnId == null || (strNewConnId != null && !strNewConnId.equals( strConnId)))
    {
      FipConnection fipCon = new FipConnection( strName, FileManager.APPNAME, m_fipProps);
      fipCon.setFileExtCommands( m_fileCmd);
      fipCon.setButtonCommands( m_buttonCmd);
      fipCon._erase();

      for( int i = 0; i < MAXBUTTONCOLUMNS*MAXBUTTONROWS; i++)
      {
        FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
            m_connObj, 0, 0, FileManager.APPNAME, m_appProps, true);
        CommandClass cmd = new CommandClass( (new Integer( i)).toString(), "", "", "", -1, 0, true, null, fic);
        setButtonCommand( cmd, i, false);
      }
      m_fileCmd.removeAllElements();
    }
  }

  /**
   * erase current connection, FileExt command properties
   */
  public void _eraseFileExt()
  {
    String strName;
    if( m_fit.getFileInterface() == null)
      return;
    strName = m_fit.getFileInterface().getId();
    if( strName == null || strName.equals( ""))
      strName = FileInterface.LOCAL;
    strName += FILEEXT;
    String strPrefix = FIP + strName + ".";
    m_fipProps.remove( strPrefix + FIPPATH);

    FipConnection fipCon = new FipConnection( strName, FileManager.APPNAME, m_fipProps);
    fipCon.setFileExtCommands( m_fileCmd);
    fipCon.setButtonCommands( m_buttonCmd);
    fipCon._eraseExt();
  }

  /**
   * synchronizeFileExt properties
   */
  public void synchronizeFileExt( Vector cmd)
  {
    if( (m_fit.getFileInterface().getId().equals(
      m_fipTarget.getFileInfoTable().getFileInterface().getId()) && !m_blTreatAsDiff))
      m_fipTarget.setFileExtCommandVector( cmd);
  }

  /**
   * synchronizeButton properties
   */
  public void synchronizeButton( Vector cmd)
  {
    if( (m_fit.getFileInterface().getId().equals(
      m_fipTarget.getFileInfoTable().getFileInterface().getId()) && !m_blTreatAsDiff))
      m_fipTarget.setButtonCommandVector( cmd);
  }

  /**
   * mkdir button handler
   */
  void mkdir_actionPerformed(ActionEvent e)
  {
    if( m_blAutoLogon)
      m_fit.getFileInterface().logon();
    m_fit.mkdir();
    if( m_fipTarget != null && m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath().equals( m_fit.getFileInterface().getAbsolutePath()))
    {
      //m_fipTarget.cloneTargetFip();
      m_fipTarget.refresh();
    }
  }

  /**
   * delete button handler
   */
  void delete_actionPerformed(ActionEvent e, boolean blForce)
  {
    int iRet = JOptionPane.YES_OPTION;

    if( !blForce)
      iRet = JOptionPane.showConfirmDialog( this, "Delete all selected files?",
                                              "Confirmation", JOptionPane.YES_NO_OPTION,
                                              JOptionPane.QUESTION_MESSAGE );
    if( iRet == JOptionPane.YES_OPTION )
    {
      if( m_blAutoLogon )
        m_fit.getFileInterface().logon();
      m_fit.delete( blForce );
      if( m_fipTarget != null && m_fipTarget.getFileInfoTable().getFileInterface().getAbsolutePath().equals( m_fit.getFileInterface().getAbsolutePath() ) )
      {
        //m_fipTarget.cloneTargetFip();
        m_fipTarget.refresh();
      }
      refresh();
    }
  }

  class LisItem implements ItemListener
  {
    public void itemStateChanged( ItemEvent event)
    {
      if( getRootPane() == null || m_blCloning)
        return;
      getRootPane().getParent().setCursor( new Cursor( Cursor.WAIT_CURSOR));
      int state = event.getStateChange();
      if( state == ItemEvent.SELECTED && !m_blInit)
        root_itemStateChanged(event);
      getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
    }
  }

  /**
   * roots  handler
   */
  void root_itemStateChanged(ItemEvent e)
  {
    String strPath = m_fit.getRootHist( (String)m_jComboBoxRoots.getSelectedItem());
    if( strPath == null)
      strPath = (String)m_jComboBoxRoots.getSelectedItem();
    init( strPath, null, m_fit.getConnectionObject(), false);
    //m_fit.repaint();
    updateLabels( true);
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      //Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON3_MASK)
      {
        rightMousePressed( event);
        event.consume();
      }
    }

    public void mouseReleased(MouseEvent event)
    {
      //Object object = event.getSource();
    }
  }

  void rightMousePressed( MouseEvent event)
  {
    m_jButtonCurrent = (JButton)event.getSource();
    m_jPopupMenuButtonContext.show( m_jButtonCurrent, event.getX(), event.getY());
  }

  public boolean configAllFileExtCommands()
  {
    boolean blRet = false;
    int idx = -1;
    String strConnId = m_strConnName;
    if( strConnId != null)
      idx = strConnId.lastIndexOf( FILEEXT);
    if( idx != -1)
      strConnId = strConnId.substring( 0, idx);
    FipAllPropertiesDialog dlg = new FipAllPropertiesDialog( this, "Connection properties",
      true, strConnId);
    dlg.setVisible( true);
    if( !dlg.isCanceled())
    {
      blRet = true;
      dlg._save();
      /*
      dlg.showAfterFileEx();
      m_fileCmd = dlg.getCommand();
      if( dlg.getCommand() != null )
      {
      }
      synchronizeFileExt( m_fileCmd );
      */
    }
    dlg.dispose();
    return blRet;
  }

  public boolean configFileExtCommands()
  {
    boolean blRet = false;
    _eraseFileExt();
    FipPropertiesDialog dlg = new FipPropertiesDialog( this, "Connection properties",
      true, m_fipProps, m_fileCmd);
    //ConfigFileExtCommandDialog dlg = new ConfigFileExtCommandDialog( getRootPane(), m_fileCmd);
    if( m_fit.getConnectionObject() != null || m_fit.getConnectionObject() instanceof FtpTypeFile
        || m_fit.getConnectionObject() instanceof CgiTypeFile)
      dlg.showFipPanel();
    else
      dlg.hideFipPanel();
    dlg.hideForFileEx();
    dlg.setVisible( true);
    if( !dlg.isCanceled())
    {
      blRet = true;
      dlg.showAfterFileEx();
      m_fileCmd = dlg.getCommand();
      if( dlg.getCommand() != null )
      {
      }
      synchronizeFileExt( m_fileCmd );
    }
    dlg.dispose();
    return blRet;
  }

  public void saveCommands()
  {
    int i;
    BufferedWriter bw;
    String strFileName = null;
    CommandClass cmd;

    strFileName = getFileInfoTable().getFileInterface().getId() + FILEEXT;

    try
    {
      bw = new BufferedWriter( new FileWriter( strFileName));
      for( i = 0; i < m_buttonCmd.size(); i++)
      {
        cmd = (CommandClass)m_buttonCmd.elementAt( i);
        cmd.save( bw);
        bw.newLine();
      }
      for( i = 0; i < m_fileCmd.size(); i++)
      {
        cmd = (CommandClass)m_fileCmd.elementAt( i);
        cmd.save( bw);
        bw.newLine();
      }
      bw.close();
    }
    catch(IOException e)
    {
      e.printStackTrace(m_jshell.getShellEnv().getErr());
    }
  }

  public void readCommands()
  {
    //int i;
    BufferedReader br;
    String strFileName = null;
    CommandClass cmd;
    Vector buttonCmd = new Vector();
    Vector fileCmd = new Vector();

    strFileName = getFileInfoTable().getFileInterface().getId() + FILEEXT;

    try
    {
      br = new BufferedReader( new FileReader( strFileName));
      cmd = new CommandClass( FileManager.APPNAME);
      while( cmd.read( br))
      {
        buttonCmd.add( cmd);
        if( br.readLine() == null)
          break;
        cmd = new CommandClass( FileManager.APPNAME);
      }
      m_buttonCmd = buttonCmd;
      cmd = new CommandClass( FileManager.APPNAME);
      while( cmd.read( br))
      {
        fileCmd.add( cmd);
        if( br.readLine() == null)
          break;
        cmd = new CommandClass( FileManager.APPNAME);
      }
      m_fileCmd = fileCmd;
      br.close();
    }
    catch(FileNotFoundException e)
    {
      e.printStackTrace(m_jshell.getShellEnv().getErr());
    }
    catch(IOException e)
    {
      e.printStackTrace(m_jshell.getShellEnv().getErr());
    }
  }

  /**
   * Process key events
   */
  public void procKeyEvent( KeyEvent e)
  {
    this.processKeyEvent( e);
    this.processComponentKeyEvent( e);
  }

  // main for testing purposes
  public static void main(String argv[])
  {
    AppProperties appProps = new AppProperties();
    JShell jshell = new JShell("FileInfoPanel");
    jshell.init();
    FileInfoPanel demo = new FileInfoPanel( jshell, true, appProps);
    demo.connect( FileInterface.LOCAL);
    JFrame f = new JFrame("FileInfoPanel");
    f.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {System.exit(0);}
    });
    f.getContentPane().add("Center", demo);
    f.pack();
    f.setVisible( true);
  }
}
