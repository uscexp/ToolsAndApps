/* *****************************************************************
 * Project: common
 * File:    PreParseContext.java
 * 
 * Creation:     03.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.tool.shell.engine.parsestate;

import haui.tool.shell.engine.JShellCommandProcessor;
import java.util.StringTokenizer;

/**
 * PreParseContext <ul> <li>defines the interface of interest to clients.</li> <li>maintains an instance of a PreParseState implementation that defines the current state.</li> </ul>
 * @author  <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class PreParseContext
{

  /** stores the associated State instance */
  private PreParseState fState;

  /** 
   * Constructor
   */
  public PreParseContext( PreParseState state)
  {
    super();
    fState = state;
  }

  /** 
   * The PreParseContext object delegates all state-specific requests to its
   * associated State instance.
   * 
   * @param commandProcessor JShellCommandProcessor
   * @param strBufCmd StringBuffer for the commnad
   * @param stMask StringTokenizer of the command line
   */
  public void request( JShellCommandProcessor commandProcessor, StringBuffer strBufCmd, StringTokenizer stMask)
  {
    fState.handle( this, commandProcessor, strBufCmd, stMask);
  }

  /** 
   * This method changes the state of the Context instance.
   */
  public void changeState( PreParseState state)
  {
    fState = state;
  }

}
