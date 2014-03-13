/* *****************************************************************
 * Project: common
 * File:    ComparatorMap.java
 * 
 * Creation:     14.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util.sort;

import haui.exception.ValidationException;
import haui.model.DataTransferObject;
import haui.util.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassHierarchyMap: stores class hierarchies for the later quick search of a common superclass for two different classes
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ComparatorMap
{
  public static final String POSTFIX = "Sorter";

  final static private ComparatorMap instance = new ComparatorMap();

  private Map classMap = new HashMap();

  static public ComparatorMap getInstance()
  {
    return instance;
  }

  /**
   * search a common superclass for the two classes
   * 
   * @param clazz1
   * @param clazz2
   * @return common superclass
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IllegalArgumentException
   * @throws ValidationException
   */
  public Comparator getCommonComparator(Class clazz1, Class clazz2, boolean alternate, boolean allowZeroReturn) throws SecurityException,
      NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ValidationException
  {
    Comparator comparator = null;
    KeyPair pair = new KeyPair(clazz1, clazz2, allowZeroReturn);

    if(!alternate)
      comparator = (Comparator)classMap.get(pair);

    if(comparator == null)
    {
      // search common superclass
      Class classToCompare = null;

      if(clazz1.equals(clazz2))
      {
        classToCompare = clazz1;
      }
      else
      {
        Class tmpClass1 = clazz1;
        while(tmpClass1 != null && !tmpClass1.equals(DataTransferObject.class) && classToCompare == null)
        {
          Class tmpClass2 = clazz2;
          while(tmpClass2 != null && !tmpClass2.equals(DataTransferObject.class) && classToCompare == null)
          {
            if(tmpClass1.equals(tmpClass2))
            {
              classToCompare = tmpClass2;
            }
            tmpClass2 = tmpClass2.getSuperclass();
          }
          tmpClass1 = tmpClass1.getSuperclass();
        }
      }
      if(classToCompare == null)
      {
        classToCompare = getClassToCompare(clazz1, clazz2);
      }
      if(classToCompare != null)
      {
        // get compare class name without package path
        String compareClassName = getClassName(classToCompare);
        Class tmpClass = null;

        while(compareClassName != null)
        {
          // build sorter class path
          String comparePackageName = DataTransferObjectSorter.class.getPackage().getName();
          StringBuffer stringBuffer = new StringBuffer(comparePackageName);
          stringBuffer.append(".");
          stringBuffer.append(compareClassName);
          stringBuffer.append(POSTFIX);

          try
          {
            tmpClass = ClassUtil.loadClass(stringBuffer.toString());
          }
          catch(Exception e)
          {
            // ignore
            tmpClass = null;
          }

          if(tmpClass != null)
          {
            if(Modifier.isAbstract(tmpClass.getModifiers()))
            {
              classToCompare = getClassToCompare(clazz1, clazz2);
              if(classToCompare != null)
                compareClassName = getClassName(classToCompare);
            }
            else
            {
              Class[] types = new Class[0];
              Constructor constructor = tmpClass.getDeclaredConstructor(types);

              if(constructor != null)
              {
                Object[] objects = new Object[0];
                DataTransferObjectSorter sorter = (DataTransferObjectSorter)constructor.newInstance(objects);
                comparator = sorter.getComparator(allowZeroReturn);
              }
              compareClassName = null;
            }
          }
          else
          {
            classToCompare = classToCompare.getSuperclass();
            compareClassName = getClassName(classToCompare);
          }
        }
        if(comparator != null && !alternate)
        {
          classMap.put(pair, comparator);
        }
      }
    }
    return comparator;
  }

  private Class getClassToCompare(Class clazz1, Class clazz2) throws ValidationException
  {
    Class classToCompare = null;

    Class[] interfaces1 = getInterfaces(clazz1);
    Class[] interfaces2 = getInterfaces(clazz2);

    boolean found = false;
    for(int i = 0; i < interfaces1.length && !found; ++i)
    {
      Class interface1 = interfaces1[i];

      for(int ii = 0; ii < interfaces2.length; ++ii)
      {
        Class interface2 = interfaces2[ii];
        if(interface1.equals(interface2))
        {
          classToCompare = interface2;
          found = true;
          break;
        }
      }
    }
    if(classToCompare == null)
    {
      throw new ValidationException("No common comperator exists for classes: " + clazz1.getName() + ", " + clazz2.getName());
    }
    return classToCompare;
  }

  private Class[] getInterfaces(Class clazz)
  {
    ArrayList list = new ArrayList();
    Class tmpClass = clazz;
    while(tmpClass != null && !tmpClass.equals(DataTransferObject.class))
    {
      Class[] classes = tmpClass.getInterfaces();
      if(classes != null)
      {
        for(int i = 0; i < classes.length; ++i)
        {
          Class current = classes[i];
          list.add(current);
        }
      }
      tmpClass = tmpClass.getSuperclass();
    }
    return (Class[])list.toArray(new Class[list.size()]);
  }

  static private String getClassName(Class classToCompare)
  {
    String compareClassName = classToCompare.getName();
    int idx = compareClassName.lastIndexOf('.');
    compareClassName = compareClassName.substring(idx + 1);
    return compareClassName;
  }

  class KeyPair
  {

    Class clazz1 = null;

    Class clazz2 = null;

    boolean allowZeroReturn;

    /**
     * @param clazz1
     * @param clazz2
     */
    public KeyPair(Class clazz1, Class clazz2, boolean allowZeroReturnForDTOs)
    {
      this.clazz1 = clazz1;
      this.clazz2 = clazz2;
    }

    public boolean equals(Object obj)
    {
      boolean result = false;
      if(obj instanceof KeyPair)
      {
        KeyPair pair = (KeyPair)obj;
        result = ((clazz1.equals(pair.clazz1) && clazz2.equals(pair.clazz2)) || (clazz2.equals(clazz1) && clazz1.equals(clazz2)))
            && pair.allowZeroReturn == this.allowZeroReturn;
      }
      return result;
    }

    public int hashCode()
    {
      return clazz1.hashCode() + clazz2.hashCode() + (this.allowZeroReturn ? 0 : 1);
    }
  }
}
