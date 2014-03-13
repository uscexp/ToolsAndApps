
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTCharConstNode extends SimpleNode {
  
  String val;
  
  public ASTCharConstNode(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( new Character( val.charAt(0)));
  }

}
