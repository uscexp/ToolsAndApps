package haui.components;

import haui.io.FileInterface.FileTypeServer;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Module:      CommPortTypeServerDialog.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\CommPortTypeServerDialog.java,v $
 *<p>
 * Description: dialog to log CommPortTypeServer output.<br>
 *</p><p>
 * Created:	    08.06.2004  by AE
 *</p><p>
 * @history     08.06.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: CommPortTypeServerDialog.java,v $
 * Revision 1.1  2004-08-31 16:03:11+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:52+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\CommPortTypeServerDialog.java,v 1.1 2004-08-31 16:03:11+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class CommPortTypeServerDialog
  extends FileTypeServerDialog
{
  private static final long serialVersionUID = -3313734636082454521L;
  
  CommPortIdentifier m_portId;
  int m_iBaud = 9600;
  int m_iMode = ParallelPort.LPT_MODE_ECP;
  Vector m_vecPortList = new Vector();

  JPanel m_jPanelControls = new JPanel();
  FlowLayout m_flowLayoutControls = new FlowLayout();
  JLabel m_jLabelPort = new JLabel();
  JComboBox m_jComboBoxCommPort = new JComboBox();
  JPanel m_jPanelParallel = new JPanel();
  FlowLayout m_flowLayoutParallel = new FlowLayout();
  JPanel m_jPanelSerial = new JPanel();
  FlowLayout m_flowLayoutSerial = new FlowLayout();
  JLabel m_jLabelMode = new JLabel();
  JLabel m_jLabelBaud = new JLabel();
  JComboBox m_jComboBoxMode = new JComboBox();
  JComboBox m_jComboBoxBaud = new JComboBox();

  public CommPortTypeServerDialog(Component frame, String title, boolean modal, FileTypeServer fts, String strAppName)
  {
    super(frame, title, modal, fts, strAppName);
    Enumeration portList;
    try
    {
      jbInit();
      //if( m_fts instanceof CommPortTypeServer)
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
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    m_jPanelControls.setLayout(m_flowLayoutControls);
    m_jLabelPort.setText("CommPort:");
    m_jPanelParallel.setLayout(m_flowLayoutParallel);
    m_jPanelSerial.setLayout(m_flowLayoutSerial);
    m_jLabelMode.setText("Mode:");
    m_jLabelBaud.setText("BAUD:");
    m_jPanelControls.add(m_jLabelPort, null);
    m_jPanelControls.add(m_jComboBoxCommPort, null);
    m_jPanelControls.add(m_jPanelParallel, null);
    m_jPanelControls.add(m_jPanelSerial, null);
    m_jPanelParallel.add(m_jLabelMode, null);
    m_jPanelSerial.add(m_jLabelBaud, null);
    m_jPanelParallel.add(m_jComboBoxMode, null);
    m_jPanelSerial.add(m_jComboBoxBaud, null);
    m_jPanelParallel.setVisible( false);
    m_jPanelSerial.setVisible( false);
    addComponent( m_jPanelControls);
  }

  public void setLabel( String str)
  {
    m_jLabelPort.setText( str);
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