
package haui.app.jjshell;

public class ASTResultType extends SimpleNode {
  
  int type;
  
  public ASTResultType(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    if( jjtGetNumChildren() == 1)
    {
      if( jjtGetChild( 0) instanceof ASTType)
      {
        jjtGetChild( 0).interpret( id);
        type = ((ASTType)jjtGetChild( 0)).type;
      }
    }
      
  }
}
