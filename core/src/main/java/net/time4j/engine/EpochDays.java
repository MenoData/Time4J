/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EpochDays.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;

import java.util.Locale;


/**
 * <p>Defines elements based on different epoch-related day numbers. </p>
 *
 * <p>Some day number procedures really use decimal fractions in order to
 * represent wall time fractions in calendar dates (for example julian days).
 * This enum only supports pure date types of type {@code Calendrical} and
 * always uses the largest integer which is still smaller than or equal to
 * the decimal fraction in question (mathematically a
 * {@code floor()}-function. </p>
 *
 * <p>Instances of this element class will be automatically registered in
 * any chronology which is compatible to {@code Calendrical}. </p>
 *
 * @author  Meno Hochschild
 * @see     Calendrical
 */
/*[deutsch]
 * <p>Definiert Elemente auf der Basis von verschiedenen epochenbezogenen
 * Tagesnummern. </p>
 *
 * <p>Einige Tagesnummerierungsverfahren benutzen real Dezimalbr&uuml;che
 * zur Darstellung von Uhrzeitanteilen in Datumsangaben. Dieses Enum
 * unterst&uuml;tzt nur reine Datumstypen vom Typ {@code Calendrical}
 * und benutzt immer die gr&ouml;&szlig;te Integer-Zahl, die noch kleiner
 * oder gleich dem jeweiligen Dezimalbruch ist (mathematisch eine
 * {@code floor()}-Funktion. </p>
 *
 * <p>Instanzen dieser Elementklasse werden in einer zur Klasse
 * {@code Calendrical} kompatiblen Chronologie automatisch registriert. </p>
 *
 * @author  Meno Hochschild
 * @see     Calendrical
 */
