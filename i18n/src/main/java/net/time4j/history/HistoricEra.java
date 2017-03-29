/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricEra.java) is part of project Time4J.
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

package net.time4j.history;

import net.time4j.base.MathUtils;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.ChronoElement;
import net.time4j.format.CalendarText;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>Represents a historic era dividing the local timeline at roughly the point
 * of Jesu birth in the context of the julian/gregorian calendar. </p>
 *
 * <p>Important limitation in historic context: The early midage often used different
 * eras for reckoning the years (not completely handled by Time4J). See also the webpage
 * <a href="http://www.newadvent.org/cathen/03738a.htm#other">General Chronology</a>. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine historische &Auml;ra, die ungef&auml;hr den angenommenen
 * Zeitpunkt von Jesu Geburt im julianisch/gregorianischen Kalender als
 * Teilung der Zeitskala benutzt. </p>
 *
 * <p>Wichtige Einschr&auml;nkung im historischen Kontext: Das fr&uuml;he Mittelalter
 * hat oft eine andere &Auml;ra f&uuml;r die Jahresz&auml;hlung verwendet (von Time4J
 * nicht vollst&auml;ndig unterst&uuml;tzt). Siehe auch die Webseite
 * <a href="http://www.newadvent.org/cathen/03738a.htm#other">General Chronology</a>. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
