package haui.tool.shell.engine;

import haui.tool.shell.engine.parsestate.CmdNoTextState;
import haui.tool.shell.engine.parsestate.CommandState;
import haui.tool.shell.engine.parsestate.ParseContext;
import haui.tool.shell.engine.parsestate.PreParseContext;
import java.util.*;

/**
 * Module:       JShellCommandProcessor.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellCommandProcessor.java,v $ <p> Description:  Java shell command handler.<br> </p><p> Created:	    23.03.2004  by AE </p><p>
 * @history       23.03.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: JShellCommandProcessor.java,v $  Revision 1.1  2004-08-31 16:03:27+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.0  2004-06-22 14:06:51+02  t026843  Initial revision  </p><p>
 * @author        Andreas Eisenhauer  </p><p>
 * @version       v1.0, 2004; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellCommandProcessor.java,v 1.1 2004-08-31 16:03:27+02 t026843 Exp t026843 $  </p><p>
 * @since         JDK1.3  </p>
 */
public class JShellCommandProcessor
{
  // constants
//  public static final short STATENONE = 0;
//  public static final short STATECOMMAND = 1;
//  public static final short STATEPUREARG = 2;
//  public static final short STATEOPTKEY = 3;
//  public static final short STATEOPTVAL = 4;
//  public static final short STATECMDNOTEXT = 0;
//  public static final short STATECMDTEXT = 1;

  // member variables
  private JShellEnv m_jse;
  private String m_strLine;
  private Vector m_vecCmds = new Vector();
  private PreParseContext preParseContext = null;
  private ParseContext parseContext = null;
  private CommandLineContainer m_commandLineContainer = new CommandLineContainer();
//  private String m_strLine;
//  private Vector m_vecCmds = new Vector();
//  private String m_strCmd;
//  private HashMap m_hmOptions = new HashMap();
//  private Vector m_vecArgsWithoutOpts = new Vector();
//  private Vector m_vecArgs = new Vector();
//  private int m_iCmdPos = -1;
//  private int m_iArgPos = -1;
//  private boolean m_blExecBackground = false;

  public JShellCommandProcessor( JShellEnv jse, String strLine)
  {
    getCommandLineContainer().setCurrentLine( strLine);
    m_jse = jse;
    if( getCommandLineContainer().getCurrentLine() != null && !getCommandLineContainer().getCurrentLine().equals( "" ) )
    {
      preParseContext = new PreParseContext( new CmdNoTextState());
      StringTokenizer stMask = new StringTokenizer( strLine, "\"", true);
      StringBuffer strBufCmd = new StringBuffer();
      preParseContext.request( this, strBufCmd, stMask);
    }
  }
  
  public JShellEnv getShellEnv()
  {
    return m_jse;
  }

/*  public JShellCommandProcessor( String strLine)
  {
    m_strLine = strLine;
    //m_jse = jse;
    if( m_strLine != null && !m_strLine.equals( "" ) )
    {
      int iState = STATECMDNOTEXT;
      StringTokenizer stMask = new StringTokenizer( strLine, "\"", true);
      StringBuffer strBufCmd = new StringBuffer();

      while( stMask.hasMoreTokens())
      {
        String str = stMask.nextToken();
        switch( iState)
        {
          case STATECMDNOTEXT:
            if( str.equals( "\"" ) )
            {
              strBufCmd.append( str);
              iState = STATECMDTEXT;
            }
            else
            {
              StringTokenizer stCmds = new StringTokenizer( str, ";", true );
              while( stCmds.hasMoreTokens() )
              {
                String strTmp = stCmds.nextToken();
                if( strTmp.equals( ";"))
                {
                  m_vecCmds.add( strBufCmd.toString() );
                  strBufCmd = new StringBuffer();
                }
                else
                  strBufCmd.append( strTmp);
              }
            }
            break;

          case STATECMDTEXT:
            if( str.equals( "\"" ) )
            {
              strBufCmd.append( str);
              iState = STATECMDNOTEXT;
            }
            else
            {
              strBufCmd.append( str);
            }
            break;
        }
      }
      if( strBufCmd.length() > 0)
        m_vecCmds.add( strBufCmd.toString() );

      processLine( (( String )m_vecCmds.elementAt( 0 )).trim() );
    }
  }
*/
  
