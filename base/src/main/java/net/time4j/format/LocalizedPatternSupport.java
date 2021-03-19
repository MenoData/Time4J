/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LocalizedPatternSupport.java) is part of project Time4J.
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

import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;

import java.time.format.FormatStyle;
import java.util.Locale;


/**
 * <p>Marker interface which indicates support for general localized format patterns compatible with
 * the CLDR-specification. </p>
 *
 * @author  Meno Hochschild
 * @see     net.time4j.engine.ChronoMerger#getFormatPattern(FormatStyle, Locale)
 * @since   3.10/4.7
 */
/*[deutsch]
 * <p>Marker-Interface, das die Unterst&uuml;tzung von lokalisierten CLDR-kompatiblen Formatmustern
 * signalisiert. </p>
 *
 * @author  Meno Hochschild
 * @see     net.time4j.engine.ChronoMerger#getFormatPattern(FormatStyle, Locale)
 * @since   3.10/4.7
 */
public interface LocalizedPatternSupport {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines a CLDR-compatible localized format pattern suitable for printing. </p>
     *
     * <p>The default implementation delegates to the underlying chronology. </p>
     *
     * @param   style   format style
     * @param   locale  language and country setting
     * @return  localized format pattern
     * @since   5.1
     * @deprecated  Use {@link #getFormatPattern(FormatStyle, Locale)}
     */
    /*[deutsch]
     * <p>Definiert ein CLDR-kompatibles lokalisiertes Formatmuster f&uuml;r die Textausgabe. </p>
     *
     * <p>Die Standardimplementierung delegiert an die zugrundeliegende Chronologie. </p>
     *
     * @param   style   format style
     * @param   locale  language and country setting
     * @return  localized format pattern
     * @since   5.1
     * @deprecated  Use {@link #getFormatPattern(FormatStyle, Locale)}
     */
    @Deprecated
    default String getFormatPattern(
        DisplayStyle style,
        Locale locale
    ) {
        return getFormatPattern(style.toThreeten(), locale);
    }

    /**
     * <p>Defines a CLDR-compatible localized format pattern suitable for printing. </p>
     *
     * <p>The default implementation delegates to the underlying chronology. </p>
     *
     * @param   style   format style
     * @param   locale  language and country setting
     * @return  localized format pattern
     * @since   5.8
     */
    /*[deutsch]
     * <p>Definiert ein CLDR-kompatibles lokalisiertes Formatmuster f&uuml;r die Textausgabe. </p>
     *
     * <p>Die Standardimplementierung delegiert an die zugrundeliegende Chronologie. </p>
     *
     * @param   style   format style
     * @param   locale  language and country setting
     * @return  localized format pattern
     * @since   5.8
     */
    default String getFormatPattern(
        FormatStyle style,
        Locale locale
    ) {
        return Chronology.lookup(this.getClass()).getFormatPattern(style, locale);
    }

    /**
     * <p>Determines if any created format pattern uses the state of this instance. </p>
     *
     * <p>The default implementation returns {@code false}. </p>
     *
     * @return  {@code true} if the method {@code getFormatPattern} uses the state of this instance else {@code false}
     * @see     #getFormatPattern(FormatStyle, Locale)
     * @since   5.1
     */
    /*[deutsch]
     * <p>Ermittelt, ob erzeugte Formatmuster den Zustand dieser Instanz auswerten. </p>
     *
     * <p>Die Standardimplementierung liefert {@code false}. </p>
     *
     * @return  {@code true} if the method {@code getFormatPattern} uses the state of this instance else {@code false}
     * @see     #getFormatPattern(FormatStyle, Locale)
     * @since   5.1
     */
    default boolean useDynamicFormatPattern() {
        return false;
    }

}
