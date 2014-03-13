
package haui.app.jjshell;

public class ASTOrNode extends SimpleNode {
  ASTOrNode(int id) {
    super(id);
  }


  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    jjtGetChild(0).interpret(id);

    if (((Boolean)ProcessStore.getInstance(id).getStack().peek()).booleanValue())
    {
      return;
    }

    jjtGetChild(1).interpret(id);
    if (ProcessStore.getInstance(id).getStack().peek() instanceof Boolean)
    {
      Boolean boolean2 = (Boolean)ProcessStore.getInstance(id).getStack().pop();
      Boolean boolean1 = (Boolean)ProcessStore.getInstance(id).getStack().pop();
      ProcessStore.getInstance(id).getStack().push( new Boolean(boolean1.booleanValue() || boolean2.booleanValue()));
    }
  }

}