  public void processLine( CommandLineContainer container, String strLine)
  {
    // reset
    container.reset();
    
    ProcessLineCache processLineCache = new ProcessLineCache( replaceVariables( strLine));
    parseContext = new ParseContext( new CommandState());
    for( processLineCache.setPosition( 0); processLineCache.getPosition() < processLineCache.getLine().length();
      processLineCache.setPosition( processLineCache.getPosition() + 1))
    {
      processLineCache.setCurrentChar( processLineCache.getLine().charAt( processLineCache.getPosition()));
      parseContext.request( container, processLineCache);
    }
    
  }

/*  public void processLine( String strLine)
  {
    // reset
    reset();

    StringBuffer strbufLine = replaceVariables( strLine);
    short sState = STATECOMMAND;
    short sStateOld = STATENONE;
    StringBuffer strbufCmd = new StringBuffer();
    StringBuffer strbufPureArg = new StringBuffer();
    StringBuffer strbufPureArgFull = new StringBuffer();
    StringBuffer strbufOptKey = new StringBuffer();
    StringBuffer strbufOptValue = new StringBuffer();
    StringBuffer strbufOptKeyValue = new StringBuffer();
    char cOld = ' ';

    for( int i = 0; i < strbufLine.length(); ++i)
    {
      char c = strbufLine.charAt( i);
      switch( c)
      {
        case ' ':
        case '\t':
        case '\n':
          switch( sState)
          {
            case STATECOMMAND:
              if( !strbufCmd.toString().equals( ""))
              {
                m_strCmd = strbufCmd.toString();
                m_vecArgs.add( m_strCmd );
                strbufCmd = new StringBuffer();
              }
              break;

            case STATEPUREARG:
              if( !strbufPureArgFull.toString().equals( ""))
              {
                m_vecArgs.add( strbufPureArgFull.toString() );
                m_vecArgsWithoutOpts.add( strbufPureArg.toString() );
                strbufPureArg = new StringBuffer();
                strbufPureArgFull = new StringBuffer();
              }
              break;

            case STATEOPTKEY:
              if( !strbufOptKey.toString().equals( ""))
              {
                m_vecArgs.add( strbufOptKey.toString() );
                m_hmOptions.put( strbufOptKey.toString(), "" );
                strbufOptKey = new StringBuffer();
                strbufOptKeyValue = new StringBuffer();
              }
              break;

            case STATEOPTVAL:
              if( !strbufOptKeyValue.toString().equals( ""))
              {
                m_vecArgs.add( strbufOptKeyValue.toString() );
                m_hmOptions.put( strbufOptKey.toString(), strbufOptValue.toString() );
                strbufOptKey = new StringBuffer();
                strbufOptValue = new StringBuffer();
                strbufOptKeyValue = new StringBuffer();
              }
              break;
          }
          sStateOld = sState;
          sState = STATENONE;
          break;

        case '\"':
          switch( sState )
          {
            case STATECOMMAND:
              strbufCmd.append( c );
              break;

            case STATEPUREARG:
              strbufPureArg.append( c );
              strbufPureArgFull.append( c );
              break;

            case STATEOPTKEY:
              strbufOptKey.append( c );
              strbufOptKeyValue.append( c );
              break;

            case STATEOPTVAL:
              if( cOld == '=')
              {
                String strTmp = strbufLine.substring( i);
                if( strTmp != null && strTmp.length() > 2)
                {
                  int iIdx = strTmp.indexOf( "\"", 1);
                  if( iIdx == -1)
                  {
                    strbufOptValue.append( strTmp.substring( 1));
                    strbufOptKeyValue.append( strTmp.substring( 0));
                    i = strbufLine.length()-1;
                  }
                  else
                  {
                    strbufOptValue.append( strTmp.substring( 1, iIdx));
                    strbufOptKeyValue.append( strTmp.substring( 0, iIdx+1));
                    i += iIdx;
                  }
                  c = strbufLine.charAt( i);
                }
              }
              else
              {
                strbufOptValue.append( c );
                strbufOptKeyValue.append( c );
              }
              break;

            case STATENONE:
              sStateOld = sState;
              sState = STATEPUREARG;
              String strTmp = strbufLine.substring( i);
              if( strTmp != null && strTmp.length() > 2)
              {
                int iIdx = strTmp.indexOf( "\"", 1);
                if( iIdx == -1)
                {
                  strbufPureArg.append( strTmp.substring( 1));
                  strbufPureArgFull.append( strTmp.substring( 0));
                  i = strbufLine.length()-1;
                }
                else
                {
                  strbufPureArg.append( strTmp.substring( 1, iIdx));
                  strbufPureArgFull.append( strTmp.substring( 0, iIdx+1));
                  i += iIdx;
                }
                c = strbufLine.charAt( i);
              }
              break;
          }
          break;

        case '\'':
          switch( sState )
          {
            case STATECOMMAND:
              strbufCmd.append( c );
              break;

            case STATEPUREARG:
              strbufPureArg.append( c );
              strbufPureArgFull.append( c );
              break;

            case STATEOPTKEY:
              strbufOptKey.append( c );
              strbufOptKeyValue.append( c );
              break;

            case STATEOPTVAL:
              if( cOld == '=')
              {
                String strTmp = strbufLine.substring( i);
                if( strTmp != null && strTmp.length() > 2)
                {
                  int iIdx = strTmp.indexOf( "\'", 1);
                  if( iIdx == -1)
                  {
                    strbufOptValue.append( strTmp.substring( 1));
                    strbufOptKeyValue.append( strTmp.substring( 0));
                    i = strbufLine.length()-1;
                  }
                  else
                  {
                    strbufOptValue.append( strTmp.substring( 1, iIdx));
                    strbufOptKeyValue.append( strTmp.substring( 0, iIdx+1));
                    i += iIdx;
                  }
                  c = strbufLine.charAt( i);
                }
              }
              else
              {
                strbufOptValue.append( c );
                strbufOptKeyValue.append( c );
              }
              break;

            case STATENONE:
              sStateOld = sState;
              sState = STATEPUREARG;
              String strTmp = strbufLine.substring( i);
              if( strTmp != null && strTmp.length() > 2)
              {
                int iIdx = strTmp.indexOf( "\'", 1);
                if( iIdx == -1)
                {
                  strbufPureArg.append( strTmp.substring( 1));
                  strbufPureArgFull.append( strTmp.substring( 0));
                  i = strbufLine.length()-1;
                }
                else
                {
                  strbufPureArg.append( strTmp.substring( 1, iIdx));
                  strbufPureArgFull.append( strTmp.substring( 0, iIdx+1));
                  i += iIdx;
                }
                c = strbufLine.charAt( i);
              }
              break;
          }
          break;

        case '&':
          if( i == strbufLine.length()-1)
            m_blExecBackground = true;
          else
          {
            if( sState == STATENONE)
            {
              switch( sStateOld)
              {
                case STATECOMMAND:
                case STATEPUREARG:
                case STATEOPTKEY:
                case STATEOPTVAL:
                  sState = STATEPUREARG;
                  break;
              }
            }
            switch( sState)
            {
              case STATECOMMAND:
                strbufCmd.append( c );
                break;

              case STATEPUREARG:
                strbufPureArg.append( c );
                strbufPureArgFull.append( c );
                break;

              case STATEOPTKEY:
                strbufOptKey.append( c );
                strbufOptKeyValue.append( c );
                break;

              case STATEOPTVAL:
                strbufOptValue.append( c );
                strbufOptKeyValue.append( c );
                break;
            }
          }
          break;

        case '-':
          switch( sState )
          {
            case STATECOMMAND:
              strbufCmd.append( c );
              break;

            case STATEPUREARG:
              strbufPureArg.append( c );
              strbufPureArgFull.append( c );
              break;

            case STATEOPTKEY:
              strbufOptKey.append( c );
              strbufOptKeyValue.append( c );
              break;

            case STATEOPTVAL:
              strbufOptValue.append( c );
              strbufOptKeyValue.append( c );
              break;

            case STATENONE:
              sStateOld = sState;
              sState = STATEOPTKEY;
              strbufOptKey.append( c );
              strbufOptKeyValue.append( c );
              break;
          }
          break;

        case '=':
          switch( sState )
          {
            case STATECOMMAND:
              strbufCmd.append( c );
              break;

            case STATEPUREARG:
              strbufPureArg.append( c );
              strbufPureArgFull.append( c );
              break;

            case STATEOPTKEY:
              sStateOld = sState;
              sState = STATEOPTVAL;
              strbufOptKeyValue.append( c );
              break;

            case STATEOPTVAL:
              strbufOptValue.append( c );
              strbufOptKeyValue.append( c );
              break;

            case STATENONE:
              sStateOld = sState;
              sState = STATEPUREARG;
              strbufPureArg.append( c );
              strbufPureArgFull.append( c );
              break;
          }
          break;

        default:
          if( sState == STATENONE)
          {
            switch( sStateOld)
            {
              case STATECOMMAND:
              case STATEPUREARG:
              case STATEOPTKEY:
              case STATEOPTVAL:
                sState = STATEPUREARG;
                break;
            }
          }
          switch( sState)
          {
            case STATECOMMAND:
              strbufCmd.append( c );
              break;

            case STATEPUREARG:
              strbufPureArg.append( c );
              strbufPureArgFull.append( c );
              break;

            case STATEOPTKEY:
              strbufOptKey.append( c );
              strbufOptKeyValue.append( c );
              break;

            case STATEOPTVAL:
              strbufOptValue.append( c );
              strbufOptKeyValue.append( c );
              break;
          }
          break;
      }
      cOld = c;
    }
    switch( sState )
    {
      case STATECOMMAND:
        if( !strbufCmd.toString().equals( ""))
        {
          m_strCmd = strbufCmd.toString();
          m_vecArgs.add( m_strCmd );
        }
        break;

      case STATEPUREARG:
        if( !strbufPureArgFull.toString().equals( ""))
        {
          m_vecArgs.add( strbufPureArgFull.toString() );
          m_vecArgsWithoutOpts.add( strbufPureArg.toString() );
        }
        break;

      case STATEOPTKEY:
        if( !strbufOptKey.toString().equals( ""))
        {
          m_vecArgs.add( strbufOptKey.toString() );
          m_hmOptions.put( strbufOptKey.toString(), "" );
        }
        break;

      case STATEOPTVAL:
        if( !strbufOptKeyValue.toString().equals( ""))
        {
          m_vecArgs.add( strbufOptKeyValue.toString() );
          m_hmOptions.put( strbufOptKey.toString(), strbufOptValue.toString() );
        }
        break;
    }
  }
*/
  public StringBuffer replaceVariables( String strLine)
  {
    if( strLine == null)
      return null;
    StringBuffer strbufRet = new StringBuffer( strLine);
    int iIdx = 0;
    while( (iIdx = strbufRet.indexOf( "$", iIdx)) != -1)
    {
      if( iIdx > 0 && strbufRet.charAt( iIdx-1) == '\\' && !( iIdx >= 1 && strbufRet.charAt( iIdx-2) == '\\'))
      {
        strbufRet.deleteCharAt( iIdx-1);
        continue;
      }
      ++iIdx;
      StringBuffer strbuf = new StringBuffer();
      for( int i = iIdx; i < strbufRet.length(); ++i)
      {
        char c = strbufRet.charAt( i);
        if( c == ' ' || c == '\t' || c == '\n')
          break;
        strbuf.append( c);
        String strVal = System.getProperty( strbuf.toString());
        if( strVal != null)
        {
          int iLen = strbuf.length();
          strbufRet.replace( iIdx-1, iIdx+iLen, strVal);
          --iIdx;
        }
      }
    }
    return strbufRet;
  }

