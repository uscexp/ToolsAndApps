package haui.app.fm;

import haui.components.JExDialog;
import haui.util.CommandClass;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Module:					ConfigButtonCommandDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConfigButtonCommandDialog.java,v $ <p> Description:    Dialog to config CommandClass buttons.<br> </p><p> Created:				08.11.2000	by	AE </p><p>
 * @history  				08.11.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ConfigButtonCommandDialog.java,v $  Revision 1.3  2004-08-31 16:03:12+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2003-06-06 10:04:01+02  t026843  modifications because of the moving the 'TypeFile's to haui.io package  Revision 1.1  2003-05-28 14:19:59+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:39+02  t026843  Initial revision  Revision 1.4  2002-09-27 15:28:46+02  t026843  Dialogs extended from JExDialog  Revision 1.3  2002-08-28 14:22:42+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.2  2002-05-29 11:18:19+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.1  2001-08-14 16:48:41+02  t026843  default button added  Revision 1.0  2001-07-20 16:32:50+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.3 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConfigButtonCommandDialog.java,v 1.3 2004-08-31 16:03:12+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ConfigButtonCommandDialog
  extends JExDialog
{
  private static final long serialVersionUID = -8409218369756640038L;
  
  JPanel m_panelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  ConfigCommandPanel m_configCommandPanel = new ConfigCommandPanel( FileManager.APPNAME);
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  JLabel m_jLabelDummyLeft = new JLabel();
  JLabel m_jLabelDummyRight = new JLabel();

  public ConfigButtonCommandDialog(Component frame, String title, boolean modal, CommandClass cmd)
  {
    super((Frame)null, title, modal, FileManager.APPNAME);
    setFrame( frame);
    try
    {
      jbInit();
      m_configCommandPanel.setCommand( cmd);
      m_configCommandPanel._init();
      m_configCommandPanel.hideMouseButtonCombo();
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  public ConfigButtonCommandDialog( Component frame, CommandClass cmd)
  {
    this( frame, "Command button configuration", true, cmd);
  }

  public ConfigButtonCommandDialog()
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
    m_panelBase.add(m_configCommandPanel, BorderLayout.CENTER);
    m_panelBase.add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonOk, null);
    m_jPanelButton.add(m_jButtonCancel, null);
    m_panelBase.add(m_jLabelDummyLeft, BorderLayout.WEST);
    m_panelBase.add(m_jLabelDummyRight, BorderLayout.EAST);
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
    m_configCommandPanel._save();
    setVisible( false);
  }

  public CommandClass getCommand()
  {
    return m_configCommandPanel.getCommand();
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
    }
    else
    {
      m_configCommandPanel.showMouseButtonCombo();
    }
    super.setVisible( b);
    m_configCommandPanel.requestFocus();
  }
}
