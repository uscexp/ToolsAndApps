
package haui.app.fm;

import haui.components.JExDialog;
import haui.util.AppProperties;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Module:					ConnectDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConnectDialog.java,v $ <p> Description:    connection dialog.<br> </p><p> Created:				26.06.2001	by	AE </p><p>
 * @history  				26.06.2001 - 26.06.2001	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ConnectDialog.java,v $  Revision 1.3  2004-08-31 16:03:10+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2004-06-22 14:08:49+02  t026843  bigger changes  Revision 1.1  2003-05-28 14:19:58+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:41+02  t026843  Initial revision  Revision 1.3  2002-09-27 15:28:46+02  t026843  Dialogs extended from JExDialog  Revision 1.2  2002-08-28 14:22:41+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.1  2001-08-14 16:48:40+02  t026843  default button added  Revision 1.0  2001-07-20 16:32:52+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				$<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConnectDialog.java,v 1.3 2004-08-31 16:03:10+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ConnectDialog
  extends JExDialog
{
  private static final long serialVersionUID = -5972654314972532227L;
  
  AppProperties m_appProps;
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  ConnectPanel m_ConnectPanel = new ConnectPanel();
  boolean m_blCanceled = false;

  public ConnectDialog(Component frame, String title, boolean modal, AppProperties appProps)
  {
    super( (Frame)null, title, modal, FileManager.APPNAME);
    setFrame( frame);
    m_appProps = appProps;
    m_ConnectPanel = new ConnectPanel( appProps);
    m_blCanceled = false;

    try
    {
      jbInit();
      m_ConnectPanel._init();
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    //setSize( 600, getHeight());
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_ConnectPanel.setBorder(BorderFactory.createEtchedBorder());
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelBase.add(m_ConnectPanel, BorderLayout.CENTER);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        m_jButtonOk_actionPerformed( event);
      else if (cmd == "Cancel")
      {
        m_blCanceled = true;
        setVisible( false);
      }
    }
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_blCanceled = false;
      m_ConnectPanel._init();
    }
    super.setVisible( b);
    m_jButtonOk.requestFocus();
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    m_ConnectPanel._save();
    setVisible( false);
  }

  public boolean isCanceled()
  {
    return m_blCanceled;
  }

  public String getSelection()
  {
    if( !m_blCanceled)
      return m_ConnectPanel.getSelection();
    return null;
  }
}
