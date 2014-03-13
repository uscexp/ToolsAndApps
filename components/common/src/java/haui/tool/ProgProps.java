
package haui.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 *		Module:					ProgProps.java
 *<p>
 *		Description:		Propertyfile reader and writer.
 *</p><p>
 *		Created:				03.09.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					03.09.1998 - 04.09.1998	by	AE:	Create progProps class<br>
 *										15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ProgProps
{
	public static int ERRORINDEX = -1;
	public static int ERRORPROPNAME = 1;
	public static int ERRORFILE = 2;
	public static int ERROROVERFLOW = 3;
	public static int OK = 0;
	String m_strPropertyNames[];
	String m_strProperties[];
	String m_strSeperator;
	int m_iPropCount;
	int m_iCurCount;
	
	/**
	Constructor for the class progProps.

	@param iCount:				Max count of properties.
	@param strSeperator:	Seperator between the property name and the property value in the property file.
	*/
	public ProgProps( int iCount, String strSeperator)
	{
		m_iPropCount = iCount;
		m_iCurCount = 0;
		m_strSeperator = strSeperator;
		m_strPropertyNames = new String[ m_iPropCount];
		m_strProperties = new String[ m_iPropCount];
	}
	
	/**
	Get the current property count.

	@return		current property count.
	*/
	public int getCurCount()
	{
		return m_iCurCount;
	}
	
	/**
	Get the max property count.

	@return		max property count.
	*/
	public int getPropCount()
	{
		return m_iPropCount;
	}
	
	/**
	Get property index by property name.

	@param strPropertyName:		Name of the property.

	@return		index of the property.
	*/
	public int getPropertyIndex( String strPropertyName)
	{
		int i;
		int iRet = ERRORINDEX;
		
		for( i = 0; i < m_iCurCount; i++)
		{
			if( strPropertyName.equals( m_strPropertyNames[i]))
			{
				iRet = i;
				break;
			}
		}
		return iRet;
	}
	
	/**
	Add a property name.

	@param strPropertyName:		Name of the property.

	@return		Error code.
	*/
	public int addPropertyName( String strPropertyName)
	{
		int iRet = ERROROVERFLOW;
		if( m_iCurCount <= m_iPropCount)
		{
			m_strPropertyNames[m_iCurCount] = strPropertyName;
			m_iCurCount++;
			iRet = OK;
		}
		return iRet;
	}
	
	/**
	Set a property value.

	@param strPropertyName:		Name of the property.
	@param strPropertyValue:	Value of the property.

	@return		Error code.
	*/
	public int setPropertyValue( String strPropertyName, String strPropertyValue)
	{
		int iIdx;
		int iRet = ERRORPROPNAME;
		
		if( (iIdx = getPropertyIndex( strPropertyName)) != -1)
		{
			m_strProperties[ iIdx] = strPropertyValue;
			iRet = OK;
		}
		return iRet;
	}
	
	/**
	Get a property value.

	@param strPropertyName:		Name of the property.

	@return		Value of the property.
	*/
	public String getPropertyValue( String strPropertyName)
	{
		int iIdx;
		String strRet = null;
		
		if( (iIdx = getPropertyIndex( strPropertyName)) != -1)
		{
			strRet = m_strProperties[ iIdx];
		}
		return strRet;
	}
	
	/**
	Read properties from file.

	@param strFileName:		Name of the property file.
	@param blSetPns:			Set property names flag (if true will set the property names).

	@return		Error code.
	*/
	public int readPropertyFile( String strFileName, boolean blSetPns)
	{
		int iRet = ERRORFILE;
		BufferedReader brProp;
		String strImp = "";
		String strPropName = null;
		String strPropValue = null;
		
		try
		{
			brProp = new BufferedReader( new FileReader( strFileName));
			while((strImp = brProp.readLine()) != null)
			{
				StringTokenizer strtok;
				strPropName = null;
				strPropValue = null;
				
				strtok = new StringTokenizer( strImp, m_strSeperator, false);
				
				if( strtok.hasMoreTokens())
				{
					strPropName = strtok.nextToken();
				}
				if( strtok.hasMoreTokens())
				{
					strPropValue = strtok.nextToken();
				}
				if( blSetPns && strPropName != null)
				{
					addPropertyName( strPropName);
				}
				if( strPropName != null && strPropValue != null)
					setPropertyValue( strPropName, strPropValue);
			}
			brProp.close();
			iRet = OK;
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found!");
		}
		catch(IOException e)
		{
			System.out.println("File read error!");
		}
		
		return iRet;
	}
	
	/**
	Save properties to file.

	@param strFileName:		Name of the property file.

	@return		Error code.
	*/
	public int savePropertyFile( String strFileName)
	{
		int i;
		int iRet = ERRORFILE;
		BufferedWriter bwProp;
		String strBuf = null;
		
		try
		{
			bwProp = new BufferedWriter( new FileWriter( strFileName));
			for( i = 0; i < m_iCurCount; i++)
			{
				if( m_strPropertyNames[i] != null && m_strProperties[i] != null)
					strBuf = m_strPropertyNames[i] + m_strSeperator + m_strProperties[i];
				else
				{
					if( m_strPropertyNames[i] != null)
						strBuf = m_strPropertyNames[i] + m_strSeperator;
				}
				if( strBuf != null)
					bwProp.write( strBuf);
				bwProp.newLine();
			}
			bwProp.close();
			iRet = OK;
		}
		catch(IOException e)
		{
			System.out.println("File write error!");
		}
		
		return iRet;
	}
}
