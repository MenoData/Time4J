/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalElement.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoFunction;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Extends a chronological element by some <i>zonal</i> queries. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Erweitert ein chronologisches Element um diverse zonale Abfragen. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 * @since   2.0
 */
public interface ZonalElement<V>
    extends ChronoElement<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a function which can query a {@link Moment} in the
     * system timezone. </p>
     *
     * <p>Note: Usually the function converts the given {@code Moment} to
     * a {@code PlainTimestamp} and then queries this local timestamp. </p>
     *
     * @return  function with the default system timezone reference,
     *          applicable on instances of {@code Moment}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Funktion, die einen {@link Moment} mit
     * Hilfe der Systemzeitzonenreferenz abfragen kann. </p>
     *
     * <p>Hinweis: Die Funktion wandelt meist den gegebenen {@code Moment}
     * in einen lokalen Zeitstempel um und fragt dann diesen ab. </p>
     *
     * @return  function with the default system timezone reference,
     *          applicable on instances of {@code Moment}
     * @since   2.0
     */
    ChronoFunction<Moment, V> inStdTimezone();

    /**
     * <p>Creates a function which can query a {@link Moment} in the
     * given timezone. </p>
     *
     * <p>Note: Usually the function converts the given {@code Moment} to
     * a {@code PlainTimestamp} and then queries this zonal timestamp. </p>
     *
     * @param   tzid        timezone id
     * @return  function applicable on instances of {@code Moment}
     * @since   2.0
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Erzeugt eine Funktion, die einen {@link Moment} mit
     * Hilfe einer Zeitzonenreferenz abfragen kann. </p>
     *
     * <p>Hinweis: Die Funktion wandelt meist den gegebenen {@code Moment}
     * in einen zonalen Zeitstempel um und fragt dann diesen ab. </p>
     *
     * @param   tzid        timezone id
     * @return  function applicable on instances of {@code Moment}
     * @since   2.0
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    ChronoFunction<Moment, V> inTimezone(TZID tzid);

    /**
     * <p>Creates a function which can query a {@link Moment} in the
     * given timezone. </p>
     *
     * <p>Note: Usually the function converts the given {@code Moment} to
     * a {@code PlainTimestamp} and then queries this zonal timestamp. </p>
     *
     * @param   tz          timezone
     * @return  function applicable on instances of {@code Moment}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Funktion, die einen {@link Moment} mit
     * Hilfe einer Zeitzonenreferenz abfragen kann. </p>
     *
     * <p>Hinweis: Die Funktion wandelt meist den gegebenen {@code Moment}
     * in einen zonalen Zeitstempel um und fragt dann diesen ab. </p>
     *
     * @param   tz          timezone
     * @return  function applicable on instances of {@code Moment}
     * @since   2.0
     */
    ChronoFunction<Moment, V> in(Timezone tz);

    /**
     * <p>Equivalent to {@code at(ZonalOffset.UTC)}. </p>
     *
     * @return  function applicable on instances of {@code Moment}
     * @since   2.0
     * @see     #at(ZonalOffset)
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code at(ZonalOffset.UTC)}. </p>
     *
     * @return  function applicable on instances of {@code Moment}
     * @since   2.0
     * @see     #at(ZonalOffset)
     */
    ChronoFunction<Moment, V> atUTC();

    /**
     * <p>Creates a function which can query a {@link Moment} at the
     * given timezone offset. </p>
     *
     * <p>Note: Usually the function converts the given {@code Moment} to
     * a {@code PlainTimestamp} and then queries this zonal timestamp. </p>
     *
     * @param   offset  timezone offset
     * @return  function applicable on instances of {@code Moment}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt einen Operator, der einen {@link Moment} mit
     * Hilfe eines Zeitzonen-Offsets anpassen kann. </p>
     *
     * <p>Hinweis: Die Funktion wandelt meist den gegebenen {@code Moment}
     * in einen zonalen Zeitstempel um und fragt dann diesen ab. </p>
     *
     * @param   offset  timezone offset
     * @return  function applicable on instances of {@code Moment}
     * @since   2.0
     */
    ChronoFunction<Moment, V> at(ZonalOffset offset);

}
