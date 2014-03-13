
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTMethodDeclarator extends SimpleNode {
  
  String method;
  
  public ASTMethodDeclarator(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    // FormalParameters()
    jjtGetChild(0).interpret(id);
  }
}
