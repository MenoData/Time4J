/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ElementRule.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.engine;


/**
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
 * <li>Als letzter Versuch wird gepr&uuml;ft, ob es eine andere geladene
 * Chronologie gibt, die kompatibel zur Klasse {@link Calendrical} ist
 * und das fragliche Element registriert hat. In diesem Fall mu&szlig;
 * auch die aktuelle Chronologie zu {@code Calendrical} kompatibel sein.
 * Wenn ja, wird eine Elementregel generiert, die die zwischen den
 * Chronologien notwendige Konvertierung automatisch vornimmt. </li>
 * <li>Wenn die Suche letztlich nichts ergeben hat, wird eine
 * {@link RuleNotFoundException} geworfen. </li></ol>
 *
 * @param   <T> generic type of time context compatible to {@code ChronoEntity}
 * @param   <V> generic type of elment value
 * @author  Meno Hochschild
 * @see     Chronology.Builder#appendElement(ChronoElement,ElementRule)
 * @see     BasicElement#derive(Chronology)
 * @author  Meno Hochschild
 * @spec    All implementations must be immutable.
 */
public interface ElementRule<T, V> {

    //~ Methoden ------------------------------------------------------

    /**
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
     */
    V getValue(T context);

    /**
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
     * @see     #getMaximum(Object) getMaximum(T)
     */
    V getMinimum(T context);

    /**
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
     * @see     #getMinimum(Object) getMinimum(T)
     */
    V getMaximum(T context);

    /**
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
     * Methode {@code AdvancedElement.lenient()} oder wenn das
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
     * @see     AdvancedElement#lenient(Comparable)
     *          AdvancedElement.lenient(V)
     * @see     ChronoElement#isLenient()
     * @see     net.time4j.ProportionalElement#setLenient(Number)
     */
    T withValue(
        T context,
        V value,
        boolean lenient
    );

    /**
     * <p>Ermittelt das Kindselement, dessen Wert auf seinen unteren Randwert
     * gesetzt wird. </p>
     *
     * <p>Der Zugriff erfolgt nur &uuml;ber die Klasse
     * {@code AdvancedElement}. </p>
     *
     * @param   context     time context to be evaluated
     * @return  child element or {@code null} if not available
     * @see     AdvancedElement#floor()
     */
    ChronoElement<?> getChildAtFloor(T context);

    /**
     * <p>Ermittelt das Kindselement, dessen Wert auf seinen oberen Randwert
     * gesetzt wird. </p>
     *
     * <p>Der Zugriff erfolgt nur &uuml;ber die Klasse
     * {@code AdvancedElement}. </p>
     *
     * @param   context     time context to be evaluated
     * @return  child element or {@code null} if not available
     * @see     AdvancedElement#ceiling()
     */
    ChronoElement<?> getChildAtCeiling(T context);

}
