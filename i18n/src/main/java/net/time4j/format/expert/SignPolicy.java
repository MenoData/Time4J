/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.format.expert;


/**
 * <p>Determines a suitable strategy for handling numerical signs. </p>
 *
 * <p>Note: Signs can usually only occur in ISO-8601-context. Therefore
 * Time4J will never process signs in a localized way. That means signs
 * are the ASCII-chars &#39;+&#39; and &#39;-&#39;. A sign precedes the
 * sequence of numerical digits. Localized formats of elements with signs
 * (for example in arab language) require a special {@code ChronoPrinter}
 * and {@code ChronoParser}. </p>
 *
 * <p>A parser will only pay attention to this configuration in strict
 * mode. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
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
     * <p>A sign will never be printed or accepted in parsing. </p>
     *
     * <p>This setting is the default. </p>
     */
    /*[deutsch]
     * <p>Das Vorzeichen wird niemals ausgegeben oder akzeptiert. </p>
     *
     * <p>Diese Einstellung dient als Standardvorgabe. </p>
     */
    SHOW_NEVER,

    /**
     * <p>A positive sign will never be printed, but a negative sign is
     * always printed. </p>
     *
     * <p>This setting is the default for proleptic years in ISO-8601 format
     * if the year numbers have less than four, but not two digits. </p>
     */
    /*[deutsch]
     * <p>Ein positives Vorzeichen wird niemals ausgegeben, aber ein negatives
     * Vorzeichen immer. </p>
     *
     * <p>Diese Einstellung ist Vorgabe f&uuml;r Jahre im ISO-8601-Format, wenn
     * die Jahreszahlen weniger als 5, aber nicht 2 Stellen haben. </p>
     */
    SHOW_WHEN_NEGATIVE,

    /**
     * <p>A positive sign will be printed if the numerical amount has more
     * digits than specified. </p>
     *
     * <p>This setting is the default for proleptic years in ISO-8601-format
     * if the year numbers have more than four digits. Negative signs will
     * always be printed. </p>
     */
    /*[deutsch]
     * <p>Ein positives Vorzeichen wird ausgegeben, wenn der zugeh&ouml;rige
     * numerische Betrag mehr Stellen hat als minimal vorgegeben. </p>
     *
     * <p>Diese Einstellung ist Vorgabe f&uuml;r Jahre im ISO-8601-Format, wenn
     * die Jahreszahlen mehr als 4 Stellen haben. Negative Vorzeichen werden
     * immer ausgegeben. </p>
     */
    SHOW_WHEN_BIG_NUMBER,

    /**
     * <p>The sign will always be printed. </p>
     */
    /*[deutsch]
     * <p>Das Vorzeichen wird immer ausgegeben. </p>
     */
    SHOW_ALWAYS;

}
