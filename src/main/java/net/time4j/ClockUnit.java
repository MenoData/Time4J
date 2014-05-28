/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ClockUnit.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;
import net.time4j.engine.TimePoint;


/**
 * <p>Repr&auml;sentiert die meistgebr&auml;chlichen Zeiteinheiten einer
 * ISO-konformen Uhrzeit. </p>
 *
 * @author  Meno Hochschild
 */
public enum ClockUnit
    implements IsoTimeUnit {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Zeiteinheit &quot;Stunden&quot; (Symbol H) */
    HOURS() {
        @Override
        public char getSymbol() {
            return 'H';
        }
        @Override
        public double getLength() {
            return 3600.0;
        }
    },

    /** Zeiteinheit &quot;Minuten&quot; (Symbol M) */
    MINUTES() {
        @Override
        public char getSymbol() {
            return 'M';
        }
        @Override
        public double getLength() {
            return 60.0;
        }
    },

    /**
     * <p>Zeiteinheit &quot;Sekunden&quot; (Symbol S). </p>
     *
     * <p>Zu beachten: Von wahren SI-Zeiteinheiten kann in Time4J erst ab
     * 1972-01-01T00:00:00Z gesprochen werden. Davor speichert Time4J Zeiten
     * in UT1. Eine SI-Sekunde wird als das 9192631770-fache der Periodendauer
     * der dem &Uuml;bergang zwischen den beiden Hyperfeinstrukturniveaus des
     * Grundzustands von Atomen des Nuklids Cs-133 entsprechenden Strahlung
     * definiert. </p>
     */
    SECONDS() {
        @Override
        public char getSymbol() {
            return 'S';
        }
        @Override
        public double getLength() {
            return 1.0;
        }
    },

    /** Zeiteinheit &quot;Millisekunden&quot; (Symbol 3) */
    MILLIS() {
        @Override
        public char getSymbol() {
            return '3';
        }
        @Override
        public double getLength() {
            return 1.0 / 1000;
        }
    },

    /** Zeiteinheit &quot;Mikrosekunden&quot; (Symbol 6) */
    MICROS() {
        @Override
        public char getSymbol() {
            return '6';
        }
        @Override
        public double getLength() {
            return 1.0 / 1000000;
        }
    },

    /** Zeiteinheit &quot;Nanosekunden&quot; (Symbol 9) */
    NANOS() {
        @Override
        public char getSymbol() {
            return '9';
        }
        @Override
        public double getLength() {
            return 1.0 / 1000000000;
        }
    };

    // Standard-Umrechnungsfaktoren
    private static final long[] FACTORS = {
        1L, 60L, 3600L, 3600000L, 3600000000L, 3600000000000L
    };

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ermittelt den zeitlichen Abstand zwischen den angegebenen
     * Zeitangaben gemessen in dieser Einheit. </p>
     *
     * @param   <T> generic type of time point
     * @param   start   starting time
     * @param   end     ending time
     * @return  duration as count of this unit
     */
    public <T extends TimePoint<? super ClockUnit, T>> long between(
        T start,
        T end
    ) {

        return start.until(end, this);

    }

    /**
     * <p>Konvertiert die angegebene Zeitdauer in einen Betrag gez&auml;hlt
     * in dieser Zeiteinheit. </p>
     *
     * <p>Konversionen von genaueren zu weniger genauen Zeiteinheiten
     * f&uuml;hren im allgemeinen zu Verlusten an Information. Zum Beispiel
     * wird die Konversion von <tt>999</tt> Millisekunden <tt>0</tt> Sekunden
     * ergeben. Umgekehrt kann die Konversion von groben zu feinen Einheiten
     * zu einem &Uuml;berlauf f&uuml;hren. </p>
     *
     * <p>Beispiel: Um 44 Minuten zu Millisekunden zu konvertieren, wird der
     * Ausdruck {@code ClockUnit.MILLIS.convert(44L, ClockUnit.MINUTES)}
     * angewandt. Zu beachten: Sind auch Minuten und Stunden zu konvertieren,
     * dann werden UTC-Schaltsekunden nicht ber&uuml;cksichtigt, d.h., eine
     * Minute hat hier immer genau 60 Sekunden. </p>
     *
     * @param   sourceDuration  amount of duration to be converted
     * @param   sourceUnit      time unit of duration to be converted
     * @return  converted duration expressed in this unit
     * @throws  ArithmeticException in case of long overflow
     */
    public long convert(
        long sourceDuration,
        ClockUnit sourceUnit
    ) {

        if (sourceDuration == 0) {
            return 0L;
        }

        int o1 = this.ordinal();
        int o2 = sourceUnit.ordinal();

        if (o1 == o2) {
            return sourceDuration;
        } else if (o1 > o2) {
            return MathUtils.safeMultiply(
                sourceDuration,
                FACTORS[o1 - o2]
            );
        } else {
            return (sourceDuration / FACTORS[o2 - o1]);
        }

    }

    /**
     * <p>Eine Uhrzeiteinheit ist nicht kalendarisch. </p>
     *
     * @return  {@code false}
     */
    @Override
    public boolean isCalendrical() {

        return false;

    }

}
