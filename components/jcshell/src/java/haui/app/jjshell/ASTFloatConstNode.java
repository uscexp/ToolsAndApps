
package haui.app.jjshell;

import haui.util.Primitive;

public class ASTFloatConstNode extends SimpleNode {
  
//  float val;
  String val;
  
  public ASTFloatConstNode(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    Node node = jjtGetParent();
    if( node.jjtGetChild(0) instanceof ASTId)
    {
      String name = ((ASTId)node.jjtGetChild(0)).name;
      Primitive primitive = ProcessStore.getInstance(id).getPrimitiveVariable( name);
      Primitive value = new Primitive( primitive.getPrimitiveType(), val);
      ProcessStore.getInstance(id).getStack().push( value.getValue());
    }
    else
    {
      Double doubleVal = new Double( val);
      ProcessStore.getInstance(id).getStack().push( doubleVal);
    }
  }

}
