package haui.io.FileInterface;

import haui.components.JExDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.tool.shell.JShellPanel;
import haui.tool.shell.engine.JShellEngine;
import haui.util.CommandClass;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

/**
 * Module: NormalFile.java<br>
 * $Source:
 * M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\NormalFile
 * .java,v $
 * <p>
 * Description: Cache for file information.<br>
 * </p>
 * <p>
 * Created: 11.06.2002 by AE
 * </p>
 * <p>
 * 
 * @history 11.06.2002 by AE: Created.<br>
 *          </p>
 *          <p>
 *          Modification:<br>
 *          $Log: NormalFile.java,v $ Revision 1.2 2004-08-31 16:03:21+02
 *          t026843 Large redesign for application dependent outputstreams,
 *          mainframes, AppProperties! Bugfixes to DbTreeTableView, additional
 *          features for jDirWork. Revision 1.1 2004-06-22 14:08:51+02 t026843
 *          bigger changes Revision 1.0 2003-06-06 10:05:37+02 t026843 Initial
 *          revision Revision 1.2 2003-06-04 15:35:54+02 t026843 bugfixes
 *          Revision 1.1 2003-05-28 14:19:52+02 t026843 reorganisations Revision
 *          1.0 2003-05-21 16:25:55+02 t026843 Initial revision Revision 1.5
 *          2002-09-18 11:16:20+02 t026843 - changes to fit extended
 *          filemanager.pl - logon and logoff moved to 'TypeFile's -
 *          startTerminal() added to 'TypeFile's, but only CgiTypeFile (until
 *          now) starts the LRShell as terminal - LRShell changed to work with
 *          filemanager.pl Revision 1.4 2002-09-03 17:08:00+02 t026843 -
 *          CgiTypeFile is now full functional. - Migrated to the extended
 *          filemanager.pl script. Revision 1.3 2002-08-07 15:25:26+02 t026843
 *          Ftp support via filetype added. Some bugfixes. Revision 1.2
 *          2002-06-21 11:00:18+02 t026843 Added gzip and bzip2 file support
 *          Revision 1.1 2002-06-19 16:13:52+02 t026843 Zip file support;
 *          writing doesn't work yet! Revision 1.0 2002-06-17 17:21:19+02
 *          t026843 Initial revision
 *          </p>
 *          <p>
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2002; $Revision: 1.2 $<br>
 *          $Header:
 *          M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\
 *          io\\NormalFile.java,v 1.2 2004-08-31 16:03:21+02 t026843 Exp t026843
 *          $
 *          </p>
 *          <p>
 * @since JDK1.2
 *        </p>
 */
public class NormalFile extends BaseTypeFile {
    // constants

    // member variables
    private File file;

    /**
     * Creates a new NormalFile instance.
     * 
     * @param strCurPath
     *            : current path
     */
    public NormalFile(String strCurPath, String strParentPath, FileInterfaceConfiguration fic) {
        super(fic);
        file = new File(strCurPath);
        parent = strParentPath;
        if (parent == null) {
            if (file.getParentFile() != null)
                parent = file.getParentFile().getAbsolutePath();
            else {
                if (parent == null) {
                    parent = getAbsolutePath();
                    int iIdx = -1;
                    if (parent.length() > 2) {
                        for (int i = parent.length() - 3; i >= 0; i--) {
                            char c = parent.charAt(i);
                            if (c == separatorChar()) {
                                iIdx = i + 1;
                                break;
                            }
                        }
                    }
                    if (iIdx == -1)
                        parent = null;
                    else
                        parent = parent.substring(0, iIdx);
                }
            }
        }
        super._init();
    }

    public File getOriginalFile() {
        return file;
    }

    public FileInterface duplicate() {
        NormalFile nf = new NormalFile(getAbsolutePath(), getParent(), getFileInterfaceConfiguration());
        return nf;
    }

    public BufferedInputStream getBufferedInputStream() throws FileNotFoundException, IOException {
        return new BufferedInputStream(new FileInputStream(getAbsolutePath()));
    }

