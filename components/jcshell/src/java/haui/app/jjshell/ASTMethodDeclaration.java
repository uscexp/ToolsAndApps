
package haui.app.jjshell;


public class ASTMethodDeclaration extends SimpleNode {
  public ASTMethodDeclaration(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    String method = ((ASTMethodDeclarator)jjtGetChild(1)).method;
    ProcessStore.getInstance( id).addMethod( method, this);
  }
  
  public void invoke( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    
    ProcessStore.getInstance( id).createNewBlockVariableMap();

    // ResultType()
    jjtGetChild(0).interpret(id);
//    int type = ((ASTResultType)jjtGetChild(0)).type;
//    Class clazz = getClassFromType(type);
    
    // MethodDeclarator()
    jjtGetChild(1).interpret(id);
    
    // Block()
    jjtGetChild(2).interpret(id);
    
//    if( clazz != null) {
//      Object object = ProcessStore.getInstance(id).getStack().pop();
//      
//      if( object instanceof ArrayList)
//      {
//        ArrayList list = (ArrayList)object;
//        ProcessStore.getInstance(id).getStack().push( setArrayValue( list, (ArrayList)value));
//      }
//      else
//      {
//        Primitive primitive = new Primitive( clazz);
//        primitive.setValue( object);
//      }
//    }

    ProcessStore.getInstance( id).removeLastBlockVariableMap();
  }
}
