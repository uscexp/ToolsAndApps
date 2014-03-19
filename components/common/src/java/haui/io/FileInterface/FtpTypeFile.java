package haui.io.FileInterface;

import haui.components.CancelProgressDialog;
import haui.components.JExDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.FtpTypeFileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.tool.shell.JShellPanel;
import haui.tool.shell.engine.JShellEngine;
import haui.util.AppProperties;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import cz.dhl.ftp.FtpConnect;
import cz.dhl.ftp.FtpFile;
import cz.dhl.io.CoFile;

/**
 * Module: FtpTypeFile.java<br>
 * $Source:
 * M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\FtpTypeFile
 * .java,v $
 * <p>
 * Description: Cache for file information.<br>
 * </p>
 * <p>
 * Created: 05.07.2002 by AE
 * </p>
 * <p>
 * 
 * @history 05.07.2002 by AE: Created.<br>
 *          </p>
 *          <p>
 *          Modification:<br>
 *          $Log: FtpTypeFile.java,v $ Revision 1.2 2004-08-31 16:03:18+02
 *          t026843 Large redesign for application dependent outputstreams,
 *          mainframes, AppProperties! Bugfixes to DbTreeTableView, additional
 *          features for jDirWork. Revision 1.1 2004-06-22 14:08:50+02 t026843
 *          bigger changes Revision 1.0 2003-06-06 10:05:36+02 t026843 Initial
 *          revision
 *          </p>
 *          <p>
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2002; $Revision: 1.2 $<br>
 *          $Header:
 *          M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\
 *          io\\FtpTypeFile.java,v 1.2 2004-08-31 16:03:18+02 t026843 Exp
 *          t026843 $
 *          </p>
 *          <p>
 * @since JDK1.2
 *        </p>
 */
public class FtpTypeFile implements FileInterface {
	// constants
	public final static String PROTOCOL = "ftp:";

	// member variables
	FtpFile ftpFile;
	protected FileInterfaceConfiguration m_fic = null;
	protected String m_ftpPath;
	protected String m_intPath;
	protected String m_parentPath;
	protected Vector m_ftpEntries = new Vector();
	protected String m_curFtpEntry;
	protected Process m_procCurrent = null;

	protected BufferedOutputStream m_bos = null;

