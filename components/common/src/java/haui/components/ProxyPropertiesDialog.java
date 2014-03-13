
package haui.components;

import haui.util.AppProperties;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Module:					ProxyPropertiesDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ProxyPropertiesDialog.java,v $ <p> Description:    Proxy properties dialog.<br> </p><p> Created:				29.09.2000	by	AE </p><p>
 * @history  				29.09.2000 - 05.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ProxyPropertiesDialog.java,v $  Revision 1.6  2004-08-31 16:03:09+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.5  2003-05-28 14:19:55+02  t026843  reorganisations  Revision 1.4  2002-09-27 15:29:42+02  t026843  Dialogs extended from JExDialog  Revision 1.3  2002-08-28 14:23:41+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.2  2001-08-14 16:49:13+02  t026843  default button added  Revision 1.1  2001-07-20 16:29:02+02  t026843  FileManager changes  Revision 1.0  2000-10-05 14:48:44+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.6 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ProxyPropertiesDialog.java,v 1.6 2004-08-31 16:03:09+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ProxyPropertiesDialog
  extends JExDialog
{
  private static final long serialVersionUID = 8447797510858147606L;
  
  JPanel m_jPanelBase = new JPanel();
  ProxyPropertiesPanel m_jPanelProxy = new ProxyPropertiesPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JButton m_jButtonOk = new JButton();
  JPanel m_jPanelButtons = new JPanel();
  JLabel m_jLabelRight = new JLabel();
  JLabel m_jLabelLeft = new JLabel();
  JButton m_jButtonCancel = new JButton();

  public ProxyPropertiesDialog(Component frame, String title, boolean modal, AppProperties appProps, String strAppName)
  {
    super(frame, title, modal, strAppName);
    m_jPanelProxy = new ProxyPropertiesPanel( appProps);
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
    setResizable( false);
    m_jPanelProxy._init();
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  public ProxyPropertiesDialog( String strAppName)
  {
    this(null, "", false, null, strAppName);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout( m_borderLayoutBase);
    m_jButtonOk.setActionCommand("Ok");
    m_jButtonOk.setText("Ok");
    m_jLabelRight.setText(" ");
    m_jLabelLeft.setText(" ");
    m_jButtonCancel.setActionCommand("Cancel");
    m_jButtonCancel.setText("Cancel");
    getContentPane().add( m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelBase.add(m_jPanelProxy, BorderLayout.CENTER);
    m_jPanelBase.add(m_jLabelRight, BorderLayout.EAST);
    m_jPanelBase.add(m_jLabelLeft, BorderLayout.WEST);
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

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_jPanelProxy._init();
    }
    m_jPanelProxy.getAuthPanel().setRequestText( ProxyAuthorizationPanel.HEAD);
    super.setVisible( b);
    m_jPanelProxy.requestFocus();
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    m_jPanelProxy._save();
    setVisible( false);
  }
}
