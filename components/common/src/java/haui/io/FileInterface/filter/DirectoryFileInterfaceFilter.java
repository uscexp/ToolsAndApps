package haui.io.FileInterface.filter;

import haui.io.FileInterface.FileInterface;

import java.io.IOException;

/**
 * Module:      DirectoryFileInterfaceFilter.java<br>
 *              $Source: $
 *<p>
 * Description: Wildcard file filter, accepts any directory.<br>
 *</p><p>
 * Created:     22.07.2004 by AE
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class DirectoryFileInterfaceFilter
  implements FileInterfaceFilter
{
  protected String m_strAppName;

  public DirectoryFileInterfaceFilter( String strAppName)
  {
    m_strAppName = strAppName;
  }

  public String getDescription()
  {
    return "dirs";
  }

  public boolean accept(FileInterface fi)
  {
    boolean blRet = false;

    if( fi.isDirectory())
      blRet = true;

    return blRet;
  }

  private void writeObject( java.io.ObjectOutputStream out )
    throws IOException
  {
    out.defaultWriteObject();
  }

  private void readObject( java.io.ObjectInputStream in )
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
  }
}
