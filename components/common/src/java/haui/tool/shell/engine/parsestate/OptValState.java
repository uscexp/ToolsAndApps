/* *****************************************************************
 * Project: common
 * File:    OptValState.java
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
public class OptValState extends ParseState
{

  /** 
   * Default constructor
   */
  public OptValState()
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
        if( !processLineCache.getOptionKeyValue().toString().equals( ""))
        {
          container.addArgument( processLineCache.getOptionKeyValue().toString() );
          container.getOptions().put( processLineCache.getOptionKey().toString(),
              processLineCache.getOptionValue().toString() );
          processLineCache.setOptionKey( new StringBuffer());
          processLineCache.setOptionValue( new StringBuffer());
          processLineCache.setOptionKeyValue( new StringBuffer());
        }
        super.changeState( context, new NoneState());
        break;

      case '\"':
        if( processLineCache.getOldChar() == '=')
        {
          String strTmp = processLineCache.getLine().substring( processLineCache.getPosition());
          if( strTmp != null && strTmp.length() > 2)
          {
            int iIdx = strTmp.indexOf( "\"", 1);
            if( iIdx == -1)
            {
              processLineCache.getOptionValue().append( strTmp.substring( 1));
              processLineCache.getOptionKeyValue().append( strTmp.substring( 0));
              processLineCache.setPosition( processLineCache.getLine().length()-1);
            }
            else
            {
              processLineCache.getOptionValue().append( strTmp.substring( 1, iIdx));
              processLineCache.getOptionKeyValue().append( strTmp.substring( 0, iIdx+1));
              processLineCache.setPosition( processLineCache.getPosition() + iIdx);
            }
            processLineCache.setCurrentChar( processLineCache.getLine().charAt( processLineCache.getPosition()));
            c = processLineCache.getCurrentChar();
          }
        }
        else
        {
          processLineCache.getOptionValue().append( c );
          processLineCache.getOptionKeyValue().append( c );
        }
        break;

      case '\'':
        if( processLineCache.getOldChar() == '=')
        {
          String strTmp = processLineCache.getLine().substring( processLineCache.getPosition());
          if( strTmp != null && strTmp.length() > 2)
          {
            int iIdx = strTmp.indexOf( "\'", 1);
            if( iIdx == -1)
            {
              processLineCache.getOptionValue().append( strTmp.substring( 1));
              processLineCache.getOptionKeyValue().append( strTmp.substring( 0));
              processLineCache.setPosition( processLineCache.getLine().length()-1);
            }
            else
            {
              processLineCache.getOptionValue().append( strTmp.substring( 1, iIdx));
              processLineCache.getOptionKeyValue().append( strTmp.substring( 0, iIdx+1));
              processLineCache.setPosition( processLineCache.getPosition() + iIdx);
            }
            processLineCache.setCurrentChar( processLineCache.getLine().charAt( processLineCache.getPosition()));
            c = processLineCache.getCurrentChar();
          }
        }
        else
        {
          processLineCache.getOptionValue().append( c );
          processLineCache.getOptionKeyValue().append( c );
        }
        break;

      case '&':
        if( processLineCache.getPosition() == processLineCache.getLine().length()-1)
          container.setExecInBackground( true);
        else
        {
          processLineCache.getOptionValue().append( c );
          processLineCache.getOptionKeyValue().append( c );
        }
        break;

      default:
        processLineCache.getOptionValue().append( c );
        processLineCache.getOptionKeyValue().append( c );
        break;
    }
    if( !processLineCache.isProccessed() && processLineCache.isLineEnd() && !processLineCache.getOptionKeyValue().toString().equals( ""))
    {
      container.addArgument( processLineCache.getOptionKeyValue().toString() );
      container.getOptions().put( processLineCache.getOptionKey().toString(),
          processLineCache.getOptionValue().toString() );
      processLineCache.setProccessed( true);
    }
  }

}
