
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTArrayDimNode extends SimpleNode {
  
  public static final String DIMENSION = "DIM_CONST_NODE";
  
  public ASTArrayDimNode(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( DIMENSION + id);
  }

}
