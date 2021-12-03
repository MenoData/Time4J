/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DayPartitionBuilder.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;


/**
 * <p>A mutable builder for creating day partition rules. </p>
 *
 * <p>This class enables the easy construction of daily shop opening times or weekly work time schedules.
 * Example: </p>
 *
 * <pre>
 *     DayPartitionRule rule =
 *      new DayPartitionBuilder()
 *          .addExclusion(PlainDate.of(2016, 8, 27))
 *          .addWeekdayRule(MONDAY, FRIDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
 *          .addWeekdayRule(MONDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(16, 0)))
 *          .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
 *          .build();
 *
 *      List&lt;TimestampInterval&gt; intervals = // determine all intervals for August and September in 2016
 *          DateInterval.between(PlainDate.of(2016, 8, 1), PlainDate.of(2016, 9, 30))
 *              .streamPartitioned(rule)
 *              .parallel()
 *              .collect(Collectors.toList());
 * </pre>
 *
 * @author  Meno Hochschild
 * @see     DayPartitionRule
 * @see     DateInterval#streamPartitioned(DayPartitionRule)
 * @since   4.18
 */
/*[deutsch]
 * <p>Dient der Erzeugung einer {@code DayPartitionRule}. </p>
 *
 * <p>Hiermit k&ouml;nnen t&auml;gliche Laden&ouml;ffnungszeiten oder w&ouml;chentliche Arbeitszeitschemata
 * auf einfache Weise erstellt werden. Beispiel: </p>
 *
 * <pre>
 *     DayPartitionRule rule =
 *      new DayPartitionBuilder()
 *          .addExclusion(PlainDate.of(2016, 8, 27))
 *          .addWeekdayRule(MONDAY, FRIDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
 *          .addWeekdayRule(MONDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(16, 0)))
 *          .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
 *          .build();
 *
 *      List&lt;TimestampInterval&gt; intervals = // ermittelt alle Intervalle f&uuml;r August und September 2016
 *          DateInterval.between(PlainDate.of(2016, 8, 1), PlainDate.of(2016, 9, 30))
 *              .streamPartitioned(rule)
 *              .parallel()
 *              .collect(Collectors.toList());
 * </pre>
 *
 * @author  Meno Hochschild
 * @see     DayPartitionRule
 * @see     DateInterval#streamPartitioned(DayPartitionRule)
 * @since   4.18
 */
public class DayPartitionBuilder {

    //~ Instanzvariablen --------------------------------------------------

    private final Predicate<PlainDate> activeFilter;
    private final Map<Weekday, List<ChronoInterval<PlainTime>>> weekdayRules;
    private final Map<PlainDate, List<ChronoInterval<PlainTime>>> exceptionRules;
    private final Set<PlainDate> exclusions;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new instance for building rules which are always active. </p>
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz zum Bauen von Regeln, die immer aktiv sind. </p>
     */
    public DayPartitionBuilder() {
        super();

        this.activeFilter = (date) -> true;
        this.weekdayRules = new EnumMap<>(Weekday.class);
        this.exceptionRules = new HashMap<>();
        this.exclusions = new HashSet<>();

    }

