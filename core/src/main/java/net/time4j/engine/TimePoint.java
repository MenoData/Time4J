/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimePoint.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;

import java.io.Serializable;


/**
 * <p>Represents an immutable time point along a time axis which is directed
 * into the future. </p>
 *
 * <p><strong>Display and change chronological element values</strong></p>
 *
 * <p>The time point consists of chronological elements. This base class
 * delegates the time arithmetic to the associated time axis respective to
 * the underlying rules of elements and units. However, any concrete subclass
 * is required to define the state and reflect it in all {@code get()}-methods
 * and also to specify the serialization behaviour. </p>
 *
 * <p>Element values can only be changed by creating a new immutable copy
 * of the original instance. This is done via all {@code with()}-methods. </p>
 *
 * <p><strong>Time axis</strong></p>
 *
 * <p>If the referenced time axis is the UTC-timeline (that is a time point
 * is defined relative to the start of UTC epoch - see
 * <a href="package-summary.html">package summary</a>) then any implementation
 * must also implement the interface {@link net.time4j.scale.UniversalTime}.
 * In every other case we have a local time axis. All time units are to be
 * defined referencing the time axis. For example, second units are interpreted
 * as local UT1-seconds on a local timestamp but on a {@code UniversalTime}
 * before 1972 as global UT1-seconds and after 1972 as atomic SI-seconds.
 * Hence Time4J has even defined different second units in the main package.
 * Applications should therefore take much care if they transform a duration
 * from one time axis to another one. </p>
 *
 * <p><strong>Sorting</strong></p>
 *
 * <p>Unless explicitly stated otherwise sorting of time points is always
 * in strict temporal order and consistent with {@code equals()}. In case
 * of doubt the documentation of the subclass is leading. Alternatively,
 * subclasses are free to implement the interface {@code Temporal} to
 * enable a temporal order. </p>
 *
 * <p><strong>Addition (or subtraction) of a time span to a time point</strong></p>
 *
 * <p>These operations are performed by all {@code plus()}- and {@code minus()}-
 * methods. A time span can either be a single time unit, or it consists of
 * several time units. </p>
 *
 * <p>If given time unit does not have a fixed length (for example months)
 * then the result of an addition can deviate from the expected element
 * value to be considered. In case of multiple additions care is required.
 * In case of doubt the original value should be saved for a later addition.
 * Example with additions of months in one or two steps (pseudo-code): </p>
 *
 * <ul>
 *  <li>[2011-08-31] + [P1M] = [2011-09-30]</li>
 *  <li>[2011-09-30] + [P1M] = [2011-10-30]</li>
 *  <li>[2011-08-31] + [P2M] = [2011-10-31]</li>
 * </ul>
 *
 * <p><strong>Difference of time points</strong></p>
 *
 * <p>The difference of time points results in a time span. The result can
 * either be expressed in one time unit only, or in multiple units which
 * represent the base unit of associated chronological element. In latter
 * case users have to define a metric, too. </p>
 *
 * <p><strong>Implementation notes</strong></p>
 *
 * <ul>
 *  <li>All subclasses must be <i>final</i> und <i>immutable</i>. </li>
 *  <li>Documentation of supported and registered elements is required. </li>
 *  <li>For a suitable choice of the type U of time units it should be
 *  noted that the time units must correspond to the internal state of
 *  a time point because otherwise the calculation of a time span between
 *  two time points cannot be complete. For example it would be a mistake
 *  to allow milliseconds for a time point which even has nanoseconds. </li>
 *  <li>The natural order should be consistent with {@code equals()}. </li>
 * </ul>
 *
 * @param   <U> generic type of time units compatible to {@link ChronoUnit}
 * @param   <T> generic type of self reference
 * @author  Meno Hochschild
 * @serial  exclude
 * @see     Chronology
 * @see     TimeAxis
 * @see     Temporal
 */
