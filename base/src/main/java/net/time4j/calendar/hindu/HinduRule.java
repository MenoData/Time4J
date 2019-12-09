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
     * <p>Sunrise of following morning is used. </p>
     *
     * <p>This is the most common rule. </p>
     */
    /*[deutsch]
     * <p>Der Sonnenaufgang des folgenden Tages wird verwendet. </p>
     *
     * <p>Das ist die am meisten gebr&auml;uchliche Regel. </p>
     */
    ORISSA,

    /**
     * <p>Sunset of current day is used. </p>
     *
     * <p>Mainly used in Tamil Nadu region. </p>
     */
    /*[deutsch]
     * <p>Der Sonnenuntergang des aktuellen Tages wird verwendet. </p>
     *
     * <p>Haupts&auml;chlich in Tamil Nadu verwendet. </p>
     */
    TAMIL,

    /**
     * <p>The seasonal time of 12 minutes after 1 PM is used which corresponds to 3/5 th of the time period
     * between sunrise and sunset. </p>
     *
     * <p>Mainly used in Kerala region. </p>
     */
    /*[deutsch]
     * <p>Der Sonnenuntergang des aktuellen Tages wird verwendet, was 3/5 der Zeit zwischen Sonnenaufgang und
     * Sonnenuntergang entspricht. </p>
     *
     * <p>Haupts&auml;chlich in Kerala verwendet. </p>
     */
    MALAYALI,

    /**
     * <p>Midnight at end of current day is used. </p>
     *
     * <p>Mainly used in Madras (Chennai). </p>
     */
    /*[deutsch]
     * <p>Mitternacht am Ende des aktuellen Tages wird verwendet. </p>
     *
     * <p>Haupts&auml;chlich in Madras (Chennai) verwendet. </p>
     */
    MADRAS,

    /**
     * <p>A rule which is used in West Bengal, Assam and Tripura. </p>
     *
     * <p>We follow the details given by Vinod K. Mishra in his script &quot;The calendars of India&quot;
     * which say: When the samkranti occurs between the sunrise and the following midnight then the
     * month begins on the next day. If samkranti occurs after midnight then the month begins on the
     * day following the next day, i.e. on the third day. </p>
     */
    /*[deutsch]
     * <p>Eine Regel, die in West Bengal, Assam and Tripura verwendet wird. </p>
     *
     * <p>Wir folgen den Details gegeben durch Vinod K. Mishra in seinem Buch &quot;The calendars of India&quot;,
     * die besagen: Wenn Samkranti zwischen Sonnenaufgang und der folgenden Mitternacht eintritt, dann f&auml;ngt
     * der Monat am n&auml;chsten Tag an. Wenn Samkranti nach Mitternacht eintritt, dann f&auml;ngt der Monat am
     * Tag nach dem n&auml;chsten Tag an, also am dritten Tag. </p>
     */
    BENGAL,

    /**
     * <p>Contains a set of calculations developed by Arya Siddhanta of Aryabhata in Julian year 499 AD,
     * mentioned by Lalla at about 720-790 AD. </p>
     *
     * <p>The old Hindu calendar uses mean values in all astronomical calculations. </p>
     */
    /*[deutsch]
     * <p>Entspricht Berechnungen, die von Arya Siddhanta von Aryabhata im julianischen Jahr 499 AD
     * entwickelt wurden, zitiert durch Lalla ungef&auml;hr um 720-790 AD. </p>
     *
     * <p>Der alte Hindukalender verwendet Mittelwerte in allen astronomischen Berechnungen. </p>
     */
    ARYA_SIDDHANTA

}
