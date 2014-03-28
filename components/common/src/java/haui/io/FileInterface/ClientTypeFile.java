package haui.io.FileInterface;

import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.remote.RemoteRequestObject;
import haui.io.FileInterface.remote.RemoteResponseObject;
import haui.util.AppProperties;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.JOptionPane;

/**
 * Module: ClientTypeFile.java<br>
 * $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\
 * ClientTypeFile.java,v $
 * <p>
 * Description: abstract base class for ClientTypeFiles.<br>
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
 *          $Log: ClientTypeFile.java,v $ Revision 1.1 2004-08-31 16:03:19+02
 *          t026843 Large redesign for application dependent outputstreams,
 *          mainframes, AppProperties! Bugfixes to DbTreeTableView, additional
 *          features for jDirWork.
 * 
 *          Revision 1.0 2004-06-22 14:06:54+02 t026843 Initial revision
 * 
 *          </p>
 *          <p>
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2004; $Revision: 1.1 $<br>
 *          $Header:
 *          M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\
 *          io\\ClientTypeFile.java,v 1.1 2004-08-31 16:03:19+02 t026843 Exp
 *          t026843 $
 *          </p>
 *          <p>
 * @since JDK1.3
 *        </p>
 */
public abstract class ClientTypeFile extends BaseTypeFile {
	boolean init = false;

	// cache variables
	char separator = ' ';
	char pathSeparator = ' ';

	public ClientTypeFile(FileInterfaceConfiguration fic) {
		super(fic);
		super._init();
	}

	protected void initUpDummy(String strName, String strParent,
			char cSeparator, char cPathSeperator) {
		this.name = strName;
		this.parent = strParent;
		this.absolutePath = strParent + cSeparator + strName;
		this.archive = false;
		this.directory = true;
		this.fileType = false;
		this.read = true;
		this.write = true;
		this.separator = cSeparator;
		this.pathSeparator = cPathSeperator;
		this.init = true;
	}

	public abstract void connect();

	public abstract void disconnect();

	public abstract void closeTransfer();

	public abstract RemoteResponseObject readResponseObject();

	public abstract void sendRequestObject(RemoteRequestObject rro);

	public boolean init(String strPath) {
		if (strPath == null)
			return false;
		RemoteRequestObject request = new RemoteRequestObject("init");
		request.addParam(strPath);
		sendRequestObject(request);
		RemoteResponseObject response = readResponseObject();
		Boolean blRet = null;

		if (response != null)
			blRet = (Boolean) response.getObject();
		if (blRet.booleanValue()) {
			this.absolutePath = getAbsolutePath();
			this.path = getPath();
			this.parent = getParent();
			this.name = getName();
			this.read = canRead();
			this.write = canWrite();
			this.archive = isArchive();
			this.directory = isDirectory();
			this.fileType = isFile();
			this.hidden = isHidden();
			this.modified = lastModified();
			this.length = length();
			this.init = true;
		}

		return blRet.booleanValue();
	}

	public boolean resetPath() {
		if (absolutePath == null)
			return false;
		RemoteRequestObject request = new RemoteRequestObject("init");
		request.addParam(absolutePath);
		sendRequestObject(request);
		RemoteResponseObject response = readResponseObject();
		Boolean blRet = null;

		if (response != null)
			blRet = (Boolean) response.getObject();

		return blRet.booleanValue();
	}

	public AppProperties getAppProperties() {
		return getFileInterfaceConfiguration().getAppProperties();
	}

	public BufferedInputStream getBufferedInputStream()
			throws FileNotFoundException, IOException {
		resetPath();
		closeTransfer();
		BufferedInputStream bisRet = null;

		sendRequestObject(new RemoteRequestObject("getBufferedInputStream"));
		RemoteResponseObject response = readResponseObject();
		Boolean bl = null;

		if (response != null)
			bl = (Boolean) response.getObject();
		if (bl != null && bl.booleanValue()) {
			bisRet = getRealInputStream();
		}

		return bisRet;
	}

	protected abstract BufferedInputStream getRealInputStream();

