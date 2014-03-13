package haui.app.fm;

import haui.components.*;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Module:					FipPropertiesPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FipPropertiesPanel.java,v $ <p> Description:    FileInfoPanel properties panel.<br> </p><p> Created:				25.06.2001	by	AE </p><p>
 * @history  				25.06.2001 - 26.06.2001	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FipPropertiesPanel.java,v $  Revision 1.2  2004-06-22 14:08:56+02  t026843  bigger changes  Revision 1.1  2003-05-28 14:19:49+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:51+02  t026843  Initial revision  Revision 1.4  2002-09-18 11:16:16+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.3  2002-09-03 17:08:01+02  t026843  - CgiTypeFile is now full functional.  - Migrated to the extended filemanager.pl script.  Revision 1.2  2002-08-07 15:25:26+02  t026843  Ftp support via filetype added.  Some bugfixes.  Revision 1.1  2001-08-14 16:48:42+02  t026843  default button added  Revision 1.0  2001-07-20 16:34:25+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FipPropertiesPanel.java,v 1.2 2004-06-22 14:08:56+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FipPropertiesPanel extends JPanel
{
  private static final long serialVersionUID = 5805665237232000749L;
  
  AppProperties m_appProps;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelCgiSettings = new JPanel();
  BorderLayout m_borderLayoutCgiSettings = new BorderLayout();
  GridLayout m_gridLayoutLogonLeft = new GridLayout();
  JLabel m_jLabelPwd = new JLabel();
  JLabel m_jLabelUser = new JLabel();
  JPanel m_jPanelLogonLeft = new JPanel();
  JLabel m_jLabelLogonHead = new JLabel();
  GridLayout m_gridLayoutLogonRight = new GridLayout();
  JPasswordField m_jPasswordFieldPwd = new JPasswordField();
  BorderLayout m_borderLayoutLogon = new BorderLayout();
  JTextField m_jTextFieldUser = new JTextField();
  JPanel m_jPanelLogonRight = new JPanel();
  JPanel m_jPanelLogon = new JPanel();
  JLabel m_jLabelFm = new JLabel();
  GridLayout m_gridLayoutPropsRight = new GridLayout();
  JLabel m_jLabelFmHead = new JLabel();
  JPanel m_jPanelFmRight = new JPanel();
  JPanel m_jPanelFm = new JPanel();
  GridLayout m_gridLayoutPropsLeft = new GridLayout();
  BorderLayout m_borderLayoutProps = new BorderLayout();
  JTextField m_jTextFieldFm = new JTextField();
  JPanel m_jPanelFmLeft = new JPanel();
  JPanel m_jPanelSelect = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutSelect = new VerticalFlowLayout();
  ButtonGroup m_buttonGroupSelect = new ButtonGroup();
  JRadioButton m_jRadioButtonFtp = new JRadioButton();
  JRadioButton m_jRadioButtonCgi = new JRadioButton();
  JLabel m_jLabelSelect = new JLabel();
  JPanel m_jPanelFtp = new JPanel();
  BorderLayout m_borderLayoutFtp = new BorderLayout();
  JLabel m_jLabelFtp = new JLabel();
  GridLayout m_gridLayoutFtpLeft = new GridLayout();
  JLabel m_jLabelFtpPwd = new JLabel();
  JLabel m_jLabelFtpUser = new JLabel();
  JPanel m_jPanelFtpLeft = new JPanel();
  JLabel m_jLabelFtpUrl = new JLabel();
  JTextField m_jTextFieldFtpUrl = new JTextField();
  GridLayout m_gridLayoutFtpRight = new GridLayout();
  JPasswordField m_jPasswordFieldFtpPwd = new JPasswordField();
  JTextField m_jTextFieldFtpUser = new JTextField();
  JPanel m_jPanelFtpRight = new JPanel();
  JExDialog m_jExDialogParent;

  public FipPropertiesPanel()
  {
    this( null, null);
  }

  public FipPropertiesPanel( JExDialog jExDialogParent, AppProperties appProps)
  {
    if( appProps != null)
      m_appProps = appProps;
    m_jExDialogParent = jExDialogParent;
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jRadioButtonFtp.addActionListener( actionlis);
    m_jRadioButtonCgi.addActionListener( actionlis);
  }

  void jbInit() throws Exception
  {
    m_jPanelLogon.setBorder(BorderFactory.createEtchedBorder());
    m_jPanelLogon.setLayout(m_borderLayoutLogon);
    m_jPanelLogonRight.setLayout(m_gridLayoutLogonRight);
    m_gridLayoutLogonRight.setColumns(1);
    m_gridLayoutLogonRight.setRows(2);
    m_jLabelLogonHead.setText("Logon cgi settings:");
    m_jLabelLogonHead.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jPanelLogonLeft.setLayout(m_gridLayoutLogonLeft);
    m_jLabelUser.setText("Logon user:");
    m_jLabelPwd.setText("Logon password:");
    m_gridLayoutLogonLeft.setRows(2);
    m_gridLayoutLogonLeft.setColumns(1);
    this.setLayout(m_borderLayoutBase);
    m_jPanelCgiSettings.setLayout(m_borderLayoutCgiSettings);
    m_jLabelFm.setText("FileManager cgi:");
    m_gridLayoutPropsRight.setColumns(1);
    m_jLabelFmHead.setText("FileManager cgi settings:");
    m_jLabelFmHead.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jPanelFmRight.setLayout(m_gridLayoutPropsRight);
    m_jPanelFm.setBorder(BorderFactory.createEtchedBorder());
    m_jPanelFm.setLayout(m_borderLayoutProps);
    m_gridLayoutPropsLeft.setColumns(1);
    m_jPanelFmLeft.setLayout(m_gridLayoutPropsLeft);
    m_jPanelSelect.setLayout(m_verticalFlowLayoutSelect);
    m_verticalFlowLayoutSelect.setHgap(1);
    m_verticalFlowLayoutSelect.setVgap(1);
    m_jRadioButtonFtp.setText("Ftp connection");
    m_jRadioButtonFtp.setHorizontalTextPosition(SwingConstants.LEFT);
    m_jRadioButtonFtp.setActionCommand("ftp");
    m_jRadioButtonCgi.setText("Cgi connection");
    m_jRadioButtonCgi.setHorizontalTextPosition(SwingConstants.LEFT);
    m_jRadioButtonCgi.setActionCommand("cgi");
    m_jPanelFtp.setLayout(m_borderLayoutFtp);
    m_jLabelFtp.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelFtp.setText("Ftp connection settings:");
    m_gridLayoutFtpLeft.setColumns(1);
    m_gridLayoutFtpLeft.setRows(3);
    m_jLabelFtpPwd.setText("Ftp password:");
    m_jLabelFtpUser.setText("Ftp user:");
    m_jPanelFtpLeft.setLayout(m_gridLayoutFtpLeft);
    m_jLabelFtpUrl.setText("Ftp url:");
    m_gridLayoutFtpRight.setRows(3);
    m_gridLayoutFtpRight.setColumns(1);
    m_jPanelFtpRight.setLayout(m_gridLayoutFtpRight);
    m_jPanelFtp.setBorder(BorderFactory.createEtchedBorder());
    m_buttonGroupSelect.add( m_jRadioButtonFtp);
    m_buttonGroupSelect.add( m_jRadioButtonCgi);
    m_jLabelSelect.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelSelect.setText("Select connection type:");
    m_jPanelSelect.setBorder(BorderFactory.createEtchedBorder());
    this.add(m_jPanelCgiSettings, BorderLayout.CENTER);
    m_jPanelCgiSettings.add(m_jPanelLogon, BorderLayout.NORTH);
    m_jPanelLogon.add(m_jPanelLogonLeft, BorderLayout.WEST);
    m_jPanelLogonLeft.add(m_jLabelUser, null);
    m_jPanelLogonLeft.add(m_jLabelPwd, null);
    m_jPanelLogon.add(m_jPanelLogonRight, BorderLayout.CENTER);
    m_jPanelLogonRight.add(m_jTextFieldUser, null);
    m_jPanelLogonRight.add(m_jPasswordFieldPwd, null);
    m_jPanelLogon.add(m_jLabelLogonHead, BorderLayout.NORTH);
    m_jPanelCgiSettings.add(m_jPanelFm, BorderLayout.CENTER);
    m_jPanelFm.add(m_jPanelFmLeft, BorderLayout.WEST);
    m_jPanelFmLeft.add(m_jLabelFm, null);
    m_jPanelFm.add(m_jPanelFmRight, BorderLayout.CENTER);
    m_jPanelFmRight.add(m_jTextFieldFm, null);
    m_jPanelFm.add(m_jLabelFmHead, BorderLayout.NORTH);
    this.add(m_jPanelSelect, BorderLayout.NORTH);
    m_jPanelSelect.add(m_jLabelSelect, null);
    m_jPanelSelect.add(m_jRadioButtonFtp, null);
    m_jPanelSelect.add(m_jRadioButtonCgi, null);
    this.add(m_jPanelFtp, BorderLayout.SOUTH);
    m_jPanelFtp.add(m_jLabelFtp, BorderLayout.NORTH);
    m_jPanelFtp.add(m_jPanelFtpLeft, BorderLayout.WEST);
    m_jPanelFtpLeft.add(m_jLabelFtpUrl, null);
    m_jPanelFtpLeft.add(m_jLabelFtpUser, null);
    m_jPanelFtpLeft.add(m_jLabelFtpPwd, null);
    m_jPanelFtp.add(m_jPanelFtpRight, BorderLayout.CENTER);
    m_jPanelFtpRight.add(m_jTextFieldFtpUrl, null);
    m_jPanelFtpRight.add(m_jTextFieldFtpUser, null);
    m_jPanelFtpRight.add(m_jPasswordFieldFtpPwd, null);
  }

  public void _init()
  {
    if( m_appProps.getProperty( FileInfoPanel.FILEMANAGERCGIURL) != null)
    {
      m_jRadioButtonCgi.setEnabled( true);
      m_jRadioButtonCgi.setSelected(true);
      m_jRadioButtonFtp.setEnabled( false);
      enableCgiPanel();
      disableFtpPanel();
      m_jTextFieldUser.setText( m_appProps.getProperty( GlobalApplicationContext.LOGONUSER));
      m_jPasswordFieldPwd.setText( m_appProps.getProperty( GlobalApplicationContext.LOGONPASSWORD));
      m_jTextFieldFm.setText( m_appProps.getProperty( FileInfoPanel.FILEMANAGERCGIURL));
      m_jTextFieldFtpUrl.setText( "");
      m_jTextFieldFtpUser.setText( "");
      m_jPasswordFieldFtpPwd.setText( "");
    }
    else
    {
      m_jRadioButtonFtp.setEnabled( true);
      m_jRadioButtonFtp.setSelected(true);
      if( m_appProps.getProperty( FileInfoPanel.FTPURL) != null)
        m_jRadioButtonCgi.setEnabled( false);
      else
        m_jRadioButtonCgi.setEnabled( true);
      enableFtpPanel();
      disableCgiPanel();
      m_jTextFieldFtpUrl.setText( m_appProps.getProperty( FileInfoPanel.FTPURL));
      m_jTextFieldFtpUser.setText( m_appProps.getProperty( FileInfoPanel.FTPUSER));
      m_jPasswordFieldFtpPwd.setText( m_appProps.getProperty( FileInfoPanel.FTPPASSWORD));
      m_jTextFieldUser.setText( "");
      m_jPasswordFieldPwd.setText( "");
      m_jTextFieldFm.setText( "");
    }
  }

  public void _save()
  {
    if( m_appProps.getProperty( FileInfoPanel.FILEMANAGERCGIURL) != null)
    {
      m_appProps.remove( GlobalApplicationContext.LOGONUSER);
      m_appProps.remove( GlobalApplicationContext.LOGONPASSWORD);
      m_appProps.remove( FileInfoPanel.FILEMANAGERCGIURL);
      m_appProps.remove( FileInfoPanel.FTPURL);
      m_appProps.remove( FileInfoPanel.FTPUSER);
      m_appProps.remove( FileInfoPanel.FTPPASSWORD);
      if( m_jTextFieldUser.getText() != null && !m_jTextFieldUser.getText().equals( ""))
        m_appProps.setProperty( GlobalApplicationContext.LOGONUSER, m_jTextFieldUser.getText());
      if( m_jPasswordFieldPwd.getPassword() != null && !(new String( m_jPasswordFieldPwd.getPassword())).equals( ""))
        m_appProps.setProperty( GlobalApplicationContext.LOGONPASSWORD, (new String( m_jPasswordFieldPwd.getPassword())));
      if( m_jTextFieldFm.getText() != null && !m_jTextFieldFm.getText().equals( ""))
        m_appProps.setProperty( FileInfoPanel.FILEMANAGERCGIURL, m_jTextFieldFm.getText());
    }
    else
    {
      m_appProps.remove( GlobalApplicationContext.LOGONUSER);
      m_appProps.remove( GlobalApplicationContext.LOGONPASSWORD);
      m_appProps.remove( FileInfoPanel.FILEMANAGERCGIURL);
      m_appProps.remove( FileInfoPanel.FTPURL);
      m_appProps.remove( FileInfoPanel.FTPUSER);
      m_appProps.remove( FileInfoPanel.FTPPASSWORD);
      if( m_jTextFieldFtpUrl.getText() != null && !m_jTextFieldFtpUrl.getText().equals( ""))
        m_appProps.setProperty( FileInfoPanel.FTPURL, m_jTextFieldFtpUrl.getText());
      if( m_jTextFieldFtpUser.getText() != null && !m_jTextFieldFtpUser.getText().equals( ""))
        m_appProps.setProperty( FileInfoPanel.FTPUSER, m_jTextFieldFtpUser.getText());
      if( m_jPasswordFieldFtpPwd.getPassword() != null && !(new String( m_jPasswordFieldFtpPwd.getPassword())).equals( ""))
        m_appProps.setProperty( FileInfoPanel.FTPPASSWORD, (new String( m_jPasswordFieldFtpPwd.getPassword())));
    }
  }

  public void update( AppProperties appProps)
  {
    m_appProps = appProps;
    _init();
  }

  public void requestFocus()
  {
    if( m_appProps.getProperty( FileInfoPanel.FILEMANAGERCGIURL) != null)
      m_jTextFieldUser.requestFocus();
    else if( m_appProps.getProperty( FileInfoPanel.FTPURL) != null)
      m_jTextFieldFtpUrl.requestFocus();
    else
      m_jRadioButtonCgi.requestFocus();
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "ftp")
      {
        enableFtpPanel();
        disableCgiPanel();
        //if( m_jExDialogParent != null)
          //m_jExDialogParent.pack();
      }
      else if (cmd == "cgi")
      {
        enableCgiPanel();
        disableFtpPanel();
        //if( m_jExDialogParent != null)
          //m_jExDialogParent.pack();
      }
    }
  }

  public void enableCgiPanel()
  {
    m_jLabelPwd.setEnabled( true);
    m_jLabelUser.setEnabled( true);
    m_jLabelLogonHead.setEnabled( true);
    m_jPasswordFieldPwd.setEnabled( true);
    m_jTextFieldUser.setEnabled( true);
    m_jLabelFmHead.setEnabled( true);
    m_jLabelFm.setEnabled( true);
    m_jTextFieldFm.setEnabled( true);
    //add( m_jPanelCgiSettings);
    //m_jPanelCgiSettings.setVisible( true);
  }

  public void disableCgiPanel()
  {
    m_jLabelPwd.setEnabled( false);
    m_jLabelUser.setEnabled( false);
    m_jLabelLogonHead.setEnabled( false);
    m_jPasswordFieldPwd.setEnabled( false);
    m_jTextFieldUser.setEnabled( false);
    m_jLabelFmHead.setEnabled( false);
    m_jLabelFm.setEnabled( false);
    m_jTextFieldFm.setEnabled( false);
    //remove( m_jPanelCgiSettings);
    //m_jPanelCgiSettings.setVisible( false);
  }

  public void enableFtpPanel()
  {
    m_jLabelFtp.setEnabled( true);
    m_jLabelFtpPwd.setEnabled( true);
    m_jLabelFtpUser.setEnabled( true);
    m_jLabelFtpUrl.setEnabled( true);
    m_jTextFieldFtpUrl.setEnabled( true);
    m_jPasswordFieldFtpPwd.setEnabled( true);
    m_jTextFieldFtpUser.setEnabled( true);
    //add( m_jPanelFtp);
    //m_jPanelFtp.setVisible( true);
  }

  public void disableFtpPanel()
  {
    m_jLabelFtp.setEnabled( false);
    m_jLabelFtpPwd.setEnabled( false);
    m_jLabelFtpUser.setEnabled( false);
    m_jLabelFtpUrl.setEnabled( false);
    m_jTextFieldFtpUrl.setEnabled( false);
    m_jPasswordFieldFtpPwd.setEnabled( false);
    m_jTextFieldFtpUser.setEnabled( false);
    //remove( m_jPanelFtp);
    //m_jPanelFtp.setVisible( false);
  }
}
