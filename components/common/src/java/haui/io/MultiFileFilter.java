
package haui.io;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.lang.reflect.Array;

/**
 * Module:      MultiFileFilter.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\MultiFileFilter.java,v $
 *<p>
 * Description: Multible extensions file filter.<br>
 *</p><p>
 * Created:     25.09.2002 by AE
 *</p><p>
 * Modification:<br>
 * $Log: MultiFileFilter.java,v $
 * Revision 1.1  2003-06-04 15:36:46+02  t026843
 * extende to use with FileInterface, too
 *
 * Revision 1.0  2003-05-28 14:21:30+02  t026843
 * Initial revision
 *
 * Revision 1.0  2002-09-27 15:31:47+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2002; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\MultiFileFilter.java,v 1.1 2003-06-04 15:36:46+02 t026843 Exp $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class MultiFileFilter
  extends FileFilter
{
  String[] m_strExt;

  public MultiFileFilter( String[] strExt)
  {
    m_strExt = strExt;
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
  public boolean accept( File f)
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
   * Get the Ext of a file.
   */
  public static String getExtension(File f)
  {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1)
    {
      ext = s.substring(i+1).toLowerCase();
    }
    return ext;
  }
}

