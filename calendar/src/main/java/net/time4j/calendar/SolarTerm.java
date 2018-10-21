/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SolarTerm.java) is part of project Time4J.
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

import net.time4j.Moment;
import net.time4j.base.MathUtils;
import net.time4j.calendar.astro.JulianDay;
import net.time4j.calendar.astro.StdSolarCalculator;
import net.time4j.tz.ZonalOffset;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * The solar terms of Chinese calendar year which divide the sun eclipse into 24 parts.
 *
 * <p>See also <a href="https://en.wikipedia.org/wiki/Solar_term">Wikipedia</a>. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * Die 24 Jahreseinteilungen des chinesischen Kalenderjahres entlang der Eklipse der Sonne.
 *
 * <p>Siehe auch <a href="https://en.wikipedia.org/wiki/Solar_term">Wikipedia</a> (English). </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
public enum SolarTerm {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Solar longitude 315. Around February 4. Meaning: Beginning of Spring.
     */
    MINOR_01_LICHUN_315,

    /**
     * Solar longitude 330. Around February 19. Meaning: Rain Water.
     */
    MAJOR_01_YUSHUI_330,

    /**
     * Solar longitude 345. Around March 6. Meaning: Waking of Insects.
     */
    MINOR_02_JINGZHE_345,

    /**
     * Solar longitude 0. Around March 21. Meaning: Vernal Equinox.
     */
    MAJOR_02_CHUNFEN_000,

    /**
     * Solar longitude 15. Around April 5. Meaning: Pure Brightness.
     */
    MINOR_03_QINGMING_015,

    /**
     * Solar longitude 30. Around April 20. Meaning: Grain Rain.
     */
    MAJOR_03_GUYU_030,

    /**
     * Solar longitude 45. Around May 6. Meaning: Beginning of Summer.
     */
    MINOR_04_LIXIA_045,

    /**
     * Solar longitude 60. Around May 21. Meaning: Grain Full.
     */
    MAJOR_04_XIAOMAN_060,

    /**
     * Solar longitude 75. Around June 6. Meaning: Grain in Ear.
     */
    MINOR_05_MANGZHONG_075,

    /**
     * Solar longitude 90. Around June 21. Meaning: Summer Solstice.
     */
    MAJOR_05_XIAZHI_090,

    /**
     * Solar longitude 105. Around July 7. Meaning: Slight Heat.
     */
    MINOR_06_XIAOSHU_105,

    /**
     * Solar longitude 120. Around July 23. Meaning: Great Heat.
     */
    MAJOR_06_DASHU_120,

    /**
     * Solar longitude 135. Around August 8. Meaning: Beginning of Autumn.
     */
    MINOR_07_LIQIU_135,

    /**
     * Solar longitude 150. Around August 23. Meaning: Limit of Heat.
     */
    MAJOR_07_CHUSHU_150,

    /**
     * Solar longitude 165. Around September 8. Meaning: White Dew.
     */
    MINOR_08_BAILU_165,

    /**
     * Solar longitude 180. Around September 23. Meaning: Autumnal Equinox.
     */
    MAJOR_08_QIUFEN_180,

    /**
     * Solar longitude 195. Around October 8. Meaning: Cold Dew.
     */
    MINOR_09_HANLU_195,

    /**
     * Solar longitude 210. Around October 24. Meaning: Descent of Frost.
     */
    MAJOR_09_SHUANGJIANG_210,

    /**
     * Solar longitude 225. Around November 8. Meaning: Beginning of Winter.
     */
    MINOR_10_LIDONG_225,

    /**
     * Solar longitude 240. Around November 22. Meaning: Slight Snow.
     */
    MAJOR_10_XIAOXUE_240,

    /**
     * Solar longitude 255. Around December 7. Meaning: Great Snow.
     */
    MINOR_11_DAXUE_255,

    /**
     * Solar longitude 270. Around December 22. Meaning: Winter Solstice.
     */
    MAJOR_11_DONGZHI_270,

    /**
     * Solar longitude 285. Around January 6. Meaning: Slight Cold.
     */
    MINOR_12_XIAOHAN_285,

    /**
     * Solar longitude 300. Around January 20. Meaning: Great Cold.
     */
    MAJOR_12_DAHAN_300;

