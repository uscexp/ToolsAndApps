package haui.app.dbtreetableview;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Frame;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import haui.components.JExDialog;

/**
 *
 *		Module:					InputDlg.java
 *<p>
 *		Description:
 *</p><p>
 *		Created:				21.08.2000	by	AE
 *</p><p>
 *		Last Modified:	19.08.2002	by	AE
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000,2002
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class InputDlg
  extends JExDialog
  implements ActionListener
{
  JPanel EditTopPannel;
  JPanel EditRightPannel;
  JPanel EditLeftPannel;
  JLabel msg;
  JLabel dummy, dummy1;
  JTextField inputField;
  JPanel ButPanel;
  JButton ok;
  JButton cancel;

  public InputDlg( Frame parent, String title, String question, String initValue)
  {
    super(parent, true, DbTtvDesktop.DBTTVDT);
    setTitle( title);
    setResizable(false);
    setModal(true);
    getContentPane().setLayout(new BorderLayout(0,0));
    EditTopPannel = new JPanel();
    EditTopPannel.setLayout(new GridLayout(2,1,10,10));
    getContentPane().add("Center", EditTopPannel);
    msg = new JLabel();
    msg.setText(question);
    EditTopPannel.add(msg);
    inputField = new JTextField();
    EditTopPannel.add(inputField);
    EditRightPannel = new JPanel();
    EditLeftPannel = new JPanel();
    dummy = new JLabel();
    dummy.setText( "  ");
    dummy1 = new JLabel();
    dummy1.setText( "  ");
    EditRightPannel.add(dummy);
    EditLeftPannel.add(dummy1);
    getContentPane().add("West", EditLeftPannel);
    getContentPane().add("East", EditRightPannel);
    ButPanel = new JPanel();
    ButPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
    getContentPane().add("South", ButPanel);
    ok = new JButton();
    ok.setText("Ok");
    ok.setActionCommand("Ok");
    ButPanel.add(ok);
    cancel = new JButton();
    cancel.setText("Cancel");
    cancel.setActionCommand("Cancel");
    ButPanel.add(cancel);

    ok.addActionListener(this);
    cancel.addActionListener(this);

    inputField.setText( initValue);
    pack();
    getRootPane().setDefaultButton( ok);
    setVisible( true);
  }

  public String getValue()
  {
    return inputField.getText();
  }

  public void actionPerformed(java.awt.event.ActionEvent event)
  {
    String cmd = event.getActionCommand();
    if (cmd == "Ok")
      ok_actionPerformed(event);
    else if( cmd == "Cancel")
      cancel_actionPerformed(event);
  }

  void ok_actionPerformed(java.awt.event.ActionEvent event)
  {
    //{{CONNECTION
    {
      setVisible(false);
      // dispose();
    }
    //}}
  }

  void cancel_actionPerformed(java.awt.event.ActionEvent event)
  {
    //{{CONNECTION
    {
      inputField.setText(null);
      setVisible(false);
      // dispose();
    }
    //}}
  }

  public void setVisible( boolean bl)
  {
    super.setVisible( bl);
    inputField.requestFocus();
  }
}