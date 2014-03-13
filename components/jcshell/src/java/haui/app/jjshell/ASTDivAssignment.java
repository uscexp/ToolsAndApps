
package haui.app.jjshell;

import haui.util.Primitive;

public class ASTDivAssignment extends SimpleNode {
  public ASTDivAssignment(int id) {
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
      primitive.div( ProcessStore.getInstance(id).getStack().peek()));
  }
}
