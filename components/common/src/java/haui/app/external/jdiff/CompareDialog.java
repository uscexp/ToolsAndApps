/*
 Copyright (C) 2002  Hatem El-Kazak
 This software is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This software is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 For any questions regarding this software contact hbelkazak@yahoo.com.
 Hatem El-Kazak, 6 April 2002
 */

package haui.app.external.jdiff;

import haui.components.JExDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.util.AppProperties;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * A dialog that enables the user to select the two files to be compared.
 */
public class CompareDialog
  extends JExDialog
{
  private static final long serialVersionUID = -7444997866047177371L;
  
  public static final int OK = 1;
  public static final int CANCEL = 0;

  protected AppProperties m_appProps;

  private int status = 0;
  private JFileChooser fileChooser = new JFileChooser( "" );
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel file1 = new JLabel();
  JTextField txtFile1 = new JTextField();
  JTextField txtFile2 = new JTextField();
  JButton btnSelect1 = new JButton();
  JButton btnSelect2 = new JButton();
  JLabel file2 = new JLabel();
  JButton btnCompare = new JButton();
  JButton btnCancel = new JButton();

  public CompareDialog( String strAppName, AppProperties appProps)
  {
    this( null, "compare files", true, strAppName, appProps );
  }

  /**
   * constructor.
   */
  public CompareDialog( Component parent, String strAppName, AppProperties appProps )
  {
    this( parent, "compare files", true, strAppName, appProps );
  }

  /**
   * constructor.
   */
  public CompareDialog( Component parent, String title, boolean modal, String strAppName, AppProperties appProps )
  {
    super( parent, title, modal, strAppName );
    m_appProps = appProps;
    try
    {
      jbInit();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * initializes this instance of CompareDialog.
   */
  private void jbInit()
    throws Exception
  {
    this.setSize( new Dimension( 344, 135 ) );
    file1.setText( "File 1" );
    file2.setText( "File 2" );
    btnSelect1.setText( "select file" );
    btnSelect1.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        btnSelectFile1( e );
      }
    } );
    btnSelect2.setText( "select file" );
    btnSelect2.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        btnSelectFile2( e );
      }
    } );
    btnCompare.setText( "Compare" );
    btnCompare.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        btnCompare( e );
      }
    } );
    btnCancel.setText( "Cancel" );
    btnCancel.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        btnCancel( e );
      }
    } );

    this.getContentPane().setLayout( gridBagLayout1 );
    this.getContentPane().add( file1,
                               new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 5, 5, 5, 5 ), 0, 0 ) );
    this.getContentPane().add( file2,
                               new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 5, 5, 5, 5 ), 0, 0 ) );
    this.getContentPane().add( txtFile1,
                               new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 5, 0, 5, 0 ), 200, 5 ) );
    this.getContentPane().add( txtFile2,
                               new GridBagConstraints( 1, 1, 2, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 5, 0, 5, 0 ), 200, 5 ) );
    this.getContentPane().add( btnSelect1,
                               new GridBagConstraints( 3, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 5, 5, 5, 5 ), 0, 0 ) );
    this.getContentPane().add( btnSelect2,
                               new GridBagConstraints( 3, 1, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 5, 5, 5, 5 ), 0, 0 ) );
    this.getContentPane().add( btnCancel,
                               new GridBagConstraints( 2, 2, 1, 1, 0.0, 0.0,
      GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
    this.getContentPane().add( btnCompare,
                               new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
  }

  void btnCancel( ActionEvent e )
  {
    dispose();
  }

  /**
   * gets the first line to be compared.
   * @uml.property  name="file1"
   */
  public String getFile1()
  {
    return this.txtFile1.getText();
  }

  /**
   * gets the second line to be compared.
   * @uml.property  name="file2"
   */
  public String getFile2()
  {
    return this.txtFile2.getText();
  }

  private void btnCompare( ActionEvent e )
  {
    if( txtFile1.getText().trim().equals( "" ) || txtFile2.getText().trim().equals( "" ) )
    {
      JOptionPane.showMessageDialog( this, "missing file name", "", JOptionPane.ERROR_MESSAGE );
      return;
    }
    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, null, 0, 0,
        m_strAppName, m_appProps, true);
    FileInterface fi1 = FileConnector.createFileInterface( txtFile1.getText(), null, false, fic);
    FileInterface fi2 = FileConnector.createFileInterface( txtFile2.getText(), null, false, fic);
    if( !fi1.exists() || !fi2.exists() )
      JOptionPane.showMessageDialog( this, "invalid file name", "", JOptionPane.ERROR_MESSAGE );
    else
    {
      status = CompareDialog.OK;
      this.dispose();
    }
  }

  private void btnSelectFile1( ActionEvent e )
  {
    int val = fileChooser.showOpenDialog( this );
    if( val == JFileChooser.APPROVE_OPTION )
    {
      try
      {
        this.txtFile1.setText( fileChooser.getSelectedFile().getCanonicalPath() );
      }
      catch( IOException ioe )
      {
        JOptionPane.showMessageDialog( this, ioe.getMessage(), "", JOptionPane.ERROR_MESSAGE );
      }
    }
  }

  private void btnSelectFile2( ActionEvent e )
  {
    int val = fileChooser.showOpenDialog( this );
    if( val == JFileChooser.APPROVE_OPTION )
    {
      try
      {
        this.txtFile2.setText( fileChooser.getSelectedFile().getCanonicalPath() );
      }
      catch( IOException ioe )
      {
        JOptionPane.showMessageDialog( this, ioe.getMessage(), "", JOptionPane.ERROR_MESSAGE );
      }
    }
  }

  /**
   * gets the status by which the Dialog was closed, ie: OK if user clicked compare button or CANCEL if user clicked cancel button.
   * @uml.property  name="status"
   */
  public int getStatus()
  {
    return status;
  }
}