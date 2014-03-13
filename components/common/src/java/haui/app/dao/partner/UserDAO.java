/* *****************************************************************
 * Project: common
 * File:    UserDAO.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.partner;

import haui.app.dao.AppBaseDAO;
import haui.app.dao.db.AbstractDataSource;
import haui.app.dao.db.DefaultDataSource;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import haui.app.service.command.ServiceContext;
import haui.exception.AppLogicException;
import haui.model.db.ICriteria;
import haui.model.db.Restrictions;
import haui.model.db.ISession;
import haui.model.partner.UserDTO;
import haui.model.util.SecurityConstants;
import haui.util.DateUtil;

/**
 * UserDAO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class UserDAO extends AppBaseDAO
{

  private Date deactivation = DateUtil.getCurrentDay();

  private static final Map localUuidCache = Collections.synchronizedMap(new HashMap());

  /**
   * @return Returns the filterDeactivated attributes which defines if deactivated reference data
   *         will be returned. Default this filter is set to true.
   */
  public final boolean isFilterDeactivation()
  {
    if(deactivation == null)
    {
      return false;
    }
    else
    {
      return true;
    }
  }

  /**
   * @param deactivation The deactivation date used as filter criteria. <code>null</code> means that
   *          the filter is turned off.
   */
  public final void setDeactivation(Date deactivation)
  {
    this.deactivation = deactivation;
  }

  /**
   * Bulk persist a number of new <code>UserDTO</code>s.
   * 
   * @param users the <code>UserDTO</code>s to save
   */
  public void saveAll(UserDTO[] users)
  {

    for(int i = 0; i < users.length; i++)
      super.saveOrUpdate(users[i]);

  }

  /**
   * Retreive the <code>UserDTO</code> for the current authenticated user, i.e. the caller of this
   * method.
   * <p>
   * This method never returns <code>null</code>.
   * 
   * @return a <code>UserDTO</code> representing to current authenticated user, never
   *         <code>null</code>
   * 
   * @throws AppLogicException if the current authenticated caller could not be established or
   *           no user entry could be found for the current caller
   */
  public UserDTO getCurrentUser() throws AppLogicException
  {
    UserDTO user = getCurrentUser(ServiceContext.getPrincipal());
    AbstractDataSource.instance().loadLazy(user.getRoles());
    return user;
  }

  /**
   * Retreive the <code>UserDTO</code> for the given authenticated user, i.e. the caller of this
   * method.
   * <p>
   * This method never returns <code>null</code>.
   * 
   * 
   * @return a <code>UserDTO</code> representing to current authenticated user, never
   *         <code>null</code>
   * 
   * @throws AppLogicException if the current authenticated caller could not be established or
   *           no user entry could be found for the current caller
   */
  public UserDTO getCurrentUser(Principal principal) throws AppLogicException
  {

    // this should never occur, as the user should have been authenticated
    // before...
    if(principal == null || principal.getName() == null)
      throw new AppLogicException("authentication failed");

    // the principals name represents his employee number
    UserDTO user = find(convert2GPN(principal));

    // might indicate a potential setup problem, i.e. we have an
    // authenticated user but we haven't configured him in our system
    if(user == null)
    {
      throw new AppLogicException("FATAL: User with employee number '" + principal.getName() + "'");
    }
    return user;
  }

  /**
   * This is a workaround to be reconfirmed hwo the standard in WM&BB works.
   * 
   * @param principal
   * @return a GPN number
   */
  protected String convert2GPN(Principal principal)
  {
    // TODO verify what the WAAS plugin sends!
    String name = principal.getName();
    if(name.startsWith("t"))
    {
      return "0" + name.replace('t', '0');
    }
    if(name.startsWith("T"))
    {
      return "0" + name.replace('T', '0');
    }
    return name;
  }

  /**
   * This is a workaround to be reconfirmed how the standard in WM&BB works.
   * 
   * @param gpn GPN number
   * @return Tnumber
   */
  public String convertGPN2TNumber(String gpn)
  {
    String tnumber = gpn;
    if(gpn != null && !(gpn.startsWith("t") || gpn.startsWith("T")) && gpn.length() == 8)
    {
      tnumber = "t" + gpn.substring(2);
    }
    return tnumber;
  }

  /**
   * Retreive the <code>UserDTO</code> given a employee number.
   * 
   * @param employeeNumber the identifier of the object
   * @return the <code>UserDTO</code> or <code>null</code>
   */
  public UserDTO find(String employeeNumber)
  {
    if(null == employeeNumber || "".equals(employeeNumber.trim()))
    {
      return null;
    }

    String stid = (String)localUuidCache.get(employeeNumber);
    if(stid != null)
    {
      return (UserDTO)findByStid(UserDTO.class, stid);
    }
    else
    {
      ISession session = getSession();
      ICriteria crit = session.createCriteria(UserDTO.class);
      crit.setCacheable(true);
      crit.add(Restrictions.instance().eq(UserDTO.PN_EMPLOYEE_NUMBER, employeeNumber));
      UserDTO user = (UserDTO)crit.uniqueResult();
      localUuidCache.put(employeeNumber, user.getStid());
      return user;
    }

  }

  /**
   * Retrieve really all <code>UserDTO</code> objects.
   * 
   * @return an array of <code>UserDTO</code> objects
   */
  public UserDTO[] findReallyAll()
  {
    ICriteria criteria = getSession().createCriteria(UserDTO.class);
    List list = criteria.list();

    UserDTO[] users = (UserDTO[])list.toArray(new UserDTO[list.size()]);
    for(int i = 0; i < users.length; i++)
    {
      localUuidCache.put(users[i].getEmployeeNumber(), users[i].getStid());
    }
    return users;
  }

  /**
   * Retrieve all <code>UserDTO</code> objects.
   * 
   * @return an array of <code>UserDTO</code> objects
   */
  public UserDTO[] findAll()
  {

    ICriteria criteria = getSession().createCriteria(UserDTO.class);
    // Filter the system user
    criteria.add(Restrictions.instance().not(Restrictions.instance().eq(UserDTO.PN_EMPLOYEE_NUMBER, SecurityConstants.SYSTEM_USER_GPN)));
    if(isFilterDeactivation())
    {
      criteria.add(Restrictions.instance().gt(UserDTO.PN_DEACTIVATION, deactivation));
    }
    List list = criteria.list();
    UserDTO[] users = (UserDTO[])list.toArray(new UserDTO[list.size()]);
    for(int i = 0; i < users.length; i++)
    {
      localUuidCache.put(users[i].getEmployeeNumber(), users[i].getStid());
    }
    return users;

  }
}
