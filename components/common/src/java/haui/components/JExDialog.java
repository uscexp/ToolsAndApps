package haui.components;

import haui.util.GlobalApplicationContext;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.JDialog;

/**
 *
 *		Module:					JExDialog.java<br>
 *										$Source: $
 *<p>
 *		Description:    Extended dialog.<br>
 *</p><p>
 *		Created:				19.09.2002	by	AE
 *</p><p>
 *		@history				19.09.2002	by	AE: Created.<br>
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
public class JExDialog
  extends JDialog
{
  private static final long serialVersionUID = 2218560550160476631L;
  
  Component m_frame;
  protected String m_strAppName;

  public JExDialog(Component frame, String title, boolean modal, String strAppName)
  {
    super( (Frame)null, title, modal);
    setFrame( frame);
    m_strAppName = strAppName;
  }

  public JExDialog( Component frame, String title, String strAppName)
  {
    this( frame, title, false, strAppName);
  }

  public JExDialog( Component frame, boolean modal, String strAppName)
  {
    this( frame, "", modal, strAppName);
  }

  public JExDialog( Component frame, String strAppName)
  {
    this( frame, "", false, strAppName);
  }

  public JExDialog( String strAppName)
  {
    this( null, "", false, strAppName);
  }

  public void setFrame( Component frame)
  {
    m_frame = frame;
  }

  public Component getFrame()
  {
    return m_frame;
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

      Component root = null;
      if( m_strAppName != null && !m_strAppName.equals( ""))
        root = GlobalApplicationContext.instance().getRootComponent();

      if( m_frame != null)
      {
        Point rootloc;
        if( root != null)
          rootloc = root.getLocation();
        else
          rootloc = getRootPane().getLocation();
        Point parloc = m_frame.getLocation();
        if( m_frame instanceof Dialog)
        {
          x = rootloc.x + m_frame.getWidth() / 2 - getWidth() / 2;
          y = rootloc.y + m_frame.getHeight() / 2 - getHeight() / 2;
        }
        else
        {
          x = rootloc.x + parloc.x + m_frame.getWidth() / 2 - getWidth() / 2;
          y = rootloc.y + m_frame.getHeight() / 2 - getHeight() / 2;
        }
      }
      else
      {
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