/*
 * Copyright (c) 1999-2001 Keiron Liddle, Aftex Software
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
*/
/**
 * A very simple GUI for the BZip program.
 * Uses swing to display dialog boxes and windows.
 * Uses a thread to throw of the compress/decompress operation.
 * Also does testing of a bzipped file.
 *
 * TODO :	Improve the test result window.
 *			Show progress during compress/decompress/test
 */
package com.aftexsw.util.bzip;

import com.aftexsw.ui.*;
import com.aftexsw.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

class BZipGUI extends JFrame {
	public BZipGUI() {
		this.setTitle("BZip for Java");
		this.setBounds(new Rectangle(100, 100, 300, 260));
		Util.centerFrame(this);
		this.getContentPane().setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton but1 = new JButton("BZip file...");
		but1.addActionListener(new ActionListener() {
					       public void actionPerformed(ActionEvent event) {
						       handleOpenToZip();
					       }
				       }
				      );
		JButton but2 = new JButton("unBZip file...");
		but2.addActionListener(new ActionListener() {
					       public void actionPerformed(ActionEvent event) {
						       handleOpenToUnzip();
					       }
				       }
				      );
		JButton but3 = new JButton("test BZipped file...");
		but3.addActionListener(new ActionListener() {
					       public void actionPerformed(ActionEvent event) {
						       handleTest();
					       }
				       }
				      );
		JButton but4 = new JButton("About BZip...");
		but4.addActionListener(new ActionListener() {
					       public void actionPerformed(ActionEvent event) {
						       handleAbout();
					       }
				       }
				      );
		buttonPanel.add(but1);
		buttonPanel.add(but2);
		buttonPanel.add(but3);
		buttonPanel.add(but4);

		getContentPane().add(buttonPanel, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
					  public void windowClosing(WindowEvent e) {
						  System.exit(0);
					  }
				  }
				 );

		try {
			Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/bzip.jpeg"));
			if (logo != null) {
				JLabel canvas = new JLabel(new ImageIcon(logo));
				JPanel imagePanel = new JPanel();
				imagePanel.setLayout(new FlowLayout());
				imagePanel.add(canvas);
				getContentPane().add(imagePanel, BorderLayout.SOUTH);
			}
		} catch(Exception e) {}
	}

	/**
   * Module:      BZipGUI$TestZipThread<br> <p> Description: BZipGUI$TestZipThread<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
   * @history      08.04.2008 by AE: Created.<br>  </p><p>
   * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
   * @version      v0.1, 2008; %version: %<br>  </p><p>
   * @since        JDK1.4  </p>
   */
	class TestZipThread extends Thread {
		File in;
		//		BZipProgress guiProg = new BZipProgress();

		TestZipThread(File i) {
			in = i;
		}

		public void run() {
			if(BZip.test(in)) {
				final JDialog jd = new JDialog(new JFrame(), "Test", true);
				//				jd.getContentPane().setLayout(new BorderLayout());
				jd.getContentPane().add(new JLabel("The file appears to be OK."), BorderLayout.NORTH);
				JButton but = new JButton("OK");
				jd.getContentPane().add(but, BorderLayout.SOUTH);
				but.addActionListener(new ActionListener() {
							      public void actionPerformed(ActionEvent ae) {
								      jd.dispose();
							      }
						      }
						     );
				jd.setVisible(true);
			} else {
				final JDialog jd = new JDialog(new JFrame(), "Test", true);
				//				jd.getContentPane().setLayout(new BorderLayout());
				jd.getContentPane().add(new JLabel("The file appears to be corrupted."), BorderLayout.NORTH);
				JButton but = new JButton("OK");
				jd.getContentPane().add(but, BorderLayout.SOUTH);
				but.addActionListener(new ActionListener() {
							      public void actionPerformed(ActionEvent ae) {
								      jd.dispose();
							      }
						      }
						     );
				jd.setVisible(true);
			}
			//			guiProg.dispose();
		}
	}

	private void handleOpenToZip() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setCurrentDirectory(new File("/"));
		int retval = chooser.showDialog(this, null);
		if(retval == JFileChooser.APPROVE_OPTION) {
			File theFile = chooser.getSelectedFile();
			if(theFile != null) {
				if(theFile.isDirectory()) {}
				else {
					String in = theFile.toString();
					String out = in + ".bz2";
					ZipThread zp = new ZipThread(theFile, new File(out), 9);
					zp.start();
				}
			}
		}
	}

	private void handleOpenToUnzip() {
		BZipFileFilter bzipFilter;

		JFileChooser chooser = new JFileChooser();
		bzipFilter = new BZipFileFilter("bz2", "BZip2 Compressed Files");
		chooser.addChoosableFileFilter(bzipFilter);
		chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		//		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int retval = chooser.showDialog(this, null);
		if(retval == JFileChooser.APPROVE_OPTION) {
			File theFile = chooser.getSelectedFile();
			if(theFile != null) {
				if(theFile.isDirectory()) {}
				else {
					String in = theFile.toString();
					String out;
					out = in.substring(0, in.length() - 4);
					UnZipThread zp = new UnZipThread(theFile, new File(out));
					zp.start();
				}
			}
		}
	}

	private void handleTest() {
		BZipFileFilter bzipFilter;

		JFileChooser chooser = new JFileChooser();
		bzipFilter = new BZipFileFilter("bz2", "BZip2 Compressed Files");
		chooser.addChoosableFileFilter(bzipFilter);

		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int retval = chooser.showDialog(this, null);
		if(retval == JFileChooser.APPROVE_OPTION) {
			File theFile = chooser.getSelectedFile();
			if(theFile != null) {
				if(theFile.isDirectory()) {}
				else {
					TestZipThread zp = new TestZipThread(theFile);
					zp.start();
				}
			}
		}
	}

	static void handleAbout() {
		Splash splash = new Splash("BZip2 for Java v0.3", "images/aftexsw.jpeg");
		splash.setBounds(200, 200, 276, 130);
		Util.centerFrame(splash);
		splash.setVisible(true);
		PerishTimer timer = new PerishTimer(splash, 15000);
		timer.start();
	}
}

/**
 * Module:      BZipFileFilter<br> <p> Description: BZipFileFilter<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
 * @history      08.04.2008 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2008; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
class BZipFileFilter extends FileFilter {
	private String extension = null;
	private String description = null;
	private boolean useExtensionsInDescription = true;

	public BZipFileFilter(String extension) {
		this(extension, null);
	}

	public BZipFileFilter(String ext, String desc) {
		extension = ext;
		description = desc;
	}

	public boolean accept(File f) {
		if(f != null) {
			if(f.isDirectory()) {
				return true;
			}
			if(extension.equals(getExtension(f)))
				return true;
		}
		return false;
	}

	String getExtension(File f) {
		if(f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if(i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}

	/**
   * @return  Returns the description.
   * @uml.property  name="description"
   */
	public String getDescription() {
		return description;
	}
}
