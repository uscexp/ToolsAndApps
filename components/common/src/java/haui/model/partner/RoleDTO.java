/* *****************************************************************
 * Project: common
 * File:    RoleDTO.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.partner;

import haui.model.DataTransferObject;
import haui.util.AppUtil;

/**
 * RoleDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class RoleDTO extends DataTransferObject
{
  static final long serialVersionUID = -4691572326707433089L;
  
  // pn attributes
  public static final String PN_CODE = "code";
  public static final String PN_NAME = "name";

  // property members
  private String code;
  private String name;

  /**
   * Creates an object of type <code>RoleDTO</code>.
   * 
   */
  public RoleDTO()
  {
    super();
  }

  /**
   * Creates an object of type <code>RoleDTO</code>.
   * 
   */
  public RoleDTO(String code)
  {
    this();
    setCode(code);
  }

  // accessor methods

  /**
   * // TODO insert comment into xml for [PersistentProperty code]
   * 
   * @return the code property
   */
  public final String getCode()
  {
    return code;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty code]
   * 
   */
  public final void setCode(String code)
  {
    this.code = code;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty name]
   * 
   * @return the name property
   */
  public final String getName()
  {
    return name;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty name]
   * 
   */
  public final void setName(String name)
  {
    this.name = name;
  }

  /**
   * Implements equals based on the code of this class.
   */
  public boolean equals(Object obj)
  {
    if(obj == null || !(obj instanceof RoleDTO))
    {
      return false;
    }
    RoleDTO rhs = (RoleDTO)obj;
    return AppUtil.equals(rhs.getCode(), getCode());
  }

  /**
   * Implements hashCode based on the code of this class.
   */
  public int hashCode()
  {
    return AppUtil.hashCode(getCode());
  }
}