    public BufferedOutputStream getBufferedOutputStream(String strNewPath) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(strNewPath));
    }

    public void closeOutputStream() throws IOException {
    }

    public FileInterface[] _listRoots() {
        if (!isCached() || fileInterfaceRoots == null) {
            File f[] = File.listRoots();
            if (f == null)
                return null;

            fileInterfaceRoots = new NormalFile[f.length];
            for (int i = 0; i < f.length; ++i) {
                fileInterfaceRoots[i] = new NormalFile(f[i].getAbsolutePath(), f[i].getParent(), getFileInterfaceConfiguration());
            }
        }
        return (FileInterface[]) fileInterfaceRoots;
    }

    public char separatorChar() {
        return File.separatorChar;
    }

    public char pathSeparatorChar() {
        return File.pathSeparatorChar;
    }

    public boolean canRead() {
        if (!isCached())
            read = file.canRead();
        return read;
    }

    public boolean canWrite() {
        if (!isCached())
            write = file.canWrite();
        return write;
    }

    public boolean isDirectory() {
        if (!isCached())
            directory = file.isDirectory();
        return directory;
    }

    public boolean isFile() {
        if (!isCached())
            fileType = file.isFile();
        return fileType;
    }

    public boolean isHidden() {
        if (!isCached())
            hidden = file.isHidden();
        return hidden;
    }

    public URL toURL() {
        URL url = null;
        try {
            url = new URL("file", null, getAbsolutePath());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return url;
    }

    public long length() {
        if (!isCached())
            length = file.length();
        return length;
    }

    public String getId() {
        return host;
    }

    public String getName() {
        if (!isCached() || name == null)
            name = file.getName();
        return name;
    }

    public String getAbsolutePath() {
        if (!isCached() || absolutePath == null)
            absolutePath = file.getAbsolutePath();
        return absolutePath;
    }

    public FileInterface getCanonicalFile() throws IOException {
        return new NormalFile(file.getCanonicalPath(), getParent(), getFileInterfaceConfiguration());
    }

    public String getPath() {
        if (!isCached() || path == null)
            path = file.getPath();
        return path;
    }

    public String getInternalPath() {
        return null;
    }

    public FileInterface getDirectAccessFileInterface() {
        return this;
    }

    public String getParent() {
        if (parent != null && !parent.equals(""))
            return parent;
        return file.getParent();
    }

    public FileInterface getParentFileInterface() {
        boolean blExtract = false;
        FileInterface fi = FileConnector.createFileInterface(getParent(), null, blExtract, getFileInterfaceConfiguration());
        return fi;
    }

    public long lastModified() {
        if (!isCached())
            modified = file.lastModified();
        return modified;
    }

    public boolean setLastModified(long time) {
        return file.setLastModified(time);
    }

    public String[] list() {
        if (!isCached() || list == null)
            list = file.list();
        return list;
    }

    public FileInterface[] _listFiles(FileInterfaceFilter filter) {
        if (!isCached() || fileInterfaces == null) {
            File[] f;
            List<FileInterface> files = new ArrayList<FileInterface>();
            if (filter == null) {
                f = file.listFiles();
                for (int i = 0; i < f.length; ++i) {
                    files.add(new NormalFile(f[i].getAbsolutePath(), f[i].getParent(), getFileInterfaceConfiguration()));
                }
            } else {
                f = file.listFiles();
                for (int i = 0; i < f.length; ++i) {
                    FileInterface file = new NormalFile(f[i].getAbsolutePath(), f[i].getParent(), getFileInterfaceConfiguration());
                    if ((filter == null) || filter.accept(file))
                        files.add(file);
                }
            }

            if (f == null)
                return null;

            fileInterfaces = (FileInterface[]) files.toArray(new FileInterface[files.size()]);
        }
        return (FileInterface[]) fileInterfaces;
    }

    public FileInterface[] _listFiles() {
        return _listFiles(null);
    }

    public boolean renameTo(FileInterface file) {
        boolean blRet = this.file.renameTo(((NormalFile) file).getOriginalFile());
        if (blRet && isCached()) {
            absolutePath = file.getAbsolutePath();
            path = file.getPath();
            name = file.getName();
        }
        return blRet;
    }

    public boolean delete() {
        return file.delete();
    }

    public boolean mkdir() {
        fileInterfaces = null;
        list = null;
        archive = false;
        directory = true;
        fileType = false;
        return file.mkdir();
    }

    public boolean exists() {
        return file.exists();
    }

    public void logon() {
        return;
    }

    public void logoff() {
        return;
    }

    @Override
    protected boolean createPysicallyNewFile() throws IOException {
        return file.createNewFile();
    }

    public void startTerminal() {
        final JShellPanel sp = new JShellPanel(getFileInterfaceConfiguration().getAppName());
        final JExDialog dlg = new JExDialog(null, "JShell - " + getId(), false, getAppName());
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
                    JShellEngine.processCommands(strbufCmd.toString(), sp.getShell().getShellEnv(), false);
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

    /**
     * ececute command
     * 
     * @param strTargetPath
     *            traget path
     * @param cmd
     *            CommandClass
     */
    public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut, OutputStream osErr, InputStream is) {
        int iRet = -1;
        try {
            iRet = FileConnector.exec(this, cmd.getCompleteCommand(this, strTargetPath), strTargetPath, cmd, osOut, osErr, is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return iRet;
    }

    public Object getConnObj() {
        return null;
    }

    public String extractToTempDir() {
        return getParent();
    }

    public void deleteOnExit() {
        file.deleteOnExit();
    }

    public boolean setReadOnly() {
        return file.setReadOnly();
    }
}