package haui.io.FileInterface.compress.archive;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.NormalFile;
import haui.io.FileInterface.compress.CompressTypeFile;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.GlobalApplicationContext;
import haui.util.ReflectionUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;

/**
 * base FileInterface class for archive type files.
 * 
 * @author ae
 */
public abstract class ArchiveTypeFile<TT extends ArchiveTypeFile<TT, TI, TE>, TI extends ArchiveInputStream, TE extends ArchiveEntry>
		extends CompressTypeFile {

	// member variables
	protected ArchiveEntry curArchiveEntry;
	protected List<ArchiveEntry> archiveEntries;
	protected Type[] types;

	@SuppressWarnings("unchecked")
	public ArchiveTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
		// subtype part
		types = ReflectionUtil.getParameterizedTypes(this);
		TI inputStream;
		try {
			inputStream = getArchiveInputStream();
			String entryPath = intPath;
			entryPath = entryPath.replace(separatorChar(), '/');
			if (inputStream != null) {
				TE archiveEntry = null;
				while ((archiveEntry = (TE) inputStream.getNextEntry()) != null) {
					TE jarArchiveEntry = archiveEntry;
					if (jarArchiveEntry.getName().equals(entryPath)) {
						curArchiveEntry = archiveEntry;
					}
					archiveEntries.add(archiveEntry);
				}
			}
		} catch (Exception e) {
			GlobalApplicationContext.instance().getOutputPrintStream()
					.println(e.toString());
		}

		// init
		if (!intPath.equals("") && curArchiveEntry == null) {
			this.read = false;
			this.write = false;
			this.absolutePath = archPath + separatorChar() + intPath;
			this.path = intPath;
			if (FileConnector.isSupported(getPath()))
				this.archive = true;
			this.directory = true;
			this.fileType = false;
			this.hidden = false;
			this.modified = file.lastModified();
			this.length = 0;
		} else if (curArchiveEntry == null) {
			this.read = file.canRead();
			this.write = file.canWrite();
			try {
				this.absolutePath = file.getAbsolutePath();
			} catch (Exception ex1) {
				GlobalApplicationContext.instance().getOutputPrintStream()
						.println(ex1.toString());
			}
			if (absolutePath == null)
				this.absolutePath = file.getPath();
			this.path = file.getPath();
			this.name = file.getName();
			this.parent = file.getParent();
			if (FileConnector.isSupported(getPath()))
				this.archive = true;
			this.directory = file.isDirectory();
			this.fileType = file.isFile();
			this.hidden = file.isHidden();
			this.modified = file.lastModified();
			this.length = file.length();
		} else {
			this.read = false;
			this.write = false;
			this.absolutePath = archPath + separatorChar() + intPath;
			this.path = intPath;
			if (FileConnector.isSupported(getPath()))
				this.archive = true;
			this.directory = curArchiveEntry.isDirectory();
			this.fileType = !curArchiveEntry.isDirectory();
			this.hidden = false;
			this.modified = curArchiveEntry.getLastModifiedDate().getTime();
			this.length = curArchiveEntry.getSize();
		}
		_init();
	}

	@SuppressWarnings("unchecked")
	private TI getArchiveInputStream() throws ArchiveException, IOException {
		InputStream inputStream = new FileInputStream(file);
		TI archiveInputStream = null;
		if (inputStream != null) {
			archiveInputStream = (TI) ReflectionUtil.newInstance(types[1],
					inputStream);
		}
		return archiveInputStream;
	}

	@SuppressWarnings({ "resource", "unchecked" })
	@Override
	public BufferedInputStream getBufferedInputStream()
			throws FileNotFoundException, IOException {
		BufferedInputStream bufferedInputStream = null;
		if (intPath.equals("") && curArchiveEntry == null) {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(
					file));
		} else {
			TI archiveInputStream = null;
			try {
				archiveInputStream = getArchiveInputStream();
			} catch (ArchiveException e) {
				throw new IOException(e.getMessage(), e);
			}
			if (archiveInputStream != null) {
				// position to current entry
				if (curArchiveEntry != null) {
					TE archiveEntry = null;
					while ((archiveEntry = (TE) archiveInputStream
							.getNextEntry()) != null) {
						if (archiveEntry.equals(curArchiveEntry)) {
							break;
						}
					}
				}
				bufferedInputStream = new BufferedInputStream(
						archiveInputStream);
			}
		}
		return bufferedInputStream;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FileInterface duplicate() {
		TT fi = (TT) ReflectionUtil.newInstance(types[0], archPath, intPath,
				getParent(), getFileInterfaceConfiguration());
		return fi;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FileInterface getCanonicalFile() throws IOException {
		return (TT) ReflectionUtil.newInstance(types[0], archPath, intPath,
				getParent(), getFileInterfaceConfiguration());
	}

	@Override
	public boolean setLastModified(long time) {
		if (curArchiveEntry == null) {
			return file.setLastModified(time);
		}
		showNotSupportedText();
		return false;
	}

	@Override
	public String[] list() {
		if (list != null)
			return list;
		List<String> strList = new ArrayList<>();
		list = new String[0];
		if (isDirectory() || (curArchiveEntry == null && isArchive())) {
			for (int i = 0; i < archiveEntries.size(); ++i) {
				String path = "";
				ArchiveEntry ae = (ArchiveEntry) archiveEntries.get(i);
				String archiveName = ae.getName();
				// boolean blDir = ze.isDirectory();
				boolean blInsert = false;
				String strCheck = "";
				if (!intPath.equals("")) {
					strCheck = intPath + "/";
					strCheck = strCheck.replace(separatorChar(), '/');
				}
				int idx = archiveName.indexOf(strCheck);
				if (idx != -1) {
					if (idx > 0 || !intPath.equals(""))
						idx += intPath.length();
					if (idx < archiveName.length()) {
						path = archiveName.substring(idx, archiveName.length());
						int pos = -1;
						if (path.length() > pos) {
							StringTokenizer sto = new StringTokenizer(path,
									"\\/", false);
							if (sto.hasMoreTokens()) {
								// boolean blFound = false;
								path = sto.nextToken();
								String tmp = getAbsolutePath()
										+ separatorChar() + path;
								if (!strList.contains(tmp))
									blInsert = true;
							}
						}
					} else {
						path = archiveName;
						blInsert = true;
					}
					if (blInsert) {
						strList.add(getAbsolutePath() + separatorChar() + path);
						blInsert = false;
					}
				}
			}
			list = new String[strList.size()];
			for (int j = 0; j < strList.size(); ++j) {
				list[j] = (String) strList.get(j);
			}
		}
		return list;
	}

	@Override
	public FileInterface[] _listFiles(FileInterfaceFilter filter) {
		if (!isCached() || fileInterfaces == null) {
			FileInterface[] fis = FileConnector.allocateFileInterfaceArray(
					this, 0);
			List<FileInterface> fileInterfaceList = null;
			fileInterfaces = fis;
			if (isDirectory() || isArchive()) {
				String[] str = list();
				if (str != null) {
					if (str.length > 0) {
						if (filter == null)
							fis = FileConnector.allocateFileInterfaceArray(
									this, str.length);
						else
							fileInterfaceList = new ArrayList<>();
						for (int j = 0; j < str.length; ++j) {
							String path = "";
							int idx = archPath.length() + 1;
							if (idx > 0 && idx < str[j].length())
								path = str[j].substring(idx, str[j].length());
							FileInterface f = FileConnector
									.createFileInterface(this.getClass(), null,
											archPath, path, getAbsolutePath(),
											getFileInterfaceConfiguration());
							if (filter == null)
								fis[j] = f;
							else {
								if (filter.accept(f))
									fileInterfaceList.add(f);
							}
						}
						if (filter != null) {
							fis = FileConnector.allocateFileInterfaceArray(
									this, fileInterfaceList.size());
							for (int j = 0; j < fileInterfaceList.size(); ++j)
								fis[j] = (FileInterface) fileInterfaceList
										.get(j);
						}
						fileInterfaces = fis;
					}
				}
			}
		}
		return fileInterfaces;
	}

	@Override
	public boolean renameTo(FileInterface file) {
		if (curArchiveEntry == null) {
			return this.file.renameTo(((NormalFile) file).getOriginalFile());
		}
		showNotSupportedMessage();
		return false;
	}

	@Override
	public boolean delete() {
		if (curArchiveEntry == null) {
			return file.delete();
		}
		showNotSupportedMessage();
		return false;
	}

	@Override
	public boolean mkdir() {
		if (curArchiveEntry == null) {
			this.fileInterfaces = null;
			this.list = null;
			this.archive = false;
			this.directory = true;
			this.fileType = false;
			return file.mkdir();
		}
		showNotSupportedMessage();
		return false;
	}

	@Override
	public boolean exists() {
		boolean blRet = false;
		if (curArchiveEntry == null && intPath.equals(""))
			return file.exists();
		blRet = file.exists();
		if (blRet && curArchiveEntry != null) {
			blRet = false;
			StringBuffer strbufEntry = new StringBuffer(
					curArchiveEntry.getName());
			if (strbufEntry.charAt(strbufEntry.length() - 1) == '/'
					|| strbufEntry.charAt(strbufEntry.length() - 1) == separatorChar())
				strbufEntry.deleteCharAt(strbufEntry.length() - 1);
			StringBuffer strbufIntPath = new StringBuffer(intPath);
			if (strbufIntPath.charAt(strbufIntPath.length() - 1) == separatorChar())
				strbufIntPath.deleteCharAt(strbufIntPath.length() - 1);
			String strEntry = strbufEntry.toString();
			strEntry = strEntry.replace('/', separatorChar());
			String strIntPath = strbufIntPath.toString();
			if (strEntry.equals(strIntPath))
				blRet = true;
		} else
			blRet = false;
		return blRet;
	}

}
