/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeLine.java) is part of project Time4J.
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

package net.time4j.engine;

import java.util.Comparator;


/**
 * <p>Represents a time axis where a point in time can be moved forward or
 * backward. </p>
 *
 * <p>As step width, the associated time axis will usually use the smallest
 * registered time unit. </p>
 *
 * @param   <T> generic type of time points
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Zeitachse, entlang der ein Zeitpunkt schrittweise
 * vorw&auml;rts oder zur&uuml;ck gesetzt werden kann. </p>
 *
 * <p>Als Schrittweite wird die zugeh&ouml;rige Zeitachse gew&ouml;hnlich die
 * kleinste registrierte Zeiteinheit verwenden. </p>
 *
 * @param   <T> generic type of time points
 * @author  Meno Hochschild
 * @since   2.0
 */
public interface TimeLine<T>
    extends Comparator<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Move given point in time forward by one step. </p>
     *
     * @param   timepoint       point in time to be moved forward
     * @return  new point in time one step after given argument
     *          or {@code null} if applied on the maximum of timeline
     * @since   2.0
     */
    /*[deutsch]
     * <p>Setzt den angegebenen Zeitpunkt einen Schritt vorw&auml;rts. </p>
     *
     * @param   timepoint       point in time to be moved forward
     * @return  new point in time one step after given argument
     *          or {@code null} if applied on the maximum of timeline
     * @since   2.0
     */
    T stepForward(T timepoint);

    /**
     * <p>Move given point in time backwards by one step. </p>
     *
     * @param   timepoint       point in time to be moved backwards
     * @return  new point in time one step before given argument
     *          or {@code null} if applied on the minimum of timeline
     * @since   2.0
     */
    /*[deutsch]
     * <p>Setzt den angegebenen Zeitpunkt einen Schritt
     * r&uuml;ckw&auml;rts. </p>
     *
     * @param   timepoint       point in time to be moved backwards
     * @return  new point in time one step before given argument
     *          or {@code null} if applied on the minimum of timeline
     * @since   2.0
     */
    T stepBackwards(T timepoint);

    /**
     * Determines if this timeline is calendrical or not.
     *
     * @return  boolean
     * @see     CalendarDate
     * @since   3.36/4.31
     */
    /*[deutsch]
     * Ermittelt, ob diese Zeitachse kalendarisch ist oder nicht.
     *
     * @return  boolean
     * @see     CalendarDate
     * @since   3.36/4.31
     */
    boolean isCalendrical();

}
