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

import de.menodata.annotations4j.Immutable;
import de.menodata.annotations4j.Nullable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;


/**
 * <p>Repr&auml;sentiert eine allgemeine Zeitspanne in mehreren Zeiteinheiten
 * mit deren zugeordneten Betr&auml;gen. </p>
 *
 * @param   <U> generischer Zeiteinheitstyp
 * @author  Meno Hochschild
 */
public interface TimeSpan<U> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert alle enthaltenen Zeitspannenelemente mit Einheit und Betrag
     * in der Reihenfolge von den gr&ouml;&szlig;ten zu den kleinsten und
     * genauesten Zeiteinheiten. </p>
     *
     * @return  unver&auml;nderliche nach der Pr&auml;zision der Einheiten
     *          aufsteigend sortierte Liste, in der jede Zeiteinheit maximal
     *          einmal vorkommt
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
     * @param   unit    zu pr&uuml;fende Zeiteinheit
     * @return  {@code true} wenn enthalten, sonst {@code false}
     * @see     #getPartialAmount(ChronoUnit)
     */
    boolean contains(@Nullable ChronoUnit unit);

    /**
     * <p>Liefert den Teilbetrag zur angegebenen Einheit als Absolutwert. </p>
     *
     * <p>Die Methode liefert {@code 0}, wenn die Zeiteinheit nicht enthalten
     * ist. Um den Gesamtwert der Zeitspanne zu bekommen, ist in der Regel
     * nicht diese Methode, sondern {@link #getTotalLength()} auszuwerten. </p>
     *
     * @param   unit    Zeiteinheit
     * @return  Teilbetrag ({@code >= 0})
     */
    long getPartialAmount(@Nullable ChronoUnit unit);

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
     * @return  {@code true} wenn negativ und nicht leer, sonst {@code false}
     */
    boolean isNegative();

    /**
     * <p>Ist diese Zeitspanne positiv? </p>
     *
     * <p>Eine Zeitspanne ist genau dann positiv, wenn sie weder leer noch
     * negativ ist. </p>
     *
     * @return  {@code true} wenn positiv und nicht leer, sonst {@code false}
     * @see     #isEmpty()
     * @see     #isNegative()
     */
    boolean isPositive();

    /**
     * <p>Liegt eine leere Zeitspanne vor? </p>
     *
     * <p>Per Definition hat eine leere Zeitspanne keine Elemente. </p>
     *
     * @return  {@code true} wenn leer, sonst {@code false}
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
     * @param   <T> generischer Zeitpunkttyp
     * @param   time    Referenzzeitpunkt, zu dem diese Zeitspanne zu
     *                  addieren ist
     * @return  neuer Zeitpunkt als Additionsergebnis
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
     * @param   <T> generischer Zeitpunkttyp
     * @param   time    Referenzzeitpunkt, von dem diese Zeitspanne zu
     *                  subtrahieren ist
     * @return  neuer Zeitpunkt als Subtraktionsergebnis
     * @see     #addTo(TimePoint)
     */
    <T extends TimePoint<? super U, T>> T subtractFrom(T time);

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Repr&auml;sentiert ein atomares Element einer Zeitspanne, das auf nur
     * einer Zeiteinheit beruht und einen positiven Betrag hat. </p>
     *
     * @param   <U> Zeiteinheitstyp
     */
    @Immutable
    public static final class Item<U>
        implements Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 1564804888291509484L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  Zeiteinheit
         */
        private final U unit;

        /**
         * @serial  Betrag zu einer Zeiteinheit {@code > 0}
         */
        private final long amount;

        //~ Konstruktoren -------------------------------------------------

        /**
         * <p>Konstruiert ein neues Zeitspannenelement. </p>
         *
         * @param   amount  Betrag {@code >= 0}
         * @param   unit    Zeiteinheit
         * @throws  IllegalArgumentException wenn der Betrag negativ ist
         */
        public Item(
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
         * <p>Liefert den positiven Betrag. </p>
         *
         * @return  long ({@code >= 0})
         */
        public long getAmount() {

            return this.amount;

        }

        /**
         * <p>Liefert die Zeiteinheit. </p>
         *
         * @return  Zeiteinheit
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
         * @serialData  Pr&uuml;ft die Konsistenz.
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
