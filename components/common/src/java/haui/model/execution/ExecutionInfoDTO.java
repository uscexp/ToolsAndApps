/* *****************************************************************
 * Project: common
 * File:    ExecutionInfoDTO.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.execution;

import haui.model.DataTransferObject;
import haui.model.data.ExecutionStatusCode;
import haui.model.data.ExecutionTerminationCode;
import haui.model.partner.UserDTO;
import haui.util.AppUtil;
import haui.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ExecutionInfoDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionInfoDTO extends DataTransferObject
{
  static final long serialVersionUID = -2679775989554183809L;
  
  // pn attributes  
  public static final String PN_DATE_SCHEDULED = "dateScheduled"; 
  public static final String PN_DATE_STARTED = "dateStarted"; 
  public static final String PN_EXECUTION_ID = "executionId"; 
  public static final String PN_DATE_CANCELED = "dateCanceled"; 
  public static final String PN_TERMINATION_CODE = "terminationCode"; 
  public static final String PN_TERMINATED = "terminated";  
  public static final String PN_CANCELED = "canceled";  
  public static final String PN_PROGRESS = "progress";  
  public static final String PN_START_USER = "startUser"; 
  public static final String PN_DATE_TERMINATED = "dateTerminated"; 
  public static final String PN_EXECUTION_STATUS_CODE = "executionStatusCode";  
  public static final String PN_EXECUTABLE_ID = "executableId"; 
  public static final String PN_SUMMARY_LOG = "summaryLog"; 
  public static final String PN_KEY_VALUE_PAIRS = "keyValuePairs";  
  public static final String PN_RUNNING = "running";

  // property members
  private Date dateScheduled;
  private Date dateStarted;
  private String executionId;
  private Date dateCanceled;
  private ExecutionTerminationCode terminationCode = ExecutionTerminationCode.NOT_TERMINATED;
  private double progress = -1;
  private UserDTO startUser;
  private Date dateTerminated;
  private ExecutionStatusCode executionStatusCode = ExecutionStatusCode.WAITING_FOR_START;
  private String executableId;
  private String summaryLog;
  private Set keyValuePairs = new HashSet();
    
  private static final Log LOG = LogFactory.getLog(ExecutionInfoDTO.class);

  /**
   * Default constructor
   */
  public ExecutionInfoDTO() {super();}

  
  // accessor methods 
 

  /**
   * // TODO insert comment into xml for [dateScheduled]
   *
   * @return the dateScheduled property 
   */
  public final Date getDateScheduled() {
    return dateScheduled;
  }

  /**
   * // TODO insert comment into xml for [dateScheduled]
   * 
   */
  public final void setDateScheduled(Date dateScheduled) {
    this.dateScheduled = dateScheduled;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [dateStarted]
   *
   * @return the dateStarted property 
   */
  public final Date getDateStarted() {
    return dateStarted;
  }

  /**
   * // TODO insert comment into xml for [dateStarted]
   * 
   */
  public final void setDateStarted(Date dateStarted) {
    this.dateStarted = dateStarted;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [executionId]
   *
   * @return the executionId property 
   */
  public final String getExecutionId() {
    return executionId;
  }

  /**
   * // TODO insert comment into xml for [executionId]
   * 
   */
  public final void setExecutionId(String executionId) {
    this.executionId = executionId;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [dateCanceled]
   *
   * @return the dateCanceled property 
   */
  public final Date getDateCanceled() {
    return dateCanceled;
  }

  /**
   * // TODO insert comment into xml for [dateCanceled]
   * 
   */
  public final void setDateCanceled(Date dateCanceled) {
    this.dateCanceled = dateCanceled;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [terminationCode]
   *
   * @return the terminationCode property 
   */
  public final ExecutionTerminationCode getTerminationCode() {
    return terminationCode;
  }

  /**
   * // TODO insert comment into xml for [terminationCode]
   * 
   */
  public final void setTerminationCode(ExecutionTerminationCode terminationCode) {
    this.terminationCode = terminationCode;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [progress]
   *
   * @return the progress property 
   */
  public final double getProgress() {
    return progress;
  }

  /**
   * // TODO insert comment into xml for [progress]
   * 
   */
  public final void setProgress(double progress) {
    this.progress = progress;
  }
  

    
 
 

  /**
   * Returns the user who started the execution.
   *
   * @return the startUser property 
   */
  public final UserDTO getStartUser() {
    return startUser;
  }

  /**
   * Returns the user who started the execution.
   * 
   */
  public final void setStartUser(UserDTO startUser) {
    this.startUser = startUser;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [dateTerminated]
   *
   * @return the dateTerminated property 
   */
  public final Date getDateTerminated() {
    return dateTerminated;
  }

  /**
   * // TODO insert comment into xml for [dateTerminated]
   * 
   */
  public final void setDateTerminated(Date dateTerminated) {
    this.dateTerminated = dateTerminated;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [executionStatusCode]
   *
   * @return the executionStatusCode property 
   */
  public final ExecutionStatusCode getExecutionStatusCode() {
    return executionStatusCode;
  }

  /**
   * // TODO insert comment into xml for [executionStatusCode]
   * 
   */
  public final void setExecutionStatusCode(ExecutionStatusCode executionStatusCode) {
    this.executionStatusCode = executionStatusCode;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [executableId]
   *
   * @return the executableId property 
   */
  public final String getExecutableId() {
    return executableId;
  }

  /**
   * // TODO insert comment into xml for [executableId]
   * 
   */
  public final void setExecutableId(String executableId) {
    this.executableId = executableId;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [summaryLog]
   *
   * @return the summaryLog property 
   */
  public final String getSummaryLog() {
    return summaryLog;
  }

  /**
   * // TODO insert comment into xml for [summaryLog]
   * 
   */
  public final void setSummaryLog(String summaryLog) {
    this.summaryLog = summaryLog;
  }
  

    
 
 

  /**
   * // TODO insert comment into xml for [keyValuePairs]
   *
   * @return the keyValuePairs property (Set of com.ubs.arte.common.model.execution.ExecutionInfoKeyValueDTO)
   */
  public final Set getKeyValuePairs() {
    return keyValuePairs;
  }

  /**
   * // TODO insert comment into xml for [keyValuePairs]
   * (Set of com.ubs.arte.common.model.execution.ExecutionInfoKeyValueDTO)
   */
  public final void setKeyValuePairs(Set keyValuePairs) {
    this.keyValuePairs = keyValuePairs;
  }
  

  
  /**
   * // TODO insert comment into xml for [keyValuePairs]
   * reverse relationsip name: null
   * (Set of com.ubs.arte.common.model.execution.ExecutionInfoKeyValueDTO)
   */
  public final boolean addKeyValuePair(ExecutionInfoKeyValueDTO keyValuePair) {
    return this.keyValuePairs.add(keyValuePair);
  }
  
  /**
   * // TODO insert comment into xml for [keyValuePairs]
   * (Set of com.ubs.arte.common.model.execution.ExecutionInfoKeyValueDTO)
   */
  public final boolean removeKeyValuePair(ExecutionInfoKeyValueDTO keyValuePair) {
    return this.keyValuePairs.remove(keyValuePair);
  } 
    
  /**
   * Constructs instance using executableId and exeutionIdentifier
   * 
   * @param executableId
   * @param executionIdentifier
   */
  public ExecutionInfoDTO(String executableId, ExecutionIdentifier executionIdentifier, UserDTO startUser) {
      setStartUser(startUser);
      setExecutableId(executableId);
      setExecutionId(executionIdentifier.getId());
  }

  /**
   * Answers a short description
   */
  public String getShortDescription() {
      StringBuffer description = new StringBuffer();

      description.append("Execution(");
      if (getExecutableId() != null) {
          description.append(getExecutableId() + " ");
      }

      if (getExecutionId() != null) {
          description.append(getExecutionId());
      }
      description.append(")");

      return description.toString();
  }

  /**
   * The estimated point in time the execution will be terminated
   * 
   * @return the estimated termination date or <code>null</code> if not known
   */
  public Date getEstimatedDateTerminated() {
      return null;
  }

  /**
   * @return the duration of the execution
   */
  public String getExecutionDuration() {
      long duration = getDateTerminated().getTime() - getDateStarted().getTime();
      String elapsedTime = DateUtil.formatElapsedTime(duration);
      // remove millisecond part and return
      elapsedTime = elapsedTime.substring(0, elapsedTime.indexOf(" s ") + 2);
      if (elapsedTime.equalsIgnoreCase("0")) elapsedTime = "01 s";
      return elapsedTime;
  }

  /**
   * 
   * @return <code>true</code> if the execution is canceled, <code>false</code> otherwise
   */
  public boolean isCanceled() {
      return getDateCanceled() != null;
  }

  public boolean isRunning() {
      return !isTerminated();
  }

  /**
   * 
   * @return the unique {@link ExecutionIdentifier} of this execution
   */
  public ExecutionIdentifier getExecutionIdentifier() {
      return new ExecutionIdentifier(this.getExecutionId());
  }

  /**
   * 
   * @param executionIdentifier
   */
  public void setExecutionIdentifier(ExecutionIdentifier executionIdentifier) {
      setExecutionId(executionIdentifier.getId());
  }

  /**
   * 
   * @return <code>true</code> if this execution has terminated, <code>false</code> otherwise
   */
  public boolean isTerminated() {
      return ExecutionStatusCode.TERMINATED == getExecutionStatusCode();
  }

  public boolean isSuccessfulTermination() {
      return getTerminationCode() == ExecutionTerminationCode.SUCCESSFUL;
  }
  
  public boolean put(String key, Object value) {
      boolean result = false;
      ExecutionInfoKeyValueDTO keyValue = getKeyValuePair(key);
      
      if(keyValue == null) {
          keyValue = new ExecutionInfoKeyValueDTO(key,value);
          addKeyValuePair(keyValue);
          result = true;
      }
      else {
          keyValue.setKeyValue(key, value);
          result = true;
      }
      return result;
  }
  
  public Object get(String key) {
      Object result = null;
      
      ExecutionInfoKeyValueDTO keyValue = getKeyValuePair(key);
      
      if(keyValue != null) {
          String type = keyValue.getType();
          String value = keyValue.getValue();
          result = AppUtil.convertToType(type, value, ExecutionInfoKeyValueDTO.DATE_TIME_PATTERN);
      }
      return result;
  }
  
  public String printKeyValuePairs() {
      StringBuffer stringBuffer = new StringBuffer();
      Set keyValues = getKeyValuePairs();
      Iterator iterator = keyValues.iterator();
      
      while (iterator.hasNext()) {
          ExecutionInfoKeyValueDTO keyValue = (ExecutionInfoKeyValueDTO) iterator.next();
          
          stringBuffer.append(keyValue.getKey());
          stringBuffer.append(": ");
          stringBuffer.append(keyValue.getValue());
          stringBuffer.append("\n");
      }
      return stringBuffer.toString();
  }
  
  private ExecutionInfoKeyValueDTO getKeyValuePair(String key) {
      ExecutionInfoKeyValueDTO result = null;
      Set keyValues = getKeyValuePairs();
      Iterator iterator = keyValues.iterator();
      
      while (iterator.hasNext()) {
          ExecutionInfoKeyValueDTO keyValue = (ExecutionInfoKeyValueDTO) iterator.next();
          
          if(keyValue.getKey().equals(key)) {
              result = keyValue;
              break;
          }
      }
      return result;
  }
}
