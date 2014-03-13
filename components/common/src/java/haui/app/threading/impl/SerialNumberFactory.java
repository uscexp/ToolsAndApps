/* *****************************************************************
 * Project: common
 * File:    SerialNumberFactory.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * SerialNumberFactory
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SerialNumberFactory
{
  private int cnt = 0;
  private int maxNum = 0;
  private final NumberFormat formatter;

  /**
   * 
   */
  public SerialNumberFactory(int arity) {
    super();
    maxNum = (int) Math.pow(10.0, arity) - 1;

    char[] fc = new char[arity];
    Arrays.fill(fc, '0');
    formatter = new DecimalFormat(new String(fc));
  }

  public synchronized int getNewNumber() {
    ++cnt;
    if (cnt < 0 || cnt > maxNum) {
      cnt = 1;
    }
    return cnt;
  }

  public String getNewNumberString() {
    return formatter.format(getNewNumber());
  }
}
