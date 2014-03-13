package haui.app.splshell.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Module:      PrimitiveTypeWrapper<br>
 *<p>
 * Description: wrapps primitive types in a Object<br>
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
public class Primitive extends Object
{
  private final static short BOOLEAN = 1;
  private final static short CHARACTER = 2;
  private final static short BYTE = 3;
  private final static short SHORT = 4;
  private final static short INTEGER = 5;
  private final static short LONG = 6;
  private final static short FLOAT = 7;
  private final static short DOUBLE = 8;
  private final static short STRING = 9;
  private final static short FILE_READER = 10;
  private final static short FILE_WRITER = 11;
  
  private Class primitiveType;
  private short typeId;
  private Object value;
  
  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code> with a default value
   * 
   * @param primitiveType class of the primitive type
   */
  public Primitive( Class primitiveType)
  {
    this.primitiveType = primitiveType;
    setTypeId();
    switch(getTypeId())
    {
      case BOOLEAN:
        this.value = new Boolean( false);
        break;
        
      case CHARACTER:
        this.value = new Character( ' ');
        break;
         
      case BYTE:
        this.value = new Byte( (byte)0);
        break;
         
      case SHORT:
        this.value = new Short( (short)0);
        break;
         
      case INTEGER:
        this.value = new Integer( (int)0);
        break;
         
      case LONG:
        this.value = new Long( (long)0);
        break;
         
      case FLOAT:
        this.value = new Float( (float)0.0);
        break;
         
      case DOUBLE:
        this.value = new Double( (double)0.0);
        break;
         
      case STRING:
        this.value = new String( "");
        break;
        
      case FILE_READER:
        this.value = null;
        break;
        
      case FILE_WRITER:
        this.value = null;
        break;
    }
  }

  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param primitiveType class of the primitive type
   * @param value primitive type value
   */
  public Primitive( Class primitiveType, boolean value)
  {
    this.primitiveType = primitiveType;
    this.value = new Boolean( value);
    setTypeId();
  }

  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param primitiveType class of the primitive type
   * @param value primitive type value
   */
  public Primitive( Class primitiveType, char value)
  {
    this.primitiveType = primitiveType;
    this.value = new Character( value);
    setTypeId();
  }

  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param primitiveType class of the primitive type
   * @param value primitive type value
   */
  public Primitive( Class primitiveType, byte value)
  {
    this.primitiveType = primitiveType;
    this.value = new Byte( value);
    setTypeId();
  }

  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param primitiveType class of the primitive type
   * @param value primitive type value
   */
  public Primitive( Class primitiveType, short value)
  {
    this.primitiveType = primitiveType;
    this.value = new Short( value);
    setTypeId();
  }

  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param primitiveType class of the primitive type
   * @param value primitive type value
   */
  public Primitive( Class primitiveType, int value)
  {
    this.primitiveType = primitiveType;
    this.value = new Integer( value);
    setTypeId();
  }

  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param primitiveType class of the primitive type
   * @param value primitive type value
   */
  public Primitive( Class primitiveType, long value)
  {
    this.primitiveType = primitiveType;
    this.value = new Long( value);
    setTypeId();
  }

  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param primitiveType class of the primitive type
   * @param value primitive type value
   */
  public Primitive( Class primitiveType, float value)
  {
    this.primitiveType = primitiveType;
    this.value = new Float( value);
    setTypeId();
  }

  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param primitiveType class of the primitive type
   * @param value primitive type value
   */
  public Primitive( Class primitiveType, double value)
  {
    this.primitiveType = primitiveType;
    this.value = new Double( value);
    setTypeId();
  }
  
  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param type class of the primitive type or wrapper class of primitive (Boolean,...)
   * @param value primitive type value
   */
  public Primitive( Class type, String value)
  {
    if( type.isPrimitive())
      this.primitiveType = type;
    else
      this.primitiveType = getClass( type);
    setTypeId();
    setValue( value);
  }
  
  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param type class of the primitive type or wrapper class of primitive (Boolean,...)
   * @param value primitive type value
   */
  public Primitive( Class type, BufferedReader value)
  {
    if( type.isPrimitive())
      this.primitiveType = type;
    else
      this.primitiveType = getClass( type);
    setTypeId();
    setValue( value);
  }
  
  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param type class of the primitive type or wrapper class of primitive (Boolean,...)
   * @param value primitive type value
   */
  public Primitive( Class type, BufferedWriter value)
  {
    if( type.isPrimitive())
      this.primitiveType = type;
    else
      this.primitiveType = getClass( type);
    setTypeId();
    setValue( value);
  }
  
  /**
   * Cronstructs <code>PrimitiveTypeWrapper</code>
   * 
   * @param type class of the primitive type or wrapper class of primitive (Boolean,...)
   * @param value primitive type value
   */
  public Primitive( Object value)
  {
    this.primitiveType = getClass( value.getClass());
    setTypeId();
    this.value = value;
  }
  
  private void setTypeId()
  {
    if( getPrimitiveType().equals( boolean.class))
      typeId = BOOLEAN;
    else if( getPrimitiveType().equals( char.class))
      typeId = CHARACTER;
    else if( getPrimitiveType().equals( byte.class))
      typeId = BYTE;
    else if( getPrimitiveType().equals( short.class))
      typeId = SHORT;
    else if( getPrimitiveType().equals( int.class))
      typeId = INTEGER;
    else if( getPrimitiveType().equals( long.class))
      typeId = LONG;
    else if( getPrimitiveType().equals( float.class))
      typeId = FLOAT;
    else if( getPrimitiveType().equals( double.class))
      typeId = DOUBLE;
    else if( getPrimitiveType().equals( String.class))
      typeId = STRING;
    else if( getPrimitiveType().equals( BufferedReader.class))
      typeId = FILE_READER;
    else if( getPrimitiveType().equals( BufferedWriter.class))
      typeId = FILE_WRITER;
  }

  public short getTypeId()
  {
    return typeId;
  }

  public Class getPrimitiveType()
  {
    return primitiveType;
  }

