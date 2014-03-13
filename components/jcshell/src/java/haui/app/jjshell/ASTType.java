
package haui.app.jjshell;

public class ASTType extends SimpleNode {
  
  int type;
  
  public ASTType(int id) {
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
