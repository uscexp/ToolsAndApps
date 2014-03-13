/* *****************************************************************
 * Project: common
 * File:    ExecutionStatistics.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

/**
 * ExecutionStatistics
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ExecutionStatistics
{
  /**
   * Makes sure the 'counter' is initialized. If the counter exists nothing happens, if it does not
   * it is initialized to zero.
   * 
   * @param counter
   * @return the current counter value
   */
  public int touch(String counter);

  /**
   * Increases the counter by the given amount.
   * 
   * @param counter the String name of the counter
   * @param amount the amount the counter should be increased by
   * @return the current counter value
   */
  public int increase(String counter, int amount);

  /**
   * Increase the counter by one.
   * 
   * @param counter the String name of the counter
   * @return the current counter value
   */
  public int increase(String counter);

  /**
   * Remove counter with name <counter>
   * 
   * @param counter the String name of the counter to be removed
   */
  public void remove(String counter);

  /**
   * Add for a certain key a statistic info.
   * 
   * @param key the String key of the statistic
   * @param value the current statistic value
   */
  public void setStatistic(String key, String value);

  /**
   * Get for a certain key a statistic info.
   * 
   * @param key the String key of the statistic
   * @return value the current statistic value
   */
  public String getStatistic(String key);
}
