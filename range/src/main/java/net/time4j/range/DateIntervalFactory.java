/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.engine.TimeLine;
import net.time4j.format.expert.ParseLog;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


final class DateIntervalFactory
    implements IntervalFactory<PlainDate, DateInterval> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Set<ChronoElement<?>> CSET;
    private static final Set<ChronoElement<?>> OSET;
    private static final Set<ChronoElement<?>> WSET;

    static {
        Set<ChronoElement<?>> cset = new HashSet<>();
        cset.add(PlainDate.YEAR);
        cset.add(PlainDate.MONTH_AS_NUMBER);
        CSET = Collections.unmodifiableSet(cset);

        Set<ChronoElement<?>> oset = new HashSet<>();
        oset.add(PlainDate.YEAR);
        OSET = Collections.unmodifiableSet(oset);

        Set<ChronoElement<?>> wset = new HashSet<>();
        wset.add(PlainDate.YEAR_OF_WEEKDATE);
        wset.add(Weekmodel.ISO.weekOfYear());
        WSET = Collections.unmodifiableSet(wset);
    }

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

        if (rawData.contains(PlainDate.DAY_OF_MONTH)) {
            return CSET;
    	} else if (rawData.contains(PlainDate.DAY_OF_YEAR)) {
            return OSET;
        } else if (rawData.contains(PlainDate.DAY_OF_WEEK)) {
            return WSET;
        }

        return Collections.emptySet();

    }

    @Override
    public boolean isCalendrical() {

        return true;

    }

    @Override
    public TimeLine<PlainDate> getTimeLine() {

        return PlainDate.axis();

    }

}
