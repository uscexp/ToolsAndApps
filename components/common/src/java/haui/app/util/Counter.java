/* *****************************************************************
 * Project: common
 * File:    Counter.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.util;

import java.io.Serializable;

/**
 * Counter
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Counter implements Serializable
{
  static final long serialVersionUID = -6641757938964246145L;

  public static final long INITIAL_VALUE = 0L;

  private long value;
  private long orgvalue;

  public Counter() {
      value = INITIAL_VALUE;
      orgvalue = INITIAL_VALUE;
  }

  public Counter(long value) {
      this.value = value;
      this.orgvalue = value;
  }

  public synchronized void increase() {
      value++;
  }

  public synchronized void decrease() {
      value--;
  }

  public synchronized void increase(long number) {
      value += number;
  }

  public synchronized void decrease(long number) {
      value -= number;
  }

  public synchronized void set(long value) {
      this.value = value;
      this.orgvalue = value;
  }

  public synchronized void reset() {
      this.value = orgvalue;
  }

  public int getInt() {
      return (int) value;
  }

  public long getLong() {
      return value;
  }

  public long getValue() {
      return value;
  }

  public String toString() {
      return "-value: " + value + " -orgiginal-value: " + orgvalue;
  }
}
