/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoEntity.java) is part of project Time4J.
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
 * <p>Represents a temporal object which associates partial temporal values
 * with chronological elements and also allows some manipulations of these
 * element values. </p>
 *
 * <p>A {@code ChronoEntity} is usually a {@code TimePoint}, where the
 * (primary) element values determine the position on a time axis such
 * that a time arithmetic is possible. Alternatively a {@code ChronoEntity}
 * can also represent a partial information like the combination of month
 * and day-of-month for a simple display of a birthday. </p>
 *
 * <p>Chronological elements are either statically registered such that
 * a direct access is enabled, or there is an (external) rule which
 * enables read- and write-access. If no element rule can be found a
 * {@code RuleNotFoundException} will be thrown. </p>
 *
 * @param   <T> generic type of self reference
 * @author  Meno Hochschild
 * @spec    All public implementations must be immutable.
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein Zeitwertobjekt, das einzelne Werte mit
 * chronologischen Elementen assoziiert und einen Zugriff auf diese
 * Werte erlaubt. </p>
 *
 * <p>Eine {@code ChronoEntity} ist gew&ouml;hnlich ein {@code TimePoint},
 * bei dem die (prim&auml;ren) Elementwerte zusammen die Position auf einem
 * Zeitstrahl festlegen, so da&szlig; auch eine Zeitarithmetik m&ouml;glich
 * ist. Alternativ kann eine {@code ChronoEntity} eine chronologische
 * Teilinformation wie z.B. die Kombination aus Monat und Tag zur einfachen
 * Darstellung eines Geburtstags repr&auml;sentieren. </p>
 *
 * <p>Chronologische Elemente sind entweder vorab registriert, so da&szlig;
 * ein Zugriff direkt m&ouml;glich ist, oder es gibt eine Regel, die den
 * Lese- bzw. Schreibzugriff erm&ouml;glicht. Ist die Regel nicht vorhanden,
 * wird eine {@code RuleNotFoundException} geworfen. </p>
 *
 * @param   <T> generic type of self reference
 * @author  Meno Hochschild
 * @spec    All public implementations must be immutable.
 */
public abstract class ChronoEntity<T extends ChronoEntity<T>> {

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
     * @return  {@code true} if the method {@code get(ChronoElement)} can
     *          be called without exception else {@code false}
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
     * @return  {@code true} if the method {@code get(ChronoElement)} can
     *          be called without exception else {@code false}
     * @see     #get(ChronoElement)
     */
    public boolean contains(ChronoElement<?> element) {

        return this.getChronology().isSupported(element);

    }

    /**
     * <p>Returns the partial value associated with given chronological
     * element. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element which has the value
     * @return  associated element value as object (never {@code null})
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for evaluating the value
     * @see     #contains(ChronoElement)
     */
    /*[deutsch]
     * <p>Fragt ein chronologisches Element nach seinem Wert als Objekt ab. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element which has the value
     * @return  associated element value as object (never {@code null})
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for evaluating the value
     * @see     #contains(ChronoElement)
     */
    public <V> V get(ChronoElement<V> element) {

        return this.getRule(element).getValue(this.getContext());

    }

