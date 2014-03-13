
package haui.app.jjshell;

public class ASTBreakStatement extends SimpleNode {

  public ASTBreakStatement(int id) {
    super(id);
  }
  
  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).setExecState( ProcessStore.BREAK_STATE);
  }
}
