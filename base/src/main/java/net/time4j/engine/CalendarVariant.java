/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarVariant.java) is part of project Time4J.
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

import java.io.Serializable;


/**
 * <p>Represents an immutable calendar variant. </p>
 *
 * <p><strong>Display and change chronological element values</strong></p>
 *
 * <p>The calendar variant consists of chronological elements. This base class
 * delegates the element and time arithmetic to the associated calendar family respective to
 * the underlying rules of elements and units. However, any concrete subclass
 * is required to define the state and reflect it in all {@code get()}-methods
 * and also to specify the serialization behaviour. </p>
 *
 * <p>Element values can only be changed by creating a new immutable copy
 * of the original instance. This is done via all {@code with()}-methods. </p>
 *
 * <p><strong>Calendar system</strong></p>
 *
 * <p>Every calendar variant is a member of a calendar family. That means referring to
 * a calendar system via a variant name. Hence a limited day arithmetic using the
 * class {@code CalendarDays} is always possible. </p>
 *
 * <p><strong>Sorting</strong></p>
 *
 * <p>The sorting algorithm prefers the temporal order then the lexicographical comparison
 * based on variant names. In case of doubt the documentation of the subclass is leading.
 * Alternatively, the interface {@code Temporal} can be used to enable a pure temporal order. </p>
 *
 * <p><strong>Implementation notes</strong></p>
 *
 * <ul>
 *  <li>All subclasses must be <i>final</i> und <i>immutable</i>. </li>
 *  <li>Documentation of supported and registered elements is required. </li>
 *  <li>The natural order should be consistent with {@code equals()}. </li>
 * </ul>
 *
 * @param   <D> generic type of self reference
 * @author  Meno Hochschild
 * @serial  exclude
 * @since   3.4/4.3
 * @see     Chronology
 * @see     CalendarFamily
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine unver&auml;nderlichen Kalendervariante. </p>
 *
 * <p><strong>Chronologische Elementwerte anzeigen und &auml;ndern</strong></p>
 *
 * <p>Der Zeitwert setzt sich aus chronologischen Elementen zusammen. Diese
 * abstrakte Basisklasse delegiert die Zeitrechnung immer an die zugeh&ouml;rige
 * Kalenderfamilie bzw. genauer an die ihr zugeordneten Regeln der Elemente, mu&szlig; aber
 * selbst den Zustand definieren, in den {@code get()}-Methoden den Zustand reflektieren und
 * auch das Serialisierungsverhalten festlegen. </p>
 *
 * <p>Da alle konkreten Implementierungen <i>immutable</i> sind und sein
 * m&uuml;ssen, sind Elementwerte nur dadurch &auml;nderbar, da&szlig; jeweils
 * eine neue Instanz mit ge&auml;nderten Elementwerten erzeugt wird. Das wird
 * unter anderem von allen {@code with()}-Methoden geleistet. </p>
 *
 * <p><strong>Kalendersystem</strong></p>
 *
 * <p>Jede Kalendervariante geh&ouml;rt zu einer Kalenderfamilie. Das schlie&szlig;t den
 * den Bezug zu einem Kalendersystem mit Hilfe eines Variantennamens ein. Daher ist eine
 * begrenzte Zeitarithmetick auf Tageseinheiten basierend immer m&ouml;glich. </p>
 *
 * <p><strong>Sortierung</strong></p>
 *
 * <p>Die Sortierung von Kalendervarianten wird die zeitliche Ordnung bevorzugen
 * und dann die lexikalische Ordnung von Variantennamen. Im Zweifelsfall ist die Dokumentation der
 * konkreten Subklasse ma&szlig;geblich. Alternativ kann auch das Interface {@code Temporal}
 * verwendet werden, um eine rein zeitliche Ordnung zu erm&ouml;glichen. </p>
 *
 * <p><strong>Implementierungshinweise</strong></p>
 *
 * <ul>
 *  <li>Alle Subklassen m&uuml;ssen <i>final</i> und <i>immutable</i> sein. </li>
 *  <li>Es mu&szlig; dokumentiert werden, welche chronologischen Elemente
 *  unterst&uuml;tzt werden bzw. registriert sind. </li>
 *  <li>Die nat&uuml;rliche Ordnung sollte konsistent mit {@code equals()}
 *  sein. </li>
 * </ul>
 *
 * @param   <D> generic type of self reference
 * @author  Meno Hochschild
 * @serial  exclude
 * @since   3.4/4.3
 * @see     Chronology
 * @see     CalendarFamily
 */
