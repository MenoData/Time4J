/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StyleProcessor.java) is part of project Time4J.
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
import net.time4j.PlainTimestamp;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BridgeChronology;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.Chronology;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;

import java.io.IOException;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Set;


/**
 * <p>Stil-Formatierung einer chronologischen Entit&auml;t. </p>
 *
 * @param   <T> generic type of entity values
 * @author  Meno Hochschild
 * @since   3.26/4.22
 */
final class StyleProcessor<T>
    implements FormatProcessor<T> {

    //~ Instanzvariablen ----------------------------------------------

    private final ChronoFormatter<T> formatter;
    private final FormatStyle dateStyle;
    private final FormatStyle timeStyle;

    //~ Konstruktoren -----------------------------------------------------

    StyleProcessor(
        FormatStyle dateStyle,
        FormatStyle timeStyle
    ) {
        this(
            null, // will be later set in quickPath()-method
            dateStyle,
            timeStyle
        );

    }

    private StyleProcessor(
        ChronoFormatter<T> formatter,
        FormatStyle dateStyle,
        FormatStyle timeStyle
    ) {
        super();

        if ((dateStyle == null) || (timeStyle == null)) {
            throw new NullPointerException("Missing display style.");
        }

        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
        this.formatter = formatter;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public int print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions, // optional
        boolean quickPath
    ) throws IOException {

        ChronoFormatter<T> cf = this.getFormatter(attributes, quickPath, formattable);
        Set<ElementPosition> newPositions = cf.print(formattable, buffer, attributes, positions != null);

        if (positions != null) {
            assert (newPositions != null);
            positions.addAll(newPositions);
        }

        return Integer.MAX_VALUE;

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        ChronoFormatter<T> cf = this.getFormatter(attributes, quickPath, null);
        T result = cf.parse(text, status, attributes);

        if (!status.isError() && (result != null)) {
            parsedResult.setResult(result);
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof StyleProcessor) {
            StyleProcessor<?> that = (StyleProcessor) obj;
            if ((this.dateStyle == that.dateStyle) && (this.timeStyle == that.timeStyle)) {
                if (this.formatter == null) {
                    return (that.formatter == null);
                } else {
                    return this.formatter.equals(that.formatter);
                }
            }
        }

        return false;

    }

    @Override
    public int hashCode() {

        return ((this.formatter == null) ? 0 : this.formatter.hashCode());

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[date-style=");
        sb.append(this.dateStyle);
        sb.append(",time-style=");
        sb.append(this.timeStyle);
        sb.append(",delegate=");
        sb.append(this.formatter);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public ChronoElement<T> getElement() {

        return null;

    }

    @Override
    public FormatProcessor<T> withElement(ChronoElement<T> element) {

        return this;

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    @Override
    public FormatProcessor<T> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        TransitionStrategy strategy =
            attributes.get(Attributes.TRANSITION_STRATEGY, Timezone.DEFAULT_CONFLICT_STRATEGY);
        TZID tzid = attributes.get(Attributes.TIMEZONE_ID, null);
        Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);

        ChronoFormatter<T> cf =
            createFormatter(
                formatter.getChronology(),
                this.dateStyle,
                this.timeStyle,
                locale,
                attributes.get(Attributes.FOUR_DIGIT_YEAR, Boolean.FALSE).booleanValue(),
                (tzid == null) ? null : Timezone.of(tzid).with(strategy),
                null);

        return new StyleProcessor<>(cf, this.dateStyle, this.timeStyle);

    }

    /**
     * <p>Supports changing the locale. </p>
     *
     * @return  date style
     * @since   5.8
     */
    FormatStyle getDateStyle() {

        return this.dateStyle;

    }

    /**
     * <p>Obtains the generated pattern. </p>
     *
     * @return  pattern maybe empty
     */
    String getGeneratedPattern() {

        return (this.formatter == null) ? "" : this.formatter.getPattern();

    }

    private ChronoFormatter<T> getFormatter(
        AttributeQuery attributes,
        boolean quickPath,
        ChronoDisplay formattable // when printing
    ) {

        if (quickPath) {
            if (formattable == null) {
                return this.formatter;
            } else if (formattable instanceof LocalizedPatternSupport) {
                LocalizedPatternSupport lps = LocalizedPatternSupport.class.cast(formattable);
                if (!lps.useDynamicFormatPattern()) {
                    return this.formatter; // static use case
                }
            }
        }

        AttributeQuery internal = this.formatter.getAttributes();
        TransitionStrategy strategy =
            attributes.get(
                Attributes.TRANSITION_STRATEGY,
                internal.get(Attributes.TRANSITION_STRATEGY, Timezone.DEFAULT_CONFLICT_STRATEGY));
        TZID tzid =
            attributes.get(
                Attributes.TIMEZONE_ID,
                internal.get(Attributes.TIMEZONE_ID, null));
        Timezone tz = ((tzid == null) ? null : Timezone.of(tzid).with(strategy));

        return createFormatter(
            this.formatter.getChronology(),
            this.dateStyle,
            this.timeStyle,
            attributes.get(Attributes.LANGUAGE, this.formatter.getLocale()),
            attributes.get(Attributes.FOUR_DIGIT_YEAR, Boolean.FALSE).booleanValue(),
            tz,
            formattable);

    }

    @SuppressWarnings("unchecked")
    private static <T> ChronoFormatter<T> createFormatter(
        Chronology<?> chronology,
        FormatStyle dateStyle,
        FormatStyle timeStyle,
        Locale locale,
        boolean fourDigitYear,
        Timezone tz, // optional
        ChronoDisplay formattable // when printing
    ) {

        String pattern;

        if (chronology.equals(PlainTimestamp.axis())) {
            pattern = CalendarText.patternForTimestamp(dateStyle, timeStyle, locale);
        } else if (chronology.equals(Moment.axis())) {
            pattern = CalendarText.patternForMoment(dateStyle, timeStyle, locale);
        } else if (chronology.getChronoType() == CalendarDate.class) {
            Chronology<?> c = chronology;
            while (c instanceof BridgeChronology) {
                c = c.preparser();
            }
            if (LocalizedPatternSupport.class.isAssignableFrom(c.getChronoType())) {
                assert (dateStyle == timeStyle);
                pattern = c.getFormatPattern(dateStyle, locale);
            } else {
                throw new UnsupportedOperationException("Localized format patterns not available: " + chronology);
            }
        } else if (formattable instanceof LocalizedPatternSupport) {
            assert (dateStyle == timeStyle);
            pattern = LocalizedPatternSupport.class.cast(formattable).getFormatPattern(dateStyle, locale);
        } else if (LocalizedPatternSupport.class.isAssignableFrom(chronology.getChronoType())) {
            assert (dateStyle == timeStyle);
            pattern = chronology.getFormatPattern(dateStyle, locale);
        } else {
            throw new UnsupportedOperationException("Localized format patterns not available: " + chronology);
        }

        if (fourDigitYear && pattern.contains("yy") && !pattern.contains("yyy")) {
            pattern = pattern.replace("yy", "yyyy");
        }

        if (chronology.getChronoType() == CalendarDate.class) {
            return (ChronoFormatter<T>) ChronoFormatter.ofGenericCalendarPattern(pattern, locale);
        }

        ChronoFormatter<?> cf = ChronoFormatter.ofPattern(pattern, PatternType.CLDR, locale, chronology);

        if (tz != null) {
            cf = cf.with(tz);
        }

        return (ChronoFormatter<T>) cf;

    }

}
