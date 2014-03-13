
package haui.dbtool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Module:					TableCellEditor.java <p> Description:		Table view CellRenderer class. </p><p> Created:				27.10.1998	by	AE </p><p> Last Modified:	15.04.1999	by	AE </p><p> history					27.10.1998 - 17.11.1998	by	AE:	Create TableCellEditor basic functionality<br> 26.11.1998	by	AE:	When started value will be set also if it is null<br> 15.04.1999	by	AE:	Converted to JDK v1.2<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999  </p><p>
 * @since  					JDK1.2  </p>
 */
public class DbTableCellEditor extends DefaultCellEditor
implements Runnable
{
  private static final long serialVersionUID = 172822205312706912L;
  
  Object value;
  JButton m_jButton;
  DbTableDialog m_dbTableDlg;
  JTable m_jtable;
  TablePanel m_tpParent;
  JDialog m_dlg;
  JLabel m_jLabelWait;
  DbTableCellEditor m_this;

  public DbTableCellEditor( String strDlgTitle, String strQuery, String strTable, String strResultColumn, boolean blEditable, boolean blOk, boolean blClear, boolean blCancel, boolean blNew, boolean blDelete,
    String strDriver, String strUrl, String strUid, String strPwd, String strAppName)
  {
    super(new JTextField());

    //{{INIT_CONTROLS
    //}}
    setClickCountToStart(2);
    m_jButton = new JButton("");
    m_dbTableDlg = new DbTableDialog( strDlgTitle, strQuery, strTable, strResultColumn, blOk, blClear, blCancel, blNew, blDelete, strDriver, strUrl, strUid, strPwd, strAppName);
    m_dbTableDlg.setEditable( blEditable);
    m_jButton.setBackground(Color.white);
    m_jButton.setBorderPainted(false);
    m_jButton.setMargin(new Insets(0,0,0,0));
    m_this = this;
    //m_jButton.setOpaque(true); //MUST do this for background to show up.

    m_jButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
/*				if( value == null)
          m_jButton.setText( "");
        else
          m_jButton.setText( value.toString());
*/
        if( m_dlg == null)
        {
          m_dlg = new JDialog();
          m_dlg.setModal( true);
          m_dlg.setResizable(false);
          m_dlg.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
          m_dlg.getContentPane().setLayout(new BorderLayout(0,0));
          m_dlg.setCursor(new Cursor(Cursor.WAIT_CURSOR));
          m_jLabelWait = new javax.swing.JLabel("Pease wait ...");
          m_jLabelWait.setSize( 120,64);
          m_dlg.getContentPane().add("Center", m_jLabelWait);
          m_dlg.pack();
        }
        m_dlg.setLocationRelativeTo(m_jButton);
        Thread thread = new Thread( m_this);
        thread.start();
        m_dlg.setVisible(true);
      }
    });
  }

  public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected,
    int row, int column)
  {
    m_jtable = table;
    this.value = value;
    //m_dbTableDlg.setVisible( true);
    return m_jButton;
  }

  public Object getCellEditorValue()
  {
    this.value = m_dbTableDlg.getResultValue();
    return this.value;
  }

  public void setParentTablePanel( TablePanel tp)
  {
    m_tpParent = tp;
  }

  public TablePanel getParentTablePanel()
  {
    return m_tpParent;
  }

  public TablePanel getTablePanel()
  {
    return m_dbTableDlg.getTablePanel();
  }

  public DbTableDialog getTableDialog()
  {
    return m_dbTableDlg;
  }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
  protected void fireEditingStopped()
  {
    super.fireEditingStopped();
  }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
  protected void fireEditingCanceled()
  {
    super.fireEditingCanceled();
  }

  public void run()
  {
    m_tpParent.BeginWait();
    //Without the following line, the dialog comes up
    //in the middle of the screen.
    m_dbTableDlg.setLocationRelativeTo(m_jButton);
    m_dbTableDlg.Query();
    m_dlg.setVisible(false);
    m_dbTableDlg.show();
    m_dbTableDlg.setResultValue( value);
    if( value != null)
    {
      int iRow = m_dbTableDlg.getTablePanel().findRow( value, m_dbTableDlg.getTablePanel().getColumnIndex( m_dbTableDlg.getResultColumn()));
      if( iRow != Constant.IDXERR)
      {
        m_dbTableDlg.getTablePanel().setRowSelectionInterval( iRow, iRow);
        m_dbTableDlg.getTablePanel().getScrollPane().getVerticalScrollBar().setValue(
          (iRow*m_dbTableDlg.getTablePanel().getScrollPane().getVerticalScrollBar().getMaximum())/m_dbTableDlg.getTablePanel().getRowCount());
      }
    }
    m_dbTableDlg.setVisible( false);
    m_dbTableDlg.setModal( true);
    m_dbTableDlg.show();
    m_jButton.setText( "");
    fireEditingStopped();
    m_dbTableDlg.setModal( false);
    m_tpParent.endWait();
  }
}

