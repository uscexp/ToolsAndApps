/* *****************************************************************
 * Project: common
 * File:    ParseContext.java
 * 
 * Creation:     03.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.tool.shell.engine.parsestate;

import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

/**
 * PatternBox: "Context" implementation. <ul> <li>defines the interface of interest to clients.</li> <li>maintains an instance of a ConcreteState subclass that defines the current state.</li> </ul>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class ParseContext
{

  /** stores the associated State instance */
  private ParseState fState;
  private ParseState fOldState = new NoneState();

  /** 
   * Constructor
   */
  public ParseContext( ParseState state)
  {
    super();
    fState = state;
  }

  public ParseState getOldState()
  {
    return fOldState;
  }

  public void setOldState( ParseState oldState)
  {
    fOldState = oldState;
  }

  /** 
   * The Context object delegates all state-specific requests to its
   * associated State instance.
   */
  public void request( CommandLineContainer container, JShellCommandProcessor.ProcessLineCache processLineCache)
  {
    fState.handle( this, container, processLineCache);
  }

  /** 
   * This method changes the state of the Context instance.
   */
  public void changeState( ParseState state)
  {
    fOldState = fState;
    fState = state;
  }

}
