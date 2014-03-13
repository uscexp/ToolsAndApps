
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

public class ASTPostIncrementExpression extends SimpleNode {
  public ASTPostIncrementExpression(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    String name;

    name = ((ASTId)jjtGetChild(0)).name;
    Primitive primitive = ProcessStore.getInstance(id).getPrimitiveVariable( name);
    ProcessStore.getInstance(id).getStack().push( primitive.getValue());
    primitive.increment();
    ProcessStore.getInstance(id).setVariable( name, primitive);
  }

}
