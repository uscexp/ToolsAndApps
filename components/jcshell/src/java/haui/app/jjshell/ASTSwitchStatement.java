
package haui.app.jjshell;

import haui.util.Primitive;

public class ASTSwitchStatement extends SimpleNode {
  
  private static final short CASE = 0;
  private static final short STATEMENT = 1;
  
  public ASTSwitchStatement(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    short state = CASE;
    boolean exit = false;
    int i, k = jjtGetNumChildren();
    
    jjtGetChild(0).interpret(id);
    Object valueToCompare = ProcessStore.getInstance(id).getStack().pop();
    Primitive primitiveToCompare = new Primitive( valueToCompare);
    
    for (i = 1; i < k && !exit; i++)
    {
      switch( state)
      {
        case CASE:
          if( jjtGetChild(i) instanceof ASTCaseNode)
          {
            jjtGetChild(i).interpret(id);
            Object value = ProcessStore.getInstance(id).getStack().pop();
            if( primitiveToCompare.equals( value))
              state = STATEMENT;
          }
          else if( jjtGetChild(i) instanceof ASTDefaultNode)
          {
            jjtGetChild(i).interpret(id);
            if( ((Boolean)ProcessStore.getInstance(id).getStack().pop()).booleanValue())
              state = STATEMENT;
          }
          break;

        case STATEMENT:
          if( !((jjtGetChild(i) instanceof ASTCaseNode) || (jjtGetChild(i) instanceof ASTDefaultNode)))
          {
            jjtGetChild(i).interpret(id);
            if( ProcessStore.getInstance(id).getExecState() == ProcessStore.BREAK_STATE)
            {
              ProcessStore.getInstance(id).setExecState( ProcessStore.OK_STATE);
              exit = true;
              break;
            }
          }
          break;
      }
    }
  }
}
