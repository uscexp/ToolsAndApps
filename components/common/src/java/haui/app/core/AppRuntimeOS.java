/* *****************************************************************
 * Project: common
 * File:    AppRuntimeOS.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.core;

import haui.model.core.AbstractMultiApplicationSingleton;
import haui.model.core.MultiApplicationSingleton;
import haui.util.AppReflectionToStringBuilder;

import java.io.File;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.util.Date;

/**
 * AppRuntimeOS
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AppRuntimeOS extends AbstractMultiApplicationSingleton
{

  public static final String LINE_SEPARATOR = System.getProperty("line.separator");
  public static final String PATH_SEPARATOR = System.getProperty("path.separator");

  public static final String NA = "n/a";

  private final Date startup = new Date();
  private final String name;

  static
  {
    AbstractMultiApplicationSingleton.setInstance(new AppRuntimeOS());
  }
  
  private AppRuntimeOS()
  {
    String name = System.getProperty("os.name");
    this.name = name == null ? "" : name.trim();
  }

  public static AppRuntimeOS getInstance()
  {
    return (AppRuntimeOS)AbstractMultiApplicationSingleton.instance();
  }

  public static AppRuntimeOS getRuntimeOS()
  {
    return getInstance();
  }

  public String getHostname()
  {
    try
    {
      return Inet4Address.getLocalHost().getCanonicalHostName();
    }
    catch(Exception e)
    {
      return NA;
    }
  }

  public String getIp()
  {
    try
    {
      return Inet4Address.getLocalHost().getHostAddress();
    }
    catch(Exception e)
    {
      return NA;
    }
  }

  public String getName()
  {
    return name;
  }

  public Date getStartupDate()
  {
    return startup;
  }

  public String getVersion()
  {
    String version = System.getProperty("os.version");
    return version == null ? NA : version.trim();
  }

  public String getArchitecture()
  {
    String arch = System.getProperty("os.arch");
    return arch == null ? NA : arch.trim();
  }

  public String getJavaVendor()
  {
    String vendor = System.getProperty("java.vendor");
    return vendor == null ? NA : vendor.trim();
  }

  public String getUser()
  {
    String user = System.getProperty("user.name");
    return user == null ? NA : user.trim();
  }

  public String getUserHome()
  {
    String home = System.getProperty("user.home");
    return home == null ? NA : home.trim();
  }

  public String getUserCountry()
  {
    String home = System.getProperty("user.country");
    return home == null ? NA : home.trim();
  }

  public String getJavaHome()
  {
    String home = System.getProperty("java.home");
    return home == null ? NA : home.trim();
  }

  public String getJavaVersion()
  {
    String jversion = System.getProperty("java.version");
    return jversion == null ? NA : jversion.trim();
  }

  public String getJavaClassVersion()
  {
    String jclassversion = System.getProperty("java.class.version");
    return jclassversion == null ? NA : jclassversion.trim();
  }

  public String getTmpDir()
  {
    String tmpdir = System.getProperty("java.io.tmpdir");
    return tmpdir == null ? NA : tmpdir.trim();
  }

  public String getWorkDir()
  {
    File file = new File(".");
    try
    {
      return file.getCanonicalPath();
    }
    catch(Exception e)
    {}
    return file.getAbsolutePath();
  }

  public boolean isWindows()
  {
    if(name.equals(""))
      return false;
    // return name.toLowerCase().indexOf("win") > -1;
    return name.toLowerCase().startsWith("win");
  }

  public boolean isUnix()
  {
    if(name.equals(""))
      return false;
    return !isWindows();
  }

  public boolean isUnknown()
  {
    return name.equals("");
  }

  public String getLineSeparator()
  {
    return AppRuntimeOS.LINE_SEPARATOR;
  }

  public String getPathSeparator()
  {
    return AppRuntimeOS.PATH_SEPARATOR;
  }

  public long getMaxMemory()
  {
    return Runtime.getRuntime().maxMemory();
  }

  public long getTotalMemory()
  {
    return Runtime.getRuntime().totalMemory();
  }

  public long getUsedMemory()
  {
    Runtime r = Runtime.getRuntime();
    return r.totalMemory() - r.freeMemory();
  }

  public long getFreeMemory()
  {
    return Runtime.getRuntime().freeMemory();
  }

  public void addShutDownHook(Object object, String methodName)
  {
    if(object == null)
      throw new IllegalArgumentException("Object must not be null");
    if(methodName == null)
      throw new IllegalArgumentException("MethodName must not be null");
    Runtime.getRuntime().addShutdownHook(new ShutDownHook(object, methodName));

  }

  private class ShutDownHook extends Thread
  {
    private final Object object;
    private final String methodName;

    private ShutDownHook(Object object, String methodName)
    {
      this.object = object;
      this.methodName = methodName;
    }

    public void run()
    {
      super.setPriority(Thread.MAX_PRIORITY);
      try
      {
        Method method = object.getClass().getDeclaredMethod(methodName, new Class[0]);
        method.invoke(object, new Object[0]);
      }
      catch(Exception e)
      {
        throw new RuntimeException("Could not invoke method \"" + methodName + "\" on object: " + object, e);
      }
    }
  }

  public String toString()
  {
    return AppReflectionToStringBuilder.toString(this);
  }
}
