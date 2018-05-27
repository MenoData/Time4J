/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NamedID.java) is part of project Time4J.
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

import java.io.Serializable;


/**
 * <p>Thin wrapper around a zone identifier. </p>
 *
 * @author  Meno Hochschild
 * @since   3.29/4.25
 */
class NamedID
    implements TZID, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -4889632013137688471L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial timezone id
     */
    private final String tzid;

    //~ Konstruktoren -----------------------------------------------------

    NamedID(String tzid) {
        super();

        this.tzid = tzid;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String canonical() {

        return this.tzid;

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof NamedID) {
            NamedID that = (NamedID) obj;
            return this.tzid.equals(that.tzid);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.tzid.hashCode();

    }

    @Override
    public String toString() {

        return this.getClass().getName() + "@" + this.tzid;

    }

}
