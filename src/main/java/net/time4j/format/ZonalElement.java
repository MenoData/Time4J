/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalElement.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Spezialelement als Zeiger auf eine Zeitzonen-ID. </p>
 *
 * @author  Meno Hochschild
 */
enum ZonalElement
    implements ChronoElement<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    TIMEZONE_ID;

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<TZID> getType() {

        return TZID.class;

    }

    @Override
    public char getSymbol() {

        return '\u0000';

    }

    /**
     * <p>Vergleicht die kanonischen Darstellungen auf lexikalischer Basis. </p>
     */
    @Override
    public int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    ) {

        TZID t1 = o1.get(Timezone.identifier());
        TZID t2 = o2.get(Timezone.identifier());

        return t1.canonical().compareTo(t2.canonical());

    }

    /**
     * <p>Als Standardminimum gilt der Offset -14:00. </p>
     *
     * @return  smallest timezone offset according to XML Schema
     */
    @Override
    public TZID getDefaultMinimum() {

        return ZonalOffset.ofHours(ZonalOffset.Sign.BEHIND_UTC, 14);

    }

    /**
     * <p>Als Standardmaximum gilt der Offset +14:00. </p>
     *
     * @return  largest timezone offset according to XML Schema
     */
    @Override
    public TZID getDefaultMaximum() {

        return ZonalOffset.ofHours(ZonalOffset.Sign.AHEAD_OF_UTC, 14);

    }

    /**
     * <p>Ein zonales Element ist kein Datumselement. </p>
     *
     * @return  {@code false}
     */
    @Override
    public boolean isDateElement() {

        return false;

    }

    /**
     * <p>Ein zonales Element ist kein Uhrzeitelement. </p>
     *
     * @return  {@code false}
     */
    @Override
    public boolean isTimeElement() {

        return false;

    }

    /**
     * <p>Ein zonales Element ist nicht nachsichtig. </p>
     *
     * @return  {@code false}
     */
    @Override
    public boolean isLenient() {

        return false;

    }

}
