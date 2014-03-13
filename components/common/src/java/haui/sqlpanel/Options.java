package haui.sqlpanel;

import haui.util.ConfigPathUtil;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Description of the Class
 * @author      A.E.
 * @created     12. Februar 2003
 */
public class Options extends Object
{
  /**
   *  Description of the Field
   */
  public boolean ready = false;

  Hashtable data;
  File m_fileRead;
  File m_fileSave;
  String m_strAppName;


  /**
   *Constructor for the Options object
   *
   * @param  name  Description of Parameter
   */
  public Options( String strAppName, String name )
  {
    super();
    m_strAppName = strAppName;
    if( ConfigPathUtil.getAppPath( m_strAppName) == null)
      ConfigPathUtil.init( m_strAppName);

    try
    {
      data = new Hashtable();

      m_fileRead = new File( ConfigPathUtil.getCurrentReadPath( m_strAppName, name) );
      m_fileSave = new File( ConfigPathUtil.getCurrentSavePath( m_strAppName, name) );

      if( !m_fileRead.exists() )
      {
        getDefaults();
        save();
      }
      else
      {
        load();
      }
      ready = true;
    }
    catch( Exception e )
    {
      e.printStackTrace();
      ready = false;
    }
  }


  /**
   *  Description of the Method
   *
   * @param  section  Description of Parameter
   * @param  key      Description of Parameter
   * @return          Description of the Returned Value
   */
  public Object get( String section, String key )
  {
    Object ret = null;
    if( data.containsKey( section ) )
    {
      Hashtable ht = (Hashtable)data.get( section );
      if( ht.containsKey( key ) )
      {
        ret = ht.get( key );
      }
    }
    return ret;
  }


  /**
   *  Gets the String attribute of the Options object
   *
   * @param  section  Description of Parameter
   * @param  key      Description of Parameter
   * @return          The String value
   */
  public String getString( String section, String key )
  {
    return (String)get( section, key );
  }


  /**
   *  Gets the Int attribute of the Options object
   *
   * @param  section  Description of Parameter
   * @param  key      Description of Parameter
   * @return          The Int value
   */
  public int getInt( String section, String key )
  {
    return ( (Integer)get( section, key ) ).intValue();
  }


  /**
   *  Gets the Boolean attribute of the Options object
   *
   * @param  section  Description of Parameter
   * @param  key      Description of Parameter
   * @return          The Boolean value
   */
  public boolean getBoolean( String section, String key )
  {
    return ( (Boolean)get( section, key ) ).booleanValue();
  }


// only useful, if first return value an String
  /**
   *  Description of the Method
   *
   * @param  section   Description of Parameter
   * @param  key       Description of Parameter
   * @param  section2  Description of Parameter
   * @return           Description of the Returned Value
   */
  public Object get( String section, String key, String section2 )
  {
    String s = (String)get( section, key );
    if( s != null )
    {
      return get( section2, s );
    }
    else
    {
      return null;
    }
  }


  /**
   *  Gets the String attribute of the Options object
   *
   * @param  section   Description of Parameter
   * @param  key       Description of Parameter
   * @param  section2  Description of Parameter
   * @return           The String value
   */
  public String getString( String section, String key, String section2 )
  {
    return (String)get( section, key, section2 );
  }


  /**
   *  Gets the AllKeys attribute of the Options object
   *
   * @param  section  Description of Parameter
   * @return          The AllKeys value
   */
  public Vector getAllKeys( String section )
  {
    Vector all = new Vector();
    if( data.containsKey( section ) )
    {
      Hashtable ht = (Hashtable)data.get( section );
      Enumeration enumeration = ht.keys();

      while( enumeration.hasMoreElements() )
      {
        all.addElement( enumeration.nextElement() );
      }
    }
    return all;
  }


