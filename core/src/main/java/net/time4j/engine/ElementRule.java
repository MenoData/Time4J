/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ElementRule.java) is part of project Time4J.
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


/**
 * <p>Represents the rule of a chronological element how to calculate or
 * to manipulate its context-dependent temporal value. </p>
 *
 * <p>Element rules will be searched by Time4J according to following
 * criteria: </p>
 *
 * <ol><li>An element rule will be registered in the {@code Chronology}
 * together with its associated chronological element. For each chronology
 * and each element instance there is exactly one rule instance. In most
 * cases it is an inner class of the temporal type in question. </li>
 * <li>If there is no registered rule for an element in a given chronology
 * then Time4J checks if the associated element is a {@code BasicElement}
 * and if the element supports the chronology. If yes then its internal
 * rule will be evaluated. </li>
 * <li>If the search did not yield any result then a
 * {@link RuleNotFoundException} will be thrown. </li></ol>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable. </p>
 *
 * @param   <T> generic type of time context compatible to {@code ChronoEntity}
 * @param   <V> generic type of elment value
 * @author  Meno Hochschild
 * @see     Chronology.Builder#appendElement(ChronoElement,ElementRule)
 * @see     BasicElement#derive(Chronology)
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Regel eines chronologischen Elements, indem
 * ein zeitkontextabh&auml;ngiger Wert berechnet oder gesetzt wird. </p>
 *
 * <p>Elementregeln werden von Time4J nach folgenden Kriterien zu einem
 * gegebenen chronologischen Element gesucht, wenn ein {@code ChronoEntity}
 * als chronologischer Typ vorliegt: </p>
 *
 * <ol><li>Eine Elementregel wird in der {@code Chronology} zusammen
 * mit dem assoziierten chronologischen Element registriert. Pro Chronologie
 * und pro Elementinstanz wird genau eine Regelinstanz registriert. Meist
 * handelt es sich um eine innere Klasse eines Zeitpunkttyps. </li>
 * <li>Gibt es zu einem Element keine registrierte Regel in der
 * fraglichen Chronologie, dann wird gepr&uuml;ft, ob ein chronologisches
 * Element des Typs {@code BasicElement} vorliegt und ob das Element
 * die Chronologie unterst&uuml;tzt. Wenn ja, wird dessen interne Regel
 * ausgewertet. Passt die Regel vom chronologischen Typ her, dann wird
 * sie verwendet. </li>
 * <li>Wenn die Suche letztlich nichts ergeben hat, wird eine
 * {@link RuleNotFoundException} geworfen. </li></ol>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable. </p>
 *
 * @param   <T> generic type of time context compatible to {@code ChronoEntity}
 * @param   <V> generic type of elment value
 * @author  Meno Hochschild
 * @see     Chronology.Builder#appendElement(ChronoElement,ElementRule)
 * @see     BasicElement#derive(Chronology)
 * @author  Meno Hochschild
 */
public interface ElementRule<T, V> {

    //~ Methoden ------------------------------------------------------

    /**
     * <p>Yields the current value of associated element in given
     * chronological context. </p>
     *
     * <p>Will be called by {@link ChronoEntity#get(ChronoElement)}. </p>
     *
     * <p>If the element associated with this rule is a primary element
     * which directly represents a part of the state of an entity then any
     * implementation will request the value directly from the state,
     * otherwise derive from context using a specific logic. The term
     * <i>primary</i> refers to if the context itself stores the value. </p>
     *
     * @param   context     time context to be evaluated
     * @return  current element value as object (never {@code null})
     * @throws  ChronoException if the associated element value cannot be evaluated
     */
    /*[deutsch]
     * <p>Ermittelt den aktuellen Wert des assoziierten Elements
     * im angegebenen Zeitwertkontext. </p>
     *
     * <p>Wird von {@link ChronoEntity#get(ChronoElement)} aufgerufen. </p>
     *
     * <p>Ist das mit dieser Regel assoziierte Element ein prim&auml;res
     * Element, also ein Teil des Zustands einer Entit&auml;t, dann wird
     * eine Implementierung den Wert direkt vom entsprechenden Zustandsfeld
     * im Kontext anfordern, sonst selbst nach eigener Logik aus dem Kontext
     * ableiten. Der Begriff <i>prim&auml;r</i> bezieht sich darauf, ob der
     * Kontext selbst den Wert speichert. </p>
     *
     * @param   context     time context to be evaluated
     * @return  current element value as object (never {@code null})
     * @throws  ChronoException if the associated element value cannot be evaluated
     */
    V getValue(T context);

