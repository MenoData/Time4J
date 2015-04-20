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

import net.time4j.engine.Chronology;
import net.time4j.format.FormatEngine;
import net.time4j.format.Leniency;
import net.time4j.format.TemporalFormatter;
import net.time4j.scale.UniversalTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


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

    private static final Map<Class<?>, Chronology<?>> SUPPORTED_TYPES;

    static {
        Map<Class<?>, Chronology<?>> tmp = new HashMap<Class<?>, Chronology<?>>();
        tmp.put(PlainDate.class, PlainDate.axis());
        tmp.put(PlainTime.class, PlainTime.axis());
        tmp.put(PlainTimestamp.class, PlainTimestamp.axis());
        tmp.put(Moment.class, Moment.axis());
        tmp.put(ZonalDateTime.class, null);
        SUPPORTED_TYPES = Collections.unmodifiableMap(tmp);
    }

    //~ Konstruktoren -----------------------------------------------------

    // Singleton-Konstruktor
    private SystemFormatEngine() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public <T> TemporalFormatter<T> create(
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

        return SUPPORTED_TYPES.containsKey(chronoType);

    }

    /**
     * Liefert die unterst&uuml;tzen Typen.
     *
     * @return  unmodifiable type map
     */
    static Map<Class<?>, Chronology<?>> getSupportedTypes() {

        return SUPPORTED_TYPES;

    }

}
