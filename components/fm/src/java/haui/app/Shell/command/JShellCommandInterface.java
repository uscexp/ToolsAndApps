package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import java.util.Vector;

/**
 *    Interface:    JShellCommandInterface.java<br>
 *                  $Source: $
 *<p>
 *    Description:  Java shell command implementation interface.<br>
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
public interface JShellCommandInterface
{
  public String getCommandString();
  public String getHelpString();

  public void registerImplementedCommand( Vector vecList);

  /**
   * check arg command is myself or not.
   */
  public boolean isThisCommand( JShellEnv jse, JShellCommandProcessor jscp);

  public int processCommand( JShellEnv jse, JShellCommandProcessor jscp)
    throws JShellException;

  public void usage( JShellEnv jse);

  public boolean isHiddenCommand();
}