    /**
     * <p>Yields the minimum value suitable for given chronological
     * context. </p>
     *
     * <p>Will be called by {@link ChronoEntity#getMinimum(ChronoElement)}. </p>
     *
     * <p><strong>ATTENTION:</strong> Defining a minimum and maximum does
     * not imply a continuum between minimum and maximum without any gaps,
     * see for example summer time switches or hebrew leap months. Furthermore,
     * a chronologically ascending order cannot be guaranteed. </p>
     *
     * @param   context     time context to be evaluated
     * @return  minimum legal and sometimes context-dependent value
     * @throws  ChronoException if the associated minimum value cannot be evaluated
     * @see     #getMaximum(Object) getMaximum(T)
     */
    /*[deutsch]
     * <p>Ermittelt den minimal zul&auml;ssigen Wert passend zum angegebenen
     * Zeitwertkontext. </p>
     *
     * <p>Wird von {@link ChronoEntity#getMinimum(ChronoElement)}
     * aufgerufen. </p>
     *
     * <p><strong>VORSICHT:</strong> Mit der Definition eines Minimums und
     * eines Maximums ist noch kein l&uuml;ckenloses Kontinuum zwischen
     * Minimum und Maximum garantiert, siehe z.B. Sommerzeit-Umstellungen
     * oder hebr&auml;ische Schaltmonate. Auch ist nicht immer eine
     * chronologisch aufsteigende Ordnung garantiert. </p>
     *
     * @param   context     time context to be evaluated
     * @return  minimum legal and sometimes context-dependent value
     * @throws  ChronoException if the associated minimum value cannot be evaluated
     * @see     #getMaximum(Object) getMaximum(T)
     */
    V getMinimum(T context);

    /**
     * <p>Yields the maximum value suitable for given chronological
     * context. </p>
     *
     * <p>Will be called by {@link ChronoEntity#getMaximum(ChronoElement)}. </p>
     *
     * <p><strong>ATTENTION:</strong> Defining a minimum and maximum does
     * not imply a continuum between minimum and maximum without any gaps,
     * see for example summer time switches or hebrew leap months. Furthermore,
     * a chronologically ascending order cannot be guaranteed. </p>
     *
     * @param   context     time context to be evaluated
     * @return  maximum legal and sometimes context-dependent value
     * @throws  ChronoException if the associated maximum value cannot be evaluated
     * @see     #getMinimum(Object) getMinimum(T)
     */
    /*[deutsch]
     * <p>Ermittelt den maximal zul&auml;ssigen Wert passend zum angegebenen
     * Zeitwertkontext. </p>
     *
     * <p>Wird von {@link ChronoEntity#getMaximum(ChronoElement)}
     * aufgerufen. </p>
     *
     * <p><strong>VORSICHT:</strong> Mit der Definition eines Minimums und
     * eines Maximums ist noch kein l&uuml;ckenloses Kontinuum zwischen
     * Minimum und Maximum garantiert, siehe z.B. Sommerzeit-Umstellungen
     * oder hebr&auml;ische Schaltmonate. Auch ist nicht immer eine
     * chronologisch aufsteigende Ordnung garantiert. </p>
     *
     * @param   context     time context to be evaluated
     * @return  maximum legal and sometimes context-dependent value
     * @throws  ChronoException if the associated maximum value cannot be evaluated
     * @see     #getMinimum(Object) getMinimum(T)
     */
    V getMaximum(T context);

    /**
     * <p>Queries if given value is valid for the element associated with this
     * rule in given context. </p>
     *
     * <p>Will be called by {@link ChronoEntity#isValid(ChronoElement, Object)
     * ChronoEntity.isValid(ChronoElement, V)}. A numerical overflow causing
     * an {@code ArithmeticException} will usually not be checked. </p>
     *
     * @param   context     time context to be evaluated
     * @param   value       candidate value to be validated (optional)
     * @return  {@code true} if valid else {@code false}
     */
    /*[deutsch]
     * <p>Ist der angegebene Wert zum mit dieser Regel assoziierten
     * Element im angegebenen Kontext g&uuml;ltig? </p>
     *
     * <p>Wird von {@link ChronoEntity#isValid(ChronoElement, Object)
     * ChronoEntity.isValid(ChronoElement, V)} aufgerufen. Eine numerische
     * &Uuml;berlaufsituation im Hinblick auf eine {@code ArithmeticException}
     * wird in der Regel nicht gepr&uuml;ft. </p>
     *
     * @param   context     time context to be evaluated
     * @param   value       candidate value to be validated (optional)
     * @return  {@code true} if valid else {@code false}
     */
    boolean isValid(
        T context,
        V value
    );

