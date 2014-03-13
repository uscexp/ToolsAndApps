/* *****************************************************************
 * Project: common
 * File:    ExtHashMap.java
 * 
 * Creation:     19.05.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.app.splshell.util;


import java.util.HashMap;
import java.util.Map;

/**
 * Module:      ExtHashMap<br>
 *<p>
 * Description: A Map which handels also primitive types.<br>
 *              Attention! The get???Value() methods use a very tolerant casting mechanism.
 *</p><p>
 * Created:     19.05.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     19.05.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class ExtHashMap extends HashMap
{

  static final long serialVersionUID = -220096739970418394L;

  public ExtHashMap()
  {
    super();
  }

  public ExtHashMap( int initialCapacity, float loadFactor)
  {
    super( initialCapacity, loadFactor);
  }

  public ExtHashMap( int initialCapacity)
  {
    super( initialCapacity);
  }

  public ExtHashMap( Map m)
  {
    super( m);
  }

  public boolean containsValue( boolean value)
  {
    Primitive primitive = new Primitive( boolean.class, value);
    
    return super.containsValue( primitive);
  }

  public boolean containsValue( char value)
  {
    Primitive primitive = new Primitive( char.class, value);
    
    return super.containsValue( primitive);
  }

  public boolean containsValue( byte value)
  {
    Primitive primitive = new Primitive( byte.class, value);
    
    return super.containsValue( primitive);
  }

  public boolean containsValue( short value)
  {
    Primitive primitive = new Primitive( short.class, value);
    
    return super.containsValue( primitive);
  }

  public boolean containsValue( int value)
  {
    Primitive primitive = new Primitive( int.class, value);
    
    return super.containsValue( primitive);
  }

  public boolean containsValue( long value)
  {
    Primitive primitive = new Primitive( long.class, value);
    
    return super.containsValue( primitive);
  }

  public boolean containsValue( float value)
  {
    Primitive primitive = new Primitive( float.class, value);
    
    return super.containsValue( primitive);
  }

  public boolean containsValue( double value)
  {
    Primitive primitive = new Primitive( double.class, value);
    
    return super.containsValue( primitive);
  }

  public Object put( Object key, boolean value)
  {
    Primitive primitive = new Primitive( boolean.class, value);
    
    return super.put( key, primitive);
  }

  public Object put( Object key, char value)
  {
    Primitive primitive = new Primitive( char.class, value);
    
    return super.put( key, primitive);
  }

  public Object put( Object key, byte value)
  {
    Primitive primitive = new Primitive( byte.class, value);
    
    return super.put( key, primitive);
  }

  public Object put( Object key, short value)
  {
    Primitive primitive = new Primitive( short.class, value);
    
    return super.put( key, primitive);
  }

  public Object put( Object key, int value)
  {
    Primitive primitive = new Primitive( int.class, value);
    
    return super.put( key, primitive);
  }

  public Object put( Object key, long value)
  {
    Primitive primitive = new Primitive( long.class, value);
    
    return super.put( key, primitive);
  }

  public Object put( Object key, float value)
  {
    Primitive primitive = new Primitive( float.class, value);
    
    return super.put( key, primitive);
  }

  public Object put( Object key, double value)
  {
    Primitive primitive = new Primitive( double.class, value);
    
    return super.put( key, primitive);
  }

  public Object put( Object key, String value)
  {
    Primitive primitive = new Primitive( value);
    
    return super.put( key, primitive);
  }

  public Object putPrimitive( Class type, Object key, String value)
  {
    Primitive primitive = new Primitive( type, value);
    
    return super.put( key, primitive);
  }

  public Object putPrimitive( Object key, Object value)
  {
    Object object = null;
    if( Primitive.getClass( value.getClass()).isPrimitive()) {
      Primitive primitive = new Primitive( value);
      
      object = super.put( key, primitive);
    }
    else
      object = super.put( key, value);
      
    return object;
  }

  public boolean getBoolean( Object key)
  {
    Primitive primitive = (Primitive)super.get( key);
    return primitive.getBooleanValue();
  }

  public char getCharacter( Object key)
  {
    Primitive primitive = (Primitive)super.get( key);
    return primitive.getCharacterValue();
  }

  public byte getByte( Object key)
  {
    Primitive primitive = (Primitive)super.get( key);
    return primitive.getByteValue();
  }

  public short getShort( Object key)
  {
    Primitive primitive = (Primitive)super.get( key);
    return primitive.getShortValue();
  }

  public int getInteger( Object key)
  {
    Primitive primitive = (Primitive)super.get( key);
    return primitive.getIntegerValue();
  }

  public long getLong( Object key)
  {
    Primitive primitive = (Primitive)super.get( key);
    return primitive.getLongValue();
  }

  public float getFloat( Object key)
  {
    Primitive primitive = (Primitive)super.get( key);
    return primitive.getFloatValue();
  }

  public double getDouble( Object key)
  {
    Primitive primitive = (Primitive)super.get( key);
    return primitive.getDoubleValue();
  }

  public Object get( Object key)
  {
    Object object = super.get( key);
    if( object instanceof Primitive[])
      return Primitive.getValueArray( (Primitive[])object);
    else if( object instanceof Primitive)
      return ((Primitive)object).getValue();
    else
      return object;
  }

  public Object getAt( Object key, int pos)
  {
    Object object = super.get( key);
    if( !(object instanceof Primitive[]))
      return null;
    return ((Primitive[])object)[pos].getValue();
  }

  public Primitive getPrimitive( Object key)
  {
    return (Primitive)super.get( key);
  }

  public Primitive[] getPrimitives( Object key)
  {
    return (Primitive[])super.get( key);
  }
}
