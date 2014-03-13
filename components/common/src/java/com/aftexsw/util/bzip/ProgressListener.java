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
 * Interface to handle the progress of the compress/decompress operation.
 * To display or otherwise use the information.
 */
package com.aftexsw.util.bzip;

interface ProgressListener {
	public void setProgress(long val);
}
