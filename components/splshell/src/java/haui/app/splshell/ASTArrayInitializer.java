
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

import java.util.ArrayList;

public class ASTArrayInitializer extends SimpleNode {
  
  public ASTArrayInitializer(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    int n = jjtGetNumChildren();
    ArrayList list = null;
    
    if( n > 0)
    {
      for( int i = 0; i < n; ++i)
      {
        jjtGetChild(i).interpret(id);
        
        if( ProcessStore.getInstance(id).getStack().peek() instanceof ArrayList)
        {
          list = new ArrayList();
          list.add( ProcessStore.getInstance(id).getStack().pop());
        }
        else if( list instanceof ArrayList)
        {
          list.add( new Primitive( ProcessStore.getInstance(id).getStack().pop()));
        }
        else
        {
          list = new ArrayList();
          list.add( new Primitive( ProcessStore.getInstance(id).getStack().pop()));
        }
      }
    }
    
    ProcessStore.getInstance(id).getStack().push( list);
  }
}
