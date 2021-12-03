/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AdjustableClock.java) is part of project Time4J.
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

package net.time4j.clock;

import net.time4j.Moment;
import net.time4j.SystemClock;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.scale.TimeScale;

import java.util.concurrent.TimeUnit;


/**
 * <p>Allows miscellaneous adjustments to any clock. </p>
 * 
 * <p>Note: This class is immutable as long as the underlying time source is immutable. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
/*[deutsch]
 * <p>Erlaubt verschiedene Verstellungen irgendeiner beliebigen Uhr. </p>
 *
 * <p>Hinweis: Diese Klasse ist solange <i>immutable</p>, wie es die Quelle ist. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
public final class AdjustableClock
    extends AbstractClock {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;

    private static final AdjustableClock SYSTEM_CLOCK_WRAPPER =
        new AdjustableClock(SystemClock.INSTANCE);

    //~ Instanzvariablen --------------------------------------------------

    private final TimeSource<?> source;
    private final TimeUnit pulse;
    private final int offsetAmount;
    private final TimeUnit offsetUnit;

    //~ Konstruktoren -----------------------------------------------------

    private AdjustableClock(TimeSource<?> source) {
        this(source, TimeUnit.NANOSECONDS, 0, TimeUnit.NANOSECONDS);

    }

    private AdjustableClock(
        TimeSource<?> source,
        TimeUnit pulse,
        int offsetAmount,
        TimeUnit offsetUnit
    ) {
        super();

        this.source = source;
        this.pulse = pulse;
        this.offsetAmount = offsetAmount;
        this.offsetUnit = offsetUnit;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields an adjustable clock around the standard system clock. </p>
     *
     * <p>The clock can be adjusted using the {@code with()}-methods. </p>
     *
     * @return  adjustable clock wrapper
     * @see     SystemClock#INSTANCE
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert eine verstellbare Uhr, die auf der Standard-Systemuhr basiert. </p>
     *
     * <p>Die Uhr kann mit Hilfe der {@code with()}-Methoden verstellt
     * werden. </p>
     *
     * @return  adjustable clock wrapper
     * @see     SystemClock#INSTANCE
     * @since   2.1
     */
    public static AdjustableClock ofSystem() {

        return SYSTEM_CLOCK_WRAPPER;

    }

    /**
     * <p>Creates a new adjustable clock for given time source. </p>
     *
     * <p>After construction, the clock can be adjusted using the
     * {@code with()}-methods. </p>
     *
     * @param   source      time source which shall be adjusted
     * @return  new adjustable clock wrapper
     * @since   2.1
     */
    /*[deutsch]
     * <p>Erzeugt eine neue verstellbare Uhr f&uuml;r die angegebene
     * Zeitquelle. </p>
     *
     * <p>Nach der Konstruktion kann die Uhr mit Hilfe der
     * {@code with()}-Methoden verstellt werden. </p>
     *
     * @param   source      time source which shall be adjusted
     * @return  new adjustable clock wrapper
     * @since   2.1
     */
    public static AdjustableClock of(TimeSource<?> source) {

        if (source.equals(SystemClock.INSTANCE)) {
            return SYSTEM_CLOCK_WRAPPER;
        } else if (source instanceof AdjustableClock) {
            return AdjustableClock.class.cast(source);
        }

        return new AdjustableClock(source);

    }

    /**
     * <p>Creates a pulsed clock which only displays the current time in
     * given pulse unit. </p>
     *
     * @param   pulse       new pulse
     * @return  adjusted clock with given pulse
     * @since   2.1
     */
    /*[deutsch]
     * <p>Erzeugt eine getaktete Uhr, die die aktuelle Uhrzeit nur in der
     * angegebenen Zeiteinheit anzeigt. </p>
     *
     * @param   pulse       new pulse
     * @return  adjusted clock with given pulse
     * @since   2.1
     */
    public AdjustableClock withPulse(TimeUnit pulse) {

        if (pulse == null) {
            throw new NullPointerException("Missing pulse.");
        } else if (pulse == this.pulse) {
            return this;
        }

        return new AdjustableClock(
            this.source,
            pulse,
            this.offsetAmount,
            this.offsetUnit);

    }

    /**
     * <p>Creates an adjusted clock which displays the current time with
     * given time shift. </p>
     *
     * @param   offset      amount of shift
     * @param   unit        unit of shift
     * @return  adjusted clock with given shift/offset
     * @since   2.1
     */
    /*[deutsch]
     * <p>Erzeugt eine getaktete Uhr, die die aktuelle Uhrzeit mit dem
     * angegebenen Versatz anzeigt. </p>
     *
     * @param   offset      amount of shift
     * @param   unit        unit of shift
     * @return  adjusted clock with given shift/offset
     * @since   2.1
     */
    public AdjustableClock withOffset(
        int offset,
        TimeUnit unit
    ) {

        if (unit == null) {
            throw new NullPointerException("Missing offset unit.");
        } else if (
            (this.offsetAmount == offset)
            && (this.offsetUnit == unit)
        ) {
            return this;
        }

        return new AdjustableClock(
            this.source,
            this.pulse,
            offset,
            unit);

    }

    @Override
    public Moment currentTime() {

        Moment result = Moment.from(this.source.currentTime());
        result = result.plus(this.offsetAmount, this.offsetUnit);

        switch (this.pulse) {
            case DAYS:
                result = applyPulse(result, 86400);
                break;
            case HOURS:
                result = applyPulse(result, 3600);
                break;
            case MINUTES:
                result = applyPulse(result, 60);
                break;
            case SECONDS:
                result = Moment.of(result.getPosixTime(), TimeScale.POSIX);
                break;
            case MILLISECONDS:
                result =
                    Moment.of(
                        result.getPosixTime(),
                        (result.getNanosecond() / MIO) * MIO,
                        TimeScale.POSIX);
                break;
            case MICROSECONDS:
                result =
                    Moment.of(
                        result.getPosixTime(),
                        (result.getNanosecond() / 1000) * 1000,
                        TimeScale.POSIX);
                break;
            case NANOSECONDS:
                break; // no-op
            default:
                throw new UnsupportedOperationException(this.pulse.name());
        }

        return result;

    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        } else if (obj instanceof AdjustableClock) {
            AdjustableClock that = (AdjustableClock) obj;
            return (
                this.source.equals(that.source)
                && (this.offsetAmount == that.offsetAmount)
                && (this.offsetUnit == that.offsetUnit)
                && (this.pulse == that.pulse)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        int hash = 3;
        hash = 37 * hash + this.source.hashCode();
        hash = 37 * hash + this.pulse.hashCode();
        hash = 37 * hash + this.offsetAmount;
        return 37 * hash + this.offsetUnit.hashCode();

    }

    /**
     * <p>For debugging purposes. </p>
     *
     * @return  description of clock state
     */
    /*[deutsch]
     * <p>F&uuml;r Debugging-Zwecke. </p>
     *
     * @return  description of clock state
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("AdjustableClock[");
        sb.append("source=");
        sb.append(this.source);
        sb.append(",offset-amount=");
        sb.append(this.offsetAmount);
        sb.append(",offset-unit=");
        sb.append(this.offsetUnit);
        sb.append(",pulse=");
        sb.append(this.pulse);
        sb.append(']');
        return sb.toString();

    }

    private static Moment applyPulse(
        Moment moment,
        int factor
    ) {

        return Moment.of(
            MathUtils.floorDivide(
                moment.getPosixTime(), factor
            ) * factor,
            TimeScale.POSIX);

    }

}
