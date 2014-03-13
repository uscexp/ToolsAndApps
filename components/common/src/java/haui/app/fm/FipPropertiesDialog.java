
package haui.app.fm;

import haui.components.JExDialog;
import haui.util.AppProperties;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * Module:					FipPropertiesDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FipPropertiesDialog.java,v $ <p> Description:    FileInfoPanel properties dialog.<br> </p><p> Created:				26.06.2001	by	AE </p><p>
 * @history  				26.06.2001 - 26.06.2001	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FipPropertiesDialog.java,v $  Revision 1.3  2004-08-31 16:03:11+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2004-06-22 14:08:57+02  t026843  bigger changes  Revision 1.1  2003-05-28 14:19:50+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:51+02  t026843  Initial revision  Revision 1.6  2002-09-27 15:28:45+02  t026843  Dialogs extended from JExDialog  Revision 1.5  2002-09-18 11:16:16+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.4  2002-08-28 14:22:43+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.3  2002-08-07 15:25:26+02  t026843  Ftp support via filetype added.  Some bugfixes.  Revision 1.2  2002-05-29 11:18:16+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.1  2001-08-14 16:48:39+02  t026843  default button added  Revision 1.0  2001-07-20 16:34:25+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				$<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FipPropertiesDialog.java,v 1.3 2004-08-31 16:03:11+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FipPropertiesDialog
  extends JExDialog
{
  private static final long serialVersionUID = -3391342058719686472L;
  
  AppProperties m_appProps;
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelComponents = new JPanel();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  BorderLayout m_borderLayoutComponents = new BorderLayout();
  JSplitPane m_jSplitPaneCenter = new JSplitPane();
  ConfigFileExtCommandPanel m_jPanelCoCmd = new ConfigFileExtCommandPanel();
  BorderLayout m_borderLayoutLeft = new BorderLayout();
  JPanel m_jPanelLeft = new JPanel();
  FipPropertiesPanel m_jPanelFip = new FipPropertiesPanel();
  boolean m_blCanceled = false;

  public FipPropertiesDialog( Component frame, String title, boolean modal, AppProperties appProps, Vector cmd)
  {
    super( frame, title, modal, FileManager.APPNAME);
    m_appProps = appProps;
    m_jPanelFip = new FipPropertiesPanel( this, appProps);
    setCommandVector( cmd);
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
    setSize( 600, getHeight());
    setResizable( false);
    m_jPanelFip._init();
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  public FipPropertiesDialog(Component frame, String title, boolean modal, AppProperties appProps)
  {
    this( frame, title, modal, appProps, null);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelComponents.setLayout(m_borderLayoutComponents);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jPanelLeft.setLayout(m_borderLayoutLeft);
    m_jPanelFip.setMinimumSize(new Dimension(300, 265));
    m_jPanelFip.setPreferredSize(new Dimension(300, 265));
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelComponents, BorderLayout.CENTER);
    m_jPanelComponents.add(m_jSplitPaneCenter, BorderLayout.CENTER);
    m_jSplitPaneCenter.add(m_jPanelCoCmd, JSplitPane.RIGHT);
    m_jSplitPaneCenter.add(m_jPanelLeft, JSplitPane.LEFT);
    m_jPanelLeft.add(m_jPanelFip, BorderLayout.NORTH);
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
      {
        setVisible( false );
        m_blCanceled = true;
      }
    }
  }

  public boolean isCanceled()
  {
    return m_blCanceled;
  }

  public void hideFipPanel()
  {
    m_jPanelFip.setVisible( false);
  }

  public void showFipPanel()
  {
    m_jPanelFip.setVisible( true);
  }

  public void hideForFileEx()
  {
    m_jPanelCoCmd.hideForFileEx();
  }

  public void showAfterFileEx()
  {
    m_jPanelCoCmd.showAfterFileEx();
  }

  public void setCommandVector( Vector cmd)
  {
    m_jPanelCoCmd.setCommandVector( cmd);
  }

  public Vector getCommand()
  {
    return m_jPanelCoCmd.getCommand();
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_jPanelFip._init();
    }
    super.setVisible( b);
    m_jPanelCoCmd.requestFocus();
    m_jSplitPaneCenter.setDividerLocation( 0.5);
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    m_jPanelFip._save();
    m_jPanelCoCmd.m_jButtonSet_actionPerformed( e);
    m_blCanceled = false;
    setVisible( false);
  }
}
