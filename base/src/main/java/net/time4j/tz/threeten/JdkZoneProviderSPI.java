/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (JdkZoneProviderSPI.java) is part of project Time4J.
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

package net.time4j.tz.threeten;

import net.time4j.Month;
import net.time4j.PlainTime;
import net.time4j.TemporalType;
import net.time4j.Weekday;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.ZoneModelProvider;
import net.time4j.tz.model.DaylightSavingRule;
import net.time4j.tz.model.GregorianTimezoneRule;
import net.time4j.tz.model.OffsetIndicator;
import net.time4j.tz.model.TransitionModel;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>SPI-implementation for the indirect evaluation of &quot;tzdb.dat&quot;-repository
 * via the new zone-api in Java-8. </p>
 *
 * @author  Meno Hochschild
 * @since   4.0
 */
public class JdkZoneProviderSPI
    implements ZoneModelProvider {

    //~ Instanzvariablen --------------------------------------------------

    private final String version;

    //~ Konstruktoren -----------------------------------------------------

    public JdkZoneProviderSPI() {
        super();

        this.version = ZoneRulesProvider.getVersions("America/New_York").lastEntry().getKey();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Set<String> getAvailableIDs() {

        return Collections.unmodifiableSet(ZoneRulesProvider.getAvailableZoneIds());

    }

    @Override
    public Map<String, String> getAliases() {

        return Collections.emptyMap();

    }

    @Override
    public String getFallback() {

        return "";

    }

    @Override
    public String getName() {

        return "TZDB";

    }

    @Override
    public String getLocation() {

        return "{java.home}/lib/tzdb.dat";

    }

    @Override
    public String getVersion() {

        return this.version;

    }

    @Override
    public TransitionHistory load(String zoneID) {

        try {
            return load(ZoneId.of(zoneID));
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException(ex);
        }

    }

    /**
     * The real implementation using a wrapper around {@code ZoneRules} derived from given {@code ZoneId}.
     *
     * @param   zoneId      threeten-zone-identifier
     * @return  timezone history
     * @throws  IllegalArgumentException if given id is wrong
     * @since   5.0
     */
    public static TransitionHistory load(ZoneId zoneId) {

        try {
            ZoneRules zoneRules = zoneId.getRules();
            ZonalOffset initialOffset = ZonalOffset.ofTotalSeconds(zoneRules.getOffset(Instant.MIN).getTotalSeconds());
            List<ZonalTransition> transitions = new ArrayList<>();
            List<DaylightSavingRule> rules = new ArrayList<>();

            for (ZoneOffsetTransition zot : zoneRules.getTransitions()) {
                Instant instant = zot.getInstant();
                long posixTime = instant.getEpochSecond();
                int previousOffset = zot.getOffsetBefore().getTotalSeconds();
                int totalOffset = zot.getOffsetAfter().getTotalSeconds();
                int dst = Math.toIntExact(zoneRules.getDaylightSavings(instant).getSeconds());
                transitions.add(new ZonalTransition(posixTime, previousOffset, totalOffset, dst));
            }

            for (ZoneOffsetTransitionRule zotr : zoneRules.getTransitionRules()) {
                DaylightSavingRule rule;

                int dom = zotr.getDayOfMonthIndicator(); // -28 bis +31 (ohne 0)
                DayOfWeek dayOfWeek = zotr.getDayOfWeek();

                Month month = Month.valueOf(zotr.getMonth().getValue());

                PlainTime timeOfDay = (
                    zotr.isMidnightEndOfDay()
                        ? PlainTime.midnightAtEndOfDay()
                        : TemporalType.LOCAL_TIME.translate(zotr.getLocalTime()));

                OffsetIndicator indicator;
                switch (zotr.getTimeDefinition()) {
                    case STANDARD:
                        indicator = OffsetIndicator.STANDARD_TIME;
                        break;
                    case UTC:
                        indicator = OffsetIndicator.UTC_TIME;
                        break;
                    case WALL:
                        indicator = OffsetIndicator.WALL_TIME;
                        break;
                    default:
                        throw new UnsupportedOperationException(zotr.getTimeDefinition().name());
                }

                int dst = (zotr.getOffsetAfter().getTotalSeconds() - zotr.getStandardOffset().getTotalSeconds());

                if (dayOfWeek == null) {
                    rule = GregorianTimezoneRule.ofFixedDay(month, dom, timeOfDay, indicator, dst);
                } else {
                    Weekday wd = Weekday.valueOf(dayOfWeek.getValue());
                    if (dom == -1) {
                        rule = GregorianTimezoneRule.ofLastWeekday(month, wd, timeOfDay, indicator, dst);
                    } else if (dom < 0) {
                        rule = new NegativeDayOfMonthPattern(month, dom, wd, timeOfDay, indicator, dst);
                    } else {
                        rule = GregorianTimezoneRule.ofWeekdayAfterDate(month, dom, wd, timeOfDay, indicator, dst);
                    }
                }

                rules.add(rule);
            }

            return TransitionModel.of(initialOffset, transitions, rules);

        } catch (DateTimeException ex) {
            throw new IllegalArgumentException(ex);
        }

    }

}
