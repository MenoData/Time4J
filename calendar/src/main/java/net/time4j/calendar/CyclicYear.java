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

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
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
 * @doctags.concurrency {immutable}
 */
/*[deutsch]
 * <p>Repr&auml;sentiert das zyklische Jahr, das in ostasiatischen Kalendern verwendet wird. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 * @doctags.concurrency {immutable}
 */
public final class CyclicYear
    implements Comparable<CyclicYear>, Serializable {

    //~ Statische Felder/Initialisierungen ------------------------------------

    // https://en.wikipedia.org/wiki/Celestial_stem
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

    private static final Map<String, String[]> LANG_2_STEM;
    private static final Map<String, String[]> LANG_2_BRANCH;
    private static final Set<String> LANGS_WITHOUT_SEP;

    static {
        Map<String, String[]> stems = new HashMap<>();
        stems.put("zh", STEMS_CHINESE);
        stems.put("ja", STEMS_CHINESE);
        stems.put("ko", STEMS_KOREAN);
        stems.put("vi", STEMS_VIETNAMESE);
        stems.put("ru", STEMS_RUSSIAN);
        LANG_2_STEM = Collections.unmodifiableMap(stems);

        Map<String, String[]> branches = new HashMap<>();
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

    private static final long serialVersionUID = 6677607109702604931L;

    //~ Instanzvariablen ------------------------------------------------------

    /**
     * @serial  the number of cyclic year
     */
    private final int year;

    //~ Konstruktoren ---------------------------------------------------------

    private CyclicYear(int year) {
        super();

        if ((year < 1) || (year > 60)) {
            throw new IllegalArgumentException("Out of range: " + year);
        }

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

        return new CyclicYear(yearOfCycle);

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
        return new CyclicYear(yearOfCycle);

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
     * <p>Obtains the localized name of this cyclic year. </p>
     *
     * <p>The original Chinese names are usually not translatable. A few languages like Korean,
     * Vietnamese and Russian use their own transcriptions. The pinyin-transcription (official
     * Chinese romanization) serves as fallback for other languages. </p>
     *
     * @param   locale      language
     * @return  display name
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

        return Integer.hashCode(this.year);

    }

    @Override
    public String toString() {

        return String.valueOf(this.year);

    }

    /**
     * @serialData  Checks the consistency of deserialized data
     * @param       in      object input stream
     * @throws      InvalidObjectException if the year is not in range 1-60
     */
    private void readObject(ObjectInputStream in)
        throws InvalidObjectException {

        if ((year < 1) || (year > 60)) {
            throw new InvalidObjectException("Out of range: " + this.year);
        }

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

            String[] array = LANG_2_STEM.get(locale.getLanguage());

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

            String[] array = LANG_2_BRANCH.get(locale.getLanguage());

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
