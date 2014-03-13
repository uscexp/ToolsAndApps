package haui.app.splshell;

import haui.app.splshell.util.Primitive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;

public class SplShellNode implements SplShellParserConstants
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
        
      case FILEREADER:
        clazz = BufferedReader.class;
        break;
        
      case FILEWRITER:
        clazz = BufferedWriter.class;
        break;
    }
    return clazz;
  }
  
  public int getType( String sType)
  {
    int type = STRING;
    if( sType.equals( "boolean"))
      type = BOOL;
    else if( sType.equals( "char"))
      type = CHAR;
    else if( sType.equals( "byte"))
      type = BYTE;
    else if( sType.equals( "short"))
      type = SHORT;
    else if( sType.equals( "int"))
      type = INT;
    else if( sType.equals( "long"))
      type = LONG;
    else if( sType.equals( "float"))
      type = FLOAT;
    else if( sType.equals( "double"))
      type = DOUBLE;
    else if( sType.equals( "string"))
      type = STRING;
    else if( sType.equals( "filereader"))
      type = FILEREADER;
    else if( sType.equals( "filewriter"))
      type = FILEWRITER;
    return type;
  }
}
