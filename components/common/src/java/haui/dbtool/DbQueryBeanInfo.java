
//Titel:ToolKit
//Version:0.1
//Copyright:Copyright (c) 1998,1999
//Autor:Andreas Eisenhauer
//Organisation:M.o.E
//Beschreibung:DbQuery Bean.

package haui.dbtool;

import java.beans.*;

/**
 *
 *		Module:					DbQueryBeanInfo.java
 *<p>
 *		Description:		Beaninfo class for DbQuery class.
 *</p><p>
 *		Created:				??.??.1998	by	AE
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
public class DbQueryBeanInfo extends SimpleBeanInfo
{
	Class beanClass = DbQuery.class;
	String iconColor16x16Filename = "Query16.gif";
	String iconColor32x32Filename = "Query32.gif";
	String iconMono16x16Filename = "Query16.gif";
	String iconMono32x32Filename = "Query32.gif";

	
	public DbQueryBeanInfo()
	{
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try 
		{
			PropertyDescriptor _closed = new PropertyDescriptor("closed", beanClass, "isClosed", null);
			
			PropertyDescriptor _dateFormat = new PropertyDescriptor("dateFormat", beanClass, null, "setDateFormat");
			
			PropertyDescriptor _debugMode = new PropertyDescriptor("debugMode", beanClass, null, "setDebugMode");
			
			PropertyDescriptor _errStream = new PropertyDescriptor("errStream", beanClass, "getErrStream", "setErrStream");
			
			PropertyDescriptor _extDebugMode = new PropertyDescriptor("extDebugMode", beanClass, null, "setExtDebugMode");
			
			PropertyDescriptor _logMode = new PropertyDescriptor("logMode", beanClass, null, "setLogMode");
			
			PropertyDescriptor _nrOfRs = new PropertyDescriptor("nrOfRs", beanClass, "getNrOfRs", "setNrOfRs");
			
			PropertyDescriptor _outStream = new PropertyDescriptor("outStream", beanClass, "getOutStream", "setOutStream");
			
			PropertyDescriptor[] pds = new PropertyDescriptor[] {
			  _closed,
			  _dateFormat,
			  _debugMode,
			  _errStream,
			  _extDebugMode,
			  _logMode,
			  _nrOfRs,
			  _outStream,
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
}



 
