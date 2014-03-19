/**
 * 
 */
package haui.io.FileInterface.compress.compressor;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

/**
 * @author ae
 *
 */
public class XZTypeFile extends CompressorTypeFile<XZTypeFile, XZCompressorInputStream> {

	public final static String EXTENSION = ".XZ";

	public XZTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}
