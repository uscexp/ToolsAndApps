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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * JDiff's About dialog.
 */
public class AboutJDiff
  extends JExDialog
{
  private static final long serialVersionUID = 6505304128242990749L;
  
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JButton jButton1 = new JButton();

  public AboutJDiff( Component parent, String strAppName )
  {
    super( parent, "About JDiff", true, strAppName );
    try
    {
      jbInit();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    this.getContentPane().setLayout( gridBagLayout1 );
    this.setModal( true );
    this.setTitle( "About JDiff" );
    this.setSize( new Dimension( 232, 94 ) );
    jLabel1.setText( "JDiff version 0.3, copyright 2002" );
    jButton1.setText( "O K" );
    jButton1.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        OKClicked( e );
      }
    } );
    this.getContentPane().add( jLabel1,
                               new GridBagConstraints( 0, 0, 5, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 5, 0, 5, 0 ), 0, 0 ) );
    this.getContentPane().add( jButton1,
                               new GridBagConstraints( 4, 1, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 2, 0, 3, 0 ), 0, 0 ) );
  }

  private void OKClicked( ActionEvent e )
  {
    dispose();
  }
}