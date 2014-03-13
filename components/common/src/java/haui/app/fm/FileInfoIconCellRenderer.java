package haui.app.fm;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 *		Module:					FileInfoIconCellRenderer.java<br>
 *										$Source: $
 *<p>
 *		Description:    TableCellRenderer for a FileInfo table.<br>
 *</p><p>
 *		Created:				25.10.2000	by	AE
 *</p><p>
 *		@history				25.10.2000	by	AE: Created.<br>
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
public class FileInfoIconCellRenderer extends JLabel
	implements TableCellRenderer
{
	private static final long serialVersionUID = 2698897729564985798L;

  // member variables

  public FileInfoIconCellRenderer()
	{
  	super();
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
    if( value instanceof ImageIcon)
    {
      setIcon( (ImageIcon)value);
    }

		return this;
	}
}
