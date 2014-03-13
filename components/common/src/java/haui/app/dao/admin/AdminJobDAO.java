/* *****************************************************************
 * Project: common
 * File:    AdminJobDAO.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.admin;

import haui.app.dao.AppBaseDAO;
import haui.model.admin.AdminJobDTO;
import haui.model.admin.TriggerDTO;
import haui.model.db.ICriteria;
import haui.model.db.ISession;
import haui.model.db.Restrictions;
import haui.model.execution.ExecutionInfoDTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * AdminJobDAO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AdminJobDAO extends AppBaseDAO
{

  /**
   * Retreive the <code>AdminJobDTO</code> given a unique code.
   * 
   * @param name the code of the object
   * @return the <code>AdminJobDTO</code> or <code>null</code>
   */
  public AdminJobDTO findByName(String name)
  {
    return (AdminJobDTO)super.findObject(AdminJobDTO.class, AdminJobDTO.PN_NAME, name);
  }

  /**
   * Retrieve all <code>AdminJobDTO</code> objects.
   * 
   * @return an array of <code>AdminJobDTO</code> objects
   */
  public AdminJobDTO[] findAll()
  {
    List list = super.findAll(AdminJobDTO.class);
    return (AdminJobDTO[])list.toArray(new AdminJobDTO[list.size()]);
  }

  /**
   * Bulk persist a number of new <code>AdminJobDTO</code>s.
   * 
   * @param adminJobs the <code>AdminJobDTO</code>s to save
   */
  public void saveAll(AdminJobDTO[] adminJobs)
  {
    for(int i = 0; i < adminJobs.length; i++)
      super.update(adminJobs[i]);
    // super.save(adminJobs[i]);
  }

  /**
   * Remove a number of <code>AdminJobDTO</code> from persistent storage.
   * 
   * @param adminJobs an array of <code>AdminJobDTO</code>s to be removed
   */
  public void removeAll(AdminJobDTO[] adminJobs)
  {
    if(null == adminJobs)
    {
      return;
    }
    for(int i = 0; i < adminJobs.length; i++)
    {
      remove(adminJobs[i]);
    }
  }

  /**
   * Bulk remove *ALL* <code>AdminJobDTO</code>s from persistent storage.
   * 
   */
  public void removeAll()
  {
    AdminJobDTO[] adminJobs = findAll();
    for(int i = 0; i < adminJobs.length; i++)
      super.remove(adminJobs[i]);
  }

  /**
   * @param job code
   * @return {@link AdminJobDTO}
   */
  public AdminJobDTO findByCode(String jobCode)
  {

    ISession session = getSession();
    ICriteria crit = session.createCriteria(AdminJobDTO.class);
    crit.add(Restrictions.instance().eq(AdminJobDTO.PN_CODE, jobCode));
    return (AdminJobDTO)crit.uniqueResult();
  }

  /**
   * @param job class
   * @return {@link AdminJobDTO}
   */
  public AdminJobDTO findByClassScope(String implementationClass, String scope)
  {

    ISession session = getSession();
    ICriteria crit = session.createCriteria(AdminJobDTO.class);
    crit.add(Restrictions.instance().eq(AdminJobDTO.PN_EXECUTOR_CLASS, implementationClass));
    crit.add(Restrictions.instance().eq(AdminJobDTO.PN_PARAMETER, scope));
    return (AdminJobDTO)crit.uniqueResult();
  }

  /**
   * @param job class
   * @return {@link AdminJobDTO}
   */
  public AdminJobDTO findByClass(String implementationClass)
  {

    ISession session = getSession();
    ICriteria crit = session.createCriteria(AdminJobDTO.class);
    crit.add(Restrictions.instance().eq(AdminJobDTO.PN_EXECUTOR_CLASS, implementationClass));
    return (AdminJobDTO)crit.uniqueResult();
  }

  /**
   * 
   * @param executionInfo
   * @return true if the given {@link ExecutionInfoDTO} represents a {@link AdminJobDTO},
   *         <code>false</code> otherwise
   */
  public static boolean isJob(ExecutionInfoDTO executionInfo)
  {
    return executionInfo.getExecutableId() != null && executionInfo.getExecutableId().startsWith(AdminJobDTO.EXECUTABLE_IDENTIFIER_PREFIX);
  }

  public String convertToString(Set dates)
  {
    StringBuffer result = new StringBuffer("");

    dates = new TreeSet(dates);

    for(Iterator iter = dates.iterator(); iter.hasNext();)
    {
      TriggerDTO date = (TriggerDTO)iter.next();
      result.append(getDateFormat().format(date.getExecutionTime()));

      if(iter.hasNext())
      {
        result.append(", ");
      }
    }
    return result.toString();
  }

  private DateFormat getDateFormat()
  {
    DateFormat format = new SimpleDateFormat("HH:mm");
    format.setLenient(false);
    return format;
  }
}
