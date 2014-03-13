/* *****************************************************************
 * Project: common
 * File:    Scheduler.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler;

import haui.app.execution.AppExecutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Scheduler
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Scheduler implements SchedulerConstants, DeregisterStrategyCode
{
  private static final Log LOG = LogFactory.getLog(Scheduler.class);

  private static final Scheduler instance = new Scheduler();

  private final List identifiers = new LinkedList();
  private final List events = new LinkedList();
  private final List times = new LinkedList();

  private boolean locked = false;
  private boolean disabled = false;

  private Scheduler()
  {
  }

  public static Scheduler getScheduler()
  {
    return instance;
  }

  public static Scheduler getInstance()
  {
    return instance;
  }

  public static boolean isRunning()
  {
    return instance != null;
  }

  public synchronized boolean register(ISchedulerEvent event)
  {
    if(event == null || event.getIdentifier() == null)
    {
      LOG.error("Event and identifier must not be null!");
      return false;
    }
    if(isLocked())
    {
      // LOG.warn("Schdeduler is locked! Could not register event: " + event.getIdentifier());
      return false;
    }
    if(identifiers.contains(event.getIdentifier()))
    {
      LOG.warn("Event already regsitered - IGNORING command");
      return false;
    }

    try
    {
      event.initialize();
    }
    catch(Throwable t)
    {
      LOG.error("Initialize for event failed! Make sure that exceptions are handled within the method! Event: " + event.getIdentifier(), t);
    }

    addEvent(event);
    LOG.debug("Event registered: " + event.getIdentifier());

    return true;
  }

  public synchronized boolean deregister(String identifier)
  {
    if(identifier == null)
    {
      LOG.error("Identifier must not be null!");
      return false;
    }
    if(isLocked())
    {
      LOG.warn("Schdeduler is locked! Could not deregister event: " + identifier);
      return false;
    }
    // if (!identifiers.contains(identifier)) {
    // LOG.warn("Event is not registered and can therefore not be deregistered! Event: " +
    // identifier);
    // return false;
    // }
    return deregister(getEvent(identifier), MANUALLY);
  }

  public synchronized boolean deregister(ISchedulerEvent event)
  {
    return deregister(event, MANUALLY);
  }

  public synchronized boolean deregister(ISchedulerEvent event, DeregisterStrategyCode code)
  {
    if(event == null || event.getIdentifier() == null)
    {
      LOG.error("Event and identifier must not be null!");
      return false;
    }
    if(isLocked())
    {
      LOG.warn("Schdeduler is locked! Could not deregister event: " + event.getIdentifier());
      return false;
    }
    if(!identifiers.contains(event.getIdentifier()))
    {
      LOG.warn("Event is not registered and can therefore not be deregistered! Event: " + event.getIdentifier());
      return false;
    }
    if(Arrays.asList(event.getDeregisterStrategies()).contains(DeregisterStrategyCode.NOT_DEREGISTRERABLE))
    {
      LOG.error("Event has strategy code NOT_DEREGISTRERABLE and can't be deregistered! Event: " + event.getIdentifier());
      return false;
    }

    code = code == null ? MANUALLY : code;
    removeEvent(event.getIdentifier());
    LOG.debug("Event deregistered: " + event.getIdentifier() + " Code: " + code);

    try
    {
      event.finalise(code);
    }
    catch(Throwable t)
    {
      LOG.error("Finalise for event failed! Make sure that exceptions are handled within the method! Event: " + event.getIdentifier(), t);
    }

    return true;
  }

  private synchronized void removeEvent(String identifier)
  {
    int index = identifiers.indexOf(identifier);
    if(index < 0)
      return;
    identifiers.remove(index);
    events.remove(index);
    times.remove(index);
  }

  private synchronized void addEvent(ISchedulerEvent event)
  {
    identifiers.add(event.getIdentifier());
    events.add(event);
    times.add(new Date());
  }

  // public synchronized void clear() {
  // identifiers.clear();
  // events.clear();
  // times.clear();
  // }

  public Date getRegistrationTime(String identifier)
  {
    if(identifier == null)
      return null;
    int index = identifiers.indexOf(identifier);
    if(index < 0)
      return null;
    return (Date)times.get(index);
  }

  public ISchedulerEvent[] getEventforExecutable(AppExecutable executable)
  {
    if(executable == null)
      return new ISchedulerEvent[0];
    List executables = new ArrayList();
    for(Iterator it = events.iterator(); it.hasNext();)
    {
      ISchedulerEvent event = (ISchedulerEvent)it.next();
      AppExecutable exec = event.getExecutable();
      if(exec.equals(executable))
      {
        executables.add(event);
      }
    }
    return (ISchedulerEvent[])executables.toArray(new ISchedulerEvent[executables.size()]);
  }

  public synchronized void lock(boolean locked)
  {
    this.locked = locked;
  }

  public boolean isLocked()
  {
    return locked;
  }

  public synchronized void disable()
  {
    LOG.warn("Scheduler has been diasbled!");
    this.disabled = true;
  }

  public boolean isDisabled()
  {
    return disabled;
  }

  public boolean isRegistered(String identifier)
  {
    if(identifier == null)
      return false;
    return identifiers.contains(identifier);
  }

  public ISchedulerEvent getEvent(String identifier)
  {
    if(identifier == null)
      return null;
    int index = identifiers.indexOf(identifier);
    if(index < 0)
      return null;
    return (ISchedulerEvent)events.get(index);
  }

  public String[] getIdentifiers()
  {
    return (String[])identifiers.toArray(new String[identifiers.size()]);
  }

  public ISchedulerEvent[] getEvents()
  {
    return (ISchedulerEvent[])events.toArray(new ISchedulerEvent[events.size()]);
  }

  public Date[] getRegistrationTimes()
  {
    return (Date[])times.toArray(new Date[times.size()]);
  }

  public int getPrecisionMillis()
  {
    return INTERVAL_TIME;
  }

  public Date getTime()
  {
    return new Date();
  }

  public long currentTimemillis()
  {
    return System.currentTimeMillis();
  }
}
