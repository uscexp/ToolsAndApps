package haui.util;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Module:      PropertyStruct.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\PropertyStruct.java,v $
 *<p>
 * Description: Represents a property struct object.<br>
 * Example:<br>
 <pre>
structname
{
  varname1 = true;  // Boolean
  varname2 = 5; // Long
  varname3 = "so so";
  varname4 = {true, false, true}; // Array (Vector) of Booleans
  varname5 = {1, 2, 4, 3};  // Array (Vector) of Longs
  varname6 = {"bla bla", "geht das so", "und mit"};  // Array (Vector) of Strings
}
 <pre>
 * Example access to the values:<br>
 * PropertyStruct ps = ...;
 * ...
 * long l = ps.longValue( "varname5[2]");
 * Boolean bl = (Boolean)ps.value( "varname1");
 * Vector vec = ps.vectorValue( "varname5");
 *</p><p>
 * Created:     25.02.2003 by AE
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     25. Februar 2003
 * @history     25.02.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: PropertyStruct.java,v $
 * Revision 1.1  2004-02-17 16:31:25+01  t026843
 * bugfixes
 *
 * Revision 1.0  2003-05-21 16:26:21+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\PropertyStruct.java,v 1.1 2004-02-17 16:31:25+01 t026843 Exp $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class PropertyStruct
{
  // member variables
  /** hash map which contains the content of the struct */
  protected HashMap m_hm;
  /** name of the struct */
  protected String m_strStructName;


  /**
   *  Constructor for the PropertyStruct object
   */
  public PropertyStruct()
  {
    m_hm = new HashMap();
  }


  /**
   *  Sets the struct name of the struct
   *
   * @param  strStructName  name of the struct
   */
  public void setStructName( String strStructName )
  {
    m_strStructName = strStructName;
  }


  /**
   *  add or update a value object
   *
   * @param  strKey    key (variable path) of the value
   * @param  objValue  value object (String, Long, Double, Boolean, Vector, PropertyStruct)
   */
  public void setValue( String strKey, Object objValue )
  {
    if( objValue instanceof PropertyStruct )
      setStructValue( strKey, (PropertyStruct)objValue );
    else if( objValue instanceof Vector )
      setVectorValue( strKey, (Vector)objValue );
    else if( objValue instanceof String || objValue instanceof Long
         || objValue instanceof Boolean || objValue instanceof Double )
      m_hm.put( strKey, objValue );
    else
      System.err.println( "Error: Wrong Object type!" );
  }


  /**
   *  add or update a string value
   *
   * @param  strKey    key (variable path) of the value
   * @param  strValue  string value
   */
  public void setStringValue( String strKey, String strValue )
  {
    setValue( strKey, strValue );
  }


  /**
   *  add or update a long value
   *
   * @param  strKey  key (variable path) of the value
   * @param  lValue  long value
   */
  public void setLongValue( String strKey, long lValue )
  {
    setValue( strKey, new Long( lValue ) );
  }


  /**
   *  add or update a int value (actually it is a long value!)
   *
   * @param  strKey  key (variable path) of the value
   * @param  iValue  int value
   */
  public void setIntValue( String strKey, int iValue )
  {
    setValue( strKey, new Long( (long)iValue ) );
  }


  /**
   *  add or update a double value
   *
   * @param  strKey  key (variable path) of the value
   * @param  dValue  double value
   */
  public void setDoubleValue( String strKey, double dValue )
  {
    setValue( strKey, new Double( dValue ) );
  }


  /**
   *  add or update a float value (actually it is a double value!)
   *
   * @param  strKey  key (variable path) of the value
   * @param  dValue  float value
   */
  public void setFloatValue( String strKey, float fValue )
  {
    setValue( strKey, new Double( (double)fValue ) );
  }


  /**
   *  add or update a boolean value
   *
   * @param  strKey   key (variable path) of the value
   * @param  blValue  boolean value
   */
  public void setBooleanValue( String strKey, boolean blValue )
  {
    setValue( strKey, new Boolean( blValue ) );
  }


  /**
   *  add or update a vector (array) value
   *
   * @param  strKey    key (variable path) of the value
   * @param  vecValue  vector value
   */
  public void setVectorValue( String strKey, Vector vecValue )
  {
    String strIdx;
    int iLen = vecValue.size();

    removeVector( strKey);
    for( int i = 0; i < iLen; ++i )
    {
      strIdx = "[" + String.valueOf( i ) + "]";
      m_hm.put( strKey + strIdx, vecValue.elementAt( i ) );
    }
  }


  /**
   *  add or update a PropertyStruct value
   *
   * @param  strKey   key (variable path) of the value
   * @param  pstruct  PropertyStruct value
   */
  public void setStructValue( String strKey, PropertyStruct pstruct )
  {
    Set tsInStructKeys = pstruct.getAllKeys();
    Iterator it = tsInStructKeys.iterator();

    if( pstruct.getStructName() != null )
      removeStructValue( strKey + "." + pstruct.getStructName());
    else
      removeStructValue( strKey);
    while( it.hasNext() )
    {
      String strInStructKey = (String)it.next();
      String strNewKey;

      if( pstruct.getStructName() != null )
        strNewKey = strKey + "." + pstruct.getStructName() + "." + strInStructKey;
      else
        strNewKey = strKey + "." + strInStructKey;

      m_hm.put( strNewKey, pstruct.value( strInStructKey ) );
    }
  }


  /**
   *  gets all keys of the PropertyStruct
   *
   * @return    all keys of the PropertyStruct
   */
  public Set getAllKeys()
  {
    return m_hm.keySet();
  }


  /**
   *  gets the name of the PropertyStruct
   *
   * @return    name of the PropertyStruct
   */
  public String getStructName()
  {
    return m_strStructName;
  }


  /**
   *  gets all the keys, which include the subkey, of the PropertyStruct
   *
   * @param  strSubKey  subkey
   * @return            all the keys which include the subkey
   */
  public Vector getAllKeysWithSubKey( String strSubKey )
  {
    Vector vec = new Vector();
    TreeSet ts = new TreeSet( m_hm.keySet() );
    Iterator it = ts.iterator();

    while( it.hasNext() )
    {
      String str = (String)it.next();

      if( str.startsWith( strSubKey ) )
        vec.add( str );
    }
    return vec;
  }


  /**
   *  gets all first level keys of the PropertyStruct
   *
   * @return    all first level keys
   */
  public Vector getAllFirstLevelKeys()
  {
    Vector vec = new Vector();
    TreeSet ts = new TreeSet( m_hm.keySet() );
    Iterator it = ts.iterator();

    while( it.hasNext() )
    {
      String str = (String)it.next();
      String strKeyPart;

      int iFirstIdx = str.indexOf( '.' );
      if( iFirstIdx > 0 )
        strKeyPart = str.substring( 0, iFirstIdx );
      else
        strKeyPart = str;

      int iIdx = strKeyPart.indexOf( '[' );
      if( iIdx != -1 )
        strKeyPart = strKeyPart.substring( 0, iIdx );
      if( !vec.contains( strKeyPart ) )
        vec.add( strKeyPart );
    }
    return vec;
  }


  /**
   *  adds (or updates) a PropertyStruct into the PropertyStruct
   *
   * @param  pstruct  PropertyStruct to add (or update)
   */
  public final void addStructValue( PropertyStruct pstruct )
  {
    if( pstruct.getStructName() == null )
    {
      System.err.println( "Error: The PropertyStruct has no struct name defined!" );
      pstruct.getStructName().charAt( 0 );
      return;
    }
    Set tsInStructKeys = pstruct.getAllKeys();
    Iterator it = tsInStructKeys.iterator();

    removeStructValue( pstruct);
    while( it.hasNext() )
    {
      String strInStructKey = (String)it.next();
      String strNewKey;

      strNewKey = pstruct.getStructName() + "." + strInStructKey;

      m_hm.put( strNewKey, pstruct.value( strInStructKey ) );
    }
  }


  /**
   *  removes a PropertyStruct from within this PropertyStruct
   *
   * @param  pstruct  PropertyStruct to remove
   */
  public final void removeStructValue( PropertyStruct pstruct )
  {
    if( pstruct.getStructName() == null )
    {
      System.err.println( "Error: The PropertyStruct has no struct name defined!" );
      return;
    }
    String strKey = pstruct.getStructName();
    removeStructValue( strKey );
  }


  /**
   *  removes a PropertyStruct from within this PropertyStruct
   *
   * @param  strKey  key (variable path) of the PropertyStruct
   */
  protected void removeStructValue( String strKey )
  {
    if( strKey != null )
    {
      Vector vec = getAllKeysWithSubKey( strKey );
      for( int i = 0; i < vec.size(); ++i )
      {
        String str = (String)vec.elementAt( i );

        remove( str );
      }
    }
  }


  /**
   *  removes a vector (array) from the PropertyStruct
   *
   * @param  strKey  key (variable path) of the vector
   */
  public void removeVector( String strKey )
  {
    if( strKey != null )
    {
      String strArrayKey = strKey + "[";
      Vector vec = getAllKeysWithSubKey( strArrayKey );
      for( int i = 0; i < vec.size(); ++i )
      {
        String str = (String)vec.elementAt( i );

        remove( str );
      }
    }
  }


  /**
   *  removes a value
   *
   * @param  strKey  key (variable path) of the value
   */
  public void remove( String strKey )
  {
    m_hm.remove( strKey );
  }


  /**
   *  gets a value object of the PropertyStruct
   *
   * @param  strKey  key (variable path) of the value
   * @return         value object (String, Long, Double, Boolean, Vector, PropertyStruct)
   */
  public Object value( String strKey )
  {
    Object obj = m_hm.get( strKey );

    if( obj == null )
    {
      obj = vectorValue( strKey );
      if( obj == null )
      {
        obj = structValue( strKey );
      }
    }
    return obj;
  }


  /**
   *  gets a string value of the PropertyStruct
   *
   * @param  strKey  key (variable path) of the value
   * @return         string value
   */
  public String stringValue( String strKey )
  {
    Object obj = value( strKey );
    if( obj == null)
      return null;
    return obj.toString();
  }


  /**
   *  gets a long value of the PropertyStruct
   *
   * @param  strKey  key (variable path) of the value
   * @return         long value
   */
  public long longValue( String strKey )
  {
    Object obj = value( strKey );
    long l = 0;

    if( obj instanceof Long )
      l = ( (Long)obj ).longValue();
    else if( obj instanceof Boolean )
    {
      boolean bl = ( (Boolean)obj ).booleanValue();
      l = bl ? 1 : 0;
    }
    else if( obj instanceof Double )
    {
      Double d = (Double)obj;
      l = d.longValue();
    }
    else if( obj instanceof String )
    {
      l = Long.parseLong( (String)obj );
    }
    return l;
  }


  /**
   *  gets a int value of the PropertyStruct. Attention: The value is only a down cast of a long value!
   *
   * @param  strKey  key (variable path) of the value
   * @return         int value
   */
  public int intValue( String strKey )
  {
    Object obj = value( strKey );
    int i = 0;

    if( obj instanceof Long )
      i = ( (Long)obj ).intValue();
    else if( obj instanceof Boolean )
    {
      boolean bl = ( (Boolean)obj ).booleanValue();
      i = bl ? 1 : 0;
    }
    else if( obj instanceof Double )
    {
      Double d = (Double)obj;
      i = d.intValue();
    }
    else if( obj instanceof String )
    {
      i = (new Long( (String)obj )).intValue();
    }
    return i;
  }


  /**
   *  gets a double value of the PropertyStruct
   *
   * @param  strKey  key (variable path) of the value
   * @return         double value
   */
  public double doubleValue( String strKey )
  {
    Object obj = value( strKey );
    double d = (double)0.0;

    if( obj instanceof Double )
    {
      d = ( (Double)obj ).doubleValue();
    }
    else if( obj instanceof Boolean )
    {
      boolean bl = ( (Boolean)obj ).booleanValue();
      d = (double)( bl ? 1.0 : 0.0 );
    }
    else if( obj instanceof Long )
    {
      Long l = (Long)obj;
      d = l.doubleValue();
    }
    else if( obj instanceof String )
    {
      d = new Double( (String)obj ).doubleValue();
    }
    return d;
  }


  /**
   *  gets a float value of the PropertyStruct. Attention: The value is only a down cast of a double value!
   *
   * @param  strKey  key (variable path) of the value
   * @return         float value
   */
  public float floatValue( String strKey )
  {
    Object obj = value( strKey );
    float f = (float)0.0;

    if( obj instanceof Double )
    {
      f = ( (Double)obj ).floatValue();
    }
    else if( obj instanceof Boolean )
    {
      boolean bl = ( (Boolean)obj ).booleanValue();
      f = (float)( bl ? 1.0 : 0.0 );
    }
    else if( obj instanceof Long )
    {
      Long l = (Long)obj;
      f = l.floatValue();
    }
    else if( obj instanceof String )
    {
      f = new Double( (String)obj ).floatValue();
    }
    return f;
  }


  /**
   *  gets a boolean value of the PropertyStruct
   *
   * @param  strKey  key (variable path) of the value
   * @return         boolean value
   */
  public boolean booleanValue( String strKey )
  {
    Object obj = value( strKey );
    boolean bl = false;

    if( obj instanceof Boolean )
    {
      bl = ( (Boolean)obj ).booleanValue();
    }
    else if( obj instanceof Long )
    {
      long l = ( (Long)obj ).longValue();
      bl = ( l > 0 );
    }
    else if( obj instanceof Double )
    {
      double d = ( (Double)obj ).doubleValue();
      bl = !( d == (double)0.0 );
    }
    else if( obj instanceof String )
    {
      String str = (String)obj;

      if( str.equalsIgnoreCase( "true" ) )
        bl = true;
      else if( str.equalsIgnoreCase( "false" ) )
        bl = false;
      else
      {
        long i = Long.parseLong( (String)obj );
        bl = ( i > 0 );
      }
    }
    return bl;
  }


  /**
   *  gets a vector (array) value of the PropertyStruct
   *
   * @param  strKey  key (variable path) of the value
   * @return         vector (array) value
   */
  public Vector vectorValue( String strKey )
  {
    String strArrayKey = strKey + "[";
    int iLen = strKey.length();
    Vector vec = null;
    Vector vecKeys = new Vector();
    Set ts = new TreeSet( m_hm.keySet() );
    Iterator it = ts.iterator();

    while( it.hasNext() )
    {
      String str = (String)it.next();
      int iIdx = str.indexOf( '.', iLen );
      if( iIdx != -1 )
        str = str.substring( 0, iIdx );

      if( str.startsWith( strArrayKey ) && !vecKeys.contains( str ) )
      {
        vecKeys.add( str );
        if( vec == null )
          vec = new Vector();
        vec.add( value( str ) );
      }
    }
    return vec;
  }


  /**
   *  gets a struct (PropertyStruct) value of the PropertyStruct
   *
   * @param  strKey  key (variable path) of the value
   * @return         struct (PropertyStruct) value
   */
  public PropertyStruct structValue( String strKey )
  {
    PropertyStruct pstruct = null;
    int iKeyLen = strKey.length();
    int iIdx = strKey.lastIndexOf( '.' );
    int i = 0;
    if( !strKey.endsWith( "]" ) )
    {
      if( pstruct == null )
        pstruct = new PropertyStruct();
      if( iIdx > -1 )
        pstruct.setStructName( strKey.substring( iIdx + 1 ) );
      else
        pstruct.setStructName( strKey );
    }
    Vector vecKeys = getAllKeysWithSubKey( strKey );
    int iLen = vecKeys.size();

    if( iLen == 0 )
      return null;
    for( i = 0; i < iLen; ++i )
    {
      String strOldKey = (String)vecKeys.elementAt( i );
      String strNewKey;

      if( iKeyLen + 1 < strOldKey.length() )
      {
        strNewKey = strOldKey.substring( iKeyLen + 1 );
        if( pstruct == null )
          pstruct = new PropertyStruct();
        pstruct.setValue( strNewKey, value( strOldKey ) );
      }
    }
    return pstruct;
  }


  /**
   *  checks if the given key exists
   *
   * @param  strKey  key to check
   * @return         true if exists, otherwise false
   */
  public boolean containsKey( String strKey )
  {
    return m_hm.containsKey( strKey );
  }


  /**
   *  checks if a given value exists. can only check native datatypes, like String, Long, Double, Boolean.
   *  can not check Vector or PropertyStruct, in this case it would return false.
   *
   * @param  objValue  value to check
   * @return           true if exists, otherwise false
   */
  public boolean containsValue( Object objValue )
  {
    return m_hm.containsValue( objValue );
  }


  /**
   *  a string representation of the values in the PropertyStruct hash map
   *
   * @return    string representation of the values
   */
  public String toSting()
  {
    return m_hm.toString();
  }


  /**
   *  write this PropertyStruct to the writer
   *
   * @param  pw      PrintWriter
   * @param  iLevel  blocklevel {}
   */
  protected void write( PrintWriter pw, int iLevel )
  {
    Vector vecFKeys = getAllFirstLevelKeys();

    pw.println();
    for( int i = 0; i < iLevel - 1; ++i )
      pw.print( "  " );
    pw.println( "{" );

    for( int iFKeys = 0; iFKeys < vecFKeys.size(); ++iFKeys )
    {
      String strFKey = (String)vecFKeys.elementAt( iFKeys );

      for( int i = 0; i < iLevel; ++i )
        pw.print( "  " );
      pw.print( strFKey );

      Object obj = value( strFKey );
      writeObject( pw, obj, true, iLevel );
    }
    for( int i = 0; i < iLevel - 1; ++i )
      pw.print( "  " );
    pw.println( "}" );
    for( int i = 0; i < iLevel - 2; ++i )
      pw.print( "  " );
  }


  /**
   *  write a vector to the writer
   *
   * @param  pw      PrintWriter
   * @param  vec     Vector to write
   * @param  iLevel  blocklevel {}
   */
  protected void writeVector( PrintWriter pw, Vector vec, int iLevel )
  {
    pw.print( " = {" );
    for( int i = 0; i < vec.size(); ++i )
    {
      Object obj = vec.elementAt( i );
      if( obj instanceof PropertyStruct )
      {
        pw.print( "  " );
      }
      if( i > 0 )
        pw.print( ", " );
      writeObject( pw, obj, false, iLevel );
    }
    pw.println( "};" );
  }


  /**
   *  write a value object (String, Long, Double, Boolean, Vector, PropertyStruct) to the writer
   *
   * @param  pw           PrintWriter
   * @param  obj          Object to write
   * @param  blSemicolon  flag if semicolon should be written
   * @param  iLevel       blocklevel {}
   */
  protected void writeObject( PrintWriter pw, Object obj, boolean blSemicolon, int iLevel )
  {
    if( obj instanceof PropertyStruct )
    {
      ( (PropertyStruct)obj ).write( pw, ++iLevel );
    }
    else if( obj instanceof Vector )
    {
      writeVector( pw, (Vector)obj, ++iLevel );
    }
    else
    {
      if( blSemicolon )
        pw.print( " = " );
      if( obj instanceof String )
      {
        pw.print( "\"" );
        pw.print( obj );
        pw.print( "\"" );
      }
      else
        pw.print( obj );
      if( blSemicolon )
        pw.println( ";" );
    }
  }
}
