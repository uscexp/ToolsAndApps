package haui.app.cdarchive;

import haui.components.desktop.*;
import haui.components.JExDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Module:      FindDialog.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\FindDialog.java,v $
 *<p>
 * Description: FindDialog.<br>
 *</p><p>
 * Created:     02.10.2003 by AE
 *</p><p>
 * @history	    02.10.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: FindDialog.java,v $
 * Revision 1.1  2004-08-31 16:03:06+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:25+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\FindDialog.java,v 1.1 2004-08-31 16:03:06+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class FindDialog
  extends JExInternalDialog
{
  // member variables
  protected JDesktopFrame m_df;
  protected boolean m_blCanceled = false;

  // GUI member variables
  protected BorderLayout m_borderLayoutBase = new BorderLayout();
  protected JPanel m_jPanelInput = new JPanel();
  protected BorderLayout m_borderLayoutInput = new BorderLayout();
  protected JLabel m_jLabelQuestion = new JLabel();
  protected JTextField m_jTextFieldInput = new JTextField();
  protected JPanel m_jPanelButtons = new JPanel();
  protected FlowLayout m_flowLayoutButtons = new FlowLayout();
  protected JButton m_jButtonOk = new JButton();
  protected JButton m_jButtonCancel = new JButton();

  public FindDialog( CdArchiveFrame af, String title, boolean modal)
  {
    super( null, af, CdArchiveDesktop.CDARCHDT, title, modal);

    try
    {
      jbInit();
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
    m_jPanelInput.setLayout(m_borderLayoutInput);
    m_borderLayoutInput.setHgap(5);
    m_borderLayoutInput.setVgap(5);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    this.getContentPane().add(m_jPanelInput,  BorderLayout.CENTER);
    m_jPanelInput.add(m_jLabelQuestion, BorderLayout.NORTH);
    m_jPanelInput.add(m_jTextFieldInput,  BorderLayout.CENTER);
    this.getContentPane().add(m_jPanelButtons,  BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
  }

  public void setMessageString( String strMessage)
  {
    m_jLabelQuestion.setText( strMessage);
  }

  public String getMessageString()
  {
    return m_jLabelQuestion.getText();
  }

  public void setFindString( String strFind)
  {
    m_jTextFieldInput.setText( strFind);
  }

  public String getFindString()
  {
    return m_jTextFieldInput.getText();
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