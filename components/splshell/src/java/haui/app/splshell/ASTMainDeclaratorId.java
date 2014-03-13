
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

public class ASTMainDeclaratorId extends SimpleNode {
  public ASTMainDeclaratorId(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    String name = "args";
    Object value = ProcessStore.getInstance(id).getArgs();
    if( value == null)
      value = new String[0];
    
    ProcessStore.getInstance(id).setNewVariable( name, value);
  }
}
