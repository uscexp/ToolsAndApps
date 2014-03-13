package haui.components;

import haui.util.AppProperties;
import haui.util.ConfigPathUtil;
import haui.util.GlobalApplicationContext;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ProtocolException;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Module:					LRShell.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\LRShell.java,v $ <p> Description:    Limited (non interactive) Remote Shell.<br> Runs non interactiv commands on a remote system via HTTP connection (cgi call 'excmd.pl'). </p><p> Created:				26.09.2000	by	AE </p><p>
 * @history  				26.09.2000 - 05.10.2000	by	AE: Created.<br>  12.10.2000	by	AE: Changed terminal emulation logic.<br>  16.10.2000	by	AE: Terminal emulation logic exported to TerminalTextArea class.<br>  </p><p>  Modification:<br>  $Log: LRShell.java,v $  Revision 1.9  2004-08-31 16:03:08+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.8  2004-06-22 14:08:54+02  t026843  bigger changes  Revision 1.7  2003-05-28 14:19:51+02  t026843  reorganisations  Revision 1.6  2003-05-21 16:46:44+02  t026843  config file name change  Revision 1.5  2002-09-18 11:16:17+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.4  2001-07-20 16:29:03+02  t026843  FileManager changes  Revision 1.3  2000-10-16 15:23:42+02  t026843  cosmetics  Revision 1.2  2000-10-16 11:39:20+02  t026843  Terminal emulation exported to the TermianlTextArea class.  Revision 1.1  2000-10-13 09:09:43+02  t026843  bugfixes + changes  Revision 1.0  2000-10-05 14:48:43+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.9 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\LRShell.java,v 1.9 2004-08-31 16:03:08+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class LRShell extends JApplet
{
  private static final long serialVersionUID = 2442007305458679593L;
  
  // property constants
  final static String APPNAME = "LRShell";
  final static String FILEMANAGERCGIURL = "CmdCgiUrl";
  final static String WRITEFILECGIURL = "WriteFileCgiUrl";
  final static String READFILECGIURL = "ReadFileCgiUrl";
  // other constants
  final static String PROPERTYFILE = "lrshell.ppr";
  final static String PROPERTYFILEHEADER = "LRShell Properties";
  // member variables
  AppProperties m_appProps = new AppProperties();
  boolean m_blSave;
  ConnectionManager m_conn;
  OutputThread m_eot;
  char m_lastKeyChar;
  boolean m_blCorrect = false;
  boolean m_blEnter = false;
  boolean isStandalone = false;
  LRShellPropertiesDialog m_propsDlg;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JMenuBar m_jMenuBar = new JMenuBar();
  JMenu m_jMenuFile = new JMenu();
  JMenuItem m_jMenuItemExit = new JMenuItem();
  JMenuItem m_jMenuItemLogon = new JMenuItem();
  JMenuItem m_jMenuItemLogoff = new JMenuItem();
  JMenu m_jMenuEdit = new JMenu();
  JMenuItem m_jMenuItemProps = new JMenuItem();
  TermTextArea m_jTextAreaShell = new TermTextArea();
  JScrollPane m_jScrollPaneShell = new JScrollPane();
  JScrollPane m_jScrollPaneErr = new JScrollPane();
  JTextArea m_jTextAreaErr = new JTextArea();
  JSplitPane m_jSplitPaneMain = new JSplitPane();
  Component m_top = null;

  //construct Applet
  public LRShell()
  {
    try
    {
      m_appProps.load( ConfigPathUtil.getCurrentReadPath( APPNAME, PROPERTYFILE));
      m_blSave = true;
    }
    catch( Exception ex)
    {
      System.err.println( ".");
    }
  }

  public LRShell( AppProperties appProps)
  {
    m_appProps = appProps;
    m_blSave = false;
  }

  public void setTopComponent( Component top)
  {
    m_top = top;
  }

  //initialise Applet
  public void init()
  {
    try
    {
      jbInit();
      /*
      m_appProps.setProperty( LRShell.READFILECGIURL, "http://thunder.prohosting.com/~vdisk/.admin/cgi/readfile.pl");
      m_appProps.remoteLoad( m_appProps.getProperty( LRShell.READFILECGIURL), this);
      */
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    //AdpWindow adapter = new AdpWindow();
    //this.addWindowListener(adapter);
    LisAction listener = new LisAction();
    m_jMenuItemExit.addActionListener(listener);
    m_jMenuItemLogon.addActionListener(listener);
    m_jMenuItemLogoff.addActionListener(listener);
    m_jMenuItemProps.addActionListener(listener);

    m_jScrollPaneShell.setPreferredSize( new Dimension( 640, 480));

    AdpComponent comadapter = new AdpComponent();
    getContentPane().addComponentListener( comadapter);

    m_eot = new OutputThread( m_jTextAreaErr, false, true);
    m_eot.start();
  }

  //Initialisation of the component
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400,300));
    this.getContentPane().setLayout(m_borderLayoutBase);
    this.setJMenuBar( m_jMenuBar);
    m_jMenuFile.setText("File");
    m_jMenuFile.setActionCommand("file");
    m_jMenuFile.setMnemonic((int)'F');
    m_jTextAreaErr.setEditable(false);
    m_jTextAreaErr.setLineWrap( true);
    m_jTextAreaErr.setEnabled(false);
    m_jSplitPaneMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
    m_jMenuBar.add(m_jMenuFile);
    m_jMenuItemLogon.setText("Logon");
    m_jMenuItemLogon.setActionCommand("logon");
    m_jMenuItemLogon.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_L, Event.CTRL_MASK));
    m_jMenuItemLogon.setMnemonic((int)'L');
    m_jMenuFile.add(m_jMenuItemLogon);
    m_jMenuItemLogoff.setText("Logoff");
    m_jMenuItemLogoff.setActionCommand("logoff");
    m_jMenuItemLogoff.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_O, Event.CTRL_MASK));
    m_jMenuItemLogoff.setMnemonic((int)'o');
    m_jMenuFile.add(m_jMenuItemLogoff);
    m_jMenuItemExit.setText("Exit");
    m_jMenuItemExit.setActionCommand("exit");
    m_jMenuItemExit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_E, Event.CTRL_MASK));
    m_jMenuItemExit.setMnemonic((int)'E');
    m_jMenuFile.add(m_jMenuItemExit);
    m_jMenuEdit.setText("Edit");
    m_jMenuEdit.setActionCommand("edit");
    m_jMenuEdit.setMnemonic((int)'E');
    m_jMenuBar.add(m_jMenuEdit);
    m_jMenuItemProps.setText("Properties");
    m_jMenuItemProps.setActionCommand("properties");
    m_jMenuItemProps.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, Event.CTRL_MASK));
    m_jMenuItemProps.setMnemonic((int)'P');
    m_jMenuEdit.add(m_jMenuItemProps);
    this.getContentPane().add(m_jSplitPaneMain, BorderLayout.CENTER);
    m_jSplitPaneMain.add(m_jScrollPaneErr, JSplitPane.BOTTOM);
    m_jSplitPaneMain.add(m_jScrollPaneShell, JSplitPane.TOP);
    m_jScrollPaneErr.getViewport().add(m_jTextAreaErr, null);
    m_jScrollPaneShell.getViewport().add(m_jTextAreaShell, null);
    m_jTextAreaShell.setEditable(true);
    m_jTextAreaShell.setLineWrap( true);
  }

  public void execCmd( String cmd)
  {
    m_jTextAreaShell.execCommand( cmd);
  }

  class TermTextArea
  extends TerminalTextArea
  {
    private static final long serialVersionUID = 2442007305458679592L;
    
    public void onEnter()
    {
      execCommand( getCommand());
    }

    public void execCommand( String cmd)
    {
      resetErrorTextArea();
      if( m_conn == null)
      {
        m_conn = new ConnectionManager( m_appProps.getProperty( LRShell.FILEMANAGERCGIURL), m_appProps, APPNAME);
        m_conn.setFrame( this);
      }
      m_conn.resetPostParams();
      //append( "\nCmd: " + getCommand());
      append( "\n");
      m_conn.addPostParam( "action", "exec");
      m_conn.addPostParam( "path", "");
      m_conn.addPostParam( "param", cmd);

      // Send data.
      m_conn.openConnection();
      m_conn.getHttpURLConnection().setUseCaches( false);
      m_conn.post();

      // Read lines from cgi-script.
      String line = null;
      int iIdx = -1;
      while( !(line = m_conn.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
        ;
      while((line = m_conn.readLine()) != null)
      {
        if( ( iIdx = line.indexOf( "<EOF>")) != -1)
        {
          line = line.substring( 0, iIdx);
          m_jTextAreaShell.append(line);
          break;
        }
        m_jTextAreaShell.append(line + "\n");
      }
      m_conn.disconnect();
    }
  }

  class AdpWindow extends WindowAdapter
  {
    public void windowClosing( WindowEvent event)
    {
      Object object = event.getSource();
      if (object == this)
      {
        exitApp();
      }
    }
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "exit")
        exit_actionPerformed(event);
      else if (cmd == "logon")
        logon_actionPerformed(event);
      else if (cmd == "logoff")
        logoff_actionPerformed(event);
      else if (cmd == "properties")
        properties_actionPerformed(event);
    }
  }

  class AdpComponent extends ComponentAdapter
  {
    public void componentResized( ComponentEvent event)
    {
      Object object = event.getSource();
      if (object == getContentPane())
        m_jSplitPaneMain.setDividerLocation( 0.8);
    }
  }

  //Applet-Information
  public String getAppletInfo()
  {
    return "Limited (non interactive) Remote Shell";
  }

  //Parameter-Infos
  public String[][] getParameterInfo()
  {
    return null;
  }
  // static initializer for setting look & feel
  static {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (Exception e) {}
  }

  /**
   * exit application
   */
  void exitApp()
  {
    if( m_blSave)
    {
      m_jTextAreaShell.append( "\n");
      // remove passwords and URL authorization
      m_appProps.remove( GlobalApplicationContext.PROXYPASSWORD);
      m_appProps.remove( GlobalApplicationContext.URLUSER);
      m_appProps.remove( GlobalApplicationContext.URLPASSWORD);
      m_appProps.remove( GlobalApplicationContext.LOGONPASSWORD);
      // save properties
      try
      {
        m_appProps.store( ConfigPathUtil.getCurrentSavePath( APPNAME, PROPERTYFILE), PROPERTYFILEHEADER);
      }
      catch( Exception ex)
      {
        System.err.println( ".");
      }
    }
    /*
    ConnectionManager conn = m_appProps.remoteStore( LRShell.WRITEFILECGIURL, PROPERTYFILEHEADER, this);
    String line = null;
    int iIdx = -1;
    while( !(line = conn.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    while((line = conn.readLine()) != null)
    {
      if( ( iIdx = line.indexOf( "<EOF>")) != -1)
      {
        line = line.substring( 0, iIdx);
        m_jTextAreaShell.append(line + "\n");
        break;
      }
      m_jTextAreaShell.append(line + "\n");
    }
    conn.disconnect();
    m_jTextAreaShell.append( "\nexit\n");
    try
    {
      Thread.sleep( 2000);
    }
    catch( InterruptedException iex)
    {
      iex.printStackTrace();
    }
    */
    if( m_blSave)
    {
      setVisible(false);	// hide the Frame
      System.exit(0);			// close the application
    }
    else
    {
      if( m_top != null)
      {
        m_top.setVisible( false);
      }
    }
  }

  public void resetErrorTextArea()
  {
    m_jTextAreaErr.setText( "");
  }

  /**
   * logon to the server (use cgi script logon.cgi)
   */
  void logon()
  {
    resetErrorTextArea();
    m_jTextAreaShell.append( "\n");
    setCursor( new Cursor( Cursor.WAIT_CURSOR));
    if( m_conn == null)
    {
      m_conn = new ConnectionManager( m_appProps.getProperty( LRShell.FILEMANAGERCGIURL), m_appProps, APPNAME);
      m_conn.setFrame( this);
    }
    m_conn.resetPostParams();
    m_conn.addPostParam( "action", "logon");
    //m_conn.addPostParam( "username", m_appProps.getProperty( LRShell.LOGONUSER));
    String strPass = m_appProps.getProperty( GlobalApplicationContext.LOGONPASSWORD);
    if( strPass == null)
    {
      AuthorizationDialog authDlg = new AuthorizationDialog( null, "Authorization", true, m_appProps, AuthorizationDialog.CGIAUTH, APPNAME);
      authDlg.setRequestText( new String("CGI authorization for ") + m_conn.getURL().getHost());
      authDlg.setVisible( true);
      strPass = m_appProps.getProperty( GlobalApplicationContext.LOGONPASSWORD);
    }

    m_conn.addPostParam( "param", m_appProps.getProperty( GlobalApplicationContext.LOGONUSER));
    m_conn.addPostParam( "path", strPass);

    //m_conn.addPostParam( "password", strPass);

    // Send data.
    m_conn.openConnection();
    try
    {
      m_conn.getHttpURLConnection().setRequestMethod( "POST");
    }
    catch( ProtocolException pex)
    {
      setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
      pex.printStackTrace();
    }
    m_conn.getHttpURLConnection().setUseCaches( false);
    m_conn.post();

    // Read lines from cgi-script.
    String line = null;
    int iIdx = -1;
    while( !(line = m_conn.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    while((line = m_conn.readLine()) != null)
    {
      if( ( iIdx = line.indexOf( "<EOF>")) != -1)
      {
        line = line.substring( 0, iIdx);
        m_jTextAreaShell.append(line + "\n");
        break;
      }
      m_jTextAreaShell.append(line + "\n");
    }
    m_conn.disconnect();
    setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
    m_jTextAreaShell.setPrompt();
  }

  /**
   * logoff to the server (use cgi script logon.cgi)
   */
  void logoff()
  {
    resetErrorTextArea();
    m_jTextAreaShell.append( "\n");
    setCursor( new Cursor( Cursor.WAIT_CURSOR));
    if( m_conn == null)
    {
      m_conn = new ConnectionManager( m_appProps.getProperty( LRShell.FILEMANAGERCGIURL), m_appProps, APPNAME);
      m_conn.setFrame( this);
    }

    m_conn.resetPostParams();
    m_conn.addPostParam( "action", "logoff");

    // Send data.
    m_conn.openConnection();
    try
    {
      m_conn.getHttpURLConnection().setRequestMethod( "POST");
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    m_conn.getHttpURLConnection().setUseCaches( false);
    m_conn.post();

    // Read lines from cgi-script.
    String line = null;
    int iIdx = -1;
    while( !(line = m_conn.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    while((line = m_conn.readLine()) != null)
    {
      if( ( iIdx = line.indexOf( "<EOF>")) != -1)
      {
        line = line.substring( 0, iIdx);
        m_jTextAreaShell.append(line + "\n");
        break;
      }
      m_jTextAreaShell.append(line + "\n");
    }
    m_conn.disconnect();
    setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
    m_jTextAreaShell.setPrompt();
  }

  /**
   * exit menu handler
   */
  void exit_actionPerformed(ActionEvent e)
  {
    exitApp();
  }

  /**
   * logon menu handler
   */
  void logon_actionPerformed(ActionEvent e)
  {
    logon();
  }

  /**
   * logoff menu handler
   */
  void logoff_actionPerformed(ActionEvent e)
  {
    logoff();
  }

  /**
   * properties menu handler
   */
  void properties_actionPerformed(ActionEvent e)
  {
    if( m_propsDlg == null)
      m_propsDlg = new LRShellPropertiesDialog( null, "Properties", true, m_appProps, APPNAME);
    m_propsDlg.setVisible( true);
  }

  // main for testing purposes
  public static void main(String argv[])
  {
    final LRShell demo = new LRShell();
    demo.init();
    JFrame f = new JFrame(APPNAME);
    f.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {System.exit(0);}
    });
    f.getContentPane().add("Center", demo);
    f.pack();
    //f.setSize(new Dimension(640,480));
    f.setVisible( true);
  }
}
