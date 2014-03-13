package haui.app.fm;

import haui.components.JExDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Module:					ConfigFileExtCommandDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConfigFileExtCommandDialog.java,v $ <p> Description:    Dialog to config CommandClass file extensions.<br> </p><p> Created:				09.11.2000	by	AE </p><p>
 * @history  				09.11.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ConfigFileExtCommandDialog.java,v $  Revision 1.3  2004-08-31 16:03:13+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2004-06-22 14:08:56+02  t026843  bigger changes  Revision 1.1  2003-05-28 14:19:58+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:40+02  t026843  Initial revision  Revision 1.4  2002-09-27 15:28:44+02  t026843  Dialogs extended from JExDialog  Revision 1.3  2002-08-28 14:22:43+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.2  2002-05-29 11:18:16+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.1  2001-08-14 16:48:40+02  t026843  default button added  Revision 1.0  2001-07-20 16:32:51+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.3 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConfigFileExtCommandDialog.java,v 1.3 2004-08-31 16:03:13+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ConfigFileExtCommandDialog
  extends JExDialog
{
  private static final long serialVersionUID = 5694887275480925471L;
  
  JPanel m_panelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  ConfigFileExtCommandPanel m_configFileExtCommandPanel = new ConfigFileExtCommandPanel();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  JLabel m_jLabelDummyLeft = new JLabel();
  JLabel m_jLabelDummyRight = new JLabel();

  public ConfigFileExtCommandDialog(Component frame, String title, boolean modal, Vector cmd)
  {
    super((Frame)null, title, modal, FileManager.APPNAME);
    setFrame( frame);
    m_configFileExtCommandPanel.setFrame( frame);
    m_configFileExtCommandPanel.setCommandVector( cmd);
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

  public ConfigFileExtCommandDialog( Component frame, Vector cmd)
  {
    this( frame, "Command file extension configuration", true, cmd);
  }

  public ConfigFileExtCommandDialog()
  {
    this( null, null);
  }

  void jbInit() throws Exception
  {
    m_panelBase.setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jLabelDummyLeft.setText(" ");
    m_jLabelDummyRight.setText(" ");
    getContentPane().add(m_panelBase);
    m_panelBase.add(m_configFileExtCommandPanel, BorderLayout.CENTER);
    m_panelBase.add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonOk, null);
    m_jPanelButton.add(m_jButtonCancel, null);
    m_panelBase.add(m_jLabelDummyLeft, BorderLayout.WEST);
    m_panelBase.add(m_jLabelDummyRight, BorderLayout.EAST);
  }

  public void hideForStarter()
  {
    m_configFileExtCommandPanel.hideForStarter();
  }

  public void showAfterStarter()
  {
    m_configFileExtCommandPanel.showAfterStarter();
  }

  public void hideForFileEx()
  {
    m_configFileExtCommandPanel.hideForFileEx();
  }

  public void showAfterFileEx()
  {
    m_configFileExtCommandPanel.showAfterFileEx();
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
    m_configFileExtCommandPanel.m_jButtonSet_actionPerformed(e);
    setVisible( false);
  }

  public Vector getCommand()
  {
    return m_configFileExtCommandPanel.getCommand();
  }

  public void setVisible( boolean b)
  {
    super.setVisible( b);
    m_configFileExtCommandPanel.requestFocus();
  }
}

