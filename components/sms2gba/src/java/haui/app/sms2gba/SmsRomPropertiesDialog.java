package haui.app.sms2gba;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import haui.components.*;
import haui.util.*;

/**
 * Module:      SmsRomPropertiesDialog.java<br>
 *              $Source: $
 *<p>
 * Description: sms rom properties.<br>
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     04.06.2003
 *</p><p>
 * Modification:<br>
 * $Log: $
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class SmsRomPropertiesDialog
  extends JExDialog
{
  // constants

  // member variables
  Sms2Gba.SmsRom m_smsRom;

  // GUI member variables
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JLabel m_jLabelLeft = new JLabel();
  JLabel m_jLabelRight = new JLabel();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  ButtonGroup m_buttonGroupOpen = new ButtonGroup();
  JLabel m_jLabelSmsProperties = new JLabel();
  JPanel m_jPanelSmsProperties = new JPanel();
  JLabel m_jLabelName = new JLabel();
  VerticalFlowLayout m_verticalFlowLayoutSmsProperties = new VerticalFlowLayout();
  JTextField m_jTextFieldName = new JTextField();
  JPanel m_jPanelName = new JPanel();
  BorderLayout m_borderLayoutName = new BorderLayout();
  JLabel m_jLabelSize = new JLabel();
  JPanel m_jPanelSize = new JPanel();
  BorderLayout m_borderLayoutSize = new BorderLayout();
  JLabel m_jLabelSizeValue = new JLabel();
  JLabel m_jLabelPath = new JLabel();
  JPanel m_jPanelPath = new JPanel();
  JTextField m_jTextFieldPath = new JTextField();
  BorderLayout m_borderLayoutPath = new BorderLayout();

  public SmsRomPropertiesDialog(Frame frame, String title, boolean modal, Sms2Gba.SmsRom smsRom)
  {
    super(frame, title, modal, Sms2Gba.APPTITLE);
    m_smsRom = smsRom;
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
    _init();
    setSize( 350, getHeight());
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jLabelLeft.setText(" ");
    m_jLabelRight.setText(" ");
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jLabelSmsProperties.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelSmsProperties.setText("SMS rom properties");
    m_jPanelSmsProperties.setLayout(m_verticalFlowLayoutSmsProperties);
    m_jPanelSmsProperties.setBorder(BorderFactory.createEtchedBorder());
    m_jLabelName.setText("Name:");
    m_jTextFieldName.setMinimumSize(new Dimension(70, 21));
    m_jTextFieldName.setPreferredSize(new Dimension(70, 21));
    m_jPanelName.setLayout(m_borderLayoutName);
    m_jLabelSize.setText("Size:  ");
    m_jPanelSize.setLayout(m_borderLayoutSize);
    m_jLabelSizeValue.setText("0");
    m_jLabelPath.setText("Path:  ");
    m_jPanelPath.setLayout(m_borderLayoutPath);
    m_jTextFieldPath.setBackground(Color.lightGray);
    m_jTextFieldPath.setEnabled(true);
    m_jTextFieldPath.setMinimumSize(new Dimension(70, 21));
    m_jTextFieldPath.setPreferredSize(new Dimension(70, 21));
    m_jTextFieldPath.setEditable(false);
    m_jPanelSize.add(m_jLabelSize, BorderLayout.WEST);
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelBase.add(m_jLabelLeft, BorderLayout.WEST);
    m_jPanelBase.add(m_jLabelRight, BorderLayout.EAST);
    m_jPanelBase.add(m_jPanelSmsProperties,  BorderLayout.CENTER);
    m_jPanelPath.add(m_jLabelPath, BorderLayout.WEST);
    m_jPanelPath.add(m_jTextFieldPath, BorderLayout.CENTER);
    m_jPanelSmsProperties.add(m_jLabelSmsProperties, null);
    m_jPanelSmsProperties.add(m_jPanelName, null);
    m_jPanelSmsProperties.add(m_jPanelPath, null);
    m_jPanelSmsProperties.add(m_jPanelSize, null);
    m_jPanelName.add(m_jLabelName, BorderLayout.WEST);
    m_jPanelName.add(m_jTextFieldName, BorderLayout.CENTER);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelSize.add(m_jLabelSizeValue,  BorderLayout.CENTER);
  }

  public void _init()
  {
    if( m_smsRom != null)
    {
      m_jTextFieldName.setText( m_smsRom.getSmsName());
      m_jTextFieldPath.setText( m_smsRom.getSmsPath());
      m_jLabelSizeValue.setText( String.valueOf( m_smsRom.size()));
    }
  }

  public void _save()
  {
    String str = m_jTextFieldName.getText();
    if( str != null && !str.equals( ""))
    {
      m_smsRom.setSmsName( str);
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

  void onOk()
  {
    _save();
    setVisible( false);
  }
}
