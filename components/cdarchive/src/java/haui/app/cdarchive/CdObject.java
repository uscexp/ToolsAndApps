package haui.app.cdarchive;

import haui.asynch.Mutex;
import haui.util.ConfigPathUtil;
import haui.util.GlobalAppProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 * Module:      CdObject.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdObject.java,v $
 *<p>
 * Description: CdObject.<br>
 *</p><p>
 * Created:     23.04.2003	by	AE
 *</p><p>
 *
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     23. April 2003
 * @history     23.04.2003	by	AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: CdObject.java,v $
 * Revision 1.3  2004-08-31 16:03:05+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.2  2004-06-22 14:08:53+02  t026843
 * bigger changes
 *
 * Revision 1.1  2004-02-17 16:26:20+01  t026843
 * change to serialized data instead of a property file for the archive database
 *
 * Revision 1.0  2003-05-21 16:24:40+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.3 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdObject.java,v 1.3 2004-08-31 16:03:05+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class CdObject
  implements Serializable
{
  // Constants
  public static final String DATAFILE = "data" + File.separator + "cdarchive.dat";

  // Default properties constants
  public static final String ID = "id";
  public static final String CATEGORY = "category";
  public static final String LABEL = "label";
  public static final String CONTENT = "content";

  // member variables
  protected static Vector m_vecData;
  protected Vector m_vecResult;
  protected int m_iCount = 0;
  protected long m_lId = -1;
  protected String m_strCategory = "";
  protected String m_strLabel = "";
  protected String m_strContent = "";
  protected static Mutex m_mutLock;

  public CdObject()
  {
    ConfigPathUtil.init( CdArchiveDesktop.CDARCHDT);
    if( m_mutLock == null)
      m_mutLock = new Mutex();
    connect();
  }

  public static boolean connect( JProgressBar pb)
  {
    boolean blRet = true;
    try
    {
      if( m_mutLock == null)
        m_mutLock = new Mutex();
      m_mutLock.acquire( IdManager.LOCKTIMEOUT);
      if( m_vecData == null )
      {
        m_vecData = new Vector();
        File file =  new File( ConfigPathUtil.getCurrentReadPath( CdArchiveDesktop.CDARCHDT, DATAFILE));

        ObjectInputStream ois = new ObjectInputStream( new FileInputStream( file));

        int iCount = ois.readInt();
        pb.setMaximum( iCount);
        pb.setValue( 0 );

        for( int i = 0; i < iCount; ++i)
        {
          CdObject co = (CdObject)ois.readObject();
          m_vecData.add( co);
          pb.setValue( i+1);
        }
      }
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      blRet = false;
    }
    finally
    {
      m_mutLock.release();
    }
    return blRet;
  }

  private static boolean connect()
  {
    return connect( null);
  }

  public static boolean _save()
  {
    boolean blRet = false;

    if( m_vecData == null)
      return blRet;

    try
    {
      int iCount = m_vecData.size();
      File file =  new File( ConfigPathUtil.getCurrentSavePath( CdArchiveDesktop.CDARCHDT, DATAFILE));
      ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( file));

      oos.writeInt( iCount);
      for( int i = 0; i < iCount; ++i)
      {
        oos.writeObject( m_vecData.elementAt(i));
      }
      blRet = true;
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    return blRet;
  }

  public static Vector getAllCategories()
  {
    if( m_vecData == null)
      connect();
    Vector vecCat = new Vector();
    for( int i = 0; i < m_vecData.size(); ++i)
    {
      CdObject co = (CdObject)m_vecData.elementAt(i);
      String str = co.getCategory();
      if( !vecCat.contains( str))
        vecCat.add( str);
    }
    return vecCat;
  }

/*
  public static String getCategory( PropertyStruct ps)
  {
    if( m_vecData == null)
      connect();
    return m_pfDb.getType( ps.getStructName());
  }

  private void removeEntry( String strCategory, PropertyStruct ps)
  {
    long lId = ps.intValue( ID);
    IdManager.instance().unregisterId( lId);
    m_pfDb.removeElement( strCategory, ps);
  }
*/
  protected static Vector getAllWithCategory( String strCategory)
  {
    if( m_vecData == null)
      connect();
    Vector vecRet = new Vector();
    for( int i = 0; i < m_vecData.size(); ++i)
    {
      CdObject co = (CdObject)m_vecData.elementAt(i);
      String str = co.getCategory();
      if( strCategory.equals( str))
      {
        vecRet.add( co );
      }
    }
    return vecRet;
  }

  public static void removeCategory( String strCategory)
  {
    if( m_vecData == null)
      connect();
    for( int i = 0; i < m_vecData.size(); ++i)
    {
      CdObject co = (CdObject)m_vecData.elementAt(i);
      String str = co.getCategory();
      if( strCategory.equals( str))
      {
        long lId = co.getId();
        IdManager.instance().unregisterId( lId);
        m_vecData.remove( co );
      }
    }
  }

  public static void renameCategory( String strOldCategory, String strNewCategory)
  {
    if( m_vecData == null)
      connect();
    for( int i = 0; i < m_vecData.size(); ++i)
    {
      CdObject co = (CdObject)m_vecData.elementAt(i);
      String str = co.getCategory();
      if( strOldCategory.equals( str))
      {
        co.setCategory( strNewCategory);
        m_vecData.setElementAt( co, i );
      }
    }
  }

  private void addSorted( Vector vec, CdObject cdObj)
  {
    boolean blInserted = false;

    for( int i = 0; i < vec.size(); ++i)
    {
      CdObject co = (CdObject)vec.elementAt(i);
      if( vec.size() == 0)
      {
        vec.add( cdObj );
        blInserted = true;
        break;
      }
      else if( co.getLabel() != null && cdObj.getLabel() != null && co.getLabel().compareTo( cdObj.getLabel()) > 0)
      {
        vec.insertElementAt( cdObj, i);
        blInserted = true;
        break;
      }
    }
    if( !blInserted)
      vec.add( cdObj );
  }

  private boolean executeQuery( String strCategory, QueryCondition qc )
  {
    boolean blRet = false;

    if( m_vecData == null)
      connect();
    m_vecResult = new Vector();
    Vector vecData = m_vecData;
    if( strCategory != null && !strCategory.equals( ""))
    {
      vecData = getAllWithCategory( strCategory);
    }
    for( int i = 0; i < vecData.size(); ++i)
    {
      CdObject co = (CdObject)vecData.elementAt(i);
      if( qc.compare( co))
      {
        addSorted( m_vecResult, co);
        //m_vecResult.add( co );
        blRet = true;
      }
    }
    m_iCount = 0;
    return blRet;
  }

  private boolean executeCheckQuery( String strCategory, QueryCondition qc)
  {
    boolean blRet = false;

    if( m_vecData == null)
      connect();
    Vector vecData = m_vecData;
    if( strCategory != null && !strCategory.equals( ""))
    {
      vecData = getAllWithCategory( strCategory);
    }
    for( int i = 0; i < vecData.size(); ++i)
    {
      CdObject co = (CdObject)vecData.elementAt(i);
      if( qc.compare( co))
      {
        blRet = true;
        break;
      }
    }
    m_iCount = 0;
    return blRet;
  }

  public Vector getResult()
  {
    return m_vecResult;
  }

  public String toString()
  {
    String str = String.valueOf( m_lId) + ", " + m_strLabel + ", " + m_strContent.substring( 0, 25) + " ...";
    return str;
  }

  public boolean findWithId( long lId)
  {
    boolean blRet = false;
    QueryCondition qc = new QueryCondition( ID, new Long( lId), QueryCondition.EQUAL);

    blRet = executeQuery( null, qc);
    return blRet;
  }

  public boolean existsId( long lId)
  {
    boolean blRet = false;
    QueryCondition qc = new QueryCondition( ID, new Long( lId), QueryCondition.EQUAL);

    blRet = executeCheckQuery( null, qc);
    return blRet;
  }

  public boolean findWithLabel( String strCategory, String strLabel)
  {
    boolean blRet = false;
    QueryCondition qc = new QueryCondition( LABEL, strLabel, QueryCondition.EQUAL);

    blRet = executeQuery( strCategory, qc);
    return blRet;
  }

  public boolean findWithLabelNoCase( String strCategory, String strLabel)
  {
    boolean blRet = false;
    QueryCondition qc = new QueryCondition( LABEL, strLabel, QueryCondition.EQUAL_NOCASE);

    blRet = executeQuery( strCategory, qc);
    return blRet;
  }

  public boolean existLabel( String strCategory, String strLabel)
  {
    boolean blRet = false;
    QueryCondition qc = new QueryCondition( LABEL, strLabel, QueryCondition.EQUAL);

    blRet = executeCheckQuery( strCategory, qc);
    return blRet;
  }

  public boolean findInContent( String strCategory, String strSubsContent)
  {
    boolean blRet = false;
    QueryCondition qc = new QueryCondition( CONTENT, strSubsContent, QueryCondition.CONTAINS_NOCASE);

    blRet = executeQuery( strCategory, qc);
    return blRet;
  }

  public boolean next()
  {
    boolean blRet = false;
    if( m_iCount < m_vecResult.size())
    {
      CdObject co = (CdObject)m_vecResult.elementAt( m_iCount);
      m_lId = co.getId();
      m_strCategory = co.getCategory();
      m_strLabel = co.getLabel();
      m_strContent = co.getContent();
      ++m_iCount;
      blRet = true;
    }
    else
    {
      m_lId = -1;
      m_strCategory = "";
      m_strLabel = "";
      m_strContent = "";
      m_strCategory = "";
    }
    return blRet;
  }

  public long getId()
  {
    return m_lId;
  }

  public String getCategory()
  {
    return m_strCategory;
  }

  public String getLabel()
  {
    return m_strLabel;
  }

  public String getContent()
  {
    return m_strContent;
  }

  public void setId( int iId)
  {
    m_lId = iId;
  }

  public void setCategory( String strCategory)
  {
    m_strCategory = strCategory;
  }

  public void setLabel( String strLabel)
  {
    m_strLabel = strLabel;
  }

  public void setContent( String strContent)
  {
    m_strContent = strContent;
  }

  public boolean insert( String strCategory)
  {
    boolean blRet = false;
    boolean blExists = false;
    if( strCategory == null )
      return blRet;

    if( ( blExists = existLabel( null, m_strLabel ) ) )
    {
      int iRet = JOptionPane.showConfirmDialog( GlobalAppProperties.instance().getRootComponent( CdArchiveDesktop.CDARCHDT),
                                                "Label already exists, overwrite entry anyway?"
                                                , "Alert", JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE );
      if( iRet == JOptionPane.NO_OPTION )
      {
        GlobalAppProperties.instance().getPrintStreamOutput( CdArchiveDesktop.CDARCHDT).println( "...cancelled");
        //System.out.println( "...cancelled" );
        return blRet;
      }
    }
    try
    {
      m_mutLock.acquire( IdManager.LOCKTIMEOUT);
      if( CdArchiveDesktop.addCategory( strCategory ) )
      {
        if( blExists )
        {
          CdObject cdObj = new CdObject();
          if( cdObj.findWithLabel( null, m_strLabel ) )
          {
            while( cdObj.next() )
              cdObj.delete();
          }
        }
        m_strCategory = strCategory;
        m_lId = IdManager.instance().registerId();
        m_vecData.add( this);
        blRet = true;
      }
      else
      {
        JOptionPane.showMessageDialog( GlobalAppProperties.instance().getRootComponent( CdArchiveDesktop.CDARCHDT), "Wrong or no category selected!"
                                       , "Error", JOptionPane.ERROR_MESSAGE );
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
    return blRet;
  }

  public void _init()
  {
    update( null);
  }

  public boolean update( String strCategory)
  {
    boolean blRet = false;
    try
    {
      m_mutLock.acquire( IdManager.LOCKTIMEOUT);
      if( existsId( m_lId))
      {
        if( strCategory != null && !strCategory.equals( m_strCategory ) )
        {
          if( CdArchiveDesktop.addCategory( strCategory ) )
          {
            m_strCategory = strCategory;
          }
        }
      }
      else
      {
        blRet = insert( strCategory);
        return blRet;
      }
      for( int i = 0; i < m_vecData.size(); ++i)
      {
        CdObject co = ( CdObject )m_vecData.elementAt( i );
        long lId = co.getId();
        if( m_lId == lId )
        {
          m_vecData.setElementAt( this, i );
        }
      }
      blRet = true;
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
    return blRet;
  }

  public boolean delete()
  {
    boolean blRet = false;
    try
    {
      m_mutLock.acquire( IdManager.LOCKTIMEOUT);
      for( int i = 0; i < m_vecData.size(); ++i)
      {
        CdObject co = ( CdObject )m_vecData.elementAt( i );
        long lId = co.getId();
        if( m_lId == lId )
        {
          m_vecData.removeElementAt( i);
        }
      }
      blRet = true;
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
    return blRet;
  }

  class QueryCondition
  {
    // constants
    public static final short EQUAL = 1;
    public static final short NOT_EQUAL = 2;
    public static final short EQUAL_NOCASE = 3;
    public static final short NOT_EQUAL_NOCASE = 4;
    public static final short GREATER = 5;
    public static final short GREATER_EQUAL = 6;
    public static final short SMALLER = 7;
    public static final short SMALLER_EQUAL = 8;
    public static final short CONTAINS = 9;
    public static final short CONTAINS_NOCASE = 10;

    // member variables
    String m_strFieldName;
    Object m_objValue;
    short m_sCondition;

    public QueryCondition( String strFieldName, Object objValue, short sCondition)
    {
      m_strFieldName = strFieldName;
      m_objValue = objValue;
      m_sCondition = sCondition;
    }

    /**
     * compares the two given values
     * <br>
     * EQUAL -> value = internal value
     * <br>
     * NOT_EQUAL -> value != internal value
     * <br>
     * EQUAL_NOCASE -> value = internal value (only Strings, not case snsitive)
     * <br>
     * NOT_EQUAL_NOCASE -> value != internal value (only Strings, not case snsitive)
     * <br>
     * GREATER -> value > internal value
     * <br>
     * GREATER_EQUAL -> value >= internal value
     * <br>
     * SMALLER -> value < internal value
     * <br>
     * SMALLER_EQUAL -> value <= internal value
     * <br>
     * CONTAINS -> value included in internal value (only Strings, case snsitive)
     * <br>
     * CONTAINS_NOCASE -> value included in internal value (only Strings, not case snsitive)
     *
     * @param  value   value of the variable
     * @return         true if contition is true
     */
    public boolean compare( CdObject cdObj)
    {
      boolean blRet = false;
      Object value = null;
      if( m_strFieldName.equals( ID))
        value = new Long( cdObj.getId());
      else if( m_strFieldName.equals( LABEL))
        value = cdObj.getLabel();
      else if( m_strFieldName.equals( CONTENT))
        value = cdObj.getContent();

      if( value.getClass() != m_objValue.getClass())
      {
        throw new ClassCastException( "Value class is not of the same type!");
      }
      switch( m_sCondition)
      {
        case EQUAL:
          blRet = value.equals( m_objValue);
          break;

        case NOT_EQUAL:
          blRet = !value.equals( m_objValue);
          break;

        case EQUAL_NOCASE:
          if( !(m_objValue instanceof String))
          {
            throw new RuntimeException( "This class can only be tested to EQUAL_NOCASE!");
          }
          blRet = ((String)value).toLowerCase().equals( ((String)m_objValue).toLowerCase());
          break;

        case NOT_EQUAL_NOCASE:
          if( !(m_objValue instanceof String))
          {
            throw new RuntimeException( "This class can only be tested to EQUAL_NOCASE!");
          }
          blRet = !((String)value).toLowerCase().equals( ((String)m_objValue).toLowerCase());
          break;

        case GREATER:
          if( !(m_objValue instanceof Comparable))
          {
            throw new RuntimeException( "This class can only be tested to EQUAL or NOT_EQUAL!");
          }
          blRet = ((Comparable)value).compareTo( m_objValue) < 0;
          break;

        case GREATER_EQUAL:
          if( !(m_objValue instanceof Comparable))
          {
            throw new RuntimeException( "This class can only be tested to EQUAL or NOT_EQUAL!");
          }
          blRet = ( ( Comparable ) value).compareTo( m_objValue) <= 0;
          break;

        case SMALLER:
          if( !(m_objValue instanceof Comparable))
          {
            throw new RuntimeException( "This class can only be tested to EQUAL or NOT_EQUAL!");
          }
          blRet = ( ( Comparable ) value).compareTo( m_objValue) > 0;
          break;

        case SMALLER_EQUAL:
          if( !(m_objValue instanceof Comparable))
          {
            throw new RuntimeException( "This class can only be tested to EQUAL or NOT_EQUAL!");
          }
          blRet = ( ( Comparable )value ).compareTo(  m_objValue) >= 0;
          break;

        case CONTAINS:
          if( !(m_objValue instanceof String))
          {
            throw new RuntimeException( "This class can only be tested to CONTAINS!");
          }
          blRet = ((String)value).indexOf( (String)m_objValue ) != -1;
          break;

        case CONTAINS_NOCASE:
          if( !(m_objValue instanceof String))
          {
            throw new RuntimeException( "This class can only be tested to CONTAINS_NOCASE!");
          }
          blRet = ((String)value).toLowerCase().indexOf( ((String)m_objValue).toLowerCase() ) != -1;
          break;
      }
      return blRet;
    }
  }

  private void writeObject(java.io.ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();

    out.writeLong( getId());
    out.writeObject( getCategory());
    out.writeObject( getLabel());
    out.writeObject( getContent());
  }

  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();

    m_lId = in.readLong();
    setCategory( (String)in.readObject());
    setLabel( (String)in.readObject());
    setContent( (String)in.readObject());
  }
}