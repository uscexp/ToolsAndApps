/* *****************************************************************
 * Project: common
 * File:    AppJobExecutionStatistics.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.util.SimpleStatisticCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * AppJobExecutionStatistics
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AppJobExecutionStatistics implements ExecutionStatistics
{

  public static final String DEFAULT_TOTAL_ERROR = "Total Errors";
  public static final String TOTAL_ERROR_STATISTIC = "totalErrorStatistic";
  public static final String DEFAULT_STATISTIC = "default";
  public static final String INSTRUMENT_IMPORT_STATISTIC_PREFIX = "Stats4instrumentImport";
  public static final String PRODUCT_IMPORT_STATISTIC_PREFIX = "Stats4productImport";
  public static final String VALIDATION_STATISTIC_PREFIX = "Stats4validation";

  private Execution execution;
  private Map jobSubStats = new HashMap();
  private SimpleStatisticCounter defaultSimpleStatisticCounter = new SimpleStatisticCounter();
  private SimpleStatisticCounter totalErrorCounter = new SimpleStatisticCounter();

  public AppJobExecutionStatistics(Execution execution) {
      jobSubStats.put(DEFAULT_STATISTIC, defaultSimpleStatisticCounter);
      jobSubStats.put(TOTAL_ERROR_STATISTIC, totalErrorCounter);
      totalErrorCounter.touch(DEFAULT_TOTAL_ERROR);
      this.execution = execution;
  }

  // by default there is one simple statistic counter initialized
  public int touch(String counter) {
      return getSubSimpleStatisticCounter(counter).touch(counter);
  }

  public int increase(String counter, int amount) {
      execution.touched();
      return getSubSimpleStatisticCounter(counter).increase(counter, amount);
  }

  public int increase(String counter) {
      execution.touched();
      return getSubSimpleStatisticCounter(counter).increase(counter);
  }

  public void setStatistic(String key, String value) {
      getSubSimpleStatisticCounter(key).setStatistic(key, value);
  }

  public String getStatistic(String key) {
      return getSubSimpleStatisticCounter(key).getStatistic(key);
  }

  public void remove(String key) {
      getSubSimpleStatisticCounter(key).remove(key);
  }

  /*
   * ############################## # # # Statistic Getter Methods # # # ##############################
   */

  public Integer getCount(String counter) {
      return getSubSimpleStatisticCounter(counter).getCount(counter);
  }

  public String[] getCounterNames() {
      ArrayList allCounterNames = new ArrayList();

      Map tempSubStats = new HashMap();
      tempSubStats = jobSubStats;
      for (Iterator iter = tempSubStats.keySet().iterator(); iter.hasNext();) {
          String key = (String) iter.next();
          SimpleStatisticCounter subStatCounter = (SimpleStatisticCounter) tempSubStats.get(key);
          String[] temp = subStatCounter.getCounterNames();
          for (int i = 0; i < temp.length; i++) {
              allCounterNames.add(temp[i]);
          }
      }

      return (String[]) allCounterNames.toArray(new String[allCounterNames.size()]);
  }

  public String getTextualStatistics() {
      StringBuffer buffer = new StringBuffer();

      // append general default statistic information.
      buffer.append(((SimpleStatisticCounter) jobSubStats.get(DEFAULT_STATISTIC)).getTextualStatistics());

      // append the sub statistics to the string buffer
      Map tempSubStats = new HashMap();
      tempSubStats = jobSubStats;
      for (Iterator iter = tempSubStats.keySet().iterator(); iter.hasNext();) {
          String key = (String) iter.next();
          if (!(key.equalsIgnoreCase(DEFAULT_STATISTIC) || key.equalsIgnoreCase(TOTAL_ERROR_STATISTIC))) {
              SimpleStatisticCounter subStatCounter = (SimpleStatisticCounter) tempSubStats.get(key);
              buffer.append(subStatCounter.getTextualStatistics());
          }
      }
      buffer.append("\n******************\n");
      buffer.append(((SimpleStatisticCounter) jobSubStats.get(TOTAL_ERROR_STATISTIC)).getTextualStatistics());
      return buffer.toString();
  }

  /*
   * ######################################### # # # Methods for multiple Sub Statistics # # # #########################################
   */
  private synchronized SimpleStatisticCounter getSubSimpleStatisticCounter(String counterName) {
      String subStatisticName = getStatisticNameByPrefix(counterName);
      SimpleStatisticCounter subSimpleStatisticCounter = (SimpleStatisticCounter) jobSubStats.get(subStatisticName);
      if (subSimpleStatisticCounter == null) {
          subSimpleStatisticCounter = new SimpleStatisticCounter();
          jobSubStats.put(subStatisticName, subSimpleStatisticCounter);
      }

      return subSimpleStatisticCounter;
  }

  private String getStatisticNameByPrefix(String counterName) {
      if (counterName.startsWith(INSTRUMENT_IMPORT_STATISTIC_PREFIX)) return INSTRUMENT_IMPORT_STATISTIC_PREFIX;
      if (counterName.startsWith(PRODUCT_IMPORT_STATISTIC_PREFIX)) return PRODUCT_IMPORT_STATISTIC_PREFIX;
      if (counterName.startsWith(VALIDATION_STATISTIC_PREFIX)) return VALIDATION_STATISTIC_PREFIX;
      if (counterName.equalsIgnoreCase(DEFAULT_TOTAL_ERROR))
          return TOTAL_ERROR_STATISTIC;
      else
          return DEFAULT_STATISTIC;
  }

  public static String removePrefix(String counterName) {
      if (counterName.startsWith(INSTRUMENT_IMPORT_STATISTIC_PREFIX)) return counterName.substring(INSTRUMENT_IMPORT_STATISTIC_PREFIX.length());
      if (counterName.startsWith(PRODUCT_IMPORT_STATISTIC_PREFIX)) return counterName.substring(PRODUCT_IMPORT_STATISTIC_PREFIX.length());
      if (counterName.startsWith(VALIDATION_STATISTIC_PREFIX))
          return counterName.substring(VALIDATION_STATISTIC_PREFIX.length());
      else
          return counterName;
  }
}
