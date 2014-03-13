
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

public class ASTBitwiseOrNode extends SimpleNode {
  ASTBitwiseOrNode(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);
    jjtGetChild(1).interpret(id);

    Primitive v2 = new Primitive( ProcessStore.getInstance(id).getStack().pop());
    Primitive v1 = new Primitive( ProcessStore.getInstance(id).getStack().pop());
    Primitive result = v1.bitwiseOr( v2);
    ProcessStore.getInstance(id).getStack().push( result.getValue());
  }
}
