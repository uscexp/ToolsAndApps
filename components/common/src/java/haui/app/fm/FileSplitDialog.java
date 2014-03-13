package haui.app.fm;

import haui.components.JExDialog;
import haui.components.VerticalFlowLayout;
import haui.io.FileInterface.FileInterface;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Module:					FileSplitDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileSplitDialog.java,v $ <p> Description:    FileSplitDialog.<br> </p><p> Created:				09.03.2004	by	AE </p><p>
 * @history  				09.03.2004	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileSplitDialog.java,v $  Revision 1.1  2004-08-31 16:03:13+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.0  2004-06-22 14:06:43+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileSplitDialog.java,v 1.1 2004-08-31 16:03:13+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FileSplitDialog
 extends JExDialog
{
  private static final long serialVersionUID = 6733214183914947006L;
  
  // member variables
  FileInterface m_fi;
  String m_strItems[] = { "1.4", "100 Zip", "650", "700", "4.7"};
  String m_strItemSizeType[] = { "MB", "MB", "MB", "MB", "GB"};
  long m_lItemSize[] = { 1468006l, 100000000l, 681574400l, 734003200l, 5046586572l};
  Thread m_th = null;
  boolean m_blCancel = false;

  // GUI member variables
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonCancel = new JButton();
  JButton m_jButtonSplit = new JButton();
  JPanel m_jPanelMain = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutMain = new VerticalFlowLayout();
  JPanel m_jPanelName = new JPanel();
  FlowLayout m_flowLayoutName = new FlowLayout();
  JLabel m_jLabelName = new JLabel();
  JTextField m_jTextFieldName = new JTextField();
  JLabel m_jLabelSize = new JLabel();
  JPanel m_jPanelSize = new JPanel();
  FlowLayout m_flowLayoutSize = new FlowLayout();
  JComboBox m_jComboBoxSize = new JComboBox();
  ButtonGroup m_buttonGroupSize = new ButtonGroup();
  JRadioButton m_jRadioButtonKb = new JRadioButton();
  JRadioButton m_jRadioButtonMb = new JRadioButton();
  JRadioButton m_jRadioButtonGb = new JRadioButton();
  JProgressBar m_jProgressBarSplit = new JProgressBar();

  public FileSplitDialog(Frame frame, String title, boolean modal, FileInterface fi)
  {
    super(frame, title, modal, FileManager.APPNAME);
    constructor( frame, fi);
  }

  public FileSplitDialog(Dialog dlg, String title, boolean modal, FileInterface fi)
  {
    super(dlg, title, modal, FileManager.APPNAME);
    constructor( dlg, fi);
  }

  private void constructor( Component frame, FileInterface fi)
  {
    m_fi = fi;
    try
    {
      jbInit();
      super.setFrame( frame);
      m_buttonGroupSize.add( m_jRadioButtonKb);
      m_buttonGroupSize.add( m_jRadioButtonMb);
      m_buttonGroupSize.add( m_jRadioButtonGb);

      for( int i = 0; i < m_strItems.length; ++i)
        m_jComboBoxSize.addItem( m_strItems[i]);
      m_jComboBoxSize.addItemListener( new LisItem());
      m_jComboBoxSize.setSelectedIndex( 0);
      m_jRadioButtonMb.setSelected( true);
      pack();
      LisAction la = new LisAction();
      m_jButtonSplit.addActionListener( la);
      m_jButtonCancel.addActionListener( la);
      m_jTextFieldName.setText( fi.getName());
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public void setVisible( boolean bl)
  {
    if( bl)
    {
      m_jButtonSplit.requestFocus();
      m_blCancel = false;
      if( m_th != null)
      {
        m_th.interrupt();
        m_th = null;
      }
    }
    super.setVisible( bl);
  }

  private void jbInit() throws Exception
  {
    m_flowLayoutName.setAlignment(FlowLayout.LEFT);
    m_jLabelName.setText("Filename: ");
    m_jTextFieldName.setPreferredSize(new Dimension(200, 21));
    m_jTextFieldName.setEditable(false);
    m_jTextFieldName.setText("");
    m_jLabelSize.setText("Size:        ");
    m_jPanelSize.setLayout(m_flowLayoutSize);
    m_flowLayoutSize.setAlignment(FlowLayout.LEFT);
    m_jComboBoxSize.setPreferredSize(new Dimension(100, 21));
    m_jComboBoxSize.setActionCommand("comboBoxSizeChanged");
    m_jComboBoxSize.setEditable(true);
    m_jRadioButtonKb.setText("KB");
    m_jRadioButtonMb.setText("MB");
    m_jRadioButtonGb.setText("GB");
    m_jPanelButton.add(m_jButtonSplit, null);
    m_jPanelButton.add(m_jButtonCancel, null);
    this.getContentPane().setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jButtonCancel.setActionCommand("cancel");
    m_jButtonCancel.setText("Cancel");
    m_jButtonSplit.setActionCommand("split");
    m_jButtonSplit.setText("Split");
    m_jPanelMain.setLayout(m_verticalFlowLayoutMain);
    m_jPanelName.setLayout(m_flowLayoutName);
    this.getContentPane().add(m_jPanelMain,  BorderLayout.CENTER);
    m_jPanelSize.add(m_jLabelSize, null);
    m_jPanelMain.add(m_jPanelName, null);
    this.getContentPane().add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelName.add(m_jLabelName, null);
    m_jPanelName.add(m_jTextFieldName, null);
    m_jPanelMain.add(m_jPanelSize, null);
    m_jPanelSize.add(m_jComboBoxSize, null);
    m_jPanelSize.add(m_jRadioButtonKb, null);
    m_jPanelSize.add(m_jRadioButtonMb, null);
    m_jPanelSize.add(m_jRadioButtonGb, null);
    m_jPanelMain.add(m_jProgressBarSplit, null);
  }

  class LisItem
   implements ItemListener
  {
    public void itemStateChanged( ItemEvent event)
    {
      int state = event.getStateChange();
      if( state == ItemEvent.SELECTED)
        onStateChanged();
    }
  }

  public void onStateChanged()
  {
    String strValue = (String)m_jComboBoxSize.getSelectedItem();

    if( strValue != null)
    {
      for( int i = 0; i < m_strItems.length; ++i)
      {
        if( strValue.equalsIgnoreCase( m_strItems[i]))
        {
          setTypeSelection( m_strItemSizeType[i] );
          break;
        }
      }
    }
  }

  public void setTypeSelection( String strType)
  {
    if( strType != null)
    {
      if( strType.equals( "KB"))
        m_jRadioButtonKb.setSelected( true);
      else if( strType.equals( "MB"))
        m_jRadioButtonMb.setSelected( true);
      else if( strType.equals( "GB"))
        m_jRadioButtonGb.setSelected( true);
    }
  }

  public long getSplitSize()
  {
    long lSize = 0;
    String strValue = (String)m_jComboBoxSize.getSelectedItem();

    if( strValue != null)
    {
      for( int i = 0; i < m_strItems.length; ++i)
      {
        if( strValue.equalsIgnoreCase( m_strItems[i]))
        {
          lSize = m_lItemSize[i];
          break;
        }
      }
    }
    if( lSize == 0)
    {
      try
      {
        lSize = Long.parseLong( strValue);
        if( m_jRadioButtonKb.isSelected())
          lSize *= 1024;
        else if( m_jRadioButtonMb.isSelected())
          lSize *= 1024*1024;
        else if( m_jRadioButtonGb.isSelected())
          lSize *= 1024*1024*1024;
      }
      catch( NumberFormatException nfex)
      {
        nfex.printStackTrace();
      }
    }
    return lSize;
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event )
    {
      getRootPane().getParent().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
      String cmd = event.getActionCommand();
      if( cmd == "split" )
        onSplit();
      else if( cmd == "cancel" )
        onCancel();
      getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
    }
  }

  public void onCancel()
  {
    if( m_th != null && m_th.isAlive())
    {
      int iRet = JOptionPane.showConfirmDialog( this, "Cancel current split process?",
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

  public void onSplit()
  {
    if( m_th != null && m_th.isAlive())
      return;
    m_th = new Thread()
    {
      public void run()
      {
        long lSlitSize = getSplitSize();
        long lFileSize = m_fi.length();

        if( lSlitSize > lFileSize)
        {
          setVisible( false);
          return;
        }
        long lFileCount = lFileSize/lSlitSize+1;
        int iNumLength = 1;
        iNumLength = String.valueOf( lFileCount).length();
        try
        {
          BufferedInputStream biOrg = m_fi.getBufferedInputStream();
          long lProgress = 0;
          long lPartProgress = 0;
          long lFileNum = 0;
          byte[] buffer = new byte[1024];
          int iRead = 0;
          m_jProgressBarSplit.setMinimum( 0);
          m_jProgressBarSplit.setMaximum( (int)(lFileSize/1024l));

          while( !m_blCancel && iRead != -1)
          {
            String strNum = String.valueOf( lFileNum);
            int iLenDiff = iNumLength - strNum.length();
            for( int i = 0; i < iLenDiff; ++i)
              strNum = "0" + strNum;
            String strPathName = m_fi.getAbsolutePath() + strNum;
            BufferedOutputStream boPart = new BufferedOutputStream( new FileOutputStream( strPathName));
            lPartProgress = 0;
            while( !m_blCancel && lPartProgress < lSlitSize && iRead != -1)
            {
              int iBufSize = 1024;
              if( lSlitSize-lPartProgress < iBufSize)
                iBufSize = (int)(lSlitSize-lPartProgress);
              if( iBufSize < 1)
                iBufSize = 1;
              if( (iRead = biOrg.read( buffer, 0, iBufSize)) != -1)
              {
                lProgress += iRead;
                lPartProgress += iRead;
                m_jProgressBarSplit.setValue( (int)(lProgress/1024l));
                boPart.write( buffer, 0, iRead);
              }
            }
            boPart.flush();
            boPart.close();
            ++lFileNum;
          }
          biOrg.close();
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