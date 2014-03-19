package haui.io.FileInterface;

import haui.components.CancelProgressDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.SocketTypeServerFileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.remote.RemoteRequestObject;
import haui.io.FileInterface.remote.RemoteResponseObject;
import haui.util.AppProperties;
import haui.util.CommandClass;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Module: FileTypeServer.java<br>
 * $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\
 * FileTypeServer.java,v $
 * <p>
 * Description: abstract base class for FileTypeServers.<br>
 * </p>
 * <p>
 * Created: 19.05.2004 by AE
 * </p>
 * <p>
 * 
 * @history 19.05.2004 by AE: Created.<br>
 *          </p>
 *          <p>
 *          Modification:<br>
 *          $Log: FileTypeServer.java,v $ Revision 1.1 2004-08-31 16:03:18+02
 *          t026843 Large redesign for application dependent outputstreams,
 *          mainframes, AppProperties! Bugfixes to DbTreeTableView, additional
 *          features for jDirWork. Revision 1.0 2004-06-22 14:06:56+02 t026843
 *          Initial revision
 *          </p>
 *          <p>
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2004; $Revision: 1.1 $<br>
 *          $Header:
 *          M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\
 *          io\\FileTypeServer.java,v 1.1 2004-08-31 16:03:18+02 t026843 Exp
 *          t026843 $
 *          </p>
 *          <p>
 * @since JDK1.3
 *        </p>
 */
public abstract class FileTypeServer implements FileInterface {
	private FileInterface m_fi = null;
	protected FileInterfaceConfiguration m_fic;
	protected boolean m_blConnected = true;

	// cache variables
	boolean m_blCache = false;

	public FileTypeServer(FileInterfaceConfiguration fic) {
		m_fic = fic;
	}

	public FileInterfaceConfiguration getFileInterfaceConfiguration() {
		return m_fic;
	}

	public void setFileInterfaceConfiguration(FileInterfaceConfiguration fic) {
		this.m_fic = fic;
	}

	public abstract void listen();

	public abstract void disconnect();

