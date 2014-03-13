/* *****************************************************************
 * Project: common
 * File:    ExecutionIdentifier.java
 * 
 * Creation:     14.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.execution;

import haui.common.id.STIDGenerator;
import haui.model.DataTransferObject;

/**
 * ExecutionIdentifier
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionIdentifier extends DataTransferObject
{
  static final long serialVersionUID = -5843702858774363777L;

  // pn attributes
  public static final String PN_ID = "id";

  // property members
  private String id;

  /**
   * default constructor
   * 
   */
  public ExecutionIdentifier()
  {
    super();
  }

  public ExecutionIdentifier(String id)
  {
    setId(id);
  }

  // accessor methods

  /**
   * @return the id property
   */
  public final String getId()
  {
    return id;
  }

  /**
   * @param id
   */
  public final void setId(String id)
  {
    this.id = id;
  }

  /**
   * Factory method for {@link ExecutionIdentifier}
   * 
   * @return a new {@link ExecutionIdentifier}
   */
  public static ExecutionIdentifier next()
  {
    ExecutionIdentifier instance = new ExecutionIdentifier();
    instance.setId(STIDGenerator.generate());
    return instance;
  }

  /**
   * A short description of this {@link ExecutionIdentifier}
   */
  public String getShortDescription()
  {
    return getId();
  }

  public String toString()
  {
    return getId();
  }

  /**
   * Equals implementation based on {@link #id}
   */
  public boolean equals(Object obj)
  {
    if(obj == null || obj.getClass() != getClass())
    {
      return false;
    }

    ExecutionIdentifier that = (ExecutionIdentifier)obj;
    if(getId() == null || that.getId() == null)
    {
      return super.equals(obj);
    }

    return getId().equals(that.getId());
  }

  public int hashCode()
  {
    if(getId() != null)
      return getId().hashCode();
    return super.hashCode();
  }
}
