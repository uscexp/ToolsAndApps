package haui.app.HttpTunnel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;
import haui.util.AppProperties;

/**
 *		Module:					ConfigConnectionMapPanel.java<br>
 *										$Source: $
 *<p>
 *		Description:    Dialog to config ConnectionMap file extensions.<br>
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
public class ConfigConnectionMapPanel extends JPanel
{
  Component m_frame = null;
  AppProperties m_appProps;
  Vector m_cm;
  Vector m_listener;
  DefaultListModel m_dlm = new DefaultListModel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  ConnectionMapPanel m_connectionMapPanel = new ConnectionMapPanel();
  BorderLayout m_borderLayoutList = new BorderLayout();
  FlowLayout m_flowLayoutListButton = new FlowLayout();
  JButton m_jButtonDelete = new JButton();
  JPanel m_jPanelList = new JPanel();
  JScrollPane m_jScrollPaneList = new JScrollPane();
  JButton m_jButtonSet = new JButton();
  JPanel m_jPanelListButton = new JPanel();
  JList m_jListConnectionMaps = new JList();
  String m_servletUrl;
  JTextArea m_jTextAreaOut;

  public ConfigConnectionMapPanel( Component frame, Vector cm, Vector listener, String servletUrl, JTextArea ta, AppProperties appProps)
  {
    m_frame = frame;
    m_servletUrl = servletUrl;
    try
    {
      jbInit();
      m_jListConnectionMaps.setModel( m_dlm);
      setConnectionMapVector( cm);
      setListenerVector( listener);
      setOutputTextArea( ta);
      setAppProperties( appProps);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonSet.addActionListener( actionlis);
    m_jButtonDelete.addActionListener( actionlis);

    LisListSelection listenerListSel = new LisListSelection();
    m_jListConnectionMaps.addListSelectionListener( listenerListSel);
  }

  public ConfigConnectionMapPanel()
  {
    this( null, null, null, null, null, null);
  }

  public void setFrame( Component frame)
  {
    m_frame = frame;
  }

  public void setServletUrl( String servletUrl)
  {
    m_servletUrl = servletUrl;
  }

  public void setOutputTextArea( JTextArea ta)
  {
    m_jTextAreaOut = ta;
  }

  public void setAppProperties( AppProperties appProps)
  {
    m_appProps = appProps;
  }

  private void stopListener( int i)
  {
    ServerListener sl = (ServerListener)m_listener.elementAt( i);
    sl.interrupt();
    //JOptionPane.showMessageDialog( this, "Listener '" + sl.getConnectionMap().getLocalAddr() + ":"
        //+ String.valueOf( sl.getConnectionMap().getLocalPort()) + "' stopped", "Info", JOptionPane.INFORMATION_MESSAGE);
    m_jTextAreaOut.append( "Listener '" + sl.getConnectionMap().getLocalAddr() + ":"
        + String.valueOf( sl.getConnectionMap().getLocalPort()) + "' stopped\n");
    //System.out.println( "Listener '" + sl.getConnectionMap().getLocalAddr() + ":"
        //+ String.valueOf( sl.getConnectionMap().getLocalPort()) + "' stopped");
  }

  private void startListener( ConnectionMap cm)
  {
    ServerListener sl = new ServerListener( cm, m_servletUrl, m_jTextAreaOut, m_appProps);
    sl.start();
    //JOptionPane.showMessageDialog( this, "Listener '" + cm.getLocalAddr() + ":" + String.valueOf( cm.getLocalPort()) + "' started",
        //"Info", JOptionPane.INFORMATION_MESSAGE);
    m_jTextAreaOut.append( "Listener '" + cm.getLocalAddr() + ":" + String.valueOf( cm.getLocalPort()) + "' started\n");
    //System.out.println( "Listener '" + cm.getLocalAddr() + ":" + String.valueOf( cm.getLocalPort()) + "' started");
  }

  public void setConnectionMapVector( Vector cm)
  {
    m_cm = cm;

    if( m_cm != null && m_cm.size() > 0)
    {
      m_connectionMapPanel.setConnectionMap( (ConnectionMap)m_cm.elementAt(0));
      m_connectionMapPanel._init();
      for( int i = 0; i < m_cm.size(); i++)
      {
        m_dlm.addElement( (ConnectionMap)m_cm.elementAt(i));
        m_jListConnectionMaps.setSelectedIndex( 0);
      }
    }
  }

  public void setListenerVector( Vector listener)
  {
    m_listener = listener;
  }

  void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jButtonDelete.setText("Delete");
    m_jPanelList.setLayout(m_borderLayoutList);
    m_jButtonSet.setText("Set");
    m_jPanelListButton.setLayout(m_flowLayoutListButton);
    this.setBorder(BorderFactory.createEtchedBorder());
    this.add(m_connectionMapPanel, BorderLayout.SOUTH);
    this.add(m_jPanelList, BorderLayout.CENTER);
    m_jPanelList.add(m_jScrollPaneList, BorderLayout.CENTER);
    m_jPanelList.add(m_jPanelListButton, BorderLayout.SOUTH);
    m_jPanelListButton.add(m_jButtonSet, null);
    m_jPanelListButton.add(m_jButtonDelete, null);
    m_jScrollPaneList.getViewport().add(m_jListConnectionMaps, null);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Set")
        m_jButtonSet_actionPerformed( event);
      else if (cmd == "Delete")
        m_jButtonDelete_actionPerformed( event);
    }
  }

  void m_jButtonSet_actionPerformed(ActionEvent e)
  {
    m_connectionMapPanel._save();
    ConnectionMap cm = m_connectionMapPanel.getConnectionMap();
    if( cm == null)
      return;
    int iIdx = -1;
    if( m_cm.size() > 0)
      iIdx = m_cm.indexOf( cm);
    if( iIdx == -1)
    {
      ConnectionMap lCm;
      for( int i = 0; i < m_cm.size(); ++i)
      {
        lCm = (ConnectionMap)m_cm.elementAt( i);
        if( lCm.getLocalAddr().equalsIgnoreCase( cm.getLocalAddr()) && lCm.getLocalPort() == cm.getLocalPort())
        {
          lCm.setRemoteAddr( cm.getRemoteAddr());
          lCm.setRemotePort( cm.getRemotePort());
          lCm.setPoll( cm.mustPoll());
          lCm.setCompressed( cm.sendCompressed());
          lCm.setCrypted( cm.isCrypted());
          m_dlm.setElementAt( lCm.clone(), i);
          iIdx = i;
          break;
        }
      }
    }
    if( iIdx == -1)
    {
      m_cm.add( cm.clone());
      m_dlm.addElement( cm.clone());
      if( m_dlm.size() > 1)
        m_jListConnectionMaps.setSelectedValue( cm, true);
      else
        m_jListConnectionMaps.setSelectedIndex( 0);
      startListener( cm);
    }
  }

  void m_jButtonDelete_actionPerformed(ActionEvent e)
  {
    ConnectionMap cm = (ConnectionMap)m_jListConnectionMaps.getSelectedValue();
    int i = m_jListConnectionMaps.getSelectedIndex();

    stopListener( i);
    m_jListConnectionMaps.clearSelection();
    m_dlm.removeElement( cm);
    m_cm.remove( cm);
    if( m_dlm.size() > 0)
      m_jListConnectionMaps.setSelectedIndex( 0);
  }

  public Vector getConnectionMap()
  {
    return m_cm;
  }

  class LisListSelection implements ListSelectionListener
  {
    public void valueChanged(ListSelectionEvent event)
    {
      JList object = (JList)event.getSource();

      int iIdx = object.getSelectedIndex();
      m_connectionMapPanel.setConnectionMap( (ConnectionMap)object.getSelectedValue());
      m_connectionMapPanel._init();
    }
  }

  public void requestFocus()
  {
    m_connectionMapPanel.requestFocus();
  }
}
