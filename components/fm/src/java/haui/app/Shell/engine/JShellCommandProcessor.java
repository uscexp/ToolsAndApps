package haui.app.Shell.engine;

import java.util.*;

/**
 *    Module:       JShellCommandProcessor.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellCommandProcessor.java,v $
 *<p>
 *    Description:  Java shell command handler.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: JShellCommandProcessor.java,v $
 *    Revision 1.1  2004-08-31 16:03:27+02  t026843
 *    Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *    Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *    Revision 1.0  2004-06-22 14:06:51+02  t026843
 *    Initial revision
 *
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: 1.1 $<br>
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellCommandProcessor.java,v 1.1 2004-08-31 16:03:27+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class JShellCommandProcessor
{
  // constants
  public static final short STATENONE = 0;
  public static final short STATECOMMAND = 1;
  public static final short STATEPUREARG = 2;
  public static final short STATEOPTKEY = 3;
  public static final short STATEOPTVAL = 4;
  public static final short STATECMDNOTEXT = 0;
  public static final short STATECMDTEXT = 1;

  // member variables
  private JShellEnv m_jse;
  private String m_strLine;
  private Vector m_vecCmds = new Vector();
  private String m_strCmd;
  private HashMap m_hmOptions = new HashMap();
  private Vector m_vecArgsWithoutOpts = new Vector();
  private Vector m_vecArgs = new Vector();
  private int m_iCmdPos = -1;
  private int m_iArgPos = -1;
  private boolean m_blExecBackground = false;

  public JShellCommandProcessor( String strLine, JShellEnv jse )
  {
    m_strLine = strLine;
    m_jse = jse;
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

  public void processLine( String strLine)
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

  public String getCurrentLine()
  {
    return m_strLine;
  }

  public final void reset()
  {
    m_strCmd = null;
    m_vecArgs.removeAllElements();
    m_vecArgsWithoutOpts.removeAllElements();
    m_hmOptions.clear();
    m_iArgPos = -1;
    m_blExecBackground = false;
  }

  public final String readNextCommandLine()
  {
    ++m_iCmdPos;
    String strRet = null;
    if( m_iCmdPos < m_vecCmds.size() )
    {
      strRet = ( String )m_vecCmds.elementAt( m_iCmdPos );
      reset();
      processLine( strRet);
    }
    else
    {
      m_vecCmds.removeAllElements();
      m_iCmdPos = -1;
      reset();
    }
    return strRet;
  }

  public final String getCommand()
  {
    return m_strCmd;
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
    if( m_iArgPos < m_vecArgs.size() )
    {
      strRet = ( String )m_vecArgs.elementAt( iArgPos );
    }
    return strRet;
  }

  public final HashMap getOptions()
  {
    return m_hmOptions;
  }

  public final boolean execInBackground()
  {
    return m_blExecBackground;
  }

  public final String[] getArgumentsWithoutOptions()
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
    int iSize = m_vecArgs.size()-1;
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
    int iSize = m_vecArgs.size()-m_iArgPos+1;
    String[] strRet = new String[iSize];
    String strTmp = null;
    for( int i = 0; i < iSize; ++i)
    {
      strTmp = readNextArgument();
      strRet[i] = strTmp;
    }
    return strRet;
  }
}