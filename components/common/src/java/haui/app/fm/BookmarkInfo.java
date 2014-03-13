package haui.app.fm;

import haui.util.AppProperties;

import java.util.Vector;

/**
 *		Module:					BookmarkInfo.java<br>
 *										$Source: $
 *<p>
 *		Description:    Contains the bookmark information.<br>
 *</p><p>
 *		Created:				25.06.2002	by	AE
 *</p><p>
 *		@history				25.06.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class BookmarkInfo
{
  // property constants
  public final static String CONNECTIONID = "ConnectionId";
  public final static String PATH = "Path";

  // member variables
  private String m_strConnectionId;
  private String m_strPath;

  public BookmarkInfo( String strConnectionId, String strPath)
  {
    m_strConnectionId = strConnectionId;
    m_strPath = strPath;
  }

  /*
  public BookmarkInfo( FileInfo finfo)
  {
    if( finfo.isLocal())
      m_strConnectionId = FileInfoPanel.LOCAL;
    else
    {
      ConnectionManager cm = finfo.getConnectionObject();
      if( cm != null)
      {
        m_strConnectionId = cm.getURL().getHost();
      }
    }
    m_strPath = finfo.getAbsolutePath();
  }
  */

  public Object clone()
  {
    BookmarkInfo biNew = new BookmarkInfo( getConnectionId(), getPath());
    return biNew;
  }

  public void setConnectionId( String strConnectionId)
  {
    m_strConnectionId = strConnectionId;
  }

  public String getConnectionId()
  {
    return m_strConnectionId;
  }

  public void setPath( String strPath)
  {
    m_strPath = strPath;
  }

  public String getPath()
  {
    return m_strPath;
  }

  public boolean equals( Object obj)
  {
    return toString().equals( obj.toString());
  }

  public String toString()
  {
    return m_strConnectionId + "; " + m_strPath;
  }

  static public void eraseBookmarks( String strPrefix, AppProperties appProps)
  {
    for( int i = 0; appProps.getProperty( strPrefix + String.valueOf( i) + "." + CONNECTIONID) != null; i++)
    {
      appProps.remove( strPrefix + String.valueOf( i) + "." + CONNECTIONID);
      appProps.remove( strPrefix + String.valueOf( i) + "." + PATH);
    }
  }

  static public Vector initBookmarkVector( String strPrefix, AppProperties appProps)
  {
    int i = 0;
    String strId;
    String strPar;
    Vector vecBookmarks = new Vector();

    for( i = 0; (strId = appProps.getProperty( strPrefix + String.valueOf( i) + "." + CONNECTIONID)) != null
      && (strPar = appProps.getProperty( strPrefix + String.valueOf( i) + "." + PATH)) != null;
      i++)
    {
      BookmarkInfo bi = new BookmarkInfo( strId, strPar);
      vecBookmarks.add( bi);
    }
    return vecBookmarks;
  }

  static public void storeBookmarks( String strPrefix, Vector vecBookmarks, AppProperties appProps)
  {
    for( int i = 0; i < vecBookmarks.size(); i++)
    {
      BookmarkInfo bi = (BookmarkInfo)vecBookmarks.elementAt( i);
      appProps.setProperty( strPrefix + String.valueOf( i) + "." + CONNECTIONID,
        bi.getConnectionId());
      appProps.setProperty( strPrefix + String.valueOf( i) + "." + PATH,
        bi.getPath());
    }
  }
}