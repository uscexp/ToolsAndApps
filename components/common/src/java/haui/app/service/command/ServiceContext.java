/* *****************************************************************
 * Project: common
 * File:    ServiceContext.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.service.command;

import haui.app.dao.db.AbstractDataSource;
import haui.app.execution.ExecutionManager;
import haui.app.execution.RunAsUOW;
import haui.app.service.lock.Lock;
import haui.app.service.lock.Locker;
import haui.common.id.STIDGenerator;
import haui.exception.AppSystemException;
import haui.model.DataTransferObject;
import haui.model.admin.ServiceContextReportDTO;
import haui.model.db.ISession;
import haui.model.partner.UserDTO;
import haui.util.DateUtil;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a unique ServiceContext for every thread executing a service request.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ServiceContext
{

  private static final Log LOG = LogFactory.getLog(ServiceContext.class);

  public static final String STICKY_DATE = "STICKY_DATE";

  public static final String EJB_CONTEXT = "ejbContext";

  public static final String TRANSACTION = "Transaction";

  public static final String TRANSACTION_ID = "TransactionId";

  public static final String PRINCIPAL = "PRINCIPAL";

  public static final String THREAD_NAME = "THREAD_NAME";

  public static final String SERVICE = "ServiceURI";

  private static final String REQUEST_ID = "RequestId";

  private static final String VALIDATION_CONTEXT = "VALIDATION_CONTEXT";

  private static final String POSTPONED_EXECUTIONS = "POSTPONED_EXECUTIONS";

  private static final String USER = "User";

  public static final String REGISTERED_LOCKERS = "REGISTERED_LOCKERS";

  // thread local holding a service context for every thread
  private static final ThreadLocal threadLocal = new ThreadLocal();

  // holds all currently existing service contexts
  public static final Set ALL_SERVICE_CONTEXTS = Collections.synchronizedSet(new HashSet());

  // the parent of this context... it is set when a thrad spawns threads
  // within the execution of a service (see ArteExecutionManager and related
  // classes)
  private ServiceContext parent = null;

  // the timestamp when this context was created
  private final Date creationTime;

  private final Map map = new HashMap();

  private ServiceContext()
  {
    creationTime = DateUtil.getCurrentTimeStamp();
  }

  public ServiceContext getParent()
  {
    return parent;
  }

  public static final ServiceContext getServiceContext()
  {
    return (ServiceContext)threadLocal.get();
  }

  /**
   * Checks if there is still a ServiceContext on the stack.
   * 
   * @return <code>true</code> if there is still one, otherwise <code>false</code>
   */
  public final static boolean exists()
  {
    return threadLocal.get() != null;
  }

  /**
   * Creates the {@link ServiceContext} for this {@link Thread} using a parent
   * {@link ServiceContext}.
   * <p>
   * This is used if {@link Thread#currentThread()} is logically a child of the {@link Thread} of
   * the parent context.
   * <p>
   * This is used in the {@link ExecutionManager} (job execution).
   * 
   * @param parent
   */
  public static final synchronized void create(ServiceContext parent)
  {
    Principal principal = (Principal)parent.getMap().get(PRINCIPAL);
    String serviceURI = (String)parent.getMap().get(SERVICE);
    Date stickyDate = (Date)parent.getMap().get(STICKY_DATE);

    create(principal, serviceURI, stickyDate, parent);
  }

  /**
   * Initialize a new ServiceContext and add it to the ThreadLocal
   */
  public final synchronized static void create(final Principal principal, final String serviceURI) throws IllegalStateException
  {
    create(principal, serviceURI, new Date(), null);
  }

  /**
   * Initialize a new ServiceContext and add it to the ThreadLocal
   */
  private final synchronized static void create(final Principal principal, final String serviceURI, Date stickyDate, ServiceContext parent)
      throws IllegalStateException
  {
    // verify that this thread has not yet used the ServiceContext without
    // calling create.
    ServiceContext context = getServiceContext();
    if(context == null)
    {
      context = new ServiceContext();
      context.parent = parent;
      context.setPrincipal(principal);
      context.getMap().put(SERVICE, serviceURI);
      context.getMap().put(THREAD_NAME, Thread.currentThread().toString());
      context.getMap().put(STICKY_DATE, stickyDate);
      context.getMap().put(POSTPONED_EXECUTIONS, new ArrayList());
      threadLocal.set(context);
      ALL_SERVICE_CONTEXTS.add(context);

      // propagate the sticky date to date util
      DateUtil.setStickyDate(stickyDate);
    }
    else
    {
      throw new IllegalStateException("The service context was already created for this thread!");
    }
  }

  /**
   * Sets the user.
   * 
   * @param user
   */
  public final synchronized static void setUser(final UserDTO user)
  {
    setUserStid(user == null ? null : user.getStid());
  }

  public final synchronized static void setUserStid(final String stid)
  {
    ServiceContext context = getServiceContext();
    if(context != null)
    {
      context.getMap().put(USER, stid);
    }
  }

  /**
   * Return the current user associated within this service call.
   * 
   * @return a UserDTO.
   */
  public final static UserDTO getUser()
  {
    String stid = getUserStid();
    if(stid == null)
      return null;
//    return (UserDTO)DefaultDataSource.getInstance().getSession().load(UserDTO.class, stid);
    return null; // FIXME return a user
  }

  public final static String getUserStid()
  {
    return (String)get(USER);
  }

  /**
   * Add locker. Every locker which is created within a service is registered by the service
   * context. This info is used to exclude the service itself from the lock!
   * 
   * @param user
   */
  public final static boolean addLocker(Locker locker)
  {
    checkExists();
    Set lockers = (Set)get(REGISTERED_LOCKERS);
    if(lockers == null)
    {
      lockers = new HashSet();
      put(REGISTERED_LOCKERS, lockers);
    }
    return lockers.add(locker);
  }

  /**
   * Remove locker.
   * 
   * @param locker
   */
  public final static boolean removeLocker(Locker locker)
  {
    checkExists();
    ServiceContext context = getServiceContext();
    if(context != null)
    {
      Set lockers = (Set)get(REGISTERED_LOCKERS);
      return lockers.remove(locker);
    }

    return false;
  }

  /**
   * Check if the service context (or one of it's parents) contains the given {@link Locker}
   * 
   * @param locker
   */
  public final static boolean ownsLocker(Locker locker)
  {
    checkExists();
    ServiceContext context = getServiceContext();

    while(context != null)
    {
      Set lockers = (Set)context.getMap().get(REGISTERED_LOCKERS);
      if(lockers != null && lockers.contains(locker))
      {
        return true;
      }

      context = context.parent;
    }
    return false;
  }

  /**
   * Answers a {@link Set} containing all {@link Locker}s owned by this context
   * 
   * @param locker
   */
  public final static Set getLockers()
  {
    checkExists();
    return (Set)get(REGISTERED_LOCKERS);
  }

  /**
   * Sets the {@link Locker}s
   * 
   * @param lockers
   */
  public final static void setLockers(Set lockers)
  {
    put(REGISTERED_LOCKERS, lockers);
  }

  private static final void cleanUpLockers()
  {
    Set lockers = (Set)get(REGISTERED_LOCKERS);
    if(lockers != null)
    {
      // first 'forget' the lockers set
      // because Lock#removeLocker calls back to service context...
      remove(REGISTERED_LOCKERS);
      Iterator lockerIterator = lockers.iterator();
      while(lockerIterator.hasNext())
      {
        Locker locker = (Locker)lockerIterator.next();
        Lock.getInstance().removeLocker(locker);
      }
    }
  }

  public static synchronized final boolean isAlive(ServiceContext context)
  {
    return ALL_SERVICE_CONTEXTS.contains(context);
  }

  /**
   * Destroy the ServiceContext for this Thread context.
   */
  public final static void destroy() throws IllegalStateException
  {
    if(!exists())
    {
      return;
    }
    ServiceContext context = getServiceContext();

    // execute all postponed services
    if(!ServiceContext.getPostponedExecutions().isEmpty())
    {
      ExecutionManager.instance().executeAsynchron(new TriggerPostponedExecutionTask(ServiceContext.getPostponedExecutions()));
    }

    cleanUpLockers();
    checkSessions();
    ALL_SERVICE_CONTEXTS.remove(context);
    threadLocal.set(null);
    // clear the sticky date on date util
    DateUtil.setStickyDate(null);
  }

