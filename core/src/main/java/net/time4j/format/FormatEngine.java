/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FormatEngine.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.ChronoEntity;
import net.time4j.scale.UniversalTime;

import java.util.Locale;


/**
 * <p>This <strong>SPI-interface</strong> allows the creation of temporal formats via a builder-approach. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor.</p>
 *
 * @param   <P> generic type of applicable format patterns
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> erlaubt die Erzeugung von temporalen Formatobjekten
 * via Builder-Entwurfsmuster. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor.</p>
 *
 * @param   <P> generic type of applicable format patterns
 * @author  Meno Hochschild
 * @since   3.0
 */
public interface FormatEngine<P extends ChronoPattern<P>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new temporal format object applicable on given chronological type. </p>
     *
     * @param   <T> generic chronological type
     * @param   chronoType      chronological type any creatable format must work with
     * @param   formatPattern   pattern of symbols to be used in formatting and parsing
     * @param   patternType     type of pattern how to interprete symbols
     * @param   locale          language and regional setting
     * @return  temporal format object
     * @throws  IllegalArgumentException if given chronological type is not formattable
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Zeitformatobjekt, das auf den angegebenen chronologischen Typ anwendbar ist. </p>
     *
     * @param   <T> generic chronological type
     * @param   chronoType      chronological type any creatable format must work with
     * @param   formatPattern   pattern of symbols to be used in formatting and parsing
     * @param   patternType     type of pattern how to interprete symbols
     * @param   locale          language and regional setting
     * @return  temporal format object
     * @throws  IllegalArgumentException if given chronological type is not formattable
     * @since   3.0
     */
    <T extends ChronoEntity<T>> TemporalFormatter<T> create(
        Class<T> chronoType,
        String formatPattern,
        P patternType,
        Locale locale
    );

    /**
     * <p>Creates a specialized formatter for RFC-1123. </p>
     *
     * <p>SPECIFICATION: The chronological type must be <code>net.time4j.Moment</code>. </p>
     *
     * @return  temporal format object
     */
    TemporalFormatter<? extends UniversalTime> createRFC1123();

    /**
     * <p>Yields the default pattern type. </p>
     *
     * @return  {@code ChronoPattern}
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert den Standard-Formatmustertyp. </p>
     *
     * @return  {@code ChronoPattern}
     * @since   3.0
     */
    P getDefaultPatternType();

    /**
     * <p>Helps to find out if given chronological type is supported. </p>
     *
     * @param   chronoType      chronological type to be queried
     * @return  {@code true} if given type is formattable else {@code false}
     * @since   3.0
     */
    /*[deutsch]
     * <p>Hilft herauszufinden, ob der angegebene chronologische Typ formatierbar ist. </p>
     *
     * @param   chronoType      chronological type to be queried
     * @return  {@code true} if given type is formattable else {@code false}
     * @since   3.0
     */
    boolean isSupported(Class<?> chronoType);

}
