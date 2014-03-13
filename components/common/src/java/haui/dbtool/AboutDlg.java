
package haui.dbtool;

import haui.components.JExDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 *		Module:					AboutDlg.java
 *<p>
 *		Description:		Simple about dialog
 *</p><p>
 *		Created:				26.06.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
class AboutDlg
extends JExDialog
implements ActionListener
{
  private static final long serialVersionUID = -749628230014118441L;

  public AboutDlg( String strAppName)
  {
    super( strAppName);
    setTitle( "About");
    setModal( true);

    String msg1 = "  TreePanel + TablePanel Java Bean: ";
    String msg2 = "    Database Tree + Table Panel ";
    String msg3 = "    Version 1.0 by Andreas Eisenhauer ";
    //Fenster
    setBackground(Color.lightGray);
    getContentPane().setLayout(new BorderLayout());
    setResizable(false);
    Point parloc = getParent().getLocation();
    setLocation(parloc.x + 30, parloc.y + 30);
    //Message
    Font fo1 = new Font( "Arial", Font.BOLD, 12);
    Font fo2 = new Font( "Arial", Font.PLAIN, 12);
    Font fo3 = new Font( "Arial", Font.PLAIN, 7);
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayout(6,1));
    JLabel label1 = new JLabel(" ");
    label1.setFont(fo3);
    JLabel label2 = new JLabel(msg1);
    label2.setFont(fo1);
    label2.setForeground( new Color( 0, 0, 150));
    JLabel label3 = new JLabel(" ");
    label3.setFont(fo3);
    JLabel label4 = new JLabel(msg2);
    label4.setFont(fo2);
    label4.setForeground( new Color( 0, 0, 150));
    JLabel label5 = new JLabel(msg3);
    label5.setFont(fo2);
    label5.setForeground( new Color( 0, 0, 150));
    JLabel label6 = new JLabel(" ");
    label6.setFont(fo3);
    panel1.add( label1);
    panel1.add( label2);
    panel1.add( label3);
    panel1.add( label4);
    panel1.add( label5);
    panel1.add( label6);
    getContentPane().add("Center", panel1);
    //Buttons
    JPanel panel2 = new JPanel();
    panel2.setLayout(new FlowLayout(FlowLayout.CENTER));
    JButton button = new JButton("Ok");
    button.addActionListener(this);
    button.setVerticalTextPosition(AbstractButton.CENTER);
    button.setHorizontalTextPosition(AbstractButton.CENTER);
    button.setMnemonic('A');
    panel2.add(button);
    getContentPane().add("South", panel2);
    pack();
    setVisible( true);
  }

  public void actionPerformed(ActionEvent event)
  {
    setVisible(false);
    // dispose();
  }
}
