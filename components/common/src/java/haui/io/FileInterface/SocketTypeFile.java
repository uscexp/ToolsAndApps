package haui.io.FileInterface;

import haui.components.JExDialog;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.SocketTypeFileInterfaceConfiguration;
import haui.io.FileInterface.remote.RemoteRequestObject;
import haui.io.FileInterface.remote.RemoteResponseObject;
import haui.tool.shell.JShellPanel;
import haui.tool.shell.engine.JShellEngine;
import haui.util.GlobalApplicationContext;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JDialog;

/**
 * Module: SocketTypeFile.java<br>
 * $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\
 * SocketTypeFile.java,v $
 * <p>
 * Description: ClientTypeFile for socket connections.<br>
 * </p>
 * <p>
 * Created: 27.05.2004 by AE
 * </p>
 * <p>
 * 
 * @history 27.05.2004 by AE: Created.<br>
 *          </p>
 *          <p>
 *          Modification:<br>
 *          $Log: SocketTypeFile.java,v $ Revision 1.1 2004-08-31 16:03:15+02
 *          t026843 Large redesign for application dependent outputstreams,
 *          mainframes, AppProperties! Bugfixes to DbTreeTableView, additional
 *          features for jDirWork.
 * 
 *          Revision 1.0 2004-06-22 14:06:58+02 t026843 Initial revision
 * 
 *          </p>
 *          <p>
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2004; $Revision: 1.1 $<br>
 *          $Header:
 *          M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\
 *          io\\SocketTypeFile.java,v 1.1 2004-08-31 16:03:15+02 t026843 Exp
 *          t026843 $
 *          </p>
 *          <p>
 * @since JDK1.3
 *        </p>
 */
public class SocketTypeFile extends ClientTypeFile {
	private BufferedInputStream bisTransfer = null;
	private BufferedOutputStream bosTransfer = null;

	public SocketTypeFile(String strCurPath, String strParentPath,
			Character cSeparator, Character cPathSeperator,
			FileInterfaceConfiguration fic, Boolean blUpDummy) {
		this(strCurPath, strParentPath, cSeparator.charValue(), cPathSeperator
				.charValue(), fic, blUpDummy.booleanValue());
	}

