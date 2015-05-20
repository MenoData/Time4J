/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PACIFIC.java) is part of project Time4J.
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


/**
 * <p>Contains all standard timezone IDs in Pacific Ocean. </p>
 */
/*[deutsch]
 * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs im Pazifischen Ozean. </p>
 */
public enum PACIFIC
    implements StdZoneIdentifier {

    //~ Statische Felder/Initialisierungen --------------------------------

    APIA("Apia", "WS"),
    AUCKLAND("Auckland", "NZ"),
    CHATHAM("Chatham", "NZ"),
    CHUUK("Chuuk", "FM"),
    EASTER("Easter", "CL"),
    EFATE("Efate", "VU"),
    ENDERBURY("Enderbury", "KI"),
    FAKAOFO("Fakaofo", "TK"),
    FIJI("Fiji", "FJ"),
    FUNAFUTI("Funafuti", "TV"),
    GALAPAGOS("Galapagos", "EC"),
    GAMBIER("Gambier", "PF"),
    GUADALCANAL("Guadalcanal", "SB"),
    GUAM("Guam", "GU"),
    HONOLULU("Honolulu", "US"),
    JOHNSTON("Johnston", "UM"),
    KIRITIMATI("Kiritimati", "KI"),
    KOSRAE("Kosrae", "FM"),
    KWAJALEIN("Kwajalein", "MH"),
    MAJURO("Majuro", "MH"),
    MARQUESAS("Marquesas", "PF"),
    MIDWAY("Midway", "UM"),
    NAURU("Nauru", "NR"),
    NIUE("Niue", "NU"),
    NORFOLK("Norfolk", "NF"),
    NOUMEA("Noumea", "NC"),
    PAGO_PAGO("Pago_Pago", "AS"),
    PALAU("Palau", "PW"),
    PITCAIRN("Pitcairn", "PN"),
    POHNPEI("Pohnpei", "FM"),
    PORT_MORESBY("Port_Moresby", "PG"),
    RAROTONGA("Rarotonga", "CK"),
    SAIPAN("Saipan", "MP"),
    TAHITI("Tahiti", "PF"),
    TARAWA("Tarawa", "KI"),
    TONGATAPU("Tongatapu", "TO"),
    WAKE("Wake", "UM"),
    WALLIS("Wallis", "WF");

    //~ Instanzvariablen --------------------------------------------------

    private final String id;
    private final String city;
    private final String country;

    //~ Konstruktoren -----------------------------------------------------

    private PACIFIC(
        String city,
        String country
    ) {

        this.id = "Pacific/" + city;
        this.city = city;
        this.country = country;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String canonical() {

        return this.id;

    }

    @Override
    public String getRegion() {

        return "Pacific";

    }

    @Override
    public String getCity() {

        return this.city;

    }

    @Override
    public String getCountry() {

        return this.country;

    }

}
