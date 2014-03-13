/* *****************************************************************
 * Project: common
 * File:    SchedulerUtil.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * SchedulerUtil
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SchedulerUtil
{

  // **************************************************************
  // ***** Calendar ***********************************************
  // **************************************************************

  /**
   * set aDate and returns a calendar object
   * 
   * @param aDate
   * @return Calendar
   */
  public static final Calendar getCalendar(Date aDate)
  {
    Calendar cal = getCalendar();
    cal.setTime(aDate);
    return cal;
  }

  /**
   * @param hours
   * @param minutes
   * @return Calendar
   */
  public static final Calendar getCalendar(int hours, int minutes)
  {
    Calendar cal = getCalendar();
    cal.set(Calendar.HOUR_OF_DAY, hours);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, 0);
    return cal;
  }

  /**
   * set aDate and returns a calendar object
   * 
   * @return calendar
   */
  public static final Calendar getCalendar()
  {
    Date current = DateUtil.getCurrentTimeStamp();
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(current);
    return calendar;
  }

  // **************************************************************
  // ***** Time ***************************************************
  // **************************************************************

  /**
   * Creates a date in a gregorian calendar with the given hours, minutes and seconds
   * <p>
   * Years, months and days of the returned Date will be set to zero.
   */
  public static Date createTime(int hours, int minutes, int seconds)
  {

    Calendar calendar = getCalendar();
    calendar.set(Calendar.YEAR, 0);
    calendar.set(Calendar.MONTH, 0);
    calendar.set(Calendar.DATE, 0);
    calendar.set(Calendar.HOUR_OF_DAY, hours);
    calendar.set(Calendar.MINUTE, minutes);
    calendar.set(Calendar.SECOND, seconds);
    return calendar.getTime();
  }

  /**
   * Creates a date in a gregorian calendar with the given hours and minutes.
   * <p>
   * Years, months and days of the returned Date will be set to zero.
   */
  public static Date createTime(int hours, int minutes)
  {
    return createTime(hours, minutes, 0);
  }

  /**
   * Creates a date in a gregorian calendar with the given date. Only the time is decisive.
   * <p>
   * Years, months and days of the returned Date will be set to zero.
   */
  public static Date createTime(Date date)
  {
    return createTime(getHours(date), getMinutes(date), getSeconds(date));
  }

  /**
   * Creates a Date object with the current time and minutes
   * <p>
   * Years, months and days of the returned Date will be set to zero.
   */
  public static Date getCurrentTime()
  {
    Calendar current = getCalendar();
    int currentHour = current.get(Calendar.HOUR_OF_DAY);
    int currentMinute = current.get(Calendar.MINUTE);
    return createTime(currentHour, currentMinute);
  }

  /**
   * Returns the current day of week.
   * <p>
   * Monday=0, Tuesday=1 etc.
   */
  public static int getCurrentDayOfWeek()
  {
    Calendar current = getCalendar();
    int dayOfWeek = current.get(Calendar.DAY_OF_WEEK);
    dayOfWeek = (dayOfWeek + 7 - 2) % 7;
    return dayOfWeek;
  }

  /**
   * Returns the date of a date object
   */
  public static int getDays(Date date)
  {
    Calendar tmp = getCalendar();
    tmp.setTime(date);
    return tmp.get(Calendar.DATE);
  }

  /**
   * Returns the hour of a date object
   */
  public static int getHours(Date date)
  {
    Calendar tmp = getCalendar();
    tmp.setTime(date);
    return tmp.get(Calendar.HOUR_OF_DAY);
  }

  /**
   * Returns the minutes of a date object
   */
  public static int getMinutes(Date date)
  {
    Calendar tmp = getCalendar();
    tmp.setTime(date);
    return tmp.get(Calendar.MINUTE);
  }

  /**
   * Returns the minutes of a date object
   */
  public static int getSeconds(Date date)
  {
    Calendar tmp = getCalendar();
    tmp.setTime(date);
    return tmp.get(Calendar.SECOND);
  }

  /**
   * Return true if date1 is strictly before date2. null is interpreted as infinite future (date
   * which will never reached).
   * 
   * @param date1 first date.
   * @param date2 second date.
   * @return true or false
   */
  public static final boolean isTimeBefore(final Date date1, final Date date2)
  {
    if(date1 == null)
      return false;
    if(date2 == null)// d1 < d2 | d1 != null, d2 == null
      return true;

    Date date_1 = createTime(date1);
    Date date_2 = createTime(date2);

    return date_1.before(date_2);
  }
}
