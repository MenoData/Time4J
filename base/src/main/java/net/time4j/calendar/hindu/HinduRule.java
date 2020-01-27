/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HinduRule.java) is part of project Time4J.
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

package net.time4j.calendar.hindu;


/**
 * <p>The Hindu calendar variants use a set of different algorithmic rules
 * how to determine the start of solar month  in relation to the zodiacal position
 * of the sun (samkranti). </p>
 *
 * <p>The rule also affects the naming of solar months. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Die Hindukalendervarianten unterst&uuml;tzen verschiedene algorithmische Regeln,
 * wie die Tierkreisposition der Sonne (Samkranti) sich auf den Start des jeweiligen
 * Sonnenmonats auswirkt. </p>
 *
 * <p>Die Regel wirkt sich auch auf die Benennung der Sonnenmonate aus. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
public enum HinduRule {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>A solar calendar which uses the sunrise of following morning as critical time. </p>
     *
     * <p>The default era is SAKA. </p>
     */
    /*[deutsch]
     * <p>Ein Sonnenkalender, der den Sonnenaufgang des folgenden Tages verwendet. </p>
     *
     * <p>Die Standard&auml;ra ist SAKA. </p>
     */
    ORISSA() {
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.SAKA;
        }
    },

    /**
     * <p>A solar calendar which uses the sunset of current day as critical time. </p>
     *
     * <p>Mainly used in Tamil Nadu region. The default era is SAKA. </p>
     */
    /*[deutsch]
     * <p>Ein Sonnenkalender, der den Sonnenuntergang des aktuellen Tages verwendet. </p>
     *
     * <p>Haupts&auml;chlich in Tamil Nadu verwendet. Die Standard&auml;ra ist SAKA. </p>
     */
    TAMIL() {
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.SAKA;
        }
    },

    /**
     * <p>A solar calendar which uses the seasonal time of 12 minutes after 1 PM
     * which corresponds to 3/5 th of the time period between sunrise and sunset
     * in this definition. </p>
     *
     * <p>Mainly used in Kerala region. The default era is KOLLAM. </p>
     */
    /*[deutsch]
     * <p>Ein Sonnenkalender, der den Sonnenuntergang des aktuellen Tages verwendet,
     * was in dieser Definition 3/5 der Zeit zwischen Sonnenaufgang und Sonnenuntergang entspricht. </p>
     *
     * <p>Haupts&auml;chlich in Kerala verwendet. Die Standard&auml;ra ist KOLLAM. </p>
     */
    MALAYALI() {
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.KOLLAM;
        }
    },

    /**
     * <p>A solar calendar which uses midnight at end of current day as critical time. </p>
     *
     * <p>Mainly used in Madras (Chennai). The default era is SAKA. </p>
     */
    /*[deutsch]
     * <p>Ein Sonnenkalender, der Mitternacht am Ende des aktuellen Tages verwendet. </p>
     *
     * <p>Haupts&auml;chlich in Madras (Chennai) verwendet. Die Standard&auml;ra ist SAKA. </p>
     */
    MADRAS() {
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.SAKA;
        }
    },

    /**
     * <p>A solar calendar which is used in West Bengal, Assam and Tripura. </p>
     *
     * <p>We follow the details given by Vinod K. Mishra in his script &quot;The calendars of India&quot;
     * which say: When the samkranti occurs between the sunrise and the following midnight then the
     * month begins on the next day. If samkranti occurs after midnight then the month begins on the
     * day following the next day, i.e. on the third day. </p>
     */
    /*[deutsch]
     * <p>Ein Sonnenkalender, der in West Bengal, Assam and Tripura verwendet wird. </p>
     *
     * <p>Wir folgen den Details gegeben durch Vinod K. Mishra in seinem Buch &quot;The calendars of India&quot;,
     * die besagen: Wenn Samkranti zwischen Sonnenaufgang und der folgenden Mitternacht eintritt, dann f&auml;ngt
     * der Monat am n&auml;chsten Tag an. Wenn Samkranti nach Mitternacht eintritt, dann f&auml;ngt der Monat am
     * Tag nach dem n&auml;chsten Tag an, also am dritten Tag. </p>
     */
    BENGAL() { // TODO: improve javadoc (tabular data with other range!)
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.BENGAL;
        }
    },

    /**
     * <p>The amanta scheme is a lunisolar calendar based on the new moon cycle
     * and starting the year with the month Chaitra. </p>
     *
     * <p>The default era is {@link HinduEra#VIKRAMA}. Used mainly in Maharashtra, Karnataka, Kerala,
     * Tamilnadu, Andhra pradesh, Telangana, and West Bengal. Gujarat uses special Amanta-versions
     * which differs in when the year starts. </p>
     *
     * @see     #AMANTA_ASHADHA
     * @see     #AMANTA_KARTIKA
     */
    /*[deutsch]
     * <p>Das Amanta-Schema ist ein lunisolarer Kalender, der auf dem Neumondzyklus
     * basiert und das Jahr mit dem Monat Chaitra beginnt. </p>
     *
     * <p>Die Standard&auml;ra ist {@link HinduEra#VIKRAMA}. In Gebrauch haupts&auml;chlich
     * in Maharashtra, Karnataka, Kerala, Tamilnadu, Andhra pradesh, Telangana, and West Bengal.
     * Gujarat verwendet besondere Amanta-Varianten, die sich im Jahresanfang unterscheiden. </p>
     *
     * @see     #AMANTA_ASHADHA
     * @see     #AMANTA_KARTIKA
     */
    AMANTA() {
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.VIKRAMA;
        }
    },

    /**
     * <p>This special amanta scheme is a lunisolar calendar based on the new moon cycle
     * and starting the year with the month Ashadha. </p>
     *
     * <p>The default era is {@link HinduEra#VIKRAMA}. Used in some parts of Gujarat. </p>
     *
     * @see     #AMANTA_KARTIKA
     */
    /*[deutsch]
     * <p>Dieses spezielle Amanta-Schema ist ein lunisolarer Kalender, der auf dem Neumondzyklus
     * basiert und das Jahr mit dem Monat Ashadha beginnt. </p>
     *
     * <p>Die Standard&auml;ra ist {@link HinduEra#VIKRAMA}. In Gebrauch in Teilen von Gujarat. </p>
     *
     * @see     #AMANTA_KARTIKA
     */
    AMANTA_ASHADHA() {
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.VIKRAMA;
        }
    },

    /**
     * <p>This special amanta scheme is a lunisolar calendar based on the new moon cycle
     * and starting the year with the month Kartika. </p>
     *
     * <p>The default era is {@link HinduEra#VIKRAMA}. Used in Gujarat. </p>
     *
     * @see     #AMANTA_ASHADHA
     */
    /*[deutsch]
     * <p>Dieses spezielle Amanta-Schema ist ein lunisolarer Kalender, der auf dem Neumondzyklus
     * basiert und das Jahr mit dem Monat Kartika beginnt. </p>
     *
     * <p>Die Standard&auml;ra ist {@link HinduEra#VIKRAMA}. In Gebrauch in Gujarat. </p>
     *
     * @see     #AMANTA_ASHADHA
     */
    AMANTA_KARTIKA() {
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.VIKRAMA;
        }
    },

    /**
     * <p>The purnimanta scheme is a lunisolar calendar based on the full moon cycle. </p>
     */
    /*[deutsch]
     * <p>Das Purnimanta-Schema ist ein lunisolarer Kalender, der auf dem Vollmondzyklus
     * basiert. </p>
     */
    PURNIMANTA() {
        @Override
        HinduEra getDefaultEra() {
            return HinduEra.VIKRAMA;
        }
    };

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates the associated and customizable variant of Hindu calendar. </p>
     *
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Erzeugt die mit der Regel verkn&uuml;pfte und anpassbare Variante des Hindu-Kalenders. </p>
     *
     * @return  HinduVariant
     */
    public HinduVariant variant() {
        return new HinduVariant(this, this.getDefaultEra());
    }

    abstract HinduEra getDefaultEra();

}
