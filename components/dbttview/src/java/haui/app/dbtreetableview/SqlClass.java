package haui.app.dbtreetableview;

import javax.swing.*;
import java.awt.Component;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *		Module:					SqlClass.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\SqlClass.java,v $
 *<p>
 *		Description:    Handels Sql parts.<br>
 *</p><p>
 *		Created:				22.08.2001	by	AE
 *</p><p>
 *		@history				22.08.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: SqlClass.java,v $
 *		Revision 1.1  2004-02-17 15:56:08+01  t026843
 *		handels now submenus
 *
 *		Revision 1.0  2003-05-21 16:25:35+02  t026843
 *		Initial revision
 *
 *		Revision 1.0  2002-09-27 15:49:33+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2001; $Revision: 1.1 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\SqlClass.java,v 1.1 2004-02-17 15:56:08+01 t026843 Exp $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class SqlClass
extends Object
{
  // constants
  public final static String COLUMNNAME = "%c";
  public final static String TABLENAME = "%t";
  public final static String SCHEMANAME = "%s";
  public final static String PARAMDLG = "%?";
  public final static String PERCENT = "%%";
  public final static String NEWLINE = "\\n";
  public final static String TAB = "\\t";

  // member variables
  String m_strIdentifier;
  String m_strSubMenu;
  String m_strSql;
  Component m_comp;

  public SqlClass()
  {
    this( null, null, null, null);
  }

  public SqlClass( String strIdentifier, String strSubMenu, String strSql, Component comp)
  {
    m_strIdentifier = strIdentifier;
    m_strSubMenu = strSubMenu;
    m_strSql = strSql;
    m_comp = comp;
  }

  public void setComponent( Component comp)
  {
    m_comp = comp;
  }

  public Component getComponent()
  {
    return m_comp;
  }

  public void setIdentifier( String strIdentifier)
  {
    m_strIdentifier = strIdentifier;
  }

  public String getIdentifier()
  {
    return m_strIdentifier;
  }

  public void setSubMenu( String strSubMenu)
  {
    m_strSubMenu = strSubMenu;
  }

  public String getSubMenu()
  {
    return m_strSubMenu;
  }

  public void setSql( String strSql)
  {
    m_strSql = strSql;
  }

  public String getSql()
  {
    return m_strSql;
  }

  static public SqlClass searchSql( Vector vSqls, String strName)
  {
    int i;
    SqlClass sql = null;

    for( i = 0; i < vSqls.size(); i++)
    {
      sql = (SqlClass)vSqls.elementAt( i);
      if( strName.toLowerCase().endsWith( sql.getIdentifier().toLowerCase()))
        break;
      else
        sql = null;
    }
    return sql;
  }

  public String getCompleteSql( String strCol, String strTab, String strSchema)
  {
    return parseParams( strCol, strTab, strSchema);
  }

  private String parseParams( String strCol, String strTab, String strSchema)
  {
    String strRet = m_strSql;
    int iIdx = -1;
    int iCont = 0;

    while( ( iIdx = strRet.indexOf( COLUMNNAME)) != -1)
    {
      if( strCol == null)
        strRet = strRet.substring( 0, iIdx) + "?" + strRet.substring( iIdx+2);
      else
      {
        String str = strRet.substring( 0, iIdx);
        strRet = strRet.substring( 0, iIdx) + strCol + strRet.substring( iIdx+2);
      }
    }
    while( ( iIdx = strRet.indexOf( TABLENAME)) != -1)
    {
      if( strTab == null)
        strRet = strRet.substring( 0, iIdx) + strRet.substring( iIdx+2);
      else
      {
        String str = strRet.substring( 0, iIdx);
        strRet = strRet.substring( 0, iIdx) + strTab + strRet.substring( iIdx+2);
      }
    }
    while( ( iIdx = strRet.indexOf( SCHEMANAME)) != -1)
    {
      if( strSchema == null)
        strRet = strRet.substring( 0, iIdx) + strRet.substring( iIdx+2);
      else
      {
        String str = strRet.substring( 0, iIdx);
        strRet = strRet.substring( 0, iIdx) + strSchema + strRet.substring( iIdx+2);
      }
    }
    while( ( iIdx = strRet.indexOf( PARAMDLG, iCont)) != -1)
    {
      if( iIdx > 0 && strRet.charAt( iIdx-1) == '%')
      {
        strRet = strRet.substring( 0, iIdx-1) + "%" + strRet.substring( iIdx+1);
        iCont = iIdx+1;
      }
      else
      {
        String str = JOptionPane.showInputDialog( m_comp, "Sql parameter?", "Parameter input",
                                                  JOptionPane.QUESTION_MESSAGE );
        if( str == null )
          str = "";
        strRet = strRet.substring( 0, iIdx ) + str + strRet.substring( iIdx + 2 );
      }
    }
    while( ( iIdx = strRet.indexOf( PERCENT)) != -1)
    {
      strRet = strRet.substring( 0, iIdx) + "%" + strRet.substring( iIdx+2);
    }
    while( ( iIdx = strRet.indexOf( NEWLINE)) != -1)
    {
      strRet = strRet.substring( 0, iIdx) + "\n" + strRet.substring( iIdx+2);
    }
    while( ( iIdx = strRet.indexOf( TAB)) != -1)
    {
      strRet = strRet.substring( 0, iIdx) + "\t" + strRet.substring( iIdx+2);
    }
    return strRet;
  }

  public boolean equals( Object obj)
  {
    return m_strIdentifier.equals( ((SqlClass)obj).getIdentifier());
  }

  public String toString()
  {
    String strSubMenu = m_strSubMenu;
    if( strSubMenu == null)
      strSubMenu = "";
    return strSubMenu + ", " + m_strIdentifier + ", " + m_strSql;
  }

  public Object clone()
  {
    SqlClass cmd = new SqlClass( m_strIdentifier, m_strSubMenu, m_strSql, m_comp);
    return cmd;
  }

  public void save( BufferedWriter bw)
  throws IOException
  {
    bw.write( getIdentifier());
    bw.newLine();
    bw.write( getSql());
    bw.newLine();
  }

  public boolean read( BufferedReader br)
  throws IOException
  {
    String str = br.readLine();
    if( str == null)
      return false;
    setIdentifier( str);
    str = br.readLine();
    if( str == null)
      return false;
    setSql( br.readLine());
    return true;
  }
}