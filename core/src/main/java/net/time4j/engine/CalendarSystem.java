/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarSystem.java) is part of project Time4J.
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

import java.util.List;


/**
 * <p>Represents a calendar system which can map a calendar date to a
 * day number corresponding to the count of days elapsed since UTC epoch
 * [1972-01-01]. </p>
 *
 * @param   <D> generic type of calendar date (subtype of {@code Calendrical})
 * @author  Meno Hochschild
 * @see     Calendrical
 * @see     net.time4j.engine.EpochDays
 * @doctags.spec    All implementations must be immutable.
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein Kalendersystem, das Datumsangaben eindeutig auf
 * eine Tagesnummer entsprechend der Anzahl der Tage seit der UTC-Epoche
 * [1972-01-01] abbilden kann. </p>
 *
 * @param   <D> generic type of calendar date (subtype of {@code Calendrical})
 * @author  Meno Hochschild
 * @see     Calendrical
 * @see     net.time4j.engine.EpochDays
 * @doctags.spec    All implementations must be immutable.
 */
public interface CalendarSystem<D> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Transforms given day number to a calendar date on the local
     * time line at the reference time of noon. </p>
     *
     * @param   utcDays     count of days since UTC epoch [1972-01-01]
     * @return  new calendar date
     */
    /*[deutsch]
     * <p>Transformiert die angegebene Tagesnummer zu einem Datum auf dem
     * lokalen Zeitstrahl mit der Referenzzeit zu 12 Uhr mittags. </p>
     *
     * @param   utcDays     count of days since UTC epoch [1972-01-01]
     * @return  new calendar date
     */
    D transform(long utcDays);

    /**
     * <p>Transforms given calendar date to a day number on the local
     * time line at the reference time of noon. </p>
     *
     * @param   date        calendar date to be transformed
     * @return  count of days since UTC epoch [1972-01-01]
     */
    /*[deutsch]
     * <p>Transformiert das angegebene Datum zu einer Tagesnummer auf dem
     * lokalen Zeitstrahl mit der Referenzzeit zu 12 Uhr mittags. </p>
     *
     * @param   date        calendar date to be transformed
     * @return  count of days since UTC epoch [1972-01-01]
     */
    long transform(D date);

    /**
     * <p>Gets the minimum day number as count of days since the
     * introduction of UTC [1972-01-01]. </p>
     *
     * @return  smallest count of days relative to UTC epoch [1972-01-01]
     * @see     #getMaximumSinceUTC()
     */
    /*[deutsch]
     * <p>Liefert die minimal m&ouml;gliche Tagesnummer als Anzahl der Tage
     * seit der Einf&uuml;hrung von UTC [1972-01-01]. </p>
     *
     * @return  smallest count of days relative to UTC epoch [1972-01-01]
     * @see     #getMaximumSinceUTC()
     */
    long getMinimumSinceUTC();

    /**
     * <p>Gets the maximum day number as count of days since the
     * introduction of UTC [1972-01-01]. </p>
     *
     * @return  largest count of days relative to UTC epoch [1972-01-01]
     * @see     #getMinimumSinceUTC()
     */
    /*[deutsch]
     * <p>Liefert die maximal m&ouml;gliche Tagesnummer als Anzahl der Tage
     * seit der Einf&uuml;hrung von UTC [1972-01-01]. </p>
     *
     * @return  largest count of days relative to UTC epoch [1972-01-01]
     * @see     #getMinimumSinceUTC()
     */
    long getMaximumSinceUTC();

    /**
     * <p>Gets the start time of a calendar day. </p>
     *
     * <p>If a calendar date is combined with a clock time then this method
     * controls the conversion of day numbers which else have the reference
     * time of noon. </p>
     *
     * @return  help object to calculate the start of day (eventually dependent
     *          on the season)
     */
    /*[deutsch]
     * <p>Ermittelt den Beginn des Tages. </p>
     *
     * <p>Falls zu einer Datumsangabe eine Uhrzeit hinzukommt, hat diese
     * Methode Einflu&szlig; auf die Konversion zwischen Tagesnummern, die
     * sonst die Referenzzeit von 12 Uhr mittags haben. </p>
     *
     * @return  help object to calculate the start of day (eventually dependent
     *          on the season)
     */
    StartOfDay getStartOfDay();

    /**
     * <p>Yields an enumeration of valid eras in ascending order. </p>
     *
     * <p>All ISO-systems only yield an empty list. A gregorian calendar
     * defines the eras {@code BC} and {@code AD} related to Jesu birth
     * however. </p>
     *
     * @return  unmodifiable list of eras (maybe empty)
     */
    /*[deutsch]
     * <p>Zeitlich aufsteigend sortierte Auflistung der f&uuml;r ein
     * Kalendersystem g&uuml;ltigen &Auml;ren. </p>
     *
     * <p>Alle ISO-Systeme liefern nur eine leere Liste. Ein gregorianischer
     * Kalender hingegen definiert die &Auml;ren {@code BC} und {@code AD}
     * bezogen auf Jesu Geburt. </p>
     *
     * @return  unmodifiable list of eras (maybe empty)
     */
    List<CalendarEra> getEras();

}
