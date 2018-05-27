/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntervalFactory.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;
import net.time4j.format.expert.ParseLog;

import java.util.Set;


/**
 * <p>Allgemeine Intervallfabrik f&uuml;r ISO-8601-Typen. </p>
 *
 * @author  Meno Hochschild
 * @see     2.0
 */
interface IntervalFactory<T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    extends IntervalCreator<T, I> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Addiert die angegebene Dauer zu einem Zeitpunkt. </p>
     *
     * @param   timepoint   point in time given duration will be added to
     * @param   period      duration as P-string
     * @param   plog        contains raw parsed data and parse position
     * @param   attributes  format control attributes
     * @return  result of addition or {@code null} in case of error
     */
    T plusPeriod(
        T timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    );

    /**
     * <p>Subtrahiert die angegebene Dauer von einem Zeitpunkt. </p>
     *
     * @param   timepoint   point in time given duration will be subtracted from
     * @param   period      duration as P-string
     * @param   plog        contains raw parsed data and parse position
     * @param   attributes  format control attributes
     * @return  result of subtraction or {@code null} in case of error
     */
    T minusPeriod(
        T timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    );

    /**
     * <p>Bestimmt die Elemente, deren mit dem Start assoziierte Werte als
     * Vorgabe f&uuml;r das Ende eines Intervalls &uuml;bernommen werden
     * sollen. </p>
     *
     * @param   rawData     parsed raw data of start component
     * @return  chronological elements as source for default temporal values
     *          of the end boundary
     */
    Set<ChronoElement<?>> stdElements(ChronoEntity<?> rawData);

    /**
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  associated {@code TimeLine}
     */
    TimeLine<T> getTimeLine();

    /**
     * <p>Werden unbegrenzte Intervallgrenzen unterst&uuml;tzt? </p>
     *
     * @return  boolean
     * @since   4.18
     */
    default boolean supportsInfinity() {

        return true;

    }

}
