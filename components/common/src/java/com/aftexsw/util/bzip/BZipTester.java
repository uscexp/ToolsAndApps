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
 * Tester class for BZip streams.
 */
package com.aftexsw.util.bzip;

import java.io.*;
import java.lang.reflect.*;

final public class BZipTester {

	public static void main(String args[]) {
		BZipTester tester = new BZipTester();
		tester.runTests();
	}

	public BZipTester() {}

	public void runTests() {
		String fName = "testdata.raw";
		long startTime;
		for(int count = 0; count < 1000;count++) {
			System.out.println("test:" + count);
			File file = new File(fName);
			createRandomData(file);
			String fcomp = "testdata.bz2";
			File compFile = new File(fcomp);
			int block = (int)(Math.random() * 10) + 1;
			System.out.println("compressing data:" + block);
			startTime= System.currentTimeMillis();
			compressData(file, compFile, block);
			System.out.println("time:" + (System.currentTimeMillis() - startTime));
			System.out.println("compressed size:" + compFile.length() + " - " + (100 * compFile.length() / (double)file.length()) + "%");
			String fout = "testdata.out";
			File out = new File(fout);
			System.out.println("uncompressing data:");
			startTime= System.currentTimeMillis();
			uncompressData(compFile, out);
			System.out.println("time:" + (System.currentTimeMillis() - startTime));
			if(!compareFiles(file, out)) {
				System.out.println("files DIFFER");
				System.exit(-1);
			} else {
				System.out.println("files are same");
			}
		}
	}

	protected void createRandomData(File out) {
		try {
			FileOutputStream outstream = new FileOutputStream(out);
			BufferedOutputStream bos = new BufferedOutputStream(outstream);
			int size = (int)(Math.random() * 1024 * 1024 * 1/*0*/);
			int runs = (int)(Math.random() * 5);
			System.out.println("creating random data:" + size + " type:" + runs);
			if(runs == 0) {
				// runs
				int pos = 0;
				while(pos < size) {
					int runlength = (int)(Math.random() * 1024 * 10);
					int ch;
					ch = (int)(Math.random() * 1000);
					for (int count = 0; count < runlength; count++) {
						bos.write(ch);
						pos++;
						if(pos > size) {
							break;
						}
					}
				}
			} else if(runs == 1) {
				// text - not really since it is still random
				for (int count = 0; count < size; count++) {
					int ch;
					ch = (char)(Math.random() * 32);
					bos.write(ch);
				}
			} else if(runs == 2) {
				// 1/4 range
				for (int count = 0; count < size; count++) {
					int ch;
					ch = (char)(Math.random() * 64);
					bos.write(ch);
				}
			} else if(runs == 3) {
				// small runs
				int pos = 0;
				while(pos < size) {
					int runlength = (int)(Math.random() * 10
							     );
					int ch;
					ch = (int)(Math.random() * 32);
					for (int count = 0; count < runlength; count++) {
						bos.write(ch);
						pos++;
						if(pos > size) {
							break;
						}
					}
				}
			} else {
				// random data
				for (int count = 0; count < size; count++) {
					int ch;
					ch = (int)(Math.random() * 32767 - 65535);
					bos.write(ch);
				}
			}
			bos.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	protected boolean compareFiles(File f1, File f2) {
		if(f1.length() != f2.length()) {
			return false;
		}
		try {
			InputStream is1 = new BufferedInputStream(new FileInputStream(f1));
			InputStream is2 = new BufferedInputStream(new FileInputStream(f2));
			while (true) {
				int ch1 = is1.read();
				int ch2 = is2.read();
				if (ch1 == ch2) {
					if (ch1 == -1) {
						return true;
					}
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	protected void compressData(File in, File out, int block) {
		try {
			FileOutputStream outstream = new FileOutputStream(out);
			BufferedOutputStream bos = new BufferedOutputStream(outstream);
			bos.write('B');
			bos.write('Z');
			FileInputStream instream = new FileInputStream(in);
			BufferedInputStream bis = new BufferedInputStream(instream);
			int ch = bis.read();
			CBZip2OutputStream bzos = new CBZip2OutputStream(bos, block);
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
	}

	protected void uncompressData(File in, File out) {
		try {
			FileOutputStream outstream = new FileOutputStream(out);
			FileInputStream instream = new FileInputStream(in);
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
}
