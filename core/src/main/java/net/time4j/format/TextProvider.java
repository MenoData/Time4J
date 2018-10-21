/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TextProvider.java) is part of project Time4J.
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

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * <p>This <strong>SPI-interface</strong> enables the access to calendrical
 * standard text informations and will be instantiated by a
 * {@code ServiceLoader}-mechanism. </p>
 *
 * <p>The motivation is mainly to override the language-dependent forms
 * of JDK-defaults with respect to standard elements like months, weekdays
 * etc. Specific text forms which are not contained in JDK will instead
 * be supplied by help of properties-files in the &quot;data&quot;-folder.
 * </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> erm&ouml;glicht den Zugriff
 * auf kalendarische Standard-Textinformationen und wird &uuml;ber einen
 * {@code ServiceLoader}-Mechanismus instanziert. </p>
 *
 * <p>Sinn und Zweck dieses Interface ist in erster Linie das sprachliche
 * Erg&auml;nzen oder &Uuml;berschreiben von JDK-Vorgaben bez&uuml;glich
 * der Standardelemente Monat, Wochentag etc. Kalenderspezifische Texte,
 * die gar nicht im JDK vorhanden sind, werden stattdessen mit Hilfe von
 * properties-Dateien im data-Verzeichnis bereitgestellt. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 * @see     java.util.ServiceLoader
 */
public interface TextProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Queries if a calendar type is supported by this text provider. </p>
     *
     * @param   calendarType    the calendar type to be checked
     * @return  {@code true} if given calendar type is supported else {@code false}
     * @since   3.32/4.27
     * @see     CalendarType
     */
    /*[deutsch]
     * <p>Fragt die Unterst&uuml;tzung eines Kalendertyps durch diesen {@code TextProvider} ab. </p>
     *
     * @param   calendarType    the calendar type to be checked
     * @return  {@code true} if given calendar type is supported else {@code false}
     * @since   3.32/4.27
     * @see     CalendarType
     */
    boolean supportsCalendarType(String calendarType);

    /**
     * <p>Queries if a language is supported by this text provider. </p>
     *
     * @param   language        the language to be checked
     * @return  {@code true} if given language is supported else {@code false}
     * @since   3.32/4.27
     */
    /*[deutsch]
     * <p>Fragt die Unterst&uuml;tzung einer Sprache durch diesen {@code TextProvider} ab. </p>
     *
     * @param   language        the language to be checked
     * @return  {@code true} if given language is supported else {@code false}
     * @since   3.32/4.27
     */
    boolean supportsLanguage(Locale language);

    /**
     * <p>Defines the supported calendar types. </p>
     *
     * @return  String-array with calendar types
     * @see     CalendarType
     */
    /*[deutsch]
     * <p>Definiert die unterst&uuml;tzten Kalendertypen. </p>
     *
     * @return  String-array with calendar types
     * @see     CalendarType
     */
    String[] getSupportedCalendarTypes();

    /**
     * <p>Yields the supported languages. </p>
     *
     * <p>Only the language part will be evaluated. </p>
     *
     * @return  Locale-array
     * @see     Locale#getLanguage()
     */
    /*[deutsch]
     * <p>Gibt die unterst&uuml;tzten Sprachen an. </p>
     *
     * <p>Nur der reine Sprachencode wird ausgewertet. </p>
     *
     * @return  Locale-array
     * @see     Locale#getLanguage()
     */
    Locale[] getAvailableLocales();

    /**
     * <p>See {@link CalendarText#getStdMonths}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @param   outputContext   output context
     * @param   leapForm        use leap form (for example the hebrew
     *                          month &quot;Adar II&quot;)?
     * @return  unmodifiable sorted array of month names
     */
    /*[deutsch]
     * <p>Siehe {@link CalendarText#getStdMonths}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @param   outputContext   output context
     * @param   leapForm        use leap form (for example the hebrew
     *                          month &quot;Adar II&quot;)?
     * @return  unmodifiable sorted array of month names
     */
    String[] months(
        String calendarType,
        Locale locale,
        TextWidth textWidth,
        OutputContext outputContext,
        boolean leapForm
    );

    /**
     * <p>See {@link CalendarText#getQuarters}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @param   outputContext   output context
     * @return  unmodifiable sorted array of quarter names
     */
    /*[deutsch]
     * <p>Siehe {@link CalendarText#getQuarters}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @param   outputContext   output context
     * @return  unmodifiable sorted array of quarter names
     */
    String[] quarters(
        String calendarType,
        Locale locale,
        TextWidth textWidth,
        OutputContext outputContext
    );

    /**
     * <p>See {@link CalendarText#getWeekdays}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @param   outputContext   output context
     * @return  unmodifiable sorted array of weekday names
     *          in calendar specific order (ISO-8601 starts with monday)
     */
    /*[deutsch]
     * <p>Siehe {@link CalendarText#getWeekdays}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @param   outputContext   output context
     * @return  unmodifiable sorted array of weekday names
     *          in calendar specific order (ISO-8601 starts with monday)
     */
    String[] weekdays(
        String calendarType,
        Locale locale,
        TextWidth textWidth,
        OutputContext outputContext
    );

    /**
     * <p>See {@link CalendarText#getEras}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @return  unmodifiable sorted array of era names
     */
    /*[deutsch]
     * <p>Siehe {@link CalendarText#getEras}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @return  unmodifiable sorted array of era names
     */
    String[] eras(
        String calendarType,
        Locale locale,
        TextWidth textWidth
    );

    /**
     * <p>See {@link CalendarText#getMeridiems}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @param   outputContext   output context
     * @return  unmodifiable sorted array of AM/PM-names
     */
    /*[deutsch]
     * <p>Siehe {@link CalendarText#getMeridiems}. </p>
     *
     * @param   calendarType    calendar type
     * @param   locale          language of text output
     * @param   textWidth       text width
     * @param   outputContext   output context
     * @return  unmodifiable sorted array of AM/PM-names
     */
    String[] meridiems(
        String calendarType,
        Locale locale,
        TextWidth textWidth,
        OutputContext outputContext
    );

    /**
     * <p>Returns a suitable object for controlling access to resources. </p>
     *
     * @return  helper object for accessing resources
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert ein geeignetes Hilfsobjekt zur fein-granularen Kontrolle
     * des Zugangs zu Ressourcen. </p>
     *
     * @return  helper object for accessing resources
     * @since   2.2
     */
    ResourceBundle.Control getControl();

}
