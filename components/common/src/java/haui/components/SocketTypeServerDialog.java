package haui.components;

import haui.io.FileInterface.FileTypeServer;
import haui.io.FileInterface.SocketTypeServer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Module:      SocketTypeServerDialog.java<br>
 *              $Source: $
 *<p>
 * Description: dialog to log SocketTypeServer output.<br>
 *</p><p>
 * Created:	    08.06.2004  by AE
 *</p><p>
 * @history     08.06.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class SocketTypeServerDialog
  extends FileTypeServerDialog
{
  private static final long serialVersionUID = 293351017582003777L;
  
  JPanel m_jPanelControls = new JPanel();
  FlowLayout m_flowLayoutControls = new FlowLayout();
  JLabel m_jLabelPort = new JLabel();
  JTextField m_jTextFieldPort = new JTextField();

  public SocketTypeServerDialog( Component frame, String title, boolean modal, FileTypeServer fts, String strAppName)
  {
    super( frame, title, modal, fts, strAppName);
    try
    {
      jbInit();
      if( m_fts instanceof SocketTypeServer)
        m_jTextFieldPort.setText( String.valueOf( ((SocketTypeServer)m_fts).getPort()));
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
    m_jLabelPort.setText("Port:");
    m_jTextFieldPort.setMinimumSize(new Dimension(75, 21));
    m_jTextFieldPort.setPreferredSize(new Dimension(75, 21));
    m_jTextFieldPort.setText("");
    m_jPanelControls.add(m_jLabelPort, null);
    m_jPanelControls.add(m_jTextFieldPort, null);
    addComponent( m_jPanelControls);
  }

  public void setLabel( String str)
  {
    m_jLabelPort.setText( str);
  }

  public int getPort()
  {
    int iRet = -1;
    String sPort = m_jTextFieldPort.getText();
    try
    {
      iRet = Integer.parseInt( sPort);
    }
    catch( NumberFormatException nfex)
    {
      nfex.printStackTrace();
    }
    return iRet;
  }

}