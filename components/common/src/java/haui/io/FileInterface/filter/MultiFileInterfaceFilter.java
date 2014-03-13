
package haui.io.FileInterface.filter;

import haui.io.FileInterface.FileInterface;

import java.lang.reflect.Array;

/**
 * Module:      MultiFileInterfaceFilter.java<br>
 *              $Source: $
 *<p>
 * Description: Multible extensions file filter.<br>
 *</p><p>
 * Created:     28.05.2003 by AE
 *</p><p>
 * Modification:<br>
 * $Log: $
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class MultiFileInterfaceFilter
  implements FileInterfaceFilter
{
  String[] m_strExt;
  String m_strAppName;

  public MultiFileInterfaceFilter( String[] strExt, String strAppName)
  {
    m_strExt = strExt;
    m_strAppName = strAppName;
  }

  /**
   *  The description of this filter
   *
   * @return    The Description value
   */
  public String getDescription()
  {
    String strDesc = "";

    if( m_strExt != null)
    {
      int iSize = Array.getLength( m_strExt);
      for( int i = 0; i < iSize; ++i)
      {
        if( i == 0)
          strDesc = "*." + m_strExt[i];
        else
          strDesc += ", *." + m_strExt[i];
      }
    }
    else
      strDesc = "Uninitialised file filter";

    return strDesc;
  }


  /**
   *  Accept all specified file extensions.
   *
   * @param  f  file
   * @return    true if accepted
   */
  public boolean accept( FileInterface f)
  {
    boolean blRet = true;

    if( f.isDirectory())
    {
      return blRet;
    }
    blRet = false;

    String strExt = getExtension( f);
    if( strExt != null && m_strExt != null)
    {
      int iSize = Array.getLength( m_strExt);
      for( int i = 0; i < iSize; ++i)
      {
        if( strExt.equals( m_strExt[i]))
        {
          blRet = true;
          break;
        }
      }
    }

    return blRet;
  }

  /*
   * Get the strExt of a file.
   */
  public static String getExtension(FileInterface f)
  {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1)
    {
      ext = s.substring(i).toLowerCase();
    }
    return ext;
  }
}
