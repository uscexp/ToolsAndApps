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
 *		Module:					AppPropertiesDialog.java<br>
 *										$Source: $
 *<p>
 *		Description:    Dialog to config application properties.<br>
 *</p><p>
 *		Created:				10.12.2001	by	AE
 *</p><p>
 *		@history				10.12.2001	by	AE: Created.<br>
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
public class AppPropertiesDialog
  extends JDialog
{
  AppProperties m_appProps;
  Component m_frame = null;
  String m_strAppName;
  JPanel m_panelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  JLabel m_jLabelServlet = new JLabel();
  JTextField m_jTextFieldServlet = new JTextField();

  public AppPropertiesDialog(Component frame, String title, boolean modal, AppProperties appProps)
  {
    super((Frame)null, title, modal);
    m_frame = frame;
    m_strAppName = HttpTunnelClientServer.APPNAME;
    m_appProps = appProps;
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
    m_jButtonCancel.addActionListener( actionlis);
  }

  public AppPropertiesDialog( Component frame, AppProperties appProps)
  {
    this( frame, "Config map configuration", true, appProps);
  }

  void jbInit() throws Exception
  {
    m_panelBase.setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jLabelServlet.setText(" Servlet URL: ");
    m_jTextFieldServlet.setText("http://");
    getContentPane().add(m_panelBase);
    m_panelBase.add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonOk, null);
    m_jPanelButton.add(m_jButtonCancel, null);
    m_panelBase.add(m_jLabelServlet, BorderLayout.WEST);
    m_panelBase.add(m_jTextFieldServlet, BorderLayout.CENTER);
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
    String servlet = m_jTextFieldServlet.getText();
    m_appProps.setProperty( HttpTunnelClientServer.SERVLET, servlet);
    setVisible( false);
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
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
    m_jTextFieldServlet.requestFocus();
    String servletUrl = m_appProps.getProperty( HttpTunnelClientServer.SERVLET);
    if( servletUrl != null)
      m_jTextFieldServlet.setText( servletUrl);
    super.setVisible( b);
  }
}