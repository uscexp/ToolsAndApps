package haui.app.dbtreetableview;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import haui.util.AppProperties;
import haui.components.JExDialog;
import haui.components.VerticalFlowLayout;

/**
 *		Module:					DBAliasDialog.java<br>
 *										$Source: $
 *<p>
 *		Description:    Dialog to config db aliases.<br>
 *</p><p>
 *		Created:				23.09.2002	by	AE
 *</p><p>
 *		@history				23.09.2002	by	AE: Created.<br>
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
public class DBAliasDialog
  extends JExDialog
{
  // member variables
  DBAlias m_dba;
  boolean m_blCancelled = false;
  DBDriverFrame m_dbdf;

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
  JLabel m_jLabelDriver = new JLabel();
  JTextField m_jTextFieldUrl = new JTextField();
  JLabel m_jLabelUrl = new JLabel();
  JLabel m_jLabelUser = new JLabel();
  JTextField m_jTextFieldUser = new JTextField();
  JPanel m_jPanelDriver = new JPanel();
  JComboBox m_jComboDriver = new JComboBox();
  FlowLayout m_flowLayoutDriver = new FlowLayout();
  JButton m_jButtonDriverNew = new JButton();

  public DBAliasDialog( Component frame, String title, boolean modal, DBAlias dba, DBDriverFrame dbdf)
  {
    super( frame, title, modal, DbTtvDesktop.DBTTVDT);
    m_dba = dba;
    m_dbdf = dbdf;
    try
    {
      jbInit();
      m_jComboDriver.removeAllItems();
      if( m_dbdf != null)
      {
        int iLen = m_dbdf.listSize();
        for( int i = 0; i < iLen; ++i)
        {
          m_jComboDriver.addItem( ((DBDriver)m_dbdf.elementAt(i)).getDriverName());
        }
      }
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }

    if( m_dba != null)
    {
      if( m_dba.getAliasName() != null)
        m_jTextFieldName.setText( m_dba.getAliasName());
      if( m_dba.getDBDriver() != null && m_dbdf != null)
        m_jComboDriver.setSelectedItem( m_dba.getDBDriver());
      else
      {
        if( m_dbdf != null && m_jComboDriver.getItemCount() > 0)
        {
          m_jComboDriver.setSelectedIndex( 0);
          int iSel = 0;
          DBDriver dbd = (DBDriver)m_dbdf.elementAt( iSel);
          if( dbd.getDriverExample() != null)
            m_jTextFieldUrl.setText( dbd.getDriverExample());
        }
      }
      if( m_dba.getDBUrl() != null || !m_dba.getDBUrl().equals( ""))
        m_jTextFieldUrl.setText( m_dba.getDBUrl());
      if( m_dba.getDBUser() != null)
        m_jTextFieldUser.setText( m_dba.getDBUser());
    }
    else
    {
      if( m_dbdf != null && m_jComboDriver.getItemCount() > 0)
      {
        m_jComboDriver.setSelectedIndex( 0);
        int iSel = 0;
        DBDriver dbd = (DBDriver)m_dbdf.elementAt( iSel);
        if( dbd.getDriverExample() != null)
          m_jTextFieldUrl.setText( dbd.getDriverExample());
      }
    }
    LisItem itemlistener = new LisItem();
    m_jComboDriver.addItemListener( itemlistener);

    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
    m_jButtonDriverNew.addActionListener( actionlis);
  }

  public DBAliasDialog( Component frame, DBAlias dba, DBDriverFrame dbdf)
  {
    this( frame, "DB alias configuration", true, dba, dbdf);
  }

  /*
  public DBAliasDialog()
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
    m_jLabelName.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelName.setText("Alias name:");
    m_flowLayoutCenter.setAlignment(FlowLayout.LEFT);
    m_flowLayoutCenter.setHgap(0);
    m_flowLayoutCenter.setVgap(0);
    m_jTextFieldName.setMinimumSize(new Dimension(300, 21));
    m_jTextFieldName.setPreferredSize(new Dimension(300, 21));
    m_jLabelDriver.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelDriver.setText("Driver name:");
    m_jTextFieldUrl.setPreferredSize(new Dimension(300, 21));
    m_jTextFieldUrl.setMinimumSize(new Dimension(300, 21));
    m_jLabelUrl.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jLabelUrl.setText("DB URL:");
    m_jLabelUser.setText("DB user:");
    m_jLabelUser.setHorizontalAlignment(SwingConstants.RIGHT);
    m_jTextFieldUser.setPreferredSize(new Dimension(300, 21));
    m_jTextFieldUser.setMinimumSize(new Dimension(300, 21));
    m_verticalFlowLayoutLabels.setVgap(8);
    m_jPanelDriver.setLayout(m_flowLayoutDriver);
    m_flowLayoutDriver.setAlignment(FlowLayout.LEFT);
    m_flowLayoutDriver.setHgap(0);
    m_flowLayoutDriver.setVgap(0);
    m_jButtonDriverNew.setActionCommand("newdriver");
    m_jButtonDriverNew.setText("New");
    m_jComboDriver.setMinimumSize(new Dimension(250, 21));
    m_jComboDriver.setPreferredSize(new Dimension(250, 21));
    getContentPane().add(m_panelBase);
    m_panelBase.add(m_jPanelCenter, BorderLayout.CENTER);
    m_jPanelCenter.add(m_jPanelLabels, null);
    m_jPanelLabels.add(m_jLabelName, null);
    m_jPanelLabels.add(m_jLabelDriver, null);
    m_jPanelLabels.add(m_jLabelUrl, null);
    m_jPanelLabels.add(m_jLabelUser, null);
    m_jPanelCenter.add(m_jPanelFields, null);
    m_jPanelFields.add(m_jTextFieldName, null);
    m_jPanelFields.add(m_jPanelDriver, null);
    m_jPanelDriver.add(m_jComboDriver, null);
    m_jPanelDriver.add(m_jButtonDriverNew, null);
    m_jPanelFields.add(m_jTextFieldUrl, null);
    m_jPanelFields.add(m_jTextFieldUser, null);
    this.getContentPane().add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonOk, null);
    m_jPanelButton.add(m_jButtonCancel, null);
  }

  class LisItem implements ItemListener
  {
    public void itemStateChanged( ItemEvent event)
    {
      if( m_dbdf != null)
      {
        int iSel = m_jComboDriver.getSelectedIndex();
        DBDriver dbd = (DBDriver)m_dbdf.elementAt( iSel);
        if( dbd.getDriverExample() != null)
          m_jTextFieldUrl.setText( dbd.getDriverExample());
      }
    }
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
      else if (cmd == "newdriver")
      {
        DBDriver dbd = (DBDriver)m_dbdf.onAdd();
        if( dbd != null)
        {
          m_jComboDriver.addItem( dbd.getDriverName());
          m_jComboDriver.setSelectedItem( dbd.getDriverName());
          if( dbd.getDriverExample() != null)
            m_jTextFieldUrl.setText( dbd.getDriverExample());
        }
      }
    }
  }

  void onOk()
  {
    if( m_dba == null)
      m_dba = new DBAlias();
    String str = m_jTextFieldName.getText();
    if( str != null)
      m_dba.setAliasName( str);
    str = (String)m_jComboDriver.getSelectedItem();
    if( str != null)
      m_dba.setDBDriver( str);
    if( m_dbdf != null && m_jComboDriver.getItemCount() > 0)
    {
      DBDriver dbd = m_dbdf.findDBDriver( str);
      if( dbd != null && dbd.getDriverClass() != null)
        m_dba.setDBDriverClass( dbd.getDriverClass());
    }
    str = m_jTextFieldUrl.getText();
    if( str != null)
      m_dba.setDBUrl( str);
    str = m_jTextFieldUser.getText();
    if( str != null)
      m_dba.setDBUser( str);
    if( m_dba.getAliasName() == null)
      m_dba = null;
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
