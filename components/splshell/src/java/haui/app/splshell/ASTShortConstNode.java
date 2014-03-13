
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTShortConstNode extends SimpleNode {
  
  short val;
  
  public ASTShortConstNode(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( new Short(val));
  }

}
