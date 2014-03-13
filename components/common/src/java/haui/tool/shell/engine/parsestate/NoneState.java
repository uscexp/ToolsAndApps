/* *****************************************************************
 * Project: common
 * File:    NoneState.java
 * 
 * Creation:     03.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.tool.shell.engine.parsestate;

import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.tool.shell.engine.JShellCommandProcessor.ProcessLineCache;

/**
 * PatternBox: "ConcreteState" implementation.
 * <ul>
 *   <li>each subclass implements a behavior associated with a state of the Context.</li>
 * </ul>
 * 
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class NoneState extends ParseState
{

  /** 
   * Default constructor
   */
  public NoneState()
  {
    super();
  }

  /** 
   * This method handles a request from a Context instance.
   */
  public void handle( ParseContext context, CommandLineContainer container,
      ProcessLineCache processLineCache)
  {
    char c = processLineCache.getCurrentChar();
    switch( c)
    {
      case '\"':
        super.changeState( context, new PureArgState());
        context.request( container, processLineCache);
        break;

      case '\'':
        super.changeState( context, new PureArgState());
        context.request( container, processLineCache);
        break;

      case '&':
        if( processLineCache.getPosition() == processLineCache.getLine().length()-1)
          container.setExecInBackground( true);
        else
        {
          ParseState stateOld = context.getOldState();
          if( context.getOldState() instanceof CommandState
              || context.getOldState() instanceof PureArgState
              || context.getOldState() instanceof OptKeyState
              || context.getOldState() instanceof OptValState)
          {
            super.changeState( context, new PureArgState());
            context.setOldState( stateOld);
            context.request( container, processLineCache);
          }
        }
        break;

      case '-':
        super.changeState( context, new OptKeyState());
        processLineCache.getOptionKey().append( c );
        processLineCache.getOptionKeyValue().append( c );
        break;

      case '=':
        super.changeState( context, new PureArgState());
        processLineCache.getPureArgument().append( c );
        processLineCache.getPureArgumentFull().append( c );
        break;

      default:
        ParseState stateOld = context.getOldState();
        if( context.getOldState() instanceof CommandState
            || context.getOldState() instanceof PureArgState
            || context.getOldState() instanceof OptKeyState
            || context.getOldState() instanceof OptValState)
        {
          super.changeState( context, new PureArgState());
          context.setOldState( stateOld);
          context.request( container, processLineCache);
        }
        break;
    }
  }

}
