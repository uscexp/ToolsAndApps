package haui.app.fm;

import haui.components.desktop.*;
import haui.components.JExDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Module:      SocketConnectDialog.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\SocketConnectDialog.java,v $
 *<p>
 * Description: SocketConnectDialog.<br>
 *</p><p>
 * Created:     01.06.2004 by AE
 *</p><p>
 * @history	    01.06.2004 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: SocketConnectDialog.java,v $
 * Revision 1.1  2004-08-31 16:03:10+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:43+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\SocketConnectDialog.java,v 1.1 2004-08-31 16:03:10+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class SocketConnectDialog
  extends JExDialog
{
  private static final long serialVersionUID = 4068807835428242198L;

  // member variables
  protected boolean m_blCanceled = false;

  // GUI member variables
  protected BorderLayout m_borderLayoutBase = new BorderLayout();
  protected JPanel m_jPanelPort = new JPanel();
  protected JLabel m_jLabelPort = new JLabel();
  protected JTextField m_jTextFieldPort = new JTextField();
  protected JPanel m_jPanelButtons = new JPanel();
  protected FlowLayout m_flowLayoutButtons = new FlowLayout();
  protected JButton m_jButtonOk = new JButton();
  protected JButton m_jButtonCancel = new JButton();
  FlowLayout m_flowLayoutPort = new FlowLayout();
  JTextField m_jTextFieldHost = new JTextField();
  JLabel m_jLabelHost = new JLabel();
  JPanel m_jPanelHost = new JPanel();
  FlowLayout m_flowLayoutHost = new FlowLayout();

  public SocketConnectDialog( Dialog dlg, String title, boolean modal)
  {
    super( dlg, title, modal, FileManager.APPNAME);
    constructor();
  }

  public SocketConnectDialog( Frame frame, String title, boolean modal)
  {
    super( frame, title, modal, FileManager.APPNAME);
    constructor();
  }

  private void constructor()
  {
    try
    {
      jbInit();
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
    m_jPanelPort.setLayout(m_flowLayoutPort);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jLabelPort.setText("Port?");
    m_jTextFieldPort.setMinimumSize(new Dimension(150, 21));
    m_jTextFieldPort.setPreferredSize(new Dimension(150, 21));
    m_jTextFieldHost.setPreferredSize(new Dimension(150, 21));
    m_jTextFieldHost.setMinimumSize(new Dimension(150, 21));
    m_jLabelHost.setText("Host?");
    m_jPanelHost.setLayout(m_flowLayoutHost);
    this.getContentPane().add(m_jPanelPort,  BorderLayout.CENTER);
    m_jPanelPort.add(m_jLabelPort, null);
    m_jPanelPort.add(m_jTextFieldPort, null);
    this.getContentPane().add(m_jPanelButtons,  BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelHost.add(m_jLabelHost, null);
    m_jPanelHost.add(m_jTextFieldHost, null);
    this.getContentPane().add(m_jPanelHost, BorderLayout.NORTH);
  }

  public void setHost( String strHost)
  {
    m_jTextFieldHost.setText( strHost);
  }

  public String getHost()
  {
    return m_jTextFieldHost.getText();
  }

  public void setPort( int iPort)
  {
    m_jTextFieldPort.setText( String.valueOf( iPort));
  }

  public int getPort()
  {
    int iRet = -1;
    try
    {
      iRet = Integer.parseInt( m_jTextFieldPort.getText() );
    }
    catch( NumberFormatException ex )
    {
      ex.printStackTrace();
    }
    return iRet;
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
}
