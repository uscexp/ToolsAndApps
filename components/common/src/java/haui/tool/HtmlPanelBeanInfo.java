
//Titel:ToolKit
//Version:0.1
//Copyright:Copyright (c) 1998,1999
//Autor:Andreas Eisenhauer
//Organisation:M.o.E
//Beschreibung:HtmlPanel Bean.

package haui.tool;

import java.beans.*;

/**
 *
 *		Module:					HtmlPanelBeanInfo.java
 *<p>
 *		Description:		BeanInfo for HtmlPanel.
 *</p><p>
 *		Created:				19.11.1998	by	AE
 *</p><p>
 *		Last Modified:	15.09.2000	by	AE
 *</p><p>
 *		history					15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *										15.09.2000	by	AE:	_searchbarVisible added<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999,2000
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class HtmlPanelBeanInfo extends SimpleBeanInfo
{
	Class beanClass = HtmlPanel.class;
	String iconColor16x16Filename = "HtmlPanel16.gif";
	String iconColor32x32Filename = "HtmlPanel32.gif";
	String iconMono16x16Filename = "HtmlPanel16.gif";
	String iconMono32x32Filename = "HtmlPanel32.gif";

	
	public HtmlPanelBeanInfo()
	{
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try 
		{
			PropertyDescriptor _backToolTipText = new PropertyDescriptor("backToolTipText", beanClass, null, "setBackToolTipText");
			
			PropertyDescriptor _homeToolTipText = new PropertyDescriptor("homeToolTipText", beanClass, null, "setHomeToolTipText");
			
			PropertyDescriptor _nextToolTipText = new PropertyDescriptor("nextToolTipText", beanClass, null, "setNextToolTipText");
			
			PropertyDescriptor _pageUrl = new PropertyDescriptor("page", beanClass, "getPage", "setPage");
			
			PropertyDescriptor _pageString = new PropertyDescriptor("page", beanClass, null, "setPage");
			
			PropertyDescriptor _toolbarVisible = new PropertyDescriptor("toolbarVisible", beanClass, null, "setToolbarVisible");
			
			PropertyDescriptor _searchbarVisible = new PropertyDescriptor("searchbarVisible", beanClass, null, "setSearchbarVisible");

			PropertyDescriptor[] pds = new PropertyDescriptor[] {
			  _backToolTipText,
			  _homeToolTipText,
			  _nextToolTipText,
			  _pageUrl,
			  _pageString,
			  _toolbarVisible,
			  _searchbarVisible,
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


 
