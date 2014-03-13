package haui.app.Shell.command.remote;

import haui.app.Shell.engine.*;
import haui.app.Shell.command.JShellCommandAlias;
import haui.app.Shell.command.*;

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
      addImplementedCommand( new JShellCommandAlias( new JShellCommandProcessor( straryLook[0], jse), straryLook[1] ) );
    }

    addImplementedCommand( new JShellRemoteCommandBye( jse) );
    addImplementedCommand( new JShellRemoteCommandLcd( jse) );
    addImplementedCommand( new JShellRemoteCommandLpwd( jse) );
    addImplementedCommand( new JShellRemoteCommandLls( jse) );
    addImplementedCommand( new JShellRemoteCommandGet( jse) );
    addImplementedCommand( new JShellRemoteCommandPut( jse) );

    addImplementedCommand( new JShellCommandAlias( new JShellCommandProcessor( "mget", jse),
      "alias for put", "get" ) );
    addImplementedCommand( new JShellCommandAlias( new JShellCommandProcessor( "mput", jse),
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