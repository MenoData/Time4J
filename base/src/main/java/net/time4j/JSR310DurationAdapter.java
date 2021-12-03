/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (JSR310DurationAdapter.java) is part of project Time4J.
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

import net.time4j.engine.TimeSpan;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>Bridge between {@code net.time4j.Duration} and a {@code TemporalAmount}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 * @serial  include
 */
final class JSR310DurationAdapter
    implements TemporalAmount, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<IsoUnit, TemporalUnit> MAP;

    static {
        Map<IsoUnit, TemporalUnit> map = new HashMap<>();
        map.put(CalendarUnit.MILLENNIA, ChronoUnit.MILLENNIA);
        map.put(CalendarUnit.CENTURIES, ChronoUnit.CENTURIES);
        map.put(CalendarUnit.DECADES, ChronoUnit.DECADES);
        map.put(CalendarUnit.YEARS, ChronoUnit.YEARS);
        map.put(CalendarUnit.QUARTERS, IsoFields.QUARTER_YEARS);
        map.put(CalendarUnit.MONTHS, ChronoUnit.MONTHS);
        map.put(CalendarUnit.WEEKS, ChronoUnit.WEEKS);
        map.put(CalendarUnit.DAYS, ChronoUnit.DAYS);
        map.put(CalendarUnit.weekBasedYears(), IsoFields.WEEK_BASED_YEARS);
        map.put(ClockUnit.HOURS, ChronoUnit.HOURS);
        map.put(ClockUnit.MINUTES, ChronoUnit.MINUTES);
        map.put(ClockUnit.SECONDS, ChronoUnit.SECONDS);
        map.put(ClockUnit.MILLIS, ChronoUnit.MILLIS);
        map.put(ClockUnit.MICROS, ChronoUnit.MICROS);
        map.put(ClockUnit.NANOS, ChronoUnit.NANOS);
        MAP = Collections.unmodifiableMap(map);
    }

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the underlying duration of Time4J
     */
    private final Duration<?> duration;

    //~ Konstruktoren -----------------------------------------------------

    JSR310DurationAdapter(Duration<?> duration) {
        super();

        this.duration = duration;

        for (TimeSpan.Item<? extends IsoUnit> item : duration.getTotalLength()) {
            IsoUnit unit = item.getUnit();
            if (!MAP.containsKey(unit)) {
                throw new UnsupportedOperationException("Cannot be used in any TemporalAmount: " + unit);
            }
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public long get(TemporalUnit unit) {

        for (Map.Entry<IsoUnit, TemporalUnit> entry : MAP.entrySet()) {
            if (entry.getValue().equals(unit)) {
                long amount = this.duration.getPartialAmount(entry.getKey());
                if (this.duration.isNegative()) {
                    amount = Math.negateExact(amount);
                }
                return amount;
            }
        }

        if (unit.equals(ChronoUnit.HALF_DAYS)) {
            long hd = Math.floorDiv(this.duration.getPartialAmount(ClockUnit.HOURS), 12);
            if (this.duration.isNegative()) {
                hd = Math.negateExact(hd);
            }
            return hd;
        }

        throw new UnsupportedTemporalTypeException(unit.toString()); // throws NPE if unit is null

    }

    @Override
    public List<TemporalUnit> getUnits() {

        List<TemporalUnit> units = new ArrayList<>();

        for (TimeSpan.Item<? extends IsoUnit> item : this.duration.getTotalLength()) {
            IsoUnit unit = item.getUnit();
            units.add(MAP.get(unit));
        }

        return Collections.unmodifiableList(units);

    }

    @Override
    public Temporal addTo(Temporal temporal) {

        return apply(temporal, this.duration);

    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {

        return apply(temporal, this.duration.inverse());

    }

    private static Temporal apply(
        Temporal temporal,
        Duration<?> duration
    ) {

        long wy = duration.getPartialAmount(CalendarUnit.weekBasedYears());

        long m = Math.multiplyExact(duration.getPartialAmount(CalendarUnit.MILLENNIA), 12L * 1000);
        m = Math.addExact(m, Math.multiplyExact(duration.getPartialAmount(CalendarUnit.CENTURIES), 12L * 100));
        m = Math.addExact(m, Math.multiplyExact(duration.getPartialAmount(CalendarUnit.DECADES), 12L * 10));
        m = Math.addExact(m, Math.multiplyExact(duration.getPartialAmount(CalendarUnit.YEARS), 12L));
        m = Math.addExact(m, Math.multiplyExact(duration.getPartialAmount(CalendarUnit.QUARTERS), 3L));
        m = Math.addExact(m, duration.getPartialAmount(CalendarUnit.MONTHS));

        long d = Math.multiplyExact(duration.getPartialAmount(CalendarUnit.WEEKS), 7L);
        d = Math.addExact(d, duration.getPartialAmount(CalendarUnit.DAYS));

        long s = Math.multiplyExact(duration.getPartialAmount(ClockUnit.HOURS), 3600L);
        s = Math.addExact(s, Math.multiplyExact(duration.getPartialAmount(ClockUnit.MINUTES), 60L));
        s = Math.addExact(s, duration.getPartialAmount(ClockUnit.SECONDS));

        long f = duration.getPartialAmount(ClockUnit.NANOS);

        if (duration.isNegative()) {
            if (f > 0) {
                temporal = temporal.minus(f, ChronoUnit.NANOS);
            }
            if (s > 0) {
                temporal = temporal.minus(s, ChronoUnit.SECONDS);
            }
            if (d > 0) {
                temporal = temporal.minus(d, ChronoUnit.DAYS);
            }
            if (m > 0) {
                temporal = temporal.minus(m, ChronoUnit.MONTHS);
            }
            if (wy > 0) {
                temporal = temporal.minus(wy, IsoFields.WEEK_BASED_YEARS);
            }
        } else {
            if (wy > 0) {
                temporal = temporal.plus(wy, IsoFields.WEEK_BASED_YEARS);
            }
            if (m > 0) {
                temporal = temporal.plus(m, ChronoUnit.MONTHS);
            }
            if (d > 0) {
                temporal = temporal.plus(d, ChronoUnit.DAYS);
            }
            if (s > 0) {
                temporal = temporal.plus(s, ChronoUnit.SECONDS);
            }
            if (f > 0) {
                temporal = temporal.plus(f, ChronoUnit.NANOS);
            }
        }

        return temporal;

    }

}