	// cache variables
	protected boolean m_blRead = false;
	protected boolean m_blWrite = false;
	protected String m_strAbsolutePath;
	protected String m_strPath;
	protected String m_strName;
	protected String m_strParent;
	protected boolean m_blArchive = false;
	protected boolean m_blDirectory = false;
	protected boolean m_blFile = false;
	protected boolean m_blHidden;
	protected long m_lModified;
	protected long m_lLength;
	protected String[] m_strList = null;
	/**
	 * @uml.property name="m_fiList"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected FileInterface[] m_fiList = null;
	/**
	 * @uml.property name="m_fiRoots"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected FileInterface[] m_fiRoots = null;

	/**
	 * Creates a new FtpTypeFile instance.
	 * 
	 * @param strCurPath
	 *            : current path
	 */
	public FtpTypeFile(String strFtpPath, String strParentPath, CoFile cf,
			FileInterfaceConfiguration fic) {
		ftpFile = new FtpFile(strFtpPath,
				((FtpTypeFileInterfaceConfiguration) fic).getFtpObj());
		m_fic = fic;
		int idx = strFtpPath.lastIndexOf(separatorChar());
		if (idx > 0 && idx == strFtpPath.length() - 1)
			strFtpPath = strFtpPath.substring(0, strFtpPath.length() - 1);
		m_ftpPath = strFtpPath;
		if (strParentPath == null) {
			String path = m_ftpPath;
			idx = path.lastIndexOf(separatorChar());
			if (idx == -1)
				try {
					strParentPath = ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.getFtpObj().pwd();
				} catch (IOException ex) {
				}
			else
				strParentPath = path.substring(0, idx);
		}
		m_parentPath = strParentPath;

		// init
		try {
			((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getFtpObj().host();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		if (cf == null) {
			m_curFtpEntry = ftpFile.getName();
			m_blRead = ftpFile.canRead();
			m_blWrite = ftpFile.canWrite();
			m_strAbsolutePath = ftpFile.getAbsolutePath();
			m_strPath = ftpFile.getAbsolutePath();
			m_strName = ftpFile.getName();
			m_strParent = m_parentPath;
			m_blArchive = false;
			m_blDirectory = ftpFile.isDirectory();
			m_blFile = ftpFile.isFile();
			m_blHidden = ftpFile.isHidden();
			m_lModified = ftpFile.lastModified();
			m_lLength = ftpFile.length();
		} else {
			m_curFtpEntry = cf.getName();
			m_blRead = cf.canRead();
			m_blWrite = cf.canWrite();
			m_strAbsolutePath = cf.getAbsolutePath();
			m_strPath = cf.getAbsolutePath();
			m_strName = cf.getName();
			m_strParent = m_parentPath;
			m_blArchive = false;
			m_blDirectory = cf.isDirectory();
			m_blFile = cf.isFile();
			m_blHidden = cf.isHidden();
			m_lModified = cf.lastModified();
			m_lLength = cf.length();
		}
	}

	public FileInterfaceConfiguration getFileInterfaceConfiguration() {
		return m_fic;
	}

	public void setFileInterfaceConfiguration(FileInterfaceConfiguration fic) {
		this.m_fic = fic;
	}

	protected String getEntry(String path) {
		String ge = null;
		if (!path.equals(""))
			ge = path;
		return ge;
	}

	/**
	 * Creates a new FtpTypeFile instance.
	 * 
	 * @param strCurPath
	 *            : current path
	 */
	public FtpTypeFile(String strFtpPath, FileInterfaceConfiguration fic)
			throws IOException {
		this(strFtpPath, null, null, fic);
	}

	public FileInterface duplicate() {
		FtpTypeFile ftf = new FtpTypeFile(getAbsolutePath(), getParent(), null,
				getFileInterfaceConfiguration());
		return ftf;
	}

	public boolean equals(Object obj) {
		boolean blRet = false;

		if (obj instanceof FileInterface) {
			FileInterface fi = (FileInterface) obj;
			if (fi.getAbsolutePath().equals(getAbsolutePath())
					&& fi.canRead() == canWrite() && fi.length() == length()
					&& fi.getId() == getId()) {
				blRet = true;
			}
		}
		return blRet;
	}

	public AppProperties getAppProperties() {
		return getFileInterfaceConfiguration().getAppProperties();
	}

	public String getAppName() {
		return getFileInterfaceConfiguration().getAppName();
	}

	public String getTempDirectory() {
		String strTmpDir = FileConnector.TempDirectory;
		if (strTmpDir == null) {
			strTmpDir = new File(".").getAbsolutePath();
			if (strTmpDir == null)
				strTmpDir = "";
			FileConnector.TempDirectory = strTmpDir;
		} else {
			int idx = strTmpDir.lastIndexOf(separatorChar());
			if (idx > 0 && idx == strTmpDir.length() - 1)
				strTmpDir = strTmpDir.substring(0, strTmpDir.length() - 1);
		}
		return strTmpDir;
	}

	public BufferedInputStream getBufferedInputStream()
			throws FileNotFoundException, IOException {
		BufferedInputStream bis = new BufferedInputStream(
				ftpFile.getInputStream());
		return bis;
	}

	public BufferedOutputStream getBufferedOutputStream(String strNewPath)
			throws FileNotFoundException {
		if (strNewPath.indexOf(getParent()) == -1) {
			m_bos = new BufferedOutputStream(new FileOutputStream(strNewPath));
		} else {
			FtpFile ff = new FtpFile(
					strNewPath,
					((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.getFtpObj());
			try {
				m_bos = new BufferedOutputStream(ff.getOutputStream());
			} catch (IOException ioex) {
				ioex.printStackTrace();
			}
		}
		return m_bos;
	}

	public FileInterface[] _listRoots() {
		if (m_fiRoots != null)
			return m_fiRoots;
		CoFile f[] = ftpFile.listCoRoots();
		if (f == null)
			return null;

		FileInterface[] file = new FileInterface[Array.getLength(f)];
		for (int i = 0; i < Array.getLength(f); ++i) {
			file[i] = FileConnector.createFileInterface(f[i].getAbsolutePath(),
					f[i].getParent(), false, getFileInterfaceConfiguration());
		}
		m_fiRoots = (FileInterface[]) file;
		return m_fiRoots;
	}

	public boolean isCached() {
		return getFileInterfaceConfiguration().isCached();
	}

	public char separatorChar() {
		return '/';
	}

	public char pathSeparatorChar() {
		return ':';
	}

	public boolean canRead() {
		return m_blRead;
	}

	public boolean canWrite() {
		return m_blWrite;
	}

	public FileInterface getCanonicalFile() throws IOException {
		return new FtpTypeFile(m_ftpPath, getFileInterfaceConfiguration());
	}

	public String getCanonicalPath() throws IOException {
		return getAbsolutePath();
	}

	public boolean isAbsolutePath(String strPath) {
		boolean blRet = false;
		FileInterface[] fi = _listRoots();

		int idx = strPath.lastIndexOf(separatorChar());
		if (idx == -1 || idx != strPath.length() - 1)
			strPath = strPath + separatorChar();

		for (int i = 0; i < fi.length; ++i) {
			if (strPath.toUpperCase().startsWith(
					fi[i].getAbsolutePath().toUpperCase())) {
				blRet = true;
				break;
			}
		}
		return blRet;
	}

	public boolean isDirectory() {
		return m_blDirectory;
	}

	public boolean isArchive() {
		return m_blArchive;
	}

	public boolean isFile() {
		return m_blFile;
	}

	public boolean isHidden() {
		return m_blHidden;
	}

	public URL toURL() {
		URL url = null;
		url = toURL();
		return url;
	}

	public long length() {
		return m_lLength;
	}

	public String getId() {
		String strHost = null;
		try {
			strHost = "Ftp-"
					+ ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.getFtpObj().host();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return strHost;
	}

	public String getHost() {
		String strHost = null;
		try {
			strHost = ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getFtpObj().host();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strHost;
	}

	public String getName() {
		if (m_strName == null)
			// nessecary for the initialisation
			return ftpFile.getName();
		else
			return m_strName;
	}

	public String getAbsolutePath() {
		return m_strAbsolutePath;
	}

	public String getPath() {
		return m_strPath;
	}

	public String getInternalPath() {
		return m_intPath;
	}

	public FileInterface getDirectAccessFileInterface() {
		FileInterface fi = this;
		String strPath = getAbsolutePath();

		int iStat;
		iStat = JOptionPane.showConfirmDialog(GlobalApplicationContext
				.instance().getRootComponent(), "Extract to temp and execute?",
				"Confirmation", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
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
		return fi;
	}

	public String getParent() {
		return m_strParent;
	}

	public FileInterface getParentFileInterface() {
		boolean blExtract = false;
		if (m_intPath != null && !m_intPath.equals("")
				&& m_intPath.indexOf(separatorChar()) != -1)
			blExtract = true;
		FileInterface fi = FileConnector.createFileInterface(getParent(), null,
				blExtract, getFileInterfaceConfiguration());
		return fi;
	}

	public FileInterface getRootFileInterface() {
		FileInterface fiRet = null;
		FileInterface fiRoots[] = null;

		if ((fiRoots = _listRoots()) != null) {
			for (int i = 0; i < fiRoots.length; ++i) {
				if (getAbsolutePath().toUpperCase().startsWith(
						fiRoots[i].getAbsolutePath().toUpperCase())) {
					fiRet = fiRoots[i];
					break;
				}
			}
		}
		return fiRet;
	}

	public boolean isRoot() {
		FileInterface fiRoot = getRootFileInterface();
		if (fiRoot != null
				&& getAbsolutePath().equalsIgnoreCase(fiRoot.getAbsolutePath()))
			return true;
		else
			return false;
	}

	public long lastModified() {
		return m_lModified;
	}

	public boolean setLastModified(long time) {
		showNotSupportedText();
		return false;
	}

	public String[] list() {
		if (m_strList != null)
			return m_strList;
		m_strList = new String[0];
		if (isDirectory() || (m_curFtpEntry == null && isArchive())) {
			CoFile[] files = ftpFile.listCoFiles();
			m_strList = new String[Array.getLength(files)];
			for (int i = 0; i < Array.getLength(files); ++i) {
				m_strList[i] = files[i].getAbsolutePath();
			}
		}
		return m_strList;
	}

	public FileInterface[] _listFiles(FileInterfaceFilter filter) {
		if (!isCached() || m_fiList == null) {
			FtpTypeFile[] ftps = new FtpTypeFile[0];
			Vector vec = null;
			m_fiList = (haui.io.FileInterface.FileInterface[]) ftps;
			if (isDirectory() || (m_curFtpEntry == null && isArchive())) {
				CoFile[] files = ftpFile.listCoFiles();
				if (files != null) {
					if (Array.getLength(files) > 0) {
						if (filter == null)
							ftps = new FtpTypeFile[Array.getLength(files)];
						else
							vec = new Vector();
						for (int j = 0; j < Array.getLength(files); ++j) {
							// ftps[j] = new FtpTypeFile(
							// files[j].getAbsolutePath(), m_ftpPath, m_ftpObj);
							// ftps[j] = new FtpTypeFile(
							// files[j].getAbsolutePath(), files[j].getParent(),
							// files[j], m_ftpObj);
							FtpTypeFile ftpFile = new FtpTypeFile(
									files[j].getAbsolutePath(),
									files[j].getParent(), files[j],
									getFileInterfaceConfiguration());
							if (filter == null)
								ftps[j] = ftpFile;
							else {
								if (filter.accept(ftpFile))
									vec.add(ftpFile);
							}
						}
						if (filter != null) {
							ftps = new FtpTypeFile[vec.size()];
							for (int j = 0; j < vec.size(); ++j)
								ftps[j] = (FtpTypeFile) vec.elementAt(j);
						}
						m_fiList = (haui.io.FileInterface.FileInterface[]) ftps;
					}
				}
			}
		}
		return m_fiList;
	}

	public FileInterface[] _listFiles() {
		return _listFiles(null);
	}

	public FileInterface[] _listFiles(boolean dontShowHidden) {
		return _listFiles(null, dontShowHidden);
	}

	public FileInterface[] _listFiles(FileInterfaceFilter filter,
			boolean dontShowHidden) {
		FileInterface[] fileInterfaces = _listFiles(filter);
		ArrayList<FileInterface> interfaces = new ArrayList<FileInterface>();

		for (int i = 0; i < fileInterfaces.length; i++) {
			FileInterface fileInterface = fileInterfaces[i];

			if (!fileInterface.isHidden()) {
				interfaces.add(fileInterface);
			}
		}
		return interfaces.toArray(new FileInterface[interfaces.size()]);
	}

	/**
	 * @return the ftpFile
	 */
	public FtpFile getFtpFile() {
		return ftpFile;
	}

	public boolean renameTo(FileInterface file) {
		return ftpFile.renameTo(((FtpTypeFile) file).getFtpFile());
	}

	public boolean delete() {
		return ftpFile.delete();
	}

	public boolean mkdir() {
		m_fiList = null;
		m_strList = null;
		m_blArchive = false;
		m_blDirectory = true;
		m_blFile = false;
		return ftpFile.mkdir();
	}

	public boolean mkdirs() {
		return mkdirs(this);
	}

	private static boolean mkdirs(FileInterface fiDir) {
		boolean blRet = false;

		if (!fiDir.exists()) {
			FileInterface fiPar = fiDir.getParentFileInterface();
			if (fiPar.equals(fiDir.getRootFileInterface()))
				blRet = fiDir.mkdir();
			else {
				blRet = mkdirs(fiPar);
				if (blRet)
					blRet = fiDir.mkdir();
			}
		} else
			blRet = true;

		return blRet;
	}

	public boolean exists() {
		return ftpFile.exists();
	}

	public void logon() {
		return;
	}

	public void logoff() {
		((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.disconnect();
		return;
	}

	public void startTerminal() {
		final JShellPanel sp = new JShellPanel(getAppName());
		final JExDialog dlg = new JExDialog(null, "JShell - " + getId(), false,
				getAppName());
		final FileInterface fiCurrent = duplicate();
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
					StringBuffer strbufCmd = new StringBuffer("ftp -user=");
					FtpConnect ftpc = null;
					if ((ftpc = ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.connect()) != null) {
						strbufCmd.append(ftpc.getUserName());
						strbufCmd.append(" -pwd=");
						strbufCmd.append(ftpc.getPassWord());
					}
					strbufCmd.append(" -exec=\"cd ");
					strbufCmd.append(getAbsolutePath());
					strbufCmd.append("\"");
					strbufCmd.append(" ftp://");
					strbufCmd.append(getHost());
					// strbufCmd.append( fiCurrent.separatorChar());
					// strbufCmd.append( fiCurrent.getAbsolutePath());
					sp.getShell().getShellEnv().setFileInterface(fiCurrent);
					JShellEngine.processCommands(strbufCmd.toString(), sp
							.getShell().getShellEnv(), false);
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

	public Process getCurrentProcess() {
		return m_procCurrent;
	}

	public void setCurrentProcess(Process proc) {
		m_procCurrent = proc;
	}

	public void killCurrentProcess() {
		if (m_procCurrent != null) {
			m_procCurrent.destroy();
			m_procCurrent = null;
		}
	}

	/**
	 * ececute command
	 * 
	 * @param strFtpgetPath
	 *            traget path
	 * @param cmd
	 *            CommandClass
	 */
	public int exec(String strFtpPath, CommandClass cmd, OutputStream osOut,
			OutputStream osErr, InputStream is) {
		// int iStat;
		int iRet = -1;
		if (!cmd.getExecLocal()) {
			String strCmd = cmd.getCompleteCommand(this, getParent(),
					strFtpPath);
			if (((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getFtpObj().command(strCmd))
				iRet = 0;
		} else {
			FileInterface fi = this;
			fi = getDirectAccessFileInterface();
			try {
				iRet = FileConnector.exec(fi, cmd.getCompleteCommand(fi,
						fi.getAbsolutePath(), strFtpPath), strFtpPath, cmd,
						osOut, osErr, is);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return iRet;
	}

	public Object getConnObj() {
		return ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getFtpObj();
	}

	public boolean createNewFile() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public void deleteOnExit() {
		// TODO Auto-generated method stub
	}

	public boolean setReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * copy file
	 * 
	 * @param bo
	 *            : BufferedOutputStream to destination
	 */
	public boolean copyFile(BufferedOutputStream bo) throws IOException {
		return copyFile(bo, null);
	}

	/**
	 * copy file
	 * 
	 * @param bo
	 *            : BufferedOutputStream to destination
	 * @param cpd
	 *            : CancelProgressDialog
	 */
	public boolean copyFile(BufferedOutputStream bo, CancelProgressDialog cpd)
			throws IOException {
		BufferedInputStream bi = getBufferedInputStream();
		byte buffer[] = new byte[BUFFERSIZE];
		int bytesRead;
		while ((bytesRead = bi.read(buffer, 0, BUFFERSIZE)) > 0) {
			bo.write(buffer, 0, bytesRead);
			if (cpd != null)
				cpd.getDetailProgressbar().setValue(
						cpd.getDetailProgressbar().getValue() + bytesRead);
		}
		bi.close();
		return true;
	}

	public String extractToTempDir() {
		String strPath = getTempDirectory();
		char sepChar = '/';
		if (strPath.indexOf('\\') != -1)
			sepChar = '\\';
		strPath += sepChar + getName();
		try {
			File file = new File(strPath);
			if (!file.exists()
					|| (file.exists() && (file.length() != length() || file
							.lastModified() < lastModified()))) {
				BufferedInputStream bis = getBufferedInputStream();
				BufferedOutputStream bos = getBufferedOutputStream(strPath);
				int iLen = 0;
				byte buf[] = new byte[512];

				while ((iLen = bis.read(buf, 0, 512)) > 0) {
					bos.write(buf, 0, iLen);
				}
				bos.flush();
				bis.close();
				bos.close();
			}
		} catch (FileNotFoundException fnfex) {
			fnfex.printStackTrace();
			return null;
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return strPath;
	}

	protected void showNotSupportedMessage() {
		JOptionPane.showMessageDialog(GlobalApplicationContext.instance()
				.getRootComponent(),
				"This function is not supported for this filetype!",
				"Filetype info", JOptionPane.WARNING_MESSAGE);
	}

	protected void showNotSupportedText() {
		GlobalApplicationContext.instance().getErrorPrintStream()
				.println("This function is not supported for this filetype!");
		System.err.println("This function is not supported for this filetype!");
	}

	public boolean isAbsolute() {
		return ftpFile.isAbsolute();
	}

	public int compareTo(FileInterface o) {
		return ftpFile.compareToOld(o);
	}
}