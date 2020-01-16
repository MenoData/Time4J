/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AryaSiddhanta.java) is part of project Time4J.
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

package net.time4j.calendar.hindu;

import net.time4j.engine.CalendarEra;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.engine.VariantSource;

import java.util.Collections;
import java.util.List;


/**
 * <p>A set of calculations developed by Arya Siddhanta of Aryabhata
 * in Julian year 499 AD, mentioned by Lalla at about 720-790 AD. </p>
 *
 * <p>The old Hindu calendar uses mean values in all astronomical calculations. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Berechnungen des alten Hindukalenders, der von Arya Siddhanta von Aryabhata
 * im julianischen Jahr 499 AD entwickelt wurde, zitiert durch Lalla ungef&auml;hr um 720-790 AD. </p>
 *
 * <p>Der alte Hindukalender verwendet Mittelwerte in allen astronomischen Berechnungen. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
public enum AryaSiddhanta
    implements VariantSource {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Describes the solar calendar whose months are between 29 and 32 days long. </p>
     */
    /*[deutsch]
     * <p>Stellt den Sonnenkalender dar, dessen Monate zwischen 29 und 32 Tagen lang sind. </p>
     */
    SOLAR {
        @Override
        CalendarSystem<HinduCalendar> getCalendarSystem() {
            return new OldCS(true);
        }
    },

    /**
     * <p>Describes the lunisolar calendar whose months correspond to the new moon cycle. </p>
     *
     * <p>Sometimes, a leap month will be inserted to synchronize the lunar year with the solar year. </p>
     */
    /*[deutsch]
     * <p>Stellt den Mondkalender dar, dessen Monate dem Neumondzyklus folgen. </p>
     *
     * <p>Gelegentlich wird ein Schaltmonat eingef&uuml;gt, um das Mondjahr mit dem Sonnenjahr
     * zu synchronisieren. </p>
     */
    LUNAR {
        @Override
        CalendarSystem<HinduCalendar> getCalendarSystem() {
            return new OldCS(false);
        }
    };

    static final String PREFIX = "AryaSiddhanta@";

    //~ Methoden ----------------------------------------------------------

    @Override
    public String getVariant() {
        return PREFIX + this.name();
    }

    abstract CalendarSystem<HinduCalendar> getCalendarSystem();

    //~ Innere Klassen ----------------------------------------------------

    private static class OldCS
        extends HinduVariant.BaseCS {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final double ARYA_SOLAR_YEAR = 15779175.0 / 43200.0;
        private static final double ARYA_SOLAR_MONTH = ARYA_SOLAR_YEAR / 12.0;


        //~ Konstruktoren -------------------------------------------------

        OldCS(boolean solar) {
            super(solar ? HinduVariant.VAR_OLD_SOLAR : HinduVariant.VAR_OLD_LUNAR);
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public long getMinimumSinceUTC() {
            return EpochDays.UTC.transform(KALI_YUGA_EPOCH, EpochDays.RATA_DIE);
        }

        @Override
        public long getMaximumSinceUTC() {
            return 0; // TODO: implementieren
        }

        @Override
        public List<CalendarEra> getEras() {
            return Collections.singletonList(HinduEra.KALI_YUGA);
        }

        @Override
        public HinduCalendar transform(long utcDays) {
            if (this.isSolar()) {
                double sun = EpochDays.RATA_DIE.transform(utcDays, EpochDays.UTC) - KALI_YUGA_EPOCH + 0.25;
                int y = (int) Math.floor(sun / ARYA_SOLAR_YEAR);
                int m = (int) HinduVariant.BaseCS.modulo(Math.floor(sun / ARYA_SOLAR_MONTH), 12) + 1;
                int dom = (int) Math.floor(HinduVariant.BaseCS.modulo(sun, ARYA_SOLAR_MONTH)) + 1;

                return new HinduCalendar(
                    super.variant,
                    y,
                    HinduMonth.ofSolar(m),
                    HinduDay.valueOf(dom));
            } else {
                return null;
            }
        }

        @Override
        public long transform(HinduCalendar date) {
            if (this.isSolar()) {
                double d =
                    KALI_YUGA_EPOCH
                        + date.getExpiredYearOfKaliYuga() * ARYA_SOLAR_YEAR
                        + (date.getMonth().getRasi() - 1) * ARYA_SOLAR_MONTH
                        + date.getDayOfMonth().getValue()
                        - 1.25;
                return EpochDays.UTC.transform((long) Math.ceil(d), EpochDays.RATA_DIE);
            } else {
                return 0L;
            }
        }

        private boolean isSolar() {
            return (super.variant == HinduVariant.VAR_OLD_SOLAR);
        }

    }

}
