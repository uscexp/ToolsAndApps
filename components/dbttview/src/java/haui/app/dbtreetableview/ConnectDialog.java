package haui.app.dbtreetableview;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import haui.util.AppProperties;
import haui.components.JExDialog;
import haui.components.VerticalFlowLayout;

/**
 *		Module:					ConnectDialog.java<br>
 *										$Source: $
 *<p>
 *		Description:    Dialog to config db driver.<br>
 *</p><p>
 *		Created:				04.10.2002	by	AE
 *</p><p>
 *		@history				04.10.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ConnectDialog
  extends JExDialog
{
  // member variables
  DBAlias m_dba;
  boolean m_blCancelled = false;
  String m_strUsr;
  String m_strPwd;

  // GUI member variables
  JPanel m_panelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  JPanel m_jPanelCenter = new JPanel();
  JPanel m_jPanelLabels = new JPanel();
  JPanel m_jPanelFields = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutLabels = new VerticalFlowLayout();
  VerticalFlowLayout m_verticalFlowLayoutFields = new VerticalFlowLayout();
  FlowLayout m_flowLayoutCenter = new FlowLayout();
  JTextField m_jTextFieldUser = new JTextField();
  JLabel m_jLabelAliasDriver = new JLabel();
  JLabel m_jLabelAliasName = new JLabel();
  JLabel m_jLabelAliasUrl = new JLabel();
  JLabel m_jLabelPwd = new JLabel();
  JLabel m_jLabelUser = new JLabel();
  JLabel m_jLabelUrl = new JLabel();
  JLabel m_jLabelDriver = new JLabel();
  JLabel m_jLabelName = new JLabel();
  JPasswordField m_jPasswordFieldPwd = new JPasswordField();

  public ConnectDialog( Component frame, String title, boolean modal, DBAlias dba)
  {
    super( (Frame)null, title, modal, DbTtvDesktop.DBTTVDT);
    setFrame( frame);
    m_dba = dba;
    try
    {
      jbInit();
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }

    int iMaxWidth = 0;
    int iWidth = 0;
    if( m_dba != null)
    {
      if( m_dba.getAliasName() != null)
      {
        m_jLabelAliasName.setText( m_dba.getAliasName());
        iWidth = m_jLabelAliasName.getWidth();
        if( iWidth > iMaxWidth)
          iMaxWidth = iWidth;
      }
      if( m_dba.getDBDriver() != null)
      {
        m_jLabelAliasDriver.setText( m_dba.getDBDriver());
        iWidth = m_jLabelAliasDriver.getWidth();
        if( iWidth > iMaxWidth)
          iMaxWidth = iWidth;
      }
      if( m_dba.getDBUrl() != null || !m_dba.getDBUrl().equals( ""))
      {
        m_jLabelAliasUrl.setText( m_dba.getDBUrl());
        iWidth = m_jLabelAliasUrl.getWidth();
        if( iWidth > iMaxWidth)
          iMaxWidth = iWidth;
      }
      if( m_dba.getDBUser() != null)
      {
        m_jTextFieldUser.setText( m_dba.getDBUser());
        iWidth = m_jLabelAliasUrl.getWidth();
        if( iWidth > iMaxWidth)
          iMaxWidth = iWidth;
      }
    }
    iWidth = m_jLabelDriver.getWidth();
    iMaxWidth += iWidth + 2*m_verticalFlowLayoutLabels.getHgap() + 20;
    m_jPanelCenter.setPreferredSize( new Dimension( iMaxWidth, m_jPanelCenter.getHeight()));
    pack();
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);

    getRootPane().setDefaultButton( m_jButtonOk);
    // set focus to password field
    addWindowListener( new WindowAdapter()
    {
      public void windowOpened( WindowEvent e )
      {
        SwingUtilities.invokeLater( new Runnable()
        {
          public void run()
          {
            m_jPasswordFieldPwd.requestFocus();
          }
        });
      }
    });
  }

  public ConnectDialog( Component frame, DBAlias dba)
  {
    this( frame, "Connect to DB", true, dba);
  }

  /*
  public ConnectDialog()
  {
    this( null, null, null);
  }
  */

  void jbInit() throws Exception
  {
    m_panelBase.setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jPanelCenter.setLayout(m_flowLayoutCenter);
    m_jPanelLabels.setLayout(m_verticalFlowLayoutLabels);
    m_jPanelFields.setLayout(m_verticalFlowLayoutFields);
    m_flowLayoutCenter.setAlignment(FlowLayout.LEFT);
    m_flowLayoutCenter.setHgap(0);
    m_flowLayoutCenter.setVgap(0);
    m_jTextFieldUser.setPreferredSize(new Dimension(300, 21));
    m_jTextFieldUser.setMinimumSize(new Dimension(300, 21));
    m_jLabelAliasDriver.setText("driver");
    m_jLabelAliasName.setText("name");
    m_jLabelAliasUrl.setText("url");
    m_jLabelPwd.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelPwd.setText("DB pwd:");
    m_jLabelUser.setText("DB user:");
    m_jLabelUser.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelUrl.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelUrl.setText("DB URL:");
    m_jLabelDriver.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelDriver.setText("Driver name:");
    m_jLabelName.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelName.setText("Alias name:");
    getContentPane().add(m_panelBase);
    m_panelBase.add(m_jPanelCenter, BorderLayout.CENTER);
    m_jPanelCenter.add(m_jPanelLabels, null);
    m_jPanelLabels.add(m_jLabelName, null);
    m_jPanelLabels.add(m_jLabelDriver, null);
    m_jPanelLabels.add(m_jLabelUrl, null);
    m_jPanelLabels.add(m_jLabelUser, null);
    m_jPanelLabels.add(m_jLabelPwd, null);
    m_jPanelCenter.add(m_jPanelFields, null);
    m_jPanelFields.add(m_jLabelAliasName, null);
    m_jPanelFields.add(m_jLabelAliasDriver, null);
    m_jPanelFields.add(m_jLabelAliasUrl, null);
    m_jPanelFields.add(m_jTextFieldUser, null);
    m_jPanelFields.add(m_jPasswordFieldPwd, null);
    this.getContentPane().add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonOk, null);
    m_jPanelButton.add(m_jButtonCancel, null);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        onOk();
      else if (cmd == "Cancel")
      {
        m_blCancelled = true;
        setVisible( false);
      }
    }
  }

  void onOk()
  {
    m_strUsr = m_jTextFieldUser.getText();
    if( m_jPasswordFieldPwd.getPassword() != null)
      m_strPwd = new String( m_jPasswordFieldPwd.getPassword());
    setVisible( false);
  }

  public boolean isCancelled()
  {
    return m_blCancelled;
  }

  public DBAlias getAlias()
  {
    return m_dba;
  }

  public String getUser()
  {
    return m_strUsr;
  }

  public String getPassword()
  {
    return m_strPwd;
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_blCancelled = false;
      int iMaxWidth = 0;
      int iWidth = 0;
      iWidth = m_jLabelAliasName.getWidth();
      if( iWidth > iMaxWidth)
        iMaxWidth = iWidth;
      iWidth = m_jLabelAliasDriver.getWidth();
      if( iWidth > iMaxWidth)
        iMaxWidth = iWidth;
      iWidth = m_jLabelAliasUrl.getWidth();
      if( iWidth > iMaxWidth)
        iMaxWidth = iWidth;
      iWidth = m_jLabelAliasUrl.getWidth();
      if( iWidth > iMaxWidth)
        iMaxWidth = iWidth;
      iWidth = m_jLabelDriver.getWidth();
      iMaxWidth += iWidth + 2*m_verticalFlowLayoutLabels.getHgap() + 20;
      m_jPanelCenter.setPreferredSize( new Dimension( iMaxWidth, m_jPanelCenter.getHeight()));
      pack();
    }
    else
    {
    }
    super.setVisible( b);
  }
}
