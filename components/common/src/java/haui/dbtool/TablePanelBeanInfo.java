
//Titel:ToolKit
//Version:
//Copyright:Copyright (c) 1998,1999
//Autor:Andreas Eisenhauer
//Organisation:M.o.E
//Beschreibung:TablePanel Bean.

package haui.dbtool;

import java.beans.*;

/**
 *
 *		Module:					TablePanelBeanInfo.java
 *<p>
 *		Description:		Database TablePanel BeanInfo class.
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
public class TablePanelBeanInfo extends SimpleBeanInfo
{
	Class beanClass = TablePanel.class;
	String iconColor16x16Filename = "TablePanel16.gif";
	String iconColor32x32Filename = "TablePanel32.gif";
	String iconMono16x16Filename = "TablePanel16.gif";
	String iconMono32x32Filename = "TablePanel32.gif";

	
	public TablePanelBeanInfo()
	{
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try 
		{
			PropertyDescriptor _accessibleContext = new PropertyDescriptor("accessibleContext", beanClass, "getAccessibleContext", null);
			
			PropertyDescriptor _autoCreateColumnsFromModel = new PropertyDescriptor("autoCreateColumnsFromModel", beanClass, "getAutoCreateColumnsFromModel", "setAutoCreateColumnsFromModel");
			
			PropertyDescriptor _autoResizeMode = new PropertyDescriptor("autoResizeMode", beanClass, "getAutoResizeMode", "setAutoResizeMode");
			
			PropertyDescriptor _cellSelectionEnabled = new PropertyDescriptor("cellSelectionEnabled", beanClass, "getCellSelectionEnabled", "setCellSelectionEnabled");
			
			PropertyDescriptor _colMinWidth = new PropertyDescriptor("colMinWidth", beanClass, null, "setColMinWidth");
			
			PropertyDescriptor _columnSelectionAllowed = new PropertyDescriptor("columnSelectionAllowed", beanClass, "getColumnSelectionAllowed", "setColumnSelectionAllowed");
			
			PropertyDescriptor _dateFormat = new PropertyDescriptor("dateFormat", beanClass, "getDateFormat", "setDateFormat");
			
			PropertyDescriptor _dbg = new PropertyDescriptor("dbg", beanClass, "getDbg", "setDbg");
			
			PropertyDescriptor _driver = new PropertyDescriptor("driver", beanClass, "getDriver", "setDriver");
			
			PropertyDescriptor _editable = new PropertyDescriptor("editable", beanClass, "isEditable", "setEditable");
			
			PropertyDescriptor _editing = new PropertyDescriptor("editing", beanClass, "isEditing", null);
			
			PropertyDescriptor _errStream = new PropertyDescriptor("errStream", beanClass, "getErrStream", "setErrStream");
			
			PropertyDescriptor _extDbg = new PropertyDescriptor("extDbg", beanClass, "getExtDbg", "setExtDbg");
			
			PropertyDescriptor _gridColor = new PropertyDescriptor("gridColor", beanClass, "getGridColor", "setGridColor");
			
			PropertyDescriptor _intercellSpacing = new PropertyDescriptor("intercellSpacing", beanClass, "getIntercellSpacing", "setIntercellSpacing");
			
			PropertyDescriptor _logMode = new PropertyDescriptor("logMode", beanClass, null, "setLogMode");
			
			PropertyDescriptor _opaque = new PropertyDescriptor("opaque", beanClass, "isOpaque", null);
			
			PropertyDescriptor _outStream = new PropertyDescriptor("outStream", beanClass, "getOutStream", "setOutStream");
			
			PropertyDescriptor _pwd = new PropertyDescriptor("pwd", beanClass, "getPwd", "setPwd");
			
			PropertyDescriptor _query = new PropertyDescriptor("query", beanClass, "getQuery", "setQuery");
			
			PropertyDescriptor _rowHeight = new PropertyDescriptor("rowHeight", beanClass, "getRowHeight", "setRowHeight");
			
			PropertyDescriptor _rowSelectionAllowed = new PropertyDescriptor("rowSelectionAllowed", beanClass, "getRowSelectionAllowed", "setRowSelectionAllowed");
			
			PropertyDescriptor _selectionBackground = new PropertyDescriptor("selectionBackground", beanClass, "getSelectionBackground", "setSelectionBackground");
			
			PropertyDescriptor _selectionForeground = new PropertyDescriptor("selectionForeground", beanClass, "getSelectionForeground", "setSelectionForeground");
			
			PropertyDescriptor _selectionMode = new PropertyDescriptor("selectionMode", beanClass, null, "setSelectionMode");
			
			PropertyDescriptor _showGrid = new PropertyDescriptor("showGrid", beanClass, null, "setShowGrid");
			
			PropertyDescriptor _showHorizontalLines = new PropertyDescriptor("showHorizontalLines", beanClass, "getShowHorizontalLines", "setShowHorizontalLines");
			
			PropertyDescriptor _showVerticalLines = new PropertyDescriptor("showVerticalLines", beanClass, "getShowVerticalLines", "setShowVerticalLines");
			
			PropertyDescriptor _table = new PropertyDescriptor("table", beanClass, "getTable", null);
			
			PropertyDescriptor _tableAdapter = new PropertyDescriptor("tableAdapter", beanClass, "getTableAdapter", null);
			
			PropertyDescriptor _tableName = new PropertyDescriptor("tableName", beanClass, "getTableName", "setTableName");
			
			PropertyDescriptor _toolTipText = new PropertyDescriptor("toolTipText", beanClass, null, "setToolTipText");
			
			PropertyDescriptor _UI = new PropertyDescriptor("UI", beanClass, "getUI", "setUI");
			
			PropertyDescriptor _UIClassID = new PropertyDescriptor("UIClassID", beanClass, "getUIClassID", null);
			
			PropertyDescriptor _url = new PropertyDescriptor("url", beanClass, "getUrl", "setUrl");
			
			PropertyDescriptor _usr = new PropertyDescriptor("usr", beanClass, "getUsr", "setUsr");
			
			PropertyDescriptor[] pds = new PropertyDescriptor[] {
			  _accessibleContext,
			  _autoCreateColumnsFromModel,
			  _autoResizeMode,
			  _cellSelectionEnabled,
			  _colMinWidth,
			  _columnSelectionAllowed,
			  _dateFormat,
			  _dbg,
			  _driver,
			  _editable,
			  _editing,
			  _errStream,
			  _extDbg,
			  _gridColor,
			  _intercellSpacing,
			  _logMode,
			  _opaque,
			  _outStream,
			  _pwd,
			  _query,
			  _rowHeight,
			  _rowSelectionAllowed,
			  _selectionBackground,
			  _selectionForeground,
			  _selectionMode,
			  _showGrid,
			  _showHorizontalLines,
			  _showVerticalLines,
			  _table,
			  _tableAdapter,
			  _tableName,
			  _toolTipText,
			  _UI,
			  _UIClassID,
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
}