	public BufferedOutputStream getBufferedOutputStream(String strNewPath)
			throws FileNotFoundException {
		resetPath();
		closeTransfer();
		BufferedOutputStream bosRet = null;

		RemoteRequestObject request = new RemoteRequestObject(
				"getBufferedOutputStream");
		request.addParam(strNewPath);
		sendRequestObject(request);
		RemoteResponseObject response = readResponseObject();
		Boolean bl = null;

		if (response != null)
			bl = (Boolean) response.getObject();
		if (bl != null && bl.booleanValue()) {
			bosRet = getRealOutputStream();
		}

		return bosRet;
	}

	protected abstract BufferedOutputStream getRealOutputStream();

	public abstract FileInterface duplicate();

	public FileInterface[] _listRoots() {
		if (!isCached() || fileInterfaceRoots == null) // only because of the
														// performance!!
		{
			resetPath();
			sendRequestObject(new RemoteRequestObject("_listRoots"));
			RemoteResponseObject response = readResponseObject();

			if (response != null)
				fileInterfaceRoots = (haui.io.FileInterface.FileInterface[]) response
						.getObject();
		}

		return fileInterfaceRoots;
	}

	public boolean isCached() {
		return getFileInterfaceConfiguration().isCached();
	}

	public char separatorChar() {
		Character cRet = null;
		if (!isCached() || separator == ' ') {
			sendRequestObject(new RemoteRequestObject("separatorChar"));
			RemoteResponseObject response = readResponseObject();

			if (response != null)
				cRet = (Character) response.getObject();
		} else
			return separator;

		return cRet.charValue();
	}

	public char pathSeparatorChar() {
		Character cRet = null;
		if (!isCached() || pathSeparator == ' ') {
			sendRequestObject(new RemoteRequestObject("pathSeparatorChar"));
			RemoteResponseObject response = readResponseObject();

			if (response != null)
				cRet = (Character) response.getObject();
		} else
			return pathSeparator;

		return cRet.charValue();
	}