public enum EpochDays
    implements ChronoElement<Long> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Reference point is the introduction of UTC on date [1972-01-01]
     * at midnight as day zero. </p>
     */
    /*[deutsch]
     * <p>Bezugspunkt ist die Einf&uuml;hrung von UTC zum Datum [1972-01-01]
     * zu Mitternacht als Tag 0. </p>
     */
    UTC(2441317),

    /**
     * <p>Reference point is the first of January 1970 at midnight as
     * day zero. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  import static net.time4j.Month.FEBRUARY;
     *
     *  PlainDate date = PlainDate.of(1970, FEBRUARY, 4);
     *  System.out.println(date.get(EpochDays.UNIX)); // output: 34
     * </pre>
     *
     * <p>Strictly spoken, the UNIX time is counted in seconds. This element
     * counts in days based on the relation &quot;1 day = 86400 seconds&quot;.
     * </p>
     */
    /*[deutsch]
     * <p>Bezugspunkt ist der erste Januar 1970 Mitternacht als Tag 0. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  import static net.time4j.Month.FEBRUARY;
     *
     *  PlainDate date = PlainDate.of(1970, FEBRUARY, 4);
     *  System.out.println(date.get(EpochDays.UNIX)); // Ausgabe: 34
     * </pre>
     *
     * <p>Streng genommen wird die UNIX-Zeit in Sekunden gez&auml;hlt. Dieses
     * Element benutzt f&uuml;r den Zweck der Datumsumrechnung die Z&auml;hlung
     * in ganzen Tagen auf der Basis &quot;1 Tag = 86400 Sekunden&quot;. </p>
     */
    UNIX(2440587),

    /**
     * <p>Count of days relative to the ISO-date [1858-11-17] as day zero. </p>
     *
     * <p>Used by astronomers for example. </p>
     */
    /*[deutsch]
     * <p>Anzahl der Tage relativ zum ISO-Datum [1858-11-17] als Tag 0. </p>
     *
     * <p>Wird u.a. von Astronomen verwendet. </p>
     */
    @FormattableElement(format = "g")
    MODIFIED_JULIAN_DATE(2400000),

    /**
     * <p>Count of days relative to the ISO-date [1900-01-01] as day 1. </p>
     *
     * <p>Used by MS-Excel in Windows operating systems. </p>
     */
    /*[deutsch]
     * <p>Anzahl der Tage relativ zum ISO-Datum [1900-01-01] als Tag 1. </p>
     *
     * <p>Wird von MS-Excel in Windows-Betriebssystemen verwendet. </p>
     */
    EXCEL(2415019),

    /**
     * <p>Count of days relative to the ISO-date [1601-01-01] as day 1. </p>
     *
     * <p>Used by COBOL for example. </p>
     */
    /*[deutsch]
     * <p>Anzahl der Tage relativ zum ISO-Datum [1601-01-01] als Tag 1. </p>
     *
     * <p>Wird u.a. von COBOL verwendet. </p>
     */
    ANSI(2305812),

    /**
     * <p>Count of days relative to the ISO-date [0001-01-01] as day 1. </p>
     *
     * <p>Used by the autors Nachum Dershowitz and Edward M. Reingold in
     * their standard work &quot;CalendarDate Calculations&quot;. </p>
     */
    /*[deutsch]
     * <p>Anzahl der Tage relativ zum ISO-Datum [0001-01-01] als Tag 1. </p>
     *
     * <p>Wird im Standard-Werk &quot;CalendarDate Calculations&quot; von den
     * Autoren Nachum Dershowitz und Edward M. Reingold benutzt. </p>
     */
    RATA_DIE(1721424),

    /**
     * <p>Count of julian days relative to the the julian calendar date
     * [1st of January 4713 BC] at noon [12:00] as day zero. </p>
     *
     * <p>The julian epoch date corresponds to the proleptic ISO-date
     * [-4713-11-24]. The ISO-day centered around noon will be taken into
     * account whose associated julian day starts at noon. </p>
     */
    /*[deutsch]
     * <p>Anzahl der julianischen Tage relativ zum julianischen Datum
     * [1 Januar 4713 BC] zur Mittagszeit [12:00] als Tag 0. </p>
     *
     * <p>Das julianische Epochendatum entspricht im proleptischen
     * gregorianischen Kalender (ISO) dem Datum [-4713-11-24]. Als Tag
     * wird der zentrisch um 12 Uhr Mittag angeordnete Tag gez&auml;hlt,
     * dessen zugeordneter julianischer Tag erst ab Mittag beginnt. </p>
     */
    // eigentlich -0.5, Umrechnung zur Mittagszeit
    JULIAN_DAY_NUMBER(-1),

    /**
     * <p>Count of days relative to gregorian cut-over date
     * [1582-10-15] as day 1. </p>
     */
    /*[deutsch]
     * <p>Anzahl der Tage relativ zum gregorianischen Umstellungsdatum
     * [1582-10-15] als Tag 1. </p>
     */
    LILIAN_DAY_NUMBER(2299159);

    //~ Instanzvariablen --------------------------------------------------

    private final int offset;

    //~ Konstruktoren -----------------------------------------------------

    private EpochDays(int julianDays) {
        this.offset = julianDays - 2440587 - 2 * 365;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Converts given day number to a day number based on this epoch
     * at reference time of midnight. </p>
     *
     * @param   amount  count of days relative to given epoch at noon
     * @param   epoch   epoch reference
     * @return  count of days relative to this epoch
     * @throws  IllegalArgumentException if the first argument is out of range
     */
    /*[deutsch]
     * <p>Rechnet die angegebene Tageszahl in eine Tageszahl auf der
     * Nummerierungsbasis dieser Instanz zur Mittagszeit um. </p>
     *
     * @param   amount  count of days relative to given epoch at noon
     * @param   epoch   epoch reference
     * @return  count of days relative to this epoch
     * @throws  IllegalArgumentException if the first argument is out of range
     */
    public long transform(
        long amount,
        EpochDays epoch
    ) {

        try {
            return MathUtils.safeAdd(
                amount,
                (epoch.offset - this.offset)
            );
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException(ex);
        }

    }

    @Override
    public Class<Long> getType() {

        return Long.class;

    }

    /**
     * <p>Defines  the format symbol. </p>
     *
     * @return  &quot;g&quot; if MODIFIED_JULIAN_DATE else ASCII-0
     */
    /*[deutsch]
     * <p>Definiert das Formatsymbol. </p>
     *
     * @return  &quot;g&quot; if MODIFIED_JULIAN_DATE else ASCII-0
     */
    @Override
    public char getSymbol() {

        return ((this == MODIFIED_JULIAN_DATE) ? 'g' : '\u0000');

    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public Long getDefaultMinimum() {

        // PlainTime.MIN.get(EpochDays.UTC) - offset
        return Long.valueOf(-365243219892L - this.offset);

    }

    @Override
    public Long getDefaultMaximum() {

        // PlainTime.MAX.get(EpochDays.UTC) - offset
        return Long.valueOf(365241779741L - this.offset);

    }

    /**
     * <p>This element is a date element. </p>
     *
     * @return  {@code true}
     */
    /*[deutsch]
     * <p>Dieses Element ist ein Datumselement. </p>
     *
     * @return  {@code true}
     */
    @Override
    public boolean isDateElement() {

        return true;

    }

    /**
     * <p>This element is no wall time element. </p>
     *
     * @return  {@code false}
     */
    /*[deutsch]
     * <p>Dieses Element ist kein Uhrzeitelement. </p>
     *
     * @return  {@code false}
     */
    @Override
    public boolean isTimeElement() {

        return false;

    }

    @Override
    public boolean isLenient() {

        return false;

    }

    @Override
    public String getDisplayName(Locale language) {

        return this.name();

    }

    /**
     * <p>Leitet eine Regel ab. </p>
     *
     * @param   <D> generic chronology type
     * @param   calsys      calendar system
     * @return  new element rule for this epoch reference
     */
    <D extends ChronoEntity<D>> ElementRule<D, Long> derive(CalendarSystem<D> calsys) {

        return new Rule<D>(this, calsys);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Rule<D extends ChronoEntity<D>>
        implements ElementRule<D, Long> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final int UTC_OFFSET = 2 * 365;

        //~ Instanzvariablen ----------------------------------------------

        private final EpochDays element;
        private final CalendarSystem<D> calsys;

        //~ Konstruktoren -------------------------------------------------

        Rule(
            EpochDays element,
            CalendarSystem<D> calsys
        ) {
            super();

            this.element = element;
            this.calsys = calsys;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Long getValue(D context) {

            long days =
                this.element.transform(
                    this.calsys.transform(context) + UTC_OFFSET,
                    EpochDays.UNIX);

            return Long.valueOf(days);

        }

        @Override
        public boolean isValid(
            D context,
            Long value
        ) {

            if (value == null) {
                return false;
            }

            try {
                long days =
                    MathUtils.safeSubtract(
                        EpochDays.UNIX.transform(value.longValue(), this.element),
                        UTC_OFFSET
                    );

                return (
                    (days <= this.calsys.getMaximumSinceUTC())
                    && (days >= this.calsys.getMinimumSinceUTC())
                );
            } catch (ArithmeticException ex) {
                return false;
            } catch (IllegalArgumentException ex) {
                return false;
            }

        }

        @Override
        public D withValue(
            D context,
            Long value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing epoch day value.");
            }

            long utcDays =
                MathUtils.safeSubtract(
                    EpochDays.UNIX.transform(value.longValue(), this.element),
                    UTC_OFFSET
                );

            return this.calsys.transform(utcDays);

        }

        @Override
        public Long getMinimum(D context) {

            long days =
                this.element.transform(
                    this.calsys.getMinimumSinceUTC() + UTC_OFFSET,
                    EpochDays.UNIX
                );

            return Long.valueOf(days);

        }

        @Override
        public Long getMaximum(D context) {

            long days =
                this.element.transform(
                    this.calsys.getMaximumSinceUTC() + UTC_OFFSET,
                    EpochDays.UNIX
                );

            return Long.valueOf(days);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(D context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(D context) {

            return null;

        }

    }

}
