package haui.app.dbtreetableview;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import haui.util.AppProperties;
import haui.components.JExDialog;
import haui.components.VerticalFlowLayout;

/**
 *		Module:					DBDriverDialog.java<br>
 *										$Source: $
 *<p>
 *		Description:    Dialog to config db driver.<br>
 *</p><p>
 *		Created:				20.09.2002	by	AE
 *</p><p>
 *		@history				20.09.2002	by	AE: Created.<br>
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
public class DBDriverDialog
  extends JExDialog
{
  // member variables
  DBDriver m_dbd;
  boolean m_blCancelled = false;

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
  JLabel m_jLabelName = new JLabel();
  JTextField m_jTextFieldName = new JTextField();
  FlowLayout m_flowLayoutCenter = new FlowLayout();
  JLabel m_jLabelClass = new JLabel();
  JTextField m_jTextFieldClass = new JTextField();
  JTextField m_jTextFieldExample = new JTextField();
  JLabel m_jLabelExample = new JLabel();

  public DBDriverDialog( Component frame, String title, boolean modal, DBDriver dbd)
  {
    super( frame, title, modal, DbTtvDesktop.DBTTVDT);
    m_dbd = dbd;
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

    if( m_dbd != null)
    {
      if( m_dbd.getDriverName() != null)
        m_jTextFieldName.setText( m_dbd.getDriverName());
      if( m_dbd.getDriverClass() != null)
        m_jTextFieldClass.setText( m_dbd.getDriverClass());
      if( m_dbd.getDriverExample() != null)
        m_jTextFieldExample.setText( m_dbd.getDriverExample());
    }
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  public DBDriverDialog( Component frame, DBDriver dbd)
  {
    this( frame, "DB driver configuration", true, dbd);
  }

  public DBDriverDialog()
  {
    this( null, null);
  }

  void jbInit() throws Exception
  {
    m_panelBase.setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jPanelCenter.setLayout(m_flowLayoutCenter);
    m_jPanelLabels.setLayout(m_verticalFlowLayoutLabels);
    m_jPanelFields.setLayout(m_verticalFlowLayoutFields);
    m_jLabelName.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelName.setText("Driver name:");
    m_flowLayoutCenter.setAlignment(FlowLayout.LEFT);
    m_flowLayoutCenter.setHgap(0);
    m_flowLayoutCenter.setVgap(0);
    m_jTextFieldName.setMinimumSize(new Dimension(300, 21));
    m_jTextFieldName.setPreferredSize(new Dimension(300, 21));
    m_jLabelClass.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelClass.setText("Driver class:");
    m_jTextFieldClass.setPreferredSize(new Dimension(300, 21));
    m_jTextFieldClass.setMinimumSize(new Dimension(300, 21));
    m_jTextFieldExample.setMinimumSize(new Dimension(300, 21));
    m_jTextFieldExample.setPreferredSize(new Dimension(300, 21));
    m_jLabelExample.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelExample.setText("Example URL:");
    m_verticalFlowLayoutLabels.setVgap(8);
    getContentPane().add(m_panelBase);
    m_panelBase.add(m_jPanelCenter, BorderLayout.CENTER);
    m_jPanelCenter.add(m_jPanelLabels, null);
    m_jPanelLabels.add(m_jLabelName, null);
    m_jPanelLabels.add(m_jLabelClass, null);
    m_jPanelLabels.add(m_jLabelExample, null);
    m_jPanelCenter.add(m_jPanelFields, null);
    m_jPanelFields.add(m_jTextFieldName, null);
    m_jPanelFields.add(m_jTextFieldClass, null);
    m_jPanelFields.add(m_jTextFieldExample, null);
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
    if( m_dbd == null)
      m_dbd = new DBDriver();
    String str = m_jTextFieldName.getText();
    if( str != null)
      m_dbd.setDriverName( str);
    str = m_jTextFieldClass.getText();
    if( str != null)
      m_dbd.setDriverClass( str);
    str = m_jTextFieldExample.getText();
    if( str != null)
      m_dbd.setDriverExample( str);
    if( m_dbd.getDriverName() == null)
      m_dbd = null;
    setVisible( false);
  }

  public boolean isCancelled()
  {
    return m_blCancelled;
  }

  public DBDriver getDriver()
  {
    return m_dbd;
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_blCancelled = false;
    }
    else
    {
    }
    super.setVisible( b);
  }
}
