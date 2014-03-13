/* *****************************************************************
 * Project: common
 * File:    ServiceContextReportDTO.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.admin;

import haui.model.DataTransferObject;

/**
 * ServiceContextReportDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ServiceContextReportDTO extends DataTransferObject
{
  // pn attributes
  public static final String PN_CREATION_TIME = "creationTime";
  public static final String PN_TOTAL_COLLECTIONS_IN_SESSION = "totalCollectionsInSession";
  public static final String PN_SERVICE_URI = "serviceUri";
  public static final String PN_AGE = "age";
  public static final String PN_STICKY_DATE = "stickyDate";
  public static final String PN_NUMBER_OP_WRITE_P_OS = "numberOpWritePOs";
  public static final String PN_PARENT_SERVICE_CONTEXT = "parentServiceContext";
  public static final String PN_TOTAL_OBJECTS_IN_SESSION = "totalObjectsInSession";
  public static final String PN_THREAD_NAME = "threadName";
  public static final String PN_PRINCIPAL = "principal";
  public static final String PN_SESSION_FLUSH_MODE = "sessionFlushMode";
  public static final String PN_OWNED_LOCKS = "ownedLocks";

  // property members
  private String creationTime;
  private String totalCollectionsInSession;
  private String serviceUri;
  private String age;
  private String stickyDate;
  private String numberOpWritePOs;
  private String parentServiceContext;
  private String totalObjectsInSession;
  private String threadName;
  private String principal;
  private String sessionFlushMode;
  private String ownedLocks;

  public ServiceContextReportDTO()
  {
  }

  // accessor methods

  /**
   * // TODO insert comment into xml for [Property creationTime]
   * 
   * @return the creationTime property
   */
  public final String getCreationTime()
  {
    return creationTime;
  }

  /**
   * // TODO insert comment into xml for [Property creationTime]
   * 
   */
  public final void setCreationTime(String creationTime)
  {
    this.creationTime = creationTime;
  }

  /**
   * The total number of collections in the session
   * 
   * @return the totalCollectionsInSession property
   */
  public final String getTotalCollectionsInSession()
  {
    return totalCollectionsInSession;
  }

  /**
   * The total number of collections in the session
   * 
   */
  public final void setTotalCollectionsInSession(String totalCollectionsInSession)
  {
    this.totalCollectionsInSession = totalCollectionsInSession;
  }

  /**
   * The Service URI
   * 
   * @return the serviceUri property
   */
  public final String getServiceUri()
  {
    return serviceUri;
  }

  /**
   * The Service URI
   * 
   */
  public final void setServiceUri(String serviceUri)
  {
    this.serviceUri = serviceUri;
  }

  /**
   * // TODO insert comment into xml for [Property age]
   * 
   * @return the age property
   */
  public final String getAge()
  {
    return age;
  }

  /**
   * // TODO insert comment into xml for [Property age]
   * 
   */
  public final void setAge(String age)
  {
    this.age = age;
  }

  /**
   * // TODO insert comment into xml for [Property stickyDate]
   * 
   * @return the stickyDate property
   */
  public final String getStickyDate()
  {
    return stickyDate;
  }

  /**
   * // TODO insert comment into xml for [Property stickyDate]
   * 
   */
  public final void setStickyDate(String stickyDate)
  {
    this.stickyDate = stickyDate;
  }

  /**
   * // TODO insert comment into xml for [Property numberOpWritePOs]
   * 
   * @return the numberOpWritePOs property
   */
  public final String getNumberOpWritePOs()
  {
    return numberOpWritePOs;
  }

  /**
   * // TODO insert comment into xml for [Property numberOpWritePOs]
   * 
   */
  public final void setNumberOpWritePOs(String numberOpWritePOs)
  {
    this.numberOpWritePOs = numberOpWritePOs;
  }

  /**
   * // TODO insert comment into xml for [Property parentServiceContext]
   * 
   * @return the parentServiceContext property
   */
  public final String getParentServiceContext()
  {
    return parentServiceContext;
  }

  /**
   * // TODO insert comment into xml for [Property parentServiceContext]
   * 
   */
  public final void setParentServiceContext(String parentServiceContext)
  {
    this.parentServiceContext = parentServiceContext;
  }

  /**
   * The total number of objects in the session
   * 
   * @return the totalObjectsInSession property
   */
  public final String getTotalObjectsInSession()
  {
    return totalObjectsInSession;
  }

  /**
   * The total number of objects in the session
   * 
   */
  public final void setTotalObjectsInSession(String totalObjectsInSession)
  {
    this.totalObjectsInSession = totalObjectsInSession;
  }

  /**
   * 
   * 
   * @return the threadName property
   */
  public final String getThreadName()
  {
    return threadName;
  }

  /**
   * 
   * 
   */
  public final void setThreadName(String threadName)
  {
    this.threadName = threadName;
  }

  /**
   * 
   * 
   * @return the principal property
   */
  public final String getPrincipal()
  {
    return principal;
  }

  /**
   * 
   * 
   */
  public final void setPrincipal(String principal)
  {
    this.principal = principal;
  }

  /**
   * // TODO insert comment into xml for [Property sessionFlushMode]
   * 
   * @return the sessionFlushMode property
   */
  public final String getSessionFlushMode()
  {
    return sessionFlushMode;
  }

  /**
   * // TODO insert comment into xml for [Property sessionFlushMode]
   * 
   */
  public final void setSessionFlushMode(String sessionFlushMode)
  {
    this.sessionFlushMode = sessionFlushMode;
  }

  /**
   * // TODO insert comment into xml for [Property ownedLocks]
   * 
   * @return the ownedLocks property
   */
  public final String getOwnedLocks()
  {
    return ownedLocks;
  }

  /**
   * // TODO insert comment into xml for [Property ownedLocks]
   * 
   */
  public final void setOwnedLocks(String ownedLocks)
  {
    this.ownedLocks = ownedLocks;
  }
}
