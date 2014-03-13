
package haui.app.jjshell;

public class ASTReturnStatement extends SimpleNode {
  public ASTReturnStatement(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    if( jjtGetNumChildren() == 1)
      jjtGetChild(0).interpret(id);
  }
}