/*[deutsch]
 * <p>Repr&auml;sentiert einen unver&auml;nderlichen Zeitpunkt auf einer in
 * die Zukunft gerichteten Zeitachse. </p>
 *
 * <p><strong>Chronologische Elementwerte anzeigen und &auml;ndern</strong></p>
 *
 * <p>Der Zeitwert setzt sich aus chronologischen Elementen zusammen. Diese
 * abstrakte Basisklasse delegiert die Zeitrechnung immer an die zugeh&ouml;rige
 * Zeitachse bzw. genauer an die ihr zugeordneten Regeln der Elemente und
 * Zeiteinheiten, mu&szlig; aber selbst den Zustand definieren, in den
 * {@code get()}-Methoden den Zustand reflektieren und auch das
 * Serialisierungsverhalten festlegen. </p>
 *
 * <p>Da alle konkreten Implementierungen <i>immutable</i> sind und sein
 * m&uuml;ssen, sind Elementwerte nur dadurch &auml;nderbar, da&szlig; jeweils
 * eine neue Instanz mit ge&auml;nderten Elementwerten erzeugt wird. Das wird
 * unter anderem von allen {@code with()}-Methoden geleistet. </p>
 *
 * <p><strong>Zeitstrahl</strong></p>
 *
 * <p>Ist die referenzierte Zeitachse die UTC-Weltzeitlinie, wenn also
 * ein Zeitpunkt dann relativ zum Beginn der UTC-Epoche liegt (siehe
 * <a href="package-summary.html">Paketbeschreibung</a>), dann mu&szlig; eine
 * Implementierung auch das Interface {@link net.time4j.scale.UniversalTime}
 * implementieren. Sonst liegt eine lokale Zeitachse vor. Alle Zeiteinheiten
 * sind bez&uuml;glich der Zeitachse zu interpretieren. Zum Beispiel gelten
 * Sekundenzeiteinheiten auf einem lokalen Zeitstempel als lokale UT1-Sekunden
 * mit externem Zeitzonenkontext, auf einem {@code UniversalTime}-Objekt
 * vor 1972 als globale UT1-Sekunden und nach 1972 als atomare SI-Sekunden.
 * Deshalb sollten Anwendungen besondere Vorsicht walten lassen, wenn eine
 * Dauer von einer Zeitachse auf eine andere Zeitachse &uuml;bertragen wird. </p>
 *
 * <p><strong>Sortierung</strong></p>
 *
 * <p>Wenn nicht ausdr&uuml;cklich anders dokumentiert, wird die Sortierung
 * von Zeitpunkten immer rein zeitlich definiert und konsistent mit
 * {@code equals()} sein. Im Zweifelsfall ist die Dokumentation der
 * konkreten Subklasse ma&szlig;geblich. Alternativ k&ouml;nnen Subklassen
 * auch das Interface {@code Temporal} implementieren, um eine rein
 * zeitliche Ordnung zu erm&ouml;glichen. </p>
 *
 * <p><strong>Addition (oder Subtraktion) einer Zeitspanne zu einem Zeitpunkt</strong></p>
 *
 * <p>Diese Operationen werden von allen {@code plus()}- und {@code minus()}-
 * Methoden geleistet. Eine Zeitspanne kann entweder nur eine einzelne
 * Zeiteinheit sein oder ist aus mehreren Zeiteinheiten zusammengesetzt. </p>
 *
 * <p>Wenn die angegebene Zeiteinheit bzw. Genauigkeit keine konstante
 * L&auml;nge definiert (zum Beispiel haben Monate in Tagen ausgedr&uuml;ckt
 * im allgemeinen unterschiedliche L&auml;ngen), dann kann das Ergebnis
 * einer Addition im Hinblick auf den erwarteten Elementwert abweichen. Bei
 * mehrfachen Additionen hintereinander ist Vorsicht angebracht, im
 * Zweifelsfall sollte der Originalwert f&uuml;r eine sp&auml;tere Addition
 * gespeichert werden. Beispiel mit Additionen von Monaten in einem oder in
 * zwei Schritten (Pseudo-Code): </p>
 *
 * <ul>
 *  <li>[2011-08-31] + [P1M] = [2011-09-30]</li>
 *  <li>[2011-09-30] + [P1M] = [2011-10-30]</li>
 *  <li>[2011-08-31] + [P2M] = [2011-10-31]</li>
 * </ul>
 *
 * <p><strong>Differenz von Zeitpunkten</strong></p>
 *
 * <p>Die Differenz von Zeitpunkten resultiert jeweils in einer Zeitspanne.
 * Das Ergebnis kann entweder in nur einer Zeiteinheit ausgedr&uuml;ckt werden,
 * oder in mehreren Zeiteinheiten, die jede die Basiseinheit des entsprechenden
 * chronologischen Elements repr&auml;sentieren. In letzterem Fall ist auch
 * eine Metrik anzugeben. </p>
 *
 * <p><strong>Implementierungshinweise</strong></p>
 *
 * <ul>
 *  <li>Alle Subklassen m&uuml;ssen <i>final</i> und <i>immutable</i>
 *  sein. </li>
 *  <li>Es mu&szlig; dokumentiert werden, welche chronologischen Elemente
 *  unterst&uuml;tzt werden bzw. registriert sind. </li>
 *  <li>Zur Wahl des Zeiteinheitstyps U ist zu beachten, da&szlig; die
 *  Zeiteinheiten mit dem internen Zeitwertzustand zusammenpassen m&uuml;ssen,
 *  weil sonst die Berechnung der Zeitspanne zwischen zwei Zeitpunkten nicht
 *  vollst&auml;ndig m&ouml;glich sein kann. Zum Beispiel w&auml;re es ein
 *  Fehler, wenn die Zeiteinheiten maximal in Millisekunden genau sind, aber
 *  die konkreten Zeitpunkte sogar Nanosekunden enthalten. </li>
 *  <li>Die nat&uuml;rliche Ordnung sollte konsistent mit {@code equals()}
 *  sein. </li>
 * </ul>
 *
 * @param   <U> generic type of time units compatible to {@link ChronoUnit}
 * @param   <T> generic type of self reference
 * @author  Meno Hochschild
 * @serial  exclude
 * @see     Chronology
 * @see     TimeAxis
 * @see     Temporal
 */
