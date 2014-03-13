package haui.io.FileInterface.filter;

import haui.io.FileInterface.FileInterface;

/**
 * Module:      FileInterfaceFilter.java<br>
 *              $Source: $
 *<p>
 * Description: <br>
 * A filter for abstract pathnames.
 *
 * <p> Instances of this interface may be passed to the <code>{@link
 * FileInterface#listFiles(haui.app.fm.FileInterface) listFiles(FileInterfaceFilter)}</code> method
 * of the <code>{@link haui.app.fm.FileInterface}</code> interface.
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
public interface FileInterfaceFilter
{
  /**
   * Tests whether or not the specified abstract pathname should be
   * included in a pathname list.
   *
   * @param  pathname  The abstract pathname to be tested
   * @return  <code>true</code> if and only if <code>pathname</code>
   *          should be included
   */
  boolean accept( FileInterface pathname);

  /**
   *  The description of this filter
   *
   * @return    The Description value
   */
  public String getDescription();
}
