/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.DisplayMode;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;

import java.io.IOException;
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
    private final DisplayStyle dateStyle;
    private final DisplayStyle timeStyle;

    //~ Konstruktoren -----------------------------------------------------

    StyleProcessor(
        DisplayStyle dateStyle,
        DisplayStyle timeStyle
    ) {
        this(
            null, // will be later set in quickPath()-method
            dateStyle,
            timeStyle
        );

    }

    private StyleProcessor(
        ChronoFormatter<T> formatter,
        DisplayStyle dateStyle,
        DisplayStyle timeStyle
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
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions, // optional
        boolean quickPath
    ) throws IOException {

        Set<ElementPosition> newPositions =
            this.formatter.print(formattable, buffer, attributes, positions != null);

        if (positions != null) {
            positions.addAll(newPositions);
        }

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        ChronoFormatter<T> cf;

        if (quickPath) {
            cf = this.formatter;
        } else {
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
            cf = createFormatter(
                this.formatter.getChronology(),
                this.dateStyle,
                this.timeStyle,
                attributes.get(Attributes.LANGUAGE, this.formatter.getLocale()),
                attributes.get(Attributes.FOUR_DIGIT_YEAR, Boolean.FALSE).booleanValue(),
                tz);
        }

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
            if (this.dateStyle.equals(that.dateStyle) && this.timeStyle.equals(that.timeStyle)) {
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
                (tzid == null) ? null : Timezone.of(tzid).with(strategy));

        return new StyleProcessor<T>(cf, this.dateStyle, this.timeStyle);

    }

    @SuppressWarnings("unchecked")
    private static <T> ChronoFormatter<T> createFormatter(
        Chronology<?> chronology,
        DisplayStyle dateStyle,
        DisplayStyle timeStyle,
        Locale locale,
        boolean fourDigitYear,
        Timezone tz // optional
    ) {

        String pattern;

        if (chronology.equals(PlainDate.axis())) {
            pattern = CalendarText.patternForDate((DisplayMode) dateStyle, locale);
        } else if (chronology.equals(PlainTime.axis())) {
            pattern = CalendarText.patternForTime((DisplayMode) timeStyle, locale);
        } else if (chronology.equals(PlainTimestamp.axis())) {
            pattern = CalendarText.patternForTimestamp((DisplayMode) dateStyle, (DisplayMode) timeStyle, locale);
        } else if (chronology.equals(Moment.axis())) {
            pattern = CalendarText.patternForMoment((DisplayMode) dateStyle, (DisplayMode) timeStyle, locale);
        } else if (LocalizedPatternSupport.class.isAssignableFrom(chronology.getChronoType())) {
            assert (dateStyle == timeStyle);
            pattern = chronology.getFormatPattern(dateStyle, locale);
        } else {
            throw new UnsupportedOperationException("Localized format patterns not available: " + chronology);
        }

        if (fourDigitYear && pattern.contains("yy") && !pattern.contains("yyy")) {
            pattern = pattern.replace("yy", "yyyy");
        }

        ChronoFormatter<?> cf = ChronoFormatter.ofPattern(pattern, PatternType.CLDR, locale, chronology);

        if (tz != null) {
            cf = cf.with(tz);
        }

        return (ChronoFormatter<T>) cf;

    }

}
