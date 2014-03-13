
package haui.app.jjshell;

public class ASTFalseNode extends SimpleNode {
  ASTFalseNode(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( new Boolean( "false"));
  }

}
