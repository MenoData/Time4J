/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.scale.TimeScale;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;


/**
 * <p>Allows miscellaneous adjustments to any clock. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
/*[deutsch]
 * <p>Erlaubt verschiedene Verstellungen irgendeiner beliebigen Uhr. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
public final class AdjustableClock
    extends AbstractClock {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;

    //~ Instanzvariablen --------------------------------------------------

    private final TimeSource<?> source;
    private final TimeUnit pulse;
    private final int offsetAmount;
    private final TimeUnit offsetUnit;

    //~ Konstruktoren -----------------------------------------------------

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
     * <p>Creates a new adjustable clock for given time source. </p>
     *
     * @param   source      time source which shall be adjusted
     * @return  new adjustable clock wrapper
     * @since   2.1
     */
    /*[deutsch]
     * <p>Erzeugt eine neue verstellbare Uhr f&uuml;r die angegebene
     * Zeitquelle. </p>
     *
     * @param   source      time source which shall be adjusted
     * @return  new adjustable clock wrapper
     * @since   2.1
     */
    public static AdjustableClock of(TimeSource<?> source) {

        if (source == null) {
            throw new NullPointerException("Missing time source.");
        }

        return new AdjustableClock(
            source,
            TimeUnit.NANOSECONDS,
            0,
            TimeUnit.NANOSECONDS);

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
     * @param   amount      amount of shift
     * @param   unit        unit of shift
     * @return  adjusted clock with given shift/offset
     * @since   2.1
     */
    /*[deutsch]
     * <p>Erzeugt eine getaktete Uhr, die die aktuelle Uhrzeit mit dem
     * angegebenen Versatz anzeigt. </p>
     *
     * @param   amount      amount of shift
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
