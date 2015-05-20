/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UltimateFormatEngine.java) is part of project Time4J.
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

package net.time4j.i18n;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.Attributes;
import net.time4j.format.DisplayMode;
import net.time4j.format.FormatEngine;
import net.time4j.format.TemporalFormatter;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.scale.UniversalTime;
import net.time4j.tz.ZonalOffset;

import java.util.Arrays;
import java.util.Locale;


/**
 * <p>The expert format engine. </p>
 *
 * @author  Meno Hochschild
 * @since   3.1
 */
public final class UltimateFormatEngine
    implements FormatEngine<PatternType> {

    //~ Statische Felder/Initialisierungen --------------------------------

    public static final FormatEngine<PatternType> INSTANCE = new UltimateFormatEngine();

    //~ Konstruktoren -----------------------------------------------------

    // for service loader only
    public UltimateFormatEngine() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public <T extends ChronoEntity<T>> TemporalFormatter<T> create(
        Class<T> chronoType,
        String formatPattern,
        PatternType patternType,
        Locale locale
    ) {

        if (this.isSupported(chronoType)) {
            return ChronoFormatter.setUp(chronoType, locale).addPattern(formatPattern, patternType).build();
        } else {
            throw new IllegalArgumentException("Not formattable: " + chronoType);
        }

    }

    @Override
    public TemporalFormatter<? extends UniversalTime> createRFC1123() {

        return ChronoFormatter.setUp(Moment.class, Locale.ENGLISH)
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
            .build()
            .withTimezone(ZonalOffset.UTC);

    }

    @Override
    public PatternType getDefaultPatternType() {

        return PatternType.CLDR;

    }

    @Override
    public boolean isSupported(Class<?> chronoType) {

        return ChronoEntity.class.isAssignableFrom(chronoType);

    }

}