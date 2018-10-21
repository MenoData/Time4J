/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.format.TimeSpanFormatter;
import net.time4j.scale.TimeScale;
import net.time4j.scale.UniversalTime;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * @since   5.0
 * @see     TimeUnit#SECONDS
 * @see     TimeUnit#NANOSECONDS
 * @see     SI#SECONDS
 * @see     SI#NANOSECONDS
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
 * @since   5.0
 * @see     TimeUnit#SECONDS
 * @see     TimeUnit#NANOSECONDS
 * @see     SI#SECONDS
 * @see     SI#NANOSECONDS
 */
public final class MachineTime<U>
    implements TimeSpan<U>, Comparable<MachineTime<U>>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MRD = 1000000000;

    private static final MachineTime<TimeUnit> POSIX_ZERO =
            new MachineTime<TimeUnit>(0, 0, POSIX);
    private static final MachineTime<SI> UTC_ZERO =
            new MachineTime<SI>(0, 0, UTC);

    /**
     * Metric on the POSIX scale (without leap seconds).
     *
     * @since   2.0
     */
    /*[deutsch]
     * Metrik auf der POSIX-Skala (ohne Schaltsekunden).
     *
     * @since   2.0
     */
    public static final TimeMetric<TimeUnit, MachineTime<TimeUnit>> ON_POSIX_SCALE =
            new Metric<TimeUnit>(POSIX);

    /**
     * <p>Metric on the UTC scale (inclusive leap seconds). </p>
     * 
     * <p>Time points before 1972 are not supported. </p>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Metrik auf der UTC-Skala (inklusive Schaltsekunden). </p>
     *
     * <p>Zeitpunkte vor 1972 werden nicht unterst&uuml;tzt. </p>
     *
     * @since   2.0
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

        if ((secs < 0) && (fraction > 0)) {
            secs++;
            fraction -= MRD;
        }

        this.seconds = secs;
        this.nanos = fraction;
        this.scale = scale;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a machine time duration on the POSIX scale. </p>
     *
     * @param   amount of units
     * @param   unit    helps to interprete given amount
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der POSIX-Skala. </p>
     *
     * @param   amount of units
     * @param   unit    helps to interprete given amount
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
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
     * @param   amount of units
     * @param   unit    helps to interprete given amount
     * @return  new machine time duration
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * @param   amount of units
     * @param   unit    helps to interprete given amount
     * @return  new machine time duration
     * @since   2.0
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
     * @param   seconds     POSIX-seconds
     * @param   fraction    nanosecond part
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der POSIX-Skala. </p>
     *
     * @param   seconds     POSIX-seconds
     * @param   fraction    nanosecond part
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    public static MachineTime<TimeUnit> ofPosixUnits(
        long seconds,
        int fraction
    ) {

        if ((seconds == 0) && (fraction == 0)) {
            return POSIX_ZERO;
        }

        return new MachineTime<TimeUnit>(seconds, fraction, POSIX);

    }

    /**
     * <p>Creates a machine time duration on the UTC scale. </p>
     *
     * @param   seconds     SI-seconds
     * @param   fraction    nanosecond part
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * @param   seconds     SI-seconds
     * @param   fraction    nanosecond part
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    public static MachineTime<SI> ofSIUnits(
        long seconds,
        int fraction
    ) {

        if ((seconds == 0) && (fraction == 0)) {
            return UTC_ZERO;
        }

        return new MachineTime<SI>(seconds, fraction, UTC);

    }

    /**
     * <p>Creates a machine time duration on the POSIX scale. </p>
     *
     * @param   seconds     decimal POSIX-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  IllegalArgumentException if the argument is infinite or NaN
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der POSIX-Skala. </p>
     *
     * @param   seconds     decimal POSIX-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  IllegalArgumentException if the argument is infinite or NaN
     * @since   2.0
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
     * <p>Creates a machine time duration on the POSIX scale. </p>
     *
     * @param   seconds     decimal POSIX-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der POSIX-Skala. </p>
     *
     * @param   seconds     decimal POSIX-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
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
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  IllegalArgumentException if the argument is infinite or NaN
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  IllegalArgumentException if the argument is infinite or NaN
     * @since   2.0
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
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Dauer als Maschinenzeit auf der UTC-Skala. </p>
     *
     * @param   seconds     decimal SI-seconds
     * @return  new machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
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
     * <p>Yields the normalized seconds of this duration. </p>
     *
     * <p>The normalization happens in case of a negative duration such that any fraction part
     * falls into the range {@code 0-999999999}. In this case, following expression is NOT true:
     * {@code Math.abs(getSeconds()) == getPartialAmount(TimeUnit.SECONDS)} </p>
     *
     * @return  long
     * @see     #getFraction()
     */
    /*[deutsch]
     * <p>Liefert die normalisierten Sekunden dieser Dauer. </p>
     *
     * <p>Die Normalisierung geschieht im Fall einer negativen Dauer so, da&szlig; ein Sekundenbruchteil
     * immer in den Bereich {@code 0-999999999} f&auml;llt. In diesem Fall ist folgender Ausdruck NICHT
     * wahr: {@code Math.abs(getSeconds()) == getPartialAmount(TimeUnit.SECONDS)} </p>
     *
     * @return  long
     * @see     #getFraction()
     */
    public long getSeconds() {

        long secs = this.seconds;

        if (this.nanos < 0) {
            secs--;
        }

        return secs;

    }

    /**
     * <p>Yields the normalized nanosecond fraction of this duration. </p>
     *
     * @return  nanosecond in range {@code 0-999999999}
     * @see     #getSeconds()
     */
    /*[deutsch]
     * <p>Liefert den normalisierten Nanosekundenteil dieser Dauer. </p>
     *
     * @return  nanosecond in range {@code 0-999999999}
     * @see     #getSeconds()
     */
    public int getFraction() {

        int n = this.nanos;

        if (n < 0) {
            n += MRD;
        }

        return n;

    }

    /**
     * <p>Yields the related time scale. </p>
     *
     * @return  either {@code TimeScale.POSIX} or {@code TimeScale.UTC}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitskala. </p>
     *
     * @return  either {@code TimeScale.POSIX} or {@code TimeScale.UTC}
     * @since   2.0
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

        if (this.nanos != 0) {
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
            return (this.nanos != 0);
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
            return Math.abs(this.nanos);
        }

        return 0;

    }

    @Override
    public boolean isNegative() {

        return ((this.seconds < 0) || (this.nanos < 0));

    }

    @Override
    public boolean isPositive() {

        return ((this.seconds > 0) || (this.nanos > 0));

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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Addiert den angegebenen Zeitbetrag zu dieser maschinellen Dauer. </p>
     *
     * @param   amount  the amount to be added
     * @param   unit    the related time unit
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    public MachineTime<U> plus(
        long amount,
        U unit
    ) {

        long s = this.seconds;
        int f = this.nanos;

        if (this.scale == POSIX) {
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Addiert den angegebenen Zeitbetrag zu dieser maschinellen Dauer. </p>
     *
     * @param   duration    other machine time to be added
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Subtrahiert den angegebenen Zeitbetrag von dieser maschinellen
     * Dauer. </p>
     *
     * @param   amount  the amount to be subtracted
     * @param   unit    the related time unit
     * @return  difference result
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    public MachineTime<U> minus(
        long amount,
        U unit
    ) {

        return this.plus(negateExact(amount), unit);

    }

    /**
     * <p>Subtracts given temporal amount from this machine time. </p>
     *
     * @param   duration    other machine time to be subtracted
     * @return  difference result
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    /*[deutsch]
     * <p>Subtrahiert den angegebenen Zeitbetrag von dieser maschinellen
     * Dauer. </p>
     *
     * @param   duration    other machine time to be subtracted
     * @return  difference result
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Wandelt eine maschinelle Dauer in ihren Absolutbetrag um. </p>
     *
     * @return  absolute machine time duration, always non-negative
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    public MachineTime<U> abs() {

        if (this.isNegative()) {
            return new MachineTime<U>(negateExact(this.seconds), -this.nanos, this.scale);
        } else {
            return this;
        }

    }

    /**
     * <p>Creates a copy with inversed sign. </p>
     *
     * @return  negated machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    /*[deutsch]
     * <p>Wandelt eine maschinelle Dauer in ihr negatives &Auml;quivalent
     * um. </p>
     *
     * @return  negated machine time duration
     * @throws  ArithmeticException in case of numerical overflow
     * @since   2.0
     */
    public MachineTime<U> inverse() {

        if (this.isEmpty()) {
            return this;
        }

        return new MachineTime<U>(negateExact(this.seconds), -this.nanos, this.scale);

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
        } else if (factor == 0) {
            if (this.scale == POSIX) {
                return cast(POSIX_ZERO);
            } else {
                return cast(UTC_ZERO);
            }
        }

        BigDecimal value =
            this.toBigDecimal().multiply(BigDecimal.valueOf(factor));
        MachineTime<?> mt;

        if (this.scale == POSIX) {
            mt = MachineTime.ofPosixSeconds(value);
        } else {
            mt = MachineTime.ofSISeconds(value);
        }

        return cast(mt);

    }

    /**
     * <p>Multiplies this duration with given decimal factor. </p>
     *
     * <p>If more rounding control is needed then users might consider the alternatives
     * {@link #multipliedBy(long)} and {@link #dividedBy(long, RoundingMode)}. </p>
     *
     * @param   factor  multiplicand
     * @return  changed copy of this duration
     * @throws  IllegalArgumentException if given factor is not a finite number
     */
    /*[deutsch]
     * <p>Multipliziert diese Dauer mit dem angegebenen Dezimalfaktor. </p>
     *
     * <p>Wenn mehr Kontrolle &uuml;ber das Rundungsverfahren gebraucht wird, gibt es die
     * Alternativen {@link #multipliedBy(long)} und {@link #dividedBy(long, RoundingMode)}. </p>
     *
     * @param   factor  multiplicand
     * @return  changed copy of this duration
     * @throws  IllegalArgumentException if given factor is not a finite number
     */
    public MachineTime<U> multipliedBy(double factor) {

        if (factor == 1.0) {
            return this;
        } else if (factor == 0.0) {
            if (this.scale == POSIX) {
                return cast(POSIX_ZERO);
            } else {
                return cast(UTC_ZERO);
            }
        } else if (!Double.isInfinite(factor) && !Double.isNaN(factor)) {
            double len = this.toBigDecimal().doubleValue() * factor;
            MachineTime<?> mt;
            if (this.scale == POSIX) {
                mt = MachineTime.ofPosixSeconds(len);
            } else {
                mt = MachineTime.ofSISeconds(len);
            }
            return cast(mt);
        } else {
            throw new IllegalArgumentException("Not finite: " + factor);
        }

    }

    /**
     * <p>Divides this duration by given divisor using given rounding mode. </p>
     *
     * @param   divisor         divisor
     * @param   roundingMode    rounding mode to be used in division
     * @return  changed copy of this duration
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Dividiert diese Dauer durch den angegebenen Teiler und benutzt die angegebene Rundung. </p>
     *
     * @param   divisor         divisor
     * @param   roundingMode    rounding mode to be used in division
     * @return  changed copy of this duration
     * @since   3.23/4.19
     */
    public MachineTime<U> dividedBy(
        long divisor,
        RoundingMode roundingMode
    ) {

        if (divisor == 1) {
            return this;
        }

        BigDecimal value =
            this.toBigDecimal().setScale(9, RoundingMode.FLOOR).divide(new BigDecimal(divisor), roundingMode);
        MachineTime<?> mt;

        if (this.scale == POSIX) {
            mt = MachineTime.ofPosixSeconds(value);
        } else {
            mt = MachineTime.ofSISeconds(value);
        }

        return cast(mt);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TimePoint<? super U, T>> T addTo(T time) {

        U s, f;

        if (this.scale == POSIX) {
            s = (U) TimeUnit.SECONDS;
            f = (U) TimeUnit.NANOSECONDS;
        } else {
            s = (U) SI.SECONDS;
            f = (U) SI.NANOSECONDS;
        }

        return time.plus(this.seconds, s).plus(this.nanos, f);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TimePoint<? super U, T>> T subtractFrom(T time) {

        U s, f;

        if (this.scale == POSIX) {
            s = (U) TimeUnit.SECONDS;
            f = (U) TimeUnit.NANOSECONDS;
        } else {
            s = (U) SI.SECONDS;
            f = (U) SI.NANOSECONDS;
        }

        return time.minus(this.seconds, s).minus(this.nanos, f);

    }

    /**
     * <p>Compares the absolute lengths and is equivalent to {@code abs().compareTo(other.abs()) < 0}. </p>
     *
     * @param   other   another machine time to be compared with
     * @return  boolean
     * @see     #compareTo(MachineTime)
     * @see     #isLongerThan(MachineTime)
     * @since   3.20/4.16
     */
    /*[deutsch]
     * <p>Vergleicht die absoluten L&auml;ngen und ist &auml;quivalent zu {@code abs().compareTo(other.abs()) < 0}. </p>
     *
     * @param   other   another machine time to be compared with
     * @return  boolean
     * @see     #compareTo(MachineTime)
     * @see     #isLongerThan(MachineTime)
     * @since   3.20/4.16
     */
    public boolean isShorterThan(MachineTime<U> other) {

        return (this.abs().compareTo(other.abs()) < 0);

    }

    /**
     * <p>Compares the absolute lengths and is equivalent to {@code abs().compareTo(other.abs()) > 0}. </p>
     *
     * @param   other   another machine time to be compared with
     * @return  boolean
     * @see     #compareTo(MachineTime)
     * @see     #isShorterThan(MachineTime)
     * @since   3.20/4.16
     */
    /*[deutsch]
     * <p>Vergleicht die absoluten L&auml;ngen und ist &auml;quivalent zu {@code abs().compareTo(other.abs()) > 0}. </p>
     *
     * @param   other   another machine time to be compared with
     * @return  boolean
     * @see     #compareTo(MachineTime)
     * @see     #isShorterThan(MachineTime)
     * @since   3.20/4.16
     */
    public boolean isLongerThan(MachineTime<U> other) {

        return (this.abs().compareTo(other.abs()) > 0);

    }

    /**
     * <p>Method of the {@code Comparable}-interface. </p>
     *
     * @param   other   another machine time to be compared with
     * @return  negative, zero or positive integer if this instance is shorter, equal or longer than other one
     * @throws  ClassCastException if this and the other machine time have different time scales
     * @see     #isShorterThan(MachineTime)
     * @see     #isLongerThan(MachineTime)
     * @since   3.20/4.16
     */
    /*[deutsch]
     * <p>Methode des {@code Comparable}-Interface. </p>
     *
     * @param   other   another machine time to be compared with
     * @return  negative, zero or positive integer if this instance is shorter, equal or longer than other one
     * @throws  ClassCastException if this and the other machine time have different time scales
     * @see     #isShorterThan(MachineTime)
     * @see     #isLongerThan(MachineTime)
     * @since   3.20/4.16
     */
    @Override
    public int compareTo(MachineTime<U> other) {

        if (this.scale == other.scale) {
            if (this.seconds < other.seconds) {
                return -1;
            } else if (this.seconds > other.seconds) {
                return 1;
            } else {
                return (this.nanos - other.nanos);
            }
        } else {
            throw new ClassCastException("Different time scales.");
        }

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

    /**
     * <p>Returns a format in technical notation including the name of the underlying time scale. </p>
     *
     * @return  String like &quot;-5s [POSIX]&quot; or &quot;4.123456789s [UTC]&quot;
     */
    /*[deutsch]
     * <p>Returns a format in technical notation including the name of the underlying time scale. </p>
     *
     * @return  String like &quot;-5s [POSIX]&quot; or &quot;4.123456789s [UTC]&quot;
     */
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
     * <p>Converts this machine time duration into a decimal number of seconds. </p>
     *
     * @return  BigDecimal
     */
    /*[deutsch]
     * <p>Wandelt diese maschinelle Dauer in einen dezimalen Sekundenbetrag um. </p>
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
            sb.append(Math.abs(this.seconds));
        } else {
            sb.append(this.seconds);
        }

        if (this.nanos != 0) {
            sb.append('.');
            String fraction = String.valueOf(Math.abs(this.nanos));
            for (int i = 9 - fraction.length(); i > 0; i--) {
                sb.append('0');
            }
            sb.append(fraction);
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

    private static long negateExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("Long overflow.");
        }
        return -a;
    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The layout
     *              is bit-compressed. The first byte contains within the
     *              four most significant bits the type id {@code 5} and as
     *              least significant bit the value 1 if this instance uses
     *              the UTC-scale. Then the bytes for the seconds and fraction
     *              follow. The fraction bytes are only written if the fraction
     *              is not zero. In that case, the second least significant bit
     *              of the header is set, too.
     *
     * Schematic algorithm:
     *
     * <pre>
     *      byte header = (5 &lt;&lt; 4);
     *      if (scale == TimeScale.UTC) header |= 1;
     *      if (this.getFraction() &gt; 0) header |= 2;
     *      out.writeByte(header);
     *      out.writeLong(getSeconds());
     *      if (this.getFraction() &gt; 0) {
     *          out.writeInt(getFraction());
     *      }
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.MACHINE_TIME_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Non-localized and user-defined format for machine-time-durations based on a
     * pattern containing some standard symbols and literals. </p>
     *
     * <p>Example (printing a wall-time-like duration): </p>
     *
     * <pre>
     *  MachineTime.Formatter f =
     *      MachineTime.Formatter.ofPattern(&quot;+hh:mm:ss&quot;);
     *  String s = f.print(MachineTime.of(27 * 3600 + 30 * 60 + 5, TimeUnit.SECONDS));
     *  System.out.println(s); // output: +27:30:05
     * </pre>
     *
     * @see     #ofPattern(String)
     * @since   3.26/4.22
     */
    /*[deutsch]
     * <p>Nicht-lokalisiertes benutzerdefiniertes Dauerformat, das auf
     * Symbolmustern beruht. </p>
     *
     * <p>Beispiel (Ausgabe einer uhrzeit&auml;hnlichen Dauer): </p>
     *
     * <pre>
     *  MachineTime.Formatter f =
     *      MachineTime.Formatter.ofPattern(&quot;+hh:mm:ss&quot;);
     *  String s = f.print(MachineTime.of(27 * 3600 + 30 * 60 + 5, TimeUnit.SECONDS));
     *  System.out.println(s); // Ausgabe: +27:30:05
     * </pre>
     *
     * @see     #ofPattern(String)
     * @since   3.26/4.22
     */
    public static final class Formatter
        extends TimeSpanFormatter<TimeUnit, MachineTime<TimeUnit>> {

        //~ Konstruktoren -------------------------------------------------

        private Formatter(String pattern) {
            super(TimeUnit.class, pattern);

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Constructs a new instance of duration formatter. </p>
         *
         * <p>Uses a pattern with symbols as followed: <br>&nbsp;</p>
         *
         * <table border="1">
         *  <caption>Legend</caption>
         *  <tr><th>Symbol</th><th>Description</th></tr>
         *  <tr><td>+</td><td>sign of duration, printing + or -</td></tr>
         *  <tr><td>-</td><td>sign of duration, printing only -</td></tr>
         *  <tr><td>D</td><td>{@link TimeUnit#DAYS}</td></tr>
         *  <tr><td>h</td><td>{@link TimeUnit#HOURS}</td></tr>
         *  <tr><td>m</td><td>{@link TimeUnit#MINUTES}</td></tr>
         *  <tr><td>s</td><td>{@link TimeUnit#SECONDS}</td></tr>
         *  <tr><td>,</td><td>decimal separator, comma is preferred</td></tr>
         *  <tr><td>.</td><td>decimal separator, dot is preferred</td></tr>
         *  <tr><td>f</td>
         *    <td>{@link TimeUnit#NANOSECONDS} as fraction, (1-9) chars</td></tr>
         *  <tr><td>'</td><td>apostroph, for escaping literal chars</td></tr>
         *  <tr><td>[]</td><td>optional section</td></tr>
         *  <tr><td>{}</td><td>section with plural forms</td></tr>
         *  <tr><td>#</td><td>placeholder for an optional digit</td></tr>
         *  <tr><td>|</td><td>joins two parsing sections by or-logic</td></tr>
         * </table>
         *
         * <p>All letters in range a-z and A-Z are always reserved chars
         * and must be escaped by apostrophes for interpretation as literals.
         * If such a letter is repeated then the count of symbols controls
         * the minimum width for formatted output. Such a minimum width also
         * reserves this area for parsing of any preceding item. If necessary a
         * number (of units) will be padded from left with the zero digit. The
         * unit symbol (with exception of &quot;f&quot;) can be preceded by
         * any count of char &quot;#&quot; (&gt;= 0). The sum of min width and
         * count of #-chars define the maximum width for formatted output and
         * parsing. </p>
         *
         * <p><strong>Optional sections</strong></p>
         *
         * <p>Optional sections enclosed by square brackets let the parser be error-tolerant
         * and continue with the next section in case of errors. During printing, an
         * optional section will only be printed if there is any non-zero part. When parsing
         * an optional section will be skipped if the input to be parsed does not match the
         * expected pattern. For example: An input missing the hour part can be handled when
         * an optional section is applied on the hour part. </p>
         *
         * <p><strong>Plural forms</strong></p>
         *
         * <p>Every expression inside curly brackets represents a combination
         * of amount, separator and pluralized unit name and has following
         * syntax: </p>
         *
         * <p>{[symbol]:[separator]:[locale]:[CATEGORY=LITERAL][:...]}</p>
         *
         * <p>The symbol is one of following chars:
         * D, h, m, s, f (legend see table above)</p>
         *
         * <p>Afterwards the definition of separator chars follows. The
         * empty separator (represented by zero space between colons) is
         * permitted, too. The next section denotes the locale necessary
         * for determination of suitable plural rules. The form
         * [language]-[country]-[variant] can be used, for example
         * &quot;en-US&quot; or &quot;en_US&quot;. At least the language
         * must be present. The underscore is an acceptable alternative
         * for the minus-sign. Finally there must be a sequence of
         * name-value-pairs in the form CATEGORY=LITERAL. Every category
         * label must be the name of a {@link net.time4j.format.PluralCategory plural category}.
         * The category OTHER must exist. Example: </p>
         *
         * <pre>
         *  MachineTime.Formatter formatter =
         *      MachineTime.Formatter.ofPattern(&quot;{D: :en:ONE=day:OTHER=days}&quot;);
         *  String s = formatter.format(MachineTime.of(3, TimeUnit.DAYS));
         *  System.out.println(s); // output: 3 days
         * </pre>
         *
         * <p><strong>Numerical placeholders</strong></p>
         *
         * <p>The maximum numerical width is the sum of min width and the count of preceding #-chars. Example: </p>
         *
         * <pre>
         *  MachineTime.Formatter formatter1 =
         *      MachineTime.Formatter.ofPattern(&quot;D&quot;);
         *  formatter1.format(MachineTime.of(123, TimeUnit.DAYS)); throws IllegalArgumentException
         *
         *  MachineTime.Formatter formatter2 =
         *      MachineTime.Formatter.ofPattern(&quot;##D&quot;);
         *  String s = formatter2.format(MachineTime.of(123, TimeUnit.DAYS));
         *  System.out.println(s); // output: 123
         * </pre>
         *
         * <p><strong>Or-logic</strong></p>
         *
         * <p>The character &quot;|&quot; starts a new section which will not be used for printing
         * but parsing in case of preceding errors. For example, following pattern enables parsing
         * a duration in days for two different languages: </p>
         *
         * <pre>
         *  &quot;{D: :en:ONE=day:OTHER=days}|{D: :de:ONE=Tag:OTHER=Tage}&quot;
         * </pre>
         *
         * @param   pattern format pattern
         * @return  new formatter instance
         * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
         */
        /*[deutsch]
         * <p>Konstruiert eine neue Formatinstanz. </p>
         *
         * <p>Benutzt ein Formatmuster mit Symbolen wie folgt: <br>&nbsp;</p>
         *
         * <table border="1">
         *  <caption>Legende</caption>
         *  <tr><th>Symbol</th><th>Beschreibung</th></tr>
         *  <tr><td>+</td><td>Vorzeichen der Dauer, gibt + oder - aus</td></tr>
         *  <tr><td>-</td><td>Vorzeichen der Dauer, gibt nur - aus</td></tr>
         *  <tr><td>D</td><td>{@link TimeUnit#DAYS}</td></tr>
         *  <tr><td>h</td><td>{@link TimeUnit#HOURS}</td></tr>
         *  <tr><td>m</td><td>{@link TimeUnit#MINUTES}</td></tr>
         *  <tr><td>s</td><td>{@link TimeUnit#SECONDS}</td></tr>
         *  <tr><td>,</td><td>Dezimaltrennzeichen, vorzugsweise Komma</td></tr>
         *  <tr><td>.</td><td>Dezimaltrennzeichen, vorzugsweise Punkt</td></tr>
         *  <tr><td>f</td>
         *    <td>{@link TimeUnit#NANOSECONDS} als Bruchteil, (1-9) Zeichen</td></tr>
         *  <tr><td>'</td><td>Apostroph, zum Markieren von Literalen</td></tr>
         *  <tr><td>[]</td><td>optionaler Abschnitt</td></tr>
         *  <tr><td>{}</td><td>Abschnitt mit Pluralformen</td></tr>
         *  <tr><td>#</td><td>numerischer Platzhalter</td></tr>
         *  <tr><td>|</td><td>verbindet zwei Abschnitte per oder-Logik</td></tr>
         * </table>
         *
         * <p>Alle Buchstaben im Bereich a-z und A-Z sind grunds&auml;tzlich
         * reservierte Zeichen und m&uuml;ssen als Literale in Apostrophe
         * gefasst werden. Wird ein Buchstabensymbol mehrfach wiederholt,
         * dann regelt die Anzahl der Symbole die Mindestbreite in der formatierten
         * Ausgabe. Solch eine Mindestbreite reserviert auch das zugeh&ouml;rige Element,
         * wenn vorangehende Dauerelemente interpretiert werden. Bei Bedarf wird eine
         * Zahl (von Einheiten) von links mit der Nullziffer aufgef&uuml;llt. Ein
         * Einheitensymbol kann eine beliebige Zahl von numerischen Platzhaltern
         * &quot;#&quot; vorangestellt haben (&gt;= 0). Die Summe aus minimaler Breite
         * und der Anzahl der #-Zeichen definiert die maximale Breite, die ein
         * Dauerelement numerisch haben darf. </p>
         *
         * <p><strong>Optionale Sektionen</strong></p>
         *
         * <p>Optionale Abschnitte, die durch eckige Klammern definiert sind, regeln, da&szlig; der
         * Interpretationsvorgang bei Fehlern nicht sofort abbricht, sondern mit dem n&auml;chsten
         * Abschnitt fortsetzt und den fehlerhaften Abschnitt ignoriert. Zum Beispiel wird ein solcher
         * Abschnitt ignoriert, wenn die Eingabe keinen Stundenteil hat, aber der Stundenteil im
         * Abschnitt als optional gekennzeichnet ist. Es gilt auch, da&szlig; optionale Abschnitte
         * nur dann etwas ausgeben, wenn es darin irgendeine von {code 0} verschiedene
         * Dauerkomponente gibt. </p>
         *
         * <p><strong>Pluralformen</strong></p>
         *
         * <p>Jeder in geschweifte Klammern gefasste Ausdruck symbolisiert
         * eine Kombination aus Betrag, Trennzeichen und pluralisierten
         * Einheitsnamen und hat folgende Syntax: </p>
         *
         * <p>{[symbol]:[separator]:[locale]:[CATEGORY=LITERAL][:...]}</p>
         *
         * <p>Das Symbol ist eines von folgenden Zeichen: D, h, m, s, f (Bedeutung siehe Tabelle)</p>
         *
         * <p>Danach folgen Trennzeichen, abgetrennt durch einen Doppelpunkt.
         * Eine leere Zeichenkette ist auch zul&auml;ssig. Danach folgt eine
         * Lokalisierungsangabe zum Bestimmen der Pluralregeln in der Form
         * [language]-[country]-[variant], zum Beispiel &quot;de-DE&quot; oder
         * &quot;en_US&quot;. Mindestens mu&szlig; die Sprache vorhanden sein.
         * Der Unterstrich wird neben dem Minuszeichen ebenfalls interpretiert.
         * Schlie&szlig;lich folgt eine Sequenz von Name-Wert-Paaren in
         * der Form CATEGORY=LITERAL. Jede Kategoriebezeichnung ist der Name
         * einer {@link PluralCategory Pluralkategorie}. Die Kategorie OTHER
         * mu&szlig; enthalten sein. Beispiel: </p>
         *
         * <pre>
         *  MachineTime.Formatter formatter =
         *      MachineTime.Formatter.ofPattern(&quot;{D: :de:ONE=Tag:OTHER=Tage}&quot;);
         *  String s = formatter.format(MachineTime.of(3, TimeUnit.DAYS));
         *  System.out.println(s); // output: 3 Tage
         * </pre>
         *
         * <p><strong>Numerische Platzhalter</strong></p>
         *
         * <p>Die maximale numerische Breite ist immer die Summe aus minimaler Breite
         * und der Anzahl der vorangehenden #-Zeichen. Beispiel: </p>
         *
         * <pre>
         *  MachineTime.Formatter formatter1 =
         *      MachineTime.Formatter.ofPattern(&quot;D&quot;);
         *  formatter1.format(MachineTime.of(123, TimeUnit.DAYS)); throws IllegalArgumentException
         *
         *  MachineTime.Formatter formatter2 =
         *      MachineTime.Formatter.ofPattern(&quot;##D&quot;);
         *  String s = formatter2.format(MachineTime.of(123, TimeUnit.DAYS));
         *  System.out.println(s); // output: 123
         * </pre>
         *
         * <p><strong>oder-Logik</strong></p>
         *
         * <p>Das Zeichen &quot;|&quot; beginnt einen neuen Abschnitt, der nicht zur Textausgabe,
         * aber beim Interpretieren (Parsen) verwendet wird, falls im vorangehenden Abschnitt
         * Fehler auftraten. Zum Beispiel erm&ouml;glicht folgendes Muster das Interpretieren
         * einer Dauer in Tagen f&uuml;r zwei verschiedene Sprachen: </p>
         *
         * <pre>
         *  &quot;{D: :en:ONE=day:OTHER=days}|{D: :de:ONE=Tag:OTHER=Tage}&quot;
         * </pre>
         *
         * @param   pattern format pattern
         * @return  new formatter instance
         * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
         */
        public static Formatter ofPattern(String pattern) {

            return new Formatter(pattern);

        }

        /**
         * <p>Creates a textual output of given duration and writes to the buffer. </p>
         *
         * <p>Note: This method performs an automatical normalization such that the underlying
         * format pattern will be respected in every possible way. </p>
         *
         * @param   duration	duration object
         * @param   buffer      I/O-buffer where the result is written to
         * @throws	IllegalArgumentException if some aspects of duration
         *          prevents printing (for example too many nanoseconds)
         * @throws  IOException if writing into buffer fails
         */
        /*[deutsch]
         * <p>Erzeugt eine textuelle Ausgabe der angegebenen Dauer und schreibt sie in den Puffer. </p>
         *
         * <p>Hinweis: Diese Methode leistet auch eine automatische Normalisierung derart, da&szlig;
         * das zugrundeliegende Formatmuster in jeder erdenklichen Weise ber&uuml;cksichtigt wird. </p>
         *
         * @param   duration	duration object
         * @param   buffer      I/O-buffer where the result is written to
         * @throws	IllegalArgumentException if some aspects of duration
         *          prevents printing (for example too many nanoseconds)
         * @throws  IOException if writing into buffer fails
         */
        @Override
        public void print(
            TimeSpan<? super TimeUnit> duration,
            Appendable buffer
        ) throws IOException {

            String p = this.getPattern();
            int n = p.length();
            StringBuilder sb = new StringBuilder(n);

            for (int i = 0; i < n; i++) {
                char c = p.charAt(i);

                if (c == '\'') {
                    i++;
                    while (i < n) {
                        if (p.charAt(i) == '\'') {
                            if ((i + 1 < n) && (p.charAt(i + 1) == '\'')) {
                                i++;
                            } else {
                                break;
                            }
                        }
                        i++;
                    }
                } else {
                    sb.append(c);
                }
            }

            String pattern = sb.toString(); // literals are now stripped off
            Set<TimeUnit> patternUnits = EnumSet.noneOf(TimeUnit.class);

            if (pattern.contains("D")) {
                patternUnits.add(TimeUnit.DAYS);
            }
            if (pattern.contains("h")) {
                patternUnits.add(TimeUnit.HOURS);
            }
            if (pattern.contains("m")) {
                patternUnits.add(TimeUnit.MINUTES);
            }
            if (pattern.contains("s")) {
                patternUnits.add(TimeUnit.SECONDS);
            }
            if (pattern.contains("f")) {
                patternUnits.add(TimeUnit.NANOSECONDS);
            }

            super.print(new Normalized(duration, patternUnits), buffer);

        }

        @Override
        protected MachineTime<TimeUnit> convert(Map<TimeUnit, Long> map, boolean negative) {

            long seconds = 0L;
            int nanos = 0;

            if (map.containsKey(TimeUnit.NANOSECONDS)) {
                nanos = MathUtils.safeCast(map.get(TimeUnit.NANOSECONDS).longValue());
            }

            for (Map.Entry<TimeUnit, Long> entry : map.entrySet()) {
                int factor;
                switch (entry.getKey()) {
                    case DAYS:
                        factor = 86400;
                        break;
                    case HOURS:
                        factor = 3600;
                        break;
                    case MINUTES:
                        factor = 60;
                        break;
                    case SECONDS:
                        factor = 1;
                        break;
                    default:
                        continue;
                }
                seconds += MathUtils.safeMultiply(entry.getValue().longValue(), factor);
            }

            if (negative) {
                seconds = MathUtils.safeNegate(seconds);
                nanos = -nanos;
            }

            return MachineTime.ofPosixUnits(seconds, nanos);

        }

        @Override
        protected TimeUnit getUnit(char symbol) {

            switch (symbol) {
                case 'D':
                    return TimeUnit.DAYS;
                case 'h':
                    return TimeUnit.HOURS;
                case 'm':
                    return TimeUnit.MINUTES;
                case 's':
                    return TimeUnit.SECONDS;
                case 'f':
                    return TimeUnit.NANOSECONDS;
                default:
                    throw new IllegalArgumentException(
                        "Unsupported pattern symbol: " + symbol);
            }

        }

    }

    private static class Normalized
        implements TimeSpan<TimeUnit> {

        //~ Instanzvariablen ----------------------------------------------

        private final TimeSpan<? super TimeUnit> duration;
        private final Set<TimeUnit> patternUnits;

        //~ Konstruktoren -------------------------------------------------

        Normalized(
            TimeSpan<? super TimeUnit> duration,
            Set<TimeUnit> patternUnits
        ) {
            super();

            if (duration == null) {
                throw new NullPointerException();
            }

            this.duration = duration;
            this.patternUnits = patternUnits;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public List<Item<TimeUnit>> getTotalLength() {
            throw new AssertionError("Never called.");
        }

        @Override
        public boolean contains(TimeUnit unit) {
            return true;
        }

        @Override
        public long getPartialAmount(TimeUnit unit) {

            if (unit == TimeUnit.NANOSECONDS) {
                return this.duration.getPartialAmount(TimeUnit.NANOSECONDS);
            }

            long days = 0L;
            long hours = 0L;
            long minutes = 0L;
            long seconds = this.duration.getPartialAmount(TimeUnit.SECONDS);

            if (this.patternUnits.contains(TimeUnit.DAYS)) {
                days = seconds / 86400;
                seconds -= (days * 86400);
            }

            if (this.patternUnits.contains(TimeUnit.HOURS)) {
                hours = seconds / 3600;
                seconds -= (hours * 3600);
            }

            if (this.patternUnits.contains(TimeUnit.MINUTES)) {
                minutes = seconds / 60;
                seconds -= (minutes * 60);
            }

            switch (unit) {
                case DAYS:
                    return days;
                case HOURS:
                    return hours;
                case MINUTES:
                    return minutes;
                case SECONDS:
                    return seconds;
                default:
                    throw new AssertionError("Never called.");
            }

        }

        @Override
        public boolean isNegative() {
            return this.duration.isNegative();
        }

        @Override
        public boolean isPositive() {
            return this.duration.isPositive();
        }

        @Override
        public boolean isEmpty() {
            return this.duration.isEmpty();
        }

        @Override
        public <T extends TimePoint<? super TimeUnit, T>> T addTo(T time) {
            throw new AssertionError("Never called.");
        }

        @Override
        public <T extends TimePoint<? super TimeUnit, T>> T subtractFrom(T time) {
            throw new AssertionError("Never called.");
        }

    }


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
                long utc2 = t2.getElapsedTime(UTC);
                long utc1 = t1.getElapsedTime(UTC);
                if (utc2 < 0 || utc1 < 0) {
                    throw new UnsupportedOperationException(
                        "Cannot calculate SI-duration before 1972-01-01.");
                }
                secs = utc2 - utc1;
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