    /**
     * <p>Creates a new instance with given filter. </p>
     *
     * <p>If only one rule is to be applied then setting an active filter has a similar effect as setting
     * an exclusion date. However, if two or more rules with different filters are created then the new
     * combined day-partition-rule (based on and-chaining) will not completely exclude certain dates
     * only because of one partial rule with a special filter. </p>
     *
     * @param   activeFilter    determines when the rule to be created is active (should be stateless)
     * @see     DayPartitionRule#and(DayPartitionRule)
     * @since   4.19
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * <p>Wenn es nur um eine Regel gilt, dann hat das Setzen eines Aktivfilters eine &auml;hnliche Wirkung
     * wie das Anwenden eines Ausschlu&szlig;datums. Allerdings gibt es einen Unterschied, wenn zwei oder
     * mehr Regeln mit verschiedenen Filtern miteinander kombiniert werden. In letzterem Fall kennt die
     * mit Hilfe von {@code and()}-Ausdr&uuml;cken kombinierte Regel nicht automatisch ein Ausschlu&szlig;datum,
     * wenn der Filter nur einer Teilregel ein Datum ausschlie&szlig;t. </p>
     *
     * @param   activeFilter    determines when the rule to be created is active (should be stateless)
     * @see     DayPartitionRule#and(DayPartitionRule)
     * @since   4.19
     */
    public DayPartitionBuilder(Predicate<PlainDate> activeFilter) {
        super();

        if (activeFilter == null) {
            throw new NullPointerException("Missing active filter.");
        }

        this.activeFilter = activeFilter;
        this.weekdayRules = new EnumMap<>(Weekday.class);
        this.exceptionRules = new HashMap<>();
        this.exclusions = new HashSet<>();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Adds a rule to partition any calendar date. </p>
     *
     * @param   partition   a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     * @since   4.20
     */
    /*[deutsch]
     * <p>F&uuml;gt eine Regel hinzu, die irgendeinen Kalendertag passend zerlegt. </p>
     *
     * @param   partition   a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     * @since   4.20
     */
    public DayPartitionBuilder addDailyRule(ClockInterval partition) {

        for (Weekday dayOfWeek : Weekday.values()) {
            this.addWeekdayRule(dayOfWeek, partition);
        }

        return this;

    }

    /**
     * <p>Adds a rule to partition a date dependending on its day of week. </p>
     *
     * <p>This method can be called multiple times for the same day of week in order to define
     * more than one disjunct partition. </p>
     *
     * @param   dayOfWeek   controls the partitioning in the final day partition rule
     * @param   partition   a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     */
    /*[deutsch]
     * <p>F&uuml;gt eine Regel hinzu, die einen Kalendertag in Abh&auml;ngigkeit von seinem Wochentag zerlegt. </p>
     *
     * <p>Diese Methode kann mehrmals mit dem gleichen Wochentag aufgerufen werden, um mehr als einen
     * Tagesabschnitt zu definieren. </p>
     *
     * @param   dayOfWeek   controls the partitioning in the final day partition rule
     * @param   partition   a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     */
    public DayPartitionBuilder addWeekdayRule(
        Weekday dayOfWeek,
        ClockInterval partition
    ) {

        if (dayOfWeek == null) {
            throw new NullPointerException("Missing day of week.");
        }

        try {
            ClockInterval p = partition.toCanonical();

            if (!p.isEmpty()) {
                List<ChronoInterval<PlainTime>> ps = this.weekdayRules.get(dayOfWeek);
                if (ps == null) {
                    ps = new ArrayList<>(5);
                    ps.add(p);
                } else {
                    ps = IntervalCollection.onClockAxis().plus(ps).plus(p).withBlocks().getIntervals();
                }
                this.weekdayRules.put(dayOfWeek, ps);
            }

            return this;
        } catch (IllegalStateException ise) {
            throw new IllegalArgumentException(ise);
        }

    }

    /**
     * <p>Adds a rule to partition a date dependending on when its day of week falls into given range. </p>
     *
     * <p>This method can be called multiple times using the same arguments in order to define
     * more than one disjunct partition. </p>
     *
     * @param   from        starting day of week
     * @param   to          ending day of week
     * @param   partition   a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     * @since   4.20
     */
    /*[deutsch]
     * <p>F&uuml;gt eine Regel hinzu, die einen Kalendertag in Abh&auml;ngigkeit davon zerlegt, ob
     * dessen Wochentag in den angegebenen Wochentagsbereich f&auml;llt. </p>
     *
     * <p>Diese Methode kann mehrmals mit den gleichen Parametern aufgerufen werden, um mehr als einen
     * Tagesabschnitt zu definieren. </p>
     *
     * @param   from        starting day of week
     * @param   to          ending day of week (inclusive)
     * @param   partition   a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     * @since   4.20
     */
    public DayPartitionBuilder addWeekdayRule(
        Weekday from,
        Weekday to,
        ClockInterval partition
    ) {

        if (to.equals(from)) {
            return this.addWeekdayRule(from, partition);
        }

        Weekday current = from;

        do {
            this.addWeekdayRule(current, partition);
        } while (!(current = current.next()).equals(to));

        return this.addWeekdayRule(to, partition);

    }

    /**
     * <p>Adds a rule to partition a date dependending on when its day of week falls into given range. </p>
     *
     * <p>This method can be called multiple times using the same arguments in order to define
     * more than one disjunct partition. </p>
     *
     * @param   spanOfWeekdays  span of weekdays with start and end
     * @param   partition       a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     * @since   4.20
     */
    /*[deutsch]
     * <p>F&uuml;gt eine Regel hinzu, die einen Kalendertag in Abh&auml;ngigkeit davon zerlegt, ob
     * dessen Wochentag in den angegebenen Wochentagsbereich f&auml;llt. </p>
     *
     * <p>Diese Methode kann mehrmals mit den gleichen Parametern aufgerufen werden, um mehr als einen
     * Tagesabschnitt zu definieren. </p>
     *
     * @param   spanOfWeekdays  span of weekdays with start and end
     * @param   partition       a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     * @since   4.20
     */
    public DayPartitionBuilder addWeekdayRule(
        SpanOfWeekdays spanOfWeekdays,
        ClockInterval partition
    ) {

        return this.addWeekdayRule(spanOfWeekdays.getStart(), spanOfWeekdays.getEnd(), partition);

    }

    /**
     * <p>Adds a rule to partition a special calendar date. </p>
     *
     * <p>This method can be called multiple times for the same special day in order to define
     * more than one disjunct partition. </p>
     *
     * @param   specialDay  controls the partitioning in the final day partition rule
     * @param   partition   a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     */
    /*[deutsch]
     * <p>F&uuml;gt eine Regel hinzu, die einen speziellen Kalendertag zerlegt. </p>
     *
     * <p>Diese Methode kann mehrmals f&uuml;r das gleiche Datum aufgerufen werden, um mehr als einen
     * Tagesabschnitt zu definieren. </p>
     *
     * @param   specialDay  controls the partitioning in the final day partition rule
     * @param   partition   a clock interval
     * @return  this instance for method chaining
     * @throws  IllegalArgumentException if there is no canonical form of given interval (for example for [00:00/24:00])
     * @see     IsoInterval#toCanonical()
     */
    public DayPartitionBuilder addSpecialRule(
        PlainDate specialDay,
        ClockInterval partition
    ) {

        if (specialDay == null) {
            throw new NullPointerException("Missing special calendar date.");
        }

        try {
            ClockInterval p = partition.toCanonical();

            if (!p.isEmpty()) {
                List<ChronoInterval<PlainTime>> ps = this.exceptionRules.get(specialDay);
                if (ps == null) {
                    ps = new ArrayList<>(5);
                    ps.add(p);
                } else {
                    ps = IntervalCollection.onClockAxis().plus(ps).plus(p).withBlocks().getIntervals();
                }
                this.exceptionRules.put(specialDay, ps);
            }

            return this;
        } catch (IllegalStateException ise) {
            throw new IllegalArgumentException(ise);
        }

    }

    /**
     * <p>Adds an exclusion date. </p>
     *
     * @param   date    the calendar date to be excluded from creating day partitions
     * @return  this instance for method chaining
     * @see     DayPartitionRule#isExcluded(PlainDate)
     * @see     #addExclusion(Collection)
     */
    /*[deutsch]
     * <p>F&uuml;gt ein Ausschlu&szlig;datum hinzu. </p>
     *
     * @param   date    the calendar date to be excluded from creating day partitions
     * @return  this instance for method chaining
     * @see     DayPartitionRule#isExcluded(PlainDate)
     * @see     #addExclusion(Collection)
     */
    public DayPartitionBuilder addExclusion(PlainDate date) {

        if (date == null) {
            throw new NullPointerException("Missing exclusion date.");
        }

        this.exclusions.add(date);
        return this;

    }

    /**
     * <p>Adds multiple exclusion dates. </p>
     *
     * @param   dates    collection of calendar dates to be excluded from creating day partitions
     * @return  this instance for method chaining
     * @see     DayPartitionRule#isExcluded(PlainDate)
     * @see     #addExclusion(PlainDate)
     */
    /*[deutsch]
     * <p>F&uuml;gt eine Menge von Ausschlu&szlig;datumsobjekten hinzu. </p>
     *
     * @param   dates    collection of calendar dates to be excluded from creating day partitions
     * @return  this instance for method chaining
     * @see     DayPartitionRule#isExcluded(PlainDate)
     * @see     #addExclusion(PlainDate)
     */
    public DayPartitionBuilder addExclusion(Collection<PlainDate> dates) {

        dates.stream().forEach(this::addExclusion);
        return this;

    }

    /**
     * <p>Creates a new day partition rule. </p>
     *
     * @return  DayPartitionRule
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Regel zur Zerlegung eines Tages in einen oder mehrere Tagesabschnitte. </p>
     *
     * @return  DayPartitionRule
     */
    public DayPartitionRule build() {

        final Map<Weekday, List<ChronoInterval<PlainTime>>> wRules = new EnumMap<>(this.weekdayRules);
        final Map<PlainDate, List<ChronoInterval<PlainTime>>> eRules = new HashMap<>(this.exceptionRules);
        final Set<PlainDate> invalid = new HashSet<>(this.exclusions);

        return new DayPartitionRule() {
            @Override
            public List<ChronoInterval<PlainTime>> getPartitions(PlainDate date) {
                if (!this.isExcluded(date) && activeFilter.test(date)) {
                    List<ChronoInterval<PlainTime>> partitions = eRules.get(date);
                    if (partitions == null) {
                        partitions = wRules.get(date.getDayOfWeek());
                    }
                    if (partitions != null) {
                        return Collections.unmodifiableList(partitions);
                    }
                }
                return Collections.emptyList();
            }
            @Override
            public boolean isExcluded(PlainDate date) {
                return invalid.contains(date);
            }
        };

    }

}
