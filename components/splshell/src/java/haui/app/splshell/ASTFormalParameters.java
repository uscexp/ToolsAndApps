
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTFormalParameters extends SimpleNode {
  public ASTFormalParameters(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    int i, k = jjtGetNumChildren();
    
    for (i = 0; i < k; i++)
      jjtGetChild(i).interpret(id);
  }
}
