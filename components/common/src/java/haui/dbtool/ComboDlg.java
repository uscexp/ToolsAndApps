
package haui.dbtool;

import haui.components.JExDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionListener;

/**
 *
 *		Module:					ComboDlg.java
 *<p>
 *		Description:		Combobox dialog to coose DB schema
 *</p><p>
 *		Created:				14.07.1998	by	AE
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
class ComboDlg
extends JExDialog
implements ActionListener
{
  private static final long serialVersionUID = -9118349759336202588L;
  
  javax.swing.JPanel CheckPanel;
  javax.swing.JCheckBox TabCheckBox;
  javax.swing.JCheckBox ViewCheckBox;
  javax.swing.JPanel ComboPanel;
  javax.swing.JComboBox SchemaComboBox;
  javax.swing.JPanel ButPanel;
  javax.swing.JButton OkButton;
  javax.swing.JLabel SchemaLabel;

  String m_strSchema;
  String m_strTypes[] = new String[2];

  public ComboDlg( String strItems[], int iItemCount, String strAppName)
  {
    super( strAppName);
    setModal( true);
    //{{INIT_CONTROLS
    //}}
    setTitle("Select");
    setResizable(false);
    setModal(true);
    getContentPane().setLayout(new BorderLayout(0,0));
    setVisible(false);
    setSize(286,110);
    CheckPanel = new javax.swing.JPanel();
    CheckPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
    CheckPanel.setBounds(0,0,286,35);
    CheckPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
    CheckPanel.setForeground(new Color(0));
    CheckPanel.setBackground(new Color(-3355444));
    getContentPane().add("North", CheckPanel);
    TabCheckBox = new javax.swing.JCheckBox();
    TabCheckBox.setText("Table");
    TabCheckBox.setSelected(true);
    TabCheckBox.setBounds(84,5,58,25);
    TabCheckBox.setFont(new Font("Dialog", Font.BOLD, 12));
    TabCheckBox.setForeground(new Color(0));
    TabCheckBox.setBackground(new Color(-3355444));
    CheckPanel.add(TabCheckBox);
    ViewCheckBox = new javax.swing.JCheckBox();
    ViewCheckBox.setText("View");
    ViewCheckBox.setBounds(147,5,55,25);
    ViewCheckBox.setFont(new Font("Dialog", Font.BOLD, 12));
    ViewCheckBox.setForeground(new Color(0));
    ViewCheckBox.setBackground(new Color(-3355444));
    CheckPanel.add(ViewCheckBox);
    ComboPanel = new javax.swing.JPanel();
    ComboPanel.setLayout(new BorderLayout(0,0));
    ComboPanel.setBounds(0,35,286,75);
    ComboPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
    ComboPanel.setForeground(new Color(0));
    ComboPanel.setBackground(new Color(-3355444));
    getContentPane().add("Center", ComboPanel);
    SchemaComboBox = new javax.swing.JComboBox();
    SchemaComboBox.setBounds(0,15,286,25);
    SchemaComboBox.setFont(new Font("Dialog", Font.BOLD, 12));
    SchemaComboBox.setForeground(new Color(0));
    SchemaComboBox.setBackground(new Color(-3355444));
    ComboPanel.add("Center", SchemaComboBox);
    ButPanel = new javax.swing.JPanel();
    ButPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
    ButPanel.setBounds(0,40,286,35);
    ButPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
    ButPanel.setForeground(new Color(0));
    ButPanel.setBackground(new Color(-3355444));
    ComboPanel.add("South", ButPanel);
    OkButton = new javax.swing.JButton();
    OkButton.setText("Ok");
    OkButton.setActionCommand("Ok");
    OkButton.setBounds(117,5,51,25);
    OkButton.setFont(new Font("Dialog", Font.BOLD, 12));
    OkButton.setForeground(new Color(0));
    OkButton.setBackground(new Color(-3355444));
    ButPanel.add(OkButton);
    SchemaLabel = new javax.swing.JLabel();
    SchemaLabel.setText("Select schema name?");
    SchemaLabel.setBounds(0,0,286,15);
    SchemaLabel.setFont(new Font("Dialog", Font.BOLD, 12));
    SchemaLabel.setForeground(new Color(-10066279));
    SchemaLabel.setBackground(new Color(-3355444));
    ComboPanel.add("North", SchemaLabel);
    //End INIT_CONTROLS
    for( int i = 0; i < iItemCount; i++)
        SchemaComboBox.addItem( strItems[i]);
    OkButton.addActionListener(this);
    Point parloc = getParent().getLocation();
      setLocation(parloc.x + 30, parloc.y + 30);
    pack();

    //{{REGISTER_LISTENERS
    //SymAction lSymAction = new SymAction();
    //OkButton.addActionListener(lSymAction);
    //}}
  }

  public String getSchema()
  {
      return m_strSchema;
  }

  public String[] getTypes()
  {
      return m_strTypes;
  }

    public void addItem( Object objItem)
    {
        SchemaComboBox.addItem( objItem);
    }

//	class SymAction implements java.awt.event.ActionListener
//	{
    public void actionPerformed(java.awt.event.ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        OkButton_actionPerformed(event);
    }
//	}

  void OkButton_actionPerformed(java.awt.event.ActionEvent event)
  {
    // to do: code goes here.

    //{{CONNECTION
    // getSelectedItem...
    {
      m_strSchema = (String)SchemaComboBox.getSelectedItem();
      if( TabCheckBox.isSelected())
        m_strTypes[0] = "TABLE";
      else
        m_strTypes[0] = null;

      if( ViewCheckBox.isSelected())
      {
        if( m_strTypes[0] != null)
          m_strTypes[1] = "VIEW";
        else
        {
          m_strTypes[0] = "VIEW";
          m_strTypes[1] = null;
        }
      }
      else
      {
        m_strTypes[1] = null;
      }

      setVisible( false);
    }
    //}}
  }
}
