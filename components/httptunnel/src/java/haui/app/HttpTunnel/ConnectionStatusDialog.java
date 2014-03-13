package haui.app.HttpTunnel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;
import haui.util.AppProperties;
import haui.util.GlobalAppProperties;

/**
 *		Module:					ConfigConnectionMapDialog.java<br>
 *										$Source: $
 *<p>
 *		Description:    Dialog to config connection maps.<br>
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
public class ConnectionStatusDialog
  extends JDialog
{
  Component m_frame = null;
  String m_strAppName;
  AppProperties m_appProps;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  String m_servletUrl;
  JScrollPane m_jScrollPaneStatus = new JScrollPane();
  Vector m_vConn;
  StatusThread m_st;
  JPanel m_jPanelCont = new JPanel();
  BorderLayout m_borderLayoutCont = new BorderLayout();
  JPanel m_jPanelStatus = new JPanel();
  GridLayout m_gridLayoutStatus = new GridLayout();
  boolean m_blNew = true;

  public ConnectionStatusDialog(Component frame, String strAppName, String title, boolean modal, Vector vConn)
  {
    super((Frame)null, title, modal);
    m_frame = frame;
    m_strAppName = strAppName;
    m_vConn = vConn;
    try
    {
      jbInit();
      pack();
      this.getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
  }

  public ConnectionStatusDialog( Component frame, String strAppName, Vector vConn)
  {
    this( frame, strAppName, "Connection status", true, vConn);
  }

  /*
  public ConnectionStatusDialog()
  {
    this( null, "Connection status", true, null);
  }
  */

  void jbInit() throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jButtonOk.setText("Ok");
    m_jPanelCont.setLayout(m_borderLayoutCont);
    m_jPanelStatus.setLayout(m_gridLayoutStatus);
    m_gridLayoutStatus.setColumns(1);
    m_jScrollPaneStatus.setPreferredSize(new Dimension(300, 300));
    getContentPane().add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonOk, null);
    this.getContentPane().add(m_jScrollPaneStatus, BorderLayout.CENTER);
    m_jScrollPaneStatus.getViewport().add(m_jPanelCont, null);
    m_jPanelCont.add(m_jPanelStatus, BorderLayout.NORTH);
  }

  public void init( Vector vConn)
  {
    m_vConn = vConn;
    if( m_st != null)
      m_st.stop();
    m_st = new StatusThread();
    m_st.init();
    m_st.start();
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

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    m_st.halt();
    setVisible( false);
  }

  public void setVisible( boolean b)
  {
    if( b && m_blNew)
    {
      m_blNew = false;
      int x = 0;
      int y = 0;
      Dimension screenDim = getToolkit().getScreenSize();
      int maxX = screenDim.width;
      int maxY = screenDim.height;

      Component root = null;
      if( m_strAppName != null && !m_strAppName.equals( ""))
        root = GlobalAppProperties.instance().getRootComponent( m_strAppName);

      if( m_frame != null)
      {
        Point rootloc;
        if( root != null)
          rootloc = root.getLocation();
        else
          rootloc = getRootPane().getLocation();
        Point parloc = m_frame.getLocation();
        x = rootloc.x + parloc.x + m_frame.getWidth()/2 - getWidth()/2;
        y = rootloc.y + m_frame.getHeight()/2 - getHeight()/2;
      }
      else
      {
        Point rootloc;
        if( root != null)
        {
          rootloc = root.getLocation();
          x = rootloc.x + root.getWidth()/2 - getWidth()/2;
          y = rootloc.y + root.getHeight()/2 - getHeight()/2;
        }
        else
        {
          rootloc = getRootPane().getLocation();
          x = rootloc.x + 50 + getRootPane().getWidth()/2 - getWidth()/2;
          y = rootloc.y + getRootPane().getHeight()/2 - getHeight()/2;
        }
      }
      if( x < 0)
        x = 0;
      if( y < 0)
        y = 0;
      if( x + getWidth() > maxX)
        x -= x + getWidth() - maxX;
      if( y + getHeight() > maxY)
        y -= y + getHeight() - maxY;
      setLocation( x, y);
    }
    super.setVisible( b);
  }

  class StatusThread
    extends Thread
  {
    Vector m_vComp = new Vector();
    boolean m_blLoop = true;

    public StatusThread()
    {
    }

    public void init()
    {
      m_jPanelStatus.removeAll();
      m_vComp.removeAllElements();
    }

    public void halt()
    {
      m_blLoop = false;
      //super.interrupt();
    }

    public void run()
    {
      int i = 0;
      m_blLoop = true;

      try
      {
        while( m_blLoop)
        {
          if( m_vConn.size() == 0)
            init();
          for( i = 0; i < m_vConn.size() && i < m_vComp.size(); ++i)
          {
            if( ((ConnectionStatusPanel)m_vComp.elementAt( i)).isKilled())
            {
              i = m_vConn.size();
              init();
              break;
              /*
              m_vComp.removeElementAt( i);
              if( m_vConn.size() < m_vComp.size())
                i = m_vComp.size();
              else
                i = m_vConn.size();
              */
            }
            else
              ((ConnectionStatusPanel)m_vComp.elementAt( i)).update( ((ServerConnection)m_vConn.elementAt(i)));
          }

          if( m_vConn.size() < m_vComp.size())
          {
            for( ; i < m_vComp.size(); ++i)
            {
              m_jPanelStatus.remove( (ConnectionStatusPanel)m_vComp.elementAt( i));
            }
            m_gridLayoutStatus.setRows( m_vConn.size());
          }
          else if( m_vConn.size() > m_vComp.size())
          {
            m_gridLayoutStatus.setRows( m_vConn.size());
            for( ; i < m_vConn.size(); ++i)
            {
              m_vComp.add( new ConnectionStatusPanel( (ServerConnection)m_vConn.elementAt(i)));
              m_jPanelStatus.add( (ConnectionStatusPanel)m_vComp.elementAt( i));
            }
          }
          m_jPanelStatus.repaint();
          Thread.sleep( 1000);
        }
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