public enum HistoricEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>BC = Before Christian</p>
     *
     * <p>Years related to this era are counted backwards and must not be smaller than {@code 1}. </p>
     */
    /*[deutsch]
     * <p>&Auml;ra vor Christi Geburt. </p>
     *
     * <p>Jahre bez&uuml;glich dieser &Auml;ra werden r&uuml;ckl&auml;ufig gez&auml;hlt und
     * d&uuml;rfen nicht kleiner als {@code 1} sein. </p>
     */
    BC,

    /**
     * <p>AD = Anno Domini</p>
     *
     * <p>Years related to this era must not be smaller than {@code 1}. </p>
     */
    /*[deutsch]
     * <p>&Auml;ra nach Christi Geburt. </p>
     *
     * <p>Jahre bez&uuml;glich dieser &Auml;ra d&uuml;rfen nicht kleiner als {@code 1} sein. </p>
     */
    AD,

    /**
     * <p>Years are reckoned from 38 BC onwards (Era of Caesars or Spanish Era). </p>
     *
     * <p>This overlapping alternative to {@link #AD} was used in Spain (until 1383), Portugal (until 1422)
     * and southern part of France. </p>
     *
     * <p>Links: </p>
     * <ul>
     *     <li><a href="https://en.wikipedia.org/wiki/Spanish_era">Wikipedia</a></li>
     *     <li><a href="http://www.britannica.com/topic/chronology/Christian#ref523287">brittanica.com</a></li>
     * </ul>
     */
    /*[deutsch]
     * <p>Jahre werden seit 38 BC (&Auml;ra der C&auml;saren oder spanische &Auml;ra) gez&auml;hlt. </p>
     *
     * <p>Diese mit {@link #AD} &uuml;berlappende Alternative wurde in Spanien (bis 1383), Portugal (bis 1422)
     * und dem s&uuml;dlichen Frankreich verwendet. </p>
     *
     * <p>Links: </p>
     * <ul>
     *     <li><a href="https://en.wikipedia.org/wiki/Spanish_era">Wikipedia</a></li>
     *     <li><a href="http://www.britannica.com/topic/chronology/Christian#ref523287">brittanica.com</a></li>
     * </ul>
     */
    HISPANIC,

    /**
     * <p>Years are reckoned since the assumed year of creation of the world (Anno Mundi) in 5508 BC. </p>
     *
     * <p>This overlapping alternative to {@link #AD} was mainly used in Russia
     * before 1700 with the rule that years start on first of September.
     * See also: <a href="https://en.wikipedia.org/wiki/Anno_Mundi">Wikipedia</a>. </p>
     *
     * @see     NewYearRule#BEGIN_OF_SEPTEMBER
     */
    /*[deutsch]
     * <p>Jahre werden seit 5508 BC gez&auml;hlt (angenommenes Jahr der Erschaffung der Welt - Anno Mundi). </p>
     *
     * <p>Diese mit {@link #AD} &uuml;berlappende Alternative wurde haupts&auml;chlich in Ru&szlig;land
     * vor 1700 verwendet mit der Regel, da&szlig; Jahre am ersten September beginnen. Siehe auch:
     * <a href="https://en.wikipedia.org/wiki/Anno_Mundi">Wikipedia</a>. </p>
     *
     * @see     NewYearRule#BEGIN_OF_SEPTEMBER
     */
    BYZANTINE,

    /**
     * <p>This overlapping alternative to {@link #AD} was used by chronists to count the years
     * since the assumed founding of Rome (753 BC - version of Varro). </p>
     *
     * <p>See also: <a href="https://en.wikipedia.org/wiki/Ab_urbe_condita">Wikipedia</a>. </p>
     */
    /*[deutsch]
     * <p>Diese mit {@link #AD} &uuml;berlappende Alternative wurde von Chronisten verwendet,
     * um die Jahre seit der angenommenen Gr&uuml;ndung der Stadt Rom zu z&auml;hlen
     * (753 BC - version of Varro). </p>
     *
     * <p>Siehe auch: <a href="https://en.wikipedia.org/wiki/Ab_urbe_condita">Wikipedia</a>. </p>
     */
    AB_URBE_CONDITA;

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getValue() {

        return ((this == BC) ? 0 : 1);

    }

    /**
     * <p>Gets the description text dependent on the locale and text width. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and width (never {@code null})
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and width (never {@code null})
     * @since   3.0
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width
    ) {

        CalendarText names = CalendarText.getIsoInstance(locale);
        return names.getEras(width).print(this);

    }

    /**
     * <p>Gets an alternative description text dependent on the locale and text width. </p>
     *
     * <p>This method yields for English the notations of <em>&quot;(Before) Common Era&quot; (BCE/CE)</em>. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  alternative descriptive text for given locale and width (never {@code null})
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert einen alternativen sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>Diese Methode liefert f&uuml;r Englisch die Bezeichnungen
     * <em>&quot;(Before) Common Era&quot; (BCE/CE)</em>. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  alternative descriptive text for given locale and width (never {@code null})
     * @since   3.0
     */
    public String getAlternativeName(
        Locale locale,
        TextWidth width
    ) {

        CalendarText names = CalendarText.getIsoInstance(locale);
        ChronoElement<HistoricEra> element = ChronoHistory.ofFirstGregorianReform().era();
        return names.getTextForms(element, ((width == TextWidth.WIDE) ? "w" : "a"), "alt").print(this);

    }

    /**
     * <p>Scales given year of era to its mathematical AD value. </p>
     *
     * @param   yearOfEra   historic year reckoned in this era
     * @return  year related to era AD
     * @throws  IllegalArgumentException if given year of era is out of range
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Skaliert das angegebene Jahr der &Auml;ra zu seinem mathematischen AD-Wert. </p>
     *
     * @param   yearOfEra   historic year reckoned in this era
     * @return  year related to era AD
     * @throws  IllegalArgumentException if given year of era is out of range
     * @since   3.19/4.15
     */
    public int annoDomini(int yearOfEra) {

        try {
            switch (this) {
                case BC:
                    return MathUtils.safeSubtract(1, yearOfEra);
                case AD:
                    return yearOfEra;
                case HISPANIC:
                    return MathUtils.safeSubtract(yearOfEra, 38);
                case BYZANTINE:
                    return MathUtils.safeSubtract(yearOfEra, 5508);
                case AB_URBE_CONDITA:
                    return MathUtils.safeSubtract(yearOfEra, 753);
                default:
                    throw new UnsupportedOperationException(this.name());
            }
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Out of range: " + yearOfEra);
        }

    }

    /**
     * <p>Scales given year of era to another year related to this era. </p>
     *
     * @param   era         era reference of given year
     * @param   yearOfEra   historic year reckoned in given era
     * @return  year related to this era
     * @throws  IllegalArgumentException if given year of era is out of range
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Skaliert das angegebene Jahr der &Auml;ra zu einem anderen Jahreswert bezogen auf diese &Auml;ra. </p>
     *
     * @param   era         era reference of given year
     * @param   yearOfEra   historic year reckoned in given era
     * @return  year related to this era
     * @throws  IllegalArgumentException if given year of era is out of range
     * @since   3.14/4.11
     */
    int yearOfEra(
        HistoricEra era,
        int yearOfEra
    ) {

        int ad = era.annoDomini(yearOfEra);

        try {
            switch (this) {
                case BC:
                    return MathUtils.safeSubtract(1, ad);
                case AD:
                    return ad;
                case HISPANIC:
                    return MathUtils.safeAdd(ad, 38);
                case BYZANTINE:
                    return MathUtils.safeAdd(ad, 5508);
                case AB_URBE_CONDITA:
                    return MathUtils.safeAdd(ad, 753);
                default:
                    throw new UnsupportedOperationException(this.name());
            }
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Out of range: " + yearOfEra);
        }

    }

}
