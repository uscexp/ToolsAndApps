
package haui.app.jjshell;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

class JJShell
{
  public static void main( String args[])
  {
    JJShellParser parser;
    if( args.length == 1)
    {
      System.out.println( "Reduced Java Shell Interpreter Version 0.1:  Reading from file " + args[0] + " . . .");
      try
      {
        parser = new JJShellParser( new FileInputStream( args[0]));
      }
      catch( FileNotFoundException e)
      {
        System.err.println( "Reduced Java Shell Interpreter Version 0.1:  File " + args[0] + " not found.");
        return;
      }
    }
    else
    {
      System.out.println( "Reduced Java Shell Interpreter Version 0.1:  Usage :");
      System.out.println( "         java jJshell inputfile");
      return;
    }
    try
    {
      parser.CompilationUnit();
      //Thread.sleep( 1); // asure that the date is never the same
      Date date = new Date();
      Long id = new Long( date.getTime());
      ProcessStore.getInstance( id).setArgs( args);
      parser.jjtree.rootNode().interpret( id);
    }
    catch( ParseException e)
    {
      System.err.println( "Reduced Java Shell Interpreter Version 0.1:  Encountered errors during parse.");
      e.printStackTrace();
    }
    catch( Exception e1)
    {
      System.err.println( "Reduced Java Shell Interpreter Version 0.1:  Encountered errors during interpretation/tree building.");
      e1.printStackTrace();
    }
  }
}
