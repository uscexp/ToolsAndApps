package haui.components;

import java.io.IOException;

/**
 * Module:					ProcessControl.java<br> $Source: $ <p> Description:    Extended JTextArea.<br> </p><p> Created:				27.06.2002	by	AE </p><p>
 * @history  				27.06.2002	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2002; $Revision: $<br>  $Header: $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ProcessControl
{
  // other constants

  // member variables
  ProcessOutputThread m_pot = null;
  ProcessInputThread m_pit = null;
  String m_strAppName;

  public void exec( String cmd, String strAppName)
  {
    try
    {
      m_strAppName = strAppName;
      Process proc = null;
      if( cmd != null && !cmd.equals( "") && !cmd.equals( " "))
        proc = Runtime.getRuntime().exec( cmd);
      if( proc != null)
      {
        m_pot = new ProcessOutputThread( m_strAppName, proc, true, true, null);
        m_pot.start();
        m_pit = new ProcessInputThread( m_strAppName, proc);
        m_pit.start();
      }
    }
    catch( IOException ioEx)
    {
      ioEx.printStackTrace();
    }
  }

  /**
   * Constructs a new ProcessControl.
   */
  public ProcessControl()
  {
  }

  //Main-Methode
  public static void main(String[] args)
  {
    ProcessControl pta = new ProcessControl();
    pta.exec( "cmd", null);
  }
}
