/* *****************************************************************
 * Project: common
 * File:    PwdInputContext.java
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
import javax.swing.text.BadLocationException;

/**
 * PatternBox: "Context" implementation. <ul> <li>defines the interface of interest to clients.</li> <li>maintains an instance of a ConcreteState subclass that defines the current state.</li> </ul>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class Context
{

  /** stores the associated State instance */
  private State fState;

  /** 
   * Constructor
   */
  public Context( State state)
  {
    super();
    fState = state;
  }

  /** 
   * The Context object delegates all state-specific requests to its
   * associated State instance.
   */
  public void request( ShellTextArea shellTextArea, KeyEvent keyEvent)
  {
    // TODO: Customize this method based on your application needs.
    fState.handle( this, shellTextArea, keyEvent);
  }

  /** 
   * This method changes the state of the Context instance.
   */
  public void changeState( State state)
  {
    fState = state;
  }

  protected void keyEventUp( ShellTextArea shellTextArea, KeyEvent keyEvent)
  {
    int iLen = 0;
    if( shellTextArea.getSelectedText() != null && keyEvent.getModifiers() != KeyEvent.SHIFT_MASK )
      shellTextArea.replaceSelection( shellTextArea.getSelectedText() );
    iLen = shellTextArea.getText().length();
    if( shellTextArea.getHistoryIndex() >= 0
        && shellTextArea.getCommandHistory().size() != 0 )
    {
      shellTextArea.setHistoryIndex( ( shellTextArea.getHistoryIndex() < 1 ) ? 0 : shellTextArea.getHistoryIndex() - 1);
      String strHist = (String)shellTextArea.getCommandHistory().elementAt( shellTextArea.getHistoryIndex() );
      shellTextArea.replaceRange( strHist, shellTextArea.getCurrentStartLinePos(), iLen );
      keyEvent.consume();
      shellTextArea.setCaretPosition( shellTextArea.getCurrentStartLinePos() + strHist.length() );
    }
  }
  
  protected void keyEventDown( ShellTextArea shellTextArea, KeyEvent keyEvent)
  {
    int iLen = 0;
    if( shellTextArea.getSelectedText() != null && keyEvent.getModifiers() != KeyEvent.SHIFT_MASK )
      shellTextArea.replaceSelection( shellTextArea.getSelectedText() );
    iLen = shellTextArea.getText().length();
    if( shellTextArea.getHistoryIndex() < shellTextArea.getCommandHistory().size() )
    {
      shellTextArea.setHistoryIndex( ( shellTextArea.getHistoryIndex() >= shellTextArea.getCommandHistory().size() - 1 )
          ? shellTextArea.getCommandHistory().size() - 1 : shellTextArea.getHistoryIndex() + 1);
      String strHist = (String)shellTextArea.getCommandHistory().elementAt( shellTextArea.getHistoryIndex() );
      shellTextArea.replaceRange( strHist, shellTextArea.getCurrentStartLinePos(), iLen );
      keyEvent.consume();
      shellTextArea.setCaretPosition( shellTextArea.getCurrentStartLinePos() + strHist.length() );
    }
  }
  
  protected void keyEventEnter( ShellTextArea shellTextArea, KeyEvent keyEvent)
  {
    byte[] b;
    String c = String.valueOf( keyEvent.getKeyChar());
    try
    {
      shellTextArea.setCaretPosition( shellTextArea.getText().length() );
      shellTextArea.setCommnand( shellTextArea.getText( shellTextArea.getCurrentStartLinePos(),
          shellTextArea.getText().length() - shellTextArea.getCurrentStartLinePos() ));
      if( shellTextArea.getCommand().length() != 0)
        shellTextArea.getCommandHistory().add( shellTextArea.getCommand() );
      shellTextArea.setHistoryIndex( shellTextArea.getCommandHistory().size());
    }
    catch( BadLocationException ex )
    {
      ex.printStackTrace();
    }
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
        GlobalApplicationContext.instance().getOutputPrintStream().println( "Reconnect Input/Output Streams!");
        //System.out.println( "Reconnect Input/Output Streams!" );
        shellTextArea.getStreamContainer().newConnection();
      }
    }
    shellTextArea.setInputMode( true);
    changeState( new InteractiveInputState());
  }
}
