/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TemporalFormatters.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ChronoPattern;
import net.time4j.format.DisplayMode;
import net.time4j.format.SignPolicy;
import net.time4j.format.TextWidth;

import java.util.Arrays;
import java.util.Locale;

import static net.time4j.PlainDate.CALENDAR_DATE;
import static net.time4j.PlainDate.DAY_OF_MONTH;
import static net.time4j.PlainDate.DAY_OF_WEEK;
import static net.time4j.PlainDate.DAY_OF_YEAR;
import static net.time4j.PlainDate.MONTH_AS_NUMBER;
import static net.time4j.PlainDate.YEAR;
import static net.time4j.PlainDate.YEAR_OF_WEEKDATE;
import static net.time4j.PlainTime.ISO_HOUR;
import static net.time4j.PlainTime.MINUTE_OF_HOUR;
import static net.time4j.PlainTime.NANO_OF_SECOND;
import static net.time4j.PlainTime.SECOND_OF_MINUTE;
import static net.time4j.PlainTime.WALL_TIME;


/**
 * <p>Sammlung von vordefinierten Format-Objekten. </p>
 *
 * @author  Meno Hochschild
 */
public class TemporalFormatters {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format mit Jahr, Monat und
     * Tag des Monats im Muster &quot;uuuuMMdd&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> ISO_BASIC_CALENDAR_DATE;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format mit Jahr, Monat und
     * Tag des Monats im Muster &quot;uuuu-MM-dd&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> ISO_EXTENDED_CALENDAR_DATE;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format mit Jahr und
     * Tag des Jahres im Muster &quot;uuuuDDD&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> ISO_BASIC_ORDINAL_DATE;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format mit Jahr und
     * Tag des Jahres im Muster &quot;uuuu-DDD&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> ISO_EXTENDED_ORDINAL_DATE;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r ein
     * Wochendatum im Muster &quot;YYYYWwwE&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> ISO_BASIC_WEEKDATE;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r ein
     * Wochendatum im Muster &quot;YYYY-Www-E&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> ISO_EXTENDED_WEEKDATE;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r eine
     * Uhrzeit nur mit Stunde und Minute im Muster &quot;HHmm&quot;. </p>
     */
    public static final ChronoFormatter<PlainTime> ISO_BASIC_TIME_HH_MM;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r eine
     * Uhrzeit inklusive Sekunde und optional Nanosekunde im Muster
     * &quot;HHmmss&quot;. </p>
     */
    public static final ChronoFormatter<PlainTime> ISO_BASIC_TIME_HH_MM_SS;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r eine
     * Uhrzeit nur mit Stunde und Minute im Muster &quot;HH:mm&quot;. </p>
     */
    public static final ChronoFormatter<PlainTime> ISO_EXTENDED_TIME_HH_MM;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r eine
     * Uhrzeit inklusive Sekunde und optional Nanosekunde im Muster
     * &quot;HH:mm:ss&quot;. </p>
     */
    public static final ChronoFormatter<PlainTime> ISO_EXTENDED_TIME_HH_MM_SS;

    /**
     * <p>Definiert das RFC-1123-Format, das zum Beispiel in Mail-Headers
     * verwendet wird. </p>
     *
     * <p>Entspricht &quot;[EEE, ]d MMM yyyy HH:mm[:ss] XX&quot;, wobei
     * der Zeitzonen-Offset XX so modifiziert ist, da&szlig; im Fall eines
     * Null-Offsets bevorzugt der Ausdruck &quot;GMT&quot; benutzt wird. Als
     * Null-Offset werden auch &quot;UT&quot; oder &quot;Z&quot; akzeptiert.
     * Die Textelemente werden ohne Beachtung der Gro&szlig;- oder
     * Kleinschreibung in Englisch interpretiert. </p>
     *
     * <p>Zu beachten: Im Gegensatz zum RFC-1123-Standard unterst&uuml;tzt die
     * Methode keine milit&auml;rischen Zeitzonen (A-Y) oder nordamerikanischen
     * Zeitzonennamen (EST, EDT, CST, CDT, MST, MDT, PST, PDT). </p>
     */
    public static final ChronoFormatter<Moment> RFC_1123;

    static {
        ISO_BASIC_CALENDAR_DATE = calendarFormat(false);
        ISO_EXTENDED_CALENDAR_DATE = calendarFormat(true);
        ISO_BASIC_ORDINAL_DATE = ordinalFormat(false);
        ISO_EXTENDED_ORDINAL_DATE = ordinalFormat(true);
        ISO_BASIC_WEEKDATE = weekdateFormat(false);
        ISO_EXTENDED_WEEKDATE = weekdateFormat(true);

        ISO_BASIC_TIME_HH_MM = isoFormat(false, false);
        ISO_EXTENDED_TIME_HH_MM = isoFormat(false, true);
        ISO_BASIC_TIME_HH_MM_SS = isoFormat(true, false);
        ISO_EXTENDED_TIME_HH_MM_SS = isoFormat(true, true);

        RFC_1123 =
            ChronoFormatter.setUp(Moment.class, Locale.ENGLISH)
            .startSection(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE)
            .startOptionalSection()
            .startSection(Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED)
            .addText(PlainDate.DAY_OF_WEEK)
            .endSection()
            .addLiteral(", ")
            .endSection()
            .addInteger(PlainDate.DAY_OF_MONTH, 1, 2)
            .addLiteral(' ')
            .startSection(Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED)
            .addText(PlainDate.MONTH_OF_YEAR)
            .endSection()
            .addLiteral(' ')
            .addFixedInteger(PlainDate.YEAR, 4)
            .addLiteral(' ')
            .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
            .addLiteral(':')
            .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
            .startOptionalSection()
            .addLiteral(':')
            .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
            .endSection()
            .addLiteral(' ')
            .addTimezoneOffset(
                DisplayMode.MEDIUM,
                false,
                Arrays.asList("GMT", "UT", "Z"))
            .endSection()
            .build();
    }

