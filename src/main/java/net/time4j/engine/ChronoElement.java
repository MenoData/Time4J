/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoElement.java) is part of project Time4J.
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

import java.util.Comparator;


/**
 * <p>Repr&auml;sentiert ein chronologisches Element, das einen Teil eines
 * Zeitwerts referenziert und als Tr&auml;ger einer chronologischen Information
 * in erster Linie Formatierzwecken dient. </p>
 *
 * <p>Jedes chronologische System kennt einen Satz von Elementen, deren Werte
 * zusammen den Gesamtzeitwert festlegen, in der Regel die zugeh&ouml;rige Zeit
 * als Punkt auf einem Zeitstrahl. Jedes Element kann mit einem Wert vom Typ V
 * assoziiert werden. Normalerweise handelt es sich um einen Integer-Wert oder
 * einen Enum-Wert. Beispiele sind die Stunde einer Uhrzeit oder der Monat
 * eines Datums. </p>
 *
 * <p>Die zugeh&ouml;rigen Werte sind, wenn vorhanden, oft als Kontinuum
 * ohne L&uuml;cken als Integer im angegebenen Wertebereich definiert.
 * Garantiert ist ein Kontinuum aber nicht, siehe zum Beispiel
 * Sommerzeit-Umstellungen oder hebr&auml;ische Schaltmonate. </p>
 *
 * @param   <V> generic type of element values, usually extending the interface
 *          {@code java.lang.Comparable} (or it can be converted to any other
 *          sortable form)
 * @author  Meno Hochschild
 * @see     ChronoEntity#get(ChronoElement)
 * @spec    All implementations must be immutable and serializable.
 */
public interface ChronoElement<V>
    extends Comparator<ChronoEntity<?>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert den innerhalb einer Chronologie eindeutigen Namen. </p>
     *
     * <p>Zusammen mit dem Namen der zugeh&ouml;rigen Chronologie kann
     * der Name des chronologischen Elements auch als Ressourcenschl&uuml;ssel
     * f&uuml;r eine lokalisierte Beschreibung dienen. </p>
     *
     * @return  name of element, unique within a chronology
     */
    String name();

    /**
     * <p>Liefert die Wertklasse. </p>
     *
     * @return  type of associated values
     */
    Class<V> getType();

    /**
     * <p>Definiert das Standard-Formatsymbol, mit dem diese Instanz in
     * Formatmustern referenziert wird. </p>
     *
     * <p>So weit wie m&ouml;glich handelt es sich um ein Symbol nach der
     * CLDR-Norm des Unicode-Konsortiums. Ist das Element nicht f&uuml;r
     * Formatierungen per Formatmuster vorgesehen, liefert diese Methode das
     * ASCII-Zeichen &quot;\u0000&quot; (Codepoint 0). </p>
     *
     * @return  format symbol as char
     * @see     FormattableElement
     */
    char getSymbol();

    /**
     * <p>F&uuml;hrt eine elementorientierte Sortierung von beliebigen
     * chronologischen Objekten bez&uuml;glich dieses Elements ein . </p>
     *
     * <p>Der Werttyp V ist typischerweise eine Implementierung des Interface
     * {@code Comparable}, so da&szlig; diese Methode oft auf dem Ausdruck
     * {@code o1.get(this).compareTo(o2.get(this))} beruhen wird. Mit anderen
     * Worten, im Normalfall werden in den angegebenen chronologischen Objekten
     * die Werte bez&uuml;glich dieses Elements verglichen. Es d&uuml;rfen
     * anders als im {@code Comparable}-Interface m&ouml;glich hierbei auch
     * Objekte verschiedener chronologischer Typen miteinander verglichen
     * werden. </p>
     *
     * <p>Es ist zu betonen, da&szlig; ein Elementwertvergleich im allgemeinen
     * keine (vollst&auml;ndige) zeitliche Sortierung indiziert. Gegenbeispiele
     * sind das Jahr innerhalb der gregorianischen BC-&Auml;ra (umgekehrte
     * zeitliche Reihenfolge vor Christi Geburt) oder die Ziffernblattanzeige
     * von Stunden (die Zahl 12 ist eigentlich der Beginn zu Mitternacht). </p>
     *
     * @param   o1  the first object to be compared
     * @param   o2  the second object to be compared
     * @return  a negative integer, zero, or a positive integer as the first
     *          argument is less than, equal to, or greater than the second
     * @throws  ChronoException if this element is not registered in any entity
     *          and/or if no element rule exists to extract the element value
     */
    @Override
    int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    );

    /**
     * <p>Gibt das &uuml;bliche chronologieunabh&auml;ngige Minimum des
     * assoziierten Elementwerts zur&uuml;ck. </p>
     *
     * <p>Hinweis: Dieses Minimum definiert keineswegs eine untere Schranke
     * f&uuml;r alle g&uuml;ltigen Elementwerte, sondern nur das Standardminimum
     * unter chronologischen Normalbedingungen, unter denen es immer existiert
     * und daher als prototypischer Wert verwendet werden kann. Ein Beispiel
     * ist der Start des Tages in ISO-8601, welcher nur unter bestimmten
     * Zeitzonenkonfigurationen von Mitternacht abweichen kann. </p>
     *
     * @return  default minimum value of element
     * @see     #getDefaultMaximum()
     */
    V getDefaultMinimum();

    /**
     * <p>Gibt das &uuml;bliche chronologieunabh&auml;ngige Maximum des
     * assoziierten Elementwerts zur&uuml;ck. </p>
     *
     * <p>Hinweis: Dieses Maximum definiert keineswegs eine obere Schranke
     * f&uuml;r alle g&uuml;ltigen Elementwerte, sondern nur das Standardmaximum
     * unter chronologischen Normalbedingungen . Ein Beispiel sind Sekunden,
     * deren Standardmaximum {@code 59} ist, w&auml;hrend das gr&ouml;&szlig;te
     * Maximum bedingt durch UTC-Schaltsekunden {@code 60} sein kann. </p>
     *
     * @return  default maximum value of element
     * @see     #getDefaultMinimum()
     */
    V getDefaultMaximum();

    /**
     * <p>Ist dieses Element eine Teilkomponente, die ein Datumselement
     * darstellt? </p>
     *
     * @return  boolean
     * @see     #isTimeElement()
     */
    boolean isDateElement();

    /**
     * <p>Ist dieses Element eine Teilkomponente, die ein Uhrzeitelement
     * darstellt? </p>
     *
     * @return  boolean
     * @see     #isDateElement()
     */
    boolean isTimeElement();

    /**
     * <p>Soll das Setzen von Werten fehlertolerant ausgef&uuml;hrt werden? </p>
     *
     * @return  {@code true} if lenient else {@code false} (standard)
     * @see     ElementRule#withValue(Object, Object, boolean)
     *          ElementRule.withValue(T, V, boolean)
     */
    boolean isLenient();

}
