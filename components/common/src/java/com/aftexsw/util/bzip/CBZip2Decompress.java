/*
 * This file is a part of bzip2 and/or libbzip2, a program and
 * library for lossless, block-sorting data compression.
 * 
 * Copyright (C) 1996-1998 Julian R Seward.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. The origin of this software must not be misrepresented; you must 
 * not claim that you wrote the original software.  If you use this 
 * software in a product, an acknowledgment in the product 
 * documentation would be appreciated but is not required.
 * 
 * 3. Altered source versions must be plainly marked as such, and must
 * not be misrepresented as being the original software.
 * 
 * 4. The name of the author may not be used to endorse or promote 
 * products derived from this software without specific prior written 
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Java version ported by Keiron Liddle, Aftex Software <keiron@aftexsw.com> 1999
 * 
 */
/**
 * The main decompression engine.
 */

package com.aftexsw.util.bzip;

import java.io.*;

/**
 * Module:      CBZip2Decompress<br> <p> Description: CBZip2Decompress<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
 * @history      08.04.2008 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2008; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
class CBZip2Decompress {
	ProgressListener progress;

	/**
	 * @deprecated
	 */
	public CBZip2Decompress(ProgressListener prog) {
		progress = prog;
/*
		ll8 = null;
		tt = null;*/
	}

	public boolean uncompressStream(InputStream zStream, OutputStream stream) {
		BZip.decompress(zStream, stream, progress);
/*
		char magic1, magic2, magic3, magic4;
		char magic5, magic6;
		int storedBlockCRC, storedCombinedCRC;
		int computedBlockCRC, computedCombinedCRC;
		int currBlockNo;

		bsSetStream(zStream);

		/*--
		A bad magic number is `recoverable from';
		return with False so the caller skips the file.
		--*
		magic3 = bsGetUChar();
		magic4 = bsGetUChar();
		if (magic3 != 'h' || magic4 < '1' || magic4 > '9') {
			bsFinishedWithStream();
			return false;
		}

		setDecompressStructureSizes ( magic4 - '0' );
		computedCombinedCRC = 0;

		currBlockNo = 0;

		while(true) {
			magic1 = bsGetUChar();
			magic2 = bsGetUChar();
			magic3 = bsGetUChar();
			magic4 = bsGetUChar();
			magic5 = bsGetUChar();
			magic6 = bsGetUChar();
			if (magic1 == 0x17 && magic2 == 0x72 && magic3 == 0x45 && magic4 == 0x38 && magic5 == 0x50 && magic6 == 0x90)
				break;

			if (magic1 != 0x31 || magic2 != 0x41 || magic3 != 0x59 || magic4 != 0x26 || magic5 != 0x53 || magic6 != 0x59)
				badBlockHeader();

			storedBlockCRC = bsGetint();

			if (bsR(1) == 1)
				blockRandomised = true;
			else
				blockRandomised = false;

			currBlockNo++;
			getAndMoveToFrontDecode();

			mCrc.initialiseCRC();
			undoReversibleTransformation_fast(stream);

			computedBlockCRC = mCrc.getFinalCRC();

			/*-- A bad CRC is considered a fatal error. --*
			if (storedBlockCRC != computedBlockCRC)
				crcError();

			computedCombinedCRC = (computedCombinedCRC << 1) | (computedCombinedCRC >> 31);
			computedCombinedCRC ^= computedBlockCRC;
		}

		storedCombinedCRC = bsGetint();
		if (storedCombinedCRC != computedCombinedCRC)
			crcError();

		bsFinishedWithStream();*/
		return true;
	}