	public boolean canRead() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("canRead"));
			RemoteResponseObject response = readResponseObject();
			Boolean blRet = null;

			if (response != null)
				blRet = (Boolean) response.getObject();

			return blRet.booleanValue();
		} else
			return read;
	}

	public boolean canWrite() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("canWrite"));
			RemoteResponseObject response = readResponseObject();
			Boolean blRet = null;

			if (response != null)
				blRet = (Boolean) response.getObject();

			return blRet.booleanValue();
		} else
			return write;
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
		if (!init) {
			sendRequestObject(new RemoteRequestObject("isDirectory"));
			RemoteResponseObject response = readResponseObject();
			Boolean blRet = null;

			if (response != null)
				blRet = (Boolean) response.getObject();

			return blRet.booleanValue();
		} else
			return directory;
	}

	public boolean isArchive() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("isArchive"));
			RemoteResponseObject response = readResponseObject();
			Boolean blRet = null;

			if (response != null)
				blRet = (Boolean) response.getObject();

			return blRet.booleanValue();
		} else
			return archive;
	}

	public boolean isFile() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("isFile"));
			RemoteResponseObject response = readResponseObject();
			Boolean blRet = null;

			if (response != null)
				blRet = (Boolean) response.getObject();

			return blRet.booleanValue();
		} else
			return fileType;
	}

	public boolean isHidden() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("isHidden"));
			RemoteResponseObject response = readResponseObject();
			Boolean blRet = null;

			if (response != null)
				blRet = (Boolean) response.getObject();

			return blRet.booleanValue();
		} else
			return hidden;
	}

	public URL toURL() {
		resetPath();
		sendRequestObject(new RemoteRequestObject("toURL"));
		RemoteResponseObject response = readResponseObject();
		URL urlRet = null;

		if (response != null)
			urlRet = (URL) response.getObject();

		return urlRet;
	}

	public long length() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("length"));
			RemoteResponseObject response = readResponseObject();
			Long lRet = null;

			if (response != null)
				lRet = (Long) response.getObject();

			return lRet.longValue();
		} else
			return length;
	}

	public abstract String getId();

	public abstract String getHost();

	public String getName() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("getName"));
			RemoteResponseObject response = readResponseObject();
			String strRet = null;

			if (response != null)
				strRet = (String) response.getObject();

			return strRet;
		} else
			return name;
	}

	public void setName(String strName) {
		this.name = strName;
	}

	public String getAbsolutePath() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("getAbsolutePath"));
			RemoteResponseObject response = readResponseObject();
			String strRet = null;

			if (response != null)
				strRet = (String) response.getObject();

			return strRet;
		} else
			return absolutePath;
	}

	public String getPath() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("getPath"));
			RemoteResponseObject response = readResponseObject();
			String strRet = null;

			if (response != null)
				strRet = (String) response.getObject();

			return strRet;
		} else
			return path;
	}

	public String getInternalPath() {
		resetPath();
		sendRequestObject(new RemoteRequestObject("getInternalPath"));
		RemoteResponseObject response = readResponseObject();
		String strRet = null;

		if (response != null)
			strRet = (String) response.getObject();

		return strRet;
	}

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

	public String getParent() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("getParent"));
			RemoteResponseObject response = readResponseObject();
			String strRet = null;

			if (response != null)
				strRet = (String) response.getObject();

			return strRet;
		} else
			return parent;
	}

	public FileInterface getParentFileInterface() {
		resetPath();
		sendRequestObject(new RemoteRequestObject("getParentFileInterface"));
		RemoteResponseObject response = readResponseObject();
		FileInterface fiRet = null;

		if (response != null)
			fiRet = (FileInterface) response.getObject();

		return fiRet;
	}

	public long lastModified() {
		if (!init) {
			sendRequestObject(new RemoteRequestObject("lastModified"));
			RemoteResponseObject response = readResponseObject();
			Long lRet = null;

			if (response != null)
				lRet = (Long) response.getObject();

			return lRet.longValue();
		} else
			return modified;
	}

	public boolean setLastModified(long time) {
		if (!init) {
			RemoteRequestObject request = new RemoteRequestObject(
					"setLastModified");
			request.addParam(new Long(time));
			sendRequestObject(request);
			RemoteResponseObject response = readResponseObject();
			Boolean lRet = null;

			if (response != null)
				lRet = (Boolean) response.getObject();

			return lRet.booleanValue();
		}
		return false;
	}

	public String[] list() {
		if (!isCached() || list == null) {
			resetPath();
			sendRequestObject(new RemoteRequestObject("list"));
			RemoteResponseObject response = readResponseObject();

			if (response != null)
				list = (String[]) response.getObject();
		}
		return list;
	}

	public FileInterface[] _listFiles(FileInterfaceFilter filter) {
		if (filter == null) {
			return _listFiles();
		}
		if (!isCached() || fileInterfaces == null) {
			resetPath();
			RemoteRequestObject request = new RemoteRequestObject("_listFiles");
			request.addParam(filter);
			sendRequestObject(request);

			RemoteResponseObject response = readResponseObject();

			if (response != null)
				fileInterfaces = (haui.io.FileInterface.FileInterface[]) response
						.getObject();
		}
		return fileInterfaces;
	}

	public FileInterface[] _listFiles() {
		if (!isCached() || fileInterfaces == null) {
			resetPath();
			sendRequestObject(new RemoteRequestObject("_listFiles"));
			RemoteResponseObject response = readResponseObject();

			if (response != null)
				fileInterfaces = (haui.io.FileInterface.FileInterface[]) response
						.getObject();
		}
		return fileInterfaces;
	}

	public boolean renameTo(FileInterface file) {
		resetPath();
		RemoteRequestObject request = new RemoteRequestObject("renameTo");
		request.addParam(file);
		sendRequestObject(request);

		RemoteResponseObject response = readResponseObject();
		Boolean blRet = null;

		if (response != null)
			blRet = (Boolean) response.getObject();

		return blRet.booleanValue();
	}

	public boolean delete() {
		resetPath();
		sendRequestObject(new RemoteRequestObject("delete"));
		RemoteResponseObject response = readResponseObject();
		Boolean blRet = null;

		if (response != null)
			blRet = (Boolean) response.getObject();

		return blRet.booleanValue();
	}

	public boolean mkdir() {
		this.fileInterfaces = null;
		this.list = null;
		this.archive = false;
		this.directory = true;
		this.fileType = false;
		resetPath();
		sendRequestObject(new RemoteRequestObject("mkdir"));
		RemoteResponseObject response = readResponseObject();
		Boolean blRet = null;

		if (response != null)
			blRet = (Boolean) response.getObject();

		return blRet.booleanValue();
	}

	public boolean exists() {
		resetPath();
		sendRequestObject(new RemoteRequestObject("exists"));
		RemoteResponseObject response = readResponseObject();
		Boolean blRet = null;

		if (response != null)
			blRet = (Boolean) response.getObject();

		return blRet.booleanValue();
	}

	public void logon() {
		RemoteRequestObject request = new RemoteRequestObject("logon");
		request.addParam(getFileInterfaceConfiguration().getAppProperties());
		sendRequestObject(request);
		readResponseObject();
	}

	public void logoff() {
		sendRequestObject(new RemoteRequestObject("logoff"));
		// RemoteResponseObject response = readResponseObject();
		disconnect();
	}

	public abstract void startTerminal();

	public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut,
			OutputStream osErr, InputStream is) {
		int iRet = -1;
		if (!cmd.getExecLocal()) {
			resetPath();
			RemoteRequestObject request = new RemoteRequestObject("exec");
			request.addParam(strTargetPath);
			request.addParam(cmd);
			// request.addParam( osOut);
			// request.addParam( osErr);
			// request.addParam( is);
			sendRequestObject(request);

			RemoteResponseObject response = readResponseObject();
			Integer intRet = null;

			if (response != null)
				intRet = (Integer) response.getObject();

			iRet = intRet.intValue();
		} else {
			FileInterface fi = getDirectAccessFileInterface();
			try {
				iRet = FileConnector.exec(fi, cmd.getCompleteCommand(fi,
						fi.getAbsolutePath(), strTargetPath), strTargetPath,
						cmd, osOut, osErr, is);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return iRet;
	}

	public Process getCurrentProcess() {
		return procCurrent;
	}

	public void setCurrentProcess(Process proc) {
		procCurrent = proc;
	}

	public void killCurrentProcess() {
		if (procCurrent != null) {
			procCurrent.destroy();
			procCurrent = null;
		}
	}

	public abstract Object getConnObj();

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();

		out.writeObject(getFileInterfaceConfiguration());
		// out.writeBoolean( m_blArchive);
		out.writeBoolean(directory);
		out.writeBoolean(fileType);
		out.writeBoolean(hidden);
		out.writeBoolean(init);
		out.writeBoolean(read);
		out.writeBoolean(write);
		out.writeObject(fileInterfaces);
		out.writeObject(fileInterfaceRoots);
		out.writeLong(length);
		out.writeLong(modified);
		out.writeObject(absolutePath);
		out.writeObject(list);
		out.writeObject(name);
		out.writeObject(parent);
		out.writeObject(path);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();

		setFileInterfaceConfiguration((FileInterfaceConfiguration) in
				.readObject());
		this.archive = false;
		this.directory = in.readBoolean();
		this.fileType = in.readBoolean();
		this.hidden = in.readBoolean();
		this.init = in.readBoolean();
		this.read = in.readBoolean();
		this.write = in.readBoolean();
		this.fileInterfaces = (FileInterface[]) in.readObject();
		this.fileInterfaceRoots = (FileInterface[]) in.readObject();
		this.length = in.readLong();
		this.modified = in.readLong();
		this.absolutePath = (String) in.readObject();
		this.list = (String[]) in.readObject();
		this.name = (String) in.readObject();
		this.parent = (String) in.readObject();
		this.path = (String) in.readObject();
	}
}