/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Iso8601Format.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.tz.ZonalOffset;

import java.util.Collections;
import java.util.Locale;

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


/**
 * <p>Collection of predefined format objects for ISO-8601. </p>
 *
 * <p>All formatters are strict by default. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Sammlung von vordefinierten Format-Objekten f&uuml;r ISO-8601. </p>
 *
 * <p>Alle Formatierer sind per Vorgabe strikt. </p>
 *
 * @author  Meno Hochschild
 */
public class Iso8601Format {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final NonZeroCondition NON_ZERO_SECOND =
        new NonZeroCondition(PlainTime.SECOND_OF_MINUTE);
    private static final NonZeroCondition NON_ZERO_FRACTION =
        new NonZeroCondition(PlainTime.NANO_OF_SECOND);
    private static final ChronoCondition<ChronoDisplay> SECOND_PART =
        NON_ZERO_SECOND.or(NON_ZERO_FRACTION);

    /**
     * <p>Defines the <i>basic</i> ISO-8601-format with year, month and day
     * of month using the pattern &quot;uuuuMMdd&quot;. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>basic</i> ISO-8601-Format mit Jahr, Monat und
     * Tag des Monats im Muster &quot;uuuuMMdd&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> BASIC_CALENDAR_DATE;

    /**
     * <p>Defines the <i>extended</i> ISO-8601-format with year, month and
     * day of month using the pattern &quot;uuuu-MM-dd&quot;. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>extended</i> ISO-8601-Format mit Jahr, Monat und
     * Tag des Monats im Muster &quot;uuuu-MM-dd&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> EXTENDED_CALENDAR_DATE;

    /**
     * <p>Defines the <i>basic</i> ISO-8601-format with year and day of year
     * using the pattern &quot;uuuuDDD&quot;. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>basic</i> ISO-8601-Format mit Jahr und
     * Tag des Jahres im Muster &quot;uuuuDDD&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> BASIC_ORDINAL_DATE;

    /**
     * <p>Defines the <i>extended</i> ISO-8601-format with year and day
     * of year using the pattern &quot;uuuu-DDD&quot;. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>extended</i> ISO-8601-Format mit Jahr und
     * Tag des Jahres im Muster &quot;uuuu-DDD&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> EXTENDED_ORDINAL_DATE;

    /**
     * <p>Defines the <i>basic</i> ISO-8601-format for a week date using
     * the pattern &quot;YYYYWwwE&quot;. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r ein
     * Wochendatum im Muster &quot;YYYYWwwE&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> BASIC_WEEK_DATE;

    /**
     * <p>Defines the <i>extended</i> ISO-8601-format for a week date
     * using the pattern &quot;YYYY-Www-E&quot;. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r ein
     * Wochendatum im Muster &quot;YYYY-Www-E&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> EXTENDED_WEEK_DATE;

    /**
     * <p>Defines the <i>basic</i> ISO-8601-format for a wall time with
     * hour, minute and optional second using the pattern
     * &quot;HH[mm[ss[SSSSSSSSS]]]&quot;. </p>
     *
     * <p>The minute part is optional during parsing, but will always
     * be printed. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r eine
     * Uhrzeit mit Stunde, Minute und optionaler Sekunde im Muster
     * &quot;HH[mm[ss[SSSSSSSSS]]]&quot;. </p>
     *
     * <p>Der Minutenteil ist beim Parsen optional, wird aber beim
     * Formatieren immer ausgegeben. </p>
     */
    public static final ChronoFormatter<PlainTime> BASIC_WALL_TIME;

    /**
     * <p>Defines the <i>extended</i> ISO-8601-format for a wall time
     * with hour, minute and optional second using the pattern
     * &quot;HH[:mm[:ss[,SSSSSSSSS]]]&quot;. </p>
     *
     * <p>The minute part is optional during parsing, but will always
     * be printed. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r eine
     * Uhrzeit mit Stunde, Minute und optionaler Sekunde im Muster
     * &quot;HH[:mm[:ss[,SSSSSSSSS]]]&quot;. </p>
     *
     * <p>Der Minutenteil ist beim Parsen optional, wird aber beim
     * Formatieren immer ausgegeben. </p>
     */
    public static final ChronoFormatter<PlainTime> EXTENDED_WALL_TIME;

    /**
     * <p>Defines the <i>basic</i> ISO-8601-format for a composition of
     * calendar date and wall time with hour and minute using the pattern
     * &quot;uuuuMMdd'T'HH[mm[ss[SSSSSSSSS]]]&quot;. </p>
     *
     * <p>Second and nanosecond elements are optional. Furthermore,
     * the count of decimal digits is flexible (0-9). The minute part is
     * optional during parsing, but will always be printed. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r eine Kombination
     * aus Kalenderdatum und Uhrzeit mit Stunde und Minute im Muster
     * &quot;uuuuMMdd'T'HH[mm[ss[SSSSSSSSS]]]&quot;. </p>
     *
     * <p>Sekunde und Nanosekunde sind optional. Auch die Anzahl der
     * Dezimalstellen ist variabel (0-9). Der Minutenteil ist beim Parsen
     * optional, wird aber beim Formatieren immer ausgegeben. </p>
     */
    public static final ChronoFormatter<PlainTimestamp> BASIC_DATE_TIME;

