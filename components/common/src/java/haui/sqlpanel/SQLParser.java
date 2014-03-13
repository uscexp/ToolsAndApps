package haui.sqlpanel;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Description of the Class
 * @author      t026843
 * @created     12. Februar 2003
 */
public class SQLParser
  extends Object
  implements DocumentListener
{
  /**
   *  Description of the Field
   */
  public SQLDocument doc;

  JTextPane text;
  Options options;

  StyleContext sqlStyle;
  DocumentListener dl;
  ParseThread parseThread;
  boolean parse = false;
  final int delayTime = 300;
  int startChange = 100000;
  int endChange = -1;

  static char[] Operator = {'*', '-', '+', '"', '.', '=', '!', '<', '>', '(', ')', '[', ']', '{', '}'};

  String[] Datatype = {"BYTE",
      "CHAR",
      "CHARACTER",
      "DATE",
      "TIME",
      "DATETIME",
      "DECIMAL",
      "DEC",
      "NUMERIC",
      "FLOAT",
      "DOUBLE",
      "INTEGER",
      "INT",
      "DECIMAL",
      "INTERVAL",
      "MONEY",
      "SERIAL",
      "SMALLFLOAT",
      "REAL",
      "SMALLINT",
      "VARCHAR",
      "NUMBER",
      "TIMESTAMP",
      "LONG",
      "NUM",
      "BIT",
      "PRECISION"};

  String[] Function = {"UPPER",
      "LOWER",
      "SUM",
      "INITCAP",
      "INSTR",
      "LEAST",
      "LENGTH",
      "LPAD",
      "LTRIM",
      "MAX",
      "MIN",
      "MONTH",
      "ROUND",
      "RPAD",
      "RTRIM",
      "SYSDATE",
      "SUBSTR",
      "SOUNDEX",
      "TO_CHAR",
      "TO_DATE",
      "TO_NUMBER",
      "TRANSLATE",
      "WEEKDAY",
      "ADD_MONTH",
      "AVG",
      "CHR",
      "COUNT",
      "DATE",
      "DAYS",
      "DAY",
      "GREATEST"};

  static String[] KeyWords = {"SELECT",
      "SET",
      "OR",
      "AND",
      "WHERE",
      "FROM",
      "INSERT",
      "UPDATE",
      "ORDER",
      "BY",
      "CLOSE",
      "COMMIT",
      "ROLLBACK",
      "CREATE",
      "DATABASE",
      "DROP",
      "TABLE",
      "VIEW",
      "JOIN",
      "INTO",
      "BETWEEN",
      "VALUES",
      "ALTER",
      "ADD",
      "MODIFY",
      "CLOSE",
      "DISTINCT",
      "INDEX",
      "GRANT",
      "DELETE",
      "GROUP",
      "AS",
      "HAVING",
      "DELETE",
      "LIKE",
      "UNIQUE",
      "PRIMARY",
      "KEY",
      "NOT",
      "NULL",
      "ASC",
      "DESC",
      "REVOKE",
      "REMARK",
      "EXISTS",
      "UNION",
      "IS"};


  /**
   *Constructor for the SQLParser object
   *
   * @param  text     Description of Parameter
   * @param  options  Description of Parameter
   */
  public SQLParser( JTextPane text, Options options )
  {
    super();

    this.text = text;
    this.options = options;

    makeStyleContext();

    doc = new SQLDocument( sqlStyle, this );

    doc.addDocumentListener( this );

    setParse( true );
  }

  static public String[] getKeywords()
  {
    return KeyWords;
  }


  static public char[] getOperators()
  {
    return Operator;
  }


// set the new string in text
  /**
   *  Sets the SQLText attribute of the SQLParser object
   *
   * @param  text  The new SQLText value
   */
  public void setSQLText( String text )
  {
    setParse( false );
    try
    {
      doc.remove( 0, doc.getLength() );
      doc.insertString( 0, text, sqlStyle.getStyle( "DEFAULT" ) );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    startChange = 100000;
    endChange = -1;

    setParse( true );
  }


  /**
   * Sets the Parse attribute of the SQLParser object
   * @param parse   The new Parse value
   * @uml.property  name="parse"
   */
  public void setParse( boolean parse )
  {
    this.parse = parse;
  }


  /**
   *  Description of the Method
   */
  public void makeStyleContext()
  {
    sqlStyle = new StyleContext();

    // (name, size, font, color, italic, undeline, bold //
    /*
        addTextStyle(new SQLParserFont("DEFAULT",
                     14, "Courier", Color.black, false, false, false));
        addTextStyle(new SQLParserFont("KEYWORD",
                     14, "Courier", Color.blue, false, false, false));
        addTextStyle(new SQLParserFont("FUNCTION",
                     14, "Courier", Color.blue, true, false, false));
        addTextStyle(new SQLParserFont("OPERATOR",
                     14, "Courier", new Color(0,128,0), false, false, false));
        addTextStyle(new SQLParserFont("DATATYPE",
                     14, "Courier", new Color(0, 128, 0), false, true, false));
        addTextStyle(new SQLParserFont("VALUE",
                     14, "Courier", Color.magenta, false, true, true));
        addTextStyle(new SQLParserFont("STRING",
                     14, "Courier", Color.magenta, true, false, false));
*/
    addTextStyle( (SQLParserFont)options.get( "PARSER", "DEFAULT" ) );
    addTextStyle( (SQLParserFont)options.get( "PARSER", "KEYWORD" ) );
    addTextStyle( (SQLParserFont)options.get( "PARSER", "FUNCTION" ) );
    addTextStyle( (SQLParserFont)options.get( "PARSER", "OPERATOR" ) );
    addTextStyle( (SQLParserFont)options.get( "PARSER", "DATATYPE" ) );
    addTextStyle( (SQLParserFont)options.get( "PARSER", "VALUE" ) );
    addTextStyle( (SQLParserFont)options.get( "PARSER", "STRING" ) );
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  public void insertUpdate( DocumentEvent e )
  {
    initializeParse( e );
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  public void removeUpdate( DocumentEvent e )
  {
    initializeParse( e );
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  public void changedUpdate( DocumentEvent e )
  {
//        initializeParse(e);
  }


// set the time of the parser thread new
  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  public void initializeParse( DocumentEvent e )
  {
    if( startChange > e.getOffset() )
    {
      startChange = e.getOffset();
    }
    if( endChange < ( startChange + e.getLength() ) )
    {
      endChange = startChange + e.getLength();
    }
    try
    {
      if( ( parseThread != null ) && ( parseThread.isAlive() ) )
      {
        parseThread.time = System.currentTimeMillis() + delayTime;
      }
      else
      {
        parseThread = new ParseThread( e.getDocument() );
        parseThread.time = System.currentTimeMillis() + delayTime;
        parseThread.start();
      }
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      //System.out.println( "error while initialize Parse" );
      //System.out.println( ex.getMessage() );
    }
  }


// set the time of the parser thread new
  /**
   *  Description of the Method
   */
  public void initializeParse()
  {
    startChange = 0;
    endChange = doc.getLength() - 1;
    try
    {
      if( ( parseThread != null ) && ( parseThread.isAlive() ) )
      {
        parseThread.time = System.currentTimeMillis() + delayTime;
      }
      else
      {
        parseThread = new ParseThread( doc );
        parseThread.time = System.currentTimeMillis() + delayTime;
        parseThread.start();
      }
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      //System.out.println( "error while initialize Parse" );
      //System.out.println( ex.getMessage() );
    }
  }


// main methode, parse the text an set the style attributes
  /**
   *  Description of the Method
   *
   * @param  doc  Description of Parameter
   */
  private synchronized void parseText( SQLDocument doc )
  {
//        SQLDocument newDoc = new SQLDocument(doc.getTheContent(), sqlStyle, this);
    try
    {
      int length = doc.getLength();
      String text = doc.getText( 0, length );
      int pos = 0;
      int start = 0;
      int end = length - 1;

      // find start of line
      if( ( startChange < length ) && ( startChange > 0 ) )
      {
        for( start = startChange; ( ( start > 0 ) && ( text.charAt( start ) != '\n' ) ); start-- )
          ;
//                start++;
      }
      // find end of line
      if( ( endChange > -1 ) && ( endChange < end ) )
      {
        for( end = endChange; ( ( end < length - 1 ) && ( text.charAt( end ) != '\n' ) ); end++ )
          ;
//                end--;
      }

      if( /* ( start == end ) ||*/ ( start > end ) )
      {
        //startChange = 100000;
        //endChange = -1;
        //setParse( false );
        return;
      }
//            doc.lock();

      text = text.toUpperCase();

      doc.setCharacterAttributes( start, end - start + 1,
          sqlStyle.getStyle( "DEFAULT" ),
          true );

//\\//\\//\\//\\//\\ * * * Test Operators * * * //\\//\\//\\//\\//\\
      for( int i = start; i <= end; i++ )
      {
        for( int t = 0; t < Operator.length; t++ )
        {
          if( text.charAt( i ) == Operator[t] )
          {
            doc.setCharacterAttributes( i,
                1,
                sqlStyle.getStyle( "OPERATOR" ),
                true );
            break;
          }
        }
      }
//\\//\\//\\//\\//\\ * * * Test Values * * * //\\//\\//\\//\\//\\
      for( int i = start; i <= end; i++ )
      {
        if( Character.isDigit( text.charAt( i ) ) )
        {
          if( ( ( i == 0 ) || ( !Character.isLetter( text.charAt( i - 1 ) ) ) || ( text.charAt( i - 1 ) == '\n' ) ) &&
              ( ( i == length-1 ) || ( !Character.isLetter( text.charAt( i + 1 ) ) ) || ( text.charAt( i + 1 ) == '\n' ) ) )
          {
            doc.setCharacterAttributes( i,
                1,
                sqlStyle.getStyle( "VALUE" ),
                true );
          }
        }
      }
//\\//\\//\\//\\//\\ * * * Test Keywords * * * //\\//\\//\\//\\//\\
      pos = 0;
      for( int i = 0; i < KeyWords.length; i++ )
      {
        pos = text.indexOf( KeyWords[i], start );
        int p1;
        int p2;
        while( ( pos > -1 ) && ( pos < end ) )
        {
          p1 = pos - 1;
          p2 = pos + KeyWords[i].length();
          if( ( ( pos == 0 ) || ( !Character.isLetterOrDigit( text.charAt( p1 ) ) ) ) &&
              ( ( ( p2 ) == text.length() ) || ( !Character.isLetterOrDigit( text.charAt( p2 ) ) ) ) )
          {

            doc.setCharacterAttributes( pos,
                KeyWords[i].length(),
                sqlStyle.getStyle( "KEYWORD" ),
                true );
          }
          // if
          pos = text.indexOf( KeyWords[i], pos + 1 );
        }
        // while
      }
      // for
//\\//\\//\\//\\//\\ * * * Test Datatypes * * * //\\//\\//\\//\\//\\
      pos = 0;
      for( int i = 0; i < Datatype.length; i++ )
      {
        pos = text.indexOf( Datatype[i], start );
        int p1;
        int p2;
        while( ( pos > -1 ) && ( pos < end ) )
        {
          p1 = pos - 1;
          p2 = pos + Datatype[i].length();
          if( ( ( pos == 0 ) || ( !Character.isLetterOrDigit( text.charAt( p1 ) ) ) ) &&
              ( ( ( p2 ) == text.length() ) || ( !Character.isLetterOrDigit( text.charAt( p2 ) ) ) ) )
          {
            int offset = 0;
            int t1 = text.indexOf( "(", p2 );
            int t2 = text.indexOf( ")", p2 + 1 );
            int t3 = text.indexOf( "(", p2 + 1 );
            if( ( ( t1 > -1 ) && ( t2 > -1 ) ) && ( ( t2 < t3 ) || ( t3 < 0 ) ) )
            {
              offset = t2 - p2 + 1;
            }

            doc.setCharacterAttributes( pos,
                Datatype[i].length() + offset,
                sqlStyle.getStyle( "DATATYPE" ),
                true );
          }
          // if
          pos = text.indexOf( Datatype[i], pos + 1 );
        }
        // while
      }
      // for
//\\//\\//\\//\\//\\ * * * Test Functions * * * //\\//\\//\\//\\//\\
      pos = 0;
      for( int i = 0; i < Function.length; i++ )
      {
        pos = text.indexOf( Function[i], start );
        int p1;
        int p2;
        while( ( pos > -1 ) && ( pos < end ) )
        {
          p1 = pos - 1;
          p2 = pos + Function[i].length();
          if( ( ( pos == 0 ) || ( !Character.isLetterOrDigit( text.charAt( p1 ) ) ) ) &&
              ( ( ( p2 ) == text.length() ) || ( !Character.isLetterOrDigit( text.charAt( p2 ) ) ) ) )
          {
            int offset = 0;
            int t1 = text.indexOf( "(", p2 );
            int t2 = text.indexOf( ")", p2 + 1 );
            int t3 = text.indexOf( "(", p2 + 1 );
            if( ( t1 > -1 ) && ( t2 > -1 ) && ( ( t2 < t3 ) || ( t3 < 0 ) ) )
            {
              offset = t2 - p2 + 1;
            }

            doc.setCharacterAttributes( pos,
                Function[i].length() + offset,
                sqlStyle.getStyle( "FUNCTION" ),
                true );
          }
          // if
          pos = text.indexOf( Function[i], pos + 1 );
        }
        // while
      }
      // for

//\\//\\//\\//\\//\\ * * * Test Strings * * * //\\//\\//\\//\\//\\
      pos = start;
      int pos2 = 0;
      while( ( ( pos = text.indexOf( "'", pos ) ) > -1 ) && ( pos <= end ) )
      {
        if( ( ( pos2 = text.indexOf( "'", pos + 1 ) ) > -1 ) && ( pos2 <= end ) )
        {
          doc.setCharacterAttributes( pos,
              pos2 - pos + 1,
              sqlStyle.getStyle( "STRING" ),
              true );
          pos = pos2 + 1;
        }
        else
        {
          break;
        }
      }

      doc.setLogicalStyle( text.length(), sqlStyle.getStyle( "DEFAULT" ) );

//            doc.unlock();
//        newDoc.addDocumentListener(dl);
//        this.doc = newDoc;
//        owner.sqlText.setDocument(this.doc);

    }
    catch( BadLocationException ble )
    {
      ble.printStackTrace();
      //System.out.println( "<<<<<<<< Error while parsing text >>>>>>>>>" );
      //System.out.println( "Location: " + ble.offsetRequested() );
    }
    catch( Exception e )
    {
      e.printStackTrace();
      //System.out.println( "<<<<<<<< Error while parsing text >>>>>>>>>" );
      //System.out.println( e.getMessage() );
    }
    startChange = 100000;
    endChange = -1;
  }


  /**
   *  Adds a feature to the TextStyle attribute of the SQLParser object
   *
   * @param  font  The feature to be added to the TextStyle attribute
   */
  private void addTextStyle( SQLParserFont font )
  {

    Style style = sqlStyle.addStyle( font.name, null );

    StyleConstants.setFontSize( style, font.size );
    StyleConstants.setForeground( style, (Color)options.get( "COLORS", font.color ) );
    StyleConstants.setItalic( style, font.italic );
    StyleConstants.setUnderline( style, font.underline );
    StyleConstants.setBold( style, font.bold );
  }


  /**
   * @author     t026843
   * @created    12. Februar 2003
   */
// inner class ParserThead, reason: while DocumentEvent
// the Document is write protected
  public class ParseThread extends Thread
  {
    /**
     *  Description of the Field
     */
    public long time = 0;

    Document myDoc;


    /**
     *Constructor for the ParseThread object
     *
     * @param  doc  Description of Parameter
     */
    public ParseThread( Document doc )
    {
      super();
      myDoc = doc;
    }


    /**
     *  Main processing method for the ParseThread object
     */
    public void run()
    {
      try
      {
        while( true )
        {
          while( time > System.currentTimeMillis() )
          {
            sleep( 100 );
          }
          if( ( parse ) && ( time != 0 ) )
          {
            parseText( (SQLDocument)myDoc );
//                        doc.render(new RenderThread());
            time = 0;
          }
          sleep( 200 );
        }
      }
      catch( Exception e )
      {
        e.printStackTrace();
        //System.out.println( "Error in ParseThread" );
        //System.out.println( e.getMessage() );
      }
    }
  }


  /**
   * @author     t026843
   * @created    12. Februar 2003
   */

  public class RenderThread extends Thread
  {
    /**
     *  Main processing method for the RenderThread object
     */
    public void run()
    {
      parseText( doc );
    }
  }

}
