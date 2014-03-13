
package haui.app.jjshell;

public class ASTMainMethodDeclaration extends SimpleNode {
  
  int type;
  
  public ASTMainMethodDeclaration(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    ProcessStore.getInstance( id).createNewBlockVariableMap();

    // MainMethodDeclarator()
    jjtGetChild(0).interpret(id);
 
    // Block()
    jjtGetChild(1).interpret(id);

    ProcessStore.getInstance( id).removeLastBlockVariableMap();
  }
}