    /**
     * <p>Defines the <i>extended</i> ISO-8601-format for a composition of
     * calendar date and wall time with hour and minute using the pattern
     * &quot;uuuu-MM-dd'T'HH[:mm[:ss[,SSSSSSSSS]]]&quot;. </p>
     *
     * <p>Second and nanosecond elements are optional. Furthermore,
     * the count of decimal digits is flexible (0-9). The minute part is
     * optional during parsing, but will always be printed. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r eine
     * Kombination aus Kalenderdatum und Uhrzeit mit Stunde und Minute
     * im Muster &quot;uuuu-MM-dd'T'HH[:mm[:ss[,SSSSSSSSS]]]&quot;. </p>
     *
     * <p>Sekunde und Nanosekunde sind optional. Auch die Anzahl der
     * Dezimalstellen ist variabel (0-9). Der Minutenteil ist beim Parsen
     * optional, wird aber beim Formatieren immer ausgegeben. </p>
     */
    public static final ChronoFormatter<PlainTimestamp> EXTENDED_DATE_TIME;

    /**
     * <p>Defines the <i>basic</i> ISO-8601-format for a composition of
     * calendar date, wall time and timezone offset using the pattern
     * &quot;uuuuMMdd'T'HH[mm[ss[SSSSSSSSS]]]{offset}&quot;. </p>
     *
     * <p>Second and nanosecond elements are optional. Furthermore,
     * the count of decimal digits is flexible (0-9). The minute part is
     * optional during parsing, but will always be printed. The offset
     * part is for printing equivalent to XX, for parsing equivalent to X. </p>
     *
     * <p>By default, the timezone offset used for printing is UTC+00:00.
     * Users can override this offset by calling the method
     * {@link ChronoFormatter#withTimezone(net.time4j.tz.TZID)}. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r eine Kombination
     * aus Kalenderdatum, Uhrzeit mit Stunde und Minute und Offset im Muster
     * &quot;uuuuMMdd'T'HH[mm[ss[SSSSSSSSS]]]{offset}&quot;. </p>
     *
     * <p>Sekunde und Nanosekunde sind optional. Auch die Anzahl der
     * Dezimalstellen ist variabel (0-9). Der Minutenteil ist beim Parsen
     * optional, wird aber beim Formatieren immer ausgegeben. Der Offset-Teil
     * ist f&uuml;r die formatierte Ausgabe &auml;quivalent zu XX, beim
     * Interpretieren &auml;quivalent zu X. </p>
     *
     * <p>Standardm&auml;&szlig;ig wird f&uuml;r die formatierte Ausgabe der
     * Zeitzonen-Offset UTC+00:00 verwendet. Diese Vorgabe kann mit Hilfe von
     * {@link ChronoFormatter#withTimezone(net.time4j.tz.TZID)} ge&auml;ndert
     * werden. </p>
     */
    public static final ChronoFormatter<Moment> BASIC_DATE_TIME_OFFSET;

    /**
     * <p>Defines the <i>extended</i> ISO-8601-format for a composition of
     * calendar date, wall time and timezone offset using the pattern
     * &quot;uuuu-MM-dd'T'HH[:mm[:ss[,SSSSSSSSS]]]{offset}&quot;. </p>
     *
     * <p>Second and nanosecond elements are optional. Furthermore,
     * the count of decimal digits is flexible (0-9). The minute part is
     * optional during parsing, but will always be printed. The offset
     * part is for printing equivalent to XXX, for parsing equivalent
     * to X (but with colon!).  </p>
     *
     * <p>By default, the timezone offset used for printing is UTC+00:00.
     * Users can override this offset by calling the method
     * {@link ChronoFormatter#withTimezone(net.time4j.tz.TZID)}. </p>
     */
    /*[deutsch]
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r eine
     * Kombination aus Kalenderdatum, Uhrzeit mit Stunde und Minute und Offset
     * im Muster &quot;uuuu-MM-dd'T'HH[:mm[:ss[,SSSSSSSSS]]]{offset}&quot;. </p>
     *
     * <p>Sekunde und Nanosekunde sind optional. Auch die Anzahl der
     * Dezimalstellen ist variabel (0-9). Der Minutenteil ist beim Parsen
     * optional, wird aber beim Formatieren immer ausgegeben.  Der Offset-Teil
     * ist f&uuml;r die formatierte Ausgabe &auml;quivalent zu XXX, beim
     * Interpretieren &auml;quivalent zu X (aber mit Doppelpunkt!). </p>
     *
     * <p>Standardm&auml;&szlig;ig wird f&uuml;r die formatierte Ausgabe der
     * Zeitzonen-Offset UTC+00:00 verwendet. Diese Vorgabe kann mit Hilfe von
     * {@link ChronoFormatter#withTimezone(net.time4j.tz.TZID)} ge&auml;ndert
     * werden. </p>
     */
    public static final ChronoFormatter<Moment> EXTENDED_DATE_TIME_OFFSET;

