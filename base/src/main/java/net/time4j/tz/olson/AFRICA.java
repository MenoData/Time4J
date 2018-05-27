/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AFRICA.java) is part of project Time4J.
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
 * <p>Contains all standard timezone IDs in Africa. </p>
 */
/*[deutsch]
 * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Afrika. </p>
 */
public enum AFRICA
    implements StdZoneIdentifier {

    //~ Statische Felder/Initialisierungen --------------------------------

    ABIDJAN("Abidjan", "CI"),
    ACCRA("Accra", "GH"),
    ADDIS_ABABA("Addis_Ababa", "ET"),
    ALGIERS("Algiers", "DZ"),
    ASMARA("Asmara", "ER"),
    BAMAKO("Bamako", "ML"),
    BANGUI("Bangui", "CF"),
    BANJUL("Banjul", "GM"),
    BISSAU("Bissau", "GW"),
    BLANTYRE("Blantyre", "MW"),
    BRAZZAVILLE("Brazzaville", "CG"),
    BUJUMBURA("Bujumbura", "BI"),
    CAIRO("Cairo", "EG"),
    CASABLANCA("Casablanca", "MA"),
    CEUTA("Ceuta", "ES"),
    CONAKRY("Conakry", "GN"),
    DAKAR("Dakar", "SN"),
    DAR_ES_SALAAM("Dar_es_Salaam", "TZ"),
    DJIBOUTI("Djibouti", "DJ"),
    DOUALA("Douala", "CM"),
    EL_AAIUN("El_Aaiun", "EH"),
    FREETOWN("Freetown", "SL"),
    GABORONE("Gaborone", "BW"),
    HARARE("Harare", "ZW"),
    JOHANNESBURG("Johannesburg", "ZA"),
    JUBA("Juba", "SS"),
    KAMPALA("Kampala", "UG"),
    KHARTOUM("Khartoum", "SD"),
    KIGALI("Kigali", "RW"),
    KINSHASA("Kinshasa", "CD"),
    LAGOS("Lagos", "NG"),
    LIBREVILLE("Libreville", "GA"),
    LOME("Lome", "TG"),
    LUANDA("Luanda", "AO"),
    LUBUMBASHI("Lubumbashi", "CD"),
    LUSAKA("Lusaka", "ZM"),
    MALABO("Malabo", "GQ"),
    MAPUTO("Maputo", "MZ"),
    MASERU("Maseru", "LS"),
    MBABANE("Mbabane", "SZ"),
    MOGADISHU("Mogadishu", "SO"),
    MONROVIA("Monrovia", "LR"),
    NAIROBI("Nairobi", "KE"),
    NDJAMENA("Ndjamena", "TD"),
    NIAMEY("Niamey", "NE"),
    NOUAKCHOTT("Nouakchott", "MR"),
    OUAGADOUGOU("Ouagadougou", "BF"),
    PORTO_NOVO("Porto-Novo", "BJ"),
    SAO_TOME("Sao_Tome", "ST"),
    TRIPOLI("Tripoli", "LY"),
    TUNIS("Tunis", "TN"),
    WINDHOEK("Windhoek", "NA");

    //~ Instanzvariablen --------------------------------------------------

    private final String id;
    private final String city;
    private final String country;

    //~ Konstruktoren -----------------------------------------------------

    private AFRICA(
        String city,
        String country
    ) {

        this.id = "Africa/" + city;
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

        return "Africa";

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
