
package haui.app.jjshell;

import haui.util.Primitive;

public class ASTBitwiseComplNode extends SimpleNode {
  ASTBitwiseComplNode(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);

    Primitive v1 = new Primitive( ProcessStore.getInstance(id).getStack().pop());
    Primitive result = v1.bitwiseCompl();
    ProcessStore.getInstance(id).getStack().push( result.getValue());
  }
}
