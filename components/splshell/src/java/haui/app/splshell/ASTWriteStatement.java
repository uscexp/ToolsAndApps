
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTWriteStatement extends SimpleNode {
  String name;

  ASTWriteStatement(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);
    
    System.out.print( ProcessStore.getInstance(id).getStack().pop());
  }

}
