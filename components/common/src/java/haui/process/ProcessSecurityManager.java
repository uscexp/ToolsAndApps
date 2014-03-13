/* *****************************************************************
 * Project: common
 * File:    ProcessSecurityManager.java
 * 
 * Creation:     03.05.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.process;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * Module:      ProcessSecurityManager: A dummy security manager for JProcess.<br>
 *<p>
 * Description: ProcessSecurityManager<br>
 *</p><p>
 * Created:     03.05.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     03.05.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class ProcessSecurityManager extends SecurityManager
{

  private static boolean m_blMultiProcess = false;
  
  public static boolean isMultiProcess()
  {
    return m_blMultiProcess;
  }

  public static void setMultiProcess( boolean multiProcess)
  {
    m_blMultiProcess = multiProcess;
  }

  public void checkExit (int status)
  {
    boolean blSuccess = ProcessManager.getInstance().kill( getThreadGroup(), status);
    if( isMultiProcess())
    {
      throw new SecurityException( "Exited normally");
    }
    else
      super.checkExit( status);
  }


  /** Override to disable all permission checkings for JProcess. */
  public void checkPermission( Permission perm)
  {
//    if( !isMultiProcess())
//      super.checkPermission( perm);
  }


  public void checkPackageAccess( String pkg)
  {
    if( !isMultiProcess())
      super.checkPackageAccess( pkg);
  }

  public void checkAccept( String host, int port)
  {
    if( !isMultiProcess())
      super.checkAccept( host, port);
  }

  public void checkAccess( Thread t)
  {
    if( !isMultiProcess())
      super.checkAccess( t);
  }

  public void checkAccess( ThreadGroup g)
  {
    if( !isMultiProcess())
      super.checkAccess( g);
  }

  public void checkAwtEventQueueAccess()
  {
    if( !isMultiProcess())
      super.checkAwtEventQueueAccess();
  }

  public void checkConnect( String host, int port, Object context)
  {
    if( !isMultiProcess())
      super.checkConnect( host, port, context);
  }

  public void checkConnect( String host, int port)
  {
    if( !isMultiProcess())
      super.checkConnect( host, port);
  }

  public void checkCreateClassLoader()
  {
    if( !isMultiProcess())
      super.checkCreateClassLoader();
  }

  public void checkDelete( String file)
  {
    if( !isMultiProcess())
      super.checkDelete( file);
  }

  public void checkExec( String cmd)
  {
    if( !isMultiProcess())
      super.checkExec( cmd);
  }

  public void checkLink( String lib)
  {
    if( !isMultiProcess())
      super.checkLink( lib);
  }

  public void checkListen( int port)
  {
    if( !isMultiProcess())
      super.checkListen( port);
  }

  public void checkMemberAccess( Class clazz, int which)
  {
    if( !isMultiProcess())
      super.checkMemberAccess( clazz, which);
  }

  public void checkMulticast( InetAddress maddr, byte ttl)
  {
    if( !isMultiProcess())
      super.checkMulticast( maddr, ttl);
  }

  public void checkMulticast( InetAddress maddr)
  {
    if( !isMultiProcess())
      super.checkMulticast( maddr);
  }

  public void checkPackageDefinition( String pkg)
  {
    if( !isMultiProcess())
      super.checkPackageDefinition( pkg);
  }

  public void checkPermission( Permission perm, Object context)
  {
    if( !isMultiProcess())
      super.checkPermission( perm, context);
  }

  public void checkPrintJobAccess()
  {
    if( !isMultiProcess())
      super.checkPrintJobAccess();
  }

  public void checkPropertiesAccess()
  {
    if( !isMultiProcess())
      super.checkPropertiesAccess();
  }

  public void checkPropertyAccess( String key)
  {
    if( !isMultiProcess())
      super.checkPropertyAccess( key);
  }

  public void checkRead( FileDescriptor fd)
  {
    if( !isMultiProcess())
      super.checkRead( fd);
  }

  public void checkRead( String file, Object context)
  {
    if( !isMultiProcess())
      super.checkRead( file, context);
  }

  public void checkRead( String file)
  {
    if( !isMultiProcess())
      super.checkRead( file);
  }

  public void checkSecurityAccess( String target)
  {
    if( !isMultiProcess())
      super.checkSecurityAccess( target);
  }

  public void checkSetFactory()
  {
    if( !isMultiProcess())
      super.checkSetFactory();
  }

  public void checkSystemClipboardAccess()
  {
    if( !isMultiProcess())
      super.checkSystemClipboardAccess();
  }

  public boolean checkTopLevelWindow( Object window)
  {
    if( !isMultiProcess())
      return super.checkTopLevelWindow( window);
    else
      return true;
  }

  public void checkWrite( FileDescriptor fd)
  {
    if( !isMultiProcess())
      super.checkWrite( fd);
  }

  public void checkWrite( String file)
  {
    if( !isMultiProcess())
      super.checkWrite( file);
  }
}