  public CommandLineContainer getCommandLineContainer()
  {
    return m_commandLineContainer;
  }

  public void setCommandLineContainer( CommandLineContainer lineContainer)
  {
    m_commandLineContainer = lineContainer;
  }
  
  public String getCurrentLine()
  {
    return m_strLine;
  }
  
  public void setCurrentLine( String strLine)
  {
    m_strLine = strLine;
  }

  public Vector getCommands()
  {
    return m_vecCmds;
  }

  public void setCommands( Vector cmds)
  {
    m_vecCmds = cmds;
  }
  
  public void addCommand( String cmd)
  {
    m_vecCmds.add( cmd);
  }

  public CommandLineIteration iterationObject()
  {
    return new CommandLineIterationHandler();
  }

  public class CommandLineContainer
  {
    private String m_strCmd;
    private HashMap m_hmOptions = new HashMap();
    private Vector m_vecArgsWithoutOpts = new Vector();
    private Vector m_vecArgs = new Vector();
    private boolean m_blExecBackground = false;

    protected Object clone()
    {
      CommandLineContainer commandLineContainer = new CommandLineContainer();
      commandLineContainer.setCurrentLine( new String( getCurrentLine()));
      commandLineContainer.setCommands( (Vector)getCommands().clone());
      commandLineContainer.setOptions( (HashMap)getOptions().clone());
      commandLineContainer.setArguments( (Vector)getArguments().clone());
      commandLineContainer.setArgumentsWithoutOptions( (Vector)getArgumentsWithoutOptions().clone());
      commandLineContainer.setExecInBackground( execInBackground());
      return commandLineContainer;
    }
    
