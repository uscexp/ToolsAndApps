
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTVarDeclaration extends SimpleNode
                               implements SplShellParserConstants {

  ASTVarDeclaration(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    int i, k = jjtGetNumChildren();

    for (i = 0; i < k; i++)
       jjtGetChild(i).interpret(id);
  }
}