    /**
     * <p>Lets given query evaluate this entity. </p>
     *
     * <p>Is equivalent to {@code function.apply(this)}. The applied
     * strategy pattern hereby enables the externalization of querying
     * the interpretation and evaluation of this entity, consequently
     * enabling user-defined queries with arbitrary result types R.
     * Main difference to chronological elements is read-only access.
     * Users have to consult the documentation of given query to decide
     * if this method will yield {@code null} or throws an exception
     * if the result is undefined or otherwise not obtainable. </p>
     *
     * @param   <R> generic type of result of query
     * @param   function    time query
     * @return  result of query or {@code null} if undefined
     * @throws  ChronoException if the given query is not executable
     */
    /*[deutsch]
     * <p>L&auml;&szlig;t die angegebene Abfrage diese Entit&auml;t
     * auswerten. </p>
     *
     * <p>Entspricht {@code function.apply(this)}. Hier&uuml;ber wird der
     * Vorgang der Zeitinterpretation externalisiert und erm&ouml;glicht
     * so benutzerdefinierte Abfragen mit beliebigen Ergebnistypen. Anders
     * als bei chronologischen Elementen ist hier nur ein Lesezugriff
     * m&ouml;glich. In der Dokumentation der jeweiligen {@code ChronoFunction}
     * ist nachzuschauen, ob diese Methode im Fall undefinierter Ergebnisse
     * {@code null} zur&uuml;ckgibt oder eine Ausnahme wirft. </p>
     *
     * @param   <R> generic type of result of query
     * @param   function    time query
     * @return  result of query or {@code null} if undefined
     * @throws  ChronoException if the given query is not executable
     */
    public final <R> R get(ChronoFunction<? super T, R> function) {

        return function.apply(this.getContext());

    }

    /**
     * <p>Queries if this entity matches given condition. </p>
     *
     * <p>Is equivalent to {@code condition.test(this)}. This method
     * will never throw an exception. This behaviour is in contrast
     * to that of {@code ChronoFunction}. </p>
     *
     * @param   condition   temporal condition
     * @return  {@code true} if the given condition is matched by this
     *          entity else {@code false}
     * @see     #get(ChronoFunction)
     */
    /*[deutsch]
     * <p>Gen&uuml;gt diese Instanz der angegebenen Bedingung? </p>
     *
     * <p>Entspricht {@code condition.test(this)}. Diese Methode wirft
     * anders als eine allgemeine {@code ChronoFunction} keine Ausnahme. </p>
     *
     * @param   condition   temporal condition
     * @return  {@code true} if the given condition is matched by this
     *          entity else {@code false}
     * @see     #get(ChronoFunction)
     */
    public boolean matches(ChronoCondition<? super T> condition) {

        return condition.test(this.getContext());

    }

    /**
     * <p>Tests if the value for given chronological value is invalid. </p>
     *
     * <p>Notes: This method tests if given value to be in question can
     * be set via the expression {@code with(element, value)}. A numerical
     * overflow situation (causing an {@code ArithmeticException}) will
     * usually not be checked. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element the given value shall be assigned to
     * @param   value       candidate value to be validated (optional)
     * @return  {@code true} if the method {@code with()} can be called
     *          without exception else {@code false}
     * @see     #with(ChronoElement, Object) with(ChronoElement, V)
     */
    /*[deutsch]
     * <p>Ist der Wert zum angegebenen chronologischen Element g&uuml;ltig? </p>
     *
     * <p>Hinweise: Diese Methode testet, ob der fragliche Wert mittels des
     * Ausdrucks {@code with(element, value)} &uuml;berhaupt gesetzt werden
     * kann. Eine numerische &Uuml;berlaufsituation im Hinblick auf eine
     * {@code ArithmeticException} wird in der Regel nicht gepr&uuml;ft. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element the given value shall be assigned to
     * @param   value       candidate value to be validated (optional)
     * @return  {@code true} if the method {@code with()} can be called
     *          without exception else {@code false}
     * @see     #with(ChronoElement, Object) with(ChronoElement, V)
     */
    public <V> boolean isValid(
        ChronoElement<V> element,
        V value
    ) {

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        }

