/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoMerger.java) is part of project Time4J.
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

import net.time4j.base.TimeSource;

import java.util.Locale;


/**
 * <p>Merges any set of chronological informations to a new chronological
 * entity. </p>
 *
 * <p>This interface abstracts the knowledge of the chronology how to
 * construct a new entity based on any set of chronological informations.
 * It is mainly used by a parser in order to interprete textual representations
 * of chronological entities. </p>
 *
 * <p>Using this low-level-interface is usually reserved for Time4J and
 * serves for internal format support. However, for constructing new
 * calendar systems implementing this interface is an essential part
 * for parsing support. Implementation note: All classes of this type
 * must be <i>immutable</i>. </p>
 *
 * @param   <T> generic type of time context
 *          (compatible to {@link ChronoEntity})
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Erzeugt aus chronologischen Informationen eine neue
 * {@code ChronoEntity}. </p>
 *
 * <p>Dieses Interface abstrahiert das Wissen der jeweiligen Zeitwertklasse,
 * wie aus beliebigen chronologischen Informationen eine neue Zeitwertinstanz
 * zu konstruieren ist und wird zum Beispiel beim Parsen von textuellen
 * Darstellungen zu Zeitwertobjekten ben&ouml;tigt. </p>
 *
 * <p>Die Benutzung dieses Low-Level-Interface bleibt in der Regel Time4J
 * vorbehalten und dient vorwiegend der internen Formatunterst&uuml;tzung.
 * Zur Konstruktion von neuen Kalendersystemen ist die Implementierung
 * dieses Interface ein wichtiger Bestandteil. Implementierungshinweis:
 * Alle Klassen dieses Typs m&uuml;ssen <i>immutable</i>, also
 * unver&auml;nderlich sein. </p>
 *
 * @param   <T> generic type of time context
 *          (compatible to {@link ChronoEntity})
 * @author  Meno Hochschild
 */
