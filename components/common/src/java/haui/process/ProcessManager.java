/* *****************************************************************
 * Project: common
 * File:    ProcessManager.java
 * 
 * Creation:     25.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.process;

import java.util.Iterator;
import java.util.Vector;

/**
 * Module:      ProcessManager<br>
 * <p>
 * Description: ProcessManager<br>
 * </p><p>
 * Created:     25.04.2006 by Andreas Eisenhauer
 * </p><p>
 * @history      25.04.2006 by AE: Created.<br>
 * </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 * </p><p>
 * @version      v0.1, 2006; %version: %<br>
 * </p><p>
 * @since        JDK1.4
 * </p>
 */
public class ProcessManager
{
  
  private final static ProcessManager m_instance = new ProcessManager();

  protected Vector m_processes = new Vector();
  
  private ProcessManager()
  {}
  
  public static ProcessManager getInstance()
  {
    return m_instance;
  }

  public Vector getProcesses()
  {
    return m_processes;
  }
  
  public boolean addProcess( JProcess process)
  {
    return getProcesses().add( process);
  }
  
  public boolean removeProcess( JProcess process)
  {
    return getProcesses().remove( process);
  }
  
  public JProcess findProcess( ThreadGroup threadGroup)
  {
    JProcess foundProcess = null;
    Iterator it = m_processes.iterator();
    
    while( it.hasNext())
    {
      JProcess process = (JProcess)it.next();
      if( process.equals( threadGroup))
      {
        foundProcess = process;
        break;
      }
    }
    return foundProcess;
  }

  public boolean kill( ThreadGroup threadGroup, int status)
  {
    boolean blSuccess = false;
    JProcess process = findProcess( threadGroup);
    
    if( process != null)
    {
      process.setExitCode( status);
      process.kill();
      blSuccess = true;
    }
    return blSuccess;
  }
}
