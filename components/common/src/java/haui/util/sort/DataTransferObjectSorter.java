/* *****************************************************************
 * Project: common
 * File:    DataTransferObjectSorter.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util.sort;

import haui.model.DataTransferObject;
import haui.util.ClassUtil;
import haui.util.SortUtil;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DataTransferObjectSorter
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class DataTransferObjectSorter
{

  private static Log LOG = LogFactory.getLog(DataTransferObjectSorter.class);

  private static final String COMPARE_METHOD = "compareTo";

  private static final String DTO_COMPARE_METHOD_WITH_ZERO = "compareToAllowZero";

  private static final Map methodMap = new HashMap();

  public Comparator getComparator()
  {
    return new DTOComparator();
  }

  public Comparator getComparator(boolean allowZero)
  {
    return new DTOComparator(allowZero);
  }

  public abstract String[] getSortFields();

  public int[] getSortOrders()
  {
    int size = getSortFields().length;
    int[] result = new int[size];
    for(int i = 0; i < size; ++i)
    {
      result[i] = SortUtil.ORDER_ASCENDING;
    }
    return result;
  }

  public class DTOComparator implements Comparator
  {

    /*
     * In order to be able to use this sorter safely as a comparator for sorted sets without losing
     * equal objects, the default is never to return 0 (if zero would be returned, stids are
     * returned). However, it is possible to return 0 if the objects are equal - this is necessary
     * when compare methods are called from within this comparator.
     */
    private final boolean allowZeroReturn;

    private String[] sortFields = getSortFields();

    private String[] alternateSortFields = null;

    /**
     * The default is never to return zero (if zero would be returned, stids are compared).
     */
    public DTOComparator()
    {
      this(false);
    }

    public DTOComparator(boolean allowZero)
    {
      this.allowZeroReturn = allowZero;
    }

    public String[] getAlternateSortFields()
    {
      return alternateSortFields;
    }

    public void setAlternateSortFields(String[] alternateSortFields)
    {
      this.alternateSortFields = alternateSortFields;
    }

    public String[] getRealSortFields()
    {
      String[] sort = null;
      if(getAlternateSortFields() != null)
        sort = getAlternateSortFields();
      else
        sort = sortFields;
      return sort;
    }

    public int compare(Object o1, Object o2)
    {
      int result = 0;
      boolean comparedObjectsAreDTOs = o1 instanceof DataTransferObject && o2 instanceof DataTransferObject;
      String[] sort = getRealSortFields();
      int[] sortingOrders = getSortOrders();
      Class[] types = new Class[1];
      types[0] = Object.class;

      // perform normal comparison first
      result = performComparison(o1, o2, sort, sortingOrders);

      // if the two objects are equal compare stids if possible
      if(!this.allowZeroReturn && result == 0 && comparedObjectsAreDTOs)
      {
        try
        {
          // compare stid (to avoid SortedSet problems)
          String[] newSort = new String[1];
          int[] newSortOrders = new int[1];
          newSort[0] = DataTransferObject.PN_STID;
          newSortOrders[0] = SortUtil.ORDER_ASCENDING;
          sort = newSort;
          sortingOrders = newSortOrders;
        }
        catch(Exception e)
        {
          LOG.error("Error adding stid to comparison", e);
        }
        result = performComparison(o1, o2, sort, sortingOrders);
      }

      return result;
    }

    private int performComparison(Object o1, Object o2, String[] sort, int[] sortingOrders)
    {
      int result = 0;
      Object[] args = new Object[0];
      Class[] types = new Class[1];
      types[0] = Object.class;

      try
      {
        KeyPair pair1 = new KeyPair(o1.getClass(), null);
        KeyPair pair2 = new KeyPair(o2.getClass(), null);

        for(int i = 0; i < sort.length && result == 0; ++i)
        {
          pair1.attribute = sort[i];
          pair2.attribute = sort[i];
          Method method1 = (Method)methodMap.get(pair1);
          Method method2 = (Method)methodMap.get(pair2);

          if(method1 == null)
          {
            method1 = ClassUtil.getGetterMethod(o1.getClass(), sort[i]);
            if(method1 != null)
              methodMap.put(pair1, method1);
          }
          if(method2 == null)
          {
            if(o1.getClass().equals(o2.getClass()))
              method2 = method1;
            else
            {
              method2 = ClassUtil.getGetterMethod(o2.getClass(), sort[i]);
              if(method2 != null)
                methodMap.put(pair2, method2);
            }
          }

          Object object1 = method1.invoke(o1, args);
          Object object2 = method2.invoke(o2, args);

          if(object1 == null && object2 == null)
            result = 0;
          else if(object1 == null && object2 != null)
          {
            result = 1;
          }
          else if(object1 != null && object2 == null)
          {
            result = -1;
          }
          else
          {
            String compareMethodName;
            if(object1 instanceof DataTransferObject)
            {
              compareMethodName = DTO_COMPARE_METHOD_WITH_ZERO;
            }
            else
            {
              compareMethodName = COMPARE_METHOD;
            }
            KeyPair comparePair = new KeyPair(object1.getClass(), compareMethodName);
            Method compareMethod = null;
            try
            {
              compareMethod = (Method)methodMap.get(comparePair);

              if(compareMethod == null)
              {
                compareMethod = ClassUtil.getDeclaredMethod(object1.getClass(), compareMethodName, types);
                methodMap.put(comparePair, compareMethod);
              }
            }
            catch(Exception e)
            {
              compareMethod = null;
              LOG.warn("There is no compare method " + compareMethod.getName() + " for the class " + object1.getClass().getName());
            }
            if(compareMethod != null)
            {
              Object[] params = new Object[1];
              params[0] = object2;

              Integer retVal = (Integer)compareMethod.invoke(object1, params);
              if(retVal != null)
              {
                result = retVal.intValue();
                if(sortingOrders[i] == SortUtil.ORDER_DESCENDING)
                  result = result * -1;
              }
            }
          }
        }
      }
      catch(Exception e)
      {
        LOG.error("Compare failed!", e);
      }
      return result;
    }
  }

  class KeyPair
  {

    Class clazz = null;

    String attribute = null;

    /**
     * @param clazz
     * @param attribute
     */
    public KeyPair(Class clazz, String attribute)
    {
      this.clazz = clazz;
      this.attribute = attribute;
    }

    public boolean equals(Object obj)
    {
      boolean result = false;
      if(obj instanceof KeyPair)
      {
        KeyPair pair = (KeyPair)obj;
        result = clazz.equals(pair.clazz) && attribute.equals(pair.attribute);
      }
      return result;
    }

    public int hashCode()
    {
      return clazz.hashCode() + attribute.hashCode();
    }
  }
}
