/* *****************************************************************
 * Project: common
 * File:    STIDGenerator.java
 * 
 * Creation:     12.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.common.id;

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generates stids.
 * <p>
 * The alogarithm used is the same as the one used by hibernate.
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public class STIDGenerator
{

  private static final Log LOG = LogFactory.getLog(STIDGenerator.class);

  // part separator
  private String sep = "";

  // singleton instance
  private static final STIDGenerator instance = new STIDGenerator();

  /**
   * generates a new uuid
   * 
   * @return the generated uuid string
   */
  public synchronized static String generate()
  {
    StringBuffer stid = new StringBuffer(36).append(instance.format(instance.getIP())).append(instance.sep).append(instance.format(instance.getJVM())).append(
        instance.sep).append(instance.format(instance.getHiTime())).append(instance.sep).append(instance.format(instance.getLoTime())).append(instance.sep)
        .append(instance.format(instance.getCount()));

    return stid.toString();
  }

  /**
   * Returns a standard identifier with internal stid generation mechanism.
   * 
   * @return a standard identifier.
   * @throws IllegalStateException
   */
  public static synchronized final String getStandardIdentifier()
  {
    String stid = generate().substring(16, 32);
    return stid;
  }

  private String format(int intval)
  {
    String formatted = Integer.toHexString(intval);
    StringBuffer buf = new StringBuffer("00000000");
    buf.replace(8 - formatted.length(), 8, formatted);
    return buf.toString();
  }

  private String format(short shortval)
  {
    String formatted = Integer.toHexString(shortval);
    StringBuffer buf = new StringBuffer("0000");
    buf.replace(4 - formatted.length(), 4, formatted);
    return buf.toString();
  }

  private static final int IP;
  static
  {
    int ipadd;
    try
    {
      ipadd = toInt(InetAddress.getLocalHost().getAddress());
    }
    catch(Exception e)
    {
      ipadd = 0;
    }
    IP = ipadd;
  }

  private static short counter = (short)0;

  private static final int JVM = (int)(System.currentTimeMillis() >>> 8);

  /**
   * Unique across JVMs on this machine (unless they load this class in the same quater second -
   * very unlikely)
   */
  private int getJVM()
  {
    return JVM;
  }

  /**
   * Unique in a millisecond for this JVM instance (unless there are > Short.MAX_VALUE instances
   * created in a millisecond)
   */
  private short getCount()
  {
    if(counter < 0)
      counter = 0;
    return counter++;
  }

  /**
   * Unique in a local network
   */
  private int getIP()
  {
    return IP;
  }

  /**
   * Unique down to millisecond
   */
  private short getHiTime()
  {
    return (short)(System.currentTimeMillis() >>> 32);
  }

  private int getLoTime()
  {
    return (int)System.currentTimeMillis();
  }

  private static int toInt(byte[] bytes)
  {
    int result = 0;
    for(int i = 0; i < 4; i++)
    {
      result = (result << 8) - Byte.MIN_VALUE + bytes[i];
    }
    return result;
  }
}
