/* *****************************************************************
 * Project: common
 * File:    IsPwdInputState.java
 * 
 * Creation:     29.03.2006 by Andreas Eisenhauer
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
public class PwdInputState extends State
{

  /** 
   * Default constructor
   */
  public PwdInputState()
  {
    super();
  }

  /** 
   * This method handles a request from a Context instance.
   */
  public void handle( Context context, ShellTextArea shellTextArea, KeyEvent keyEvent)
  {
    if( !shellTextArea.isInPasswordInputMode())
    {
      super.changeState( context, new NormalInputState());
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
          shellTextArea.setCaretPosition( shellTextArea.getCurrentStartLinePos());
          switch( iCode )
          {
            case KeyEvent.VK_ENTER:
              shellTextArea.setCommnand( shellTextArea.getPassword().toString());
              if( shellTextArea.getCommand() != null )
              {
                b = shellTextArea.getCommand().getBytes();
                try
                {
                  shellTextArea.getStreamContainer().getShellOutputStream().write( b, 0, b.length );
                  b = c.getBytes();
                  shellTextArea.getStreamContainer().getShellOutputStream().write( b, 0, b.length );
                }
                catch( IOException ioex )
                {
                  ioex.printStackTrace();
                  GlobalApplicationContext.instance().getOutputPrintStream().println(
                      "Reconnect Input/Output Streams!");
                  //System.out.println( "Reconnect Input/Output Streams!" );
                  shellTextArea.getStreamContainer().newConnection();
                }
              }
              shellTextArea.setPasswordInputMode( false);
              super.changeState( context, new NormalInputState());
              break;

            default:
              shellTextArea.append( "*");
              keyEvent.consume();
              break;
          }
          break;

        case KeyEvent.KEY_TYPED:
          shellTextArea.getPassword().append( c);
          keyEvent.consume();
          break;

        case KeyEvent.KEY_RELEASED:
          keyEvent.consume();
          break;
      }
    }
  }

}
