/* *****************************************************************
 * Project: common
 * File:    AppUtil.java
 * 
 * Creation:     21.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import haui.exception.AppSystemException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AppUtil
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AppUtil
{

  private static final Log LOG = LogFactory.getLog(AppUtil.class);

  public static final int HASHCODE = 17;
  public static final int HASHCODE_MULTIPLIER = 37;

  /**
   * convert a value to a specified type (there must be a constructor with string argument for the
   * type)
   * 
   * @param type class to convert to
   * @param value value to convert
   * @return converted value or if failed to convert the original value
   */
  public static Object convertToType(String type, Object value, String datePattern)
  {
    Object object = null;
    String valueToConvert = null;
    if(value != null && type != null)
    {
      if(value instanceof String)
      {
        valueToConvert = (String)value;
      }
      else
      {
        valueToConvert = value.toString();
      }
      try
      {
        Class clazz = ClassUtil.loadClass(type);
        if(clazz != null)
        {
          if(value.getClass().equals(clazz))
          {
            object = value;
          }
          else if(clazz.equals(String.class))
          {
            object = valueToConvert;
          }
          else if(clazz.equals(Date.class))
          {
            try
            {
              SimpleDateFormat format = new SimpleDateFormat(datePattern);
              object = format.parse(valueToConvert);
            }
            catch(Exception e)
            {
              long time = Date.parse(valueToConvert);
              object = new Date(time);
            }
          }
          else
          {
            Class[] parameterTypes = new Class[1];
            parameterTypes[0] = String.class;
            Constructor<?> constructor = clazz.getConstructor(parameterTypes);

            if(constructor != null)
            {
              Object[] args = new Object[1];
              args[0] = valueToConvert;
              object = constructor.newInstance(args);
            }
          }
        }
      }
      catch(Exception e)
      {
        LOG.error("Can't instanciate type: " + type, e);
      }
    }
    if(object == null)
      object = value;
    return object;
  }

  /**
   * Adds an object to the given array.
   * 
   * @param object the object to be added.
   * @param array the array to be used to add the object.
   * @return a new array containing the new model.
   */
  public static Object[] add(Object object, Object[] array)
  {
    Object[] target;
    // create a new array of object
    if(array == null)
    {
      target = (Object[])Array.newInstance(object.getClass(), 1);
      target[0] = object;
    }
    else
    {
      target = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
      System.arraycopy(array, 0, target, 0, array.length);
      target[array.length] = object;
    }
    return target;
  }

  /**
   * Copy a Collection. The content of the given collection is copied into the new Collection.
   * 
   * @param collection the collection to be copied.
   * @return a copy of the given collection.
   */
  public static Collection copy(Collection collection)
  {
    Collection newCollection = null;
    if(collection != null)
    {
      newCollection = (Collection)create(collection.getClass());
      newCollection.addAll(collection);
    }
    return newCollection;
  }

  /**
   * Copy a Map. The content of the given map is copied into the new Map
   * 
   * @param map the map to be copied.
   * @return a copy of the given map.
   */
  public static Map copy(Map map)
  {
    Map newMap = (Map)create(map.getClass());
    newMap.putAll(map);
    return newMap;
  }

  /**
   * Copy an array.
   * 
   * @param array the array to be copied.
   * @return a copy of the given array
   */
  public static Object[] copy(Object[] array)
  {
    // create a new array of models
    Object[] target = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length);
    System.arraycopy(array, 0, target, 0, array.length);
    return target;
  }

  /**
   * Create a new object defined by the given one. No content is copied.
   * 
   * @param clazz the class to be instantied.
   * @return a new instantiated object.
   */
  public static Object create(Class clazz)
  {
    try
    {
      return clazz.newInstance();
    }
    catch(InstantiationException ie)
    {
      throw new AppSystemException("Failed to create " + getShortName(clazz), ie);
    }
    catch(IllegalAccessException iae)
    {
      throw new AppSystemException("Failed to create " + getShortName(clazz), iae);
    }
  }

  /**
   * test equality with null test (if both are null returns <code>true</code>).
   * 
   * @return <code>true</code> if both values are <code>null</code> or the equals returns true.
   */
  public static boolean equals(Object object1, Object object2)
  {
    if(null == object1 && null == object2)
    {
      return true;
    }

    if(null == object1)
    {
      return false;
    }

    if(null == object2)
    {
      return false;
    }

    return object1.equals(object2);
  }

  public static boolean equals(Object[] objects)
  {
    if(objects == null || objects.length == 0 || objects.length % 2 != 0)
    {
      throw new IllegalArgumentException();
    }
    for(int n = 0; n < objects.length; n = n + 2)
    {
      if(!AppUtil.equals(objects[n], objects[n + 1]))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Answers a display String representation of an input date, in the format DD-MM-YYYY. Answers an
   * empty string if the date is null.
   * 
   * @param aDate
   * @return String value
   */
  public static String getDisplayString(Date aDate)
  {
    StringBuffer buffer = new StringBuffer();
    if(aDate != null)
    {
      Calendar cal = new GregorianCalendar();
      cal.setTime(aDate);
      buffer.append(cal.get(Calendar.DATE));
      buffer.append("-");
      buffer.append(cal.get(Calendar.MONTH) + 1);
      buffer.append("-");
      buffer.append(cal.get(Calendar.YEAR));
    }
    return buffer.toString();
  }

  /**
   * Answers a display String representation of an input object. Answers an empty string if the
   * object is null.
   * 
   * @param aObject
   * @return String value
   */
  public static String getDisplayString(Object aObject)
  {
    String result = "";
    if(aObject != null)
    {
      result = aObject.toString();
    }
    return result;
  }

  /**
   * Retreive the name of the class wihtout package information.
   * 
   * @return the shortname.
   */
  public static String getShortName(Object obj)
  {
    String name = obj.getClass().getName();
    return name.substring(name.lastIndexOf(".") + 1);
  }

  /**
   * Returns a hash code value for the given key object.
   * 
   * @param multiplier to be used to calculate a new hash code value.
   * @param hashCode to be used as offset value.
   * @param object to be used to construct the hashCode.
   * @return a hash code value for this object.
   */
  public static int hashCode(int multiplier, int hashCode, Object object)
  {
    if(object == null)
    {
      return hashCode * multiplier;
    }
    return multiplier * hashCode + object.hashCode();
  }

  /**
   * Returns a hash code value for the given key object.
   * 
   * @param hashCode to be used as offset value.
   * @param object to be used to construct the hashCode.
   * @return a hash code value for this object.
   */
  public static int hashCode(int hashCode, Object object)
  {
    return hashCode(HASHCODE_MULTIPLIER, hashCode, object);
  }

  /**
   * Returns a hash code value for the given key object.
   * 
   * @param object to be used to construct the hashCode.
   * @return a hash code value for this object.
   */
  public static int hashCode(Object object)
  {
    return AppUtil.hashCode(HASHCODE, object);
  }

  /**
   * Returns a hash code value for the given array of primary key objects.
   * 
   * @param keys a array of objects used to construct the hashCode.
   * @return a hash code value for this object.
   */
  public static int hashCode(Object[] keys)
  {
    int hashCode = HASHCODE;
    if(keys == null)
    {
      return hashCode;
    }

    for(int i = 0; i < keys.length; i++)
    {
      hashCode = hashCode(hashCode, keys[i]);
    }
    return hashCode;
  }

  /**
   * test the collection ==<code>null</code> or size() == 0
   */
  public static boolean isEmpty(Collection col)
  {
    if(col == null || col.isEmpty())
      return true;
    for(Iterator it = col.iterator(); it.hasNext();)
    {
      if(it.next() != null)
        return false;
    }
    return true;
  }

  /**
   * Checks whether the Collection has a particular number of entries that are not null
   * 
   * @param number the number of elements
   * @return true if there are the number of elements in this collections which are not null
   */
  public static boolean hasNumberOfEntries(Collection col, int number)
  {
    if(col == null || number > col.size() || number < 0)
    {
      return false;
    }
    int count = 0;
    for(Iterator it = col.iterator(); it.hasNext();)
    {
      Object o = it.next();
      if(o != null)
      {
        count++;
      }
    }
    return count == number;
  }

  /**
   * test the string ==<code>null</code> or length() == 0
   */
  public static boolean isEmpty(String string)
  {
    if(null == string || string.length() == 0)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Tests whether an input object == <code>null</code>.
   */
  public static boolean isNull(Object aObject)
  {
    return aObject == null;
  }

  /**
   * Tests whether an input object array == <code>null</code> or is empty.
   */
  public static boolean isEmpty(Object[] aObjectArray)
  {
    if(aObjectArray == null || aObjectArray.length == 0)
      return true;
    for(int i = 0; i < aObjectArray.length; i++)
    {
      if(aObjectArray[i] != null)
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Removes all occurences of the given object from the given array.
   * 
   * @param object the object to be removed.
   * @param array the array to be used to remove the object.
   * 
   * @return a new array containing the new model.
   */
  public static Object[] remove(Object object, Object[] array)
  {
    boolean removed = false;

    Object[] target = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length);

    for(int i = 0, j = 0; i < target.length; i++)
    {
      if(AppUtil.equals(array[i], object) == false)
      {
        target[j++] = array[i];
      }
      else
      {
        removed = true;
      }
    }
    if(removed)
    {
      Object[] target2 = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);
      System.arraycopy(target, 0, target2, 0, target2.length);
      return target2;
    }
    return target;
  }

  /**
   * Searches for the same object (applying the equals method) in a given collection and returns it
   * (without changing the collection) if found.
   * 
   * @param aObject the object to be found in the collection
   * @param aCollection the collection where the object is sought
   * 
   * @return the object found in the collection
   */
  public static Object getEqualObject(Object aObject, Collection aCollection)
  {
    assert aObject != null: "AppUtil>>getEqualObject - aObject is null";
    assert aCollection != null: "AppUtil>>getEqualObject - aCollection is null";

    Object result = null;
    Iterator iterator = aCollection.iterator();
    Object item = null;
    while(iterator.hasNext())
    {
      item = iterator.next();
      if(aObject.equals(item))
      {
        result = item;
        break;
      }
    }
    return result;
  }

  /**
   * Deflates the given byte array.
   * 
   * @param data the byte array to be deflated.
   * @return a byte array containing the deflated data or null if the input byte array was null
   * 
   * @throws ArteSystemException if something went wrong
   */
  public static byte[] deflateData(byte[] data)
  {
    if(data == null)
      return null;
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DeflaterOutputStream deflater = new DeflaterOutputStream(bout);
    copyStream(in, deflater, true);
    LOG.debug("deflated " + data.length + " to " + bout.size() + " ratio: " + (double)bout.size() / (double)data.length);
    return bout.toByteArray();
  }

  /**
   * Inflates the given byte array.
   * 
   * @param data the byte array to be inflated
   * @return a byte array containing the inflated data or null if the input byte array was null
   * 
   * @throws ArteSystemException if something went wrong
   */
  public static byte[] inflateData(byte[] data)
  {
    if(data == null)
      return null;
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    InflaterInputStream inflater = new InflaterInputStream(bin);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    copyStream(inflater, bout, true);
    LOG.debug("inflated " + data.length + " to " + bout.size() + " compression-ratio: " + (double)data.length / (double)bout.size());
    return bout.toByteArray();
  }

  /**
   * Reads everything from the given stream into an byte array.
   * 
   * @param in the {@link InputStream} to read from
   * @param close <code>true</code> if the steam should be closed at the end, <code>false</code>
   *          otherwise
   * 
   * @return a byte array containing all data read fro the given input stream
   * @throws ArteSystemException
   */
  public static byte[] readStreamToByteArray(InputStream in, boolean close)
  {
    assert in != null: "AppUtil>>copyStream - InputStream is null";

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    copyStream(in, bout, close);
    return bout.toByteArray();
  }

  /**
   * Reads everything from the give {@link InputStream} and writes it to the given
   * {@link OutputStream}.
   * 
   * @param in the {@link InputStream} to read from
   * @param out the {@link OutputStream} wo write to
   * @param close <code>true</code> if the steams should be closed at the end, <code>false</code>
   *          otherwise
   * 
   * @throws ArteSystemException if somethings goes wrong
   */
  public static void copyStream(InputStream in, OutputStream out, boolean close)
  {
    assert in != null: "AppUtil>>copyStream - InputStream is null";
    assert out != null: "AppUtil>>copyStream - InputStream is null";

    try
    {
      byte[] buffer = new byte[1024];
      int len;

      while((len = in.read(buffer)) > 0)
      {
        out.write(buffer, 0, len);
      }

      out.flush();
    }
    catch(IOException ioe)
    {
      throw new AppSystemException("Error during stream copy", ioe);
    }
    finally
    {
      if(close)
      {
        close(in);
        close(out);
      }
    }
  }

  /**
   * Closes an {@link InputStream}
   * 
   * @param out the InputStream
   */
  public static final void close(InputStream in)
  {
    try
    {
      if(in != null)
      {
        in.close();
      }
    }
    catch(IOException ioe)
    {
      LOG.warn("Failed to close input stream!", ioe);
    }
  }

  /**
   * Closes an {@link OutputStream}
   * 
   * @param out the OutputStream
   */
  public static final void close(OutputStream out)
  {
    try
    {
      if(out != null)
      {
        out.close();
      }
    }
    catch(IOException ioe)
    {
      LOG.warn("Failed to close output stream!", ioe);
    }
  }

  /**
   * Utility to load a file. It tris the following options 1.) Load as relatice/or absolute file 2.)
   * Try to load from an URL 3.) Try to load from classpath
   * 
   * @param filename
   * @return {@link InputStream}
   */
  public static final InputStream load(String filename)
  {
    InputStream in = null;

    // First try to get it as file
    if((in = loadAsFile(filename)) != null)
    {
      return in;
    }
    // try to get it as URL
    if((in = loadAsURL(filename)) != null)
    {
      return in;
    }

    // try to get from the classpath.
    if((in = loadFromClassPath(filename)) != null)
    {
      return in;
    }
    throw new AppSystemException(filename);
  }

  /**
   * Answers the current memmory usage of the vm.
   * 
   * @return
   */
  public static long getMemoryUsage()
  {
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

  private static final InputStream loadFromClassPath(String filename)
  {
    InputStream in = null;
    try
    {
      in = AppUtil.class.getResourceAsStream(filename);
      if(in != null && LOG.isDebugEnabled())
      {
        LOG.debug("Open " + filename + " from classpath.");
      }
    }
    catch(Exception e)
    {}
    return in;
  }

  private static final InputStream loadAsURL(String filename)
  {
    InputStream in = null;
    try
    {
      in = new URL(filename).openStream();
      if(LOG.isDebugEnabled())
      {
        LOG.debug("Open " + filename + " as URL");
      }
    }
    catch(Exception e)
    {
      if(!filename.startsWith("file://"))
      {
        in = loadAsURL("file://" + filename);
      }
    }
    return in;
  }

  private static final InputStream loadAsFile(String filename)
  {
    InputStream in = null;
    try
    {
      in = new FileInputStream(new File(filename));
      if(LOG.isDebugEnabled())
      {
        LOG.debug("Open " + filename + " as File");
      }
    }
    catch(Exception e)
    {
      if(!filename.startsWith("/"))
      {
        in = loadAsFile("/" + filename);
      }
    }
    return in;
  }

  public static void unique(List list)
  {
    synchronized(list)
    {
      List l = new ArrayList(list.size());
      for(Iterator it = list.iterator(); it.hasNext();)
      {
        Object o = it.next();
        if(!l.contains(o))
        {
          l.add(o);
        }
      }
      list.clear();
      list.addAll(l);
    }
  }

  public static List toList(Object object)
  {
    return AppUtil.toList(new Object[] { object });
  }

  public static List subList(List list, int fromIndex, int toIndex)
  {

    // do not use List.subList .. create a new List
    // return list == null ? Collections.EMPTY_LIST : list.subList(fromIndex, toIndex);

    if(list == null)
      return Collections.EMPTY_LIST;
    if(fromIndex < 0)
      throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
    if(toIndex > list.size())
      throw new IndexOutOfBoundsException("toIndex = " + toIndex);
    if(fromIndex > toIndex)
      throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");

    List newlist = new ArrayList((toIndex - fromIndex));
    for(int n = fromIndex; n < toIndex; n++)
    {
      newlist.add(list.get(n));
    }
    return newlist;
  }

  public static List toList(Object[] objects)
  {
    // do not use Arrays.asList .. create a real ArrayList
    // return objects == null ? Collections.EMPTY_LIST : Arrays.asList(objects);

    if(objects == null)
      return Collections.EMPTY_LIST;
    List list = new ArrayList(objects.length);
    for(int n = 0; n < objects.length; n++)
    {
      list.add(objects[n]);
    }
    return list;
  }

  public static Set toSet(Object object)
  {
    return AppUtil.toSet(new Object[] { object });
  }

  public static Set toSet(Object[] objects)
  {
    if(objects == null)
      return Collections.EMPTY_SET;
    Set set = new HashSet(objects.length);
    for(int n = 0; n < objects.length; n++)
    {
      if(objects[n] == null)
        continue;
      set.add(objects[n]);
    }
    return set;
  }

  public static Map toMap(List keys, List values)
  {
    if(keys == null || values == null)
      return Collections.EMPTY_MAP;

    Object[] k = keys.toArray(new Object[keys.size()]);
    Object[] v = values.toArray(new Object[values.size()]);
    return toMap(k, v);
  }

  public static Map toMap(Object[] keys, Object[] values)
  {
    if(keys == null || values == null)
      return Collections.EMPTY_MAP;
    Map map = new HashMap(keys.length);
    for(int n = 0; n < keys.length; n++)
    {
      if(keys[n] == null)
        continue;
      map.put(keys[n], values[n]);
    }
    return map;
  }

  public static String getStackTrace(Throwable t)
  {
    StringWriter writer = new StringWriter();
    t.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }
}
