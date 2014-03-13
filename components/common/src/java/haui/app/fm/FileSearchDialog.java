package haui.app.fm;

import haui.components.JExDialog;
import haui.util.AppProperties;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Module:      FileSearchDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileSearchDialog.java,v $ <p> Description: file search dialog.<br> </p><p> Created:     20.08.2004 by AE </p><p>
 * @history      20.08.2004 AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileSearchDialog.java,v $  Revision 1.0  2004-08-31 15:57:36+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: 1.0 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileSearchDialog.java,v 1.0 2004-08-31 15:57:36+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.2  </p>
 */
public class FileSearchDialog
  extends JExDialog
{
  private static final long serialVersionUID = 9011251943800760926L;
  
  BorderLayout m_borderLayoutMain = new BorderLayout();
  FileSearchPanel m_fileSearchPanel = null;

  public FileSearchDialog( Component frame, String title, boolean modal, String strAppName,
                           FileInfoPanel fip, AppProperties appProps)
  {
    super( frame, title, modal, strAppName);
    try
    {
      m_fileSearchPanel = new FileSearchPanel( fip, appProps, strAppName)
                          {
                            private static final long serialVersionUID = 9011251943800760925L;
        
                            public boolean cancel()
                            {
                              boolean blCancel = super.cancel();
                              if( !blCancel)
                                FileSearchDialog.this.setVisible( false);
                              return blCancel;
                            }
                          };
      jbInit();
      pack();
      getRootPane().setDefaultButton( m_fileSearchPanel.getDeaultButton());
      AdpWindow adpWindow = new AdpWindow();
      this.addWindowListener( adpWindow);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    this.getContentPane().setLayout( m_borderLayoutMain);
    this.getContentPane().add( m_fileSearchPanel,  BorderLayout.CENTER);
  }

  class AdpWindow
    extends WindowAdapter
  {
    public void windowClosing( WindowEvent event)
    {
      Object object = event.getSource();
      if( object == FileSearchDialog.this)
      {
        closeDialog();
      }
    }
  }

  public void closeDialog()
  {
    m_fileSearchPanel.cancel();
    setVisible( false);
  }
}