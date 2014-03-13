
package haui.app.jjshell;

public class ASTByteConstNode extends SimpleNode {
  
  byte val;
  
  public ASTByteConstNode(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( new Byte(val));
  }
}
