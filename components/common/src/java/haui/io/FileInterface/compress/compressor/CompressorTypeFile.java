/**
 * 
 */
package haui.io.FileInterface.compress.compressor;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.NormalFile;
import haui.io.FileInterface.compress.CompressTypeFile;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.ReflectionUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.apache.commons.compress.compressors.CompressorInputStream;

/**
 * base FileInterface class for archive type files.
 * 
 * @author ae
 */
public class CompressorTypeFile<TT extends CompressorTypeFile<TT, TI>, TI extends CompressorInputStream>
		extends CompressTypeFile {

	protected String curCompressEntry;
	protected String compressEntry;
	protected Type[] types;

	public CompressorTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
		types = ReflectionUtil.getParameterizedTypes(this);
		String ge = "";
		ge = file.getName();
		int idx = -1;
		if ((idx = archPath.toLowerCase().lastIndexOf('.')) != -1)
			ge = ge.substring(0, idx - 1);
		compressEntry = ge;
		if (intPath != null && !intPath.equals(""))
			curCompressEntry = intPath;

		// init
		if (curCompressEntry == null) {
			this.read = file.canRead();
			this.write = file.canWrite();
			this.absolutePath = archPath;
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
			idx = intPath.lastIndexOf(separatorChar());
			this.name = curCompressEntry;
			if (FileConnector.isSupported(getPath()))
				this.archive = true;
			this.directory = false;
			this.fileType = true;
			this.hidden = false;
			this.modified = file.lastModified();
			this.length = 0;
		}
		_init();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BufferedInputStream getBufferedInputStream()
			throws FileNotFoundException, IOException {
		InputStream inputStream = new FileInputStream(file);
		BufferedInputStream bis = null;
		if (intPath.equals("") && curCompressEntry == null) {
			bis = new BufferedInputStream(inputStream);
		} else {
			TI compressorInputStream = (TI) ReflectionUtil.newInstance(
					types[1], inputStream);
			bis = new BufferedInputStream(compressorInputStream);
		}
		return bis;
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
		return file.setLastModified(time);
	}

	@Override
	public String[] list() {
		if (list != null)
			return list;
		list = new String[0];
		if (isDirectory() || (curCompressEntry == null && isArchive())) {
			list = new String[1];
			list[0] = compressEntry;
		}
		return list;
	}

	@Override
	public FileInterface[] _listFiles(FileInterfaceFilter filter) {
		if (!isCached() || fileInterfaces == null) {
			FileInterface[] fis = FileConnector.allocateFileInterfaceArray(
					this, 0);
			fileInterfaces = fis;
			if (isDirectory() || isArchive()) {
				FileInterface f = FileConnector.createFileInterface(
						this.getClass(), null, archPath, path,
						getAbsolutePath(), getFileInterfaceConfiguration());
				if (filter != null) {
					if (filter.accept(f)) {
						fileInterfaces = FileConnector
								.allocateFileInterfaceArray(this, 1);
						fileInterfaces[0] = f;
					}
				} else {
					fileInterfaces = FileConnector.allocateFileInterfaceArray(
							this, 1);
					fileInterfaces[0] = f;
				}
			}
		}
		return fileInterfaces;
	}

	@Override
	public boolean renameTo(FileInterface file) {
		if (curCompressEntry == null) {
			return this.file.renameTo(((NormalFile) file).getOriginalFile());
		}
		showNotSupportedMessage();
		return false;
	}

	@Override
	public boolean delete() {
		if (curCompressEntry == null) {
			return this.file.delete();
		}
		showNotSupportedMessage();
		return false;
	}

	@Override
	public boolean mkdir() {
		if (curCompressEntry == null) {
			return this.file.mkdir();
		}
		showNotSupportedMessage();
		return false;
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

}
