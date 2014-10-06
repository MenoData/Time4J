/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MachineTime.java) is part of project Time4J.
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
import net.time4j.base.UnixTime;
import net.time4j.engine.TimeMetric;
import net.time4j.engine.TimePoint;
import net.time4j.engine.TimeSpan;
import net.time4j.scale.TimeScale;
import net.time4j.scale.UniversalTime;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.time4j.scale.TimeScale.POSIX;
import static net.time4j.scale.TimeScale.UTC;


/**
 * <p>Represents a duration for machine times in decimal seconds with
 * nanosecond precision. </p>
 *
 * <p>Note: Other time units are NOT contained but can be used in construction
 * of a machine time. Example: </p>
 *
 * <pre>
 *  MachineTime&lt;TimeUnit&gt; mt = MachineTime.of(1, TimeUnit.HOURS);
 *  System.out.println(mt.contains(TimeUnit.HOURS)); // false
 *  System.out.println(mt.getSeconds); // 3600L
 * </pre>
 *
 * @param   <U> either {@code TimeUnit} or {@code SI}
 * @author  Meno Hochschild
 * @since   1.3
 * @see     TimeUnit#SECONDS
 * @see     TimeUnit#NANOSECONDS
 * @see     SI#SECONDS
 * @see     SI#NANOSECONDS
 * @concurrency <immutable>
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Dauer f&uuml;r maschinelle Zeiten in dezimalen
 * Sekunden mit Nanosekundengenauigkeit. </p>
 *
 * <p>Hinweis: Andere Zeiteinheiten sind NICHT enthalten, k&ouml;nnen aber in
 * der Konstruktuion einer maschinellen Dauer verwendet werden. Beispiel: </p>
 *
 * <pre>
 *  MachineTime&lt;TimeUnit&gt; mt = MachineTime.of(1, TimeUnit.HOURS);
 *  System.out.println(mt.contains(TimeUnit.HOURS)); // false
 *  System.out.println(mt.getSeconds); // 3600L
 * </pre>
 *
 * @param   <U> either {@code TimeUnit} or {@code SI}
 * @author  Meno Hochschild
 * @since   1.3
 * @see     TimeUnit#SECONDS
 * @see     TimeUnit#NANOSECONDS
 * @see     SI#SECONDS
 * @see     SI#NANOSECONDS
 * @concurrency <immutable>
 */
