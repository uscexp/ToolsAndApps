package haui.io.FileInterface;

import haui.components.CancelProgressDialog;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import haui.util.CommandClass;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Module: FileInterface.java<br>
 * $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\
 * FileInterface.java,v $
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
 *          $Log: FileInterface.java,v $ Revision 1.2 2004-08-31 16:03:27+02
 *          t026843 Large redesign for application dependent outputstreams,
 *          mainframes, AppProperties! Bugfixes to DbTreeTableView, additional
 *          features for jDirWork. Revision 1.1 2004-06-22 14:08:49+02 t026843
 *          bigger changes Revision 1.0 2003-06-06 10:05:36+02 t026843 Initial
 *          revision Revision 1.2 2003-06-04 15:35:56+02 t026843 bugfixes
 *          Revision 1.1 2003-05-28 14:19:53+02 t026843 reorganisations Revision
 *          1.0 2003-05-21 16:25:48+02 t026843 Initial revision Revision 1.5
 *          2002-09-18 11:16:20+02 t026843 - changes to fit extended
 *          filemanager.pl - logon and logoff moved to 'TypeFile's -
 *          startTerminal() added to 'TypeFile's, but only CgiTypeFile (until
 *          now) starts the LRShell as terminal - LRShell changed to work with
 *          filemanager.pl Revision 1.4 2002-09-03 17:07:59+02 t026843 -
 *          CgiTypeFile is now full functional. - Migrated to the extended
 *          filemanager.pl script. Revision 1.3 2002-08-07 15:25:27+02 t026843
 *          Ftp support via filetype added. Some bugfixes. Revision 1.2
 *          2002-06-21 11:00:17+02 t026843 Added gzip and bzip2 file support
 *          Revision 1.1 2002-06-19 16:13:52+02 t026843 Zip file support;
 *          writing doesn't work yet! Revision 1.0 2002-06-17 17:21:18+02
 *          t026843 Initial revision
 *          </p>
 *          <p>
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2002; $Revision: 1.2 $<br>
 *          $Header:
 *          M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\
 *          io\\FileInterface.java,v 1.2 2004-08-31 16:03:27+02 t026843 Exp
 *          t026843 $
 *          </p>
 *          <p>
 * @since JDK1.2
 *        </p>
 */
public interface FileInterface extends Comparable<FileInterface> {
	// constants
	public final static int BUFFERSIZE = 32768;
	public final static String TEMPDIR = "TempDir";
	public final static String EXTENDEDPATH = "ExtPath";
	public final static String SEARCHDEPTH = "SearchDepth";
	public final static String LOCAL = "local";
	public final static String SOCKET = "socket";
	public final static String COMMPORT = "commport";

	// member variables

	public String getTempDirectory();

	public BufferedInputStream getBufferedInputStream()
			throws FileNotFoundException, IOException;

	public BufferedOutputStream getBufferedOutputStream(String strNewPath)
			throws FileNotFoundException, IOException;

	public FileInterface duplicate();

	public FileInterfaceConfiguration getFileInterfaceConfiguration();

	/**
	 * @param fic
	 * @uml.property name="fileInterfaceConfiguration"
	 */
	public void setFileInterfaceConfiguration(FileInterfaceConfiguration fic);

	public FileInterface[] _listRoots();

	public boolean equals(Object obj);

	public boolean isCached();

	public char separatorChar();

	public char pathSeparatorChar();

	public boolean canRead();

	public boolean canWrite();

	public boolean isAbsolutePath(String strPath);

	public boolean isAbsolute();

	public boolean isDirectory();

	public boolean isArchive();

	public boolean isFile();

	public boolean isHidden();

	public boolean isRoot();

