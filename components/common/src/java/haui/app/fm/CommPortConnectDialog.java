package haui.app.fm;

import haui.components.JExDialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Module:      CommPortConnectDialog.java<br>
 *              $Source: $
 *<p>
 * Description: CommPortConnectDialog.<br>
 *</p><p>
 * Created:     17.06.2004 by AE
 *</p><p>
 * @history	    17.06.2004 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class CommPortConnectDialog
  extends JExDialog
{
  private static final long serialVersionUID = 4501037594454650754L;
  
  // member variables
  protected boolean m_blCanceled = false;
  CommPortIdentifier m_portId;
  int m_iBaud = 9600;
  int m_iMode = ParallelPort.LPT_MODE_ECP;
  Vector m_vecPortList = new Vector();

  // GUI member variables
  protected BorderLayout m_borderLayoutBase = new BorderLayout();
  protected JPanel m_jPanelButtons = new JPanel();
  protected FlowLayout m_flowLayoutButtons = new FlowLayout();
  protected JButton m_jButtonOk = new JButton();
  protected JButton m_jButtonCancel = new JButton();
  JComboBox m_jComboBoxCommPort = new JComboBox();
  JLabel m_jLabelPort = new JLabel();
  JPanel m_jPanelControls = new JPanel();
  FlowLayout m_flowLayoutControls = new FlowLayout();
  JLabel m_jLabelBaud = new JLabel();
  JPanel m_jPanelSerial = new JPanel();
  JComboBox m_jComboBoxBaud = new JComboBox();
  FlowLayout m_flowLayoutSerial = new FlowLayout();
  JComboBox m_jComboBoxMode = new JComboBox();
  JPanel m_jPanelParallel = new JPanel();
  FlowLayout m_flowLayoutParallel = new FlowLayout();
  JLabel m_jLabelMode = new JLabel();

  public CommPortConnectDialog( Dialog dlg, String title, boolean modal)
  {
    super( dlg, title, modal, FileManager.APPNAME);
    constructor();
  }

  public CommPortConnectDialog( Frame frame, String title, boolean modal)
  {
    super( frame, title, modal, FileManager.APPNAME);
    constructor();
  }

  private void constructor()
  {
    Enumeration portList;
    try
    {
      jbInit();
      portList = CommPortIdentifier.getPortIdentifiers();

      while( portList.hasMoreElements() )
      {
        m_portId = ( CommPortIdentifier )portList.nextElement();
        m_vecPortList.add( m_portId);
        m_jComboBoxCommPort.addItem( m_portId.getName());
        m_jComboBoxCommPort.setSelectedIndex( 0);
      }
      Integer iBaud = new Integer( 153600);
      m_jComboBoxBaud.addItem( iBaud);
      iBaud = new Integer( 76800);
      m_jComboBoxBaud.addItem( iBaud);
      iBaud = new Integer( 38400);
      m_jComboBoxBaud.addItem( iBaud);
      iBaud = new Integer( 19200);
      m_jComboBoxBaud.addItem( iBaud);
      iBaud = new Integer( 9600);
      m_jComboBoxBaud.addItem( iBaud);

      String strMode = "ECP";
      m_jComboBoxMode.addItem( strMode);
      strMode = "EPP";
      m_jComboBoxMode.addItem( strMode);
      strMode = "NIBBLE";
      m_jComboBoxMode.addItem( strMode);
      strMode = "Byte";
      m_jComboBoxMode.addItem( strMode);
      strMode = "Compatibility";
      m_jComboBoxMode.addItem( strMode);

      LisItem itemlistener = new LisItem();
      m_jComboBoxCommPort.addItemListener( itemlistener);
      m_jComboBoxBaud.addItemListener( itemlistener);
      m_jComboBoxMode.addItemListener( itemlistener);

      setPanelVisible();
      pack();
      m_jButtonOk.requestDefaultFocus();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  private void jbInit() throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutBase);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jLabelPort.setText("CommPort:");
    m_jPanelControls.setLayout(m_flowLayoutControls);
    m_jLabelBaud.setText("BAUD:");
    m_jPanelSerial.setLayout(m_flowLayoutSerial);
    m_jPanelSerial.setVisible(false);
    m_jPanelParallel.setLayout(m_flowLayoutParallel);
    m_jPanelParallel.setVisible(false);
    m_jLabelMode.setText("Mode:");
    this.getContentPane().add(m_jPanelButtons,  BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelSerial.add(m_jLabelBaud, null);
    m_jPanelSerial.add(m_jComboBoxBaud, null);
    m_jPanelParallel.add(m_jLabelMode, null);
    m_jPanelParallel.add(m_jComboBoxMode, null);
    m_jPanelControls.add(m_jLabelPort, null);
    m_jPanelControls.add(m_jComboBoxCommPort, null);
    m_jPanelControls.add(m_jPanelParallel, null);
    m_jPanelControls.add(m_jPanelSerial, null);
    m_jPanelParallel.setVisible( false);
    m_jPanelSerial.setVisible( false);
    this.getContentPane().add(m_jPanelControls,  BorderLayout.CENTER);
  }

  public CommPortIdentifier getCommPortId()
  {
    CommPortIdentifier portId = null;

    int iIdx = m_jComboBoxCommPort.getSelectedIndex();
    if( iIdx != -1)
      portId = (CommPortIdentifier)m_vecPortList.elementAt( iIdx);
    return portId;
  }

  public int getMode()
  {
    return m_iMode;
  }

  public int getBaud()
  {
    return m_iBaud;
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_jButtonOk.requestFocus();
    }
    super.setVisible( b);
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if( cmd == "Ok" )
        onOk();
      else if( cmd == "Cancel" )
      {
        m_blCanceled = true;
        setVisible( false );
      }
    }
  }

  void onOk()
  {
    setVisible( false);
  }

  public boolean isCanceled()
  {
    return m_blCanceled;
  }

  private void setPanelVisible()
  {
    CommPortIdentifier portId = null;

    int iIdx = m_jComboBoxCommPort.getSelectedIndex();
    if( iIdx != -1 )
      portId = ( CommPortIdentifier )m_vecPortList.elementAt( iIdx );

    switch( portId.getPortType() )
    {
      case CommPortIdentifier.PORT_SERIAL:
        m_jPanelParallel.setVisible( false );
        m_jPanelSerial.setVisible( true );
        m_jComboBoxBaud.setSelectedIndex( 0 );
        pack();
        break;

      case CommPortIdentifier.PORT_PARALLEL:
        m_jPanelSerial.setVisible( false );
        m_jPanelParallel.setVisible( true );
        m_jComboBoxMode.setSelectedIndex( 0 );
        pack();
        break;

      default:
        throw new IllegalStateException( "Unknown port type: " + m_portId );
    }
  }

  class LisItem
    implements ItemListener
  {
    public void itemStateChanged( ItemEvent event)
    {
      int state = event.getStateChange();
      Object obj = event.getSource();
      if( state == ItemEvent.SELECTED)
      {
        if( obj == m_jComboBoxCommPort )
        {
          setPanelVisible();
        }
        else if( obj == m_jComboBoxBaud)
        {
          m_iBaud = ((Integer)m_jComboBoxBaud.getSelectedItem()).intValue();
        }
        else if( obj == m_jComboBoxMode)
        {
          switch( m_jComboBoxMode.getSelectedIndex())
          {
            case 0:
              m_iMode = ParallelPort.LPT_MODE_ECP;
              break;

            case 1:
              m_iMode = ParallelPort.LPT_MODE_EPP;
              break;

            case 2:
              m_iMode = ParallelPort.LPT_MODE_NIBBLE;
              break;

            case 3:
              m_iMode = ParallelPort.LPT_MODE_PS2;
              break;

            case 4:
              m_iMode = ParallelPort.LPT_MODE_SPP;
              break;
          }
        }
      }
    }
  }
}
