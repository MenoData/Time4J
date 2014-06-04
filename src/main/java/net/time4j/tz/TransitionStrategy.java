/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TransitionStrategy.java) is part of project Time4J.
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

import net.time4j.base.GregorianDate;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;


/**
 * <p>Dient der Aufl&ouml;sung von lokalen Zeitangaben zu einer UTC-Weltzeit,
 * wenn wegen L&uuml;cken oder &Uuml;berlappungen auf dem lokalen Zeitstrahl
 * Konflikte auftreten. </p>
 *
 * @author  Meno Hochschild
 * @spec    All implementations must be immutable, thread-safe and serializable.
 */
public interface TransitionStrategy {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Konvertiert eine lokale Zeitangabe in einen globalen Zeitstempel. </p>
     *
     * <p>Ein direkter Aufruf der Methode kann manchmal nur sekundengenau
     * sein, wenn {@code WallTime} keinen unmittelbaren Zugriff auf den
     * Sekundenbruchteil gestattet. Es ist deshalb immer besser, stattdessen
     * {@code PlainTimestamp#at(Timezone)} mit einer strategie-behafteten
     * Zeitzone zu verwenden. </p>
     *
     * @param   localDate   local calendar date in given timezone
     * @param   localTime   local wall time in given timezone
     * @param   timezone    timezone data containing offset history
     * @return  global unix timestamp
     * @see     net.time4j.PlainTimestamp#at(Timezone)
     * @see     Timezone#with(TransitionStrategy)
     */
    UnixTime resolve(
        GregorianDate localDate,
        WallTime localTime,
        Timezone timezone
    );

}
