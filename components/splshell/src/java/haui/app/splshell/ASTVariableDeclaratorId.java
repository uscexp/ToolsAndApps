
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

import java.util.ArrayList;

public class ASTVariableDeclaratorId extends SimpleNode {

  String name;

  public ASTVariableDeclaratorId(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    Node node = jjtGetParent();
    if( !(node instanceof ASTFormalParameter))
    {
      node = node.jjtGetParent();
    }
    node = node.jjtGetChild(0);
    int n = jjtGetNumChildren();
    int dim = 0;
    Primitive primitive = null;
    Object value = null;
    int type = STRING;
    
    if( node instanceof ASTType)
    {
      type = ((ASTType)node).type;
    }
    
    if( n > 0)
    {
      for( int i = 0; i < n; ++i)
      {
        jjtGetChild(i).interpret(id);
        if( ProcessStore.getInstance(id).getStack().peek().equals( ASTArrayDimNode.DIMENSION + id))
        {
          ProcessStore.getInstance(id).getStack().pop();
          ++dim;
        }
      }
    }
    
    Class clazz = getClassFromType( type);
    primitive = new Primitive( clazz);

    if( dim > 0)
    {
      ArrayList list = null;
      for( int i = 0; i < dim; ++i)
      {
        if( i == 0)
        {
          if( dim == 1)
          {
            value = new ArrayList();
            ((ArrayList)value).add( primitive);
            if( ((ArrayList)value).get( 0) instanceof ArrayList)
              list = (ArrayList)((ArrayList)value).get( 0);
          }
          else
          {
            value = new ArrayList();
            if( i == 0)
            {
              ((ArrayList)value).add(  new ArrayList());
              list = (ArrayList)((ArrayList)value).get( 0);
            }
            else
            {
              list.add( new ArrayList());
              list = (ArrayList)list.get( 0);
            }
          }
        }
        else
        {
          if( i+1 == dim)
          {
            list.add( primitive);
          }
          else
          {
            list.add( new ArrayList());
            list = (ArrayList)list.get( 0);
          }
        }
      }
    }
    else
      value = primitive;
    
    if( value != null)
    {
      ProcessStore.getInstance(id).setNewVariable( name, value);
    }
  }
}
