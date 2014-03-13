
package haui.app.splshell;

import haui.app.splshell.util.Primitive;
import haui.app.splshell.util.ProcessStore;

import java.util.ArrayList;


public class ASTVariableDeclarator extends SimpleNode {

  String name;

  public ASTVariableDeclarator(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    int i, k = jjtGetNumChildren();
    Object value = null;

    for (i = 0; i < k; i++)
    {
      if( jjtGetChild(i) instanceof ASTVariableDeclaratorId)
      {
        jjtGetChild(i).interpret(id);
        name = ((ASTVariableDeclaratorId)jjtGetChild(i)).name;
      }
      else
      {
        jjtGetChild(i).interpret(id);
        value = ProcessStore.getInstance(id).getStack().peek(); // Assignment value

        if( value instanceof ArrayList)
        {
          ArrayList list = (ArrayList)ProcessStore.getInstance(id).getVariable(name);
          ProcessStore.getInstance(id).setVariable( name, setArrayValue( list, (ArrayList)value));
        }
        else
        {
          Primitive primitive = ProcessStore.getInstance(id).getPrimitiveVariable( name);
          primitive.setValue( value);
          ProcessStore.getInstance(id).setVariable( name, primitive);
        }
      }
    }
  }
}
