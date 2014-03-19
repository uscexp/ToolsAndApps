/**
 * 
 */
package haui.io.FileInterface.compress;

import haui.components.JExDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.BaseTypeFile;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.tool.shell.JShellPanel;
import haui.tool.shell.engine.JShellEngine;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * base FileInterface class for compress type files.
 * 
 * @author ae
 */
public abstract class CompressTypeFile extends BaseTypeFile {

	// member variables
	protected String archPath;
	protected String intPath;
	protected File file;

	public CompressTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(fic);
		file = new File(strArchPath);

		int idx = strArchPath.lastIndexOf(separatorChar());
		if (idx > 0 && idx == strArchPath.length() - 1)
			strArchPath = strArchPath.substring(0, strArchPath.length() - 1);
		archPath = strArchPath;
		idx = strIntPath.indexOf(separatorChar());
		if (idx == 0)
			strIntPath = strIntPath.substring(1, strIntPath.length());
		idx = strIntPath.lastIndexOf(separatorChar());
		if (idx > 0 && idx == strIntPath.length() - 1)
			strIntPath = strIntPath.substring(0, strIntPath.length() - 1);
		intPath = strIntPath;
		if (strParentPath == null) {
			String path = archPath + separatorChar() + intPath;
			idx = path.lastIndexOf(separatorChar());
			strParentPath = path.substring(0, idx);
		}
		parent = strParentPath;
		idx = strIntPath.lastIndexOf(separatorChar());
		name = intPath.substring(idx + 1, intPath.length());
	}
	
	@Override
	public BufferedOutputStream getBufferedOutputStream(String strNewPath)
			throws FileNotFoundException, IOException {
		return new BufferedOutputStream(new FileOutputStream(strNewPath));
	}

	@Override
	public char separatorChar() {
		return File.separatorChar;
	}

	@Override
	public char pathSeparatorChar() {
		return File.pathSeparatorChar;
	}

	@Override
	public boolean canRead() {
		return read;
	}

	@Override
	public boolean canWrite() {
		return write;
	}

	@Override
	public boolean isDirectory() {
		return directory;
	}

	@Override
	public boolean isFile() {
		return fileType;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public URL toURL() {
		URL url = null;
		try {
			url = new URL("file", null, getAbsolutePath());
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
		return url;
	}

	@Override
	public long length() {
		return length;
	}

	@Override
	public String getId() {
		return host;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getAbsolutePath() {
		return absolutePath;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getInternalPath() {
		return intPath;
	}

	@Override
	public FileInterface getDirectAccessFileInterface() {
		FileInterface fi = this;
		String strPath = getAbsolutePath();

		if (getInternalPath() != null && !getInternalPath().equals("")) {
			int iStat;
			iStat = JOptionPane.showConfirmDialog(GlobalApplicationContext
					.instance().getRootComponent(),
					"Extract to temp and execute?", "Confirmation",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (iStat == JOptionPane.NO_OPTION) {
				GlobalApplicationContext.instance().getOutputPrintStream()
						.println("...cancelled");
				// System.out.println( "...cancelled" );
				return null;
			}
			strPath = extractToTempDir();
			if (strPath != null)
				fi = FileConnector.createFileInterface(strPath, null, false,
						getFileInterfaceConfiguration());
		}
		return fi;
	}

	@Override
	public String getParent() {
		return parent;
	}

	@Override
	public FileInterface getParentFileInterface() {
		boolean blExtract = false;
		if (intPath != null && !intPath.equals("")
				&& intPath.indexOf(separatorChar()) != -1)
			blExtract = true;
		FileInterface fi = FileConnector.createFileInterface(getParent(), null,
				blExtract, getFileInterfaceConfiguration());
		return fi;
	}

	@Override
	public long lastModified() {
		return modified;
	}

	@Override
	public void logon() {
		return;
	}

	@Override
	public void logoff() {
		return;
	}

	@Override
	public void startTerminal() {
		final JShellPanel sp = new JShellPanel(getFileInterfaceConfiguration()
				.getAppName());
		final JExDialog dlg = new JExDialog(null, "JShell - " + getId(), false,
				getAppName());
		dlg.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				Object object = event.getSource();
				if (object == dlg) {
					sp.stop();
					super.windowClosing(event);
					dlg.dispose();
				}
			}
		});
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.getContentPane().add("Center", sp);
		dlg.pack();
		dlg.setVisible(true);
		Thread th = new Thread() {
			public void run() {
				try {
					StringBuffer strbufCmd = new StringBuffer("cd \"");
					strbufCmd.append(getAbsolutePath());
					strbufCmd.append("\"");
					sp.getShell().getShellEnv().setFileInterface(duplicate());
					JShellEngine.processCommands(strbufCmd.toString(), sp
							.getShell().getShellEnv(), false);
					sp.start();
					dlg.setVisible(false);
					dlg.dispose();
				} catch (Exception ex) {
					ex.printStackTrace();
					dlg.setVisible(false);
				}
			}
		};
		th.start();
	}

	@Override
	public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut,
			OutputStream osErr, InputStream is) {
		FileInterface fi = this;
		int iRet = -1;
		fi = getDirectAccessFileInterface();
		try {
			iRet = FileConnector.exec(fi, cmd.getCompleteCommand(fi,
					fi.getAbsolutePath(), strTargetPath), strTargetPath, cmd,
					osOut, osErr, is);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return iRet;
	}

	@Override
	public Object getConnObj() {
		return null;
	}

}
