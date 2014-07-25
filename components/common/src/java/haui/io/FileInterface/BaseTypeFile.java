package haui.io.FileInterface;

import haui.components.CancelProgressDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * Module: BaseTypeFile<br>
 * $Source: $
 * <p>
 * Description: base FileInterface class.<br>
 * </p>
 * <p>
 * Created: 17.12.2004 by AE
 * </p>
 * <p>
 * 
 * @history 17.12.2004 by AE: Created.<br>
 *          </p>
 *          <p>
 *          Modification:<br>
 *          $Log: $
 *          </p>
 *          <p>
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2004; $Revision: $<br>
 *          $Header: $
 *          </p>
 *          <p>
 * @since JDK1.2
 *        </p>
 */
public abstract class BaseTypeFile implements FileInterface {
    // member variables
    protected FileInterfaceConfiguration fic;
    protected Process procCurrent = null;

    // cache variables
    protected boolean read = false;
    protected boolean write = false;
    protected String absolutePath;
    protected String path;
    protected String name;
    protected String parent;
    protected boolean archive = false;
    protected boolean directory = false;
    protected boolean fileType = false;
    protected boolean hidden;
    protected long modified;
    protected long length;
    protected String[] list = null;
    protected FileInterface[] fileInterfaces = null;
    protected FileInterface[] fileInterfaceRoots = null;
    protected String host = LOCAL;
    private boolean init = false;

    public BaseTypeFile(FileInterfaceConfiguration fic) {
        setFileInterfaceConfiguration(fic);
    }

    /**
     * must be called from subclasses after initialising an FileInterface to
     * cache (if set to true) important attributes!
     */
    public void _init() {
        if (getFileInterfaceConfiguration().isCached()) {
            read = canRead();
            write = canWrite();
            archive = isArchive();
            directory = isDirectory();
            fileType = isFile();
            hidden = isHidden();
            modified = lastModified();
            length = length();
            init = true;
        }
    }

    public boolean equals(Object obj) {
        boolean blRet = false;

        if (obj instanceof FileInterface) {
            FileInterface fi = (FileInterface) obj;
            if (fi.getAbsolutePath().equals(getAbsolutePath()) && fi.canRead() == canWrite() && fi.length() == length() && fi.getId() == getId()) {
                blRet = true;
            }
        }
        return blRet;
    }

    public FileInterfaceConfiguration getFileInterfaceConfiguration() {
        return fic;
    }

    public void setFileInterfaceConfiguration(FileInterfaceConfiguration fic) {
        this.fic = fic;
    }

    public String getTempDirectory() {
        String strTmpDir = FileConnector.getTempDirectory();
        if (strTmpDir == null) {
            strTmpDir = new File(".").getAbsolutePath();
            if (strTmpDir == null)
                strTmpDir = "";
            FileConnector.setTempDirectory(strTmpDir);
        } else {
            int idx = strTmpDir.lastIndexOf(separatorChar());
            if (idx > 0 && idx == strTmpDir.length() - 1)
                strTmpDir = strTmpDir.substring(0, strTmpDir.length() - 1);
        }
        return strTmpDir;
    }

    public FileInterface[] _listRoots() {
        if (fileInterfaceRoots != null)
            return fileInterfaceRoots;
        File[] f = null;
        if (host != LOCAL) {
            int idx = getAbsolutePath().indexOf(separatorChar());
            if (idx < 0 && getAbsolutePath() != null) {
                f = new File[1];
                f[0] = new File(getAbsolutePath());
            } else if (idx == 0) {
                f = new File[1];
                f[0] = new File(String.valueOf(separatorChar()));
            } else {
                f = new File[1];
                String p = getAbsolutePath().substring(0, idx);
                f[0] = new File(p);
            }
        } else {
            f = File.listRoots();
        }
        if (f == null)
            return null;

        FileInterface[] file = new FileInterface[Array.getLength(f)];
        for (int i = 0; i < Array.getLength(f); ++i) {
            file[i] = FileConnector.createFileInterface(f[i].getAbsolutePath(), f[i].getParent(), false, getFileInterfaceConfiguration());
        }
        fileInterfaceRoots = (FileInterface[]) file;
        return fileInterfaceRoots;
    }

    public boolean isCached() {
        return getFileInterfaceConfiguration().isCached() && init;
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

    public boolean isAbsolutePath(String strPath) {
        boolean blRet = false;
        FileInterface[] fi = _listRoots();

        int idx = strPath.lastIndexOf(separatorChar());
        if (idx == -1 || idx != strPath.length() - 1)
            strPath = strPath + separatorChar();

        for (int i = 0; i < fi.length; ++i) {
            if (strPath.toUpperCase().startsWith(fi[i].getAbsolutePath().toUpperCase())) {
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

    public boolean isArchive() {
        if (isCached())
            return archive;
        if (FileConnector.isSupported(getAbsolutePath()))
            return true;
        return false;
    }

    public String getHost() {
        return host;
    }

    public FileInterface getRootFileInterface() {
        FileInterface fiRet = null;
        FileInterface fiRoots[] = null;

        if ((fiRoots = _listRoots()) != null) {
            for (int i = 0; i < fiRoots.length; ++i) {
                if (getAbsolutePath().toUpperCase().startsWith(fiRoots[i].getAbsolutePath().toUpperCase())) {
                    fiRet = fiRoots[i];
                    break;
                }
            }
        }
        return fiRet;
    }

    public boolean isRoot() {
        FileInterface fiRoot = getRootFileInterface();
        if (fiRoot != null && getAbsolutePath().equalsIgnoreCase(fiRoot.getAbsolutePath()))
            return true;
        else
            return false;
    }

    public AppProperties getAppProperties() {
        return getFileInterfaceConfiguration().getAppProperties();
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAppName() {
        return getFileInterfaceConfiguration().getAppName();
    }

    public FileInterface[] _listFiles() {
        return _listFiles(null);
    }

    public FileInterface[] _listFiles(boolean dontShowHidden) {
        return _listFiles(null, dontShowHidden);
    }

    public FileInterface[] _listFiles(FileInterfaceFilter filter, boolean dontShowHidden) {
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

    public Process getCurrentProcess() {
        return procCurrent;
    }

    public void setCurrentProcess(Process proc) {
        this.procCurrent = proc;
    }

    public void killCurrentProcess() {
        if (procCurrent != null) {
            procCurrent.destroy();
            procCurrent = null;
        }
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

    public int compareTo(FileInterface file) {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return getAbsolutePath().toUpperCase().compareTo(((FileInterface) file).getAbsolutePath().toUpperCase());
        } else {
            return getAbsolutePath().compareTo(((FileInterface) file).getAbsolutePath());
        }
    }

    public boolean createNewFile() throws IOException {
        boolean result = false;
        if (exists()) {
            result = createPysicallyNewFile();
        }
        return result;
    }

    protected abstract boolean createPysicallyNewFile() throws IOException;

    protected void showNotSupportedMessage() {
        JOptionPane.showMessageDialog(GlobalApplicationContext.instance().getRootComponent(), "This function is not supported for this filetype!", "Filetype info", JOptionPane.WARNING_MESSAGE);
    }

    protected void showNotSupportedText() {
        GlobalApplicationContext.instance().getErrorPrintStream().println("This function is not supported for this filetype!");
    }
}
