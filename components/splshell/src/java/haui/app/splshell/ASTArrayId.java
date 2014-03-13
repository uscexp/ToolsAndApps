
package haui.app.splshell;

import java.util.ArrayList;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

public class ASTArrayId extends SimpleNode {

  String name;
  
  int dim;
  Primitive[] primitiveIdxs;

  public ASTArrayId(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    dim = jjtGetNumChildren();

    for( int i = 0; i < dim; ++i)
    {
      jjtGetChild(i).interpret(id);
    }
    
    ArrayList list = (ArrayList)ProcessStore.getInstance(id).getVariable(name);
    Object value = null;
    primitiveIdxs = new Primitive[dim];
    int i = 0;
    int ii = dim-1;
    
    for( ii = dim-1; ii+dim >= dim; --ii)
    {
      primitiveIdxs[ii] = new Primitive( ProcessStore.getInstance(id).getStack().pop());
    }
    for( i = 0; i < dim; ++i)
    {
      if( i+1 < dim)
        list = (ArrayList)list.get( primitiveIdxs[i].getIntegerValue());
      else
      {
        if( primitiveIdxs[i].getIntegerValue() >= list.size()) {
          expandArray( primitiveIdxs[i].getIntegerValue(), list);
        }
        value = list.get( primitiveIdxs[i].getIntegerValue());
      }
    }
    if( value instanceof ArrayList)
      ProcessStore.getInstance(id).getStack().push( value);
    else
      ProcessStore.getInstance(id).getStack().push( ((Primitive)value).getValue());
  }
  
  public Object setArrayValue( Long id, Object value)
  {
    ArrayList list = (ArrayList)ProcessStore.getInstance(id).getVariable(name);
    Object object = list;
    int i = 0;
    
    for( i = 0; i < dim-1; ++i)
    {
      list = (ArrayList)list.get( primitiveIdxs[i].getIntegerValue());
    }
    expandArray( primitiveIdxs[i].getIntegerValue(), list);
    Primitive primitive = (Primitive)list.get(primitiveIdxs[i].getIntegerValue());
    primitive.setValue( value);
    list.set( primitiveIdxs[i].getIntegerValue(), primitive);
    return object;
  }
  
  private void expandArray(int idx, ArrayList list)
  {
    int i = 0;
    
    Primitive primitive = (Primitive)list.get(0);
    Class type = primitive.getPrimitiveType();
    
    for( i = list.size(); i < idx+1; ++i)
    {
      list.add( new Primitive( type));
    }
  }
}
