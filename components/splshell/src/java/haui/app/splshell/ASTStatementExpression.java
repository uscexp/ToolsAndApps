
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTStatementExpression extends SimpleNode {
  ASTStatementExpression(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret( id);
    ProcessStore.getInstance(id).getStack().pop(); //just throw away the value.
  }

}