public abstract class CalendarVariant<D extends CalendarVariant<D>>
    extends ChronoEntity<D>
    implements CalendarDate, VariantSource, Comparable<D>, Serializable {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a copy of this instance with given variant. </p>
     *
     * <p>If given variant is equal to the variant of this instance
     * then the method will just return this instance. </p>
     *
     * @param   variant     name of new variant
     * @return  copy of this instance with equal epoch-day-value but different variant
     * @throws  ChronoException if given variant is not supported
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Instanz mit der angegebenen Variante. </p>
     *
     * <p>Wenn die angegebene Variante der Variante dieser Instanz gleicht, wird die
     * Methode einfach nur diese Instanz zur&uuml;ckgeben. </p>
     *
     * @param   variant     name of new variant
     * @return  copy of this instance with equal epoch-day-value but different variant
     * @throws  ChronoException if given variant is not supported
     * @since   3.14/4.11
     */
    public D withVariant(String variant) {

        if (variant.equals(this.getVariant())) { // NPE-check
            return this.getContext();
        }

        return this.transform(this.getChronology().getChronoType(), variant);

    }

    /**
     * <p>Equivalent to {@link #withVariant(String) withVariant(variantSource.getVariant())}. </p>
     *
     * @param   variantSource   source of desired calendar variant
     * @return  copy of this instance with equal epoch-day-value but different variant
     * @throws  ChronoException if given variant is not supported
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@link #withVariant(String) withVariant(variantSource.getVariant())}. </p>
     *
     * @param   variantSource   source of desired calendar variant
     * @return  copy of this instance with equal epoch-day-value but different variant
     * @throws  ChronoException if given variant is not supported
     * @since   3.14/4.11
     */
    public D withVariant(VariantSource variantSource) {

        return this.withVariant(variantSource.getVariant());

    }

    /**
     * <p>Compares two calendar variants preferably by their temporal positions
     * on the common date axis and then by their variant names. </p>
     *
     * <p>Implementation note: In order to make the natural order consistent
     * with {@code equals()} the whole state must be taken into account,
     * with preference for those attributes which define the temporal
     * position on the time axis. </p>
     *
     * @param   calendarVariant     the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     * @see     #equals(Object)
     */
    /*[deutsch]
     * <p>Vergleicht zwei Kalendervarianten bevorzugt nach ihrer Position auf der
     * gemeinsamen Zeitachse und dann lexikalisch nach ihren Variantennamen. </p>
     *
     * <p>Implementierungshinweis: Damit die nat&uuml;rliche Ordnung konsistent
     * mit {@code equals()} ist, m&uuml;ssen zum Vergleich alle internen
     * Zustandsattribute herangezogen werden, bevorzugt aber die Attribute,
     * die die zeitliche Position festlegen. </p>
     *
     * @param   calendarVariant     the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     * @see     #equals(Object)
     */
    @Override
    public int compareTo(D calendarVariant) {

        long t1 = this.getDaysSinceEpochUTC();
        long t2 = calendarVariant.getDaysSinceEpochUTC();

        if (t1 < t2) {
            return - 1;
        } else if (t1 > t2) {
            return 1;
        } else {
            return this.getVariant().compareTo(calendarVariant.getVariant());
        }

    }

    @Override
    public boolean isAfter(CalendarDate other) {

        long t1 = this.getDaysSinceEpochUTC();
        long t2 = other.getDaysSinceEpochUTC();
        return (t1 > t2);

    }

    @Override
    public boolean isBefore(CalendarDate other) {

        long t1 = this.getDaysSinceEpochUTC();
        long t2 = other.getDaysSinceEpochUTC();
        return (t1 < t2);

    }

    @Override
    public boolean isSimultaneous(CalendarDate other) {

        long t1 = this.getDaysSinceEpochUTC();
        long t2 = other.getDaysSinceEpochUTC();
        return (t1 == t2);

    }

    /**
     * <p>Adds given calendar days to this instance. </p>
     *
     * @param   days    calendar days to be added
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Kalendertage zu dieser Instanz. </p>
     *
     * @param   days    calendar days to be added
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   3.4/4.3
     */
    public D plus(CalendarDays days) {

        long result = Math.addExact(this.getDaysSinceEpochUTC(), days.getAmount());

        try {
            return this.getCalendarSystem().transform(result);
        } catch (IllegalArgumentException iae) {
            ArithmeticException ex = new ArithmeticException("Out of range: " + result);
            ex.initCause(iae);
            throw ex;
        }

    }

    /**
     * <p>Subtracts given calendar days from this instance. </p>
     *
     * @param   days    calendar days to be subtracted
     * @return  result of subtraction
     * @throws  ArithmeticException in case of numerical overflow
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Kalendertage von dieser Instanz. </p>
     *
     * @param   days    calendar days to be subtracted
     * @return  result of subtraction
     * @throws  ArithmeticException in case of numerical overflow
     * @since   3.4/4.3
     */
    public D minus(CalendarDays days) {

        return this.plus(CalendarDays.of(Math.negateExact(days.getAmount())));

    }

    /**
     * <p>Compares the whole state of this instance with given object. </p>
     *
     * <p>Implementations will usually define their state based on the temporal position
     * and the variant name. Exceptions from this rule should be explicitly documented and reasoned. </p>
     *
     * @see     #compareTo(CalendarVariant)
     */
    /*[deutsch]
     * <p>Vergleicht den gesamten Zustand dieser Instanz mit dem des angegebenen Objekts. </p>
     *
     * <p>Implementierungen werden &uuml;blicherweise ihren Zustand auf Basis der zeitlichen Position
     * und der Variantennamen definieren, da dies am ehesten der Erwartungshaltung der Anwender entspricht.
     * Ausnahmen sind explizit zu dokumentieren und zu begr&uuml;nden. </p>
     *
     * @see     #compareTo(TimePoint)
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * <p>Subclasses must redefine this method corresponding to the
     * behaviour of {@code equals()}. </p>
     */
    /*[deutsch]
     * <p>Subklassen m&uuml;ssen diese Methode passend zum Verhalten
     * von {@code equals()} redefinieren. </p>
     */
    @Override
    public abstract int hashCode();

    /**
     * <p>Provides a complete textual representation of the state of this calendar variant. </p>
     */
    /*[deutsch]
     * <p>Liefert eine vollst&auml;ndige Beschreibung des Zustands dieser Kalendervariante. </p>
     */
    @Override
    public abstract String toString();

    @Override
    public long getDaysSinceEpochUTC() {

        return this.getCalendarSystem().transform(this.getContext());

    }

    /**
     * <p>Returns the assigned calendar family which contains all necessary
     * chronological rules. </p>
     *
     * <p>Concrete subclasses must create in a <i>static initializer</i> a
     * calendar family by help of {@code CalendarFamily.Builder}, keep it as static
     * constant and make it available here. Using the procedure guarantees
     * that a basic set of registered elements and rules will be installed. </p>
     *
     * @return  chronological system as calendar family (never {@code null})
     * @since   3.4/4.3
     * @see     CalendarFamily.Builder
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Kalenderfamilie, die alle notwendigen
     * chronologischen Regeln enth&auml;lt. </p>
     *
     * <p>Konkrete Subklassen m&uuml;ssen in einem <i>static initializer</i>
     * mit Hilfe von {@code CalendarFamily.Builder} eine Kalenderfamilie bauen, in
     * einer eigenen Konstanten halten und hier verf&uuml;gbar machen.
     * &Uuml;ber dieses Verfahren wird zugleich ein Basissatz von Elementen
     * und chronologischen Regeln vorinstalliert. </p>
     *
     * @return  chronological system as calendar family (never {@code null})
     * @since   3.4/4.3
     * @see     CalendarFamily.Builder
     */
    @Override
    protected abstract CalendarFamily<D> getChronology();

    /**
     * <p>Obtains the calendar system of the underlying variant. </p>
     *
     * @return  CalendarSystem
     * @since   5.6
     */
    /*[deutsch]
     * <p>Liefert das Kalendersystem der zugrundeliegenden Variante. </p>
     *
     * @return  CalendarSystem
     * @since   5.6
     */
    protected CalendarSystem<D> getCalendarSystem() {

        return this.getChronology().getCalendarSystem(this.getVariant());

    }

    @SuppressWarnings("unchecked")
    @Override
    <V> ElementRule<D, V> getRule(ChronoElement<V> element) {

        if (element instanceof EpochDays) {
            EpochDays ed = EpochDays.class.cast(element);
            return (ElementRule<D, V>) ed.derive(this.getCalendarSystem());
        } else {
            return super.getRule(element);
        }

    }

}
