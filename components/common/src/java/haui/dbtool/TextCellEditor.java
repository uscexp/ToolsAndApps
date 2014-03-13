
package haui.dbtool;

import java.awt.Component;
import java.awt.Cursor;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 *		Module:					TextCellEditor.java
 *<p>
 *		Description:		Table view CellEditor class.
 *</p><p>
 *		Created:				20.10.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					20.10.1998 - 02.11.1998	by	AE:	Create TextCellEditor basic functionality<br>
 *										15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class TextCellEditor extends DefaultCellEditor
{
  private static final long serialVersionUID = 2956863664819755068L;
  
  javax.swing.JScrollPane m_jScrollPane;
	javax.swing.JTextArea m_jTextArea;
	Object value;

	public TextCellEditor()
	{
		super(new JTextField());
		
		m_jScrollPane = new javax.swing.JScrollPane();
		//getViewport().add(m_jScrollPane);
		m_jTextArea = new javax.swing.JTextArea();
		m_jTextArea.setLineWrap(true);
		m_jScrollPane.getViewport().add(m_jTextArea);
		m_jTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		
		m_jScrollPane.setOpaque(true); //MUST do this for background to show up.
	}
	
	public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected,
		int row, int column)
	{
		if( value == null)
			value = "";
		m_jTextArea.setText( value.toString());
		this.value = m_jTextArea.getText();
		return m_jScrollPane;
	}
	
	public Object getCellEditorValue()
	{
		this.value = m_jTextArea.getText();
		return this.value;
	}
	
	public JTextArea getTextArea()
	{
		return m_jTextArea;
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

