/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BracketPolicy.java) is part of project Time4J.
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

import net.time4j.engine.CalendarDate;


/**
 * <p>Determines a suitable strategy for formatting the open or closed
 * state of interval boundaries. </p>
 *
 * <p>An open boundary will be printed as leading &quot;(&quot; or as
 * trailing &quot;)&quot;. A closed boundary will be printed as leading
 * &quot;[&quot; or as trailing &quot;]&quot;. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Legt die Format- und Interpretationsstrategie f&uuml;r Intervallklammern
 * fest, welche den offenen oder geschlossenen Zustand von Intervallgrenzen
 * anzeigen. </p>
 *
 * <p>Eine offene Intervallgrenze wird als f&uuml;hrende Klammer
 * &quot;(&quot; oder Endklammer &quot;)&quot; ausgegeben. Eine
 * geschlossene Intervallgrenze verwendet stattdessen die Klammern
 * &quot;[&quot; oder &quot;]&quot;. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public enum BracketPolicy {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>The brackets indicating the open or closed state of a boundary
     * will be printed if the boundary state deviates from standard. </p>
     *
     * <p>This setting is the default. Calendrical intervals are closed
     * by default. All other intervals are half-open (right-open). Infinite
     * intervals are always printed with brackets. </p>
     */
    /*[deutsch]
     * <p>Klammern, die den offenen oder geschlossenen Zustand einer
     * Intervallgrenze anzeigen, werden genau dann ausgegeben, wenn der
     * Zustand der Intervallgrenzen vom Standard abweicht. </p>
     *
     * <p>Diese Einstellung dient als Standardvorgabe. Kalendarische
     * Intervalle sind per Standard immer geschlossen, alle anderen
     * Intervalle halb-offen (rechts-offen). Unbegrenzte Intervalle
     * werden immer mit Klammern angezeigt. </p>
     */
    SHOW_WHEN_NON_STANDARD() {
        @Override
        public boolean display(ChronoInterval<?> interval) {
            if (interval.getStart().isOpen()) {
                return true;
            }

            Boundary<?> end = interval.getEnd();

            if (end.isInfinite()) {
                return true;
            } else {
                Object obj = end.getTemporal();
                if (obj instanceof CalendarDate) {
                    return end.isOpen();
                } else {
                    return end.isClosed();
                }
            }
        }
    },

    /**
     * <p>The brackets indicating the open or closed state of a boundary
     * will always be printed. </p>
     */
    /*[deutsch]
     * <p>Klammern, die den offenen oder geschlossenen Zustand einer
     * Intervallgrenze anzeigen, werden immer ausgegeben. </p>
     */
    SHOW_ALWAYS() {
        @Override
        public boolean display(ChronoInterval<?> interval) {
            return true;
        }
    },

    /**
     * <p>The brackets indicating the open or closed state of a boundary
     * will never be printed. </p>
     */
    /*[deutsch]
     * <p>Klammern, die den offenen oder geschlossenen Zustand einer
     * Intervallgrenze anzeigen, werden nie ausgegeben. </p>
     */
    SHOW_NEVER() {
        @Override
        public boolean display(ChronoInterval<?> interval) {
            return false;
        }
    };

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Decides if to display the boundaries of given interval. </p>
     *
     * @param   interval    interval whose boundaries are to be printed or not
     * @return  {@code true} if boundaries are displayed else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Entscheidet, ob die Grenzen des angegebenen Intervalls angezeigt
     * werden. </p>
     *
     * @param   interval    interval whose boundaries are to be printed or not
     * @return  {@code true} if boundaries are displayed else {@code false}
     * @since   2.0
     */
    public boolean display(ChronoInterval<?> interval) {
        throw new AbstractMethodError();
    }

}
