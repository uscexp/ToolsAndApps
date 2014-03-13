/* *****************************************************************
 * Project: common
 * File:    CmdNoTextState.java
 * 
 * Creation:     03.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.tool.shell.engine.parsestate;

import java.util.StringTokenizer;

import haui.tool.shell.engine.JShellCommandProcessor;

/**
 * CmdNoTextState
 * <ul>
 *   <li>this state represents and handles a command line with several commands.</li>
 * </ul>
 * 
 * @author <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class CmdNoTextState extends PreParseState
{

  /** 
   * Default constructor
   */
  public CmdNoTextState()
  {
    super();
  }

  /** 
   * This method handles a request from a PreParseContext instance.
   * 
   * @param context PreParseContext
   * @param commandProcessor JShellCommandProcessor
   * @param strBufCmd StringBuffer for the commnad
   * @param stMask StringTokenizer of the command line
   */
  public void handle( PreParseContext context, JShellCommandProcessor commandProcessor, StringBuffer strBufCmd, StringTokenizer stMask)
  {
    while( stMask.hasMoreTokens())
    {
      String str = stMask.nextToken();
      if( str.equals( "\"" ) )
      {
        strBufCmd.append( str);
        super.changeState( context, new CmdTextState());
        context.request( commandProcessor, strBufCmd, stMask);
      }
      else
      {
        StringTokenizer stCmds = new StringTokenizer( str, ";", true );
        while( stCmds.hasMoreTokens() )
        {
          String strTmp = stCmds.nextToken();
          if( strTmp.equals( ";"))
          {
            commandProcessor.addCommand( strBufCmd.toString() );
            strBufCmd.delete( 0, strBufCmd.length() - 1);
          }
          else
            strBufCmd.append( strTmp);
        }
      }
    }
    if( strBufCmd.length() > 0)
    {
      commandProcessor.addCommand( strBufCmd.toString() );
      strBufCmd.delete(0, strBufCmd.length());
      commandProcessor.processLine( commandProcessor.getCommandLineContainer(),
          (( String )commandProcessor.getCommands().elementAt( 0 )).trim() );
    }
  }
}
