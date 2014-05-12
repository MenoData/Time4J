/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalTimestamp.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.Chronology;
import net.time4j.scale.TimeScale;
import net.time4j.scale.UniversalTime;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import static net.time4j.PlainTime.SECOND_OF_MINUTE;


/**
 * <p>Zonaler Zeitstempel als Kombination aus lokalem Zeitstempel und
 * Zeitzonendaten. </p>
 *
 * @author  Meno Hochschild
 */
final class ZonalTimestamp
    extends ChronoEntity<ZonalTimestamp>
    implements UniversalTime {

    //~ Instanzvariablen --------------------------------------------------

    private final Moment moment;
    private final Timezone zone;
    private transient final PlainTimestamp timestamp;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt einen zonalen Zeitstempel. </p>
     *
     * @param   moment          global timestamp
     * @param   tzid            timezone id (optional)
     * @throws  IllegalArgumentException if leapsecond shall be formatted
     *          with non-full-minute-timezone-offset
     */
    ZonalTimestamp(
        Moment moment,
        TZID tzid
    ) {
        super();

        if (tzid == null) {
            this.zone = Timezone.ofSystem();
        } else {
            this.zone = Timezone.of(tzid);
        }

        ZonalOffset offset = this.zone.getOffset(moment);

        if (moment.isLeapSecond()) {
            if (
                (offset.getFractionalAmount() != 0)
                || ((offset.getAbsoluteSeconds() % 60) != 0)
            ) {
                throw new IllegalArgumentException(
                    "Leap second can only be formatted "
                    + " with timezone-offset in full minutes: "
                    + offset);
            }
        }

        this.moment = moment;
        this.timestamp = PlainTimestamp.from(moment, offset);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(ChronoElement<?> element) {

        return this.timestamp.contains(element);

    }

    @Override
    public <V> V get(ChronoElement<V> element) {

        if (
            this.moment.isLeapSecond()
            && (element == SECOND_OF_MINUTE)
        ) {
            return element.getType().cast(Integer.valueOf(60));
        }

        return this.timestamp.get(element);

    }

    // benutzt in ChronoFormatter/FractionProcessor
    @Override
    public <V> V getMinimum(ChronoElement<V> element) {

        return this.timestamp.getMinimum(element);

    }

    // benutzt in ChronoFormatter/FractionProcessor
    @Override
    public <V> V getMaximum(ChronoElement<V> element) {

        V max = this.timestamp.getMaximum(element);

        if (
            (element == SECOND_OF_MINUTE)
            && (this.timestamp.getYear() >= 1972)
        ) {
            PlainTimestamp ts = this.timestamp.with(element, max);

            if (!this.zone.isInvalid(ts, ts)) {
                Moment transformed = ts.inTimezone(this.zone);
                Moment test = transformed.plus(1, SI.SECONDS);

                if (test.isLeapSecond()) {
                    return element.getType().cast(Integer.valueOf(60));
                }
            }
        }

        return max;

    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R get(ChronoFunction<? super ZonalTimestamp, R> function) {

        if (function == Timezone.identifier()) {
            return (R) this.zone.getID();
        }

        return super.get(function);

    }

    @Override
    public <V> boolean isValid(
        ChronoElement<V> element,
        V value
    ) {

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        }

        return false;

    }

    @Override
    public <V> ZonalTimestamp with(
        ChronoElement<V> element,
        V value
    ) {

        throw new IllegalArgumentException(
            "Zonal timestamps are effectively read-only.");

    }

    @Override
    protected Chronology<ZonalTimestamp> getChronology() {

        throw new UnsupportedOperationException(
            "Zonal timestamps have no defined chronology.");

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ZonalTimestamp) {
            ZonalTimestamp that = (ZonalTimestamp) obj;
            return (
                this.moment.equals(that.moment)
                && this.zone.equals(that.zone)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (this.moment.hashCode() ^ this.zone.hashCode());

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("[moment=");
        sb.append(this.moment);
        sb.append(", time-zone=");
        sb.append(this.zone);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public long getElapsedTime(TimeScale scale) {

        return this.moment.getElapsedTime(scale);

    }

    @Override
    public int getNanosecond(TimeScale scale) {

        return this.moment.getNanosecond(scale);

    }

    @Override
    public boolean isLeapSecond() {

        return this.moment.isLeapSecond();

    }

    @Override
    public long getPosixTime() {

        return this.moment.getPosixTime();

    }

    @Override
    public int getNanosecond() {

        return this.moment.getNanosecond();

    }

    @Override
    protected ZonalTimestamp getContext() {

        return this;

    }

}
