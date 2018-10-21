/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AnnualDate.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.TimeSource;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.IntElementRule;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.Temporal;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.tz.Timezone;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;


/**
 * <p>Represents a combination of month and day-of-month as XML-pendant for {@code xsd:gMonthDay}. </p>
 *
 * <p>February, the 29th is always valid within the context of this class only. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #MONTH_AS_NUMBER}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 * </ul>
 *
 * <p>The calendar year is missing. Therefore this class cannot model a complete calendar date like
 * {@link PlainDate}. For the same reason, a temporal arithmetic is not defined. The main purpose
 * of this class is just modelling partial dates like birthdays etc. Formatting example for localized
 * formatting styles: </p>
 *
 * <pre>
 *    ChronoFormatter&lt;AnnualDate&gt; usStyle =
 *      ChronoFormatter.ofStyle(DisplayMode.SHORT, Locale.US, AnnualDate.chronology());
 *    ChronoFormatter&lt;AnnualDate&gt; germanStyle =
 *      ChronoFormatter.ofStyle(DisplayMode.SHORT, Locale.GERMANY, AnnualDate.chronology());
 *    System.out.println(&quot;US-format: &quot; + usStyle.format(AnnualDate.of(9, 11))); // US-format: 9/11
 *    System.out.println(&quot;German: &quot; + germanStyle.format(AnnualDate.of(9, 11))); // German: 11.9.
 * </pre>
 *
 * <p>Note: The current annual date can be determined by an expression like:
 * {@code AnnualDate current = SystemClock.inLocalView().now(AnnualDate.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.44
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Kombination aus Monat und Tag-des-Monats (Jahrestag) als
 * XML-Pendant zu {@code xsd:gMonthDay}. </p>
 *
 * <p>Der 29. Februar ist nur im Kontext dieser Klasse immer g&uuml;ltig. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #MONTH_AS_NUMBER}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 * </ul>
 *
 * <p>Das Kalenderjahr fehlt. Deshalb kann diese Klasse kein vollst&auml;ndiges Kalenderdatum wie
 * {@link PlainDate} modellieren. Aus dem gleichen Grund ist eine Zeitarithmetik nicht definiert.
 * Der Hauptzweck dieser Klasse ist daher die Modellierung von kalendarischen Teilangaben wie
 * Geburtstagen usw. Formatierungsbeispiel f&uuml;r lokalisierte Formatstile: </p>
 *
 * <pre>
 *    ChronoFormatter&lt;AnnualDate&gt; usStyle =
 *      ChronoFormatter.ofStyle(DisplayMode.SHORT, Locale.US, AnnualDate.chronology());
 *    ChronoFormatter&lt;AnnualDate&gt; germanStyle =
 *      ChronoFormatter.ofStyle(DisplayMode.SHORT, Locale.GERMANY, AnnualDate.chronology());
 *    System.out.println(&quot;US-format: &quot; + usStyle.format(AnnualDate.of(9, 11))); // US-format: 9/11
 *    System.out.println(&quot;German: &quot; + germanStyle.format(AnnualDate.of(9, 11))); // German: 11.9.
 * </pre>
 *
 * <p>Hinweis: Der aktuelle Jahrestag kann mit einem Ausdruck wie folgt bestimmt werden:
 * {@code AnnualDate current = SystemClock.inLocalView().now(AnnualDate.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.44
 */
