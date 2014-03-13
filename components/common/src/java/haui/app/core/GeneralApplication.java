/* *****************************************************************
 * Project: common
 * File:    GeneralApplication.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.core;

import haui.app.execution.AppExecutionManager;
import haui.app.execution.ExecutionManager;
import haui.app.security.GeneralCurrentUserMapper;
import haui.app.security.ICurrentUserMapper;
import haui.app.threading.ThreadingComponent;
import haui.app.threading.impl.plain.PlainThreadingComponentImpl;
import haui.model.core.AbstractMultiApplicationSingleton;
import haui.util.DateUtil;
import haui.util.GlobalApplicationContext;

import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import haui.app.logic.scheduler.AppSchedulerController;
import haui.app.logic.scheduler.SchedulerConstants;

/**
 * App starting point. This class implements a singleton representing the AppServer.
 * <p>
 * Provides accss to shared Resources such as the {@link ThreadingComponent} and the {@link ExecutionManager}.
 * <p>
 * This class is also responsible for the initializatin of the App server.
 * <p>
 * The {@link #init()} method is calles once at server startup (from the JobInvokerServlet init method) in order to initialize the App server.
 * <p>
 * The reason this class is in the common package is that otherwise (in the app) package it would not be visible from the Web Tier (which trggers the initialization).
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class GeneralApplication extends Application
{

  private static final Log LOG = LogFactory.getLog(GeneralApplication.class);

  static
  {
    AbstractMultiApplicationSingleton.setInstance(new GeneralApplication());
  }

  public static final String PROPERTY_KEY_ARCHITECTURE = "app.architecture";
  private static final String PROPERTY_APP_SCHEDULER_ACTIVE = "app.scheduler.active";

  private boolean initialized = false;

  // the shared threading component instance
  private ThreadingComponent threadingComponent = new PlainThreadingComponentImpl();

  // the shared execution manager instance
  private ExecutionManager executionManager;

  // the shared current user mapper instance.
  private ICurrentUserMapper currentUserMapper = null;

  private GeneralApplication()
  {
  }

  /**
   * Initialiizes the APP Server.
   */
  public synchronized void init()
  {

    if(initialized)
    {
      LOG.debug("App.init: ignoring call... application has already been initialized!");
      return;
    }

    // The timezone has always to be CET.
//    TimeZone.setDefault(DateUtil.TIME_ZONE);

    LOG.info("Start initialization of APP ...");
    LOG.info("APP-Java:         " + getOS().getJavaVendor() + " " + getOS().getJavaVersion() + " (class-version: " + getOS().getJavaClassVersion() + ")");
    LOG.info("APP-Java-Home:    " + getOS().getJavaHome());
    LOG.info("APP-User:         " + getOS().getUser());
    LOG.info("APP-User-Home:    " + getOS().getUserHome());
    LOG.info("APP-User-Country: " + getOS().getUserCountry());
    LOG.info("APP-Memory:       " + getOS().getMaxMemory() + " bytes");
    LOG.info("APP-OS:           " + getOS().getName() + " " + getOS().getVersion() + " on " + getOS().getArchitecture() + " architecture");
    LOG.info("APP-Server:       " + getOS().getHostname() + " (" + getOS().getIp() + ")");
    LOG.info("APP-Workdir:      " + getOS().getWorkDir());
    LOG.info("APP-Tmpdir:       " + getOS().getTmpDir());
    LOG.info("APP-Time:         " + DateUtil.getCurrentTimeStamp());
    LOG.info("APP-Timezone:     " + TimeZone.getDefault().getDisplayName() + " (" + TimeZone.getDefault().getID() + ")");
    LOG.info("APP-Locale:       " + Locale.getDefault());

    boolean successful = true;

    successful = loadProperties() && successful;

//    AbstractDataSource.init(HDVDataSource.getInstance()); // TODO set concrete DataSource

    // initialize the threading component
    successful = initializeThreadingComponent() && successful;

    // load the system parameters
    successful = loadSystemParameters() && successful;

    // initialize the execution manager
    successful = initializeExecutionManager() && successful;

    // initialize the job scheduler
    if(GlobalApplicationContext.instance().getApplicationProperties().getProperty(PROPERTY_APP_SCHEDULER_ACTIVE, false))
    {
      successful = initializeAppScheduler() && successful;
    }

    if(successful)
    {
      LOG.info("Finished initialization of application! :-) A P P is up and running!");
    }
    else
    {
      RuntimeException e = new RuntimeException("Initialization of APP failed!");
      LOG.fatal("Finished initialization with errors! :-( A P P not successfully initialized!", e);
      throw e;
    }
    initialized = true;

    System.gc();
  }

  public boolean isInitialized()
  {
    return initialized;
  }

  public synchronized void shutdown()
  {
    if(threadingComponent != null)
    {
      threadingComponent.shutdown();
    }
  }

  public static GeneralApplication getInstance()
  {
    return (GeneralApplication)instance();
  }

  /**
   * Answers the {@link ExecutionManager} instance
   * 
   * @return the {@link ExecutionManager}
   */
  public ExecutionManager getExecutionManager()
  {
    return AppExecutionManager.getInstance();
  }

  public AppRuntimeOS getOS()
  {
    return AppRuntimeOS.getInstance();
  }

  /**
   * Answers the {@link ThreadingComponent} instance.
   * 
   * @return the {@link ThreadingComponent}
   */
  public ThreadingComponent getThreadingComponent()
  {
    return threadingComponent;
  }

  public static boolean loadProperties()
  {
    try
    {
      LOG.info("Loading properties");
//      PropertyUtil.setProperties(PropertyUtil.load(System.getProperty(AppConstants.APP_CONFIG), "/app.properties"));
    }
    catch(Exception e)
    {
      return false;
    }
    return true;
  }

  private boolean initializeThreadingComponent()
  {
    try
    {
      threadingComponent = (ThreadingComponent)new PlainThreadingComponentImpl();
      LOG.info("Threading Component Initialized: " + threadingComponent);
    }
    catch(Exception e)
    {
      LOG.error("Error Initializing Threading Compoment", e);
      return false;
    }
    return true;
  }

  private boolean initializeExecutionManager()
  {
    try
    {
      this.executionManager = AppExecutionManager.getInstance();
      LOG.info("Execution Manager Initialized: " + executionManager);
    }
    catch(Exception e)
    {
      LOG.error("Error Initializing Execution Manager", e);
      return false;
    }
    return true;
  }

  private boolean initializeAppScheduler()
  {
    try
    {
      threadingComponent.fireAndForget(new AppSchedulerController());
      LOG.info("App scheduler initialized... rate: " + SchedulerConstants.INTERVAL_TIME + " ms");
    }
    catch(Exception e)
    {
      LOG.error("Error initializing APP scheduler", e);
      return false;
    }
    return true;
  }

  private boolean loadSystemParameters()
  {
    try
    {
      // TODO add system parameter DTOs and functionality
      // ServiceMethod method = Services.getMethod(ReferenceDataService.class.getName(),
      // "loadSystemParameters", new Class[] {});
      // ServiceController.execute(method, new Object[] {}, new SystemPrincipal());
      // LOG.info("System Parameters Initialized");

    }
    catch(Exception e)
    {
      LOG.error("Erorr Initializing System parameters", e);
      return false;
    }
    return true;
  }

  /**
   * @param principal
   * @return
   */
  public ICurrentUserMapper getCurrentUserMapper()
  {

    if(currentUserMapper == null)
    {
      currentUserMapper = new GeneralCurrentUserMapper();
    }
    return currentUserMapper;
  }
}
