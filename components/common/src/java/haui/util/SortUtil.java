/* *****************************************************************
 * Project: common
 * File:    SortUtil.java
 * 
 * Creation:     14.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import haui.exception.ValidationException;
import haui.util.sort.ComparatorMap;
import haui.util.sort.DataTransferObjectSorter.DTOComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * SortUtil
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SortUtil
{
  public static final int ORDER_ASCENDING = 0;
  public static final int ORDER_DESCENDING = 1;

  /**
   * get a common comparator for the given classes
   * 
   * @param feClass1 class which has FundamentalEntity as superclass
   * @param feClass2 class which has FundamentalEntity as superclass
   * @return Comperator which fits for both classes
   * @throws ValidationException
   */
  static public Comparator getComparator(Class feClass1, Class feClass2, boolean allowZeroReturn) throws ValidationException
  {
    Comparator comparator = null;
    Exception exception = null;
    try
    {
      comparator = ComparatorMap.getInstance().getCommonComparator(feClass1, feClass2, false, allowZeroReturn);
    }
    catch(Exception ex)
    {
      exception = ex;
    }
    if(comparator == null)
    {
      if(exception == null)
        throw new ValidationException("No common comperator exists for classes: " + feClass1.getName() + "and " + feClass2.getName() + " or its superclasses");
      else
        throw new ValidationException("No common comperator exists for classes: " + feClass1.getName() + "and " + feClass2.getName() + " or its superclasses",
            exception);
    }
    return comparator;
  }

  /**
   * get a common comparator for the given classes
   * 
   * @param feClass1 class which has FundamentalEntity as superclass
   * @param feClass2 class which has FundamentalEntity as superclass
   * @return Comperator which fits for both classes
   * @throws ValidationException
   */
  static public Comparator getAlternateComparator(Class feClass1, Class feClass2, String[] alternateSortFields) throws ValidationException
  {
    return getAlternateComparator(feClass1, feClass2, alternateSortFields, ORDER_ASCENDING);
  }

  /**
   * get a common comparator for the given classes
   * 
   * @param feClass1 class which has FundamentalEntity as superclass
   * @param feClass2 class which has FundamentalEntity as superclass
   * @return Comperator which fits for both classes
   * @throws ValidationException
   */
  static public Comparator getAlternateComparator(Class feClass1, Class feClass2, String[] alternateSortFields, int sortOrder) throws ValidationException
  {
    Comparator comparator = null;
    Exception exception = null;
    try
    {
      comparator = ComparatorMap.getInstance().getCommonComparator(feClass1, feClass2, true, false);
      if(comparator != null)
      {
        ((DTOComparator)comparator).setAlternateSortFields(alternateSortFields);
      }
    }
    catch(Exception ex)
    {
      exception = ex;
    }
    if(comparator == null)
    {
      if(exception == null)
        throw new ValidationException("No common comperator exists for classes: " + feClass1.getName() + "and " + feClass2.getName() + " or its superclasses");
      else
        throw new ValidationException("No common comperator exists for classes: " + feClass1.getName() + "and " + feClass2.getName() + " or its superclasses",
            exception);
    }
    return comparator;
  }

  /**
   * Create a sortet set from a collection. The objects in the collection must implement the
   * Comparable interface.
   * 
   * @param collection comparable objects
   * @return SortedSet
   */
  static public SortedSet sortCollection(Collection collection)
  {
    SortedSet sortedSet = new TreeSet(collection);
    return sortedSet;
  }
}
