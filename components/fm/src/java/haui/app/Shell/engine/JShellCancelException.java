package haui.app.Shell.engine;

/**
 *    Module:       JShellCancelException.java<br>
 *                  $Source: $
 *<p>
 *    Description:  Java shell cancel exception.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: $
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: $<br>
 *                  $Header: $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class JShellCancelException
  extends JShellException
{

  public JShellCancelException()
  {
  }

  public JShellCancelException(String strMessage)
  {
    super(strMessage);
  }
}