    private static final SolarTerm[] ENUMS = SolarTerm.values();
    private static final double MEAN_TROPICAL_YEAR = 365.242189;

    private static final String[] SIMPLE = {
        "lichun", "yushui", "jingzhe", "chunfen", "qingming", "guyu", "lixia", "xiaoman",
        "mangzhong", "xiazhi", "xiaoshu", "dashu", "liqiu", "chushu", "bailu", "qiufen",
        "hanlu", "shuangjiang", "lidong", "xiaoxue", "daxue", "dongzhi", "xiaohan", "dahan"
    };

    private static final String[] TRANSSCRIPTION = {
        "lìchūn", "yǔshuǐ", "jīngzhé", "chūnfēn", "qīngmíng", "gǔyǔ", "lìxià", "xiǎomǎn",
        "mángzhòng", "xiàzhì", "xiǎoshǔ", "dàshǔ", "lìqiū", "chǔshǔ", "báilù", "qiūfēn",
        "hánlù", "shuāngjiàng", "lìdōng", "xiǎoxuě", "dàxuě", "dōngzhì", "xiǎohán", "dàhán"
    };

    private static final String[] CHINESE_SIMPLIFIED = {
        "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满",
        "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
        "寒露", "霜降", "立冬", "小雪", "大雪", "冬至", "小寒", "大寒"
    };

    private static final String[] CHINESE_TRADITIONAL = {
        "立春", "雨水", "驚蟄", "春分", "清明", "穀雨", "立夏", "小滿",
        "芒種", "夏至", "小暑", "大暑", "立秋", "處暑", "白露", "秋分",
        "寒露", "霜降", "立冬", "小雪", "大雪", "冬至", "小寒", "大寒"
    };

    private static final String[] KOREAN = { // Hangul characters in South Korea
        "입춘", "우수", "경칩", "춘분", "청명", "곡우", "입하", "소만",
        "망종", "하지", "소서", "대서", "입추", "처서", "백로", "추분",
        "한로", "상강", "입동", "소설", "대설", "동지", "소한", "대한"
    };

    private static final String[] VIETNAMESE = {
        "Lập xuân", "Vũ thủy", "Kinh trập", "Xuân phân", "Thanh minh", "Cốc vũ", "Lập hạ", "Tiểu mãn",
        "Mang chủng", "Hạ chí", "Tiểu thử", "Đại thử", "Lập thu", "Xử thử", "Bạch lộ", "Thu phân",
        "Hàn lộ", "Sương giáng", "Lập đông", "Tiểu tuyết", "Đại tuyết", "Đông chí", "Tiểu hàn", "Đại hàn"
    };

    private static final String[] JAPANESE = {
        "立春", "雨水", "啓蟄", "春分", "清明", "穀雨", "立夏", "小満",
        "芒種", "夏至", "小暑", "大暑", "立秋", "処暑", "白露", "秋分",
        "寒露", "霜降", "立冬", "小雪", "大雪", "冬至", "小寒", "大寒"
    };

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains a major {@code SolarTerm} by given index
     * according to the traditional order in the Chinese calendar. </p>
     *
     * @param   index   an integer in range {@code 1-12}
     * @throws  IllegalArgumentException if the index is out of range
     */
    /*[deutsch]
     * <p>Liefert eine Haupt-Instanz von {@code SolarTerm} mit Hilfe des angegebenen Index
     * entsprechend der traditionellen Reihenfolge im chinesischen Kalender. </p>
     *
     * @param   index   an integer in range {@code 1-12}
     * @throws  IllegalArgumentException if the index is out of range
     */
    public static SolarTerm ofMajor(int index) {

        if ((index < 1) || (index > 12)) {
            throw new IllegalArgumentException("Out of range: " + index);
        }

        return ENUMS[(2 * index) - 1];

    }

    /**
     * <p>Obtains a minor {@code SolarTerm} by given index
     * according to the traditional order in the Chinese calendar. </p>
     *
     * @param   index   an integer in range {@code 1-12}
     * @throws  IllegalArgumentException if the index is out of range
     */
    /*[deutsch]
     * <p>Liefert eine Neben-Instanz von {@code SolarTerm} mit Hilfe des angegebenen Index
     * entsprechend der traditionellen Reihenfolge im chinesischen Kalender. </p>
     *
     * @param   index   an integer in range {@code 1-12}
     * @throws  IllegalArgumentException if the index is out of range
     */
    public static SolarTerm ofMinor(int index) {

        if ((index < 1) || (index > 12)) {
            throw new IllegalArgumentException("Out of range: " + index);
        }

        return ENUMS[2 * (index - 1)];

    }

