package haui.io.FileInterface.filter;

import haui.io.FileInterface.FileInterface;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Module:      WildcardFileInterfaceFilter.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\WildcardFileInterfaceFilter.java,v $
 *<p>
 * Description: Wildcard file filter.<br>
 *</p><p>
 * Created:     25.03.2004 by AE
 *</p><p>
 * Modification:<br>
 * $Log: WildcardFileInterfaceFilter.java,v $
 * Revision 1.1  2004-08-31 16:03:21+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:58+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\WildcardFileInterfaceFilter.java,v 1.1 2004-08-31 16:03:21+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class WildcardFileInterfaceFilter
  implements FileInterfaceFilter
{
  // constants
  public static final char WILDCARDANYCHAR = '*';
  public static final char WILDCARDONECHAR = '?';
  protected static final short WCNONE = 0;
  protected static final short WCANY = 1;
  protected static final short WCONE = 2;

  // member variables
  protected String m_strWildcardText;
  protected String m_strAppName;

  public WildcardFileInterfaceFilter( String strWildcardText, String strAppName)
  {
    m_strWildcardText = strWildcardText;
    m_strAppName = strAppName;
  }

  public boolean accept(FileInterface fi)
  {
    boolean blRet = false;
    StringTokenizer stAny = new StringTokenizer( m_strWildcardText, String.valueOf( WILDCARDANYCHAR), false);
    boolean blStartsWithAnyWc = m_strWildcardText.startsWith( String.valueOf( WILDCARDANYCHAR));
    boolean blStartsWithOneWc = m_strWildcardText.startsWith( String.valueOf( WILDCARDONECHAR));
    boolean blEndsWithAnyWc = m_strWildcardText.endsWith( String.valueOf( WILDCARDANYCHAR));
    boolean blEndsWithOneWc = m_strWildcardText.endsWith( String.valueOf( WILDCARDONECHAR));
    short sLastWc = WCNONE;
    int iIdx = -1;
    int i = 0;

    do
    {
      String strPart = "";
      if( stAny.hasMoreTokens())
        strPart = stAny.nextToken();
      else
        strPart = fi.getName();

      StringTokenizer stOne = new StringTokenizer( strPart, String.valueOf( WILDCARDONECHAR), false);
      int iIdxNew = -1;
      int ii = 0;

      if( stOne.countTokens() > 1)
      {
        while( stOne.hasMoreTokens())
        {
          String strPartOne = stOne.nextToken();
          iIdxNew = fi.getName().indexOf( strPartOne );
          if( i == 0 && ii == 0 && !blStartsWithAnyWc)
          {
            if( (!blStartsWithOneWc && iIdxNew != 0) || (blStartsWithOneWc && iIdxNew != 1))
            {
              blRet = false;
              break;
            }
            else
              blRet = true;
          }
          if( (sLastWc == WCANY && iIdxNew > iIdx) || (sLastWc == WCONE && iIdxNew == iIdx+2))
            blRet = true;
          else if( i > 0)
          {
            blRet = false;
            break;
          }
          if( !stOne.hasMoreTokens() && !stAny.hasMoreTokens() && !blEndsWithAnyWc)
          {
            if( blEndsWithOneWc && iIdxNew + strPartOne.length() != fi.getName().length()-1)
            {
              blRet = false;
              break;
            }
            else if( !blEndsWithOneWc && !fi.getName().endsWith( strPartOne))
            {
              blRet = false;
              break;
            }
          }
          iIdx = iIdxNew;
          sLastWc = WCONE;
          ++ii;
        }
        sLastWc = WCANY;
      }
      else
      {
        iIdxNew = fi.getName().indexOf( strPart);
        if( i == 0 && !blStartsWithAnyWc && iIdxNew != 0)
        {
          blRet = false;
          break;
        }
        else
        {
          if( i == 0)
            blRet = true;
        }
        if( (sLastWc == WCANY && iIdxNew > iIdx) || (sLastWc == WCONE && iIdxNew == iIdx+2))
          blRet = true;
        else if( i > 0)
        {
          blRet = false;
          break;
        }
        if( !stAny.hasMoreTokens() && !blEndsWithAnyWc)
        {
          if( blEndsWithOneWc && iIdxNew + strPart.length() != fi.getName().length()-1)
          {
            blRet = false;
            break;
          }
          else if( !blEndsWithOneWc && !fi.getName().endsWith( strPart))
          {
            blRet = false;
            break;
          }
        }
        iIdx = iIdxNew;
        sLastWc = WCANY;
      }
      ++i;
    }while( stAny.hasMoreTokens());

    return blRet;
  }

  public String getDescription()
  {
    return m_strWildcardText;
  }

  static public Vector createFileInterfaceFilters( String strWildcards, String strAppName)
  {
    Vector vecFi = new Vector();
    StringTokenizer st = new StringTokenizer( strWildcards, ",;", false);

    while( st.hasMoreTokens())
    {
      String str = st.nextToken().trim();
      FileInterfaceFilter fif = new WildcardFileInterfaceFilter( str, strAppName);
      vecFi.add( fif);
    }
    return vecFi;
  }

  public String toString()
  {
    return m_strWildcardText;
  }

  private void writeObject( java.io.ObjectOutputStream out )
    throws IOException
  {
    out.defaultWriteObject();

    out.writeObject( m_strWildcardText );
  }

  private void readObject( java.io.ObjectInputStream in )
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();

    m_strWildcardText = (String)in.readObject();
  }
}