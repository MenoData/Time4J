/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SimpleEra.java) is part of project Time4J.
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

import net.time4j.base.GregorianDate;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ElementRule;


/**
 * <p>Repr&auml;sentiert eine vereinfachte &Auml;ra, die den angenommenen
 * Zeitpunkt von Jesu Geburt im proleptischen gregorianischen Kalender als
 * Teilung der Zeitskala benutzt. </p>
 *
 * @author  Meno Hochschild
 */
enum SimpleEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>&Auml;ra vor Christi Geburt. </p>
     *
     * <p>BC = Before Christian</p>
     */
    BC {
        @Override
        public GregorianDate getDate() {
            return PlainDate.of(0, Month.DECEMBER, 31);
        }
        @Override
        public boolean isStarting() {
            return false;
        }
    },

    /**
     * <p>&Auml;ra nach Christi Geburt. </p>
     *
     * <p>AD = Anno Domini</p>
     */
    AD {
        @Override
        public GregorianDate getDate() {
            return PlainDate.of(1, Month.JANUARY, 1);
        }
        @Override
        public boolean isStarting() {
            return true;
        }
    };

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getValue() {

        return this.ordinal();

    }

    //~ Innere Klassen ----------------------------------------------------

    static class Rule<T extends ChronoEntity<T>>
        implements ElementRule<T, SimpleEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public SimpleEra getValue(T context) {

            int year = context.get(PlainDate.CALENDAR_DATE).getYear();
            return ((year <= 0) ? SimpleEra.BC : SimpleEra.AD);

        }

        @Override
        public SimpleEra getMinimum(T context) {

            return SimpleEra.BC;

        }

        @Override
        public SimpleEra getMaximum(T context) {

            return SimpleEra.AD;

        }

        @Override
        public boolean isValid(
            T context,
            SimpleEra value
        ) {

            return (value != null);

        }

        @Override
        public T withValue(
            T context,
            SimpleEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new NullPointerException("Missing era.");
            }

            PlainDate date = context.get(PlainDate.CALENDAR_DATE);
            int year = date.getYear();

            if (
                ((year <= 0) && (value == SimpleEra.AD))
                || ((year > 0) && (value == SimpleEra.BC))
            ) {
                date = date.with(PlainDate.YEAR, 1 - year);
            }

            return context.with(PlainDate.CALENDAR_DATE, date);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            return PlainDate.YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            return PlainDate.YEAR_OF_ERA;

        }

    }

}
