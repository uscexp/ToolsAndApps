/* Generated By:JJTree: Do not edit this line. ASTForExpression.java */

package haui.app.jjshell;

public class ASTForExpression extends SimpleNode {
  public ASTForExpression(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);
  }
}
