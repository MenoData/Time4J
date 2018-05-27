/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WinZoneID.java) is part of project Time4J.
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

package net.time4j.tz.spi;

import net.time4j.tz.TZID;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
 * <p>The resolved id of a windows zone. </p>
 *
 * @author  Meno Hochschild
 * @since   3.1
 */
final class WinZoneID
    implements TZID, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -4077231634935102213L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  canonical windows zone id
     */
    private final String id;

    //~ Konstruktoren -----------------------------------------------------

    WinZoneID(String id) {
        super();

        check(id);
        this.id = id;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String canonical() {

        return this.id;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof WinZoneID) {
            WinZoneID that = (WinZoneID) obj;
            return this.id.equals(that.id);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.id.hashCode();

    }

    @Override
    public String toString() {

        return this.id;

    }

    private static void check(String id) {

        if (!id.startsWith("WINDOWS~")) {
            throw new IllegalArgumentException("Not a windows zone: " + id);
        }

    }

    /**
     * @serialData  Checks the consistency.
     * @throws      InvalidObjectException in case of inconsistencies
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
        check(this.id);

    }

}
