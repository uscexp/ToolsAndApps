
package haui.components.desktop;

import javax.swing.*;
import java.awt.*;

/**
 * Module:      ExDesktopManager.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\desktop\\ExDesktopManager.java,v $
 *<p>
 * Description: DbTreeTableView properties dialog.<br>
 *</p><p>
 * Created:     27.09.2002 by AE
 *</p><p>
 * @history     27.09.2002 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: ExDesktopManager.java,v $
 * Revision 1.0  2003-05-21 16:26:03+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2000; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\desktop\\ExDesktopManager.java,v 1.0 2003-05-21 16:26:03+02 t026843 Exp $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class ExDesktopManager
  extends DefaultDesktopManager
{
  private static final long serialVersionUID = 1507349680689129862L;
  
  // member variables
  JDesktopPane m_dp;

  public ExDesktopManager( JDesktopPane dp)
  {
    super();
    m_dp = dp;
  }

  public void resizeDesktop( JComponent f)
  {
    int iDpWidth = m_dp.getWidth();
    int iDpHeight = m_dp.getHeight();
    int iVisWidth = (int)m_dp.getVisibleRect().getWidth();
    int iVisHeight = (int)m_dp.getVisibleRect().getHeight();
    int iXl = f.getX();
    int iYu = f.getY();
    int iXr = iXl + f.getWidth();
    int iYd = iYu + f.getHeight();
    boolean blChanged = false;

    if( iXr > iDpWidth)
    {
      blChanged = true;
      iDpWidth += (iXr - iDpWidth);
    }
    else if( iXr < iDpWidth && iXr > iVisWidth)
    {
      blChanged = true;
      iDpWidth = iVisWidth + (iXr - iVisWidth);
    }
    else if( iXr < iDpWidth && iXr < iVisWidth)
    {
      blChanged = true;
      iDpWidth = iVisWidth;
    }
    if( iYd > iDpHeight)
    {
      blChanged = true;
      iDpHeight += (iYd - iDpHeight);
    }
    else if( iYd < iDpHeight && iYd > iVisHeight)
    {
      blChanged = true;
      iDpHeight = iVisHeight + (iYd - iVisHeight);
    }
    else if( iYd < iDpHeight && iYd < iVisHeight)
    {
      blChanged = true;
      iDpHeight = iVisHeight;
    }
    if( blChanged)
    {
      m_dp.setPreferredSize( new Dimension( iDpWidth, iDpHeight));
      m_dp.setSize( iDpWidth, iDpHeight);
      m_dp.revalidate();
    }
  }

  public void endDraggingFrame( JComponent f)
  {
    super.endDraggingFrame( f);
    resizeDesktop( f);
  }

  public void endResizingFrame( JComponent f)
  {
    super.endResizingFrame( f);
    resizeDesktop( f);
  }
}
