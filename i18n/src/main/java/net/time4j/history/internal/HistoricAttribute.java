/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricAttribute.java) is part of project Time4J.
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

package net.time4j.history.internal;

import net.time4j.engine.AttributeKey;
import net.time4j.format.Attributes;
import net.time4j.history.ChronoHistory;


/**
 * <p>Collection of some format attributes for internal purposes only. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Eine Menge von einigen Formatattributen nur f&uuml;r interne Zwecke. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public final class HistoricAttribute {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Format attribute which determines the calendar history. </p>
     *
     * <p>Users will not directly use this attribute but adjust a given {@code ChronoFormatter}
     * by its method {@code with(ChronoHistory)}. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(net.time4j.history.ChronoHistory)
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Formatattribut, das die Kalenderhistorie bestimmt. </p>
     *
     * <p>Anwender werden nicht direkt dieses Attribut verwenden, sondern stattdessen die
     * Methode {@code ChronoFormatter.with(ChronoHistory)} aufrufen. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(net.time4j.history.ChronoHistory)
     * @since   3.14/4.11
     */
    public static final AttributeKey<ChronoHistory> CALENDAR_HISTORY =
        Attributes.createKey("CALENDAR_HISTORY", ChronoHistory.class);

    /**
     * <p>Format attribute which prefers the notation of &quot;Common Era&quot; in formatting
     * an enum of type {@link net.time4j.history.HistoricEra}. </p>
     *
     * <p>Users will not directly use this attribute but call the method
     * {@code ChronoFormatter.withAlternativeEraNames()} instead. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#withAlternativeEraNames()
     */
    /*[deutsch]
     * <p>Formatattribut, das eine alternative nicht-christliche Schreibweise f&uuml;r die Formatierung
     * eines Enums des Typs {@link net.time4j.history.HistoricEra} bevorzugt. </p>
     *
     * <p>Anwender werden nicht direkt dieses Attribut verwenden, sondern stattdessen die
     * Methode {@code ChronoFormatter.withAlternativeEraNames()} aufrufen. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#withAlternativeEraNames()
     */
    public static final AttributeKey<Boolean> COMMON_ERA = Attributes.createKey("COMMON_ERA", Boolean.class);

    /**
     * <p>Format attribute which enforces latin notations of historic eras ignoring the locale. </p>
     *
     * <p>Users will not directly use this attribute but call the method
     * {@code ChronoFormatter.withLatinEraNames()} instead. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#withLatinEraNames()
     */
    /*[deutsch]
     * <p>Formatattribut, das eine lateinische Schreibweise f&uuml;r die Formatierung
     * eines Enums des Typs {@link HistoricEra} erzwingt, ohne Ber&uuml;cksichtigung der Spracheinstellung. </p>
     *
     * <p>Anwender werden nicht direkt dieses Attribut verwenden, sondern stattdessen die
     * Methode {@code ChronoFormatter.withLatinEraNames()} aufrufen. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#withLatinEraNames()
     */
    public static final AttributeKey<Boolean> LATIN_ERA = Attributes.createKey("LATIN_ERA", Boolean.class);

    /**
     * <p>Format attribute which transmits min width information to year-of-era. </p>
     *
     * <p>Users will never directly use this attribute but define the min width on the formatter instead. </p>
     *
     * @since   3.14/4.11
     */
    public static final AttributeKey<Integer> MIN_WIDTH_OF_YEAR =
        Attributes.createKey("MIN_WIDTH_OF_YEAR", Integer.class);

    /**
     * <p>Format attribute which transmits max width information to year-of-era. </p>
     *
     * <p>Users will never directly use this attribute but define the max width on the formatter instead. </p>
     *
     * @since   3.14/4.11
     */
    public static final AttributeKey<Integer> MAX_WIDTH_OF_YEAR =
        Attributes.createKey("MAX_WIDTH_OF_YEAR", Integer.class);

    //~ Konstruktoren -----------------------------------------------------

    private HistoricAttribute() {
        // no instantiation

    }

}
