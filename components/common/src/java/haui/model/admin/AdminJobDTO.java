/* *****************************************************************
 * Project: common
 * File:    AdminJobDTO.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.admin;

import haui.model.DataTransferObject;
import haui.model.execution.ExecutionIdentifier;
import haui.model.execution.ExecutionInfoDTO;
import haui.util.AppUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * AdminJobDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AdminJobDTO extends DataTransferObject
{
  static final long serialVersionUID = -2322014659599361665L;

  // pn attributes
  public static final String PN_MONDAY_EXECUTION = "mondayExecution";
  public static final String PN_EXECUTION_ACTIVE = "executionActive";
  public static final String PN_EXECUTION_INFOS = "executionInfos";
  public static final String PN_WEDNESDAY_EXECUTION = "wednesdayExecution";
  public static final String PN_SUNDAY_EXECUTION = "sundayExecution";
  public static final String PN_EXECUTOR_CLASS = "executorClass";
  public static final String PN_TRIGGERS = "triggers";
  public static final String PN_CODE = "code";
  public static final String PN_TUESDAY_EXECUTION = "tuesdayExecution";
  public static final String PN_THURSDAY_EXECUTION = "thursdayExecution";
  public static final String PN_DESCRIPTION = "description";
  public static final String PN_FRIDAY_EXECUTION = "fridayExecution";
  public static final String PN_NAME = "name";
  public static final String PN_PARAMETER = "parameter";
  public static final String PN_SATURDAY_EXECUTION = "saturdayExecution";

  public static final String EXECUTABLE_IDENTIFIER_PREFIX = "AdminJob_";

  // property members
  private boolean mondayExecution;
  private boolean executionActive = false;
  private Set executionInfos = new HashSet();
  private boolean wednesdayExecution;
  private boolean sundayExecution;
  private String executorClass;
  private Set triggers = new HashSet();
  private String code;
  private boolean tuesdayExecution;
  private boolean thursdayExecution;
  private String description;
  private boolean fridayExecution;
  private String name;
  private String parameter;
  private boolean saturdayExecution;

  // Default constructor
  protected AdminJobDTO()
  {
  }

  public AdminJobDTO(String code, String aName)
  {
    setCode(code);
    setName(aName);
  }

  // accessor methods

  /**
   * // TODO insert comment into xml for [mondayExecution]
   * 
   * @return the mondayExecution property
   */
  public final boolean isMondayExecution()
  {
    return mondayExecution;
  }

  /**
   * // TODO insert comment into xml for [mondayExecution]
   * 
   */
  public final void setMondayExecution(boolean mondayExecution)
  {
    this.mondayExecution = mondayExecution;
  }

  /**
   * Checks if an execution is active. If there is no execution day set the execution is not active
   * too!
   * 
   * @return the executionActive property
   */
  public final boolean isExecutionActive()
  {
    return executionActive;
  }

  /**
   * Checks if an execution is active. If there is no execution day set the execution is not active
   * too!
   * 
   */
  public final void setExecutionActive(boolean executionActive)
  {
    this.executionActive = executionActive;
  }

  /**
   * // TODO insert comment into xml for [ToMany executionInfos]
   * 
   * @return the executionInfos property (Set of
   *         com.ubs.arte.common.model.execution.ExecutionInfoDTO)
   */
  public final Set getExecutionInfos()
  {
    return executionInfos;
  }

  /**
   * // TODO insert comment into xml for [ToMany executionInfos] (Set of
   * com.ubs.arte.common.model.execution.ExecutionInfoDTO)
   */
  public final void setExecutionInfos(Set executionInfos)
  {
    this.executionInfos = executionInfos;
  }

  /**
   * // TODO insert comment into xml for [ToMany executionInfos] reverse relationsip name: null (Set
   * of com.ubs.arte.common.model.execution.ExecutionInfoDTO)
   */
  public final boolean addExecutionInfo(ExecutionInfoDTO executionInfo)
  {
    return this.executionInfos.add(executionInfo);
  }

  /**
   * // TODO insert comment into xml for [ToMany executionInfos] (Set of
   * com.ubs.arte.common.model.execution.ExecutionInfoDTO)
   */
  public final boolean removeExecutionInfo(ExecutionInfoDTO executionInfo)
  {
    return this.executionInfos.remove(executionInfo);
  }

  /**
   * // TODO insert comment into xml for [wednesdayExecution]
   * 
   * @return the wednesdayExecution property
   */
  public final boolean isWednesdayExecution()
  {
    return wednesdayExecution;
  }

  /**
   * // TODO insert comment into xml for [wednesdayExecution]
   * 
   */
  public final void setWednesdayExecution(boolean wednesdayExecution)
  {
    this.wednesdayExecution = wednesdayExecution;
  }

  /**
   * // TODO insert comment into xml for [sundayExecution]
   * 
   * @return the sundayExecution property
   */
  public final boolean isSundayExecution()
  {
    return sundayExecution;
  }

  /**
   * // TODO insert comment into xml for [sundayExecution]
   * 
   */
  public final void setSundayExecution(boolean sundayExecution)
  {
    this.sundayExecution = sundayExecution;
  }

  /**
   * // TODO insert comment into xml for [executorClass]
   * 
   * @return the executorClass property
   */
  public final String getExecutorClass()
  {
    return executorClass;
  }

  /**
   * // TODO insert comment into xml for [executorClass]
   * 
   */
  public final void setExecutorClass(String executorClass)
  {
    this.executorClass = executorClass;
  }

  /**
   * // TODO insert comment into xml for [PersistentOneToMany triggers]
   * 
   * @return the triggers property (Set of com.ubs.arte.common.model.admin.TriggerDTO)
   */
  public final Set getTriggers()
  {
    return triggers;
  }

  /**
   * // TODO insert comment into xml for [PersistentOneToMany triggers] (Set of
   * com.ubs.arte.common.model.admin.TriggerDTO)
   */
  public final void setTriggers(Set triggers)
  {
    this.triggers = triggers;
  }

  /**
   * // TODO insert comment into xml for [PersistentOneToMany triggers] reverse relationsip name:
   * null (Set of com.ubs.arte.common.model.admin.TriggerDTO)
   */
  public final boolean addTrigger(TriggerDTO trigger)
  {
    return this.triggers.add(trigger);
  }

  /**
   * // TODO insert comment into xml for [PersistentOneToMany triggers] (Set of
   * com.ubs.arte.common.model.admin.TriggerDTO)
   */
  public final boolean removeTrigger(TriggerDTO trigger)
  {
    return this.triggers.remove(trigger);
  }

  /**
   * // TODO insert comment into xml for [code]
   * 
   * @return the code property
   */
  public final String getCode()
  {
    return code;
  }

  /**
   * // TODO insert comment into xml for [code]
   * 
   */
  public final void setCode(String code)
  {
    this.code = code;
  }

  /**
   * // TODO insert comment into xml for [tuesdayExecution]
   * 
   * @return the tuesdayExecution property
   */
  public final boolean isTuesdayExecution()
  {
    return tuesdayExecution;
  }

  /**
   * // TODO insert comment into xml for [tuesdayExecution]
   * 
   */
  public final void setTuesdayExecution(boolean tuesdayExecution)
  {
    this.tuesdayExecution = tuesdayExecution;
  }

  /**
   * // TODO insert comment into xml for [thursdayExecution]
   * 
   * @return the thursdayExecution property
   */
  public final boolean isThursdayExecution()
  {
    return thursdayExecution;
  }

  /**
   * // TODO insert comment into xml for [thursdayExecution]
   * 
   */
  public final void setThursdayExecution(boolean thursdayExecution)
  {
    this.thursdayExecution = thursdayExecution;
  }

  /**
   * // TODO insert comment into xml for [description]
   * 
   * @return the description property
   */
  public final String getDescription()
  {
    return description;
  }

  /**
   * // TODO insert comment into xml for [description]
   * 
   */
  public final void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * // TODO insert comment into xml for [fridayExecution]
   * 
   * @return the fridayExecution property
   */
  public final boolean isFridayExecution()
  {
    return fridayExecution;
  }

  /**
   * // TODO insert comment into xml for [fridayExecution]
   * 
   */
  public final void setFridayExecution(boolean fridayExecution)
  {
    this.fridayExecution = fridayExecution;
  }

  /**
   * // TODO insert comment into xml for [name]
   * 
   * @return the name property
   */
  public final String getName()
  {
    return name;
  }

  /**
   * // TODO insert comment into xml for [name]
   * 
   */
  public final void setName(String name)
  {
    this.name = name;
  }

  /**
   * // TODO insert comment into xml for [parameter]
   * 
   * @return the parameter property
   */
  public final String getParameter()
  {
    return parameter;
  }

  /**
   * // TODO insert comment into xml for [parameter]
   * 
   */
  public final void setParameter(String parameter)
  {
    this.parameter = parameter;
  }

  /**
   * // TODO insert comment into xml for [saturdayExecution]
   * 
   * @return the saturdayExecution property
   */
  public final boolean isSaturdayExecution()
  {
    return saturdayExecution;
  }

  /**
   * // TODO insert comment into xml for [saturdayExecution]
   * 
   */
  public final void setSaturdayExecution(boolean saturdayExecution)
  {
    this.saturdayExecution = saturdayExecution;
  }

  public boolean isMultipleExecutionTimes()
  {
    return (!AppUtil.isEmpty(getTriggers()) && getTriggers().size() > 1);
  }

  /**
   * The execution identifier that identifies {@link ExecutionInfoDTO}s of this AdminJob
   * 
   * @return the {@link ExecutionIdentifier} for {@link ExecutionInfoDTO} belonging to this AdminJob
   */
  public String getExecutableIdentifier()
  {
    return EXECUTABLE_IDENTIFIER_PREFIX + getCode();
  }

  /**
   * Checks if the given {@link ExecutionInfoDTO} is an execution of this AdminJobDTO
   * 
   * @param executionInfo
   * @return <code>true</code> if the given {@link ExecutionInfoDTO} is an execution of this
   *         {@link AdminJobDTO}, <code>false</code> otherwise
   */
  public boolean isExecutionOfThisJob(ExecutionInfoDTO executionInfo)
  {
    if(getExecutableIdentifier() == null || executionInfo == null)
      return false;
    return getExecutableIdentifier().equals(executionInfo.getExecutableId());
  }

  public boolean hasActiveDays()
  {
    return (isMondayExecution() || isTuesdayExecution() || isWednesdayExecution() || isThursdayExecution() || isFridayExecution() || isSaturdayExecution() || isSundayExecution());
  }

  // **************************************************************
  // ***** Setter methods *****************************************
  // **************************************************************

  /**
   * Schedules the job to be run on a daily basis, i.e. all days of the week, including weekend.
   */
  public void setDailyExecution()
  {
    setMondayExecution(true);
    setTuesdayExecution(true);
    setWednesdayExecution(true);
    setThursdayExecution(true);
    setFridayExecution(true);
    setSaturdayExecution(true);
    setSundayExecution(true);
  }

  /**
   * Schedules the job to be run on a daily basis during the week only, i.e. excluding weekend.
   */
  public void setWeekDayExecution()
  {
    setMondayExecution(true);
    setTuesdayExecution(true);
    setWednesdayExecution(true);
    setThursdayExecution(true);
    setFridayExecution(true);
    setSaturdayExecution(false);
    setSundayExecution(false);
  }

  public void addExecutionTime(Date executionTime)
  {
    addTrigger(new TriggerDTO(this, executionTime));
  }

  /**
   * 
   * 
   * @param triggers
   */
  public void setTriggerArray(TriggerDTO[] triggers)
  {

    removeAllTriggers();
    if(triggers != null)
      addTriggers(triggers);
  }

  /**
   * @param triggers
   * @return whether the position was successfully added
   */
  private boolean addTriggers(TriggerDTO[] triggers)
  {

    for(int i = 0; i < triggers.length; i++)
    {
      TriggerDTO triggerDTO = triggers[i];
      triggerDTO.setAdminJob(this);
      addTrigger(triggerDTO);
    }

    return true;
  }

  /**
 * 
 */
  private void removeAllTriggers()
  {
    getTriggers().clear();
  }

  public String getShortDescription()
  {
    return getName();
  }

  /**
   * adds the ExecutionInfo if the object is an execInfo of this AdminJob.
   * 
   * @see #isExecutionInfoModelsOfThisJob(ExecutionInfoModel)
   * @param execInfo
   */
  public boolean addExecutionInfoForThisJob(ExecutionInfoDTO execInfo)
  {
    if(!isExecutionOfThisJob(execInfo))
    {
      return false;
    }

    addExecutionInfo(execInfo);
    return true;
  }

  /**
   * replaces the given ExecutionInfoDTO.
   * 
   * remove and add.
   * 
   * @param execInfo
   * @return
   */
  public boolean replaceExecutionInfo(ExecutionInfoDTO execInfo)
  {
    removeExecutionInfo(execInfo);
    return addExecutionInfoForThisJob(execInfo);
  }
}
