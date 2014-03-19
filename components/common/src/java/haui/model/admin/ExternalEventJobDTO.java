/* *****************************************************************
 * Project: common
 * File:    ExternalEventJobDTO.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.admin;

import haui.model.execution.AbstractEventDTO;
import haui.util.ReflectionUtil;

/**
 * ExternalEventJobDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExternalEventJobDTO extends AdminJobDTO
{
  static final long serialVersionUID = 1442288689085688191L;

  private AbstractEventDTO event;

  public ExternalEventJobDTO(String code, String name, AbstractEventDTO executionEvent)
  {
    super(code, name);
    this.event = executionEvent;
  }

  public ExternalEventJobDTO(AbstractEventDTO executionEvent)
  {
    super(executionEvent.getExecutionClass(), executionEvent.getEventType());
    this.event = executionEvent;
  }

  @Override
  public String getExecutableIdentifier()
  {
    String id = ReflectionUtil.getShortName(AdminJobDTO.class);
    id += event.getStid();
    return id;
  }
}
