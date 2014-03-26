/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeSpan.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
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
 * <p>Repr&auml;sentiert eine allgemeine Zeitspanne in mehreren Zeiteinheiten
 * mit deren zugeordneten Betr&auml;gen. </p>
 *
 * @param   <U> generic type of time unit
 * @author  Meno Hochschild
 */
public interface TimeSpan<U> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert alle enthaltenen Zeitspannenelemente mit Einheit und Betrag
     * in der Reihenfolge von den gr&ouml;&szlig;ten zu den kleinsten und
     * genauesten Zeiteinheiten. </p>
     *
     * @return  unmodifiable list sorted by precision of units in ascending
     *          order where every time unit exists at most once
     */
    List<Item<U>> getTotalLength();

    /**
     * <p>Ist die angegebene Zeiteinheit in dieser Zeitspanne enthalten? </p>
     *
     * <p>Standardm&auml;&szlig;ig entspricht die konkrete
     * Implementierung folgendem Ausdruck: </p>
     *
     * <pre>
     *  for (Item&lt;?&gt; item : getTotalLength()) {
     *      if (item.getUnit().equals(unit)) {
     *          return (item.getAmount > 0);
     *      }
     *  }
     *  return false;
     * </pre>
     *
     * @param   unit    time unit to be asked (optional)
     * @return  {@code true} if exists else {@code false}
     * @see     #getPartialAmount(ChronoUnit)
     */
    boolean contains(ChronoUnit unit);

    /**
     * <p>Liefert den Teilbetrag zur angegebenen Einheit als Absolutwert. </p>
     *
     * <p>Die Methode liefert {@code 0}, wenn die Zeiteinheit nicht enthalten
     * ist. Um den Gesamtwert der Zeitspanne zu bekommen, ist in der Regel
     * nicht diese Methode, sondern {@link #getTotalLength()} auszuwerten. </p>
     *
     * @param   unit    time unit (optional)
     * @return  amount as part of time span ({@code >= 0})
     */
    long getPartialAmount(ChronoUnit unit);

    /**
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
     * <p>Liegt eine leere Zeitspanne vor? </p>
     *
     * <p>Per Definition hat eine leere Zeitspanne keine Elemente. </p>
     *
     * @return  {@code true} if empty else {@code false}
     */
    boolean isEmpty();

    /**
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
     * <p>Repr&auml;sentiert ein atomares Element einer Zeitspanne, das auf nur
     * einer Zeiteinheit beruht und einen positiven Betrag hat. </p>
     *
     * @param   <U> type of time unit
     * @concurrency <immutable>
     */
    public static final class Item<U>
        implements Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 1564804888291509484L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  time unit
         */
        private final U unit;

        /**
         * @serial  amount associated with a time unit {@code > 0}
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
         * <p>Konstruiert ein neues Zeitspannenelement. </p>
         *
         * @param   amount  amount in units {@code >= 0}
         * @param   unit    time unit
         * @return  new timespan item
         * @throws  IllegalArgumentException if amount is negative
         */
        public static <U> Item<U> of(
            long amount,
            U unit
        ) {

            return new Item<U>(amount, unit);

        }

        /**
         * <p>Liefert den positiven Betrag. </p>
         *
         * @return  amount in units ({@code >= 0})
         */
        public long getAmount() {

            return this.amount;

        }

        /**
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
