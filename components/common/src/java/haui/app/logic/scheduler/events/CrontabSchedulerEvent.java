/* *****************************************************************
 * Project: common
 * File:    CrontabSchedulerEvent.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler.events;

import haui.exception.AppSystemException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import haui.app.execution.AppExecutable;
import haui.util.DateUtil;

/**
 * CrontabSchedulerEvent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class CrontabSchedulerEvent extends AbstractSchedulerEvent
{
  protected TimeParser parser;

  public CrontabSchedulerEvent()
  {
  }

  public CrontabSchedulerEvent(String cronstring, AppExecutable executable)
  {
    super(executable);
    setCronString(cronstring);
  }

  public boolean isTimeForNextExecutionReached()
  {
    if(!isActive())
      return false;
    if(parser == null)
    {
      setActive(false);
      return false;
    }
    return parser.isTime(DateUtil.currentTimeMillis());
  }

  public String getCronString()
  {
    return parser == null ? null : parser.getTimeString();
  }

  public void setCronString(String cronstring)
  {
    parser = new TimeParser(cronstring);
  }

  public boolean isCronStringValid(String cronstring)
  {
    try
    {
      new TimeParser(cronstring);
    }
    catch(Exception e)
    {
      return false;
    }
    return true;
  }

  protected static final class TimeParser
  {

    private final Calendar c = Calendar.getInstance();
    private final String timestring;

    private int[] minutes;
    private int[] hours;
    private int[] days;
    private int[] months;
    private int[] daysofweek;

    private TimeParser(String timestring)
    {
      parse(timestring);
      this.timestring = timestring;
    }

    private void parse(String time)
    {
      if(time == null)
      {
        throw new AppSystemException("Timestring cannot be null!");
      }
      // Little bit of formatting
      time = replaceAll(time, "\n", " ");
      time = replaceAll(time, "\r", " ");
      while(time.indexOf(", ") > -1)
      {
        time = replaceAll(time, ", ", ",");
      }
      while(time.indexOf(",\t") > -1)
      {
        time = replaceAll(time, ",\t", ",");
      }

      StringTokenizer str = new StringTokenizer(time, "\t ");
      int tokenscount = str.countTokens();
      if(tokenscount != 5)
      {
        throw new AppSystemException("Timestring has to have 5 fields. Current # of fields: " + tokenscount);
      }

      minutes = parseField(str.nextToken(), 60, true);
      hours = parseField(str.nextToken(), 24, true);
      days = parseField(str.nextToken(), 31, false);
      months = parseField(str.nextToken(), 12, true);
      daysofweek = parseField(str.nextToken(), 7, false);

      // Sunday can be 0 or 7 (1 or 8) in Cron
      for(int n = 0; n < daysofweek.length; n++)
      {
        if(daysofweek[n] != -1)
        {
          daysofweek[n] = daysofweek[n] + 1;
        }
        if(daysofweek[n] == 8)
        {
          daysofweek[n] = 1;
        }
      }
    }

    private int[] parseField(String token, int maxlength, boolean zero)
    {
      int[] array = new int[maxlength];
      token = token.trim();
      if(token.equals("*"))
      {
        for(int n = 0; n < maxlength; n++)
        {
          array[n] = zero ? n : n + 1;
        }
        return array;
      }

      // handle hyphen characters
      int index = token.indexOf("-");
      if(index > -1)
      {
        try
        {
          String from = token.substring(0, index);
          String to = token.substring(index + 1, token.length());
          int f = Integer.parseInt(from.trim());
          int t = Integer.parseInt(to.trim());
          if(t < f)
          {
            throw new AppSystemException("parse error");
          }
          StringBuffer buffer = new StringBuffer();
          for(int n = f; n <= t; n++)
          {
            buffer.append(n).append(",");
          }
          if(buffer.toString().endsWith(","))
          {
            buffer.delete(buffer.length() - 1, buffer.length());
          }
          token = buffer.toString();
        }
        catch(Exception e)
        {
          throw new AppSystemException("Hyphen in string has to have the follwoing format: <number-from>-<number-to>");
        }
      }

      StringTokenizer str = new StringTokenizer(token, ",");
      int tokenscount = str.countTokens();
      if(tokenscount == 0)
        throw new AppSystemException("Given timestring is not valid!");
      List list = new ArrayList(tokenscount);
      while(str.hasMoreTokens())
      {
        String itoken = str.nextToken().trim();
        while(itoken.startsWith("0"))
        {
          itoken = itoken.substring(1, itoken.length()).trim();
        }
        while(itoken.length() < 3)
        {
          itoken = "0" + itoken;
        }
        if(!list.contains(itoken))
        {
          list.add(itoken);
        }
      }
      Collections.sort(list);
      int n = 0;
      for(Iterator it = list.iterator(); it.hasNext() && n < maxlength; n++)
      {
        String number = (String)it.next();
        try
        {
          array[n] = Integer.parseInt(number);
        }
        catch(Exception e)
        {
          throw new AppSystemException("Only numbers are allowed in a comma separated list. Not allowed: " + number);
        }
        int min = zero ? 0 : 1;
        int max = zero ? maxlength - 1 : maxlength;
        if(array[n] < min || array[n] > max)
        {
          throw new AppSystemException(array[n] + " is not in range (" + min + "-" + max + ")");
        }
      }
      for(int i = n; i < maxlength; i++)
      {
        array[i] = -1;
      }
      return array;
    }

    private String replaceAll(String string, String search, String replace)
    {
      int start = 0;
      int slength = search.length();
      StringBuffer buffer = null;
      for(int pos = 0; (pos = string.indexOf(search, start)) > -1; start = pos + slength)
      {
        if(buffer == null)
        {
          buffer = replace.length() <= slength ? new StringBuffer(string.length()) : new StringBuffer(string.length() + 30);
        }
        buffer.append(string.substring(start, pos)).append(replace);
      }
      return buffer == null ? string : buffer.append(string.substring(start)).toString();
    }

    private boolean contains(int[] array, int value)
    {
      for(int n = 0; n < array.length; n++)
      {
        if(array[n] == value)
          return true;
      }
      return false;
    }

    public String getTimeString()
    {
      return timestring;
    }

    public boolean isTime(Date date)
    {
      return isTime(date.getTime());
    }

    public boolean isTime(long millis)
    {
      c.setTimeInMillis(millis);
      if(!contains(months, c.get(Calendar.MONTH)))
        return false;
      if(!contains(daysofweek, c.get(Calendar.DAY_OF_WEEK)))
        return false;
      if(!contains(days, c.get(Calendar.DAY_OF_MONTH)))
        return false;
      if(!contains(hours, c.get(Calendar.HOUR_OF_DAY)))
        return false;
      if(!contains(minutes, c.get(Calendar.MINUTE)))
        return false;

      return true;
    }
  }
}
