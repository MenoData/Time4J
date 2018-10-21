/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TemporalFormatter.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.AttributeQuery;
import net.time4j.tz.TZID;

import java.text.ParseException;
import java.util.Locale;


/**
 * <p>Generic facade for any temporal/chronological format object which can print temporal objects to text
 * or parse texts to temporal objects. </p>
 *
 * @param   <T> generic type of applicable chronological types
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Allgemeine Fassade von Zeitformatobjekten, die temporale Objekte zu Text formatieren oder umgekehrt Text als
 * temporale Objekte interpretieren. </p>
 *
 * @param   <T> generic type of applicable chronological types
 * @author  Meno Hochschild
 * @since   3.0
 */
public interface TemporalFormatter<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Synonym for {@code print(T)}. </p>
     *
     * <p>This method adapts the standard naming style of most Java formatters
     * while Time4J prefers the name &quot;print&quot;. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   3.0
     */
    /*[deutsch]
     * <p>Synonym f&uuml;r {@code print(T)}. </p>
     *
     * <p>Diese Methode &uuml;bernimmt den &uuml;blichen Namensstil der meisten Java-Formatierer,
     * w&auml;hrend Time4J den Namen &quot;print&quot; bevorzugt. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   3.0
     */
    String format(T formattable);

    /**
     * <p>Prints given chronological entity as formatted text. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.0
     */
    /*[deutsch]
     * <p>Formatiert das angegebene Objekt als Text. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.0
     */
    String print(T formattable);

    /**
     * <p>Interpretes given text as chronological entity. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.0
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.0
     */
    T parse(CharSequence text) throws ParseException;

    /**
     * <p>Interpretes given text as chronological entity and updates given raw values. </p>
     *
     * @param   text        text to be parsed
     * @param   rawValues   holder for raw values (always as new instance)
     * @return  parsed result
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @throws  ParseException if parsing does not work
     * @since   4.0
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text und aktualisiert die &uuml;bergebenen Rohwerte. </p>
     *
     * @param   text        text to be parsed
     * @param   rawValues   holder for raw values (always as new instance)
     * @return  parsed result
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @throws  ParseException if parsing does not work
     * @since   4.0
     */
    T parse(
        CharSequence text,
        RawValues rawValues
    ) throws ParseException;

    /**
     * <p>Creates a copy of this formatter with given timezone id which
     * shall be used in formatting or parsing. </p>
     *
     * <p>The timezone is in most cases only relevant for the type
     * {@link net.time4j.Moment}. When formatting the timezone helps
     * to convert the UTC value into a zonal representation. When
     * parsing the timezone serves as replacement value if the formatted
     * text does not contain any timezone. </p>
     *
     * @param   tzid        timezone id
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     * @throws  IllegalArgumentException if timezone data cannot be loaded
     * @see     Attributes#TIMEZONE_ID
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit der angegebenen Zeitzone, die beim
     * Formatieren oder Parsen verwendet werden soll. </p>
     *
     * <p>Die Zeitzone ist nur f&uuml;r den Typ {@link net.time4j.Moment}
     * von Bedeutung. Beim Formatieren wandelt sie die UTC-Darstellung in
     * eine zonale Repr&auml;sentation um. Beim Parsen dient sie als
     * Ersatzwert, wenn im zu interpretierenden Text keine Zeitzone
     * gefunden werden konnte. </p>
     *
     * @param   tzid        timezone id
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     * @throws  IllegalArgumentException if timezone data cannot be loaded
     * @see     Attributes#TIMEZONE_ID
     * @since   3.0
     */
    TemporalFormatter<T> withTimezone(TZID tzid);

    /**
     * <p>Equivalent to {@link #withTimezone(TZID)
     * withTimezone(Timezone.of(tzid).getID())}. </p>
     *
     * @param   tzid    timezone id
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     #withTimezone(TZID)
     * @see     Attributes#TIMEZONE_ID
     * @since   3.0
     */
    /*[deutsch]
     * <p>Entspricht {@link #withTimezone(TZID)
     * withTimezone(Timezone.of(tzid).getID())}. </p>
     *
     * @param   tzid    timezone id
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     #withTimezone(TZID)
     * @see     Attributes#TIMEZONE_ID
     * @since   3.0
     */
    TemporalFormatter<T> withTimezone(String tzid);

    /**
     * <p>Creates a copy of this formatter with given locale. </p>
     *
     * <p>Note that changing the locale cannot change the inner structure
     * of this formatter even if the structure is no longer appropriate for
     * given locale. An example is the English AM/PM-pattern which will be
     * preserved even if the language changes from English to German. </p>
     *
     * @param   locale      new language and country configuration
     * @return  changed copy with given language and localized symbols while
     *          this instance remains unaffected
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit der alternativ angegebenen
     * Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Hinweis: Das &Auml;ndern der Sprach- und L&auml;ndereinstellung kann
     * nicht die innere Struktur dieses Formatierers &auml;ndern, selbst wenn die
     * innere Struktur f&uuml;r die gegebene Sprache nicht mehr geeignet ist.
     * Zum Beispiel wird das englische AM/PM-Format beibehalten, auch wenn sich
     * die Sprache von Englisch zu Deutsch &auml;ndert. </p>
     *
     * @param   locale      new language and country configuration
     * @return  changed copy with given language and localized symbols while
     *          this instance remains unaffected
     * @since   3.0
     */
    TemporalFormatter<T> with(Locale locale);

    /**
     * <p>Sets the leniency mode. </p>
     *
     * <p>By default any temporal formatter is smart. </p>
     *
     * @param   leniency    determines how strict the parser should be
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     * @see     Attributes#LENIENCY
     * @since   3.0
     */
    /*[deutsch]
     * <p>Setzt die Nachsichtigkeit. </p>
     *
     * <p>Standardm&auml;&szlig;ig ist ein Zeitformatobjekt <i>smart</i>. </p>
     *
     * @param   leniency    determines how strict the parser should be
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     * @see     Attributes#LENIENCY
     * @since   3.0
     */
    TemporalFormatter<T> with(Leniency leniency);

    /**
     * <p>Determines all global format attributes if available. </p>
     *
     * <p>Global attributes are valid for the whole formatter. Sectional attributes which might
     * exist and control the behaviour of only a part of the formatter cannot be overridden. </p>
     *
     * @return  attribute query
     * @since   3.0
     */
    /*[deutsch]
     * <p>Bestimmt alle globalen Formatattribute, die verf&uuml;gbar sind. </p>
     *
     * <p>Globale Formatattribute gelten f&uuml;r das gesamte Zeitformatobjekt. Sektionale Attribute, die
     * vielleicht existieren und das Verhalten nur eines Teils des Formatobjekts kontrollieren, k&ouml;nnen
     * nicht &uuml;berschrieben werden. </p>
     *
     * @return  attribute query
     * @since   3.0
     */
    AttributeQuery getAttributes();

}
