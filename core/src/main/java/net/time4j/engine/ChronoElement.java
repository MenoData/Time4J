/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoElement.java) is part of project Time4J.
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

import java.util.Comparator;
import java.util.Locale;


/**
 * <p>Represents a chronological element which refers to a partial value of
 * the whole temporal value and mainly serves for formatting purposes (as
 * a carrier of a chronological information). </p>
 *
 * <p>Each chronological system knows a set of elements whose values
 * compose the total temporal value, usually the associated time coordinates
 * on a time axis. Each element can be associated with a value of type V.
 * Normally this value is an integer or an enum. Examples are the hour on
 * a clock or the month of a calendar date. </p>
 *
 * <p>The associated values are often defined as continuum without any
 * gaps as integer within a given value range. However, there is no
 * guarantee for such a continuum, for example daylight-saving-jumps or
 * hebrew leap months. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable and serializable. </p>
 *
 * @param   <V> generic type of element values, usually extending the interface
 *          {@code java.lang.Comparable} (or it can be converted to any other
 *          sortable form)
 * @author  Meno Hochschild
 * @see     ChronoEntity#get(ChronoElement)
 */
/*[deutsch]
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
 * <p><strong>Specification:</strong>
 * All implementations must be immutable and serializable. </p>
 *
 * @param   <V> generic type of element values, usually extending the interface
 *          {@code java.lang.Comparable} (or it can be converted to any other
 *          sortable form)
 * @author  Meno Hochschild
 * @see     ChronoEntity#get(ChronoElement)
 */
