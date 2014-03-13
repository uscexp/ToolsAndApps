package haui.app.fm;

import haui.components.*;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.util.AppProperties;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 * Module:					FileConcatDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileConcatDialog.java,v $ <p> Description:    FileConcatDialog.<br> </p><p> Created:				10.03.2004	by	AE </p><p>
 * @history  				10.03.2004	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileConcatDialog.java,v $  Revision 1.1  2004-08-31 16:03:12+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.0  2004-06-22 14:06:43+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileConcatDialog.java,v 1.1 2004-08-31 16:03:12+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FileConcatDialog
  extends JExDialog
{
  private static final long serialVersionUID = -3693431914163875491L;
  
  // menber variables
  AppProperties m_appProps;
  /**
   * @uml.property  name="m_fis"
   * @uml.associationEnd  multiplicity="(0 -1)"
   */
  FileInterface[] m_fis;
  Thread m_th = null;
  boolean m_blCancel = false;

  // GUI member variables
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JButton m_jButtonCancel = new JButton();
  JPanel m_jPanelButton = new JPanel();
  JButton m_jButtonConcat = new JButton();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JPanel m_jPanelName = new JPanel();
  FlowLayout m_flowLayoutName = new FlowLayout();
  JLabel m_jLabelName = new JLabel();
  JTextField m_jTextFieldName = new JTextField();
  JPanel m_jPanelCenter = new JPanel();
  FileInfoTable m_fileInfoTable = new FileInfoTable( m_appProps);
  JScrollPane m_jScrollPaneFit = new JScrollPane();
  JProgressBar m_jProgressBarConcat = new JProgressBar();
  BorderLayout m_borderLayoutCenter = new BorderLayout();

  public FileConcatDialog( Frame frame, String title, boolean modal, FileInterface[] fis, AppProperties appProps )
  {
    super( frame, title, modal, FileManager.APPNAME);
    constructor( frame, fis, appProps );
  }

  public FileConcatDialog( Dialog dlg, String title, boolean modal, FileInterface[] fis, AppProperties appProps )
  {
    super( dlg, title, modal, FileManager.APPNAME);
    constructor( dlg, fis, appProps );
  }

  private void constructor( Component frame, FileInterface[] fis, AppProperties appProps )
  {
    m_fis = fis;
    m_appProps = appProps;
    m_fileInfoTable.setAppProperties( m_appProps);
    try
    {
      jbInit();
      super.setFrame( frame );
      pack();

      LisAction la = new LisAction();
      m_jButtonConcat.addActionListener( la);
      m_jButtonCancel.addActionListener( la);

      if( fis != null)
      {
        m_fileInfoTable.init( m_fis );
        int iCount = m_fis.length;
        int iNumLen = String.valueOf( iCount).length();
        String strName = m_fis[0].getName();
        if( iNumLen < strName.length())
          strName = strName.substring( 0, strName.length()-iNumLen);
        m_jTextFieldName.setText( strName);
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    m_jPanelButton.setLayout( m_flowLayoutButton );
    m_jButtonCancel.setText( "Cancel" );
    m_jButtonCancel.setActionCommand( "cancel" );
    this.getContentPane().setLayout( m_borderLayoutBase );
    m_jButtonConcat.setActionCommand( "concat" );
    m_jButtonConcat.setText( "Concat" );
    m_jPanelName.setLayout(m_flowLayoutName);
    m_jLabelName.setText("File name: ");
    m_flowLayoutName.setAlignment(FlowLayout.LEFT);
    m_jTextFieldName.setPreferredSize(new Dimension(200, 21));
    m_jTextFieldName.setText("");
    m_jPanelCenter.setLayout(m_borderLayoutCenter);
    m_jScrollPaneFit.setPreferredSize(new Dimension(350, 300));
    m_jPanelButton.add( m_jButtonConcat, null );
    m_jPanelButton.add( m_jButtonCancel, null );
    this.getContentPane().add(m_jPanelCenter,  BorderLayout.CENTER);
    this.getContentPane().add(m_jPanelName,  BorderLayout.NORTH);
    this.getContentPane().add( m_jPanelButton, BorderLayout.SOUTH );
    m_jScrollPaneFit.setViewportView(m_fileInfoTable);
    m_jPanelName.add(m_jLabelName, null);
    m_jPanelName.add(m_jTextFieldName, null);
    m_jPanelCenter.add(m_jScrollPaneFit,  BorderLayout.CENTER);
    m_jPanelCenter.add(m_jProgressBarConcat,  BorderLayout.SOUTH);
  }

  public void setVisible( boolean bl)
  {
    if( bl)
    {
      m_jButtonConcat.requestFocus();
      m_blCancel = false;
      if( m_th != null)
      {
        m_th.interrupt();
        m_th = null;
      }
    }
    super.setVisible( bl);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event )
    {
      getRootPane().getParent().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
      String cmd = event.getActionCommand();
      if( cmd == "concat" )
        onConcat();
      else if( cmd == "cancel" )
        onCancel();
      getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
    }
  }

  public void onCancel()
  {
    if( m_th != null && m_th.isAlive())
    {
      int iRet = JOptionPane.showConfirmDialog( this, "Cancel current concatenate process?",
                                                "Confirmation", JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE );
      if( iRet == JOptionPane.YES_OPTION )
      {
        m_blCancel = true;
        try
        {
          Thread.sleep( 100);
          if( m_th != null && m_th.isAlive())
            m_th.interrupt();
          Thread.sleep( 100);
          if( m_th != null && m_th.isAlive())
            m_th.stop();
        }
        catch( Exception ex)
        {
          ex.printStackTrace();
        }
      }
      else
        return;
    }
    m_blCancel = true;
    setVisible( false);
  }

  public void onConcat()
  {
    if( m_th != null && m_th.isAlive())
      return;
    m_th = new Thread()
    {
      public void run()
      {
        m_fileInfoTable.selectAll();
        FileInterface[] fis = m_fileInfoTable.getSelectedFileInterfaces();
        //long lCompSize = m_fileInfoTable.lengthOfSelectedFiles();
        long lCompSize = FileConnector.lengthAndCountOfFiles( fis, null, false)[0];
        long lProgress = 0;
        m_fileInfoTable.setRowSelectionInterval( 0, 0);
        byte[] buffer = new byte[1024];
        int iBufSize = 1024;
        String strName = m_jTextFieldName.getText();
        if( strName == null)
        {
          JOptionPane.showMessageDialog( FileConcatDialog.this, "Please insert file name!", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        String strAbsName = fis[0].getParent() + fis[0].separatorChar() + strName;
        File file = new File( strAbsName);
        int iExt = 1;
        if( file.exists())
        {
          strAbsName += ".bk0";
          file = new File( strAbsName);
        }
        while( file.exists())
        {
          strAbsName = strAbsName.substring( 0, strAbsName.length()-1) + String.valueOf( iExt);
          file = new File( strAbsName);
          ++iExt;
        }

        m_jProgressBarConcat.setMinimum( 0);
        m_jProgressBarConcat.setMaximum( (int)(lCompSize/1024l));
        try
        {
          BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( strAbsName));

          for( int i = 0; !m_blCancel && i < fis.length; ++i)
          {
            BufferedInputStream bis = fis[i].getBufferedInputStream();
            int iRead = 0;
            while( !m_blCancel && (iRead = bis.read( buffer, 0, iBufSize)) != -1)
            {
              lProgress += iRead;
              m_jProgressBarConcat.setValue( (int)(lProgress/1024l));
              bos.write( buffer, 0, iRead);
            }
            bos.flush();
            bis.close();
          }
          bos.close();
        }
        catch( Exception ex)
        {
          ex.printStackTrace();
        }
        setVisible( false);
      }
    };
    m_th.start();
  }
}
