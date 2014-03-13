
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

public class ASTMultAssignment extends SimpleNode {
  public ASTMultAssignment(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    String name;

    jjtGetChild(1).interpret(id);
    name = ((ASTId)jjtGetChild(0)).name;
    Primitive primitive = ProcessStore.getInstance(id).getPrimitiveVariable( name);
    ProcessStore.getInstance(id).setVariable( name,
       primitive.mult( ProcessStore.getInstance(id).getStack().peek()));
  }
}
