
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTForUpdate extends SimpleNode {
  public ASTForUpdate(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);
  }
}
