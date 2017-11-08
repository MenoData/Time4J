/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.base.MathUtils;

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
     * <p>This number system is used worldwide. Direct conversion of negative integers is not supported. </p>
     */
    /*[deutsch]
     * Arabische Zahlen mit den Dezimalziffern 0-9 (Standardeinstellung).
     *
     * <p>Dieses Zahlsystem wird weltweit verwendet. Die direkte Konversion von negativen Ganzzahlen
     * wird jedoch nicht unterst&uuml;tzt. </p>
     */
    ARABIC() {
        @Override
        public String toNumeral(int number) {
            if (number < 0) {
                throw new IllegalArgumentException("Cannot convert: " + number);
            }
            return Integer.toString(number);
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            int result = Integer.parseInt(numeral);
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
    },

    /**
     * Arabic-Indic numbers (used in many Arabic countries).
     *
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Arabisch-indische Zahlen (in vielen arabischen L&auml;ndern verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    ARABIC_INDIC() {
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
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Erweiterte arabisch-indische Zahlen (zum Beispiel im Iran).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    ARABIC_INDIC_EXT() {
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
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Bengalii-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    BENGALI() {
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
     * The Devanagari digits used in parts of India.
     *
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Devanagari-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    DEVANAGARI() {
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
     * Note: Must not be negative. </p>
     *
     * @since   3.26/4.22
     */
    /*[deutsch]
     * Zw&ouml;lfersystem, das ein Stellenwertsystem zur Zahlendarstellung mit der Basis 12 darstellt.
     *
     * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Duodezimalsystem">Wikipedia</a>.
     * Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.26/4.22
     */
    DOZENAL() {
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
     * Attention: This enum is not a decimal system. </p>
     */
    /*[deutsch]
     * &Auml;thiopische Numerale (immer positiv).
     *
     * <p>Siehe auch <a href="http://www.geez.org/Numerals/">A Look at Ethiopic Numerals</a>.
     * Achtung: Dieses Enum ist kein Dezimalsystem. </p>
     */
    ETHIOPIC() {
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
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Gujarati-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    GUJARATI() {
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
     * The Japanese numbers limited to the range 1-9999.
     *
     * <p>See also <a href="https://en.wikipedia.org/wiki/Japanese_numerals">Wikipedia</a>. </p>
     *
     * @since   3.32/4.27
     */
    /*[deutsch]
     * Die japanischen Zahlen begrenzt auf den Bereich 1-9999.
     *
     * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Japanische_Zahlschrift">Wikipedia</a>. </p>
     *
     * @since   3.32/4.27
     */
    JAPANESE() {
        @Override
        public String toNumeral(int number) {
            if ((number < 1) || (number > 9999)) {
                throw new IllegalArgumentException("Cannot convert: " + number);
            }
            String digits = this.getDigits();
            int sen = number / 1000;
            int r = number % 1000;
            int hyaku = r / 100;
            r = r % 100;
            int ju = r / 10;
            int n = r % 10;
            StringBuilder numeral = new StringBuilder();
            if (sen >= 1) {
                if (sen > 1) {
                    numeral.append(digits.charAt(sen - 1));
                }
                numeral.append('\u5343');
            }
            if (hyaku >= 1) {
                if (hyaku > 1) {
                    numeral.append(digits.charAt(hyaku - 1));
                }
                numeral.append('\u767e');
            }
            if (ju >= 1) {
                if (ju > 1) {
                    numeral.append(digits.charAt(ju - 1));
                }
                numeral.append('\u5341');
            }
            if (n > 0) {
                numeral.append(digits.charAt(n - 1));
            }
            return numeral.toString();
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            int total = 0;
            int ju = 0;
            int hyaku = 0;
            int sen = 0;
            String digits = this.getDigits();
            for (int i = numeral.length() - 1; i >= 0; i--) {
                char c = numeral.charAt(i);
                switch (c) {
                    case '十':
                        if ((ju == 0) && (hyaku == 0) && (sen == 0)) {
                            ju++;
                        } else {
                            throw new IllegalArgumentException("Invalid Japanese numeral: " + numeral);
                        }
                        break;
                    case '百':
                        if ((hyaku == 0) && (sen == 0)) {
                            hyaku++;
                        } else {
                            throw new IllegalArgumentException("Invalid Japanese numeral: " + numeral);
                        }
                        break;
                    case '千':
                        if (sen == 0) {
                            sen++;
                        } else {
                            throw new IllegalArgumentException("Invalid Japanese numeral: " + numeral);
                        }
                        break;
                    default:
                        boolean ok = false;
                        for (int k = 0; k < 9; k++) {
                            if (digits.charAt(k) == c) {
                                int n = k + 1;
                                if (sen == 1) {
                                    total += (n * 1000);
                                    sen = -1;
                                } else if (hyaku == 1) {
                                    total += (n * 100);
                                    hyaku = -1;
                                } else if (ju == 1) {
                                    total += (n * 10);
                                    ju = -1;
                                } else {
                                    total += n;
                                }
                                ok = true;
                                break;
                            }
                        }
                        if (!ok) { // unknown digit
                            throw new IllegalArgumentException("Invalid Japanese numeral: " + numeral);
                        }
                }
            }
            if (ju == 1) {
                total += 10;
            }
            if (hyaku == 1) {
                total += 100;
            }
            if (sen == 1) {
                total += 1000;
            }
            return total;
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
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Traditionelles Zahlsystem vom Khmer-Volk in Kambodscha verwendet.
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    KHMER() {
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
     * The number system used in Myanmar (Burma).
     *
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Das traditionelle Zahlsystem von Myanmar (Burma).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    MYANMAR() {
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
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.37/4.32
     */
    /*[deutsch]
     * Die Orya-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.37/4.32
     */
    ORYA() {
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
     * <a href="https://en.wikipedia.org/wiki/Roman_numerals">Roman Numerals</a>. </p>
     */
    /*[deutsch]
     * R&ouml;mische Numerale im Wertbereich 1-3999.
     *
     * <p>Wenn die Nachsichtigkeit strikt ist, wird das Interpretieren von r&ouml;mischen Numeralen
     * nur dem modernen Gebrauch folgen. Die Gro&szlig;- und Kleinschreibung spielt keine Rolle. Siehe
     * auch <a href="https://en.wikipedia.org/wiki/Roman_numerals">Roman Numerals</a>. </p>
     */
    ROMAN() {
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
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Telugu-Ziffern (in Teilen von Indien verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    TELUGU() {
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
     * <p>Note: Must not be negative. </p>
     *
     * @since   3.23/4.19
     */
    /*[deutsch]
     * Die Thai-Ziffern (in Thailand verwendet).
     *
     * <p>Hinweis: Darf nicht negativ sein. </p>
     *
     * @since   3.23/4.19
     */
    THAI() {
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

    private static final int[] NUMBERS = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] LETTERS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    private static final int[] D_FACTORS = {1, 12, 144, 1728, 20736};

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
            int delta = this.getDigits().charAt(0) - '0';
            String standard = Integer.toString(number);
            StringBuilder numeral = new StringBuilder();
            for (int i = 0, n = standard.length(); i < n; i++) {
                int codepoint = standard.charAt(i) + delta;
                numeral.append((char) codepoint);
            }
            return numeral.toString();
        } else {
            throw new IllegalArgumentException("Cannot convert: " + number);
        }

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
            int delta = this.getDigits().charAt(0) - '0';
            StringBuilder standard = new StringBuilder();
            for (int i = 0, n = numeral.length(); i < n; i++) {
                int codepoint = numeral.charAt(i) - delta;
                standard.append((char) codepoint);
            }
            int result = Integer.parseInt(standard.toString());
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
     * <p>Note: If letters are used as digits then the upper case will be used. </p>
     *
     * @return  String containing all valid digit characters in ascending order
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Definiert alle g&uuml;ltigen Ziffernsymbole von der kleinsten bis zur
     * gr&ouml;&szlig;ten Ziffer. </p>
     *
     * <p>Hinweis: Wenn Buchstaben als Ziffern verwendet werden, dann wird
     * die Gro&szlig;schreibung angewandt. </p>
     *
     * @return  String containing all valid digit characters in ascending order
     * @since   3.23/4.19
     */
    public String getDigits() {

        throw new AbstractMethodError();

    }

    /**
     * <p>Does this number system describe a decimal system where the digits can be mapped to the range 0-9? </p>
     *
     * @return  boolean
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Beschreibt dieses Zahlensystem ein Dezimalsystem, dessen Ziffern sich auf den Bereich 0-9 abbilden
     * lassen? </p>
     *
     * @return  boolean
     * @since   3.23/4.19
     */
    public boolean isDecimal() {

        throw new AbstractMethodError();

    }

    private static int addEthiopic(
        int total,
        int sum,
        int factor
    ) {

        return MathUtils.safeAdd(total, MathUtils.safeMultiply(sum, factor));

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

}
