
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.GeneralFileInterfaceConfiguration;
import haui.tool.shell.JShell;
import haui.tool.shell.engine.JShellEngine;

public class ASTJshStatement extends SimpleNode {
  
  String command;
  
  public ASTJshStatement(int id) {
    super(id);
  }

  public void interpret( Long id)
  {
    if( !ProcessStore.getInstance(id).checkPrecondition())
      return;

    try
    {
      // delete leading and ending <code>"</code>
      if( command.startsWith( "\""))
        command = command.substring( 1, command.length()-1);
      if( ProcessStore.getInstance(id).getJShell() == null)
      {
        JShell shell = new JShell( SplShell.APPNAME);
        shell.getShellEnv().setFileInterface( FileConnector.createFileInterface( ".", null, false,
            new GeneralFileInterfaceConfiguration( SplShell.APPNAME, null, true)) );
        shell.getShellEnv().setTargetPath( ".");
        shell.getShellEnv().setExecLocal( true);
        shell.setOut(System.out);
        shell.setErr(System.err);
        ProcessStore.getInstance(id).setJShell( shell);
      }
//      ProcessStore.getInstance(id).getJShell().getShellEngine().processCommands( command, 
//          ProcessStore.getInstance(id).getJShell().getShellEnv(), false);
      JShellEngine.processCommands( command, 
          ProcessStore.getInstance(id).getJShell().getShellEnv(), false);
    }
    catch( Exception e)
    {
      System.err.println( SplShell.LONGNAME + ": Error executing " + command + " ; " + e.getMessage());
      return;
    }
  }
}
