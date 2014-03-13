
package haui.app.jjshell;

public class ASTBlock extends SimpleNode {
  ASTBlock(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance( id).createNewBlockVariableMap();
    
    int i, k = jjtGetNumChildren();

    for (i = 0; i < k; i++)
      jjtGetChild(i).interpret(id);
 
    ProcessStore.getInstance( id).removeLastBlockVariableMap();
  }

}
