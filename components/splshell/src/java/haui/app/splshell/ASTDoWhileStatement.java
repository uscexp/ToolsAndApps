
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTDoWhileStatement extends SimpleNode {
  public ASTDoWhileStatement(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    do {
      jjtGetChild(0).interpret(id);
      if( ProcessStore.getInstance(id).getExecState() == ProcessStore.BREAK_STATE)
      {
        ProcessStore.getInstance(id).setExecState( ProcessStore.OK_STATE);
        break;
      }
      if( ProcessStore.getInstance(id).getExecState() == ProcessStore.CONT_STATE)
      {
        ProcessStore.getInstance(id).setExecState( ProcessStore.OK_STATE);
      }

      jjtGetChild(1).interpret(id);
    } while (((Boolean)ProcessStore.getInstance(id).getStack().pop()).booleanValue());
  }

}
