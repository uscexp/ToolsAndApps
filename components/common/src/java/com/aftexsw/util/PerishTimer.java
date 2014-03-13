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
package com.aftexsw.util;

/**
 * Module:      PerishTimer<br> <p> Description: PerishTimer<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
 * @history      08.04.2008 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2008; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public class PerishTimer extends Thread {
	public PerishTimer(Perishable inPerishable, long inLife) {
		perishable = inPerishable;
		lifetime = inLife;
	}

	public void run() {
		synchronized(this) {
			try {
				wait(lifetime);
			} catch(Exception e) {}
		}
		perishable.perish();
	}

	long lifetime;
	private Perishable perishable;
}