    public ArgumentIteration iterationObject()
    {
      return new ArgumentIterationHandler( this);
    }
    
    public JShellEnv getShellEnv()
    {
      return m_jse;
    }

    public final void reset()
    {
      setCommand( null);
      getArguments().removeAllElements();
      getArgumentsWithoutOptions().removeAllElements();
      getOptions().clear();
      setExecInBackground( false);
    }
    
    public String getCurrentLine()
    {
      return m_strLine;
    }
    
    public void setCurrentLine( String strLine)
    {
      m_strLine = strLine;
    }

    public Vector getCommands()
    {
      return m_vecCmds;
    }

    public void setCommands( Vector cmds)
    {
      m_vecCmds = cmds;
    }
    
    public void addCommand( String cmd)
    {
      m_vecCmds.add( cmd);
    }

    public final String getCommand()
    {
      return m_strCmd;
    }
    
    public void setCommand( String strCmd)
    {
      m_strCmd = strCmd;
    }

    public final HashMap getOptions()
    {
      return m_hmOptions;
    }
    
    public void setOptions( HashMap hmOptions)
    {
      m_hmOptions = hmOptions;
    }

    public final boolean execInBackground()
    {
      return m_blExecBackground;
    }
    
    public void setExecInBackground( boolean blExecBackground)
    {
      m_blExecBackground = blExecBackground;
    }

