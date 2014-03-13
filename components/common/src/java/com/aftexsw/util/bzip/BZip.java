/*
 * Copyright (c) 1999-2001 Keiron Liddle, Aftex Software
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
*/
/**
 * Does all the compress and decompress pre-operation stuff.
 * Sets up the streams and file header characters.
 * Uses multiply overloaded methods to call for the compress/decompress.
 */
package com.aftexsw.util.bzip;

import java.io.*;
import java.lang.*;

/**
 * Module:      BZip<br> <p> Description: BZip<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
 * @history      08.04.2008 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2008; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public final class BZip {
	final public static void decompress(File infile, File outfile) {
		decompress(infile, outfile, dummyProg);
	}

	final public static void compress(File infile, File outfile, int blockSize) {
		compress(infile, outfile, blockSize, dummyProg);
	}

	final public static void decompress(String infilename, String outfilename, ProgressListener prog) {
		File theinFile = new File(infilename);
		File theoutFile = new File(outfilename);
		decompress(theinFile, theoutFile, prog);
	}

	final public static void decompress(File infile, File outfile, ProgressListener prog) {
		try {
			FileOutputStream fos = new FileOutputStream(outfile);
			FileInputStream fis = new FileInputStream(infile);
			decompress(fis, fos, prog);
			fis.close();
			fos.close();
			//infile.close();
			//outfile.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	final public static void decompress(InputStream instream, File outfile, ProgressListener prog) {
		try {
			FileOutputStream fos = new FileOutputStream(outfile);
			decompress(instream, fos, prog);
			fos.close();
			//outfile.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	final public static void decompress(File infile, OutputStream outstream, ProgressListener prog) {
		try {
			FileInputStream fis = new FileInputStream(infile);
			decompress(fis, outstream, prog);
			fis.close();
			//infile.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use the BZip2InputStream to uncompress data.
	*/
	final public static void decompress(InputStream instream, OutputStream outstream, ProgressListener prog) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(outstream);
			BufferedInputStream bis = new BufferedInputStream(instream);
			int b = bis.read();
			if(b != 'B')
				return;
			b = bis.read();
			if(b != 'Z')
				return;
			CBZip2InputStream bzis = new CBZip2InputStream(bis);
			int ch = bzis.read();
			while(ch != -1) {
				bos.write(ch);
				ch = bzis.read();
			}
			bos.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	final public static boolean test(String infilename) {
		File theinFile = new File(infilename);
		return test(theinFile);
	}

	final public static boolean test(File infile) {
		try {
			FileInputStream fis = new FileInputStream(infile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			int b = bis.read();
			if(b != 'B') {
				System.out.println("incorrect start characters");
				return false;
			}
			b = bis.read();
			if(b != 'Z') {
				System.out.println("incorrect start characters");
				return false;
			}
			CBZip2Decompress cbd = new CBZip2Decompress(dummyProg);
			//return cbd.testStream(bis);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	final public static ProgressListener dummyProg = new ProgressListener() {
				public void setProgress(long val) {}
			}
			;

	final public static void compress(String infilename, String outfilename, int blockSize, ProgressListener prog) {
		File theinFile = new File(infilename);
		File theoutFile = new File(outfilename);
		compress(theinFile, theoutFile, blockSize, prog);
	}

	final public static void compress(File infile, File outfile, int blockSize, ProgressListener prog) {
		try {
			FileOutputStream fos = new FileOutputStream(outfile);
			FileInputStream fis = new FileInputStream(infile);
			compress(fis, fos, blockSize, prog);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	final public static void compress(InputStream instream, File outfile, int blockSize, ProgressListener prog) {
		try {
			FileOutputStream fos = new FileOutputStream(outfile);
			compress(instream, fos, blockSize, prog);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	final public static void compress(File infile, OutputStream outstream, int blockSize, ProgressListener prog) {
		try {
			FileInputStream fis = new FileInputStream(infile);
			compress(fis, outstream, blockSize, prog);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Use the CBZip2OutputStream to compress data.
	*/
	final public static void compress(InputStream instream, OutputStream outstream, int blockSize, ProgressListener prog) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(outstream);
			bos.write('B');
			bos.write('Z');
			BufferedInputStream bis = new BufferedInputStream(instream);
			int ch = bis.read();
			CBZip2OutputStream bzos = new CBZip2OutputStream(bos);
			while(ch != -1) {
				bzos.write(ch);
				ch = bis.read();
			}
			bzos.close();
			bos.flush();
			bos.close();
			outstream.flush();
			outstream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.gc();
	}
}
