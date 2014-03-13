package haui.app.fm;

import haui.io.FileInterface.FileInterface;

import java.util.*;

/**
 *		Module:					FinfoHistory.java<br>
 *										$Source: $
 *<p>
 *		Description:    FileInfo history.<br>
 *</p><p>
 *		Created:				13.06.2002	by	AE
 *</p><p>
 *		@history				13.06.2002	by	AE: Created.<br>
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
public class FinfoHistory
{
  // constants
  public final static int MAXHISTORY = 20;

  // member variables
  private Vector m_vFinfoHistory = new Vector();
  private int m_iHistPos = -1;

  public FinfoHistory()
  {
  }

  public void add( FileInterface finfo)
  {
    ++m_iHistPos;
    if( m_iHistPos >= MAXHISTORY-1 && m_iHistPos <= m_vFinfoHistory.size())
    {
      --m_iHistPos;
      m_vFinfoHistory.removeElementAt( 0);
      m_vFinfoHistory.add( finfo);
    }
    else if( m_iHistPos >= 0 && m_iHistPos < MAXHISTORY-1 && m_iHistPos < m_vFinfoHistory.size())
    {
      m_vFinfoHistory.setElementAt( finfo, m_iHistPos);
    }
    else if( m_iHistPos < MAXHISTORY-1 && m_iHistPos >= m_vFinfoHistory.size())
    {
      m_vFinfoHistory.add( finfo);
    }
  }

  public void update( FileInterface finfo)
  {
    if( m_iHistPos >= 0 && m_iHistPos < MAXHISTORY && m_iHistPos < m_vFinfoHistory.size())
    {
      m_vFinfoHistory.setElementAt( finfo, m_iHistPos);
    }
  }

  public FileInterface previous()
  {
    FileInterface fi = null;
    --m_iHistPos;
    if( m_iHistPos < 0)
    {
      fi = null;
      m_iHistPos = 0;
      //m_vFinfoHistory.insertElementAt(fi, 0);
    }
    else if( m_iHistPos >= 0 && m_iHistPos < MAXHISTORY && m_iHistPos < m_vFinfoHistory.size())
      fi = (FileInterface)m_vFinfoHistory.elementAt( m_iHistPos);
    else
    {
      m_iHistPos = m_vFinfoHistory.size()-1;
      fi = null;
    }
    return fi;
  }

  public FileInterface next()
  {
    FileInterface fi = null;
    ++m_iHistPos;
    if( m_iHistPos >= MAXHISTORY)
    {
      fi = null;
      m_iHistPos = MAXHISTORY-1;
      //m_vFinfoHistory.removeElementAt( 0);
      //m_vFinfoHistory.add( fi);
    }
    else if( m_iHistPos >= 0 && m_iHistPos < MAXHISTORY && m_iHistPos < m_vFinfoHistory.size())
      fi = (FileInterface)m_vFinfoHistory.elementAt( m_iHistPos);
    else
    {
      m_iHistPos = m_vFinfoHistory.size()-1;
      fi = null;
    }
    return fi;
  }

  public void removeAll()
  {
    m_vFinfoHistory.removeAllElements();
    m_iHistPos = 0;
  }
}