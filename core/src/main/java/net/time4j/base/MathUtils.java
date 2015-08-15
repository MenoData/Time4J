/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MathUtils.java) is part of project Time4J.
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

package net.time4j.base;


/**
 * <p>Defines some mathematical routines which are needed in calendrical
 * calculations. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert diverse mathematische Routinen, die in kalendarischen
 * Berechnungen gebraucht werden. </p>
 *
 * @author  Meno Hochschild
 */
public final class MathUtils {

    //~ Konstruktoren -----------------------------------------------------

    private MathUtils() {
        // keine Instanzierung
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Performs a safe type-cast to an int-primitive. </p>
     *
     * @param   num     long-primitive
     * @return  int as type-cast
     * @throws  ArithmeticException if int-range overflows
     */
    /*[deutsch]
     * <p>Macht einen sicheren TypeCast auf ein int-Primitive. </p>
     *
     * @param   num     long-primitive
     * @return  int as type-cast
     * @throws  ArithmeticException if int-range overflows
     */
    public static int safeCast(long num) {

        if (num < Integer.MIN_VALUE || num > Integer.MAX_VALUE) {
            throw new ArithmeticException("Out of range: " + num);
        } else {
            return (int) num;
        }

    }

    /**
     * <p>Sums up the numbers with range check. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  sum
     * @throws  ArithmeticException if int-range overflows
     */
    /*[deutsch]
     * <p>Addiert die Zahlen mit &Uuml;berlaufkontrolle. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  sum
     * @throws  ArithmeticException if int-range overflows
     */
    public static int safeAdd(
        int op1,
        int op2
    ) {

        if (op2 == 0) {
            return op1;
        }

        long result = ((long) op1) + ((long) op2);

        if ((result < Integer.MIN_VALUE) || (result > Integer.MAX_VALUE)) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Integer overflow: (");
            sb.append(op1);
            sb.append(',');
            sb.append(op2);
            sb.append(')');
            throw new ArithmeticException(sb.toString());
        } else {
            return (int) result;
        }

    }

    /**
     * <p>Sums up the numbers with range check. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  sum
     * @throws  ArithmeticException if long-range overflows
     */
    /*[deutsch]
     * <p>Addiert die Zahlen mit &Uuml;berlaufkontrolle. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  sum
     * @throws  ArithmeticException if long-range overflows
     */
    public static long safeAdd(
        long op1,
        long op2
    ) {

        if (op2 == 0L) {
            return op1;
        }

        if (
            (op2 > 0)
            ? (op1 > Long.MAX_VALUE - op2)
            : (op1 < Long.MIN_VALUE - op2)
        ) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Long overflow: (");
            sb.append(op1);
            sb.append(',');
            sb.append(op2);
            sb.append(')');
            throw new ArithmeticException(sb.toString());
        }

        return op1 + op2;

    }

    /**
     * <p>Subtracts the numbers from each other with range check. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  difference
     * @throws  ArithmeticException if int-range overflows
     */
    /*[deutsch]
     * <p>Subtrahiert die Zahlen mit &Uuml;berlaufkontrolle. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  difference
     * @throws  ArithmeticException if int-range overflows
     */
    public static int safeSubtract(
        int op1,
        int op2
    ) {

        if (op2 == 0) {
            return op1;
        }

        long result = ((long) op1) - ((long) op2);

        if ((result < Integer.MIN_VALUE) || (result > Integer.MAX_VALUE)) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Integer overflow: (");
            sb.append(op1);
            sb.append(',');
            sb.append(op2);
            sb.append(')');
            throw new ArithmeticException(sb.toString());
        } else {
            return (int) result;
        }

    }

    /**
     * <p>Subtracts the numbers from each other with range check. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  difference
     * @throws  ArithmeticException if long-range overflows
     */
    /*[deutsch]
     * <p>Subtrahiert die Zahlen mit &Uuml;berlaufkontrolle. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  difference
     * @throws  ArithmeticException if long-range overflows
     */
    public static long safeSubtract(
        long op1,
        long op2
    ) {

        if (op2 == 0L) {
            return op1;
        }

        if (
            (op2 > 0)
            ? (op1 < Long.MIN_VALUE + op2)
            : (op1 > Long.MAX_VALUE + op2)
        ) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Long overflow: (");
            sb.append(op1);
            sb.append(',');
            sb.append(op2);
            sb.append(')');
            throw new ArithmeticException(sb.toString());
        }

        return op1 - op2;

    }

    /**
     * <p>Multiplies the numbers with range check. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  product
     * @throws  ArithmeticException if int-range overflows
     */
    /*[deutsch]
     * <p>Multipliziert die Zahlen mit &Uuml;berlaufkontrolle. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  product
     * @throws  ArithmeticException if int-range overflows
     */
    public static int safeMultiply(
        int op1,
        int op2
    ) {

        if (op2 == 1) {
            return op1;
        }

        long result = ((long) op1) * ((long) op2);

        if ((result < Integer.MIN_VALUE) || (result > Integer.MAX_VALUE)) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Integer overflow: (");
            sb.append(op1);
            sb.append(',');
            sb.append(op2);
            sb.append(')');
            throw new ArithmeticException(sb.toString());
        } else {
            return (int) result;
        }

    }

    /**
     * <p>Multiplies the numbers with range check. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  product
     * @throws  ArithmeticException if long-range overflows
     */
    /*[deutsch]
     * <p>Multipliziert die Zahlen mit &Uuml;berlaufkontrolle. </p>
     *
     * @param   op1     first operand
     * @param   op2     second operand
     * @return  product
     * @throws  ArithmeticException if long-range overflows
     */
    public static long safeMultiply(
        long op1,
        long op2
    ) {

        if (op2 == 1L) {
            return op1;
        }

        if (
            (op2 > 0)
            ? (op1 > Long.MAX_VALUE / op2) || (op1 < Long.MIN_VALUE / op2)
            : ((op2 < -1)
                ? (op1 > Long.MIN_VALUE / op2) || (op1 < Long.MAX_VALUE / op2)
                : (op2 == -1) && (op1 == Long.MIN_VALUE))
        ) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Long overflow: (");
            sb.append(op1);
            sb.append(',');
            sb.append(op2);
            sb.append(')');
            throw new ArithmeticException(sb.toString());
        }

        return op1 * op2;

    }

    /**
     * <p>Inverts the number with range check. </p>
     *
     * @param   value   value to be negated
     * @return  the expression {@code -value}
     * @throws  ArithmeticException if int-range overflows
     */
    /*[deutsch]
     * <p>Pr&uuml;ft auch Extremf&auml;lle beim Negieren. </p>
     *
     * @param   value   value to be negated
     * @return  the expression {@code -value}
     * @throws  ArithmeticException if int-range overflows
     */
    public static int safeNegate(int value) {

        if (value == Integer.MIN_VALUE) {
            throw new ArithmeticException("Not negatable: " + value);
        } else {
            return -value;
        }

    }

    /**
     * <p>Inverts the number with range check. </p>
     *
     * @param   value   value to be negated
     * @return  the expression {@code -value}
     * @throws  ArithmeticException if long-range overflows
     */
    /*[deutsch]
     * <p>Pr&uuml;ft auch Extremf&auml;lle beim Negieren. </p>
     *
     * @param   value   value to be negated
     * @return  the expression {@code -value}
     * @throws  ArithmeticException if long-range overflows
     */
    public static long safeNegate(long value) {

        if (value == Long.MIN_VALUE) {
            throw new ArithmeticException("Not negatable: " + value);
        } else {
            return -value;
        }

    }

    /**
     * <p>Returns the largest lower limit of quotient. </p>
     *
     * <p>Examples: </p>
     *
     * <ul>
     *  <li>{@code floorDivide(2, 2) == 1}</li>
     *  <li>{@code floorDivide(1, 2) == 0}</li>
     *  <li>{@code floorDivide(0, 2) == 0}</li>
     *  <li>{@code floorDivide(-1, 2) == -1}</li>
     *  <li>{@code floorDivide(-2, 2) == -1}</li>
     *  <li>{@code floorDivide(-3, 2) == -2}</li>
     * </ul>
     *
     * @param   value       numerator
     * @param   divisor     divisor
     * @return  quotient as result of division
     */
    /*[deutsch]
     * <p>Liefert die gr&ouml;&szlig;te untere Schranke des Quotienten. </p>
     *
     * <p>Beispiele: </p>
     *
     * <ul>
     *  <li>{@code floorDivide(2, 2) == 1}</li>
     *  <li>{@code floorDivide(1, 2) == 0}</li>
     *  <li>{@code floorDivide(0, 2) == 0}</li>
     *  <li>{@code floorDivide(-1, 2) == -1}</li>
     *  <li>{@code floorDivide(-2, 2) == -1}</li>
     *  <li>{@code floorDivide(-3, 2) == -2}</li>
     * </ul>
     *
     * @param   value       numerator
     * @param   divisor     divisor
     * @return  quotient as result of division
     */
    public static int floorDivide(
        int value,
        int divisor
    ) {

        if (value >= 0) {
            return (value / divisor);
        } else {
            return ((value + 1) / divisor) - 1;
        }

    }

    /**
     * <p>See {@link #floorDivide(int, int)}. </p>
     *
     * @param   value       numerator
     * @param   divisor     divisor
     * @return  quotient as result of division
     */
    /*[deutsch]
     * <p>Siehe {@link #floorDivide(int, int)}. </p>
     *
     * @param   value       numerator
     * @param   divisor     divisor
     * @return  quotient as result of division
     */
    public static long floorDivide(
        long value,
        int divisor
    ) {

        if (value >= 0) {
            return (value / divisor);
        } else {
            return ((value + 1) / divisor) - 1;
        }

    }

    /**
     * <p>Calculates the remainder based on {@link #floorDivide(int, int)}. </p>
     *
     * <p>Examples: </p>
     *
     * <ul>
     *  <li>{@code floorModulo(2, 2) == 0}</li>
     *  <li>{@code floorModulo(1, 2) == 1}</li>
     *  <li>{@code floorModulo(0, 2) == 0}</li>
     *  <li>{@code floorModulo(-1, 2) == 1}</li>
     *  <li>{@code floorModulo(-2, 2) == 0}</li>
     *  <li>{@code floorModulo(-3, 2) == 1}</li>
     * </ul>
     *
     * @param   value       numerator
     * @param   divisor     divisor
     * @return  remainder of division (never negative if divisor is positive)
     */
    /*[deutsch]
     * <p>Modulo-Operator, der den Divisionsrest auf Basis von
     * {@link #floorDivide(int, int)} berechnet. </p>
     *
     * <p>Beispiele: </p>
     *
     * <ul>
     *  <li>{@code floorModulo(2, 2) == 0}</li>
     *  <li>{@code floorModulo(1, 2) == 1}</li>
     *  <li>{@code floorModulo(0, 2) == 0}</li>
     *  <li>{@code floorModulo(-1, 2) == 1}</li>
     *  <li>{@code floorModulo(-2, 2) == 0}</li>
     *  <li>{@code floorModulo(-3, 2) == 1}</li>
     * </ul>
     *
     * @param   value       numerator
     * @param   divisor     divisor
     * @return  remainder of division (never negative if divisor is positive)
     */
    public static int floorModulo(
        int value,
        int divisor
    ) {

        return (value - divisor * (floorDivide(value, divisor)));

    }

    /**
     * <p>See {@link #floorModulo(int, int)}. </p>
     *
     * @param   value       numerator
     * @param   divisor     divisor
     * @return  remainder of division (never negative if divisor is positive)
     */
    /*[deutsch]
     * <p>Siehe {@link #floorModulo(int, int)}. </p>
     *
     * @param   value       numerator
     * @param   divisor     divisor
     * @return  remainder of division (never negative if divisor is positive)
     */
    public static int floorModulo(
        long value,
        int divisor
    ) {

        long ret = (value - divisor * (floorDivide(value, divisor)));
        return (int) ret; // Type-Cast hier wegen modulo-Semantik sicher

    }

}
