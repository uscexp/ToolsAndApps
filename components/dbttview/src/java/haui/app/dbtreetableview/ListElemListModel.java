package haui.app.dbtreetableview;

import javax.swing.DefaultListModel;

/**
 *		Module:					ListElemListModel.java<br>
 *										$Source: $
 *<p>
 *		Description:    ListModel for ListElems.<br>
 *</p><p>
 *		Created:				26.09.2002	by	AE
 *</p><p>
 *		@history				26.09.2002	by	AE: Created.<br>
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
public class ListElemListModel
  extends DefaultListModel
{
  public boolean contains( Object elem)
  {
    boolean blRet = false;
    ListElem liElem = (ListElem)elem;

    for( int i = 0; i < size(); i++)
    {
      blRet = ((ListElem)elementAt( i)).equals( liElem.getSqlStatement());
      if( blRet)
        break;
    }
    return blRet;
  }
}

