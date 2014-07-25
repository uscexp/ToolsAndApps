package haui.io.FileInterface;

import haui.components.CancelProgressDialog;
import haui.components.JExDialog;
import haui.exception.AppSystemException;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.FtpTypeFileInterfaceConfiguration;
import haui.io.FileInterface.vfs.FtpConnectionObject;
import haui.io.FileInterface.vfs.VfsTypeFile;
import haui.tool.shell.JShellPanel;
import haui.tool.shell.engine.JShellEngine;
import haui.util.CommandClass;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JDialog;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.vfs2.provider.ftp.FtpClientFactory;

/**
 * Module: FtpTypeFile.java<br>
 * <p>
 * Created: 05.07.2002 by AE
 * </p>
 * 
 * @author ae
 */
public class FtpTypeFile extends VfsTypeFile<FtpTypeFile> {

    public FtpTypeFile(String strFtpPath, String strParentPath, FileInterfaceConfiguration fic) {
        super(strFtpPath, strParentPath, fic);
    }

    public FtpTypeFile(String strFtpPath, FileInterfaceConfiguration fic) throws IOException {
        this(strFtpPath, null, fic);
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

    public String getId() {
        String id = ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration()).getConnectionObject().getProtokoll().getProtokollName() + "-" + getHost();
        return id;
    }

    public String getHost() {
        if (host == LOCAL || !isCached())
            host = ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration()).getConnectionObject().getHost();
        return host;
    }

    public void startTerminal() {
        final JShellPanel sp = new JShellPanel(getAppName());
        final JExDialog dlg = new JExDialog(null, "JShell - " + getId(), false, getAppName());
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
                    FtpConnectionObject ftpc = null;
                    if ((ftpc = ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration()).getConnectionObject()) != null) {
                        strbufCmd.append(ftpc.getUsr());
                        strbufCmd.append(" -pwd=");
                        strbufCmd.append(ftpc.getPwd());
                    }
                    strbufCmd.append(" -exec=\"cd ");
                    strbufCmd.append(getAbsolutePath());
                    strbufCmd.append("\"");
                    strbufCmd.append(" ");
                    strbufCmd.append(((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration()).getConnectionObject().getProtokoll().getUriPrefix());
                    strbufCmd.append(getHost());
                    // strbufCmd.append( fiCurrent.separatorChar());
                    // strbufCmd.append( fiCurrent.getAbsolutePath());
                    sp.getShell().getShellEnv().setFileInterface(fiCurrent);
                    JShellEngine.processCommands(strbufCmd.toString(), sp.getShell().getShellEnv(), false);
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

    /**
     * ececute command
     * 
     * @param strFtpgetPath
     *            traget path
     * @param cmd
     *            CommandClass
     */
    public int exec(String strFtpPath, CommandClass cmd, OutputStream osOut, OutputStream osErr, InputStream is) {
        // int iStat;
        int iRet = -1;
        try {
            if (!cmd.getExecLocal()) {
                FtpTypeFileInterfaceConfiguration configuration = (FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration();
                FtpConnectionObject connectionObject = configuration.getConnectionObject();
                String strCmd = cmd.getCompleteCommand(this, getParent(), strFtpPath);
                FTPClient ftpClient = FtpClientFactory.createConnection(connectionObject.getHost(), connectionObject.getPort(), connectionObject.getUsr().toCharArray(), connectionObject.getPwd()
                        .toCharArray(), strFtpPath, configuration.getFileSystemOptions());
                String params = null; // FIXME fill separate command and params
                if (ftpClient != null && ftpClient.doCommand(strCmd, params))
                    iRet = 0;
            } else {
                iRet = super.exec(strFtpPath, cmd, osOut, osErr, is);
            }
        } catch (FileNotFoundException fnfex) {
            throw new AppSystemException(fnfex);
        } catch (IOException ioex) {
            throw new AppSystemException(ioex);
        }
        return iRet;
    }

    public Object getConnObj() {
        return ((FtpTypeFileInterfaceConfiguration) getFileInterfaceConfiguration()).getConnectionObject();
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
    public boolean copyFile(BufferedOutputStream bo, CancelProgressDialog cpd) throws IOException {
        BufferedInputStream bi = getBufferedInputStream();
        byte buffer[] = new byte[BUFFERSIZE];
        int bytesRead;
        while ((bytesRead = bi.read(buffer, 0, BUFFERSIZE)) > 0) {
            bo.write(buffer, 0, bytesRead);
            if (cpd != null)
                cpd.getDetailProgressbar().setValue(cpd.getDetailProgressbar().getValue() + bytesRead);
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
            if (!file.exists() || (file.exists() && (file.length() != length() || file.lastModified() < lastModified()))) {
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
}