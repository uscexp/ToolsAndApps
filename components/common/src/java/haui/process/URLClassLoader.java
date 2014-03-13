/* *****************************************************************
 * Project: common
 * File:    URLClassLoader.java
 * 
 * Creation:     04.05.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.process;

import haui.common.id.STIDGenerator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Module:      URLClassLoader<br>
 *<p>
 * Description: Generic classloader for URL-based classpaths.
 * <p>
 * TODO: add archive support, and rewrite as this is currently a hack
 *</p><p>
 * Created:     04.05.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     04.05.2006 by AE: Created.<br>
 *</p><p>
 * @author Luke Gorrie<br>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class URLClassLoader extends java.net.URLClassLoader
{

  protected Vector _urlClassPath;
  protected Hashtable _classCache;
  protected Hashtable domains = new Hashtable();
  protected String id = STIDGenerator.getStandardIdentifier();

  /**
   * Creates a ClassLoader with no classpath.
   **/
  // GG: Commented out
  /*
   * public URLClassLoader() { _urlClassPath = new Vector(); _classCache = new Hashtable(); }
   */

  /**
   * Creates a ClassLoader with a single URL for a classpath.
   * 
   * @param classpath The base URL to search for classes relative to.
   **/
  // GG: Commented out
  /*
   * public URLClassLoader(URL classpath) { this(); addClassPath(classpath); }
   */

  /**
   * Creates a ClassLoader with a list of classpath URLs.
   * 
   * @param classpath The URLs to search for classes relative to.
   **/
  public URLClassLoader(URL[] classpath)
  {
    // GG: Changed
    super((classpath == null) ? new URL[0] : classpath);
    _urlClassPath = new Vector();
    _classCache = new Hashtable();
    addClassPath(classpath);
  }

  /**
   * get unique id
   * @return the id
   */
  public String getId()
  {
    return id;
  }

  public void addClassPath(URL[] classpath)
  {
    if(classpath != null)
    {
      for(int i = 0; i < classpath.length; i++)
        addClassPath(classpath[i]);
    }
  }

  /**
   * Add a URL to the classpath. This URL is searched for for classes.
   * 
   * @param classpath The base URL to search.
   **/
  public void addClassPath(URL classpath)
  {
    if(classpath == null)
      return;
    // GG : Transform the .jar or .zip path to jar: url
    if((classpath.getFile().toLowerCase().endsWith(".zip")) || (classpath.getFile().toLowerCase().endsWith(".jar")))
    {
      try
      {
        _urlClassPath.addElement(new URL("jar:" + classpath.toString() + "!/"));
      }
      catch(MalformedURLException exception)
      {}
    }
    else
      _urlClassPath.addElement(classpath);
  }

  public Class loadMainClass(String name) throws ClassNotFoundException
  {
    return loadClass(name, true);
  }

  // GG: Commented out

  /*
   * public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
   * 
   * return loadClass(name, resolve, false); }
   * 
   * public Class loadClass(String name, boolean resolve, boolean mainClass) throws
   * ClassNotFoundException {
   * 
   * Class klass = null;
   * 
   * try {
   * 
   * // give the vm a chance to find it in the classpath try { klass = findSystemClass(name); if
   * (klass != null) return klass; } catch (ClassNotFoundException e) { }
   * 
   * // check cache klass = (Class)_classCache.get(name); if (klass != null) { return klass; }
   * 
   * // read from custom classpath URL []pathUsed=new URL [1]; byte[] data = readClassFile(name,
   * pathUsed); if (data != null) { if (mainClass) { forcePublic(data); } if
   * (!domains.containsKey(pathUsed[0])) { synchronized (this) { if
   * (!domains.containsKey(pathUsed[0])) { domains.put(pathUsed[0], new ProtectionDomain (new
   * CodeSource (pathUsed[0],null), null)); } } } klass = defineClass(name, data, 0, data.length,
   * (ProtectionDomain)domains.get (pathUsed[0]) ); }
   * 
   * if (klass == null) throw new ClassNotFoundException("Class not found: " + name);
   * 
   * _classCache.put(name, klass);
   * 
   * return klass;
   * 
   * } finally { // if the class was found, and is to be resolved, resolve if ((klass != null) &&
   * resolve) resolveClass(klass); } }
   */

  /**
   * Try to read the byte[] data for a class file from the classpath.
   * 
   * @param name The fully-qualified name of the class.
   * @return A byte[] containing the classfile data, or null if not found.
   **/
  protected byte[] readClassFile(String classname, URL[] pathUsed)
  {
    classname = classname.replace('.', '/') + ".class";
    return readFile(classname, pathUsed);
  }

  // GG: Commented out
  /*
   * public InputStream getResourceAsStream(String name) { byte[] data = readFile(name); return data
   * == null ? null : new ByteArrayInputStream(data); }
   */

  // bit of a hack. :-)
  public URL getResource(String name)
  {
    // GG: Called superclass function
    return super.getResource(name);
    /*
     * URL path = null; Enumeration classpath = _urlClassPath.elements(); while
     * (classpath.hasMoreElements()) { URL base_path = (URL)classpath.nextElement(); try { path =
     * new URL(base_path, name); return path; } catch (IOException e) { // file not accessible or
     * doesn't exist } } return null;
     */
  }

  protected byte[] readFile(String name)
  {
    return readFile(name, null);
  }

  protected byte[] readFile(String name, URL pathUsed[])
  {

    // enumeration of base-urls to try
    Enumeration classpath = _urlClassPath.elements();
    byte[] data = null;
    URL base_path;

    // loop until a class is read, or there are no more paths to try
    while((data == null) && (classpath.hasMoreElements()))
    {

      // pop a path to try
      base_path = (URL)classpath.nextElement();

      try
      {

        // create a fully qualified class url
        URL path = new URL(base_path, name);

        // io streams
        ByteArrayOutputStream out_buffer = new ByteArrayOutputStream();
        InputStream in = new BufferedInputStream(path.openStream());

        // read into buffer
        int octet;
        while((octet = in.read()) != -1)
          out_buffer.write(octet);

        // pull class data out of buffer
        data = out_buffer.toByteArray();

        if(pathUsed != null)
          pathUsed[0] = base_path;

      }
      catch(IOException e)
      {
        // class not found in that path
      }

    }

    // null if class not found
    return data;

  }

  /**
   * Converts a path string into an array of URLs. eg. "foo:http://bar/" would become a 2-URL array,
   * with "file:///foo/" and "http://bar/".
   * 
   * @param classpath The path to decode, entries delimited by the appropriate charactor for the
   *          platform.
   **/
  public static URL[] decodePathString(String classpath)
  {

    // create a file:/// as a base URL. This was "foo" will be seen as
    // file:///foo
    URL base_url = null;
    try
    {
      base_url = new URL("file:/");
    }
    catch(MalformedURLException e)
    {}

    // vector to store URLs in temporarily
    Vector classpath_urls = new Vector();

    // if a base url is provided and the classpath is non-null
    if((base_url != null) && (classpath != null))
    {

      // tokenize path
      StringTokenizer tok = new StringTokenizer(classpath, ",");
      while(tok.hasMoreTokens())
      {
        String path = tok.nextToken();
        // create URL for path entry

        URL path_url = null;
        try
        {
          path_url = new URL(path);
        }
        catch(MalformedURLException e)
        {
          try
          {
            path_url = new URL(base_url, path);
          }
          catch(Exception e2)
          {}
        }

        if(path_url != null)
          classpath_urls.addElement(path_url);
      }
    }

    URL[] paths = null;

    // if there are any valid classpath entries
    if(!classpath_urls.isEmpty())
    {
      // convert the vector into a URL array
      paths = new URL[classpath_urls.size()];
      for(int i = 0; i < paths.length; i++)
        paths[i] = (URL)classpath_urls.elementAt(i);
    }

    // return path url array
    return paths;

  }

  /**
   * Take a byte array of class data and modify it to ensure that the class is public. This is used
   * for the "main" classes of applications.
   */
  public void forcePublic(byte[] theClass)
  {
    int constant_pool_count = ((theClass[8] & 0xff) << 8) | (theClass[9] & 0xff);

    int currOffset = 10;

    // seek through everything in the way of the access modifiers
    for(int i = 1; i < constant_pool_count; i++)
    {
      switch(theClass[currOffset] & 0xff)
      {
        case 7:
        case 8: // CONSTANT_Class, CONSTANT_String
          currOffset += 3;
          break;
        case 9:
        case 10:
        case 11:
        case 12:
        case 3:
        case 4: // CONSTANT_Fieldref, CONSTANT_Methodref
          currOffset += 5; // CONSTANT_InterfaceMethodref, CONSTANT_NameAndType
          break; // CONSTANT_Integer, CONSTANT_Float
        case 5:
        case 6: // CONSTANT_Long, CONSTANT_Double
          currOffset += 9;
          i++;
          break;
        case 1:
          int length = ((theClass[++currOffset] & 0xff) << 8) | (theClass[++currOffset] & 0xff);
          currOffset += length + 1;
          break;
        default:
          return;
      }
    }

    // add PUBLIC flag
    theClass[currOffset + 1] |= 1;
  }

}
