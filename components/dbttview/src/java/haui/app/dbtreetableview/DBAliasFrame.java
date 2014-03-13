package haui.app.dbtreetableview;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.Array;

/**
 *		Module:					DBAliasFrame.java<br>
 *										$Source: $
 *<p>
 *		Description:    InternalFrame for DBDrivers.<br>
 *</p><p>
 *		Created:				23.09.2002	by	AE
 *</p><p>
 *		@history				23.09.2002	by	AE: Created.<br>
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
public class DBAliasFrame
  extends BaseToolFrame
{
  // Constants
  public static final String DBA = "DbAlias";

  // member variables
  DBDriverFrame m_dbdf;
  DbTtvDesktop m_dbttvdp;

  // GUI member variables
  JComboBox m_jComboBoxAlias;

  public DBAliasFrame( DBDriverFrame dbdf, DbTtvDesktop dbttvdp)
  {
    super( DBA);
    m_dbdf = dbdf;
    m_dbttvdp = dbttvdp;
    DBAlias[] dbas = DBAlias.loadDBAliases();
    if( dbas != null)
    {
      setArray( dbas);
    }
    if( listSize() > 0)
      setSelectedIndex( 0);

    // init combobox
    m_jComboBoxAlias = new JComboBox();
    updateAliasComboBox();
    m_jComboBoxAlias.setPreferredSize( new Dimension( 180, 25));
    m_jComboBoxAlias.setMaximumSize( new Dimension( 180, 25));
    LisItem itemlistener = new LisItem();
    m_jComboBoxAlias.addItemListener( itemlistener);

    int iPosY = 200;
    if( m_dbdf != null)
      iPosY = m_dbdf.getHeight();
    setLocation( 0, iPosY);
  }

  public JComboBox getAliasComboBox()
  {
    return m_jComboBoxAlias;
  }

  public void updateAliasComboBox()
  {
    int iSize = listSize();
    m_jComboBoxAlias.removeAllItems();
    m_jComboBoxAlias.addItem( "Connect To...");
    if( iSize > 0)
    {
      for( int i = 0; i < iSize; ++i)
      {
        m_jComboBoxAlias.addItem( ((DBAlias)elementAt( i)).getAliasName());
      }
    }
  }

  public Object onAdd()
  {
    DBAliasDialog dlg = new DBAliasDialog( this, null, m_dbdf);
    dlg.setVisible( true);
    if( !dlg.isCancelled())
    {
      addElement( dlg.getAlias());
      updateAliasComboBox();
    }
    return dlg.getAlias();
  }

  public void onEdit()
  {
    int iSel = getSelectedIndex();
    if( iSel > -1)
    {
      DBAliasDialog dlg = new DBAliasDialog( this, (DBAlias)elementAt( iSel), m_dbdf);
      dlg.setVisible( true);
      if( !dlg.isCancelled())
      {
        setElementAt( dlg.getAlias(), iSel);
      }
    }
  }

  public void onDelete()
  {
    int iSel = getSelectedIndex();
    if( iSel > -1)
    {
      int iRet;
      iRet = JOptionPane.showConfirmDialog( this, "Really delete " + ((DBAlias)elementAt( iSel)).getAliasName() + "?",
        "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if( iRet == JOptionPane.YES_OPTION)
      {
        removeElementAt( iSel);
        updateAliasComboBox();
      }
    }
  }

  public void onCopy()
  {
    int iSel = getSelectedIndex();
    if( iSel > -1)
    {
      DBAliasDialog dlg = new DBAliasDialog( this, (DBAlias)((DBAlias)elementAt( iSel)).clone(), m_dbdf);
      dlg.setVisible( true);
      if( !dlg.isCancelled())
      {
        addElement( dlg.getAlias());
        updateAliasComboBox();
      }
    }
  }

  public void onConnect()
  {
    if( m_dbttvdp != null)
    {
      m_dbttvdp.createFrame( (DBAlias)elementAt( getSelectedIndex()));
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
    DBAlias dbas[] = new DBAlias[ iLen];
    for( int i = 0; i < iLen; ++i)
    {
      dbas[i] = (DBAlias)elementAt( i);
    }
    if( dbas != null && iLen > 0)
      DBAlias.saveDBAliases( dbas);
  }

  void onLeftMouseDoublePressed( MouseEvent event)
  {
    onConnect();
  }

  class LisItem implements ItemListener
  {
    public void itemStateChanged( ItemEvent event)
    {
      Object obj = event.getSource();
      if( obj == m_jComboBoxAlias)
      {
        int iSel = m_jComboBoxAlias.getSelectedIndex()-1;
        if( iSel > -1)
        {
          m_jComboBoxAlias.setPopupVisible(false);
          setSelectedIndex( iSel);
          onConnect();
          m_jComboBoxAlias.setSelectedIndex( 0);
        }
      }
    }
  }
}
