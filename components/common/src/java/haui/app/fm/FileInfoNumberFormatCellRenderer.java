package haui.app.fm;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 *		Module:					FileInfoNumberFormatCellRenderer.java<br>
 *										$Source: $
 *<p>
 *		Description:    TableCellRenderer for a FileInfo table.<br>
 *</p><p>
 *		Created:				03.11.2000	by	AE
 *</p><p>
 *		@history				03.11.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class FileInfoNumberFormatCellRenderer extends JLabel
	implements TableCellRenderer
{
  private static final long serialVersionUID = -2334315766482020899L;
  
  // member variables
  Font m_font;

	public FileInfoNumberFormatCellRenderer()
	{
  	super();
    setHorizontalAlignment( SwingConstants.RIGHT);
    m_font = new Font( getFont().getName(), Font.PLAIN, getFont().getSize());
    setFont( m_font);
		setOpaque(true); //MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus,
		int row, int column)
	{
    if (isSelected) {
      super.setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    }
    else {
      super.setForeground(table.getForeground());
      super.setBackground(table.getBackground());
    }
    if( value instanceof Number)
    {
      setText( NumberFormat.getInstance().format( (Number)value));
    }

		return this;
	}
}