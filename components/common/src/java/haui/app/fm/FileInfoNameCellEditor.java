package haui.app.fm;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *		Module:					FileInfoNameCellEditor.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoNameCellEditor.java,v $
 *<p>
 *		Description:    FileInfoTable name CellEditor.<br>
 *</p><p>
 *		Created:				26.10.2000	by	AE
 *</p><p>
 *		@history				26.10.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: FileInfoNameCellEditor.java,v $
 *		Revision 1.0  2003-05-21 16:25:46+02  t026843
 *		Initial revision
 *
 *		Revision 1.0  2001-07-20 16:34:27+02  t026843
 *		Initial revision
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision: 1.0 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoNameCellEditor.java,v 1.0 2003-05-21 16:25:46+02 t026843 Exp $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class FileInfoNameCellEditor extends DefaultCellEditor
{
	private static final long serialVersionUID = -1346730578158238094L;
  
  String m_value;
  JTextField m_jTextField;

	public FileInfoNameCellEditor()
	{
		super( new JTextField());
    setClickCountToStart( 1);
    m_jTextField = (JTextField)getComponent();
	}
	
	public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected,
		int row, int column)
	{
  	FileInfoTable fiTable = (FileInfoTable)table;
		m_value = (String)fiTable.getName( row);
    //Rectangle recName = fiTable.getCellRect( row, column, false);
    //Rectangle recExt = fiTable.getCellRect( row, column+1, false);
    //m_jTextField.setSize( (int)(recName.getWidth() + recExt.getWidth()), (int)(recName.getHeight()));
		m_jTextField.setText( m_value);
		return m_jTextField;
	}
	
	public Object getCellEditorValue()
	{
		m_value = m_jTextField.getText();
		return m_value;
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
