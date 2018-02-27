/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CyclicYear.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;
import net.time4j.format.CalendarText;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Represents the cyclic year used in East Asian calendars. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * <p>Repr&auml;sentiert das zyklische Jahr, das in ostasiatischen Kalendern verwendet wird. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
public final class CyclicYear
    implements Comparable<CyclicYear>, Serializable {

    //~ Statische Felder/Initialisierungen ------------------------------------

    // https://en.wikipedia.org/wiki/Celestial_stem
    private static final String[] STEMS_SIMPLE = {
        "jia", "yi", "bing", "ding", "wu", "ji", "geng", "xin", "ren", "gui"
    };
    private static final String[] STEMS_PINYIN = {
        "jiǎ", "yǐ", "bǐng", "dīng", "wù", "jǐ", "gēng", "xīn", "rén", "guǐ"
    };
    private static final String[] STEMS_CHINESE = {
        "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"
    };
    private static final String[] STEMS_KOREAN = {
        "갑", "을", "병", "정", "무", "기", "경", "신", "임", "계"
    };
    private static final String[] STEMS_VIETNAMESE = {
        "giáp", "ất", "bính", "đinh", "mậu", "kỷ", "canh", "tân", "nhâm", "quý"
    };
    private static final String[] STEMS_RUSSIAN = {
        "Цзя", "И", "Бин", "Дин", "У", "Цзи", "Гэн", "Синь", "Жэнь", "Гуй"
    };

    // https://en.wikipedia.org/wiki/Earthly_Branches
    private static final String[] BRANCHES_SIMPLE = {
        "zi", "chou", "yin", "mao", "chen", "si", "wu", "wei", "shen", "you", "xu", "hai"
    };
    private static final String[] BRANCHES_PINYIN = {
        "zǐ", "chǒu", "yín", "mǎo", "chén", "sì", "wǔ", "wèi", "shēn", "yǒu", "xū", "hài"
    };
    private static final String[] BRANCHES_CHINESE = {
        "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"
    };
    private static final String[] BRANCHES_KOREAN = {
        "자", "축", "인", "묘", "진", "사", "오", "미", "신", "유", "술", "해"
    };
    private static final String[] BRANCHES_VIETNAMESE = {
        "tí", "sửu", "dần", "mão", "thìn", "tị", "ngọ", "mùi", "thân", "dậu", "tuất", "hợi"
    };
    private static final String[] BRANCHES_RUSSIAN = {
        "Цзы", "Чоу", "Инь", "Мао", "Чэнь", "Сы", "У", "Вэй", "Шэнь", "Ю", "Сюй", "Хай"
    };

    private static final CyclicYear[] INSTANCES;
    private static final Map<String, String[]> LANG_2_STEM;
    private static final Map<String, String[]> LANG_2_BRANCH;
    private static final Set<String> LANGS_WITHOUT_SEP; // implicit invariant: only one char for stem or branch

    static {
        CyclicYear[] instances = new CyclicYear[60];
        for (int i = 0; i < 60; i++) {
            instances[i] = new CyclicYear(i + 1);
        }
        INSTANCES = instances;

        Map<String, String[]> stems = new HashMap<String, String[]>();
        stems.put("root", STEMS_SIMPLE);
        stems.put("zh", STEMS_CHINESE);
        stems.put("ja", STEMS_CHINESE);
        stems.put("ko", STEMS_KOREAN);
        stems.put("vi", STEMS_VIETNAMESE);
        stems.put("ru", STEMS_RUSSIAN);
        LANG_2_STEM = Collections.unmodifiableMap(stems);

        Map<String, String[]> branches = new HashMap<String, String[]>();
        branches.put("root", BRANCHES_SIMPLE);
        branches.put("zh", BRANCHES_CHINESE);
        branches.put("ja", BRANCHES_CHINESE);
        branches.put("ko", BRANCHES_KOREAN);
        branches.put("vi", BRANCHES_VIETNAMESE);
        branches.put("ru", BRANCHES_RUSSIAN);
        LANG_2_BRANCH = Collections.unmodifiableMap(branches);

        Set<String> set = new HashSet<String>();
        set.add("zh");
        set.add("ja");
        set.add("ko");
        LANGS_WITHOUT_SEP = Collections.unmodifiableSet(set);
    }

    private static final long serialVersionUID = 4908662352833192131L;

    //~ Instanzvariablen ------------------------------------------------------

    /**
     * @serial  the number of cyclic year
     */
    private final int year;

    //~ Konstruktoren ---------------------------------------------------------

    private CyclicYear(int year) {
        super();

        this.year = year;
    }

    //~ Methoden --------------------------------------------------------------

    /**
     * <p>Obtains an instance of cyclic year. </p>
     *
     * @param   yearOfCycle     year number in the range 1-60
     * @return  CyclicYear
     * @throws  IllegalArgumentException if the parameter is out of range
     */
    /*[deutsch]
     * <p>Liefert eine Instanz eines zyklischen Jahres. </p>
     *
     * @param   yearOfCycle     year number in the range 1-60
     * @return  CyclicYear
     * @throws  IllegalArgumentException if the parameter is out of range
     */
    public static CyclicYear of(int yearOfCycle) {

        if ((yearOfCycle < 1) || (yearOfCycle > 60)) {
            throw new IllegalArgumentException("Out of range: " + yearOfCycle);
        }

        return INSTANCES[yearOfCycle - 1];

    }

    /**
     * <p>Obtains an instance of cyclic year. </p>
     *
     * @param   stem    celestial stem
     * @param   branch  terrestrial branch
     * @return  CyclicYear
     */
    /*[deutsch]
     * <p>Liefert eine Instanz eines zyklischen Jahres. </p>
     *
     * @param   stem    celestial stem
     * @param   branch  terrestrial branch
     * @return  CyclicYear
     */
    public static CyclicYear of(
        Stem stem,
        Branch branch
    ) {

        int a = stem.ordinal();
        int b = branch.ordinal();
        int yearOfCycle = (a + 1 + MathUtils.floorModulo(25 * (b - a), 60));
        return CyclicYear.of(yearOfCycle);

    }

    /**
     * <p>Obtains the associated year number in the range 1-60. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die Jahreszahl im Bereich 1-60. </p>
     *
     * @return  int
     */
    public int getNumber() {

        return this.year;

    }

    /**
     * <p>Obtains the celestial stem of this cyclic year. </p>
     *
     * @return  Stem
     */
    /*[deutsch]
     * <p>Liefert den Himmelsstamm dieses zyklischen Jahres. </p>
     *
     * @return  Stem
     */
    public Stem getStem(){

        int n = this.year % 10;

        if (n == 0) {
            n = 10;
        }

        return Stem.values()[n - 1];

    }

    /**
     * <p>Obtains the terrestrial branch of this cyclic year. </p>
     *
     * @return  Branch
     */
    /*[deutsch]
     * <p>Liefert den Erdzweig dieses zyklischen Jahres. </p>
     *
     * @return  Branch
     */
    public Branch getBranch(){

        int n = this.year % 12;

        if (n == 0) {
            n = 12;
        }

        return Branch.values()[n - 1];

    }

    /**
     * <p>Parses the given localized name as a combination of stem and branch to a cyclic year. </p>
     *
     * <p>The original Chinese names are usually not translatable. A few languages like Korean,
     * Vietnamese and Russian use their own transcriptions. The pinyin-transcription (official
     * Chinese romanization) serves as fallback for other languages. And the root locale will use
     * a simplified version of pinyin without diacritic accents. This method will always expect
     * a minus sign as separator between stem and branch unless the language is Chinese, Korean
     * or Japanese. </p>
     *
     * @param   text        the text to be parsed
     * @param   locale      language
     * @return  parsed cyclic year
     * @throws  ParseException if the text cannot be parsed
     * @see     #getDisplayName(Locale)
     */
    /*[deutsch]
     * <p>Interpretiert den sprachabh&auml;ngigen Namen als Kombination von Himmelsstamm und Erdzweig. </p>
     *
     * <p>Die chinesischen Originalnamen sind normalerweise nicht &uuml;bersetzbar. Einige wenige
     * Sprachen wie Koreanisch, Vietnamesisch und Russisch haben ihre eigenen Transkriptionen. Die
     * Pinyin-Transkription (offizielle chinesische Romanisierung) dient als Ausweichoption f&uuml;r
     * andere Sprachen. Und die {@code Locale.ROOT}-Einstellung wird eine vereinfachte Pinyin-Version
     * ohne diakritische Akzente verwenden. Zwischen Himmelsstamm und Erdzweig wird immer ein Minus-Zeichen
     * erwartet, es sei denn, die Sprache ist Chinesisch, Koreanisch oder Japanisch. </p>
     *
     * @param   text        the text to be parsed
     * @param   locale      language
     * @return  parsed cyclic year
     * @throws  ParseException if the text cannot be parsed
     * @see     #getDisplayName(Locale)
     */
    public static CyclicYear parse(
        String text,
        Locale locale
    ) throws ParseException {

        ParsePosition pp = new ParsePosition(0);
        CyclicYear cy = parse(text, pp, locale, true);

        if (cy == null) {
            throw new ParseException(text, 0);
        }

        return cy;

    }

    /**
     * <p>Obtains the localized name of this cyclic year. </p>
     *
     * <p>The original Chinese names are usually not translatable. A few languages like Korean,
     * Vietnamese and Russian use their own transcriptions. The pinyin-transcription (official
     * Chinese romanization) serves as fallback for other languages. </p>
     *
     * @param   locale      language
     * @return  display name
     * @see     #parse(String, Locale)
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Namen dieses zyklischen Jahres. </p>
     *
     * <p>Die chinesischen Originalnamen sind normalerweise nicht &uuml;bersetzbar. Einige wenige
     * Sprachen wie Koreanisch, Vietnamesisch und Russisch haben ihre eigenen Transkriptionen. Die
     * Pinyin-Transkription (offizielle chinesische Romanisierung) dient als Ausweichoption f&uuml;r
     * andere Sprachen. </p>
     *
     * @param   locale      language
     * @return  display name
     * @see     #parse(String, Locale)
     */
    public String getDisplayName(Locale locale) {

        Stem stem = this.getStem();
        Branch branch = this.getBranch();
        String sep = LANGS_WITHOUT_SEP.contains(locale.getLanguage()) ? "" : "-";
        return stem.getDisplayName(locale) + sep + branch.getDisplayName(locale);

    }

    /**
     * <p>Obtains the animal describing this cyclic year. </p>
     *
     * @param   locale      language
     * @return  name of associated zodiac animal
     */
    /*[deutsch]
     * <p>Liefert das chinesische Tierkreiszeichen dieses Jahres. </p>
     *
     * @param   locale      language
     * @return  name of associated zodiac animal
     */
    public String getZodiac(Locale locale) {

        return this.getBranch().getZodiac(locale);

    }

    /**
     * <p>Rolls this cyclic year by given amount. </p>
     *
     * @param   amount  determines how many years/units this instance should be rolled
     * @return  changed copy of this instance
     */
    /*[deutsch]
     * <p>Rollt dieses zyklische Jahr um den angegebenen Betrag. </p>
     *
     * @param   amount  determines how many years/units this instance should be rolled
     * @return  changed copy of this instance
     */
    public CyclicYear roll(int amount) {

        if (amount == 0) {
            return this;
        }

        return CyclicYear.of(MathUtils.floorModulo(MathUtils.safeAdd(this.year - 1, amount), 60) + 1);

    }

    /**
     * <p>Obtains an unambivalent year reference for given Qing dynasty. </p>
     *
     * <p>Note: The years AD 1662 and AD 1722 have the same cyclic year so an unambivalent year reference
     * cannot be determined. This is because the Kangxi-era was 61 years long. </p>
     *
     * @param   era    the Chinese era representing a historic Qing dynasty
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the era is not a Qing dynasty or if the combination of this cyclic year
     *          together with the era is ambivalent (rare case in {@code QING_KANGXI_1662_1723})
     */
    /*[deutsch]
     * <p>Liefert eine eindeutige Jahresreferenz zur angegebenen Qing-Dynastie. </p>
     *
     * <p>Hinweis: Die Jahre AD 1662 und AD 1722 haben das gleiche zyklische Jahr, so da&szlig; eine eindeutige
     * Jahresreferenz unm&ouml;glich ist. Das ist durch die L&auml;nge der Kangxi-&Auml;ra von 61 Jahren bedingt. </p>
     *
     * @param   era    the Chinese era representing a historic Qing dynasty
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the era is not a Qing dynasty or if the combination of this cyclic year
     *          together with the era is ambivalent (rare case in {@code QING_KANGXI_1662_1723})
     */
    public EastAsianYear inQingDynasty(ChineseEra era) {

        if (era.isQingDynasty()) {
            if ((era == ChineseEra.QING_KANGXI_1662_1723) && (this.year == 39)){
                throw new IllegalArgumentException(
                    "Ambivalent cyclic year in Kangxi-era (1662 or 1722): " + this.getDisplayName(Locale.ROOT));
            } else {
                final int start = era.getStartAsGregorianYear();
                final int delta = this.year - EastAsianYear.forGregorian(start).getYearOfCycle().getNumber();
                return new EastAsianYear() {
                    @Override
                    public int getElapsedCyclicYears() {
                        return start + delta + ((delta < 0) ? 2696 : 2636);
                    }
                };
            }
        } else {
            throw new IllegalArgumentException("Chinese era must be related to a Qing dynasty.");
        }

    }

    /**
     * <p>Obtains a year reference for given cycle number (technical identifier). </p>
     *
     * @param   cycle   number of sexagesimal year cycle
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the cycle is smaller than {@code 1}
     */
    /*[deutsch]
     * <p>Liefert eine Jahresreferenz zur angegebenen Jahreszyklusnummer (technisches Kennzeichen). </p>
     *
     * @param   cycle   number of sexagesimal year cycle
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the cycle is smaller than {@code 1}
     */
    public EastAsianYear inCycle(final int cycle) {

        if (cycle < 1) {
            throw new IllegalArgumentException("Cycle number must not be smaller than 1: " + cycle);
        }

        return new EastAsianYear() {
            @Override
            public int getElapsedCyclicYears() {
                return (cycle - 1) * 60 + year - 1;
            }
        };

    }

    @Override
    public int compareTo(CyclicYear other) {

        return (this.year - other.year);

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof CyclicYear) {
            return (this.year == ((CyclicYear) obj).year);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.year;

    }

    @Override
    public String toString() {

        return this.getDisplayName(Locale.ROOT) + "(" + String.valueOf(this.year) + ")";

    }

    /**
     * <p>Parses the given localized name to a cyclic year. </p>
     *
     * @param   text        the text to be parsed
     * @param   pp          when to start parsing
     * @param   locale      language
     * @return  parsed cyclic year or {@code null} if the text cannot be parsed
     */
    /*[deutsch]
     * <p>Interpretiert den sprachabh&auml;ngigen Namen als zyklisches Jahr. </p>
     *
     * @param   text        the text to be parsed
     * @param   pp          when to start parsing
     * @param   locale      language
     * @return  parsed cyclic year or {@code null} if the text cannot be parsed
     */
    static CyclicYear parse(
        CharSequence text,
        ParsePosition pp,
        Locale locale,
        boolean lenient
    ) {

        int pos = pp.getIndex();
        int len = text.length();
        boolean root = locale.getLanguage().isEmpty();

        if ((pos + 1 >= len) ||(pos < 0)) {
            pp.setErrorIndex(pos);
            return null;
        }

        Stem stem = null;
        Branch branch = null;

        if (LANGS_WITHOUT_SEP.contains(locale.getLanguage())) {
            for (Stem s : Stem.values()) {
                if (s.getDisplayName(locale).charAt(0) == text.charAt(pos)) {
                    stem = s;
                    break;
                }
            }
            if (stem != null) {
                for (Branch b : Branch.values()) {
                    if (b.getDisplayName(locale).charAt(0) == text.charAt(pos + 1)) {
                        branch = b;
                        pos += 2;
                        break;
                    }
                }
            }
        } else {
            int sep = -1;

            for (int i = pos + 1; i < len; i++) {
                if (text.charAt(i) == '-') {
                    sep = i;
                    break;
                }
            }

            if (sep == -1) {
                pp.setErrorIndex(pos);
                return null;
            }

            for (Stem s : Stem.values()) {
                String test = s.getDisplayName(locale);
                for (int i = pos; i < sep; i++) {
                    int offset = i - pos;
                    char c = text.charAt(i);
                    if (root) {
                        c = toASCII(c);
                    }
                    if ((offset < test.length()) && (test.charAt(offset) == c)) {
                        if (offset + 1 == test.length()) {
                            stem = s;
                            break;
                        }
                    } else {
                        break; // not found
                    }
                }
            }

            if (stem == null) {
                if (lenient && !root && (sep + 1 < len)) {
                    return parse(text, pp, Locale.ROOT, true); // recursive
                } else {
                    pp.setErrorIndex(pos);
                    return null;
                }
            }

            for (Branch b : Branch.values()) {
                String test = b.getDisplayName(locale);
                for (int i = sep + 1; i < len; i++) {
                    int offset = i - sep - 1;
                    char c = text.charAt(i);
                    if (root) {
                        c = toASCII(c);
                    }
                    if ((offset < test.length()) && (test.charAt(offset) == c)) {
                        if (offset + 1 == test.length()) {
                            branch = b;
                            pos = i + 1;
                            break;
                        }
                    } else {
                        break; // not found
                    }
                }
            }
        }

        if ((stem == null) || (branch == null)) {
            if (lenient && !root) {
                return parse(text, pp, Locale.ROOT, true); // recursive
            } else {
                pp.setErrorIndex(pos);
                return null;
            }
        }

        pp.setIndex(pos);
        return CyclicYear.of(stem, branch);

    }

    /*
            "jiǎ", "yǐ", "bǐng", "dīng", "wù", "jǐ", "gēng", "xīn", "rén", "guǐ"
            "zǐ", "chǒu", "yín", "mǎo", "chén", "sì", "wǔ", "wèi", "shēn", "yǒu", "xū", "hài"
    */
    private static char toASCII(char c) {

        switch (c) {
            case 'ǎ':
            case 'à':
                return 'a';
            case 'ǐ':
            case 'ī':
            case 'í':
            case 'ì':
                return 'i';
            case 'ū':
            case 'ù':
                return 'u';
            case 'ē':
            case 'é':
            case 'è':
                return 'e';
            case 'ǒ':
                return 'o';
            default:
                return c;
        }

    }

    /**
     * @serialData  Checks the consistency of deserialized data and ensures singleton semantic
     * @throws      IllegalArgumentException if the year is not in range 1-60
     */
    private Object readResolve() throws ObjectStreamException {

        return CyclicYear.of(this.year);

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Defines the ten celestial stems. </p>
     */
    /*[deutsch]
     * <p>Definiert die zehn Himmelsst&auml;mme. </p>
     */
    public static enum Stem {

        //~ Statische Felder/Initialisierungen ----------------------------

        JIA_1_WOOD_YANG,

        YI_2_WOOD_YIN,

        BING_3_FIRE_YANG,

        DING_4_FIRE_YIN,

        WU_5_EARTH_YANG,

        JI_6_EARTH_YIN,

        GENG_7_METAL_YANG,

        XIN_8_METAL_YIN,

        REN_9_WATER_YANG,

        GUI_10_WATER_YIN;

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Obtains the localized name of this celestial stem. </p>
         *
         * @param   locale      language
         * @return  display name
         * @see     CyclicYear#getDisplayName(Locale)
         */
        /*[deutsch]
         * <p>Liefert den sprachabh&auml;ngigen Namen dieses Himmelsstamms. </p>
         *
         * @param   locale      language
         * @return  display name
         * @see     CyclicYear#getDisplayName(Locale)
         */
        public String getDisplayName(Locale locale) {

            String lang = locale.getLanguage();
            String[] array = LANG_2_STEM.get(lang.isEmpty() ? "root" : lang);

            if (array == null) {
                array = STEMS_PINYIN;
            }

            return array[this.ordinal()];

        }

    }

    /**
     * <p>Defines the twelvth terrestrial branches with their animal signs (zodiacs). </p>
     */
    /*[deutsch]
     * <p>Definiert die zw&ouml;lf Erdzweige mit ihren Tierkreisverkn&uuml;pfungen. </p>
     */
    public static enum Branch {

        //~ Statische Felder/Initialisierungen ----------------------------

        ZI_1_RAT,

        CHOU_2_OX,

        YIN_3_TIGER,

        MAO_4_HARE,

        CHEN_5_DRAGON,

        SI_6_SNAKE,

        WU_7_HORSE,

        WEI_8_SHEEP,

        SHEN_9_MONKEY,

        YOU_10_FOWL,

        XU_11_DOG,

        HAI_12_PIG;

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Obtains the localized name of this terrestrial branch. </p>
         *
         * @param   locale      language
         * @return  display name
         * @see     CyclicYear#getDisplayName(Locale)
         */
        /*[deutsch]
         * <p>Liefert den sprachabh&auml;ngigen Namen dieses Erdzweigs. </p>
         *
         * @param   locale      language
         * @return  display name
         * @see     CyclicYear#getDisplayName(Locale)
         */
        public String getDisplayName(Locale locale) {

            String lang = locale.getLanguage();
            String[] array = LANG_2_BRANCH.get(lang.isEmpty() ? "root" : lang);

            if (array == null) {
                array = BRANCHES_PINYIN;
            }

            return array[this.ordinal()];

        }

        /**
         * <p>Obtains the animal describing this terrestrial branch. </p>
         *
         * @param   locale      language
         * @return  name of associated zodiac animal
         */
        /*[deutsch]
         * <p>Liefert das chinesische Tierkreiszeichen. </p>
         *
         * @param   locale      language
         * @return  name of associated zodiac animal
         */
        public String getZodiac(Locale locale) {

            String key = "zodiac-" + String.valueOf(this.ordinal() + 1);
            return CalendarText.getInstance("chinese", locale).getTextForms().get(key);

        }

    }

}