    /**
     * <p>Obtains an instance of {@code SolarTerm} at given moment. </p>
     *
     * @param   moment      the moment for which the solar term needs to be searched
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     * @see     JulianDay#MIN
     * @see     JulianDay#MAX
     */
    /*[deutsch]
     * <p>Liefert eine Instanz von {@code SolarTerm} zum angegebenen Moment. </p>
     *
     * @param   moment      the moment for which the solar term needs to be searched
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     * @see     JulianDay#MIN
     * @see     JulianDay#MAX
     */
    public static SolarTerm of(Moment moment) {

        double jde = JulianDay.ofEphemerisTime(moment).getValue();
        int angleIndex = (int) Math.floor(solarLongitude(jde) / 15);
        return ENUMS[(angleIndex + 3) % 24];

    }

    /**
     * <p>Obtains the associated index according to the traditional order in the Chinese calendar. </p>
     *
     * <p>The index itself is not unique because a solar term must be qualified by the major/minor-property, too. </p>
     *
     * @return  int (in range {@code 1-12})
     * @see     #isMajor()
     * @see     #isMinor()
     */
    /*[deutsch]
     * <p>Liefert den assoziierten Index entsprechend der traditionellen Reihenfolge im chinesischen Kalender. </p>
     *
     * <p>Der Index selbst ist nicht eindeutig, weil ein {@code SolarTerm} zus&auml;tzlich durch
     * die major/minor-Eigenschaft qualifiziert werden mu&szlig;. </p>
     *
     * @return  int (in range {@code 1-12})
     * @see     #isMajor()
     * @see     #isMinor()
     */
    public int getIndex() {

        return (this.ordinal() / 2) + 1;

    }

    /**
     * <p>Does this instance represent a principal (major) solar term in multiples of 30 degrees? </p>
     *
     * @return  boolean
     * @see     #isMinor()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert diese Instanz eine Hauptjahreseinteilung in Vielfachen von 30 Grad? </p>
     *
     * @return  boolean
     * @see     #isMinor()
     */
    public boolean isMajor() {

        return (this.ordinal() % 2) == 1;

    }

    /**
     * <p>Does this instance represent a minor solar term? </p>
     *
     * @return  boolean
     * @see     #isMajor()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert diese Instanz eine Nebenjahreseinteilung? </p>
     *
     * @return  boolean
     * @see     #isMajor()
     */
    public boolean isMinor() {

        return (this.ordinal() % 2) == 0;

    }

    /**
     * <p>Obtains the associated solar longitude in degrees. </p>
     *
     * @return  angle in degrees (range {@code 0 <= angle < 360})
     */
    /*[deutsch]
     * <p>Liefert die assoziierte ekliptische L&auml;nge der Sonne in Grad. </p>
     *
     * @return  angle in degrees (range {@code 0 <= angle < 360})
     */
    public int getSolarLongitude() {

        return ((this.ordinal() + 21) % 24) * 15;

    }

    /**
     * <p>Rolls this instance by given amount of solar terms. </p>
     *
     * @param   amount      count of solar terms (maybe negative)
     * @return  result of rolling operation
     */
    /*[deutsch]
     * <p>Rollt um die angegebene Anzahl von {@code SolarTerm} vor oder zur&uuml;ck. </p>
     *
     * @param   amount      count of solar terms (maybe negative)
     * @return  result of rolling operation
     */
    public SolarTerm roll(int amount) {

        return ENUMS[MathUtils.floorModulo(this.ordinal() + amount, 24)];

    }

