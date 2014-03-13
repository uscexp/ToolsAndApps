
package haui.app.jjshell;

public class ASTDoubleConstNode extends SimpleNode {
  
  double val;
  
  public ASTDoubleConstNode(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( new Double(val));
  }

}
