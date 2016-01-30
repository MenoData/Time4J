/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

import java.util.Locale;

/**
 * <p>Defines the number system. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Definiert ein Zahlsystem. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public enum NumberSystem {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Arabic numbers with digits 0-9 (default setting).
     */
    /*[deutsch]
     * Arabische Zahlen mit den Ziffern 0-9 (Standardeinstellung).
     */
    ARABIC() {
        @Override
        public String toNumeral(int number) {
            return Integer.toString(number);
        }
        @Override
        public int toInteger(String numeral, Leniency leniency) {
            return Integer.parseInt(numeral);
        }
        @Override
        public boolean contains(char digit) {
            return ((digit >= '0') && (digit <= '9'));
        }
    },

    /**
     * Ethiopic numerals.
     *
     * <p>See also <a href="http://www.geez.org/Numerals/">A Look at Ethiopic Numerals</a>. </p>
     */
    /*[deutsch]
     * &Auml;thiopische Numerale.
     *
     * <p>Siehe auch <a href="http://www.geez.org/Numerals/">A Look at Ethiopic Numerals</a>. </p>
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
                    total = add(total, sum, factor);
                    if (hundred) {
                        factor *= 100;
                    } else {
                        factor *= 10000;
                    }
                    sum = 0;
                    hundred = false;
                    thousand = true;
                } else if (digit == ETHIOPIC_HUNDRED) {
                    total = add(total, sum, factor);
                    factor *= 100;
                    sum = 0;
                    hundred = true;
                    thousand = false;
                }
            }
            if ((hundred || thousand) && (sum == 0)) {
                sum = 1;
            }
            total = add(total, sum, factor);
            return total;
        }
        @Override
        public boolean contains(char digit) {
            return (
                ((digit >= ETHIOPIC_ONE) && (digit <= ETHIOPIC_TEN))
                || (digit == ETHIOPIC_HUNDRED)
                || (digit == ETHIOPIC_TEN_THOUSAND));
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
                                    if ((count > 1) || !isValidCombination(roman, test)) {
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
    };

    private static final char ETHIOPIC_ONE          = 0x1369;
    private static final char ETHIOPIC_TEN          = 0x1372;
    private static final char ETHIOPIC_HUNDRED      = 0x137B;
    private static final char ETHIOPIC_TEN_THOUSAND = 0x137C;

    private static final int[] NUMBERS = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] LETTERS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

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

        throw new AbstractMethodError();

    }

    /**
     * <p>Converts given text numeral to an integer in smart mode. </p>
     *
     * @param   numeral      text numeral to be evaluated as number
     * @return  integer
     * @throws  NumberFormatException if given number has wrong format
     * @throws  ArithmeticException if int-range overflows
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene Numeral zu einer Ganzzahl im SMART-Modus. </p>
     *
     * @param   numeral      text numeral to be evaluated as number
     * @return  integer
     * @throws  NumberFormatException if given number has wrong format
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
     * @throws  NumberFormatException if given number has wrong format
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
     * @throws  NumberFormatException if given number has wrong format
     * @throws  ArithmeticException if int-range overflows
     * @since   3.15/4.12
     */
    public int toInteger(
        String numeral,
        Leniency leniency
    ) {

        throw new AbstractMethodError();

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

        throw new AbstractMethodError();

    }

    private static int add(
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

    private static boolean isValidCombination(
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
