package haui.components.desktop;

import haui.components.JExDialog;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.lang.reflect.Array;
import javax.swing.*;

/**
 * Module:      WindowSelectDialog.java<br> $Source: $ <p> Description: WindowSelectDialog.<br> </p><p> Created:     23.04.2003 by AE </p><p>
 * @history  	    23.04.2003 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2003; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.2  </p>
 */
public class WindowSelectDialog
  extends JExDialog
{
  private static final long serialVersionUID = -4604134678062206567L;
  
  // member variables
  AppAction m_appAction;
  JInternalFrame[] m_ifArray;

  // GUI member variables
  JDesktopPane m_dp;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  DefaultListModel m_defLM = new DefaultListModel();
  JList m_jList = new JList(m_defLM);

  public WindowSelectDialog( Component frame, JDesktopPane dp, String strAppName)
  {
    super( (Frame)null, "Select window", true, strAppName);
    setFrame( frame);
    m_dp = dp;
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE);
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    setResizable( false);
    // fill list
    setArray();
    if( Array.getLength( m_ifArray) > 0)
      setSelectedIndex( 0);

    // add key listenser
    AdpKey adpKey = new AdpKey();
    m_jList.addKeyListener( adpKey);

    // add mouse handler to list
    MouseHandler mh = new MouseHandler();
    m_jList.addMouseListener( mh);

    // add action handler
    m_appAction = new AppAction();

    setSize( 200, 200);
  }

  private void jbInit()
    throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutBase);
    m_jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.getContentPane().add(m_jList, BorderLayout.CENTER);
  }

  class AdpKey
    extends KeyAdapter
  {
    public void keyReleased( KeyEvent e)
    {
      //int id = e.getID();
      //int len = Array.getLength( m_ifArray);

      if( e.getKeyCode() == KeyEvent.VK_CONTROL)
      {
        int iIdx = getSelectedIndex();
        showWindow( iIdx);
      }
    }
  }

  class AppAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if( cmd == "add")
        ;
    }
  }

  private void setArray()
  {
    m_ifArray = m_dp.getAllFrames();
    int iCount = Array.getLength( m_ifArray);
    for( int i = 0; i < iCount; ++i)
    {
      addElement( m_ifArray[i].getTitle());
    }
  }

  public int listSize()
  {
    return m_defLM.size();
  }

  public int getSelectedIndex()
  {
    return m_jList.getSelectedIndex();
  }

  public void setSelectedIndex( int idx)
  {
    m_jList.setSelectedIndex( idx);
  }

  public void addElement( Object obj)
  {
    m_defLM.addElement( obj);
  }

  public Object elementAt( int iIdx)
  {
    return m_defLM.elementAt( iIdx);
  }

  public void setElementAt( Object obj, int iIdx)
  {
    m_defLM.setElementAt( obj, iIdx);
  }

  public void removeElementAt( int iIdx)
  {
    m_defLM.removeElementAt( iIdx);
  }

   public void removeAllElements()
  {
    m_defLM.removeAllElements();
  }

  public void onExit()
  {
    setVisible(false); // hide the Frame
    dispose();	     // free the system resources
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      //Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2)
        onLeftMouseDoublePressed( event);
    }
  }

  void onLeftMouseDoublePressed( MouseEvent event)
  {
    int iIdx = getSelectedIndex();
    showWindow( iIdx);
  }

  public void nextSelection()
  {
    int iLen = Array.getLength( m_ifArray);
    int iIdx = getSelectedIndex();
    ++iIdx;
    if( iIdx >= iLen)
      iIdx = 0;
    setSelectedIndex( iIdx);
  }

  private void showWindow( int iIdx)
  {
    if( iIdx > -1)
    {
      if( m_ifArray[iIdx].isIcon())
      {
        try
        {
          m_ifArray[iIdx].setIcon( false);
        }
        catch( PropertyVetoException e)
        {}
      }
      m_ifArray[iIdx].toFront();
      try
      {
        m_ifArray[iIdx].setSelected(true);
      }
      catch( PropertyVetoException e)
      {}
    }
    setVisible( false);
    dispose();
  }
}
