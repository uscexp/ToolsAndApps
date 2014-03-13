
package haui.app.jjshell;


public class ASTStringConstNode extends SimpleNode {
  
  String val;

  public ASTStringConstNode(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;
    // delete leading and ending <code>"</code>
    if( val.startsWith( "\""))
      val = val.substring( 1, val.length()-1);
    StringBuffer sb = new StringBuffer( val);
    int idx = -1;
    while( (idx = sb.indexOf( "\\n")) > -1)
    {
      sb.replace(idx, idx + 2, "\n");
    }
    while( (idx = sb.indexOf( "\\t")) > -1)
    {
      sb.replace(idx, idx + 2, "\t");
    }
    while( (idx = sb.indexOf( "\\r")) > -1)
    {
      sb.replace(idx, idx + 2, "\r");
    }
    while( (idx = sb.indexOf( "\\f")) > -1)
    {
      sb.replace(idx, idx + 2, "\f");
    }
    ProcessStore.getInstance(id).getStack().push( sb.toString());
  }
}
