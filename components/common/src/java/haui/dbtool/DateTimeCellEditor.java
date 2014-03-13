
package haui.dbtool;

import java.awt.Component;
import java.sql.Timestamp;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 *		Module:					DateTimeCellEditor.java
 *<p>
 *		Description:		Table view CellRenderer class.
 *</p><p>
 *		Created:				31.08.2000	by	AE
 *</p><p>
 *		Last Modified:	31.08.2000	by	AE
 *</p><p>
 *		history					31.08.2000	by	AE:	Create DateTimeCellEditor basic functionality<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DateTimeCellEditor extends DefaultCellEditor
{
  private static final long serialVersionUID = 2282241983405832329L;
  
  JTextField m_jTextField;
  String value;

  public DateTimeCellEditor()
  {
    super( new JTextField());
    m_jTextField = (JTextField)getComponent();
  }

  public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected,
    int row, int column)
  {
    if( value == null)
      value = "";
    m_jTextField.setText( value.toString());
    this.value = m_jTextField.getText();
    return m_jTextField;
  }

  public Object getCellEditorValue()
  {
    this.value = m_jTextField.getText();
    if( this.value == null || this.value.equals( ""))
      return null;
    return Timestamp.valueOf( this.value);
  }

  public JTextField getTextField()
  {
    return m_jTextField;
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
}