@CalendarType("iso8601")
public final class AnnualDate
    extends ChronoEntity<AnnualDate>
    implements Comparable<AnnualDate>, Temporal<AnnualDate>, LocalizedPatternSupport, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Element with the calendar month as enum in the value range {@code JANUARY-DECEMBER}). </p>
     */
    /*[deutsch]
     * <p>Element mit dem Monat als Enum (Wertebereich {@code JANUARY-DECEMBER}). </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final ChronoElement<Month> MONTH_OF_YEAR = PlainDate.MONTH_OF_YEAR;

    /**
     * <p>Element with the calendar month in numerical form and the value range
     * {@code 1-12}. </p>
     *
     * <p>Normally the enum-variant is recommended due to clarity and
     * type-safety. The enum-form can also be formatted as text. </p>
     *
     * @see     #MONTH_OF_YEAR
     */
    /*[deutsch]
     * <p>Element mit dem Monat in Nummernform (Wertebereich {@code 1-12}). </p>
     *
     * <p>Im allgemeinen empfiehlt sich wegen der Typsicherheit und sprachlichen
     * Klarheit die enum-Form, die zudem auch als Text formatierbar ist. </p>
     *
     * @see     #MONTH_OF_YEAR
     */
    public static final ChronoElement<Integer> MONTH_AS_NUMBER = PlainDate.MONTH_AS_NUMBER;

    /**
     * <p>Element with the day of month in the value range {@code 1-28/29/30/31}. </p>
     */
    /*[deutsch]
     * <p>Element mit dem Tag des Monats (Wertebereich {@code 1-28/29/30/31}). </p>
     */
    @FormattableElement(format = "d")
    public static final ChronoElement<Integer> DAY_OF_MONTH = PlainDate.DAY_OF_MONTH;

    private static final Chronology<AnnualDate> ENGINE =
        Chronology.Builder
            .setUp(AnnualDate.class, new Merger())
            .appendElement(DAY_OF_MONTH, new IntegerElementRule(true))
            .appendElement(MONTH_OF_YEAR, new MonthElementRule())
            .appendElement(MONTH_AS_NUMBER, new IntegerElementRule(false))
            .build();

    private static final long serialVersionUID = 7510648008819092983L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the gregorian month in range 1-12
     */
    private final int month;

    /**
     * @serial  the day of month in range 1-29/30/31
     */
    private final int dayOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    private AnnualDate(
        int month,
        int dayOfMonth
    ) {
        super();

        this.month = month;
        this.dayOfMonth = dayOfMonth;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new annual date. </p>
     *
     * @param   month      gregorian month as enum
     * @param   dayOfMonth the day of month in range 1-29/30/31
     * @return  new annual date
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen Jahrestag. </p>
     *
     * @param   month       gregorian month as enum
     * @param   dayOfMonth  the day of month in range 1-29/30/31
     * @return  new annual date
     */
    public static AnnualDate of(
        Month month,
        int dayOfMonth
    ) {

        return of(month.getValue(), dayOfMonth);

    }

    /**
     * <p>Creates a new annual date. </p>
     *
     * @param   month      gregorian month in range 1-12
     * @param   dayOfMonth the day of month in range 1-29/30/31
     * @return  new annual date
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen Jahrestag. </p>
     *
     * @param   month       gregorian month in range 1-12
     * @param   dayOfMonth  the day of month in range 1-29/30/31
     * @return  new annual date
     */
    public static AnnualDate of(
        int month,
        int dayOfMonth
    ) {

        check(month, dayOfMonth);
        return new AnnualDate(month, dayOfMonth);

    }

    /**
     * <p>Converts given gregorian date to an annual date. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  AnnualDate
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene gregorianische Datum zu einem Jahrestag. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  AnnualDate
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    public static AnnualDate from(GregorianDate date) {

        PlainDate iso = PlainDate.from(date); // includes validation
        return new AnnualDate(iso.getMonth(), iso.getDayOfMonth());

    }

    /**
     * <p>Obtains the current annual date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(AnnualDate.chronology())}. </p>
     *
     * @return  current annual date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.32/4.27
     */
    /*[deutsch]
     * <p>Ermittelt den aktuellen Jahrestag in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(AnnualDate.chronology())}. </p>
     *
     * @return  current annual date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.32/4.27
     */
    public static AnnualDate nowInSystemTime() {

        return SystemClock.inLocalView().now(ENGINE);

    }

    /**
     * <p>Obtains the gregorian month. </p>
     *
     * @return  Month
     */
    /*[deutsch]
     * <p>Liefert den gregorianischen Monat. </p>
     *
     * @return  Month
     */
    public Month getMonth() {

        return Month.valueOf(this.month);

    }

    /**
     * <p>Obtains the day of month. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  int
     */
    public int getDayOfMonth() {

        return this.dayOfMonth;

    }

    @Override
    public boolean isAfter(AnnualDate temporal) {

        return (this.compareTo(temporal) > 0);

    }

    @Override
    public boolean isBefore(AnnualDate temporal) {

        return (this.compareTo(temporal) < 0);

    }

    @Override
    public boolean isSimultaneous(AnnualDate temporal) {

        return (this.compareTo(temporal) == 0);

    }

    @Override
    public int compareTo(AnnualDate other) {

        if (this.month < other.month) {
            return -1;
        } else if (this.month > other.month) {
            return 1;
        } else if (this.dayOfMonth < other.dayOfMonth) {
            return -1;
        } else if (this.dayOfMonth > other.dayOfMonth) {
            return 1;
        }

        return 0;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof AnnualDate) {
            AnnualDate that = (AnnualDate) obj;
            return ((this.month == that.month) && (this.dayOfMonth == that.dayOfMonth));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (this.month << 16) + this.dayOfMonth;

    }

    /**
     * <p>Yields the full description in the XML-format &quot;--MM-dd&quot;. </p>
     *
     * @return  String compatible to lexical space of xsd:gMonthDay
     * @see     #parseXML(String)
     */
    /*[deutsch]
     * <p>Liefert die vollst&auml;ndige Beschreibung im XML-Format &quot;--MM-dd&quot;. </p>
     *
     * @return  String compatible to lexical space of xsd:gMonthDay
     * @see     #parseXML(String)
     */
    @Override
    public String toString() {

        return toString(this.month, this.dayOfMonth);

    }

    /**
     * <p>Parses given string to an annual date where the input is in XML-format &quot;--MM-dd&quot;. </p>
     *
     * @param   xml     string compatible to lexical space of xsd:gMonthDay
     * @return  AnnualDate
     * @throws  ParseException if parsing fails
     * @see     #toString()
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Jahrestag im XML-Format &quot;--MM-dd&quot;. </p>
     *
     * @param   xml     string compatible to lexical space of xsd:gMonthDay
     * @return  AnnualDate
     * @throws  ParseException if parsing fails
     * @see     #toString()
     */
    public static AnnualDate parseXML(String xml) throws ParseException {

        if ((xml.length() == 7) && (xml.charAt(0) == '-') && (xml.charAt(1) == '-') && (xml.charAt(4) == '-')) {
            int m1 = toDigit(xml, 2);
            int m2 = toDigit(xml, 3);
            int d1 = toDigit(xml, 5);
            int d2 = toDigit(xml, 6);
            return new AnnualDate(m1 * 10 + m2, d1 * 10 + d2);
        } else {
            throw new ParseException("Not compatible to standard XML-format: " + xml, xml.length());
        }

    }

    /**
     * <p>Creates a complete ISO calendar date for given gregorian year. </p>
     *
     * @param   year        proleptic iso year [(-999,999,999)-999,999,999]
     * @return  new or cached calendar date instance
     * @throws  IllegalArgumentException if the year argument is out of range or if this instance represents
     *          the 29th of February and is not valid for given year
     * @see     #isValidDate(int)
     */
    /*[deutsch]
     * <p>Erzeugt ein vollst&auml;ndiges ISO-Kalenderdatum zum angegebenen gregorianischen Jahr. </p>
     *
     * @param   year        proleptic iso year [(-999,999,999)-999,999,999]
     * @return  new or cached calendar date instance
     * @throws  IllegalArgumentException if the year argument is out of range or if this instance represents
     *          the 29th of February and is not valid for given year
     * @see     #isValidDate(int)
     */
    public PlainDate atYear(int year) {

        return PlainDate.of(year, this.month, this.dayOfMonth);

    }

    /**
     * <p>Checks if this instance results in a valid ISO calendar date for given gregorian year. </p>
     *
     * @param   year        proleptic iso year [(-999,999,999)-999,999,999]
     * @return  {@code true} if the year argument is out of range or if this instance represents
     *          the 29th of February and is not valid for given year else {@code false}
     * @see     #atYear(int)
     */
    /*[deutsch]
     * <p>Pr&uuml;ft, ob diese Instanz zum angegebenen gregorianischen Jahr ein g&uuml;ltiges
     * ISO-Kalenderdatum ergibt. </p>
     *
     * @param   year        proleptic iso year [(-999,999,999)-999,999,999]
     * @return  {@code true} if the year argument is out of range or if this instance represents
     *          the 29th of February and is not valid for given year else {@code false}
     * @see     #atYear(int)
     */
    public boolean isValidDate(int year) {

        return (
            (year >= GregorianMath.MIN_YEAR) && (year <= GregorianMath.MAX_YEAR)
            && ((this.month != 2) || (this.dayOfMonth != 29) || GregorianMath.isLeapYear(year))
        );

    }

    /**
     * <p>Determines the next possible exact annual date. </p>
     *
     * <p>If this annual date is a leap day then the next date can be some years later. Example: </p>
     *
     * <pre>
     *     System.out.println(PlainDate.of(2015, 2, 28).with(AnnualDate.of(Month.FEBRUARY, 29).asNextEvent()));
     *     // 2016-02-29
     *     System.out.println(PlainDate.of(2016, 2, 28).with(AnnualDate.of(Month.FEBRUARY, 29).asNextEvent()));
     *     // 2016-02-29
     *     System.out.println(PlainDate.of(2016, 2, 29).with(AnnualDate.of(Month.FEBRUARY, 29).asNextEvent()));
     *     // 2020-02-29
     * </pre>
     *
     * @return  chronological operator
     * @see     #asNextRoundedEvent()
     */
    /*[deutsch]
     * <p>Bestimmt den n&auml;chstm&ouml;glichen exakten Jahrestag. </p>
     *
     * <p>Wenn dieser Jahrestag ein Schalttag ist, kann das n&auml;chste Ereignis Jahre sp&auml;ter
     * folgen. Beispiel: </p>
     *
     * <pre>
     *     System.out.println(PlainDate.of(2015, 2, 28).with(AnnualDate.of(Month.FEBRUARY, 29).asNextEvent()));
     *     // 2016-02-29
     *     System.out.println(PlainDate.of(2016, 2, 28).with(AnnualDate.of(Month.FEBRUARY, 29).asNextEvent()));
     *     // 2016-02-29
     *     System.out.println(PlainDate.of(2016, 2, 29).with(AnnualDate.of(Month.FEBRUARY, 29).asNextEvent()));
     *     // 2020-02-29
     * </pre>
     *
     * @return  chronological operator
     * @see     #asNextRoundedEvent()
     */
    public ChronoOperator<PlainDate> asNextExactEvent() {

        return new ChronoOperator<PlainDate>() {
            @Override
            public PlainDate apply(PlainDate date) {
                int year = date.getYear();
                int month = AnnualDate.this.getMonth().getValue();
                int dom = AnnualDate.this.getDayOfMonth();
                if ((month < date.getMonth()) || ((month == date.getMonth()) && (dom <= date.getDayOfMonth()))) {
                    year++;
                }
                if ((month == 2) && (dom == 29)) {
                    while (!GregorianMath.isLeapYear(year)) {
                        year++;
                    }
                }
                return PlainDate.of(year, month, dom);
            }
        };

    }

    /**
     * <p>Determines the next possible annual date and rounds up to next day if necessary. </p>
     *
     * <p>If this annual date is a leap day then the next date can be advanced to begin of March. Example: </p>
     *
     * <pre>
     *     System.out.println(PlainDate.of(2015, 2, 28).with(AnnualDate.of(Month.FEBRUARY, 29).asNextEvent()));
     *     // 2015-03-01
     * </pre>
     *
     * @return  chronological operator
     * @see     #asNextExactEvent()
     */
    /*[deutsch]
     * <p>Bestimmt den n&auml;chstm&ouml;glichen Jahrestag und rundet notfalls zum n&auml;chsten Tag. </p>
     *
     * <p>Wenn dieser Jahrestag ein Schalttag ist, kann das n&auml;chste Ereignis auf den ersten M&auml;rz
     * verschoben werden. Beispiel: </p>
     *
     * <pre>
     *     System.out.println(PlainDate.of(2015, 2, 28).with(AnnualDate.of(Month.FEBRUARY, 29).asNextEvent()));
     *     // 2015-03-01
     * </pre>
     *
     * @return  chronological operator
     * @see     #asNextExactEvent()
     */
    public ChronoOperator<PlainDate> asNextRoundedEvent() {

        return new ChronoOperator<PlainDate>() {
            @Override
            public PlainDate apply(PlainDate date) {
                int year = date.getYear();
                int month = AnnualDate.this.getMonth().getValue();
                int dom = AnnualDate.this.getDayOfMonth();
                if ((month < date.getMonth()) || ((month == date.getMonth()) && (dom <= date.getDayOfMonth()))) {
                    year++;
                }
                if ((month == 2) && (dom == 29) && !GregorianMath.isLeapYear(year)) {
                    month = 3;
                    dom = 1;
                }
                return PlainDate.of(year, month, dom);
            }
        };

    }

    /**
     * <p>Yields the associated chronology. </p>
     *
     * @return  the underlying rule engine
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Chronologie. </p>
     *
     * @return  the underlying rule engine
     */
    public static Chronology<AnnualDate> chronology() {

        return ENGINE;

    }

    @Override
    protected Chronology<AnnualDate> getChronology() {

        return ENGINE;

    }

    @Override
    protected AnnualDate getContext() {

        return this;

    }

    private static void check(
        int month,
        int dayOfMonth
    ) {

        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Month not in range 1-12: " + month);
        } else if ((dayOfMonth < 1) || (dayOfMonth > getMaxDay(month))) {
            throw new IllegalArgumentException("Out of bounds: " + toString(month, dayOfMonth));
        }

    }

    private static int getMaxDay(int month) {

        switch (month) {
            case 2:
                return 29;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }

    }

    private static int toDigit(
        String xml,
        int offset
    ) throws ParseException {

        char c = xml.charAt(offset);

        if ((c >= '0') && (c <= '9')) {
            return (c - '0');
        } else {
            throw new ParseException("Digit expected: " + xml, offset);
        }

    }

    private static String toString(
        int month,
        int dayOfMonth
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month);
        sb.append('-');
        if (dayOfMonth < 10) {
            sb.append('0');
        }
        sb.append(dayOfMonth);
        return sb.toString();

    }

    /**
     * @return  this instance
     * @throws  InvalidObjectException if the state is inconsistent
     * @serialData Checks the consistency
     */
    private Object readResolve() throws InvalidObjectException {

        try {
            check(this.month, this.dayOfMonth);
            return this;
        } catch (IllegalArgumentException iae) {
            throw new InvalidObjectException(iae.getMessage());
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Merger
        implements ChronoMerger<AnnualDate> {

        //~ Methoden ------------------------------------------------------

        @Override
        public AnnualDate createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {

            Timezone zone;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                zone = Timezone.of(attributes.get(Attributes.TIMEZONE_ID));
            } else if (attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax()) {
                zone = Timezone.ofSystem();
            } else {
                return null;
            }

            PlainDate date = Moment.from(clock.currentTime()).toZonalTimestamp(zone.getID()).toDate();
            return AnnualDate.of(date.getMonth(), date.getDayOfMonth());

        }

        @Override
        public AnnualDate createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int dom = entity.getInt(DAY_OF_MONTH);

            if (dom != Integer.MIN_VALUE) {
                int m = entity.getInt(PlainDate.MONTH_AS_NUMBER); // optimization
                if ((m == Integer.MIN_VALUE) && entity.contains(MONTH_OF_YEAR)) {
                    m = entity.get(MONTH_OF_YEAR).getValue();
                }
                if (m != Integer.MIN_VALUE) {
                    if ((dom >= 1) && (dom <= getMaxDay(m))) {
                        if ((m >= 1) && (m <= 12)) {
                            return new AnnualDate(m, dom);
                        } else {
                            entity.with(ValidationElement.ERROR_MESSAGE, "Month out of bounds: " + m);
                        }
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Day-of-month out of bounds: " + dom);
                    }
                }
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(
            AnnualDate context,
            AttributeQuery attributes
        ) {
            return context;
        }

        @Override
        public Chronology<?> preparser() {
            return null;
        }

        @Override
        public String getFormatPattern(DisplayStyle style, Locale locale) {
            Map<String, String> map = CalendarText.getIsoInstance(locale).getTextForms();
            String key = null;
            switch (style.getStyleValue()) {
                case DateFormat.FULL:
                    key = "F_MMMMd";
                    break;
                case DateFormat.LONG:
                    key = "F_MMMd";
                    break;
                case DateFormat.MEDIUM:
                    key = "F_MMd";
                    break;
                case DateFormat.SHORT:
                    key = "F_Md";
                    break;
            }
            String pattern = getFormatPattern(map, key);
            return ((pattern == null) ? "MM-dd" : pattern);
        }

        @Override
        public StartOfDay getDefaultStartOfDay() {
            return StartOfDay.MIDNIGHT;
        }

        @Override
        public int getDefaultPivotYear() {
            return PlainDate.axis().getDefaultPivotYear();
        }

        private static String getFormatPattern(
            Map<String, String> map,
            String key
        ) {
            if (map.containsKey(key)) {
                return map.get(key);
            }

            if ("F_MMMMd".equals(key)) {
                return getFormatPattern(map, "F_MMMd");
            } else if ("F_MMMd".equals(key)) {
                return getFormatPattern(map, "F_MMd");
            } else if ("F_MMd".equals(key)) {
                return getFormatPattern(map, "F_Md");
            } else {
                return null;
            }
        }

    }

    private static class MonthElementRule
        implements ElementRule<AnnualDate, Month> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Month getValue(AnnualDate context) {

            return context.getMonth();

        }

        @Override
        public Month getMinimum(AnnualDate context) {

            return Month.JANUARY;

        }

        @Override
        public Month getMaximum(AnnualDate context) {

            return Month.DECEMBER;

        }

        @Override
        public boolean isValid(
            AnnualDate context,
            Month value
        ) {

            return (value != null);

        }

        @Override
        public AnnualDate withValue(
            AnnualDate context,
            Month value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing new value.");
            }

            int m = value.getValue();
            return new AnnualDate(m, Math.min(context.dayOfMonth, getMaxDay(m)));

        }

        @Override
        public ChronoElement<?> getChildAtFloor(AnnualDate context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(AnnualDate context) {

            return DAY_OF_MONTH;

        }

    }

    private static class IntegerElementRule
        implements IntElementRule<AnnualDate> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean daywise;

        //~ Konstruktoren -------------------------------------------------

        IntegerElementRule(boolean daywise) {
            super();

            this.daywise = daywise;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int getInt(AnnualDate context) {

            return (this.daywise ? context.dayOfMonth : context.month);

        }

        @Override
        public boolean isValid(
            AnnualDate context,
            int value
        ) {

            if (value < 1) {
                return false;
            } else if (this.daywise) {
                return (value <= getMaxDay(context.month));
            } else {
                return (value <= 12);
            }

        }

        @Override
        public AnnualDate withValue(
            AnnualDate context,
            int value,
            boolean lenient
        ) {

            if (this.daywise) {
                return AnnualDate.of(context.month, value);
            } else {
                return AnnualDate.of(value, Math.min(context.dayOfMonth, getMaxDay(value)));
            }

        }

        @Override
        public Integer getValue(AnnualDate context) {

            return Integer.valueOf(this.getInt(context));

        }

        @Override
        public Integer getMinimum(AnnualDate context) {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getMaximum(AnnualDate context) {

            if (this.daywise) {
                return Integer.valueOf(getMaxDay(context.month));
            } else {
                return Integer.valueOf(12);
            }

        }

        @Override
        public boolean isValid(AnnualDate context, Integer value) {

            if (value == null) {
                return false;
            }

            return this.isValid(context, value.intValue());

        }

        @Override
        public AnnualDate withValue(
            AnnualDate context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing new value.");
            }

            return this.withValue(context, value.intValue(), lenient);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(AnnualDate context) {

            return (this.daywise ? null : DAY_OF_MONTH);

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(AnnualDate context) {

            return (this.daywise ? null : DAY_OF_MONTH);

        }

    }

}
