/* *****************************************************************
 * Project: common
 * File:    GlobalAppProperties.java
 * 
 * Creation:     15.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.util;

import haui.model.core.ApplicationConfiguration;
import haui.model.core.MultiApplicationSingleton;
import haui.process.URLClassLoader;

import java.awt.Component;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * Module:      GlobalAppProperties<br>
 * <p>
 * Description: GlobalAppProperties<br>
 * </p><p>
 * Created:     15.03.2006 by Andreas Eisenhauer
 * </p><p>
 * @history      15.03.2006 by AE: Created.<br>
 * </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 * </p><p>
 * @version      v0.1, 2006; %version: %<br>
 * </p><p>
 * @since        JDK1.4
 * </p>
 */
public class GlobalApplicationContext
{
  private static final long serialVersionUID = -3012892994584017453L;
  
  public final static String ROOT_APP_ID = "rootApplication";

  // constants
  public final static String PROXYENABLE = "proxy.enable";
  public final static String PROXYHOST = "proxy.host";
  public final static String PROXYPORT = "proxy.port";
  public final static String PROXYUSER = "proxy.user";
  public final static String PROXYPASSWORD = "proxy.password";
  public final static String PROXYNOPROXYLIST = "proxy.noProxyList";

  public final static String URLUSER = "url.user";
  public final static String URLPASSWORD = "url.password";
  public final static String LOGONUSER = "cgi.LogonUser";
  public final static String LOGONPASSWORD = "cgi.LogonPassword";

  public final static int ERRORINDEX = -1;
  public final static int ERRORPROPNAME = 1;
  public final static int ERRORFILE = 2;
  public final static int ERROROVERFLOW = 3;
  public final static int OK = 0;

  public static final String SCHEDULER_EXECUTION_TERMINATION_WAITTIME_PROPERTY = "app.scheduler.execution.termination.waittime";
  public static final int SCHEDULER_DEFAULT_EXECUTION_TERMINATION_WAITTIME = 150000;
  public static final String CURRENT_USER_MAPPER_CLASS = "app.current.user.mapper.class";
  public static final String CURRENT_USER_MAPPER_CLASS_DEFAULT_CLASS = "haui.app.security.DefaultCurrentUserMapper";
  public static final String LONG_TRANSACTION_TIMEOUTS = "app.general.tx.long.timeout";
  
  // member variables
  private static GlobalApplicationContext instance = new GlobalApplicationContext();
  
  private AppProperties globalAppProperties = new AppProperties();
  
  private HashMap<String, ApplicationConfiguration> applicationConfigurations = new HashMap<String, ApplicationConfiguration>();
  
  public static GlobalApplicationContext instance()
  {
    return instance;
  }

  /**
   * get {@link ApplicationConfiguration} for the current application
   * 
   * @return {@link ApplicationConfiguration}
   */
  public ApplicationConfiguration getApplicationConfiguration()
  {
    String id = ROOT_APP_ID;
    Thread thread = Thread.currentThread();
    ClassLoader classLoader = thread.getContextClassLoader();
    if(classLoader instanceof URLClassLoader)
    {
      id = ((URLClassLoader)classLoader).getId();
    }
    ApplicationConfiguration applicationConfiguration = getApplicationConfiguration(id);
    if(applicationConfiguration == null)
    {
      applicationConfiguration = new ApplicationConfiguration(id);
      putApplicationConfiguration(id, applicationConfiguration);
    }
    return applicationConfiguration;
  }
  
  public AppProperties getApplicationProperties()
  {
    return getApplicationConfiguration().getAppProperties();
  }

  /**
   * @param key application key
   * @return {@link ApplicationConfiguration}
   */
  protected ApplicationConfiguration getApplicationConfiguration(Object key)
  {
    return applicationConfigurations.get(key);
  }

  /**
   * @param key application key
   * @param value {@link ApplicationConfiguration}
   * @return {@link ApplicationConfiguration}
   */
  protected ApplicationConfiguration putApplicationConfiguration(String key, ApplicationConfiguration value)
  {
    return applicationConfigurations.put(key, value);
  }

  /**
   * @param key application key
   * @return {@link ApplicationConfiguration}
   */
  protected ApplicationConfiguration removeApplicationConfiguration(Object key)
  {
    return applicationConfigurations.remove(key);
  }

  /**
   * @return the globalAppProperties
   */
  public AppProperties getGlobalAppProperties()
  {
    return globalAppProperties;
  }

  public void addApplicationClass(Class cl)
  {
    getApplicationConfiguration().setApplicationClass(cl);
  }

  public Class getApplicationClass()
  {
    return getApplicationConfiguration().getApplicationClass();
  }

  public void setErrorPrintStream(PrintStream ps)
  {
    getApplicationConfiguration().setErrorPrintStream(ps);
  }

  public PrintStream getErrorPrintStream()
  {
    PrintStream ps = getApplicationConfiguration().getErrorPrintStream();
    if( ps == null)
    {
      ps = getApplicationConfiguration().getOutputPrintStream();
      if( ps == null)
        ps = System.err;
    }
    return ps;
  }

  public void setOutputPrintStream(PrintStream ps)
  {
    getApplicationConfiguration().setOutputPrintStream(ps);
  }

  public PrintStream getOutputPrintStream()
  {
    PrintStream ps = getApplicationConfiguration().getOutputPrintStream();
    if( ps == null)
      ps = System.out;
    return ps;
  }

  public void addRootComponent(Component comp)
  {
    getApplicationConfiguration().setRootComponent(comp);
  }

  public Component getRootComponent()
  {
    return getApplicationConfiguration().getRootComponent();
  }
  
  public void registerApplication(Class appClass)
  {
    getApplicationConfiguration().setApplicationClass(appClass);
  }

  public void deregisterApplication()
  {
    removeApplicationConfiguration(getApplicationConfiguration().getId());
  }
  
  public MultiApplicationSingleton getSingletonInstance(String singletonId)
  {
    MultiApplicationSingleton singleton = getApplicationConfiguration().getSingleton(singletonId);
    return singleton;
  }

  public void setSingletonInstance(String singletonId, MultiApplicationSingleton singletonInstance)
  {
    getApplicationConfiguration().setSingleton(singletonId, singletonInstance);
  }

  /*
  public ConnectionManager remoteStore( String urlCgi, String header, Component frame)
  {
    ConnectionManager m_conn;

    m_conn = new ConnectionManager( urlCgi, this);
    if( frame != null)
      m_conn.setFrame( frame);

    // Send data.
    m_conn.openConnection();
    try
    {
      m_conn.getHttpURLConnection().setRequestMethod( "POST");
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    m_conn.getHttpURLConnection().setUseCaches( false);
    m_conn.getHttpURLConnection().setDoOutput(true);
    try
    {
      BufferedOutputStream bos = new BufferedOutputStream( m_conn.getHttpURLConnection().getOutputStream());

      super.store( bos, header);
      bos.close();
    }
    catch( FileNotFoundException fnfex)
    {
      fnfex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    return m_conn;
  }

  public void remoteLoad( String urlCgi, Component frame)
  {
    ConnectionManager m_conn;

    m_conn = new ConnectionManager( urlCgi, this);
    if( frame != null)
      m_conn.setFrame( frame);

    // Send data.
    m_conn.openConnection();
    m_conn.getHttpURLConnection().setUseCaches( false);
    try
    {
      BufferedInputStream bis = new BufferedInputStream( m_conn.getHttpURLConnection().getInputStream());

      super.load( bis);
      bis.close();
    }
    catch( FileNotFoundException fnfex)
    {
      fnfex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    m_conn.disconnect();
  }
*/
}
