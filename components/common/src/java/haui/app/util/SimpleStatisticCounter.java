/* *****************************************************************
 * Project: common
 * File:    SimpleStatisticCounter.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.util;

import haui.app.execution.AppJobExecutionStatistics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SimpleStatisticCounter
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SimpleStatisticCounter
{
  private final Map counters = Collections.synchronizedMap(new LinkedHashMap());
  private final Map statistics = Collections.synchronizedMap(new LinkedHashMap());

  public SimpleStatisticCounter()
  {
  }

  public synchronized int increase(String counter, int amount)
  {
    Integer count = (Integer)counters.get(counter);
    if(count == null)
      count = new Integer(0);

    count = new Integer(count.intValue() + amount);
    counters.put(counter, count);

    return count.intValue();
  }

  public void remove(String counter)
  {
    counters.remove(counter);
  }

  public Integer getCount(String counter)
  {
    return (Integer)counters.get(counter);
  }

  public String[] getCounterNames()
  {
    return (String[])counters.keySet().toArray(new String[counters.keySet().size()]);
  }

  public int touch(String counter)
  {
    return increase(counter, 0);
  }

  public int increase(String counter)
  {
    return increase(counter, 1);
  }

  public String getTextualStatistics()
  {
    return getTextualStatistics("\n");
  }

  public String getTextualStatistics(String separator)
  {
    StringBuffer buffer = new StringBuffer();

    // append the statistics to the string buffer
    Map tempMap = new HashMap();
    tempMap = statistics;
    for(Iterator iter = tempMap.keySet().iterator(); iter.hasNext();)
    {
      String key = (String)iter.next();
      buffer.append(tempMap.get(key) + separator);
    }

    // append the counters to the string buffer
    String[] counterStrings = (String[])counters.keySet().toArray(new String[counters.keySet().size()]);
    // Arrays.sort(counterStrings);
    for(int i = 0; i < counterStrings.length; i++)
    {
      String counter = AppJobExecutionStatistics.removePrefix(counterStrings[i]);
      Integer count = getCount(counterStrings[i]);

      buffer.append(count + " " + counter + separator);
    }

    return buffer.toString();
  }

  /**
   * Add for a certain key a statistic info.
   * 
   * @param key the String key of the statistic
   * @param value the current statistic value
   */
  public void setStatistic(String key, String value)
  {
    statistics.put(key, value);
  }

  /**
   * Get for a certain key a statistic info.
   * 
   * @param key the String key of the statistic
   * @return value the current statistic value
   */
  public String getStatistic(String key)
  {
    return (String)statistics.get(key);
  }

  /*
   * Methods not used but must be implemented here for inheritence of interface
   */
  public String getStatistic(String subStatistic, String key)
  {
    return getStatistic(key);
  }

  public int increase(String subStatistic, String counter)
  {
    return increase(counter);
  }

  public void setStatistic(String subStatistic, String key, String value)
  {
    setStatistic(key, value);
  }

  public int touch(String subStatistic, String counter)
  {
    return touch(counter);
  }
}
