/* *****************************************************************
 * Project: common
 * File:    OptKeyState.java
 * 
 * Creation:     03.04.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.tool.shell.engine.parsestate;

import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.tool.shell.engine.JShellCommandProcessor.ProcessLineCache;

/**
 * PatternBox: "ConcreteState" implementation.
 * <ul>
 *   <li>each subclass implements a behavior associated with a state of the Context.</li>
 * </ul>
 * 
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 */
public class OptKeyState extends ParseState
{

  /** 
   * Default constructor
   */
  public OptKeyState()
  {
    super();
  }

  /** 
   * This method handles a request from a Context instance.
   */
  public void handle( ParseContext context, CommandLineContainer container,
      ProcessLineCache processLineCache)
  {
    char c = processLineCache.getCurrentChar();
    switch( c)
    {
      case ' ':
      case '\t':
      case '\n':
        if( !processLineCache.getOptionKey().toString().equals( ""))
        {
          container.addArgument( processLineCache.getOptionKey().toString() );
          container.getOptions().put( processLineCache.getOptionKey().toString(), "" );
          processLineCache.setOptionKey( new StringBuffer());
          processLineCache.setOptionKeyValue( new StringBuffer());
        }
        super.changeState( context, new NoneState());
        break;

      case '&':
        if( processLineCache.getPosition() == processLineCache.getLine().length()-1)
          container.setExecInBackground( true);
        else
        {
          processLineCache.getOptionKey().append( c );
          processLineCache.getOptionKeyValue().append( c );
        }
        break;

      case '=':
        processLineCache.getOptionKeyValue().append( c );
        super.changeState( context, new OptValState());
        break;

      default:
        processLineCache.getOptionKey().append( c );
        processLineCache.getOptionKeyValue().append( c );
        break;
    }
    if( !processLineCache.isProccessed() && processLineCache.isLineEnd() && !processLineCache.getOptionKey().toString().equals( ""))
    {
      container.addArgument( processLineCache.getOptionKey().toString() );
      container.getOptions().put( processLineCache.getOptionKey().toString(), "" );
      processLineCache.setProccessed( true);
    }
  }

}