    public final String[] getArgumentsWithoutOptionsAsString()
    {
      String[] strArray = null;
      if( m_vecArgsWithoutOpts.size() > 0)
        strArray = new String[m_vecArgsWithoutOpts.size()];
      for( int i = 0; i < m_vecArgsWithoutOpts.size(); ++i)
      {
        strArray[i] = (String)m_vecArgsWithoutOpts.elementAt(i);
      }
      return strArray;
    }
    
    public Vector getArgumentsWithoutOptions()
    {
      return m_vecArgsWithoutOpts;
    }
    
    public void setArgumentsWithoutOptions( Vector vecArgsWithoutOpts)
    {
      m_vecArgsWithoutOpts = vecArgsWithoutOpts;
    }
    
    public Vector getArguments()
    {
      return m_vecArgs;
    }
    
    public void setArguments( Vector vecArgs)
    {
      m_vecArgs = vecArgs;
    }

    public void addArgument( String strArg)
    {
      m_vecArgs.add( strArg);
    }
    
    public void addArgumentWithoutOption( String strArg)
    {
      m_vecArgsWithoutOpts.add( strArg);
    }

  }
  
  /**
   * Module:      JShellCommandProcessor$ArgumentIterationHandler<br> <p> Description: JShellCommandProcessor$ArgumentIterationHandler<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
   * @history      08.04.2008 by AE: Created.<br>  </p><p>
   * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
   * @version      v0.1, 2008; %version: %<br>  </p><p>
   * @since        JDK1.4  </p>
   */
  public class ArgumentIterationHandler implements ArgumentIteration
  {
    private int m_iArgPos = -1;
    private CommandLineContainer m_clc;

