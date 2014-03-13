package haui.components;

import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Module:					LRShellPropertiesPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\LRShellPropertiesPanel.java,v $ <p> Description:    LRShell properties panel.<br> </p><p> Created:				02.10.2000	by	AE </p><p>
 * @history  				02.10.2000 - 05.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: LRShellPropertiesPanel.java,v $  Revision 1.4  2003-05-28 14:19:51+02  t026843  reorganisations  Revision 1.3  2002-09-18 11:16:17+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.2  2001-08-14 16:49:12+02  t026843  default button added  Revision 1.1  2000-10-13 09:09:45+02  t026843  bugfixes + changes  Revision 1.0  2000-10-05 14:48:43+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.4 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\LRShellPropertiesPanel.java,v 1.4 2003-05-28 14:19:51+02 t026843 Exp $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class LRShellPropertiesPanel extends JPanel
{
  private static final long serialVersionUID = 7145577846724160847L;
  
  AppProperties m_appProps;
  JPanel m_jPanelLogon = new JPanel();
  BorderLayout m_borderLayoutLogon = new BorderLayout();
  JPanel m_jPanelLogonLeft = new JPanel();
  GridLayout m_gridLayoutLogonLeft = new GridLayout();
  GridLayout m_gridLayoutLogonRight = new GridLayout();
  JPanel m_jPanelLogonRight = new JPanel();
  JLabel m_jLabelUser = new JLabel();
  JLabel m_jLabelLogonHead = new JLabel();
  JLabel m_jLabelPwd = new JLabel();
  JTextField m_jTextFieldUser = new JTextField();
  JLabel m_jLabelCmdHead = new JLabel();
  GridLayout m_gridLayoutCmdRight = new GridLayout();
  JPanel m_jPanelCmd = new JPanel();
  JTextField m_jTextFieldCmd = new JTextField();
  JPanel m_jPanelCmdRight = new JPanel();
  JLabel m_jLabelCmd = new JLabel();
  GridLayout m_gridLayoutCmdLeft = new GridLayout();
  BorderLayout m_borderLayoutCmd = new BorderLayout();
  JPanel m_jPanelCmdLeft = new JPanel();
  JPasswordField m_jPasswordFieldPwd = new JPasswordField();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelPropsLeft = new JPanel();
  GridLayout m_gridLayoutPropsRight = new GridLayout();
  JPanel m_jPanelPropsRight = new JPanel();
  GridLayout m_gridLayoutPropsLeft = new GridLayout();
  JPanel m_jPanelProps = new JPanel();
  BorderLayout m_borderLayoutProps = new BorderLayout();
  JLabel m_jLabelPropsHead = new JLabel();
  JLabel m_jLabelRead = new JLabel();
  JTextField m_jTextFieldRead = new JTextField();
  JLabel m_jLabelWrite = new JLabel();
  JTextField m_jTextFieldWrite = new JTextField();

  public LRShellPropertiesPanel()
  {
    this( null);
  }

  public LRShellPropertiesPanel( AppProperties appProps)
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
  }

  void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jPanelLogon.setLayout(m_borderLayoutLogon);
    m_jPanelLogonLeft.setLayout(m_gridLayoutLogonLeft);
    m_gridLayoutLogonLeft.setColumns(1);
    m_gridLayoutLogonLeft.setRows(2);
    m_gridLayoutLogonRight.setRows(2);
    m_gridLayoutLogonRight.setColumns(1);
    m_jPanelLogonRight.setLayout(m_gridLayoutLogonRight);
    m_jLabelUser.setText("Logon user:");
    m_jLabelLogonHead.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelLogonHead.setText("Logon settings:");
    m_jPanelLogon.setBorder(BorderFactory.createEtchedBorder());
    m_jLabelPwd.setText("Logon password:");
    m_jLabelCmdHead.setText("Command settings:");
    m_jLabelCmdHead.setFont(new java.awt.Font("Dialog", 1, 12));
    m_gridLayoutCmdRight.setColumns(1);
    m_jPanelCmd.setBorder(BorderFactory.createEtchedBorder());
    m_jPanelCmd.setLayout(m_borderLayoutCmd);
    m_jPanelCmdRight.setLayout(m_gridLayoutCmdRight);
    m_jLabelCmd.setText("Command cgi url:");
    m_gridLayoutCmdLeft.setColumns(1);
    m_jPanelCmdLeft.setLayout(m_gridLayoutCmdLeft);
    m_jPanelPropsLeft.setLayout(m_gridLayoutPropsLeft);
    m_gridLayoutPropsRight.setColumns(1);
    m_gridLayoutPropsRight.setRows(2);
    m_jPanelPropsRight.setLayout(m_gridLayoutPropsRight);
    m_gridLayoutPropsLeft.setColumns(1);
    m_gridLayoutPropsLeft.setRows(2);
    m_jPanelProps.setBorder(BorderFactory.createEtchedBorder());
    m_jPanelProps.setLayout(m_borderLayoutProps);
    m_jLabelPropsHead.setText("Remote property file settings:");
    m_jLabelPropsHead.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelRead.setText("Read props cgi:");
    m_jLabelWrite.setText("Write props cgi:");
    this.add(m_jPanelLogon, BorderLayout.NORTH);
    m_jPanelLogon.add(m_jPanelLogonLeft, BorderLayout.WEST);
    m_jPanelLogonLeft.add(m_jLabelUser, null);
    m_jPanelLogonLeft.add(m_jLabelPwd, null);
    m_jPanelLogon.add(m_jPanelLogonRight, BorderLayout.CENTER);
    m_jPanelLogonRight.add(m_jTextFieldUser, null);
    m_jPanelLogonRight.add(m_jPasswordFieldPwd, null);
    m_jPanelLogon.add(m_jLabelLogonHead, BorderLayout.NORTH);
    this.add(m_jPanelCmd, BorderLayout.SOUTH);
    m_jPanelCmd.add(m_jPanelCmdLeft, BorderLayout.WEST);
    m_jPanelCmdLeft.add(m_jLabelCmd, null);
    m_jPanelCmd.add(m_jPanelCmdRight, BorderLayout.CENTER);
    m_jPanelCmdRight.add(m_jTextFieldCmd, null);
    m_jPanelCmd.add(m_jLabelCmdHead, BorderLayout.NORTH);
    this.add(m_jPanelProps, BorderLayout.CENTER);
    m_jPanelProps.add(m_jPanelPropsLeft, BorderLayout.WEST);
    m_jPanelPropsLeft.add(m_jLabelRead, null);
    m_jPanelPropsLeft.add(m_jLabelWrite, null);
    m_jPanelProps.add(m_jPanelPropsRight, BorderLayout.CENTER);
    m_jPanelPropsRight.add(m_jTextFieldRead, null);
    m_jPanelPropsRight.add(m_jTextFieldWrite, null);
    m_jPanelProps.add(m_jLabelPropsHead, BorderLayout.NORTH);
  }

  public void _init()
  {
    m_jTextFieldUser.setText( m_appProps.getProperty( GlobalApplicationContext.LOGONUSER));
    m_jPasswordFieldPwd.setText( m_appProps.getProperty( GlobalApplicationContext.LOGONPASSWORD));
    m_jTextFieldRead.setText( m_appProps.getProperty( LRShell.READFILECGIURL));
    m_jTextFieldWrite.setText( m_appProps.getProperty( LRShell.WRITEFILECGIURL));
    m_jTextFieldCmd.setText( m_appProps.getProperty( LRShell.FILEMANAGERCGIURL));
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
    if( m_jTextFieldRead.getText() == null || m_jTextFieldRead.getText().equals( ""))
      m_appProps.remove( LRShell.READFILECGIURL);
    else
      m_appProps.setProperty( LRShell.READFILECGIURL, m_jTextFieldRead.getText());
    if( m_jTextFieldWrite.getText() == null || m_jTextFieldWrite.getText().equals( ""))
      m_appProps.remove( LRShell.WRITEFILECGIURL);
    else
      m_appProps.setProperty( LRShell.WRITEFILECGIURL, m_jTextFieldWrite.getText());
    if( m_jTextFieldCmd.getText() == null || m_jTextFieldCmd.getText().equals( ""))
      m_appProps.remove( LRShell.FILEMANAGERCGIURL);
    else
      m_appProps.setProperty( LRShell.FILEMANAGERCGIURL, m_jTextFieldCmd.getText());
  }

  public void requestFocus()
  {
    m_jTextFieldUser.requestFocus();
  }
}
