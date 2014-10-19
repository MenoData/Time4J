/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimestampIntervalFactory.java) is part of project Time4J.
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
import net.time4j.PlainTimestamp;
import net.time4j.Weekmodel;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.ParseLog;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


final class TimestampIntervalFactory
    implements IntervalFactory<PlainTimestamp, TimestampInterval> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final TimestampIntervalFactory INSTANCE =
        new TimestampIntervalFactory();

    //~ Konstruktoren -----------------------------------------------------

    private TimestampIntervalFactory() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public TimestampInterval between(
        Boundary<PlainTimestamp> start,
        Boundary<PlainTimestamp> end
    ) {

        return new TimestampInterval(start, end);

    }

    @Override
    public PlainTimestamp plusPeriod(
        PlainTimestamp timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        try {
            return timepoint.plus(Duration.parsePeriod(period));
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public PlainTimestamp minusPeriod(
        PlainTimestamp timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        try {
            return timepoint.minus(Duration.parsePeriod(period));
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public Set<ChronoElement<?>> stdElements(ChronoEntity<?> rawData) {

        Set<ChronoElement<?>> set = new HashSet<ChronoElement<?>>();

        if (rawData.contains(PlainDate.DAY_OF_WEEK)) {
            set.add(PlainDate.YEAR_OF_WEEKDATE);
            set.add(Weekmodel.ISO.weekOfYear());
        } else {
            set.add(PlainDate.YEAR);
            if (rawData.contains(PlainDate.DAY_OF_MONTH)) {
                set.add(PlainDate.MONTH_AS_NUMBER);
                set.add(PlainDate.DAY_OF_MONTH);
            } else if (rawData.contains(PlainDate.DAY_OF_YEAR)) {
                set.add(PlainDate.DAY_OF_YEAR);
            }
        }

        return Collections.unmodifiableSet(set);

    }

}
