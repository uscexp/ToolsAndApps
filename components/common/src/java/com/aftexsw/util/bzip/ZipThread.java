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
package com.aftexsw.util.bzip;

import java.io.*;

/**
 * Module:      ZipThread<br> <p> Description: ZipThread<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
 * @history      08.04.2008 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2008; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
class ZipThread extends Thread {
	File in;
	File out;
	int block;
	BZipProgress guiProg;

	ZipThread(File i, File o, int b) {
		guiProg = new BZipProgress(i.length(), "zipping file " + i);
		in = i;
		out = o;
		block = b;
	}

	public void run() {
		BZip.compress(in, out, block, guiProg/*BZip.dummyProg*/);
		guiProg.dispose();
	}
}
