/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdNormalizer.java) is part of project Time4J.
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
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Normalizer;
import net.time4j.engine.TimeSpan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.time4j.CalendarUnit.*;
import static net.time4j.ClockUnit.*;


/**
 * <p>Hilfsobjekt f&uuml;r Standard-Normalisierungen einer Dauer. </p>
 *
 * @author  Meno Hochschild
 * @since   3.1
 */
class StdNormalizer<U extends IsoUnit>
    implements Normalizer<U>, Comparator<TimeSpan.Item<? extends ChronoUnit>> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;
    private static final int MRD = 1000000000;

    //~ Instanzvariablen --------------------------------------------------

    private final boolean mixed;

    //~ Konstruktoren -----------------------------------------------------

    private StdNormalizer(boolean mixed) {
        super();

        this.mixed = mixed;

    }

    //~ Methoden ----------------------------------------------------------

    static StdNormalizer<IsoUnit> ofMixedUnits() {

        return new StdNormalizer<IsoUnit>(true);

    }

    static StdNormalizer<CalendarUnit> ofCalendarUnits() {

        return new StdNormalizer<CalendarUnit>(false);

    }

    static StdNormalizer<ClockUnit> ofClockUnits() {

        return new StdNormalizer<ClockUnit>(false);

    }

    static Comparator<TimeSpan.Item<? extends ChronoUnit>> comparator() {

        return new StdNormalizer<IsoUnit>(false);

    }

    @Override
    public int compare(
        TimeSpan.Item<? extends ChronoUnit> o1,
        TimeSpan.Item<? extends ChronoUnit> o2
    ) {

        return compare(o1.getUnit(), o2.getUnit());

    }

    @SuppressWarnings("unchecked")
    @Override
    public Duration<U> normalize(TimeSpan<? extends U> timespan) {

        int count = timespan.getTotalLength().size();
        List<TimeSpan.Item<U>> items = new ArrayList<TimeSpan.Item<U>>(count);
        long years = 0, months = 0, weeks = 0, days = 0;
        long hours = 0, minutes = 0, seconds = 0, nanos = 0;

        for (int i = 0; i < count; i++) {
            TimeSpan.Item<? extends U> item = timespan.getTotalLength().get(i);
            long amount = item.getAmount();
            U unit = item.getUnit();

            if (unit instanceof CalendarUnit) {
                switch ((CalendarUnit.class.cast(unit))) {
                    case MILLENNIA:
                        years =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 1000),
                                years
                            );
                        break;
                    case CENTURIES:
                        years =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 100),
                                years
                            );
                        break;
                    case DECADES:
                        years =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 10),
                                years
                            );
                        break;
                    case YEARS:
                        years = MathUtils.safeAdd(amount, years);
                        break;
                    case QUARTERS:
                        months =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 3),
                                months
                            );
                        break;
                    case MONTHS:
                        months = MathUtils.safeAdd(amount, months);
                        break;
                    case WEEKS:
                        weeks = amount;
                        break;
                    case DAYS:
                        days = amount;
                        break;
                    default:
                        throw new UnsupportedOperationException(unit.toString());
                }
            } else if (unit instanceof ClockUnit) {
                switch ((ClockUnit.class.cast(unit))) {
                    case HOURS:
                        hours = amount;
                        break;
                    case MINUTES:
                        minutes = amount;
                        break;
                    case SECONDS:
                        seconds = amount;
                        break;
                    case MILLIS:
                        nanos =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, MIO),
                                nanos
                            );
                        break;
                    case MICROS:
                        nanos =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 1000L),
                                nanos
                            );
                        break;
                    case NANOS:
                        nanos = MathUtils.safeAdd(amount, nanos);
                        break;
                    default:
                        throw new UnsupportedOperationException(unit.toString());
                }
            } else {
                items.add(TimeSpan.Item.of(amount, unit));
            }
        }

        long f = 0, s = 0, n = 0, h = 0;

        if ((hours | minutes | seconds | nanos) != 0) {
            f = nanos % MRD;
            seconds = MathUtils.safeAdd(seconds, nanos / MRD);
            s = seconds % 60;
            minutes = MathUtils.safeAdd(minutes, seconds / 60);
            n = minutes % 60;
            hours = MathUtils.safeAdd(hours, minutes / 60);

            if (this.mixed) {
                h = hours % 24;
                days = MathUtils.safeAdd(days, hours / 24);
            } else {
                h = hours;
            }
        }

        U unit;

        if ((years | months | days) != 0) {
            long y = MathUtils.safeAdd(years, months / 12);
            long m = months % 12;
            long d =
                MathUtils.safeAdd(
                    MathUtils.safeMultiply(weeks, 7),
                    days
                );

            if (y != 0) {
                unit = (U) YEARS;
                items.add(TimeSpan.Item.of(y, unit));
            }
            if (m != 0) {
                unit = (U) MONTHS;
                items.add(TimeSpan.Item.of(m, unit));
            }
            if (d != 0) {
                unit = (U) DAYS;
                items.add(TimeSpan.Item.of(d, unit));
            }
        } else if (weeks != 0) {
            unit = (U) WEEKS;
            items.add(TimeSpan.Item.of(weeks, unit));
        }

        if (h != 0) {
            unit = (U) HOURS;
            items.add(TimeSpan.Item.of(h, unit));
        }

        if (n != 0) {
            unit = (U) MINUTES;
            items.add(TimeSpan.Item.of(n, unit));
        }

        if (s != 0) {
            unit = (U) SECONDS;
            items.add(TimeSpan.Item.of(s, unit));
        }

        if (f != 0) {
            unit = (U) NANOS;
            items.add(TimeSpan.Item.of(f, unit));
        }

        return new Duration<U>(items, timespan.isNegative());

    }

    static int compare(
        ChronoUnit u1,
        ChronoUnit u2
    ) {

        int result = Double.compare(u2.getLength(), u1.getLength());

        if (
            (result == 0)
                && !u1.equals(u2)
            ) {
            throw new IllegalArgumentException(
                "Mixing different units of same length not allowed.");
        }

        return result;

    }

}
