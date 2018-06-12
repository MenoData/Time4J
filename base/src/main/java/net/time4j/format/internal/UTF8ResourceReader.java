/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UTF8ResourceReader.java) is part of project Time4J.
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

package net.time4j.format.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;


/**
 * Skips eventually existing byte-order-marks of an input stream encoded in UTF-8.
 *
 * @author  Meno Hochschild
 * @since   3.23/4.19
 */
class UTF8ResourceReader
    extends Reader {

    //~ Instanzvariablen --------------------------------------------------

    private final PushbackInputStream pis;
    private BufferedReader internal = null;

    //~ Konstruktoren -----------------------------------------------------

    UTF8ResourceReader(InputStream is) {
        super();

        this.pis = new PushbackInputStream(is, 3);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * See {@code BufferedReader}.
     *
     * @return  A String containing the contents of the line, not including any line-termination characters,
     *          or null if the end of the stream has been reached
     * @throws  IOException in case of any I/O-error
     */
    public String readLine() throws IOException {

        this.init();
        return this.internal.readLine();

    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {

        this.init();
        return this.internal.read(cbuf, off, len);

    }

    @Override
    public boolean ready() throws IOException {

        this.init();
        return this.internal.ready();

    }

    @Override
    public void close() throws IOException {

        final Reader reader = this.internal;

        if (reader == null) {
            this.pis.close();
        } else {
            reader.close();
        }

    }

    private void init() throws IOException {

        if (this.internal != null) {
            return;
        }

        byte[] bom = new byte[3];
        int n = this.pis.read(bom, 0, 3);

        boolean hasByteOrderMarks = (
            (n == 3)
            && (bom[0] == (byte) 0xEF)
            && (bom[1] == (byte) 0xBB)
            && (bom[2] == (byte) 0xBF)
        );

        if (!hasByteOrderMarks && (n > 0)) {
            this.pis.unread(bom, 0, n);
        }

        this.internal = new BufferedReader(new InputStreamReader(this.pis, "UTF-8"));

    }

}
