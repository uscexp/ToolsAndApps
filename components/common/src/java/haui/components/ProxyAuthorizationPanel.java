
package haui.components;

import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.*;
import javax.swing.*;

/**
 * Module:					ProxyAuthorizationPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ProxyAuthorizationPanel.java,v $ <p> Description:    Proxy authorization panel.<br> </p><p> Created:				05.10.2000	by	AE </p><p>
 * @history  				05.10.2000	by	AE: Created.<br>  06.10.2000	by	AE: m_jTextAreaHead size calculation added.<br>  </p><p>  Modification:<br>  $Log: ProxyAuthorizationPanel.java,v $  Revision 1.5  2003-05-28 14:19:54+02  t026843  reorganisations  Revision 1.4  2002-06-17 17:17:16+02  t026843  <>  Revision 1.3  2001-08-14 16:49:14+02  t026843  default button added  Revision 1.2  2000-10-16 11:38:34+02  t026843  cosmetics  Revision 1.1  2000-10-13 09:09:45+02  t026843  bugfixes + changes  Revision 1.0  2000-10-05 14:48:44+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.5 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ProxyAuthorizationPanel.java,v 1.5 2003-05-28 14:19:54+02 t026843 Exp $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ProxyAuthorizationPanel
extends JPanel
{
  private static final long serialVersionUID = 219589534236571096L;
  
  public final static String HEAD = "Authorization for this proxy:";
  AppProperties m_appProps;
  String m_strUser;
  String m_strPwd;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelMain = new JPanel();
  GridLayout m_gridLayoutMain = new GridLayout();
  JLabel m_jLabelUser = new JLabel();
  JTextField m_jTextFieldUser = new JTextField();
  JLabel m_jLabelPassword = new JLabel();
  JPasswordField m_jPasswordFieldPwd = new JPasswordField();
  JTextArea m_jTextAreaHead = new JTextArea();

  public ProxyAuthorizationPanel()
  {
    this( null);
  }

  public ProxyAuthorizationPanel( AppProperties appProps)
  {
    if( appProps != null)
      m_appProps = appProps;
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    m_jTextAreaHead.setLineWrap( true);
  }

  private void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jTextAreaHead.setDisabledTextColor(Color.black);
    m_jTextAreaHead.setBackground(Color.lightGray);
    m_jTextAreaHead.setEnabled(false);
    m_jTextAreaHead.setText( HEAD);
    m_jTextAreaHead.setEditable(false);
    m_jTextAreaHead.setFont(new java.awt.Font("Monospaced", 1, 12));
    this.add(m_jTextAreaHead, BorderLayout.NORTH);
    m_gridLayoutMain.setColumns(1);
    m_gridLayoutMain.setRows(4);
    m_jPanelMain.setLayout(m_gridLayoutMain);
    m_jLabelUser.setText("User:");
    m_jLabelPassword.setText("Password:");
    this.add(m_jPanelMain, BorderLayout.CENTER);
    m_jPanelMain.add(m_jLabelUser, null);
    m_jPanelMain.add(m_jTextFieldUser, null);
    m_jPanelMain.add(m_jLabelPassword, null);
    m_jPanelMain.add(m_jPasswordFieldPwd, null);
  }

  public void setRequestText( String str)
  {
    m_jTextAreaHead.setText( str);
    int iHeight = (str.length() * (m_jTextAreaHead.getFontMetrics(m_jTextAreaHead.getFont()).getWidths())[0]/300 + 1)
                  * (m_jTextAreaHead.getFontMetrics(m_jTextAreaHead.getFont()).getHeight());
    m_jTextAreaHead.setPreferredSize( new Dimension( 300, iHeight));
  }

  void enableComponents( boolean bl)
  {
    m_jTextFieldUser.setEnabled( bl);
    m_jPasswordFieldPwd.setEnabled( bl);
  }

  public void _init()
  {
    if( m_appProps != null)
    {
      m_strUser = m_appProps.getProperty( GlobalApplicationContext.PROXYUSER);
      m_strPwd = m_appProps.getProperty( GlobalApplicationContext.PROXYPASSWORD);
    }

    m_jTextFieldUser.setText( m_strUser);
    m_jPasswordFieldPwd.setText( m_strPwd);
  }

  public void _save()
  {
    m_strUser = m_jTextFieldUser.getText();
    m_strPwd = new String( m_jPasswordFieldPwd.getPassword());
    if( m_appProps != null)
    {
      if( m_strUser == null || m_strUser.equals( ""))
        m_appProps.remove( GlobalApplicationContext.PROXYUSER);
      else
        m_appProps.setProperty( GlobalApplicationContext.PROXYUSER, m_strUser);
      if( m_strPwd == null || m_strPwd.equals( ""))
        m_appProps.remove( GlobalApplicationContext.PROXYPASSWORD);
      else
        m_appProps.setProperty( GlobalApplicationContext.PROXYPASSWORD, m_strPwd);
    }
  }

  public void requestFocus()
  {
    m_jTextFieldUser.requestFocus();
  }
}