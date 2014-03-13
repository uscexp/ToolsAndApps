package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.app.Shell.util.*;

import java.text.Collator;
import java.util.Vector;

/**
 *    Module:       JShellCommandDefault.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandDefault.java,v $
 *<p>
 *    Description:  Java shell default command implementation.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: JShellCommandDefault.java,v $
 *    Revision 1.0  2004-06-22 14:06:45+02  t026843
 *    Initial revision
 *
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: 1.0 $<br>
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandDefault.java,v 1.0 2004-06-22 14:06:45+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public abstract class JShellCommandDefault
  implements JShellCommandInterface
{
  private JShellCommandProcessor m_jscp = null;
  private String m_strDisplayName = null;

  public boolean isHiddenCommand()
  {
    return false;
  }

  public final void setCommand( JShellCommandProcessor jscp)
  {
    m_jscp = jscp;
  }

  public final JShellCommandProcessor getCommand()
  {
    return m_jscp;
  }

  public final String getCommandString()
  {
    if( m_jscp == null)
      return null;
    return m_jscp.getCommand();
  }

  public final void setDisplayString( String strDisplayName )
  {
    m_strDisplayName = strDisplayName;
  }

  public final String getDisplayString()
  {
    if( m_strDisplayName == null )
    {
      return "";
    }
    return m_strDisplayName;
  }

  public final String getHelpString()
  {
    String strRet = StringUtil.fullfillStringLengthLeft( getCommandString(), 8 ) + ": "
                    + getDisplayString();
    return strRet;
  }

  public void registerImplementedCommand( Vector vecList )
  {
    vecList.addElement( getCommandString() );
    vecList.addElement( getCommand().getCommand());
  }

  public boolean isThisCommand( JShellEnv jse, JShellCommandProcessor jscp)
  {
    Collator collator = jse.getCollatorInstance();
    return collator.equals( jscp.getCommand(), getCommandString() );
  }
}