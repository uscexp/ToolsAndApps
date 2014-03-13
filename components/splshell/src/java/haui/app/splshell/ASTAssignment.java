
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

import java.util.ArrayList;

public class ASTAssignment extends SimpleNode {
  ASTAssignment(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    String name = null;
    
    jjtGetChild(1).interpret(id);
    Object object = ProcessStore.getInstance(id).getStack().peek();
    
    if( jjtGetChild(0) instanceof ASTId)
      name = ((ASTId)jjtGetChild(0)).name;
    else if( jjtGetChild(0) instanceof ASTArrayId)
    {
      jjtGetChild(0).interpret(id);
      ProcessStore.getInstance(id).getStack().pop();
      name = ((ASTArrayId)jjtGetChild(0)).name;
      object = ((ASTArrayId)jjtGetChild(0)).setArrayValue( id, object);
    }
    
    if( object instanceof ArrayList)
    {
      ArrayList list = (ArrayList)ProcessStore.getInstance(id).getVariable(name);
      ProcessStore.getInstance(id).setVariable( name, setArrayValue( list, (ArrayList)object));
    }
    else
    {
      Primitive primitive = ProcessStore.getInstance(id).getPrimitiveVariable( name);
      primitive.setValue( ProcessStore.getInstance(id).getStack().peek());
      ProcessStore.getInstance(id).setVariable( name, primitive);
    }
  }
}
