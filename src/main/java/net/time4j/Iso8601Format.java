/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Iso8601Format.java) is part of project Time4J.
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

import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.DisplayMode;
import net.time4j.format.SignPolicy;

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
 * <p>Sammlung von vordefinierten Format-Objekten. </p>
 *
 * @author  Meno Hochschild
 */
public class Iso8601Format {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final NonZeroCondition NON_ZERO_SECOND =
        new NonZeroCondition(PlainTime.SECOND_OF_MINUTE);
    private static final NonZeroCondition NON_ZERO_FRACTION =
        new NonZeroCondition(PlainTime.NANO_OF_SECOND);
    private static final ChronoCondition<ChronoEntity<?>> SECOND_PART =
        NON_ZERO_SECOND.or(NON_ZERO_FRACTION);

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format mit Jahr, Monat und
     * Tag des Monats im Muster &quot;uuuuMMdd&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> BASIC_CALENDAR_DATE;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format mit Jahr, Monat und
     * Tag des Monats im Muster &quot;uuuu-MM-dd&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> EXTENDED_CALENDAR_DATE;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format mit Jahr und
     * Tag des Jahres im Muster &quot;uuuuDDD&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> BASIC_ORDINAL_DATE;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format mit Jahr und
     * Tag des Jahres im Muster &quot;uuuu-DDD&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> EXTENDED_ORDINAL_DATE;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r ein
     * Wochendatum im Muster &quot;YYYYWwwE&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> BASIC_WEEK_DATE;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r ein
     * Wochendatum im Muster &quot;YYYY-Www-E&quot;. </p>
     */
    public static final ChronoFormatter<PlainDate> EXTENDED_WEEK_DATE;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r eine
     * Uhrzeit mit Stunde und Minute im Muster &quot;HHmm&quot;. </p>
     *
     * <p>Die weiteren Elemente wie Sekunde und Nanosekunde sind optional.
     * Auch die Anzahl der Dezimalstellen ist variabel. </p>
     */
    public static final ChronoFormatter<PlainTime> BASIC_WALL_TIME;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r eine
     * Uhrzeit mit Stunde und Minute im Muster &quot;HH:mm&quot;. </p>
     *
     * <p>Die weiteren Elemente wie Sekunde und Nanosekunde sind optional.
     * Auch die Anzahl der Dezimalstellen ist variabel. </p>
     */
    public static final ChronoFormatter<PlainTime> EXTENDED_WALL_TIME;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r eine Kombination
     * aus Kalenderdatum und Uhrzeit mit Stunde und Minute im Muster
     * &quot;uuuuMMdd'T'HHmm[ss[SSSSSSSSS]]&quot;. </p>
     *
     * <p>Sekunde und Nanosekunde sind optional. Auch die Anzahl der
     * Dezimalstellen ist variabel. </p>
     */
    public static final ChronoFormatter<PlainTimestamp> BASIC_DATE_TIME;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r eine
     * Kombination aus Kalenderdatum und Uhrzeit mit Stunde und Minute
     * im Muster &quot;uuuu-MM-dd'T'HH:mm[:ss[,SSSSSSSSS]]&quot;. </p>
     *
     * <p>Sekunde und Nanosekunde sind optional. Auch die Anzahl der
     * Dezimalstellen ist variabel. </p>
     */
    public static final ChronoFormatter<PlainTimestamp> EXTENDED_DATE_TIME;

    /**
     * <p>Definiert das <i>basic</i> ISO-8601-Format f&uuml;r eine Kombination
     * aus Kalenderdatum, Uhrzeit mit Stunde und Minute und Offset im Muster
     * &quot;uuuuMMdd'T'HHmm[ss[SSSSSSSSS]]X&quot;. </p>
     *
     * <p>Sekunde und Nanosekunde sind optional. Auch die Anzahl der
     * Dezimalstellen ist variabel. </p>
     */
    public static final ChronoFormatter<Moment> BASIC_DATE_TIME_OFFSET;

    /**
     * <p>Definiert das <i>extended</i> ISO-8601-Format f&uuml;r eine
     * Kombination aus Kalenderdatum, Uhrzeit mit Stunde und Minute und Offset
     * im Muster &quot;uuuu-MM-dd'T'HH:mm[:ss[,SSSSSSSSS]]X&quot;. </p>
     *
     * <p>Sekunde und Nanosekunde sind optional. Auch die Anzahl der
     * Dezimalstellen ist variabel. </p>
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
            ChronoFormatter
            .setUp(PlainDate.class, Locale.ROOT);
        addCalendarDate(builder, extended);
        return builder.build();

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

    private static ChronoFormatter<PlainTime> timeFormat(boolean extended) {

        ChronoFormatter.Builder<PlainTime> builder =
            ChronoFormatter
            .setUp(PlainTime.class, Locale.ROOT)
            .addFixedInteger(ISO_HOUR, 2);

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(MINUTE_OF_HOUR, 2);
        addSeconds(builder, extended);
        return builder.build();

    }

    private static ChronoFormatter<PlainTimestamp> timestampFormat(
        boolean extended
    ) {

        ChronoFormatter.Builder<PlainTimestamp> builder =
            ChronoFormatter
            .setUp(PlainTimestamp.class, Locale.ROOT);
        addCalendarDate(builder, extended);
        builder.addLiteral('T');
        builder.addFixedInteger(ISO_HOUR, 2);

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(MINUTE_OF_HOUR, 2);
        addSeconds(builder, extended);
        return builder.build();

    }

    private static ChronoFormatter<Moment> momentFormat(boolean extended) {

        ChronoFormatter.Builder<Moment> builder =
            ChronoFormatter
            .setUp(Moment.class, Locale.ROOT);

        addCalendarDate(builder, extended);
        builder.addLiteral('T');
        builder.addFixedInteger(ISO_HOUR, 2);

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(MINUTE_OF_HOUR, 2);
        addSeconds(builder, extended);

        builder.addTimezoneOffset(
            DisplayMode.SHORT,
            extended,
            Collections.singletonList("Z"));

        return builder.build();

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

    private static <T extends ChronoEntity<T>> void addSeconds(
        ChronoFormatter.Builder<T> builder,
        boolean extended
    ) {

        builder.startOptionalSection(SECOND_PART);

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(SECOND_OF_MINUTE, 2);
        builder.startOptionalSection(NON_ZERO_FRACTION);
        builder.addFraction(NANO_OF_SECOND, 0, 9, true);
        builder.endSection();
        builder.endSection();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class NonZeroCondition
        implements ChronoCondition<ChronoEntity<?>> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<Integer> element;

        //~ Konstruktoren -------------------------------------------------

        NonZeroCondition(ChronoElement<Integer> element) {
            super();

            this.element = element;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean test(ChronoEntity<?> context) {

            return (
                !context.contains(this.element)
                || (context.get(this.element).intValue() != 0)
            );

        }

        ChronoCondition<ChronoEntity<?>> or(final NonZeroCondition other) {

            return new ChronoCondition<ChronoEntity<?>>() {
                @Override
                public boolean test(ChronoEntity<?> context) {
                    return (
                        NonZeroCondition.this.test(context)
                        || other.test(context)
                    );
                }
            };

        }

    }

}
