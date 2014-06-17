/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
 * <p>Identifies a timezone. </p>
 *
 * <p>In most cases, the timezone ID has the Olson-format 
 * &quot;{region}/{city}&quot; or is an offset in the format
 * &quot;UTC&#x00B1;hh:mm&quot;. In latter case applications can
 * instead directly use in instance of type {@code ZonalOffset},
 * especially if the timezone offset for a given timepoint is
 * already known. </p>
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
 * data behind. For example we have {@code TZID.ASIA.HEBRON} which exists
 * first since TZ-version 2011n. Another example is the ID
 * &quot;Europe/Tiraspol&quot; which existed for a short time and is
 * mssing in the version 2011n however. Such timezone IDs will be marked
 * as <i>deprecated</i> and labelled with a suitable alias. Purpose of
 * predefined constants is just a safe and performant access (protection
 * against typing errors). </p>
 *
 * <p>If a timezone offset is known for historical timezones before the year
 * 1970 then users should generally prefer the class {@code ZonalOffset} because
 * the timezone data associated with the enum constants are not necessarily
 * correct. </p>
 *
 * @author  Meno Hochschild
 * @spec    All implementations must be immutable, thread-safe and serializable.
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
 * gibt es {@code TZID.ASIA.HEBRON} erst seit der TZ-Version 2011n.
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
 * @author  Meno Hochschild
 * @spec    All implementations must be immutable, thread-safe and serializable.
 */
public interface TZID {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Represents the full canonical name of a timezone (for
     * example &quot;Europe/Paris&quot; or &quot;UTC+01:00&quot;). </p>
     *
     * @return  String in TZDB format (Olson-ID) or in offset format
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den vollst&auml;ndigen kanonischen Namen
     * einer Zeitzone (zum Beispiel &quot;Europe/Paris&quot; oder
     * &quot;UTC+01:00&quot;). </p>
     *
     * @return  String in TZDB format (Olson-ID) or in offset format
     */
    String canonical();

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Contains all standard timezone IDs in Africa. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Afrika. </p>
     */
    public static enum AFRICA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

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

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;
        private final String country;

        //~ Konstruktoren -------------------------------------------------