    public ArgumentIterationHandler( CommandLineContainer clc)
    {
      this.m_clc = clc;
    }
    
    public CommandLineContainer getCommandLineContainer()
    {
      return m_clc;
    }

    protected final void reset()
    {
      m_iArgPos = -1;
    }
    
    public boolean hasNextArgument()
    {
      boolean blExist = false;
      if( m_iArgPos+1 < getCommandLineContainer().getArguments().size())
        blExist = true;
      return blExist;
    }
    
    public final String getArguments()
    {
      m_iArgPos = 0;
      String strRet = readNextArgument();
      String strTmp = null;
      while( (strTmp = readNextArgument()) != null)
      {
        strRet += " " + strTmp;
      }
      return strRet;
    }
    
    public final String[] getArgumentArray()
    {
      int iSize = getCommandLineContainer().getArguments().size()-1;
      String[] strRet = new String[iSize];
      m_iArgPos = 0;
      String strTmp = null;
      for( int i = 0; i < iSize; ++i)
      {
        strTmp = readNextArgument();
        strRet[i] = strTmp;
      }
      return strRet;
    }

    public final String getArgumentsFromPosition()
    {
      String strRet = readNextArgument();
      String strTmp = null;
      while( (strTmp = readNextArgument()) != null)
      {
        strRet += " " + strTmp;
      }
      return strRet;
    }
    
    public final String[] getArgumentArrayFromPosition()
    {
      int iSize = getCommandLineContainer().getArguments().size()-m_iArgPos+1;
      String[] strRet = new String[iSize];
      String strTmp = null;
      for( int i = 0; i < iSize; ++i)
      {
        strTmp = readNextArgument();
        strRet[i] = strTmp;
      }
      return strRet;
    }

    public final String readNextArgument()
    {
      ++m_iArgPos;
      return readArgumentAt( m_iArgPos);
    }

    public final String readArgumentAt( int iArgPos)
    {
      m_iArgPos = iArgPos;
      String strRet = null;
      if( m_iArgPos < getCommandLineContainer().getArguments().size() )
      {
        strRet = ( String )getCommandLineContainer().getArguments().elementAt( iArgPos );
      }
      return strRet;
    }
  }
  
  /**
   * Module:      JShellCommandProcessor$CommandLineIterationHandler<br> <p> Description: JShellCommandProcessor$CommandLineIterationHandler<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
   * @history      08.04.2008 by AE: Created.<br>  </p><p>
   * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
   * @version      v0.1, 2008; %version: %<br>  </p><p>
   * @since        JDK1.4  </p>
   */
  public class CommandLineIterationHandler implements CommandLineIteration
  {
    private int m_iCmdPos = -1;
    private CommandLineContainer m_clc = null;
    