	public boolean isConnected() {
		return m_blConnected;
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

	public abstract RemoteRequestObject readRequestObject();

	public abstract void sendResponseObject(RemoteResponseObject rro);

	public void setInternalFileInterface(FileInterface fi) {
		m_fi = fi;
	}

	public FileInterface getInternalFileInterface() {
		return m_fi;
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
		throw new java.lang.UnsupportedOperationException(
				"Method getBufferedInputStream() not implemented.");
	}

	public BufferedOutputStream getBufferedOutputStream(String strNewPath)
			throws FileNotFoundException {
		throw new java.lang.UnsupportedOperationException(
				"Method getBufferedOutputStream(String) not implemented.");
	}

	public FileInterface[] _listRoots() {
		if (m_fi != null)
			return m_fi._listRoots();
		return null;
	}

	public void setCache(boolean bl) {
		m_blCache = bl;
	}

	public boolean isCached() {
		return m_blCache;
	}

	public char separatorChar() {
		if (m_fi != null)
			return m_fi.separatorChar();
		return ' ';
	}

	public char pathSeparatorChar() {
		if (m_fi != null)
			return m_fi.pathSeparatorChar();
		return ' ';
	}

	public boolean canRead() {
		if (m_fi != null)
			return m_fi.canRead();
		return false;
	}

	public boolean canWrite() {
		if (m_fi != null)
			return m_fi.canWrite();
		return false;
	}

	public boolean isAbsolutePath(String strPath) {
		boolean blRet = false;
		FileInterface[] fi = m_fi._listRoots();

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

	public String getCanonicalPath() throws IOException {
		return getAbsolutePath();
	}

	public boolean isAbsolute() {
		return isAbsolutePath(getPath());
	}

	public boolean isDirectory() {
		if (m_fi != null)
			return m_fi.isDirectory();
		return false;
	}

	public boolean isArchive() {
		if (m_fi != null)
			return m_fi.isArchive();
		return false;
	}

	public boolean isFile() {
		if (m_fi != null)
			return m_fi.isFile();
		return false;
	}

	public boolean isHidden() {
		if (m_fi != null)
			return m_fi.isHidden();
		return false;
	}

	public URL toURL() {
		if (m_fi != null)
			return m_fi.toURL();
		return null;
	}

	public long length() {
		if (m_fi != null)
			return m_fi.length();
		return 0;
	}

	public String getId() {
		if (m_fi != null)
			return m_fi.getId();
		return null;
	}

	public String getHost() {
		if (m_fi != null)
			return m_fi.getHost();
		return null;
	}

	public String getName() {
		if (m_fi != null)
			return m_fi.getName();
		return null;
	}

	public String getAbsolutePath() {
		if (m_fi != null)
			return m_fi.getAbsolutePath();
		return null;
	}

	public String getPath() {
		if (m_fi != null)
			return m_fi.getPath();
		return null;
	}

	public String getInternalPath() {
		if (m_fi != null)
			return m_fi.getInternalPath();
		return null;
	}

	public FileInterface getDirectAccessFileInterface() {
		if (m_fi != null)
			return m_fi;
		return null;
	}

	public String getParent() {
		if (m_fi != null)
			return m_fi.getParent();
		return null;
	}

	public FileInterface getParentFileInterface() {
		if (m_fi != null)
			return m_fi.getParentFileInterface();
		return null;
	}

	public FileInterface getRootFileInterface() {
		if (m_fi != null)
			return m_fi.getRootFileInterface();
		return null;
	}

	public boolean isRoot() {
		if (m_fi != null)
			return m_fi.isRoot();
		return false;
	}

	public long lastModified() {
		if (m_fi != null)
			return m_fi.lastModified();
		return 0;
	}

	public boolean setLastModified(long time) {
		if (m_fi != null)
			return m_fi.setLastModified(time);
		return false;
	}

	public String[] list() {
		if (m_fi != null)
			return m_fi.list();
		return null;
	}

	public FileInterface[] _listFiles(FileInterfaceFilter filter) {
		if (m_fi != null)
			return m_fi._listFiles(filter);
		return null;
	}

	public FileInterface[] _listFiles() {
		if (m_fi != null)
			return m_fi._listFiles();
		return null;
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

	public boolean renameTo(FileInterface file) {
		if (m_fi != null)
			return m_fi.renameTo(file);
		return false;
	}

	public boolean delete() {
		if (m_fi != null)
			return m_fi.delete();
		return false;
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

	public boolean mkdir() {
		if (m_fi != null)
			return m_fi.mkdir();
		return false;
	}

	public boolean exists() {
		if (m_fi != null)
			return m_fi.exists();
		return false;
	}

	public void logon() {
		if (m_fi != null)
			m_fi.logon();
	}

	public void logoff() {
		if (m_fi != null)
			m_fi.logoff();
		disconnect();
	}

	public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut,
			OutputStream osErr, InputStream is) {
		if (m_fi != null) {
			cmd.setConnectionObject(m_fi.getConnObj());
			return m_fi.exec(strTargetPath, cmd, osOut, osErr, is);
		}
		return -1;
	}

	public Process getCurrentProcess() {
		if (m_fi != null)
			return m_fi.getCurrentProcess();
		return null;
	}

	public void setCurrentProcess(Process proc) {
		if (m_fi != null)
			m_fi.setCurrentProcess(proc);
	}

	public void killCurrentProcess() {
		if (m_fi != null)
			m_fi.killCurrentProcess();
	}

	public Object getConnObj() {
		if (m_fi != null)
			return m_fi.getConnObj();
		return null;
	}

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
		if (m_fi != null)
			return m_fi.copyFile(bo, cpd);
		return false;
	}

	public String extractToTempDir() {
		if (m_fi != null)
			return m_fi.extractToTempDir();
		return null;
	}

	public boolean init(String strPath) {
		if (m_fi != null) {
			if (!getAbsolutePath().equals(strPath)) {
				m_fi = FileConnector.changeDirectory(m_fi, strPath,
						getFileInterfaceConfiguration());
				if (getFileInterfaceConfiguration() instanceof SocketTypeServerFileInterfaceConfiguration)
					((SocketTypeServerFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.append("Current path is " + m_fi.getAbsolutePath()
									+ "\n");
			}
			return true;
		}
		return false;
	}

	public abstract int prepareTeminalStart();

	public void startTerminal() {
	}

	public int compareTo(FileInterface file) {
		// FIXME ignore case for windows
		return getAbsolutePath().compareTo(
				((FileInterface) file).getAbsolutePath());
	}

	public boolean createNewFile() throws IOException {
		return m_fi.createNewFile();
	}

	public void deleteOnExit() {
		m_fi.deleteOnExit();
	}

	public boolean setReadOnly() {
		return m_fi.setReadOnly();
	}

}
