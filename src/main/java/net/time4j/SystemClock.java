/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SystemClock.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TZID;


/**
 * <p>Repr&auml;sentiert eine Uhr, die auf dem Taktgeber des Betriebssystems
 * basiert. </p>
 *
 * <p>Mit der System-Property &quot;net.time4j.SystemClock.nanoTime&quot;
 * kann gesteuert werden, ob diese Uhr intern auf dem Ausdruck
 * {@link System#nanoTime()} (wenn Property auf {@code true} gesetzt)
 * oder {@link System#currentTimeMillis()} (Standard) basiert. </p>
 *
 * @author  Meno Hochschild
 */
public final class SystemClock
    implements TimeSource<Moment> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;
    private static final int MRD = MIO * 1000;
    private static final long CALIBRATED_OFFSET;
    private static final boolean HIGH_PRECISION;

    static {
        HIGH_PRECISION = Boolean.getBoolean("net.time4j.SystemClock.nanoTime");

        if (HIGH_PRECISION) {
            long millis = System.currentTimeMillis();
            long nanos = 0;

            for (int i = 0; i < 10; i++) {
                nanos = System.nanoTime();
                long next = System.currentTimeMillis();
                if (millis == next) {
                    break; // nun ist sicher, daß nanos zu millis synchron ist
                } else {
                    millis = next;
                }
            }

            CALIBRATED_OFFSET = (
                MathUtils.safeSubtract(
                    MathUtils.safeMultiply(millis, MIO),
                    nanos
                )
            );
        } else {
            CALIBRATED_OFFSET = 0;
        }
    }

    /**
     * <p>Singleton-Instanz. </p>
     */
    public static final SystemClock INSTANCE = new SystemClock();

    //~ Instanzvariablen --------------------------------------------------

    private final Clock source;

    //~ Konstruktoren -----------------------------------------------------

    private SystemClock() {
        super();

        this.source = (HIGH_PRECISION ? Clock.PRECISION : Clock.STANDARD);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Moment currentTime() {

        return this.source.getTime();

    }

    /**
     * <p>Liefert die aktuelle seit [1970-01-01T00:00:00,000Z] verstrichene
     * Zeit in Millisekunden. </p>
     *
     * @return  count of milliseconds since UNIX epoch without leap seconds
     * @see     #currentTimeInMicros()
     */
    public long currentTimeInMillis() {

        return this.source.getMillis();

    }

    /**
     * <p>Liefert die aktuelle seit [1970-01-01T00:00:00,000000Z] verstrichene
     * Zeit in Mikrosekunden. </p>
     *
     * <p>Basiert diese Uhr nur auf {@link System#currentTimeMillis()}, wird
     * diese Methode lediglich den Millisekundenwert mit dem Faktor {@code 1000}
     * multiplizieren. Auf den meisten Betriebssystemen ist die Genauigkeit
     * auch nur auf Millisekunden begrenzt. Das gilt selbst dann, wenn diese
     * Uhr auf {@link System#nanoTime()} basiert, weil hier wenigstens einmal
     * zum Zweck der Kalibrierung auf {@code System.currentTimeMillis()}
     * zur&uuml;ckgegriffen werden mu&szlig;. </p>
     *
     * @return  count of microseconds since UNIX epoch without leap seconds
     */
    public long currentTimeInMicros() {

        return this.source.getMicros();

    }

    /**
     * <p>Erzeugt eine lokale Uhr in der angegebenen Zeitzone. </p>
     *
     * @return  local clock in standard time zone
     */
    public static ZonalClock inStdTimezone() {

        return ZonalClock.ofSystem();

    }

    /**
     * <p>Erzeugt eine lokale Uhr in der angegebenen Zeitzone. </p>
     *
     * @param   tzid        time zone id
     * @return  local clock in given time zone
     */
    public static ZonalClock inTimezone(TZID tzid) {

        return new ZonalClock(SystemClock.INSTANCE, tzid);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static enum Clock {

        //~ Statische Felder/Initialisierungen ----------------------------

        STANDARD() {
            @Override
            Moment getTime() {
                long millis = getMillis();
                int nanos = ((int) (millis % 1000)) * MIO;
                return Moment.of(
                    millis / 1000,
                    nanos,
                    TimeScale.POSIX);
            }
            @Override
            long getMillis() {
                return System.currentTimeMillis();
            }
            @Override
            long getMicros() {
                return MathUtils.safeMultiply(getMillis(), 1000);
            }
            @Override
            long getNanos() {
                return MathUtils.safeMultiply(getMillis(), MIO);
            }
        },

        PRECISION() {
            @Override
            Moment getTime() {
                long nanos = getNanos();
                return Moment.of(
                    nanos / MRD,
                    (int) (nanos % MRD),
                    TimeScale.POSIX);
            }
            @Override
            long getMillis() {
                return getNanos() / MIO;
            }
            @Override
            long getMicros() {
                return getNanos() / 1000;
            }
            @Override
            long getNanos() {
                return MathUtils.safeAdd(
                    System.nanoTime(),
                    CALIBRATED_OFFSET
                );
            }
        };

        //~ Methoden ------------------------------------------------------

        abstract Moment getTime();

        abstract long getMillis();

        abstract long getMicros();

        abstract long getNanos();

    }

}
