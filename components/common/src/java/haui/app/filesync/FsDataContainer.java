package haui.app.filesync;

import haui.io.FileInterface.FileInterface;
import haui.resource.ResouceManager;
import javax.swing.Icon;

/**
 * Module:      FsDataContainer.java<br> $Source: $ <p> Description: FsDataContainer for filesync package.<br> </p><p> Created:     09.12.2004  by AE </p><p>
 * @history      09.12.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.3  </p>
 */
public class FsDataContainer
{
  public FileInterface fiLeft;
  public FileInterface fiRight;
  public String strCompare;
  public Icon iconCompare;
  private String m_strAppName;
  public boolean blDuplicate;

  public FsDataContainer( FileInterface fiLeft, FileInterface fiRight, String strCompare, String strAppName)
  {
    m_strAppName = strAppName;
    this.fiLeft = fiLeft;
    this.fiRight = fiRight;
    this.strCompare = strCompare;
    if( strCompare.equals( FileSyncTableModel.EQ))
    {
      this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "equal.gif" );
    }
    else if( strCompare.equals( FileSyncTableModel.NOTEQ))
    {
      this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "notequal.gif" );
    }
    else if( strCompare.equals( FileSyncTableModel.GE))
    {
      this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "toright.gif" );
    }
    else if( strCompare.equals( FileSyncTableModel.LE))
    {
      this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "toleft.gif" );
    }
  }

  public void setEqual()
  {
    strCompare = FileSyncTableModel.EQ;
    this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "equal.gif" );
  }

  public void change()
  {
    if( strCompare.equals( FileSyncTableModel.EQ))
    {
      strCompare = FileSyncTableModel.NOTEQ;
      this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "notequal.gif" );
    }
    else if( strCompare.equals( FileSyncTableModel.NOTEQ))
    {
      strCompare = FileSyncTableModel.GE;
      this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "toright.gif" );
    }
    else if( strCompare.equals( FileSyncTableModel.GE))
    {
      strCompare = FileSyncTableModel.LE;
      this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "toleft.gif" );
    }
    else if( strCompare.equals( FileSyncTableModel.LE))
    {
      strCompare = FileSyncTableModel.EQ;
      this.iconCompare = ResouceManager.getCommonImageIcon( m_strAppName, "equal.gif" );
    }
  }
}