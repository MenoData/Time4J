/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TZID.java) is part of project Time4J.
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

package net.time4j.tz;


/**
 * <p>Identifies a timezone. </p>
 *
 * <p>In most cases, the timezone ID has the Olson-format
 * &quot;{region}/{city}&quot; or is an offset in the format
 * &quot;UTC&#x00B1;hh:mm&quot;. In latter case applications can
 * instead directly use an instance of type {@code ZonalOffset},
 * especially if the timezone offset for a given timepoint is
 * already known. </p>
 *
 * <p><strong>Provider-specific keys</strong> have the
 * {@link ZoneProvider#getName() name} of the provider followed by the
 * separator char &quot;~&quot; and the normal canonical identifier. This
 * form can be used if a custom registered {@link ZoneProvider} shall be
 * used instead of the default provider for lookup of the zonal data. For
 * example following key will search the timezone data via the API of
 * {@code java.util.TimeZone} even if there is configured another default
 * zone provider: &quot;java.util.TimeZone~Europe/Berlin&quot; </p>
 *
 * <p>Lexical comparisons of IDs should always be done by the method
 * {@link #canonical()} because an object of type {@code TZID} is only
 * designed for encapsulating a canonical name. <strong>The comparison
 * using the method {@code equals()} is not allowed. </strong></p>
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
 * <p>Identifiziert eine Zeitzone. </p>
 *
 * <p>Meistens liegt die ID im Olson-Format &quot;{region}/{city}&quot; oder
 * als Offset-Angabe im Format &quot;UTC&#x00B1;hh:mm&quot; vor. In letzterem
 * Fall kann und sollte auch direkt ein Objekt des Typs {@code ZonalOffset}
 * in Betracht gezogen werden, insbesondere dann, wenn eine Verschiebung
 * zu einem gegebenen Zeitpunkt schon bekannt ist. </p>
 *
 * <p><strong>Provider-spezifische Schl&uuml;ssel</strong> haben den
 * {@link ZoneProvider#getName() Namen} des {@code ZoneProvider} gefolgt
 * von der Tilde &quot;~&quot; und der normalen kanonischen ID. Diese
 * Form kann verwendet werden, wenn ein registrierter benutzerdefinierter
 * {@link ZoneProvider} an Stelle des Standard-Provider f&uuml;r die
 * Suche nach den Zeitzonendaten herangezogen werden soll. Zum Beispiel
 * wird folgender Schl&uuml;ssel die Zeitzonendaten &uuml;ber das API von
 * {@code java.util.TimeZone} suchen, sogar wenn ein anderer Standard-Provider
 * eingestellt ist:
 * &quot;java.util.TimeZone~Europe/Berlin&quot; </p>
 *
 * <p>Ein (lexikalischer) Vergleich von IDs sollte immer &uuml;ber die Methode
 * {@link #canonical()} gemacht werden, weil ein {@code TZID} nur dem Zweck
 * dient, einen kanonischen Namen zu kapseln. <strong>Der Vergleich &uuml;ber
 * die Objekt-Methode {@code equals()} ist nicht erlaubt. </strong></p>
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
public interface TZID {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Represents the full canonical name of a timezone (for
     * example &quot;Europe/Paris&quot; or &quot;UTC+01:00&quot;). </p>
     *
     * @return  String in TZDB format (Olson-ID) or in canonical offset format
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den vollst&auml;ndigen kanonischen Namen
     * einer Zeitzone (zum Beispiel &quot;Europe/Paris&quot; oder
     * &quot;UTC+01:00&quot;). </p>
     *
     * @return  String in TZDB format (Olson-ID) or in canonical offset format
     */
    String canonical();

}
