
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTForStatement extends SimpleNode {
  public ASTForStatement(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    boolean exit = false;
    int i, k = jjtGetNumChildren();
    Node updateNode = null;
    
    ProcessStore.getInstance( id).createNewBlockVariableMap();
    
    if( (jjtGetChild(0) instanceof ASTForInit))
      jjtGetChild(0).interpret(id);
    do {
      for (i = 0; i < k && !exit; i++)
      {
        if( (jjtGetChild(i) instanceof ASTForInit))
          continue;
        if( jjtGetChild(i) instanceof ASTForUpdate)
          updateNode = jjtGetChild(i);
        else if( jjtGetChild(i) instanceof ASTForExpression)
        {
          jjtGetChild(i).interpret(id);
          if( !((Boolean)ProcessStore.getInstance(id).getStack().pop()).booleanValue())
            exit = true;
        }
        else
        {
          // Statement
          jjtGetChild(i).interpret(id);
          if( ProcessStore.getInstance(id).getExecState() == ProcessStore.BREAK_STATE)
          {
            ProcessStore.getInstance(id).setExecState( ProcessStore.OK_STATE);
            exit = true;
            break;
          }
          if( ProcessStore.getInstance(id).getExecState() == ProcessStore.CONT_STATE)
          {
            ProcessStore.getInstance(id).setExecState( ProcessStore.OK_STATE);
          }
          // ForUpdate
          if( updateNode != null)
            updateNode.interpret( id);
        }
      }
    } while (!exit);
    
    ProcessStore.getInstance( id).removeLastBlockVariableMap();
  }
}
