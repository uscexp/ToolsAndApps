
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTNotNode extends SimpleNode {
  ASTNotNode(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);
    ProcessStore.getInstance(id).getStack().push(
       new Boolean(!((Boolean)ProcessStore.getInstance(id).getStack().pop()).booleanValue()));
  }
}
