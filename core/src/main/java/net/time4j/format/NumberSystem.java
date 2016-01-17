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
        public int toInteger(String numeral) {
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
        public int toInteger(String numeral) {
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
            return ((digit >= ETHIOPIC_ONE) && (digit <= ETHIOPIC_TEN_THOUSAND));
        }
    };

    private static final char ETHIOPIC_ONE          = 0x1369;
    private static final char ETHIOPIC_TEN          = 0x1372;
    private static final char ETHIOPIC_HUNDRED      = 0x137B;
    private static final char ETHIOPIC_TEN_THOUSAND = 0x137C;

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
     * <p>Converts given text numeral to an integer. </p>
     *
     * @param   numeral      text numeral to be evaluated as number
     * @return  integer
     * @throws  ArithmeticException if int-range overflows
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene Numeral zu einer Ganzzahl. </p>
     *
     * @param   numeral      text numeral to be evaluated as number
     * @return  integer
     * @throws  ArithmeticException if int-range overflows
     * @since   3.11/4.8
     */
    public int toInteger(String numeral) {

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

}
