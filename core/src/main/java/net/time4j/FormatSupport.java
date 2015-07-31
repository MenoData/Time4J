/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FormatSupport.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.ResourceLoader;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.ChronoPattern;
import net.time4j.format.DisplayMode;
import net.time4j.format.FormatEngine;
import net.time4j.format.TemporalFormatter;
import net.time4j.tz.TZID;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * <p>Defines some helper routines for format support of basic types. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
class FormatSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final FormatEngine<?> DEFAULT_FORMAT_ENGINE;

    static {
        FormatEngine<?> last = null;
        FormatEngine<?> best = null;

        for (FormatEngine<?> tmp : ResourceLoader.getInstance().services(FormatEngine.class)) {
            if (tmp.isSupported(ChronoEntity.class)) {
                best = tmp;
                break;
            } else {
                last = tmp;
            }
        }

        if (best == null) {
            if (last == null) {
                best = Platform.PATTERN.getFormatEngine();
            } else {
                best = last;
            }
        }

        DEFAULT_FORMAT_ENGINE = best;
    }

    //~ Konstruktoren -----------------------------------------------------

    private FormatSupport() {
        // no instantiation

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erzeugt einen Formatierer f&uuml;r lokale Entit&auml;ten. </p>
     *
     * @param   <T> generic type of associated chronological entities
     * @param   <P> generic type of pattern
     * @param   chronoType      chronological type
     * @param   formatPattern   pattern defining the structure of formatter
     * @param   patternType     type of pattern indicating the format engine
     * @param   locale          language and country setting
     * @return  new temporal formatter
     * @since   3.0
     */
    static <T extends ChronoEntity<T>, P extends ChronoPattern<P>> TemporalFormatter<T> createFormatter(
        Class<T> chronoType,
        String formatPattern,
        P patternType,
        Locale locale
    ) {

        FormatEngine<P> formatEngine = patternType.getFormatEngine();
        return formatEngine.create(chronoType, formatPattern, patternType, locale);

    }

    /**
     * <p>Erzeugt einen Formatierer f&uuml;r globale Entit&auml;ten. </p>
     *
     * @param   <T> generic type of associated chronological entities
     * @param   <P> generic type of pattern
     * @param   chronoType      chronological type
     * @param   formatPattern   pattern defining the structure of formatter
     * @param   patternType     type of pattern indicating the format engine
     * @param   locale          language and country setting
     * @param   tzid            timezone id
     * @return  new temporal formatter
     * @since   3.0
     */
    static <T extends ChronoEntity<T>, P extends ChronoPattern<P>> TemporalFormatter<T> createFormatter(
        Class<T> chronoType,
        String formatPattern,
        P patternType,
        Locale locale,
        TZID tzid
    ) {

        return createFormatter(chronoType, formatPattern, patternType, locale).withTimezone(tzid);

    }

    /**
     * <p>Erzeugt einen Formatierer f&uuml;r lokale Entit&auml;ten. </p>
     *
     * @param   <T> generic type of associated chronological entities
     * @param   chronoType      chronological type
     * @param   formatPattern   format pattern
     * @param   locale          language and country setting
     * @return  new temporal formatter
     * @since   3.0
     */
    static <T extends ChronoEntity<T>> TemporalFormatter<T> createFormatter(
        Class<T> chronoType,
        String formatPattern,
        Locale locale
    ) {

        return createFormatter(chronoType, DEFAULT_FORMAT_ENGINE, formatPattern, locale);

    }

    /**
     * <p>Erzeugt einen Formatierer f&uuml;r globale Entit&auml;ten. </p>
     *
     * @param   <T> generic type of associated chronological entities
     * @param   chronoType      chronological type
     * @param   formatPattern   format pattern
     * @param   locale          language and country setting
     * @param   tzid            timezone id
     * @return  new temporal formatter
     * @since   3.0
     */
    static <T extends ChronoEntity<T>> TemporalFormatter<T> createFormatter(
        Class<T> chronoType,
        String formatPattern,
        Locale locale,
        TZID tzid
    ) {

        return createFormatter(chronoType, DEFAULT_FORMAT_ENGINE, formatPattern, locale).withTimezone(tzid);

    }

    /**
     * <p>Hilfsmethode zum Konvertieren des Anzeigestils in eine
     * {@code DateFormat}-Konstante. </p>
     *
     * @param   mode    Anzeigestil von Time4J
     * @return  JDK-Anzeigestil
     * @since   3.0
     */
    static int getFormatStyle(DisplayMode mode) {

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

    /**
     * <p>Extrahiert ein Formatmuster, wenn m&ouml;glich. </p>
     *
     * @param   df      JDK-DateFormat
     * @return  format pattern
     * @throws  IllegalStateException if format pattern cannot be determined
     * @since   3.0
     */
    static String getFormatPattern(DateFormat df) {

        if (df instanceof SimpleDateFormat) {
            return SimpleDateFormat.class.cast(df).toPattern();
        }

        throw new IllegalStateException("Cannot retrieve format pattern.");

    }

    /**
     * <p>Yields the best available format engine. </p>
     *
     * @return  format engine
     * @since   3.0
     */
    static FormatEngine<?> getDefaultFormatEngine() {

        return DEFAULT_FORMAT_ENGINE;

    }

    private static <T extends ChronoEntity<T>, P extends ChronoPattern<P>> TemporalFormatter<T> createFormatter(
        Class<T> chronoType,
        FormatEngine<P> formatEngine,
        String formatPattern,
        Locale locale
    ) {

        return formatEngine.create(chronoType, formatPattern, formatEngine.getDefaultPatternType(), locale);

    }

}
