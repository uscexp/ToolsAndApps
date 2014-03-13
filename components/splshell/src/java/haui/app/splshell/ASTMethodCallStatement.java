
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTMethodCallStatement extends SimpleNode {
  
  String method;
  
  public ASTMethodCallStatement(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    int i, k = jjtGetNumChildren();

    // put the parameters in invers order to the stack to get them later for the
    // method call in correct order
    for (i = k-1; i >= 0; --i)
    {
      jjtGetChild(i).interpret(id);
    }

    ProcessStore.getInstance(id).moveWorkingMapToArchive();
    ProcessStore.getInstance(id).createNewBlockVariableMap();
    
    ASTMethodInterface methodNode = (ASTMethodInterface)ProcessStore.getInstance( id).getMethod( method);
    if( methodNode != null)
    {
      methodNode.invoke(id);
    }
    
    ProcessStore.getInstance(id).restoreLastMapFromArchive();
  }
}
