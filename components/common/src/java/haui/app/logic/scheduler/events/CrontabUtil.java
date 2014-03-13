/* *****************************************************************
 * Project: common
 * File:    CrontabUtil.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler.events;

import java.util.StringTokenizer;

/**
 * CrontabUtil
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class CrontabUtil
{
  public static String getCronString(String time, boolean mon, boolean tue, boolean wen, boolean thu, boolean fri, boolean sat, boolean sun)
      throws IllegalArgumentException
  {
    try
    {
      StringTokenizer str = new StringTokenizer(time, ":");
      int hour = Integer.parseInt(str.nextToken());
      int minute = Integer.parseInt(str.nextToken());
      return getCronString(hour, minute, mon, tue, wen, thu, fri, sat, sun);
    }
    catch(Exception e)
    {
      throw new IllegalArgumentException("Time is in wrong format! Format expected <hh>:<mm>");
    }
  }

  public static String getCronString(int hour, int minute, boolean mon, boolean tue, boolean wen, boolean thu, boolean fri, boolean sat, boolean sun)
      throws IllegalArgumentException
  {

    if(hour > 23 || hour < 0)
    {
      throw new IllegalArgumentException("Hour has to be between 0 and 23");
    }
    if(minute > 59 || minute < 0)
    {
      throw new IllegalArgumentException("Minute has to be between 0 and 59");
    }

    StringBuffer time = new StringBuffer();
    time.append(minute);
    time.append(" ");
    time.append(hour);
    time.append(" * * ");

    if(mon)
      time.append(1).append(",");
    if(tue)
      time.append(2).append(",");
    if(wen)
      time.append(3).append(",");
    if(thu)
      time.append(4).append(",");
    if(fri)
      time.append(5).append(",");
    if(sat)
      time.append(6).append(",");
    if(sun)
      time.append(7).append(",");

    if(time.toString().endsWith(","))
    {
      time.delete(time.length() - 1, time.length());
    }
    else
    {
      time.append("*");
    }

    return time.toString();

  }
}
