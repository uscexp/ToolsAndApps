/* *****************************************************************
 * Project: common
 * File:    JShellCommandBsh.java
 * 
 * Creation:     18.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.tool.shell.command;

import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellException;
import haui.tool.shell.engine.JShellExecuteException;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.util.HashMap;

/**
 * Module:      JShellCommandBsh<br>
 *<p>
 * Description: JShellCommandBsh<br>
 *</p><p>
 * Created:     18.04.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     18.04.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class JShellCommandBsh extends JShellCommandDefault
{

  public JShellCommandBsh( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "bsh")).getCommandLineContainer());
    setDisplayString("beanshell interpreter");
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
          jse.getOut().print( "bsh expression evaluated");
        }
        else if( blSource)
        {
          jse.getBshInterpreter().source( strArgs[1]);
          iStatus = JShellEnv.OKVALUE;
          jse.getOut().print( "bsh batch executed");
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
    strbufUsage.append( "\n -e\tevaluate a bsh expression");
    strbufUsage.append( "\n -s\tsource a bsh batch from file");

    jse.getOut().println( strbufUsage.toString());
  }

}
