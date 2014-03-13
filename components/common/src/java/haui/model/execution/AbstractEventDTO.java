/* *****************************************************************
 * Project: common
 * File:    AbstractEventDTO.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.execution;

import haui.model.DataTransferObject;

import java.util.Date;

/**
 * AbstractEventDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractEventDTO extends DataTransferObject
{
  static final long serialVersionUID = 7290312958011310463L;

  // pn attributes
  public static final String PN_EVENT_TIME_STAMP = "eventTimeStamp";
  public static final String PN_DONE_TIME_STAMP = "doneTimeStamp";
  public static final String PN_EXECUTION_CLASS = "executionClass";
  public static final String PN_EVENT_TYPE = "eventType";

  // property members
  private Date eventTimeStamp;
  private Date doneTimeStamp;
  private String executionClass;
  private String eventType;

  /**
   * default constructor
   * 
   */
  public AbstractEventDTO()
  {
    super();
  }

  // accessor methods

  /**
   * // TODO insert comment into xml for [eventTimeStamp]
   * 
   * @return the eventTimeStamp property
   */
  public final Date getEventTimeStamp()
  {
    return eventTimeStamp;
  }

  /**
   * // TODO insert comment into xml for [eventTimeStamp]
   * 
   */
  public final void setEventTimeStamp(Date eventTimeStamp)
  {
    this.eventTimeStamp = eventTimeStamp;
  }

  /**
   * // TODO insert comment into xml for [doneTimeStamp]
   * 
   * @return the doneTimeStamp property
   */
  public final Date getDoneTimeStamp()
  {
    return doneTimeStamp;
  }

  /**
   * // TODO insert comment into xml for [doneTimeStamp]
   * 
   */
  public final void setDoneTimeStamp(Date doneTimeStamp)
  {
    this.doneTimeStamp = doneTimeStamp;
  }

  /**
   * // TODO insert comment into xml for [executionClass]
   * 
   * @return the executionClass property
   */
  public final String getExecutionClass()
  {
    return executionClass;
  }

  /**
   * // TODO insert comment into xml for [executionClass]
   * 
   */
  public final void setExecutionClass(String executionClass)
  {
    this.executionClass = executionClass;
  }

  /**
   * // TODO insert comment into xml for [eventType]
   * 
   * @return the eventType property
   */
  public final String getEventType()
  {
    return eventType;
  }

  /**
   * // TODO insert comment into xml for [eventType]
   * 
   */
  public final void setEventType(String eventType)
  {
    this.eventType = eventType;
  }
}
