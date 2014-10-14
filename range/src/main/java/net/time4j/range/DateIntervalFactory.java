/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DateIntervalFactory.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.Duration;
import net.time4j.PlainDate;
import net.time4j.Weekmodel;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.ParseLog;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


final class DateIntervalFactory
    implements IsoIntervalFactory<PlainDate, DateInterval> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final DateIntervalFactory INSTANCE = new DateIntervalFactory();

    //~ Konstruktoren -----------------------------------------------------

    private DateIntervalFactory() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public DateInterval between(
        Boundary<PlainDate> start,
        Boundary<PlainDate> end
    ) {

        return new DateInterval(start, end);

    }

    @Override
    public PlainDate plusPeriod(
        PlainDate timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        try {
            return timepoint.plus(Duration.parseCalendarPeriod(period));
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public PlainDate minusPeriod(
        PlainDate timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        try {
            return timepoint.minus(Duration.parseCalendarPeriod(period));
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public Set<ChronoElement<?>> stdElements(ChronoEntity<?> rawData) {

        Set<ChronoElement<?>> set = new HashSet<ChronoElement<?>>();

        if (rawData.contains(PlainDate.DAY_OF_WEEK)) {
            if (!rawData.contains(PlainDate.YEAR_OF_WEEKDATE)) {
                set.add(PlainDate.YEAR_OF_WEEKDATE);
                if (!rawData.contains(Weekmodel.ISO.weekOfYear())) {
                    set.add(Weekmodel.ISO.weekOfYear());
                }
            }
        } else if (!rawData.contains(PlainDate.YEAR)) {
            set.add(PlainDate.YEAR);
            if (
                !rawData.contains(PlainDate.MONTH_OF_YEAR)
                && !rawData.contains(PlainDate.MONTH_AS_NUMBER)
                && !rawData.contains(PlainDate.DAY_OF_YEAR)
            ) {
                set.add(PlainDate.MONTH_AS_NUMBER);
            }
        }

        return Collections.unmodifiableSet(set);

    }

}
