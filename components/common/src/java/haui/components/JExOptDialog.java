package haui.components;

import haui.util.GlobalApplicationContext;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.JOptionPane;

/**
 *
 *		Module:					JExOptDialog.java<br>
 *										$Source: $
 *<p>
 *		Description:    Extended optionpane dialog.<br>
 *</p><p>
 *		Created:				24.09.2002	by	AE
 *</p><p>
 *		@history				24.09.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class JExOptDialog
  extends JOptionPane
{
  private static final long serialVersionUID = -3168580383877717945L;
  
  Component m_frame;
  String m_strAppName;

  public JExOptDialog(Component frame, String strAppName, String title, boolean modal)
  {
    super( null, JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
    m_frame = frame;
    m_strAppName = strAppName;
  }

  public JExOptDialog( Component frame, String strAppName, String title)
  {
    this( frame, strAppName, title, false);
  }

  public JExOptDialog( Component frame, String strAppName, boolean modal)
  {
    this( frame, strAppName, "", modal);
  }

  public JExOptDialog( Component frame, String strAppName)
  {
    this( frame, strAppName, "", false);
  }

  public JExOptDialog()
  {
    this( (Frame)null, null, "", false);
  }

  public void setFrame( Component frame)
  {
    m_frame = frame;
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      Dimension screenDim = getToolkit().getScreenSize();
      int x = 0;
      int y = 0;
      int maxX = screenDim.width;
      int maxY = screenDim.height;
      if( m_frame != null)
      {
        Component root = GlobalApplicationContext.instance().getRootComponent();
        Point rootloc;
        if( root != null)
          rootloc = root.getLocation();
        else
          rootloc = getRootPane().getLocation();
        Point parloc = m_frame.getLocation();
        x = rootloc.x + parloc.x + m_frame.getWidth()/2 - getWidth()/2;
        y = rootloc.y + m_frame.getHeight()/2 - getHeight()/2;
      }
      else
      {
        Component root = GlobalApplicationContext.instance().getRootComponent();
        Point rootloc;
        if( root != null)
        {
          rootloc = root.getLocation();
          x = rootloc.x + root.getWidth()/2 - getWidth()/2;
          y = rootloc.y + root.getHeight()/2 - getHeight()/2;
        }
        else
        {
          rootloc = getRootPane().getLocation();
          x = rootloc.x + 50 + getRootPane().getWidth()/2 - getWidth()/2;
          y = rootloc.y + getRootPane().getHeight()/2 - getHeight()/2;
        }
      }
      if( x < 0)
        x = 0;
      if( y < 0)
        y = 0;
      if( x + getWidth() > maxX)
        x -= x + getWidth() - maxX;
      if( y + getHeight() > maxY)
        y -= y + getHeight() - maxY;
      setLocation( x, y);
    }
    super.setVisible( b);
  }
}