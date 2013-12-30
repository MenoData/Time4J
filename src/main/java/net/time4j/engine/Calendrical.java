/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Calendrical.java) is part of project Time4J.
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


/**
 * <p>Abstrakte Basisklasse aller reinen Datumstypen, die &uuml;ber ihre
 * Epochentage ineinander konvertierbar sind. </p>
 *
 * @param   <U> generischer Zeiteinheitstyp (kompatibel zu {@link ChronoUnit})
 * @param   <D> generischer Selbstbezug
 * @author  Meno Hochschild
 */
public abstract class Calendrical<U, D extends Calendrical<U, D>>
    extends TimePoint<U, D>
    implements Temporal<Calendrical<?, ?>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Konvertiert dieses Datum zum angegebenen Zieltyp auf Basis der
     * Anzahl der Tage relativ zur UTC-Epoche [1972-01-01]. </p>
     *
     * <p>Die Konversion findet auf dem lokalen Zeitstrahl um 12 Uhr mittags
     * als angenommener Referenzzeit statt. Diese Referenzzeit stellt sicher,
     * da&szlig; alle Datumstypen konvertierbar bleiben, auch wenn in einem
     * Kalendersystem ein Tag nicht um Mitternacht startet. </p>
     *
     * @param   <T> generischer Zieldatumstyp
     * @param   target  Klasse, zu der dieses Datum konvertiert werden soll
     * @return  konvertiertes Datum
     * @throws  IllegalArgumentException wenn zur Zielklasse keine Chronologie
     *          existiert
     * @throws  ArithmeticException bei &Uuml;berlauf der Epochentage
     * @see     #getEpochDays()
     */
    public <T extends Calendrical<?, T>> T transform(Class<T> target) {

        long epochDays = this.getEpochDays();
        Chronology<T> chronology = Chronology.lookup(target);

        if (chronology == null) {
            // kommt normal nie vor, weil sich jede Chrono selbst registriert
            throw new IllegalArgumentException(
                "Cannot find any chronology for given target type: "
                + target.getName());
        }

        CalendarSystem<T> calsys = chronology.getCalendarSystem();

        if (
            (calsys.getMinimumOfEpochDays() > epochDays)
            || (calsys.getMaximumOfEpochDays() < epochDays)
        ) {
            throw new ArithmeticException(
                "Cannot transform <" + epochDays + "> to: " + target.getName());
        } else {
            return calsys.transform(epochDays);
        }

    }

    @Override
    public boolean isBefore(Calendrical<?, ?> date) {

        return (this.getEpochDays() < date.getEpochDays());

    }

    @Override
    public boolean isAfter(Calendrical<?, ?> date) {

        return (this.getEpochDays() > date.getEpochDays());

    }

    @Override
    public boolean isSimultaneous(Calendrical<?, ?> date) {

        return (this.getEpochDays() == date.getEpochDays());

    }

    /**
     * <p>Definiert eine totale respektive eine nat&uuml;rliche Ordnung. </p>
     *
     * <p>Diese Implementierung wertet die zeitliche Position auf dem
     * gemeinsamen Zeitstrahl aus, also die Epochentage. Nur Datumsobjekte
     * gleichen Kalendertyps k&ouml;nnen miteinander verglichen werden.
     * Die Sortierung ist daher konsistent mit {@code equals()}, solange
     * Subklassen nicht weitere Zustandsattribute definieren. Sollen garantiert
     * Datumsobjekte verschiedenen Typs nur zeitlich verglichen werden, kann
     * entweder eine {@code DayNumberElement}-Instanz als {@code Comparator}
     * oder eine der {@code Temporal}-Methoden {@code isAfter()},
     * {@code isBefore()} und {@code isSimultaneous()} verwendet werden. </p>
     *
     * @param   date    Vergleichsdatum
     * @return  negativer Integer, {@code 0} oder positiver Integer wenn diese
     *          Instanz kleiner, gleich oder gr&ouml;&szlig;er als das Argument
     *          <i>date</i> ist
     * @throws  ClassCastException wenn unterschiedliche Datumstypen vorliegen
     * @see     net.time4j.DayNumberElement#compare
     * @see     #getEpochDays()
     * @see     #isBefore(Calendrical)
     * @see     #isAfter(Calendrical)
     */
    @Override
    public int compareTo(D date) {

        Class<?> t1 = this.getChronology().getChronoType();
        Class<?> t2 = date.getChronology().getChronoType();

        if (t1 != t2) {
            throw new ClassCastException(
                "Cannot compare different types of dates, "
                + "use object of type DayNumberElement as comparator instead.");
        }

        long d1 = this.getEpochDays();
        long d2 = date.getEpochDays();

        return ((d1 < d2) ? -1 : ((d1 == d2) ? 0 : 1));

    }

    /**
     * <p>Basiert auf den Epochentagen und dem Kalendersystem. </p>
     *
     * <p>Mit anderen Worten: Zwei Datumsobjekte sind genau dann gleich, wenn
     * sie zeitlich gleich UND vom selben Kalendertyp sind. Subklassen, die
     * weitere Zustandsattribute definieren, m&uuml;ssen diese Methode
     * geeignet &uuml;berschreiben. </p>
     *
     * <p>Soll ein rein zeitlicher Vergleich sichergestellt sein, dann
     * ist stattdessen die Methode {@link #isSimultaneous(Calendrical)}
     * zu verwenden. </p>
     *
     * @param   obj     Vergleichsobjekt
     * @return  Vergleichsergebnis
     * @see     #getEpochDays()
     * @see     Chronology#getChronoType()
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof Calendrical) {
            Calendrical<?, ?> that = (Calendrical) obj;
            Class<?> t1 = this.getChronology().getChronoType();
            Class<?> t2 = that.getChronology().getChronoType();
            return (
                (t1 == t2)
                && (this.getEpochDays() == that.getEpochDays())
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Basiert auf den Epochentagen. </p>
     *
     * @return  int
     * @see     #getEpochDays()
     */
    @Override
    public int hashCode() {

        long days = this.getEpochDays();
        return (int) (days ^ (days >>> 32));

    }

    /**
     * <p>Ermittelt die Anzahl von Tagen seit dem Beginn der
     * UTC-Epoche [1972-01-01]. </p>
     *
     * <p>Zur Interoperabilit&auml;t mit dem JDK, das millisekundenbasiert
     * arbeitet, kann ein Faktor von {@code 86400 * 1000} nebst dem UTC-Offset
     * von {@code 2 * 365} Tagen verwendet werden. Beispiel: </p>
     *
     * <pre>
     *  IsoDate date = new IsoDate(1970, 1, 1);
     *  long millis =
     *      TimeMath.safeMultiply(date.getEpochDays + 2 * 365, 86400 * 1000);
     *  java.util.Date jud = new java.util.Date(millis);
     * </pre>
     *
     * @return  Anzahl der Tage seit der UTC-Epoche [1972-01-01]
     */
    public long getEpochDays() {

        return this.getCalendarSystem().transform(this.getContext());

    }

    /**
     * <p>Erstellt eine Kopie mit den angegebenen Epochentagen. </p>
     *
     * @param   days    Anzahl der Tage relativ zur UTC-Epoche [1972-01-01]
     * @return  ge&auml;nderte Kopie
     * @see     #getEpochDays()
     */
    public D withEpochDays(long days) {

        return this.getCalendarSystem().transform(days);

    }

    /**
     * <p>Ermittelt den Beginn des Tages. </p>
     *
     * <p>Falls zu einer Datumsangabe eine Uhrzeit hinzukommt, hat diese
     * Methode Einflu&szlig; auf die Konversion zwischen Tagesnummern, die
     * immer zur Referenzzeit von 12 Uhr mittags stattfindet. </p>
     *
     * @return  Verschiebung in Sekunden relativ zu Mitternacht
     * @see     StartOfDay
     */
    public int getStartOfDay() {

        CalendarSystem<D> calsys = this.getCalendarSystem();
        StartOfDay sod = calsys.getStartOfDay();

        if (sod.isFixed()) {
            return sod.getShift(0); // Optimierung
        } else {
            return sod.getShift(calsys.transform(this.getContext()));
        }

    }

    private CalendarSystem<D> getCalendarSystem() {

        return this.getChronology().getCalendarSystem();

    }

}
