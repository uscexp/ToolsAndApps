package haui.app.fm;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.InputEvent;

/**
 *		Module:					FileInfoTableHeader.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoTableHeader.java,v $
 *<p>
 *		Description:    FileInfo table header.<br>
 *</p><p>
 *		Created:				26.10.2000	by	AE
 *</p><p>
 *		@history				26.10.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: FileInfoTableHeader.java,v $
 *		Revision 1.0  2003-05-21 16:25:47+02  t026843
 *		Initial revision
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision: 1.0 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoTableHeader.java,v 1.0 2003-05-21 16:25:47+02 t026843 Exp $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class FileInfoTableHeader extends JTableHeader
{

  private static final long serialVersionUID = -7648721186472780033L;

  public FileInfoTableHeader()
  {
    super();
    setPreferredSize( new Dimension( 1, 15));
    initObj();
  }

  public FileInfoTableHeader(TableColumnModel cm)
  {
    super( cm);
    setPreferredSize( new Dimension( 1, 15));
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
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK)
        leftMousePressed( event);
    }

    void leftMousePressed( MouseEvent event)
    {
      ((FileInfoTable)getTable()).sort( event.getPoint());
    }

    void rightMousePressed( MouseEvent event)
    {
      ((FileInfoTable)getTable()).showHeaderPopup( event.getX(), event.getY());
    }
  }
}