
package haui.app.jjshell;

public class ASTLongConstNode extends SimpleNode {
  
  long val;
  
  public ASTLongConstNode(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( new Long(val));
  }

}