//  private static void checkHibernateSessions()
//  {
//    Session[] sessions = getSessions();
//    for(int n = 0; n < sessions.length; n++)
//    {
//      Session session = sessions[n];
//      if(session != null && session.isOpen())
//      {
//        throw new IllegalStateException("All Hibernate sessions must be closed befor destroying the ServiceContext.");
//      }
//    }
//  }

  private static void checkSessions()
  {
    ISession[] sessions = getSessions();
    for(int n = 0; n < sessions.length; n++)
    {
      ISession session = sessions[n];
      if(session != null && session.isOpen())
      {
        throw new IllegalStateException("All Hibernate sessions must be closed befor destroying the ServiceContext.");
      }
    }
  }

  private static ISession[] getSessions()
  {
    checkExists();
    Set set = getServiceContext().getMap().keySet();
    List list = new ArrayList(set.size());
    for(Iterator it = set.iterator(); it.hasNext();)
    {
      Object o = it.next();
      if(!(o instanceof ISession))
        continue;
      list.add(o);
    }
    return (ISession[])list.toArray(new ISession[list.size()]);
  }

  /**
   * Return the value of the principal object.
   * 
   * @return a PrincipalObject.
   */
  public final static Principal getPrincipal()
  {
    return (Principal)get(PRINCIPAL);
  }

  /**
   * Set the value of the principal object.
   */
  protected final void setPrincipal(Principal principal)
  {
    // setzen des java.security.Principal darf nur einmal möglich sein. aber
    // für die testcases muss dies auch gehen!!! (Nov 1, 2004, O. Nautsch)
    // Solved as a ServiceContext can be destroyed for a certain Thread.
    // (Nov 9, 2004, Günter Speckhofer)
    if(map.get(PRINCIPAL) != null)
    {
      throw new IllegalStateException("There is already a PrincipalObject with this particular thread associated.");
    }
    map.put(PRINCIPAL, principal);
  }

  // public final static UserDTO getUser() {
  // UserDTO user = (UserDTO) get(USER);
  // if (user != null && user.getUuid() != null &&
  // !DefaultDataSource.getInstance().getSession().contains(user)) {
  // user = (UserDTO) DefaultDataSource.getInstance().getSession().get(UserDTO.class,
  // user.getUuid());
  // }
  // return user;
  // }

  /**
   * Return the value of the request id object. If none has been set each call will return a new
   * requestId.
   * 
   * @return a valuen for the request id.
   */
  public final static String getRequestId()
  {
    String requestId = (String)get(REQUEST_ID);
    if(requestId == null)
    {
      requestId = STIDGenerator.generate();
    }
    return requestId;
  }

  /**
   * Set the value of the request id object.
   * 
   * @param requestId a value for the request id.
   */
  public final static void setRequestId(String requestId)
  {
    put(REQUEST_ID, requestId);
  }

  public final static String getTransactionId()
  {
    String transactionId = (String)get(TRANSACTION_ID);
    if(transactionId == null)
      transactionId = STIDGenerator.generate();
    return transactionId;
  }

  /**
   * Return the value of the Service URI.
   * 
   * @return a Service URI.
   */
  public final static String getServiceURI()
  {
    return (String)get(SERVICE);
  }

  /**
   * Pessimistic locking. Checks if a version is currently locked by another process. Mainly used in
   * iEod for state changes.
   * 
   * @param version
   * @return boolean
   */
  public boolean isLocked(DataTransferObject dto)
  {
    return Lock.getInstance().isLocked(dto);
  }

  /**
   * Set the validationContext
   * 
   * @param validationContext
   */
  public static final void setValidationContext(ServiceValidationContext validationContext)
  {
    put(VALIDATION_CONTEXT, validationContext);
  }

  /**
   * Answers the validation context for this service context
   * 
   * @return the validationContext
   */
  public static final ServiceValidationContext getValidationContext()
  {
    return (ServiceValidationContext)get(VALIDATION_CONTEXT);
  }

  /**
   * Returns the value to which this map maps the specified key. Returns <tt>null</tt> if the map
   * contains no mapping for this key. A return value of <tt>null</tt> does not <i>necessarily </i>
   * indicate that the map contains no mapping for the key; it's also possible that the map
   * explicitly maps the key to <tt>null</tt>. The <tt>containsKey</tt> operation may be used to
   * distinguish these two cases.
   * 
   * @return the value to which this map maps the specified key.
   * @param key key whose associated value is to be returned.
   */
  public final static Object get(String key)
  {
    checkExists();
    ServiceContext context = getServiceContext();
    return context.getMap().get(key);
  }

  /**
   * Associates the specified value with the specified key in this map. If the map previously
   * contained a mapping for this key, the old value will be replaced.
   * 
   * @param key key with which the specified value is to be associated.
   * @param value value to be associated with the specified key.
   * @return previous value associated with specified key, or <tt>null</tt> if there was no mapping
   *         for key. A <tt>null</tt> return can also indicate that the HashMap previously
   *         associated <tt>null</tt> with the specified key.
   * @throws IllegalArgumentException if the key is a reserved key.
   */
  public final static Object put(String key, Object value)
  {
    checkExists();
    checkReservedKeys(key);
    ServiceContext context = getServiceContext();
    return context.getMap().put(key, value);
  }

  /**
   * Removes the mapping for this key from this map if present.
   * 
   * @param key key whose mapping is to be removed from the map.
   * @return previous value associated with specified key, or <tt>null</tt> if there was no mapping
   *         for key. A <tt>null</tt> return can also indicate that the map previously associated
   *         <tt>null</tt> with the specified key.
   * @throws IllegalArgumentException if the key is a reserved key.
   */
  public static final Object remove(String key)
  {
    checkExists();
    checkReservedKeys(key);
    ServiceContext context = getServiceContext();
    return context.getMap().remove(key);
  }

  /**
   * Returns the map holding all SessionContext Informations
   * 
   * @return a map
   */
  public final Map getMap()
  {
    return map;
  }

  private static final void checkExists()
  {
    if(!exists())
      throw new IllegalStateException("The ServiceContext does not exist for this Thread " + Thread.currentThread().getName());
  }

  /**
   * Ensures that no reserved key is used as a key to override the security constraints.
   * 
   * @param key the key to check.
   * @throws IllegalArgumentException if the key is a reserved key.
   */
  protected final static void checkReservedKeys(String key) throws IllegalArgumentException
  {
    if(PRINCIPAL.compareTo(key) == 0)
    {
      throw new IllegalArgumentException("The key " + PRINCIPAL + " is a reserved key! Use another key");
    }
    if(SERVICE.compareTo(key) == 0)
    {
      throw new IllegalArgumentException("The key " + SERVICE + " is a reserved key! Use another key");
    }
  }

  public String toString()
  {
    String msg = super.toString();
    if(map != null)
    {
      String service = (String)map.get(SERVICE);
      if(service != null)
        msg = service;
    }
    msg = msg + " age: " + DateUtil.formatElapsedTime(getAge());
    return msg;
  }

  public Date getCreationTime()
  {
    return creationTime;
  }

  public long getAge()
  {
    return DateUtil.getCurrentTimeStamp().getTime() - getCreationTime().getTime();
  }

  public static final boolean insideTransaction()
  {
    if(get(TRANSACTION) == null)
    {
      return false;
    }
    return true;
  }

  /**
   * @return a list of postponed executions for this thread.
   */
  public static final List getPostponedExecutions()
  {
    return (List)get(POSTPONED_EXECUTIONS);
  }

  public static final void addPostponedExecutions(RunAsUOW runAs)
  {
    ((List)get(POSTPONED_EXECUTIONS)).add(runAs);
  }

  public static final String generateReport()
  {
    try
    {
      StringBuffer report = new StringBuffer();

      ServiceContext[] allContexts = (ServiceContext[])ALL_SERVICE_CONTEXTS.toArray(new ServiceContext[ALL_SERVICE_CONTEXTS.size()]);
      Arrays.sort(allContexts, new Comparator()
      {
        public int compare(Object o1, Object o2)
        {
          return ((ServiceContext)o1).getCreationTime().compareTo(((ServiceContext)o2).getCreationTime());
        }
      });

      report.append("totalServiceContexts: " + (allContexts.length - 1));
      report.append("\n");
      for(int i = 0; i < allContexts.length; i++)
      {
        ServiceContext context = allContexts[i];

        // skip the current service context :-)
        if(context == getServiceContext())
          continue;

        String serviceUri = (String)context.getMap().get(SERVICE);
        Principal principal = (Principal)context.getMap().get(PRINCIPAL);
        String threadName = (String)context.getMap().get(THREAD_NAME);
        ISession[] sessions = getSessions();
        Set locks = (Set)context.getMap().get(REGISTERED_LOCKERS);

        report.append("serviceUri: " + serviceUri + " ");
        report.append("principal: " + principal + " ");
        report.append("threadName: " + threadName + " ");
        for(int n = 0; n < sessions.length; n++)
        {
          report.append("session: " + generateSessionReport(sessions[n]) + " ");
        }

        report.append("locks: " + generateLockReport(locks) + " ");
        report.append("age: " + DateUtil.formatElapsedTime(context.getAge()));

        report.append("\n");

      }
      return report.toString();
    }
    catch(Throwable t)
    {
      throw new AppSystemException("Error generating service context report: " + t.getMessage());
    }
  }

  public static final ServiceContextReportDTO[] generateServiceContextReport()
  {
    ServiceContext[] allContexts = (ServiceContext[])ServiceContext.ALL_SERVICE_CONTEXTS.toArray(new ServiceContext[ALL_SERVICE_CONTEXTS.size()]);
    Arrays.sort(allContexts, new Comparator()
    {
      public int compare(Object o1, Object o2)
      {
        return ((ServiceContext)o1).getCreationTime().compareTo(((ServiceContext)o2).getCreationTime());
      }
    });

    return generateServiceContextReport(allContexts);
  }

  public static final ServiceContextReportDTO[] generateServiceContextReport(ServiceContext[] allContexts)
  {
    try
    {
      ArrayList list = new ArrayList();
      for(int i = 0; i < allContexts.length; i++)
      {
        ServiceContext context = allContexts[i];

        // skip the current service context :-)
        if(context == ServiceContext.getServiceContext())
        {
          continue;
        }
        else
        {
          String serviceUri = (String)context.getMap().get(ServiceContext.SERVICE);
          Principal principal = (Principal)context.getMap().get(ServiceContext.PRINCIPAL);
          String threadName = (String)context.getMap().get(ServiceContext.THREAD_NAME);
          ISession session = (ISession)context.getMap().get(AbstractDataSource.class.getName());
          Set locks = (Set)context.getMap().get(ServiceContext.REGISTERED_LOCKERS);

          ServiceContextReportDTO report = new ServiceContextReportDTO();
          report.setServiceUri(serviceUri);
          report.setPrincipal(principal.toString());
          report.setThreadName(threadName);

//          report.setSessionFlushMode(session.getFlushMode().toString());

          if(context.getParent() != null)
            report.setParentServiceContext((String)context.getParent().getMap().get(ServiceContext.SERVICE));

//          PostProcessObjects po = (PostProcessObjects)context.getMap().get(POST_PROCESS_OBJECTS);
//          if(po != null)
//            report.setNumberOpWritePOs(String.valueOf(po.getTransientObjects().size()));

//          if(session instanceof SessionImpl)
//          {
//            SessionImpl impl = (SessionImpl)session;
//            report.setTotalObjectsInSession(String.valueOf(impl.getPersistenceContext().getEntityEntries().size()));
//            report.setTotalCollectionsInSession(String.valueOf(impl.getPersistenceContext().getCollectionEntries().size()));
//          }
//          else
//          {
            report.setTotalCollectionsInSession(session.toString());
            report.setTotalObjectsInSession(session.toString());
//          }

          report.setOwnedLocks(ServiceContext.generateLockReport(locks));
          report.setAge(DateUtil.formatElapsedTime(context.getAge()));
          report.setCreationTime(DateUtil.format(context.getCreationTime()));
          report.setStickyDate(DateUtil.format((Date)context.getMap().get(ServiceContext.STICKY_DATE)));

          list.add(report);
        }

      }
      return (ServiceContextReportDTO[])list.toArray(new ServiceContextReportDTO[list.size()]);
    }
    catch(Throwable t)
    {
      throw new AppSystemException("Error generating service context report: " + t.getMessage());
    }
  }

  public static String generateLockReport(Set locks)
  {
    if(locks != null)
    {
      StringBuffer lockReport = new StringBuffer();

      Iterator lockIterator = locks.iterator();
      while(lockIterator.hasNext())
      {
        Locker lock = (Locker)lockIterator.next();

        lockReport.append(lock.toString());
        if(lockIterator.hasNext())
        {
          lockReport.append(",");
        }
      }

      return lockReport.toString();
    }
    return "none";
  }

  private static String generateSessionReport(ISession session)
  {
//    if(session instanceof SessionImpl)
//    {
//      SessionImpl impl = (SessionImpl)session;
//      int totalObjects = impl.getPersistenceContext().getEntityEntries().size();
//      int totalCollections = impl.getPersistenceContext().getCollectionEntries().size();
//      return "totalObjects: " + totalObjects + " totalCollections: " + totalCollections + "";
//    }
    return session.toString();
  }
}
