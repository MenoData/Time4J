/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricDate.java) is part of project Time4J.
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

package net.time4j.history;

import net.time4j.base.GregorianMath;


/**
 * <p>Defines a historic date which consists of era, year of era, month and day of month. </p>
 *
 * <p>Instances of this class are agnostic of any specific new-year-strategy other than the
 * {@link NewYearRule#BEGIN_OF_JANUARY default (first of January)} but can use such a strategy
 * as parameter to determine {@link #getYearOfEra(NewYearStrategy) the real historic year}
 * for display purposes. </p>
 *
 * <p>The natural order is based on the timeline order. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Definiert ein historisches Datum, das aus &Auml;ra, Jahr der &Auml;ra, Monat und Tag
 * des Monats besteht. </p>
 *
 * <p>Instanzen dieser Klasse kennen selber keine andere Neujahrsstrategie als den
 * {@link NewYearRule#BEGIN_OF_JANUARY Standard (erster Januar)}, k&ouml;nnen aber eine
 * solche Strategie als Parameter nutzen, um {@link #getYearOfDisplay(NewYearStrategy)
 * das reale historische Jahr} f&uuml;r Anzeigezwecke zu bestimmen. </p>
 *
 * <p>Die nat&uuml;rliche Ordnung basiert auf der zeitlichen Reihenfolge. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
public final class HistoricDate
    implements Comparable<HistoricDate> {

    //~ Instanzvariablen --------------------------------------------------

    private final HistoricEra era;
    private final int yearOfEra;
    private final int month;
    private final int dom;

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    HistoricDate(
        HistoricEra era,
        int yearOfEra,
        int month,
        int dom
    ) {
        super();

        this.era = era;
        this.yearOfEra = yearOfEra;
        this.month = month;
        this.dom = dom;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Constructs a new tuple of given historic chronological components. </p>
     *
     * <p>Note: A detailed validation is not done. Such a validation is the responsibility
     * of any {@code ChronoHistory}, however. The converted AD-year must not be beyond the
     * defined range of the class {@code PlainDate}. </p>
     *
     * @param   era         historic era
     * @param   yearOfEra   year of related era ({@code >= 1}) starting on January the first
     * @param   month       historic month (1-12)
     * @param   dom         historic day of month (1-31)
     * @return  new historic date (not yet validated)
     * @throws  IllegalArgumentException if any argument is out of required maximum range
     * @since   3.0
     * @see     ChronoHistory#isValid(HistoricDate)
     * @see     GregorianMath#MIN_YEAR
     * @see     GregorianMath#MAX_YEAR
     */
    /*[deutsch]
     * <p>Konstruiert ein neues Tupel aus den angegebenen historischen Zeitkomponenten. </p>
     *
     * <p>Hinweis: Eine detaillierte Validierung wird nicht gemacht. Das ist stattdessen Aufgabe
     * der {@code ChronoHistory}. Das umgerechnete AD-Jahr darf nicht au&szlig;erhalb des
     * Definitionsbereichs der Klasse {@code PlainDate} liegen. </p>
     *
     * @param   era         historic era
     * @param   yearOfEra   year of related era ({@code >= 1}) starting on January the first
     * @param   month       historic month (1-12)
     * @param   dom         historic day of month (1-31)
     * @return  new historic date (not yet validated)
     * @throws  IllegalArgumentException if any argument is out of required maximum range
     * @since   3.0
     * @see     ChronoHistory#isValid(HistoricDate)
     * @see     GregorianMath#MIN_YEAR
     * @see     GregorianMath#MAX_YEAR
     */
    public static HistoricDate of(
        HistoricEra era,
        int yearOfEra,
        int month,
        int dom
    ) {

        if (era == null) {
            throw new NullPointerException("Missing historic era.");
        } else if (
            (dom < 1 || dom > 31)
            || (month < 1 || month > 12)
            || (Math.abs(era.annoDomini(yearOfEra)) > GregorianMath.MAX_YEAR)
        ) {
            throw new IllegalArgumentException(
                "Out of range: " + toString(era, yearOfEra, month, dom));
        } else if (era == HistoricEra.BYZANTINE) {
            if ((yearOfEra < 0) || ((yearOfEra == 0) && (month < 9))) {
                throw new IllegalArgumentException(
                    "Before creation of the world: " + toString(era, yearOfEra, month, dom));
            }
        } else if (yearOfEra < 1) {
            throw new IllegalArgumentException(
                "Year of era must be positive: " + toString(era, yearOfEra, month, dom));
        }

        return new HistoricDate(era, yearOfEra, month, dom);

    }

    /**
     * <p>Yields the historic era. </p>
     *
     * @return  HistoricEra
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert die historische &Auml;ra. </p>
     *
     * @return  HistoricEra
     * @since   3.0
     */
    public HistoricEra getEra() {

        return this.era;

    }

    /**
     * <p>Yields the year of the historic era starting on January the first. </p>
     *
     * @return  year of era ({@code >= 1})
     * @see     #getYearOfEra(NewYearStrategy)
     * @see     NewYearRule#BEGIN_OF_JANUARY
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert das Jahr der historischen &Auml;ra beginnend am ersten Tag des Januar. </p>
     *
     * @return  year of era ({@code >= 1})
     * @see     #getYearOfEra(NewYearStrategy)
     * @see     NewYearRule#BEGIN_OF_JANUARY
     * @since   3.0
     */
    public int getYearOfEra() {

        return this.yearOfEra;

    }

    /**
     * <p>Yields the displayed historic year whose begin is determined by given new-year-strategy. </p>
     *
     * @param   newYearStrategy     strategy how to determine New Year
     * @return  displayed historic year (never negative)
     * @see     #getYearOfEra()
     * @see     NewYearRule
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Liefert das historische Anzeigejahr, dessen Beginn von der angegebenen Neujahrsstrategie
     * bestimmt wird. </p>
     *
     * @param   newYearStrategy     strategy how to determine New Year
     * @return  displayed historic year (never negative)
     * @see     #getYearOfEra()
     * @see     NewYearRule
     * @since   3.14/4.11
     */
    public int getYearOfEra(NewYearStrategy newYearStrategy) {

        return newYearStrategy.displayedYear(this);

    }

    /**
     * <p>Yields the historic month. </p>
     *
     * @return  month (1-12)
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert den historischen Monat. </p>
     *
     * @return  month (1-12)
     * @since   3.0
     */
    public int getMonth() {

        return this.month;

    }

    /**
     * <p>Yields the historic day of month. </p>
     *
     * @return  day of month (1-31)
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert den historischen Tag des Monats. </p>
     *
     * @return  day of month (1-31)
     * @since   3.0
     */
    public int getDayOfMonth() {

        return this.dom;

    }

    @Override
    public int compareTo(HistoricDate other) {

        int ad1 = this.era.annoDomini(this.yearOfEra);
        int ad2 = other.era.annoDomini(other.yearOfEra);

        if (ad1 < ad2) {
            return -1;
        } else if (ad1 > ad2) {
            return 1;
        }

        int delta = this.getMonth() - other.getMonth();

        if (delta == 0) {
            delta = this.getDayOfMonth() - other.getDayOfMonth();
        }

        return ((delta < 0) ? -1 : (delta > 0 ? 1 : 0));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HistoricDate) {
            HistoricDate that = (HistoricDate) obj;
            return (
                (this.era == that.era)
                && (this.yearOfEra == that.yearOfEra)
                && (this.month == that.month)
                && (this.dom == that.dom)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        int hash = (this.yearOfEra * 1000) + this.month * 32 + this.dom;
        return (this.era == HistoricEra.AD) ? hash : -hash;

    }

    @Override
    public String toString() {

        return toString(this.era, this.yearOfEra, this.month, this.dom);

    }

    private static String toString(
        HistoricEra era,
        int yearOfEra,
        int month,
        int dom
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append(era);
        sb.append('-');
        String yoe = String.valueOf(yearOfEra);
        for (int i = 4 - yoe.length(); i > 0; i--) {
            sb.append('0');
        }
        sb.append(yoe);
        sb.append('-');
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month);
        sb.append('-');
        if (dom < 10) {
            sb.append('0');
        }
        sb.append(dom);
        return sb.toString();

    }

}
