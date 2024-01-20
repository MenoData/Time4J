/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2024 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SexagesimalName.java) is part of project Time4J.
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
 * <p>Represents the cyclic sexagesimal names used in East Asian calendars following a 60 unit cycle. </p>
 *
 * <p>It is mainly used for cyclic years. The Chinese calendar also knows cyclic months and days. </p>
 *
 * @author  Meno Hochschild
 * @since   5.7
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den zyklischen sexagesimalen Namen, der in ostasiatischen Kalendern verwendet wird
 * und sich nach 60 Einheiten wiederholt. </p>
 *
 * <p>Wird haupts&auml;chlich im Zusammenhang mit zyklischen Jahren verwendet. Im chinesischen Kalender auch
 * auf Monate und Tage anwendbar. </p>
 *
 * @author  Meno Hochschild
 * @since   5.7
 */
public class SexagesimalName
    implements Comparable<SexagesimalName>, Serializable {

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

    private static final SexagesimalName[] INSTANCES;
    private static final Map<String, String[]> LANG_2_STEM;
    private static final Map<String, String[]> LANG_2_BRANCH;
    private static final Set<String> LANGS_WITHOUT_SEP; // implicit invariant: only one char for stem or branch

    static {
        SexagesimalName[] instances = new SexagesimalName[60];
        for (int i = 0; i < 60; i++) {
            instances[i] = new SexagesimalName(i + 1);
        }
        INSTANCES = instances;

        Map<String, String[]> stems = new HashMap<>();
        stems.put("root", STEMS_SIMPLE);
        stems.put("zh", STEMS_CHINESE);
        stems.put("ja", STEMS_CHINESE);
        stems.put("ko", STEMS_KOREAN);
        stems.put("vi", STEMS_VIETNAMESE);
        stems.put("ru", STEMS_RUSSIAN);
        LANG_2_STEM = Collections.unmodifiableMap(stems);

        Map<String, String[]> branches = new HashMap<>();
        branches.put("root", BRANCHES_SIMPLE);
        branches.put("zh", BRANCHES_CHINESE);
        branches.put("ja", BRANCHES_CHINESE);
        branches.put("ko", BRANCHES_KOREAN);
        branches.put("vi", BRANCHES_VIETNAMESE);
        branches.put("ru", BRANCHES_RUSSIAN);
        LANG_2_BRANCH = Collections.unmodifiableMap(branches);

        Set<String> set = new HashSet<>();
        set.add("zh");
        set.add("ja");
        set.add("ko");
        LANGS_WITHOUT_SEP = Collections.unmodifiableSet(set);
    }

    private static final long serialVersionUID = -4556668597489844917L;

    //~ Instanzvariablen ------------------------------------------------------

    /**
     * @serial  the number of cyclic field (1-60)
     */
    private final int number;

    //~ Konstruktoren ---------------------------------------------------------

    SexagesimalName(int number) {
        super();

        this.number = number;
    }

    //~ Methoden --------------------------------------------------------------

    /**
     * <p>Obtains an instance of cyclic sexagesimal name. </p>
     *
     * @param   numberOfCycle     cyclic number in the range 1-60
     * @return  SexagesimalName
     * @throws  IllegalArgumentException if the parameter is out of range
     */
    /*[deutsch]
     * <p>Liefert eine Instanz eines zyklischen sexagesimalen Namens. </p>
     *
     * @param   numberOfCycle     cyclic number in the range 1-60
     * @return  SexagesimalName
     * @throws  IllegalArgumentException if the parameter is out of range
     */
    public static SexagesimalName of(int numberOfCycle) {

        if ((numberOfCycle < 1) || (numberOfCycle > 60)) {
            throw new IllegalArgumentException("Out of range: " + numberOfCycle);
        }

        return INSTANCES[numberOfCycle - 1];

    }

    /**
     * <p>Obtains an instance of cyclic sexagesimal name. </p>
     *
     * @param   stem    celestial stem
     * @param   branch  terrestrial branch
     * @return  SexagesimalName
     * @throws  IllegalArgumentException if the combination of stem and branch is invalid
     */
    /*[deutsch]
     * <p>Liefert eine Instanz eines zyklischen sexagesimalen Namens. </p>
     *
     * @param   stem    celestial stem
     * @param   branch  terrestrial branch
     * @return  SexagesimalName
     * @throws  IllegalArgumentException if the combination of stem and branch is invalid
     */
    public static SexagesimalName of(
        Stem stem,
        Branch branch
    ) {

        int a = stem.ordinal();
        int b = branch.ordinal();
        int num = (a + 1 + MathUtils.floorModulo(25 * (b - a), 60));
        SexagesimalName sn = SexagesimalName.of(num); // might throw an exception, too

        if ((sn.getStem() == stem) && (sn.getBranch() == branch)) {
            return sn;
        }

        throw new IllegalArgumentException("Invalid combination of stem and branch.");

    }

    /**
     * <p>Obtains the associated cyclic number in the range 1-60. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die zugeordnete zyklische Nummer im Bereich 1-60. </p>
     *
     * @return  int
     */
    public int getNumber() {

        return this.number;

    }

    /**
     * <p>Obtains the celestial stem of this cyclic sexagesimal name. </p>
     *
     * @return  Stem
     */
    /*[deutsch]
     * <p>Liefert den Himmelsstamm dieses zyklischen sexagesimalen Namens. </p>
     *
     * @return  Stem
     */
    public Stem getStem(){

        int n = this.number % 10;

        if (n == 0) {
            n = 10;
        }

        return Stem.values()[n - 1];

    }

    /**
     * <p>Obtains the terrestrial branch of this cyclic sexagesimal name. </p>
     *
     * @return  Branch
     */
    /*[deutsch]
     * <p>Liefert den Erdzweig dieses zyklischen sexagesimalen Namens. </p>
     *
     * @return  Branch
     */
    public Branch getBranch(){

        int n = this.number % 12;

        if (n == 0) {
            n = 12;
        }

        return Branch.values()[n - 1];

    }

    /**
     * <p>Parses the given localized name as a combination of stem and branch to a cyclic sexagesimal name. </p>
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
     * @return  parsed cyclic sexagesimal name
     * @throws  ParseException if the text cannot be parsed
     * @see     #getDisplayName(Locale)
     */
    /*[deutsch]
     * <p>Interpretiert den sprachabh&auml;ngigen Namen als Kombination von Himmelsstamm und Erdzweig
     * zu einem zyklischen sexagesimalen Namen. </p>
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
     * @return  parsed cyclic sexagesimal name
     * @throws  ParseException if the text cannot be parsed
     * @see     #getDisplayName(Locale)
     */
    public static SexagesimalName parse(
        String text,
        Locale locale
    ) throws ParseException {

        ParsePosition pp = new ParsePosition(0);
        SexagesimalName sn = SexagesimalName.parse(text, pp, locale, true);

        if (sn == null) {
            throw new ParseException(text, 0);
        }

        return sn;

    }

    /**
     * <p>Obtains the localized name of this cyclic sexagesimal name. </p>
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
     * <p>Liefert den sprachabh&auml;ngigen Namen dieses zyklischen sexagesimalen Namens. </p>
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
     * <p>Obtains the animal describing this cyclic sexagesimal name. </p>
     *
     * <p>Convenient short form for {@code getBranch().getZodiac(locale)}. </p>
     *
     * @param   locale      language
     * @return  name of associated zodiac animal
     */
    /*[deutsch]
     * <p>Liefert das chinesische Tierkreiszeichen dieses zyklischen sexagesimalen Namens. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r {@code getBranch().getZodiac(locale)}. </p>
     *
     * @param   locale      language
     * @return  name of associated zodiac animal
     */
    public String getZodiac(Locale locale) {

        return this.getBranch().getZodiac(locale);

    }

    /**
     * <p>Rolls this cyclic sexagesimal name by given amount. </p>
     *
     * @param   amount  determines by how many units this instance should be rolled
     * @return  changed copy of this instance
     */
    /*[deutsch]
     * <p>Rollt diesen zyklischen sexagesimalen Namen um den angegebenen Betrag. </p>
     *
     * @param   amount  determines by how many units this instance should be rolled
     * @return  changed copy of this instance
     */
    public SexagesimalName roll(int amount) {

        if (amount == 0) {
            return this;
        }

        return SexagesimalName.of(MathUtils.floorModulo(MathUtils.safeAdd(this.number - 1, amount), 60) + 1);

    }

    @Override
    public int compareTo(SexagesimalName other) {

        if (this.getClass().equals(other.getClass())) {
            return (this.number - SexagesimalName.class.cast(other).number);
        } else {
            throw new ClassCastException("Cannot compare different types.");
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this.getClass().equals(obj.getClass())) {
            return (this.number == ((SexagesimalName) obj).number);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return Integer.hashCode(this.number);

    }

    @Override
    public String toString() {

        return this.getDisplayName(Locale.ROOT) + "(" + String.valueOf(this.number) + ")";

    }

    /**
     * <p>Parses the given localized name to a cyclic sexagesimal name. </p>
     *
     * @param   text        the text to be parsed
     * @param   pp          when to start parsing
     * @param   locale      language
     * @return  parsed cyclic sexagesimal name or {@code null} if the text cannot be parsed
     */
    /*[deutsch]
     * <p>Interpretiert den sprachabh&auml;ngigen Namen als zyklischen sexagesimalen Namen. </p>
     *
     * @param   text        the text to be parsed
     * @param   pp          when to start parsing
     * @param   locale      language
     * @return  parsed cyclic sexagesimal name or {@code null} if the text cannot be parsed
     */
    static SexagesimalName parse(
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
                    return SexagesimalName.parse(text, pp, Locale.ROOT, true); // recursive
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
                return SexagesimalName.parse(text, pp, Locale.ROOT, true); // recursive
            } else {
                pp.setErrorIndex(pos);
                return null;
            }
        }

        pp.setIndex(pos);
        return SexagesimalName.of(stem, branch);

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
     * @throws      IllegalArgumentException if the cyclic number is not in range 1-60
     */
    Object readResolve() throws ObjectStreamException {

        return SexagesimalName.of(this.number);

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
         * @see     SexagesimalName#getDisplayName(Locale)
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
         * @see     SexagesimalName#getDisplayName(Locale)
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