public interface ChronoElement<V>
    extends Comparator<ChronoDisplay> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the name which is unique within the context of a given
     * chronology. </p>
     *
     * <p>The name can also serve as resource key together with the name of
     * a chronology for a localized description. </p>
     *
     * @return  name of element, unique within a chronology
     */
    /*[deutsch]
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
     * <p>Yields the reified value type. </p>
     *
     * @return  type of associated values
     */
    /*[deutsch]
     * <p>Liefert die Wertklasse. </p>
     *
     * @return  type of associated values
     */
    Class<V> getType();

    /**
     * <p>Defines the default format symbol which is used in format patterns
     * to refer to this element. </p>
     *
     * <p>In most cases the symbol should closely match the symbol-mapping
     * as defined by the CLDR-standard of unicode-organization. Is the
     * element not designed for formatting using patterns then this method
     * just yields the ASCII-char &quot;\u0000&quot; (Codepoint 0). </p>
     *
     * @return  format symbol as char
     * @see     FormattableElement
     */
    /*[deutsch]
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
     * <p>Applies an element-orientated sorting of any chronological
     * entities. </p>
     *
     * <p>The value type V is usually a subtype of the interface
     * {@code Comparable} so that this method will usually be based on
     * the expression {@code o1.get(this).compareTo(o2.get(this))}. In
     * other words, this method compares the element values of given
     * chronological entities. It is even permitted to compare entities
     * of different chronological types as long as the entities both
     * support this element. </p>
     *
     * <p>It should be noted however that a element value comparison does
     * often not induce any temporal (complete) order. Counter examples
     * are the year of gregorian BC-era (reversed temporal order before
     * Jesu birth) or the clock display of hours (12 is indeed the begin
     * at midnight). </p>
     *
     * @param   o1  the first object to be compared
     * @param   o2  the second object to be compared
     * @return  a negative integer, zero, or a positive integer as the first
     *          argument is less than, equal to, or greater than the second
     * @throws  ChronoException if this element is not registered in any entity
     *          and/or if no element rule exists to extract the element value
     */
    /*[deutsch]
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
     * werden, solange die zu vergleichenden Objekte dieses Element
     * unterst&uuml;tzen. </p>
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
        ChronoDisplay o1,
        ChronoDisplay o2
    );

    /**
     * <p>Returns the default minimum of this element which is not dependent
     * on the chronological context. </p>
     *
     * <p>Note: This minimum does not necessarily define a minimum for all
     * possible circumstances but only the default minimum under normal
     * conditions such that the default minimum always exists and can be
     * used as prototypical value. An example is the start of day which
     * is usually midnight in ISO-8601 and can only deviate in specialized
     * timezone context. </p>
     *
     * @return  default minimum value of element
     * @see     #getDefaultMaximum()
     */
    /*[deutsch]
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
     * <p>Returns the default maximum of this element which is not dependent
     * on the chronological context. </p>
     *
     * <p>Note: This maximum does not necessarily define a maximum for all
     * possible circumstances but only the default maximum under normal
     * conditions. An example is the second of minute whose default maximum
     * is {@code 59} while the largest maximum can be {@code 60} in context
     * of UTC-leapseconds. </p>
     *
     * @return  default maximum value of element
     * @see     #getDefaultMinimum()
     */
    /*[deutsch]
     * <p>Gibt das &uuml;bliche chronologieunabh&auml;ngige Maximum des
     * assoziierten Elementwerts zur&uuml;ck. </p>
     *
     * <p>Hinweis: Dieses Maximum definiert keineswegs eine obere Schranke
     * f&uuml;r alle g&uuml;ltigen Elementwerte, sondern nur das Standardmaximum
     * unter chronologischen Normalbedingungen . Ein Beispiel ist das
     * Element SECOND_OF_MINUTE, dessen Standardmaximum {@code 59} ist,
     * w&auml;hrend das gr&ouml;&szlig;te Maximum bedingt durch
     * UTC-Schaltsekunden {@code 60} sein kann. </p>
     *
     * @return  default maximum value of element
     * @see     #getDefaultMinimum()
     */
    V getDefaultMaximum();

    /**
     * <p>Queries if this element represents a calendar date element. </p>
     *
     * @return  boolean
     * @see     #isTimeElement()
     */
    /*[deutsch]
     * <p>Ist dieses Element eine Teilkomponente, die ein Datumselement
     * darstellt? </p>
     *
     * @return  boolean
     * @see     #isTimeElement()
     */
    boolean isDateElement();

    /**
     * <p>Queries if this element represents a wall time element. </p>
     *
     * @return  boolean
     * @see     #isDateElement()
     */
    /*[deutsch]
     * <p>Ist dieses Element eine Teilkomponente, die ein Uhrzeitelement
     * darstellt? </p>
     *
     * @return  boolean
     * @see     #isDateElement()
     */
    boolean isTimeElement();

    /**
     * <p>Queries if setting of element values is performed in a lenient
     * way. </p>
     *
     * @return  {@code true} if lenient else {@code false} (standard)
     * @see     ElementRule#withValue(Object, Object, boolean)
     *          ElementRule.withValue(T, V, boolean)
     */
    /*[deutsch]
     * <p>Soll das Setzen von Werten fehlertolerant ausgef&uuml;hrt werden? </p>
     *
     * @return  {@code true} if lenient else {@code false} (standard)
     * @see     ElementRule#withValue(Object, Object, boolean)
     *          ElementRule.withValue(T, V, boolean)
     */
    boolean isLenient();

    /**
     * <p>Obtains a localized name for display purposes if possible. </p>
     *
     * <p>Most elements have no localized names, but in case the i18n-module is loaded then elements
     * like eras, years, quarters, months, weeks, day-of-month, day-of-week, am/pm, hour, minute and second
     * do have localization support. The default implementation falls back to the technical element name. </p>
     *
     * <p>Note that the displayed name does not need to be unique for different elements. For example the
     * localized names of {@code PlainDate.MONTH_OF_YEAR} and {@code PlainDate.MONTH_AS_NUMBER} are equal. </p>
     *
     * @param   language    language setting
     * @return  localized name or if not available then {@link #name() a technical name} will be chosen
     * @since   3.22/4.18
     */
    /*[deutsch]
     * <p>Ermittelt einen lokalisierten Anzeigenamen wenn m&ouml;glich. </p>
     *
     * <p>Die meisten Elemente haben keine lokalisierten Namen, aber falls das i18n-Modul geladen ist,
     * haben Elemente wie &Auml;ras, Jahre, Quartale, Monate, Wochen, Tag, Wochentag, AM/PM, Stunde,
     * Minute und Sekunde lokalisierte Namen. Die Standardimplementierung f&auml;llt auf den technischen
     * Elementnamen zur&uuml;ck. </p>
     *
     * <p>Hinweis: Der Anzeigename mu&szlig; nicht eindeutig sein. Zum Beispiel sind die Anzeigenamen
     * von {@code PlainDate.MONTH_OF_YEAR} und {@code PlainDate.MONTH_AS_NUMBER} in der Regel gleich. </p>
     *
     * @param   language    language setting
     * @return  localized name or if not available then {@link #name() a technical name} will be chosen
     * @since   3.22/4.18
     */
    String getDisplayName(Locale language);

}