	/**
	 * Returns the canonical pathname string of this abstract pathname.
	 * 
	 * <p>
	 * A canonical pathname is both absolute and unique. The precise definition
	 * of canonical form is system-dependent. This method first converts this
	 * pathname to absolute form if necessary, as if by invoking the
	 * {@link #getAbsolutePath} method, and then maps it to its unique form in a
	 * system-dependent way. This typically involves removing redundant names
	 * such as <tt>"."</tt> and <tt>".."</tt> from the pathname, resolving
	 * symbolic links (on UNIX platforms), and converting drive letters to a
	 * standard case (on Microsoft Windows platforms).
	 * 
	 * <p>
	 * Every pathname that denotes an existing file or directory has a unique
	 * canonical form. Every pathname that denotes a nonexistent file or
	 * directory also has a unique canonical form. The canonical form of the
	 * pathname of a nonexistent file or directory may be different from the
	 * canonical form of the same pathname after the file or directory is
	 * created. Similarly, the canonical form of the pathname of an existing
	 * file or directory may be different from the canonical form of the same
	 * pathname after the file or directory is deleted.
	 * 
	 * @return The canonical pathname string denoting the same file or directory
	 *         as this abstract pathname
	 * 
	 * @throws IOException
	 *             If an I/O error occurs, which is possible because the
	 *             construction of the canonical pathname may require filesystem
	 *             queries
	 * 
	 * @throws SecurityException
	 *             If a required system property value cannot be accessed.
	 * 
	 * @since JDK1.1
	 */
	public String getCanonicalPath() throws IOException;

	/**
	 * Returns the canonical form of this abstract pathname. Equivalent to
	 * <code>new&nbsp;FileInterface(this.{@link #getCanonicalPath}())</code>.
	 * 
	 * @return The canonical pathname string denoting the same file or directory
	 *         as this abstract pathname
	 * 
	 * @throws IOException
	 *             If an I/O error occurs, which is possible because the
	 *             construction of the canonical pathname may require filesystem
	 *             queries
	 * 
	 * @throws SecurityException
	 *             If a required system property value cannot be accessed.
	 * 
	 * @since 1.2
	 */
	public FileInterface getCanonicalFile() throws IOException;

	public URL toURL();

	public long length();

	public String getId();

	public String getHost();

	public String getName();

	public String getAbsolutePath();

	public String getPath();

	public String getInternalPath();

	public FileInterface getDirectAccessFileInterface();

	public String getParent();

	public FileInterface getParentFileInterface();

	public FileInterface getRootFileInterface();

	public AppProperties getAppProperties();

	public String getAppName();

	public long lastModified();

	public boolean setLastModified(long time);

	public String[] list();

	public FileInterface[] _listFiles(FileInterfaceFilter filter);

	public FileInterface[] _listFiles();

	public FileInterface[] _listFiles(boolean dontShowHidden);

	public FileInterface[] _listFiles(FileInterfaceFilter filter,
			boolean dontShowHidden);

	public boolean renameTo(FileInterface file);

	public boolean delete();

	public boolean mkdir();

	public boolean mkdirs();

	public boolean exists();

	public void logon();

	public void logoff();

	public void startTerminal();

	public boolean createNewFile() throws IOException;

	public void deleteOnExit();

	public boolean setReadOnly();

	/**
	 * ececute command
	 * 
	 * @param strTargetPath
	 *            traget path
	 * @param cmd
	 *            CommandClass
	 */
	public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut,
			OutputStream osErr, InputStream is);

	/**
	 * @return
	 * @uml.property name="currentProcess"
	 */
	public Process getCurrentProcess();

	/**
	 * @param proc
	 * @uml.property name="currentProcess"
	 */
	public void setCurrentProcess(Process proc);

	public void killCurrentProcess();

	public Object getConnObj();

	/**
	 * copy file
	 * 
	 * @param bo
	 *            : BufferedOutputStream to destination
	 */
	public boolean copyFile(BufferedOutputStream bo) throws IOException;

	/**
	 * copy file
	 * 
	 * @param bo
	 *            : BufferedOutputStream to destination
	 * @param cpd
	 *            : CancelProgressDialog
	 */
	public boolean copyFile(BufferedOutputStream bo, CancelProgressDialog cpd)
			throws IOException;

	public String extractToTempDir();
}