/*
	public boolean testStream(InputStream zStream) {
		try {
			char magic1, magic2, magic3, magic4;
			char magic5, magic6;
			int storedBlockCRC, storedCombinedCRC;
			int computedBlockCRC, computedCombinedCRC;
			int currBlockNo;

			bsSetStream(zStream);

			magic3 = bsGetUChar ();
			magic4 = bsGetUChar ();
			if (magic3 != 'h' || magic4 < '1' || magic4 > '9') {
				bsFinishedWithStream();
				return false;
			}

			setDecompressStructureSizes(magic4 - '0');
			computedCombinedCRC = 0;

			currBlockNo = 0;

			while (true) {
				magic1 = bsGetUChar();
				magic2 = bsGetUChar();
				magic3 = bsGetUChar();
				magic4 = bsGetUChar();
				magic5 = bsGetUChar();
				magic6 = bsGetUChar();
				if (magic1 == 0x17 && magic2 == 0x72 && magic3 == 0x45 && magic4 == 0x38 && magic5 == 0x50 && magic6 == 0x90)
					break;

				currBlockNo++;
				if (magic1 != 0x31 || magic2 != 0x41 || magic3 != 0x59 || magic4 != 0x26 || magic5 != 0x53 || magic6 != 0x59) {
					bsFinishedWithStream();
					return false;
				}
				storedBlockCRC = bsGetint();

				if (bsR(1) == 1)
					blockRandomised = true;
				else
					blockRandomised = false;

				getAndMoveToFrontDecode ();

				mCrc.initialiseCRC();
				undoReversibleTransformation_fast(new OutputStream() {
									  public void write(int ch) {}
								  }
								 );

				computedBlockCRC = mCrc.getFinalCRC();

				if (storedBlockCRC != computedBlockCRC) {
					bsFinishedWithStream();
					return false;
				}

				computedCombinedCRC = (computedCombinedCRC << 1) | (computedCombinedCRC >> 31);
				computedCombinedCRC ^= computedBlockCRC;
			};

			storedCombinedCRC  = bsGetint();
			if (storedCombinedCRC != computedCombinedCRC) {
				bsFinishedWithStream();
				return false;
			}

			bsFinishedWithStream();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
*/
/*
	private static void uncompressOutOfMemory() {
		//		throw new CMemoryError();
	}

	private static void blockOverrun() {
		System.out.println("block overrun");
		cadvise();
	}

	private static void badBlockHeader() {
		System.out.println("bad block header");
		cadvise();
	}

	private static void crcError() {
		System.out.println("crc error");
		cadvise();
	}

	private void bsFinishedWithStream() {
		bsStream = null;
	}

	private void bsSetStream(InputStream f) {
		bsStream = f;
		bsLive = 0;
		bsBuff = 0;
		bytesOut = 0;
		bytesIn = 0;
	}

	private int bsR(int n) {
		int v;
		{
			while (bsLive < n) {
				int zzi;
				int thech = 0;
				try {
					thech = bsStream.read();
				} catch(IOException e) {
					compressedStreamEOF();
				}
				if(thech == -1) {
					compressedStreamEOF();
				}
				zzi = thech;
				bsBuff = (bsBuff << 8) | (zzi & 0xff);
				bsLive += 8;
			}
		}

		v = (bsBuff >> (bsLive-n)) & ((1 << n) - 1);
		bsLive -= n;
		return v;
	}

	private char bsGetUChar() {
		return (char)bsR(8);
	}

	private int bsGetint() {
		int u = 0;
		u = (u << 8) | bsR(8);
		u = (u << 8) | bsR(8);
		u = (u << 8) | bsR(8);
		u = (u << 8) | bsR(8);
		return u;
	}

	private int bsGetIntVS(int numBits) {
		return (int)bsR(numBits);
	}

	private void hbCreateDecodeTables(int[] limit, int[] base, int[] perm, char[] length, int minLen, int maxLen, int alphaSize) {
		int pp, i, j, vec;

		pp = 0;
		for(i = minLen; i <= maxLen; i++)
			for(j = 0; j < alphaSize; j++)
				if (length[j] == i) {
					perm[pp] = j;
					pp++;
				};

		for(i = 0; i < MAX_CODE_LEN; i++)
			base[i] = 0;
		for(i = 0; i < alphaSize; i++)
			base[length[i]+1]++;

		for(i = 1; i < MAX_CODE_LEN; i++)
			base[i] += base[i-1];

		for (i = 0; i < MAX_CODE_LEN; i++)
			limit[i] = 0;
		vec = 0;

		for (i = minLen; i <= maxLen; i++) {
			vec += (base[i+1] - base[i]);
			limit[i] = vec-1;
			vec <<= 1;
		}
		for (i = minLen + 1; i <= maxLen; i++)
			base[i] = ((limit[i-1] + 1) << 1) - base[i];
	}

	private void recvDecodingTables() {
		char len[][] = new char[N_GROUPS][MAX_ALPHA_SIZE];
		int i, j, t, nGroups, nSelectors, alphaSize;
		int minLen, maxLen;
		boolean inUse16[] = new boolean[16];

		/*--- Receive the mapping table ---*
		for (i = 0; i < 16; i++)
			if (bsR(1) == 1)
				inUse16[i] = true;
			else
				inUse16[i] = false;

		for (i = 0; i < 256; i++)
			inUse[i] = false;

		for (i = 0; i < 16; i++)
			if (inUse16[i])
				for (j = 0; j < 16; j++)
					if (bsR(1) == 1)
						inUse[i * 16 + j] = true;

		makeMaps();
		alphaSize = nInUse+2;

		/*--- Now the selectors ---*
		nGroups = bsR ( 3 );
		nSelectors = bsR ( 15 );
		for (i = 0; i < nSelectors; i++) {
			j = 0;
			while (bsR(1) == 1)
				j++;
			selectorMtf[i] = (char)j;
		}

		/*--- Undo the MTF values for the selectors. ---*
		{
			char pos[] = new char[N_GROUPS];
			char tmp, v;
			for (v = 0; v < nGroups; v++)
				pos[v] = v;

			for (i = 0; i < nSelectors; i++) {
				v = selectorMtf[i];
				tmp = pos[v];
				while (v > 0) {
					pos[v] = pos[v-1];
					v--;
				}
				pos[0] = tmp;
				selector[i] = tmp;
			}
		}

		/*--- Now the coding tables ---*
		for (t = 0; t < nGroups; t++) {
			int curr = bsR ( 5 );
			for (i = 0; i < alphaSize; i++) {
				while (bsR(1) == 1) {
					if (bsR(1) == 0)
						curr++;
					else
						curr--;
				}
				len[t][i] = (char)curr;
			}
		}

		/*--- Create the Huffman decoding tables ---*
		for (t = 0; t < nGroups; t++) {
			minLen = 32;
			maxLen = 0;
			for (i = 0; i < alphaSize; i++) {
				if (len[t][i] > maxLen)
					maxLen = len[t][i];
				if (len[t][i] < minLen)
					minLen = len[t][i];
			}
			hbCreateDecodeTables (
				limit[t], base[t], perm[t], len[t],
				minLen, maxLen, alphaSize
			);
			minLens[t] = minLen;
		}
	}

	private void getAndMoveToFrontDecode () {
		char yy[] = new char[256];
		int  i, j, nextSym, limitLast;
		int  EOB, groupNo, groupPos;

		limitLast = baseBlockSize * blockSize100k;
		origPtr   = bsGetIntVS(24);

		recvDecodingTables();
		EOB = nInUse+1;
		groupNo  = -1;
		groupPos = 0;

		/*--
		Setting up the unzftab entries here is not strictly
		necessary, but it does save having to do it later
		in a separate pass, and so saves a block's worth of
		cache misses.
		--*
		for (i = 0; i <= 255; i++)
			unzftab[i] = 0;

		for (i = 0; i <= 255; i++)
			yy[i] = (char) i;

		last = -1;

		{
			int zt, zn, zvec, zj;
			if (groupPos == 0) {
				groupNo++;
				groupPos = G_SIZE;
			}
			groupPos--;
			zt = selector[groupNo];
			zn = minLens[zt];
			zvec = bsR ( zn );
			while (zvec > limit[zt][zn]) {
				zn++;
				{
					{
						while (bsLive < 1) {
							int zzi;
							char thech = 0;
							try {
								thech = (char)bsStream.read();
							} catch(IOException e) {
								compressedStreamEOF();
							}
							if(thech == -1) {
								compressedStreamEOF();
							}
							zzi = thech;
							bsBuff = (bsBuff << 8) | (zzi & 0xff);
							bsLive += 8;
						}
					}
					zj = (bsBuff >> (bsLive-1)) & 1;
					bsLive--;
				}
				zvec = (zvec << 1) | zj;
			}
			nextSym = perm[zt][zvec - base[zt][zn]];
		}

		while(true) {

			if (nextSym == EOB)
				break;

			if (nextSym == RUNA || nextSym == RUNB) {
				char ch;
				int s = -1;
				int N = 1;
				do {
					if (nextSym == RUNA)
						s = s + (0+1) * N;
					else if (nextSym == RUNB)
						s = s + (1+1) * N;
					N = N * 2;
					{
						int zt, zn, zvec, zj;
						if (groupPos == 0) {
							groupNo++;
							groupPos = G_SIZE;
						}
						groupPos--;
						zt = selector[groupNo];
						zn = minLens[zt];
						zvec = bsR ( zn );
						while (zvec > limit[zt][zn]) {
							zn++;
							{
								{
									while (bsLive < 1) {
										int zzi;
										char thech = 0;
										try {
											thech = (char)bsStream.read();
										} catch(IOException e) {
											compressedStreamEOF();
										}
										if(thech == -1) {
											compressedStreamEOF();
										}
										zzi = thech;
										bsBuff = (bsBuff << 8) | (zzi & 0xff);
										bsLive += 8;
									}
								}
								zj = (bsBuff >> (bsLive-1)) & 1;
								bsLive--;
							}
							zvec = (zvec << 1) | zj;
						};
						nextSym = perm[zt][zvec - base[zt][zn]];
					}
				} while (nextSym == RUNA || nextSym == RUNB);

				s++;
				ch = seqToUnseq[yy[0]];
				unzftab[ch] += s;

				while (s > 0) {
					last++;
					ll8[last] = ch;
					s--;
				};

				if (last >= limitLast)
					blockOverrun();
				continue;
			} else {
				char tmp;
				last++;
				if (last >= limitLast)
					blockOverrun();

				tmp = yy[nextSym-1];
				unzftab[seqToUnseq[tmp]]++;
				ll8[last] = seqToUnseq[tmp];

				/*--
				This loop is hammered during decompression,
				hence the unrolling.

				for (j = nextSym-1; j > 0; j--) yy[j] = yy[j-1];
				--*

				j = nextSym-1;
				for (; j > 3; j -= 4) {
					yy[j]   = yy[j-1];
					yy[j-1] = yy[j-2];
					yy[j-2] = yy[j-3];
					yy[j-3] = yy[j-4];
				}
				for (; j > 0; j--)
					yy[j] = yy[j-1];

				yy[0] = tmp;
				{
					int zt, zn, zvec, zj;
					if (groupPos == 0) {
						groupNo++;
						groupPos = G_SIZE;
					}
					groupPos--;
					zt = selector[groupNo];
					zn = minLens[zt];
					zvec = bsR ( zn );
					while (zvec > limit[zt][zn]) {
						zn++;
						{
							{
								while (bsLive < 1) {
									int zzi;
									char thech = 0;
									try {
										thech = (char)bsStream.read();
									} catch(IOException e) {
										compressedStreamEOF();
									}
									zzi = thech;
									bsBuff = (bsBuff << 8) | (zzi & 0xff);
									bsLive += 8;
								}
							}
							zj = (bsBuff >> (bsLive-1)) & 1;
							bsLive--;
						}
						zvec = (zvec << 1) | zj;
					};
					nextSym = perm[zt][zvec - base[zt][zn]];
				}
				continue;
			}
		}
	}

	private void undoReversibleTransformation_fast(OutputStream dst) {
		int cftab[] = new int[257];
		int i, tPos;
		char ch;

		/*--
		We assume here that the global array unzftab will
		already be holding the frequency counts for
		ll8[0 .. last].
		--*/

		/*-- Set up cftab to facilitate generation of T^(-1) --*
		cftab[0] = 0;
		for (i = 1; i <= 256; i++)
			cftab[i] = unzftab[i-1];
		for (i = 1; i <= 256; i++)
			cftab[i] += cftab[i-1];
		/*-- compute the T^(-1) vector --*
		for (i = 0; i <= last; i++) {
			ch = (char)ll8[i];
			tt[cftab[ch]] = i;
			cftab[ch]++;
		}
		cftab = null;

		/*--
		We recreate the original by subscripting L through T^(-1).
		The run-length-decoder below requires characters incrementally,
		so tPos is set to a starting value, and is updated by
		the GET_FAST macro.
		--*
		tPos = tt[origPtr];

		/*-------------------------------------------------*/
		/*--
		This is pretty much a verbatim copy of the
		run-length decoder present in the distribution
		bzip-0.21; it has to be here to avoid creating
		block[] as an intermediary structure.  As in 0.21,
		this code derives from some sent to me by
		Christian von Roques.
		--*
		int temp;
		{
			int i2, count, chPrev, ch2;

			count = 0;
			i2 = 0;
			ch2 = 256;   /*-- not a char and not EOF --*

			if (blockRandomised) {
				int rNToGo = 0;
				int rTPos  = 0;
				while ( i2 <= last ) {
					chPrev = ch2;
					ch2 = ll8[tPos];
					tPos = tt[tPos];
					if (rNToGo == 0) {
						rNToGo = rNums[rTPos];
						rTPos++;
						if(rTPos == 512)
							rTPos = 0;
					}
					rNToGo--;
					ch2 ^= (int)((rNToGo == 1) ? 1 : 0);

					// handle 16 bit signed numbers
					ch2 &= 0xFF;

					i2++;

					try {
						dst.write(ch2);
					} catch(IOException e) {}
					mCrc.updateCRC(ch2);

					if (ch2 != chPrev) {
						count = 1;
					} else {
						count++;
						if (count >= 4) {
							int j2;
							char z;
							z = ll8[tPos];
							tPos = tt[tPos];
							if (rNToGo == 0) {
								rNToGo = rNums[rTPos];
								rTPos++;
								if(rTPos == 512)
									rTPos = 0;
							}
							rNToGo--;
							z ^= ((rNToGo == 1) ? 1 : 0);
							// handle 16 bit signed numbers
							z &= 0xFF;
							for (j2 = 0;  j2 < (int)z;  j2++) {
								try {
									dst.write(ch2);
								} catch(IOException e) {}
								mCrc.updateCRC(ch2);
							}
							i2++;
							count = 0;
						}
					}
				}
			} else {
				while ( i2 <= last ) {
					chPrev = ch2;
					ch2 = ll8[tPos];
					tPos = tt[tPos];
					i2++;

					char theChar = (char)ch2;
					try {
						dst.write(theChar);
					} catch(IOException e) {}
					mCrc.updateCRC(ch2);

					if (ch2 != chPrev) {
						count = 1;
					} else {
						count++;
						if (count >= 4) {
							int j2;
							char z;
							z = ll8[tPos];
							tPos = tt[tPos];
							for (j2 = 0;  j2 < (int)z;  j2++) {
								theChar = (char)ch2;
								try {
									dst.write(theChar);
								} catch(IOException e) {}
								mCrc.updateCRC(ch2);
							}
							i2++;
							count = 0;
						}
					}
				}
			}   /*-- if (blockRandomised) --*
		}
	}

	private InputStream bsStream;

	private void setDecompressStructureSizes ( int newSize100k ) {
		if (! (0 <= newSize100k && newSize100k <= 9 && 0 <= blockSize100k && blockSize100k <= 9))
			panic();

		blockSize100k = newSize100k;

		if(newSize100k == 0)
			return;

		int n = baseBlockSize * newSize100k;
		ll8 = new char[n];
		tt = new int[n];

		//		if (ll8 == NULL || tt == NULL) {
		//		}
	}

	private int[] tt;
	private char[] ll8;

	/*--
	  freq table collected to save a pass over the data
	  during decompression.
	--*
	private int unzftab[] = new int[256];

	private int limit[][] = new int[N_GROUPS][MAX_ALPHA_SIZE];
	private int base[][] = new int[N_GROUPS][MAX_ALPHA_SIZE];
	private int perm[][] = new int[N_GROUPS][MAX_ALPHA_SIZE];
	private int minLens[] = new int[N_GROUPS];
*/
}
