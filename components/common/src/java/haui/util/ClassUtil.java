/* *****************************************************************
 * Project: common
 * File:    ClassUtil.java
 * 
 * Creation:     21.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import haui.exception.AppSystemException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

/**
 * Provides class utility functionality.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ClassUtil
{

  private static final Log LOG = LogFactory.getLog(ClassUtil.class);

  private static final String MODIFIERS_FIELD = "modifiers";

  private static final ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();

  // **************************************************************
  // ***** Public Method-specific *********************************
  // **************************************************************

  public static Object newInstance(String className) throws Exception
  {
    Class clazz = loadClass(className);
    return newInstance(clazz);
  }

  public static Object newInstance(Class clazz) throws Exception
  {
    Constructor constructor = clazz.getDeclaredConstructor(new Class[0]);
    boolean accessible = constructor.isAccessible();
    constructor.setAccessible(true);
    Object newObject = constructor.newInstance(new Object[0]);
    constructor.setAccessible(accessible);
    return newObject;
  }

  public static Object getSingletonInstance(String className)
  {
    Class clazz = null;
    Object instance = null;
    if(instance == null)
    {
      try
      {
        clazz = ClassUtil.loadClass(className);

        if(clazz != null)
        {
          Class[] parameterTypes = new Class[0];
          Method method = null;
          try
          {
            method = clazz.getMethod("getInstance", parameterTypes);
          }
          catch(NoSuchMethodException e)
          {
            method = null;
          }
          if(method == null)
          {
            method = clazz.getMethod("instance", parameterTypes);
          }

          instance = method.invoke(clazz, new Object[0]);
        }
      }
      catch(Exception e)
      {
        throw new AppSystemException("Failed to load class " + className, e);
      }
    }
    return instance;
  }

  public static Class loadClass(String className) throws ClassNotFoundException
  {
    Class clazz = null;
    try
    {
      clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
    }
    catch(ClassNotFoundException exc)
    {
      clazz = null;
    }
    try
    {
      if(clazz == null)
      {
        clazz = Class.forName(className);
      }
    }
    catch(ClassNotFoundException exc)
    {
      throw exc;
    }
    return clazz;
  }

  /**
   * Returns the value of the given field or null.
   * 
   * @param instance
   * @param field
   * @return
   */
  public static Object getValue(Object instance, Field field)
  {
    try
    {
      boolean accessible = field.isAccessible();
      field.setAccessible(true);
      Object value = field.get(instance);
      field.setAccessible(accessible);
      return value;
    }
    catch(Exception e)
    {
      LOG.warn("Failed to access field " + field.getName(), e);
    }
    return null;
  }

  /**
   * Returns an array of all fields for a specified class including all inherited ones.
   * 
   * @param clazz
   * @return Field[] array
   */
  public static final Field[] getDeclaredFields(Class clazz)
  {
    ArrayList declaredFields = new ArrayList();
    declaredFields.addAll(Arrays.asList(clazz.getDeclaredFields()));

    if(clazz.getSuperclass() != null)
    {
      declaredFields.addAll(Arrays.asList(getDeclaredFields(clazz.getSuperclass())));
    }
    return (Field[])declaredFields.toArray(new Field[declaredFields.size()]);
  }

  /**
   * Returns a field for a specified class including all inherited ones.
   * 
   * @param clazz
   * @return Field[] array
   * @throws NoSuchFieldException
   * @throws SecurityException
   */
  public static final Field getDeclaredField(Class clazz, String fieldName)
  {
    Field field = null;

    while(clazz != null)
    {
      try
      {
        field = clazz.getDeclaredField(fieldName);
        if(field == null)
          clazz = clazz.getSuperclass();
        else
          break;
      }
      catch(Exception e)
      {
        clazz = clazz.getSuperclass();
      }
    }
    return field;
  }

  /**
   * Return the assumed field name from PN attribute.
   * 
   * @param clazz
   * @param pnAttribute
   * @return variableName
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   */
  public static String getFieldName(Class clazz, String pnAttribute) throws IllegalArgumentException, IllegalAccessException
  {
    Field field = null;
    String fieldName = null;

    while(clazz != null)
    {
      try
      {
        field = clazz.getDeclaredField(pnAttribute);
        if(field == null)
          clazz = clazz.getSuperclass();
        else
          break;
      }
      catch(Exception e)
      {
        clazz = clazz.getSuperclass();
      }
    }

    if(field != null)
    {
      fieldName = (String)field.get(null);
    }
    return fieldName;
  }

  /**
   * Returns a getter method for a specified PN_-attribute and class including all inherited ones.
   * 
   * @param clazz
   * @param pnAttribute
   * @return Field
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws NoSuchMethodException
   * @throws SecurityException
   */
  public static final Method getDeclaredGetMethod(Class clazz, String pnAttribute) throws IllegalArgumentException, IllegalAccessException, SecurityException,
      NoSuchMethodException
  {
    Field field = null;
    StringBuffer methodText = new StringBuffer();
    Method method = null;

    while(clazz != null)
    {
      try
      {
        field = clazz.getDeclaredField(pnAttribute);
        if(field == null)
          clazz = clazz.getSuperclass();
        else
          break;
      }
      catch(Exception e)
      {
        clazz = clazz.getSuperclass();
      }
    }

    if(field != null)
    {
      String fieldText = (String)field.get(null);
      methodText.append(fieldText);
      methodText.setCharAt(0, Character.toUpperCase(fieldText.charAt(0)));
      if(field.getType().equals(Boolean.class))
        methodText.insert(0, "is");
      else
        methodText.insert(0, "get");
      Class[] clazzs = new Class[0];
      method = clazz.getDeclaredMethod(methodText.toString(), clazzs);
    }
    return method;
  }

  /**
   * Returns an array of all methods for a specified class including all inherited ones.
   * 
   * @param clazz
   * @return Field[] array
   */
  public static final Method[] getDeclaredMethods(Class clazz)
  {
    ArrayList declaredMethods = new ArrayList();
    declaredMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));

    if(clazz.getSuperclass() != null)
    {
      declaredMethods.addAll(Arrays.asList(getDeclaredMethods(clazz.getSuperclass())));
    }
    return (Method[])declaredMethods.toArray(new Method[declaredMethods.size()]);

  }

  /**
   * Returns a collection of public static methods for a specified class.
   * 
   * @param aClass
   * @return Collection(Method)
   */
  public static Collection getPublicMethods(Class aClass)
  {
    ArrayList publicMethods = new ArrayList();
    Method[] tmpMethods = aClass.getMethods();
    for(int i = 0; i < tmpMethods.length; i++)
    {
      publicMethods.add(tmpMethods[i]);
    }
    return publicMethods;
  }

  /**
   * Answers the public method in a specified class, matching the given name, parameter and return
   * type. Ignores checks against the parameter and return types if null.
   * 
   * @param aClass
   * @param aMethodName
   * @param aParamTypes
   * @param aReturnType
   * @return Method
   * @throws IllegalArgumentException if mandatory input data is null
   * @throws ArteSystemException if no method was found
   */
  public static Method getPublicMethod(Class aClass, String aMethodName, Class[] aParamTypes, Class aReturnType)
  {

    assert aClass != null: "ClassUtil>>getPublicMethod - aClass is null";
    assert aMethodName != null && aMethodName.length() > 0: "ClassUtil>>getPublicMethod - aMethodName is null/empty";

    Method result = null;

    // iterate through methods for match
    Method[] classMethods = aClass.getMethods();
    Method classMethod = null;
    for(int i = 0; i < classMethods.length; i++)
    {
      classMethod = classMethods[i];
      if(compareMethodWithSpecs(classMethod, aMethodName, aParamTypes, aReturnType))
      {
        result = classMethod;
        break;
      }
    }
    if(result == null)
    {
      throw new AppSystemException(aMethodName);
    }
    return result;
  }

  /**
   * Return the getter method of an instance variable named in a specified class.
   * 
   * @param aClass
   * @param aInstanceVariableName
   * @return Method
   * @throws IllegalArgumentException if mandatory input data is null
   * @throws ArteSystemException if no getter method for the given attribute was found
   */
  public static Method getPublicGetterMethod(Class aClass, String aInstanceVariableName) throws AppSystemException
  {
    String methodName = "get" + StringUtil.changeFirstCharToUpperCase(aInstanceVariableName);
    return getPublicMethod(aClass, methodName, new Class[0]);
  }

  /**
   * Return the getter method of an instance variable named in a specified class.
   * 
   * @param aClass
   * @param aInstanceVariableName
   * @return Method
   * @throws IllegalArgumentException if mandatory input data is null
   * @throws ArteSystemException if no getter method for the given attribute was found
   */
  public static Method getGetterMethod(Class aClass, String aInstanceVariableName) throws AppSystemException
  {
    Method method = null;

    String methodName = StringUtil.changeFirstCharToUpperCase(aInstanceVariableName);
    try
    {
      method = getDeclaredMethod(aClass, "get" + methodName, new Class[0]);
    }
    catch(AppSystemException ase)
    {
      method = getDeclaredMethod(aClass, "is" + methodName, new Class[0]);
    }
    return method;
  }

  /**
   * Return the assumed field name for a getter method.
   * 
   * @param method the method
   * @return variableName
   */
  public static String getFieldName(Method method) throws AppSystemException
  {
    String name = method.getName();

    if(name.startsWith("get"))
    {
      return StringUtil.changeFirstCharToLowerCase(name.substring(3, name.length()));
    }
    Class returnType = method.getReturnType();
    if(returnType.isPrimitive() && "boolean".equals(returnType.getName()))
    {
      if(name.startsWith("is"))
      {
        return StringUtil.changeFirstCharToLowerCase(name.substring(2, name.length()));
      }
    }
    throw new AppSystemException("field of the method " + name + " not found!");
  }

  public static Class getAttributeType(Class aClass, String aInstanceVariableName) throws AppSystemException
  {
    Class fieldType = null;
    try
    {
      fieldType = aClass.getDeclaredField(aInstanceVariableName).getType();
    }
    catch(NoSuchFieldException nsfe)
    {
      if(aClass.getSuperclass() == null)
      {
        throw new AppSystemException(aInstanceVariableName + " not found in " + aClass.getName(), nsfe);
      }
      else
      {
        fieldType = getAttributeType(aClass.getSuperclass(), aInstanceVariableName);
      }
    }
    return fieldType;
  }

  /**
   * Return the getter method of an instance variable named in a specified class.
   * 
   * @param aClass
   * @param aMethodName
   * @return Method
   * @throws IllegalArgumentException if mandatory input data is null
   * @throws ArteSystemException if no method was found
   */
  public static Method getDeclaredMethod(Class aClass, String aMethodName, Class[] aParamTypes) throws AppSystemException
  {
    Method method = null;

    try
    {
      method = aClass.getDeclaredMethod(aMethodName, aParamTypes);
    }
    catch(NoSuchMethodException nsme)
    {
      if(aClass.getSuperclass() == null)
      {
        throw new AppSystemException(getMethodSignature(aMethodName, aParamTypes) + " not found in " + aClass.getName(), nsme);
      }
      else
      {
        method = getDeclaredMethod(aClass.getSuperclass(), aMethodName, aParamTypes);
      }
    }
    return method;
  }

  /**
   * Return the public getter method of an instance variable named in a specified class.
   * 
   * @param aClass
   * @param aMethodName
   * @return Method
   * @throws IllegalArgumentException if mandatory input data is null
   * @throws ArteSystemException if no method was found
   */
  public static Method getPublicMethod(Class aClass, String aMethodName, Class[] aParamTypes) throws AppSystemException
  {
    try
    {
      return aClass.getMethod(aMethodName, aParamTypes);
    }
    catch(NoSuchMethodException nsme)
    {
      throw new AppSystemException(getMethodSignature(aMethodName, aParamTypes) + " not found in " + aClass.getName(), nsme);
    }
  }

  public final static String getMethodSignature(String aMethodName, Class[] aParamTypes)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(aMethodName);
    buffer.append("(");
    for(int i = 0; i < aParamTypes.length; i++)
    {
      buffer.append(aParamTypes[i]);
      if(i != aParamTypes.length - 1)
      {
        buffer.append(", ");
      }
    }
    buffer.append(")");
    return buffer.toString();
  }

  // Initialize mapping of primitive to Objects
  static private HashMap<Object, Object> primitiveObjectMap = new HashMap<Object, Object>();

  static
  {
    primitiveObjectMap.put(java.lang.Boolean.class, java.lang.Boolean.TYPE);
    primitiveObjectMap.put(java.lang.Long.class, java.lang.Long.TYPE);
    primitiveObjectMap.put(java.lang.Double.class, java.lang.Double.TYPE);
    primitiveObjectMap.put(java.lang.Float.class, java.lang.Float.TYPE);
    primitiveObjectMap.put(java.lang.Integer.class, java.lang.Integer.TYPE);
  }

  /**
   * Return the setter method of an instance variable named in a specified class. It doesnt matter
   * if the class is public, protected or private
   * 
   * @param aClass
   * @param aInstanceVariableName
   * @param aParamType
   * @return Method
   * @throws IllegalArgumentException if mandatory input data is null
   * @throws ArteSystemException if no method was found
   */
  public static Method getSetterMethod(Class aClass, String aInstanceVariableName, Class aParamType)
  {
    Class[] paramTypes = { aParamType };
    String methodName = "set" + StringUtil.changeFirstCharToUpperCase(aInstanceVariableName);
    Method method = null;
    try
    {
      method = getDeclaredMethod(aClass, methodName, paramTypes);
    }
    catch(AppSystemException ase)
    {
      paramTypes[0] = (Class)primitiveObjectMap.get(aParamType);
      if(paramTypes[0] == null)
      {
        throw ase;
      }
      method = getDeclaredMethod(aClass, methodName, paramTypes);
    }
    return method;
  }

  // **************************************************************
  // ***** General methods ****************************************
  // **************************************************************

  /**
   * Answers whether an input method has the same input method name, parameter types and return
   * type. Ignores checks against the parameter and return types if null.
   * 
   * @param aMethod
   * @param aMethodName
   * @param aParamTypes - input parameter types
   * @param aReturnType
   * @return boolean
   */
  private static boolean compareMethodWithSpecs(Method aMethod, String aMethodName, Class[] aParamTypes, Class aReturnType)
  {

    return (aMethod.getName().equalsIgnoreCase(aMethodName) && (aReturnType == null || aReturnType.equals(aMethod.getReturnType())) && (aParamTypes == null || compareClassArrays(
        aParamTypes, aMethod.getParameterTypes())));
  }

  private static boolean compareClassArrays(Class[] array1, Class[] array2)
  {
    boolean same = false;
    int array1Length = array1.length;
    if(array1Length == array2.length)
    {
      for(int i = 0; i < array1Length; i++)
      {
        Class array1Class = array1[i];
        Class array2Class = array2[i];
        if(!array1Class.equals(array2Class))
        {
          break;
        }
      }
      same = true;
    }
    return same;
  }

  /**
   * Create a class for the given class name.
   * 
   * @param className to be created.
   * @return the new class.
   * @throws ArteSystemException in case the class was not found.
   */
  public static Class create(String className) throws AppSystemException
  {
    try
    {
      Class clazz = loadClass(className);
      return clazz;
    }
    catch(ClassNotFoundException cnfe)
    {
      throw new AppSystemException("can't create " + className, cnfe);
    }

  }

  /**
   * Instantiat a new class for the given class name.
   * 
   * @param clazz to be created.
   * @return the new object.
   * @throws ArteSystemException in case the class was not found.
   */
  public static Object instantiate(Class clazz) throws AppSystemException
  {
    try
    {
      return clazz.newInstance();
    }
    catch(InstantiationException ie)
    {
      LOG.warn("Failed to instantiate new instance of " + clazz.getName() + ". " + ie);
      throw new AppSystemException("Failed to instantiate new instance of " + clazz.getName(), ie);
    }
    catch(IllegalAccessException iae)
    {
      throw new AppSystemException("Failed to instantiate new instance of " + clazz.getName(), iae);
    }
  }

  /**
   * Instantiat a new class for the given class name.
   * 
   * @param clazz to be created.
   * @return the new object.
   * @throws ArteSystemException in case the class was not found.
   */
  public static Object instantiate(String className) throws AppSystemException
  {
    try
    {
      Class clazz = ClassUtil.loadClass(className);
      return clazz.newInstance();
    }
    catch(InstantiationException ie)
    {
      LOG.warn("Failed to instantiate new instance of " + className + ". " + ie);
      throw new AppSystemException("Failed to instantiate new instance of " + className, ie);
    }
    catch(IllegalAccessException iae)
    {
      throw new AppSystemException("Failed to instantiate new instance of " + className, iae);
    }
    catch(ClassNotFoundException e)
    {
      throw new AppSystemException("Failed to load class " + className, e);
    }
  }

  /**
   * Find all subclasses for a given class in the classloader hierarchy of ClassUtil
   * 
   * @param clazz for which all subclasses should be found.
   * @return a list of subclasses.
   */
  public static List findAllSubClasses(Class clazz)
  {
    ArrayList list = new ArrayList();
    Package[] pcks = Package.getPackages();
    for(int i = 0; i < pcks.length; i++)
    {
      list.addAll(findAllSubClasses(pcks[i], clazz));
    }
    return list;

  }

  /**
   * Find all subclasses for a given class in the given package and all its subpackages. Only
   * packages inside "com.ubs.arte" are considered.
   * 
   * @param pckg the package to be used as starting point for the find.
   * @param clazz for which all subclasses should be found.
   * @return a list of subclasses.
   */
  public static List findAllSubClasses(Package pckg, Class clazz)
  {

    ArrayList list = new ArrayList();

    String name = new String(pckg.getName());

    if(!name.startsWith("com.ubs.arte"))
    {
      return list;
    }
    if(!name.startsWith("/"))
    {
      name = "/" + name;
    }
    name = name.replace('.', '/');

    // Get a File object for the package
    URL url = ClassUtil.class.getResource(name);
    if(url == null)
    {
      return list;
    }

    File directory = new File(url.getFile()).getAbsoluteFile();

    if(directory.exists())
    {
      // Get the list of the files contained in the package
      String[] files = directory.list();
      File file;
      for(int i = 0; i < files.length; i++)
      {
        file = new File(files[i]);
        if(file.isDirectory())
        {
          String newPackagename = pckg.getName();
          newPackagename += newPackagename.substring(newPackagename.lastIndexOf('.'), newPackagename.length());
          list.addAll(findAllSubClasses(Package.getPackage(newPackagename), clazz));
        }
        else
        {
          // we are only interested in .class files
          if(files[i].endsWith(".class"))
          {
            // removes the .class extension
            String classname = pckg.getName() + "." + files[i].substring(0, files[i].length() - 6);
            try
            {
              // Try to create an instance of the object
              Class subClazz = loadClass(classname);
              if(clazz.isAssignableFrom(subClazz))
              {
                list.add(subClazz);
              }
            }
            catch(ClassNotFoundException cnfe)
            {
              LOG.warn("Unable to instantiate " + classname + " class! " + cnfe);
            }
          }
        }
      }
    }
    return list;
  }

  /**
   * Retreive the name of the class wihtout package information.
   * 
   * @return the shortname.
   */
  public static String getShortName(Class clazz)
  {
    String name = clazz.getName();
    return name.substring(name.lastIndexOf(".") + 1);
  }

  public static void setStaticFinalField(Field field, Object value) throws NoSuchFieldException, IllegalAccessException
  {
    // we mark the field to be public
    field.setAccessible(true);
    // next we change the modifier in the Field instance to
    // not be final anymore, thus tricking reflection into
    // letting us modify the static final field
    Field modifiersField = Field.class.getDeclaredField(MODIFIERS_FIELD);
    modifiersField.setAccessible(true);
    int modifiers = modifiersField.getInt(field);
    // blank out the final bit in the modifiers int
    modifiers &= ~Modifier.FINAL;
    modifiersField.setInt(field, modifiers);
    FieldAccessor fa = reflection.newFieldAccessor(field, false);
    fa.set(null, value);
  }
  
  public static Object invokeStaticMethod(Class clazz, String name, Class[] parameterTypes, Object[] parameters) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
  {
    if(parameterTypes == null && parameters != null)
    {
      parameterTypes = new Class[parameters.length];
      for(int i = 0; i < parameters.length; ++i)
      {
        parameterTypes[i] = parameters.getClass();
      }
    }
    Method method = clazz.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method.invoke(null, parameters);
  }
}
