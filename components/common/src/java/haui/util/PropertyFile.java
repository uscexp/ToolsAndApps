package haui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.JProgressBar;

/**
 * Module:      PropertyFile.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\PropertyFile.java,v $
 *<p>
 * Description: Represents a properties object that is loaded from a property file.<br>
 * Example:<br>
 <pre>
// Comment

type elementname  // element type and name
{
  varname1 = 10.0;  // Double
  varname2 = "hallo, so geht das";  // String
  varname3 = {
    {   // Array (Vector) of structs
      varname1 = true;  // Boolean
      varname2 = 5; // Long
      varname3 = "so so";
      varname4 = {true, false, true}; // Array (Vector) of Booleans
      varname5 = {1, 2, 4, 3};  // Array (Vector) of Longs
      varname6 = {"bla bla", "geht das so", "und mit"};  // Array (Vector) of Strings
    }
    ,
    {
      varname1 = true;
    }
  };
  varname4  // Struct (PropertyStruct)
  {
    varname1 = 10;
    varname2 = "xyz";
  }
}
 <pre>
 * Example access to the values:<br>
 * PropertyFile pf = ...;
 * ...
 * double d = pf.doubleValue( "elementname.varname1");
 * long l = pf.longValue( "elementname.varname3[0].varname5[2]");
 * Boolean bl = (Boolean)pf.value( "elementname.varname3[0].varname1");
 * Vector vec = pf.vectorValue( "elementname.varname3[0].varname5");
 * PropertyStruct pstruct = pf.structValue( "elementname.varname4");
 * PropertyStruct pstruct1 = pf.structValue( "elementname.varname3[0]");
 *</p><p>
 * Created:     21.02.2003 by AE
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     21. Februar 2003
 * @history     21.02.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: PropertyFile.java,v $
 * Revision 1.2  2004-06-22 14:08:54+02  t026843
 * bigger changes
 *
 * Revision 1.1  2004-02-17 16:31:26+01  t026843
 * bugfixes
 *
 * Revision 1.0  2003-05-21 16:26:21+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.2 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\PropertyFile.java,v 1.2 2004-06-22 14:08:54+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class PropertyFile
     extends PropertyStruct
{

  // member variables
  /** URL to the property file */
  private URL m_fileRead;
  private URL m_fileSave;

  /** comment of the property file */
  private String m_strComment;

  // constants
  /** key to accass all the types of the property file */
  private final static String TYPEKEY = "_TYPE_";
  /** parse state type */
  private final static int TYPE = 1;
  /** parse state elementname */
  private final static int NAME = 2;
  /** parse state element body */
  private final static int BODY = 3;
  /** parse state key (variable path) */
  private final static int KEY = 4;
  /** parse state normal value (String, Long, Double, Boolean) */
  private final static int NORMALVALUE = 5;
  /** parse state array (vector) */
  private final static int ARRAYVALUE = 6;
  /** parse state struct */
  private final static int STRUCT = 7;

  //private boolean m_blChache;

  /**
   *Constructor for the PropertyFile object
   *
   * @param  file  URL to file
   */
  public PropertyFile( URL fileRead, URL fileSave /*, boolean blChache */ )
  {
    m_fileRead = fileRead;
    m_fileSave = fileSave;
    //m_blChache = blChache;
  }


  /**
   *  Sets the types of a PropertyFile
   *
   * @param  vecTypes  Vector with all types (strings)
   */
  public void setTypes( Vector vecTypes )
  {
    m_hm.put( TYPEKEY, vecTypes );
  }


  /**
   *  Gets the types of a PropertyFile
   *
   * @return    Vector with all types (strings)
   */
  public Vector getTypes()
  {
    return (Vector)m_hm.get( TYPEKEY );
  }


  /**
   *  Gets the type of an elementname
   *
   * @param  strElementname  name of the element
   *
   * @return    type of the element
   */
  public String getType( String strElementname)
  {
    String strRet = null;
    Vector vecTypes = getTypes();

    for( int i = 0; i < vecTypes.size(); ++i)
    {
      String strType = (String)vecTypes.elementAt( i);
      Vector vecElements = getElementnamesByType( strType);

      if( vecElements != null && vecElements.contains( strElementname))
      {
        strRet = strType;
        break;
      }
    }
    return strRet;
  }


  /**
   *  Gets the elementnames by type of a PropertyFile
   *
   * @param  strTypeKey  type
   * @return             vector of the elementnames (strings) of that type
   */
  public Vector getElementnamesByType( String strTypeKey )
  {
    return (Vector)m_hm.get( strTypeKey );
  }


  /**
   *  Gets all the elements of a PropertyFile
   *
   * @return             vector of the elements (PropertyStruct)
   */
  public Vector getAllElements()
  {
    Vector vecTypeNames = getTypes();
    Vector vecPStructs = new Vector();

    for( int i = 0; i < vecTypeNames.size(); ++i)
    {
      String strTypeKey = (String)vecTypeNames.elementAt( i);
      Vector vecNames = (Vector)m_hm.get( strTypeKey );

      if( vecNames != null)
      {
        PropertyStruct pstruct;
        for( int j = 0; j < vecNames.size(); ++j )
        {
          pstruct = structValue( ( String )vecNames.elementAt( j ) );
          vecPStructs.add( pstruct );
        }
      }
    }

    return vecPStructs;
  }

  /**
  /**
   *  Gets all the elements for a type of a PropertyFile
   *
   * @param  strTypeKey  type
   * @return             vector of the elements (PropertyStruct) of that type
   */
  public Vector getElementsByType( String strTypeKey )
  {
    Vector vecNames = (Vector)m_hm.get( strTypeKey );
    Vector vecPStructs = new Vector();

    if( vecNames != null)
    {
      PropertyStruct pstruct;
      for( int i = 0; i < vecNames.size(); ++i )
      {
        pstruct = structValue( ( String )vecNames.elementAt( i ) );
        vecPStructs.add( pstruct );
      }
    }

    return vecPStructs;
  }


 /**
  *  Find all the elements by PropertyCondition of a PropertyFile
  *
  * @param  pc           PropertyCondition to filter the result
  * @return              vector of the elements (PropertyStruct) found
  */
 public Vector getElementsByCondition( PropertyCondition pc)
 {
   Vector vecRet = new Vector();
   Vector vecPStructs = getAllElements();

   for( int i = 0; i < vecPStructs.size(); ++i)
   {
     PropertyStruct ps = (PropertyStruct)vecPStructs.elementAt( i);
     pc.extractValueToBeChecked( ps);
     if( pc.compare())
       vecRet.add( ps);
   }

   return vecRet;
 }


  /**
   *  Find all the elements by type and PropertyCondition of a PropertyFile
   *
   * @param  strTypeKey   type
   * @param  pc           PropertyCondition to filter the result
   * @return              vector of the elements (PropertyStruct) found
   */
  public Vector getElementsByTypeAndCondition( String strTypeKey, PropertyCondition pc)
  {
    Vector vecRet = new Vector();
    Vector vecPStructs = getElementsByType( strTypeKey);

    for( int i = 0; i < vecPStructs.size(); ++i)
    {
      PropertyStruct ps = (PropertyStruct)vecPStructs.elementAt( i);
      pc.extractValueToBeChecked( ps);
      if( pc.compare())
        vecRet.add( ps);
    }

    return vecRet;
  }


  /**
   *  Sets a comment of the PropertyFile. Has to begin with '//' on each new lie!
   *
   * @param  strComment  comment
   */
  public void setComment( String strComment )
  {
    m_strComment = strComment;
  }


  /**
   *  Gets the comment of a PropertyFile
   *
   * @return    comment
   */
  public String getComment()
  {
    return m_strComment;
  }


  /**
   *  check existance of the property file
   *
   * @return    true if exist, otherwise false
   */
  public boolean exists()
  {
    File fi;
    try
    {
      fi = new File( m_fileRead.getPath());
    }
    catch( Exception ex)
    {
      return false;
    }
    if( fi == null)
      return false;
    return fi.exists();
  }

  /**
   *  load and parse the property file
   */
  public void load()
  {
    load( null);
  }


  /**
   *  load and parse the property file
   */
  public void load( JProgressBar pb)
  {
    try
    {
      BufferedReader br = new BufferedReader( new InputStreamReader( m_fileRead.openStream() ) );
      if( pb != null)
      {
        long lSize = (new File( m_fileRead.getPath())).length();
        pb.setMaximum( ( int )lSize );
        pb.setValue( 0 );
        //pb.setIndeterminate( false );
      }
      parseTopLevel( br, pb);
      br.close();
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }


  /**
   *  save the properties to file
   */
  public void save()
  {
    try
    {
      PrintWriter pw = new PrintWriter( new FileWriter( m_fileSave.getPath()));
      write( pw, 0 );
      pw.flush();
      pw.close();
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }


  /**
   *  Adds a new type
   *
   * @param  strType  the new type
   * @return          false if allready exists, otherwise true
   */
  public boolean addType( String strType )
  {
    boolean blRet = false;
    Vector vecTypeKeys = (Vector)m_hm.get( TYPEKEY );
    if( vecTypeKeys == null )
      vecTypeKeys = new Vector();
    if( !vecTypeKeys.contains( strType ) )
    {
      vecTypeKeys.add( strType );
      m_hm.put( TYPEKEY, vecTypeKeys );
      blRet = true;
    }
    return blRet;
  }


  /**
   *  removes a type and all of its elements
   *
   * @param  strType  type to remove
   */
  public void removeType( String strType )
  {
    Vector vecTypeKeys = (Vector)m_hm.get( TYPEKEY );
    if( vecTypeKeys == null )
      return;
    Vector vecElems = getElementsByType( strType);
    if( vecElems != null)
    {
      for( int i= 0; i < vecElems.size(); ++i)
      {
        PropertyStruct ps = ( PropertyStruct )vecElems.elementAt( i );
        removeElement( strType, ps);
      }
    }
    vecTypeKeys.removeElement( strType );
    m_hm.put( TYPEKEY, vecTypeKeys );
    remove( strType);
  }


  /**
   *  rename a type and move all its elements
   *
   * @param  strOldType  type to rename
   * @param  strNewType  new type name
   */
  public void renameType( String strOldType,  String strNewType)
  {
    Vector vecTypeKeys = (Vector)m_hm.get( TYPEKEY );
    if( vecTypeKeys == null )
      return;
    Vector vecElems = getElementsByType( strOldType);
    if( vecElems != null)
    {
      for( int i= 0; i < vecElems.size(); ++i)
      {
        PropertyStruct ps = ( PropertyStruct )vecElems.elementAt( i );
        removeElement( strOldType, ps);
        addElement( strNewType, ps);
      }
    }
    //removeType( strOldType);
  }


  /**
   *  Adds an element with a given type
   *
   * @param  strType  type of the element
   * @param  pstruct  element
   */
  public void addElement( String strType, PropertyStruct pstruct )
  {
    super.addStructValue( pstruct );
    addType( strType );
    addElementname( strType, pstruct.getStructName() );
  }


  /**
   *  Adds a struct to the PropertyStruct
   *
   * @param  pstruct  struct
   */
  /*
  public void addStructValue( PropertyStruct pstruct )
  {
    System.err.println( "Error: You can't use the function 'addStructValue( PropertyStruct pstruct)' in class PropertyFile," );
    System.err.println( "please use 'addElement( String strType, PropertyStruct pstruct)' instead!" );
  }
  */


  /**
   *  removes an element with a given type
   *
   * @param  strType  type
   * @param  pstruct  element
   */
  public void removeElement( String strType, PropertyStruct pstruct )
  {
    super.removeStructValue( pstruct );
    removeElementname( strType, pstruct.getStructName() );

    Vector vec = getElementnamesByType( strType);
    if( vec != null && vec.size() == 0)
      removeType( strType );
  }


  /**
   *  removes a struct from a PropertyStruct
   *
   * @param  pstruct  struct
   */
  /*
  public void removeStructValue( PropertyStruct pstruct )
  {
    System.err.println( "Error: You can't use the function 'removeStruct( PropertyStruct pstruct)' in class PropertyFile," );
    System.err.println( "please use 'removeElement( String strType, PropertyStruct pstruct)' instead!" );
  }
  */


  /**
   *  Adds an elementname to the PropertyFile
   *
   * @param  strType  type of the element
   * @param  strName  elementname
   * @return          false if allready exists, otherwise true
   */
  public boolean addElementname( String strType, String strName )
  {
    boolean blRet = false;
    Vector vec = (Vector)m_hm.get( strType );
    if( vec == null )
    {
      vec = new Vector();
    }
    if( !vec.contains( strName ) )
    {
      vec.add( strName );
      m_hm.put( strType, vec );
      blRet = true;
    }
    return blRet;
  }


  /**
   *  removes an elementname with a given type
   *
   * @param  strType  type of the element
   * @param  strName  elementname
   */
  public void removeElementname( String strType, String strName )
  {
    Vector vec = (Vector)m_hm.get( strType );
    if( vec == null )
      return;
    vec.removeElement( strName );
    m_hm.put( strType, vec );
  }


  /**
   *  writes PropertyFile to a file
   *
   * @param  pw      PrintWriter
   * @param  iLevel  blocklevel {}
   */
  protected void write( PrintWriter pw, int iLevel )
  {
    // write comment
    if( getComment() != null)
      pw.println( getComment() );

    // get types
    Vector vecTypes = getTypes();

    // type loop
    for( int iTypes = 0; iTypes < vecTypes.size(); ++iTypes )
    {
      String strType = (String)vecTypes.elementAt( iTypes );
      Vector vecElements = getElementnamesByType( strType );

      for( int iElem = 0; iElem < vecElements.size(); ++iElem )
      {
        pw.println();
        pw.print( strType );
        pw.print( " " );
        String strElem = (String)vecElements.elementAt( iElem );
        PropertyStruct pstruct = structValue( strElem );
        pw.print( strElem );
        pstruct.write( pw, iLevel + 1 );
      }
    }
  }


  /**
   *  converts a valuestring to the correct object type (parser)
   *
   * @param  strValue            value as string
   * @param  iString             state of string
   * @return                     value with correct object type
   * @exception  ParseException  if the value is not of a correct type
   */
  private Object getValue( String strValue, int iString )
    throws ParseException
  {
    if( iString > 0 )
    {
      return strValue;
    }
    else
    {
      if( strValue.equals( "true" ) || strValue.equals( "false" ) )
        return new Boolean( strValue );
      else
      {
        try
        {
          long iValue = Long.parseLong( strValue );
          if( !strValue.equals( String.valueOf( iValue ) ) )
            throw new ParseException( "Syntax Error, String value without \"\", in PropertyFile: " + m_fileRead.getFile(), 0 );
          return new Long( iValue );
        }
        catch( NumberFormatException nfex )
        {
          Double fValue = new Double( strValue );
          return fValue;
        }
      }
    }
  }


  /**
   *  Gets the index of last array (parser)
   *
   * @param  vecArrayIdx  vector with all indexes
   * @return              index of last array
   */
  private int getLastArrayIndex( Vector vecArrayIdx )
  {
    int iIdx = -1;

    if( !vecArrayIdx.isEmpty() )
    {
      Integer iObj = (Integer)vecArrayIdx.lastElement();
      iIdx = iObj.intValue();
    }
    return iIdx;
  }


  /**
   *  parse the top level of the PropertyFile
   *
   * @param  br                  reader to red the file
   * @exception  ParseException  on syntax errors
   * @exception  IOException     on reader access errors
   */
  private void parseTopLevel( BufferedReader br, JProgressBar pb)
    throws ParseException, IOException
  {
    //int iLen = 0;
    int iIdx;
    int iBracketCount = 0;
    String strLine;
    String strEffLine;
    String strType = "";
    String strName = "";
    String strBody = "";
    char c = '\n';
    //char cPrev = '\n';
    char cNext = '\n';
    int iParseState = TYPE;
    boolean blStart = true;
    int iFileLen = 0;

    while( ( strLine = br.readLine() ) != null )
    {
      if( pb != null)
      {
        iFileLen += strLine.getBytes().length;
        pb.setValue( iFileLen );
      }
      if( strBody.length() > 0 )
        strBody += '\n';
      iIdx = strLine.indexOf( "//" );
      if( iIdx == 0 )
      {
        if( blStart )
        {
          if( m_strComment == null )
            m_strComment = strLine + "\n";
          else
            m_strComment += strLine + "\n";
        }
        continue;
      }
      blStart = false;
      if( iIdx == -1 )
        strEffLine = strLine.trim();
      else
        strEffLine = strLine.substring( 0, iIdx - 1 ).trim();

      int iStrLen = strEffLine.length();
      if( iStrLen > 0 )
        cNext = strEffLine.charAt( 0 );
      for( int i = 0; i < iStrLen; ++i )
      {
        //cPrev = c;
        c = cNext;
        if( i < iStrLen - 1 )
          cNext = strEffLine.charAt( i + 1 );
        else
          cNext = '\n';

        switch ( c )
        {
            case ' ':
            case '\t':
              switch ( iParseState )
              {
                  case BODY:
                    strBody += c;
                    break;
              }
              break;
            case '{':
              ++iBracketCount;
              strBody += c;
              break;
            case '}':
              --iBracketCount;
              strBody += c;
              if( iBracketCount == 0 )
              {
                parseBody( strName, strBody );
                if( !addElementname( strType, strName ) )
                  throw new ParseException( "Error, double definition of element, in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
                addType( strType );
                iParseState = TYPE;
                strType = "";
                strName = "";
                strBody = "";
              }
              break;
            default:
              switch ( iParseState )
              {
                  case TYPE:
                    strType += c;
                    switch ( cNext )
                    {
                        case '\n':
                        case ' ':
                        case '\t':
                          iParseState = NAME;
                          break;
                    }
                    break;
                  case NAME:
                    strName += c;
                    switch ( cNext )
                    {
                        case '\n':
                        case ' ':
                        case '\t':
                          iParseState = BODY;
                          break;
                    }
                    break;
                  case BODY:
                    strBody += c;
                    break;
              }
              break;
        }
      }
    }
  }


  /**
   *  parse body of an element
   *
   * @param  strName             element name
   * @param  strBody             body string
   * @exception  ParseException  on syntax errors
   * @exception  IOException     on reader access errors
   */
  private void parseBody( String strName, String strBody )
    throws ParseException, IOException
  {
    //int iLen = 0;
    int iIdx;
    int iBracketCount = 0;
    int iString = 0;
    Vector vecArrayIdx = new Vector();
    String strLine;
    String strEffLine;
    String strKey = "";
    Vector vecKey = new Vector();
    vecKey.add( strName );
    String strValue = "";
    char c = '\n';
    //char cPrev = '\n';
    char cNext = '\n';
    Vector vecParseState = new Vector();
    vecParseState.add( new Integer( KEY ) );
    strBody = strBody.substring( 1, strBody.length() - 1 );
    BufferedReader br = new BufferedReader( new StringReader( strBody ) );

    while( ( strLine = br.readLine() ) != null )
    {
      iIdx = strLine.indexOf( "//" );
      if( iIdx == 0 )
        continue;
      if( iIdx == -1 )
        strEffLine = strLine.trim();
      else
        strEffLine = strLine.substring( 0, iIdx - 1 ).trim();
      if( iString == 1 && cNext == '\n')
      {
        strValue += cNext;
        c = cNext;
      }

      int iStrLen = strEffLine.length();

      switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
      {
        case NORMALVALUE:
        case ARRAYVALUE:
          if( iString == 1)
          {
            iIdx = strEffLine.indexOf( '"');
            if( iIdx == -1)
            {
              strValue += strEffLine;
              continue;
            }
            else if( iIdx > 0)
            {
              String str = strEffLine.substring( 0, iIdx);
              strValue += str;
              strEffLine = strEffLine.substring( iIdx);
              iStrLen = strEffLine.length();
            }
          }
          break;
      }

      if( iStrLen > 0 )
        cNext = strEffLine.charAt( 0 );
      for( int i = 0; i < iStrLen; ++i )
      {
        //cPrev = c;
        c = cNext;
        if( i < iStrLen - 1 )
          cNext = strEffLine.charAt( i + 1 );
        else
          cNext = '\n';

        switch ( c )
        {
            case ' ':
            case '\t':
              switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
              {
                  case NORMALVALUE:
                  case ARRAYVALUE:
                    if( iString == 1 )
                      strValue += c;
                    break;
              }
              break;
            case '"':
              switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
              {
                  case NORMALVALUE:
                  case ARRAYVALUE:
                    if( iString >= 0 )
                      ++iString;
                    break;
                  default:
                    throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
              }
              break;
            case ',':
              switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
              {
                  case NORMALVALUE:
                    if( iString == 1 )
                      strValue += c;
                    else
                      throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
                    break;
                  case ARRAYVALUE:
                    if( iString == 1 )
                      strValue += c;
                    else
                    {
                      if( strValue.length() > 0 )
                      {
                        m_hm.put( makeKey( vecKey ), getValue( strValue, iString ) );
                        strValue = "";
                        iString = 0;
                      }
                      int iArrayIdx = getLastArrayIndex( vecArrayIdx );
                      if( iArrayIdx > -1 )
                      {
                        vecArrayIdx.removeElementAt( vecArrayIdx.size() - 1 );
                        vecKey.removeElementAt( vecKey.size() - 1 );
                      }
                      ++iArrayIdx;
                      vecArrayIdx.add( new Integer( iArrayIdx ) );
                      vecKey.add( "[" + Integer.toString( iArrayIdx ) + "]" );
                      //vecParseState.removeElementAt( vecParseState.size()-1);
                      //vecKey.removeElementAt( vecKey.size()-1);
                    }
                    break;
                  default:
                    throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
              }
              break;
            case ';':
              switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
              {
                  case NORMALVALUE:
                    if( iString == 1 )
                      strValue += c;
                    else
                    {
                      if( strValue.length() > 0 )
                      {
                        m_hm.put( makeKey( vecKey ), getValue( strValue, iString ) );
                        strValue = "";
                        iString = 0;
                      }
                      vecParseState.removeElementAt( vecParseState.size() - 1 );
                      vecKey.removeElementAt( vecKey.size() - 1 );
                    }
                    break;
                  case ARRAYVALUE:
                    if( iString == 1 )
                      strValue += c;
                    else
                    {
                      throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
                    }
                    break;
                  default:
                    throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
              }
              break;
            case '=':
              switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
              {
                  case KEY:
                    vecKey.add( strKey );
                    strKey = "";
                    iString = 0;
                    vecParseState.add( new Integer( NORMALVALUE ) );
                    break;
                  case NORMALVALUE:
                  case ARRAYVALUE:
                    if( iString == 1 )
                      strValue += c;
                    else
                      throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
                    break;
                  default:
                    throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
              }
              break;
            case '{':
              ++iBracketCount;
              switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
              {
                  case KEY:
                    vecKey.add( strKey );
                    strKey = "";
                    iString = 0;
                    vecParseState.add( new Integer( STRUCT ) );
                    vecParseState.add( new Integer( KEY ) );
                    break;
                  case NORMALVALUE:
                    if( iString == 1 )
                      strValue += c;
                    else
                    {
                      vecParseState.add( new Integer( ARRAYVALUE ) );
                      int iArrayIdx = 0;
                      vecArrayIdx.add( new Integer( iArrayIdx ) );
                      vecKey.add( "[" + Integer.toString( iArrayIdx ) + "]" );
                    }
                    break;
                  case ARRAYVALUE:
                    if( iString == 1 )
                      strValue += c;
                    else
                    {
                      vecParseState.add( new Integer( STRUCT ) );
                      vecParseState.add( new Integer( KEY ) );
                    }
                    break;
                  default:
                    throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
              }
              break;
            case '}':
              --iBracketCount;
              switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
              {
                  case NORMALVALUE:
                    if( iString == 1 )
                      strValue += c;
                    break;
                  case ARRAYVALUE:
                    if( iString == 1 )
                      strValue += c;
                    else
                    {
                      int iArrayIdx = getLastArrayIndex( vecArrayIdx );
                      if( strValue.length() > 0 )
                      {
                        m_hm.put( makeKey( vecKey ), getValue( strValue, iString ) );
                        strValue = "";
                        iString = 0;
                      }
                      vecParseState.removeElementAt( vecParseState.size() - 1 );
                      if( iArrayIdx > -1 )
                      {
                        vecArrayIdx.removeElementAt( vecArrayIdx.size() - 1 );
                        vecKey.removeElementAt( vecKey.size() - 1 );
                      }
                    }
                    break;
                  case KEY:
                    // remove KEY
                    vecParseState.removeElementAt( vecParseState.size() - 1 );
                    // remove STRUCT
                    vecParseState.removeElementAt( vecParseState.size() - 1 );
                    break;
                  default:
                    throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
              }
              break;
            default:
              switch ( ( (Integer)vecParseState.lastElement() ).intValue() )
              {
                  case KEY:
                    strKey += c;
                    break;
                  case NORMALVALUE:
                  case ARRAYVALUE:
                    if( iString == 2 )
                      throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
                    strValue += c;
                    break;
                  default:
                    throw new ParseException( "Syntax Error in PropertyFile: " + m_fileRead.getFile() + "; In or near text line: " + strLine, 0 );
              }
              break;
        }
      }
    }
    if( iBracketCount != 0 )
      throw new ParseException( "Syntax Error, wrong bracket count, in PropertyFile: " + m_fileRead.getFile(), 0 );
  }


  /**
   *  generate an string representation of a key
   *
   * @param  vecKey  vector with the key parts
   * @return         string representation of the key
   */
  private String makeKey( Vector vecKey )
  {
    String strKey = "";
    String str = "";
    for( int i = 0; i < vecKey.size(); ++i )
    {
      str = (String)vecKey.elementAt( i );
      if( i > 0 && str.indexOf( '[' ) == -1 )
        strKey += ".";
      strKey += str;
    }
    return strKey;
  }
}
