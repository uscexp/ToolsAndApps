
package haui.tool;

import java.beans.*;

/**
 *
 *		Module:					SplashDlgBeanInfo.java
 *<p>
 *		Description:		Splash window BeanInfo class.
 *</p><p>
 *		Created:				16.11.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998.1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class SplashDlgBeanInfo extends SimpleBeanInfo
{
	Class beanClass = SplashDlg.class;
	String iconColor16x16Filename = "SplashDlg16.gif";
	String iconColor32x32Filename = "SplashDlg32.gif";
	String iconMono16x16Filename = "SplashDlg16.gif";
	String iconMono32x32Filename = "SplashDlg32.gif";

	
	public SplashDlgBeanInfo()
	{
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try 
		{
			PropertyDescriptor _headFont = new PropertyDescriptor("headFont", beanClass, null, "setHeadFont");
			
			PropertyDescriptor _textFont = new PropertyDescriptor("textFont", beanClass, null, "setTextFont");
			
			PropertyDescriptor _icon = new PropertyDescriptor("icon", beanClass, null, "setIcon");
			
			PropertyDescriptor _head = new PropertyDescriptor("head", beanClass, null, "setHead");
			
			PropertyDescriptor _text = new PropertyDescriptor("text", beanClass, null, "setText");
			
			PropertyDescriptor[] pds = new PropertyDescriptor[] {
			  _headFont,
			  _textFont,
			  _icon,
			  _head,
			  _text,
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

 
