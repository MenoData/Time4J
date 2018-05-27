/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EUROPE.java) is part of project Time4J.
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
 * <p>Contains all standard timezone IDs in Europe. </p>
 */
/*[deutsch]
 * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Europa. </p>
 */
public enum EUROPE
    implements StdZoneIdentifier {

    //~ Statische Felder/Initialisierungen --------------------------------

    AMSTERDAM("Amsterdam", "NL"),
    ANDORRA("Andorra", "AD"),
    ATHENS("Athens", "GR"),
    BELGRADE("Belgrade", "RS"),
    BERLIN("Berlin", "DE"),
    BRATISLAVA("Bratislava", "SK"), // link to Europe/Prague
    BRUSSELS("Brussels", "BE"),
    BUCHAREST("Bucharest", "RO"),
    BUDAPEST("Budapest", "HU"),
    CHISINAU("Chisinau", "MD"),
    COPENHAGEN("Copenhagen", "DK"),
    DUBLIN("Dublin", "IE"),
    GIBRALTAR("Gibraltar", "GI"),
    GUERNSEY("Guernsey", "GG"), // link to Europe/London
    HELSINKI("Helsinki", "FI"),
    ISLE_OF_MAN("Isle_of_Man", "IM"), // link to Europe/London
    ISTANBUL("Istanbul", "TR"),
    JERSEY("Jersey", "JE"), // link to Europe/London
    KALININGRAD("Kaliningrad", "RU"),
    KIEV("Kiev", "UA"),
    LISBON("Lisbon", "PT"),
    LJUBLJANA("Ljubljana", "SI"), // link to Europe/Belgrade
    LONDON("London", "GB"),
    LUXEMBOURG("Luxembourg", "LU"),
    MADRID("Madrid", "ES"),
    MALTA("Malta", "MT"),
    MARIEHAMN("Mariehamn", "AX"), // link to Europe/Helsinki
    MINSK("Minsk", "BY"),
    MONACO("Monaco", "MC"),
    MOSCOW("Moscow", "RU"),
    OSLO("Oslo", "NO"),
    PARIS("Paris", "FR"),
    PODGORICA("Podgorica", "ME"), // link to Europe/Belgrade
    PRAGUE("Prague", "CZ"),
    RIGA("Riga", "LV"),
    ROME("Rome", "IT"),
    SAMARA("Samara", "RU"),
    SAN_MARINO("San_Marino", "SM"), // link to Europe/Rome
    SARAJEVO("Sarajevo", "BA"), // link to Europe/Belgrade
    SIMFEROPOL("Simferopol", "UA"),
    SOFIA("Sofia", "BG"),
    SKOPJE("Skopje", "MK"), // link to Europe/Belgrade
    STOCKHOLM("Stockholm", "SE"),
    TALLINN("Tallinn", "EE"),
    TIRANE("Tirane", "AL"),
    UZHGOROD("Uzhgorod", "UA"),
    VADUZ("Vaduz", "LI"),
    VATICAN("Vatican", "VA"), // link to Europe/Rome
    VIENNA("Vienna", "AT"),
    VILNIUS("Vilnius", "LT"),
    VOLGOGRAD("Volgograd", "RU"),
    WARSAW("Warsaw", "PL"),
    ZAGREB("Zagreb", "HR"), // link to Europe/Belgrade
    ZAPOROZHYE("Zaporozhye", "UA"),
    ZURICH("Zurich", "CH");

    //~ Instanzvariablen --------------------------------------------------

    private final String id;
    private final String city;
    private final String country;

    //~ Konstruktoren -----------------------------------------------------

    private EUROPE(
        String city,
        String country
    ) {

        this.id = "Europe/" + city;
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

        return "Europe";

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
