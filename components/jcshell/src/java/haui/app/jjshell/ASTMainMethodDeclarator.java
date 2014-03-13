
package haui.app.jjshell;

public class ASTMainMethodDeclarator extends SimpleNode {
  public ASTMainMethodDeclarator(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    // MainParameter()
    jjtGetChild(0).interpret(id);
  }
}