    /**
     * <p>Determines the new value of the associated element in given
     * chronological context and yields the result. </p>
     *
     * <p>Will be called by {@link ChronoEntity#with(ChronoElement, Object)
     * ChronoEntity.with(ChronoElement, V)}. The third parameter is in most
     * cases only relevant if the value type is no enum type but for example
     * an integer type. The lenient mode causes the tolerant interpretation
     * of invalid values like 31st of April as 1st of May. This mode is only
     * active if an element is either explicitly declared as lenient or if
     * the method {@code StdOperator.setLenient()} is used. </p>
     *
     * @param   context     time context to be evaluated
     * @param   value       new element value (optional)
     * @param   lenient     leniency mode
     * @return  changed copy of context which itself remains unaffected
     * @throws  IllegalArgumentException if given value is out of range or
     *          not valid dependent on the given time context
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #isValid(Object, Object) isValid(T, V)
     * @see     StdOperator#setLenient(Object, ChronoElement)
     * @see     ChronoElement#isLenient()
     * @see     net.time4j.ProportionalElement#setLenient(Number)
     */
    /*[deutsch]
     * <p>Bestimmt den neuen Wert des assoziierten Elements im
     * angegebenen Zeitwertkontext und liefert das Ergebnis. </p>
     *
     * <p>Wird von {@link ChronoEntity#with(ChronoElement, Object)
     * ChronoEntity.with(ChronoElement, V)} aufgerufen. Der dritte
     * Parameter spielt meist nur eine Rolle, wenn der Werttyp kein Enum
     * ist, sondern z.B. ein Integer. Dieser Nachsichtigkeitsmodus
     * f&uuml;hrt dazu, da&szlig; eigentlich ung&uuml;ltige Werte
     * wie der 31. April als 1. Mai interpretiert werden. Aktiv ist
     * dieser Modus nur bei Verwendung der einen Operator liefernden
     * Methode {@code StdOperator.setLenient()} oder wenn das
     * Element ausdr&uuml;cklich als nachsichtig deklariert wird. </p>
     *
     * @param   context     time context to be evaluated
     * @param   value       new element value (optional)
     * @param   lenient     leniency mode
     * @return  changed copy of context which itself remains unaffected
     * @throws  IllegalArgumentException if given value is out of range or
     *          not valid dependent on the given time context
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #isValid(Object, Object) isValid(T, V)
     * @see     StdOperator#setLenient(Object, ChronoElement)
     * @see     ChronoElement#isLenient()
     * @see     net.time4j.ProportionalElement#setLenient(Number)
     */
    T withValue(
        T context,
        V value,
        boolean lenient
    );

    /**
     * <p>Yields the child element whose value is to be set to the minimum
     * value. </p>
     *
     * <p>The access to this method only happens indirectly in the class
     * {@code StdOperator}. </p>
     *
     * @param   context     time context to be evaluated
     * @return  child element or {@code null} if not available
     * @see     StdOperator#atFloor(ChronoElement)
     */
    /*[deutsch]
     * <p>Ermittelt das Kindselement, dessen Wert auf seinen unteren Randwert
     * gesetzt wird. </p>
     *
     * <p>Der Zugriff erfolgt nur &uuml;ber die Klasse {@code StdOperator}. </p>
     *
     * @param   context     time context to be evaluated
     * @return  child element or {@code null} if not available
     * @see     StdOperator#atFloor(ChronoElement)
     */
    ChronoElement<?> getChildAtFloor(T context);

    /**
     * <p>Yields the child element whose value is to be set to the maximum
     * value. </p>
     *
     * <p>The access to this method only happens indirectly in the class
     * {@code StdOperator}. </p>
     *
     * @param   context     time context to be evaluated
     * @return  child element or {@code null} if not available
     * @see     StdOperator#atCeiling(ChronoElement)
     */
    /*[deutsch]
     * <p>Ermittelt das Kindselement, dessen Wert auf seinen oberen Randwert
     * gesetzt wird. </p>
     *
     * <p>Der Zugriff erfolgt nur &uuml;ber die Klasse {@code StdOperator}. </p>
     *
     * @param   context     time context to be evaluated
     * @return  child element or {@code null} if not available
     * @see     StdOperator#atCeiling(ChronoElement)
     */
    ChronoElement<?> getChildAtCeiling(T context);

}
