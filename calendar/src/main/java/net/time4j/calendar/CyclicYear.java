/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CyclicYear.java) is part of project Time4J.
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

package net.time4j.calendar;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
 * <p>Represents the cyclic year used in East Asian calendars. </p>
 *
 * @author  Meno Hochschild
 * @since   3.39/4.34
 * @doctags.concurrency {immutable}
 */
/*[deutsch]
 * <p>Repr&auml;sentiert das zyklische Jahr, das in ostasiatischen Kalendern verwendet wird. </p>
 *
 * @author  Meno Hochschild
 * @since   3.39/4.34
 * @doctags.concurrency {immutable}
 */
public final class CyclicYear
    implements Comparable<CyclicYear>, Serializable {

    //~ Statische Felder/Initialisierungen ------------------------------------

    //~ Instanzvariablen ------------------------------------------------------

    /**
     * @serial  the number of cyclic year
     */
    private final int year;

    //~ Konstruktoren ---------------------------------------------------------

    private CyclicYear(int year) {
        super();

        if ((year < 1) || (year > 60)) {
            throw new IllegalArgumentException("Out of range: " + year);
        }

        this.year = year;
    }

    //~ Methoden --------------------------------------------------------------

    /**
     * <p>Obtains an instance of cyclic year. </p>
     *
     * @param   yearOfCycle     year number in the range 1-60
     * @return  CyclicYear
     * @throws  IllegalArgumentException if the parameter is out of range
     */
    /*[deutsch]
     * <p>Liefert eine Instanz eines zyklischen Jahres. </p>
     *
     * @param   yearOfCycle     year number in the range 1-60
     * @return  CyclicYear
     * @throws  IllegalArgumentException if the parameter is out of range
     */
    public static CyclicYear of(int yearOfCycle) {

        return new CyclicYear(yearOfCycle);

    }

    /**
     * <p>Obtains the associated year number in the range 1-60. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die Jahreszahl im Bereich 1-60. </p>
     *
     * @return  int
     */
    public int getNumber() {

        return this.year;

    }

    @Override
    public int compareTo(CyclicYear other) {

        return (this.year - other.year);

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof CyclicYear) {
            return (this.year == ((CyclicYear) obj).year);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return Integer.hashCode(this.year);

    }

    @Override
    public String toString() {

        return String.valueOf(this.year);

    }

    /**
     * @serialData  Checks the consistency of deserialized data
     * @param       in      object input stream
     * @throws      InvalidObjectException if the year is not in range 1-60
     */
    private void readObject(ObjectInputStream in)
        throws InvalidObjectException {

        if ((year < 1) || (year > 60)) {
            throw new InvalidObjectException("Out of range: " + this.year);
        }

    }

}
