/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoDisplay.java) is part of project Time4J.en
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

import net.time4j.tz.TZID;


/**
 * <p>Represents a view on a set of chronological elements associated
 * with their temporal values. </p>
 *
 * <p>A {@code ChronoDisplay} serves mainly for formatted output. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein Zeitwertobjekt, das einzelne Werte mit
 * chronologischen Elementen assoziiert und einen Lesezugriff auf diese
 * Werte erlaubt. </p>
 *
 * <p>Ein {@code ChronoDisplay} dient haupts&auml;chlich der formatierten
 * Darstellung. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public interface ChronoDisplay {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Queries if the value for given chronological element can be
     * accessed via {@code get(element)}. </p>
     *
     * <p>If the argument is missing then this method will yield {@code false}.
     * Note: Elements which are not registered but define a suitable rule
     * are also accessible. </p>
     *
     * @param   element     chronological element to be asked (optional)
     * @return  {@code true} if the element is registered or there is an element rule for evaluating the value
     *          else {@code false}
     * @see     #get(ChronoElement)
     */
    /*[deutsch]
     * <p>Ist der Wert zum angegebenen chronologischen Element abfragbar
     * beziehungsweise enthalten? </p>
     *
     * <p>Fehlt das Argument, dann liefert die Methode {@code false}. Zu
     * beachten: Es werden hier nicht nur registrierte Elemente als abfragbar
     * gewertet, sondern auch solche, die z.B. eine passende chronologische
     * Regel definieren. </p>
     *
     * @param   element     chronological element to be asked (optional)
     * @return  {@code true} if the element is registered or there is an element rule for evaluating the value
     *          else {@code false}
     * @see     #get(ChronoElement)
     */
    boolean contains(ChronoElement<?> element);

    /**
     * <p>Returns the partial value associated with given chronological
     * element. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element which has the value
     * @return  associated element value as object (never {@code null})
     * @throws  ChronoException if there is no suitable element rule for evaluating the value
     *          or if the associated element value is not defined over the complete range of this instance
     * @see     #contains(ChronoElement)
     */
    /*[deutsch]
     * <p>Fragt ein chronologisches Element nach seinem Wert als Objekt ab. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element which has the value
     * @return  associated element value as object (never {@code null})
     * @throws  ChronoException if there is no suitable element rule for evaluating the value
     *          or if the associated element value is not defined over the complete range of this instance
     * @see     #contains(ChronoElement)
     */
    <V> V get(ChronoElement<V> element);

    /**
     * <p>Yields the minimum value of given chronological element in the
     * current context of this object. </p>
     *
     * <p>The definition of a minimum and a maximum does generally not
     * imply that every intermediate value between minimum and maximum
     * is valid in this context. For example in the timezone Europe/Berlin
     * the hour [T02:00] will be invalid if switching to summer time. </p>
     *
     * <p>In most cases the minimum value is not dependent on this
     * context. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element whose minimum value is to be evaluated
     * @return  minimum maybe context-dependent element value
     * @throws  ChronoException if there is no suitable element rule for evaluating the minimum value
     *          or if the associated element value is not defined over the complete range of this instance
     * @see     ChronoElement#getDefaultMinimum()
     * @see     #getMaximum(ChronoElement)
     */
    /*[deutsch]
     * <p>Ermittelt das Minimum des mit dem angegebenen chronologischen Element
     * assoziierten Elementwerts. </p>
     *
     * <p>Die Definition eines Minimums und eines Maximums bedeutet im
     * allgemeinen <strong>nicht</strong>, da&szlig; jeder Zwischenwert
     * innerhalb des Bereichs im Kontext g&uuml;ltig sein mu&szlig;. Zum
     * Beispiel wird bei Sommerzeitumstellungen in der Zeitzone Europe/Berlin
     * die Stunde [T02:00] komplett fehlen. </p>
     *
     * <p>Oft wird der Minimalwert von diesem Kontext unabh&auml;ngig sein. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element whose minimum value is to be evaluated
     * @return  minimum maybe context-dependent element value
     * @throws  ChronoException if there is no suitable element rule for evaluating the minimum value
     *          or if the associated element value is not defined over the complete range of this instance
     * @see     ChronoElement#getDefaultMinimum()
     * @see     #getMaximum(ChronoElement)
     */
    <V> V getMinimum(ChronoElement<V> element);

    /**
     * <p>Yields the maximum value of given chronological element in the
     * current context of this object. </p>
     *
     * <p>Maximum values are different from minimum case often dependent
     * on the context. An example is the element SECOND_OF_MINUTE whose
     * maximum is normally {@code 59} but can differ in UTC-context with
     * leap seconds. Another more common example is the maximum of the
     * element DAY_OF_MONTH (28-31) which is dependent on the month and year
     * of this context (leap years!). </p>
     *
     * <p>Note: In timezone-related timestamps possible offset jumps
     * inducing gaps on the local timeline will be conserved. That means
     * that minimum and maximum do not guarantee a continuum of valid
     * intermediate values. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element whose maximum value is to be evaluated
     * @return  maximum maybe context-dependent element value
     * @throws  ChronoException if there is no suitable element rule for evaluating the maximum value
     *          or if the associated element value is not defined over the complete range of this instance
     * @see     ChronoElement#getDefaultMaximum()
     * @see     #getMinimum(ChronoElement)
     */
    /*[deutsch]
     * <p>Ermittelt das Maximum des mit dem angegebenen chronologischen Element
     * assoziierten Elementwerts. </p>
     *
     * <p>Maximalwerte sind anders als Minima h&auml;ufig vom Kontext
     * abh&auml;ngig. Ein Beispiel sind Sekundenelemente und die Frage der
     * Existenz von Schaltsekunden im gegebenen Kontext (evtl. als Maximum
     * die Werte {@code 58} oder {@code 60} statt {@code 59} m&ouml;glich).
     * Ein anderes Beispiel ist der Maximalwert eines Tags (28-31), der
     * abh&auml;ngig vom Monats- und Jahreskontext (Schaltjahre!) ist. </p>
     *
     * <p>Zu beachten: In zeitzonen-bezogenen Zeitstempeln bleiben eventuelle
     * Zeitzonenspr&uuml;nge erhalten. Das bedeutet, da&szlig; Minimum und
     * Maximum nicht ber&uuml;cksichtigen, ob sie in eine L&uuml;cke fallen
     * oder zwischen ihnen ein Offset-Sprung existiert. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element whose maximum value is to be evaluated
     * @return  maximum maybe context-dependent element value
     * @throws  ChronoException if there is no suitable element rule for evaluating the maximum value
     *          or if the associated element value is not defined over the complete range of this instance
     * @see     ChronoElement#getDefaultMaximum()
     * @see     #getMinimum(ChronoElement)
     */
    <V> V getMaximum(ChronoElement<V> element);

    /**
     * <p>Queries if this object contains a timezone for display purposes. </p>
     *
     * @return  {@code true} if a timezone is available and can be achieved
     *          with {@link #getTimezone()} else {@code false}
     */
    /*[deutsch]
     * <p>Ermittelt, ob eine Zeitzone f&uuml;r Anzeigezwecke vorhanden ist. </p>
     *
     * @return  {@code true} if a timezone is available and can be achieved
     *          with {@link #getTimezone()} else {@code false}
     */
    boolean hasTimezone();

    /**
     * <p>Returns the associated timezone ID for display purposes
     * if available. </p>
     *
     * <p>Note: Although global types like {@code Moment} indeed have a
     * timezone reference (namely UTC+00:00), such types will not support
     * formatted output without explicitly giving a timezone for display
     * purposes. Therefore calling this method on global types will throw
     * an exception. This method is not just about any timezone reference
     * but a timezone designed for display purposes. </p>
     *
     * @return  timezone identifier if available
     * @throws  ChronoException if the timezone is not available
     * @see     #hasTimezone()
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Zeitzonen-ID f&uuml;r Anzeigezwecke,
     * wenn vorhanden. </p>
     *
     * <p>Hinweis: Obwohl globale Typen wie {@code Moment} sehr wohl einen
     * Zeitzonenbezug haben (n&auml;mlich UTC+00:00), werden sie keine
     * formatierte Ausgabe ohne eine explizite Zeitzonenangabe f&uuml;r
     * Anzeigezwecke unterst&uuml;tzen. Deshalb wird der Aufruf dieser
     * Methode bei globalen Typen eine Ausnahme werfen. Diese Methode ist
     * nicht einfach auf irgendeine Zeitzone bezogen, die vorhanden sein
     * mag, sondern explizit nur f&uuml;r Zeitzonen im Kontext von formatierten
     * Ausgaben gedacht. </p>
     *
     * @return  timezone identifier if available
     * @throws  ChronoException if the timezone is not available
     * @see     #hasTimezone()
     */
    TZID getTimezone();

}
