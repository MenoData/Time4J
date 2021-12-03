/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoInterval.java) is part of project Time4J.
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


/**
 * <p>Represents a temporal interval on a timeline. </p>
 *
 * <p>Note: Time4J-intervals contain every timepoint between start and
 * end boundary without exception (continuous intervals). The start must
 * not be after the end. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein Zeitintervall auf einem Zeitstrahl. </p>
 *
 * <p>Hinweis: Time4J-Intervalle enthalten jeden Zeitpunkt zwischen
 * Start und Ende ohne Ausnahme (kontinuierliche Intervalle). Der Start
 * darf niemals nach dem Ende liegen. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @author  Meno Hochschild
 * @since   2.0
 */
public interface ChronoInterval<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the lower bound of this interval. </p>
     *
     * @return  start interval boundary
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die untere Grenze dieses Intervalls. </p>
     *
     * @return  start interval boundary
     * @since   2.0
     */
    Boundary<T> getStart();

    /**
     * <p>Yields the upper bound of this interval. </p>
     *
     * @return  end interval boundary
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die obere Grenze dieses Intervalls. </p>
     *
     * @return  end interval boundary
     * @since   2.0
     */
    Boundary<T> getEnd();

    /**
     * <p>Determines if this interval has finite boundaries. </p>
     *
     * @return  {@code true} if start and end are finite else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Intervall endliche Grenzen hat. </p>
     *
     * @return  {@code true} if start and end are finite else {@code false}
     * @since   2.0
     */
    default boolean isFinite() {
        return !(this.getStart().isInfinite() || this.getEnd().isInfinite());
    }

    /**
     * <p>Determines if this interval is empty. </p>
     *
     * @return  {@code true} if this interval does not contain any time point else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Intervall leer ist. </p>
     *
     * @return  {@code true} if this interval does not contain any time point else {@code false}
     * @since   2.0
     */
    boolean isEmpty();

    /**
     * <p>Queries if given time point belongs to this interval. </p>
     *
     * @param   temporal    time point to be queried
     * @return  {@code true} if given time point belongs to this interval else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Ermittelt, ob der angegebene Zeitpunkt zu diesem Intervall
     * geh&ouml;rt. </p>
     *
     * @param   temporal    time point to be queried
     * @return  {@code true} if given time point belongs to this interval else {@code false}
     * @since   2.0
     */
    boolean contains(T temporal);

    /**
     * <p>Does this interval contain the other one? </p>
     *
     * <p>An interval cannot contain infinite intervals but can contain an empty interval if it contains
     * the start anchor of the empty interval. </p>
     *
     * @param   other       another interval whose relation to this interval is to be investigated
     * @return  {@code true} if this interval contains the other one else {@code false}
     * @see     #intersects(ChronoInterval)
     * @since   3.25/4.21
     */
    /*[deutsch]
     * <p>Enth&auml;lt dieses Intervall das andere Intervall? </p>
     *
     * <p>Ein Intervall kann nie unendliche Intervalle enthalten, aber sehr wohl ein leeres Intervall,
     * wenn es dessen Startanker enth&auml;lt. </p>
     *
     * @param   other       another interval whose relation to this interval is to be investigated
     * @return  {@code true} if this interval contains the other one else {@code false}
     * @see     #intersects(ChronoInterval)
     * @since   3.25/4.21
     */
    boolean contains(ChronoInterval<T> other);

    /**
     * <p>Is this interval after the given time point? </p>
     *
     * @param   temporal    reference time point
     * @return  {@code true} if this interval is after given time point else {@code false}
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall nach dem angegebenen Zeitpunkt? </p>
     *
     * @param   temporal    reference time point
     * @return  {@code true} if this interval is after given time point else {@code false}
     */
    boolean isAfter(T temporal);

    /**
     * <p>Is this interval after the other one? </p>
     *
     * @param   other       another interval whose relation to this interval is to be investigated
     * @return  {@code true} if this interval is after the other one else {@code false}
     * @since   3.25/4.21
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall nach dem anderen? </p>
     *
     * @param   other       another interval whose relation to this interval is to be investigated
     * @return  {@code true} if this interval is after the other one else {@code false}
     * @since   3.25/4.21
     */
    default boolean isAfter(ChronoInterval<T> other) {
        return other.isBefore(this);
    }

    /**
     * <p>Is this interval before the given time point? </p>
     *
     * @param   temporal    reference time point
     * @return  {@code true} if this interval is before given time point else {@code false}
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall vor dem angegebenen Zeitpunkt? </p>
     *
     * @param   temporal    reference time point
     * @return  {@code true} if this interval is before given time point else {@code false}
     */
    boolean isBefore(T temporal);

    /**
     * <p>Is this interval before the other one? </p>
     *
     * @param   other       another interval whose relation to this interval is to be investigated
     * @return  {@code true} if this interval is before the other one else {@code false}
     * @since   3.25/4.21
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall vor dem anderen? </p>
     *
     * @param   other       another interval whose relation to this interval is to be investigated
     * @return  {@code true} if this interval is before the other one else {@code false}
     * @since   3.25/4.21
     */
    boolean isBefore(ChronoInterval<T> other);

    /**
     * <p>Queries if this interval abuts the other one such that there is neither any overlap nor any gap between. </p>
     * 
     * <p>Note: Empty intervals never abut. </p>
     *
     * @param   other       another interval which might abut this interval
     * @return  {@code true} if there is no intersection and no gap between else {@code false}
     * @since   3.25/4.21
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Intervall das angegebene Intervall so ber&uuml;hrt, da&szlig;
     * weder eine &Uuml;berlappung noch eine L&uuml;cke dazwischen existieren. </p>
     *
     * <p>Hinweis: Leere Intervalle ber&uuml;hren sich nie. </p>
     *
     * @param   other       another interval which might abut this interval
     * @return  {@code true} if there is no intersection and no gap between else {@code false}
     * @since   3.25/4.21
     */
    boolean abuts(ChronoInterval<T> other);

    /**
     * <p>Queries if this interval intersects the other one such that there is at least one common time point. </p>
     *
     * <p>In contrast to {@link #contains(ChronoInterval)}, an interval can never intersect an empty interval. </p>
     *
     * @param   other       another interval which might have an intersection with this interval
     * @return  {@code true} if there is an non-empty intersection of this interval and the other one else {@code false}
     * @see     #isBefore(ChronoInterval)
     * @see     #isAfter(ChronoInterval)
     * @since   3.25/4.21
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Intervall sich mit dem angegebenen Intervall so &uuml;berschneidet, da&szlig;
     * mindestens ein gemeinsamer Zeitpunkt existiert. </p>
     *
     * <p>Im Unterschied zu {@link #contains(ChronoInterval)} kann sich ein Intervall niemals mit einem leeren
     * Intervall &uuml;berschneiden. </p>
     *
     * @param   other       another interval which might have an intersection with this interval
     * @return  {@code true} if there is an non-empty intersection of this interval and the other one else {@code false}
     * @since   3.25/4.21
     * @see     #isBefore(ChronoInterval)
     * @see     #isAfter(ChronoInterval)
     */
    default boolean intersects(ChronoInterval<T> other) {
        if (this.isEmpty() || other.isEmpty()) {
            return false;
        }
        return !(this.isBefore(other) || this.isAfter(other));
    }

}