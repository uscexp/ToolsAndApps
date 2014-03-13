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

import com.aftexsw.ui.*;

import javax.swing.*;
import java.awt.*;

class BZipProgress extends JWindow implements ProgressListener {
	JProgressBar pb = new JProgressBar();
	long lastVal = -1;
	long totalBytes = 200;

	BZipProgress(long total, String name) {
		totalBytes = total;
		setSize(400, 40);
		Util.centerFrame(this);
		getContentPane().setLayout(new FlowLayout());
		JLabel label = new JLabel("Progress of " + name + " :", JLabel.CENTER);
		getContentPane().add(label);
		getContentPane().add(pb);
		setVisible(true);
	}

	public void setProgress(long val) {
		if(val != lastVal) {
			pb.setValue((int)(100 * val / totalBytes));
			lastVal = val;
		}
	}
}

