package haui.components;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Module:      TextInputDialog.java<br>
 *              $Source: $
 *<p>
 * Description: TextInputDialog.<br>
 *</p><p>
 * Created:     02.10.2003 by AE
 *</p><p>
 * @history	    02.10.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class TextInputDialog
  extends JExDialog
{
  private static final long serialVersionUID = -5466709474916423070L;

  // member variables
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

  public TextInputDialog( Dialog dlg, String title, boolean modal, String strAppName)
  {
    super( dlg, title, modal, strAppName);
    constructor();
  }

  public TextInputDialog( Frame frame, String title, boolean modal, String strAppName)
  {
    super( frame, title, modal, strAppName);
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
    pack();
  }

  public String getMessageString()
  {
    return m_jLabelQuestion.getText();
  }

  public void setInputString( String strFind)
  {
    m_jTextFieldInput.setText( strFind);
  }

  public String getInputString()
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
