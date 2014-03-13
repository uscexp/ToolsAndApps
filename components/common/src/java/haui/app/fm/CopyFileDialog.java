
package haui.app.fm;

import haui.components.JExDialog;

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
 *
 *		Module:					CopyFileDialog.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\CopyFileDialog.java,v $
 *<p>
 *		Description:    File overwrite confirmation dialog.<br>
 *</p><p>
 *		Created:				11.07.2001	by	AE
 *</p><p>
 *		@history				11.07.2001 - 11.07.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: CopyFileDialog.java,v $
 *		Revision 1.1  2004-08-31 16:03:13+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.0  2003-05-21 16:25:42+02  t026843
 *		Initial revision
 *
 *		Revision 1.3  2002-09-27 15:28:46+02  t026843
 *		Dialogs extended from JExDialog
 *
 *		Revision 1.2  2002-08-28 14:22:42+02  t026843
 *		- filmanager.pl upload added.
 *		- first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.
 *
 *		Revision 1.1  2001-08-14 16:48:39+02  t026843
 *		default button added
 *
 *		Revision 1.0  2001-07-20 16:32:53+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				$<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\CopyFileDialog.java,v 1.1 2004-08-31 16:03:13+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class CopyFileDialog
  extends JExDialog
{
  private static final long serialVersionUID = -6691641140914337367L;
  
  // constants
  public static int YES_OPTION = 1;
  public static int OVALL_OPTION = 2;
  public static int NO_OPTION = 3;
  public static int CANCEL_OPTION = 4;

  // member variables
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonYes = new JButton();
  JButton m_jButtonCancel = new JButton();
  JLabel m_jLabelQuestion = new JLabel();
  JButton m_jButtonOvAll = new JButton();
  JButton m_jButtonNo = new JButton();
  int m_iRes = CANCEL_OPTION;

  public CopyFileDialog(Component frame, String title, boolean modal)
  {
    this( frame, title, modal, "");
  }

  public CopyFileDialog(Component frame, String title, boolean modal, String question)
  {
    super( (Frame)null, title, modal, FileManager.APPNAME);
    setFrame( frame);

    try
    {
      jbInit();
      setText( question);
      pack();
      getRootPane().setDefaultButton( m_jButtonYes);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    //setSize( 600, getHeight());
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonYes.addActionListener( actionlis);
    m_jButtonOvAll.addActionListener( actionlis);
    m_jButtonNo.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonYes.setActionCommand("yes");
    m_jButtonYes.setText("Yes");
    m_jButtonCancel.setText("Cancel");
    m_jLabelQuestion.setText("Question");
    m_jButtonOvAll.setActionCommand("ovall");
    m_jButtonOvAll.setText("Overwrite all");
    m_jButtonNo.setActionCommand("no");
    m_jButtonNo.setText("No");
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonYes, null);
    m_jPanelButtons.add(m_jButtonOvAll, null);
    m_jPanelButtons.add(m_jButtonNo, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelBase.add(m_jLabelQuestion, BorderLayout.CENTER);
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd.equals( "yes"))
        m_iRes = YES_OPTION;
      if (cmd.equals( "ovall"))
        m_iRes = OVALL_OPTION;
      if (cmd.equals( "no"))
        m_iRes = NO_OPTION;
      else if (cmd.equals( "Cancel"))
        m_iRes = CANCEL_OPTION;
      setVisible( false);
    }
  }

  public void setVisible( boolean b)
  {
    super.setVisible( b);
  }

  public void setText( String str)
  {
    m_jLabelQuestion.setText( str);
  }

  public int getValue()
  {
    return m_iRes;
  }
}