public abstract class TimePoint<U, T extends TimePoint<U, T>>
    extends ChronoEntity<T>
    implements Comparable<T>, Serializable {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Compares two time points preferably by their temporal positions
     * on the common time axis. </p>
     *
     * <p>Implementation note: In order to make the natural order consistent
     * with {@code equals()} the whole state must be taken into account,
     * with preference for those attributes which define the temporal
     * position on the time axis. </p>
     *
     * @see     #equals(Object)
     */
    /*[deutsch]
     * <p>Vergleicht zwei Zeitpunkte bevorzugt nach ihrer Position auf der
     * gemeinsamen Zeitachse. </p>
     *
     * <p>Implementierungshinweis: Damit die nat&uuml;rliche Ordnung konsistent
     * mit {@code equals()} ist, m&uuml;ssen zum Vergleich alle internen
     * Zustandsattribute herangezogen werden, bevorzugt aber die Attribute,
     * die die zeitliche Position festlegen. </p>
     *
     * @see     #equals(Object)
     */
    @Override
    public abstract int compareTo(T timePoint);

    /**
     * <p>Adds the given time span to this time point and yields
     * the result of the addition. </p>
     *
     * <p>Delegates to {@link TimeSpan#addTo(TimePoint)}. </p>
     *
     * @param   timeSpan    time span to be added to this instance
     * @return  result of addition as changed copy, this
     *          instance remains unaffected
     * @throws  RuleNotFoundException if any time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #minus(TimeSpan)
     */
    /*[deutsch]
     * <p>Addiert die angegebene Zeitspanne zur Bezugszeit und liefert das
     * Additionsergebnis zur&uuml;ck. </p>
     *
     * <p>Delegiert an {@link TimeSpan#addTo(TimePoint)}. </p>
     *
     * @param   timeSpan    time span to be added to this instance
     * @return  result of addition as changed copy, this
     *          instance remains unaffected
     * @throws  RuleNotFoundException if any time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #minus(TimeSpan)
     */
    public T plus(TimeSpan<? extends U> timeSpan) {

        try {
            return timeSpan.addTo(this.getContext());
        } catch (IllegalArgumentException iae) {
            ArithmeticException ex =
                new ArithmeticException(
                    "Result beyond boundaries of time axis.");
            ex.initCause(iae);
            throw ex;
        }

    }

    /**
     * <p>Subtracts given time span from this time point and yields
     * the result of subtraction. </p>
     *
     * <p>Delegiert an {@link TimeSpan#subtractFrom(TimePoint)}. </p>
     *
     * @param   timeSpan    time span to be subtracted from this instance
     * @return  result of subtraction as changed copy, this
     *          instance remains unaffected
     * @throws  RuleNotFoundException if any time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #plus(TimeSpan)
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebene Zeitspanne von der Bezugszeit und
     * liefert das Subtraktionsergebnis zur&uuml;ck. </p>
     *
     * <p>Delegiert an {@link TimeSpan#subtractFrom(TimePoint)}. </p>
     *
     * @param   timeSpan    time span to be subtracted from this instance
     * @return  result of subtraction as changed copy, this
     *          instance remains unaffected
     * @throws  RuleNotFoundException if any time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #plus(TimeSpan)
     */
    public T minus(TimeSpan<? extends U> timeSpan) {

        try {
            return timeSpan.subtractFrom(this.getContext());
        } catch (IllegalArgumentException iae) {
            ArithmeticException ex =
                new ArithmeticException(
                    "Result beyond boundaries of time axis.");
            ex.initCause(iae);
            throw ex;
        }

    }

    /**
     * <p>Adds given amount in units to this time point and yields the
     * result of addition. </p>
     *
     * <p>Similar to {@link #plus(TimeSpan)} but with the difference
     * that the timespan is only given in one single time unit. Example
     * in pseudo-code: </p>
     *
     * <ul>
     *  <li>[2011-05-31].plus(1, &lt;MONTHS&gt;) = [2011-06-30]</li>
     *  <li>[2011-05-31].plus(4, &lt;DAYS&gt;) = [2011-06-04]</li>
     *  <li>[2011-06-04].plus(-4, &lt;DAYS&gt;) = [2011-05-31]</li>
     *  <li>[2010-04-29].plus(397, &lt;DAYS&gt;) = [2011-05-31]</li>
     *  <li>[2010-04-29].plus(13, &lt;MONTHS&gt;) = [2011-05-29]</li>
     *  <li>[2010-04-29].plus(-2, &lt;MONTHS&gt;) = [2010-02-28]</li>
     *  <li>[2010-04-29].plus(1, &lt;YEARS&gt;) = [2011-04-29]</li>
     * </ul>
     *
     * @param   amount      amount to be added (maybe negative)
     * @param   unit        time unit
     * @return  result of addition as changed copy, this instance
     *          remains unaffected
     * @throws  RuleNotFoundException if given time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #plus(TimeSpan)
     */
    /*[deutsch]
     * <p>Addiert den angegebenen Betrag der entsprechenden Zeiteinheit
     * zu dieser Bezugszeit und liefert das Additionsergebnis zur&uuml;ck. </p>
     *
     * <p>&Auml;hnlich wie {@link #plus(TimeSpan)}, aber mit dem Unterschied,
     * da&szlig; die Zeitspanne in nur einer Zeiteinheit angegeben wird.
     * Beispiel in Pseudo-Code: </p>
     *
     * <ul>
     *  <li>[2011-05-31].plus(1, &lt;MONTHS&gt;) = [2011-06-30]</li>
     *  <li>[2011-05-31].plus(4, &lt;DAYS&gt;) = [2011-06-04]</li>
     *  <li>[2011-06-04].plus(-4, &lt;DAYS&gt;) = [2011-05-31]</li>
     *  <li>[2010-04-29].plus(397, &lt;DAYS&gt;) = [2011-05-31]</li>
     *  <li>[2010-04-29].plus(13, &lt;MONTHS&gt;) = [2011-05-29]</li>
     *  <li>[2010-04-29].plus(-2, &lt;MONTHS&gt;) = [2010-02-28]</li>
     *  <li>[2010-04-29].plus(1, &lt;YEARS&gt;) = [2011-04-29]</li>
     * </ul>
     *
     * @param   amount      amount to be added (maybe negative)
     * @param   unit        time unit
     * @return  result of addition as changed copy, this instance
     *          remains unaffected
     * @throws  RuleNotFoundException if given time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #plus(TimeSpan)
     */
    public T plus(
        long amount,
        U unit
    ) {

        if (amount == 0) {
            return this.getContext();
        }

        try {
            return this.getRule(unit).addTo(this.getContext(), amount);
        } catch (IllegalArgumentException iae) {
            ArithmeticException ex =
                new ArithmeticException(
                    "Result beyond boundaries of time axis.");
            ex.initCause(iae);
            throw ex;
        }

    }

    /**
     * <p>Subtracts given amount in units from this time point and
     * yields the result of subtraction. </p>
     *
     * @param   amount      amount to be subtracted (maybe negative)
     * @param   unit        time unit
     * @return  result of subtraction as changed copy, this instance
     *          remains unaffected
     * @throws  RuleNotFoundException if given time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #plus(long, Object) plus(long, U)
     */
    /*[deutsch]
     * <p>Subtrahiert den angegebenen Betrag der entsprechenden Zeiteinheit
     * von dieser Bezugszeit und liefert das Ergebnis zur&uuml;ck. </p>
     *
     * @param   amount      amount to be subtracted (maybe negative)
     * @param   unit        time unit
     * @return  result of subtraction as changed copy, this instance
     *          remains unaffected
     * @throws  RuleNotFoundException if given time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #plus(long, Object) plus(long, U)
     */
    public T minus(
        long amount,
        U unit
    ) {

        return this.plus(MathUtils.safeNegate(amount), unit);

    }

    /**
     * <p>Calculates the (most normalized) time span between this time point
     * and given end time point using the given metric. </p>
     *
     * @param   <P> generic type of time span result
     * @param   end             end time point
     * @param   metric          temporal distance metric
     * @return  difference between this and given end time point
     *          expressed as time span
     * @throws  ArithmeticException in case of numerical overflow
     */
    /*[deutsch]
     * <p>Ermittelt die (meist normalisierte) Zeitspanne zwischen dieser
     * Bezugszeit und dem Endzeitpunkt in der angegebenen Metrik. </p>
     *
     * @param   <P> generic type of time span result
     * @param   end             end time point
     * @param   metric          temporal distance metric
     * @return  difference between this and given end time point
     *          expressed as time span
     * @throws  ArithmeticException in case of numerical overflow
     */
    public <P> P until(
        T end,
        TimeMetric<? extends U, P> metric
    ) {

        return metric.between(this.getContext(), end);

    }

    /**
     * <p>Calculates the temporal distance between this time point and
     * given end time point in only one time unit. </p>
     *
     * <p>Similar to {@link #until(TimePoint, TimeMetric)} but with the
     * difference that the time span is onyl calculated in one time unit
     * as long-primitive. In many cases a remainder of subtraction will
     * be left if given unit is not the smallest possible unit. Time points
     * whose element values differ less than one base unit will be
     * considered as equal. Examples in pseudo-code: </p>
     *
     * <ul>
     *  <li>[2011-05-31].until([2011-06-04], &lt;MONTHS&gt;) = 0</li>
     *  <li>[2011-05-31].until([2011-06-04], &lt;DAYS&gt;) = 4</li>
     *  <li>[2011-06-04].until([2011-05-31], &lt;DAYS&gt;) = -4</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;DAYS&gt;) = 397</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;MONTHS&gt;) = 13</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;YEARS&gt;) = 1</li>
     *  <li>[2010-05-31].until([2011-05-31], &lt;YEARS&gt;) = 1</li>
     *  <li>[2010-06-01].until([2011-05-31], &lt;YEARS&gt;) = 0</li>
     * </ul>
     *
     * @param   end     end time point
     * @param   unit    time unit
     * @return  difference between this and given end time point
     *          as count of given time unit
     * @throws  RuleNotFoundException if given time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #until(TimePoint, TimeMetric)
     */
    /*[deutsch]
     * <p>Ermittelt den zeitlichen Abstand zwischen dieser Bezugszeit und dem
     * angegebenen Zeitpunkt nur in einer Zeiteinheit zur&uuml;ck. </p>
     *
     * <p>&Auml;hnlich wie {@link #until(TimePoint, TimeMetric)}, aber mit
     * dem Unterschied, da&szlig; die Zeitspanne nur in einer Zeiteinheit als
     * long-Primitive ermittelt wird. Es wird meist ein Subtraktionsrest
     * verbleiben, wenn es sich nicht um die kleinstm&ouml;gliche bzw. genaueste
     * Zeiteinheit handelt. Zeitpunkte, deren Elementwerte sich um weniger als
     * eine Einheit unterscheiden, gelten als gleich. Beispiele in
     * Pseudo-Code: </p>
     *
     * <ul>
     *  <li>[2011-05-31].until([2011-06-04], &lt;MONTHS&gt;) = 0</li>
     *  <li>[2011-05-31].until([2011-06-04], &lt;DAYS&gt;) = 4</li>
     *  <li>[2011-06-04].until([2011-05-31], &lt;DAYS&gt;) = -4</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;DAYS&gt;) = 397</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;MONTHS&gt;) = 13</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;YEARS&gt;) = 1</li>
     *  <li>[2010-05-31].until([2011-05-31], &lt;YEARS&gt;) = 1</li>
     *  <li>[2010-06-01].until([2011-05-31], &lt;YEARS&gt;) = 0</li>
     * </ul>
     *
     * @param   end     end time point
     * @param   unit    time unit
     * @return  difference between this and given end time point
     *          as count of given time unit
     * @throws  RuleNotFoundException if given time unit is not registered
     *          and does also not implement {@link BasicUnit} to yield
     *          a suitable unit rule for the underlying time axis
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #until(TimePoint, TimeMetric)
     */
    public long until(
        T end,
        U unit
    ) {

        return this.getRule(unit).between(this.getContext(), end);

    }

    /**
     * <p>Determines the minimum of both time points. </p>
     *
     * @param   <U> generic type of time units compatible to {@link ChronoUnit}
     * @param   <T> generic type of self reference
     * @param   t1      first time point
     * @param   t2      second time point
     * @return  minimum of t1 and t2
     */
    /*[deutsch]
     * <p>Bestimmt das Minimum der beiden Zeitpunkte. </p>
     *
     * @param   <U> generic type of time units compatible to {@link ChronoUnit}
     * @param   <T> generic type of self reference
     * @param   t1      first time point
     * @param   t2      second time point
     * @return  minimum of t1 and t2
     */
    public static <U, T extends TimePoint<U, T>> T min(
        T t1,
        T t2
    ) {

        return (t1.compareTo(t2) > 0) ? t2 : t1;

    }

    /**
     * <p>Determines the maximum of both time points. </p>
     *
     * @param   <U> generic type of time units compatible to {@link ChronoUnit}
     * @param   <T> generic type of self reference
     * @param   t1      first time point
     * @param   t2      second time point
     * @return  maximum of t1 and t2
     */
    /*[deutsch]
     * <p>Bestimmt das Maximum der beiden Zeitpunkte. </p>
     *
     * @param   <U> generic type of time units compatible to {@link ChronoUnit}
     * @param   <T> generic type of self reference
     * @param   t1      first time point
     * @param   t2      second time point
     * @return  maximum of t1 and t2
     */
    public static <U, T extends TimePoint<U, T>> T max(
        T t1,
        T t2
    ) {

        return (t1.compareTo(t2) > 0) ? t1 : t2;

    }

    /**
     * <p>Compares the whole state of this instance with given object. </p>
     *
     * <p>Implementations will usually define their state only
     * based on the temporal position on the time axis because this
     * is the most intuitive approach. Exceptions from this rule should
     * be explicitly documented and reasoned. </p>
     *
     * @see     #compareTo(TimePoint)
     */
    /*[deutsch]
     * <p>Vergleicht den gesamten Zustand dieser Instanz mit dem des
     * angegebenen Objekts. </p>
     *
     * <p>Implementierungen werden &uuml;blicherweise ihren Zustand nur
     * auf Basis der zeitlichen Position definieren, da dies am ehesten
     * der Erwartungshaltung der Anwender entspricht. Ausnahmen sind
     * explizit zu dokumentieren und zu begr&uuml;nden. </p>
     *
     * @see     #compareTo(TimePoint)
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * <p>Subclasses must redefine this method corresponding to the
     * behaviour of {@code equals()}. </p>
     */
    /*[deutsch]
     * <p>Subklassen m&uuml;ssen diese Methode passend zum Verhalten
     * von {@code equals()} redefinieren. </p>
     */
    @Override
    public abstract int hashCode();

    /**
     * <p>Provides a complete textual representation of the state of
     * this time point. </p>
     *
     * <p>The textual description often follows the conventions of ISO-8601.
     * Usually the description starts with the chronological informations
     * which are coarse-grained and ends with those ones which are
     * fine-grained (for example the ISO-notation YYYY-MM-DD). </p>
     */
    /*[deutsch]
     * <p>Liefert eine vollst&auml;ndige Beschreibung des Zustands dieses
     * Zeitpunkts. </p>
     *
     * <p>Die textuelle Beschreibung folgt oft den Konventionen des
     * ISO-8601-Standards, indem zuerst die groben und dann die feineren
     * chronologischen Informationen mit h&ouml;herer Detailgenauigkeit folgen
     * (zum Beispiel die ISO-Notation YYYY-MM-DD). </p>
     */
    @Override
    public abstract String toString();

    /**
     * <p>Returns the assigned time axis which contains all necessary
     * chronological rules. </p>
     *
     * <p>Concrete subclasses must create in a <i>static initializer</i> a
     * time axis by help of {@code TimeAxis.Builder}, keep it as static
     * constant and make it available here. Using the procedure guarantees
     * that a basic set of registered elements, units and rules will be
     * installed. </p>
     *
     * @return  chronological system as time axis (never {@code null})
     * @see     TimeAxis.Builder
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse, die alle notwendigen
     * chronologischen Regeln enth&auml;lt. </p>
     *
     * <p>Konkrete Subklassen m&uuml;ssen in einem <i>static initializer</i>
     * mit Hilfe von {@code TimeAxis.Builder} eine Zeitachse bauen, in
     * einer eigenen Konstanten halten und hier verf&uuml;gbar machen.
     * &Uuml;ber dieses Verfahren wird zugleich ein Basissatz von Elementen,
     * Zeiteinheiten und chronologischen Regeln vorinstalliert. </p>
     *
     * @return  chronological system as time axis (never {@code null})
     * @see     TimeAxis.Builder
     */
    @Override
    protected abstract TimeAxis<U, T> getChronology();

    // Einheitsregel
    private UnitRule<T> getRule(U unit) {

        return this.getChronology().getRule(unit);

    }

}
