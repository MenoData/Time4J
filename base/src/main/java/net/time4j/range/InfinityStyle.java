/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (InfinityStyle.java) is part of project Time4J.
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

import net.time4j.engine.TimeLine;
import net.time4j.format.expert.ChronoPrinter;


/**
 * <p>Determines how to print infinite interval boundaries. </p>
 *
 * @author  Meno Hochschild
 * @since   4.18
 */
/*[deutsch]
 * <p>Legt fest, wie unbegrenzte Intervallgrenzen formatiert werden. </p>
 *
 * @author  Meno Hochschild
 * @since   4.18
 */
public enum InfinityStyle {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Printing infinite intervals is not supported by this style
     * and will throw an {@code IllegalStateException}. </p>
     */
    /*[deutsch]
     * <p>Die Formatierung unendlicher Intervall wird durch diesen Stil
     * mit Hilfe einer {@code IllegalStateException} verhindert. </p>
     */
    ABORT,

    /**
     * <p>The mathematical symbols &quot;-&#x221E;&quot; (infinite past)
     * or &quot;+&#x221E;&quot; (infinite future) will be used. </p>
     */
    /*[deutsch]
     * <p>Verwendet die mathematischen Symbole &quot;-&#x221E;&quot; (unbegrenzte
     * Vergangenheit) oder &quot;+&#x221E;&quot; (unbegrenzte Zukunft). </p>
     */
    SYMBOL() {
        @Override
        <T> String displayPast(
            ChronoPrinter<T> printer,
            TimeLine<T> timeLine
        ) {
            return "-\u221E";
        }
        @Override
        <T> String displayFuture(
            ChronoPrinter<T> printer,
            TimeLine<T> timeLine
        ) {
            return "+\u221E";
        }
    },

    /**
     * <p>The hyphen &quot;-&quot; will be used for infinite past as well
     * as for infinite future. </p>
     */
    /*[deutsch]
     * <p>Das Minuszeichen &quot;-&quot; wird sowohl f&uuml;r die unbegrenzte
     * Vergangenheit als auch f&uuml;r die unbegrenzte Zukunft verwendet. </p>
     */
    HYPHEN() {
        @Override
        <T> String displayPast(
            ChronoPrinter<T> printer,
            TimeLine<T> timeLine
        ) {
            return "-";
        }
        @Override
        <T> String displayFuture(
            ChronoPrinter<T> printer,
            TimeLine<T> timeLine
        ) {
            return "-";
        }
    },

    /**
     * <p>The minimum and maximum of the underlying time axis (timeline)
     * are used instead of the infinite boundaries. </p>
     *
     * <p>This style is syntactically in agreement with strict ISO-8601 standard
     * (which does not know infinite intervals at all). </p>
     */
    /*[deutsch]
     * <p>Das Minimum und Maximum der zugrundeliegenden Zeitachse (Zeitstrahl)
     * werden anstelle der unbegrenzten Intervallgrenzen verwendet. </p>
     *
     * <p>Dieser Stil ist syntaktisch in &Uuml;bereinstimmung mit dem ISO-8601-Standard,
     * der selbst keine unendlichen Intervalle kennt. </p>
     */
    MIN_MAX() {
        @Override
        <T> String displayPast(
            ChronoPrinter<T> printer,
            TimeLine<T> timeLine
        ) {
            return printer.print(timeLine.getMinimum());
        }
        @Override
        <T> String displayFuture(
            ChronoPrinter<T> printer,
            TimeLine<T> timeLine
        ) {
            return printer.print(timeLine.getMaximum());
        }
    };

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Prints the infinite past. </p>
     *
     * @param   printer     used for printing the minimum of timeline
     * @param   timeLine    underlying timeline
     * @return  formatted output
     * @throws  IllegalStateException if infinite intervals are not supported by this style
     */
    /*[deutsch]
     * <p>Gibt die unbegrenzte Vergangenheit aus. </p>
     *
     * @param   printer     used for printing the minimum of timeline
     * @param   timeLine    underlying timeline
     * @return  formatted output
     * @throws  IllegalStateException if infinite intervals are not supported by this style
     */
    <T> String displayPast(
        ChronoPrinter<T> printer,
        TimeLine<T> timeLine
    ) {
        throw new IllegalStateException("Infinite intervals are not supported.");
    }

    /**
     * <p>Prints the infinite future. </p>
     *
     * @param   printer     used for printing the maximum of timeline
     * @param   timeLine    underlying timeline
     * @return  formatted output
     * @throws  IllegalStateException if infinite intervals are not supported by this style
     */
    /*[deutsch]
     * <p>Gibt die unbegrenzte Zukunft aus. </p>
     *
     * @param   printer     used for printing the maximum of timeline
     * @param   timeLine    underlying timeline
     * @return  formatted output
     * @throws  IllegalStateException if infinite intervals are not supported by this style
     */
    <T> String displayFuture(
        ChronoPrinter<T> printer,
        TimeLine<T> timeLine
    ) {
        throw new IllegalStateException("Infinite intervals are not supported.");
    }

}
