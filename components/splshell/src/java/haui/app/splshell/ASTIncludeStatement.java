
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ASTIncludeStatement extends SimpleNode {
  
  String path;
  
  public ASTIncludeStatement(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;

    try
    {
      // delete leading and ending <code>"</code>
      if( path.startsWith( "\""))
        path = path.substring( 1, path.length()-1);
      if( path != null)
      {
        parser = new SplShellParser( new FileInputStream( path));
      }
    }
    catch( FileNotFoundException e)
    {
      System.err.println( SplShell.LONGNAME + ": Include File " + path + " not found.");
      return;
    }
    try
    {
      parser.CompilationUnit();
      parser.jjtree.rootNode().interpret( id);
    }
    catch( ParseException e)
    {
      System.err.println( SplShell.LONGNAME + ":  Encountered errors during parse of include file " + path);
      e.printStackTrace();
    }
    catch( Exception e1)
    {
      System.err.println( SplShell.LONGNAME + ":  Encountered errors during interpretation/tree building for include file " + path);
      e1.printStackTrace();
    }
  }
}
