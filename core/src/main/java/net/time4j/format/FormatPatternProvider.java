/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FormatPatternProvider.java) is part of project Time4J.
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * <p>This <strong>SPI-interface</strong> enables the access to localized
 * date-, time- or interval patterns according to the CLDR-specifiation and is instantiated via a
 * {@code ServiceLoader}-mechanism. </p>
 *
 * <p>If there is no external {@code FormatPatternProvider} then Time4J will
 * just delegate to the JDK. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor.</p>
 *
 * @author  Meno Hochschild
 * @since   3.9/4.6
 * @see     java.util.ServiceLoader
 * @see     java.text.SimpleDateFormat#toPattern()
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> erm&ouml;glicht den Zugriff
 * auf {@code Locale}-abh&auml;ngige Formatmuster f&uuml;r Datum, Uhrzeit oder Intervalle
 * entsprechend der CLDR-Spezifikation und wird &uuml;ber einen {@code ServiceLoader}-Mechanismus
 * instanziert. </p>
 *
 * <p>Wird kein externer {@code FormatPatternProvider} gefunden, wird intern
 * eine Instanz erzeugt, die an das JDK delegiert. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor.</p>
 *
 * @author  Meno Hochschild
 * @since   3.9/4.6
 * @see     java.util.ServiceLoader
 * @see     java.text.SimpleDateFormat#toPattern()
 */
public interface FormatPatternProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Default provider which delegates to standard JVM resources. </p>
     */
    /*[deutsch]
     * <p>Standardimplementierung, die an die Ressourcen der JVM delegiert. </p>
     */
    FormatPatternProvider DEFAULT =
        new FormatPatternProvider() {
            @Override
            public String getDatePattern(Locale locale, DisplayMode mode) {
                int style = this.getFormatStyle(mode);
                DateFormat df = DateFormat.getDateInstance(style, locale);
                return this.getFormatPattern(df);
            }

            @Override
            public String getTimePattern(Locale locale, DisplayMode mode) {
                int style = this.getFormatStyle(mode);
                DateFormat df = DateFormat.getTimeInstance(style, locale);
                return this.removeZones(this.getFormatPattern(df));
            }

            @Override
            public String getDateTimePattern(Locale locale, DisplayMode mode) {
                int style = this.getFormatStyle(mode);
                DateFormat df = DateFormat.getDateTimeInstance(style, style, locale);
                return this.getFormatPattern(df);
            }

            @Override
            public String getIntervalPattern(Locale locale) {
                return "{0} - {1}";
            }

            private int getFormatStyle(DisplayMode mode) {
                switch (mode) {
                    case FULL:
                        return DateFormat.FULL;
                    case LONG:
                        return DateFormat.LONG;
                    case MEDIUM:
                        return DateFormat.MEDIUM;
                    case SHORT:
                        return DateFormat.SHORT;
                    default:
                        throw new UnsupportedOperationException("Unknown: " + mode);
                }
            }

            private String getFormatPattern(DateFormat df) {
                if (df instanceof SimpleDateFormat) {
                    return SimpleDateFormat.class.cast(df).toPattern();
                }
                throw new IllegalStateException("Cannot retrieve format pattern: " + df);
            }

            // JDK-Patterns hinten, mittig und vorne von Zeitzonen-Symbolen befreien
            private String removeZones(String pattern) {
                String s = pattern.replace(" z", "");
                if (s.charAt(s.length() - 1) == 'z') {
                    for (int i = s.length() - 1; i > 0; i--) {
                        if (s.charAt(i - 1) != 'z') {
                            s = s.substring(0, i).trim();
                            break;
                        }
                    }
                }
                if (s.charAt(0) == 'z') {
                    for (int i = 1; i < s.length(); i++) {
                        if (s.charAt(i) != 'z') {
                            s = s.substring(i).trim();
                            break;
                        }
                    }
                }
                return s;
            }
        };

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the localized date pattern. </p>
     *
     * @param   locale      language and country setting
     * @param   mode        display mode
     * @return  localized date pattern
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Datumsmuster. </p>
     *
     * @param   locale      language and country setting
     * @param   mode        display mode
     * @return  localized date pattern
     */
    String getDatePattern(
        Locale locale,
        DisplayMode mode
    );

    /**
     * <p>Returns the localized time pattern. </p>
     *
     * <p>Implementation note: This method must not return a pattern which contains any timezone offset. </p>
     *
     * @param   locale      language and country setting
     * @param   mode        display mode
     * @return  localized time pattern
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Uhrzeitmuster. </p>
     *
     * <p>Implementierungshinweis: Diese Methode darf keine Zeitzonen-Offsets im Muster enthalten. </p>
     *
     * @param   locale      language and country setting
     * @param   mode        display mode
     * @return  localized time pattern
     */
    String getTimePattern(
        Locale locale,
        DisplayMode mode
    );

    /**
     * <p>Returns the localized date-time pattern. </p>
     *
     * @param   locale      language and country setting
     * @param   mode        display mode
     * @return  localized date-time pattern
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Datums- und Uhrzeitmuster. </p>
     *
     * @param   locale      language and country setting
     * @param   mode        display mode
     * @return  localized date-time pattern
     */
    String getDateTimePattern(
        Locale locale,
        DisplayMode mode
    );

    /**
     * <p>Returns the localized interval pattern. </p>
     *
     * <p>Expressions of the form &quot;{0}&quot; will be interpreted as the start boundary format
     * and expressions of the form &quot;{1}&quot; will be interpreted as the end boundary format</p>
     *
     * @param   locale      language and country setting
     * @return  localized interval pattern
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Intervallmuster. </p>
     *
     * <p>Die Ausdr&uuml;cke &quot;{0}&quot; und &quot;{1}&quot; werden als Formathalter f&uuml;r die
     * Start- und End-Intervallgrenzen interpretiert. </p>
     *
     * @param   locale      language and country setting
     * @return  localized interval pattern
     */
    String getIntervalPattern(Locale locale);

}
