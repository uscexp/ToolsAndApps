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
 * Main class to run the BZip program from.
 * Mostly just uses the command line and performs the appropriate operation.
 * Calls stuff in the BZip class to do the compress/decompress operations.
 * If the gui is used it call the BZipGui class.
 *
 * TODO:	Deal properly with stdout/stdin
 *			Improve flag handling.
 */
package com.aftexsw.util.bzip;

import java.io.*;
import java.lang.reflect.*;

final public class Main {
	final private static void usage() {
		System.out.println("java com.aftexsw.util.bzip.Main [-g] [-d] [-h] [-v] [-l] [-t] [-1..-9] filein [fileout]");
		System.out.println("java com.aftexsw.util.bzip.Main [--help] [--version] [-license]");
		System.out.println("java com.aftexsw.util.bzip.Main [--decompress] filein [fileout]");
		System.out.println("-g use GUI to run BZip");
		System.out.println("-d --decompress decompress the input file (otherwise compress)");
		System.out.println("-o --stdout direct output to std out");
		System.out.println("-i --stdin direct input to std in");
		System.out.println("-h --help display help");
		System.out.println("-v --version display version");
		System.out.println("-l --license display license");
		System.out.println("-t --test test the input file");
		System.out.println("-1 .. -9 multiple of 100k to use for the block size");
	}

	final private static void license() {
		System.out.println("BZip2 for Java version 0.5");
		System.out.println("C code Copyright (C) 1996-1998 Julian R Seward.  All rights reserved.");
		System.out.println("port by Keiron Liddle, Aftex Software <keiron@aftexsw.com>");
	}

	final private static void version() {
		System.out.println("BZip2 for Java version 0.5");
		System.out.println("port by Keiron Liddle, Aftex Software <keiron@aftexsw.com>");
	}

	final public static void gui() {
		BZipGUI bg = new BZipGUI();
		bg.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			Class clazz = Class.forName("com.aftexsw.util.bzip.Mac");
			Method meth = clazz.getMethod("setupForMac", new Class[] {});
			Boolean bool = (Boolean)meth.invoke(null, new Object[] {});
			if(bool.booleanValue())
				return;
		} catch(Throwable t) {}
		if(args.length == 0) {
			usage();
			return;
		}
		String fileIn, fileOut;
		boolean compFlag = true;
		boolean testFlag = false;
		boolean tostdout = false;
		boolean fromstdin = false;
		int blockSize100k = 9;
		for(int count = 0; count < args.length; count++) {
			if(args[count].startsWith("-")) {
				if(args[count].equals("--help")) {
					usage();
					return;
				} else if(args[count].equals("-h")) {
					usage();
					return;
				} else if(args[count].equals("-g")) {
					gui();
					return;
				} else if(args[count].equals("--license")) {
					license();
					return;
				} else if(args[count].equals("-l")) {
					license();
					return;
				} else if(args[count].equals("--version")) {
					version();
					return;
				} else if(args[count].equals("-v")) {
					version();
					return;
				} else if(args[count].equals("--stdout")) {
					tostdout = true;
				} else if(args[count].equals("-o")) {
					tostdout = true;
				} else if(args[count].equals("--stdin")) {
					fromstdin = true;
				} else if(args[count].equals("-i")) {
					fromstdin = true;
				} else if(args[count].equals("-1")) {
					blockSize100k = 1;
				} else if(args[count].equals("-2")) {
					blockSize100k = 2;
				} else if(args[count].equals("-3")) {
					blockSize100k = 3;
				} else if(args[count].equals("-4")) {
					blockSize100k = 4;
				} else if(args[count].equals("-5")) {
					blockSize100k = 5;
				} else if(args[count].equals("-6")) {
					blockSize100k = 6;
				} else if(args[count].equals("-7")) {
					blockSize100k = 7;
				} else if(args[count].equals("-8")) {
					blockSize100k = 8;
				} else if(args[count].equals("-9")) {
					blockSize100k = 9;
				} else if(args[count].equals("--decompress")) {
					compFlag = false;
				} else if(args[count].equals("-d")) {
					compFlag = false;
				} else if(args[count].equals("-t")) {
					testFlag = true;
				} else {}
			}
			else {
				break;
			}
		}
		if((args.length == 1) || (args[args.length - 2].startsWith("-"))) {
			if(args[args.length - 1].startsWith("-")) {
				usage();
				return;
			}
			fileIn = args[args.length - 1];
			if(compFlag) {
				fileOut = fileIn + ".bz2";
			} else {
				if(fileIn.endsWith(".bz2")) {
					fileOut = fileIn.substring(0, fileIn.length() - 4);
				} else {
					fileOut = fileIn + ".out";
				}
			}
		} else {
			fileIn = args[args.length - 2];
			fileOut = args[args.length - 1];
		}
		if(testFlag) {
			if(BZip.test(fileIn)) {
				System.out.println("the file apears to be OK");
			} else {
				System.out.println("the file appears to be corrupted!");
				//				System.out.println("try java com.aftexsw.util.bzip.Main -r (file) to recover good blocks");
			}
		}
		else {
			if(tostdout) {
				if(fromstdin) {
					if(compFlag) {
						BZip.compress(System.in, System.out, blockSize100k, BZip.dummyProg);
					} else {
						BZip.decompress(System.in, System.out, BZip.dummyProg);
					}
				} else {
					if(compFlag) {
						BZip.compress(new File(fileIn), System.out, blockSize100k, BZip.dummyProg);
					} else {
						BZip.decompress(new File(fileIn), System.out, BZip.dummyProg);
					}
				}
			} else {
				if(fromstdin) {
					if(compFlag) {
						BZip.compress(System.in, new File(fileOut), blockSize100k, BZip.dummyProg);
					} else {
						BZip.decompress(System.in, new File(fileOut), BZip.dummyProg);
					}
				} else {
					if(compFlag) {
						BZip.compress(new File(fileIn), new File(fileOut), blockSize100k, BZip.dummyProg);
					} else {
						BZip.decompress(new File(fileIn), new File(fileOut), BZip.dummyProg);
					}
				}
			}
		}
		System.out.println("finished BZip");
	}
}
