package haui.app.HttpTunnel;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

/**
 *		Module:					ConnectionMapPanel.java<br>
 *										$Source: $
 *<p>
 *		Description:    JPanel to config ConnectionMap(s).<br>
 *</p><p>
 *		Created:				06.12.2001	by	AE
 *</p><p>
 *		@history				06.12.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2001; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ConnectionMapPanel
  extends JPanel
{
  ConnectionMap m_cm;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelFieldLabels = new JPanel();
  GridLayout m_gridLayoutFieldLabels = new GridLayout();
  JLabel m_jLabelLocalPort = new JLabel();
  JLabel m_jLabelRemoteAddr = new JLabel();
  JPanel m_jPanelCenter = new JPanel();
  BorderLayout m_borderLayoutCenter = new BorderLayout();
  JPanel m_jPanelFields = new JPanel();
  GridLayout m_gridLayoutFields = new GridLayout();
  JTextField m_jTextFieldLocalPort = new JTextField();
  JTextField m_jTextFieldRemoteAddr = new JTextField();
  JLabel m_jLabelLocalAddr = new JLabel();
  JTextField m_jTextFieldLocalAddr = new JTextField();
  JLabel m_jLabelTitle = new JLabel();
  TitledBorder titledBorder1;
  JTextField m_jTextFieldRemotePort = new JTextField();
  JLabel m_jLabelRemotePort = new JLabel();
  JLabel m_jLabelPoll = new JLabel();
  JLabel m_jLabelComp = new JLabel();
  JLabel m_jLabelCrypt = new JLabel();
  JCheckBox m_jCheckBoxPoll = new JCheckBox();
  JCheckBox m_jCheckBoxComp = new JCheckBox();
  JCheckBox m_jCheckBoxCrypt = new JCheckBox();

  public ConnectionMapPanel()
  {
    this( null);
  }

  public ConnectionMapPanel( ConnectionMap cm)
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    if( cm != null)
      m_cm = (ConnectionMap)cm.clone();
  }

  void jbInit() throws Exception
  {
    titledBorder1 = new TitledBorder("");
    this.setLayout(m_borderLayoutBase);
    m_jPanelFieldLabels.setLayout(m_gridLayoutFieldLabels);
    m_gridLayoutFieldLabels.setColumns(1);
    m_gridLayoutFieldLabels.setRows(7);
    m_jLabelLocalPort.setText("Local port:");
    m_jLabelRemoteAddr.setText("Remote addr.:");
    m_jPanelCenter.setLayout(m_borderLayoutCenter);
    m_jPanelFields.setLayout(m_gridLayoutFields);
    m_gridLayoutFields.setColumns(1);
    m_gridLayoutFields.setRows(7);
    m_jLabelLocalAddr.setText("Local addr.:");
    m_jLabelTitle.setText("Connection map settings:");
    m_jLabelTitle.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelRemotePort.setText("Remote port:");
    m_jLabelPoll.setText("Poll");
    m_jLabelComp.setText("Compress");
    m_jLabelCrypt.setText("Encrypt");
    this.add(m_jPanelCenter, BorderLayout.CENTER);
    m_jPanelCenter.add(m_jPanelFields, BorderLayout.CENTER);
    m_jPanelFields.add(m_jTextFieldLocalAddr, null);
    m_jPanelFields.add(m_jTextFieldLocalPort, null);
    m_jPanelFields.add(m_jTextFieldRemoteAddr, null);
    m_jPanelFields.add(m_jTextFieldRemotePort, null);
    m_jPanelFields.add(m_jCheckBoxPoll, null);
    m_jPanelFields.add(m_jCheckBoxComp, null);
    m_jPanelFields.add(m_jCheckBoxCrypt, null);
    m_jPanelCenter.add(m_jPanelFieldLabels, BorderLayout.WEST);
    m_jPanelFieldLabels.add(m_jLabelLocalAddr, null);
    m_jPanelFieldLabels.add(m_jLabelLocalPort, null);
    m_jPanelFieldLabels.add(m_jLabelRemoteAddr, null);
    m_jPanelFieldLabels.add(m_jLabelRemotePort, null);
    m_jPanelFieldLabels.add(m_jLabelPoll, null);
    m_jPanelFieldLabels.add(m_jLabelComp, null);
    m_jPanelFieldLabels.add(m_jLabelCrypt, null);
    this.add(m_jLabelTitle, BorderLayout.NORTH);
  }

  public void setConnectionMap( ConnectionMap cm)
  {
    if( cm != null)
      m_cm = (ConnectionMap)cm.clone();
  }

  public ConnectionMap getConnectionMap()
  {
    return m_cm;
  }

  public void _init()
  {
    if( m_cm == null)
      return;
    m_jTextFieldLocalAddr.setText( m_cm.getLocalAddr());
    m_jTextFieldLocalPort.setText( String.valueOf( m_cm.getLocalPort()));
    m_jTextFieldRemoteAddr.setText( m_cm.getRemoteAddr());
    m_jTextFieldRemotePort.setText( String.valueOf( m_cm.getRemotePort()));
    m_jCheckBoxPoll.setSelected( m_cm.mustPoll());
    m_jCheckBoxComp.setSelected( m_cm.sendCompressed());
    m_jCheckBoxCrypt.setSelected( m_cm.isCrypted());
  }

  public void _save()
  {
    if( m_jTextFieldLocalAddr.getText() == null || m_jTextFieldLocalAddr.getText().equals(""))
    {
      m_cm = null;
      return;
    }
    if( m_cm == null)
      m_cm = new ConnectionMap();
    m_cm.setLocalAddr( m_jTextFieldLocalAddr.getText());
    m_cm.setLocalPort( Integer.parseInt( m_jTextFieldLocalPort.getText()));
    m_cm.setRemoteAddr( m_jTextFieldRemoteAddr.getText());
    m_cm.setRemotePort( Integer.parseInt( m_jTextFieldRemotePort.getText()));
    m_cm.setPoll( m_jCheckBoxPoll.isSelected());
    m_cm.setCompressed( m_jCheckBoxComp.isSelected());
    m_cm.setCrypted( m_jCheckBoxCrypt.isSelected());
  }

  public void requestFocus()
  {
    m_jTextFieldLocalAddr.requestFocus();
  }
}