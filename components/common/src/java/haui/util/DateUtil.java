/* *****************************************************************
 * Project: common
 * File:    DateUtil.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holds utility methods used for Date handling.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class DateUtil
{
  // TODO THIS CLASS NEEDS SOME REFACTORING

  /**
   * The timezone is set to Default. We don't need/want to make this property configurable, as a
   * change of the time zone here will entail a data migration of all dates on the DB.
   */

  public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("CET");

  /**
   * A constant representing: 31.12.9999 00:00 CET time.
   */
  public static final Date END_OF_THE_WORLD = parse("31.12.9999", TIME_ZONE.getID());

  /**
   * A constant representing: 01.01.1900 00:00 CET time.
   */
  public static final Date BEGINNING_OF_THE_WORLD = parse("01.01.1900", TIME_ZONE.getID());

  /**
   * The date format used in ARTE: <code>dd.MM.yyyy</code>.
   */
  public static final String DATE_FORMAT = "dd.MM.yyyy";

  /**
   * a "00" format (for minutes and seconds)
   */
  public static final DecimalFormat TWO_DIGIT_FORMAT = new DecimalFormat("00");

  /**
   * a "000" format (for milliseconds)
   */
  public static final DecimalFormat THREE_DIGIT_FORMAT = new DecimalFormat("000");

  /**
   * The date/time format used in ARTE: <code>dd.MM.yyyy HH:mm:ss z</code>.
   */
  public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss z";

  protected static final Log LOG = LogFactory.getLog(DateUtil.class);

  private static final long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;

  protected static int offset = 0;

  private static final ThreadLocal stickyDate = new ThreadLocal();

  /**
   * Sets the stickyDate for {@link Thread#currentThread()}. If the stickyDate is set, time will
   * 'stand still' at stickyDate for the current thread until the stickDate is cleared by setting it
   * back to <code>null</code>
   * 
   * @param date
   */
  public static void setStickyDate(Date date)
  {
    stickyDate.set(date);
  }

  public static void unsetStickyDate()
  {
    stickyDate.set(null);
  }

  /**
   * Creates a new {@link Date} instance (like new Date()), if a stickDate is set with
   * {@link #setStickyDate(Date)} for the current thread, then the sticky date is returned instead
   * of a new date instance.
   * 
   * @return a Date instance reflecting the current time
   */
  public static Date newDate()
  {
    Date date = (Date)stickyDate.get();
    return date != null ? date : new Date();
  }

  /**
   * Formats a Date into a date string adhering to the format defined by {@link #DATE_FORMAT}.
   * 
   * @param date the date value to be formatted into a string
   * @return the formatted date string, <code>dd.MM.yyyy</code>
   */
  public static String format(Date date)
  {
    return format(date, false);
  }

  /**
   * Formats a Date into a date string adhering to the format defined by {@link #DATE_FORMAT} or
   * {@link #DATE_TIME_FORMAT}.
   * 
   * @param date the date value to be formatted into a string time if <code>true</code> use
   *          {@link #DATE_FORMAT}, otherwise use {@link #DATE_TIME_FORMAT}
   * @return the formatted date string, <code>dd.MM.yyyy</code>
   */
  public static String format(Date date, boolean time)
  {
    DateFormat df = getDateFormat(time ? DATE_TIME_FORMAT : DATE_FORMAT, null);
    return df.format(date);
  }

  /**
   * Formats milliseconds to "00 h : 00 m : 00 s 000 ms" format
   * 
   * @param ms
   * @return the time formated
   */
  public static String formatElapsedTime(long ms)
  {
    if(ms < 0)
      ms = 0;

    long s = 0;
    long m = 0;
    long h = 0;

    if(ms >= 1000)
      s = ms / 1000;
    ms = ms % 1000;
    if(s >= 60)
      m = s / 60;
    s = s % 60;
    if(m >= 60)
      h = m / 60;
    m = m % 60;

    List elements = new ArrayList();
    elements.add(THREE_DIGIT_FORMAT.format(ms) + " ms");
    if(s > 0)
      elements.add(TWO_DIGIT_FORMAT.format(s) + " s");
    if(m > 0)
      elements.add(TWO_DIGIT_FORMAT.format(m) + " m");
    if(h > 0)
      elements.add(TWO_DIGIT_FORMAT.format(h) + " h");

    Collections.reverse(elements);

    StringBuffer time = new StringBuffer();
    Iterator it = elements.iterator();
    while(it.hasNext())
    {
      String element = (String)it.next();
      time.append(element);
      if(it.hasNext())
      {
        time.append(" : ");
      }
    }

    return time.toString();
  }

  /**
   * Parses text from the beginning of the given string to produce a date.
   * <p>
   * Parsing insists on strict adherence, i.e. the input must be in the form defined by
   * {@link #DATE_FORMAT}.
   * 
   * @param source the date string to be parsed, <code>dd.MM.yyyy</code>
   * @return a Date, or <code>null</code> if the input could not be parsed
   */
  public static Date parse(String source)
  {
    try
    {
      DateFormat df = getDateFormat(DATE_FORMAT, null);
      return df.parse(source);
    }
    catch(ParseException e)
    {
      LOG.debug("Date string '" + source + "' could not be parsed", e);
      return null;
    }
  }

  /**
   * Parses text from the beginning of the given string to produce a date.
   * <p>
   * Parsing insists on strict adherence, i.e. the input must be in the form defined by
   * {@link #DATE_FORMAT}.
   * 
   * @param source the date string to be parsed.
   * @param format the format to be usewd for parsing.
   * @return a Date, or <code>null</code> if the input could not be parsed
   */
  public static Date parse(String source, String zone, String format)
  {
    try
    {
      DateFormat df = getDateFormat(format, zone);
      return df.parse(source);
    }
    catch(ParseException e)
    {
      LOG.debug("Date string '" + source + "' could not be parsed", e);
      return null;
    }
  }

  /**
   * Parses text from the beginning of the given string to produce a date.
   * <p>
   * Parsing insists on strict adherence, i.e. the input must be in the form specified by
   * {@link #DATE_FORMAT}.
   * 
   * @param source the date string to be parsed, <code>dd.MM.yyyy</code>
   * @param zone the ID for a <code>TimeZone</code>
   * @return a Date, or <code>null</code> if the input could not be parsed
   */
  public static Date parse(String source, String zone)
  {
    try
    {
      DateFormat df = getDateFormat(DATE_FORMAT, zone);
      return df.parse(source);
    }
    catch(ParseException e)
    {
      LOG.debug("Date string '" + source + "' could not be parsed", e);
      return null;
    }
  }

  /**
   * Cut off hours, minutes, seconds and millis in CET time.
   * <p>
   * It is expected that all clients using this class are in timezone CET, because cutting off hours
   * is incommensurate with properly supporting time zones.
   * <p>
   * This means that even though the behaviour of this method is well defined, it might not be very
   * intuitive for a user who has set his time zone to something different than CET.
   * <p>
   * For example if somebody in New York will calls this method at day x at 22:00 from within a VM
   * with default timezone EST, this function will return the date x at 18:00 because, this is x+1
   * 00:00 CET, which makes sense, because x 22:00 EST was in fact x+1 4:00 CET and cutting off that
   * date will result in x+1 0:00 CET or explained x 18:00 EST.
   * <p>
   * This is very confusing so it's best if all clients configure their timezone to CET.
   * 
   * @return a date representing day x at 00:00.00.0 CET or <code>null</code>.
   */
  public static Date truncate(Date date)
  {
    if(date == null)
    {
      return null;
    }
    return truncate(date.getTime());

    // if (date.getTime() == newDate.getTime()) {
    // return date;
    // } else {
    // return newDate;
    // }

  }

  /**
   * Cut off hours, minutes, seconds and millis in CET time.
   * <p>
   * It is expected that all clients using this class are in timezone CET, because cutting off hours
   * is incommensurate with properly supporting time zones.
   * <p>
   * This means that even though the behaviour of this method is well defined, it might not be very
   * intuitive for a user who has set his time zone to something different than CET.
   * <p>
   * For example if somebody in New York calls this method at day x at 22:00 from within a VM with
   * default timezone EST, this function will return the date x at 18:00 because, this is x+1 00:00
   * CET, which makes sense, because x 22:00 EST was in fact x+1 4:00 CET and cutting off that date
   * will result in x+1 0:00 CET or explained x 18:00 EST.
   * <p>
   * This is very confusing so it's best if all clients configure their timezone to CET.
   * 
   * @return time for day x at 00:00.00.0 CET in milliseconds.
   */
  private static Date truncate(long time)
  {
    return adjustDate(new Date(time), TIME_ZONE);
  }

  /**
   * Calculate the current CET day at 0:00:00.000 CET.
   * 
   * @return a date representing the current (CET) day at 00:00.00.0 CET.
   */
  public static final Date getCurrentDay()
  {
    // TODO Günter what is the offset for not scope related objects?
    Date date = DateUtil.addDays(newDate(), offset);
    return adjustDate(date, TIME_ZONE);
  }

  /**
   * adjust date for a given scope.
   * 
   * @return a date representing the current day at 00:00.00.0 for the given timezone.
   * 
   */
  protected static final Date adjustDate(Date date, TimeZone timezone)
  {
    long time = date.getTime();
    long offset = timezone.getOffset(time);

    time += offset;
    time -= time % MILLISECONDS_IN_DAY; // cut off hrs, mins, secs, millis

    return new Date(time - TIME_ZONE.getOffset(date.getTime()));
  }

  /**
   * Calculates the current Date
   * 
   * @return the current time stamp
   */
  public static final Date getCurrentTimeStamp()
  {
    return new Date(DateUtil.currentTimeMillis());
  }

  public static final long currentTimeMillis()
  {
    long millis = System.currentTimeMillis();
    if(offset != 0)
    {
      millis += (offset * MILLISECONDS_IN_DAY);
    }
    return millis;
  }

  /**
   * @param date the basis date
   * @param millis ms to add
   * @return the newly adjusted date
   */
  public static final Date addMillis(Date date, int millis)
  {
    if(date == null)
    {
      return null;
    }
    long milliseconds = date.getTime();
    long newMilliseconds = milliseconds + millis;
    return new Date(newMilliseconds);
  }

  /**
   * @param date the basis date
   * @param offset the number of days (+ve or -ve) to adjust by
   * @return the newly adjusted date
   */
  public static final Date addDays(Date date, int offset)
  {
    if(date == null)
    {
      return null;
    }
    long milliseconds = date.getTime();
    long newMilliseconds = milliseconds + (offset * MILLISECONDS_IN_DAY);
    return new Date(newMilliseconds);
  }

  /**
   * adss an number of days to the current date
   * 
   * @param offset the number of days (+ve or -ve) to adjust by
   * @return the newly adjusted date
   */
  public static final Date addDays(int offset)
  {
    return DateUtil.addDays(DateUtil.getCurrentDay(), offset);
  }

  public static Date addBusinessDays(int offset)
  {
    return DateUtil.addBusinessDays(DateUtil.getCurrentDay(), offset);
  }

  /**
   * @param date
   * @param offset
   * @return a business day
   */
  public static Date addBusinessDays(Date date, int offset)
  {

    Date result = date;

    for(int i = 0; i < Math.abs(offset);)
    {
      int days = 1;
      if(offset < 0)
      {
        days = -1;
      }

      result = addDays(result, days);
      if(isWeekDay(result))
      {
        i++;
      }
    }

    return result;
  }

  public static boolean isWeekDay(Date date)
  {
    int dayOfWeek = getDayOfWeek(date);
    return !(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
  }

  /**
   * @param date the basis date
   * @param offset the number of days (+ve or -ve) to adjust by
   * @return the newly adjusted date
   */
  public static final Date addDays(Date date, Date offset)
  {
    long milliseconds = date.getTime();
    long milliOffset = offset.getTime();
    long newMilliseconds = milliseconds + milliOffset;
    return new Date(newMilliseconds);
  }

  /**
   * @param date the basis date
   * @param offset the number of days (+ve or -ve) to adjust by
   * @return the newly adjusted date
   */
  public static final Date subtractDays(Date date, int offset)
  {
    long milliseconds = date.getTime();
    long newMilliseconds = milliseconds - (offset * MILLISECONDS_IN_DAY);
    return new Date(newMilliseconds);
  }

  public static final int getDifferenceInDays(Date date1, Date date2)
  {
    if(date1 == null || date2 == null)
      return 0;
    if(date1.equals(date2))
      return 0;
    long difference = date1.getTime() - date2.getTime();
    // if (date1.after(date2)) difference = ;
    // else difference = date2.getTime() - date1.getTime();

    difference = difference / (1000 * 3600 * 24);
    return (int)difference;
  }

  // **************************************************************
  // ***** Comparisons ********************************************
  // **************************************************************

  /**
   * Return true if date1 is strictly before date2. null is interpreted as infinite future (date
   * which will never reached).
   * 
   * @param date1 first date.
   * @param date2 second date.
   * @return true or false
   */
  public static final boolean isBefore(final Date date1, final Date date2)
  {
    if(date1 == null)
      return false;
    if(date2 == null) // d1 < d2 | d1 != null, d2 == null
      return true;

    return date1.getTime() < date2.getTime() ? true : false;
    // return truncate(date1).before(truncate(date2));
  }

  /**
   * Return true if date1 is equals to date2. null is interpreted as far future (date which will
   * never reached).
   * 
   * @param date1 first date.
   * @param date2 second date.
   * @return true or false
   */
  public static final boolean isEquals(final Date date1, final Date date2)
  {

    if(date1 == null || date2 == null)
    {
      return date1 == date2;
    }

    return date1.getTime() == date2.getTime();
  }

  public static final boolean isDayEqual(final Date date1, final Date date2)
  {
    Date d1 = DateUtil.truncate(date1);
    Date d2 = DateUtil.truncate(date2);
    return DateUtil.isEquals(d1, d2);
  }

  /**
   * Return true if date1 is before or equals to date2
   * 
   * @param date1 first date.
   * @param date2 second date.
   * @return true or false
   */
  public static final boolean isBeforeOrEquals(final Date date1, final Date date2)
  {
    return DateUtil.isBefore(date1, date2) || isEquals(date1, date2);
  }

  /**
   * Return true if date1 is after or equals to date2
   * 
   * @param date1 first date.
   * @param date2 second date.
   * @return true or false
   */
  public static final boolean isAfterOrEquals(final Date date1, final Date date2)
  {
    return isAfter(date1, date2) || isEquals(date1, date2);
  }

  /**
   * Return true if date1 is strictly after date2
   * 
   * @param date1 first date.
   * @param date2 second date.
   * @return true or false
   */
  public static final boolean isAfter(final Date date1, final Date date2)
  {
    return isBefore(date2, date1);
  }

  /**
   * Return true if from <= date < until. Null is interpreted as infinite in the future.
   * 
   * @param from the from date.
   * @param date the date to be checked.
   * @param until the end date.
   * @return true or false
   */
  public static final boolean isBetween(final Date from, final Date date, final Date until)
  {
    return isBeforeOrEquals(from, date) && isBefore(date, until);
  }

  /**
   * Return the maximal value of date1 and date2.
   * 
   * @param date1 to be used.
   * @param date2 to be used.
   * @return the maximal value of date1 and date2.
   */
  public static final Date max(final Date date1, final Date date2)
  {
    return isBefore(date1, date2) ? date2 : date1;
  }

  /**
   * Return the minimum value of date1 and date2.
   * 
   * @param date1 to be used.
   * @param date2 to be used.
   * @return the minimum value of date1 and date2.
   */
  public static final Date min(final Date date1, final Date date2)
  {
    return isBefore(date1, date2) ? date1 : date2;
  }

  /**
   * Creates a date in a gregorian calendar with the given hours and minutes.
   * <p>
   * Years, months and days of the returned Date will be set to zero.
   */
  public static Date createDate(int year, int month, int date, int hours, int minutes, int seconds)
  {
    // return new GregorianCalendar(year, month, date, hours,
    // minutes).getTime();
    Calendar calendar = new GregorianCalendar(TIME_ZONE);
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DATE, date);
    calendar.set(Calendar.HOUR_OF_DAY, hours);
    calendar.set(Calendar.MINUTE, minutes);
    calendar.set(Calendar.SECOND, seconds);
    return new Date(calendar.getTimeInMillis());
  }

  /**
   * Creates a date in a gregorian calendar with the given hours and minutes.
   * <p>
   * Years, months and days of the returned Date will be set to zero.
   */
  public static Date createDate(int year, int month, int date)
  {
    Calendar calendar = new GregorianCalendar(TIME_ZONE);
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DATE, date);
    return new Date(calendar.getTimeInMillis());
  }

  /**
   * Answers the best guess "asOfDate" for a given validFrom and validUntil date.
   * <ul>
   * <li> If the valid from date is in the future, the "asOfDate" is set to the valid from date 
   * </li> <li> During productive phase (between valid from and valid until) "today" is returned as
   * the "asOfDate" </li> <li> If the valid until date is in the past, the valid until date is used
   * </li>
   * </ul>
   * 
   * @param validFrom the relevant valid from date
   * @param validUntil the relevant valid until date
   * @return the best guess "asOfDate"
   */
  public static Date bitemporalAsOfDate(Date validFrom, Date validUntil)
  {
    Date today = getCurrentDay();
    Date asOfDate = validFrom;

    // valid from has been reached
    if(isBeforeOrEquals(validFrom, today))
    {
      asOfDate = today;
    }

    // valid until is in the past
    if(isBefore(validUntil, today))
    {
      asOfDate = validUntil;
    }

    return asOfDate;
  }

  /**
   * Helper method to get <code>DateFormat</code> used for formatting and parsing dates adhering to
   * the format {@link #DATE_FORMAT} or {@link #DATE_TIME_FORMAT}.
   * 
   */
  private static DateFormat getDateFormat(String format, String zone)
  {
    DateFormat df = new SimpleDateFormat(format);
    df.setLenient(false); // insist on strict adherence
    if(zone != null)
      df.setTimeZone(TimeZone.getTimeZone(zone));
    return df;
  }

  /**
   * @return date or the next business day if it is a weekend day
   */
  public static Date getNextBusinessDay(Date date)
  {
    return addBusinessDays(date, 1);
  }

  /**
   * @return date or the next business day if it is a weekend day
   */
  public static Date getPreviousBusinessDay(Date date)
  {
    return addBusinessDays(date, -1);
  }

  /**
   * Gets the day of the week. See Calendar.MONDAY etc.
   * 
   * Be aware that MONDAY is 2 and so on!
   * 
   * @param date
   * @return day of week.
   */
  public static int getDayOfWeek(Date date)
  {
    Calendar calendar = new GregorianCalendar(TIME_ZONE);
    calendar.setTime(date);
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    return calendar.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Creates a {@link Date} array holding all dates (as days) in the given date range
   * 
   * @param fromDate
   * @param toDate
   * @return dates between fromDate and toDate including the limits
   */
  public static Date[] getEveryDayBetweenIncl(Date fromDate, Date toDate)
  {
    fromDate = truncate(fromDate);
    toDate = truncate(toDate);
    // TODO delete, we have already MILLISECONDS_IN_DAY
    // int oneDayMilliSecs = 24 * 60 * 60 * 1000;

    List days = new ArrayList();
    long tmpDateInMilliSec = fromDate.getTime();
    days.add(fromDate); // incl from
    while(tmpDateInMilliSec < toDate.getTime())
    {
      // tmpDateInMilliSec = tmpDateInMilliSec + oneDayMilliSecs;
      tmpDateInMilliSec = tmpDateInMilliSec + MILLISECONDS_IN_DAY;
      days.add(new Date(tmpDateInMilliSec));
    }

    return (Date[])days.toArray(new Date[days.size()]);
  }

  /**
   * TODO lifecycle hack incremation of offset
   */
  public static void increment()
  {
    ++offset;
  }

  /**
   * TODO lifecycle hack decremenation of offset
   */
  public static void decrement()
  {
    --offset;
  }

  /**
   * TODO lifecycle hack decremenation of offset
   */
  public static void reset()
  {
    offset = 0;
  }

  public static long hoursToMillis(int hours)
  {
    return DateUtil.toMillis(hours, 0, 0, 0);
  }

  public static long minutesToMillis(int minutes)
  {
    return DateUtil.toMillis(0, minutes, 0, 0);
  }

  public static long toMillis(int hours, int minutes, int seconds)
  {
    return DateUtil.toMillis(hours, minutes, seconds, 0);
  }

  public static long toMillis(int hours, int minutes, int seconds, int millis)
  {
    long time = 0;
    time += hours * 60L * 60L * 1000L;
    time += minutes * 60L * 1000L;
    time += seconds * 1000L;
    time += millis;
    return time;
  }
}
