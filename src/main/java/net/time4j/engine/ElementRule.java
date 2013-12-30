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

import de.menodata.annotations4j.Nullable;


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
 * <p>Alle Implementierungen m&uuml;ssen <i>immutable</i> sein. </p>
 *
 * @param   <T> Typ des Zeitwertkontexts (ein Subtyp von {@code TimePoint})
 * @param   <V> generischer Werttyp
 * @author  Meno Hochschild
 * @see     Chronology.Builder#appendElement(ChronoElement,ElementRule)
 * @see     BasicElement#derive(Chronology)
 * @author  Meno Hochschild
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
     * @param   context     aktueller Zeitwertkontext
     * @return  aktueller Wert als Objekt
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
     * @param   context     aktueller Zeitwertkontext
     * @return  minimaler legaler Wert
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
     * @param   context     aktueller Zeitwertkontext
     * @return  maximaler legaler Wert
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
     * @param   context     aktueller Zeitwertkontext
     * @param   value       zu pr&uuml;fender Wert als Objekt
     * @return  {@code true} wenn g&uuml;ltig, sonst {@code false}
     */
    boolean isValid(
        T context,
        @Nullable V value
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
     * dieser Modus nur bei Verwendung der einen Versteller liefernden
     * Methode {@code AdvancedElement.setToLenientValue()} oder wenn das
     * Element ausdr&uuml;cklich als nachsichtig deklariert wird. </p>
     *
     * @param   context     aktueller Zeitwertkontext
     * @param   value       neuer Elementwert
     * @param   lenient     Fehlertoleranzmodus
     * @return  neuer ge&auml;nderter Zeitwertkontext
     * @throws  IllegalArgumentException wenn das Argument nicht im
     *          erforderlichen Wertbereich liegt oder im konkreten
     *          Kontext nicht erlaubt ist
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @see     #isValid(Object, Object) isValid(T, V)
     * @see     AdvancedElement#setToLenientValue(Comparable)
     *          AdvancedElement.setToLenientValue(V)
     * @see     AdvancedElement#setToStrictValue(Comparable)
     *          AdvancedElement.setToStrictValue(V)
     * @see     ChronoElement#isLenient()
     */
    T withValue(
        T context,
        @Nullable V value,
        boolean lenient
    );

    /**
     * <p>Ermittelt das Kindselement, dessen Wert auf seinen unteren Randwert
     * gesetzt wird. </p>
     *
     * <p>Der Zugriff erfolgt nur &uuml;ber die Klasse
     * {@code AdvancedElement}. </p>
     *
     * @param   context     aktueller Zeitwertkontext
     * @return  Kindselement oder {@code null}, wenn nicht vorhanden
     * @see     AdvancedElement#setToFloor()
     */
    @Nullable
    ChronoElement<?> getChildAtFloor(T context);

    /**
     * <p>Ermittelt das Kindselement, dessen Wert auf seinen oberen Randwert
     * gesetzt wird. </p>
     *
     * <p>Der Zugriff erfolgt nur &uuml;ber die Klasse
     * {@code AdvancedElement}. </p>
     *
     * @param   context     aktueller Zeitwertkontext
     * @return  Kindselement oder {@code null}, wenn nicht vorhanden
     * @see     AdvancedElement#setToCeiling()
     */
    @Nullable
    ChronoElement<?> getChildAtCeiling(T context);

}
