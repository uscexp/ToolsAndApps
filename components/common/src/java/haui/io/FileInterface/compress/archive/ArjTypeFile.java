/**
 * 
 */
package haui.io.FileInterface.compress.archive;

import org.apache.commons.compress.archivers.arj.ArjArchiveEntry;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

/**
 * @author ae
 *
 */
public class ArjTypeFile extends ArchiveTypeFile<ArjTypeFile, ArjArchiveInputStream, ArjArchiveEntry> {

	public final static String EXTENSION = ".arj";

	public ArjTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}
