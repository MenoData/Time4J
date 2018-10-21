/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SimpleUT.java) is part of project Time4J.
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

package net.time4j.tz;

import net.time4j.base.UnixTime;

/**
 * Simple implementation of UnixTime.
 *
 * @author  Meno Hochschild
 * @since   4.0
 */
class SimpleUT
    implements UnixTime {

    //~ Instanzvariablen --------------------------------------------------

    private final long posix;
    private final int nano;

    //~ Konstruktoren -----------------------------------------------------

    private SimpleUT(long posix, int nano) {
        super();

        this.posix = posix;
        this.nano = nano;
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public long getPosixTime() {
        return this.posix;
    }

    @Override
    public int getNanosecond() {
        return this.nano;
    }

    static UnixTime previousTime(UnixTime ut) {
        return previousTime(ut.getPosixTime(), ut.getNanosecond());
    }

    static UnixTime previousTime(
        long posix,
        int nano
    ) {
        return new SimpleUT(
            (nano == 0) ? (posix - 1) : posix,
            (nano == 0) ? 999999999 : (nano - 1)
        );
    }

}