    public CommandLineIterationHandler()
    {
      m_clc = (CommandLineContainer)m_commandLineContainer.clone();
    }
    
    public CommandLineContainer getCommandLineContainer()
    {
      return m_clc;
    }

    public boolean hasNextCommandLine()
    {
      boolean blExist = false;
      if( m_iCmdPos+1 < getCommandLineContainer().getCommands().size())
        blExist = true;
      return blExist;
    }
    
    public final String readNextCommandLine()
    {
      ++m_iCmdPos;
      String strRet = null;
      if( m_iCmdPos < getCommandLineContainer().getCommands().size() )
      {
        strRet = ( String )getCommandLineContainer().getCommands().elementAt( m_iCmdPos );
        processLine( getCommandLineContainer(), strRet);
      }
      else
      {
        getCommandLineContainer().getCommands().removeAllElements();
        m_iCmdPos = -1;
      }
      return strRet;
    }

    protected void processLine( CommandLineContainer container, String strLine)
    {
      JShellCommandProcessor.this.processLine( container, strLine);
    }
  }
  
  public class ProcessLineCache
  {
    private StringBuffer strbufLine;
    private StringBuffer strbufCmd = new StringBuffer();
    private StringBuffer strbufPureArg = new StringBuffer();
    private StringBuffer strbufPureArgFull = new StringBuffer();
    private StringBuffer strbufOptKey = new StringBuffer();
    private StringBuffer strbufOptValue = new StringBuffer();
    private StringBuffer strbufOptKeyValue = new StringBuffer();
    private char c = ' ';
    private char cOld = ' ';
    private int iPos = 0;
    private boolean blProccessed = false;

    public boolean isProccessed()
    {
      return blProccessed;
    }

    public void setProccessed( boolean blProccessed)
    {
      this.blProccessed = blProccessed;
    }

    public boolean isLineEnd()
    {
      return getPosition() >= getLine().length()-1;
    }
    
    public ProcessLineCache( StringBuffer strbufLine)
    {
      super();
      this.strbufLine = strbufLine;
    }

    public StringBuffer getCommand()
    {
      return strbufCmd;
    }

    public void setCommand( StringBuffer strbufCmd)
    {
      this.strbufCmd = strbufCmd;
    }

    public StringBuffer getLine()
    {
      return strbufLine;
    }

    public void setLine( StringBuffer strbufLine)
    {
      this.strbufLine = strbufLine;
    }

    public StringBuffer getOptionKey()
    {
      return strbufOptKey;
    }

    public void setOptionKey( StringBuffer strbufOptKey)
    {
      this.strbufOptKey = strbufOptKey;
    }

    public StringBuffer getOptionKeyValue()
    {
      return strbufOptKeyValue;
    }

    public void setOptionKeyValue( StringBuffer strbufOptKeyValue)
    {
      this.strbufOptKeyValue = strbufOptKeyValue;
    }

    public StringBuffer getOptionValue()
    {
      return strbufOptValue;
    }

    public void setOptionValue( StringBuffer strbufOptValue)
    {
      this.strbufOptValue = strbufOptValue;
    }

    public StringBuffer getPureArgument()
    {
      return strbufPureArg;
    }

    public void setPureArgument( StringBuffer strbufPureArg)
    {
      this.strbufPureArg = strbufPureArg;
    }

    public StringBuffer getPureArgumentFull()
    {
      return strbufPureArgFull;
    }

    public void setPureArgumentFull( StringBuffer strbufPureArgFull)
    {
      this.strbufPureArgFull = strbufPureArgFull;
    }

    public char getCurrentChar()
    {
      return c;
    }

    public void setCurrentChar( char c)
    {
      cOld = this.c;
      this.c = c;
    }

    public char getOldChar()
    {
      return cOld;
    }

//    public void setOldChar( char old)
//    {
//      cOld = old;
//    }

    public int getPosition()
    {
      return iPos;
    }

    public void setPosition( int pos)
    {
      iPos = pos;
    }
  }
}