    static {
        BASIC_CALENDAR_DATE = calendarFormat(false);
        EXTENDED_CALENDAR_DATE = calendarFormat(true);
        BASIC_ORDINAL_DATE = ordinalFormat(false);
        EXTENDED_ORDINAL_DATE = ordinalFormat(true);
        BASIC_WEEK_DATE = weekdateFormat(false);
        EXTENDED_WEEK_DATE = weekdateFormat(true);

        BASIC_WALL_TIME = timeFormat(false);
        EXTENDED_WALL_TIME = timeFormat(true);

        BASIC_DATE_TIME = timestampFormat(false);
        EXTENDED_DATE_TIME = timestampFormat(true);

        BASIC_DATE_TIME_OFFSET = momentFormat(false);
        EXTENDED_DATE_TIME_OFFSET = momentFormat(true);
    }

    //~ Konstruktoren -----------------------------------------------------

    private Iso8601Format() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    private static ChronoFormatter<PlainDate> calendarFormat(boolean extended) {

        ChronoFormatter.Builder<PlainDate> builder =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT);
        addCalendarDate(builder, extended);
        return builder.build().with(Leniency.STRICT);

    }

    private static ChronoFormatter<PlainDate> ordinalFormat(boolean extended) {

        ChronoFormatter.Builder<PlainDate> builder =
            ChronoFormatter
                .setUp(PlainDate.class, Locale.ROOT)
                .addInteger(YEAR, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        return builder.addFixedInteger(DAY_OF_YEAR, 3).build().with(Leniency.STRICT);

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

        return builder.addFixedNumerical(DAY_OF_WEEK, 1).build().with(Leniency.STRICT);

    }

    private static ChronoFormatter<PlainTime> timeFormat(boolean extended) {

        ChronoFormatter.Builder<PlainTime> builder =
            ChronoFormatter.setUp(PlainTime.class, Locale.ROOT);
        addWallTime(builder, extended);
        return builder.build().with(Leniency.STRICT);

    }

    private static ChronoFormatter<PlainTimestamp> timestampFormat(
        boolean extended
    ) {

        ChronoFormatter.Builder<PlainTimestamp> builder =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT);
        addCalendarDate(builder, extended);
        builder.addLiteral('T');
        addWallTime(builder, extended);
        return builder.build().with(Leniency.STRICT);

    }

    private static ChronoFormatter<Moment> momentFormat(boolean extended) {

        ChronoFormatter.Builder<Moment> builder =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT);

        builder.addCustomized(
            Moment.axis().element(),
            momentFormat(DisplayMode.MEDIUM, extended),
            momentFormat(DisplayMode.SHORT, extended)
        );

        return builder.build().withTimezone(ZonalOffset.UTC).with(Leniency.STRICT);

    }

    private static ChronoFormatter<Moment> momentFormat(
        DisplayMode mode,
        boolean extended
    ) {

        ChronoFormatter.Builder<Moment> builder =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT);

        addCalendarDate(builder, extended);
        builder.addLiteral('T');
        addWallTime(builder, extended);

        builder.addTimezoneOffset(
            mode,
            extended,
            Collections.singletonList("Z"));

        return builder.build().withTimezone(ZonalOffset.UTC);

    }

    private static <T extends ChronoEntity<T>> void addCalendarDate(
        ChronoFormatter.Builder<T> builder,
        boolean extended
    ) {

        builder.addInteger(YEAR, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addFixedInteger(MONTH_AS_NUMBER, 2);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addFixedInteger(DAY_OF_MONTH, 2);

    }

    private static <T extends ChronoEntity<T>> void addWallTime(
        ChronoFormatter.Builder<T> builder,
        boolean extended
    ) {

        builder.addFixedInteger(ISO_HOUR, 2);
        builder.startOptionalSection();

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(MINUTE_OF_HOUR, 2);
        builder.startOptionalSection(SECOND_PART);

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(SECOND_OF_MINUTE, 2);
        builder.startOptionalSection(NON_ZERO_FRACTION);
        builder.addFraction(NANO_OF_SECOND, 0, 9, true);
        builder.endSection();
        builder.endSection();
        builder.endSection();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class NonZeroCondition
        implements ChronoCondition<ChronoDisplay> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<Integer> element;

        //~ Konstruktoren -------------------------------------------------

        NonZeroCondition(ChronoElement<Integer> element) {
            super();

            this.element = element;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean test(ChronoDisplay context) {

            return (
                context.contains(this.element)
                && (context.get(this.element).intValue() != 0)
            );

        }

        ChronoCondition<ChronoDisplay> or(final NonZeroCondition other) {

            return new ChronoCondition<ChronoDisplay>() {
                @Override
                public boolean test(ChronoDisplay context) {
                    return (
                        NonZeroCondition.this.test(context)
                        || other.test(context)
                    );
                }
            };

        }

    }

}
