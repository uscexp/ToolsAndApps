/* *****************************************************************
 * Project: common
 * File:    PICorrectState.java
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
 * PatternBox: "ConcreteState" implementation.
 * <ul>
 *   <li>each subclass implements a behavior associated with a state of the Context.</li>
 * </ul>
 * 
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class PICorrectState extends State
{

  /** 
   * Default constructor
   */
  public PICorrectState()
  {
    super();
  }

  /** 
   * This method handles a request from a Context instance.
   */
  public void handle( Context context, ShellTextArea shellTextArea, KeyEvent keyEvent)
  {
    super.changeState( context, new PwdInputState());
    context.request( shellTextArea, keyEvent);
  }

}
