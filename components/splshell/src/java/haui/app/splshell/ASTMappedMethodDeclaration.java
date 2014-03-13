
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTMappedMethodDeclaration extends SimpleNode implements ASTMethodInterface {
  public ASTMappedMethodDeclaration(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
  }
  
  public void invoke( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    // MappedBlock()
    jjtGetChild(0).interpret(id);
  }
}