    /**
     * <p>Determines the date when this solar term will happen on or after given date. </p>
     *
     * @param   date    the starting date of the search for this solar term
     * @return  resulting date when this solar term will happen first
     */
    /*[deutsch]
     * <p>Ermittelt das Datum, wann diese Jahreseinteilung zum oder nach dem angegebenen Datum auftreten wird. </p>
     *
     * @param   date    the starting date of the search for this solar term
     * @return  resulting date when this solar term will happen first
     */
    public <D extends EastAsianCalendar<?, D>> D onOrAfter(D date) {

        EastAsianCS<D> calsys = date.getCalendarSystem();
        long utcDays = date.getDaysSinceEpochUTC();
        ZonalOffset offset = calsys.getOffset(utcDays);
        Moment m = calsys.midnight(utcDays);
        return calsys.transform(this.atOrAfter(m).toZonalTimestamp(offset).toDate().getDaysSinceEpochUTC());

    }

    /**
     * <p>Gets the description text dependent on the locale. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     */
    public String getDisplayName(Locale locale) {

        String[] textforms = getTextForms(locale);
        return textforms[this.ordinal()];

    }

    /**
     * <p>Tries to interprete given text as solar term. </p>
     *
     * @param   text    the text to be parsed
     * @param   locale  language setting
     * @return  the parsed solar term if successful
     * @throws  ParseException if parsing fails
     */
    /*[deutsch]
     * <p>Versucht, den angegebenen Text als {@code SolarTerm} zu interpretieren. </p>
     *
     * @param   text    the text to be parsed
     * @param   locale  language setting
     * @return  the parsed solar term if successful
     * @throws  ParseException if parsing fails
     */
    public static SolarTerm parse(
        CharSequence text,
        Locale locale
    ) throws ParseException {

        SolarTerm st = parse(text, locale, new ParsePosition(0));

        if (st == null) {
            throw new ParseException("Cannot parse: " + text, 0);
        } else {
            return st;
        }

    }

    // also called by EastAsianST
    static SolarTerm parse(
        CharSequence text,
        Locale locale,
        ParsePosition status
    ) {

        String[] textforms = getTextForms(locale);
        boolean rootLocale = locale.getLanguage().isEmpty();
        int offset = status.getIndex();

        for (int i = 0; i < textforms.length; i++) {
            String test = textforms[i];
            String comp = text.subSequence(offset, Math.min(text.length(), offset + test.length())).toString();
            if ((rootLocale && comp.equalsIgnoreCase(test)) || comp.equals(test)) {
                status.setIndex(status.getIndex() + test.length());
                return ENUMS[i];
            }
        }

        if (locale.getLanguage().isEmpty() || (textforms != TRANSSCRIPTION)) {
            status.setErrorIndex(status.getIndex());
            return null;
        } else {
            return parse(text, Locale.ROOT, status);
        }

    }

    // also called by EastAsianCS
    static double solarLongitude(double jde) {
        return StdSolarCalculator.TIME4J.getFeature(jde, "solar-longitude"); // slightly more precise than CC
    }

    private static double modulo360(double angle) {
        return angle - 360.0 * Math.floor(angle / 360.0); // always >= 0.0
    }

    private static String[] getTextForms(Locale locale) {
        if (locale.getLanguage().equals("zh")) {
            return locale.getCountry().equals("TW") ? CHINESE_TRADITIONAL : CHINESE_SIMPLIFIED;
        } else if (locale.getLanguage().equals("ko")) {
            return KOREAN;
        } else if (locale.getLanguage().equals("vi")) {
            return VIETNAMESE;
        } else if (locale.getLanguage().equals("ja")) {
            return JAPANESE;
        } else {
            return locale.getLanguage().isEmpty() ? SIMPLE : TRANSSCRIPTION;
        }
    }

    private Moment atOrAfter(Moment moment) {

        double angle = this.getSolarLongitude();
        double jd0 = JulianDay.ofEphemerisTime(moment).getValue();
        double estimate = jd0 + modulo360(angle - solarLongitude(jd0)) * MEAN_TROPICAL_YEAR / 360.0;
        double low = Math.max(jd0, estimate - 5);
        double high = estimate + 5;

        while (true) {
            double x = (low + high) / 2;

            if (high - low < 0.00001) { // < 0.9 seconds
                return JulianDay.ofEphemerisTime(x).toMoment();
            }

            double delta = (solarLongitude(x) - angle); // call depth ~ 20 times

            if (modulo360(delta) < 180.0) {
                high = x;
            } else {
                low = x;
            }
        }

    }

}
