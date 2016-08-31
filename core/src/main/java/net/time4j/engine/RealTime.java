/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RealTime.java) is part of project Time4J.
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

package net.time4j.engine;


/**
 * <p>Represents a machine time span in (possibly fractional) seconds only. </p>
 *
 * @param   <U> generic type of time unit applicable on time points defined in UTC or POSIX
 * @author  Meno Hochschild
 * @since   3.23/4.19
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Maschinenzeit, die in Sekunden und eventuell Sekundenbruchteilen definiert ist. </p>
 *
 * @param   <U> generic type of time unit applicable on time points defined in UTC or POSIX
 * @author  Meno Hochschild
 * @since   3.23/4.19
 */
public interface RealTime<U>
    extends TimeSpan<U> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the normalized seconds of this duration. </p>
     *
     * <p>The normalization happens in case of a negative duration such that any fraction part
     * falls into the range {@code 0-999999999}. In this case, following expression is NOT true:
     * {@code Math.abs(getSeconds()) == getPartialAmount(TimeUnit.SECONDS)} </p>
     *
     * @return  long
     * @see     #getFraction()
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die normalisierten Sekunden dieser Dauer. </p>
     *
     * <p>Die Normalisierung geschieht im Fall einer negativen Dauer so, da&szlig; ein Sekundenbruchteil
     * immer in den Bereich {@code 0-999999999} f&auml;llt. In diesem Fall ist folgender Ausdruck NICHT
     * wahr: {@code Math.abs(getSeconds()) == getPartialAmount(TimeUnit.SECONDS)} </p>
     *
     * @return  long
     * @see     #getFraction()
     * @since   2.0
     */
    long getSeconds();

    /**
     * <p>Yields the normalized nanosecond fraction of this duration. </p>
     *
     * @return  nanosecond in range {@code 0-999999999}
     * @see     #getSeconds()
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert den normalisierten Nanosekundenteil dieser Dauer. </p>
     *
     * @return  nanosecond in range {@code 0-999999999}
     * @see     #getSeconds()
     * @since   2.0
     */
    int getFraction();

}
