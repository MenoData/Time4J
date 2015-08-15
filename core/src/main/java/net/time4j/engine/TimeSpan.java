/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeSpan.java) is part of project Time4J.
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

package net.time4j.engine;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;


/**
 * <p>Represents a common time span with an associated sign and
 * a sequence of time units and related amounts. </p>
 *
 * @param   <U> generic type of time unit
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine allgemeine vorzeichenbehaftete Zeitspannem
 * in mehreren Zeiteinheiten mit deren zugeordneten Betr&auml;gen. </p>
 *
 * @param   <U> generic type of time unit
 * @author  Meno Hochschild
 */
public interface TimeSpan<U> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields all containted time span items with amount and unit in
     * the order from largest to smallest time units. </p>
     *
     * @return  unmodifiable list sorted by precision of units in ascending
     *          order where every time unit exists at most once
     */
    /*[deutsch]
     * <p>Liefert alle enthaltenen Zeitspannenelemente mit Einheit und Betrag
     * in der Reihenfolge von den gr&ouml;&szlig;ten zu den kleinsten und
     * genauesten Zeiteinheiten. </p>
     *
     * @return  unmodifiable list sorted by precision of units in ascending
     *          order where every time unit exists at most once
     */
    List<Item<U>> getTotalLength();

    /**
     * <p>Queries if given time unit is part of this time span. </p>
     *
     * <p>By default the implementation uses following expression: </p>
     *
     * <pre>
     *  for (Item&lt;?&gt; item : getTotalLength()) {
     *      if (item.getUnit().equals(unit)) {
     *          return (item.getAmount() &gt; 0);
     *      }
     *  }
     *  return false;
     * </pre>
     *
     * @param   unit    time unit to be asked (optional)
     * @return  {@code true} if exists else {@code false}
     * @see     #getPartialAmount(Object) getPartialAmount(U)
     */
    /*[deutsch]
     * <p>Ist die angegebene Zeiteinheit in dieser Zeitspanne enthalten? </p>
     *
     * <p>Standardm&auml;&szlig;ig entspricht die konkrete
     * Implementierung folgendem Ausdruck: </p>
     *
     * <pre>
     *  for (Item&lt;?&gt; item : getTotalLength()) {
     *      if (item.getUnit().equals(unit)) {
     *          return (item.getAmount() &gt; 0);
     *      }
     *  }
     *  return false;
     * </pre>
     *
     * @param   unit    time unit to be asked (optional)
     * @return  {@code true} if exists else {@code false}
     * @see     #getPartialAmount(Object) getPartialAmount(U)
     */
    boolean contains(U unit);

    /**
     * <p>Yields the partial amount associated with given time unit. </p>
     *
     * <p>The method returns {@code 0} if this time span does not contain
     * given time unit. In order to get the total length/amount of this
     * time span users have to evaluate the method {@link #getTotalLength()}
     * instead. </p>
     *
     * @param   unit    time unit (optional)
     * @return  amount as part of time span ({@code >= 0})
     */
    /*[deutsch]
     * <p>Liefert den Teilbetrag zur angegebenen Einheit als Absolutwert. </p>
     *
     * <p>Die Methode liefert {@code 0}, wenn die Zeiteinheit nicht enthalten
     * ist. Um den Gesamtwert der Zeitspanne zu bekommen, ist in der Regel
     * nicht diese Methode, sondern {@link #getTotalLength()} auszuwerten. </p>
     *
     * @param   unit    time unit (optional)
     * @return  amount as part of time span ({@code >= 0})
     */
    long getPartialAmount(U unit);

    /**
     * <p>Queries if this time span is negative. </p>
     *
     * <p>A negative time span relates to the subtraction of two time
     * points where first one is after second one. The partial amounts
     * of every time span are never negative. Hence this attribute is not
     * associated with the partial amounts but only with the time span
     * itself. </p>
     *
     * <p>Note: An empty time span itself is never negative in agreement
     * with the mathematical relation {@code (-1) * 0 = 0}. </p>
     *
     * @return  {@code true} if negative and not empty else {@code false}
     */
    /*[deutsch]
     * <p>Ist diese Zeitspanne negativ? </p>
     *
     * <p>Der Begriff der negativen Zeitspanne bezieht sich auf die Subtraktion
     * zweier Zeitpunkte, von denen der erste vor dem zweiten liegt. Die
     * einzelnen Betr&auml;ge der Zeitspanne sind nie negativ. Dieses Attribut
     * ist daher nicht mit den einzelnen Betr&auml;gen assoziiert, sondern
     * bezieht sich nur auf die Zeitspanne insgesamt. </p>
     *
     * <p>Notiz: Eine leere Zeitspanne ist selbst niemals negativ in
     * &Uuml;bereinstimmung mit der mathematischen Relation
     * {@code (-1) * 0 = 0}. </p>
     *
     * @return  {@code true} if negative and not empty else {@code false}
     */
    boolean isNegative();

    /**
     * <p>Queries if this time span is positive. </p>
     *
     * <p>A time span is positive if it is neither empty nor negative. </p>
     *
     * @return  {@code true} if positive and not empty else {@code false}
     * @see     #isEmpty()
     * @see     #isNegative()
     */
    /*[deutsch]
     * <p>Ist diese Zeitspanne positiv? </p>
     *
     * <p>Eine Zeitspanne ist genau dann positiv, wenn sie weder leer noch
     * negativ ist. </p>
     *
     * @return  {@code true} if positive and not empty else {@code false}
     * @see     #isEmpty()
     * @see     #isNegative()
     */
    boolean isPositive();

    /**
     * <p>Queries if this time span is empty. </p>
     *
     * <p>Per definition an empty time span has no items with a partial
     * amount different from {@code 0}. </p>
     *
     * @return  {@code true} if empty else {@code false}
     */
    /*[deutsch]
     * <p>Liegt eine leere Zeitspanne vor? </p>
     *
     * <p>Per Definition hat eine leere Zeitspanne keine Elemente mit
     * einem von {@code 0} verschiedenen Teilbetrag. </p>
     *
     * @return  {@code true} if empty else {@code false}
     */
    boolean isEmpty();

    /**
     * <p>Adds this time span to given time point. </p>
     *
     * <p>Is equivalent to the expression {@link TimePoint#plus(TimeSpan)
     * time.plus(this)}. Due to better readability usage of the
     * {@code TimePoint}-method is recommended. Implementations are
     * required to document the used algorithm in detailed manner. </p>
     *
     * @param   <T> generic type of time point
     * @param   time    reference time point to add this time span to
     * @return  new time point as result of addition
     * @see     #subtractFrom(TimePoint)
     */
    /*[deutsch]
     * <p>Addiert diese Zeitspanne zum angegebenen Zeitpunkt. </p>
     *
     * <p>Entspricht dem Ausdruck {@link TimePoint#plus(TimeSpan)
     * time.plus(this)}. Aus Gr&uuml;nden des besseren Lesestils empfiehlt
     * sich jedoch meistens die Verwendung der {@code TimePoint}-Methode.
     * Implementierungen m&uuml;ssen den verwendeten Algorithmus genau
     * dokumentieren. </p>
     *
     * @param   <T> generic type of time point
     * @param   time    reference time point to add this time span to
     * @return  new time point as result of addition
     * @see     #subtractFrom(TimePoint)
     */
    <T extends TimePoint<? super U, T>> T addTo(T time);

    /**
     * <p>Subtracts this time span from given time point. </p>
     *
     * <p>Is equivalent to the expression {@link TimePoint#minus(TimeSpan)
     * time.minus(this)}. Due to better readability usage of the
     * {@code TimePoint}-method is recommended. Implementations are
     * required to document the used algorithm in detailed manner. </p>
     *
     * @param   <T> generic type of time point
     * @param   time    reference time point to subtract this time span from
     * @return  new time point as result of subtraction
     * @see     #addTo(TimePoint)
     */
    /*[deutsch]
     * <p>Subtrahiert diese Zeitspanne vom angegebenen Zeitpunkt. </p>
     *
     * <p>Entspricht dem Ausdruck {@link TimePoint#minus(TimeSpan)
     * time.minus(this)}. Aus Gr&uuml;nden des besseren Lesestils empfiehlt
     * sich jedoch meistens die Verwendung der {@code TimePoint}-Methode.
     * Implementierungen m&uuml;ssen den verwendeten Algorithmus genau
     * dokumentieren. </p>
     *
     * @param   <T> generic type of time point
     * @param   time    reference time point to subtract this time span from
     * @return  new time point as result of subtraction
     * @see     #addTo(TimePoint)
     */
    <T extends TimePoint<? super U, T>> T subtractFrom(T time);

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Represents a single item of a time span which is based on only one
     * time unit and has a non-negative amount. </p>
     *
     * @param   <U> type of time unit
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert ein atomares Element einer Zeitspanne, das auf nur
     * einer Zeiteinheit beruht und einen nicht-negativen Betrag hat. </p>
     *
     * @param   <U> type of time unit
     */
    public static final class Item<U>
        implements Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 1564804888291509484L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  time unit
         */
        /*[deutsch]
         * @serial  Zeiteinheit
         */
        private final U unit;

        /**
         * @serial  amount associated with a time unit {@code > 0}
         */
        /*[deutsch]
         * @serial  mit der Zeiteinheit assoziierter Betrag {@code > 0}
         */
        private final long amount;

        //~ Konstruktoren -------------------------------------------------

        private Item(
            long amount,
            U unit
        ) {
            super();

            if (unit == null) {
                throw new NullPointerException("Missing chronological unit.");
            } else if (amount < 0) {
                throw new IllegalArgumentException(
                    "Temporal amount must be positive or zero: " + amount);
            }

            this.amount = amount;
            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Creates a new time span item. </p>
         *
         * @param   <U> type of time unit
         * @param   amount  amount in units {@code >= 0}
         * @param   unit    time unit
         * @return  new time span item
         * @throws  IllegalArgumentException if amount is negative
         */
        /*[deutsch]
         * <p>Konstruiert ein neues Zeitspannenelement. </p>
         *
         * @param   <U> type of time unit
         * @param   amount  amount in units {@code >= 0}
         * @param   unit    time unit
         * @return  new time span item
         * @throws  IllegalArgumentException if amount is negative
         */
        public static <U> Item<U> of(
            long amount,
            U unit
        ) {

            return new Item<U>(amount, unit);

        }

        /**
         * <p>Yields the non-negative amount. </p>
         *
         * @return  amount in units ({@code >= 0})
         */
        /*[deutsch]
         * <p>Liefert den nicht-negativen Betrag. </p>
         *
         * @return  amount in units ({@code >= 0})
         */
        public long getAmount() {

            return this.amount;

        }

        /**
         * <p>Yields the time unit. </p>
         *
         * @return  time unit
         */
        /*[deutsch]
         * <p>Liefert die Zeiteinheit. </p>
         *
         * @return  time unit
         */
        public U getUnit() {

            return this.unit;

        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            } else if (obj instanceof Item) {
                Item<?> that = Item.class.cast(obj);
                return (
                    (this.amount == that.amount)
                    && this.unit.equals(that.unit)
                );
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {

            int hash = this.unit.hashCode();
            hash = 29 * hash + (int) (this.amount ^ (this.amount >>> 32));
            return hash;

        }

        /**
         * <p>Provides a canonical representation in the format
         * 'P' amount '{' unit '}', for example &quot;P4{YEARS}&quot;. </p>
         *
         * @return  String
         */
        /*[deutsch]
         * <p>Liefert eine kanonische Darstellung im Format
         * 'P' amount '{' unit '}', zum Beispiel &quot;P4{YEARS}&quot;. </p>
         *
         * @return  String
         */
        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append('P');
            sb.append(this.amount);
            sb.append('{');
            sb.append(this.unit);
            sb.append('}');
            return sb.toString();

        }

        /**
         * @serialData  Checks the consistency.
         * @param       in      object input stream
         * @throws      InvalidObjectException if the state is inconsistent
         * @throws      ClassNotFoundException if class loading fails
         */
        private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {

            in.defaultReadObject();

            if (
                (this.unit == null)
                || (this.amount < 0)
            ) {
                throw new InvalidObjectException("Inconsistent state.");
            }

        }

    }

}
