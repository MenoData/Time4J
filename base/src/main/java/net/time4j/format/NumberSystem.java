/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2024 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NumberSystem.java) is part of project Time4J.
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

package net.time4j.format;

import java.io.IOException;
import java.util.Locale;


/**
 * <p>Defines the number system. </p>
 *
 * <p>Attention: This enum can only handle non-negative integers. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Definiert ein Zahlsystem. </p>
 *
 * <p>Achtung: Dieses Enum kann nur nicht-negative Ganzzahlen verarbeiten. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public enum NumberSystem {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Arabic numbers with the decimal digits 0-9 (default setting).
     *
     * <p>This number system is used worldwide. Direct conversion of negative integers is not supported.
     * {@link #getCode() Code}: &quot;latn&quot; as inconsistently defined in CLDR (has nothing to do with Latin). </p>
     */
    /*[deutsch]
     * Arabische Zahlen mit den Dezimalziffern 0-9 (Standardeinstellung).
     *
     * <p>Dieses Zahlsystem wird weltweit verwendet. Die direkte Konversion von negativen Ganzzahlen
     * wird jedoch nicht unterst&uuml;tzt. {@link #getCode() Code}: &quot;latn&quot;, inkonsistent in CLDR definiert,
     * weil es mit Latein nichts zu tun hat. </p>
     */
    ARABIC("latn") {
        @Override
        public String toNumeral(int number) {
            if (number < 0) {
                throw new IllegalArgumentException("Cannot convert: " + number);
            }
            return Integer.toString(number);
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            int result = 
                Math.toIntExact(Long.parseLong(numeral));
            if (result < 0) {
                throw new NumberFormatException("Cannot convert negative number: " + numeral);
            }
            return result;
        }
        @Override
        public boolean contains(char digit) {
            return ((digit >= '0') && (digit <= '9'));
        }
        @Override
        public String getDigits() {
            return "0123456789";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
        @Override
        public boolean hasDecimalCodepoints() {
            return true;
        }
    },

    /**
     * Arabic-Indic numbers (used in many Arabic countries).
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;arab&quot; as defined shortened in CLDR. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Arabisch-indische Zahlen (in vielen arabischen L&auml;ndern verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;arab&quot;
     * wie in CLDR verk&uuml;rzt definiert. </p>
     *
     * @since   3.23/4.19
     */
    ARABIC_INDIC("arab") {
        @Override
        public String getDigits() {
            return "\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * Extended Arabic-Indic numbers (used for example in Iran).
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;arabext&quot;. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Erweiterte arabisch-indische Zahlen (zum Beispiel im Iran).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;arabext&quot;. </p>
     *
     * @since   3.23/4.19
     */
    ARABIC_INDIC_EXT("arabext") {
        @Override
        public String getDigits() {
            return "\u06F0\u06F1\u06F2\u06F3\u06F4\u06F5\u06F6\u06F7\u06F8\u06F9";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * The Bengali digits used in parts of India.
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;beng&quot;. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Bengali-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;beng&quot;. </p>
     *
     * @since   3.23/4.19
     */
    BENGALI("beng") {
        @Override
        public String getDigits() {
            return "\u09E6\u09E7\u09E8\u09E9\u09EA\u09EB\u09EC\u09ED\u09EE\u09EF";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * The Chinese decimal system mainly used for years.
     * 
     * <p>Whole numbers will be read as digit by digit. When parsing, the special
     * zero char &quot;〇&quot; will be handled like the default zero char &quot;零&quot;.
     * The method {@code getDigits()} contains both zero char variants. Example:
     * The output of {@code NumberSystem.CHINESE_DECIMAL.toInteger("二零零九")}
     * will be {@code 2009}, the same with the input {@code 二〇〇九}. </p>
     * 
     * <p>Important note: Although this number system can be almost handled like
     * other decimal systems, it is not such one. Its method {@code isDecimal()}
     * will always yield the result {@code false}, because a) the codepoints
     * of all digits are not mappable to the range 0-9 and b) an alternative
     * zero character exists. </p>
     * 
     * <p>Example of usage: </p>
     * 
     * <pre>
     *  ChronoFormatter&lt;PlainDate&gt; f = 
            ChronoFormatter.setUp(PlainDate.axis(), Locale.CHINA)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_DECIMAL)
                .addPattern(&quot;yyyy年M月&quot;, PatternType.CLDR)
                .endSection()
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_MANDARIN)
                .addPattern(&quot;d日&quot;, PatternType.CLDR)
                .endSection()
                .build();
        PlainDate date = f.parse(&quot;二零零九年零一月十三日&quot;); 
        System.out.println(date); // 2009-01-13
     * </pre>
     *
     * <p>Note: Must not be negative. 
     * {@link #getCode() Code}: &quot;hanidec&quot;. </p>
     *
     * @see     #CHINESE_MANDARIN
     * @since   5.9
     */
    /*[deutsch]
     * Das vorwiegend f&uuml;r Jahresangaben verwendete chinesische Dezimalsystem.
     *
     * <p>Ganze Zahlen werden Ziffer f&uuml;r Ziffer gelesen. Beim Interpretieren
     * von Numeralen wird das spezielle Nullzeichen &quot;〇&quot; wie das
     * Standard-Nullzeichen &quot;零&quot; behandelt. Die Methode {@code getDigits()}
     * enth&auml;lt beide Nullzeichen. Beispiel: Das Ergebnis von
     * {@code NumberSystem.CHINESE_DECIMAL.toInteger("二零零九")} wird
     * {@code 2009} sein, dito mit der Eingabe {@code 二〇〇九}. </p>
     *
     * <p>Wichtiger Hinweis: Obwohl sich dieses Zahlsystem fast wie ein
     * Dezimalsystem anf&uuml;hlt, ist es nicht wirklich eins. Seine Methode
     * {@code isDecimal()} wird immer {@code false} liefern, weil erstens die
     * Unicode-Codepoints aller Ziffern sich nicht auf den Bereich 0-9
     * abbilden lassen und zweitens ein alternatives Nullzeichen existiert. </p>
     *
     * <p>Anwendungsbeispiel: </p>
     * 
     * <pre>
     *  ChronoFormatter&lt;PlainDate&gt; f = 
            ChronoFormatter.setUp(PlainDate.axis(), Locale.CHINA)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_DECIMAL)
                .addPattern(&quot;yyyy年M月&quot;, PatternType.CLDR)
                .endSection()
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_MANDARIN)
                .addPattern(&quot;d日&quot;, PatternType.CLDR)
                .endSection()
                .build();
        PlainDate date = f.parse(&quot;二零零九年零一月十三日&quot;); 
        System.out.println(date); // 2009-01-13
     * </pre>
     *
     * <p>Hinweis: Darf nicht negativ sein. 
     * {@link #getCode() Code}: &quot;hanidec&quot;. </p>
     *
     * @see     #CHINESE_MANDARIN
     * @since   5.9
     */
    CHINESE_DECIMAL("hanidec") {
        @Override
        public String toNumeral(int number) {
            String digits = this.getDigits();
            String standard = Integer.toString(number);
            StringBuilder numeral = new StringBuilder();
            
            for (int i = 0, n = standard.length(); i < n; i++) {
                int digit = standard.charAt(i) - '0';
                int index = ((digit == 0) ? 0 : digit + 1);
                numeral.append(digits.charAt(index));
            }
            
            return numeral.toString();
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            String digits = this.getDigits();
            int factor = 1;
            long total = 0L;
            
            for (int i = numeral.length() - 1; i >= 0; i--) {
                char c = numeral.charAt(i);
                if ((c == CHINESE_ZERO_STD) || (c == CHINESE_ZERO_ALT)) {
                    factor *= 10;
                    continue;
                }
                boolean found = false;
                for (int j = digits.length() - 1; j >= 2; j--) {
                    if (digits.charAt(j) == c) {
                        total += ((j - 1) * factor);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    factor *= 10;
                } else {
                    throw new NumberFormatException("Invalid numeral: " + numeral);
                }
            }
            
            return Math.toIntExact(total);
        }
        @Override
        public String getDigits() {
            return "\u96F6\u3007\u4E00\u4E8C\u4E09\u56DB\u4E94\u516D\u4E03\u516B\u4E5D";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
        @Override
        public boolean hasDecimalCodepoints() {
            return false;
        }
    },

    /**
     * The Chinese day counting system used for the day of month in lunar
     * calendar in the range 1-32.
     * 
     * <p>In Chinese the days of the lunar month have special numbering. 
     * Days 1-10 use 初一, 初二, … 初十. For days 21-29 the number is formed
     * using 廿 instead of 二十 to indicate 20. <Strong>Attention:</strong> 
     * It is not usual to apply this numbering style in the context of
     * gregorian calendar where arabic digits are far more appropriate,
     * even in Chinese language. </p>
     * 
     * <p>Note: The numbers 31 and 32 are not used in the Chinese calendar but
     * maybe in other lunar calendars. </p>
     *
     * <p>Example of usage: </p>
     * 
     * <pre>
        ChronoFormatter&lt;ChineseCalendar&gt; formatter =
            ChronoFormatter.setUp(ChineseCalendar.axis(), Locale.CHINA)
                .addPattern(&quot;r(U)MMMM&quot;, PatternType.CLDR_DATE)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_LUNAR_DAYS)
                .addPattern(&quot;d日(&quot;, PatternType.CLDR)
                .endSection()
                .addCustomized( // zodiac printer
                    ChineseCalendar.YEAR_OF_CYCLE, 
                    (CyclicYear year, StringBuilder buffer, AttributeQuery attrs) -&gt; {
                        buffer.append(year.getZodiac(Locale.TRADITIONAL_CHINESE));
                        return Collections.emptySet();
                    },
                    ChronoParser.unsupported())
                .addLiteral(')')
                .build();
        assertThat(
            formatter.format(ChineseCalendar.ofNewYear(2024)),
            is(&quot;2024(甲辰)正月初一日(龍)&quot;));
     * </pre>
     *
     * <p>Note: Must not be negative or zero. 
     * {@link #getCode() Code}: &quot;hanidays&quot;. </p>
     *
     * @see     #CHINESE_MANDARIN
     * @since   5.9.4
     */
    /*[deutsch]
     * Das vorwiegend f&uuml;r Tagesangaben im lunisolaren Kalender verwendete 
     * chinesische Zahlsystem im Bereich 1-32.
     *
     * <p>Die chinesischen Tage des lunaren Monats haben eine besondere
     * Nummerierung. Die Tage 1-10 verwenden 初一, 初二, … 初十, die Tage 21-29 
     * benutzen für die Kombination 二十 (=20) die Abk&uuml;rzung 廿.
     * <Strong>Achtung:</strong> Es ist nicht &uuml;blich, diesen
     * Nummerierungsstil auf den gregorianischen Kalender anzuwenden,
     * nicht einmal in der chinesischen Sprache (stattdessen einfach
     * die arabischen Ziffern 0-9 verwenden). </p>
     *
     * <p>Hinweis: Die Zahlen 31 und 32 werden im chinesischen Kalender nicht
     * gebraucht, aber vielleicht in anderen lunaren Kalendern. </p>
     *
     * <p>Anwendungsbeispiel: </p>
     * 
     * <pre>
        ChronoFormatter&lt;ChineseCalendar&gt; formatter =
            ChronoFormatter.setUp(ChineseCalendar.axis(), Locale.CHINA)
                .addPattern(&quot;r(U)MMMM&quot;, PatternType.CLDR_DATE)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_LUNAR_DAYS)
                .addPattern(&quot;d日(&quot;, PatternType.CLDR)
                .endSection()
                .addCustomized( // zodiac printer
                    ChineseCalendar.YEAR_OF_CYCLE, 
                    (CyclicYear year, StringBuilder buffer, AttributeQuery attrs) -&gt; {
                        buffer.append(year.getZodiac(Locale.TRADITIONAL_CHINESE));
                        return Collections.emptySet();
                    },
                    ChronoParser.unsupported())
                .addLiteral(')')
                .build();
        assertThat(
            formatter.format(ChineseCalendar.ofNewYear(2024)),
            is(&quot;2024(甲辰)正月初一日(龍)&quot;));
     * </pre>
     *
     * <p>Hinweis: Darf nicht negativ oder null sein. 
     * {@link #getCode() Code}: &quot;hanidays&quot;. </p>
     *
     * @see     #CHINESE_MANDARIN
     * @since   5.9.4
     */
    CHINESE_LUNAR_DAYS("hanidays") {
        @Override
        public String toNumeral(int number) {
            if (number < 1 || number > 32) {
                throw new IllegalArgumentException(
                    "Number must be in supported range 1-32: " + number);
            }
            return HANIDAYS[number - 1];
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            for (int i = 0; i < 32; i++) {
                if (HANIDAYS[i].equals(numeral)) {
                    return i + 1;
                }
            }
            throw new IllegalArgumentException(
                "Not a recognized Chinese lunar day number: " + numeral);
        }
        @Override
        public String getDigits() {
            return "\u521D\u4E00\u4E8C\u4E09\u56DB\u4E94\u516D\u4E03\u516B\u4E5D\u5341\u5EFF\u5345";
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * The Chinese numbers in the Mandarin dialect limited to the range 0-9999.
     * 
     * <p>It is not a decimal system but simulates the spoken numbers.
     * The sign &quot;兩&quot; will replace the sign &quot;二&quot; (=2) for
     * all numbers 200 or greater. When parsing, both 2-versions are supported.
     * And the special zero char &quot;〇&quot; will be handled like the default
     * zero char &quot;零&quot;. </p>
     * 
     * <p>Note: This numbering system can also be used in Taiwan because
     * traditional Chinese differs from this system only for numbers at 10,000
     * or above. </p>
     *
     * <p>See also <a href="https://en.wikibooks.org/wiki/Chinese_(Mandarin)/Numbers">Wikibooks</a>.
     * The {@link #getCode() code} is: &quot;mandarin&quot;. </p>
     *
     * @see     #CHINESE_DECIMAL
     * @see     #CHINESE_SIMPLIFIED
     * @since   5.9
     */
    /*[deutsch]
     * Die chinesischen Zahlen im Mandarin-Dialekt begrenzt auf den Bereich 0-9999.
     *
     * <p>Es ist kein Dezimalsystem, simuliert aber die gesprochenen Zahlen.
     * Das Zeichen &quot;兩&quot; ersetzt das Zeichen &quot;二&quot; (=2)
     * f&uuml;r alle Zahlen 200 oder gr&ouml;&szlig;er. Beim Interpretieren
     * von Numeralen werden beide 2-er Zeiche ebenfalls erkannt. Au&szlig;erdem
     * wird das spezielle Nullzeichen &quot;〇&quot; wie das
     * Standard-Nullzeichen &quot;零&quot; behandelt. </p>
     *
     * <p>Hinweis: Dieses System kann auch auf Taiwan verwendet werden, weil
     * es sich von traditionellem Chinesisch nur für Zahlen von 10.000 oder
     * h&ouml;her unterscheidet. </p>
     *
     * <p>Siehe auch <a href="https://en.wikibooks.org/wiki/Chinese_(Mandarin)/Numbers">Wikibooks</a>.
     * Der {@link #getCode() Code} lautet: &quot;mandarin&quot;. </p>
     *
     * @see     #CHINESE_DECIMAL
     * @see     #CHINESE_SIMPLIFIED
     * @since   5.9
     */
    CHINESE_MANDARIN("mandarin") {
        @Override
        public String toNumeral(int number) {
            if (number == 0) {
                return "" + CHINESE_ZERO_STD;
            } else if ((number < 1) || (number > 9999)) {
                throw new IllegalArgumentException("Cannot convert: " + number);
            }
            String digits = CHINESE_DECIMAL.getDigits();
            int qian = number / 1000;
            int r = number % 1000;
            int bai = r / 100;
            r = r % 100;
            int shi = r / 10;
            int n = r % 10;
            StringBuilder numeral = new StringBuilder();
            if (qian >= 1) {
                numeral.append((qian == 2) ? CHINESE_TWO_ALT : digits.charAt(qian + 1));
                numeral.append(CHINESE_THOUSAND);
                if ((bai == 0) && ((shi > 0) || (n > 0))) {
                    numeral.append(CHINESE_ZERO_STD);
                }
            }
            if (bai >= 1) {
                numeral.append((bai == 2) ? CHINESE_TWO_ALT : digits.charAt(bai + 1));
                numeral.append(CHINESE_HUNDRED);
            }
            if ((shi == 0) && !(numeral.length() == 0) && (n > 0)) {
                if (numeral.charAt(numeral.length() - 1) != CHINESE_ZERO_STD) {
                    numeral.append(CHINESE_ZERO_STD); // don't repeat zero char
                }
            } else if ((shi == 1) && (bai == 0) && (qian == 0)) {
                numeral.append(CHINESE_TEN);
            } else if (shi >= 1) {
                numeral.append(digits.charAt(shi + 1));
                numeral.append(CHINESE_TEN);
            }
            if (n > 0) {
                numeral.append(digits.charAt(n + 1));
            }
            return numeral.toString();
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            String num = 
                numeral
                    .replace(CHINESE_ZERO_ALT, CHINESE_ZERO_STD)
                    .replace(CHINESE_TWO_ALT, CHINESE_TWO_STD);
            if ((num.length() == 1) && (num.charAt(0) == CHINESE_ZERO_STD)) {
                return 0;
            }
            int total = 0;
            int shi = 0;
            int bai = 0;
            int qian = 0;
            String digits = CHINESE_DECIMAL.getDigits();
            for (int i = num.length() - 1; i >= 0; i--) {
                char c = num.charAt(i);
                switch (c) {
                    case CHINESE_ZERO_STD:
                        break;
                    case CHINESE_TEN:
                        if ((shi == 0) && (bai == 0) && (qian == 0)) {
                            shi++;
                        } else {
                            throw new IllegalArgumentException("Invalid Chinese numeral: " + numeral);
                        }
                        break;
                    case CHINESE_HUNDRED:
                        if ((bai == 0) && (qian == 0)) {
                            bai++;
                        } else {
                            throw new IllegalArgumentException("Invalid Chinese numeral: " + numeral);
                        }
                        break;
                    case CHINESE_THOUSAND:
                        if (qian == 0) {
                            qian++;
                        } else {
                            throw new IllegalArgumentException("Invalid Chinese numeral: " + numeral);
                        }
                        break;
                    default:
                        boolean ok = false;
                        for (int k = 1; k <= 9; k++) {
                            if (digits.charAt(k + 1) == c) {
                                if (qian == 1) {
                                    total += (k * 1000);
                                    qian = -1;
                                } else if (bai == 1) {
                                    total += (k * 100);
                                    bai = -1;
                                } else if (shi == 1) {
                                    total += (k * 10);
                                    shi = -1;
                                } else {
                                    total += k;
                                }
                                ok = true;
                                break;
                            }
                        }
                        if (!ok) { // unknown digit
                            throw new IllegalArgumentException("Invalid Chinese numeral: " + numeral);
                        }
                }
            }
            if (shi == 1) {
                total += 10;
            }
            if (bai == 1) {
                total += 100;
            }
            if (qian == 1) {
                total += 1000;
            }
            return total;
        }
        @Override
        public String getDigits() {
            return "零〇一二兩三四五六七八九十百千";
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * Like {@code CHINESE_MANDARIN } but with the main difference of printing
     * the zero character as &quot;〇&quot;.
     * 
     * <p>Furthermore, the 2-char in all numbers greater than 199 will always
     * be printed using &quot;二&quot;. </p>
     *
     * <p>The {@link #getCode() code} is: &quot;hans&quot;. </p>
     *
     * @see     #CHINESE_MANDARIN
     * @since   5.9.4
     */
    /*[deutsch]
     * Wie {@code CHINESE_MANDARIN}, aber mit dem Hauptunterschied, da&szlig;
     * als Standardzeichen f&uuml;r die Null &quot;〇&quot; verwendet wird.
     *
     * <p>Au&szlig;erdem werden Zahlen gr&ouml;&szlig;er als 199 f&uuml;r das
     * 2-Zeichen in Hundertern und Tausendern immer das Zeichen &quot;二&quot;
     * verwenden. </p>
     *
     * <p>Der {@link #getCode() Code} lautet: &quot;hans&quot;. </p>
     *
     * @see     #CHINESE_MANDARIN
     * @since   5.9.4
     */
    CHINESE_SIMPLIFIED("hans") {
        @Override
        public String toNumeral(int number) {
            return NumberSystem.CHINESE_MANDARIN
                .toNumeral(number)
                .replace(CHINESE_ZERO_STD, CHINESE_ZERO_ALT)
                .replace(CHINESE_TWO_ALT, CHINESE_TWO_STD);
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            return NumberSystem.CHINESE_MANDARIN.toInteger(numeral, leniency);
        }
        @Override
        public String getDigits() {
            return "零〇一二兩三四五六七八九十百千";
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * The Devanagari digits used in parts of India.
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;deva&quot;. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Devanagari-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;deva&quot;. </p>
     *
     * @since   3.23/4.19
     */
    DEVANAGARI("deva") {
        @Override
        public String getDigits() {
            return "\u0966\u0967\u0968\u0969\u096A\u096B\u096C\u096D\u096E\u096F";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * Dozenal numbers describe a 12-based positional numbering system.
     *
     * <p>See also <a href="https://en.wikipedia.org/wiki/Duodecimal">Wikipedia</a>.
     * Note: Must not be negative. {@link #getCode() Code}: &quot;dozenal&quot; (no CLDR-equivalent). </p>
     *
     * @since   3.26/4.22
     */
    /*[deutsch]
     * Zw&ouml;lfersystem, das ein Stellenwertsystem zur Zahlendarstellung mit der Basis 12 darstellt.
     *
     * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Duodezimalsystem">Wikipedia</a>.
     * Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;dozenal&quot; (kein CLDR-&Auml;quivalent). </p>
     *
     * @since   3.26/4.22
     */
    DOZENAL("dozenal") {
        @Override
        public String toNumeral(int number) {
            if (number < 0) {
                throw new IllegalArgumentException("Cannot convert: " + number);
            }
            return Integer.toString(number, 12).replace('a', '\u218A').replace('b', '\u218B');
        }
        @Override
        public int toNumeral(int number, Appendable buffer) throws IOException {
            if (number >= 0) {
                int count = 0;
                for (int i = 1; i <= 4; i++) {
                    if (number < D_FACTORS[i]) {
                        count = i;
                        break;
                    }
                }
                if (count > 0) {
                    int j = count - 1;
                    do {
                        int q = number / D_FACTORS[j];
                        buffer.append((q == 11) ? '\u218B' : (q == 10 ? '\u218A' : (char) (q + '0')));
                        number -=  q * D_FACTORS[j];
                        j--;
                    } while (j >= 0);
                    return count;
                }
            }
            return super.toNumeral(number, buffer);
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            int result = Integer.parseInt(numeral.replace('\u218A', 'a').replace('\u218B', 'b'), 12);
            if (result < 0) {
                throw new NumberFormatException("Cannot convert negative number: " + numeral);
            }
            return result;
        }
        @Override
        public boolean contains(char digit) {
            return ((digit >= '0') && (digit <= '9')) || (digit == '\u218A') || (digit == '\u218B');
        }
        @Override
        public String getDigits() {
            return "0123456789\u218A\u218B";
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * Ethiopic numerals (always positive).
     *
     * <p>See also <a href="http://www.geez.org/Numerals/">A Look at Ethiopic Numerals</a>.
     * Attention: This enum is not a decimal system. {@link #getCode() Code}: &quot;ethiopic&quot;. </p>
     */
    /*[deutsch]
     * &Auml;thiopische Numerale (immer positiv).
     *
     * <p>Siehe auch <a href="http://www.geez.org/Numerals/">A Look at Ethiopic Numerals</a>.
     * Achtung: Dieses Enum ist kein Dezimalsystem. {@link #getCode() Code}: &quot;ethiopic&quot;. </p>
     */
    ETHIOPIC("ethiopic") {
        @Override
        public String toNumeral(int number) {
            if (number < 1) {
                throw new IllegalArgumentException("Can only convert positive numbers: " + number);
            }

            String value = String.valueOf(number);
            int n = value.length() - 1;

            if ((n % 2) == 0) {
                value = "0" + value;
                n++;
            }

            StringBuilder numeral = new StringBuilder();
            char asciiOne, asciiTen, ethioOne, ethioTen;

            for (int place = n; place >= 0; place--) {
                ethioOne = ethioTen = 0x0;
                asciiTen = value.charAt(n - place);
                place--;
                asciiOne = value.charAt(n - place);

                if (asciiOne != '0') {
                    ethioOne = (char) ((int) asciiOne + (ETHIOPIC_ONE - '1'));
                }

                if (asciiTen != '0') {
                    ethioTen = (char) ((int) asciiTen + (ETHIOPIC_TEN - '1'));
                }

                int pos = (place % 4) / 2;
                char sep = 0x0;

                if (place != 0) {
                    sep = (
                        (pos != 0)
                        ? (((ethioOne != 0x0) || (ethioTen != 0x0)) ? ETHIOPIC_HUNDRED : 0x0)
                        : ETHIOPIC_TEN_THOUSAND);
                }

                if ((ethioOne == ETHIOPIC_ONE) && (ethioTen == 0x0) && (n > 1)) {
                    if ((sep == ETHIOPIC_HUNDRED) || ((place + 1) == n)) {
                        ethioOne = 0x0;
                    }
                }

                if (ethioTen != 0x0) {
                    numeral.append(ethioTen);
                }
                if (ethioOne != 0x0) {
                    numeral.append(ethioOne);
                }
                if (sep != 0x0) {
                    numeral.append(sep);
                }
            }

            return numeral.toString();
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            int total = 0;
            int sum = 0;
            int factor = 1;
            boolean hundred = false;
            boolean thousand = false;
            int n = numeral.length() - 1;

            for (int place = n; place >= 0; place--) {
                char digit = numeral.charAt(place);
                if ((digit >= ETHIOPIC_ONE) && (digit < ETHIOPIC_TEN)) { // 1-9
                    sum += (1 + digit - ETHIOPIC_ONE);
                } else if ((digit >= ETHIOPIC_TEN) && (digit < ETHIOPIC_HUNDRED)) { // 10-90
                    sum += ((1 + digit - ETHIOPIC_TEN) * 10);
                } else if (digit == ETHIOPIC_TEN_THOUSAND) {
                    if (hundred && (sum == 0)) {
                        sum = 1;
                    }
                    total = addEthiopic(total, sum, factor);
                    if (hundred) {
                        factor *= 100;
                    } else {
                        factor *= 10000;
                    }
                    sum = 0;
                    hundred = false;
                    thousand = true;
                } else if (digit == ETHIOPIC_HUNDRED) {
                    total = addEthiopic(total, sum, factor);
                    factor *= 100;
                    sum = 0;
                    hundred = true;
                    thousand = false;
                }
            }
            if ((hundred || thousand) && (sum == 0)) {
                sum = 1;
            }
            total = addEthiopic(total, sum, factor);
            return total;
        }
        @Override
        public boolean contains(char digit) {
            return ((digit >= ETHIOPIC_ONE) && (digit <= ETHIOPIC_TEN_THOUSAND));
        }
        @Override
        public String getDigits() {
            return
                "\u1369\u136A\u136B\u136C\u136D\u136E\u136F\u1370\u1371"
                + "\u1372\u1373\u1374\u1375\u1376\u1377\u1378\u1379\u137A"
                + "\u137B\u137C";
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * The Gujarati digits used in parts of India.
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;gujr&quot;. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Gujarati-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;gujr&quot;. </p>
     *
     * @since   3.23/4.19
     */
    GUJARATI("gujr") {
        @Override
        public String getDigits() {
            return "\u0AE6\u0AE7\u0AE8\u0AE9\u0AEA\u0AEB\u0AEC\u0AED\u0AEE\u0AEF";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * The Gurmukhi digits used mainly by Sikhs in parts of India.
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;guru&quot;. </p>
     *
     * @since   5.9
     */
    /*[deutsch]
     * Die Gurmukhi-Ziffern (besonders von Sikhs in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;guru&quot;. </p>
     *
     * @since   5.9
     */
    GURMUKHI("guru") {
        @Override
        public String getDigits() {
            return "\u0A66\u0A67\u0A68\u0A69\u0A6A\u0A6B\u0A6C\u0A6D\u0A6E\u0A6F";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * The Japanese numbers limited to the range 1-9999.
     *
     * <p>It is not a decimal system but simulates the spoken numbers. See also
     * <a href="https://en.wikipedia.org/wiki/Japanese_numerals">Wikipedia</a>.
     * The {@link #getCode() code} is: &quot;jpan&quot;. </p>
     *
     * @since   3.32/4.27
     */
    /*[deutsch]
     * Die japanischen Zahlen begrenzt auf den Bereich 1-9999.
     *
     * <p>Es ist kein Dezimalsystem und ahmt die gesprochenen Zahlen nach. Siehe
     * auch <a href="https://de.wikipedia.org/wiki/Japanische_Zahlschrift">Wikipedia</a>.
     * Der {@link #getCode() Code} lautet: &quot;jpan&quot;. </p>
     *
     * @since   3.32/4.27
     */
    JAPANESE("jpan") {
        @Override
        public String toNumeral(int number) {
            return japkorToNumeral(number, this.getDigits(), false);
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            return japKorToInteger(numeral, this.getDigits(), leniency, false);
        }
        @Override
        public String getDigits() {
            return "一二三四五六七八九十百千";
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * Traditional number system used by Khmer people in Cambodia.
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;khmr&quot;. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Traditionelles Zahlsystem vom Khmer-Volk in Kambodscha verwendet.
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;khmr&quot;. </p>
     *
     * @since   3.23/4.19
     */
    KHMER("khmr") {
        @Override
        public String getDigits() {
            return "\u17E0\u17E1\u17E2\u17E3\u17E4\u17E5\u17E6\u17E7\u17E8\u17E9";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * The pure Korean numbers in Hangul script limited to the range 1-99.
     *
     * <p>It is not a decimal system and is often used for hour values. See also
     * <a href="https://en.wikipedia.org/wiki/Korean_numerals">Wikipedia</a>.
     * The {@link #getCode() code} is: &quot;korean&quot; (no CLDR-equivalent). </p>
     *
     * @see     #KOREAN_SINO
     * @since   5.9
     */
    /*[deutsch]
     * Die rein koreanischen Zahlen im Hangul-Skript begrenzt auf den Bereich 1-99.
     *
     * <p>Es ist kein Dezimalsystem und wird z.B. f&uuml;r Stundenangaben verwendet. Siehe
     * auch <a href="https://en.wikipedia.org/wiki/Korean_numerals">Wikipedia</a>.
     * Der {@link #getCode() Code} lautet: &quot;korean&quot; (kein CLDR-&Auml;quivalent). </p>
     *
     * @see     #KOREAN_SINO
     * @since   5.9
     */
    KOREAN_NATIVE("korean") {
        @Override
        public String toNumeral(int number) {
            if ((number < 1) || (number > 99)) {
                throw new IllegalArgumentException("Cannot convert: " + number);
            }
            int ten = number / 10;
            int r = number % 10;
            StringBuilder numeral = new StringBuilder();
            if (ten >= 1) {
                numeral.append(KOREAN_NATIVE_NUMBERS[ten + 8]);
            }
            if (r > 0) {
                numeral.append(KOREAN_NATIVE_NUMBERS[r - 1]);
            }
            return numeral.toString();
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            int total = 0;
            String test = numeral;
            for (int i = 0; i < 9; i++) {
                String digit = KOREAN_NATIVE_NUMBERS[i];
                if (test.endsWith(digit)) {
                    int k = test.length() - digit.length();
                    total = i + 1;
                    if (k == 0) {
                        return total;
                    } else {
                        test = test.substring(0, k);
                        break;
                    }
                }
            }
            for (int i = 9; i < 18; i++) {
                String digit = KOREAN_NATIVE_NUMBERS[i];
                if (test.endsWith(digit)) {
                    int k = test.length() - digit.length();
                    test = test.substring(0, k);
                    total += (10 * (i - 8));
                    break;
                }
            }
            if (test.isEmpty()) {
                return total;
            } else {
                throw new IllegalArgumentException("Cannot convert: " + numeral);
            }
        }
        @Override
        public String getDigits() {
            StringBuilder sb = new StringBuilder(18);
            for (String num : KOREAN_NATIVE_NUMBERS) {
                sb.append(num);
            }
            return sb.toString();
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * The Sino-Korean numbers in Hangul script limited to the range 0-9999.
     *
     * <p>It is not really a decimal system but similar to the Japanese system. See
     * also <a href="https://en.wikipedia.org/wiki/Korean_numerals">Wikipedia</a>.
     * Alternative characters like {@code &#xB839;} and {@code &#xACF5;}  for zero or
     * {@code &#xB959;} for the digit six are accepted in parsing, too, if {@code leniency}
     * is not strict. The {@link #getCode() code} is: &quot;koreansino&quot;
     * (no CLDR-equivalent). </p>
     *
     * @see     #KOREAN_NATIVE
     * @since   5.9
     */
    /*[deutsch]
     * Die sino-koreanischen Zahlen im Hangul-Skript begrenzt auf den Bereich 0-9999.
     *
     * <p>Es ist nicht wirklich ein Dezimalsystem, aber &auml;hnlich zu den japanischen Zahlen.
     * Siehe auch <a href="https://en.wikipedia.org/wiki/Korean_numerals">Wikipedia</a>.
     * Alternative Zeichen wie {@code &#xB839;} und {@code &#xACF5;}  f&uuml;r null oder
     * {@code &#xB959;} f&uuml;r die Ziffer sechs werden ebenfalls interpretiert, wenn {@code leniency}
     * nicht strikt ist. Der {@link #getCode() Code} lautet: &quot;koreansino&quot;
     * (kein CLDR-&Auml;quivalent). </p>
     *
     * @see     #KOREAN_NATIVE
     * @since   5.9
     */
    KOREAN_SINO("koreansino") {
        @Override
        public String toNumeral(int number) {
            return japkorToNumeral(number, this.getDigits(), true);
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            return japKorToInteger(numeral, this.getDigits(), leniency, true);
        }
        @Override
        public String getDigits() {
            return "영일이삼사오육칠팔구십백천";
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * Traditional number system used in Laos.
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;laoo&quot;. </p>
     *
     * @since   5.9
     */
    /*[deutsch]
     * Traditionelles Zahlsystem in Laos verwendet.
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;laoo&quot;. </p>
     *
     * @since   5.9
     */
    LAO("laoo") {
        @Override
        public String getDigits() {
            return "\u0ED0\u0ED1\u0ED2\u0ED3\u0ED4\u0ED5\u0ED6\u0ED7\u0ED8\u0ED9";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * The number system used in Myanmar (Burma).
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;mymr&quot;. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Das traditionelle Zahlsystem von Myanmar (Burma).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;mymr&quot;. </p>
     *
     * @since   3.23/4.19
     */
    MYANMAR("mymr") {
        @Override
        public String getDigits() {
            return "\u1040\u1041\u1042\u1043\u1044\u1045\u1046\u1047\u1048\u1049";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * The Orya digits used in parts of India.
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;orya&quot;. </p>
     *
     * @since   3.37/4.32
     */
    /*[deutsch]
     * Die Orya-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;orya&quot;. </p>
     *
     * @since   3.37/4.32
     */
    ORYA("orya") {
        @Override
        public String getDigits() {
            return "\u0B66\u0B67\u0B68\u0B69\u0B6A\u0B6B\u0B6C\u0B6D\u0B6E\u0B6F";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * Roman numerals in range 1-3999.
     *
     * <p>If the leniency is strict then parsing of Roman numerals will only follow modern usage.
     * The parsing is always case-insensitive. See also
     * <a href="https://en.wikipedia.org/wiki/Roman_numerals">Roman Numerals</a>.
     * {@link #getCode() Code}: &quot;roman&quot; (deviation from CLDR). </p>
     */
    /*[deutsch]
     * R&ouml;mische Numerale im Wertbereich 1-3999.
     *
     * <p>Wenn die Nachsichtigkeit strikt ist, wird das Interpretieren von r&ouml;mischen Numeralen
     * nur dem modernen Gebrauch folgen. Die Gro&szlig;- und Kleinschreibung spielt keine Rolle. Siehe
     * auch <a href="https://en.wikipedia.org/wiki/Roman_numerals">Roman Numerals</a>.
     * {@link #getCode() Code}: &quot;roman&quot; (Abweichung von CLDR). </p>
     */
    ROMAN("roman") {
        @Override
        public String toNumeral(int number) {
            if ((number < 1) || (number > 3999)) {
                throw new IllegalArgumentException("Out of range (1-3999): " + number);
            }
            int n = number;
            StringBuilder roman = new StringBuilder();
            for (int i = 0; i < NUMBERS.length; i++) {
                while (n >= NUMBERS[i]) {
                    roman.append(LETTERS[i]);
                    n -= NUMBERS[i];
                }
            }
            return roman.toString();
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            if (numeral.isEmpty()) {
                throw new NumberFormatException("Empty Roman numeral.");
            }
            String ucase = numeral.toUpperCase(Locale.US); // use ASCII-base
            boolean strict = leniency.isStrict();
            int len = numeral.length();
            int i = 0;
            int total = 0;
            while (i < len) {
                char roman = ucase.charAt(i);
                int value = getValue(roman);
                int j = i + 1;
                int count = 1;
                if (j == len) {
                    total += value;
                } else {
                    while (j < len) {
                        char test = ucase.charAt(j);
                        j++;
                        if (test == roman) {
                            count++;
                            if ((count >= 4) && strict) {
                                throw new NumberFormatException(
                                    "Roman numeral contains more than 3 equal letters in sequence: " + numeral);
                            }
                            if (j == len) {
                                total += (value * count);
                            }
                        } else {
                            int next = getValue(test);
                            if (next < value) {
                                total += (value * count);
                                j--;
                            } else { // next > value
                                if (strict) {
                                    if ((count > 1) || !isValidRomanCombination(roman, test)) {
                                        throw new NumberFormatException("Not conform with modern usage: " + numeral);
                                    }
                                }
                                total = total + next - (value * count);
                            }
                            break;
                        }
                    }
                }
                i = j;
            }
            if (total > 3999) {
                throw new NumberFormatException("Roman numbers bigger than 3999 not supported.");
            } else if (strict) {
                if (total >= 900 && ucase.contains("DCD")) {
                    throw new NumberFormatException("Roman number contains invalid sequence DCD.");
                }
                if (total >= 90 && ucase.contains("LXL")) {
                    throw new NumberFormatException("Roman number contains invalid sequence LXL.");
                }
                if (total >= 9 && ucase.contains("VIV")) {
                    throw new NumberFormatException("Roman number contains invalid sequence VIV.");
                }
            }
            return total;
        }
        @Override
        public boolean contains(char digit) {
            char c = Character.toUpperCase(digit);
            return ((c == 'I') || (c == 'V') || (c == 'X') || (c == 'L') || (c == 'C') || (c == 'D') || (c == 'M'));
        }
        @Override
        public String getDigits() {
            return "IVXLCDM";
        }
        @Override
        public boolean isDecimal() {
            return false;
        }
    },

    /**
     * The Telugu digits used in parts of India.
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;telu&quot;. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Telugu-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;telu&quot;. </p>
     *
     * @since   3.23/4.19
     */
    TELUGU("telu") {
        @Override
        public String getDigits() {
            return "\u0C66\u0C67\u0C68\u0C69\u0C6A\u0C6B\u0C6C\u0C6D\u0C6E\u0C6F";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    },

    /**
     * The Thai digits used in Thailand (Siam).
     *
     * <p>Note: Must not be negative. {@link #getCode() Code}: &quot;thai&quot;. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Thai-Ziffern (in Thailand verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. {@link #getCode() Code}: &quot;thai&quot;. </p>
     *
     * @since   3.23/4.19
     */
    THAI("thai") {
        @Override
        public String getDigits() {
            return "\u0E50\u0E51\u0E52\u0E53\u0E54\u0E55\u0E56\u0E57\u0E58\u0E59";
        }
        @Override
        public boolean isDecimal() {
            return true;
        }
    };

    private static final char ETHIOPIC_ONE          = 0x1369; // 1, 2, ..., 8, 9
    private static final char ETHIOPIC_TEN          = 0x1372; // 10, 20, ..., 80, 90
    private static final char ETHIOPIC_HUNDRED      = 0x137B;
    private static final char ETHIOPIC_TEN_THOUSAND = 0x137C;
    
    private static final char CHINESE_TWO_ALT = '\u5169';
    private static final char CHINESE_TWO_STD = '\u4E8C';
    private static final char CHINESE_ZERO_ALT = '\u3007';
    private static final char CHINESE_ZERO_STD = '\u96F6';
    private static final char CHINESE_TEN = '\u5341';
    private static final char CHINESE_HUNDRED = '\u767E';
    private static final char CHINESE_THOUSAND = '\u5343';

    private static final int[] NUMBERS = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] LETTERS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    private static final int[] D_FACTORS = {1, 12, 144, 1728, 20736};
    
    private static final String[] KOREAN_NATIVE_NUMBERS = {
        "하나", "둘", "셋", "넷", "다섯", "여섯", "일곱", "여덟", "아홉",
        "열", "스물", "서른", "마흔", "쉰", "예순", "일흔", "여든", "아흔"
    };
    
    private static final String[] HANIDAYS = new String[]{
        "\u521D\u4E00", "\u521D\u4E8C", "\u521D\u4E09", "\u521D\u56DB", "\u521D\u4E94",
        "\u521D\u516D", "\u521D\u4E03", "\u521D\u516B", "\u521D\u4E5D", "\u521D\u5341",
        "\u5341\u4E00", "\u5341\u4E8C", "\u5341\u4E09", "\u5341\u56DB", "\u5341\u4E94",
        "\u5341\u516D", "\u5341\u4E03", "\u5341\u516B", "\u5341\u4E5D", "\u4E8C\u5341",
        "\u5EFF\u4E00", "\u5EFF\u4E8C", "\u5EFF\u4E09", "\u5EFF\u56DB", "\u5EFF\u4E94",
        "\u5EFF\u516D", "\u5EFF\u4E03", "\u5EFF\u516B", "\u5EFF\u4E5D", "\u4E09\u5341",
        "\u5345\u4E00", "\u5345\u4E8C"
    };    

    //~ Instanzvariablen --------------------------------------------------

    private final String code;

    //~ Konstruktoren -----------------------------------------------------

    private NumberSystem(String code) {
        this.code = code;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Converts given integer to a text numeral. </p>
     *
     * @param   number      number to be displayed as text
     * @return  text numeral
     * @throws  IllegalArgumentException if the conversion is not supported for given number
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Konvertiert die angegebene Zahl zu einem Textnumeral. </p>
     *
     * @param   number      number to be displayed as text
     * @return  text numeral
     * @throws  IllegalArgumentException if the conversion is not supported for given number
     * @since   3.11/4.8
     */
    public String toNumeral(int number) {

        if (this.isDecimal() && (number >= 0)) {
            String digits = this.getDigits();
            
            if (digits.length() == 10) {
                String standard = Integer.toString(number);
                StringBuilder numeral = new StringBuilder();
                
                for (int i = 0, n = standard.length(); i < n; i++) {
                    int digit = standard.charAt(i) - '0';
                    numeral.append(digits.charAt(digit));
                }
                
                return numeral.toString();
            }
        }

        throw new IllegalArgumentException("Cannot convert: " + number);
        
    }

    /**
     * <p>Converts given integer to a text numeral which will then
     * be written into buffer. </p>
     *
     * @param   number      number to be displayed as text
     * @param   buffer      the buffer where any formatted number goes to
     * @return  count of characters written to the buffer
     * @throws  IllegalArgumentException if the conversion is not supported for given number
     * @throws  IOException if writing to the buffer fails
     * @since   3.27/4.22
     */
    /*[deutsch]
     * <p>Konvertiert die angegebene Zahl zu einem Textnumeral, das
     * dann in den Puffer geschrieben wird. </p>
     *
     * @param   number      number to be displayed as text
     * @param   buffer      the buffer where any formatted number goes to
     * @return  count of characters written to the buffer
     * @throws  IllegalArgumentException if the conversion is not supported for given number
     * @throws  IOException if writing to the buffer fails
     * @since   3.27/4.22
     */
    public int toNumeral(
        int number,
        Appendable buffer
    ) throws IOException {

        String digits = this.toNumeral(number);
        buffer.append(digits);
        return digits.length();

    }

    /**
     * <p>Converts given text numeral to an integer in smart mode. </p>
     *
     * @param   numeral      text numeral to be evaluated as number
     * @return  integer
     * @throws  IllegalArgumentException if given number has wrong format
     * @throws  ArithmeticException if int-range overflows
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene Numeral zu einer Ganzzahl im SMART-Modus. </p>
     *
     * @param   numeral      text numeral to be evaluated as number
     * @return  integer
     * @throws  IllegalArgumentException if given number has wrong format
     * @throws  ArithmeticException if int-range overflows
     * @since   3.11/4.8
     */
    public final int toInteger(String numeral) {

        return this.toInteger(numeral, Leniency.SMART);

    }

    /**
     * <p>Converts given text numeral to an integer. </p>
     *
     * <p>In most cases, the leniency will not be taken into account, but parsing of some odd roman numerals
     * can be enabled in non-strict mode (for example: IIXX instead of XVIII). </p>
     *
     * @param   numeral     text numeral to be evaluated as number
     * @param   leniency    determines how lenient the parsing of given numeral should be
     * @return  integer
     * @throws  IllegalArgumentException if given number has wrong format
     * @throws  ArithmeticException if int-range overflows
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene Numeral zu einer Ganzzahl. </p>
     *
     * <p>In den meisten F&auml;llen wird das Nachsichtigkeitsargument nicht in Betracht gezogen. Aber die
     * Interpretation von nicht dem modernen Gebrauch entsprechenden r&ouml;mischen Numeralen kann im
     * nicht-strikten Modus erfolgen (zum Beispiel IIXX statt XVIII). </p>
     *
     * @param   numeral     text numeral to be evaluated as number
     * @param   leniency    determines how lenient the parsing of given numeral should be
     * @return  integer
     * @throws  IllegalArgumentException if given number has wrong format
     * @throws  ArithmeticException if int-range overflows
     * @since   3.15/4.12
     */
    public int toInteger(
        String numeral,
        Leniency leniency
    ) {

        if (this.isDecimal()) {
            String digits = this.getDigits();
            StringBuilder standard = new StringBuilder();
            
            for (int i = 0, n = numeral.length(); i < n; i++) {
                char c = numeral.charAt(i);
                boolean found = false;
                for (int j = digits.length() - 1; j >= 0; j--) {
                    if (digits.charAt(j) == c) {
                        char digit = (char) (j + '0');
                        standard.append(digit);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new NumberFormatException("Invalid numeral: " + numeral);
                }
            }
            
            int result = 
                Math.toIntExact(Long.parseLong(standard.toString()));
            
            if (result < 0) {
                throw new NumberFormatException("Cannot convert negative number: " + numeral);
            }
            
            return result;
        } else {
            throw new NumberFormatException("Cannot convert: " + numeral);
        }

    }

    /**
     * <p>Does this number system contains given digit char? </p>
     *
     * @param   digit   numerical char to be checked
     * @return  boolean
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Enth&auml;lt dieses Zahlensystem die angegebene Ziffer? </p>
     *
     * @param   digit   numerical char to be checked
     * @return  boolean
     * @since   3.11/4.8
     */
    public boolean contains(char digit) {

        String digits = this.getDigits();

        for (int i = 0, n = digits.length(); i < n; i++) {
            if (digits.charAt(i) == digit) {
                return true;
            }
        }

        return false;

    }

    /**
     * <p>Defines all digit characters from the smallest to the largest one. </p>
     *
     * <p>Note: If letters are used as digits then the upper case will be used. 
     * Pure decimal systems always use 10 digits here from zero to nine in
     * ascending order. </p>
     *
     * @return  String containing all valid digit characters in ascending order
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Definiert alle g&uuml;ltigen Ziffernsymbole von der kleinsten bis zur
     * gr&ouml;&szlig;ten Ziffer. </p>
     *
     * <p>Hinweis: Wenn Buchstaben als Ziffern verwendet werden, dann wird
     * die Gro&szlig;schreibung angewandt. Reine Dezimalsysteme verwenden hier
     * immer genau 10 Ziffern von 0 bis 9 in aufsteigender Ordnung. </p>
     *
     * @return  String containing all valid digit characters in ascending order
     * @since   3.23/4.19
     */
    public String getDigits() {

        throw new AbstractMethodError();

    }

    /**
     * <p>Does this number system describe a decimal system where the digits 
     * can be mapped to the range 0-9? </p>
     * 
     * <p>Every decimal system defines the first character of the result of
     * {@link #getDigits()} as zero character. </p>
     *
     * @return  boolean
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Beschreibt dieses Zahlensystem ein Dezimalsystem, dessen Ziffern
     * sich auf den Bereich 0-9 abbilden lassen? </p>
     *
     * <p>Jedes Dezimalsystem definiert das erste Zeichen des Ergebnisses
     * von {@link #getDigits()} als Nullzeichen. </p>
     *
     * @return  boolean
     * @since   3.23/4.19
     */
    public boolean isDecimal() {

        throw new AbstractMethodError();

    }

    /**
     * <p>Does this number system describe a decimal system where all 
     * associated code points can be mapped to the range 0-9? </p>
     * 
     * <p>There must be exactly 10 digit characters whose code points are 
     * in same numerical order from 0 to 9, each with step width 1. The
     * default implementation just delegates to {@link #isDecimal()}. </p>
     *
     * @return  boolean
     * @since   5.9
     */
    /*[deutsch]
     * <p>Beschreibt dieses Zahlensystem ein Dezimalsystem, dessen Ziffern und 
     * Codepoints sich auf den Bereich 0-9 abbilden lassen? </p>
     *
     * <p>Es m&uuml;ssen genau 10 Zahlzeichen vorhanden sein, deren Unicode-
     * Codepoints mit der Schrittweite von 1 aufsteigend sortiert sind. Die
     * Standardimplementierung delegiert an {@link #isDecimal()}. </p>
     *
     * @return  boolean
     * @since   5.9
     */
    public boolean hasDecimalCodepoints() {

        return this.isDecimal();

    }

    /**
     * <p>Obtains an identifier which can be used together with the unicode extension &quot;nu&quot;
     * in {@code Locale}-parameters. </p>
     *
     * <p>Example: {@code Locale.forLanguageTag("th-TH-u-nu-" + NumberSystem.THAI.getCode())}
     * would set the Thai-number-system where the code &quot;thai&quot; is used. </p>
     *
     * @return  unicode extension identifier (in most cases defined like in CLDR)
     * @since   4.24
     */
    /*[deutsch]
     * <p>Liefert ein Kennzeichen, das zusammen mit der Unicode-Erweiterung &quot;nu&quot;
     * in {@code Locale}-Parametern verwendet werden kann. </p>
     *
     * <p>Beispiel: {@code Locale.forLanguageTag("th-TH-u-nu-" + NumberSystem.THAI.getCode())} w&uuml;rde
     * das Thai-Zahlensystem setzen, indem der Code &quot;thai&quot; verwendet wird. </p>
     *
     * @return  unicode extension identifier (in most cases defined like in CLDR)
     * @since   4.24
     */
    public String getCode() {

        return this.code;

    }

    private static int addEthiopic(
        int total,
        int sum,
        int factor
    ) {

        return Math.addExact(total, Math.multiplyExact(sum, factor));

    }

    private static int getValue(char roman) {

        switch (roman) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
            default:
                throw new NumberFormatException("Invalid Roman digit: " + roman);
        }

    }

    private static boolean isValidRomanCombination(
        char previous,
        char next
    ) {

        switch (previous) {
            case 'C':
                return ((next == 'M') || (next == 'D'));
            case 'X':
                return ((next == 'C') || (next == 'L'));
            case 'I':
                return ((next == 'X') || (next == 'V'));
            default:
                return false;
        }

    }

    private static String japkorToNumeral(
        int number,
        String digits,
        boolean hasZero
    ) {
        
        if (hasZero && (number == 0)) {
            return String.valueOf(digits.charAt(0));
        } else if ((number < 1) || (number > 9999)) {
            throw new IllegalArgumentException("Cannot convert: " + number);
        }

        int thousand = number / 1000;
        int r = number % 1000;
        int hundred = r / 100;
        r = r % 100;
        int ten = r / 10;
        int n = r % 10;
        int len = digits.length();
        StringBuilder numeral = new StringBuilder();
        int offset = (hasZero ? 0 : 1);

        if (thousand >= 1) {
            if (thousand > 1) {
                numeral.append(digits.charAt(thousand - offset));
            }
            numeral.append(digits.charAt(len - 1)); // 千
        }
        
        if (hundred >= 1) {
            if (hundred > 1) {
                numeral.append(digits.charAt(hundred - offset));
            }
            numeral.append(digits.charAt(len - 2)); // 百
        }
        
        if (ten >= 1) {
            if (ten > 1) {
                numeral.append(digits.charAt(ten - offset));
            }
            numeral.append(digits.charAt(len - 3)); // 十
        }
        
        if (n > 0) {
            numeral.append(digits.charAt(n - offset));
        }
        
        return numeral.toString();
        
    }

    private static int japKorToInteger(
        String numeral, 
        String digits,
        Leniency leniency,
        boolean korean
    ) {
        
        int total = 0;
        int ten = 0;
        int hundred = 0;
        int thousand = 0;
        int len = digits.length();
        boolean error = false;
        
        for (int i = numeral.length() - 1; i >= 0; i--) {
            char c = numeral.charAt(i);
            
            if (!leniency.isStrict() && (c == '\uB959')) {
                c = '\uC721'; // tolerant parsing of alternative Hangul char 육 (=6)
            }
            
            if (c == digits.charAt(len - 3)) { // 十
                if ((ten == 0) && (hundred == 0) && (thousand == 0)) {
                    ten++;
                } else {
                    error = true;
                }
            } else if (c == digits.charAt(len - 2)) { // 百
                if ((hundred == 0) && (thousand == 0)) {
                    hundred++;
                } else {
                    error = true;
                }
            } else if (c == digits.charAt(len - 1)) { // 千
                if (thousand == 0) {
                    thousand++;
                } else {
                    error = true;
                }
            } else if (korean && (c == '\uC601')) {
                if (numeral.length() == 1) {
                    return 0;
                } else {
                    error = true;
                }
            } else if (
                korean 
                && !leniency.isStrict() 
                && ((c == '\uB839') || (c == '\uACF5')) // zero alternatives: 령 or 공
            ) {
                if (numeral.length() == 1) {
                    return 0;
                } else {
                    error = true;
                }
            } else {
                error = true;
                int offset = (korean ? 1 : 0);
                
                for (int k = 0; k < 9; k++) {
                    if (digits.charAt(k + offset) == c) {
                        int n = k + 1;
                        if (thousand == 1) {
                            total += (n * 1000);
                            thousand = -1;
                        } else if (hundred == 1) {
                            total += (n * 100);
                            hundred = -1;
                        } else if (ten == 1) {
                            total += (n * 10);
                            ten = -1;
                        } else {
                            total += n;
                        }
                        error = false;
                        break;
                    }
                }
            }
            
            if (error) {
                throw new IllegalArgumentException("Invalid numeral: " + numeral);
            }
        }
        
        if (ten == 1) {
            total += 10;
        }
        
        if (hundred == 1) {
            total += 100;
        }
        
        if (thousand == 1) {
            total += 1000;
        }
        
        return total;
        
    }

}
