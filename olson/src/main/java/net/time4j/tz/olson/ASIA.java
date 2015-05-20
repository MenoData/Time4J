/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ASIA.java) is part of project Time4J.
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
 * <p>Contains all standard timezone IDs in Asia. </p>
 */
/*[deutsch]
 * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Asien. </p>
 */
public enum ASIA
    implements StdZoneIdentifier {

    //~ Statische Felder/Initialisierungen --------------------------------

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

    //~ Instanzvariablen --------------------------------------------------

    private final String id;
    private final String city;
    private final String country;

    //~ Konstruktoren -----------------------------------------------------

    private ASIA(
        String city,
        String country
    ) {

        this.id = "Asia/" + city;
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

        return "Asia";

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
