/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoEntity.java) is part of project Time4J.
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
 * <p>Implementierungshinweis: Alle &ouml;ffentliche Implementierungen
 * m&uuml;ssen <i>immutable</i> sein. </p>
 *
 * @param   <T> generischer Selbstbezug
 * @author  Meno Hochschild
 */
public abstract class ChronoEntity<T extends ChronoEntity<T>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ist der Wert zum angegebenen chronologischen Element abfragbar
     * beziehungsweise enthalten? </p>
     *
     * <p>Fehlt das Argument, dann liefert die Methode {@code false}. Zu
     * beachten: Im Gegensatz zum Iterator {@code iterator()} werden hier
     * nicht nur registrierte Elemente als abfragbar gewertet, sondern auch
     * solche, die z.B. eine passende chronologische Regel definieren. </p>
     *
     * @param   element     gesuchtes chronologisches Element
     * @return  {@code true} wenn die Methode {@code get(ChronoElement)}
     *          ohne Abbruch aufgerufen werden kann, sonst {@code false}
     * @see     #get(ChronoElement)
     */
    public boolean contains(@Nullable ChronoElement<?> element) {

        return this.getChronology().isSupported(element);

    }

    /**
     * <p>Fragt ein chronologisches Element nach seinem Wert als Objekt ab. </p>
     *
     * @param   <V> generischer Elementwerttyp
     * @param   element     Element, zu dem der Wert gesucht wird
     * @return  assoziierter Elementwert als Objekt (nie {@code null})
     * @throws  ChronoException wenn das Element nicht registriert ist
     *          und auch keine Regel zum Lesen des Werts gefunden werden kann
     * @see     #contains(ChronoElement)
     */
    public <V> V get(ChronoElement<V> element) {

        return this.getRule(element).getValue(this.getContext());

    }

    /**
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
     * @param   <R> Ergebnistyp
     * @param   function    Zeitabfrage
     * @return  Ergebnis der Abfrage oder {@code null} wenn undefiniert
     * @throws  ChronoException wenn die Abfrage nicht ausf&uuml;hrbar ist
     */
    @Nullable
    public final <R> R get(ChronoFunction<? super T, R> function) {

        return function.apply(this.getContext());

    }

    /**
     * <p>Gen&uuml;gt diese Instanz der angegebenen Bedingung? </p>
     *
     * <p>Entspricht {@code condition.test(this)}. Diese Methode wirft
     * anders als eine allgemeine {@code ChronoFunction} keine Ausnahme. </p>
     *
     * @param   condition   Zeitbedingung
     * @return  {@code true} wenn die Bedingung von dieser Instanz erf&uuml;llt
     *          wird, sonst {@code false}
     * @see     #get(ChronoFunction)
     */
    public final boolean matches(ChronoCondition<? super T> condition) {

        return condition.test(this.getContext());

    }

    /**
     * <p>Ist der Wert zum angegebenen chronologischen Element g&uuml;ltig? </p>
     *
     * <p>Hinweise: Diese Methode testet, ob der fragliche Wert mittels des
     * Ausdrucks {@code with(element, value)} &uuml;berhaupt gesetzt werden
     * kann. Eine numerische &Uuml;berlaufsituation im Hinblick auf eine
     * {@code ArithmeticException} wird in der Regel nicht gepr&uuml;ft. </p>
     *
     * @param   <V> generischer Elementwerttyp
     * @param   element     gesuchtes chronologisches Element
     * @param   value       zu pr&uuml;fender Elementwert
     * @return  {@code true} wenn die Methode {@code with()} ohne Abbruch
     *          aufgerufen werden kann, sonst {@code false}
     * @see     #with(ChronoElement, Object) with(ChronoElement, V)
     */
    public <V> boolean isValid(
        ChronoElement<V> element,
        @Nullable V value
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
     * <p>Ist der Wert zum angegebenen chronologischen Element g&uuml;ltig? </p>
     *
     * <p>Hinweis: Eine numerische &Uuml;berlaufsituation im Hinblick auf eine
     * {@code ArithmeticException} wird nicht gepr&uuml;ft. </p>
     *
     * @param   element     gesuchtes chronologisches Element
     * @param   value       zu pr&uuml;fender Elementwert
     * @return  {@code true} wenn die Methode {@code with()} ohne Abbruch
     *          aufgerufen werden kann, sonst {@code false}
     * @see     #with(ChronoElement, int)
     */
    public boolean isValid(
        ChronoElement<Integer> element,
        int value
    ) {

        return this.isValid(element, Integer.valueOf(value));

    }

    /**
     * <p>Ist der Wert zum angegebenen chronologischen Element g&uuml;ltig? </p>
     *
     * <p>Hinweis: Eine numerische &Uuml;berlaufsituation im Hinblick auf eine
     * {@code ArithmeticException} wird nicht gepr&uuml;ft. </p>
     *
     * @param   element     gesuchtes chronologisches Element
     * @param   value       zu pr&uuml;fender Elementwert
     * @return  {@code true} wenn die Methode {@code with()} ohne Abbruch
     *          aufgerufen werden kann, sonst {@code false}
     * @see     #with(ChronoElement, long)
     */
    public boolean isValid(
        ChronoElement<Long> element,
        long value
    ) {

        return this.isValid(element, Long.valueOf(value));

    }

    /**
     * <p>Erstellt eine Kopie dieser Instanz mit dem ge&auml;nderten
     * Elementwert. </p>
     *
     * <p>Ein {@code null}-Wert wird fast immer als ung&uuml;ltig angesehen,
     * also zu einer {@code IllegalArgumentException} f&uuml;hren. Subklassen,
     * die {@code null} zulassen, m&uuml;ssen das explizit dokumentieren. </p>
     *
     * @param   <V> generischer Elementwerttyp
     * @param   element     chronologisches Element
     * @param   value       neuer Elementwert
     * @return  ge&auml;nderte Kopie gleichen Typs
     * @throws  ChronoException wenn das Element nicht registriert ist
     *          und auch keine Regel zum Setzen des Werts gefunden werden kann
     * @throws  IllegalArgumentException wenn der Wert ung&uuml;ltig ist
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
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
     * <p>Erstellt eine Kopie dieser Instanz mit dem ge&auml;nderten
     * Elementwert. </p>
     *
     * @param   element     chronologisches Element
     * @param   value       neuer Elementwert
     * @return  ge&auml;nderte Kopie gleichen Typs
     * @throws  ChronoException wenn das Element nicht registriert ist
     *          und auch keine Regel zum Setzen des Werts gefunden werden kann
     * @throws  IllegalArgumentException wenn der Wert ung&uuml;ltig ist
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @see     #isValid(ChronoElement, Object) isValid(ChronoElement, V)
     */
    public T with(
        ChronoElement<Integer> element,
        int value
    ) {

        return this.with(element, Integer.valueOf(value));

    }

    /**
     * <p>Erstellt eine Kopie dieser Instanz mit dem ge&auml;nderten
     * Elementwert. </p>
     *
     * @param   element     chronologisches Element
     * @param   value       neuer Elementwert
     * @return  ge&auml;nderte Kopie gleichen Typs
     * @throws  ChronoException wenn das Element nicht registriert ist
     *          und auch keine Regel zum Setzen des Werts gefunden werden kann
     * @throws  IllegalArgumentException wenn der Wert ung&uuml;ltig ist
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @see     #isValid(ChronoElement, Object) isValid(ChronoElement, V)
     */
    public T with(
        ChronoElement<Long> element,
        long value
    ) {

        return this.with(element, Long.valueOf(value));

    }

    /**
     * <p>Liefert eine Kopie dieser Instanz zur&uuml;ck, die mit Hilfe
     * eines {@code ChronoOperator}-Objekts angepasst wird. </p>
     *
     * <p>Entspricht {@code operator.apply(this)}. Hier&uuml;ber wird eine
     * benutzerdefinierte Manipulation externalisiert und ist semantisch
     * &auml;hnlich wie im lesenden Gegenst&uuml;ck {@code ChronoFunction}. </p>
     *
     * @param   operator    Hilfsobjekt zur Anpassung der Elementwerte
     * @return  Kopie dieser Instanz mit ge&auml;nderten Elementwerten
     * @throws  ChronoException wenn keine Regel zur Anpassung gefunden wird
     * @throws  IllegalArgumentException wenn das Setzen eines falschen Werts
     *          versucht wird
     * @throws  ArithmeticException bei numerischem &Uuml;berlauf
     * @see     #get(ChronoFunction)
     */
    public final T with(ChronoOperator<T> operator) {

        return operator.apply(this.getContext());

    }

    /**
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
     * @param   <V> generischer Elementwerttyp
     * @param   element     Element, zu dem das Minimum gesucht wird
     * @return  minimaler im Prinzip kontextabh&auml;ngiger Elementwert
     * @throws  ChronoException wenn das Element nicht registriert ist
     *          und auch keine Regel zum Setzen des Werts gefunden werden kann
     * @see     ChronoElement#getDefaultMinimum()
     * @see     #getMaximum(ChronoElement)
     */
    public <V> V getMinimum(ChronoElement<V> element) {

        return this.getRule(element).getMinimum(this.getContext());

    }

    /**
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
     * @param   <V> generischer Elementwerttyp
     * @param   element     Element, zu dem das Maximum gesucht wird
     * @return  maximaler im Prinzip kontextabh&auml;ngiger Elementwert
     * @throws  ChronoException wenn das Element nicht registriert ist
     *          und auch keine Regel zum Setzen des Werts gefunden werden kann
     * @see     ChronoElement#getDefaultMaximum()
     * @see     #getMinimum(ChronoElement)
     */
    public <V> V getMaximum(ChronoElement<V> element) {

        return this.getRule(element).getMaximum(this.getContext());

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
     * @return  chronologisches System
     * @throws  UnsupportedOperationException wenn nicht vorhanden
     * @see     Chronology.Builder
     */
    public abstract Chronology<T> getChronology();

    /**
     * <p>Liefert den Selbstbezug. </p>
     *
     * @return  Zeitwertkontext (normalerweise diese Instanz)
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
