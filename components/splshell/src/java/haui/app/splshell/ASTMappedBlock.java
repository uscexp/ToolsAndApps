
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class ASTMappedBlock extends SimpleNode {
  
  boolean statik = false;
  Vector paramDefs;
  String methodName;
  String typeName;
  String returnType;
  
  public ASTMappedBlock(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance( id).createNewBlockVariableMap();
    
    Object object = null;
    Primitive[] primitives = new Primitive[paramDefs.size()];
    Object[] params = new Object[paramDefs.size()];
    Class[] clazzs = new Class[paramDefs.size()];
    
    if( !statik)
    {
      object = ProcessStore.getInstance(id).getStack().pop();
    }
    
    for( int i = 0; i < paramDefs.size(); ++i)
    {
      int type = getType( (String)paramDefs.get( i));
      Object value = ProcessStore.getInstance(id).getStack().pop();
      if( value instanceof ArrayList)
      {
        params[i] = value;
        clazzs[i] = params[i].getClass();
      }
      else
      {
        Primitive primitive = new Primitive( getClassFromType(type));
        primitive.setValue( value);
        primitives[i] = primitive;
        params[i] = primitive.getValue();
        clazzs[i] = primitives[i].getPrimitiveType();
      }      
    }
    
    Class clazz;
    Object returnObj = null;
    Object retVal = null;
    try
    {
      clazz = Class.forName( typeName);
      if( methodName.equals( "constructor"))
      {
        Constructor constructor = clazz.getConstructor( clazzs);
        returnObj = constructor.newInstance( params);
      }
      else
      {
        Method method = clazz.getMethod( methodName, clazzs);
        returnObj = method.invoke( object, params);
      }
      
      boolean array = false;
      
      if( returnType != null && returnType.endsWith( "]"))
      {
        int idx = returnType.indexOf( '[');
        if( idx > 0)
        {
          returnType = returnType.substring( 0, idx);
          array = true;
        }
      }
      
      if( returnObj != null)
      {
        if( returnObj instanceof Collection && returnType.equals( "ArrayList"))
        {
          ArrayList list = new ArrayList( (Collection)returnObj);
          retVal = list;
        }
        else if( array)
        {
          // TODO extend for multidimensional arrays
          ArrayList list = new ArrayList();
          Primitive primitive = null;
          int type = getType( returnType);
          for( int i = 0; i < Array.getLength( returnObj); ++i)
          {
            primitive = new Primitive( getClassFromType( type));
            primitive.setValue( Array.get( returnObj, i));
            list.add( primitive);
          }
          retVal = list;
        }
        else
        {
          try
          {
            Primitive primitive = null;
            if( returnType != null)
            {
              int type = getType( returnType);
              primitive = new Primitive( getClassFromType( type));
              primitive.setValue( returnObj);
            }
            else
            {
              primitive = new Primitive( returnObj);
            }
            retVal = primitive.getValue();
          }
          catch( Exception e)
          {
            System.err.println( e.getMessage() + " Return type not supported!");
          }
        }
        if( retVal != null)
          ProcessStore.getInstance(id).getStack().push( retVal);
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
 
    ProcessStore.getInstance( id).removeLastBlockVariableMap();
  }
}
