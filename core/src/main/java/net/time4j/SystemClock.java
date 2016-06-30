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
import net.time4j.base.ResourceLoader;
import net.time4j.base.TimeSource;
import net.time4j.scale.LeapSeconds;
import net.time4j.scale.TickProvider;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;


/**
 * <p>Represents a clock which is based on the clock of the underlying operating system. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Uhr, die auf dem Taktgeber des Betriebssystems basiert. </p>
 *
 * @author  Meno Hochschild
 */
public final class SystemClock
    implements TimeSource<Moment> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;
    private static final int MRD = MIO * 1000;

    private static final TickProvider PROVIDER;
    private static final boolean MONOTON_MODE;

    static {
        String platform = System.getProperty("java.vm.name");
        TickProvider candidate = null;

        for (TickProvider temp : ResourceLoader.getInstance().services(TickProvider.class)) {
            if (platform.equals(temp.getPlatform())) {
                candidate = temp;
                break;
            }
        }

        if (candidate == null) {
            candidate = new StdTickProvider();
        }

        PROVIDER = candidate;
        MONOTON_MODE = Boolean.getBoolean("net.time4j.systemclock.nanoTime");
    }

    /**
     * <p>Standard implementation. </p>
     *
     * <p>The system property &quot;net.time4j.systemclock.nanoTime&quot; controls if this clock is internally
     * based on the expression {@link System#nanoTime()} (if property is set to &quot;true&quot;) or
     * {@link System#currentTimeMillis()} (default). The standard case is a clock which is affected by
     * OS-triggered time jumps and user adjustments so there is no guarantee for a monotonic time. </p>
     */
    /*[deutsch]
     * <p>Standard-Implementierung. </p>
     *
     * <p>Mit der System-Property &quot;net.time4j.systemclock.nanoTime&quot; kann gesteuert werden, ob diese
     * Uhr intern auf dem Ausdruck {@link System#nanoTime()} (wenn Property auf &quot;true&quot; gesetzt)
     * oder {@link System#currentTimeMillis()} (Standard) basiert. Der Standardfall ist eine Uhr, die
     * f&uuml;r Zeitspr&uuml;nge und manuelle Verstellungen der Betriebssystem-Uhr empfindlich ist, so
     * da&szlig; keine Garantie f&uuml;r eine monoton ablaufende Zeit gegeben werden kann. </p>
     */
    public static final SystemClock INSTANCE = new SystemClock(false, calibrate());

    /**
     * <p>Monotonic clock based on the best available clock of the underlying operating system. </p>
     *
     * <p>A side effect of this implementation can be increased nominal precision up to nanoseconds
     * although no guarantee is made to ensure nanosecond accuracy. The accuracy is often limited to
     * milliseconds. However, the main focus and motivation is realizing a monotonic behaviour by
     * delegating to the use of a monotonic clock of the underlying OS. Equivalent to {@code CLOCK_MONOTONIC}
     * on a Linux-server. </p>
     *
     * @see     TickProvider#getNanos()
     * @since   3.2/4.1
     */
    /*[deutsch]
     * <p>Monotone Uhr, die auf der besten verf&uuml;gbaren Uhr des Betriebssystems basiert. </p>
     *
     * <p>Ein Seiteneffekt dieser Implementierung kann eine erh&ouml;hte nominelle Genauigkeit bis hin
     * zu Nanosekunden sein. Allerdings ist die reale Genauigkeit oft auf Millisekunden beschr&auml;nkt.
     * Jedoch liegt der Hauptfokus darauf, ein monotones Verhalten dadurch zu realisieren, da&szlig;
     * eine monotone Uhr des zugrundeliegenden Betriebssystems verwendet wird. &Auml;quivalent zu
     * {@code CLOCK_MONOTONIC} auf einem Linux-Server. </p>
     *
     * @see     TickProvider#getNanos()
     * @since   3.2/4.1
     */
    public static final SystemClock MONOTONIC = new SystemClock(true, calibrate());

    //~ Instanzvariablen --------------------------------------------------

    private final boolean monotonic;
    private final long offset;

    //~ Konstruktoren -----------------------------------------------------

    private SystemClock(
        boolean monotonic,
        long offset
    ) {
        super();

        this.monotonic = monotonic;
        this.offset = offset;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Moment currentTime() {

        if (this.monotonic || MONOTON_MODE) {
            long nanos = this.utcNanos();
            return Moment.of(
                MathUtils.floorDivide(nanos, MRD),
                MathUtils.floorModulo(nanos, MRD),
                TimeScale.UTC);
        } else {
            long millis = System.currentTimeMillis();
            return Moment.of(
                MathUtils.floorDivide(millis, 1000),
                MathUtils.floorModulo(millis, 1000) * MIO,
                TimeScale.POSIX);
        }

    }

    /**
     * <p>Yields the current time in milliseconds elapsed since
     * [1970-01-01T00:00:00,000Z]. </p>
     *
     * @return  count of milliseconds since UNIX epoch without leap seconds
     * @see     #currentTimeInMicros()
     */
    /*[deutsch]
     * <p>Liefert die aktuelle seit [1970-01-01T00:00:00,000Z] verstrichene
     * Zeit in Millisekunden. </p>
     *
     * @return  count of milliseconds since UNIX epoch without leap seconds
     * @see     #currentTimeInMicros()
     */
    public long currentTimeInMillis() {

        if (this.monotonic || MONOTON_MODE) {
            long nanos = this.utcNanos();
            long secs = LeapSeconds.getInstance().strip(MathUtils.floorDivide(nanos, MRD));
            return MathUtils.safeMultiply(secs, 1000) + MathUtils.floorModulo(nanos, MIO);
        } else {
            return System.currentTimeMillis();
        }

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

        if (this.monotonic || MONOTON_MODE) {
            long nanos = this.utcNanos();
            long secs = LeapSeconds.getInstance().strip(MathUtils.floorDivide(nanos, MRD));
            return MathUtils.safeMultiply(secs, MIO) + MathUtils.floorModulo(nanos, 1000);
        } else {
            return MathUtils.safeMultiply(System.currentTimeMillis(), 1000);
        }

    }

    /**
     * <p>Yields the current time in microseconds elapsed since
     * UTC epoch [1972-01-01T00:00:00,000000Z]. </p>
     *
     * @return  count of microseconds since UTC epoch including leap seconds
     * @see     #currentTimeInMicros()
     * @since   3.2/4.1
     */
    /*[deutsch]
     * <p>Liefert die aktuelle seit [1972-01-01T00:00:00,000000Z] verstrichene
     * UTC-Zeit in Mikrosekunden. </p>
     *
     * @return  count of microseconds since UTC epoch including leap seconds
     * @see     #currentTimeInMicros()
     * @since   3.2/4.1
     */
    public long realTimeInMicros() {

        if (this.monotonic || MONOTON_MODE) {
            return MathUtils.floorDivide(this.utcNanos(), 1000);
        } else {
            long millis = System.currentTimeMillis();
            long utc = LeapSeconds.getInstance().enhance(MathUtils.floorDivide(millis, 1000));
            return MathUtils.safeMultiply(utc, MIO) + MathUtils.floorModulo(millis, 1000) * 1000;
        }

    }

    /**
     * <p>Creates a local clock in platform timezone. </p>
     *
     * <p>Uses the standard clock {@code SystemClock.INSTANCE} and the platform timezone data. </p>
     *
     * @return  local clock in system timezone using the platform timezone data
     * @since   3.3/4.2
     * @see     net.time4j.tz.Timezone#ofSystem()
     * @see     #INSTANCE
     * @see     java.util.TimeZone
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der Plattform-Zeitzone. </p>
     *
     * <p>Verwendet die Standarduhr {@code SystemClock.INSTANCE} und die Zeitzonendaten der Plattform. </p>
     *
     * @return  local clock in system timezone using the platform timezone data
     * @since   3.3/4.2
     * @see     net.time4j.tz.Timezone#ofSystem()
     * @see     #INSTANCE
     * @see     java.util.TimeZone
     */
    public static ZonalClock inPlatformView() {

        Timezone sys = Timezone.ofSystem();

        if (sys.getHistory() == null) {
            return new ZonalClock(INSTANCE, sys);
        }

        String tzid = "java.util.TimeZone~" + sys.getID().canonical();
        return new ZonalClock(INSTANCE, tzid);

    }

    /**
     * <p>Creates a local clock in system timezone. </p>
     *
     * <p>Uses the standard clock {@code SystemClock.INSTANCE}. </p>
     *
     * @return  cached local clock in system timezone using the best available timezone data
     * @see     net.time4j.tz.Timezone#ofSystem()
     * @see     #INSTANCE
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der System-Zeitzone. </p>
     *
     * <p>Verwendet die Standarduhr {@code SystemClock.INSTANCE}. </p>
     *
     * @return  cached local clock in system timezone using the best available timezone data
     * @see     net.time4j.tz.Timezone#ofSystem()
     * @see     #INSTANCE
     */
    public static ZonalClock inLocalView() {

        return ZonalClock.ofSystem();

    }

    /**
     * <p>Creates a local clock in given timezone. </p>
     *
     * <p>In order to achieve a monotonic zonal clock, users can use the expression
     * {@code new ZonalClock(SystemClock.MONOTONIC, tzid}. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     #INSTANCE
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der angegebenen Zeitzone. </p>
     *
     * <p>Um eine monotone zonale Uhr zu erhalten, k&ouml;nnen Anwender den Ausdruck
     * {@code new ZonalClock(SystemClock.MONOTONIC, tzid} verwenden. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     #INSTANCE
     */
    public static ZonalClock inZonalView(TZID tzid) {

        return new ZonalClock(SystemClock.INSTANCE, tzid);

    }

    /**
     * <p>Creates a local clock in given timezone. </p>
     *
     * <p>In order to achieve a monotonic zonal clock, users can use the expression
     * {@code new ZonalClock(SystemClock.MONOTONIC, tzid}. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     #INSTANCE
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der angegebenen Zeitzone. </p>
     *
     * <p>Um eine monotone zonale Uhr zu erhalten, k&ouml;nnen Anwender den Ausdruck
     * {@code new ZonalClock(SystemClock.MONOTONIC, tzid} verwenden. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     #INSTANCE
     */
    public static ZonalClock inZonalView(String tzid) {

        return new ZonalClock(SystemClock.INSTANCE, tzid);

    }

    /**
     * <p>Equivalent to {@code SystemClock.INSTANCE.currentTime()}. </p>
     *
     * @return  current time using the standard implementation
     * @see     #INSTANCE
     * @since   2.3
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code SystemClock.INSTANCE.currentTime()}. </p>
     *
     * @return  current time using the standard implementation
     * @see     #INSTANCE
     * @since   2.3
     */
    public static Moment currentMoment() {

        return SystemClock.INSTANCE.currentTime();

    }

    /**
     * <p>Recalibrates this instance and yields a new copy. </p>
     *
     * <p>This method is only relevant if this clock is operated in monotonic mode. It is strongly advised
     * not to recalibrate during or near a leap second. Please also note that this method might cause jumps
     * in time - even backwards. </p>
     *
     * @return  new and recalibrated copy of this instance
     * @see     #MONOTONIC
     * @since   3.2/4.1
     */
    /*[deutsch]
     * <p>Eicht diese Instanz und liefert eine neue Kopie. </p>
     *
     * <p>Diese Methode ist nur relevant, wenn diese Uhr im monotonen Modus l&auml;uft. Es wird dringend
     * angeraten, nicht w&auml;hrend oder nahe einer Schaltsekunde zu eichen. Achtung: Diese Methode kann
     * Zeitspr&uuml;nge verursachen - eventuell sogar r&uuml;ckw&auml;rts. </p>
     *
     * @return  new and recalibrated copy of this instance
     * @see     #MONOTONIC
     * @since   3.2/4.1
     */
    public SystemClock recalibrated() {

        return new SystemClock(this.monotonic, calibrate());

    }

    /**
     * <p>Synchronizes this instance with given time source and yields a new copy. </p>
     *
     * <p>This method is only relevant if this clock is operated in monotonic mode. It is strongly advised
     * not to recalibrate during or near a leap second. Please also note that this method might cause jumps
     * in time - even backwards. </p>
     *
     * @param   clock       another clock which this instance should be synchronized with
     * @return  synchronized copy of this instance
     * @see     #MONOTONIC
     * @since   3.2/4.1
     */
    /*[deutsch]
     * <p>Synchronisiert diese Instanz mit der angegebenen Zeitquelle und liefert eine neue Kopie. </p>
     *
     * <p>Diese Methode ist nur relevant, wenn diese Uhr im monotonen Modus l&auml;uft. Es wird dringend
     * angeraten, nicht w&auml;hrend oder nahe einer Schaltsekunde zu eichen. Achtung: Diese Methode kann
     * Zeitspr&uuml;nge verursachen - eventuell sogar r&uuml;ckw&auml;rts. </p>
     *
     * @param   clock       another clock which this instance should be synchronized with
     * @return  synchronized copy of this instance
     * @see     #MONOTONIC
     * @since   3.2/4.1
     */
    public SystemClock synchronizedWith(TimeSource<?> clock) {

        Moment time = Moment.from(clock.currentTime());
        long compare = (MONOTON_MODE ? System.nanoTime() : PROVIDER.getNanos());

        long utc = time.getElapsedTime(TimeScale.UTC);
        long instantNanos = MathUtils.safeMultiply(utc, MRD) + time.getNanosecond(TimeScale.UTC);
        long newOffset = MathUtils.safeSubtract(instantNanos, compare);

        return new SystemClock(this.monotonic, newOffset);

    }

    private static long calibrate() {

        long millis = System.currentTimeMillis();
        long compare = 0;

        for (int i = 0; i < 10; i++) {
            compare = (MONOTON_MODE ? System.nanoTime() : PROVIDER.getNanos());
            long next = System.currentTimeMillis();
            if (millis == next) {
                break; // nun ist sicher, daß nanos zu millis synchron ist
            } else {
                millis = next;
            }
        }

        long utc = LeapSeconds.getInstance().enhance(MathUtils.floorDivide(millis, 1000));
        long instantNanos = MathUtils.safeMultiply(utc, MRD) + MathUtils.floorModulo(millis, 1000) * MIO;
        return MathUtils.safeSubtract(instantNanos, compare);

    }

    private long utcNanos() {

        long nanos = (MONOTON_MODE ? System.nanoTime() : PROVIDER.getNanos());
        return MathUtils.safeAdd(nanos, this.offset);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class StdTickProvider
        implements TickProvider {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getPlatform() {

            return "";

        }

        @Override
        public long getNanos() {

            return System.nanoTime();

        }

    }

}
