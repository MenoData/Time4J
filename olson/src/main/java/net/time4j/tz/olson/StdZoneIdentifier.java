/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdZoneIdentifier.java) is part of project Time4J.
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

package net.time4j.tz.olson;

import net.time4j.tz.TZID;


// IMPLEMENTIERUNGSHINWEISE:
// -----------------------------------------------------------------------
// Add => Neue Enum-Konstanten bevorzugt lexikalisch sortiert einfügen
//        (ordinal-Werte sind ohnehin nicht zur Speicherung vorgesehen)
// -----------------------------------------------------------------------
// Remove => BEISPIEL FUER DIE QUASI-ENTFERNUNG EINER ID AUS DEM STANDARD:
//
//        /** @deprecated  Use "Europe/Chisinau" instead. */
//        @Deprecated TIRASPOL("Tiraspol")
// ***********************************************************************

/**
 * <p>Identifies a timezone in a standard way. </p>
 *
 * <p>This timezone ID has the Olson-format &quot;{region}/{city}&quot;. </p>
 *
 * <p>Lexical comparisons of IDs should always be done by the method
 * {@link #canonical()} because an object of type {@code TZID} is only
 * designed for encapsulating a canonical name. <strong>The comparison
 * using the method {@code equals()} is not allowed. </strong></p>
 *
 * <p>The predefined enum constants actually mirror the TZ-version
 * <span style="text-decoration:underline;"><tt>2013i</tt></span> and
 * are usually associated wih timezones whose rules have changed or are
 * about to change. The enum constants do <strong>NOT</strong> mean
 * that they are also valid or that there are always well-defined timezone
 * data behind. For example we have {@code ASIA.HEBRON} which exists
 * first since TZ-version 2011n. Another example is the ID
 * &quot;Europe/Tiraspol&quot; which existed for a short time and is
 * missing in the version 2011n however. Such timezone IDs will be marked
 * as <i>deprecated</i> and labelled with a suitable alias. Purpose of
 * predefined constants is just a safe and performant access (protection
 * against typing errors). </p>
 *
 * <p>If a timezone offset is known for historical timezones before the year
 * 1970 then users should generally prefer the class {@code ZonalOffset} because
 * the timezone data associated with the enum constants are not necessarily
 * correct. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable, thread-safe and serializable. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Identifiziert eine Zeitzone per IANA-Standard. </p>
 *
 * <p>Die ID liegt im Olson-Format &quot;{region}/{city}&quot; vor. </p>
 *
 * <p>Ein (lexikalischer) Vergleich von IDs sollte immer &uuml;ber die Methode
 * {@link #canonical()} gemacht werden, weil ein {@code TZID} nur dem Zweck
 * dient, einen kanonischen Namen zu kapseln. <strong>Der Vergleich &uuml;ber
 * die Objekt-Methode {@code equals()} ist nicht erlaubt. </strong></p>
 *
 * <p>Die vordefinierten Enum-Konstanten spiegeln aktuell die TZ-Version
 * <span style="text-decoration:underline;"><tt>2013i</tt></span> wider
 * und sind in der Regel mit Zeitzonen verkn&uuml;pft, deren Regeln sich im
 * Laufe der Zeit ge&auml;ndert haben oder es aktuell tun. Die Enum-Konstanten
 * bedeuten <strong>NICHT</strong>, da&szlig; sie auch g&uuml;ltig
 * sind bzw. da&szlig; dazu immer Zonendaten existieren. Zum Beispiel
 * gibt es {@code ASIA.HEBRON} erst seit der TZ-Version 2011n.
 * Ein anderes Beispiel ist die ID &quot;Europe/Tiraspol&quot;, die kurz
 * mal existierte, in der Version 2011n aber fehlt. Entsprechende IDs
 * werden in zuk&uuml;nftigen API-Releases als <i>deprecated</i> markiert
 * und mit einem passenden Aliasnamen dokumentiert. Sinn der vordefinierten
 * Konstanten ist nur ein sicherer und performanter Zugang (Schutz gegen
 * Schreibfehler!). </p>
 *
 * <p>Falls f&uuml;r historische Zeitangaben vor dem Jahr 1970 ein Offset
 * wohlbekannt ist, ist generell der Klasse {@code ZonalOffset} der Vorzug
 * vor den Enum-Konstanten zu geben, weil die mit den Enums verkn&uuml;pften
 * historischen Zeitzonendaten nicht notwendig korrekt sein m&uuml;ssen. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable, thread-safe and serializable. </p>
 *
 * @author  Meno Hochschild
 */
public interface StdZoneIdentifier
    extends TZID {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the timezone region - in most cases a continent. </p>
     *
     * @return  the first part of zone identifier (for example
     *          &quot;Europe&quot; in &quot;Europe/Paris&quot;)
     */
    /*[deutsch]
     * <p>Liefert die Regionskennung - meistens ein Kontinent. </p>
     *
     * @return  der erste Teil der Zeitzonen-ID (zum Beispiel
     *          &quot;Europe&quot; in &quot;Europe/Paris&quot;)
     */
    String getRegion();

    /**
     * <p>Yields the exemplar city. </p>
     *
     * @return  the second part of zone identifier (for example
     *          &quot;Paris&quot; in &quot;Europe/Paris&quot;)
     */
    /*[deutsch]
     * <p>Liefert die Stadtkennung (ausgew&auml;hlte Beispiel-Stadt). </p>
     *
     * @return  der zweite Teil der Zeitzonen-ID (zum Beispiel
     *          &quot;Paris&quot; in &quot;Europe/Paris&quot;)
     */
    String getCity();

    /**
     * <p>Yields the belonging country in ISO-3166-format - related to the exemplar city. </p>
     *
     * @return  country code
     * @since   3.1
     */
    /*[deutsch]
     * <p>Liefert den zugeh&ouml;rigen L&auml;nder-Code im ISO-3166-format - bezogen auf die Beispielstadt. </p>
     *
     * @return  L&auml;nder-Code
     * @since   3.1
     */
    String getCountry();

}
