package haui.app.HttpTunnel;

import haui.util.ConfigPathUtil;

import java.lang.String;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import haui.util.AppProperties;

/**
 *
 *		Module:					ConnectionMap.java<br>
 *										$Source: $
 *<p>
 *		Description:    Holds the connection mapping info.<br>
 *</p><p>
 *		Created:				05.12.2001	by	AE
 *</p><p>
 *		@history				05.12.2001 - 05.12.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v0.1, 2001; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ConnectionMap
  extends Object
{
  // property constants
  public final static String MAPSFILEELEMENTSEPERATOR = ";";
  public final static String MAPSFILENAME = "httpt_map.pps";
  public final static String MAPSFILEHEADER = "HttpTunnelClientServer connection mapping properties";

  // member variables
  static AppProperties appMaps = new AppProperties();
  String m_strLocalAddr;
  String m_strRemoteAddr;
  int m_iLocalPort;
  int m_iRemotePort;
  boolean m_blPoll = true;
  boolean m_blCompressed = true;
  boolean m_blEncrypt = true;

  public ConnectionMap()
  {
  }

  public ConnectionMap( String strMap)
  {
    parse( strMap);
  }

  public Object clone()
  {
    return new ConnectionMap( this.toString());
  }

  public boolean equals( Object obj)
  {
    return ((ConnectionMap)obj).toString().equalsIgnoreCase( this.toString());
  }

  public String getLocalAddr()
  {
    return m_strLocalAddr;
  }

  public String getRemoteAddr()
  {
    return m_strRemoteAddr;
  }

  public int getLocalPort()
  {
    return m_iLocalPort;
  }

  public int getRemotePort()
  {
    return m_iRemotePort;
  }

  public boolean mustPoll()
  {
    return m_blPoll;
  }

  public boolean sendCompressed()
  {
    return m_blCompressed;
  }

  public boolean isCrypted()
  {
    return m_blEncrypt;
  }

  public void setLocalAddr( String strLocalAddr)
  {
    m_strLocalAddr = strLocalAddr;
  }

  public void setRemoteAddr( String strRemoteAddr)
  {
    m_strRemoteAddr = strRemoteAddr;
  }

  public void setLocalPort( int iLocalPort)
  {
    m_iLocalPort = iLocalPort;
  }

  public void setRemotePort( int iRemotePort)
  {
    m_iRemotePort = iRemotePort;
  }

  public void setPoll( boolean blPoll)
  {
    m_blPoll = blPoll;
  }

  public void setCompressed( boolean blCompressed)
  {
    m_blCompressed = blCompressed;
  }

  public void setCrypted( boolean blEncrypt)
  {
    m_blEncrypt = blEncrypt;
  }

  static public Vector _load()
  {
    appMaps.load( ConfigPathUtil.getCurrentReadPath( HttpTunnelClientServer.APPNAME, MAPSFILENAME));
    Vector vMaps = new Vector();
    String strMap;
    for( int i = 0; (strMap = appMaps.getProperty( "Map" + String.valueOf( i))) != null; i++)
    {
      ConnectionMap cm = new ConnectionMap( strMap);
      vMaps.add( cm);
    }
    return vMaps;
  }

  static public void _save( Vector vMaps)
  {
    ConnectionMap cm = null;
    ConnectionMap._erase();
    for( int i = 0; i < vMaps.size(); i++)
    {
      cm = (ConnectionMap)vMaps.elementAt( i);
      appMaps.setProperty( "Map" + String.valueOf( i), cm.toString());
    }
    appMaps.store( ConfigPathUtil.getCurrentSavePath( HttpTunnelClientServer.APPNAME, MAPSFILENAME), MAPSFILEHEADER);
  }

  static public void _erase()
  {
    for( int i = 0; appMaps.getProperty( "Map" + String.valueOf( i)) != null; i++)
    {
      appMaps.remove( "Map" + String.valueOf( i));
    }
  }

  private void parse( String strMap)
    throws NoSuchElementException
  {
    StringTokenizer stFirst = new StringTokenizer( strMap, MAPSFILEELEMENTSEPERATOR, false);
    String strTmp;
    String strLocal, strRemote;

    if( stFirst.countTokens() < 2)
      throw new NoSuchElementException( "Wrong format of connection mapping string!");

    strTmp = stFirst.nextToken();
    StringTokenizer stSecond = new StringTokenizer( strTmp, ":", false);
    if( stSecond.countTokens() != 2)
      throw new NoSuchElementException( "Wrong format of connection mapping string!");
    m_strLocalAddr = stSecond.nextToken();
    m_iLocalPort = Integer.parseInt( stSecond.nextToken());

    strTmp = stFirst.nextToken();
    stSecond = new StringTokenizer( strTmp, ":", false);
    if( stSecond.countTokens() != 2)
      throw new NoSuchElementException( "Wrong format of connection mapping string!");
    m_strRemoteAddr = stSecond.nextToken();
    m_iRemotePort = Integer.parseInt( stSecond.nextToken());

    if( stFirst.hasMoreTokens())
    {
      strTmp = stFirst.nextToken();
      m_blPoll = new Boolean( strTmp).booleanValue();
    }
    else
      m_blPoll = true;

    if( stFirst.hasMoreTokens())
    {
      strTmp = stFirst.nextToken();
      m_blCompressed = new Boolean( strTmp).booleanValue();
    }
    else
      m_blCompressed = true;

    if( stFirst.hasMoreTokens())
    {
      strTmp = stFirst.nextToken();
      m_blEncrypt = new Boolean( strTmp).booleanValue();
    }
    else
      m_blEncrypt = true;
  }

  public String toString()
  {
    if( m_strLocalAddr == null || m_strRemoteAddr == null)
      return "";
    return m_strLocalAddr + ":" + String.valueOf( m_iLocalPort) + MAPSFILEELEMENTSEPERATOR + m_strRemoteAddr + ":" + String.valueOf( m_iRemotePort)
      + MAPSFILEELEMENTSEPERATOR + String.valueOf( m_blPoll) + MAPSFILEELEMENTSEPERATOR + String.valueOf( m_blCompressed)
      + MAPSFILEELEMENTSEPERATOR + String.valueOf( m_blEncrypt);
  }
}