    //~ Konstruktoren -----------------------------------------------------

    private TemporalFormatters() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erstellt ein neues Formatobjekt, das eine Komposition der angegebenen
     * Datums- und Uhrzeitformate darstellt. </p>
     *
     * <p>Die Sprach- und L&auml;ndereinstellung wird vom Datumsformat
     * &uuml;bernommen. </p>
     *
     * @param   dateFormat      calendar date formatter
     * @param   timeFormat      walltime formatter
     * @return  composed format object for a plain timestamp
     */
    public static ChronoFormatter<PlainTimestamp> compose(
        ChronoFormatter<PlainDate> dateFormat,
        ChronoFormatter<PlainTime> timeFormat
    ) {

        return ChronoFormatter
            .setUp(PlainTimestamp.class, dateFormat.getLocale())
            .addCustomized(CALENDAR_DATE, dateFormat)
            .addCustomized(WALL_TIME, timeFormat)
            .build();

    }

    /**
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Musters
     * in der Standard-Sprach- und L&auml;ndereinstellung und in der
     * System-Zeitzone. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen oder Zeitzonen
     * angepasst werden. </p>
     *
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code Moment}-objects
     *          using system locale and system time zone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     PatternType#CLDR
     * @see     ChronoFormatter#with(Locale)
     * @see     ChronoFormatter#withTimezone(net.time4j.tz.TZID)
     */
    public static ChronoFormatter<Moment> localized(
        String formatPattern,
        ChronoPattern patternType
    ) {

        return ChronoFormatter
            .setUp(Moment.class, Locale.getDefault())
            .addPattern(formatPattern, patternType)
            .build()
            .withSystemTimezone();

    }

    /**
     * <p>Erzeugt ein neues Datumsformat mit Hilfe des angegebenen Musters
     * in der Standard-Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen angepasst werden. </p>
     *
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code PlainDate}-objects
     *          using system locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     PatternType#CLDR
     * @see     ChronoFormatter#with(Locale)
     */
    public static ChronoFormatter<PlainDate> localizedDate(
        String formatPattern,
        ChronoPattern patternType
    ) {

        return ChronoFormatter
            .setUp(PlainDate.class, Locale.getDefault())
            .addPattern(formatPattern, patternType)
            .build();

    }

    /**
     * <p>Erzeugt ein neues Uhrzeitformat mit Hilfe des angegebenen Musters
     * in der Standard-Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen angepasst werden. </p>
     *
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code PlainTime}-objects
     *          using system locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     PatternType#CLDR
     * @see     ChronoFormatter#with(Locale)
     */
    public static ChronoFormatter<PlainTime> localizedTime(
        String formatPattern,
        ChronoPattern patternType
    ) {

        return ChronoFormatter
            .setUp(PlainTime.class, Locale.getDefault())
            .addPattern(formatPattern, patternType)
            .build();

    }

    private static ChronoFormatter<PlainDate> calendarFormat(boolean extended) {

        ChronoFormatter.Builder<PlainDate> builder =
            ChronoFormatter
            .setUp(PlainDate.class, Locale.ROOT)
            .addInteger(YEAR, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addFixedInteger(MONTH_AS_NUMBER, 2);

        if (extended) {
            builder.addLiteral('-');
        }

        return builder.addFixedInteger(DAY_OF_MONTH, 2).build();

    }

    private static ChronoFormatter<PlainDate> ordinalFormat(boolean extended) {

        ChronoFormatter.Builder<PlainDate> builder =
            ChronoFormatter
            .setUp(PlainDate.class, Locale.ROOT)
            .addInteger(YEAR, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        return builder.addFixedInteger(DAY_OF_YEAR, 3).build();

    }

    private static ChronoFormatter<PlainDate> weekdateFormat(boolean extended) {

        ChronoFormatter.Builder<PlainDate> builder =
            ChronoFormatter
            .setUp(PlainDate.class, Locale.ROOT)
            .addInteger(
                YEAR_OF_WEEKDATE,
                4,
                9,
                SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addLiteral('W');
        builder.addFixedInteger(Weekmodel.ISO.weekOfYear(), 2);

        if (extended) {
            builder.addLiteral('-');
        }

        return builder.addFixedNumerical(DAY_OF_WEEK, 1).build();

    }

    private static ChronoFormatter<PlainTime> isoFormat(
        boolean full,
        boolean extended
    ) {

        ChronoFormatter.Builder<PlainTime> builder =
            ChronoFormatter
            .setUp(PlainTime.class, Locale.ROOT)
            .addFixedInteger(ISO_HOUR, 2);

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(MINUTE_OF_HOUR, 2);

        if (full) {
            if (extended) {
                builder.addLiteral(':');
            }

            builder.addFixedInteger(SECOND_OF_MINUTE, 2);
            builder.startOptionalSection();
            builder.addFixedInteger(NANO_OF_SECOND, 9);
            builder.endSection();
        }

        return builder.build();

    }

}