        private AFRICA(
            String city,
            String country
        ) {

            this.id = "Africa/" + city;
            this.city = city;
            this.country = country;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "Africa";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return this.country;

        }

    }

    /**
     * <p>Contains all standard timezone IDs in America. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Amerika. </p>
     */
    public static enum AMERICA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

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

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;
        private final String country;

        //~ Konstruktoren -------------------------------------------------

        private AMERICA(
            String city,
            String country
        ) {

            this.id = "America/" + city;
            this.city = city;
            this.country = country;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "America";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return this.country;

        }

        //~ Innere Klassen ------------------------------------------------

        /**
         * <p>Contains all standard timezone IDs in Argentina. </p>
         */
        /*[deutsch]
         * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Argentinien. </p>
         */
        public static enum ARGENTINA
            implements TZID {

            //~ Statische Felder/Initialisierungen ------------------------

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

            //~ Instanzvariablen ------------------------------------------

            private final String id;
            private final String city;

            //~ Konstruktoren ---------------------------------------------

            private ARGENTINA(String city) {

                this.id = "America/Argentina/" + city;
                this.city = city;

            }

            //~ Methoden --------------------------------------------------

            @Override
            public String canonical() {

                return this.id;

            }

            public String getRegion() {

                return "America/Argentina";

            }

            public String getCity() {

                return this.city;

            }

            String getCountry() {

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
            implements TZID {

            //~ Statische Felder/Initialisierungen ------------------------

            INDIANAPOLIS("Indianapolis"),
            KNOX("Knox"),
            MARENGO("Marengo"),
            PETERSBURG("Petersburg"),
            TELL_CITY("Tell_City"),
            VEVAY("Vevay"),
            VINCENNES("Vincennes"),
            WINAMAC("Winamac");

            //~ Instanzvariablen ------------------------------------------

            private final String id;
            private final String city;

            //~ Konstruktoren ---------------------------------------------

            private INDIANA(String city) {

                this.id = "America/Indiana/" + city;
                this.city = city;

            }

            //~ Methoden --------------------------------------------------

            @Override
            public String canonical() {

                return this.id;

            }

            public String getRegion() {

                return "America/Indiana";

            }

            public String getCity() {

                return this.city;

            }

            String getCountry() {

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
            implements TZID {

            //~ Statische Felder/Initialisierungen ------------------------

            LOUISVILLE("Louisville"),
            MONTICELLO("Monticello");

            //~ Instanzvariablen ------------------------------------------

            private final String id;
            private final String city;

            //~ Konstruktoren ---------------------------------------------

            private KENTUCKY(String city) {

                this.id = "America/Kentucky/" + city;
                this.city = city;

            }

            //~ Methoden --------------------------------------------------

            @Override
            public String canonical() {

                return this.id;

            }

            public String getRegion() {

                return "America/Kentucky";

            }

            public String getCity() {

                return this.city;

            }

            String getCountry() {

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
            implements TZID {

            //~ Statische Felder/Initialisierungen ------------------------

            BEULAH("Beulah"),
            CENTER("Center"),
            NEW_SALEM("New_Salem");

            //~ Instanzvariablen ------------------------------------------

            private final String id;
            private final String city;

            //~ Konstruktoren ---------------------------------------------

            private NORTH_DAKOTA(String city) {

                this.id = "America/North_Dakota/" + city;
                this.city = city;

            }

            //~ Methoden --------------------------------------------------

            @Override
            public String canonical() {

                return this.id;

            }

            public String getRegion() {

                return "America/North_Dakota";

            }

            public String getCity() {

                return this.city;

            }

            String getCountry() {

                return "US";

            }

        }

    }

    /**
     * <p>Contains all standard timezone IDs in Antarctica. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in der Antarktis. </p>
     */
    public static enum ANTARCTICA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        CASEY("Casey", "AQ"),
        DAVIS("Davis", "AQ"),
        DUMONTDURVILLE("DumontDUrville", "AQ"),
        MACQUARIE("Macquarie", "AU"),
        MAWSON("Mawson", "AQ"),
        MCMURDO("McMurdo", "AQ"),
        PALMER("Palmer", "AQ"),
        ROTHERA("Rothera", "AQ"),
        SYOWA("Syowa", "AQ"),
        VOSTOK("Vostok", "AQ");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;
        private final String country;

        //~ Konstruktoren -------------------------------------------------

        private ANTARCTICA(
            String city,
            String country
        ) {

            this.id = "Antarctica/" + city;
            this.city = city;
            this.country = country;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "Antarctica";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return this.country;

        }

    }

    /**
     * <p>Contains all standard timezone IDs in Asia. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Asien. </p>
     */
    public static enum ASIA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        ADEN("Aden", "YE"),
        ALMATY("Almaty", "KZ"),
        AMMAN("Amman", "JO"),
        ANADYR("Anadyr", "RU"),
        AQTAU("Aqtau", "KZ"),
        AQTOBE("Aqtobe", "KZ"),
        ASHGABAT("Ashgabat", "TM"),
        BAGHDAD("Baghdad", "IQ"),
        BAHRAIN("Bahrain", "BH"),
        BAKU("Baku", "AZ"),
        BANGKOK("Bangkok", "TH"),
        BEIRUT("Beirut", "LB"),
        BISHKEK("Bishkek", "KG"),
        BRUNEI("Brunei", "BN"),
        CHOIBALSAN("Choibalsan", "MN"),
        CHONGQING("Chongqing", "CN"),
        COLOMBO("Colombo", "LK"),
        DAMASCUS("Damascus", "SY"),
        DHAKA("Dhaka", "BD"),
        DILI("Dili", "TL"),
        DUBAI("Dubai", "AE"),
        DUSHANBE("Dushanbe", "TJ"),
        GAZA("Gaza", "PS"),
        HARBIN("Harbin", "CN"),
        HEBRON("Hebron", "PS"),
        HO_CHI_MINH("Ho_Chi_Minh", "VN"),
        HONG_KONG("Hong_Kong", "HK"),
        HOVD("Hovd", "MN"),
        IRKUTSK("Irkutsk", "RU"),
        JAKARTA("Jakarta", "ID"),
        JAYAPURA("Jayapura", "ID"),
        JERUSALEM("Jerusalem", "IL"),
        KABUL("Kabul", "AF"),
        KAMCHATKA("Kamchatka", "RU"),
        KARACHI("Karachi", "PK"),
        KASHGAR("Kashgar", "CN"),
        KATHMANDU("Kathmandu", "NP"),
        KHANDYGA("Khandyga", "RU"),
        KOLKATA("Kolkata", "IN"),
        KRASNOYARSK("Krasnoyarsk", "RU"),
        KUALA_LUMPUR("Kuala_Lumpur", "MY"),
        KUCHING("Kuching", "MY"),
        KUWAIT("Kuwait", "KW"),
        MACAU("Macau", "MO"),
        MAGADAN("Magadan", "RU"),
        MAKASSAR("Makassar", "ID"),
        MANILA("Manila", "PH"),
        MUSCAT("Muscat", "OM"),
        NICOSIA("Nicosia", "CY"),
        NOVOKUZNETSK("Novokuznetsk", "RU"),
        NOVOSIBIRSK("Novosibirsk", "RU"),
        OMSK("Omsk", "RU"),
        ORAL("Oral", "KZ"),
        PHNOM_PENH("Phnom_Penh", "KH"),
        PONTIANAK("Pontianak", "ID"),
        PYONGYANG("Pyongyang", "KP"),
        QATAR("Qatar", "QA"),
        QYZYLORDA("Qyzylorda", "KZ"),
        RANGOON("Rangoon", "MM"),
        RIYADH("Riyadh", "SA"),
        SAKHALIN("Sakhalin", "RU"),
        SAMARKAND("Samarkand", "UZ"),
        SEOUL("Seoul", "KR"),
        SHANGHAI("Shanghai", "CN"),
        SINGAPORE("Singapore", "SG"),
        TAIPEI("Taipei", "TW"),
        TASHKENT("Tashkent", "UZ"),
        TBILISI("Tbilisi", "GE"),
        TEHRAN("Tehran", "IR"),
        THIMPHU("Thimphu", "BT"),
        TOKYO("Tokyo", "JP"),
        ULAANBAATAR("Ulaanbaatar", "MN"),
        URUMQI("Urumqi", "CN"),
        UST_NERA("Ust-Nera", "RU"),
        VIENTIANE("Vientiane", "LA"),
        VLADIVOSTOK("Vladivostok", "RU"),
        YAKUTSK("Yakutsk", "RU"),
        YEKATERINBURG("Yekaterinburg", "RU"),
        YEREVAN("Yerevan", "AM");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;
        private final String country;

        //~ Konstruktoren -------------------------------------------------

        private ASIA(
            String city,
            String country
        ) {

            this.id = "Asia/" + city;
            this.city = city;
            this.country = country;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "Asia";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return this.country;

        }

    }

    /**
     * <p>Contains all standard timezone IDs in Atlantic Ocean. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs im Atlantischen Ozean. </p>
     */
    public static enum ATLANTIC
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        AZORES("Azores", "PT"),
        BERMUDA("Bermuda", "BM"),
        CANARY("Canary", "ES"),
        CAPE_VERDE("Cape_Verde", "CV"),
        FAROE("Faroe", "FO"),
        MADEIRA("Madeira", "PT"),
        REYKJAVIK("Reykjavik", "IS"),
        SOUTH_GEORGIA("South_Georgia", "GS"),
        ST_HELENA("St_Helena", "SH"),
        STANLEY("Stanley", "FK");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;
        private final String country;

        //~ Konstruktoren -------------------------------------------------

        private ATLANTIC(
            String city,
            String country
        ) {

            this.id = "Atlantic/" + city;
            this.city = city;
            this.country = country;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "Atlantic";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return this.country;

        }

    }

    /**
     * <p>Contains all standard timezone IDs in Australia. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Australien. </p>
     */
    public static enum AUSTRALIA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        ADELAIDE("Adelaide"),
        BRISBANE("Brisbane"),
        BROKEN_HILL("Broken_Hill"),
        CURRIE("Currie"),
        DARWIN("Darwin"),
        EUCLA("Eucla"),
        HOBART("Hobart"),
        LINDEMAN("Lindeman"),
        LORD_HOWE("Lord_Howe"),
        MELBOURNE("Melbourne"),
        PERTH("Perth"),
        SYDNEY("Sydney");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private AUSTRALIA(String city) {

            this.id = "Australia/" + city;
            this.city = city;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "Australia";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return "AU";

        }

    }

    /**
     * <p>Contains all standard timezone IDs in Europe. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Europa. </p>
     */
    public static enum EUROPE
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

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

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;
        private final String country;

        //~ Konstruktoren -------------------------------------------------

        private EUROPE(
            String city,
            String country
        ) {

            this.id = "Europe/" + city;
            this.city = city;
            this.country = country;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "Europe";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return this.country;

        }

    }

    /**
     * <p>Contains all standard timezone IDs in Indian Ocean. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs im Indischen Ozean. </p>
     */
    public static enum INDIAN
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        ANTANANARIVO("Antananarivo", "MG"),
        CHAGOS("Chagos", "IO"),
        CHRISTMAS("Christmas", "CX"),
        COCOS("Cocos", "CC"),
        COMORO("Comoro", "KM"),
        KERGUELEN("Kerguelen", "TF"),
        MAHE("Mahe", "SC"),
        MALDIVES("Maldives", "MV"),
        MAURITIUS("Mauritius", "MU"),
        MAYOTTE("Mayotte", "YT"),
        REUNION("Reunion", "RE");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;
        private final String country;

        //~ Konstruktoren -------------------------------------------------

        private INDIAN(
            String city,
            String country
        ) {

            this.id = "Indian/" + city;
            this.city = city;
            this.country = country;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "Indian";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return this.country;

        }

    }

    /**
     * <p>Contains all standard timezone IDs in Pacific Ocean. </p>
     */
    /*[deutsch]
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs im Pazifischen Ozean. </p>
     */
    public static enum PACIFIC
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

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

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;
        private final String country;

        //~ Konstruktoren -------------------------------------------------

        private PACIFIC(
            String city,
            String country
        ) {

            this.id = "Pacific/" + city;
            this.city = city;
            this.country = country;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.id;

        }

        public String getRegion() {

            return "Pacific";

        }

        public String getCity() {

            return this.city;

        }

        String getCountry() {

            return this.country;

        }

    }

}
