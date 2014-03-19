package haui.io.FileInterface;

import haui.io.FileInterface.configuration.CommPortTypeFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.remote.RemoteRequestObject;
import haui.io.FileInterface.remote.RemoteResponseObject;
import haui.util.GlobalApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;

/**
 * Module: CommPortTypeFile.java<br>
 * $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\
 * CommPortTypeFile.java,v $
 * <p>
 * Description: ClientTypeFile for comm port connections.<br>
 * </p>
 * <p>
 * Created: 17.06.2004 by AE
 * </p>
 * <p>
 * 
 * @history 17.06.2004 by AE: Created.<br>
 *          </p>
 *          <p>
 *          Modification:<br>
 *          $Log: CommPortTypeFile.java,v $ Revision 1.1 2004-08-31 16:03:19+02
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
 *          io\\CommPortTypeFile.java,v 1.1 2004-08-31 16:03:19+02 t026843 Exp
 *          t026843 $
 *          </p>
 *          <p>
 * @since JDK1.3
 *        </p>
 */
public class CommPortTypeFile extends ClientTypeFile {
	private BufferedInputStream bisTransfer = null;
	private BufferedOutputStream bosTransfer = null;

	public CommPortTypeFile(String strCurPath, String strParentPath,
			char cSeparator, char cPathSeperator,
			FileInterfaceConfiguration fic, boolean blUpDummy) {
		super(fic);
		connect();
		if (blUpDummy)
			initUpDummy("..", strParentPath, cSeparator, cPathSeperator);
		else
			init(strCurPath);
		/*
		 * if( m_sc.isConnected()) { init( strCurPath); }
		 */
	}

	public CommPortTypeFile(String strCurPath, String strParentPath,
			FileInterfaceConfiguration fic) {
		this(strCurPath, strParentPath, ' ', ' ', fic, false);
	}

	public CommPortTypeFile(NormalFile nf) {
		super(nf.getFileInterfaceConfiguration());
		this.absolutePath = nf.getAbsolutePath();
		this.path = nf.getPath();
		this.parent = nf.getParent();
		this.name = nf.getName();
		this.read = nf.canRead();
		this.write = nf.canWrite();
		this.archive = nf.isArchive();
		this.directory = nf.isDirectory();
		this.fileType = nf.isFile();
		this.hidden = nf.isHidden();
		this.modified = nf.lastModified();
		this.length = nf.length();
		this.init = true;
	}

	public void setAdditionalData(FileInterfaceConfiguration fic) {
		super.setFileInterfaceConfiguration(fic);
	}

	public void connect() {
	}

	public void disconnect() {
		closeTransfer();
		if (getFileInterfaceConfiguration() != null) {
			((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getCommPortConnection().close();
		}
	}

	public String getId() {
		return FileInterface.COMMPORT
				+ "-"
				+ ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getCommPortConnection().getCommPortId().getName();
	}

	public String getHost() {
		return ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getCommPortConnection().getCommPortId().getName();
	}

	public Object getConnObj() {
		return ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getCommPortConnection();
	}

	public FileInterface duplicate() {
		// m_cc.closeOpenConnections();
		sendRequestObject(new RemoteRequestObject("duplicate"));
		RemoteResponseObject response = readResponseObject();
		Boolean bl = null;
		CommPortTypeFile stf = null;

		if (response != null)
			bl = (Boolean) response.getObject();
		if (bl != null && bl.booleanValue()) {
			stf = new CommPortTypeFile(getAbsolutePath(), getParent(),
					getFileInterfaceConfiguration());
		}

		return stf;
	}

	public void closeConnectionStreams() {
		((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getCommPortConnection().closeConnectionStreams();
	}

	public void reopenConnectionStreams() {
		((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getCommPortConnection().reopenConnectionStreams();
	}

	public void closeTransfer() {
		try {
			if (bisTransfer != null) {
				bisTransfer.close();
				bisTransfer = null;
			}
			if (bosTransfer != null) {
				bosTransfer.close();
				bosTransfer = null;
			}
			GlobalApplicationContext.instance().getOutputPrintStream()
					.println("Transfer complete!\n");
			// System.out.println( "Transfer complete!\n");
			reopenConnectionStreams();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected BufferedInputStream getRealInputStream() {
		BufferedInputStream bis = null;
		try {
			if (((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getCommPortConnection().getCommPort() != null
					&& ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.getCommPortConnection().isConnected()) {
				closeConnectionStreams();
			}
			bis = new BufferedInputStream(
					((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.getCommPortConnection().getCommPort()
							.getInputStream());
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return bis;
	}

	protected BufferedOutputStream getRealOutputStream() {
		BufferedOutputStream bos = null;
		try {
			if (((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getCommPortConnection().getCommPort() != null
					&& ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.getCommPortConnection().isConnected()) {
				closeConnectionStreams();
			}
			bos = new BufferedOutputStream(
					((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
							.getCommPortConnection().getCommPort()
							.getOutputStream());
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return bos;
	}

	public FileInterface getCanonicalFile() throws IOException {
		return new CommPortTypeFile(absolutePath, parent,
				getFileInterfaceConfiguration());
	}

	public void sendRequestObject(RemoteRequestObject rro) {
		if (((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getCommPortConnection() != null
				&& !((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getCommPortConnection().isConnected()) {
			closeTransfer();
			connect();
		}
		if (((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getCommPortConnection() != null
				&& ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getCommPortConnection().isConnected()) {
			ObjectOutputStream oos = ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getCommPortConnection().getObjectOutputStream();

			if (oos != null) {
				try {
					oos.writeObject(rro);
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}
			}
		}
	}

	public RemoteResponseObject readResponseObject() {
		RemoteResponseObject rroRet = null;

		if (((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getCommPortConnection() != null
				&& !((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getCommPortConnection().isConnected()) {
			closeTransfer();
			connect();
		}
		if (((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getCommPortConnection() != null
				&& ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getCommPortConnection().isConnected()) {
			ObjectInputStream ois = ((CommPortTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getCommPortConnection().getObjectInputStream();

			if (ois != null) {
				try {
					rroRet = (RemoteResponseObject) ois.readObject();
					Object obj = rroRet.getObject();
					boolean blArr = false;
					try {
						Array.getLength(obj);
						blArr = true;
						if (Array.get(obj, 0) instanceof CommPortTypeFile) {
							for (int i = 0; i < Array.getLength(obj); ++i) {
								CommPortTypeFile ctf = (CommPortTypeFile) Array
										.get(obj, i);
								ctf.setAdditionalData(getFileInterfaceConfiguration());
							}
						}
					} catch (IllegalArgumentException ex) {
					}
					if (!blArr && obj instanceof CommPortTypeFile) {
						((CommPortTypeFile) obj)
								.setAdditionalData(getFileInterfaceConfiguration());
					}
				} catch (ClassNotFoundException cnfex) {
					cnfex.printStackTrace();
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}
			}
		}
		return rroRet;
	}

	public void startTerminal() {
		throw new java.lang.UnsupportedOperationException(
				"Method startTerminal() not supported.");
	}
}
