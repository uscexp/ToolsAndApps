
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTMainParameter extends SimpleNode {
  
  public ASTMainParameter(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    // MainDeclaratorId()
    jjtGetChild(0).interpret(id);
  }
}
