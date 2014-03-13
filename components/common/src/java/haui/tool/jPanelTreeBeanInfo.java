
package haui.tool;

import java.beans.*;

/**
 *
 *		Module:					jPanelTreeBeanInfo.java
 *<p>
 *		Description:		BeanInfo for jPanelTree.
 *</p><p>
 *		Created:				08.10.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v0.1, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class jPanelTreeBeanInfo extends SimpleBeanInfo
{
	Class beanClass = jPanelTree.class;
	String iconColor16x16Filename = "jPanelTree16.gif";
	String iconColor32x32Filename = "jPanelTree32.gif";
	String iconMono16x16Filename = "jPanelTree16.gif";
	String iconMono32x32Filename = "jPanelTree32.gif";

	
	public jPanelTreeBeanInfo()
	{
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try 
		{
			PropertyDescriptor _label = new PropertyDescriptor("label", beanClass, null, "setLabel");
			
			PropertyDescriptor _rootLabel = new PropertyDescriptor("rootLabel", beanClass, null, "setRootLabel");
			
			PropertyDescriptor _selectedNode = new PropertyDescriptor("selectedNode", beanClass, "getSelectedNode", null);
			
			PropertyDescriptor _userObject = new PropertyDescriptor("userObject", beanClass, "getUserObject", null);
			
			PropertyDescriptor _userObjectPath = new PropertyDescriptor("userObjectPath", beanClass, "getUserObjectPath", null);
			
			PropertyDescriptor[] pds = new PropertyDescriptor[] {
			  _label,
			  _rootLabel,
			  _selectedNode,
			  _userObject,
			  _userObjectPath,
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





