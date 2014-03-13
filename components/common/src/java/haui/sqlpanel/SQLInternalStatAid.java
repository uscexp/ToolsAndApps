package haui.sqlpanel;

import haui.components.desktop.JDesktopFrame;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.JTextComponent;

/**
 * Module:      SQLInternalStatAid.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\sqlpanel\\SQLInternalStatAid.java,v $ <p> Description: SQLInternalStatAid.<br> </p><p> Created:     19.02.2004 by AE </p><p>
 * @history  	    19.02.2004 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: SQLInternalStatAid.java,v $  Revision 1.0  2004-06-22 14:07:00+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: 1.0 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\sqlpanel\\SQLInternalStatAid.java,v 1.0 2004-06-22 14:07:00+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.3  </p>
 */
public class SQLInternalStatAid
     extends JInternalFrame
{

  private static final long serialVersionUID = 455619210148613363L;
  
  JInternalFrame m_frame;
  JDesktopFrame m_df;
  Component m_comp;
  AdpParentInternalFrame m_adpParentIntFrame;
  AdpFocus m_adpFoc;
  SQLStatementAid m_sqlStatAid;
  int m_iX;
  int m_iY;

  /**
   *Constructor for the SQLInternalStatAid object
   *
   * @param  df         Description of Parameter
   * @param  frame      Description of Parameter
   * @param  title      Description of Parameter
   * @param  modal      Description of Parameter
   * @param  strDriver  Description of Parameter
   * @param  strUrl     Description of Parameter
   * @param  strUid     Description of Parameter
   * @param  strPwd     Description of Parameter
   * @param  strSchema  Description of Parameter
   */
  public SQLInternalStatAid( JDesktopFrame df, JInternalFrame frame, Component comp, String title,
                             String strDriver, String strUrl, String strUid, String strPwd, String strSchema)
  {
    super( title );
    m_frame = frame;
    m_df = df;
    m_comp = comp;

    try
    {
      m_sqlStatAid = new SQLStatementAid( this, strDriver, strUrl, strUid, strPwd, strSchema);
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    m_adpFoc = new AdpFocus();
    m_sqlStatAid.getList().addFocusListener( m_adpFoc);

    // Change frame border
    putClientProperty( "JInternalFrame.frameType", "optionDialog" );

    try
    {
      setSelected( true );
    }
    catch( PropertyVetoException ignored )
    {
    }
  }

  private void jbInit() throws Exception
  {
    this.getContentPane().add(m_sqlStatAid, BorderLayout.CENTER);
  }

  private class AdpParentInternalFrame
    extends InternalFrameAdapter
  {
    public void internalFrameActivated( InternalFrameEvent event)
    {
      try
      {
        toFront();
        m_frame.setSelected( true );
      }
      catch( PropertyVetoException ignored )
      {
      }
    }
  }

  private class AdpFocus
    extends FocusAdapter
  {
    public void focusGained( FocusEvent event)
    {
      Object object = event.getSource();
      if (object == m_sqlStatAid.getList())
      {
        try
        {
          toFront();
          m_frame.setSelected( true );
          m_comp.requestFocus();
        }
        catch( PropertyVetoException ignored )
        {
        }
      }
    }
  }

  /**
   *  Sets the Frame attribute of the JExInternalDialog object
   *
   * @param  frame  The new Frame value
   */
  public void setFrame( JInternalFrame frame )
  {
    m_frame = frame;
  }

  public void replaceText()
  {
    m_sqlStatAid.replaceText();
  }

  /**
   *  Sets the Visible attribute of the JExInternalDialog object
   *
   * @param  b  The new Visible value
   */
  public void setVisible( boolean b )
  {
    if( b )
    {
      // Add modal internal frame to desktop
      if( m_frame != null)
        m_frame.getDesktopPane().add( this);
      else if( m_df != null)
        m_df.getDesktopPane().add( this);
      if( m_frame != null)
      {
        m_adpParentIntFrame = new AdpParentInternalFrame();
        m_frame.addInternalFrameListener( m_adpParentIntFrame );
      }

      //Dimension screenDim = getToolkit().getScreenSize();
      int x = 0;
      int y = 0;
      int minX = getRootPane().getLocation().x;
      int minY = getRootPane().getLocation().y;
      int maxX = getRootPane().getWidth();
      int maxY = getRootPane().getHeight();
      if( m_frame != null)
      {
        minX = m_frame.getDesktopPane().getLocation().x;
        minY = m_frame.getDesktopPane().getLocation().y;
        maxX = m_frame.getDesktopPane().getWidth();
        maxY = m_frame.getDesktopPane().getHeight();
      }
      else if( m_df != null)
      {
        minX = m_df.getDesktopPane().getLocation().x;
        minY = m_df.getDesktopPane().getLocation().y;
        maxX = m_df.getDesktopPane().getWidth();
        maxY = m_df.getDesktopPane().getHeight();
      }
      if( m_frame != null)
      {
        x = m_iX;
        y = m_iY;
      }
      else if( m_df != null)
      {
        x = m_df.getWidth()/2 - getWidth()/2;
        y = m_df.getHeight()/2 - getHeight()/2;
      }
      else
      {
        x = m_iX;
        y = m_iY;
      }
      if( x < minX )
        x = minX;
      if( y < minY )
        y = minY;
      if( x + getWidth() > maxX )
        x -= x + getWidth() - maxX;
      if( y + getHeight() > maxY )
        y -= y + getHeight() - maxY;
      setLocation( x, y );
    }
    super.setVisible( b );
    if( !b)
    {
      try
      {
        if( m_frame != null)
        {
          // remove this from desktop
          m_frame.getDesktopPane().remove( this);
          // remove added internal frame listener
          m_frame.removeInternalFrameListener( m_adpParentIntFrame);
          m_frame.setSelected( true );
        }
        else if( m_df != null)
        {
          // remove this from desktop
          m_df.getDesktopPane().remove( this);
        }
      }
      catch( PropertyVetoException ignored )
      {
      }
    }
  }

  public void show( JTextComponent jTextComponent)
  {
    if( m_frame != null)
      m_frame.setCursor( new Cursor( Cursor.WAIT_CURSOR));
    Rectangle rec =   m_sqlStatAid.show( jTextComponent);
    if( rec != null)
    {
      Point pTc = jTextComponent.getLocationOnScreen();
      Point pDf = m_frame.getLocationOnScreen();
      int iHeight = jTextComponent.getFontMetrics( jTextComponent.getFont()).getHeight();
      int iX = pTc.x - pDf.x;
      int iY = pTc.y - pDf.y + iHeight;
      m_iX = iX + rec.x;
      m_iY = iY + rec.y;
      pack();
      setVisible( true );
      m_sqlStatAid.setSelectionInternalText();
    }
    else
      setVisible( false);
    if( m_frame != null)
      m_frame.setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  public void close()
  {
    m_sqlStatAid.close();
  }

  public int getStartPosition()
  {
    return m_sqlStatAid.getStartPosition();
  }

  public int getEndPosition()
  {
    return m_sqlStatAid.getEndPosition();
  }

  public void setSelection()
  {
    m_sqlStatAid.setSelection();
  }

  public void moveSelectionHome()
  {
    m_sqlStatAid.moveSelectionHome();
  }

  public void moveSelectionEnd()
  {
    m_sqlStatAid.moveSelectionEnd();
  }

  public void moveSelectionUp()
  {
    m_sqlStatAid.moveSelectionUp();
  }

  public void moveSelectionDown()
  {
    m_sqlStatAid.moveSelectionDown();
  }

  public void moveSelectionPageUp()
  {
    m_sqlStatAid.moveSelectionPageUp();
  }

  public void moveSelectionPageDown()
  {
    m_sqlStatAid.moveSelectionPageDown();
  }
}
