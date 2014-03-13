package haui.components.desktop;

import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Module:      JExInternalDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\desktop\\JExInternalDialog.java,v $ <p> Description: Extended internal frame dialog.<br> </p><p> Created:     30.09.2002 by AE </p><p>
 * @history      30.09.2002 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: JExInternalDialog.java,v $  Revision 1.4  2004-08-31 16:03:16+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.3  2004-06-22 14:08:53+02  t026843  bigger changes  Revision 1.2  2004-02-17 15:49:55+01  t026843  catching exeptions during event dispatch  Revision 1.1  2003-05-28 14:19:53+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:26:04+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2002; $Revision: 1.4 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\desktop\\JExInternalDialog.java,v 1.4 2004-08-31 16:03:16+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.2  </p>
 */
public class JExInternalDialog
     extends JInternalFrame
{
   String m_strAppName;

  JInternalFrame m_frame;
  JDesktopFrame m_df;
  final JPanel m_glass = new JPanel();
  Component m_oldGlass;
  AdpParentInternalFrame m_adpParentIntFrame;
  boolean m_blModal;
  boolean m_blLock;

  /**
   *Constructor for the ModalInternalFrame object
   *
   * @param  title     Description of Parameter
   * @param  rootPane  Description of Parameter
   * @param  desktop   Description of Parameter
   * @param  pane      Description of Parameter
   */
  //public JExInternalDialog( String title, JRootPane rootPane, JDesktopPane desktop /*, JOptionPane pane */)
  public JExInternalDialog( JDesktopFrame df, JInternalFrame frame, String strAppName, String title, boolean modal)
  {
    super( title );
    m_strAppName = strAppName;
    m_frame = frame;
    m_df = df;
    m_blModal = modal;
    this.setDefaultCloseOperation( JInternalFrame.DISPOSE_ON_CLOSE);

    // create opaque glass pane
    m_glass.setOpaque( false );

    // Attach mouse listeners
    MouseInputAdapter adapter = new MouseInputAdapter(){};
    m_glass.addMouseListener( adapter);
    m_glass.addMouseMotionListener( adapter);

    if( m_frame != null)
    {
      m_adpParentIntFrame = new AdpParentInternalFrame();
      m_frame.addInternalFrameListener( m_adpParentIntFrame );
    }

    // Change frame border
    putClientProperty( "JInternalFrame.frameType", "optionDialog" );

    // Size frame
    /*
    Dimension size = getPreferredSize();
    Dimension rootSize = m_frame.getSize();

    setBounds( ( rootSize.width - size.width ) / 2, ( rootSize.height - size.height ) / 2,
        size.width, size.height );
    m_frame.validate();
    */
    try
    {
      setSelected( true );
    }
    catch( PropertyVetoException ignored )
    {
    }
  }

  private class AdpParentInternalFrame
    extends InternalFrameAdapter
  {
    public void internalFrameActivated( InternalFrameEvent event)
    {
      try
      {
        toFront();
        setSelected( true );
      }
      catch( PropertyVetoException ignored )
      {
      }
    }
  }

  /**
   *  Description of the Method
   */
  private synchronized void startModal()
  {
    try
    {
      if( SwingUtilities.isEventDispatchThread() )
      {
        EventQueue theQueue = getToolkit().getSystemEventQueue();
        while( isVisible() )
        {
          try
          {
            AWTEvent event = theQueue.getNextEvent();
            Object source = event.getSource();
            if( event instanceof ActiveEvent )
            {
              ( ( ActiveEvent )event ).dispatch();
            }
            else if( source instanceof Component )
            {
              ( ( Component )source ).dispatchEvent( event );
            }
            else if( source instanceof MenuComponent )
            {
              ( ( MenuComponent )source ).dispatchEvent( event );
            }
            else
            {
              GlobalApplicationContext.instance().getOutputPrintStream().println( "Unable to dispatch: " + event);
              //System.err.println( "Unable to dispatch: " + event );
            }
          }
          catch( Exception ex)
          {
            ex.printStackTrace();
          }
        }
      }
      else
      {
        while( isVisible() )
        {
          wait();
        }
      }
    }
    catch( InterruptedException ignored )
    {
    }
  }


  /**
   *  Description of the Method
   */
  private synchronized void stopModal()
  {
    notifyAll();
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


  /**
   *  Sets the Visible attribute of the JExInternalDialog object
   *
   * @param  b  The new Visible value
   */
  public void setVisible( boolean b )
  {
    Object lock = new Object();
    if( b )
    {
      // Add modal internal frame to desktop
      if( m_frame != null)
        m_frame.getDesktopPane().add( this);
      else if( m_df != null)
        m_df.getDesktopPane().add( this);
      // Change glass pane to our panel
      if( m_glass != null)
      {
        if( m_frame != null)
        {
          m_oldGlass = m_frame.getRootPane().getGlassPane();
          m_frame.getRootPane().setGlassPane( m_glass );
        }
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
        Point parloc = m_frame.getLocation();
        x = parloc.x + m_frame.getWidth()/2 - getWidth()/2;
        y = m_frame.getHeight()/2 - getHeight()/2;
      }
      else if( m_df != null)
      {
        x = m_df.getWidth()/2 - getWidth()/2;
        y = m_df.getHeight()/2 - getHeight()/2;
      }
      else
      {
        Component root = GlobalApplicationContext.instance().getRootComponent();
        if( root != null)
        {
          x = root.getWidth()/2 - getWidth()/2;
          y = root.getHeight()/2 - getHeight()/2;
        }
        else
        {
          x = 50 + getRootPane().getWidth()/2 - getWidth()/2;
          y = getRootPane().getHeight()/2 - getHeight()/2;
        }
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
    if( b)
    {
      synchronized(lock)
      {
        if( m_blLock )
        {
          try
          {
            Thread.sleep( 500 );
          }
          catch( InterruptedException ex )
          {
          }
        }
        m_blLock = true;
        if( /* m_frame != null &&*/ m_glass != null )
          m_glass.setVisible( b );
        super.setVisible( b );
        if( m_blModal )
          startModal();
        m_blLock = false;
      }
    }
    else
    {
      synchronized(lock)
      {
        if( m_blLock )
        {
          try
          {
            Thread.sleep( 500 );
          }
          catch( InterruptedException ex )
          {
          }
        }
        m_blLock = true;
        if( /* m_frame != null &&*/ m_glass != null )
          m_glass.setVisible( b );
        super.setVisible( b );
        if( m_blModal )
          stopModal();
          // reset to old glasspane
        if( m_oldGlass != null )
        {
          if( m_frame != null )
            m_frame.getRootPane().setGlassPane( m_oldGlass );
        }
        try
        {
          if( m_frame != null )
          {
            // remove this from desktop
            m_frame.getDesktopPane().remove( this );
            // remove added internal frame listener
            m_frame.removeInternalFrameListener( m_adpParentIntFrame );
            m_frame.setSelected( true );
          }
          else if( m_df != null )
          {
            // remove this from desktop
            m_df.getDesktopPane().remove( this );
          }
        }
        catch( PropertyVetoException ignored )
        {
        }
        m_blLock = false;
      }
    }
  }
}
