/* *****************************************************************
 * Project: common
 * File:    InteractiveInputState.java
 * 
 * Creation:     31.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.components.shelltextarea;

import haui.util.GlobalApplicationContext;

import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * PatternBox: "ConcreteState" implementation.
 * <ul>
 *   <li>each subclass implements a behavior associated with a state of the Context.</li>
 * </ul>
 * 
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class InteractiveInputState extends State
{

  /** 
   * Default constructor
   */
  public InteractiveInputState()
  {
    super();
  }

  /** 
   * This method handles a request from a Context instance.
   */
  public void handle( Context context, ShellTextArea shellTextArea, KeyEvent keyEvent)
  {
    if( !shellTextArea.isInInputMode())
    {
      super.changeState( context, new NormalInputState());
      context.request( shellTextArea, keyEvent);
    }
    else if( shellTextArea.isInPasswordInputMode())
    {
      super.changeState( context, new InteractivePwdInputState());
      context.request( shellTextArea, keyEvent);
    }
    else
    {
      int id = keyEvent.getID();
      int iCode = keyEvent.getKeyCode();
      //int iMod = keyEvent.getModifiers();
      byte[] b;
      String c = String.valueOf( keyEvent.getKeyChar());
      //int iLen = 0;
      
      switch( id )
      {
        case KeyEvent.KEY_PRESSED:
          try
          {
            if( !(iCode == KeyEvent.VK_SHIFT || iCode == KeyEvent.VK_CONTROL || iCode == KeyEvent.VK_ALT
                || iCode == KeyEvent.VK_ALT_GRAPH || iCode == KeyEvent.VK_CAPS_LOCK || iCode == KeyEvent.VK_UP
                || iCode == KeyEvent.VK_DOWN || iCode == KeyEvent.VK_LEFT || iCode == KeyEvent.VK_RIGHT
                || iCode == KeyEvent.VK_BACK_SPACE || iCode == KeyEvent.VK_DELETE)) {
              b = c.getBytes();
              shellTextArea.getStreamContainer().getShellOutputStream().write( b, 0, b.length );
            }
            switch( iCode )
            {
              case KeyEvent.VK_ENTER:
                shellTextArea.append( "\n");
                break;
            }
            keyEvent.consume();
          }
          catch( IOException ioex )
          {
            ioex.printStackTrace();
            GlobalApplicationContext.instance().getOutputPrintStream().println( "Reconnect Input/Output Streams!");
            //System.out.println( "Reconnect Input/Output Streams!" );
            shellTextArea.getStreamContainer().newConnection();
          }
          break;

        case KeyEvent.KEY_TYPED:
          break;

        case KeyEvent.KEY_RELEASED:
          break;
      }
    }
  }

}
