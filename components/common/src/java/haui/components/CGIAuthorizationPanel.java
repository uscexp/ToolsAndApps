package haui.components;

import haui.util.*;
import java.awt.*;
import javax.swing.*;

/**
 * Module:					CGIAuthorizationPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\CGIAuthorizationPanel.java,v $ <p> Description:    URL authorization panel.<br> </p><p> Created:				19.09.2002	by	AE </p><p>
 * @history  				19.09.2002 - 19.09.2002	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: CGIAuthorizationPanel.java,v $  Revision 1.1  2003-05-28 14:19:59+02  t026843  reorganisations  Revision 1.0  2002-09-18 11:20:12+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\CGIAuthorizationPanel.java,v 1.1 2003-05-28 14:19:59+02 t026843 Exp $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class CGIAuthorizationPanel
extends JPanel
{
  private static final long serialVersionUID = 8843388351118563476L;
  
  final static String HEAD = "Authorization for this CGI:";
  AppProperties m_appProps;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelMain = new JPanel();
  GridLayout m_gridLayoutMain = new GridLayout();
  JLabel m_jLabelUser = new JLabel();
  JTextField m_jTextFieldUser = new JTextField();
  JLabel m_jLabelPassword = new JLabel();
  JPasswordField m_jPasswordFieldPwd = new JPasswordField();
  JTextArea m_jTextAreaHead = new JTextArea();

  public CGIAuthorizationPanel()
  {
    this( null);
  }

  public CGIAuthorizationPanel( AppProperties appProps)
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
    m_jPanelMain.setLayout(m_gridLayoutMain);
    m_gridLayoutMain.setColumns(1);
    m_gridLayoutMain.setRows(4);
    m_jLabelUser.setText("User:");
    m_jLabelPassword.setText("Password:");
    m_jTextAreaHead.setDisabledTextColor(Color.black);
    m_jTextAreaHead.setBackground(Color.lightGray);
    m_jTextAreaHead.setEnabled(false);
    m_jTextAreaHead.setText( HEAD);
    m_jTextAreaHead.setEditable(false);
    m_jTextAreaHead.setFont(new java.awt.Font("Monospaced", 1, 12));
    this.add(m_jPanelMain, BorderLayout.CENTER);
    m_jPanelMain.add(m_jLabelUser, null);
    m_jPanelMain.add(m_jTextFieldUser, null);
    m_jPanelMain.add(m_jLabelPassword, null);
    m_jPanelMain.add(m_jPasswordFieldPwd, null);
    this.add(m_jTextAreaHead, BorderLayout.NORTH);
  }

  public void setRequestText( String str)
  {
    m_jTextAreaHead.setText( str);
    int iHeight = (str.length() * (m_jTextAreaHead.getFontMetrics(m_jTextAreaHead.getFont()).getWidths())[0]/300 + 1)
                  * (m_jTextAreaHead.getFontMetrics(m_jTextAreaHead.getFont()).getHeight());
    m_jTextAreaHead.setPreferredSize( new Dimension( 300, iHeight));
  }

  public void _init()
  {
    m_jTextFieldUser.setText( m_appProps.getProperty( GlobalApplicationContext.LOGONUSER));
    m_jPasswordFieldPwd.setText( m_appProps.getProperty( GlobalApplicationContext.LOGONPASSWORD));
  }

  public void _save()
  {
    if( m_jTextFieldUser.getText() == null || m_jTextFieldUser.getText().equals( ""))
      m_appProps.remove( GlobalApplicationContext.LOGONUSER);
    else
      m_appProps.setProperty( GlobalApplicationContext.LOGONUSER, m_jTextFieldUser.getText());
    if( m_jPasswordFieldPwd.getPassword() == null || (new String( m_jPasswordFieldPwd.getPassword())).equals( ""))
      m_appProps.remove( GlobalApplicationContext.LOGONPASSWORD);
    else
      m_appProps.setProperty( GlobalApplicationContext.LOGONPASSWORD, (new String( m_jPasswordFieldPwd.getPassword())));
  }

  public void requestFocus()
  {
    m_jTextFieldUser.requestFocus();
  }
}