	public SocketTypeFile(String strCurPath, String strParentPath,
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

	public SocketTypeFile(String strCurPath, String strParentPath,
			FileInterfaceConfiguration fic) {
		this(strCurPath, strParentPath, ' ', ' ', fic, false);
	}

	public void connect() {
		try {
			((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getSocketConnection().getSocket();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	public SocketTypeFile(NormalFile nf) {
		super(nf.getFileInterfaceConfiguration()); // TODO correct this error!
		absolutePath = nf.getAbsolutePath();
		path = nf.getPath();
		parent = nf.getParent();
		name = nf.getName();
		read = nf.canRead();
		write = nf.canWrite();
		archive = nf.isArchive();
		directory = nf.isDirectory();
		fileType = nf.isFile();
		hidden = nf.isHidden();
		modified = nf.lastModified();
		length = nf.length();
		init = true;
	}

	public void setAdditionalData(FileInterfaceConfiguration fic) {
		setFileInterfaceConfiguration(fic);
	}

	public void disconnect() {
		closeTransfer();
		((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.disconnect();
	}

	public String getId() {
		return FileInterface.SOCKET + "-" + getHost();
	}

	public String getHost() {
		return ((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getHost();
	}

	public Object getConnObj() {
		return ((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getSocketConnection();
	}

	public FileInterface duplicate() {
		// m_sc.closeOpenConnections();
		sendRequestObject(new RemoteRequestObject("duplicate"));
		RemoteResponseObject response = readResponseObject();
		Boolean bl = null;
		SocketTypeFile stf = null;

		if (response != null)
			bl = (Boolean) response.getObject();
		if (bl != null && bl.booleanValue()) {
			stf = new SocketTypeFile(getAbsolutePath(), getParent(),
					getFileInterfaceConfiguration());
		}

		return stf;
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
			((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.closeTransferSocket();
			GlobalApplicationContext.instance().getOutputPrintStream()
					.println("Transfer complete!\n");
			// System.out.println( "Transfer complete!\n");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected BufferedInputStream getRealInputStream() {
		BufferedInputStream bis = null;
		try {
			Socket transferSock = ((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.allocateNewTransferSocket();
			if (transferSock != null) {
				// m_transferSock.getOutputStream();
				bis = new BufferedInputStream(transferSock.getInputStream());
			}
		} catch (UnknownHostException uhex) {
			uhex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return bis;
	}

	protected BufferedOutputStream getRealOutputStream() {
		BufferedOutputStream bos = null;
		try {
			Socket transferSock = ((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.allocateNewTransferSocket();

			if (transferSock != null)
				bos = new BufferedOutputStream(transferSock.getOutputStream());
		} catch (UnknownHostException uhex) {
			uhex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return bos;
	}

	public FileInterface getCanonicalFile() throws IOException {
		return new SocketTypeFile(absolutePath, parent,
				getFileInterfaceConfiguration());
	}

	public void sendRequestObject(RemoteRequestObject rro) {
		if (((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getSocketConnection() != null
				&& !((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getSocketConnection().isConnected()) {
			connect();
		}
		if (((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getSocketConnection() != null
				&& ((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getSocketConnection().isConnected()) {
			ObjectOutputStream oos = ((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getSocketConnection().getObjectOutputStream();

			if (oos != null) {
				try {
					oos.writeObject(rro);
					// System.out.println( "RemoteRequestObject: " +
					// rro.toString());
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}
			}
		}
	}

	public RemoteResponseObject readResponseObject() {
		RemoteResponseObject rroRet = null;

		if (((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getSocketConnection() != null
				&& !((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getSocketConnection().isConnected()) {
			connect();
		}
		if (((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
				.getSocketConnection() != null
				&& ((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
						.getSocketConnection().isConnected()) {
			ObjectInputStream ois = ((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
					.getSocketConnection().getObjectInputStream();

			if (ois != null) {
				try {
					rroRet = (RemoteResponseObject) ois.readObject();
					Object obj = rroRet.getObject();
					boolean blArr = false;
					try {
						Array.getLength(obj);
						blArr = true;
						if (Array.get(obj, 0) instanceof SocketTypeFile) {
							for (int i = 0; i < Array.getLength(obj); ++i) {
								SocketTypeFile stf = (SocketTypeFile) Array
										.get(obj, i);
								stf.setAdditionalData(getFileInterfaceConfiguration());
							}
						}
					} catch (Exception ex) {
					}
					if (!blArr && obj != null && obj instanceof SocketTypeFile) {
						((SocketTypeFile) obj)
								.setAdditionalData(getFileInterfaceConfiguration());
					}
					// System.out.println( "RemoteResponseObject: " +
					// rroRet.toString());
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
		// prepare terminal start
		sendRequestObject(new RemoteRequestObject("prepareTeminalStart"));
		RemoteResponseObject response = readResponseObject();
		Integer iRet = null;

		if (response != null) {
			iRet = (Integer) response.getObject();
		}
		final int iPort = iRet.intValue();

		if (iPort == 0)
			return;

		final JShellPanel sp = new JShellPanel(getAppName());
		final JExDialog dlg = new JExDialog(null, "JShell - " + getId(), false,
				getAppName());
		// final FileInterface fiCurrent = duplicate();
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
					StringBuffer strbufCmd = new StringBuffer("dlsocket -host=");
					strbufCmd
							.append(((SocketTypeFileInterfaceConfiguration) getFileInterfaceConfiguration())
									.getHost());
					strbufCmd.append(" -port=");
					strbufCmd.append(iPort);

					strbufCmd.append(" -exec=\"cd ");
					strbufCmd.append(getAbsolutePath());
					strbufCmd.append("\"");
					// sp.getShell().getShellEnv().setFileInterface( fiCurrent
					// );
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
}