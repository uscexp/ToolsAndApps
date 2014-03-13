package haui.io.FileInterface.remote;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Module:      RemoteResponseObject.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\RemoteResponseObject.java,v $
 *<p>
 * Description: class which holds the response data from a FileTypeServer to a ClientTypeFile.<br>
 *</p><p>
 * Created:	    19.05.2004  by AE
 *</p><p>
 * @history     19.05.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: RemoteResponseObject.java,v $
 * Revision 1.0  2004-06-22 14:06:57+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\RemoteResponseObject.java,v 1.0 2004-06-22 14:06:57+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class RemoteResponseObject
  implements Serializable
{
  private static final long serialVersionUID = -2963633022670572779L;
  
  Object m_obj = null;
  //Object[] m_objArray = null;

  public RemoteResponseObject()
  {
  }

  public boolean isObject()
  {
    return m_obj != null;
  }

  public void setObject( Object obj)
  {
    m_obj = obj;
  }

  public Object getObject()
  {
    return m_obj;
  }

  public String toString()
  {
    String str = super.toString();
    try
    {
      boolean blArr = false;

      try
      {
        if( Array.getLength( getObject() ) > 0 )
        {
          blArr = true;
        }
      }
      catch( IllegalArgumentException ex1 )
      {
      }
      str += ": ";
      if( blArr)
      {
        for( int i = 0; i < Array.getLength( getObject()); ++i)
        {
          Object obj = Array.get( getObject(), i);
          if( i == 0)
            str += obj.toString();
          else
            str += ", " + obj.toString();
        }
      }
      else
        str += getObject().toString();
    }
    catch( Exception ex )
    {
    }
    return str;
  }

/*
  public boolean isObjectArray()
  {
    return m_objArray != null;
  }

  public void setObjectArray( Object[] objArray)
  {
    m_objArray = objArray;
  }

  public Object getObjectArray()
  {
    return m_objArray;
  }
*/

  private void writeObject( java.io.ObjectOutputStream out )
    throws IOException
  {
    out.defaultWriteObject();

    out.writeObject( getObject() );
  }

  private void readObject( java.io.ObjectInputStream in )
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();

    setObject( in.readObject() );
  }
}