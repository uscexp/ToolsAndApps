package haui.io.FileInterface.remote;

import haui.io.FileInterface.filter.FileInterfaceFilter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

/**
 * Module:      RemoteRequestObject.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\RemoteRequestObject.java,v $
 *<p>
 * Description: class which holds the request data from a ClientTypeFile to a FileTypeServer.<br>
 *</p><p>
 * Created:	    19.05.2004  by AE
 *</p><p>
 * @history     19.05.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: RemoteRequestObject.java,v $
 * Revision 1.0  2004-06-22 14:06:57+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\RemoteRequestObject.java,v 1.0 2004-06-22 14:06:57+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class RemoteRequestObject
  implements Serializable
{
  private static final long serialVersionUID = -268370662875701821L;
  
  private String m_strMethod;
  private Vector m_vecParams = new Vector();
  private int m_iPos = 0;

  public RemoteRequestObject()
  {
  }

  public RemoteRequestObject( String strMethod)
  {
    m_strMethod = strMethod;
  }

  public void setMethod( String strMethod)
  {
    m_strMethod = strMethod;
  }

  public String getMethod()
  {
    return m_strMethod;
  }

  public void addParam( Object obj)
  {
    m_vecParams.add( obj);
  }

  public boolean hasMoreParams()
  {
    return m_iPos < m_vecParams.size();
  }

  public int getParamCount()
  {
    return m_vecParams.size();
  }

  public Object nextParam()
  {
    if( hasMoreParams())
    {
      Object obj = m_vecParams.elementAt( m_iPos++);
      return obj;
    }
    return null;
  }

  public Object[] getParamArray()
  {
    Object[] objArr = null;
    if( getParamCount() > 0)
    {
      objArr = new Object[getParamCount()];

      for( int i = 0; i < m_vecParams.size(); ++i )
      {
        objArr[i] = m_vecParams.elementAt( i);
      }
    }
    return objArr;
  }

  public Class[] getParamTypes()
  {
    Class[] classArgTypes = null;

    if( getParamCount() > 0 )
    {
      classArgTypes = new Class[getParamCount()];
      for( int i = 0; i < m_vecParams.size(); ++i )
      {
        Object obj = m_vecParams.elementAt( i);
        Class cl = null;
        if( obj instanceof FileInterfaceFilter)
          cl = FileInterfaceFilter.class;
        else
          cl = obj.getClass();
        classArgTypes[i] = cl;
      }
    }
    return classArgTypes;
  }

  public String toString()
  {
    String str = super.toString();
    try
    {
      str += ": " + getMethod() + "( ";
      Object[] objs = getParamArray();

      for( int i = 0; objs != null && i < objs.length; ++i )
      {
        Object obj = objs[i];
        if( i == 0 )
        {
          str += obj.toString();
        }
        else
        {
          str += ", " + obj.toString();
        }
      }
      str += ")";
    }
    catch( Exception ex )
    {
    }

    return str;
  }

  private void writeObject( java.io.ObjectOutputStream out )
    throws IOException
  {
    out.defaultWriteObject();

    out.writeObject( getMethod() );
    out.writeObject( m_vecParams);
  }

  private void readObject( java.io.ObjectInputStream in )
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();

    setMethod( (String)in.readObject() );
    m_vecParams = (Vector)in.readObject();
  }
}