package haui.app.dbtreetableview;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.Array;

/**
 *		Module:					DBDriverFrame.java<br>
 *										$Source: $
 *<p>
 *		Description:    InternalFrame for DBDrivers.<br>
 *</p><p>
 *		Created:				20.09.2002	by	AE
 *</p><p>
 *		@history				20.09.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DBDriverFrame
  extends BaseToolFrame
{
  // Constants
  public static final String DBD = "DbDriver";

  // member variables

  // GUI member variables

  public DBDriverFrame()
  {
    super( DBD);
    m_jButtonConnect.setVisible( false);
    DBDriver[] dbds = DBDriver.loadDBDrivers();
    if( dbds != null)
    {
      setArray( dbds);
    }
    if( listSize() > 0)
      setSelectedIndex( 0);
    setLocation( 0, 0);
  }

  public DBDriver findDBDriver( String strDriverName)
  {
    DBDriver dbdRet = null;
    DBDriver dbd = null;
    int iLen = listSize();
    for( int i = 0; i < iLen; ++i)
    {
      dbd = (DBDriver)elementAt( i);
      if( dbd.getDriverName().equals( strDriverName))
      {
        dbdRet = dbd;
        break;
      }
    }
    return dbdRet;
  }

  public Object onAdd()
  {
    DBDriverDialog dlg = new DBDriverDialog( this, null);
    dlg.setVisible( true);
    if( !dlg.isCancelled())
    {
      addElement( dlg.getDriver());
    }
    return dlg.getDriver();
  }

  public void onEdit()
  {
    int iSel = getSelectedIndex();
    if( iSel > -1)
    {
      DBDriverDialog dlg = new DBDriverDialog( this, (DBDriver)elementAt( iSel));
      dlg.setVisible( true);
      if( !dlg.isCancelled())
      {
        setElementAt( dlg.getDriver(), iSel);
      }
    }
  }

  public void onDelete()
  {
    int iSel = getSelectedIndex();
    if( iSel > -1)
    {
      int iRet;
      iRet = JOptionPane.showConfirmDialog( this, "Really delete " + ((DBDriver)elementAt( iSel)).getDriverName() + "?",
        "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if( iRet == JOptionPane.YES_OPTION)
      {
        removeElementAt( iSel);
      }
    }
  }

  public void onCopy()
  {
    int iSel = getSelectedIndex();
    if( iSel > -1)
    {
      DBDriverDialog dlg = new DBDriverDialog( this, (DBDriver)((DBDriver)elementAt( iSel)).clone());
      dlg.setVisible( true);
      if( !dlg.isCancelled())
      {
        addElement( dlg.getDriver());
      }
    }
  }

  public void onExit()
  {
    _save();
    super.onExit();
  }

  public void _save()
  {
    int iLen = listSize();
    DBDriver dbds[] = new DBDriver[ iLen];
    for( int i = 0; i < iLen; ++i)
    {
      dbds[i] = (DBDriver)elementAt( i);
    }
    if( dbds != null && iLen > 0)
      DBDriver.saveDBDrivers( dbds);
  }
}