        return (
            this.contains(element)
            && this.getRule(element).isValid(this.getContext(), value)
        );

    }

    /**
     * <p>Tests if the value for given chronological value is invalid. </p>
     *
     * <p>Notes: This method tests if given value to be in question can
     * be set via the expression {@code with(element, value)}. A numerical
     * overflow situation (causing an {@code ArithmeticException}) will
     * usually not be checked. </p>
     *
     * @param   element     element the given value shall be assigned to
     * @param   value       candidate value to be validated
     * @return  {@code true} if the method {@code with()} can be called
     *          without exception else {@code false}
     * @see     #with(ChronoElement, int)
     */
    /*[deutsch]
     * <p>Ist der Wert zum angegebenen chronologischen Element g&uuml;ltig? </p>
     *
     * <p>Hinweis: Eine numerische &Uuml;berlaufsituation im Hinblick auf eine
     * {@code ArithmeticException} wird nicht gepr&uuml;ft. </p>
     *
     * @param   element     element the given value shall be assigned to
     * @param   value       candidate value to be validated
     * @return  {@code true} if the method {@code with()} can be called
     *          without exception else {@code false}
     * @see     #with(ChronoElement, int)
     */
    public boolean isValid(
        ChronoElement<Integer> element,
        int value
    ) {

        return this.isValid(element, Integer.valueOf(value));

    }

    /**
     * <p>Tests if the value for given chronological value is invalid. </p>
     *
     * <p>Notes: This method tests if given value to be in question can
     * be set via the expression {@code with(element, value)}. A numerical
     * overflow situation (causing an {@code ArithmeticException}) will
     * usually not be checked. </p>
     *
     * @param   element     element the given value shall be assigned to
     * @param   value       candidate value to be validated
     * @return  {@code true} if the method {@code with()} can be called
     *          without exception else {@code false}
     * @see     #with(ChronoElement, long)
     */
    /*[deutsch]
     * <p>Ist der Wert zum angegebenen chronologischen Element g&uuml;ltig? </p>
     *
     * <p>Hinweis: Eine numerische &Uuml;berlaufsituation im Hinblick auf eine
     * {@code ArithmeticException} wird nicht gepr&uuml;ft. </p>
     *
     * @param   element     element the given value shall be assigned to
     * @param   value       candidate value to be validated
     * @return  {@code true} if the method {@code with()} can be called
     *          without exception else {@code false}
     * @see     #with(ChronoElement, long)
     */
    public boolean isValid(
        ChronoElement<Long> element,
        long value
    ) {

        return this.isValid(element, Long.valueOf(value));

    }

    /**
     * <p>Creates a copy of this instance with the changed element value. </p>
     *
     * <p>A {@code null} value will almost ever be seen as invalid causing an
     * {@code IllegalArgumentException}. Subclasses which permit {@code null}
     * must explicitly document this feature. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     chronological element
     * @param   value       new element value
     * @return  changed copy of the same type, this instance remains unaffected
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for setting the value
     * @throws  IllegalArgumentException if the value is not valid
     * @throws  ArithmeticException in case of arithmetic overflow
     * @see     #isValid(ChronoElement, Object) isValid(ChronoElement, V)
     */
    /*[deutsch]
     * <p>Erstellt eine Kopie dieser Instanz mit dem ge&auml;nderten
     * Elementwert. </p>
     *
     * <p>Ein {@code null}-Wert wird fast immer als ung&uuml;ltig angesehen,
     * also zu einer {@code IllegalArgumentException} f&uuml;hren. Subklassen,
     * die {@code null} zulassen, m&uuml;ssen das explizit dokumentieren. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     chronological element
     * @param   value       new element value
     * @return  changed copy of the same type, this instance remains unaffected
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for setting the value
     * @throws  IllegalArgumentException if the value is not valid
     * @throws  ArithmeticException in case of arithmetic overflow
     * @see     #isValid(ChronoElement, Object) isValid(ChronoElement, V)
     */
    public <V> T with(
        ChronoElement<V> element,
        V value
    ) {

        return this.getRule(element).withValue(
            this.getContext(),
            value,
            element.isLenient()
        );

    }

    /**
     * <p>Creates a copy of this instance with the changed element value. </p>
     *
     * @param   element     chronological element
     * @param   value       new element value
     * @return  changed copy of the same type, this instance remains unaffected
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for setting the value
     * @throws  IllegalArgumentException if the value is not valid
     * @throws  ArithmeticException in case of arithmetic overflow
     * @see     #isValid(ChronoElement, Object) isValid(ChronoElement, V)
     */
    /*[deutsch]
     * <p>Erstellt eine Kopie dieser Instanz mit dem ge&auml;nderten
     * Elementwert. </p>
     *
     * @param   element     chronological element
     * @param   value       new element value
     * @return  changed copy of the same type, this instance remains unaffected
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for setting the value
     * @throws  IllegalArgumentException if the value is not valid
     * @throws  ArithmeticException in case of arithmetic overflow
     * @see     #isValid(ChronoElement, Object) isValid(ChronoElement, V)
     */
    public T with(
        ChronoElement<Integer> element,
        int value
    ) {

        return this.with(element, Integer.valueOf(value));

    }

    /**
     * <p>Creates a copy of this instance with the changed element value. </p>
     *
     * @param   element     chronological element
     * @param   value       new element value
     * @return  changed copy of the same type, this instance remains unaffected
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for setting the value
     * @throws  IllegalArgumentException if the value is not valid
     * @throws  ArithmeticException in case of arithmetic overflow
     * @see     #isValid(ChronoElement, Object) isValid(ChronoElement, V)
     */
    /*[deutsch]
     * <p>Erstellt eine Kopie dieser Instanz mit dem ge&auml;nderten
     * Elementwert. </p>
     *
     * @param   element     chronological element
     * @param   value       new element value
     * @return  changed copy of the same type, this instance remains unaffected
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for setting the value
     * @throws  IllegalArgumentException if the value is not valid
     * @throws  ArithmeticException in case of arithmetic overflow
     * @see     #isValid(ChronoElement, Object) isValid(ChronoElement, V)
     */
    public T with(
        ChronoElement<Long> element,
        long value
    ) {

        return this.with(element, Long.valueOf(value));

    }

    /**
     * <p>Creates a copy of this instance which is adjusted by given
     * {@code ChronoOperator} using a strategy pattern approach. </p>
     *
     * <p>Is equivalent to {@code operator.apply(this)}. Hereby a user-defined
     * manipulation will be externalized and is semantically similar to the
     * reading counterpart {@code ChronoFunction}. </p>
     *
     * @param   operator    operator for adjusting the element values
     * @return  changed copy of the same type, this instance remains unaffected
     * @throws  ChronoException if no element rule exists for setting the values
     * @throws  IllegalArgumentException if any new value is not valid
     * @throws  ArithmeticException in case of arithmetic overflow
     * @see     #get(ChronoFunction)
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieser Instanz zur&uuml;ck, die mit Hilfe
     * eines {@code ChronoOperator}-Objekts angepasst wird. </p>
     *
     * <p>Entspricht {@code operator.apply(this)}. Hier&uuml;ber wird eine
     * benutzerdefinierte Manipulation externalisiert und ist semantisch
     * &auml;hnlich wie im lesenden Gegenst&uuml;ck {@code ChronoFunction}. </p>
     *
     * @param   operator    operator for adjusting the element values
     * @return  changed copy of the same type, this instance remains unaffected
     * @throws  ChronoException if no element rule exists for setting the values
     * @throws  IllegalArgumentException if any new value is not valid
     * @throws  ArithmeticException in case of arithmetic overflow
     * @see     #get(ChronoFunction)
     */
    public T with(ChronoOperator<T> operator) {

        return operator.apply(this.getContext());

    }

    /**
     * <p>Yields the minimum value of given chronological element in the
     * current context of this entity. </p>
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
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for getting the minimum value
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
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for getting the minimum value
     * @see     ChronoElement#getDefaultMinimum()
     * @see     #getMaximum(ChronoElement)
     */
    public <V> V getMinimum(ChronoElement<V> element) {

        return this.getRule(element).getMinimum(this.getContext());

    }

    /**
     * <p>Yields the maximum value of given chronological element in the
     * current context of this entity. </p>
     *
     * <p>Maximum values are different from minimum case often dependent
     * on the context. An example is the element SECOND_OF_MINUTE whose
     * maximum is normally {@code 59} but can differ in UTC-context with
     * leap seconds. Another more common example is the maximum of the
     * element DAY_OF_MONTH (28-31) which is dependent on the month and year
     * of this context/entity (leap years!). </p>
     *
     * <p>Note: In timezone-related timestamps possible offset jumps
     * inducing gaps on the local timeline will be conserved. That means
     * that minimum and maximum do not guarantee a continuum of valid
     * intermediate values. </p>
     *
     * @param   <V> generic type of element value
     * @param   element     element whose maximum value is to be evaluated
     * @return  maximum maybe context-dependent element value
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for getting the maximum value
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
     * @throws  ChronoException if the element is not registered and there
     *          is no element rule for getting the maximum value
     * @see     ChronoElement#getDefaultMaximum()
     * @see     #getMinimum(ChronoElement)
     */
    public <V> V getMaximum(ChronoElement<V> element) {

        return this.getRule(element).getMaximum(this.getContext());

    }

    /**
     * <p>Queries if this entity contains a timezone. </p>
     *
     * <p>This implementation has no timezone by default and yields
     * {@code false}. Subclasses with timezone reference will override
     * the method in a suitable way. </p>
     *
     * @return  {@code true} if a timezone is available and can be achieved
     *          with {@link #getTimezone()} else {@code false}
     */
    /**
     * <p>Ermittelt, ob eine Zeitzone vorhanden ist. </p>
     *
     * <p>Diese Implementierung kennt standardm&auml;&szlig;ig keine Zeitzone
     * und liefert {@code false}. Subklassen mit Zeitzonenbezug werden die
     * Methode in geeigneter Weise &uuml;berschreiben. </p>
     *
     * @return  {@code true} if a timezone is available and can be achieved
     *          with {@link #getTimezone()} else {@code false}
     */
    public boolean hasTimezone() {

        return false;

    }

    /**
     * <p>Returns the associated timezone ID if available. </p>
     *
     * <p>This implementation throws a {@code ChronoException}
     * by default. Subclasses with timezone reference will override
     * the method in a suitable way. </p>
     *
     * @return  timezone identifier if available
     * @throws  ChronoException if the timezone is not available
     * @see     #hasTimezone()
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Zeitzonen-ID, wenn vorhanden. </p>
     *
     * <p>Diese Implementierung wirft standardm&auml;&szlig;ig eine
     * {@code ChronoException}. Subklassen mit Zeitzonenbezug werden
     * die Methode in geeigneter Weise &uuml;berschreiben. </p>
     *
     * @return  timezone identifier if available
     * @throws  ChronoException if the timezone is not available
     * @see     #hasTimezone()
     */
    public TZID getTimezone() {

        throw new ChronoException("Timezone not available: " + this);

    }

    /**
     * <p>Liefert die zugeh&ouml;rige Chronologie, die alle notwendigen
     * chronologischen Regeln enth&auml;lt. </p>
     *
     * <p>Konkrete Subklassen m&uuml;ssen in einem <i>static initializer</i>
     * mit Hilfe von {@code Chronology.Builder} oder {@code TimeAxis.Builder}
     * eine Chronologie bauen, in einer eigenen Konstanten halten und hier
     * verf&uuml;gbar machen. &Uuml;ber dieses Verfahren wird zugleich ein
     * Basissatz von Elementen und chronologischen Regeln vorinstalliert. </p>
     *
     * @return  chronological system
     * @throws  UnsupportedOperationException if not available (subclasses
     *          must document this rare case)
     * @see     Chronology.Builder
     */
    protected abstract Chronology<T> getChronology();

    /**
     * <p>Liefert den Selbstbezug. </p>
     *
     * @return  time context (usually this instance)
     */
    protected T getContext() {

        Chronology<T> c = this.getChronology();
        Class<T> type = c.getChronoType();

        if (type.isInstance(this)) {
            return type.cast(this);
        } else {
            for (ChronoElement<?> element : c.getRegisteredElements()) {
                if (type == element.getType()) {
                    return type.cast(this.get(element));
                }
            }
        }

        throw new IllegalStateException(
            "Implementation error: Cannot find entity context.");

    }

    private <V> ElementRule<T, V> getRule(ChronoElement<V> element) {

        return this.getChronology().getRule(element);

    }

}
