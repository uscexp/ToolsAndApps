
package haui.app.jjshell;

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
