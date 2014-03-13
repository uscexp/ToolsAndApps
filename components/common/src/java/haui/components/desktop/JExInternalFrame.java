package haui.components.desktop;

import haui.util.GlobalApplicationContext;
import java.awt.Cursor;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;

/**
 * Module:      JExInternalFrame.java<br> $Source: $ <p> Description: JExInternalFrame.<br> </p><p> Created:     13.05.2003	by	AE </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @created      13.05.2003  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @version      v1.0, 2003; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.3  </p>
 */
public class JExInternalFrame
  extends JInternalFrame
{
  private static final long serialVersionUID = 4309317128622680221L;
  
  // Constants

  // member variables
  static int m_openFrameCount = 0;
  static final int m_xOffset = 25 ;
  static final int m_yOffset = 25 ;
  protected JDesktopFrame m_df;
  protected String m_strAppName;

  public JExInternalFrame( JDesktopFrame df, String strAppName, String strName, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
  {
    super( strName + (String.valueOf( ++m_openFrameCount)), resizable, closable, maximizable, iconifiable);
    m_strAppName = strAppName;
    if( GlobalApplicationContext.instance().getRootComponent() != null)
      GlobalApplicationContext.instance().getRootComponent().setCursor(new Cursor( Cursor.WAIT_CURSOR));

    m_df = df;
    setFrameIcon( (new ImageIcon( getClass().getResource( JDesktopFrame.APPICON))));
    //Set the window's location.
    if( m_df != null)
    {
      setSize( (int)(m_df.getWidth()*0.8d), (int)(m_df.getHeight()*0.8d));
      int iX = m_xOffset*m_openFrameCount;
      int iRx = iX + getWidth();
      int iY = m_yOffset*m_openFrameCount;
      int iBy = iY + getHeight();
      if( iRx > m_df.getWidth() || iBy > m_df.getHeight())
        m_openFrameCount = 1;
    }

    if( m_openFrameCount < 1)
      m_openFrameCount = 1;
    setLocation( m_xOffset*m_openFrameCount, m_yOffset*m_openFrameCount);

    if( GlobalApplicationContext.instance().getRootComponent() != null)
      GlobalApplicationContext.instance().getRootComponent().setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
  }

  public JDesktopFrame getDesktopFrame()
  {
    return m_df;
  }

  protected void onExit()
  {
    --m_openFrameCount;
    setVisible(false); // hide the Frame
    m_df.getDesktopPane().remove( this);
    dispose();	     // free the system resources
  }
}