/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalOperator.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j;

import net.time4j.engine.ChronoOperator;
import net.time4j.tz.TZID;
import net.time4j.tz.TransitionStrategy;


/**
 * <p>Definiert eine Manipulation von Datums- oder Zeitobjekten nach
 * dem Strategy-Entwurfsmuster. </p>
 *
 * @param   <T> generic target type
 * @author  Meno Hochschild
 */
public interface ZonalOperator<T>
    extends ChronoOperator<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erzeugt einen Operator, der einen {@link Moment} mit
     * Hilfe der Systemzeitzonenreferenz anpassen kann. </p>
     *
     * <p>Hinweis: Der Operator wandelt meist den gegebenen {@code Moment}
     * in einen lokalen Zeitstempel um, bearbeitet dann diese lokale
     * Darstellung und konvertiert das Ergebnis in einen neuen {@code Moment}
     * zur&uuml;ck. Ein Spezialfall sind Inkrementierungen und Decrementierungen
     * von (Sub-)Sekundenelementen, bei denen ggf. direkt auf dem globalen
     * Zeitstrahl operiert wird. </p>
     *
     * @return  operator with the default system time zone reference,
     *          applicable on instances of {@code Moment}
     * @see     TransitionStrategy#PUSH_FORWARD
     */
    ChronoOperator<Moment> inSystemTimezone();

    /**
     * <p>Erzeugt einen Operator, der einen {@link Moment} mit
     * Hilfe einer Zeitzonenreferenz anpassen kann. </p>
     *
     * <p>Hinweis: Der Operator wandelt meist den gegebenen {@code Moment}
     * in einen lokalen Zeitstempel um, bearbeitet dann diese lokale
     * Darstellung und konvertiert das Ergebnis in einen neuen {@code Moment}
     * zur&uuml;ck. Ein Spezialfall sind Inkrementierungen und Decrementierungen
     * von (Sub-)Sekundenelementen, bei denen ggf. direkt auf dem globalen
     * Zeitstrahl operiert wird. </p>
     *
     * @param   tzid        time zone id
     * @param   strategy    conflict resolving strategy
     * @return  operator with the given time zone reference, applicable on
     *          instances of {@code Moment}
     */
    ChronoOperator<Moment> inTimezone(
        TZID tzid,
        TransitionStrategy strategy
    );

    /**
     * <p>Wandelt diese Instanz in eine Form um, die auf einen
     * {@code PlainTimestamp} angewandt werden kann. </p>
     *
     * @return  operator applicable on instances of {@code PlainTimestamp}
     */
    // TODO: Mit Time4J-2.0 (Java 8) wahrscheinlich überflüssig
    ChronoOperator<PlainTimestamp> onTimestamp();

}
