/* *****************************************************************
 * Project: common
 * File:    JShellExecuteException.java
 * 
 * Creation:     18.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.tool.shell.engine;

/**
 * Module:      JShellExecuteException<br>
 *<p>
 * Description: JShellExecuteException<br>
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
public class JShellExecuteException extends JShellException
{
  
  static final long serialVersionUID = -6470119066098549018L;

  public JShellExecuteException()
  {
  }

  public JShellExecuteException(String strMessage)
  {
    super(strMessage);
  }
}
