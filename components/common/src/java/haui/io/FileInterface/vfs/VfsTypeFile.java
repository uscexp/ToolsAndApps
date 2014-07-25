/**
 * 
 */
package haui.io.FileInterface.vfs;

import haui.exception.AppSystemException;
import haui.io.FileConnector;
import haui.io.FileInterface.BaseTypeFile;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.VfsTypeFileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;
import haui.util.ReflectionUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

/**
 * Base class for Apache virtual file systems files.
 * 
 * @author ae
 * 
 */
public abstract class VfsTypeFile<TT extends VfsTypeFile<TT>> extends BaseTypeFile {

    protected FileSystemManager fileSystemManager;
    protected FileObject fileObject;
    protected Type[] types;

    public VfsTypeFile(String path, String parentPath, FileInterfaceConfiguration fic) {
        super(fic);
        this.path = path;
        this.parent = parentPath;
        try {
            fileSystemManager = VFS.getManager();
            logon();
            if (this.parent == null) {
                this.parent = getParent();
            }
            types = ReflectionUtil.getParameterizedTypes(this);
            _init();
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    @Override
    public BufferedInputStream getBufferedInputStream() throws FileNotFoundException, IOException {
        FileContent fileContent = fileObject.getContent();
        InputStream inputStream = fileContent.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return bufferedInputStream;
    }

    @SuppressWarnings("resource")
    @Override
    public BufferedOutputStream getBufferedOutputStream(String strNewPath) throws FileNotFoundException, IOException {
        BufferedOutputStream bufferedOutputStream = null;
        if (strNewPath.indexOf(getParent()) == -1) {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(strNewPath));
        } else {
            try {
                FileObject fo = fileSystemManager.resolveFile(((VfsTypeFileInterfaceConfiguration) fic).getUri(strNewPath));

                OutputStream outputStream = fo.getContent().getOutputStream();
                bufferedOutputStream = new BufferedOutputStream(outputStream);
            } catch (IOException ioex) {
                throw new AppSystemException(ioex);
            }
        }
        return bufferedOutputStream;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FileInterface duplicate() {
        TT fi = (TT) ReflectionUtil.newInstance(types[0], path, getParent(), getFileInterfaceConfiguration());
        return fi;
    }

    @Override
    public boolean canRead() {
        try {
            if (!isCached())
                read = fileObject.isReadable();
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return read;
    }

    @Override
    public boolean canWrite() {
        boolean result = false;
        try {
            result = fileObject.isWriteable();
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public boolean isDirectory() {
        boolean result = false;
        try {
            result = fileObject.getType() == FileType.FOLDER;
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public boolean isFile() {
        boolean result = false;
        try {
            result = fileObject.getType() == FileType.FILE;
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public boolean isHidden() {
        boolean result = false;
        try {
            result = fileObject.isHidden();
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FileInterface getCanonicalFile() throws IOException {
        FileInterface result = (TT) ReflectionUtil.newInstance(types[0], path, getParent(), getFileInterfaceConfiguration());
        return result;
    }

    @Override
    public URL toURL() {
        URL result = null;
        try {
            result = fileObject.getURL();
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public long length() {
        try {
            if (!isCached())
                length = fileObject.getContent().getSize();
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return length;
    }

    @Override
    public String getName() {
        if (!isCached())
            name = fileObject.getName().getBaseName();
        return name;
    }

    @Override
    public String getAbsolutePath() {
        if (!isCached())
            absolutePath = fileObject.getName().getPath();
        return absolutePath;
    }

    @Override
    public String getPath() {
        if (!isCached())
            path = fileObject.getName().getPath();
        return path;
    }

    @Override
    public String getInternalPath() {
        return getPath();
    }

    @Override
    public FileInterface getDirectAccessFileInterface() {
        FileInterface fi = this;
        if (getId() != LOCAL) {
            String strPath = getAbsolutePath();

            int iStat;
            iStat = JOptionPane.showConfirmDialog(GlobalApplicationContext.instance().getRootComponent(), "Extract to temp and execute?", "Confirmation", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (iStat == JOptionPane.NO_OPTION) {
                GlobalApplicationContext.instance().getOutputPrintStream().println("...cancelled");
                // System.out.println( "...cancelled" );
                return null;
            }
            strPath = extractToTempDir();
            if (strPath != null)
                fi = FileConnector.createFileInterface(strPath, null, false, getFileInterfaceConfiguration());
        }
        return fi;
    }

    @Override
    public String getParent() {
        try {
            if (!isCached()) {
                FileObject fo = fileObject.getParent();
                if (fo != null)
                    parent = fo.getName().getPath();
            }
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return parent;
    }

    @Override
    public FileInterface getParentFileInterface() {
        FileInterface fi = FileConnector.createFileInterface(getParent(), null, false, getFileInterfaceConfiguration());
        return fi;
    }

    @Override
    public long lastModified() {
        try {
            if (!isCached()) {
                modified = fileObject.getContent().getLastModifiedTime();
            }
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return modified;
    }

    @Override
    public boolean setLastModified(long time) {
        boolean result = true;
        try {
            fileObject.getContent().setLastModifiedTime(time);
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public String[] list() {
        try {
            if (list == null || !isCached()) {
                list = new String[0];
                if (isDirectory() || isArchive()) {
                    FileObject[] files = fileObject.getChildren();
                    list = new String[Array.getLength(files)];
                    for (int i = 0; i < Array.getLength(files); ++i) {
                        list[i] = files[i].getName().getPath();
                    }
                }
            }
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FileInterface[] _listFiles(FileInterfaceFilter filter) {
        try {
            if (!isCached() || fileInterfaces == null) {
                VfsTypeFile<TT>[] vfsTypeFiles = (VfsTypeFile<TT>[]) FileConnector.allocateFileInterfaceArray(this, 0);
                List<VfsTypeFile<TT>> vfsTypeFileList = null;
                fileInterfaces = vfsTypeFiles;
                if (isDirectory() || isArchive()) {
                    FileObject[] files = fileObject.getChildren();
                    if (files != null) {
                        if (Array.getLength(files) > 0) {
                            if (filter == null)
                                vfsTypeFiles = new VfsTypeFile[Array.getLength(files)];
                            else
                                vfsTypeFileList = new ArrayList<>();
                            for (int j = 0; j < Array.getLength(files); ++j) {
                                VfsTypeFile<TT> vfsFile = (TT) ReflectionUtil.newInstance(types[0], files[j].getName().getPath(), files[j].getName().getParent(), getFileInterfaceConfiguration());

                                if (filter == null)
                                    vfsTypeFiles[j] = vfsFile;
                                else {
                                    if (filter.accept(vfsFile))
                                        vfsTypeFileList.add(vfsFile);
                                }
                            }
                            if (filter != null) {
                                vfsTypeFiles = (VfsTypeFile<TT>[]) FileConnector.allocateFileInterfaceArray(this, vfsTypeFileList.size());
                                ;
                                for (int j = 0; j < vfsTypeFileList.size(); ++j)
                                    vfsTypeFiles[j] = (VfsTypeFile<TT>) vfsTypeFileList.get(j);
                            }
                            fileInterfaces = (haui.io.FileInterface.FileInterface[]) vfsTypeFiles;
                        }
                    }
                }
            }
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return fileInterfaces;
    }

    public FileInterface[] _listRoots() {
        if (fileInterfaceRoots != null)
            return fileInterfaceRoots;
        try {
            if (getId() == LOCAL) {
                fileInterfaceRoots = super._listRoots();
            } else {
                FileObject f = fileObject.getFileSystem().getRoot();
                if (f == null)
                    return null;

                FileInterface[] file = new FileInterface[1];
                file[0] = FileConnector.createFileInterface(f.getName().getPath(), f.getParent().getName().getPath(), false, getFileInterfaceConfiguration());
                fileInterfaceRoots = (FileInterface[]) file;
            }
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return fileInterfaceRoots;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean renameTo(FileInterface file) {
        boolean result = true;
        try {
            FileObject dest = ((VfsTypeFile<TT>) file).getFileObject();
            fileObject.moveTo(dest);
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public boolean delete() {
        boolean result = true;
        try {
            fileObject.delete();
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public boolean mkdir() {
        boolean result = true;
        try {
            fileInterfaces = null;
            list = null;
            archive = false;
            directory = true;
            fileType = false;
            if (isDirectory())
                fileObject.createFolder();
            else
                result = false;
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public boolean mkdirs() {
        return mkdir();
    }

    @Override
    public boolean exists() {
        boolean result = true;
        try {
            result = fileObject.exists();
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public void deleteOnExit() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean setReadOnly() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean createPysicallyNewFile() throws IOException {
        boolean result = true;
        try {
            fileInterfaces = null;
            list = null;
            archive = false;
            directory = false;
            fileType = true;
            if (isDirectory())
                fileObject.createFile();
            else
                result = false;
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
        return result;
    }

    @Override
    public void logon() {
        try {
            fileObject = fileSystemManager.resolveFile(((VfsTypeFileInterfaceConfiguration) fic).getUri(path), ((VfsTypeFileInterfaceConfiguration) fic).getFileSystemOptions());
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
    }

    @Override
    public void logoff() {
        fileSystemManager.closeFileSystem(fileObject.getFileSystem());
    }

    @Override
    public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut, OutputStream osErr, InputStream is) {
        FileInterface fi = this;
        int iRet = -1;
        fi = getDirectAccessFileInterface();
        try {
            iRet = FileConnector.exec(fi, cmd.getCompleteCommand(fi, fi.getAbsolutePath(), strTargetPath), strTargetPath, cmd, osOut, osErr, is);
        } catch (IOException ex) {
            throw new AppSystemException(ex);
        }
        return iRet;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAbsolutePath() == null) ? 0 : getAbsolutePath().hashCode()) + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        VfsTypeFile<TT> fi = (VfsTypeFile<TT>) obj;
        if (fi.getAbsolutePath().equals(getAbsolutePath()) && fi.canRead() == canWrite() && fi.length() == length() && fi.getId() == getId()) {
            return true;
        }
        return true;
    }

}
