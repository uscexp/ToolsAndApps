package haui.app.HttpTunnel;

import haui.components.ProxyPropertiesDialog;
import haui.resource.ResouceManager;
import haui.util.AppProperties;
import haui.util.ConfigPathUtil;
import haui.util.GlobalAppProperties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Module:      HttpTunnelClientServer.java<br>
 *              $Source: $
 *<p>
 * Description: HTTP tunnel which tunnels a TCP connection via a java servlet.<br>
 *</p><p>
 * Created:     05.12.2001 by	AE
 *</p><p>
 *
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     05. Dec. 2001
 * @history     05.12.2001	by	AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *
 *</p><p>
 * @version     v1.0, 2001; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class HttpTunnelClientServer
  extends JApplet
{
  // property constants
  public final static String APPNAME = "HttpTunnel";
  public final static String SERVLET = "Servlet";
  public final static String PROPFILENAME = "httpt.pps";
  public final static String PROPFILEHEADER = "HttpTunnelClientServer application properties";
  public final static String MAPSFILENAME = "httpt_map.pps";
  public final static String MAPSFILEHEADER = "HttpTunnelClientServer connection mapping properties";

  // member variables
  Vector m_vMaps = new Vector();
  Vector m_vListeners = new Vector();
  Vector m_vConnections = new Vector();
  AppProperties m_appProps = new AppProperties();
  ProxyPropertiesDialog m_propsDlg;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  FlowLayout m_flowLayoutOutHead = new FlowLayout();
  JScrollPane m_jScrollPaneOut = new JScrollPane();
  JPanel m_jPanelOut = new JPanel();
  JLabel m_jLabelOutHead = new JLabel();
  JTextArea m_jTextAreaOut = new JTextArea()
  {
    public void append( String str)
    {
      super.append( str);
      setCaretPosition( getText().length());
    }
  };
  BorderLayout m_borderLayoutOut = new BorderLayout();
  JPanel m_jPanelOutHead = new JPanel();
  JMenuItem m_jMenuItemProps = new JMenuItem();
  JMenuItem m_jMenuItemSave = new JMenuItem();
  JMenuBar m_jMenuBar = new JMenuBar();
  JMenu m_jMenuFile = new JMenu();
  JMenuItem m_jMenuItemExit = new JMenuItem();
  JMenuItem m_jMenuItemClear = new JMenuItem();
  JMenu m_jMenuView = new JMenu();
  JMenu m_jMenuExtra = new JMenu();
  JToolBar m_jToolBar = new JToolBar();
  JButton m_jButtonOutClear = new JButton();
  JMenuItem m_jMenuItemMaps = new JMenuItem();
  JMenuItem m_jMenuItemServletProp = new JMenuItem();
  ConfigConnectionMapDialog m_configmapsDlg = null;
  JButton m_jButtonNumConn = new JButton();
  ConnectionStatusDialog m_numConnDlg = null;

  public HttpTunnelClientServer()
  {
  }

  //initialise Applet
  public void init()
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    _load();
    LisAction actionlistener = new LisAction();
    m_jButtonOutClear.addActionListener( actionlistener);
    m_jMenuItemProps.addActionListener( actionlistener);
    m_jMenuItemServletProp.addActionListener( actionlistener);
    m_jMenuItemExit.addActionListener( actionlistener);
    m_jMenuItemClear.addActionListener( actionlistener);
    m_jMenuItemSave.addActionListener( actionlistener);
    m_jMenuItemMaps.addActionListener( actionlistener);
    m_jButtonNumConn.addActionListener( actionlistener);

    /*
    try
    {
      PipedInputStream pis = new PipedInputStream();
      PipedOutputStream pos = new PipedOutputStream( pis);
      System.setOut( new PrintStream( pos));
      System.setErr( new PrintStream( pos));
      m_jTextAreaOut.read( new BufferedReader( new InputStreamReader( pis)), "Output");
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    */

    for( int i = 0; i < m_vMaps.size(); i++)
    {
      ConnectionMap cm = (ConnectionMap)m_vMaps.elementAt( i);
      String servletUrl = m_appProps.getProperty( SERVLET);
      if( servletUrl == null)
      {
        servletprop_actionPerformed(null);
        servletUrl = m_appProps.getProperty( SERVLET);
      }
      ServerListener sl = new ServerListener( cm, servletUrl, m_jTextAreaOut, m_appProps);
      sl.start();
      m_jTextAreaOut.append( "Listener '" + cm.getLocalAddr() + ":" + String.valueOf( cm.getLocalPort()) + "' started\n");
      //System.out.println( "Listener '" + cm.getLocalAddr() + ":" + String.valueOf( cm.getLocalPort()) + "' started");
      m_vListeners.add( sl);
    }
  }

  //Initialisation of the component
  private void jbInit()
    throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutBase);
    this.setJMenuBar( m_jMenuBar);
    m_jScrollPaneOut.setPreferredSize(new Dimension(74, 100));
    m_flowLayoutOutHead.setVgap(0);
    m_flowLayoutOutHead.setHgap(0);
    m_flowLayoutOutHead.setAlignment(FlowLayout.LEFT);
    m_jPanelOut.setLayout(m_borderLayoutOut);
    m_jLabelOutHead.setText("Log: ");
    m_jTextAreaOut.setEditable(false);
    m_jPanelOutHead.setLayout(m_flowLayoutOutHead);
    m_jMenuItemProps.setMnemonic('P');
    m_jMenuItemProps.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_P, Event.CTRL_MASK));
    m_jMenuItemProps.setActionCommand("properties");
    m_jMenuItemProps.setText("Proxy props");
    m_jMenuItemSave.setMnemonic((int)'S');
    m_jMenuItemSave.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_S, Event.CTRL_MASK));
    m_jMenuItemSave.setActionCommand("Save");
    m_jMenuItemSave.setText("Save config");
    m_jMenuFile.setText("File");
    m_jMenuFile.setActionCommand("file");
    m_jMenuFile.setMnemonic((int)'F');
    m_jMenuItemExit.setText("Exit");
    m_jMenuItemExit.setActionCommand("exit");
    m_jMenuItemExit.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_E, Event.CTRL_MASK));
    m_jMenuItemExit.setMnemonic((int)'E');
    m_jMenuItemClear.setText("Clear output");
    m_jMenuItemClear.setActionCommand("Clear");
    m_jMenuItemClear.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_R, Event.CTRL_MASK));
    m_jMenuItemClear.setMnemonic((int)'C');
    m_jMenuView.setText("View");
    m_jMenuView.setActionCommand("view");
    m_jMenuView.setMnemonic((int)'V');
    m_jMenuExtra.setText("Extra");
    m_jMenuExtra.setActionCommand("extra");
    m_jMenuExtra.setMnemonic((int)'E');
    m_jButtonOutClear.setIcon(ResouceManager.getCommonImageIcon( HttpTunnelClientServer.APPNAME, "clearout.gif"));
    m_jButtonOutClear.setMaximumSize(new Dimension(20, 18));
    m_jButtonOutClear.setMinimumSize(new Dimension(20, 18));
    m_jButtonOutClear.setPreferredSize(new Dimension(20, 18));
    m_jButtonOutClear.setToolTipText("Clear output");
    m_jButtonOutClear.setActionCommand("Clear");
    m_jMenuItemMaps.setText("Config maps");
    m_jMenuItemMaps.setActionCommand("configmaps");
    m_jMenuItemMaps.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_M, Event.CTRL_MASK));
    m_jMenuItemMaps.setMnemonic((int)'S');
    m_jMenuItemServletProp.setText("Servlet url");
    m_jMenuItemServletProp.setActionCommand("servletprop");
    m_jMenuItemServletProp.setMnemonic('S');
    m_jButtonNumConn.setActionCommand("NumConn");
    m_jButtonNumConn.setText("0");
    m_jButtonNumConn.setToolTipText("Number of connections");
    m_jButtonNumConn.setPreferredSize(new Dimension(20, 18));
    m_jButtonNumConn.setMinimumSize(new Dimension(20, 18));
    m_jButtonNumConn.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jButtonNumConn.setMaximumSize(new Dimension(52, 18));
    this.getContentPane().add(m_jPanelOut, BorderLayout.CENTER);
    m_jPanelOut.add(m_jScrollPaneOut, BorderLayout.CENTER);
    m_jPanelOut.add(m_jPanelOutHead, BorderLayout.NORTH);
    m_jPanelOutHead.add(m_jLabelOutHead, null);
    this.getContentPane().add(m_jToolBar, BorderLayout.NORTH);
    m_jToolBar.add(m_jButtonOutClear, null);
    m_jToolBar.add(m_jButtonNumConn, null);
    m_jScrollPaneOut.getViewport().add(m_jTextAreaOut, null);
    m_jMenuBar.add(m_jMenuFile);
    m_jMenuBar.add(m_jMenuView);
    m_jMenuBar.add(m_jMenuExtra);
    m_jMenuFile.add(m_jMenuItemProps);
    m_jMenuFile.add(m_jMenuItemServletProp);
    m_jMenuFile.add(m_jMenuItemExit);
    m_jMenuView.add(m_jMenuItemClear);
    m_jMenuExtra.add(m_jMenuItemSave);
    m_jMenuExtra.add(m_jMenuItemMaps);
  }

  //Das Applet starten
  public void start()
  {
  }

  //Das Applet anhalten
  public void stop()
  {
    for( int i = 0; i < m_vListeners.size(); i++)
    {
      ServerListener sl = (ServerListener)m_vListeners.elementAt( i);
      sl.interrupt();
    }
  }

  //Das Applet löschen
  public void destroy()
  {
  }

  //Applet-Information holen
  public String getAppletInfo()
  {
    return "HTTP tunnel which tunnels a TCP connection via a java servlet.";
  }

  //Parameter-Infos holen
  public String[][] getParameterInfo()
  {
    return null;
  }

  public void setRootComponent( Component comp)
  {
    GlobalAppProperties.instance().addRootComponent( APPNAME, comp);
  }

  public void addConnection( ServerConnection sc)
  {
    m_vConnections.add( sc);
    m_jButtonNumConn.setText( String.valueOf( m_vConnections.size()));
  }

  public void removeConnection( ServerConnection sc)
  {
    m_vConnections.removeElement( sc);
    m_jButtonNumConn.setText( String.valueOf( m_vConnections.size()));
  }

  /**
   * load config
   */
  public void _load()
  {
    if( m_appProps != null)
      m_appProps.load( ConfigPathUtil.getCurrentReadPath( APPNAME, PROPFILENAME));
    m_vMaps = ConnectionMap._load();
    m_jTextAreaOut.append( "Load configs\n");
    //System.out.println( "Load configs");
  }

  /**
   * save config
   */
  public void _save()
  {
    if( m_appProps != null)
    {
      // remove passwords
      String strProxyPwd = m_appProps.getProperty( GlobalAppProperties.PROXYPASSWORD);
      m_appProps.remove( GlobalAppProperties.PROXYPASSWORD);

      m_appProps.store( ConfigPathUtil.getCurrentSavePath( APPNAME, PROPFILENAME), PROPFILEHEADER);
      // set passwords
      if( strProxyPwd != null)
        m_appProps.setProperty( GlobalAppProperties.PROXYPASSWORD, strProxyPwd);
    }
    ConnectionMap._save( m_vMaps);
    m_jTextAreaOut.append( "Save configs\n");
    //System.out.println( "Save configs");
  }

  /**
   * exit application
   */
  public void exitApp()
  {
    stop();
    Component co = GlobalAppProperties.instance().getRootComponent( APPNAME);
    if( co != null && co instanceof JFrame)
      ((JFrame)co).dispose();
    System.exit(0);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      getRootPane().getParent().setCursor( new Cursor( Cursor.WAIT_CURSOR));
      String cmd = event.getActionCommand();
      if (cmd == "Clear")
      {
        m_jTextAreaOut.setText( "");
      }
      else if (cmd == "properties")
        props_actionPerformed(event);
      else if (cmd == "servletprop")
        servletprop_actionPerformed(event);
      else if (cmd == "exit")
        exit_actionPerformed(event);
      else if (cmd == "Save")
        _save();
      else if (cmd == "configmaps")
        configmaps_actionPerformed(event);
      else if (cmd == "NumConn")
        numconn_actionPerformed(event);
      getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
    }
  }

  /**
   * properties menu handler
   */
  void props_actionPerformed(ActionEvent e)
  {
    if( m_propsDlg == null)
      m_propsDlg = new ProxyPropertiesDialog( this, "Properties", true, m_appProps, APPNAME);
    m_propsDlg.setVisible( true);
  }

  /**
   * servletprop menu handler
   */
  void servletprop_actionPerformed(ActionEvent e)
  {
    AppPropertiesDialog dlg = new AppPropertiesDialog( null, m_appProps);
    String servletUrl = m_appProps.getProperty( SERVLET);
    for( int i = 0; i < m_vListeners.size(); ++i)
    {
      ((ServerListener)m_vListeners.elementAt( i)).setServletUrl( servletUrl);
    }
    dlg.setVisible( true);
  }

  /**
   * config maps menu handler
   */
  void configmaps_actionPerformed(ActionEvent e)
  {
    String servletUrl = m_appProps.getProperty( SERVLET);
    if( servletUrl == null)
    {
      servletprop_actionPerformed(null);
      servletUrl = m_appProps.getProperty( SERVLET);
    }
    if( m_configmapsDlg != null)
    {
      m_configmapsDlg.setVisible( false);
      m_configmapsDlg.dispose();
      m_configmapsDlg = null;
    }
    m_configmapsDlg =
    new ConfigConnectionMapDialog( null, APPNAME, "Config connection maps", false, m_vMaps, m_vListeners, servletUrl,
        m_jTextAreaOut, m_appProps);
    m_configmapsDlg.setVisible( true);
  }

  /**
   * numconn menu handler
   */
  void numconn_actionPerformed(ActionEvent e)
  {
    if( m_numConnDlg == null)
      m_numConnDlg = new ConnectionStatusDialog( null, APPNAME, "Connection status", false, m_vConnections);
    m_numConnDlg.init( m_vConnections);
    m_numConnDlg.setVisible( true);
  }

  /**
   * exit menu handler
   */
  void exit_actionPerformed(ActionEvent e)
  {
    exitApp();
  }

  public static void main(String[] args)
  {
    final HttpTunnelClientServer httpTunnelClientServer = new HttpTunnelClientServer();
    JFrame frame = new JFrame();
    frame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        httpTunnelClientServer.exitApp();
        //System.exit(0);
      }
    });
    frame.setTitle("HttpTunnelClientServer");
    frame.getContentPane().add( httpTunnelClientServer, BorderLayout.CENTER);
    httpTunnelClientServer.init();
    httpTunnelClientServer.setRootComponent( frame);
    httpTunnelClientServer.start();
    frame.setSize( 400, 200);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    //frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
    frame.setLocation((d.width - frame.getSize().width)-20, (d.height - frame.getSize().height)-20);
    frame.setIconImage( (new ImageIcon( haui.app.HttpTunnel.HttpTunnelClientServer.class.getResource("ht.gif"))).getImage());
    frame.setVisible(true);
    /*
    while( frame.isVisible())
    {
      try
      {
        Thread.sleep( 1000);
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
      frame.toFront();
    }
    */
  }

  // static initializer for setting look & feel
  static
  {
    try
    {
      GlobalAppProperties.instance().addAppClass( APPNAME, Class.forName( "haui.app.HttpTunnel.HttpTunnelClientServer" ) );
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch( Exception e )
    {}
  }
}