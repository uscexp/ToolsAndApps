package haui.components;

import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Module:					ProxyPropertiesPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ProxyPropertiesPanel.java,v $ <p> Description:    Proxy properties panel.<br> </p><p> Created:				29.09.2000	by	AE </p><p>
 * @history  				29.09.2000 - 05.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ProxyPropertiesPanel.java,v $  Revision 1.2  2003-05-28 14:19:55+02  t026843  reorganisations  Revision 1.1  2001-08-14 16:49:13+02  t026843  default button added  Revision 1.0  2000-10-05 14:48:45+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.2 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ProxyPropertiesPanel.java,v 1.2 2003-05-28 14:19:55+02 t026843 Exp $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ProxyPropertiesPanel extends JPanel
{
  private static final long serialVersionUID = 4997421696398451528L;
  
  AppProperties m_appProps;
  boolean m_blEnable;
  String m_strHost;
  String m_strPort;
  String m_strNoProxyList;

  JCheckBox m_jCheckBoxProxyEnabled = new JCheckBox();
  JLabel m_jLabelHost = new JLabel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelLeft = new JPanel();
  GridLayout m_gridLayoutLeft = new GridLayout();
  JPanel m_jPanelRight = new JPanel();
  GridLayout m_gridLayoutRight = new GridLayout();
  JLabel m_jLabelPort = new JLabel();
  JLabel m_jLabelNonProxyList = new JLabel();
  JTextField m_jTextFieldHost = new JTextField();
  JTextField m_jTextFieldPort = new JTextField();
  JTextField m_jTextFieldNonProxyList = new JTextField();
  ProxyAuthorizationPanel m_jPanelAuth = new ProxyAuthorizationPanel();

  public ProxyPropertiesPanel()
  {
    this( null);
  }

  public ProxyPropertiesPanel( AppProperties appProps)
  {
    if( appProps != null)
      m_appProps = appProps;
    m_jPanelAuth = new ProxyAuthorizationPanel( m_appProps);
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jCheckBoxProxyEnabled.addActionListener( actionlis);
    enableComponents();
  }

  private void jbInit() throws Exception
  {
    m_jCheckBoxProxyEnabled.setText("Enable Proxy/Firewall authentication");
    m_jCheckBoxProxyEnabled.setActionCommand("proxy_enable");
    m_jLabelHost.setText("Host:");
    this.setLayout(m_borderLayoutBase);
    m_jPanelLeft.setLayout(m_gridLayoutLeft);
    m_gridLayoutLeft.setColumns(1);
    m_gridLayoutLeft.setRows(3);
    m_jPanelRight.setLayout(m_gridLayoutRight);
    m_gridLayoutRight.setColumns(1);
    m_gridLayoutRight.setRows(3);
    m_jLabelPort.setText("Port:");
    m_jLabelNonProxyList.setText("No proxy for:");
    this.add(m_jCheckBoxProxyEnabled, BorderLayout.NORTH);
    m_jPanelLeft.add(m_jLabelHost, null);
    m_jPanelLeft.add(m_jLabelPort, null);
    m_jPanelLeft.add(m_jLabelNonProxyList, null);
    this.add(m_jPanelLeft, BorderLayout.WEST);
    m_jPanelRight.add(m_jTextFieldHost, null);
    m_jPanelRight.add(m_jTextFieldPort, null);
    m_jPanelRight.add(m_jTextFieldNonProxyList, null);
    this.add(m_jPanelRight, BorderLayout.CENTER);
    this.add(m_jPanelAuth, BorderLayout.SOUTH);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "proxy_enable")
        enableComponents();
    }
  }

  public ProxyAuthorizationPanel getAuthPanel()
  {
    return m_jPanelAuth;
  }

  void enableComponents()
  {
    boolean bl = m_jCheckBoxProxyEnabled.isSelected();
    m_jTextFieldHost.setEnabled( bl);
    m_jTextFieldPort.setEnabled( bl);
    m_jTextFieldNonProxyList.setEnabled( bl);
    m_jPanelAuth.enableComponents( bl);
  }

  public void _init()
  {
    if( m_appProps != null)
    {
      m_blEnable = m_appProps.getBooleanProperty( GlobalApplicationContext.PROXYENABLE);
      enableComponents();
      m_strHost = m_appProps.getProperty( GlobalApplicationContext.PROXYHOST);
      m_strPort = m_appProps.getProperty( GlobalApplicationContext.PROXYPORT);
      m_strNoProxyList = m_appProps.getProperty( GlobalApplicationContext.PROXYNOPROXYLIST);
    }

    m_jCheckBoxProxyEnabled.setSelected( m_blEnable);
    m_jTextFieldHost.setText( m_strHost);
    m_jTextFieldPort.setText( m_strPort);
    m_jTextFieldNonProxyList.setText( m_strNoProxyList);
    m_jPanelAuth._init();
  }

  public void _save()
  {
    m_blEnable = m_jCheckBoxProxyEnabled.isSelected();
    m_strHost = m_jTextFieldHost.getText();
    m_strPort = m_jTextFieldPort.getText();
    m_strNoProxyList = m_jTextFieldNonProxyList.getText();

    if( m_appProps != null)
    {
      m_appProps.setBooleanProperty( GlobalApplicationContext.PROXYENABLE, m_blEnable);
      if( m_strHost == null || m_strHost.equals( ""))
        m_appProps.remove( GlobalApplicationContext.PROXYHOST);
      else
        m_appProps.setProperty( GlobalApplicationContext.PROXYHOST, m_strHost);
      if( m_strPort == null || m_strPort.equals( ""))
        m_appProps.remove( GlobalApplicationContext.PROXYPORT);
      else
        m_appProps.setProperty( GlobalApplicationContext.PROXYPORT, m_strPort);
      if( m_strNoProxyList == null || m_strNoProxyList.equals( ""))
        m_appProps.remove( GlobalApplicationContext.PROXYNOPROXYLIST);
      else
        m_appProps.setProperty( GlobalApplicationContext.PROXYNOPROXYLIST, m_strNoProxyList);
    }
    m_jPanelAuth._save();
  }

  public void requestFocus()
  {
    m_jCheckBoxProxyEnabled.requestFocus();
  }
}
