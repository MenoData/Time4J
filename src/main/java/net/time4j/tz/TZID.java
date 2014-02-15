/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TZID.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
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
 * <p>Identifiziert eine Zeitzone. </p>
 *
 * <p>Meistens liegt die ID im Olson-Format &quot;{region}/{city}&quot; oder
 * als Offset-Angabe im Format &quot;UTC&#x00B1;hh:mm&quot; vor. In letzterem
 * Fall kann und sollte auch direkt ein Objekt des Typs {@code ZonalOffset}
 * in Betracht gezogen werden, insbesondere dann, wenn eine Verschiebung
 * zu einem gegebenen Zeitpunkt schon bekannt ist. </p>
 *
 * Ein (lexikalischer) Vergleich von IDs ist NICHT via {@code equals()},
 * sondern nur &uuml;ber die Methode {@link #canonical()} statthaft, weil
 * ein {@code TZID} lediglich dem Zweck dient, einen kanonischen Namen zu
 * kapseln. </p>
 *
 * <p>Die vordefinierten Enum-Konstanten spiegeln aktuell die TZ-Version
 * <span style="text-decoration:underline;"><tt>2011n</tt></span> wider
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
     * <p>Repr&auml;sentiert den vollst&auml;ndigen kanonischen Namen
     * einer Zeitzone (zum Beispiel &quot;Europe/Paris&quot; oder
     * &quot;UTC+01:00&quot;). </p>
     *
     * @return  String in TZDB format (Olson-ID) or in offset format
     */
    String canonical();

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Afrika. </p>
     */
    public static enum AFRICA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        ABIDJAN("Abidjan"),
        ACCRA("Accra"),
        ADDIS_ABABA("Addis_Ababa"),
        ALGIERS("Algiers"),
        ASMARA("Asmara"),
        BAMAKO("Bamako"),
        BANGUI("Bangui"),
        BANJUL("Banjul"),
        BISSAU("Bissau"),
        BLANTYRE("Blantyre"),
        BRAZZAVILLE("Brazzaville"),
        BUJUMBURA("Bujumbura"),
        CAIRO("Cairo"),
        CASABLANCA("Casablanca"),
        CEUTA("Ceuta"),
        CONAKRY("Conakry"),
        DAKAR("Dakar"),
        DAR_ES_SALAAM("Dar_es_Salaam"),
        DJIBOUTI("Djibouti"),
        DOUALA("Douala"),
        EL_AAIUN("El_Aaiun"),
        FREETOWN("Freetown"),
        GABORONE("Gaborone"),
        HARARE("Harare"),
        JOHANNESBURG("Johannesburg"),
        JUBA("Juba"),
        KAMPALA("Kampala"),
        KHARTOUM("Khartoum"),
        KIGALI("Kigali"),
        KINSHASA("Kinshasa"),
        LAGOS("Lagos"),
        LIBREVILLE("Libreville"),
        LOME("Lome"),
        LUANDA("Luanda"),
        LUBUMBASHI("Lubumbashi"),
        LUSAKA("Lusaka"),
        MALABO("Malabo"),
        MAPUTO("Maputo"),
        MASERU("Maseru"),
        MBABANE("Mbabane"),
        MOGADISHU("Mogadishu"),
        MONROVIA("Monrovia"),
        NAIROBI("Nairobi"),
        NDJAMENA("Ndjamena"),
        NIAMEY("Niamey"),
        NOUAKCHOTT("Nouakchott"),
        OUAGADOUGOU("Ouagadougou"),
        PORTO_NOVO("Porto-Novo"),
        SAO_TOME("Sao_Tome"),
        TRIPOLI("Tripoli"),
        TUNIS("Tunis"),
        WINDHOEK("Windhoek");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private AFRICA(String city) {

            this.id = "Africa/" + city;
            this.city = city;

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

    }

    /**
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Amerika. </p>
     */
    public static enum AMERICA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        ADAK("Adak"),
        ANCHORAGE("Anchorage"),
        ANGUILLA("Anguilla"),
        ANTIGUA("Antigua"),
        ARAGUAINA("Araguaina"),
        ARUBA("Aruba"),
        ASUNCION("Asuncion"),
        ATIKOKAN("Atikokan"),
        BAHIA("Bahia"),
        BAHIA_BANDERAS("Bahia_Banderas"),
        BARBADOS("Barbados"),
        BELEM("Belem"),
        BELIZE("Belize"),
        BLANC_SABLON("Blanc-Sablon"),
        BOA_VISTA("Boa_Vista"),
        BOGOTA("Bogota"),
        BOISE("Boise"),
        CAMBRIDGE_BAY("Cambridge_Bay"),
        CAMPO_GRANDE("Campo_Grande"),
        CANCUN("Cancun"),
        CARACAS("Caracas"),
        CAYENNE("Cayenne"),
        CAYMAN("Cayman"),
        CHICAGO("Chicago"),
        CHIHUAHUA("Chihuahua"),
        COSTA_RICA("Costa_Rica"),
        CUIABA("Cuiaba"),
        CURACAO("Curacao"),
        DANMARKSHAVN("Danmarkshavn"),
        DAWSON("Dawson"),
        DAWSON_CREEK("Dawson_Creek"),
        DENVER("Denver"),
        DETROIT("Detroit"),
        DOMINICA("Dominica"),
        EDMONTON("Edmonton"),
        EIRUNEPE("Eirunepe"),
        EL_SALVADOR("El_Salvador"),
        FORTALEZA("Fortaleza"),
        GLACE_BAY("Glace_Bay"),
        GODTHAB("Godthab"),
        GOOSE_BAY("Goose_Bay"),
        GRAND_TURK("Grand_Turk"),
        GRENADA("Grenada"),
        GUADELOUPE("Guadeloupe"),
        GUATEMALA("Guatemala"),
        GUAYAQUIL("Guayaquil"),
        GUYANA("Guyana"),
        HALIFAX("Halifax"),
        HAVANA("Havana"),
        HERMOSILLO("Hermosillo"),
        INUVIK("Inuvik"),
        IQALUIT("Iqaluit"),
        JAMAICA("Jamaica"),
        JUNEAU("Juneau"),
        LA_PAZ("La_Paz"),
        LIMA("Lima"),
        LOS_ANGELES("Los_Angeles"),
        MACEIO("Maceio"),
        MANAGUA("Managua"),
        MANAUS("Manaus"),
        MARTINIQUE("Martinique"),
        MATAMOROS("Matamoros"),
        MAZATLAN("Mazatlan"),
        MENOMINEE("Menominee"),
        MERIDA("Merida"),
        METLAKATLA("Metlakatla"),
        MEXICO_CITY("Mexico_City"),
        MIQUELON("Miquelon"),
        MONCTON("Moncton"),
        MONTERREY("Monterrey"),
        MONTEVIDEO("Montevideo"),
        MONTREAL("Montreal"),
        MONTSERRAT("Montserrat"),
        NASSAU("Nassau"),
        NEW_YORK("New_York"),
        NIPIGON("Nipigon"),
        NOME("Nome"),
        NORONHA("Noronha"),
        OJINAGA("Ojinaga"),
        PANAMA("Panama"),
        PANGNIRTUNG("Pangnirtung"),
        PARAMARIBO("Paramaribo"),
        PHOENIX("Phoenix"),
        PORT_OF_SPAIN("Port_of_Spain"),
        PORT_AU_PRINCE("Port-au-Prince"),
        PORTO_VELHO("Porto_Velho"),
        PUERTO_RICO("Puerto_Rico"),
        RAINY_RIVER("Rainy_River"),
        RANKIN_INLET("Rankin_Inlet"),
        RECIFE("Recife"),
        REGINA("Regina"),
        RESOLUTE("Resolute"),
        RIO_BRANCO("Rio_Branco"),
        SANTA_ISABEL("Santa_Isabel"),
        SANTAREM("Santarem"),
        SANTIAGO("Santiago"),
        SANTO_DOMINGO("Santo_Domingo"),
        SAO_PAULO("Sao_Paulo"),
        SCORESBYSUND("Scoresbysund"),
        SITKA("Sitka"),
        ST_JOHNS("St_Johns"),
        ST_KITTS("St_Kitts"),
        ST_LUCIA("St_Lucia"),
        ST_THOMAS("St_Thomas"),
        ST_VINCENT("St_Vincent"),
        SWIFT_CURRENT("Swift_Current"),
        TEGUCIGALPA("Tegucigalpa"),
        THULE("Thule"),
        THUNDER_BAY("Thunder_Bay"),
        TIJUANA("Tijuana"),
        TORONTO("Toronto"),
        TORTOLA("Tortola"),
        VANCOUVER("Vancouver"),
        WHITEHORSE("Whitehorse"),
        WINNIPEG("Winnipeg"),
        YAKUTAT("Yakutat"),
        YELLOWKNIFE("Yellowknife");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private AMERICA(String city) {

            this.id = "America/" + city;
            this.city = city;

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

        //~ Innere Klassen ------------------------------------------------

        /**
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

        }

        /**
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

        }

        /**
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

        }

        /**
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

        }

    }

    /**
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in der Antarktis. </p>
     */
    public static enum ANTARCTICA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        CASEY("Casey"),
        DAVIS("Davis"),
        DUMONTDURVILLE("DumontDUrville"),
        MACQUARIE("Macquarie"),
        MAWSON("Mawson"),
        MCMURDO("McMurdo"),
        PALMER("Palmer"),
        ROTHERA("Rothera"),
        SYOWA("Syowa"),
        VOSTOK("Vostok");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private ANTARCTICA(String city) {

            this.id = "Antarctica/" + city;
            this.city = city;

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

    }

    /**
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Asien. </p>
     */
    public static enum ASIA
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        ADEN("Aden"),
        ALMATY("Almaty"),
        AMMAN("Amman"),
        ANADYR("Anadyr"),
        AQTAU("Aqtau"),
        AQTOBE("Aqtobe"),
        ASHGABAT("Ashgabat"),
        BAGHDAD("Baghdad"),
        BAHRAIN("Bahrain"),
        BAKU("Baku"),
        BANGKOK("Bangkok"),
        BEIRUT("Beirut"),
        BISHKEK("Bishkek"),
        BRUNEI("Brunei"),
        CHOIBALSAN("Choibalsan"),
        CHONGQING("Chongqing"),
        COLOMBO("Colombo"),
        DAMASCUS("Damascus"),
        DHAKA("Dhaka"),
        DILI("Dili"),
        DUBAI("Dubai"),
        DUSHANBE("Dushanbe"),
        GAZA("Gaza"),
        HARBIN("Harbin"),
        HEBRON("Hebron"),
        HO_CHI_MINH("Ho_Chi_Minh"),
        HONG_KONG("Hong_Kong"),
        HOVD("Hovd"),
        IRKUTSK("Irkutsk"),
        JAKARTA("Jakarta"),
        JAYAPURA("Jayapura"),
        JERUSALEM("Jerusalem"),
        KABUL("Kabul"),
        KAMCHATKA("Kamchatka"),
        KARACHI("Karachi"),
        KASHGAR("Kashgar"),
        KATHMANDU("Kathmandu"),
        KOLKATA("Kolkata"),
        KRASNOYARSK("Krasnoyarsk"),
        KUALA_LUMPUR("Kuala_Lumpur"),
        KUCHING("Kuching"),
        KUWAIT("Kuwait"),
        MACAU("Macau"),
        MAGADAN("Magadan"),
        MAKASSAR("Makassar"),
        MANILA("Manila"),
        MUSCAT("Muscat"),
        NICOSIA("Nicosia"),
        NOVOKUZNETSK("Novokuznetsk"),
        NOVOSIBIRSK("Novosibirsk"),
        OMSK("Omsk"),
        ORAL("Oral"),
        PHNOM_PENH("Phnom_Penh"),
        PONTIANAK("Pontianak"),
        PYONGYANG("Pyongyang"),
        QATAR("Qatar"),
        QYZYLORDA("Qyzylorda"),
        RANGOON("Rangoon"),
        RIYADH("Riyadh"),
        RIYADH87("Riyadh87"),
        RIYADH88("Riyadh88"),
        RIYADH89("Riyadh89"),
        SAKHALIN("Sakhalin"),
        SAMARKAND("Samarkand"),
        SEOUL("Seoul"),
        SHANGHAI("Shanghai"),
        SINGAPORE("Singapore"),
        TAIPEI("Taipei"),
        TASHKENT("Tashkent"),
        TBILISI("Tbilisi"),
        TEHRAN("Tehran"),
        THIMPHU("Thimphu"),
        TOKYO("Tokyo"),
        ULAANBAATAR("Ulaanbaatar"),
        URUMQI("Urumqi"),
        VIENTIANE("Vientiane"),
        VLADIVOSTOK("Vladivostok"),
        YAKUTSK("Yakutsk"),
        YEKATERINBURG("Yekaterinburg"),
        YEREVAN("Yerevan");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private ASIA(String city) {

            this.id = "Asia/" + city;
            this.city = city;

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

    }

    /**
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs im Atlantischen Ozean. </p>
     */
    public static enum ATLANTIC
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        AZORES("Azores"),
        BERMUDA("Bermuda"),
        CANARY("Canary"),
        CAPE_VERDE("Cape_Verde"),
        FAROE("Faroe"),
        MADEIRA("Madeira"),
        REYKJAVIK("Reykjavik"),
        SOUTH_GEORGIA("South_Georgia"),
        ST_HELENA("St_Helena"),
        STANLEY("Stanley");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private ATLANTIC(String city) {

            this.id = "Atlantic/" + city;
            this.city = city;

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

    }

    /**
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

    }

    /**
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Europa. </p>
     */
    public static enum EUROPE
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        AMSTERDAM("Amsterdam"),
        ANDORRA("Andorra"),
        ATHENS("Athens"),
        BELGRADE("Belgrade"),
        BERLIN("Berlin"),
        BRUSSELS("Brussels"),
        BUCHAREST("Bucharest"),
        BUDAPEST("Budapest"),
        CHISINAU("Chisinau"),
        COPENHAGEN("Copenhagen"),
        DUBLIN("Dublin"),
        GIBRALTAR("Gibraltar"),
        HELSINKI("Helsinki"),
        ISTANBUL("Istanbul"),
        KALININGRAD("Kaliningrad"),
        KIEV("Kiev"),
        LISBON("Lisbon"),
        LONDON("London"),
        LUXEMBOURG("Luxembourg"),
        MADRID("Madrid"),
        MALTA("Malta"),
        MINSK("Minsk"),
        MONACO("Monaco"),
        MOSCOW("Moscow"),
        OSLO("Oslo"),
        PARIS("Paris"),
        PRAGUE("Prague"),
        RIGA("Riga"),
        ROME("Rome"),
        SAMARA("Samara"),
        SIMFEROPOL("Simferopol"),
        SOFIA("Sofia"),
        STOCKHOLM("Stockholm"),
        TALLINN("Tallinn"),
        TIRANE("Tirane"),
        UZHGOROD("Uzhgorod"),
        VADUZ("Vaduz"),
        VIENNA("Vienna"),
        VILNIUS("Vilnius"),
        VOLGOGRAD("Volgograd"),
        WARSAW("Warsaw"),
        ZAPOROZHYE("Zaporozhye"),
        ZURICH("Zurich");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private EUROPE(String city) {

            this.id = "Europe/" + city;
            this.city = city;

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

    }

    /**
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs im Indischen Ozean. </p>
     */
    public static enum INDIAN
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        ANTANANARIVO("Antananarivo"),
        CHAGOS("Chagos"),
        CHRISTMAS("Christmas"),
        COCOS("Cocos"),
        COMORO("Comoro"),
        KERGUELEN("Kerguelen"),
        MAHE("Mahe"),
        MALDIVES("Maldives"),
        MAURITIUS("Mauritius"),
        MAYOTTE("Mayotte"),
        REUNION("Reunion");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private INDIAN(String city) {

            this.id = "Indian/" + city;
            this.city = city;

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

    }

    /**
     * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs im Pazifischen Ozean. </p>
     */
    public static enum PACIFIC
        implements TZID {

        //~ Statische Felder/Initialisierungen ----------------------------

        APIA("Apia"),
        AUCKLAND("Auckland"),
        CHATHAM("Chatham"),
        CHUUK("Chuuk"),
        EASTER("Easter"),
        EFATE("Efate"),
        ENDERBURY("Enderbury"),
        FAKAOFO("Fakaofo"),
        FIJI("Fiji"),
        FUNAFUTI("Funafuti"),
        GALAPAGOS("Galapagos"),
        GAMBIER("Gambier"),
        GUADALCANAL("Guadalcanal"),
        GUAM("Guam"),
        HONOLULU("Honolulu"),
        JOHNSTON("Johnston"),
        KIRITIMATI("Kiritimati"),
        KOSRAE("Kosrae"),
        KWAJALEIN("Kwajalein"),
        MAJURO("Majuro"),
        MARQUESAS("Marquesas"),
        MIDWAY("Midway"),
        NAURU("Nauru"),
        NIUE("Niue"),
        NORFOLK("Norfolk"),
        NOUMEA("Noumea"),
        PAGO_PAGO("Pago_Pago"),
        PALAU("Palau"),
        PITCAIRN("Pitcairn"),
        POHNPEI("Pohnpei"),
        PORT_MORESBY("Port_Moresby"),
        RAROTONGA("Rarotonga"),
        SAIPAN("Saipan"),
        TAHITI("Tahiti"),
        TARAWA("Tarawa"),
        TONGATAPU("Tongatapu"),
        WAKE("Wake"),
        WALLIS("Wallis");

        //~ Instanzvariablen ----------------------------------------------

        private final String id;
        private final String city;

        //~ Konstruktoren -------------------------------------------------

        private PACIFIC(String city) {

            this.id = "Pacific/" + city;
            this.city = city;

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

    }

}
