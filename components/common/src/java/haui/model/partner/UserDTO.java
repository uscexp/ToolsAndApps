/* *****************************************************************
 * Project: common
 * File:    UserDTO.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.partner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import haui.model.DataTransferObject;

/**
 * UserDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class UserDTO extends DataTransferObject
{
  static final long serialVersionUID = 8815388995144817023L;
  
  // pn attributes
  public static final String PN_EMPLOYEE_NUMBER = "employeeNumber";
  public static final String PN_EMAIL = "email";
  public static final String PN_ACTIVATION = "activation";
  public static final String PN_ROLES = "roles";
  public static final String PN_PRIMARY_UNIT = "primaryUnit";
  public static final String PN_NAME = "name";
  public static final String PN_DEACTIVATION = "deactivation";

  // property members
  private String employeeNumber;
  private String email;
  private Date activation;
  private Set roles = new HashSet();
  private String primaryUnit;
  private String name;
  private Date deactivation;

  /**
   * default constructor
   * 
   */
  public UserDTO()
  {
    super();
  }

  /**
   * Creates a new user with the given employee number.
   * 
   * @param employeeNumber the users unique employee number
   */
  public UserDTO(String employeeNumber)
  {
    super();
    setEmployeeNumber(employeeNumber);
  }

  // accessor methods

  /**
   * // TODO insert comment into xml for [PersistentProperty employeeNumber]
   * 
   * @return the employeeNumber property
   */
  public final String getEmployeeNumber()
  {
    return employeeNumber;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty employeeNumber]
   * 
   */
  public final void setEmployeeNumber(String employeeNumber)
  {
    this.employeeNumber = employeeNumber;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty email]
   * 
   * @return the email property
   */
  public final String getEmail()
  {
    return email;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty email]
   * 
   */
  public final void setEmail(String email)
  {
    this.email = email;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty activation]
   * 
   * @return the activation property
   */
  public final Date getActivation()
  {
    return activation;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty activation]
   * 
   */
  public final void setActivation(Date activation)
  {
    this.activation = activation;
  }

  /**
   * // TODO insert comment into xml for [PersistentManyToMany roles]
   * 
   * @return the roles property (Set of com.ubs.arte.common.model.partner.RoleDTO)
   */
  public final Set getRoles()
  {
    return roles;
  }

  /**
   * // TODO insert comment into xml for [PersistentManyToMany roles] (Set of
   * com.ubs.arte.common.model.partner.RoleDTO)
   */
  public final void setRoles(Set roles)
  {
    this.roles = roles;
  }

  /**
   * // TODO insert comment into xml for [PersistentManyToMany roles] reverse relationsip name: null
   * (Set of com.ubs.arte.common.model.partner.RoleDTO)
   */
  public final boolean addRole(RoleDTO role)
  {
    return this.roles.add(role);
  }

  /**
   * // TODO insert comment into xml for [PersistentManyToMany roles] (Set of
   * com.ubs.arte.common.model.partner.RoleDTO)
   */
  public final boolean removeRole(RoleDTO role)
  {
    return this.roles.remove(role);
  }

  /**
   * // TODO insert comment into xml for [PersistentManyToOne primaryUnit]
   * 
   * @return the primaryUnit property
   */
  public final String getPrimaryUnit()
  {
    return primaryUnit;
  }

  /**
   * // TODO insert comment into xml for [PersistentManyToOne primaryUnit]
   * 
   */
  public final void setPrimaryUnit(String primaryUnit)
  {
    this.primaryUnit = primaryUnit;
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
   * // TODO insert comment into xml for [PersistentProperty deactivation]
   * 
   * @return the deactivation property
   */
  public final Date getDeactivation()
  {
    return deactivation;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty deactivation]
   * 
   */
  public final void setDeactivation(Date deactivation)
  {
    this.deactivation = deactivation;
  }
}
