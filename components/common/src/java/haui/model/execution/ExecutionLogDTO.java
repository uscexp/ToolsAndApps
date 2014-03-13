/* *****************************************************************
 * Project: common
 * File:    ExecutionLogDTO.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.execution;

import haui.model.DataTransferObject;
import haui.util.AppUtil;

/**
 * ExecutionLogDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionLogDTO extends DataTransferObject
{
  static final long serialVersionUID = -56796093273603713L;

  // pn attributes
  public static final String PN_EXECUTION_INFO = "executionInfo";
  public static final String PN_LOG_FILE = "logFile";
  public static final String PN_DEFLATED_LOG_FILE = "deflatedLogFile";

  // property members
  private ExecutionInfoDTO executionInfo;
  private byte[] deflatedLogFile;

  /**
   * default constructor
   */
  public ExecutionLogDTO()
  {
  }

  public ExecutionLogDTO(ExecutionInfoDTO executionInfo)
  {
    setExecutionInfo(executionInfo);
  }

  // accessor methods

  /**
   * Make the created log file persistent
   * 
   * @param data
   */
  public void setLogFile(byte[] data)
  {
    setDeflatedLogFile(AppUtil.inflateData(data));
  }

  /**
   * 
   * @return log File from DB
   */
  public byte[] getLogFile()
  {
    return AppUtil.deflateData(getDeflatedLogFile());
  }

  /**
   * // TODO insert comment into xml for [executionInfo]
   * 
   * @return the executionInfo property
   */
  public final ExecutionInfoDTO getExecutionInfo()
  {
    return executionInfo;
  }

  /**
   * // TODO insert comment into xml for [executionInfo]
   * 
   */
  public final void setExecutionInfo(ExecutionInfoDTO executionInfo)
  {
    this.executionInfo = executionInfo;
  }

  /**
   * // TODO insert comment into xml for [deflatedLogFile]
   * 
   * @return the deflatedLogFile property
   */
  public final byte[] getDeflatedLogFile()
  {
    return deflatedLogFile;
  }

  /**
   * // TODO insert comment into xml for [deflatedLogFile]
   * 
   */
  public final void setDeflatedLogFile(byte[] deflatedLogFile)
  {
    this.deflatedLogFile = deflatedLogFile;
  }
}