  public Object getValue()
  {
    return value;
  }
  
  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public boolean getBooleanValue()
  {
    boolean primValue = false;
    switch( typeId)
    {
      case BOOLEAN:
        primValue = ((Boolean)getValue()).booleanValue();
        break;

      case CHARACTER:
        primValue = ((Character)getValue()).charValue() != 0;
        break;

      case BYTE:
        primValue = ((Byte)getValue()).shortValue() != 0;
        break;

      case SHORT:
        primValue = ((Short)getValue()).shortValue() != 0;
        break;

      case INTEGER:
        primValue = ((Integer)getValue()).intValue() != 0;
        break;

      case LONG:
        primValue = ((Long)getValue()).longValue() != 0;
        break;

      case FLOAT:
        primValue = ((Float)getValue()).floatValue() != 0.0;
        break;

      case DOUBLE:
        primValue = ((Double)getValue()).doubleValue() != 0.0;
        break;

      case STRING:
        primValue = (new Boolean( ((String)getValue()))).booleanValue();
        break;

      default:
        primValue = false;
        break;
    }
    return primValue;
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public char getCharacterValue()
  {
    char primValue = ' ';
    switch( typeId)
    {
      case CHARACTER:
        primValue = ((Character)getValue()).charValue();
        break;

      case BYTE:
        primValue = (char)((Byte)getValue()).shortValue();
        break;

      case SHORT:
        primValue = (char)((Short)getValue()).shortValue();
        break;

      case INTEGER:
        primValue = (char)((Integer)getValue()).intValue();
        break;

      case LONG:
        primValue = (char)((Long)getValue()).longValue();
        break;

      case STRING:
        primValue = ((String)getValue()).charAt(0);
        break;

      default:
        primValue = ' ';
        break;
    }
    return primValue;
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public byte getByteValue()
  {
    byte primValue = 0;
    switch( typeId)
    {
      case BOOLEAN:
        primValue = ((Boolean)getValue()).booleanValue() ? (byte)1 : (byte)0;
        break;

      case CHARACTER:
        primValue = ((Character)getValue()).toString().getBytes()[0];
        break;

      case BYTE:
        primValue = ((Byte)getValue()).byteValue();
        break;

      case SHORT:
        primValue = ((Short)getValue()).byteValue();
        break;

      case INTEGER:
        primValue = ((Integer)getValue()).byteValue();
        break;

      case LONG:
        primValue = ((Long)getValue()).byteValue();
        break;

      case FLOAT:
        primValue = ((Float)getValue()).byteValue();
        break;

      case DOUBLE:
        primValue = ((Double)getValue()).byteValue();
        break;

      case STRING:
        primValue = (new Byte( ((String)getValue()))).byteValue();
        break;

      default:
        primValue = 0;
        break;
    }
    return primValue;
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public short getShortValue()
  {
    short primValue = 0;
    switch( typeId)
    {
      case BOOLEAN:
        primValue = ((Boolean)getValue()).booleanValue() ? (short)1 : (short)0;
        break;

      case CHARACTER:
        primValue = (short)((Character)getValue()).charValue();
        break;

      case BYTE:
        primValue = ((Byte)getValue()).shortValue();
        break;

      case SHORT:
        primValue = ((Short)getValue()).shortValue();
        break;

      case INTEGER:
        primValue = ((Integer)getValue()).shortValue();
        break;

      case LONG:
        primValue = ((Long)getValue()).shortValue();
        break;

      case FLOAT:
        primValue = ((Float)getValue()).shortValue();
        break;

      case DOUBLE:
        primValue = ((Double)getValue()).shortValue();
        break;

      case STRING:
        primValue = (new Short( ((String)getValue()))).shortValue();
        break;

      default:
        primValue = 0;
        break;
    }
    return primValue;
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public int getIntegerValue()
  {
    int primValue = 0;
    switch( typeId)
    {
      case BOOLEAN:
        primValue = ((Boolean)getValue()).booleanValue() ? 1 : 0;
        break;

      case CHARACTER:
        primValue = (int)((Character)getValue()).charValue();
        break;

      case BYTE:
        primValue = ((Byte)getValue()).intValue();
        break;

      case SHORT:
        primValue = ((Short)getValue()).intValue();
        break;

      case INTEGER:
        primValue = ((Integer)getValue()).intValue();
        break;

      case LONG:
        primValue = ((Long)getValue()).intValue();
        break;

      case FLOAT:
        primValue = ((Float)getValue()).intValue();
        break;

      case DOUBLE:
        primValue = ((Double)getValue()).intValue();
        break;

      case STRING:
        primValue = (new Integer( ((String)getValue()))).intValue();
        break;

      default:
        primValue = 0;
        break;
    }
    return primValue;
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public long getLongValue()
  {
    long primValue = 0;
    switch( typeId)
    {
      case BOOLEAN:
        primValue = ((Boolean)getValue()).booleanValue() ? 1 : 0;
        break;

      case CHARACTER:
        primValue = (long)((Character)getValue()).charValue();
        break;

      case BYTE:
        primValue = ((Byte)getValue()).longValue();
        break;

      case SHORT:
        primValue = ((Short)getValue()).longValue();
        break;

      case INTEGER:
        primValue = ((Integer)getValue()).longValue();
        break;

      case LONG:
        primValue = ((Long)getValue()).longValue();
        break;

      case FLOAT:
        primValue = ((Float)getValue()).longValue();
        break;

      case DOUBLE:
        primValue = ((Double)getValue()).longValue();
        break;

      case STRING:
        primValue = (new Long( ((String)getValue()))).longValue();
        break;

      default:
        primValue = 0;
        break;
    }
    return primValue;
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public float getFloatValue()
  {
    float primValue = (float)0.0;
    switch( typeId)
    {
      case BOOLEAN:
        primValue = ((Boolean)getValue()).booleanValue() ? (float)1.0 : (float)0.0;
        break;

      case CHARACTER:
        primValue = (float)((Character)getValue()).charValue();
        break;

      case BYTE:
        primValue = ((Byte)getValue()).floatValue();
        break;

      case SHORT:
        primValue = ((Short)getValue()).floatValue();
        break;

      case INTEGER:
        primValue = ((Integer)getValue()).floatValue();
        break;

      case LONG:
        primValue = ((Long)getValue()).floatValue();
        break;

      case FLOAT:
        primValue = ((Float)getValue()).floatValue();
        break;

      case DOUBLE:
        primValue = ((Double)getValue()).floatValue();
        break;

      case STRING:
        primValue = (new Float( ((String)getValue()))).floatValue();
        break;

      default:
        primValue = (float)0.0;
        break;
    }
    return primValue;
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public double getDoubleValue()
  {
    double primValue = 0.0;
    switch( typeId)
    {
      case BOOLEAN:
        primValue = ((Boolean)getValue()).booleanValue() ? 1 : 0;
        break;

      case CHARACTER:
        primValue = (double)((Character)getValue()).charValue();
        break;

      case BYTE:
        primValue = ((Byte)getValue()).doubleValue();
        break;

      case SHORT:
        primValue = ((Short)getValue()).doubleValue();
        break;

      case INTEGER:
        primValue = ((Integer)getValue()).doubleValue();
        break;

      case LONG:
        primValue = ((Long)getValue()).doubleValue();
        break;

      case FLOAT:
        primValue = ((Float)getValue()).doubleValue();
        break;

      case DOUBLE:
        primValue = ((Double)getValue()).doubleValue();
        break;

      case STRING:
        primValue = (new Double( ((String)getValue()))).doubleValue();
        break;

      default:
        primValue = 0.0;
        break;
    }
    return primValue;
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( boolean value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = Boolean.valueOf( value);
        break;

      case CHARACTER:
        this.value = new Character( ' ');
        break;

      case BYTE:
        this.value = new Byte( (value ? (byte)1 : (byte)0));
        break;

      case SHORT:
        this.value = new Short( (value ? (short)1 : (short)0));
        break;

      case INTEGER:
        this.value = new Integer( (value ? 1 : 0));
        break;

      case LONG:
        this.value = new Long( (value ? 1 : 0));
        break;

      case FLOAT:
        this.value = new Float( (value ? (float)1.0 : (float)0.0));
        break;

      case DOUBLE:
        this.value = new Double( (value ? 1.0 : 0.0));
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      default:
        this.value = Boolean.valueOf( false);
        break;
    }
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( char value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = (value == 0 ? Boolean.valueOf( false) : Boolean.valueOf( true));
        break;

      case CHARACTER:
        this.value = new Character( value);
        break;

      case BYTE:
        this.value = new Byte( String.valueOf( value).getBytes()[0]);
        break;

      case SHORT:
        this.value = new Short( (short)value);
        break;

      case INTEGER:
        this.value = new Integer( (int)value);
        break;

      case LONG:
        this.value = new Long( (long)value);
        break;

      case FLOAT:
        this.value = new Float( (float)value);
        break;

      case DOUBLE:
        this.value = new Double( (double)value);
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      default:
        this.value = new Character( ' ');
        break;
    }
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( byte value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = (value == 0 ? Boolean.valueOf( false) : Boolean.valueOf( true));
        break;

      case CHARACTER:
        this.value = new Character( (char)value);
        break;

      case BYTE:
        this.value = new Byte( value);
        break;

      case SHORT:
        this.value = new Short( (short)value);
        break;

      case INTEGER:
        this.value = new Integer( (int)value);
        break;

      case LONG:
        this.value = new Long( (long)value);
        break;

      case FLOAT:
        this.value = new Float( (float)value);
        break;

      case DOUBLE:
        this.value = new Double( (double)value);
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      default:
        this.value = new Byte( (byte)0);
        break;
    }
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( short value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = (value == 0 ? Boolean.valueOf( false) : Boolean.valueOf( true));
        break;

      case CHARACTER:
        this.value = new Character( (char)value);
        break;

      case BYTE:
        this.value = new Byte( (byte)value);
        break;

      case SHORT:
        this.value = new Short( (short)value);
        break;

      case INTEGER:
        this.value = new Integer( (int)value);
        break;

      case LONG:
        this.value = new Long( (long)value);
        break;

      case FLOAT:
        this.value = new Float( (float)value);
        break;

      case DOUBLE:
        this.value = new Double( (double)value);
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      default:
        this.value = new Short( (short)0);
        break;
    }
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( int value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = (value == 0 ? Boolean.valueOf( false) : Boolean.valueOf( true));
        break;

      case CHARACTER:
        this.value = new Character( (char)value);
        break;

      case BYTE:
        this.value = new Byte( (byte)value);
        break;

      case SHORT:
        this.value = new Short( (short)value);
        break;

      case INTEGER:
        this.value = new Integer( (int)value);
        break;

      case LONG:
        this.value = new Long( (long)value);
        break;

      case FLOAT:
        this.value = new Float( (float)value);
        break;

      case DOUBLE:
        this.value = new Double( (double)value);
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      default:
        this.value = new Integer( (int)0);
        break;
    }
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( long value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = (value == 0 ? Boolean.valueOf( false) : Boolean.valueOf( true));
        break;

      case CHARACTER:
        this.value = new Character( (char)value);
        break;

      case BYTE:
        this.value = new Byte( (byte)value);
        break;

      case SHORT:
        this.value = new Short( (short)value);
        break;

      case INTEGER:
        this.value = new Integer( (int)value);
        break;

      case LONG:
        this.value = new Long( (long)value);
        break;

      case FLOAT:
        this.value = new Float( (float)value);
        break;

      case DOUBLE:
        this.value = new Double( (double)value);
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      default:
        this.value = new Long( (long)0);
        break;
    }
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( float value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = (value == 0.0 ? Boolean.valueOf( false) : Boolean.valueOf( true));
        break;

      case CHARACTER:
        this.value = new Character( (char)value);
        break;

      case BYTE:
        this.value = new Byte( (byte)value);
        break;

      case SHORT:
        this.value = new Short( (short)value);
        break;

      case INTEGER:
        this.value = new Integer( (int)value);
        break;

      case LONG:
        this.value = new Long( (long)value);
        break;

      case FLOAT:
        this.value = new Float( (float)value);
        break;

      case DOUBLE:
        this.value = new Double( (double)value);
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      default:
        this.value = new Float( (float)0.0);
        break;
    }
  }

  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( double value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = (value == 0.0 ? Boolean.valueOf( false) : Boolean.valueOf( true));
        break;

      case CHARACTER:
        this.value = new Character( (char)value);
        break;

      case BYTE:
        this.value = new Byte( (byte)value);
        break;

      case SHORT:
        this.value = new Short( (short)value);
        break;

      case INTEGER:
        this.value = new Integer( (int)value);
        break;

      case LONG:
        this.value = new Long( (long)value);
        break;

      case FLOAT:
        this.value = new Float( (float)value);
        break;

      case DOUBLE:
        this.value = new Double( (double)value);
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      default:
        this.value = new Double( (double)0.0);
        break;
    }
  }
  
  /**
   * Attention! This method use a very tolerant casting mechanism.
   */
  public void setValue( String value)
  {
    switch( typeId)
    {
      case BOOLEAN:
        this.value = new Boolean( value);
        break;

      case CHARACTER:
        this.value = new Character( value.charAt(0));
        break;

      case BYTE:
        this.value = new Byte( value);
        break;

      case SHORT:
        this.value = new Short( value);
        break;

      case INTEGER:
        this.value = new Integer( value);
        break;

      case LONG:
        this.value = new Long( value);
        break;

      case FLOAT:
        this.value = new Float( value);
        break;

      case DOUBLE:
        this.value = new Double( value);
        break;

      case STRING:
        this.value = String.valueOf( value);
        break;

      case FILE_READER:
        try
        {
          this.value = new BufferedReader( new FileReader( value));
        }
        catch( FileNotFoundException ex)
        {
          throw new RuntimeException( ex);
        }
        break;

      case FILE_WRITER:
        try
        {
          this.value = new BufferedWriter( new FileWriter( value));
        }
        catch( IOException ex)
        {
          throw new RuntimeException( ex);
        }
        break;

      default:
        this.value = value;
        break;
    }
  }

  /**
   * set BufferedReader
   */
  public void setValue( BufferedReader value)
  {
    if( typeId == FILE_READER)
      this.value = value;
  }

  /**
   * set BufferedWriter
   */
  public void setValue( BufferedWriter value)
  {
    if( typeId == FILE_WRITER)
      this.value = value;
  }

  public void setValue( Object value)
  {
    Primitive primitive = new Primitive( value);
    switch( primitive.getTypeId())
    {
      case CHARACTER:
        switch( getTypeId())
        {
          case BOOLEAN:
            value = new Boolean( primitive.getBooleanValue());
            break;
    
          case CHARACTER:
            value = new Character( primitive.getCharacterValue());
            break;
    
          case BYTE:
            value = new Byte( primitive.getByteValue());
            break;
    
          case SHORT:
            value = new Short( primitive.getShortValue());
            break;
    
          case INTEGER:
            value = new Integer( primitive.getIntegerValue());
            break;
    
          case LONG:
            value = new Long( primitive.getLongValue());
            break;
    
          case FLOAT:
            value = new Float( primitive.getFloatValue());
            break;
    
          case DOUBLE:
            value = new Double( primitive.getDoubleValue());
            break;
            
          case STRING:
            this.value = String.valueOf( value);
            break;

          case FILE_READER:
            this.value = (BufferedReader)value;
            break;

          case FILE_WRITER:
            this.value = (BufferedWriter)value;
            break;
        }
        break;

      case BYTE:
        switch( getTypeId())
        {
          case BOOLEAN:
            value = new Boolean( primitive.getBooleanValue());
            break;
    
          case CHARACTER:
            value = new Character( primitive.getCharacterValue());
            break;
    
          case BYTE:
            value = new Byte( primitive.getByteValue());
            break;
    
          case SHORT:
            value = new Short( primitive.getShortValue());
            break;
    
          case INTEGER:
            value = new Integer( primitive.getIntegerValue());
            break;
    
          case LONG:
            value = new Long( primitive.getLongValue());
            break;
    
          case FLOAT:
            value = new Float( primitive.getFloatValue());
            break;
    
          case DOUBLE:
            value = new Double( primitive.getDoubleValue());
            break;
            
          case STRING:
            this.value = String.valueOf( value);
            break;

          case FILE_READER:
            this.value = (BufferedReader)value;
            break;

          case FILE_WRITER:
            this.value = (BufferedWriter)value;
            break;
        }
        break;

      case SHORT:
        switch( getTypeId())
        {
          case BOOLEAN:
            value = new Boolean( primitive.getBooleanValue());
            break;
    
          case CHARACTER:
            value = new Character( primitive.getCharacterValue());
            break;
    
          case BYTE:
            value = new Byte( primitive.getByteValue());
            break;
    
          case SHORT:
            value = primitive.getValue();
            break;
    
          case INTEGER:
            value = new Integer( primitive.getIntegerValue());
            break;
    
          case LONG:
            value = new Long( primitive.getLongValue());
            break;
    
          case FLOAT:
            value = new Float( primitive.getFloatValue());
            break;
    
          case DOUBLE:
            value = new Double( primitive.getDoubleValue());
            break;
            
          case STRING:
            this.value = String.valueOf( value);
            break;

          case FILE_READER:
            this.value = (BufferedReader)value;
            break;

          case FILE_WRITER:
            this.value = (BufferedWriter)value;
            break;
        }
        break;

      case INTEGER:
        switch( getTypeId())
        {
          case BOOLEAN:
            value = new Boolean( primitive.getBooleanValue());
            break;
    
          case CHARACTER:
            value = new Character( primitive.getCharacterValue());
            break;
    
          case BYTE:
            value = new Byte( primitive.getByteValue());
            break;
    
          case SHORT:
            value = new Short( primitive.getShortValue());
            break;
    
          case INTEGER:
            value = primitive.getValue();
            break;
    
          case LONG:
            value = new Long( primitive.getLongValue());
            break;
    
          case FLOAT:
            value = new Float( primitive.getFloatValue());
            break;
    
          case DOUBLE:
            value = new Double( primitive.getDoubleValue());
            break;
            
          case STRING:
            this.value = String.valueOf( value);
            break;

          case FILE_READER:
            this.value = (BufferedReader)value;
            break;

          case FILE_WRITER:
            this.value = (BufferedWriter)value;
            break;
        }
        break;

      case LONG:
        switch( getTypeId())
        {
          case BOOLEAN:
            value = new Boolean( primitive.getBooleanValue());
            break;
    
          case CHARACTER:
            value = new Character( primitive.getCharacterValue());
            break;
    
          case BYTE:
            value = new Byte( primitive.getByteValue());
            break;
    
          case SHORT:
            value = new Short( primitive.getShortValue());
            break;
    
          case INTEGER:
            value = new Integer( primitive.getIntegerValue());
            break;
    
          case LONG:
            value = primitive.getValue();
            break;
    
          case FLOAT:
            value = new Float( primitive.getFloatValue());
            break;
    
          case DOUBLE:
            value = new Double( primitive.getDoubleValue());
            break;
            
          case STRING:
            this.value = String.valueOf( value);
            break;

          case FILE_READER:
            this.value = (BufferedReader)value;
            break;

          case FILE_WRITER:
            this.value = (BufferedWriter)value;
            break;
        }
        break;

      case FLOAT:
        switch( getTypeId())
        {
          case BOOLEAN:
            value = new Boolean( primitive.getBooleanValue());
            break;
    
          case CHARACTER:
            value = new Character( primitive.getCharacterValue());
            break;
    
          case BYTE:
            value = new Byte( primitive.getByteValue());
            break;
    
          case SHORT:
            value = new Short( primitive.getShortValue());
            break;
    
          case INTEGER:
            value = new Integer( primitive.getIntegerValue());
            break;
    
          case LONG:
            value = new Long( primitive.getLongValue());
            break;
    
          case FLOAT:
            value = new Float( primitive.getFloatValue());
            break;
    
          case DOUBLE:
            value = new Double( primitive.getDoubleValue());
            break;
            
          case STRING:
            this.value = String.valueOf( value);
            break;

          case FILE_READER:
            this.value = (BufferedReader)value;
            break;

          case FILE_WRITER:
            this.value = (BufferedWriter)value;
            break;
        }
        break;

      case DOUBLE:
        switch( getTypeId())
        {
          case BOOLEAN:
            value = new Boolean( primitive.getBooleanValue());
            break;
    
          case CHARACTER:
            value = new Character( primitive.getCharacterValue());
            break;
    
          case BYTE:
            value = new Byte( primitive.getByteValue());
            break;
    
          case SHORT:
            value = new Short( primitive.getShortValue());
            break;
    
          case INTEGER:
            value = new Integer( primitive.getIntegerValue());
            break;
    
          case LONG:
            value = new Long( primitive.getLongValue());
            break;
    
          case FLOAT:
            value = new Float( primitive.getFloatValue());
            break;
    
          case DOUBLE:
            value = new Double( primitive.getDoubleValue());
            break;
            
          case STRING:
            this.value = String.valueOf( value);
            break;

          case FILE_READER:
            this.value = (BufferedReader)value;
            break;

          case FILE_WRITER:
            this.value = (BufferedWriter)value;
            break;
        }
        break;
    }
    this.value = value;
  }

  public String resolveGetMethodName()
  {
    String method = null;
    switch( typeId)
    {
      case BOOLEAN:
        method = "getBooleanValue";
        break;

      case CHARACTER:
        method = "getCharacterValue";
        break;

      case BYTE:
        method = "getByteValue";
        break;

      case SHORT:
        method = "getShortValue";
        break;

      case INTEGER:
        method = "getIntegerValue";
        break;

      case LONG:
        method = "getLongValue";
        break;

      case FLOAT:
        method = "getFloatValue";
        break;

      case DOUBLE:
        method = "getDoubleValue";
        break;

      case STRING:
        method = "getValue";
        break;

      case FILE_READER:
        method = "getValue";
        break;

      case FILE_WRITER:
        method = "getValue";
        break;

      default:
        method = null;
        break;
    }
    return method;
  }

  public static Class getClass( Class type)
  {
    Class clazz = null;
    if( type != null)
    {
      if( type.equals( Boolean.class))
        clazz = boolean.class;
      else if( type.equals( Character.class))
        clazz = char.class;
      else if( type.equals( Byte.class))
        clazz = byte.class;
      else if( type.equals( Short.class))
        clazz = short.class;
      else if( type.equals( Integer.class))
        clazz = int.class;
      else if( type.equals( Long.class))
        clazz = long.class;
      else if( type.equals( Float.class))
        clazz = float.class;
      else if( type.equals( Double.class))
        clazz = double.class;
      else if( type.equals( String.class))
        clazz = String.class;
      else if( type.equals( BufferedReader.class))
        clazz = BufferedReader.class;
      else if( type.equals( BufferedWriter.class))
        clazz = BufferedWriter.class;
    }
    
    return clazz;
  }
  
  public static Primitive[] createValue( Object[] objects)
  {
    if( objects == null)
      return null;
    
    Primitive[] primitives = new Primitive[objects.length];
    
    for( int i = 0; i < primitives.length; ++i) {
      primitives[i] = new Primitive( objects[i]);
    }
    return primitives;
  }
  
  public static Primitive createValue( Object object)
  {
    if( object == null)
      return null;
    
    Primitive primitive = new Primitive( object);
    
    return primitive;
  }
  
  public static Object[] getValueArray( Primitive[] primitives)
  {
    int count = primitives.length;
    if( primitives == null || count == 0)
      return null;
    
    Class clazz = primitives[0].getPrimitiveType();
    Object[] objects = null;
    if( clazz != null)
    {
      if( clazz.equals( boolean.class))
        objects = new Boolean[count];
      else if( clazz.equals( char.class))
        objects = new Character[count];
      else if( clazz.equals( byte.class))
        objects = new Byte[count];
      else if( clazz.equals( short.class))
        objects = new Short[count];
      else if( clazz.equals( int.class))
        objects = new Integer[count];
      else if( clazz.equals( long.class))
        objects = new Long[count];
      else if( clazz.equals( float.class))
        objects = new Float[count];
      else if( clazz.equals( double.class))
        objects = new Double[count];
      else if( clazz.equals( String.class))
        objects = new String[count];
      else if( clazz.equals( BufferedReader.class))
        objects = new BufferedReader[count];
      else if( clazz.equals( BufferedWriter.class))
        objects = new BufferedWriter[count];
    }
    
    for( int i = 0; i < count; ++i) {
      objects[i] = primitives[i].getValue();
    }
    return objects;
  }
  
  public String toString()
  {
    return value.toString();
  }

  public int hashCode()
  {
    return value.hashCode();
  }

  public boolean equals( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    boolean result = false;
    switch( typeId)
    {
      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Character)value).charValue() > 0 ? true : false)
                == ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Character)value).charValue()
                == ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, (byte)((Character)value).charValue()
                == ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, (short)((Character)value).charValue()
                == ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, (int)((Character)value).charValue()
                == ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, (long)((Character)value).charValue()
                == ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, (float)((Character)value).charValue()
                == ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, (double)((Character)value).charValue()
                == ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Character)value).toString().equals( objValue));
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Byte)value).byteValue() > 0 ? true : false)
                == ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, (char)((Byte)value).byteValue()
                == ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                == ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Byte)value).shortValue()
                == ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Byte)value).intValue()
                == ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Byte)value).longValue()
                == ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Byte)value).floatValue()
                == ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Byte)value).doubleValue()
                == ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Byte)value).toString().equals( objValue));
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Short)value).shortValue() > 0 ? true : false)
                == ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                == ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                == ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                == ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Short)value).intValue()
                == ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Short)value).longValue()
                == ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Short)value).floatValue()
                == ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Short)value).doubleValue()
                == ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Short)value).toString().equals( objValue));
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Integer)value).intValue() > 0 ? true : false)
                == ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                == ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                == ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                == ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                == ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Integer)value).longValue()
                == ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Integer)value).floatValue()
                == ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Integer)value).doubleValue()
                == ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Integer)value).toString().equals( objValue));
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Long)value).longValue() > 0 ? true : false)
                == ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                == ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                == ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                == ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                == ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                == ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Long)value).floatValue()
                == ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Long)value).doubleValue()
                == ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Long)value).toString().equals( objValue));
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Float)value).floatValue() > 0 ? true : false)
                == ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                == ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                == ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                == ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                == ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                == ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                == ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Float)value).doubleValue()
                == ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Float)value).toString().equals( objValue));
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Double)value).doubleValue() > 0 ? true : false)
                == ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                == ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                == ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                == ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                == ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                == ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                == ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                == ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Double)value).toString().equals( objValue));
            break;
        }
        break;
        
        default:
          result = value.equals( objValue);
          primitive = new Primitive( boolean.class, result);
          break;
    }
    result = primitive.getBooleanValue();
    return result;
  }

  public boolean notEquals( Object obj)
  {
    return !equals( obj);
  }

  public Primitive bitwiseAnd( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, ((Boolean)value).booleanValue() & ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (((Boolean)value).booleanValue() ? 'y' : 'n')
                & ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)(((Boolean)value).booleanValue() ? 1 : 0)
                & ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)(((Boolean)value).booleanValue() ? 1 : 0)
                & ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)(((Boolean)value).booleanValue() ? 1 : 0)
                & ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                & ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                & ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                & ((Double)objValue).longValue());
            break;
        }
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Character)value).charValue() > 0 ? true : false)
                & ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, ((Character)value).charValue()
                & ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)((Character)value).charValue()
                & ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)((Character)value).charValue()
                & ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)((Character)value).charValue()
                & ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                & ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                & ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                & ((Double)objValue).longValue());
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Byte)value).byteValue() > 0 ? true : false)
                & ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Byte)value).byteValue()
                & ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Byte)value).byteValue()
                & ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Byte)value).shortValue()
                & ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Byte)value).intValue()
                & ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Byte)value).longValue()
                & ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Byte)value).longValue()
                & ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Byte)value).longValue()
                & ((Double)objValue).longValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Short)value).shortValue() > 0 ? true : false)
                & ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                & ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                & ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                & ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Short)value).intValue()
                & ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Short)value).longValue()
                & ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Short)value).longValue()
                & ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Short)value).longValue()
                & ((Double)objValue).longValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Integer)value).shortValue() > 0 ? true : false)
                & ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                & ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                & ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                & ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                & ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Integer)value).longValue()
                & ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Integer)value).longValue()
                & ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Integer)value).longValue()
                & ((Double)objValue).longValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Long)value).shortValue() > 0 ? true : false)
                & ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                & ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                & ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                & ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                & ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                & ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Long)value).longValue()
                & ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Long)value).longValue()
                & ((Double)objValue).longValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Float)value).shortValue() > 0 ? true : false)
                & ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Float)value).byteValue()
                & ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Float)value).byteValue()
                & ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Float)value).shortValue()
                & ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Float)value).intValue()
                & ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                & ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                & ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                & ((Double)objValue).longValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Double)value).shortValue() > 0 ? true : false)
                & ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Double)value).byteValue()
                & ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Double)value).byteValue()
                & ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Double)value).shortValue()
                & ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Double)value).intValue()
                & ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                & ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                & ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                & ((Double)objValue).longValue());
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive bitwiseCompl()
  {
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = new Primitive( int.class, ~(((Boolean)value).booleanValue() ? 1 : 2));
        break;

      case CHARACTER:
        primitive = new Primitive( char.class, ~((Character)value).charValue());
        break;

      case BYTE:
        primitive = new Primitive( byte.class, ~((Byte)value).byteValue());
        break;

      case SHORT:
        primitive = new Primitive( short.class, ~((Short)value).shortValue());
        break;

      case INTEGER:
        primitive = new Primitive( int.class, ~((Integer)value).intValue());
        break;

      case LONG:
        primitive = new Primitive( long.class, ~((Long)value).longValue());
        break;

      case FLOAT:
        primitive = new Primitive( long.class, ~((Float)value).longValue());
        break;

      case DOUBLE:
        primitive = new Primitive( long.class, ~((Double)value).longValue());
        break;
    }
    return primitive;
  }

  public Primitive bitwiseOr( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, ((Boolean)value).booleanValue() | ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (((Boolean)value).booleanValue() ? 'y' : 'n')
                | ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)(((Boolean)value).booleanValue() ? 1 : 0)
                | ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)(((Boolean)value).booleanValue() ? 1 : 0)
                | ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)(((Boolean)value).booleanValue() ? 1 : 0)
                | ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                | ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                | ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                | ((Double)objValue).longValue());
            break;
        }
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Character)value).charValue() > 0 ? true : false)
                | ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, ((Character)value).charValue()
                | ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)((Character)value).charValue()
                | ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)((Character)value).charValue()
                | ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)((Character)value).charValue()
                | ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                | ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                | ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                | ((Double)objValue).longValue());
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Byte)value).byteValue() > 0 ? true : false)
                | ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Byte)value).byteValue()
                | ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Byte)value).byteValue()
                | ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Byte)value).shortValue()
                | ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Byte)value).intValue()
                | ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Byte)value).longValue()
                | ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Byte)value).longValue()
                | ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Byte)value).longValue()
                | ((Double)objValue).longValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Short)value).shortValue() > 0 ? true : false)
                | ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                | ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                | ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                | ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Short)value).intValue()
                | ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Short)value).longValue()
                | ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Short)value).longValue()
                | ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Short)value).longValue()
                | ((Double)objValue).longValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Integer)value).shortValue() > 0 ? true : false)
                | ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                | ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                | ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                | ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                | ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Integer)value).longValue()
                | ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Integer)value).longValue()
                | ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Integer)value).longValue()
                | ((Double)objValue).longValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Long)value).shortValue() > 0 ? true : false)
                | ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                | ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                | ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                | ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                | ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                | ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Long)value).longValue()
                | ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Long)value).longValue()
                | ((Double)objValue).longValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Float)value).shortValue() > 0 ? true : false)
                | ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Float)value).byteValue()
                | ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Float)value).byteValue()
                | ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Float)value).shortValue()
                | ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Float)value).intValue()
                | ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                | ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                | ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                | ((Double)objValue).longValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Double)value).shortValue() > 0 ? true : false)
                | ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Double)value).byteValue()
                | ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Double)value).byteValue()
                | ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Double)value).shortValue()
                | ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Double)value).intValue()
                | ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                | ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                | ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                | ((Double)objValue).longValue());
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive bitwiseXor( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, ((Boolean)value).booleanValue() ^ ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (((Boolean)value).booleanValue() ? 'y' : 'n')
                ^ ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)(((Boolean)value).booleanValue() ? 1 : 0)
                ^ ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)(((Boolean)value).booleanValue() ? 1 : 0)
                ^ ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)(((Boolean)value).booleanValue() ? 1 : 0)
                ^ ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                ^ ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                ^ ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)(((Boolean)value).booleanValue() ? 1 : 0)
                ^ ((Double)objValue).longValue());
            break;
        }
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Character)value).charValue() > 0 ? true : false)
                ^ ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, ((Character)value).charValue()
                ^ ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)((Character)value).charValue()
                ^ ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)((Character)value).charValue()
                ^ ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)((Character)value).charValue()
                ^ ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                ^ ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                ^ ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                ^ ((Double)objValue).longValue());
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Byte)value).byteValue() > 0 ? true : false)
                ^ ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Byte)value).byteValue()
                ^ ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Byte)value).byteValue()
                ^ ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Byte)value).shortValue()
                ^ ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Byte)value).intValue()
                ^ ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Byte)value).longValue()
                ^ ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Byte)value).longValue()
                ^ ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Byte)value).longValue()
                ^ ((Double)objValue).longValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Short)value).shortValue() > 0 ? true : false)
                ^ ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                ^ ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                ^ ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                ^ ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Short)value).intValue()
                ^ ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Short)value).longValue()
                ^ ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Short)value).longValue()
                ^ ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Short)value).longValue()
                ^ ((Double)objValue).longValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Integer)value).shortValue() > 0 ? true : false)
                ^ ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                ^ ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                ^ ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                ^ ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                ^ ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Integer)value).longValue()
                ^ ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Integer)value).longValue()
                ^ ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Integer)value).longValue()
                ^ ((Double)objValue).longValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Long)value).shortValue() > 0 ? true : false)
                ^ ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                ^ ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                ^ ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                ^ ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                ^ ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                ^ ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, (long)((Long)value).longValue()
                ^ ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, (long)((Long)value).longValue()
                ^ ((Double)objValue).longValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Float)value).shortValue() > 0 ? true : false)
                ^ ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Float)value).byteValue()
                ^ ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Float)value).byteValue()
                ^ ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Float)value).shortValue()
                ^ ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Float)value).intValue()
                ^ ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                ^ ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                ^ ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, ((Float)value).longValue()
                ^ ((Double)objValue).longValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( boolean.class, (((Double)value).shortValue() > 0 ? true : false)
                ^ ((Boolean)objValue).booleanValue());
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Double)value).byteValue()
                ^ ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Double)value).byteValue()
                ^ ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Double)value).shortValue()
                ^ ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Double)value).intValue()
                ^ ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                ^ ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                ^ ((Float)objValue).longValue());
            break;

          case DOUBLE:
            primitive = new Primitive( long.class, ((Double)value).longValue()
                ^ ((Double)objValue).longValue());
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive div( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, ((Character)value).charValue()
                / ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)((Character)value).charValue()
                / ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)((Character)value).charValue()
                / ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)((Character)value).charValue()
                / ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                / ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, (float)((Character)value).charValue()
                / ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, (double)((Character)value).charValue()
                / ((Double)objValue).doubleValue());
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Byte)value).byteValue()
                / ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Byte)value).byteValue()
                / ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Byte)value).shortValue()
                / ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Byte)value).intValue()
                / ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Byte)value).longValue()
                / ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Byte)value).floatValue()
                / ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Byte)value).doubleValue()
                / ((Double)objValue).doubleValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                / ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                / ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                / ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Short)value).intValue()
                / ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Short)value).longValue()
                / ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Short)value).floatValue()
                / ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Short)value).doubleValue()
                / ((Double)objValue).doubleValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                / ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                / ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                / ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                / ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Integer)value).longValue()
                / ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Integer)value).floatValue()
                / ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Integer)value).doubleValue()
                / ((Double)objValue).doubleValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                / ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                / ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                / ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                / ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                / ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Long)value).floatValue()
                / ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Long)value).doubleValue()
                / ((Double)objValue).doubleValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                / ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                / ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                / ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                / ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                / ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                / ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Float)value).doubleValue()
                / ((Double)objValue).doubleValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                / ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                / ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                / ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                / ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                / ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                / ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                / ((Double)objValue).doubleValue());
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive greaterEqual( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Character)value).charValue()
                >= ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, (byte)((Character)value).charValue()
                >= ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, (short)((Character)value).charValue()
                >= ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, (int)((Character)value).charValue()
                >= ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, (long)((Character)value).charValue()
                >= ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, (float)((Character)value).charValue()
                >= ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, (double)((Character)value).charValue()
                >= ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Character)value).toString().compareTo((String)objValue) >= 0);
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, (char)((Byte)value).byteValue()
                >= ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                >= ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Byte)value).shortValue()
                >= ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Byte)value).intValue()
                >= ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Byte)value).longValue()
                >= ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Byte)value).floatValue()
                >= ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Byte)value).doubleValue()
                >= ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                >= (new Byte((String)objValue)).byteValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                >= ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                >= ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                >= ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Short)value).intValue()
                >= ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Short)value).longValue()
                >= ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Short)value).floatValue()
                >= ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Short)value).doubleValue()
                >= ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                >= (new Short((String)objValue)).shortValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                >= ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                >= ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                >= ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                >= ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Integer)value).longValue()
                >= ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Integer)value).floatValue()
                >= ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Integer)value).doubleValue()
                >= ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                >= (new Integer((String)objValue)).intValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                >= ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                >= ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                >= ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                >= ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                >= ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Long)value).floatValue()
                >= ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Long)value).doubleValue()
                >= ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                >= (new Long((String)objValue)).longValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                >= ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                >= ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                >= ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                >= ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                >= ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                >= ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Float)value).doubleValue()
                >= ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                >= (new Float((String)objValue)).floatValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                >= ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                >= ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                >= ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                >= ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                >= ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                >= ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                >= ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                >= (new Double((String)objValue)).doubleValue());
            break;
        }
        break;

      case STRING:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Character)objValue).toString())
                >= 0);
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Byte)objValue).toString())
                >= 0);
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Short)objValue).toString())
                >= 0);
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Integer)objValue).toString())
                >= 0);
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Long)objValue).toString())
                >= 0);
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Float)objValue).toString())
                >= 0);
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Double)objValue).toString())
                >= 0);
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( (String)objValue)
                >= 0);
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive greaterThan( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Character)value).charValue()
                > ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, (byte)((Character)value).charValue()
                > ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, (short)((Character)value).charValue()
                > ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, (int)((Character)value).charValue()
                > ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, (long)((Character)value).charValue()
                > ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, (float)((Character)value).charValue()
                > ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, (double)((Character)value).charValue()
                > ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Character)value).toString().compareTo((String)objValue) > 0);
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, (char)((Byte)value).byteValue()
                > ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                > ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Byte)value).shortValue()
                > ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Byte)value).intValue()
                > ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Byte)value).longValue()
                > ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Byte)value).floatValue()
                > ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Byte)value).doubleValue()
                > ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                > (new Byte((String)objValue)).byteValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                > ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                > ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                > ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Short)value).intValue()
                > ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Short)value).longValue()
                > ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Short)value).floatValue()
                > ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Short)value).doubleValue()
                > ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                > (new Short((String)objValue)).shortValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                > ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                > ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                > ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                > ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Integer)value).longValue()
                > ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Integer)value).floatValue()
                > ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Integer)value).doubleValue()
                > ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                > (new Integer((String)objValue)).intValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                > ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                > ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                > ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                > ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                > ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Long)value).floatValue()
                > ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Long)value).doubleValue()
                > ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                > (new Long((String)objValue)).longValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                > ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                > ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                > ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                > ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                > ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                > ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Float)value).doubleValue()
                > ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                > (new Float((String)objValue)).floatValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                > ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                > ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                > ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                > ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                > ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                > ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                > ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                > (new Double((String)objValue)).doubleValue());
            break;
        }
        break;

      case STRING:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Character)objValue).toString())
                > 0);
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Byte)objValue).toString())
                > 0);
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Short)objValue).toString())
                > 0);
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Integer)objValue).toString())
                > 0);
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Long)objValue).toString())
                > 0);
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Float)objValue).toString())
                > 0);
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Double)objValue).toString())
                > 0);
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( (String)objValue)
                > 0);
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive lesserEqual( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Character)value).charValue()
                <=((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, (byte)((Character)value).charValue()
                <=((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, (short)((Character)value).charValue()
                <=((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, (int)((Character)value).charValue()
                <=((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, (long)((Character)value).charValue()
                <=((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, (float)((Character)value).charValue()
                <=((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, (double)((Character)value).charValue()
                <=((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Character)value).toString().compareTo((String)objValue) <= 0);
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, (char)((Byte)value).byteValue()
                <=((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                <=((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Byte)value).shortValue()
                <=((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Byte)value).intValue()
                <=((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Byte)value).longValue()
                <=((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Byte)value).floatValue()
                <=((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Byte)value).doubleValue()
                <=((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                <= (new Byte((String)objValue)).byteValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                <=((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                <=((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                <=((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Short)value).intValue()
                <=((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Short)value).longValue()
                <=((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Short)value).floatValue()
                <=((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Short)value).doubleValue()
                <=((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                <= (new Short((String)objValue)).shortValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                <=((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                <=((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                <=((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                <=((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Integer)value).longValue()
                <=((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Integer)value).floatValue()
                <=((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Integer)value).doubleValue()
                <=((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                <= (new Integer((String)objValue)).intValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                <=((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                <=((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                <=((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                <=((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                <=((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Long)value).floatValue()
                <=((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Long)value).doubleValue()
                <=((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                <= (new Long((String)objValue)).longValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                <=((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                <=((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                <=((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                <=((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                <=((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                <=((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Float)value).doubleValue()
                <=((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                <= (new Float((String)objValue)).floatValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                <=((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                <=((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                <=((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                <=((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                <=((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                <=((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                <=((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                <= (new Double((String)objValue)).doubleValue());
            break;
        }
        break;

      case STRING:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Character)objValue).toString())
                <= 0);
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Byte)objValue).toString())
                <= 0);
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Short)objValue).toString())
                <= 0);
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Integer)objValue).toString())
                <= 0);
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Long)objValue).toString())
                <= 0);
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Float)objValue).toString())
                <= 0);
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Double)objValue).toString())
                <= 0);
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( (String)objValue)
                <= 0);
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive lesserThan( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Character)value).charValue()
                < ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, (byte)((Character)value).charValue()
                < ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, (short)((Character)value).charValue()
                < ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, (int)((Character)value).charValue()
                < ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, (long)((Character)value).charValue()
                < ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, (float)((Character)value).charValue()
                < ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, (double)((Character)value).charValue()
                < ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Character)value).toString().compareTo((String)objValue) < 0);
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, (char)((Byte)value).byteValue()
                < ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                < ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Byte)value).shortValue()
                < ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Byte)value).intValue()
                < ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Byte)value).longValue()
                < ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Byte)value).floatValue()
                < ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Byte)value).doubleValue()
                < ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Byte)value).byteValue()
                < (new Byte((String)objValue)).byteValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                < ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                < ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                < ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Short)value).intValue()
                < ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Short)value).longValue()
                < ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Short)value).floatValue()
                < ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Short)value).doubleValue()
                < ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Short)value).shortValue()
                < (new Short((String)objValue)).shortValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                < ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                < ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                < ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                < ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Integer)value).longValue()
                < ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Integer)value).floatValue()
                < ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Integer)value).doubleValue()
                < ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Integer)value).intValue()
                < (new Integer((String)objValue)).intValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                < ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                < ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                < ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                < ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                < ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Long)value).floatValue()
                < ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Long)value).doubleValue()
                < ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Long)value).longValue()
                < (new Long((String)objValue)).longValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                < ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                < ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                < ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                < ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                < ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                < ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Float)value).doubleValue()
                < ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Float)value).floatValue()
                < (new Float((String)objValue)).floatValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                < ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                < ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                < ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                < ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                < ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                < ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                < ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((Double)value).doubleValue()
                < (new Double((String)objValue)).doubleValue());
            break;
        }
        break;

      case STRING:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Character)objValue).toString())
                < 0);
            break;

          case BYTE:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Byte)objValue).toString())
                < 0);
            break;

          case SHORT:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Short)objValue).toString())
                < 0);
            break;

          case INTEGER:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Integer)objValue).toString())
                < 0);
            break;

          case LONG:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Long)objValue).toString())
                < 0);
            break;

          case FLOAT:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Float)objValue).toString())
                < 0);
            break;

          case DOUBLE:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( ((Double)objValue).toString())
                < 0);
            break;

          case STRING:
            primitive = new Primitive( boolean.class, ((String)value).compareTo( (String)objValue)
                < 0);
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive mod( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, ((Character)value).charValue()
                % ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)((Character)value).charValue()
                % ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)((Character)value).charValue()
                % ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)((Character)value).charValue()
                % ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                % ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, (float)((Character)value).charValue()
                % ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, (double)((Character)value).charValue()
                % ((Double)objValue).doubleValue());
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Byte)value).byteValue()
                % ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Byte)value).byteValue()
                % ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Byte)value).shortValue()
                % ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Byte)value).intValue()
                % ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Byte)value).longValue()
                % ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Byte)value).floatValue()
                % ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Byte)value).doubleValue()
                % ((Double)objValue).doubleValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                % ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                % ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                % ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Short)value).intValue()
                % ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Short)value).longValue()
                % ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Short)value).floatValue()
                % ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Short)value).doubleValue()
                % ((Double)objValue).doubleValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                % ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                % ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                % ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                % ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Integer)value).longValue()
                % ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Integer)value).floatValue()
                % ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Integer)value).doubleValue()
                % ((Double)objValue).doubleValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                % ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                % ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                % ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                % ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                % ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Long)value).floatValue()
                % ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Long)value).doubleValue()
                % ((Double)objValue).doubleValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                % ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                % ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                % ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                % ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                % ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                % ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Float)value).doubleValue()
                % ((Double)objValue).doubleValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                % ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                % ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                % ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                % ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                % ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                % ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                % ((Double)objValue).doubleValue());
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive mult( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, ((Character)value).charValue()
                * ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)((Character)value).charValue()
                * ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)((Character)value).charValue()
                * ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)((Character)value).charValue()
                * ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                * ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, (float)((Character)value).charValue()
                * ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, (double)((Character)value).charValue()
                * ((Double)objValue).doubleValue());
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Byte)value).byteValue()
                * ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Byte)value).byteValue()
                * ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Byte)value).shortValue()
                * ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Byte)value).intValue()
                * ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Byte)value).longValue()
                * ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Byte)value).floatValue()
                * ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Byte)value).doubleValue()
                * ((Double)objValue).doubleValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                * ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                * ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                * ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Short)value).intValue()
                * ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Short)value).longValue()
                * ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Short)value).floatValue()
                * ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Short)value).doubleValue()
                * ((Double)objValue).doubleValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                * ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                * ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                * ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                * ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Integer)value).longValue()
                * ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Integer)value).floatValue()
                * ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Integer)value).doubleValue()
                * ((Double)objValue).doubleValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                * ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                * ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                * ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                * ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                * ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Long)value).floatValue()
                * ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Long)value).doubleValue()
                * ((Double)objValue).doubleValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                * ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                * ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                * ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                * ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                * ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                * ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Float)value).doubleValue()
                * ((Double)objValue).doubleValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                * ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                * ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                * ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                * ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                * ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                * ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                * ((Double)objValue).doubleValue());
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive substract( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, ((Character)value).charValue()
                - ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)((Character)value).charValue()
                - ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)((Character)value).charValue()
                - ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)((Character)value).charValue()
                - ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                - ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, (float)((Character)value).charValue()
                - ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, (double)((Character)value).charValue()
                - ((Double)objValue).doubleValue());
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Byte)value).byteValue()
                - ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Byte)value).byteValue()
                - ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Byte)value).shortValue()
                - ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Byte)value).intValue()
                - ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Byte)value).longValue()
                - ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Byte)value).floatValue()
                - ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Byte)value).doubleValue()
                - ((Double)objValue).doubleValue());
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                - ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                - ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                - ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Short)value).intValue()
                - ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Short)value).longValue()
                - ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Short)value).floatValue()
                - ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Short)value).doubleValue()
                - ((Double)objValue).doubleValue());
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                - ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                - ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                - ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                - ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Integer)value).longValue()
                - ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Integer)value).floatValue()
                - ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Integer)value).doubleValue()
                - ((Double)objValue).doubleValue());
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                - ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                - ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                - ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                - ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                - ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Long)value).floatValue()
                - ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Long)value).doubleValue()
                - ((Double)objValue).doubleValue());
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                - ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                - ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                - ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                - ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                - ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                - ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Float)value).doubleValue()
                - ((Double)objValue).doubleValue());
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                - ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                - ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                - ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                - ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                - ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                - ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                - ((Double)objValue).doubleValue());
            break;
        }
        break;
    }
    return primitive;
  }

  public Primitive add( Object obj)
  {
    Object objValue = null;
    if( !(obj instanceof Primitive))
      obj = new Primitive( obj);
    objValue = ((Primitive)obj).getValue();
    Primitive primitive = null;
    switch( typeId)
    {
      case BOOLEAN:
        primitive = null;
        break;

      case CHARACTER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, ((Character)value).charValue()
                + ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, (byte)((Character)value).charValue()
                + ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, (short)((Character)value).charValue()
                + ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, (int)((Character)value).charValue()
                + ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, (long)((Character)value).charValue()
                + ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, (float)((Character)value).charValue()
                + ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, (double)((Character)value).charValue()
                + ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( ((Character)value).toString() + (String)objValue);
            break;
        }
        break;

      case BYTE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( char.class, (char)((Byte)value).byteValue()
                + ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( byte.class, ((Byte)value).byteValue()
                + ((Byte)objValue).byteValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Byte)value).shortValue()
                + ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Byte)value).intValue()
                + ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Byte)value).longValue()
                + ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Byte)value).floatValue()
                + ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Byte)value).doubleValue()
                + ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( String.class, ((Byte)value).toString() + (String)objValue);
            break;
        }
        break;

      case SHORT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                + ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                + ((Byte)objValue).shortValue());
            break;

          case SHORT:
            primitive = new Primitive( short.class, ((Short)value).shortValue()
                + ((Short)objValue).shortValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Short)value).intValue()
                + ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Short)value).longValue()
                + ((Long)objValue).shortValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Short)value).floatValue()
                + ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Short)value).doubleValue()
                + ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( String.class, ((Short)value).toString() + (String)objValue);
            break;
        }
        break;

      case INTEGER:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                + ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                + ((Byte)objValue).intValue());
            break;

          case SHORT:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                + ((Short)objValue).intValue());
            break;

          case INTEGER:
            primitive = new Primitive( int.class, ((Integer)value).intValue()
                + ((Integer)objValue).intValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Integer)value).longValue()
                + ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Integer)value).floatValue()
                + ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Integer)value).doubleValue()
                + ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( String.class, ((Integer)value).toString() + (String)objValue);
            break;
        }
        break;

      case LONG:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                + ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                + ((Byte)objValue).longValue());
            break;

          case SHORT:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                + ((Short)objValue).longValue());
            break;

          case INTEGER:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                + ((Integer)objValue).longValue());
            break;

          case LONG:
            primitive = new Primitive( long.class, ((Long)value).longValue()
                + ((Long)objValue).longValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Long)value).floatValue()
                + ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Long)value).doubleValue()
                + ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( String.class, ((Long)value).toString() + (String)objValue);
            break;
        }
        break;

      case FLOAT:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                + ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                + ((Byte)objValue).floatValue());
            break;

          case SHORT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                + ((Short)objValue).floatValue());
            break;

          case INTEGER:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                + ((Integer)objValue).floatValue());
            break;

          case LONG:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                + ((Long)objValue).floatValue());
            break;

          case FLOAT:
            primitive = new Primitive( float.class, ((Float)value).floatValue()
                + ((Float)objValue).floatValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Float)value).doubleValue()
                + ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( String.class, ((Float)value).toString() + (String)objValue);
            break;
        }
        break;

      case DOUBLE:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = null;
            break;

          case CHARACTER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                + ((Character)objValue).charValue());
            break;

          case BYTE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                + ((Byte)objValue).doubleValue());
            break;

          case SHORT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                + ((Short)objValue).doubleValue());
            break;

          case INTEGER:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                + ((Integer)objValue).doubleValue());
            break;

          case LONG:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                + ((Long)objValue).doubleValue());
            break;

          case FLOAT:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                + ((Float)objValue).doubleValue());
            break;

          case DOUBLE:
            primitive = new Primitive( double.class, ((Double)value).doubleValue()
                + ((Double)objValue).doubleValue());
            break;

          case STRING:
            primitive = new Primitive( String.class, ((Double)value).toString() + (String)objValue);
            break;
        }
        break;

      case STRING:
        switch( ((Primitive)obj).getTypeId())
        {
          case BOOLEAN:
            primitive = new Primitive( ((String)value) + ((Boolean)objValue).toString());;
            break;

          case CHARACTER:
            primitive = new Primitive( ((String)value) + ((Character)objValue).toString());
            break;

          case BYTE:
            primitive = new Primitive( ((String)value) + ((Byte)objValue).toString());
            break;

          case SHORT:
            primitive = new Primitive( ((String)value) + ((Short)objValue).toString());
            break;

          case INTEGER:
            primitive = new Primitive( ((String)value) + ((Integer)objValue).toString());
            break;

          case LONG:
            primitive = new Primitive( ((String)value) + ((Long)objValue).toString());
            break;

          case FLOAT:
            primitive = new Primitive( ((String)value) + ((Float)objValue).toString());
            break;

          case DOUBLE:
            primitive = new Primitive( ((String)value) + ((Double)objValue).toString());
            break;

          case STRING:
            primitive = new Primitive( ((String)value) + (String)objValue);
            break;
        }
        break;
    }
    return primitive;
  }

  public void increment()
  {
    switch( typeId)
    {
      case CHARACTER:
        setValue( ((Character)value).charValue() + 1);
        break;

      case BYTE:
        setValue( ((Byte)value).byteValue() + 1);
        break;

      case SHORT:
        setValue( ((Short)value).shortValue() + 1);
        break;

      case INTEGER:
        setValue( ((Integer)value).intValue() + 1);
        break;

      case LONG:
        setValue( ((Long)value).longValue() + 1);
        break;

      case FLOAT:
        setValue( ((Float)value).floatValue() + (float)1);
        break;

      case DOUBLE:
        setValue( ((Double)value).doubleValue() + (double)1);
        break;
    }
  }

  public void decrement()
  {
    switch( typeId)
    {
      case CHARACTER:
        setValue( ((Character)value).charValue() - 1);
        break;

      case BYTE:
        setValue( ((Byte)value).byteValue() - 1);
        break;

      case SHORT:
        setValue( ((Short)value).shortValue() - 1);
        break;

      case INTEGER:
        setValue( ((Integer)value).intValue() - 1);
        break;

      case LONG:
        setValue( ((Long)value).longValue() - 1);
        break;

      case FLOAT:
        setValue( ((Float)value).floatValue() - (float)1);
        break;

      case DOUBLE:
        setValue( ((Double)value).doubleValue() - (double)1);
        break;
    }
  }

}
