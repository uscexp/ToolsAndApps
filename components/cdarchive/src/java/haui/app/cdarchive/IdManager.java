package haui.app.cdarchive;

import haui.asynch.Mutex;
import haui.util.AppProperties;
import haui.util.ConfigPathUtil;
import haui.util.GlobalAppProperties;
import haui.util.PropertyFile;
import haui.util.PropertyStruct;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Vector;

/**
 * Module:      IdManager.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\IdManager.java,v $
 *<p>
 * Description: Manages, register, unregister and creates unique object ids.<br>
 *</p><p>
 * Created:     05.05.2003 by AE
 *</p><p>
 *
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     05. May 2003
 * @history     05.05.2003	by	AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: IdManager.java,v $
 * Revision 1.3  2004-08-31 16:03:05+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.2  2004-06-22 14:08:53+02  t026843
 * bigger changes
 *
 * Revision 1.1  2004-02-17 16:31:58+01  t026843
 * bugfixes
 *
 * Revision 1.0  2003-05-21 16:24:42+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.3 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\IdManager.java,v 1.3 2004-08-31 16:03:05+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class IdManager
{
  // constants
  public final static int LOCKTIMEOUT = 1000;
  private final static String IDMANAGERCONF = "data" + File.separator + "idmanager.ppr";
  private final static String IDMANAGER = "IdManager";
  private final static String FREEIDS = "freeIds";
  private final static String USEDIDS = "usedIds";
  private final static String MAXID = "maxId";
  private final static String IDMANAGERHEADER = "IdManager free ids";

  private static final IdManager m_idmanager = new IdManager();
  private PropertyFile m_pfIdManager;
  private Vector m_vecUsedIds;
  private Vector m_vecFreeIds;
  private long m_lCurrentMax;
  private AppProperties m_appPropsFreeIds = new AppProperties();
  private Mutex m_mutLock = new Mutex();

  private IdManager()
  {
    ConfigPathUtil.init( CdArchiveDesktop.CDARCHDT);
    m_lCurrentMax = 0;
    try
    {
      m_pfIdManager = new PropertyFile( ( new File( ConfigPathUtil.getCurrentReadPath( CdArchiveDesktop.CDARCHDT, IDMANAGERCONF) ) ).toURL(),
                                       ( new File( ConfigPathUtil.getCurrentSavePath( CdArchiveDesktop.CDARCHDT, IDMANAGERCONF) ) ).toURL() );
      m_pfIdManager.load();
      PropertyStruct psIdManager = m_pfIdManager.structValue( IDMANAGER );
      init( psIdManager );
    }
    catch( MalformedURLException muex)
    {
      muex.printStackTrace();
    }
  }

  public void _save()
  {
    // store idmanager config
    if( !m_vecFreeIds.isEmpty() || !m_vecUsedIds.isEmpty())
    {
      // store idmanager data
      PropertyStruct psIdManager = m_pfIdManager.structValue( IDMANAGER );
      String strType =  IDMANAGER;
      if( psIdManager != null)
        strType = m_pfIdManager.getType( IDMANAGER);
      else
      {
        psIdManager = new PropertyStruct();
        psIdManager.setStructName( IDMANAGER);
      }
      store( psIdManager);
      m_pfIdManager.addElement( strType, psIdManager);
      m_pfIdManager.save();
    }
  }

  public static IdManager instance()
  {
    return m_idmanager;
  }

  public long registerId()
  {
    long lUniqueId = getNextFreeId();
    if( lUniqueId == 0)
    {
      lUniqueId = getNextFreeId();
    }
    else
    {
      if( m_lCurrentMax < lUniqueId)
        m_lCurrentMax = lUniqueId;
    }
    Long lUId = new Long( lUniqueId);

    if( !m_vecUsedIds.contains( lUId))
    {
      m_vecUsedIds.addElement( lUId);
    }
    else
    {
      GlobalAppProperties.instance().getPrintStreamOutput( CdArchiveDesktop.CDARCHDT).println( "Error registering id: Id " + String.valueOf( lUniqueId) + " is already registered");
      //System.err.println( "Error registering id: Id " + String.valueOf( lUniqueId) + " is already registered");
    }
    return lUniqueId;
  }

  public void unregisterId( long lId)
  {
    Long lUId = new Long( lId);
    m_vecFreeIds.addElement( lUId);
    m_vecUsedIds.removeElement( lUId);
  }

  public boolean isUsedId( long lId)
  {
    return m_vecUsedIds.contains( new Long( lId));
  }

  protected long getNextFreeId()
  {
    long lId = 0;
    if( m_vecFreeIds.isEmpty())
      lId = ++m_lCurrentMax;
    else
    {
      Long lUId = (Long)m_vecFreeIds.firstElement();
      lId = lUId.longValue();
      m_vecFreeIds.removeElement( lUId);
    }
    return lId;
  }

  public long getCurrentMaxId()
  {
    return m_lCurrentMax;
  }

  public Vector getAllRegisteredIds()
  {
    return m_vecUsedIds;
  }

  private void init( PropertyStruct ps)
  {
    try
    {
      m_mutLock.acquire( LOCKTIMEOUT );
      if( m_vecFreeIds == null || m_vecUsedIds == null)
      {
        if( ps != null )
        {
          m_lCurrentMax = ps.longValue( MAXID );
          m_vecFreeIds = ps.vectorValue( FREEIDS );
          m_vecUsedIds = ps.vectorValue( USEDIDS );
        }
        if( m_vecFreeIds == null)
          m_vecFreeIds = new Vector();
        if( m_vecUsedIds == null)
          m_vecUsedIds = new Vector();
      }
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
  }

  public void store( PropertyStruct ps)
  {
    try
    {
      m_mutLock.acquire( LOCKTIMEOUT );
      ps.setLongValue( MAXID, m_lCurrentMax);
      if( m_vecFreeIds != null)
        ps.setVectorValue( FREEIDS, m_vecFreeIds);
      if( m_vecUsedIds != null)
        ps.setVectorValue( USEDIDS, m_vecUsedIds);
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
  }
}