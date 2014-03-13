package haui.util;

import haui.io.StringBufferOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 *
 *		Module:					AppProperties.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\AppProperties.java,v $
 *<p>
 *		Description:    Holds the application properties.<br>
 *</p><p>
 *		Created:				29.09.2000	by	AE
 *</p><p>
 *		@history				29.09.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: AppProperties.java,v $
 *		Revision 1.3  2004-08-31 16:03:06+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.2  2004-06-22 14:08:51+02  t026843
 *		bigger changes
 *
 *		Revision 1.1  2004-02-17 16:28:58+01  t026843
 *		workaround for the ByteArrayOutputStream problem
 *
 *		Revision 1.0  2003-05-28 14:21:51+02  t026843
 *		Initial revision
 *
 *		Revision 1.6  2003-05-21 16:47:21+02  t026843
 *		getObjectProperty(...) and setObjectProperty(...) added
 *
 *		Revision 1.5  2002-09-27 15:29:41+02  t026843
 *		Dialogs extended from JExDialog
 *
 *		Revision 1.4  2002-09-18 11:16:22+02  t026843
 *		- changes to fit extended filemanager.pl
 *		- logon and logoff moved to 'TypeFile's
 *		- startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal
 *		- LRShell changed to work with filemanager.pl
 *
 *		Revision 1.3  2002-05-29 11:18:19+02  t026843
 *		Added:
 *		- starter menu
 *		- config starter dialog
 *		- file extensions configurable for right, middle and left mouse button
 *
 *		Changed:
 *		- icons minimized
 *		- changed layout of file ex. and button cmd config dialog
 *		- output area hideable
 *		- other minor changes
 *
 *		bugfixes:
 *		- some minor bugfixes
 *
 *		Revision 1.2  2001-07-20 16:29:04+02  t026843
 *		FileManager changes
 *
 *		Revision 1.1  2000-10-13 09:09:45+02  t026843
 *		bugfixes + changes
 *
 *		Revision 1.0  2000-10-05 14:48:45+02  t026843
 *		Initial revision
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision: 1.3 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\AppProperties.java,v 1.3 2004-08-31 16:03:06+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class AppProperties
  extends Properties
{
  private static final long serialVersionUID = -2443386762078173069L;

  public AppProperties()
  {
    super();
  }

  public Object clone()
  {
    AppProperties appProps = (AppProperties)super.clone();
    //appProps.addRootComponentHashMap( m_hmRootComp);
    //appProps.addPrintStreamOutputHashMap( m_hmPrintStreamOutput);
    //appProps.addPrintStreamErrorHashMap( m_hmPrintStreamError);
    Set keys = keySet();
    Iterator itKeys = keys.iterator();
    while( itKeys.hasNext())
    {
      Object objKey = itKeys.next();
      Object objValue = get( objKey);
      appProps.put( objKey, objValue);
    }

    return appProps;
  }

  /**
   * @param key
   * @return
   * @deprecated use {@link AppProperties#getProperty(String, boolean)}
   */
  public boolean getBooleanProperty( String key)
  {
    return getProperty(key, false);
  }

  /**
   * @param key
   * @param value
   * @deprecated use {@link AppProperties#setProperty(String, boolean)}
   */
  public void setBooleanProperty( String key, boolean value)
  {
    Boolean bl = new Boolean( value);
    setProperty( key, bl.toString());
  }
  
  public boolean getProperty(String key, boolean defaultValue)
  {
    String str = getProperty( key);
    if( str == null)
      return defaultValue;
    Boolean bl = new Boolean( str);
    return bl.booleanValue();
  }
  
  public boolean setProperty(String key, boolean value)
  {
    Boolean bl = new Boolean( value);
    setProperty( key, bl.toString());
    return value;
  }

  /**
   * @param key
   * @return
   * @deprecated use {@link AppProperties#getProperty(String, int)}
   */
  public Integer getIntegerProperty( String key)
  {
    String str = getProperty( key);
    if( str == null)
      return null;
    Integer iObj = new Integer( str);
    return iObj;
  }

  /**
   * @param key
   * @param value
   * @deprecated use {@link AppProperties#setProperty(String, int)}
   */
  public void setIntegerProperty( String key, Integer value)
  {
    setProperty( key, value.toString());
  }

  public int getProperty(String key, int defaultValue)
  {
    String str = getProperty( key);
    if( str == null)
      return defaultValue;
    Integer iObj = new Integer( str);
    return iObj.intValue();
  }
  
  public int setProperty(String key, int value)
  {
    Integer integer = new Integer( value);
    setProperty( key, integer.toString());
    return value;
  }

  public Object getObjectProperty( String key)
  {
    Object obj = null;
    try
    {
      String str = getProperty( key );
      if( str == null )
        return null;
      haui.io.StringBufferInputStream sbis = new haui.io.StringBufferInputStream( str, "ISO-8859-1");
      ObjectInputStream ois = new ObjectInputStream( sbis );
      //ByteArrayInputStream bais = new ByteArrayInputStream( str.getBytes("US-ASCII"));
      //ObjectInputStream ois = new ObjectInputStream( bais );
      obj = ois.readObject();
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    return obj;
  }

  public void setObjectProperty( String key, Object value)
  {
    try
    {
      StringBufferOutputStream sbos = new StringBufferOutputStream( "ISO-8859-1");
      ObjectOutputStream oos = new ObjectOutputStream( sbos );
      //ByteArrayOutputStream baos = new ByteArrayOutputStream();
      //ObjectOutputStream oos = new ObjectOutputStream( baos );
      oos.writeObject( value);
      oos.flush();
      oos.close();
      String str = sbos.toString();
      setProperty( key, str);
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public boolean store( String file, String header)
  {
    boolean blRet = false;
    try
    {
      BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( file));

      super.store( bos, header);
      bos.close();
      blRet = true;
    }
    catch( FileNotFoundException fnfex)
    {
      fnfex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    return blRet;
  }

  public boolean load( String file)
  {
    boolean blRet = false;
    try
    {
      BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file));

      super.load( bis);
      bis.close();
      blRet = true;
    }
    catch( FileNotFoundException fnfex)
    {
      fnfex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    return blRet;
  }
}
