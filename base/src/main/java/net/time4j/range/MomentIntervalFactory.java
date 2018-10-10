/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MomentIntervalFactory.java) is part of project Time4J.
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
import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.FlagElement;
import net.time4j.engine.TimeLine;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ParseLog;
import net.time4j.tz.OverlapResolver;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;

import java.text.ParseException;
import java.util.Set;


final class MomentIntervalFactory
    implements IntervalFactory<Moment, MomentInterval> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final MomentIntervalFactory INSTANCE = new MomentIntervalFactory();

    //~ Konstruktoren -----------------------------------------------------

    private MomentIntervalFactory() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public MomentInterval between(
        Boundary<Moment> start,
        Boundary<Moment> end
    ) {

        return new MomentInterval(start, end);

    }

    @Override
    public Moment plusPeriod(
        Moment timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        Timezone tz = getTimezone(plog.getRawValues(), attributes);
        PlainTimestamp tsp = timepoint.toZonalTimestamp(tz.getID());

        try {
            return tsp.plus(Duration.parsePeriod(period)).in(tz);
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public Moment minusPeriod(
        Moment timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        Timezone tz = getTimezone(plog.getRawValues(), attributes);
        PlainTimestamp tsp = timepoint.toZonalTimestamp(tz.getID());

        try {
            return tsp.minus(Duration.parsePeriod(period)).in(tz);
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public Set<ChronoElement<?>> stdElements(ChronoEntity<?> rawData) {

        return TimestampIntervalFactory.INSTANCE.stdElements(rawData);

    }

    @Override
    public TimeLine<Moment> getTimeLine() {

        return Moment.axis();

    }

    // übernommen von Moment.Merger
    private static Timezone getTimezone(
        ChronoEntity<?> entity,
        AttributeQuery attrs
    ) {

        TZID tzid = null;

        if (entity.hasTimezone()) {
            tzid = entity.getTimezone();
        } else if (attrs.contains(Attributes.TIMEZONE_ID)) {
            tzid = attrs.get(Attributes.TIMEZONE_ID); // Ersatzwert
        }

        if (tzid != null) {
            if (entity.contains(FlagElement.DAYLIGHT_SAVING)) {
                boolean dst = entity.get(FlagElement.DAYLIGHT_SAVING).booleanValue();
                TransitionStrategy strategy =
                    attrs
                        .get(Attributes.TRANSITION_STRATEGY, Timezone.DEFAULT_CONFLICT_STRATEGY)
                        .using(dst ? OverlapResolver.EARLIER_OFFSET : OverlapResolver.LATER_OFFSET);
                return Timezone.of(tzid).with(strategy);
            } else if (attrs.contains(Attributes.TRANSITION_STRATEGY)) {
                return Timezone.of(tzid).with(attrs.get(Attributes.TRANSITION_STRATEGY));
            } else {
                return Timezone.of(tzid);
            }
        }

        throw new AssertionError(
            "Timezone must exist if a moment was successfully parsed.");

    }

}
