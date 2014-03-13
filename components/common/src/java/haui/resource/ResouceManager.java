package haui.resource;

import haui.resource.image.Locator;
import haui.util.GlobalApplicationContext;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Module:      ResouceManager.java<br>
 *              $Source: $
 *<p>
 * Description: ResouceManager.<br>
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     27.05.2003
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @version     v1.0, 2003; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class ResouceManager
{
  // constants
  public static final String COMMONIMAGES = "image";

  public ResouceManager()
  {
  }

  public static ImageIcon getCommonImageIcon( String strAppName, String strName)
  {
    try
    {
      //URL url = (new Locator()).getClass().getResource( strName);
      URL url = null;
      if( strAppName != null)
      {
        StringBuffer strbufPath = new StringBuffer();
        Class cl = GlobalApplicationContext.instance().getApplicationClass();
        strbufPath.append("/haui/resource/image/");
        strbufPath.append( strName);
        url = cl.getResource( strbufPath.toString());
      }
      else
      {
        url = (new Locator()).getClass().getResource( strName);
      }
      return new ImageIcon( url);
    }
    catch (Exception ex)
    {

    }
    return null;
  }
}