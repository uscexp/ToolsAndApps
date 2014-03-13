
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

public class ASTIntConstNode extends SimpleNode {

//  int val;
  String val;

  ASTIntConstNode(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    Node node = jjtGetParent();
    if( !(node instanceof ASTMethodCallStatement) && node.jjtGetChild(0) instanceof ASTId)
    {
      String name = ((ASTId)node.jjtGetChild(0)).name;
      Primitive primitive = ProcessStore.getInstance(id).getPrimitiveVariable( name);
      Primitive value = new Primitive( primitive.getPrimitiveType(), val);
      ProcessStore.getInstance(id).getStack().push( value.getValue());
    }
    else if( !(node instanceof ASTMethodCallStatement) && node.jjtGetChild(0) instanceof ASTArrayId)
    {
      String name = ((ASTArrayId)node.jjtGetChild(0)).name;
      Object value = ProcessStore.getInstance(id).getVariable( name);
      Primitive primitive = null;
      while( value instanceof Object[])
      {
        value = ((Object[])value)[0];
      }
      if( value instanceof Primitive)
        primitive = (Primitive)value;
      if( primitive != null) {
        primitive = new Primitive( primitive.getPrimitiveType(), val);
        ProcessStore.getInstance(id).getStack().push( primitive.getValue());
      }
      else
        ProcessStore.getInstance(id).getStack().push( val);
    }
    else
    {
      Long longVal = new Long( val);
      ProcessStore.getInstance(id).getStack().push( longVal);
    }
  }

}
