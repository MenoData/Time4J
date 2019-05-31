/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2019 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DayPartitionRule.java) is part of project Time4J.
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
import net.time4j.PlainTimestamp;

import java.util.Collections;
import java.util.List;


/**
 * <p>Represents a rule how to partition a day into disjunct clock intervals. </p>
 *
 * @author  Meno Hochschild
 * @see     DayPartitionBuilder
 * @see     DateInterval#streamPartitioned(DayPartitionRule)
 * @since   4.18
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Regel, wie ein Tag in einzelne Uhrzeitintervalle separiert wird. </p>
 *
 * @author  Meno Hochschild
 * @see     DayPartitionBuilder
 * @see     DateInterval#streamPartitioned(DayPartitionRule)
 * @since   4.18
 */
@FunctionalInterface
public interface DayPartitionRule {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the partitions for given date if defined. </p>
     *
     * <p>All resulting partitions are half-open clock intervals (start inclusive and end exclusive)
     * and refer only to given calendar date. </p>
     *
     * @param   date    the calendar date to be queried
     * @return  unmodifiable sorted list of canonical day partitions, maybe empty
     * @see     ClockInterval#comparator()
     * @see     IsoInterval#toCanonical()
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert die Tagesabschnitte zum angegebenen Datum, wenn definiert. </p>
     *
     * <p>Alle resultierenden Tagesabschnitte sind halboffene Uhrzeitintervalle (Start inklusive und Ende exklusive)
     * und beziehen sich ausschlie&szlig;lich auf das angegebene Kalenderdatum. </p>
     *
     * @param   date    the calendar date to be queried
     * @return  unmodifiable sorted list of canonical day partitions, maybe empty
     * @see     ClockInterval#comparator()
     * @see     IsoInterval#toCanonical()
     * @since   5.0
     */
    List<ChronoInterval<PlainTime>> getPartitions(PlainDate date);

    /**
     * <p>Combines this rule with another one. </p>
     *
     * <p>The new rule will never take into account dates which are excluded by either this rule or the given one. </p>
     *
     * @param   rule    another day partition rule to be combined with this one
     * @return  new combined rule
     * @see     #isExcluded(PlainDate)
     */
    /*[deutsch]
     * <p>Liefert die Kombination dieser und der angegebenen Regel. </p>
     *
     * <p>Die neue Regel wird nie ein Datum ber&uuml;cksichtigen, das entweder von dieser oder der angegebenen
     * Regel ausgeschlossen ist. </p>
     *
     * @param   rule    another day partition rule to be combined with this one
     * @return  new combined rule
     * @see     #isExcluded(PlainDate)
     */
    default DayPartitionRule and(DayPartitionRule rule) {
        return date ->
            this.isExcluded(date) || rule.isExcluded(date)
            ? Collections.emptyList()
            : IntervalCollection.onClockAxis()
                .plus(this.getPartitions(date))
                .plus(rule.getPartitions(date))
                .withBlocks()
                .getIntervals();
    }

    /**
     * <p>Determines if given date is excluded from creating day partitions. </p>
     *
     * <p>The default implementation is equivalent to {@code getPartition(date).isEmpty()}. </p>
     *
     * @param   date    the calendar date to be checked for exclusion
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ermittelt, ob das angegebene Datum die Erzeugung von Tagesabschnitten ausschlie&szlig;t. </p>
     *
     * <p>Die Standardimplementierung entspricht einfach {@code getPartition(date).isEmpty()}. </p>
     *
     * @param   date    the calendar date to be checked for exclusion
     * @return  boolean
     */
    default boolean isExcluded(PlainDate date) {
        return this.getPartitions(date).isEmpty();
    }

    /**
     * <p>Does this rule match given timestamp such that any rule interval contains it? </p>
     *
     * <p>Example: If this rule describes shop opening times then this method yields the answer to the
     * question if the shop is open at given timestamp. </p>
     *
     * @param   timestamp   the timestamp to be checked
     * @return  {@code true} if given timestamp fits to this rule else {@code false}
     * @since   4.20
     */
    /*[deutsch]
     * <p>Pr&uuml;ft, ob irgendein Zeitintervall dieser Regel den angegebenen Zeitstempel enth&auml;lt. </p>
     *
     * <p>Beispiel: Wenn diese Regel Laden&ouml;ffnungszeiten beschreibt, dann liefert diese Methode die
     * Antwort auf die Frage, ob der Laden zur angegebenen Zeit offen ist. </p>
     *
     * @param   timestamp   the timestamp to be checked
     * @return  {@code true} if given timestamp fits to this rule else {@code false}
     * @since   4.20
     */
    default boolean matches(PlainTimestamp timestamp) {
        for (ChronoInterval<PlainTime> interval : this.getPartitions(timestamp.toDate())) {
            if (interval.contains(timestamp.toTime())) {
                return true;
            }
        }
        return false;
    }

}
