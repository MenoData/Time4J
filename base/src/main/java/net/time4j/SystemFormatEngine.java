/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SystemFormatEngine.java) is part of project Time4J.
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

import net.time4j.engine.ChronoEntity;
import net.time4j.format.FormatEngine;
import net.time4j.format.Leniency;
import net.time4j.format.TemporalFormatter;
import net.time4j.scale.UniversalTime;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


/**
 * <p>The system format engine. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
class SystemFormatEngine
    implements FormatEngine<Platform> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final FormatEngine<Platform> INSTANCE = new SystemFormatEngine();
    static final String RFC_1123_PATTERN = "<RFC-1123>";

    private static final Set<Class<?>> SUPPORTED_TYPES;

    static {
        Set<Class<?>> tmp = new HashSet<Class<?>>();
        tmp.add(PlainDate.class);
        tmp.add(PlainTime.class);
        tmp.add(PlainTimestamp.class);
        tmp.add(Moment.class);
        SUPPORTED_TYPES = Collections.unmodifiableSet(tmp);
    }

    //~ Konstruktoren -----------------------------------------------------

    // Singleton-Konstruktor
    private SystemFormatEngine() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public <T extends ChronoEntity<T>> TemporalFormatter<T> create(
        Class<T> chronoType,
        String formatPattern,
        Platform patternType,
        Locale locale
    ) {

        if (patternType == null) {
            throw new NullPointerException("Missing pattern type.");
        } else if (!this.isSupported(chronoType)) {
            throw new IllegalArgumentException("Not formattable: " + chronoType);
        }

        return new SystemTemporalFormatter<T>(
            chronoType,
            formatPattern,
            locale,
            Leniency.SMART,
            null
        );

    }

    @Override
    public TemporalFormatter<? extends UniversalTime> createRFC1123() {

        return new SystemTemporalFormatter<Moment>(
            Moment.class,
            RFC_1123_PATTERN,
            Locale.ENGLISH,
            Leniency.SMART,
            "GMT"
        );

    }

    @Override
    public Platform getDefaultPatternType() {

        return Platform.PATTERN;

    }

    @Override
    public boolean isSupported(Class<?> chronoType) {

        return SUPPORTED_TYPES.contains(chronoType);

    }

}
