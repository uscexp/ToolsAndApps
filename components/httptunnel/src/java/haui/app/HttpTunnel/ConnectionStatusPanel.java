package haui.app.HttpTunnel;

import haui.resource.ResouceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;

/**
 *		Module:					ConnectionStatusPanel.java<br>
 *										$Source: $
 *<p>
 *		Description:    JPanel to config ConnectionMap(s).<br>
 *</p><p>
 *		Created:				23.04.2002	by	AE
 *</p><p>
 *		@history				23.04.2002	by	AE: Created.<br>
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
public class ConnectionStatusPanel
  extends JPanel
{
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JLabel m_jLabelInfo = new JLabel();
  JPanel m_jPanelStatus = new JPanel();
  BorderLayout m_borderLayoutStatus = new BorderLayout();
  JLabel m_jLabelStatus = new JLabel();
  JLabel m_jLabelBytes = new JLabel();
  JButton m_jButtonKill = new JButton();
  ServerConnection m_sc;
  boolean m_blKilled = false;

  public ConnectionStatusPanel()
  {
    this( null);
  }

  public ConnectionStatusPanel( ServerConnection sc)
  {
    m_sc = sc;
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    LisAction actionlistener = new LisAction();
    m_jButtonKill.addActionListener( actionlistener);
  }

  void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jLabelInfo.setText(" Info: connection info");
    m_jPanelStatus.setLayout(m_borderLayoutStatus);
    m_jLabelStatus.setText("Status: status");
    m_jLabelBytes.setText(" Bytes: 0");
    m_jButtonKill.setMaximumSize(new Dimension(16, 16));
    m_jButtonKill.setMinimumSize(new Dimension(16, 16));
    m_jButtonKill.setPreferredSize(new Dimension(16, 16));
    m_jButtonKill.setActionCommand("kill");
    m_jButtonKill.setIcon( ResouceManager.getCommonImageIcon( HttpTunnelClientServer.APPNAME, "bulletgreen.gif"));
    this.add(m_jLabelInfo, BorderLayout.EAST);
    this.add(m_jPanelStatus, BorderLayout.CENTER);
    m_jPanelStatus.add(m_jLabelStatus, BorderLayout.WEST);
    m_jPanelStatus.add(m_jLabelBytes, BorderLayout.CENTER);
    this.add(m_jButtonKill, BorderLayout.WEST);
  }

  public void update( ServerConnection sc)
  {
    m_sc = sc;
    m_jLabelInfo.setText( (new String( " Info: ")) + m_sc.getConnectionMap().toString());
    m_jLabelStatus.setText( (new String( "Status: ")) + m_sc.getStatus());
    //System.out.println( m_sc.getTotalBytesReceived());
    m_jLabelBytes.setText( (new String( " Bytes: ")) + String.valueOf( m_sc.getTotalBytesReceived()));
  }

  public boolean isKilled()
  {
    return m_blKilled;
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "kill")
        kill_actionPerformed(event);
    }
  }

  /**
   * kill menu handler
   */
  void kill_actionPerformed(ActionEvent e)
  {
    if( m_sc != null)
      m_sc.stopIt();
      //m_sc.interrupt();
    m_jButtonKill.setIcon( ResouceManager.getCommonImageIcon( HttpTunnelClientServer.APPNAME, "bulletred.gif"));
    m_blKilled = true;
  }
}
