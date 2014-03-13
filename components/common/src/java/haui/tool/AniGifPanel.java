
package haui.tool;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 *		Module:					AniGifPanel.java
 *<p>
 *		Description:
 *</p><p>
 *		Created:				21.09.2000	by	AE
 *</p><p>
 *		Last Modified:	16.10.2002	by	AE
 *</p><p>
 *		@history				21.09.2000	by	AE: Created.<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000,2001,2002
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class AniGifPanel
extends JPanel
implements ImageObserver
{
  private static final long serialVersionUID = 3255186194284939448L;
  
  private ImageIcon m_agif;
  private int m_aw;
  private int m_ah;
  private BufferedImage m_bimg;


  public AniGifPanel()
  {
  }

  public AniGifPanel( ImageIcon anigif)
  {
    setImage( anigif);
  }

  public void setImage( ImageIcon anigif)
  {
    m_agif = anigif;
    try
    {
      MediaTracker tracker = new MediaTracker( this);
      tracker.addImage( m_agif.getImage(), 0);
      tracker.waitForID( 0);
    }
    catch( Exception e)
    {
    }
    m_aw = m_agif.getImage().getWidth( this) / 2;
    m_ah = m_agif.getImage().getHeight( this) / 2;
    setPreferredSize( new Dimension( m_agif.getImage().getWidth( this), m_agif.getImage().getHeight( this)));
    setMinimumSize( new Dimension( m_agif.getImage().getWidth( this), m_agif.getImage().getHeight( this)));
  }

  public void drawAni( int w, int h, Graphics2D g2)
  {
    g2.drawImage( m_agif.getImage(), w/2-m_aw, h/2-m_ah, this);
  }


  public Graphics2D createGraphics2D( int w, int h)
  {
    Graphics2D g2 = null;
    if( m_bimg == null || m_bimg.getWidth() != w || m_bimg.getHeight() != h)
    {
      m_bimg = (BufferedImage)createImage(w, h);
    }
    g2 = m_bimg.createGraphics();
    g2.setBackground( getBackground());
    g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2.clearRect( 0, 0, w, h);
    return g2;
  }


  public void paint( Graphics g)
  {
    Dimension d = getSize();
    int w = d.width;
    int h = d.height;
    if( w == 0)
      w = 1;
    if( h == 0)
      h = 1;
    Graphics2D g2 = createGraphics2D( w, h);
    drawAni( w, h, g2);
    g2.dispose();
    g.drawImage( m_bimg, 0, 0, this);
  }

  // overrides imageUpdate to control the animated gif's animation
  public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height)
  {
    if( isShowing() && (infoflags & ALLBITS) != 0)
      repaint();
    if( isShowing() && (infoflags & FRAMEBITS) != 0)
      repaint();
    return isShowing();
  }

  /* test main function
  public static void main( String argv[]) {
    final AniGifPanel ag = new AniGifPanel();
    ag.setImage( "a_cons23.gif");
    JFrame f = new JFrame( "AniGifPanel");
    f.addWindowListener( new WindowAdapter()
    {
        public void windowClosing( WindowEvent e)
        {
          System.exit(0);
        }
    });
    f.getContentPane().add( "Center", ag);
    f.pack();
    f.show();
  }
  */
}
