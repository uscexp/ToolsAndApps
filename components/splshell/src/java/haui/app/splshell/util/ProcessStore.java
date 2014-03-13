/* *****************************************************************
 * Project: jjshell
 * File:    VariableStore.java
 * 
 * Creation:     23.06.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.app.splshell.util;

import haui.app.splshell.Node;
import haui.tool.shell.JShell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Module:      VariableStore<br>
 *<p>
 * Description: VariableStore<br>
 *</p><p>
 * Created:     23.06.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     23.06.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class ProcessStore
{

  private static HashMap instances = new HashMap();
  
  public static short OK_STATE = 0;
  public static short BREAK_STATE = 1;
  public static short CONT_STATE = 2;
  
  private short execState = OK_STATE;

  /** globale variables */
  private ExtHashMap global = new ExtHashMap();
  
  /**
   * store for method nodes
   */
  private HashMap methods = new HashMap();

  /** Stack for calculations. */
  private Stack stack = new Stack();
  
  /**
   * Block variable hierarchy:
   * stores variable HashMaps (ExtHashMap))
   */
  private ArrayList working = new ArrayList();
  
  /**
   * stores old block variable hierarchies for later retreval
   * e.g. returning from a method call<br>
   * stores ArrayLists of 'working' variable HashMaps
   */
  private ArrayList oldBlockHierarchy = new ArrayList();
  
  private JShell jShell = null;
  
  private String[] args;

  private ProcessStore()
  {
  }
  
//  private void init()
//  {
//    // add first (main) variable HashMap
//    working.add( new ExtHashMap());
//  }
  
  public static ProcessStore getInstance( Long id)
  {
    ProcessStore store = (ProcessStore)instances.get( id);
    if( store == null)
    {
      store = new ProcessStore();
//      store.init();
      instances.put( id, store);
    }
    
    return store;
  }
  
  public JShell getJShell()
  {
    return jShell;
  }

  public void setJShell( JShell shell)
  {
    jShell = shell;
  }

  public boolean checkPrecondition()
  {
    return execState == OK_STATE;
  }

  public String[] getArgs()
  {
    return args;
  }

  public void setArgs( String[] args)
  {
    this.args = args;
  }

  public short getExecState()
  {
    return execState;
  }

  public void setExecState( short execState)
  {
    this.execState = execState;
  }

  /**
   * get stack for this shell
   * 
   * @return Stack
   */
  public Stack getStack()
  {
    return stack;
  }

  /**
   * get a variable, first from the highest block hierarchy down to the
   * global variables
   * 
   * @param key name of the variable
   * @return value of the variable
   */
  public Object getVariable( Object key)
  {
    Object object = null;
    
    for( int i = working.size()-1; i >= 0; --i)
    {
      ExtHashMap map = (ExtHashMap)working.get( i);
      object = map.get( key);
      if( object != null)
        break;
    }
    if( object == null)
      object = global.get( key);
    
    return object;
  }
  
  /**
   * get a Primitive variable, first from the highest block hierarchy down to the
   * global variables
   * 
   * @param key name of the variable
   * @return value of the variable
   */
  public Primitive getPrimitiveVariable( Object key)
  {
    Primitive object = null;
    
    for( int i = working.size()-1; i >= 0; --i)
    {
      ExtHashMap map = (ExtHashMap)working.get( i);
      object = map.getPrimitive( key);
      if( object != null)
        break;
    }
    if( object == null)
      object = global.getPrimitive( key);
    
    return object;
  }
  
  /**
   * set an already defined variable, first from the highest block hierarchy
   * down to the global variables
   * 
   * @param key name of the variable
   * @param value value of the variable
   * @return true if successfully assignd to an existing variable else false
   */
  public boolean setVariable( Object key, Object value)
  {
    boolean success = false;
    Object object = null;
    
    for( int i = working.size()-1; i >= 0; --i)
    {
      ExtHashMap map = (ExtHashMap)working.get( i);
      object = map.get( key);
      if( object != null)
      {
        map.put( key, value);
        success = true;
        break;
      }
    }
    if( !success)
    {
      object = global.get( key);
      if( object != null)
      {
        global.put( key, value);
        success = true;
      }
    }
    return success;
  }
  
  /**
   * set a new variable, on the highest block hierarchy or global
   * if no hierarchy exists
   * 
   * @param key name of the variable
   * @param value value of the variable
   * @return true if at least one local block hierarchy exists else false
   */
  public boolean setNewVariable( Object key, Object value)
  {
    boolean success = false;
    
    success = setLocalVariable( key, value);
    
    if( !success) {
      setGlobalVariable( key, value);
      success = true;
    }
    
    return success;
  }

  /**
   * set a new local variable, on the highest block hierarchy
   * 
   * @param key name of the variable
   * @param value value of the variable
   * @return true if at least one local block hierarchy exists else false
   */
  public boolean setLocalVariable( Object key, Object value)
  {
    boolean success = false;
    
    if( working.size() > 0)
    {
      ExtHashMap map = (ExtHashMap)working.get( working.size()-1);
      map.put( key, value);
      success = true;
    }
    return success;
  }

  /**
   * set a new global variable, on the global hierarchy
   * 
   * @param key name of the variable
   * @param value value of the variable
   */
  public void setGlobalVariable( Object key, Object value)
  {
    global.put( key, value);
  }
  
  /**
   * create and add a new variable map for a new block hierarchy
   */
  public void createNewBlockVariableMap()
  {
    working.add( new ExtHashMap());
  }
  
  /**
   * remove the last block hierarchy variable map
   * 
   * @return true if the last local block hierarchy could be removed else false
   */
  public boolean removeLastBlockVariableMap()
  {
    boolean success = false;
    
    if( working.size() > 0)
    {
      if( working.remove( working.size()-1) != null)
        success = true;
    }
    return success;
  }
  
  /**
   * removes all block hierarchy variable maps
   */
  public void removeAllBlockVariableMaps()
  {
    working.clear();
  }
  
  /**
   * move current block hierarchy variable maps to the archive
   * for later retreval
   */
  public void moveWorkingMapToArchive()
  {
    oldBlockHierarchy.add( working);
    working = new ArrayList();
    createNewBlockVariableMap();
  }
  
  /**
   * restore the last block hierarchy variable map from
   * archive to working
   * 
   * @return true if restored successfully else false
   */
  public boolean restoreLastMapFromArchive()
  {
    boolean success = false;
    Object object = null;
    if( oldBlockHierarchy.size() > 0)
    {
      object = oldBlockHierarchy.remove( oldBlockHierarchy.size()-1);
      if( object != null)
      {
        working = (ArrayList)object;
        success = true;
      }
    }
    return success;
  }
  
  /**
   * removes all block hierarchy variable maps from
   * archive
   */
  public void removeAllMapsFromArchive()
  {
    oldBlockHierarchy.clear();
  }

  /**
   * add a method node hierarchy
   * 
   * @param name method id
   * @param method method node
   */
  public void addMethod( String name, Node method)
  {
    methods.put( name, method);
  }
  
  /**
   * get a method node with a method id
   * 
   * @param name method id
   * @return method node
   */
  public Node getMethod( String name)
  {
    return (Node)methods.get( name);
  }
  
  /**
   * remove a method node with a method id
   * 
   * @param name method id
   * @return removed method node
   */
  public Node removeMethod( String name)
  {
    return (Node)methods.remove( name);
  }
  
  /**
   * clear all method nodes
   */
  public void clearMethods()
  {
    methods.clear();
  }
}
