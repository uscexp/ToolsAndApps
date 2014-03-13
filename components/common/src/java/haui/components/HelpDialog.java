package haui.components;

import haui.tool.HtmlPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import javax.swing.JPanel;

/**
 * Module:      HelpDialog.java<br> $Source: $ <p> Description: Help dialog.<br> </p><p> Created:     23.04.2003 by AE </p><p>
 * @history  	    23.04.2003 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2003; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.2  </p>
 */
public class HelpDialog
  extends JExDialog
{
  private static final long serialVersionUID = 9083015842222472080L;
  
  JPanel HelpPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  HtmlPanel HelpView = null;

  public HelpDialog(Frame frame, String title, boolean modal, String strAppName)
  {
    super(frame, title, modal, strAppName);
    HelpView = new HtmlPanel( strAppName);
    try
    {
      jbInit();
      pack();
      if( frame != null)
      {
        Point parloc = frame.getLocation();
        setLocation(parloc.x + 30, parloc.y + 30);
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public HelpDialog( String strAppName)
  {
    this(null, "", false, strAppName);
  }

  void jbInit() throws Exception
  {
    HelpPanel.setLayout(borderLayout1);
    HelpPanel.setMinimumSize(new Dimension(300, 300));
    HelpPanel.setPreferredSize(new Dimension(500, 500));
    getContentPane().add(HelpPanel);
    HelpPanel.add(HelpView, BorderLayout.CENTER);
  }

  public HtmlPanel getHtmlPanel()
  {
    return HelpView;
  }
}
