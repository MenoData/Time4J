/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChineseEra.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.engine.CalendarEra;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>The Chinese calendar does not have a universally accepted way of continuously counting years
 * and prefers the sexagesimal cyclic years so this enum is mainly useful for either historic or
 * half-academic debates. </p>
 *
 * <p>The first year of an era is always counted as year 1. The historic Qing-eras started on Chinese New Year
 * after the previous emperor had died or resigned. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * <p>Der chinesische Kalender hat kein universell akzeptiertes System der kontinuierlichen Jahresz&auml;hlung
 * und zieht die sexagesimalen zyklischen Jahresangaben vor, so da&szlig; dieses Enum eher f&uuml;r historische
 * oder halbakademische Debatten n&uuml;tzlich ist. </p>
 *
 * <p>Das erste Jahr einer &Auml;ra wird immer als Jahr 1 gez&auml;hlt. Die historischen Qing-&Auml;ren
 * begannen stets am chinesischen Neujahrstag nach dem Ausscheiden des vorherigen Kaisers. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
public enum ChineseEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Counts years since the reign of emperor Shunzhi of Qing dynasty (from 1644 to 1662). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Shunzhi der Qing-Dynastie (1644-1662). </p>
     */
    QING_SHUNZHI_1644_1662,

    /**
     * <p>Counts years since the reign of emperor Kangxi of Qing dynasty (from 1662 to 1723). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Kangxi der Qing-Dynastie (1662-1723). </p>
     */
    QING_KANGXI_1662_1723,

    /**
     * <p>Counts years since the reign of emperor Yongzheng of Qing dynasty (from 1723 to 1736). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Yongzheng der Qing-Dynastie (1723-1736). </p>
     */
    QING_YONGZHENG_1723_1736,

    /**
     * <p>Counts years since the reign of emperor Qianlong of Qing dynasty (from 1736 to 1796). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Qianlong der Qing-Dynastie (1736-1796). </p>
     */
    QING_QIANLONG_1736_1796,

    /**
     * <p>Counts years since the reign of emperor Jiaqing of Qing dynasty (from 1796 to 1821). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Jiaqing der Qing-Dynastie (1796-1821). </p>
     */
    QING_JIAQING_1796_1821,

    /**
     * <p>Counts years since the reign of emperor Daoguang of Qing dynasty (from 1821 to 1851). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Daoguang der Qing-Dynastie (1821-1851). </p>
     */
    QING_DAOGUANG_1821_1851,

    /**
     * <p>Counts years since the reign of emperor Xianfeng of Qing dynasty (from 1851 to 1862). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Xianfeng der Qing-Dynastie (1851-1862). </p>
     */
    QING_XIANFENG_1851_1862,

    /**
     * <p>Counts years since the reign of emperor Tongzhi of Qing dynasty (from 1862 to 1875). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Tongzhi der Qing-Dynastie (1862-1875). </p>
     */
    QING_TONGZHI_1862_1875,

    /**
     * <p>Counts years since the reign of emperor Guangxu of Qing dynasty (from 1875 to 1909). </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Guangxu der Qing-Dynastie (1875-1909). </p>
     */
    QING_GUANGXU_1875_1909,

    /**
     * <p>Counts years since the reign of emperor Xuantong of Qing dynasty (from 1909 to 1912). </p>
     *
     * <p>This was the era of the last (child-)emperor in Chinese history whose name was Pu-yi.
     * He was forced to abdicate on February the 12th of 1912. </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit der Thronbesteigung des Kaisers Xuantong der Qing-Dynastie (1909-1912). </p>
     *
     * <p>Dies war die &Auml;ra des letzten (Kinds-)Kaisers der chinesischen Geschichte, dessen Name Pu-yi war.
     * Er wurde am 12. Februar 1912 zur Abdankung gezwungen. </p>
     */
    QING_XUANTONG_1909_1912,

    /**
     * <p>Marks the begin of the reign of legendary yellow emperor Huang-di which was assumed by Sun-yat-sen
     * in year 2698 BCE. </p>
     *
     * <p>This way of continuous counting of years was also used by the Chinese community of San Francisco.
     * However, other sources report as begin of the reign rather the year 2697 BCE (probably Sun-yat-sen
     * has miscalculated his era proposal by one year or maybe mistreated the year zero). </p>
     */
    /*[deutsch]
     * <p>Kennzeichnet den Beginn der Herrschaft des mythischen Kaisers Huang-di, dessen Datum von Sun-yat-sen
     * als im Jahre 2698 BC liegend angenommen wurde. </p>
     *
     * <p>Diese Art der kontinuierlichen Jahresz&auml;hlung wurde auch von der chinesischen Kommune in
     * San Francisco verwendet. Allerdings berichten verschiedene Quellen als Beginn der Herrschaft von
     * Huang-di das Jahr 2697 BC (vermutlich hat sich Sun-yat-sen um ein Jahr verz&auml;hlt oder das Jahr 0
     * falsch ber&uuml;cksichtigt). </p>
     */
    YELLOW_EMPEROR;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Equivalent to the expression {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE);

    }

    /**
     * <p>Gets the description text dependent on the locale and style parameters. </p>
     *
     * <p>The second argument controls the width of description. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and style (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>&Uuml;ber das zweite Argument kann gesteuert werden, ob eine kurze
     * oder eine lange Form des Beschreibungstexts ausgegeben werden soll. Das
     * ist besonders sinnvoll in Benutzeroberfl&auml;chen, wo zwischen der
     * Beschriftung und der detaillierten Erl&auml;uterung einer graphischen
     * Komponente unterschieden wird. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and style (never {@code null})
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width
    ) {

        String lang = locale.getLanguage();

        switch (this) {
            case QING_SHUNZHI_1644_1662:
                if (lang.equals("zh")) {
                    return "順治";
                } else if (lang.isEmpty()) {
                    return "Shunzhi";
                } else {
                    return "Shùnzhì";
                }
            case QING_KANGXI_1662_1723:
                if (lang.equals("zh")) {
                    return "康熙";
                } else if (lang.isEmpty()) {
                    return "Kangxi";
                } else {
                    return "Kāngxī";
                }
            case QING_YONGZHENG_1723_1736:
                if (lang.equals("zh")) {
                    return "雍正";
                } else if (lang.isEmpty()) {
                    return "Yongzheng";
                } else {
                    return "Yōngzhèng";
                }
            case QING_QIANLONG_1736_1796:
                if (lang.equals("zh")) {
                    return "乾隆";
                } else if (lang.isEmpty()) {
                    return "Qianlong";
                } else {
                    return "Qiánlóng";
                }
            case QING_JIAQING_1796_1821:
                if (lang.equals("zh")) {
                    return "嘉慶";
                } else if (lang.isEmpty()) {
                    return "Jiaqing";
                } else {
                    return "Jiāqìng";
                }
            case QING_DAOGUANG_1821_1851:
                if (lang.equals("zh")) {
                    return "道光";
                } else if (lang.isEmpty()) {
                    return "Daoguang";
                } else {
                    return "Dàoguāng";
                }
            case QING_XIANFENG_1851_1862:
                if (lang.equals("zh")) {
                    return "咸豐";
                } else if (lang.isEmpty()) {
                    return "Xianfeng";
                } else {
                    return "Xiánfēng";
                }
            case QING_TONGZHI_1862_1875:
                if (lang.equals("zh")) {
                    return "同治";
                } else if (lang.isEmpty()) {
                    return "Tongzhi";
                } else {
                    return "Tóngzhì";
                }
            case QING_GUANGXU_1875_1909:
                if (lang.equals("zh")) {
                    return "光緒";
                } else if (lang.isEmpty()) {
                    return "Guangxu";
                } else {
                    return "Guāngxù";
                }
            case QING_XUANTONG_1909_1912:
                if (lang.equals("zh")) {
                    return "宣統";
                } else if (lang.isEmpty()) {
                    return "Xuantong";
                } else {
                    return "Xuāntǒng";
                }
            case YELLOW_EMPEROR:
                if (lang.equals("zh")) {
                    return "黄帝紀年";
                } else if (width == TextWidth.WIDE) {
                    return (lang.isEmpty() ? "Anno Huangdi" : "Anno Huángdì");
                } else if (width == TextWidth.NARROW) {
                    return "H";
                } else {
                    return (lang.isEmpty() ? "Huangdi" : "Huángdì");
                }
            default:
                throw new UnsupportedOperationException("Not yet implemented: " + this.name());
        }

    }

    /**
     * <p>Does this era belongs to the Qing dynasty? </p>
     *
     * @return  {@code true} if this era is associated with the Qing dynasty else {@code false}
     */
    /*[deutsch]
     * <p>Geh&ouml;rt diese &Auml;ra zur Qing-Dynastie? </p>
     *
     * @return  {@code true} if this era is associated with the Qing dynasty else {@code false}
     */
    public boolean isQingDynasty() {

        return (this.ordinal() <= QING_XUANTONG_1909_1912.ordinal());

    }

    int getMinYearOfEra() {

        return (this == YELLOW_EMPEROR) ? 1645 + 2698 : 1;

    }

    int getMaxYearOfEra() {

        switch (this) {
            case QING_SHUNZHI_1644_1662:
                return 1662 - 1644;
            case QING_KANGXI_1662_1723:
                return 1723 - 1662;
            case QING_YONGZHENG_1723_1736:
                return 1736 - 1723;
            case QING_QIANLONG_1736_1796:
                return 1796 - 1736;
            case QING_JIAQING_1796_1821:
                return 1821 - 1796;
            case QING_DAOGUANG_1821_1851:
                return 1851 - 1821;
            case QING_XIANFENG_1851_1862:
                return 1862 - 1851;
            case QING_TONGZHI_1862_1875:
                return 1875 - 1862;
            case QING_GUANGXU_1875_1909:
                return 1909 - 1875;
            case QING_XUANTONG_1909_1912:
                return 3; // new year in 1912 is 6 days after abdication: 1912-02-18
            case YELLOW_EMPEROR:
                return 2999 + 2698;
            default:
                throw new UnsupportedOperationException("Not yet implemented: " + this.name());
        }

    }

    int getStartAsGregorianYear() {

        switch (this) {
            case QING_SHUNZHI_1644_1662:
                return 1644;
            case QING_KANGXI_1662_1723:
                return 1662;
            case QING_YONGZHENG_1723_1736:
                return 1723;
            case QING_QIANLONG_1736_1796:
                return 1736;
            case QING_JIAQING_1796_1821:
                return 1796;
            case QING_DAOGUANG_1821_1851:
                return 1821;
            case QING_XIANFENG_1851_1862:
                return 1851;
            case QING_TONGZHI_1862_1875:
                return 1862;
            case QING_GUANGXU_1875_1909:
                return 1875;
            case QING_XUANTONG_1909_1912:
                return 1909;
            case YELLOW_EMPEROR:
                return -2697;
            default:
                throw new UnsupportedOperationException("Not yet implemented: " + this.name());
        }

    }

}
