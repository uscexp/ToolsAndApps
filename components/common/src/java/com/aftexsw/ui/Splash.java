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
package com.aftexsw.ui;

import com.aftexsw.util.*;

import javax.swing.*;
import java.awt.*;

public class Splash extends JWindow implements Perishable {
	public Splash() {
		this("Initializing...", "images/aftexsw.jpeg");
	}

	public Splash(String desc, String im) {
		getContentPane().setLayout(new BorderLayout());
		Image logo = null;
		try {
			logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource(im));
		} catch (Exception e) {}


		if (logo != null) {
			JLabel canvas = new JLabel(new ImageIcon(logo));
			getContentPane().add(canvas, BorderLayout.NORTH);
		}
		JLabel lab1 = new JLabel(desc, JLabel.CENTER);
		JLabel lab2 = new JLabel("Keiron Liddle, Aftex Software © 1999-2001", JLabel.CENTER);
		getContentPane().add(lab1, BorderLayout.CENTER);
		getContentPane().add(lab2, BorderLayout.SOUTH);
		Util.centerFrame(this);
	}

	public void perish() {
		dispose();
	}
}