public interface ChronoMerger<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new entity which reflects current time. </p>
     *
     * <p>In a date-only chronology this method will create the current date
     * using the necessary timezone contained in given attributes. </p>
     *
     * @param   clock           source for current time
     * @param   attributes      configuration attributes which might contain
     *                          the timezone to translate current time to
     *                          local time
     * @return  new time context or {@code null} if given data are insufficient
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Entit&auml;t, die der aktuellen Zeit
     * entspricht. </p>
     *
     * <p>In einer rein datumsbezogenen kalendarischen Chronologie wird hier
     * das aktuelle Tagesdatum erzeugt, indem zus&auml;tzlich &uuml;ber die
     * Attribute die notwendige Zeitzone ermittelt wird. </p>
     *
     * @param   clock           source for current time
     * @param   attributes      configuration attributes which might contain
     *                          the timezone to translate current time to
     *                          local time
     * @return  new time context or {@code null} if given data are insufficient
     */
    T createFrom(
        TimeSource<?> clock,
        AttributeQuery attributes
    );

    /**
     * <p>Creates a new entity of type T based on given chronological
     * data. </p>
     *
     * <p>Typically the method will query the given {@code entity} with
     * different priorities for elements which can compose a new chronological
     * entity (per group). For example a calendar date can be composed either
     * by epoch days or the group (year)-(month)-(day-of-month) or the group
     * (year)-(day-of-year) etc. </p>
     *
     * <p>A text parser will call this method after having resolved a text
     * into single chronological elements and values. Implementations should
     * always validate the parsed values. In case of error, they are free to
     * either throw an {@code IllegalArgumentException} or to generate
     * and to save an error message by mean of the expression
     * {@code entity.with(ValidationElement.ERROR_MESSAGE, message}. </p>
     *
     * @param   entity          any chronological entity like parsed
     *                          elements with their values
     * @param   attributes      configuration attributes given by parser
     * @param   preparsing      preparsing phase active?
     * @return  new time context or {@code null} if given data are insufficient
     * @throws  IllegalArgumentException in any case of inconsistent data
     * @see     ValidationElement#ERROR_MESSAGE
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Entit&auml;t basierend auf den angegebenen
     * chronologischen Daten. </p>
     *
     * <p>Typischerweise wird mit verschiedenen Priorit&auml;ten das Argument
     * {@code entity} nach Elementen abgefragt, die gruppenweise einen
     * Zeitwert konstruieren. Zum Beispiel kann ein Datum entweder &uuml;ber
     * die Epochentage, die Gruppe Jahr-Monat-Tag oder die Gruppe Jahr und Tag
     * des Jahres konstruiert werden. </p>
     *
     * <p>Ein Textinterpretierer ruft diese Methode auf, nachdem ein Text
     * elementweise in chronologische Elemente und Werte aufgel&ouml;st
     * wurde. Implementierungen sollten immer die interpretierten Werte
     * validieren. Im Fehlerfall sind Implementierungen frei, entweder eine
     * {@code IllegalArgumentException} zu werfen oder mittels des Ausdrucks
     * {@code entity.with(ValidationElement.ERROR_MESSAGE, message} eine
     * Fehlermeldung nur zu generieren und zu speichern. </p>
     *
     * @param   entity          any chronological entity like parsed
     *                          elements with their values
     * @param   attributes      configuration attributes given by parser
     * @param   preparsing      preparsing phase active?
     * @return  new time context or {@code null} if given data are insufficient
     * @throws  IllegalArgumentException in any case of inconsistent data
     * @see     ValidationElement#ERROR_MESSAGE
     */
    T createFrom(
        ChronoEntity<?> entity,
        AttributeQuery attributes,
        boolean preparsing
    );

    /**
     * <p>Transforms the current context/entity into another set of chronological
     * values which finally shall be formatted using given attributes. </p>
     *
     * @param   context         actual chronological context to be formatted
     * @param   attributes      controls attributes during formatting
     * @return  replacement entity which will finally be used for formatting
     * @throws  IllegalArgumentException in any case of inconsistent data
     */
    /*[deutsch]
     * <p>Transformiert den aktuellen Kontext unter Beachtung der Attribute
     * bei Bedarf in den tats&auml;chlich zu formatierenden Satz von
     * chronologischen Werten. </p>
     *
     * @param   context         actual chronological context to be formatted
     * @param   attributes      controls attributes during formatting
     * @return  replacement entity which will finally be used for formatting
     * @throws  IllegalArgumentException in any case of inconsistent data
     */
    ChronoDisplay preformat(
        T context,
        AttributeQuery attributes
    );

    /**
     * <p>This method defines a child chronology which can preparse
     * a chronological text. </p>
     *
     * @return  preparsing chronology or {@code null}
     */
    /*[deutsch]
     * <p>Diese Methode definiert eine Kindschronologie, wenn eine
     * Vorinterpretierung des chronologischen Texts notwendig ist. </p>
     *
     * @return  preparsing chronology or {@code null}
     */
    Chronology<?> preparser();

    /**
     * <p>Defines a CLDR-compatible localized format pattern. </p>
     *
     * @param   style   format style
     * @param   locale  language and country setting
     * @return  localized format pattern
     * @throws  UnsupportedOperationException if given style is not supported
     *          or if no localized format pattern support is available
     * @see     net.time4j.format.LocalizedPatternSupport
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Definiert ein CLDR-kompatibles lokalisiertes Formatmuster. </p>
     *
     * @param   style   format style
     * @param   locale  language and country setting
     * @return  localized format pattern
     * @throws  UnsupportedOperationException if given style is not supported
     *          or if no localized format pattern support is available
     * @see     net.time4j.format.LocalizedPatternSupport
     * @since   3.10/4.7
     */
    String getFormatPattern(
        DisplayStyle style,
        Locale locale
    );

}
