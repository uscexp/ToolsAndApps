
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTTrueNode extends SimpleNode {
  ASTTrueNode(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( new Boolean( "true"));
  }

}
