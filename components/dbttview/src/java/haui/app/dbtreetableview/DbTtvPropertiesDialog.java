package haui.app.dbtreetableview;

import haui.components.CGIAuthorizationPanel;
import haui.components.JExDialog;
import haui.components.ProxyAuthorizationPanel;
import haui.components.URLAuthorizationPanel;
import haui.components.VerticalFlowLayout;
import haui.util.AppProperties;
import haui.util.GlobalAppProperties;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 *
 *		Module:					DbTtvPropertiesDialog.java<br>
 *										$Source: $
 *<p>
 *		Description:    DbTreeTableView properties dialog.<br>
 *</p><p>
 *		Created:				26.09.2002	by	AE
 *</p><p>
 *		@history				26.09.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DbTtvPropertiesDialog
  extends JExDialog
{
  // constants
  public final static int URLAUTH = 1;
  public final static int PROXYAUTH = 2;
  public final static int CGIAUTH = 3;

  // member variables
  AppProperties m_appProps;
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  URLAuthorizationPanel m_jPanelURLAuthorization = new URLAuthorizationPanel();
  ProxyAuthorizationPanel m_jPanelProxyAuthorization = new ProxyAuthorizationPanel();
  CGIAuthorizationPanel m_jPanelCGIAuthorization = new CGIAuthorizationPanel();
  JLabel m_jLabelLeft = new JLabel();
  JLabel m_jLabelRight = new JLabel();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  JTabbedPane m_jTabbedPaneCenter = new JTabbedPane();
  JPanel m_jPanelDbTtv = new JPanel();
  ButtonGroup m_buttonGroupOpen = new ButtonGroup();
  JPanel jPanelGeneral = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutGeneral = new VerticalFlowLayout();
  JRadioButton m_jRadioButtonOpenMax = new JRadioButton();
  JRadioButton m_jRadioButtonOpenMaxToOp = new JRadioButton();
  JPanel m_jPanelOpen = new JPanel();
  JRadioButton m_jRadioButtonOpenCascade = new JRadioButton();
  VerticalFlowLayout m_verticalFlowLayoutOpen = new VerticalFlowLayout();
  JLabel m_jLabelOpen = new JLabel();
  VerticalFlowLayout m_verticalFlowLayoutDbTtv = new VerticalFlowLayout();
  JPanel m_jPanelSession = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutSession = new VerticalFlowLayout();
  JCheckBox m_jCheckBoxAutocommit = new JCheckBox();
  JPanel m_jPanelLimit = new JPanel();
  FlowLayout m_flowLayoutLimit = new FlowLayout();
  JLabel m_jLabelLimit = new JLabel();
  JTextField m_jTextFieldLimit = new JTextField();
  JCheckBox m_jCheckBoxFixColumn = new JCheckBox();
  JCheckBox m_jCheckBoxDebug = new JCheckBox();
  JLabel m_jLabelSession = new JLabel();

  public DbTtvPropertiesDialog(Frame frame, String title, boolean modal, AppProperties appProps)
  {
    super(frame, title, modal, DbTtvDesktop.DBTTVDT);
    m_appProps = appProps;
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
    m_buttonGroupOpen.add( m_jRadioButtonOpenCascade);
    m_buttonGroupOpen.add( m_jRadioButtonOpenMaxToOp);
    m_buttonGroupOpen.add( m_jRadioButtonOpenMax);
    _init();
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  public DbTtvPropertiesDialog()
  {
    this(null, "", false, null);
  }

  void jbInit() throws Exception
  {
    m_jRadioButtonOpenCascade.setText("Cascaded.");
    m_jRadioButtonOpenCascade.setActionCommand("opencascade");
    m_jPanelOpen.setLayout(m_verticalFlowLayoutOpen);
    m_jRadioButtonOpenMaxToOp.setText("Maximized to output.");
    m_jRadioButtonOpenMaxToOp.setActionCommand("openmaxtoop");
    m_jRadioButtonOpenMax.setActionCommand("openmax");
    m_jRadioButtonOpenMax.setText("Maximized.");
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jLabelLeft.setText(" ");
    m_jLabelRight.setText(" ");
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    jPanelGeneral.setLayout(m_verticalFlowLayoutGeneral);
    m_jPanelOpen.setBorder(BorderFactory.createEtchedBorder());
    m_jLabelOpen.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelOpen.setText("Open Windows:");
    m_jPanelDbTtv.setLayout(m_verticalFlowLayoutDbTtv);
    m_jPanelSession.setLayout(m_verticalFlowLayoutSession);
    m_jCheckBoxAutocommit.setText("Autocommit");
    m_jPanelLimit.setLayout(m_flowLayoutLimit);
    m_jLabelLimit.setText("Limit rows to ");
    m_jTextFieldLimit.setMinimumSize(new Dimension(70, 21));
    m_jTextFieldLimit.setPreferredSize(new Dimension(70, 21));
    m_flowLayoutLimit.setAlignment(FlowLayout.LEFT);
    m_jCheckBoxFixColumn.setText("Fix columns in table.");
    m_jCheckBoxDebug.setText("Sql debug output");
    m_jPanelSession.setBorder(BorderFactory.createEtchedBorder());
    m_jLabelSession.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelSession.setText("Default properties");
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelBase.add(m_jLabelLeft, BorderLayout.WEST);
    m_jPanelBase.add(m_jLabelRight, BorderLayout.EAST);
    m_jPanelBase.add(m_jTabbedPaneCenter, BorderLayout.CENTER);
    m_jTabbedPaneCenter.add(jPanelGeneral, "General");
    jPanelGeneral.add(m_jPanelOpen, null);
    m_jPanelOpen.add(m_jLabelOpen, null);
    m_jPanelOpen.add(m_jRadioButtonOpenCascade, null);
    m_jPanelOpen.add(m_jRadioButtonOpenMaxToOp, null);
    m_jPanelOpen.add(m_jRadioButtonOpenMax, null);
    m_jTabbedPaneCenter.add(m_jPanelDbTtv, "DB-Session");
    m_jPanelDbTtv.add(m_jPanelSession, null);
    m_jPanelSession.add(m_jLabelSession, null);
    m_jPanelLimit.add(m_jLabelLimit, null);
    m_jPanelLimit.add(m_jTextFieldLimit, null);
    m_jPanelSession.add(m_jCheckBoxAutocommit, null);
    m_jPanelSession.add(m_jPanelLimit, null);
    m_jPanelSession.add(m_jCheckBoxFixColumn, null);
    m_jPanelSession.add(m_jCheckBoxDebug, null);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
  }

  public void _init()
  {
    boolean bl = m_appProps.getBooleanProperty( DbTtvDesktop.WINCASCADED);
    m_jRadioButtonOpenCascade.setSelected( bl);
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.WINMAXTOOP);
    m_jRadioButtonOpenMaxToOp.setSelected( bl);
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.WINMAX);
    m_jRadioButtonOpenMax.setSelected( bl);
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.AUTOCOMMIT);
    m_jCheckBoxAutocommit.setSelected( bl);
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.FIXCOLUMNS);
    m_jCheckBoxFixColumn.setSelected( bl);
    bl = m_appProps.getBooleanProperty( DbTtvDesktop.SQLDEBUG);
    m_jCheckBoxDebug.setSelected( bl);
    Integer iRowLimit = m_appProps.getIntegerProperty( DbTtvDesktop.ROWLIMIT);
    if( iRowLimit != null)
      m_jTextFieldLimit.setText( iRowLimit.toString());
  }

  public void _save()
  {
    boolean bl = m_jRadioButtonOpenCascade.isSelected();
    m_appProps.setBooleanProperty( DbTtvDesktop.WINCASCADED, bl);
    bl = m_jRadioButtonOpenMaxToOp.isSelected();
    m_appProps.setBooleanProperty( DbTtvDesktop.WINMAXTOOP, bl);
    bl = m_jRadioButtonOpenMax.isSelected();
    m_appProps.setBooleanProperty( DbTtvDesktop.WINMAX, bl);
    bl = m_jCheckBoxAutocommit.isSelected();
    m_appProps.setBooleanProperty( DbTtvDesktop.AUTOCOMMIT, bl);
    bl = m_jCheckBoxFixColumn.isSelected();
    m_appProps.setBooleanProperty( DbTtvDesktop.FIXCOLUMNS, bl);
    bl = m_jCheckBoxDebug.isSelected();
    m_appProps.setBooleanProperty( DbTtvDesktop.SQLDEBUG, bl);
    String str = m_jTextFieldLimit.getText();
    int iLim = 0;
    if( str != null)
    {
      try
      {
        iLim = Integer.parseInt( str);
      }
      catch( NumberFormatException nfex)
      {
        iLim = 0;
        GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT).println( "Error: Wrong number in limit row field!");
        //System.err.println( "Error: Wrong number in limit row field!");
      }
    }
    m_appProps.setIntegerProperty( DbTtvDesktop.ROWLIMIT, new Integer( iLim));
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        m_jButtonOk_actionPerformed( event);
      else if (cmd == "Cancel")
        setVisible( false);
    }
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
    }
    super.setVisible( b);
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    _save();
    setVisible( false);
  }
}
