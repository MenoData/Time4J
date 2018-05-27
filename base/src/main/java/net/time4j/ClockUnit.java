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
import net.time4j.engine.Normalizer;
import net.time4j.engine.TimePoint;
import net.time4j.engine.TimeSpan;


/**
 * <p>Represents the most common time units on an ISO-8601-conforming
 * analogue clock counting the scale ticks. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die meistgebr&auml;chlichen Zeiteinheiten einer
 * ISO-konformen Uhrzeit, entsprechend den Skalenstrichen auf einer
 * analogen Uhr. </p>
 *
 * @author  Meno Hochschild
 */
public enum ClockUnit
    implements IsoTimeUnit {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Time unit &quot;hours&quot; (symbol H) */
    /*[deutsch] Zeiteinheit &quot;Stunden&quot; (Symbol H) */
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

    /** Time unit &quot;minutes&quot; (symbol M) */
    /*[deutsch] Zeiteinheit &quot;Minuten&quot; (Symbol M) */
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
     * <p>Time unit &quot;seconds&quot; (symbol S) according to the
     * position of the second pointer on an analogue clock. </p>
     *
     * <p>This unit is NOT the SI-second. </p>
     *
     * @see     SI
     */
    /*[deutsch]
     * <p>Zeiteinheit &quot;Sekunden&quot; (Symbol S) entsprechend der
     * Stellung des Sekundenzeigers auf einer analogen Uhr. </p>
     *
     * <p>Diese Zeiteinheit ist nicht die SI-Sekunde. </p>
     *
     * @see     SI
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

    /** Time unit &quot;milliseconds&quot; (symbol 3) */
    /*[deutsch] Zeiteinheit &quot;Millisekunden&quot; (Symbol 3) */
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

    /** Time unit &quot;microseconds&quot; (symbol 6) */
    /*[deutsch] Zeiteinheit &quot;Mikrosekunden&quot; (Symbol 6) */
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

    /** Time unit &quot;nanoseconds&quot; (symbol 9) */
    /*[deutsch] Zeiteinheit &quot;Nanosekunden&quot; (Symbol 9) */
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
     * <p>Calculates the temporal distance between given wall times
     * in this unit. </p>
     *
     * @param   <T> generic type of time point
     * @param   start   starting time
     * @param   end     ending time
     * @return  duration as count of this unit
     */
    /*[deutsch]
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
     * <p>Converts the given duration to a temporal amount measured in
     * this unit. </p>
     *
     * <p>Conversions from more precise to less precise units are usually
     * associated with a loss of information. For example the conversion
     * of <tt>999</tt> milliseconds results to <tt>0</tt> seconds. In reverse,
     * the conversion of less precise to more precise units can result in
     * a numerical overflow. </p>
     *
     * <p>Example: In order to convert 44 minutes to milliseconds, the
     * expression {@code ClockUnit.MILLIS.convert(44L, ClockUnit.MINUTES)}
     * is applied. Note: If hours or minutes are to be converted then
     * UTC-leapseconds will be ignored that is a minute has here always
     * 60 seconds. </p>
     *
     * @param   sourceDuration  amount of duration to be converted
     * @param   sourceUnit      time unit of duration to be converted
     * @return  converted duration expressed in this unit
     * @throws  ArithmeticException in case of long overflow
     */
    /*[deutsch]
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
                FACTORS[o1] / FACTORS[o2]
            );
        } else {
            long factor = FACTORS[o2] / FACTORS[o1];
            return (sourceDuration / factor); // possible truncation
        }

    }

    /**
     * <p>Converts the given duration to an amount in this unit and performs
     * any necessary truncation if needed. </p>
     *
     * @param   duration    temporal amount in clock units to be converted
     * @return  count of this unit representing given duration
     * @since   1.2
     */
    /*[deutsch]
     * <p>Konvertiert die angegebene Dauer zu einer Anzahl von Zeiteinheiten
     * dieser Instanz und rundet bei Bedarf. </p>
     *
     * @param   duration    temporal amount in clock units to be converted
     * @return  count of this unit representing given duration
     * @since   1.2
     */
    public long convert(TimeSpan<? extends ClockUnit> duration) {

        if (duration.isEmpty()) {
            return 0;
        }

        long total = 0;
        ClockUnit smallest = null;

        for (int i = duration.getTotalLength().size() - 1; i >= 0; i--) {
            TimeSpan.Item<? extends ClockUnit> item =
                duration.getTotalLength().get(i);
            ClockUnit unit = item.getUnit();

            if (smallest == null) {
                smallest = unit;
                total = item.getAmount();
            } else {
                total =
                    MathUtils.safeAdd(
                        total,
                        smallest.convert(item.getAmount(), unit));
            }
        }

        if (duration.isNegative()) {
            total = MathUtils.safeNegate(total);
        }

        return this.convert(total, smallest); // possibly lossy

    }

    /**
     * <p>Yields a normalizer which converts a given duration in another
     * duration with only this clock unit. </p>
     *
     * @return  normalizer
     * @since   1.2
     * @see     #convert(TimeSpan)
     */
    /*[deutsch]
     * <p>Liefert einen Normalisierer, der eine Dauer in eine andere Dauer nur mit dieser
     * Zeiteinheit konvertiert. </p>
     *
     * @return  normalizer
     * @since   1.2
     * @see     #convert(TimeSpan)
     */
    public Normalizer<ClockUnit> only() {

        return ClockNormalizer.ofOnlyMode(this);

    }

    /**
     * <p>Yields a normalizer which converts a given duration in another
     * duration with smaller units truncated. </p>
     *
     * @return  normalizer
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert einen Normalisierer, der eine Dauer in eine andere Dauer so
     * konvertiert, da&szlig; Dauerelemente mit kleineren Zeiteinheiten
     * abgeschnitten werden. </p>
     *
     * @return  normalizer
     * @since   3.0
     */
    public Normalizer<ClockUnit> truncated() {

        return ClockNormalizer.ofTruncateMode(this);

    }

    /**
     * <p>Yields a normalizer which converts a given duration in another
     * normalized duration with smaller units truncated and this unit rounded. </p>
     *
     * <p>This normalizer is a combination of {@code Duration.STD_CLOCK_PERIOD},
     * {@code truncated()} and a special half-up rounding. Example: </p>
     *
     * <pre>
     *  Duration&lt;ClockUnit&gt; timePeriod = Duration.ofClockUnits(4, 55, 90);
     *  System.out.println(timePeriod.with(ClockUnit.MINUTES.rounded()));
     *  // output: P4H57M
     * </pre>
     *
     * @return  normalizer
     * @since   3.0
     * @see     Duration#STD_CLOCK_PERIOD
     */
    /*[deutsch]
     * <p>Liefert einen Normalisierer, der eine Dauer in eine andere normalisierte Dauer so
     * konvertiert, da&szlig; Dauerelemente mit kleineren Zeiteinheiten abgeschnitten werden
     * und diese Zeiteinheit gerundet wird. </p>
     *
     * <p>Dieser Normalisierer ist eine Kombination aus {@code Duration.STD_CLOCK_PERIOD},
     * {@code truncated()} und einer kaufm&auml;nnischen Rundung. Beispiel: </p>
     *
     * <pre>
     *  Duration&lt;ClockUnit&gt; timePeriod = Duration.ofClockUnits(4, 55, 90);
     *  System.out.println(timePeriod.with(ClockUnit.MINUTES.rounded()));
     *  // Ausgabe: P4H57M
     * </pre>
     *
     * @return  normalizer
     * @since   3.0
     * @see     Duration#STD_CLOCK_PERIOD
     */
    public Normalizer<ClockUnit> rounded() {

        return ClockNormalizer.ofRoundingMode(this);

    }

    /**
     * <p>A wall time unit is never calendrical. </p>
     *
     * @return  {@code false}
     */
    /*[deutsch]
     * <p>Eine Uhrzeiteinheit ist nicht kalendarisch. </p>
     *
     * @return  {@code false}
     */
    @Override
    public boolean isCalendrical() {

        return false;

    }

}
