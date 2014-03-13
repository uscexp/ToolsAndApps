
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

import java.util.ArrayList;

public class ASTFormalParameter extends SimpleNode {
  public ASTFormalParameter(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    // Type()
    jjtGetChild(0).interpret(id);
    
    // VariableDeclaratorId()
    jjtGetChild(1).interpret(id);
    String name = ((ASTVariableDeclaratorId)jjtGetChild(1)).name;
    Object value = ProcessStore.getInstance(id).getStack().pop();

    if( value instanceof ArrayList)
    {
      ArrayList list = (ArrayList)ProcessStore.getInstance(id).getVariable(name);
      ProcessStore.getInstance(id).setVariable( name, setArrayValue( list, (ArrayList)value));
    }
    else
    {
      Primitive primitive = ProcessStore.getInstance(id).getPrimitiveVariable( name);
      primitive.setValue( value);
      ProcessStore.getInstance(id).setVariable( name, primitive);
    }
  }
}