public final class MachineTime<U>
    implements TimeSpan<U>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MRD = 1000000000;

    private static final MachineTime<TimeUnit> POSIX_ZERO =
        new MachineTime<TimeUnit>(0, 0, TimeScale.POSIX);
    private static final MachineTime<SI> UTC_ZERO =
        new MachineTime<SI>(0, 0, TimeScale.UTC);

    /**
     * Metric on the POSIX scale (without leap seconds).
     *
     * @since   1.3
     */
    /*[deutsch]
     * Metrik auf der POSIX-Skala (ohne Schaltsekunden).
     *
     * @since   1.3
     */
    public static final
    TimeMetric<TimeUnit, MachineTime<TimeUnit>> ON_POSIX_SCALE =
        new Metric<TimeUnit>(POSIX);

    /**
     * Metric on the UTC scale (inclusive leap seconds).
     *
     * @since   1.3
     */
    /*[deutsch]
     * Metrik auf der UTC-Skala (inklusive Schaltsekunden).
     *
     * @since   1.3
     */
    public static final TimeMetric<TimeUnit, MachineTime<SI>> ON_UTC_SCALE =
        new Metric<SI>(UTC);

    private static final long serialVersionUID = -4150291820807606229L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final long seconds;
    private transient final int nanos;
    private transient final TimeScale scale;

    //~ Konstruktoren -----------------------------------------------------

    private MachineTime(
        long secs,
        int fraction,
        TimeScale scale
    ) {
        super();

        while (fraction < 0) {
            fraction += MRD;
            secs = MathUtils.safeSubtract(secs, 1);
        }

        while (fraction >= MRD) {
            fraction -= MRD;
            secs = MathUtils.safeAdd(secs, 1);
        }

        this.seconds =  secs;
        this.nanos = fraction;
        this.scale = scale;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a machine time duration on the POSIX scale. </p>
     *
     * <p>If there is a subsecond fraction then the result will be
     * normalized such that the nanosecond part is always in the range
     * {@code 0-999999999}. </p>
     *
     * @param   amount of units
     * @param   unit    helps to interprete given amount
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der POSIX-Skala. </p>
     *
     * <p>Wenn es einen Sekundenbruchteil gibt, dann wird das Ergebnis
     * immer so normalisiert sein, da&szlig; der Nanosekundenteil in
     * den Bereich {@code 0-999999999} f&auml;llt. </p>
     *
     * @param   amount of units
     * @param   unit    helps to interprete given amount
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public static MachineTime<TimeUnit> of(
        long amount,
        TimeUnit unit
    ) {

        if (unit.compareTo(TimeUnit.SECONDS) >= 0) {
            long secs =
                MathUtils.safeMultiply(
                    amount,
                    TimeUnit.SECONDS.convert(1, unit));
            return ofPosixUnits(secs, 0);
        }

        long total =
            MathUtils.safeMultiply(
                amount,
                TimeUnit.NANOSECONDS.convert(1, unit));
        long secs = MathUtils.floorDivide(total, MRD);
        int fraction = MathUtils.floorModulo(total, MRD);
        return ofPosixUnits(secs, fraction);

    }

    /**
     * <p>Creates a machine time duration on the UTC scale. </p>
     *
     * <p>If there is a subsecond fraction then the result will be
     * normalized such that the nanosecond part is always in the range
     * {@code 0-999999999}. </p>
     *
     * @param   amount of units
     * @param   unit    helps to interprete given amount
     * @return  new machine time duration
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * <p>Wenn es einen Sekundenbruchteil gibt, dann wird das Ergebnis
     * immer so normalisiert sein, da&szlig; der Nanosekundenteil in
     * den Bereich {@code 0-999999999} f&auml;llt. </p>
     *
     * @param   amount of units
     * @param   unit    helps to interprete given amount
     * @return  new machine time duration
     * @since   1.3
     */
    public static MachineTime<SI> of(
        long amount,
        SI unit
    ) {

        switch (unit) {
            case SECONDS:
                return ofSIUnits(amount, 0);
            case NANOSECONDS:
                long secs = MathUtils.floorDivide(amount, MRD);
                int fraction = MathUtils.floorModulo(amount, MRD);
                return ofSIUnits(secs, fraction);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    /**
     * <p>Creates a machine time duration on the POSIX scale. </p>
     *
     * <p>If there is a subsecond fraction then the result will be
     * normalized such that the nanosecond part is always in the range
     * {@code 0-999999999}. </p>
     *
     * @param   seconds     POSIX-seconds
     * @param   fraction    nanosecond part
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der POSIX-Skala. </p>
     *
     * <p>Wenn es einen Sekundenbruchteil gibt, dann wird das Ergebnis
     * immer so normalisiert sein, da&szlig; der Nanosekundenteil in
     * den Bereich {@code 0-999999999} f&auml;llt. </p>
     *
     * @param   seconds     POSIX-seconds
     * @param   fraction    nanosecond part
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public static MachineTime<TimeUnit> ofPosixUnits(
        long seconds,
        int fraction
    ) {

        if ((seconds == 0) && (fraction == 0)) {
            return POSIX_ZERO;
        }

        return new MachineTime<TimeUnit>(
            seconds,
            fraction,
            TimeScale.POSIX);

    }

    /**
     * <p>Creates a machine time duration on the UTC scale. </p>
     *
     * <p>If there is a subsecond fraction then the result will be
     * normalized such that the nanosecond part is always in the range
     * {@code 0-999999999}. </p>
     *
     * @param   seconds     SI-seconds
     * @param   fraction    nanosecond part
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * <p>Wenn es einen Sekundenbruchteil gibt, dann wird das Ergebnis
     * immer so normalisiert sein, da&szlig; der Nanosekundenteil in
     * den Bereich {@code 0-999999999} f&auml;llt. </p>
     *
     * @param   seconds     SI-seconds
     * @param   fraction    nanosecond part
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public static MachineTime<SI> ofSIUnits(
        long seconds,
        int fraction
    ) {

        if ((seconds == 0) && (fraction == 0)) {
            return UTC_ZERO;
        }

        return new MachineTime<SI>(
            seconds,
            fraction,
            TimeScale.UTC);

    }

    /**
     * <p>Creates a machine time duration on the UTC scale. </p>
     *
     * <p>If there is a subsecond fraction then the result will be
     * normalized such that the nanosecond part is always in the range
     * {@code 0-999999999}. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  IllegalArgumentException if the argument is infinite or NaN
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * <p>Wenn es einen Sekundenbruchteil gibt, dann wird das Ergebnis
     * immer so normalisiert sein, da&szlig; der Nanosekundenteil in
     * den Bereich {@code 0-999999999} f&auml;llt. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  IllegalArgumentException if the argument is infinite or NaN
     * @since   1.3
     */
    public static MachineTime<TimeUnit> ofPosixSeconds(double seconds) {

        if (Double.isInfinite(seconds) || Double.isNaN(seconds)) {
            throw new IllegalArgumentException("Invalid value: " + seconds);
        }

        long secs = (long) Math.floor(seconds);
        int fraction = (int) ((seconds - secs) * MRD);
        return ofPosixUnits(secs, fraction);

    }

    /**
     * <p>Creates a machine time duration on the UTC scale. </p>
     *
     * <p>If there is a subsecond fraction then the result will be
     * normalized such that the nanosecond part is always in the range
     * {@code 0-999999999}. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * <p>Wenn es einen Sekundenbruchteil gibt, dann wird das Ergebnis
     * immer so normalisiert sein, da&szlig; der Nanosekundenteil in
     * den Bereich {@code 0-999999999} f&auml;llt. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public static MachineTime<TimeUnit> ofPosixSeconds(BigDecimal seconds) {

        BigDecimal secs = seconds.setScale(0, RoundingMode.FLOOR);
        int fraction =
            seconds.subtract(secs)
            .multiply(BigDecimal.valueOf(MRD))
            .setScale(0, RoundingMode.DOWN)
            .intValueExact();
        return ofPosixUnits(secs.longValueExact(), fraction);

    }

    /**
     * <p>Creates a machine time duration on the UTC scale. </p>
     *
     * <p>If there is a subsecond fraction then the result will be
     * normalized such that the nanosecond part is always in the range
     * {@code 0-999999999}. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  IllegalArgumentException if the argument is infinite or NaN
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * <p>Wenn es einen Sekundenbruchteil gibt, dann wird das Ergebnis
     * immer so normalisiert sein, da&szlig; der Nanosekundenteil in
     * den Bereich {@code 0-999999999} f&auml;llt. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  IllegalArgumentException if the argument is infinite or NaN
     * @since   1.3
     */
    public static MachineTime<SI> ofSISeconds(double seconds) {

        if (Double.isInfinite(seconds) || Double.isNaN(seconds)) {
            throw new IllegalArgumentException("Invalid value: " + seconds);
        }

        long secs = (long) Math.floor(seconds);
        int fraction = (int) ((seconds - secs) * MRD);
        return ofSIUnits(secs, fraction);

    }

    /**
     * <p>Creates a machine time duration on the UTC scale. </p>
     *
     * <p>If there is a subsecond fraction then the result will be
     * normalized such that the nanosecond part is always in the range
     * {@code 0-999999999}. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * <p>Wenn es einen Sekundenbruchteil gibt, dann wird das Ergebnis
     * immer so normalisiert sein, da&szlig; der Nanosekundenteil in
     * den Bereich {@code 0-999999999} f&auml;llt. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public static MachineTime<SI> ofSISeconds(BigDecimal seconds) {

        BigDecimal secs = seconds.setScale(0, RoundingMode.FLOOR);
        int fraction =
            seconds.subtract(secs)
            .multiply(BigDecimal.valueOf(MRD))
            .setScale(0, RoundingMode.DOWN)
            .intValueExact();
        return ofSIUnits(secs.longValueExact(), fraction);

    }

    /**
     * <p>Yields the seconds of this duration. </p>
     *
     * @return  long
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert die Sekunden dieser Dauer. </p>
     *
     * @return  long
     * @since   1.3
     */
    public long getSeconds() {

        return this.seconds;

    }

    /**
     * <p>Yields the nanosecond fraction of this duration. </p>
     *
     * @return  nanosecond in range {@code 0-999999999}
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert den Nanosekundenteil dieser Dauer. </p>
     *
     * @return  nanosecond in range {@code 0-999999999}
     * @since   1.3
     */
    public int getFraction() {

        return this.nanos;

    }

    /**
     * <p>Yields the related time scale. </p>
     *
     * @return  either {@code TimeScale.POSIX} or {@code TimeScale.UTC}
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitskala. </p>
     *
     * @return  either {@code TimeScale.POSIX} or {@code TimeScale.UTC}
     * @since   1.3
     */
    public TimeScale getScale() {

        return this.scale;

    }

    @Override
    public List<Item<U>> getTotalLength() {

        List<Item<U>> tmp = new ArrayList<Item<U>>(2);

        if (this.seconds != 0) {
            Object u = ((this.scale == UTC) ? SI.SECONDS : TimeUnit.SECONDS);
            U unit = cast(u);
            tmp.add(Item.of(Math.abs(this.seconds), unit));
        }

        if (this.nanos > 0) {
            Object u =
                ((this.scale == UTC) ? SI.NANOSECONDS : TimeUnit.NANOSECONDS);
            U unit = cast(u);
            tmp.add(Item.of(Math.abs(this.nanos), unit));
        }

        return Collections.unmodifiableList(tmp);

    }

    @Override
    public boolean contains(Object unit) {

        if (
            ((this.scale == POSIX) && TimeUnit.SECONDS.equals(unit))
            || ((this.scale == UTC) && SI.SECONDS.equals(unit))
        ) {
            return (this.seconds != 0);
        } else if (
            ((this.scale == POSIX) && TimeUnit.NANOSECONDS.equals(unit))
            || ((this.scale == UTC) && SI.NANOSECONDS.equals(unit))
        ) {
            return (this.nanos > 0);
        }

        return false;

    }

    @Override
    public long getPartialAmount(Object unit) {

        if (
            ((this.scale == POSIX) && TimeUnit.SECONDS.equals(unit))
            || ((this.scale == UTC) && SI.SECONDS.equals(unit))
        ) {
            return Math.abs(this.seconds);
        } else if (
            ((this.scale == POSIX) && TimeUnit.NANOSECONDS.equals(unit))
            || ((this.scale == UTC) && SI.NANOSECONDS.equals(unit))
        ) {
            return this.nanos;
        }

        return 0;

    }

    @Override
    public boolean isNegative() {

        return (this.seconds < 0);

    }

    @Override
    public boolean isPositive() {

        return (
            (this.seconds > 0)
            || ((this.seconds == 0) && (this.nanos > 0)));

    }

    @Override
    public boolean isEmpty() {

        return (this.seconds == 0) && (this.nanos == 0);

    }

    /**
     * <p>Add given temporal amount to this machine time. </p>
     *
     * @param   amount  the amount to be added
     * @param   unit    the related time unit
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Addiert den angegebenen Zeitbetrag zu dieser maschinellen Dauer. </p>
     *
     * @param   amount  the amount to be added
     * @param   unit    the related time unit
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public MachineTime<U> plus(
        long amount,
        U unit
    ) {

        long s = this.seconds;
        int f = this.nanos;

        if (this.scale == TimeScale.POSIX) {
            TimeUnit u = TimeUnit.class.cast(unit);

            if (u.compareTo(TimeUnit.SECONDS) >= 0) {
                s =
                    MathUtils.safeAdd(
                        s,
                        MathUtils.safeMultiply(
                            amount,
                            TimeUnit.SECONDS.convert(1, u))
                    );
            } else {
                long total =
                    MathUtils.safeAdd(
                        f,
                        MathUtils.safeMultiply(
                            amount,
                            TimeUnit.NANOSECONDS.convert(1, u))
                    );
                s = MathUtils.safeAdd(s, MathUtils.floorDivide(total, MRD));
                f = MathUtils.floorModulo(total, MRD);
            }
        } else {
            switch (SI.class.cast(unit)) {
                case SECONDS:
                    s = MathUtils.safeAdd(s, amount);
                    break;
                case NANOSECONDS:
                    long total = MathUtils.safeAdd(f, amount);
                    s = MathUtils.safeAdd(s, MathUtils.floorDivide(total, MRD));
                    f = MathUtils.floorModulo(total, MRD);
                    break;
                default:
                    throw new UnsupportedOperationException(unit.toString());
            }
        }

        return new MachineTime<U>(s, f, this.scale);

    }

    /**
     * <p>Add given temporal amount to this machine time. </p>
     *
     * @param   duration    other machine time to be added
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Addiert den angegebenen Zeitbetrag zu dieser maschinellen Dauer. </p>
     *
     * @param   duration    other machine time to be added
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public MachineTime<U> plus(MachineTime<U> duration) {

        if (duration.isEmpty()) {
            return this;
        } else if (this.isEmpty()) {
            return duration;
        }

        long s = MathUtils.safeAdd(this.seconds, duration.seconds);
        int f = this.nanos + duration.nanos;
        return new MachineTime<U>(s, f, this.scale);

    }

    /**
     * <p>Subtracts given temporal amount from this machine time. </p>
     *
     * @param   amount  the amount to be subtracted
     * @param   unit    the related time unit
     * @return  difference result
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Subtrahiert den angegebenen Zeitbetrag von dieser maschinellen
     * Dauer. </p>
     *
     * @param   amount  the amount to be subtracted
     * @param   unit    the related time unit
     * @return  difference result
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public MachineTime<U> minus(
        long amount,
        U unit
    ) {

        return this.plus(MathUtils.safeNegate(amount), unit);

    }

    /**
     * <p>Subtracts given temporal amount from this machine time. </p>
     *
     * @param   duration    other machine time to be subtracted
     * @return  difference result
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Subtrahiert den angegebenen Zeitbetrag von dieser maschinellen
     * Dauer. </p>
     *
     * @param   duration    other machine time to be subtracted
     * @return  difference result
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public MachineTime<U> minus(MachineTime<U> duration) {

        if (duration.isEmpty()) {
            return this;
        } else if (this.isEmpty()) {
            return duration.inverse();
        }

        long s = MathUtils.safeSubtract(this.seconds, duration.seconds);
        int f = this.nanos - duration.nanos;
        return new MachineTime<U>(s, f, this.scale);

    }

    /**
     * <p>Converts this machine duration to its absolute amount. </p>
     *
     * @return  absolute machine time duration, always non-negative
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Wandelt eine maschinelle Dauer in ihren Absolutbetrag um. </p>
     *
     * @return  absolute machine time duration, always non-negative
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public MachineTime<U> abs() {

        if (this.isNegative()) {
            return new MachineTime<U>(
                MathUtils.safeNegate(this.seconds), -this.nanos, this.scale);
        } else {
            return this;
        }

    }

    /**
     * <p>Creates a copy with inversed sign. </p>
     *
     * @return  negated machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    /*[deutsch]
     * <p>Wandelt eine maschinelle Dauer in ihr negatives &Auml;quivalent
     * um. </p>
     *
     * @return  negated machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   1.3
     */
    public MachineTime<U> inverse() {

        if (this.isEmpty()) {
            return this;
        }

        return new MachineTime<U>(
            MathUtils.safeNegate(this.seconds), -this.nanos, this.scale);

    }

    /**
     * <p>Multiplies this duration with given factor. </p>
     *
     * @param   factor  multiplicand
     * @return  changed copy of this duration
     */
    /*[deutsch]
     * <p>Multipliziert diese Dauer mit dem angegebenen Faktor. </p>
     *
     * @param   factor  multiplicand
     * @return  changed copy of this duration
     */
    public MachineTime<U> multipliedBy(long factor) {

        if (factor == 1) {
            return this;
        }

        BigDecimal value =
            this.toBigDecimal().multiply(BigDecimal.valueOf(factor));
        MachineTime<?> mt;

        if (this.scale == TimeScale.POSIX) {
            mt = MachineTime.ofPosixSeconds(value);
        } else {
            mt = MachineTime.ofSISeconds(value);
        }

        return cast(mt);

    }

    /**
     * <p>Divides this duration by given divisor using rounding
     * mode {@code HALF_UP}. </p>
     *
     * @param   divisor     divisor
     * @return  changed copy of this duration
     * @see     RoundingMode#HALF_UP
     */
    /*[deutsch]
     * <p>Dividiert diese Dauer durch den angegebenen Teiler und
     * benutzt die kaufm&auml;nnische Rundung. </p>
     *
     * @param   divisor     Teiler
     * @return  ge&auml;nderte Kopie dieser Dauer
     * @see     RoundingMode#HALF_UP
     */
    public MachineTime<U> dividedBy(long divisor) {

        if (divisor == 1) {
            return this;
        }

        BigDecimal value =
            this.toBigDecimal()
                .setScale(9, RoundingMode.FLOOR)
                .divide(new BigDecimal(divisor), RoundingMode.HALF_UP);
        MachineTime<?> mt;

        if (this.scale == TimeScale.POSIX) {
            mt = MachineTime.ofPosixSeconds(value);
        } else {
            mt = MachineTime.ofSISeconds(value);
        }

        return cast(mt);

    }

    @Override
    public <T extends TimePoint<? super U, T>> T addTo(T time) {

        if (this.scale == POSIX) {
            U s = cast(TimeUnit.SECONDS);
            U f = cast(TimeUnit.NANOSECONDS);
            return time.plus(this.seconds, s).plus(this.nanos, f);
        }
        
        Object t = time;
        
        if (t instanceof Moment) {
            Moment moment = (Moment) t;
            Moment result =
                moment.plus(this.seconds, SI.SECONDS)
                      .plus(this.nanos, SI.NANOSECONDS);
            return cast(result);
        }

        throw new UnsupportedOperationException(
            "Use 'Moment.plus(MachineTime<SI>)' instead.");

    }

    @Override
    public <T extends TimePoint<? super U, T>> T subtractFrom(T time) {

        if (this.scale == POSIX) {
            U s = cast(TimeUnit.SECONDS);
            U f = cast(TimeUnit.NANOSECONDS);
            return time.minus(this.seconds, s).minus(this.nanos, f);
        }
        
        Object t = time;
        
        if (t instanceof Moment) {
            Moment moment = (Moment) t;
            Moment result =
                moment.minus(this.seconds, SI.SECONDS)
                      .minus(this.nanos, SI.NANOSECONDS);
            return cast(result);
        }

        throw new UnsupportedOperationException(
            "Use 'Moment.minus(MachineTime<SI>)' instead.");

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof MachineTime) {
            MachineTime<?> that = (MachineTime<?>) obj;
            return (
                (this.seconds == that.seconds)
                && (this.nanos == that.nanos)
                && (this.scale == that.scale));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        int hash = 7;
        hash = 23 * hash + (int) (this.seconds ^ (this.seconds >>> 32));
        hash = 23 * hash + this.nanos;
        hash = 23 * hash + this.scale.hashCode();
        return hash;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        this.createNumber(sb);
        sb.append("s [");
        sb.append(this.scale.name());
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Converts this machine time duration into a decimal number of
     * seconds. </p>
     *
     * @return  BigDecimal
     */
    /*[deutsch]
     * <p>Wandelt diese maschinelle Dauer in einen dezimalen Sekundenbetrag
     * um. </p>
     *
     * @return  BigDecimal
     */
    public BigDecimal toBigDecimal() {

        StringBuilder sb = new StringBuilder();
        this.createNumber(sb);
        return new BigDecimal(sb.toString());

    }

    private void createNumber(StringBuilder sb) {

        if (this.isNegative()) {
            sb.append('-');
            long s = this.seconds;
            if (this.nanos > 0) {
                s++;
            }
            sb.append(Math.abs(s));
        } else {
            sb.append(this.seconds);
        }

        if (this.nanos > 0) {
            sb.append('.');
            int f = this.nanos;
            if (this.isNegative()) {
                f = MRD - f;
            }
            String fraction = String.valueOf(f);
            for (int i = 9 - fraction.length(); i > 0; i--) {
                sb.append('0');
            }
            sb.append(fraction);
        }

    }

    @SuppressWarnings("unchecked")
    private static <U> U cast(Object unit) {

        return (U) unit;

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The layout
     *              is bit-compressed. The first byte contains within the
     *              four most significant bits the type id {@code 7} and as
     *              least significant bit the value 1 if this instance uses
     *              the UTC-scale. Then the bytes for the seconds and fraction
     *              follow. The fraction bytes are only written if the fraction
     *              is not zero. In that case, the second least significant bit
     *              of the header is set, too.
     *
     * Schematic algorithm:
     *
     * <pre>
     *      byte header = (7 << 4);
     *      if (scale == TimeScale.UTC) header |= 1;
     *      if (this.getFraction() > 0) header |= 2;
     *      out.writeByte(header);
     *      out.writeLong(getSeconds());
     *      if (this.getFraction() > 0) {
     *          out.writeInt(getFraction());
     *      }
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.MACHINE_TIME_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Metric<U>
        implements TimeMetric<TimeUnit, MachineTime<U>> {

        //~ Instanzvariablen ----------------------------------------------

        private final TimeScale scale;

        //~ Konstruktoren -------------------------------------------------

        private Metric(TimeScale scale) {
            super();

            this.scale = scale;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public
        <T extends TimePoint<? super TimeUnit, T>> MachineTime<U> between(
            T start,
            T end
        ) {

            long secs;
            int nanos;

            if (
                (this.scale == UTC)
                && (start instanceof UniversalTime)
            ) {
                UniversalTime t1 = (UniversalTime) start;
                UniversalTime t2 = (UniversalTime) end;
                secs = t2.getElapsedTime(UTC) - t1.getElapsedTime(UTC);
                nanos = t2.getNanosecond(UTC) - t1.getNanosecond(UTC);
            } else if (start instanceof UnixTime) {
                UnixTime t1 = (UnixTime) start;
                UnixTime t2 = (UnixTime) end;
                secs = t2.getPosixTime() - t1.getPosixTime();
                nanos = t2.getNanosecond() - t1.getNanosecond();
            } else {
                throw new UnsupportedOperationException(
                    "Machine time requires objects of type 'UnixTime'.");
            }

            return new MachineTime<U>(secs, nanos, this.scale);

        }

    }

}
