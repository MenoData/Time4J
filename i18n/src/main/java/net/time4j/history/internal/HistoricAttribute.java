/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.PlainDate;
import net.time4j.engine.AttributeKey;
import net.time4j.format.Attributes;


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
     * <p>Format attribute which determines the historical calendar variant. </p>
     *
     * <p>Users will not directly use this attribute but adjust a given {@code ChronoFormatter}
     * by its method {@code with(ChronoHistory)}. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(net.time4j.history.ChronoHistory)
     */
    /*[deutsch]
     * <p>Formatattribut, das die historische Kalendervariante bestimmt. </p>
     *
     * <p>Anwender werden nicht direkt dieses Attribut verwenden, sondern stattdessen die
     * Methode {@code ChronoFormatter.with(ChronoHistory)} aufrufen. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(net.time4j.history.ChronoHistory)
     */
    public static final AttributeKey<HistoricVariant> HISTORIC_VARIANT =
        Attributes.createKey("HISTORIC_VARIANT", HistoricVariant.class);

    /**
     * <p>Format attribute which can cause the format engine to create a chronological history with
     * given cutover date. </p>
     *
     * <p>Users will not directly use this attribute but adjust a given {@code ChronoFormatter}
     * by its method {@code withGregorianCutOver()}. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#withGregorianCutOver(PlainDate)
     */
    /*[deutsch]
     * <p>Formatattribut, das die Formatmaschine dazu veranlassen kann, eine {@code ChronoHistory} f&uuml;r
     * den angegebenen Attributwert als Umstellungsdatum zu erzeugen. </p>
     *
     * <p>Anwender werden nicht direkt dieses Attribut verwenden, sondern stattdessen die
     * Methode {@code ChronoFormatter.withGregorianCutOver()} aufrufen. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#withGregorianCutOver(PlainDate)
     */
    public static final AttributeKey<PlainDate> CUTOVER_DATE =
        Attributes.createKey("CUTOVER_DATE", PlainDate.class);

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
     * <p>Format attribute which can cause the format engine to create a chronological history with
     * given triennal julian leap years. </p>
     *
     * <p>Users will not directly use this attribute but adjust a given {@code ChronoFormatter}
     * by its method {@code with(ChronoHistory)}. The value is a comma-separated list of integers. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(net.time4j.history.ChronoHistory)
     */
    /*[deutsch]
     * <p>Formatattribut, das die Formatmaschine dazu veranlassen kann, eine {@code ChronoHistory} f&uuml;r
     * die angegebenen julianischen Schaltjahre im 3-Jahres-Rhythmus zu erzeugen. </p>
     *
     * <p>Anwender werden nicht direkt dieses Attribut verwenden, sondern stattdessen die
     * Methode {@code ChronoFormatter.with(ChronoHistory)} aufrufen. Der Wert ist eine komma-getrennte
     * Auflistung von Integern. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(net.time4j.history.ChronoHistory)
     */
    public static final AttributeKey<Object> ANCIENT_JULIAN_LEAP_YEARS =
        Attributes.createKey("ANCIENT_JULIAN_LEAP_YEARS", Object.class);

    //~ Konstruktoren -----------------------------------------------------

    private HistoricAttribute() {
        // no instantiation

    }

}
