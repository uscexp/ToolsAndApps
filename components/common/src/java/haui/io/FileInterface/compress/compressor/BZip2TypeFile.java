/**
 * 
 */
package haui.io.FileInterface.compress.compressor;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

/**
 * @author ae
 *
 */
public class BZip2TypeFile extends CompressorTypeFile<BZip2TypeFile, BZip2CompressorInputStream> {

	public final static String EXTENSION = ".bz2";
	public final static String EXTENSION1 = ".tbz";

	public BZip2TypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}
