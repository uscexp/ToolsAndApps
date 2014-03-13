package haui.app.dbtreetableview;

import java.util.Vector;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import haui.components.JExDialog;

/**
 *
 *		Module:					jDialogTable.java
 *<p>
 *		Description:
 *</p><p>
 *		Created:				30.04.1999	by	AE
 *</p><p>
 *		Last Modified:	08.09.2000	by	AE
 *</p><p>
 *		@history				30.04.1999	by	AE: Created.
 *										08.09.2000	by	AE: Edit multible (copied) rows functionality added.<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1999,2000
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class jDialogTable
  extends JExDialog
  implements ActionListener
{
  JScrollPane m_jScrollPane;
  JTable m_jTable;
  JLabel m_jLabelMessage;
  JPanel m_jPanelNav = new JPanel();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  Vector m_vecValues;
  DefaultTableModel m_model;

  public jDialogTable(Frame parent)
  {
    super(parent, DbTtvDesktop.DBTTVDT);
    setFrame( parent);
    constructor( parent, null);
  }

  public jDialogTable(String strTitle)
  {
    super( null, DbTtvDesktop.DBTTVDT);
    setTitle( strTitle);
    constructor( null, null);
  }

  public jDialogTable(Frame parent, String strTitle, String strMsg)
  {
    super(parent, DbTtvDesktop.DBTTVDT);
    setTitle( strTitle);
    setFrame( parent);
    constructor( parent, strMsg);
  }

  public void constructor( Frame parent, String strMsg)
  {
    m_jScrollPane = new JScrollPane();
    m_jTable = new JTable();
    m_jLabelMessage = new JLabel();
    if( strMsg == null)
      strMsg = "Fill in your values:";
    m_vecValues = new Vector();

    if( parent != null)
    {
      Point parloc = parent.getLocation();
      setLocation(parloc.x + 30, parloc.y + 30);
    }
    getContentPane().setLayout( new BorderLayout(0,0));
    setVisible( false);
    setModal( true);
    m_jScrollPane.setOpaque( true);
    getContentPane().add( "Center", m_jScrollPane);
    m_jScrollPane.getViewport().add( m_jTable);
    m_jLabelMessage.setText( strMsg);
    getContentPane().add( "North", m_jLabelMessage);
    m_jPanelNav.setLayout( new FlowLayout(FlowLayout.CENTER,5,5));
    getContentPane().add( "South", m_jPanelNav);
    m_jButtonOk.setText( "Ok");
    m_jButtonOk.setActionCommand( "ok");
    m_jPanelNav.add( m_jButtonOk);
    m_jButtonCancel.setText( "Cancel");
    m_jButtonCancel.setActionCommand( "cancel");
    m_jPanelNav.add( m_jButtonCancel);

    m_jButtonOk.addActionListener( this);
    m_jButtonCancel.addActionListener( this);
    getRootPane().setDefaultButton( m_jButtonOk);
  }

  public void setModel( TableModel tabModel, Vector newData)
  {
    Vector vecNames = new Vector();

    for( int i = 0; i < tabModel.getColumnCount(); i++)
    {
      vecNames.addElement( tabModel.getColumnName( i));
    }
    if( newData == null)
      m_model = new DefaultTableModel( vecNames, 1);
    else
      m_model = new DefaultTableModel( newData, vecNames);
    m_jTable.setModel( m_model);
  }

  public JTable getTable()
  {
    return m_jTable;
  }

  public Vector getValues()
  {
    return m_vecValues;
  }

  public void actionPerformed(java.awt.event.ActionEvent event)
  {
    String cmd = event.getActionCommand();
    if( cmd.equals( "ok"))
    {
      jButtonOk_actionPerformed(event);
    }
    else
    {
      if( cmd.equals( "cancel"))
        jButtonCancel_actionPerformed(event);
    }
  }

  void jButtonOk_actionPerformed(java.awt.event.ActionEvent event)
  {
    Vector row = new Vector();
    if( getTable().isEditing())
      getTable().getCellEditor(getTable().getEditingRow(), getTable().getEditingColumn()).stopCellEditing();

    m_vecValues = m_model.getDataVector();
    setVisible( false);
  }

  void jButtonCancel_actionPerformed(java.awt.event.ActionEvent event)
  {
    m_vecValues = null;
    setVisible( false);
  }

/*
  // testing the implementation
  static public void main(String args[])
  {
    jDialogTable dlg = new jDialogTable( "Add row");
    String str[] = new String[3];
    str[0] = "C1";
    str[1] = "C2";
    str[2] = "C3";
    DefaultTableModel tabModel = new DefaultTableModel( str, 1);

    dlg.setModel( tabModel);
    dlg.pack();
    //dlg.setSize( new Dimension(dlg.getSize().width, 85));
    dlg.setVisible( true);
  }
*/
}
