/* *****************************************************************
 * Project: common
 * File:    ParseState.java
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
 * PatternBox: "State" implementation.
 * <ul>
 *   <li>defines an interface for encapsulating the behavior associated with a particular state of the Context.</li>
 * </ul>
 * 
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public abstract class ParseState
{

  /** 
   * This method changes the state of the given Context parameter.
   */
  protected void changeState( ParseContext context, ParseState state)
  {
    context.changeState( state);
  }

  /** 
   * This abstract method must be implemented by the ConcreteState.
   */
  public abstract void handle( ParseContext context, CommandLineContainer container,
      ProcessLineCache processLineCache);

}
