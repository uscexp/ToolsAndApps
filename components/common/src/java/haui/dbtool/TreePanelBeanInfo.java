
//Titel:ToolKit
//Version:0.1
//Copyright:Copyright (c) 1998,1999
//Autor:Andreas Eisenhauer
//Organisation:M.o.E
//Beschreibung:TreePanel Bean.

package haui.dbtool;

import java.beans.*;

/**
 *
 *		Module:					TreePanelBeanInfo.java
 *<p>
 *		Description:		BeanInfo class for the database TreePanel class.
 *</p><p>
 *		Created:				06.07.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class TreePanelBeanInfo extends SimpleBeanInfo
{
	Class beanClass = TreePanel.class;
	String iconColor16x16Filename = "TreePanel16.gif";
	String iconColor32x32Filename = "TreePanel32.gif";
	String iconMono16x16Filename = "TreePanel16.gif";
	String iconMono32x32Filename = "TreePanel32.gif";

	
	public TreePanelBeanInfo()
	{
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try 
		{
			PropertyDescriptor _aboutVisible = new PropertyDescriptor("aboutVisible", beanClass, null, "setAboutVisible");
			
			PropertyDescriptor _connectVisible = new PropertyDescriptor("connectVisible", beanClass, null, "setConnectVisible");
			
			PropertyDescriptor _dbg = new PropertyDescriptor("dbg", beanClass, "getDbg", "setDbg");
			
			PropertyDescriptor _driver = new PropertyDescriptor("driver", beanClass, "getDriver", "setDriver");
			
			PropertyDescriptor _errStream = new PropertyDescriptor("errStream", beanClass, "getErrStream", "setErrStream");
			
			PropertyDescriptor _extDbg = new PropertyDescriptor("extDbg", beanClass, "getExtDbg", "setExtDbg");
			
			PropertyDescriptor _outStream = new PropertyDescriptor("outStream", beanClass, "getOutStream", "setOutStream");
			
			PropertyDescriptor _pwd = new PropertyDescriptor("pwd", beanClass, "getPwd", "setPwd");
			
			PropertyDescriptor _selectStatement = new PropertyDescriptor("selectStatement", beanClass, "getSelectStatement", null);
			
			PropertyDescriptor _tableName = new PropertyDescriptor("tableName", beanClass, "getTableName", null);
			
			PropertyDescriptor _toolBarVisible = new PropertyDescriptor("toolBarVisible", beanClass, null, "setToolBarVisible");
			
			PropertyDescriptor _url = new PropertyDescriptor("url", beanClass, "getUrl", "setUrl");
			
			PropertyDescriptor _usr = new PropertyDescriptor("usr", beanClass, "getUsr", "setUsr");
			
			PropertyDescriptor[] pds = new PropertyDescriptor[] {
			  _aboutVisible,
			  _connectVisible,
			  _dbg,
			  _driver,
			  _errStream,
			  _extDbg,
			  _outStream,
			  _pwd,
			  _selectStatement,
			  _tableName,
			  _toolBarVisible,
			  _url,
			  _usr,
			};
			return pds;
		}
		catch (IntrospectionException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public java.awt.Image getIcon(int iconKind)
	{
		switch (iconKind) {
		case BeanInfo.ICON_COLOR_16x16:
		  return iconColor16x16Filename != null ? loadImage(iconColor16x16Filename) : null;
		case BeanInfo.ICON_COLOR_32x32:
		  return iconColor32x32Filename != null ? loadImage(iconColor32x32Filename) : null;
		case BeanInfo.ICON_MONO_16x16:
		  return iconMono16x16Filename != null ? loadImage(iconMono16x16Filename) : null;
		case BeanInfo.ICON_MONO_32x32:
		  return iconMono32x32Filename != null ? loadImage(iconMono32x32Filename) : null;
		}
		return null;
	}

	public BeanInfo[] getAdditionalBeanInfo()
	{
		Class superclass = beanClass.getSuperclass();
		try 
		{
			BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
			return new BeanInfo[] { superBeanInfo };
		}
		catch (IntrospectionException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}


