/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TarInputStream.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.tool;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * <p>Spezieller Eingabestrom zum Einlesen von TAR-Dateien. </p>
 *
 * @author  Meno Hochschild
 */
class TarInputStream
    extends FilterInputStream {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int DATA_BLOCK = 512;
    private static final int HEADER_BLOCK = 512;

    //~ Instanzvariablen --------------------------------------------------

    private TarEntry currentEntry;
    private long currentFileSize;
    private long bytesRead;

    //~ Konstruktoren -----------------------------------------------------

    public TarInputStream(InputStream in) {
        super(in);

        this.currentEntry = null;
        this.currentFileSize = 0;
        this.bytesRead = 0;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean markSupported() {

        return false;

    }

    @Override
    public void mark(int readLimit) {
        // no-op
    }

    @Override
    public void reset() throws IOException {

        throw new IOException("Mark/Reset not supported.");

    }

    @Override
    public int read() throws IOException {

        byte[] buf = new byte[1];
        int res = this.read(buf, 0, 1);

        if (res != -1) {
            return buf[0];
        }

        return -1;

    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {

        if (this.currentEntry != null) {
            long diff = this.currentEntry.getSize() - this.currentFileSize;

            if (diff == 0) {
                return -1;
            } else if (len > diff) {
                len = (int) diff;
            }
        }

        int br = super.read(b, off, len);

        if (br != -1) {
            if (this.currentEntry != null) {
                this.currentFileSize += br;
            }
            this.bytesRead += br;
        }

        return br;

    }

    TarEntry getNextEntry() throws IOException {

        this.closeCurrentEntry();

        byte[] header = new byte[HEADER_BLOCK];
        byte[] temp = new byte[HEADER_BLOCK];
        int tr = 0;

        while (tr < HEADER_BLOCK) {
            int res = this.read(temp, 0, HEADER_BLOCK - tr);
            System.arraycopy(temp, 0, header, tr, res);
            tr += res;
        }

        boolean eof = true;

        for (byte b : header) {
            if (b != 0) {
                eof = false;
                break;
            }
        }

        if (!eof) {
            this.currentEntry = new TarEntry(header);
        }

        return this.currentEntry;

    }

    private void closeCurrentEntry() throws IOException {

        if (this.currentEntry != null) {
            long diff = this.currentEntry.getSize() - this.currentFileSize;

            if (diff > 0) {
                long bs = 0;
                while (bs < diff) {
                    long res = this.skip(diff - bs);
                    bs += res;
                }
                this.bytesRead += diff;
            }

            this.currentEntry = null;
            this.currentFileSize = 0;

            // bis zum Ende eines Datenblocks Bytes überspringen
            if (this.bytesRead > 0) {
                int extra = (int) (this.bytesRead % DATA_BLOCK);

                if (extra > 0) {
                    long bs = 0;
                    while (bs < DATA_BLOCK - extra) {
                        long res = this.skip(DATA_BLOCK - extra - bs);
                        bs += res;
                    }
                    this.bytesRead += (DATA_BLOCK - extra);
                }
            }

        }

    }

}
