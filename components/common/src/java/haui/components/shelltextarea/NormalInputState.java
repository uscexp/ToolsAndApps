/* *****************************************************************
 * Project: common
 * File:    NoPwdInputState.java
 * 
 * Creation:     29.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.components.shelltextarea;

import java.awt.event.KeyEvent;

/**
 * PatternBox: "ConcreteState" implementation.
 * <ul>
 *   <li>each subclass implements a behavior associated with a state of the Context.</li>
 * </ul>
 * 
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class NormalInputState extends State
{

  /** 
   * Default constructor
   */
  public NormalInputState()
  {
    super();
  }

  /** 
   * This method handles a request from a Context instance.
   */
  public void handle( Context context, ShellTextArea shellTextArea, KeyEvent keyEvent)
  {
    if( shellTextArea.isInPasswordInputMode())
    {
      super.changeState( context, new PwdInputState());
      context.request( shellTextArea, keyEvent);
    }
    else
    {
      int id = keyEvent.getID();
      int iCode = keyEvent.getKeyCode();
      int iMod = keyEvent.getModifiers();
      //byte[] b;
      //String c = String.valueOf( keyEvent.getKeyChar());
      //int iLen = 0;
      
      switch( id )
      {
        case KeyEvent.KEY_PRESSED:
          if( shellTextArea.getCaretPosition() < shellTextArea.getCurrentStartLinePos()
              || shellTextArea.getSelectionStart() < shellTextArea.getCurrentStartLinePos()
              || shellTextArea.getSelectionEnd() < shellTextArea.getCurrentStartLinePos() ) {
            shellTextArea.setCorrection( true);
            super.changeState( context, new NICorrectState());
            context.request( shellTextArea, keyEvent);
            return;
          }

          switch( iCode )
          {
            case KeyEvent.VK_UP:
              context.keyEventUp( shellTextArea, keyEvent);
              keyEvent.consume();
              break;

            case KeyEvent.VK_DOWN:
              context.keyEventDown( shellTextArea, keyEvent);
              break;

            case KeyEvent.VK_ENTER:
              context.keyEventEnter( shellTextArea, keyEvent);
              break;

            case KeyEvent.VK_C:
              if( iMod == KeyEvent.CTRL_MASK )
              {
                shellTextArea.copy();
                keyEvent.consume();
              }
              break;

            case KeyEvent.VK_X:
              if( iMod == KeyEvent.CTRL_MASK )
                shellTextArea.copy();
              break;

            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_LEFT:
              if( shellTextArea.getCaretPosition() <= shellTextArea.getCurrentStartLinePos()
                  || shellTextArea.getSelectionStart() <= shellTextArea.getCurrentStartLinePos()
                  || shellTextArea.getSelectionEnd() <= shellTextArea.getCurrentStartLinePos() ) {
                super.changeState( context, new NICorrectState());
                keyEvent.consume();
              }
              break;

            default:
               break;
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
