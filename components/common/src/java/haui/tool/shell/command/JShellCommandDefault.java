package haui.tool.shell.command;

import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.util.StringUtil;
import java.text.Collator;
import java.util.Vector;

/**
 * Module:       JShellCommandDefault.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandDefault.java,v $ <p> Description:  Java shell default command implementation.<br> </p><p> Created:	    23.03.2004  by AE </p><p>
 * @history       23.03.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: JShellCommandDefault.java,v $  Revision 1.0  2004-06-22 14:06:45+02  t026843  Initial revision  </p><p>
 * @author        Andreas Eisenhauer  </p><p>
 * @version       v1.0, 2004; $Revision: 1.0 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandDefault.java,v 1.0 2004-06-22 14:06:45+02 t026843 Exp t026843 $  </p><p>
 * @since         JDK1.3  </p>
 */
public abstract class JShellCommandDefault
  implements JShellCommandInterface
{
  private CommandLineContainer m_clc = null;
  private String m_strDisplayName = null;

  public boolean isHiddenCommand()
  {
    return false;
  }

  public final void setCommand( CommandLineContainer clc)
  {
    m_clc = clc;
  }

  public final CommandLineContainer getCommand()
  {
    return m_clc;
  }

  public final String getCommandString()
  {
    if( m_clc == null)
      return null;
    return m_clc.getCommand();
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

  public boolean isThisCommand( JShellEnv jse, CommandLineContainer clc)
  {
    Collator collator = jse.getCollatorInstance();
    return collator.equals( clc.getCommand(), getCommandString() );
  }
}