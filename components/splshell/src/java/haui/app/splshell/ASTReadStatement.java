
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

public class ASTReadStatement extends SimpleNode {
  String name;

  ASTReadStatement(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    Primitive primitive;
    byte[] b = new byte[64];
    int i;
    
    if ((primitive = ProcessStore.getInstance(id).getPrimitiveVariable(name)) == null)
      System.err.println("Undefined variable : " + name);
    
    System.out.print("Enter a value for \'" + name + "\' (" + primitive.getPrimitiveType().getName() + ") : ");
    System.out.flush();
    try
    {
      i = System.in.read(b);
      primitive.setValue( (new String(b, 0, 0, i - 1)).trim());
      ProcessStore.getInstance(id).setVariable(name, primitive);
    } catch(Exception e) {
      System.err.println("Exception : " + e.getClass().getName());
      System.exit(1);
    }
  }
}
