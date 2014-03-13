/* *****************************************************************
 * Project: common
 * File:    ApplicationConfiguration.java
 * 
 * Creation:     08.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.core;

import haui.util.AppProperties;

import java.awt.Component;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * ApplicationConfiguration
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ApplicationConfiguration
{

  private String id;
  private AppProperties appProperties = new AppProperties();
  private Component rootComponent;
  private PrintStream outputPrintStream;
  private PrintStream errorPrintStream;
  private Class applicationClass;
  private HashMap<String, MultiApplicationSingleton> singletons = new HashMap<String, MultiApplicationSingleton>();
  
  /**
   * @param id
   */
  public ApplicationConfiguration(String id)
  {
    super();
    this.id = id;
  }

  /**
   * @return the id
   */
  public String getId()
  {
    return id;
  }

  /**
   * @return the appProperties
   */
  public AppProperties getAppProperties()
  {
    return appProperties;
  }

  /**
   * @return the rootComponent
   */
  public Component getRootComponent()
  {
    return rootComponent;
  }

  /**
   * @param rootComponent the rootComponent to set
   */
  public void setRootComponent(Component rootComponent)
  {
    this.rootComponent = rootComponent;
  }

  /**
   * @return the outputPrintStream
   */
  public PrintStream getOutputPrintStream()
  {
    return outputPrintStream;
  }

  /**
   * @param outputPrintStream the outputPrintStream to set
   */
  public void setOutputPrintStream(PrintStream outputPrintStream)
  {
    this.outputPrintStream = outputPrintStream;
  }

  /**
   * @return the errorPrintStream
   */
  public PrintStream getErrorPrintStream()
  {
    return errorPrintStream;
  }

  /**
   * @param errorPrintStream the errorPrintStream to set
   */
  public void setErrorPrintStream(PrintStream errorPrintStream)
  {
    this.errorPrintStream = errorPrintStream;
  }

  /**
   * @return the applicationClass
   */
  public Class getApplicationClass()
  {
    return applicationClass;
  }

  /**
   * @param applicationClass the applicationClass to set
   */
  public void setApplicationClass(Class applicationClass)
  {
    this.applicationClass = applicationClass;
  }

  /**
   * @return the singletons
   */
  public HashMap<String, MultiApplicationSingleton> getSingletons()
  {
    return singletons;
  }

  /**
   * @param singletons the singletons to set
   */
  public void setSingletons(HashMap<String, MultiApplicationSingleton> singletons)
  {
    this.singletons = singletons;
  }

  /**
   * @param key
   * @return
   * @see java.util.HashMap#get(java.lang.Object)
   */
  public MultiApplicationSingleton getSingleton(String singletonId)
  {
    return (MultiApplicationSingleton)singletons.get(singletonId);
  }

  /**
   * @param key
   * @param value
   * @return
   * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
   */
  public Object setSingleton(String singletonId, MultiApplicationSingleton value)
  {
    return singletons.put(singletonId, value);
  }

}
