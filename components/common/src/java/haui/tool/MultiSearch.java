
package haui.tool;

import haui.util.GlobalApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 *		Module:					MultiSearch.java
 *<p>
 *		Description:		Search functionality.
 *</p><p>
 *		Created:				13.09.2000	by	AE
 *</p><p>
 *		Last Modified:	14.09.2000	by	AE
 *</p><p>
 *		history					13.09.2000 - 14.09.2000	by	AE:	Create MultiSearch basic functionality<br>
 *										22.09.2000	by	AE:	Bugfix in checkInFile(...)<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class MultiSearch
{
  Vector m_strInc = new Vector();
  Vector m_strExc = new Vector();
  String m_strAppName;

  public MultiSearch( String strAppName)
  {
    constructor( strAppName, null);
  }

  public MultiSearch( String strAppName, String strSearchTerm)
  {
    constructor( strAppName, strSearchTerm);
  }

  private void constructor( String strAppName, String strSearchTerm)
  {
    m_strAppName = strAppName;
    setSearchTerm( strSearchTerm);
  }

  /**
    * parse the input to include and exclude list
    *
    * @param strSearchTerm: string which have the words to include(+) or exclude(-)
    */
  private void parse( String strSearchTerm)
  {
    StringTokenizer sto = new StringTokenizer( strSearchTerm);

    while( sto.hasMoreTokens())
    {
      String strTmp = sto.nextToken();

      if( strTmp.indexOf( '+') == 0)
      {
        m_strInc.add( strTmp.substring( 1));
      }
      else
      {
        if( strTmp.indexOf( '-') == 0)
        {
          m_strExc.add( strTmp.substring( 1));
        }
        else
        {
          m_strInc.add( strTmp);
        }
      }
    }
  }

  public void setSearchTerm( String strSearchTerm)
  {
    if( strSearchTerm == null)
      return;
    parse( strSearchTerm);
  }

  /**
    * get list of include words
    *
    * @return array of include words
    */
  public String[] getIncList()
  {
    return (String[])m_strInc.toArray();
  }

  /**
    * get list of exclude words
    *
    * @return array of exclude words
    */
  public String[] getExcList()
  {
    return (String[])m_strExc.toArray();
  }

  /**
    * check the input string for include and exclude words
    *
    * @param strToCheck: string to check
    *
    * @return true if the input string contents all the words from the include list and not from the exclude list
    */
  public boolean check( String strToCheck)
  {
    // check for exclude list
    for( int i = 0; i < m_strExc.size(); i++)
    {
      if( check( strToCheck, (String)m_strExc.elementAt( i)))
      {
        return false;
      }
    }

    for( int i = 0; i < m_strInc.size(); i++)
    {
      if( !check( strToCheck, (String)m_strInc.elementAt( i)))
      {
        return false;
      }
    }
    return true;
  }

  /**
    * check the input string for include and exclude words ignoring case
    *
    * @param strToCheck: string to check
    *
    * @return true if the input string contents all the words from the include list and not from the exclude list
    */
  public boolean checkIgnoreCase( String strToCheck)
  {
    // check for exclude list
    for( int i = 0; i < m_strExc.size(); i++)
    {
      if( checkIgnoreCase( strToCheck, (String)m_strExc.elementAt( i)))
      {
        return false;
      }
    }

    for( int i = 0; i < m_strInc.size(); i++)
    {
      if( !checkIgnoreCase( strToCheck, (String)m_strInc.elementAt( i)))
      {
        return false;
      }
    }
    return true;
  }

  /**
    * check the input string for the word
    *
    * @param strToCheck: string to check
    * @param strWord: word to check with
    *
    * @return true if the input string the word
    */
  static public boolean check( String strToCheck, String strWord)
  {
    if( strToCheck.indexOf( strWord) != -1)
    {
      return true;
    }
    return false;
  }

  /**
    * check the input string for the word ignoring case
    *
    * @param strToCheck: string to check
    * @param strWord: word to check with
    *
    * @return true if the input string the word
    */
  static public boolean checkIgnoreCase( String strToCheck, String strWord)
  {
    if( strToCheck.toUpperCase().indexOf( strWord.toUpperCase()) != -1)
    {
      return true;
    }
    return false;
  }

  /**
    * check the file for include and exclude words
    *
    * @param strFile: file to check
    *
    * @return true if the file contents all the words from the include list and not from the exclude list
    */
  public boolean checkInFile( String strFile)
  {
    return checkInFile( strFile, false);
  }

  /**
    * check the file for include and exclude words ignoring case
    *
    * @param strFile: file to check
    *
    * @return true if the file contents all the words from the include list and not from the exclude list
    */
  public boolean checkInFileIgnoreCase( String strFile)
  {
    return checkInFile( strFile, true);
  }

  private boolean checkInFile( String strFile, boolean blIgnoreCase)
  {
    BufferedReader br;
    String strIn;
    Vector strInc = new Vector( m_strInc);
    Vector strExc = new Vector( m_strExc);

    try
    {
      br = new BufferedReader( new FileReader( strFile));

      while((strIn = br.readLine()) != null)
      {
        for( int i = 0; i < strExc.size(); i++)
        {
          boolean blRet = false;
          if( blIgnoreCase)
            blRet = checkIgnoreCase( strIn, (String)strExc.elementAt( i));
          else
            blRet = check( strIn, (String)strExc.elementAt( i));
          if( blRet)
          {
            br.close();
            return false;
          }
        }

        for( int i = strInc.size()-1; i >= 0; i--)
        {
          boolean blRet = false;
          if( blIgnoreCase)
            blRet = checkIgnoreCase( strIn, (String)strInc.elementAt( i));
          else
            blRet = check( strIn, (String)strInc.elementAt( i));
          if( blRet)
          {
            strInc.removeElementAt( i);
          }
        }
      }
      br.close();
      if( strInc.size() > 0)
        return false;
      else
        return true;
    }
    catch(FileNotFoundException e)
    {
      GlobalApplicationContext.instance().getOutputPrintStream().println( "File not found!");
      //System.out.println("File not found!");
      e.printStackTrace();
    }
    catch(IOException e)
    {
      GlobalApplicationContext.instance().getOutputPrintStream().println( "File read error!");
      //System.out.println("File read error!");
      e.printStackTrace();
    }
    return false;
  }

  /**
    * check all files of the directory for include and exclude words
    *
    * @param strDir: directory to check
    *
    * @return vector of all filenames which contents all the words from the include list and not from the exclude list
    */
  public Vector checkInDir( String strDir)
  {
    return checkInDir( strDir, false);
  }

  /**
    * check all files of the directory for include and exclude words ignoring case
    *
    * @param strDir: directory to check
    *
    * @return vector of all filenames which contents all the words from the include list and not from the exclude list
    */
  public Vector checkInDirIgnoreCase( String strDir)
  {
    return checkInDir( strDir, true);
  }

  private Vector checkInDir( String strDir, boolean blIgnoreCase)
  {
    Vector strResult = new Vector();
    File dir = new File( strDir);
    File files[] = dir.listFiles();

    for( int i = 0; i < Array.getLength( files); i++)
    {
      if( files[i].isFile())
      {
        String strName = files[i].getAbsolutePath();
        if( checkInFile( strName, blIgnoreCase))
        {
          strResult.add( strName);
        }
      }
    }
    return strResult;
  }

  /**
    * check all files of the directory and subdirectories for include and exclude words
    *
    * @param strDir: starting directory to check
    *
    * @return vector of all filenames which contents all the words from the include list and not from the exclude list
    */
  public Vector checkInDirAndSubDir( String strDir)
  {
    return checkInDirAndSubDir( strDir, false);
  }

  /**
    * check all files of the directory and subdirectories for include and exclude words ignoring case
    *
    * @param strDir: starting directory to check
    *
    * @return vector of all filenames which contents all the words from the include list and not from the exclude list
    */
  public Vector checkInDirAndSubDirIgnoreCase( String strDir)
  {
    return checkInDirAndSubDir( strDir, true);
  }

  private Vector checkInDirAndSubDir( String strDir, boolean blIgnoreCase)
  {
    Vector strResult = new Vector();
    Vector strTmp;
    File dir = new File( strDir);
    File files[] = dir.listFiles();

    for( int i = 0; i < Array.getLength( files); i++)
    {
      String strName = files[i].getAbsolutePath();
      if( files[i].isDirectory())
      {
        strTmp = checkInDirAndSubDir( strName, blIgnoreCase);
        if( strTmp != null && strTmp.size() > 0)
          strResult.addAll( strTmp);
      }

      if( files[i].isFile())
      {
        if( checkInFile( strName, blIgnoreCase))
        {
          strResult.add( strName);
        }
      }
    }
    return strResult;
  }
}