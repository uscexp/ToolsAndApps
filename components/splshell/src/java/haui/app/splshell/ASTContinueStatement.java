
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTContinueStatement extends SimpleNode {
  public ASTContinueStatement(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).setExecState( ProcessStore.CONT_STATE);
  }
}
