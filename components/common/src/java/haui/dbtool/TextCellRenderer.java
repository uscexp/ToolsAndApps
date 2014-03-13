
package haui.dbtool;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 *		Module:					TextCellRenderer.java
 *<p>
 *		Description:		tree view CellRenderer class.
 *</p><p>
 *		Created:				20.10.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					20.10.1998 - 21.10.1998	by	AE:	Create TextCellRenderer basic functionality<br>
 *										15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class TextCellRenderer extends JScrollPane
	implements TableCellRenderer
{
  private static final long serialVersionUID = 304073669286918624L;
  
  javax.swing.JTextArea m_jTextArea;

	public TextCellRenderer()
	{
		super();
	
		setAutoscrolls(true);
		setOpaque(true);
		m_jTextArea = new javax.swing.JTextArea();
		m_jTextArea.setLineWrap(true);
		getViewport().add(m_jTextArea);
		
		setOpaque(true); //MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent( JTable table, Object color, boolean isSelected, boolean hasFocus,
		int row, int column)
	{
		m_jTextArea.setText( (String)table.getValueAt( row, column));
		return this;
	}
}

