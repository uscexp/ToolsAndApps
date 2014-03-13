/* *****************************************************************
 * Project: common
 * File:    AppReflectionToStringBuilder.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AppReflectionToStringBuilder
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AppReflectionToStringBuilder extends ReflectionToStringBuilder
{

  private static final Log LOG = LogFactory.getLog(AppReflectionToStringBuilder.class);

  private boolean appendNullValues = false;

  /**
   * Construct a new builder using the default style.
   * 
   * @param object the Object to build a toString for, not <code>null</code>
   */
  public AppReflectionToStringBuilder(Object object)
  {
    super(object);
  }

  /**
   * Construct a new builder using the given style.
   * 
   * @param object the Object to build a toString for, not <code>null</code>
   * @param style the style of the toString to create, if <code>null</code> then the default style
   *          is used.
   */
  public AppReflectionToStringBuilder(Object object, ToStringStyle style)
  {
    super(object, style);
  }

  /**
   * Returns <code>true</code> if <code>null</code> values are to be appended.
   * <p>
   * Default is <code>false</code>.
   */
  public boolean isAppendNullValues()
  {
    return appendNullValues;
  }

  /**
   * Set to <code>true</code> to append <code>null</code> values.
   */
  public void setAppendNullValues(boolean appendNullValues)
  {
    this.appendNullValues = appendNullValues;
  }

  private static Class hibernateCollection = null;
  static
  {
    try
    {
      // are we running on a system with hibernate?
      hibernateCollection = ClassUtil.loadClass("org.hibernate.collection.PersistentCollection");
    }
    catch(ClassNotFoundException e)
    {
      LOG.debug("Hibernate not found. Excluding fields holding a hibernate collections! ");
    }
  }

  /**
   * Returns whether or not to append the given <code>Field</code>.
   * <ul>
   * <li>Transient fields are appended only if {@link #isAppendTransients()} returns <code>true
   * </code>. <li>Static fields are appended only if {@link #isAppendStatics()} returns <code>true
   * </code>. <li><code>null</code> values are appended only if {@link #isAppendNullValues()}
   * returns <code>false</code>. <li>Collections are only appended if they are either of type:
   * <ul>
   * <li><code>java.util.HashSet</code> <li>or <code>java.util.ArrayList</code> <li>or <code>
   * org.hibernate.collection.PersistentCollection</code> AND were initialized.
   * </ul>
   * <li>Inner class fields are not appened.
   * </ul>
   * 
   * @param field the field to test
   * @return whether or not to append the given field
   */
  protected boolean accept(Field field)
  {
    Object value = null;
    try
    {
      value = getValue(field);
      if(value == null)
        return appendNullValues;
    }
    catch(IllegalAccessException e)
    {
      /*
       * this can't happen. We would get a Security exception instead. In any case throw a runtime
       * exception, if the impossible happens...
       */
      throw new InternalError("Unexpected IllegalAccessException: " + e.getMessage());
    }

    Class clazz = value.getClass(); // get runtime type
    // special treatment for hibernate collections
    if(hibernateCollection != null && ClassUtils.isAssignable(clazz, hibernateCollection))
    {
      try
      {
        // accept field only if collection has been initialized
        Boolean initialized = (Boolean)MethodUtils.invokeMethod(value, "wasInitialized", null);
        return initialized.booleanValue();
      }
      catch(NoSuchMethodException e)
      {
        LOG.debug("Method 'wasInitialized()' not found. Excluding field '" + field.getName() + "'.");
        return false;
      }
      catch(IllegalAccessException e)
      {
        LOG.debug("Unexpected IllegalAccessException. Excluding field '" + field.getName() + "'.");
        return false;
      }
      catch(InvocationTargetException e)
      {
        LOG.debug("Unexpected InvocationTargetException. Excluding field '" + field.getName() + "'.");
        return false;
      }
    }

    return super.accept(field);
  }
}
