/* *****************************************************************
 * Project: common
 * File:    JShellCommandSpl.java
 * 
 * Creation:     15.08.2007 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2007 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.tool.shell.command;

import java.util.HashMap;

import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellException;
import haui.tool.shell.engine.JShellExecuteException;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

/**
 * Module:      JShellCommandSpl<br>
 *<p>
 * Description: JShellCommandSpl<br>
 *</p><p>
 * Created:     15.08.2007 by Andreas Eisenhauer
 *</p><p>
 * @history     15.08.2007 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2007; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class JShellCommandSpl extends JShellCommandDefault
{

  public JShellCommandSpl(JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "spl")).getCommandLineContainer());
    setDisplayString("simple programming language interpreter");
  }

  public int processCommand( JShellEnv jse, CommandLineContainer clc) throws JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    try
    {
      HashMap hmOptions = clc.getOptions();
      String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
      boolean blEval = false;
      boolean blSource = false;
      if( (hmOptions.size() == 0 || hmOptions.size() == 1))
      {
        if( hmOptions.get( "-e") != null && strArgs != null)
          blEval = true;
        if( hmOptions.get( "-s") != null && strArgs != null)
          blSource = true;
        if( blEval)
        {
          jse.getBshInterpreter().eval( strArgs[0]);
          iStatus = JShellEnv.OKVALUE;
          jse.getOut().print( "spl expression evaluated");
        }
        else if( blSource)
        {
          jse.getBshInterpreter().source( strArgs[1]);
          iStatus = JShellEnv.OKVALUE;
          jse.getOut().print( "spl batch executed");
        }
//        else
//        {
//          Thread thread = new Thread( jse.getInteractiveBshInterpreter());
//          thread.start();
//        }
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
      throw new JShellExecuteException( ex.getMessage());
    }

    return iStatus;
  }

  public void usage( JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " [-e] <expression>\n");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " [-s] <file>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n -e\tevaluate a spl expression");
    strbufUsage.append( "\n -s\tsource a spl batch from file");

    jse.getOut().println( strbufUsage.toString());
  }

}
