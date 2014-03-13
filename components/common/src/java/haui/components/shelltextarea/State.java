/* *****************************************************************
 * Project: common
 * File:    PwdInputState.java
 * 
 * Creation:     29.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.components.shelltextarea;

import java.awt.event.KeyEvent;

/**
 * PatternBox: "State" implementation.
 * <ul>
 *   <li>defines an interface for encapsulating the behavior associated with a particular state of the Context.</li>
 * </ul>
 * 
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public abstract class State
{

  /** 
   * This method changes the state of the given Context parameter.
   */
  protected void changeState( Context context, State state)
  {
    context.changeState( state);
  }

  /** 
   * This abstract method must be implemented by the ConcreteState.
   */
  public abstract void handle( Context context, ShellTextArea shellTextArea, KeyEvent keyEvent);

}
