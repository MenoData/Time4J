/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.engine.ChronoEntity;
import net.time4j.format.FormatEngine;
import net.time4j.format.TemporalFormatter;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.scale.UniversalTime;

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

        return ChronoFormatter.RFC_1123;

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