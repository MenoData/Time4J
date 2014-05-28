/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SignPolicy.java) is part of project Time4J.
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


/**
 * <p>Legt die Format- und Interpretationsstrategie f&uuml;r numerische
 * Vorzeichen fest. </p>
 *
 * <p>Notiz: Vorzeichen kommen in Time4J nur im ISO-8601-Kontext vor, daher
 * werden sie niemals lokalisiert behandelt. Das bedeutet, als Vorzeichen
 * werden die ASCII-Zeichen &#39;+&#39; und &#39;-&#39; benutzt und links von
 * der Ziffernfolge angeordnet. Lokalisierte Formate von vorzeichenbehafteten
 * Elementen etwa im arabischen Sprachraum ben&ouml;tigen einen speziellen
 * {@code ChronoPrinter} bzw. {@code ChronoParser}. </p>
 *
 * <p>Ein Parser wird diese Eigenschaft nur im strikten Modus beachten. </p>
 *
 * @author  Meno Hochschild
 */
public enum SignPolicy {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Das Vorzeichen wird niemals ausgegeben oder akzeptiert. </p>
     *
     * <p>Diese Einstellung dient als Standardvorgabe. </p>
     */
    SHOW_NEVER,

    /**
     * <p>Ein positives Vorzeichen wird niemals ausgegeben, aber ein negatives
     * Vorzeichen immer. </p>
     *
     * <p>Diese Einstellung ist Vorgabe f&uuml;r Jahre im ISO-8601-Format, wenn
     * die Jahreszahlen weniger als 5, aber nicht 2 Stellen haben. </p>
     */
    SHOW_WHEN_NEGATIVE,

    /**
     * <p>Ein positives Vorzeichen wird ausgegeben, wenn der zugeh&ouml;rige
     * numerische Betrag mehr Stellen hat als minimal vorgegeben. </p>
     *
     * <p>Diese Einstellung ist Vorgabe f&uuml;r Jahre im ISO-8601-Format, wenn
     * die Jahreszahlen mehr als 4 Stellen haben. Negative Vorzeichen werden
     * immer ausgegeben. </p>
     */
    SHOW_WHEN_BIG_NUMBER,

    /**
     * <p>Das Vorzeichen wird immer ausgegeben. </p>
     */
    SHOW_ALWAYS;

}
