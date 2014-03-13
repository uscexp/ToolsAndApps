
package haui.app.jjshell;

public class ASTId extends SimpleNode {

  String name;

  ASTId(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    ProcessStore.getInstance(id).getStack().push( ProcessStore.getInstance(id).getVariable(name));
  }

}
