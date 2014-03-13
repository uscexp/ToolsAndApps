
package haui.components;

import haui.util.AppProperties;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Module:					LRShellPropertiesDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\LRShellPropertiesDialog.java,v $ <p> Description:    Proxy properties dialog.<br> </p><p> Created:				29.09.2000	by	AE </p><p>
 * @history  				29.09.2000 - 05.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: LRShellPropertiesDialog.java,v $  Revision 1.7  2004-08-31 16:03:14+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.6  2003-05-28 14:19:51+02  t026843  reorganisations  Revision 1.5  2002-09-27 15:29:42+02  t026843  Dialogs extended from JExDialog  Revision 1.4  2002-08-28 14:23:41+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.3  2001-08-14 16:49:12+02  t026843  default button added  Revision 1.2  2001-07-20 16:29:02+02  t026843  FileManager changes  Revision 1.1  2000-10-13 09:09:44+02  t026843  bugfixes + changes  Revision 1.0  2000-10-05 14:48:43+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.7 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\LRShellPropertiesDialog.java,v 1.7 2004-08-31 16:03:14+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class LRShellPropertiesDialog
  extends JExDialog
{
  private static final long serialVersionUID = 5574900066028353710L;
  
  AppProperties m_appProps;
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  ProxyPropertiesPanel m_jPanelProxy = new ProxyPropertiesPanel();
  LRShellPropertiesPanel m_jPanelLRShell = new LRShellPropertiesPanel();
  JPanel m_jPanelComponents = new JPanel();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  BorderLayout m_borderLayoutComponents = new BorderLayout();

  public LRShellPropertiesDialog(Frame frame, String title, boolean modal, AppProperties appProps, String strAppName)
  {
    super(frame, title, modal, strAppName);
    m_appProps = appProps;
    m_jPanelLRShell = new LRShellPropertiesPanel( appProps);
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
    setSize( 400, getHeight());
    setResizable( false);
    m_jPanelLRShell._init();
    m_jPanelProxy._init();
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelComponents.setLayout(m_borderLayoutComponents);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jPanelProxy.setBorder(BorderFactory.createEtchedBorder());
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelComponents, BorderLayout.CENTER);
    m_jPanelComponents.add(m_jPanelLRShell, BorderLayout.NORTH);
    m_jPanelComponents.add(m_jPanelProxy, BorderLayout.CENTER);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
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

  public void setFrame( Component frame)
  {
    m_frame = frame;
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_jPanelLRShell._init();
      m_jPanelProxy._init();
    }
    super.setVisible( b);
    m_jPanelLRShell.requestFocus();
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    m_jPanelLRShell._save();
    m_jPanelProxy._save();
    setVisible( false);
  }
}
