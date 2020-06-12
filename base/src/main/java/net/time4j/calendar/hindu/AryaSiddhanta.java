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
import net.time4j.engine.EpochDays;

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
public enum AryaSiddhanta {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Describes the solar calendar whose months are between 29 and 32 days long. </p>
     */
    /*[deutsch]
     * <p>Stellt den Sonnenkalender dar, dessen Monate zwischen 29 und 32 Tagen lang sind. </p>
     */
    SOLAR {
        @Override
        public HinduCS getCalendarSystem() {
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
        public HinduCS getCalendarSystem() {
            return new OldCS(false);
        }
    };

    static final String PREFIX = "AryaSiddhanta@";

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the associated non-customizable variant of Hindu calendar. </p>
     *
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Liefert die hiermit verkn&uuml;pfte nicht anpassbare Variante des Hindu-Kalenders. </p>
     *
     * @return  HinduVariant
     */
    public HinduVariant variant() {
        return (this == SOLAR) ? HinduVariant.VAR_OLD_SOLAR : HinduVariant.VAR_OLD_LUNAR;
    }

    /**
     * <p>Obtains the associated calendar system. </p>
     *
     * @return  CalendarSystem for the old Hindu calendar
     */
    /*[deutsch]
     * <p>Liefert das zugeh&ouml;rige Kalendersystem. </p>
     *
     * @return  CalendarSystem for the old Hindu calendar
     */
    abstract HinduCS getCalendarSystem();

    //~ Innere Klassen ----------------------------------------------------

    private static class OldCS
        extends HinduCS {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final double ARYA_SOLAR_YEAR = 15779175.0 / 43200.0;
        private static final double ARYA_SOLAR_MONTH = ARYA_SOLAR_YEAR / 12.0;
        private static final double ARYA_LUNAR_MONTH = 1577917500.0 / 53433336.0;

        //~ Konstruktoren -------------------------------------------------

        OldCS(boolean solar) {
            super(solar ? HinduVariant.VAR_OLD_SOLAR : HinduVariant.VAR_OLD_LUNAR);
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public List<CalendarEra> getEras() {
            return Collections.singletonList(HinduEra.KALI_YUGA);
        }

        @Override
        HinduCalendar create(long utcDays) {
            double sun = EpochDays.RATA_DIE.transform(utcDays, EpochDays.UTC) - KALI_YUGA_EPOCH + 0.25;

            if (this.isSolar()) {
                int y = (int) Math.floor(sun / ARYA_SOLAR_YEAR);
                int m = (int) modulo(Math.floor(sun / ARYA_SOLAR_MONTH), 12) + 1;
                int dom = (int) Math.floor(modulo(sun, ARYA_SOLAR_MONTH)) + 1;

                return new HinduCalendar(
                    super.variant,
                    y,
                    HinduMonth.ofSolar(m),
                    HinduDay.valueOf(dom),
                    utcDays
                );
            } else { // lunisolar
                double newMoon = sun - modulo(sun, ARYA_LUNAR_MONTH);
                double modNMAS = modulo(newMoon, ARYA_SOLAR_MONTH);
                boolean leap = (ARYA_SOLAR_MONTH - ARYA_LUNAR_MONTH >= modNMAS) && (modNMAS > 0);
                int y = (int) (Math.ceil((newMoon + ARYA_SOLAR_MONTH) / ARYA_SOLAR_YEAR) - 1);
                int m = (int) (modulo(Math.ceil(newMoon / ARYA_SOLAR_MONTH), 12) + 1);
                int dom = (int) (modulo(Math.floor(sun * 30 / ARYA_LUNAR_MONTH), 30) + 1);
                HinduMonth month = HinduMonth.ofLunisolar(m);

                return new HinduCalendar(
                    super.variant,
                    y,
                    leap ? month.withLeap() : month,
                    HinduDay.valueOf(dom),
                    utcDays
                );
            }
        }

        @Override
        HinduCalendar create(int kyYear, HinduMonth month, HinduDay dom) {
            double d;

            if (this.isSolar()) {
                d = kyYear * ARYA_SOLAR_YEAR
                    + (month.getRasi() - 1) * ARYA_SOLAR_MONTH
                    + dom.getValue()
                    - 1.25;
            } else { // lunisolar
                double mina = (12 * kyYear - 1) * ARYA_SOLAR_MONTH;
                double lunarNewYear = ARYA_LUNAR_MONTH * (Math.floor(mina / ARYA_LUNAR_MONTH) + 1);
                int m = month.getValue().getValue();

                if (
                    month.isLeap()
                    || (Math.ceil((lunarNewYear - mina) / (ARYA_SOLAR_MONTH - ARYA_LUNAR_MONTH)) > m)
                ) {
                    m--;
                }

                d = lunarNewYear
                    + ARYA_LUNAR_MONTH * m
                    + (dom.getValue() - 1) * (ARYA_LUNAR_MONTH / 30) - 0.25;
            }

            return new HinduCalendar(
                super.variant,
                kyYear,
                month,
                dom,
                EpochDays.UTC.transform((long) Math.ceil(KALI_YUGA_EPOCH + d), EpochDays.RATA_DIE)
            );
        }

        @Override
        boolean isValid(int kyYear, HinduMonth month, HinduDay dom) {
            if ((kyYear < 0) || (kyYear > 5999) || (month == null) || (dom == null)) {
                return false;
            }

            if (this.isSolar() && (month.isLeap() || dom.isLeap())) {
                return false;
            }

            // solar: 30/31, lunar: 29/30
            if (dom.getValue() > (this.isSolar() ? 31 : 30)) {
                return false;
            }

            return !this.isExpunged(kyYear, month, dom);
        }

        @Override
        public long getMinimumSinceUTC() {
            // solar: 0, vaishakha, 1 | lunar: 0, *chaitra, 1
            long min = this.isSolar() ? KALI_YUGA_EPOCH : KALI_YUGA_EPOCH - 29;
            return EpochDays.UTC.transform(min, EpochDays.RATA_DIE);
        }

        @Override
        public long getMaximumSinceUTC() {
            // solar: 5999, chaitra, 30 | lunar: 5999, phalguna, 30
            return this.isSolar() ? 338699L : 338671L;
        }

        private boolean isSolar() {
            return (super.variant == HinduVariant.VAR_OLD_SOLAR);
        }

    }

}