  /**
   *  Description of the Method
   */
  public void close()
  {
    try
    {
      save();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   */
  public boolean save()
  {
    try
    {
      FileOutputStream fos = new FileOutputStream( m_fileSave );
      ObjectOutputStream oos = new ObjectOutputStream( fos );

      oos.writeObject( data );

      oos.flush();
      fos.close();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    return false;
  }


  /**
   *  Description of the Method
   */
  public void load()
  {
    try
    {
      FileInputStream fis = new FileInputStream( m_fileRead );
      ObjectInputStream ois = new ObjectInputStream( fis );

      data = (Hashtable)ois.readObject();

      ois.close();
      fis.close();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }


  /**
   *  Description of the Method
   *
   * @param  section  Description of Parameter
   * @param  key      Description of Parameter
   * @param  value    Description of Parameter
   */
  public void put( String section, String key, Object value )
  {
    Hashtable ht = (Hashtable)data.get( section );
    if( ht == null )
    {
      ht = new Hashtable();
    }
    ht.put( key, value );
    data.put( section, ht );
  }


  /**
   *  Gets the Defaults attribute of the Options object
   */
  private void getDefaults()
  {
    Hashtable main = new Hashtable();
    main.put( "VERSION", new Double( 1.04 ) );
    main.put( "POS", new Point( 10, 10 ) );
    main.put( "SIZE", new Dimension( 640, 480 ) );

    Hashtable parser = new Hashtable();
    parser.put( "DEFAULT", new SQLParserFont( "DEFAULT", 14, "Courier", "BLACK", false, false, false ) );
    parser.put( "KEYWORD", new SQLParserFont( "KEYWORD", 14, "Courier", "BLUE", false, false, true ) );
    parser.put( "FUNCTION", new SQLParserFont( "FUNCTION", 14, "Courier", "DARKBLUE", true, false, true ) );
    parser.put( "OPERATOR", new SQLParserFont( "OPERATOR", 14, "Courier", "DARKGREEN", false, false, false ) );
    parser.put( "DATATYPE", new SQLParserFont( "DATATYPE", 14, "Courier", "DARKGREEN", false, true, false ) );
    parser.put( "VALUE", new SQLParserFont( "VALUE", 14, "Courier", "MAGENTA", false, false, true ) );
    parser.put( "STRING", new SQLParserFont( "STRING", 14, "Courier", "DARKRED", true, false, false ) );
    //parser.put( "BACKGROUND", "PAPER" );
    parser.put( "BACKGROUND", "WHITE" );

    Hashtable colors = new Hashtable();
    colors.put( "BLACK", Color.black );
    colors.put( "WHITE", Color.white );
    colors.put( "RED", Color.red );
    colors.put( "DARKRED", new Color( 128, 0, 0 ) );
    colors.put( "BLUE", Color.blue );
    colors.put( "DARKBLUE", new Color( 0, 0, 128 ) );
    colors.put( "GREEN", Color.green );
    colors.put( "DARKGREEN", new Color( 0, 128, 0 ) );
    colors.put( "CYAN", Color.cyan );
    colors.put( "MAGENTA", Color.magenta );
    colors.put( "YELLOW", Color.yellow );
    colors.put( "GRAY", Color.gray );
    colors.put( "DARKGRAY", Color.darkGray );
    colors.put( "LIGHTGRAY", Color.lightGray );
    colors.put( "ORANGE", Color.orange );
    colors.put( "PINK", Color.pink );
    colors.put( "PAPER", new Color( 255, 255, 220 ) );

    Hashtable html = new Hashtable();
    html.put( "PAGEBG", Color.white );
    html.put( "HEADERBG", Color.gray );
    html.put( "HEADERFG", Color.black );
    html.put( "TABLEBG", Color.white );
    html.put( "TABLEFG", Color.white );
    html.put( "BORDERWIDTH", new Integer( 1 ) );
    html.put( "CELLPADDING", new Integer( 0 ) );
    html.put( "CELLSPACING", new Integer( 0 ) );
    html.put( "MAXROWS", new Integer( 0 ) );
    html.put( "PAGEBREAK", new Boolean( false ) );

    data.put( "MAIN", main );
    data.put( "PARSER", parser );
    data.put( "COLORS", colors );
    data.put( "HTML", html );
  }

}
