
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTAndNode extends SimpleNode {
  ASTAndNode(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);

    if (!((Boolean)ProcessStore.getInstance(id).getStack().peek()).booleanValue())
    {
      return;
    }

    jjtGetChild(1).interpret(id);
    
    Boolean boolean2 = (Boolean)ProcessStore.getInstance(id).getStack().pop();
    Boolean boolean1 = (Boolean)ProcessStore.getInstance(id).getStack().pop();
     
    ProcessStore.getInstance(id).getStack().push( new Boolean(boolean1.booleanValue() &&
                                boolean2.booleanValue()));

  }

}
