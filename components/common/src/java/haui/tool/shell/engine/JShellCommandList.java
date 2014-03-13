package haui.tool.shell.engine;

import haui.tool.shell.command.JShellCommandAlias;
import haui.tool.shell.command.JShellCommandBsh;
import haui.tool.shell.command.JShellCommandCat;
import haui.tool.shell.command.JShellCommandCd;
import haui.tool.shell.command.JShellCommandCls;
import haui.tool.shell.command.JShellCommandCp;
import haui.tool.shell.command.JShellCommandDlsocket;
import haui.tool.shell.command.JShellCommandEcho;
import haui.tool.shell.command.JShellCommandEnv;
import haui.tool.shell.command.JShellCommandFtp;
import haui.tool.shell.command.JShellCommandHelp;
import haui.tool.shell.command.JShellCommandInterface;
import haui.tool.shell.command.JShellCommandJexec;
import haui.tool.shell.command.JShellCommandJsh;
import haui.tool.shell.command.JShellCommandKill;
import haui.tool.shell.command.JShellCommandLs;
import haui.tool.shell.command.JShellCommandMkdir;
import haui.tool.shell.command.JShellCommandMv;
import haui.tool.shell.command.JShellCommandPs;
import haui.tool.shell.command.JShellCommandPwd;
import haui.tool.shell.command.JShellCommandRm;
import haui.tool.shell.command.JShellCommandSet;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.util.ConfigPathUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *    Module:       JShellCommandList.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellCommandList.java,v $
 *<p>
 *    Description:  Java shell command list of intern commands.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: JShellCommandList.java,v $
 *    Revision 1.1  2004-08-31 16:03:20+02  t026843
 *    Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *    Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *    Revision 1.0  2004-06-22 14:06:50+02  t026843
 *    Initial revision
 *
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: 1.1 $<br>
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellCommandList.java,v 1.1 2004-08-31 16:03:20+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class JShellCommandList
{
  public static final String[][] straryCommandList =
  {
    {"exec", "excecute program on system os"},
    {"exit", "exit                   , quit is alias"},
    {"quit", "exit"}
  };

  /**
   * list of implemented command object.
   */
  private Vector m_vecImplementedCommandElementList = new Vector( 128 );

  public JShellCommandList( JShellEnv jse)
  {
    init( jse);
  }

  public void init( JShellEnv jse)
  {
    for( int index = 0; index < straryCommandList.length; index++ )
    {
      String[] straryLook = straryCommandList[index];
      addImplementedCommand( new JShellCommandAlias( (new JShellCommandProcessor( jse, straryLook[0])).getCommandLineContainer(),
          straryLook[1] ) );
    }

    addImplementedCommand( new JShellCommandCat( jse) );
    addImplementedCommand( new JShellCommandEcho( jse) );
    //addImplementedCommand( new JShellCommandEdit( jse) );
    addImplementedCommand( new JShellCommandHelp( jse) );
    addImplementedCommand( new JShellCommandCd( jse) );
    addImplementedCommand( new JShellCommandMv( jse) );
    addImplementedCommand( new JShellCommandCp( jse) );
    addImplementedCommand( new JShellCommandRm( jse) );
    addImplementedCommand( new JShellCommandMkdir( jse) );
    addImplementedCommand( new JShellCommandLs( jse) );
    addImplementedCommand( new JShellCommandPwd( jse) );
    addImplementedCommand( new JShellCommandPs( jse) );
    addImplementedCommand( new JShellCommandKill( jse) );
    addImplementedCommand( new JShellCommandEnv( jse) );
    addImplementedCommand( new JShellCommandSet( jse) );
    addImplementedCommand( new JShellCommandJsh( jse) );
    addImplementedCommand( new JShellCommandJexec( jse) );
    addImplementedCommand( new JShellCommandCls( jse) );
    addImplementedCommand( new JShellCommandBsh( jse) );
    //addImplementedCommand( new JShellCommandJacksum( jse) );

    // commands with processor
    addImplementedCommand( new JShellCommandFtp( jse) );
    addImplementedCommand( new JShellCommandDlsocket( jse) );

    initFromFile( jse);

    sortImplementedCommand();
  }

  private void initFromFile( JShellEnv jse)
  {
    try
    {
      File fileLoad = new File( ConfigPathUtil.getCurrentReadPath( jse.getAppName(), JShellEnv.COMMANDFILE) );
      if( !fileLoad.exists())
      {
        return;
      }
      BufferedReader reader = new BufferedReader( new FileReader( fileLoad ) );
      String strRead;
      while( (strRead = reader.readLine()) != null)
      {
        StringTokenizer toknizerTab = new StringTokenizer( strRead, "\t\n", false );
        String strCommand = null;
        String strDisplayString = null;
        String strPathName = null;

        try
        {
          strCommand = toknizerTab.nextToken();
          strDisplayString = toknizerTab.nextToken();
          strPathName = toknizerTab.nextToken();
        }
        catch( NoSuchElementException ex )
        {
          // None.
        }

        if( strCommand != null )
        {
          /*
          if( strCommand.equals( "edit" ) )
          {
            // to hidden command.
            strCommand = "$" + strCommand;
          }
          */

          addImplementedCommand( new JShellCommandAlias( (new JShellCommandProcessor( jse, strCommand)).getCommandLineContainer(),
            strDisplayString, strPathName ) );
        }
      }
      reader.close();
    }
    catch( IOException ex )
    {
      jse.getErr().println( ex.toString() );
      ex.printStackTrace();
    }
  }

  protected void sortImplementedCommand()
  {
    Comparator cmparetorJShellCommandInterface = new Comparator()
    {
      public int compare( Object o1, Object o2 )
      {
        JShellCommandInterface f1 = ( JShellCommandInterface )o1;
        JShellCommandInterface f2 = ( JShellCommandInterface )o2;

        return f1.getCommandString().compareTo( f2.getCommandString() );
      }
    };

    Collections.sort( m_vecImplementedCommandElementList, cmparetorJShellCommandInterface );
  }

  /**
   *
   */
  protected void addImplementedCommand( JShellCommandInterface cmdelement )
  {
    m_vecImplementedCommandElementList.addElement( cmdelement );
  }

  public Vector getImplementedCommandList()
  {
    Vector vecRet = new Vector( 128 );
    for( int index = 0; index < m_vecImplementedCommandElementList.size(); index++ )
    {
      JShellCommandInterface cmdelement = ( JShellCommandInterface )
                                           m_vecImplementedCommandElementList.elementAt( index );
      cmdelement.registerImplementedCommand( vecRet );

    }
    return vecRet;
  }

  public String getImplementedCommandListForDisplayString()
  {
    StringBuffer strbuf = new StringBuffer();
    for( int index = 0; index < m_vecImplementedCommandElementList.size(); index++ )
    {
      JShellCommandInterface cmdelementLook = ( JShellCommandInterface )
                                               m_vecImplementedCommandElementList.elementAt( index );
      if( cmdelementLook.getCommandString().charAt( 0 ) == '$' )
      {
        continue;
      }

      strbuf.append( " " );
      strbuf.append( cmdelementLook.getHelpString() );

      // The pattern that it is desirable to acquire a line feed from the system in fact.
      // But, it was mentioned with \n because it was not that truth was.
      strbuf.append( "\n" );
    }
    return strbuf.toString();
  }

  public int processCommand( JShellEnv jse, CommandLineContainer clc)
    throws JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUE;
    for( int index = 0; index < m_vecImplementedCommandElementList.size(); index++ )
    {
      JShellCommandInterface cmdelementLook = ( JShellCommandInterface )
                                               m_vecImplementedCommandElementList.elementAt( index );
      if( cmdelementLook.isThisCommand( jse, clc ) )
      {
        iStatus = cmdelementLook.processCommand( jse, clc);
      }
    }
    return iStatus;
  }

  public JShellCommandInterface getShellCommand( JShellEnv jse, CommandLineContainer clc)
    throws JShellException
  {
    JShellCommandInterface jsci = null;
    for( int index = 0; index < m_vecImplementedCommandElementList.size(); index++ )
    {
      JShellCommandInterface cmdelementLook = ( JShellCommandInterface )
                                               m_vecImplementedCommandElementList.elementAt( index );
      if( cmdelementLook.isThisCommand( jse, clc) )
      {
        jsci = cmdelementLook;
        break;
      }
    }
    return jsci;
  }

  public void usage( JShellEnv jse, CommandLineContainer clc)
  {
    for( int index = 0; index < m_vecImplementedCommandElementList.size(); index++ )
    {
      JShellCommandInterface cmdelementLook = ( JShellCommandInterface )
                                               m_vecImplementedCommandElementList.elementAt( index );
      if( cmdelementLook.isThisCommand( jse, clc ) )
      {
        cmdelementLook.usage( jse);
      }
    }
  }

  public String getFullPathNameByCommand( String strCommand )
  {
    for( int index = 0; index < m_vecImplementedCommandElementList.size(); index++ )
    {
      JShellCommandInterface cmdelementLook = ( JShellCommandInterface )
                                               m_vecImplementedCommandElementList.elementAt( index );
      if( strCommand.equals( cmdelementLook.getCommandString() ) )
      {
        if( cmdelementLook instanceof JShellCommandAlias )
        {
          JShellCommandAlias cmdDef = ( JShellCommandAlias )cmdelementLook;
          return cmdDef.getFullPathName();
        }
        else
        {
          return null;
        }
      }
    }
    return null;
  }
}