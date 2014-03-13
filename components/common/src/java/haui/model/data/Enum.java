/* *****************************************************************
 * Project: common
 * File:    Enum.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.data;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An abstract superclass for all code holders.
 * 
 * This is an alternative Superclass for code objects, it automatically uses the name of the public static field as the
 * code string for each instance. in addition to that it implements singleton behaviour thus equality can be checket
 * using the == operator.
 * 
 * <pre>
 * RuleDTO rule = ... // a RuleDTO instance 
 * if (rule.getRuleType().getAffectedObjectTypeCode() == RuleAffectedObjectTypeCode.TEMPLATE) {
 *  // ... do something ...
 * }
 * </pre>
 * 
 * in order for this to work, a property is to be used in hibernate mappings instead of a component! the propery must
 * use the com.ubs.arte.app.dao.impl.hibernate.CodeType user type.<br>
 * for an example see RuleTypeDTO.hbm.xml in the src/hibernate folder.
 * 
 * <p>
 * subclasses must implement readResolve() like this:
 * 
 * <pre>
 * private Object readResolve() throws ObjectStreamException {
 *     return Enum.getCode(getClass(), getCode());
 * }
 * </pre>
 * 
 * ...this is required for singleton behaviour to work across multiple jvms (serialization)
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Enum implements IEnum, Serializable, Comparable {

  private static final long serialVersionUID = 3512437153618768696L;

  public static final transient String PN_CODE = "code";

  private String code = null;

  protected Enum() {}

  /**
   * 
   * @return the code value
   */
  public String getCode() {
      
      if (code != null) return code;

      try {
          Class cls = getClass();
          Field[] fields = cls.getFields();
          for (int i = 0; i < fields.length; i++) {

              Object object = fields[i].get(this);
              // if (fields[i].getType() == cls) {
              if (Enum.class.isAssignableFrom(fields[i].getType()) || object instanceof IEnum) {
                  if (object.equals(this)) {
                      code = fields[i].getName();
                      break;
                  }
              }
          }
          if (code == null) {
              // should never happen
              throw new RuntimeException("No Enum-code found for class: " + getClass());
          }
          return code;
      } catch (IllegalAccessException iae) {
          throw new RuntimeException(iae);
      }
  }

  // a hashtable holding all singleton instances for all AbstractCode2
  // subclasses.
  // // uses lazy initialization.
  // // this is used by the getCode(Class codeClass, String name) method
  // private static Hashtable availableCodeInstancesPerSubclass = new
  // Hashtable();
  //
  // private static Hashtable getAvailableCodeInstances(Class codeClass) {
  // // verify that codeClass inherits from AbstractCode2
  // Class currentSuperClass = codeClass;
  // while (currentSuperClass != Object.class && currentSuperClass !=
  // Enum.class) {
  // currentSuperClass = currentSuperClass.getSuperclass();
  // }
  //
  // if (currentSuperClass != Enum.class) {
  // throw new IllegalStateException("codeClass must inherit from " +
  // Enum.class);
  // }
  //
  // Hashtable availableCodeInstances = (Hashtable)
  // availableCodeInstancesPerSubclass.get(codeClass);
  // if (availableCodeInstances == null) {
  // availableCodeInstances = new Hashtable();
  // availableCodeInstancesPerSubclass.put(codeClass, availableCodeInstances);
  //
  // // find all available codes from the public static fields of the class
  // Field[] fields = codeClass.getFields();
  //
  // for (int i = 0; i < fields.length; i++) {
  //
  // if (fields[i].getType() == codeClass &&
  // Modifier.isStatic(fields[i].getModifiers()) &&
  // Modifier.isPublic(fields[i].getModifiers())) {
  //
  // try {
  // availableCodeInstances.put(fields[i].getName(), fields[i].get(null));
  // } catch (IllegalArgumentException e) {
  // e.printStackTrace();
  // } catch (IllegalAccessException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // }
  // }
  // return availableCodeInstances;
  // }

  private static final Map instances = new HashMap();

  private static Map getAvailableCodeInstances(Class codeClass) {

      Map map = (Map) instances.get(codeClass);
      if (map != null) {
          return map;
      }

      try {
          map = new HashMap();
          instances.put(codeClass, map);

          List classes = getAllSuperclasses(codeClass);
          classes.add(0, codeClass);
          for (Iterator it = classes.iterator(); it.hasNext();) {
              Class cls = (Class) it.next();
              if (cls.equals(Enum.class) || cls.equals(Object.class)) break;
              Field[] fields = cls.getFields();
              for (int i = 0; i < fields.length; i++) {
                  if (Enum.class.isAssignableFrom(fields[i].getType()) || IEnum.class.isAssignableFrom(fields[i].getType())) {
                      String key = fields[i].getName();
                      if (!map.containsKey(key)) {
                          map.put(key, fields[i].get(null));
                      }
                  }
              }
          }

          return map;

      } catch (IllegalAccessException iae) {
          throw new RuntimeException(iae);
      }
  }

  private static List getAllSuperclasses(Class cls) {
      // return org.apache.commons.lang.ClassUtils.getAllSuperclasses(cls);

      if (cls == null) return null;
      List classes = new ArrayList();
      Class superclass = cls.getSuperclass();
      while (superclass != null) {
          classes.add(superclass);
          superclass = superclass.getSuperclass();
      }
      return classes;
  }

  /**
   * finds the correct singleton instance for a codeClass (subclass of AbstractCode2) and a code string.
   * 
   * @param codeClass
   * @param code
   * @return the singleton instance
   */
  public static Enum getCode(Class codeClass, String code) {
      Map availableCodeInstances = getAvailableCodeInstances(codeClass);
      Enum codeInstance = (Enum) availableCodeInstances.get(code);
      return codeInstance;
  }

  public static Collection getAllInstances(Class codeClass) {
      Collection availableInstances = getAvailableCodeInstances(codeClass).values();
      return Collections.unmodifiableCollection(availableInstances);
  }

  public int hashCode() {
      return getCode().hashCode();
  }

  /**
   * implement writeObject method (serialization api) to make sure the code propertiy is initialized!
   * 
   * @param stream
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
      getCode();
      stream.defaultWriteObject();
  }

  public int compareTo(Object object) {
      try {
          Enum that = (Enum) object;
          return this.getCode().compareTo(that.getCode());
      } catch (Exception e) {}
      return 0;
  }

  public String toString() {
      return getCode();
  }
}
