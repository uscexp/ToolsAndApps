
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTWhileStatement extends SimpleNode {
  ASTWhileStatement(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    do {
      jjtGetChild(0).interpret(id);

      if (((Boolean)ProcessStore.getInstance(id).getStack().pop()).booleanValue())
      {
        jjtGetChild(1).interpret(id);
        if( ProcessStore.getInstance(id).getExecState() == ProcessStore.BREAK_STATE)
        {
          ProcessStore.getInstance(id).setExecState( ProcessStore.OK_STATE);
          break;
        }
        if( ProcessStore.getInstance(id).getExecState() == ProcessStore.CONT_STATE)
        {
          ProcessStore.getInstance(id).setExecState( ProcessStore.OK_STATE);
        }
      }
      else
        break;
    } while (true);
  }

}
