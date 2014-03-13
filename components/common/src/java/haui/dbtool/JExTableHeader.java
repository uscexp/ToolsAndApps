package haui.dbtool;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 *		Module:					JExTableHeader.java
 *<p>
 *		Description:
 *</p><p>
 *		Created:				01.09.2000	by	AE
 *</p><p>
 *		Last Modified:	01.09.2000	by	AE
 *</p><p>
 *		@history				01.09.2000	by	AE: Created.<br>
 *										04.09.2000	by	AE: Popup (mousehandler) functionality added.<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class JExTableHeader extends JTableHeader
{
  private static final long serialVersionUID = 5163830057361062590L;

  public JExTableHeader()
	{
  	super();
    initObj();
	}

	public JExTableHeader(TableColumnModel cm)
  {
  	super( cm);
    initObj();
  }

  public void initObj()
  {
  	MouseHandler mouseHandler = new MouseHandler();
    addMouseListener( mouseHandler);
  }

  class MouseHandler extends MouseAdapter
  {
	  public void mouseClicked(MouseEvent event)
		{
			//Object object = event.getSource();
	   	if( event.getModifiers() == InputEvent.BUTTON3_MASK)
				rightMousePressed( event);
		}

		void rightMousePressed( MouseEvent event)
		{
    	((JExTable)getTable()).showPopup( event.getX(), event.getY());
		}
  }
}
