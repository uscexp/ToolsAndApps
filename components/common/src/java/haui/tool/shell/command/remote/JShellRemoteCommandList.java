package haui.tool.shell.command.remote;

import haui.tool.shell.command.JShellCommandAlias;
import haui.tool.shell.command.JShellCommandCat;
import haui.tool.shell.command.JShellCommandCd;
import haui.tool.shell.command.JShellCommandCls;
import haui.tool.shell.command.JShellCommandCp;
import haui.tool.shell.command.JShellCommandEcho;
import haui.tool.shell.command.JShellCommandEnv;
import haui.tool.shell.command.JShellCommandHelp;
import haui.tool.shell.command.JShellCommandLs;
import haui.tool.shell.command.JShellCommandMkdir;
import haui.tool.shell.command.JShellCommandMv;
import haui.tool.shell.command.JShellCommandPwd;
import haui.tool.shell.command.JShellCommandRm;
import haui.tool.shell.command.JShellCommandSet;
import haui.tool.shell.engine.JShellCommandList;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;

/**
 *    Module:       JShellCommandList.java<br>
 *                  $Source: $
 *<p>
 *    Description:  Java shell command list of intern commands.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: $
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: $<br>
 *                  $Header: $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class JShellRemoteCommandList
  extends JShellCommandList
{
  public JShellRemoteCommandList(  JShellEnv jse)
  {
    super( jse);
  }

  public void init( JShellEnv jse)
  {
    for( int index = 0; index < straryCommandList.length; index++ )
    {
      String[] straryLook = straryCommandList[index];
      addImplementedCommand( new JShellCommandAlias( (new JShellCommandProcessor( jse, straryLook[0])).getCommandLineContainer(),
          straryLook[1] ) );
    }

    addImplementedCommand( new JShellRemoteCommandBye( jse) );
    addImplementedCommand( new JShellRemoteCommandLcd( jse) );
    addImplementedCommand( new JShellRemoteCommandLpwd( jse) );
    addImplementedCommand( new JShellRemoteCommandLls( jse) );
    addImplementedCommand( new JShellRemoteCommandGet( jse) );
    addImplementedCommand( new JShellRemoteCommandPut( jse) );

    addImplementedCommand( new JShellCommandAlias( (new JShellCommandProcessor( jse, "mget")).getCommandLineContainer(),
      "alias for put", "get" ) );
    addImplementedCommand( new JShellCommandAlias( (new JShellCommandProcessor( jse, "mput")).getCommandLineContainer(),
      "alias for put", "put" ) );

    // general commands
    addImplementedCommand( new JShellCommandCat( jse) );
    addImplementedCommand( new JShellCommandEcho( jse) );
    //addImplementedCommand( new JShellCommandEdit( jse) );
    addImplementedCommand( new JShellCommandHelp( jse) );
    addImplementedCommand( new JShellCommandCd( jse) );
    addImplementedCommand( new JShellCommandMv( jse) );
    addImplementedCommand( new JShellCommandCp( jse) );
    addImplementedCommand( new JShellCommandRm( jse) );
    addImplementedCommand( new JShellCommandMkdir( jse) );
    addImplementedCommand( new JShellCommandLs( jse) );
    addImplementedCommand( new JShellCommandPwd( jse) );
    addImplementedCommand( new JShellCommandEnv( jse) );
    addImplementedCommand( new JShellCommandSet( jse) );
    addImplementedCommand( new JShellCommandCls( jse) );

    sortImplementedCommand();
  }
}