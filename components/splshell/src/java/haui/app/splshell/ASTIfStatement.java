
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTIfStatement extends SimpleNode {

  ASTIfStatement(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);

    if (((Boolean)ProcessStore.getInstance(id).getStack().pop()).booleanValue())
      jjtGetChild(1).interpret(id);
    else if (jjtGetNumChildren() == 3)
      jjtGetChild(2).interpret(id);
  }

}
