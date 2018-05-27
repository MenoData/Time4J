/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AMERICA.java) is part of project Time4J.
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
 * <p>Contains all standard timezone IDs in America. </p>
 */
/*[deutsch]
 * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Amerika. </p>
 */
public enum AMERICA
    implements StdZoneIdentifier {

    //~ Statische Felder/Initialisierungen --------------------------------

    ADAK("Adak", "US"),
    ANCHORAGE("Anchorage", "US"),
    ANGUILLA("Anguilla", "AI"),
    ANTIGUA("Antigua", "AG"),
    ARAGUAINA("Araguaina", "BR"),
    ARUBA("Aruba", "AW"),
    ASUNCION("Asuncion", "PY"),
    ATIKOKAN("Atikokan", "CA"),
    BAHIA("Bahia", "BR"),
    BAHIA_BANDERAS("Bahia_Banderas", "MX"),
    BARBADOS("Barbados", "BB"),
    BELEM("Belem", "BR"),
    BELIZE("Belize", "BZ"),
    BLANC_SABLON("Blanc-Sablon", "CA"),
    BOA_VISTA("Boa_Vista", "BR"),
    BOGOTA("Bogota", "CO"),
    BOISE("Boise", "US"),
    CAMBRIDGE_BAY("Cambridge_Bay", "CA"),
    CAMPO_GRANDE("Campo_Grande", "BR"),
    CANCUN("Cancun", "MX"),
    CARACAS("Caracas", "VE"),
    CAYENNE("Cayenne", "GF"),
    CAYMAN("Cayman", "KY"),
    CHICAGO("Chicago", "US"),
    CHIHUAHUA("Chihuahua", "MX"),
    COSTA_RICA("Costa_Rica", "CR"),
    CRESTON("Creston", "CA"),
    CUIABA("Cuiaba", "BR"),
    CURACAO("Curacao", "CW"),
    DANMARKSHAVN("Danmarkshavn", "GL"),
    DAWSON("Dawson", "CA"),
    DAWSON_CREEK("Dawson_Creek", "CA"),
    DENVER("Denver", "US"),
    DETROIT("Detroit", "US"),
    DOMINICA("Dominica", "DM"),
    EDMONTON("Edmonton", "CA"),
    EIRUNEPE("Eirunepe", "BR"),
    EL_SALVADOR("El_Salvador", "SV"),
    FORTALEZA("Fortaleza", "BR"),
    GLACE_BAY("Glace_Bay", "CA"),
    GODTHAB("Godthab", "GL"),
    GOOSE_BAY("Goose_Bay", "CA"),
    GRAND_TURK("Grand_Turk", "TC"),
    GRENADA("Grenada", "GD"),
    GUADELOUPE("Guadeloupe", "GP"),
    GUATEMALA("Guatemala", "GT"),
    GUAYAQUIL("Guayaquil", "EC"),
    GUYANA("Guyana", "GY"),
    HALIFAX("Halifax", "CA"),
    HAVANA("Havana", "CU"),
    HERMOSILLO("Hermosillo", "MX"),
    INUVIK("Inuvik", "CA"),
    IQALUIT("Iqaluit", "CA"),
    JAMAICA("Jamaica", "JM"),
    JUNEAU("Juneau", "US"),
    LA_PAZ("La_Paz", "BO"),
    LIMA("Lima", "PE"),
    LOS_ANGELES("Los_Angeles", "US"),
    LOWER_PRINCES("Lower_Princes", "SX"), // link to America/Curacao
    KRALENDIJK("Kralendijk", "BQ"), // link to America/Curacao
    MACEIO("Maceio", "BR"),
    MANAGUA("Managua", "NI"),
    MANAUS("Manaus", "BR"),
    MARIGOT("Marigot", "MF"), // link to America/Port_of_Spain
    MARTINIQUE("Martinique", "MQ"),
    MATAMOROS("Matamoros", "MX"),
    MAZATLAN("Mazatlan", "MX"),
    MENOMINEE("Menominee", "US"),
    MERIDA("Merida", "MX"),
    METLAKATLA("Metlakatla", "US"),
    MEXICO_CITY("Mexico_City", "MX"),
    MIQUELON("Miquelon", "PM"),
    MONCTON("Moncton", "CA"),
    MONTERREY("Monterrey", "MX"),
    MONTEVIDEO("Montevideo", "UY"),
    MONTREAL("Montreal", "CA"),
    MONTSERRAT("Montserrat", "MS"),
    NASSAU("Nassau", "BS"),
    NEW_YORK("New_York", "US"),
    NIPIGON("Nipigon", "CA"),
    NOME("Nome", "US"),
    NORONHA("Noronha", "BR"),
    OJINAGA("Ojinaga", "MX"),
    PANAMA("Panama", "PA"),
    PANGNIRTUNG("Pangnirtung", "CA"),
    PARAMARIBO("Paramaribo", "SR"),
    PHOENIX("Phoenix", "US"),
    PORT_OF_SPAIN("Port_of_Spain", "TT"),
    PORT_AU_PRINCE("Port-au-Prince", "HT"),
    PORTO_VELHO("Porto_Velho", "BR"),
    PUERTO_RICO("Puerto_Rico", "PR"),
    RAINY_RIVER("Rainy_River", "CA"),
    RANKIN_INLET("Rankin_Inlet", "CA"),
    RECIFE("Recife", "BR"),
    REGINA("Regina", "CA"),
    RESOLUTE("Resolute", "CA"),
    RIO_BRANCO("Rio_Branco", "BR"),
    SANTA_ISABEL("Santa_Isabel", "MX"),
    SANTAREM("Santarem", "BR"),
    SANTIAGO("Santiago", "CL"),
    SANTO_DOMINGO("Santo_Domingo", "DO"),
    SAO_PAULO("Sao_Paulo", "BR"),
    SCORESBYSUND("Scoresbysund", "GL"),
    SITKA("Sitka", "US"),
    ST_BARTHELEMY("St_Barthelemy", "BL"), // link to America/Port_of_Spain
    ST_JOHNS("St_Johns", "CA"),
    ST_KITTS("St_Kitts", "KN"),
    ST_LUCIA("St_Lucia", "LC"),
    ST_THOMAS("St_Thomas", "VI"),
    ST_VINCENT("St_Vincent", "VC"),
    SWIFT_CURRENT("Swift_Current", "CA"),
    TEGUCIGALPA("Tegucigalpa", "HN"),
    THULE("Thule", "GL"),
    THUNDER_BAY("Thunder_Bay", "CA"),
    TIJUANA("Tijuana", "MX"),
    TORONTO("Toronto", "CA"),
    TORTOLA("Tortola", "VG"),
    VANCOUVER("Vancouver", "CA"),
    WHITEHORSE("Whitehorse", "CA"),
    WINNIPEG("Winnipeg", "CA"),
    YAKUTAT("Yakutat", "US"),
    YELLOWKNIFE("Yellowknife", "CA");

    //~ Instanzvariablen --------------------------------------------------

    private final String id;
    private final String city;
    private final String country;

    //~ Konstruktoren -----------------------------------------------------

    private AMERICA(
        String city,
        String country
    ) {

        this.id = "America/" + city;
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

        return "America";

    }

    @Override
    public String getCity() {

        return this.city;

    }

    @Override
    public String getCountry() {

        return this.country;

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Contains all standard timezone IDs in Argentina. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Argentinien. </p>
     */
    public static enum ARGENTINA
        implements StdZoneIdentifier {

        //~ Statische Felder/Initialisierungen ----------------------------

        BUENOS_AIRES("Buenos_Aires"),
        CATAMARCA("Catamarca"),
        CORDOBA("Cordoba"),
        JUJUY("Jujuy"),
        LA_RIOJA("La_Rioja"),
        MENDOZA("Mendoza"),
        RIO_GALLEGOS("Rio_Gallegos"),
        SALTA("Salta"),
        SAN_JUAN("San_Juan"),
        SAN_LUIS("San_Luis"),
        TUCUMAN("Tucuman"),
        USHUAIA("Ushuaia");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private ARGENTINA(String city) {

            this.id = "America/Argentina/" + city;
            this.city = city;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        @Override
        public String getRegion() {

            return "America/Argentina";

        }

        @Override
        public String getCity() {

            return this.city;

        }

        @Override
        public String getCountry() {

            return "AR";

        }

    }

    /**
     * <p>Contains all standard timezone IDs in USA/Indiana. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in USA/Indiana. </p>
     */
    public static enum INDIANA
        implements StdZoneIdentifier {

        //~ Statische Felder/Initialisierungen ----------------------------

        INDIANAPOLIS("Indianapolis"),
        KNOX("Knox"),
        MARENGO("Marengo"),
        PETERSBURG("Petersburg"),
        TELL_CITY("Tell_City"),
        VEVAY("Vevay"),
        VINCENNES("Vincennes"),
        WINAMAC("Winamac");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private INDIANA(String city) {

            this.id = "America/Indiana/" + city;
            this.city = city;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        @Override
        public String getRegion() {

            return "America/Indiana";

        }

        @Override
        public String getCity() {

            return this.city;

        }

        @Override
        public String getCountry() {

            return "US";

        }

    }

    /**
     * <p>Contains all standard timezone IDs in USA/Kentucky. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in USA/Kentucky. </p>
     */
    public static enum KENTUCKY
        implements StdZoneIdentifier {

        //~ Statische Felder/Initialisierungen ----------------------------

        LOUISVILLE("Louisville"),
        MONTICELLO("Monticello");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private KENTUCKY(String city) {

            this.id = "America/Kentucky/" + city;
            this.city = city;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        @Override
        public String getRegion() {

            return "America/Kentucky";

        }

        @Override
        public String getCity() {

            return this.city;

        }

        @Override
        public String getCountry() {

            return "US";

        }

    }

    /**
     * <p>Contains all standard timezone IDs in USA/North-Dakota. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in USA/Nord-Dakota. </p>
     */
    public static enum NORTH_DAKOTA
        implements StdZoneIdentifier {

        //~ Statische Felder/Initialisierungen ----------------------------

        BEULAH("Beulah"),
        CENTER("Center"),
        NEW_SALEM("New_Salem");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private NORTH_DAKOTA(String city) {

            this.id = "America/North_Dakota/" + city;
            this.city = city;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        @Override
        public String getRegion() {

            return "America/North_Dakota";

        }

        @Override
        public String getCity() {

            return this.city;

        }

        @Override
        public String getCountry() {

            return "US";

        }

    }

}
