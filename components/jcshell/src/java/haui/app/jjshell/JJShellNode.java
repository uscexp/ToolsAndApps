package haui.app.jjshell;

import haui.util.Primitive;

import java.util.ArrayList;

public class JJShellNode implements JJShellParserConstants
{
  /** Symbol table */
//  protected static ExtHashMap symtab = new ExtHashMap();

  /** Stack for calculations. */
//  protected static Stack stack = new Stack();
  
  public void interpret( Long id)
  {
    throw new UnsupportedOperationException(); // It better not come here.
  }


  public ArrayList setArrayValue( ArrayList listOrg, ArrayList listVal)
  {
    Object objectOrg = null, objectVal = null;
    Class type = null;
    int i = 0;
    
    for( i = 0; i < listVal.size(); ++i)
    {
      objectVal = listVal.get( i);
      objectOrg = listOrg.get( 0);
      
      if( objectVal instanceof ArrayList)
      {
        listOrg = setArrayValue( (ArrayList)objectOrg, (ArrayList)objectVal);
        listVal.set( i, listOrg);
      }
      else
      {
        if( i == 0)
        {
          type = ((Primitive)objectOrg).getPrimitiveType();
        }
        String val = ((Primitive)objectVal).getValue().toString();
        Primitive primitive = new Primitive( type, val);
        listVal.set( i, primitive);
      }
    }
    return listVal;
  }
  
  public Class getClassFromType( int type)
  {
    Class clazz = null;
    switch(type)
    {
      case BOOL:
        clazz = boolean.class;
        break;
        
      case CHAR:
        clazz = char.class;
        break;
         
      case BYTE:
        clazz = byte.class;
        break;
         
      case SHORT:
        clazz = short.class;
        break;
         
      case INT:
        clazz = int.class;
        break;
         
      case LONG:
        clazz = long.class;
        break;
         
      case FLOAT:
        clazz = float.class;
        break;
         
      case DOUBLE:
        clazz = double.class;
        break;
         
      case STRING:
        clazz = String.class;
        break;
    }
    return clazz;
  }
}
