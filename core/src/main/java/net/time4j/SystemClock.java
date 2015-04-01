/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SystemClock.java) is part of project Time4J.
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
import net.time4j.base.TimeSource;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TZID;


/**
 * <p>Represents a clock which is based on the clock of the underlying
 * operating system. </p>
 *
 * <p>The system property &quot;net.time4j.systemclock.nanoTime&quot;
 * controls if this clock is internally based on the expression
 * {@link System#nanoTime()} (if property is set to &quot;true&quot;)
 * or {@link System#currentTimeMillis()} (default). </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Uhr, die auf dem Taktgeber des Betriebssystems
 * basiert. </p>
 *
 * <p>Mit der System-Property &quot;net.time4j.systemclock.nanoTime&quot;
 * kann gesteuert werden, ob diese Uhr intern auf dem Ausdruck
 * {@link System#nanoTime()} (wenn Property auf &quot;true&quot; gesetzt)
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
        HIGH_PRECISION = Boolean.getBoolean("net.time4j.systemclock.nanoTime");

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
     * <p>Singleton-instance. </p>
     */
    /*[deutsch]
     * <p>Singleton-Instanz. </p>
     */
    public static final SystemClock INSTANCE = new SystemClock();

    //~ Konstruktoren -----------------------------------------------------

    private SystemClock() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Moment currentTime() {

        if (HIGH_PRECISION) {
            long nanos = getNanos();
            return Moment.of(
                nanos / MRD,
                (int) (nanos % MRD),
                TimeScale.POSIX);
        } else {
            long millis = System.currentTimeMillis();
            int nanos = ((int) (millis % 1000)) * MIO;
            return Moment.of(millis / 1000, nanos, TimeScale.POSIX);
        }

    }

    /**
     * <p>Yields the current time in milliseconds elapsed since
     * [1970-01-01T00:00:00,000Z]. </p>
     *
     * <p>Starting with version 1.1 this method always delegates to
     * {@link System#currentTimeMillis()}. </p>
     *
     * @return  count of milliseconds since UNIX epoch without leap seconds
     * @see     #currentTimeInMicros()
     */
    /*[deutsch]
     * <p>Liefert die aktuelle seit [1970-01-01T00:00:00,000Z] verstrichene
     * Zeit in Millisekunden. </p>
     *
     * <p>Beginnend mit der Version 1.1 delegiert diese Methode immer
     * an {@link System#currentTimeMillis()}. </p>
     *
     * @return  count of milliseconds since UNIX epoch without leap seconds
     * @see     #currentTimeInMicros()
     */
    public long currentTimeInMillis() {

        return System.currentTimeMillis();

    }

    /**
     * <p>Yields the current time in microseconds elapsed since
     * [1970-01-01T00:00:00,000000Z]. </p>
     *
     * <p>If this clock is based only on {@link System#currentTimeMillis()}
     * then this method will just multiply the millisecond value by factor
     * {@code 1000}. On many operating systems the precision is limited to
     * milliseconds. This is even true if this clock is based on
     * {@link System#nanoTime()} because for purpose of calibration even
     * here the method {@code System.currentTimeMillis()} must be accessed
     * at least one time. </p>
     *
     * @return  count of microseconds since UNIX epoch without leap seconds
     */
    /*[deutsch]
     * <p>Liefert die aktuelle seit [1970-01-01T00:00:00,000000Z] verstrichene
     * Zeit in Mikrosekunden. </p>
     *
     * <p>Basiert diese Uhr nur auf {@link System#currentTimeMillis()}, wird
     * diese Methode lediglich den Millisekundenwert mit dem Faktor {@code 1000}
     * multiplizieren. Auf vielen Betriebssystemen ist die Genauigkeit auch
     * nur auf Millisekunden begrenzt. Das gilt selbst dann, wenn diese
     * Uhr auf {@link System#nanoTime()} basiert, weil hier wenigstens einmal
     * zum Zweck der Kalibrierung auf {@code System.currentTimeMillis()}
     * zur&uuml;ckgegriffen werden mu&szlig;. </p>
     *
     * @return  count of microseconds since UNIX epoch without leap seconds
     */
    public long currentTimeInMicros() {

        if (HIGH_PRECISION) {
            return getNanos() / 1000;
        } else {
            return MathUtils.safeMultiply(System.currentTimeMillis(), 1000);
        }

    }

    /**
     * <p>Creates a local clock in system timezone. </p>
     *
     * @return  local clock in system timezone
     * @see     net.time4j.tz.Timezone#ofSystem()
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der System-Zeitzone. </p>
     *
     * @return  local clock in system timezone
     * @see     net.time4j.tz.Timezone#ofSystem()
     */
    public static ZonalClock inLocalView() {

        return ZonalClock.ofSystem();

    }

    /**
     * <p>Creates a local clock in given timezone. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der angegebenen Zeitzone. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public static ZonalClock inZonalView(TZID tzid) {

        return new ZonalClock(SystemClock.INSTANCE, tzid);

    }

    /**
     * <p>Creates a local clock in given timezone. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der angegebenen Zeitzone. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public static ZonalClock inZonalView(String tzid) {

        return new ZonalClock(SystemClock.INSTANCE, tzid);

    }

    /**
     * <p>Equivalent to {@code SystemClock.INSTANCE.currentTime()}. </p>
     *
     * @return  current time
     * @since   2.3
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code SystemClock.INSTANCE.currentTime()}. </p>
     *
     * @return  current time
     * @since   2.3
     */
    public static Moment currentMoment() {

        return SystemClock.INSTANCE.currentTime();

    }

    private long getNanos() {
        
        return MathUtils.safeAdd(System.nanoTime(), CALIBRATED_OFFSET);
        
    }

}
