package haui.components;

import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.Frame;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Module:					HttpProxyHandler.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\HttpProxyHandler.java,v $ <p> Description:    Handels HTTP proxies.<br> </p><p> Created:				28.09.2000	by	AE </p><p>
 * @history  				26.09.2000 - 05.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: HttpProxyHandler.java,v $  Revision 1.3  2004-08-31 16:03:11+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2003-05-28 14:19:54+02  t026843  reorganisations  Revision 1.1  2002-01-15 11:14:25+01  t026843  Data header changed to a dynamic header and extended with host and port  Revision 1.0  2000-10-05 14:48:41+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.3 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\HttpProxyHandler.java,v 1.3 2004-08-31 16:03:11+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class HttpProxyHandler
{
  AppProperties m_appProps;
  String m_strAppName;
  String m_strUsr;
  String m_strPwd;
  String m_auth;
  boolean m_blProxy;
  String m_strProxyHost;
  int m_iProxyPort;
  String m_strNoProxyList;
  ProxyPropertiesDialog m_propsDlg;

  public HttpProxyHandler( String strAppName)
  {
    this( null, strAppName);
  }

  public HttpProxyHandler( AppProperties appProps, String strAppName)
  {
    m_appProps = appProps;
    m_strAppName = strAppName;
  }

  public ProxyPropertiesDialog showProxyPropertiesDialog( Frame frame)
  {
    m_propsDlg = new ProxyPropertiesDialog( frame, "proxy settings", true, m_appProps, m_strAppName);
    m_propsDlg.setVisible( true);
    if( m_appProps != null)
      m_propsDlg.dispose();
    return m_propsDlg;
  }

  public void reset()
  {
    System.getProperties().remove("proxySet");
    System.getProperties().remove("proxyHost");
    System.getProperties().remove("proxyPort");
    System.getProperties().remove("http.proxyHost");
    System.getProperties().remove("http.proxyPort");
    System.getProperties().remove("http.nonProxyHosts");
    System.getProperties().remove("https.proxyHost");
    System.getProperties().remove("https.proxyPort");
    System.getProperties().remove("https.nonProxyHosts");
    Authenticator.setDefault(null);
    m_auth = null;
  }

  public void getAllProperties()
  {
    if( m_appProps != null)
    {
      m_blProxy = m_appProps.getBooleanProperty( GlobalApplicationContext.PROXYENABLE);
      m_strProxyHost = m_appProps.getProperty( GlobalApplicationContext.PROXYHOST);
      m_iProxyPort = (new Integer( m_appProps.getProperty( GlobalApplicationContext.PROXYPORT))).intValue();;
      m_strUsr = m_appProps.getProperty( GlobalApplicationContext.PROXYUSER);
      m_strPwd = m_appProps.getProperty( GlobalApplicationContext.PROXYPASSWORD);
      m_strNoProxyList = m_appProps.getProperty( GlobalApplicationContext.PROXYNOPROXYLIST);
    }
  }

  public void setProxyEnabled( boolean blProxy)
  {
    m_blProxy = blProxy;
    reset();
  }

  public boolean useProxy()
  {
    return m_blProxy;
  }

  public String getProxyHost()
  {
    return m_strProxyHost;
  }

  public void setProxyHost( String strProxyHost)
  {
    m_strProxyHost = strProxyHost;
    reset();
  }

  public int getProxyPort()
  {
    return m_iProxyPort;
  }

  public void setProxyPort( int iProxyPort)
  {
    m_iProxyPort = iProxyPort;
    reset();
  }

  public String getUser()
  {
    return m_strUsr;
  }

  public void setUser( String strUsr)
  {
    m_strUsr = strUsr;
    reset();
  }

  public String getPassword()
  {
    return m_strPwd;
  }

  public void setPassword( String strPwd)
  {
    m_strPwd = strPwd;
    reset();
  }

  public String getNoProxyList()
  {
    return m_strNoProxyList;
  }

  public void setNoProxyList( String strNoProxyList)
  {
    m_strNoProxyList = strNoProxyList;
    reset();
  }

  public String getAuthorization()
  {
    getAllProperties();
    if( !m_blProxy)
      return "";
    if( m_auth == null)
    {
      System.getProperties().put("proxySet", String.valueOf( useProxy()));
      System.setProperty("proxySet", String.valueOf( useProxy()));
      System.getProperties().put("http.proxySet", String.valueOf( useProxy()));
      System.setProperty("http.proxySet", String.valueOf( useProxy()));
      System.getProperties().put("https.proxySet", String.valueOf( useProxy()));
      System.setProperty("https.proxySet", String.valueOf( useProxy()));
      if( getProxyHost() != null)
      {
        System.getProperties().put("proxyHost", getProxyHost());
        System.setProperty("http.proxyHost", getProxyHost());
        System.setProperty("https.proxyHost", getProxyHost());
      }
      if( getProxyPort() != 0)
      {
        System.getProperties().put("proxyPort", String.valueOf( getProxyPort()));
        System.setProperty("http.proxyPort", String.valueOf( getProxyPort()));
        System.setProperty("https.proxyPort", String.valueOf( getProxyPort()));
      }
      if( m_strNoProxyList != null)
      {
        System.setProperty("http.nonProxyHosts", m_strNoProxyList);
        System.setProperty("https.nonProxyHosts", m_strNoProxyList);
      }
      // set proxy authentication
      if (getUser() == null || getPassword() == null || getUser().length()==0)
      {
        Authenticator.setDefault( new ProxyAuthenticator(null));
      }
      else
      {
        PasswordAuthentication pw = new PasswordAuthentication( getUser(), getPassword().toCharArray());
        Authenticator.setDefault( new ProxyAuthenticator(pw));
      }
      // set proxy authentication  string
      String authStr = getUser() + ":" + getPassword();
      m_auth = "Basic" + new sun.misc.BASE64Encoder().encode(authStr.getBytes());
    }
    return m_auth;
  }
}

class ProxyAuthenticator
extends Authenticator
{
  PasswordAuthentication pw = null;
  public ProxyAuthenticator(PasswordAuthentication pw)
  {
    this.pw = pw;
  }

  protected PasswordAuthentication getPasswordAuthentication()
  {
    return pw;
  }
}