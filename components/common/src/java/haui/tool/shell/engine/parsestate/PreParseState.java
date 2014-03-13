/* *****************************************************************
 * Project: common
 * File:    PreParseState.java
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
 * PreParseState
 * <ul>
 *   <li>defines an interface for encapsulating the behavior associated with a particular state of the PreParseContext.</li>
 * </ul>
 * 
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public abstract class PreParseState
{

  /** 
   * This method changes the state of the given Context parameter.
   */
  protected void changeState( PreParseContext context, PreParseState state)
  {
    context.changeState( state);
  }

  /**
   * This abstract method must be implemented by the ConcreteState.
   * 
   * @param context PreParseContext
   * @param commandProcessor JShellCommandProcessor
   * @param strBufCmd StringBuffer for the commnad
   * @param stMask StringTokenizer of the command line
   */
  public abstract void handle( PreParseContext context, JShellCommandProcessor commandProcessor, StringBuffer strBufCmd, StringTokenizer stMask);

}
