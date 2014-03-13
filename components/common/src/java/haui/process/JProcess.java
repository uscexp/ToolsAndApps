/* *****************************************************************
 * Project: common
 * File:    JProcess.java
 * 
 * Creation:     21.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.process;

import haui.tool.shell.engine.JShellEnv;
import haui.util.GlobalApplicationContext;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Module:      JProcess<br>
 *<p>
 * Description: JProcess<br>
 *</p><p>
 * Created:     21.04.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     21.04.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class JProcess
  implements Runnable
{
  /** State constant. */
  public static final int UNSTARTED = 0 ;
  /** State constant. */
  public static final int RUNNING = 1 ;
  /** State constant. */
  public static final int DEAD = 2 ;

  protected int m_iState = UNSTARTED;
  protected int m_iExitCode;
  protected String m_strCallingAppName;
  protected String m_name;
  protected boolean m_blKilling = false;
  protected String[] m_strArgs;
  protected Method m_main;
  protected Thread m_thread;
  /** Threadgroup for threads in this process */
  protected ThreadGroup m_tgProcesses = null;
  
  public JProcess(String name, String [] strArgs, String strCallingAppName) {
    this.m_strArgs = strArgs;
    m_strCallingAppName = strCallingAppName;
    
    try
    {               
      m_name = name;
      if( !(System.getSecurityManager() instanceof ProcessSecurityManager) ) {
        ProcessSecurityManager manager = new ProcessSecurityManager();
        System.setSecurityManager( manager);
      }
      // Class.forName() uses loaded class instead of asking class loader
      String strPath = System.getProperty( "default.classpath");
      URL[] urls = JShellEnv.convertClasspathToUrlArray( strPath);
      ClassLoader classLoader = new URLClassLoader( urls);
      Class klass = classLoader.loadClass (name);
      m_thread = new Thread( m_tgProcesses, this,
          "Main thread for " + m_name);
      m_thread.setContextClassLoader( classLoader);

      Class [] para_types = new Class [] {String [].class};
      m_main = klass.getDeclaredMethod ("main", para_types);
      m_tgProcesses = new ThreadGroup( name);
      m_tgProcesses.setMaxPriority( Thread.MAX_PRIORITY -1);
      
      ProcessManager.getInstance().addProcess( this);
    }
    catch (Exception e)
    {
      e.printStackTrace ();
    }
  }
  
  public ThreadGroup getProcessesThreadGroup()
  {
    return m_tgProcesses;
  }

  public Thread getThread()
  {
    return m_thread;
  }

  public void setThread( Thread thread)
  {
    this.m_thread = thread;
  }

  public void launch()
  {
    try
    {
      //installSystemWrappers();
      m_thread.start();
    }
    catch (Exception e)
    {
      e.printStackTrace ();
    }
  }

  public int getState()
  {
    return m_iState;
  }

  protected synchronized void setState(int state)
  {
    m_iState = state;
    notifyAll();
  }

  public int getExitCode()
  {
    return m_iExitCode;
  }

  public void setExitCode(int code)
  {
    m_iExitCode = code;
  }

  /** Wait for the process to finish.  */
  public synchronized void waitFor()
  {

    while (getState() != DEAD)
      {
        try { wait(); } catch (InterruptedException e) {}
      }
  }

  public static synchronized void installSystemWrappers() {

      System.setOut(
        new PrintStream(new FileOutputStream(FileDescriptor.out), true));
      System.setErr(
        new PrintStream(new FileOutputStream(FileDescriptor.err), true));
      System.setIn(
        new BufferedInputStream(new FileInputStream(FileDescriptor.in)));
  }
 
  public void kill()
  {
    m_blKilling = true;
    getProcessesThreadGroup().stop();
    if( ProcessManager.getInstance().removeProcess( this))
      setState( DEAD);
  }

  public void run () {
    if (m_main == null) return;

    try {
      setState(RUNNING);
      GlobalApplicationContext.instance().registerApplication(m_main.getDeclaringClass());
      m_main.invoke (null, new Object [] {m_strArgs});

    } catch (Exception e) {
      // safely ignored sub vm exit
      GlobalApplicationContext.instance().getErrorPrintStream().println( m_name + " exited.");
      //e.printStackTrace();
    }
  }

  public boolean equals( ThreadGroup tg)
  {
    boolean blEqual = false;
    ThreadGroup threadGroup = getProcessesThreadGroup();
    
    if( threadGroup.hashCode() == tg.hashCode()
        || (threadGroup.getParent() != null && threadGroup.getParent().hashCode() == tg.hashCode()))
      blEqual = true;
    return blEqual;
  }
}

