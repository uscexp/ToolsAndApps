
package haui.io.FileInterface.filter;

import haui.io.FileInterface.FileInterface;

import java.util.Vector;

/**
 * Module:      OrFileInterfaceFilter.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\OrFileInterfaceFilter.java,v $
 *<p>
 * Description: A file filter that accpets files that are accepted by one of the defined file filters.<br>
 *</p><p>
 * Created:     28.07.2004 by AE
 *</p><p>
 * Modification:<br>
 * $Log: OrFileInterfaceFilter.java,v $
 * Revision 1.0  2004-08-31 15:59:06+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\OrFileInterfaceFilter.java,v 1.0 2004-08-31 15:59:06+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class OrFileInterfaceFilter
  implements FileInterfaceFilter
{
  private Vector m_vecFilters = new Vector();
  protected String m_strAppName;

  public OrFileInterfaceFilter( String strAppName)
  {
    m_strAppName = strAppName;
  }

  public OrFileInterfaceFilter( Vector filters, String strAppName)
  {
    m_strAppName = strAppName;
    addFilter( filters);
  }

  public OrFileInterfaceFilter( FileInterfaceFilter[] filters, String strAppName)
  {
    m_strAppName = strAppName;
    addFilter( filters);
  }

  public OrFileInterfaceFilter( FileInterfaceFilter filter1, FileInterfaceFilter filter2, String strAppName)
  {
    m_strAppName = strAppName;
    m_vecFilters.add( filter1);
    m_vecFilters.add( filter2);
  }

  public String getDescription()
  {
    StringBuffer strBuf = new StringBuffer();

    for( int i = 0; i < m_vecFilters.size(); ++i)
    {
      if( i == 0)
        strBuf.append( ((FileInterfaceFilter)m_vecFilters.elementAt(i)).getDescription());
      else
      {
        strBuf.append( ", ");
        strBuf.append( ((FileInterfaceFilter)m_vecFilters.elementAt(i)).getDescription());
      }
    }

    return strBuf.toString();
  }

  public void addFilter( FileInterfaceFilter filter)
  {
    m_vecFilters.add( filter);
  }

  public void addFilter( FileInterfaceFilter[] filters)
  {
    FileInterfaceFilter fif;
    for( int i = 0; i < filters.length; ++i)
    {
      fif = filters[i];
      m_vecFilters.add( fif );
    }
  }

  public void addFilter( Vector filters)
  {
    FileInterfaceFilter fif;
    for( int i = 0; i < filters.size(); ++i)
    {
      fif = (FileInterfaceFilter)filters.elementAt( i);
      m_vecFilters.add( fif );
    }
  }

  public boolean accept (FileInterface fi)
  {
    boolean blRet = false;

    for( int i = 0; i < m_vecFilters.size(); ++i)
    {
      blRet = ((FileInterfaceFilter)m_vecFilters.elementAt(i)).accept( fi);
      if( blRet)
        break;
    }

    return blRet;
  }
}
