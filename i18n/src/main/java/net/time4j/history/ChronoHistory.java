/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoHistory.java) is part of project Time4J.
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

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.EpochDays;
import net.time4j.format.TextElement;
import net.time4j.history.internal.HistoricVariant;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Represents the chronological history of calendar reforms in a given region. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Geschichte der Kalenderreformen in einer gegebenen Region. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
public final class ChronoHistory
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Describes no real historical event but just the proleptic gregorian calendar which is assumed
     * to be in power all times. </p>
     *
     * <p>This constant rather serves for academic purposes. Users will normally use {@code PlainDate}
     * without an era. </p>
     */
    /*[deutsch]
     * <p>Beschreibt kein wirkliches historisches Ereignis, sondern einfach nur den proleptisch gregorianischen
     * Kalender, der als f&uuml;r alle Zeiten g&uuml;ltig angesehen wird. </p>
     *
     * <p>Diese Konstante dient eher akademischen &Uuml;bungen. Anwender werden normalerweise direkt die Klasse
     * {@code PlainDate} ohne das &Auml;ra-Konzept nutzen. </p>
     */
    public static final ChronoHistory PROLEPTIC_GREGORIAN;

    /**
     * <p>Describes no real historical event but just the proleptic julian calendar which is assumed
     * to be in power all times. </p>
     *
     * <p>This constant rather serves for academic purposes because the julian calendar is now nowhere in power
     * and has not existed before the calendar reform of Julius Caesar. </p>
     */
    /*[deutsch]
     * <p>Beschreibt kein wirkliches historisches Ereignis, sondern einfach nur den proleptisch julianischen
     * Kalender, der als f&uuml;r alle Zeiten g&uuml;ltig angesehen wird. </p>
     *
     * <p>Diese Konstante dient eher akademischen &Uuml;bungen, weil der julianische Kalender aktuell nirgendwo
     * in der Welt in Kraft ist und vor der Kalenderreform von Julius Caesar nicht existierte. </p>
     */
    public static final ChronoHistory PROLEPTIC_JULIAN;

    private static final long EARLIEST_CUTOVER;
    private static final ChronoHistory INTRODUCTION_BY_POPE_GREGOR;
    private static final ChronoHistory SWEDEN;

    private static final Map<String, ChronoHistory> LOOKUP;

    static {
        PROLEPTIC_GREGORIAN =
            new ChronoHistory(
                HistoricVariant.PROLEPTIC_GREGORIAN,
                Collections.singletonList(
                    new CutOverEvent(Long.MIN_VALUE, CalendarAlgorithm.GREGORIAN, CalendarAlgorithm.GREGORIAN)));

        PROLEPTIC_JULIAN =
            new ChronoHistory(
                HistoricVariant.PROLEPTIC_JULIAN,
                Collections.singletonList(
                    new CutOverEvent(Long.MIN_VALUE, CalendarAlgorithm.JULIAN, CalendarAlgorithm.JULIAN)));

        EARLIEST_CUTOVER = PlainDate.of(1582, 10, 15).get(EpochDays.MODIFIED_JULIAN_DATE);
        INTRODUCTION_BY_POPE_GREGOR = ChronoHistory.ofGregorianReform(EARLIEST_CUTOVER);

        List<CutOverEvent> events = new ArrayList<CutOverEvent>();
        events.add(new CutOverEvent(-57959, CalendarAlgorithm.JULIAN, CalendarAlgorithm.SWEDISH)); // 1700-03-01
        events.add(new CutOverEvent(-53575, CalendarAlgorithm.SWEDISH, CalendarAlgorithm.JULIAN)); // 1712-03-01
        events.add(new CutOverEvent(-38611, CalendarAlgorithm.JULIAN, CalendarAlgorithm.GREGORIAN)); // 1753-03-01
        SWEDEN = new ChronoHistory(HistoricVariant.SWEDEN, Collections.unmodifiableList(events));

        Map<String, ChronoHistory> tmp = new HashMap<String, ChronoHistory>();
        tmp.put("GB", ChronoHistory.ofGregorianReform(PlainDate.of(1752, 9, 14)));
        tmp.put("RU", ChronoHistory.ofGregorianReform(PlainDate.of(1918, 2, 14)));
        tmp.put("SE", SWEDEN);
        LOOKUP = Collections.unmodifiableMap(tmp);
    }

    // Dient der Serialisierungsunterstützung.
    private static final long serialVersionUID = 4100690610730913643L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final HistoricVariant variant;
    private transient final List<CutOverEvent> events;
    private transient final AncientJulianLeapYears ajly;

    private transient final TextElement<HistoricEra> eraElement;
    private transient final TextElement<Integer> yearOfEraElement;
    private transient final TextElement<Integer> monthElement;
    private transient final TextElement<Integer> dayOfMonthElement;
    private transient final Set<ChronoElement<?>> elements;

    //~ Konstruktoren -----------------------------------------------------

    private ChronoHistory(
        HistoricVariant variant,
        List<CutOverEvent> events
    ) {
        this(variant, events, null);

    }

    private ChronoHistory(
        HistoricVariant variant,
        List<CutOverEvent> events,
        AncientJulianLeapYears ajly
    ) {
        super();

        if (events.isEmpty()) {
            throw new IllegalArgumentException(
                "At least one cutover event must be present in chronological history.");
        } else if (variant == null) {
            throw new NullPointerException("Missing historical variant.");
        }

        this.variant = variant;
        this.events = events;
        this.ajly = ajly;

        this.eraElement = new HistoricalEraElement(this);
        this.yearOfEraElement = HistoricalIntegerElement.forYearOfEra(this);
        this.monthElement = HistoricalIntegerElement.forMonth(this);
        this.dayOfMonthElement = HistoricalIntegerElement.forDayOfMonth(this);

        Set<ChronoElement<?>> tmp = new HashSet<ChronoElement<?>>();
        tmp.add(this.eraElement);
        tmp.add(this.yearOfEraElement);
        tmp.add(this.monthElement);
        tmp.add(this.dayOfMonthElement);
        this.elements = Collections.unmodifiableSet(tmp);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Describes the original switch from julian to gregorian calendar introduced
     * by pope Gregor on 1582-10-15. </p>
     *
     * @return  chronological history with cutover to gregorian calendar on 1582-10-15
     * @see     #ofGregorianReform(PlainDate)
     * @since   3.0
     */
    /*[deutsch]
     * <p>Beschreibt die Umstellung vom julianischen zum gregorianischen Kalender wie
     * von Papst Gregor zu 1582-10-15 eingef&uuml;hrt. </p>
     *
     * @return  chronological history with cutover to gregorian calendar on 1582-10-15
     * @see     #ofGregorianReform(PlainDate)
     * @since   3.0
     */
    public static ChronoHistory ofFirstGregorianReform() {

        return INTRODUCTION_BY_POPE_GREGOR;

    }

    /**
     * <p>Describes a single switch from julian to gregorian calendar at given date. </p>
     *
     * <p>Since version 3.1 there are two special cases to consider. If the given date is the maximum/minimum
     * of the date axis then this method will return the proleptic julian/gregorian {@code ChronoHistory}. </p>
     *
     * @param   start   calendar date when the gregorian calendar was introduced
     * @return  new chronological history with only one cutover from julian to gregorian calendar
     * @throws  IllegalArgumentException if given date is before first introduction of gregorian calendar on 1582-10-15
     * @see     #ofFirstGregorianReform()
     * @since   3.0
     */
    /*[deutsch]
     * <p>Beschreibt die Umstellung vom julianischen zum gregorianischen Kalender am angegebenen Datum. </p>
     *
     * <p>Seit Version 3.1 gibt es zwei Sonderf&auml;lle. Wenn das angegebene Datum das Maximum/Minimum der
     * Datumsachse darstellt, dann wird diese Methode die proleptisch julianische/gregorianische Variante
     * von {@code ChronoHistory} zur&uuml;ckgeben. </p>
     *
     * @param   start   calendar date when the gregorian calendar was introduced
     * @return  new chronological history with only one cutover from julian to gregorian calendar
     * @throws  IllegalArgumentException if given date is before first introduction of gregorian calendar on 1582-10-15
     * @see     #ofFirstGregorianReform()
     * @since   3.0
     */
    public static ChronoHistory ofGregorianReform(PlainDate start) {

        if (start.equals(PlainDate.axis().getMaximum())) {
            return ChronoHistory.PROLEPTIC_JULIAN;
        } else if (start.equals(PlainDate.axis().getMinimum())) {
            return ChronoHistory.PROLEPTIC_GREGORIAN;
        }

        long mjd = start.get(EpochDays.MODIFIED_JULIAN_DATE);
        check(mjd);

        if (mjd == EARLIEST_CUTOVER) {
            return INTRODUCTION_BY_POPE_GREGOR;
        }

        return ChronoHistory.ofGregorianReform(mjd);

    }

    /**
     * <p>Determines the history of gregorian calendar reforms for given locale. </p>
     *
     * <p>The actual implementation just falls back to the introduction of gregorian calendar by
     * pope Gregor - with the exception of Russia, Sweden and England. Later releases of Time4J will
     * refine the implementation for most European countries. For any cutover date not supported by this
     * method, users can call {@code ofGregorianReform(PlainDate)} instead. </p>
     *
     * <p>This method does not use the language part of given locale but the country part (ISO-3166). </p>
     *
     * @param   locale  country setting
     * @return  localized chronological history
     * @since   3.0
     * @see     #ofGregorianReform(PlainDate)
     */
    /*[deutsch]
     * <p>Ermittelt die Geschichte der gregorianischen Kalenderreformen f&uuml;r die
     * angegebene Region. </p>
     *
     * <p>Die aktuelle Implementierung f&auml;llt au&szlig;er f&uuml;r Russland, Schweden und England
     * auf die erste Einf&uuml;hrung des gregorianischen Kalenders durch Papst Gregor zur&uuml;ck.
     * Sp&auml;tere Releases von Time4J werden diesen Ansatz f&uuml;r die meisten europ&auml;ischen
     * L&auml;nder verfeinern. F&uuml;r jedes hier nicht unterst&uuml;tzte Umstellungsdatum k&ouml;nnen
     * Anwender stattdessen {@code ofGregorianReform(PlainDate)} nutzen. </p>
     *
     * <p>Diese Methode nutzt nicht den Sprach-, sondern den L&auml;nderteil des Arguments (ISO-3166). </p>
     *
     * @param   locale  country setting
     * @return  localized chronological history
     * @since   3.0
     * @see     #ofGregorianReform(PlainDate)
     */
    public static ChronoHistory of(Locale locale) {

        // TODO: support more gregorian cutover dates (for example Scotland, Germany-various locations etc.)

        ChronoHistory history = LOOKUP.get(locale.getCountry());

        if (history == null) {
            return ChronoHistory.ofFirstGregorianReform();
        } else {
            return history;
        }

    }

    /**
     * <p>The Swedish calendar has three cutover dates due to a failed experiment
     * when switching to gregorian calendar in the years 1700-1712 step by step. </p>
     *
     * @return  swedish chronological history
     * @since   3.0
     */
    /*[deutsch]
     * <p>Der schwedische Kalender hat drei Umstellungszeitpunkte, weil ein Experiment in den
     * Jahren 1700-1712 zur schrittweisen Einf&uuml;hrung des gregorianischen Kalenders mi&szlig;lang. </p>
     *
     * @return  swedish chronological history
     * @since   3.0
     */
    public static ChronoHistory ofSweden() {

        return SWEDEN;

    }

    /**
     * <p>Is given historical date valid? </p>
     *
     * <p>If the argument is {@code null} then this method returns {@code false}. </p>
     *
     * @param   date    historical calendar date to be checked, maybe {@code null}
     * @return  {@code false} if given date is invalid else {@code true}
     * @since   3.0
     */
    /*[deutsch]
     * <p>Ist das angegebene historische g&uuml;ltig? </p>
     *
     * <p>Wenn das Argument {@code null} ist, liefert die Methode {@code false}. </p>
     *
     * @param   date    historical calendar date to be checked, maybe {@code null}
     * @return  {@code false} if given date is invalid else {@code true}
     * @since   3.0
     */
    public boolean isValid(HistoricDate date) {

        if (date == null) {
            return false;
        }

        Calculus algorithm = this.getAlgorithm(date);
        return ((algorithm != null) && algorithm.isValid(date));

    }

    /**
     * <p>Converts given historical date to an ISO-8601-date. </p>
     *
     * @param   date    historical calendar date
     * @return  ISO-8601-date (gregorian)
     * @since   3.0
     * @throws  IllegalArgumentException if given date is invalid or out of supported range
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene historische Datum zu einem ISO-8601-Datum. </p>
     *
     * @param   date    historical calendar date
     * @return  ISO-8601-date (gregorian)
     * @since   3.0
     * @throws  IllegalArgumentException if given date is invalid or out of supported range
     */
    public PlainDate convert(HistoricDate date) {

        Calculus algorithm = this.getAlgorithm(date);

        if (algorithm == null) {
            throw new IllegalArgumentException("Invalid historical date: " + date);
        }

        return PlainDate.of(algorithm.toMJD(date), EpochDays.MODIFIED_JULIAN_DATE);

    }

    /**
     * <p>Converts given ISO-8601-date to a historical date. </p>
     *
     * @param   date    ISO-8601-date (gregorian)
     * @return  historical calendar date
     * @throws  IllegalArgumentException if given date is out of supported range
     * @since   3.0
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene ISO-8601-Datum zu einem historischen Datum. </p>
     *
     * @param   date    ISO-8601-date (gregorian)
     * @return  historical calendar date
     * @throws  IllegalArgumentException if given date is out of supported range
     * @since   3.0
     */
    public HistoricDate convert(PlainDate date) {

        long mjd = date.get(EpochDays.MODIFIED_JULIAN_DATE);

        for (int i = this.events.size() - 1; i >= 0; i--) {
            CutOverEvent event = this.events.get(i);

            if (mjd >= event.start) {
                return event.algorithm.fromMJD(mjd);
            }
        }

        return this.getJulianAlgorithm().fromMJD(mjd);

    }

    /**
     * <p>Yields the variant of a historic calendar. </p>
     *
     * @return  text describing the internal state
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die Variante eines historischen Kalenders. </p>
     *
     * @return  text describing the internal state
     * @since   3.11/4.8
     */
    public String getVariant() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.variant.name());

        switch (this.variant){
            case PROLEPTIC_GREGORIAN:
            case PROLEPTIC_JULIAN:
                sb.append(":no-cutover");
                break;
            case INTRODUCTION_ON_1582_10_15:
            case SINGLE_CUTOVER_DATE:
                sb.append(":cutover=");
                sb.append(this.getGregorianCutOverDate());
                // fall-through
            default:
                sb.append(":ancient-julian-leap-years=");
                if (this.ajly != null) {
                    int[] pattern = this.ajly.getPattern();
                    sb.append('[');
                    sb.append(pattern[0]);
                    for (int i = 1; i < pattern.length; i++) {
                        sb.append(',');
                        sb.append(pattern[i]);
                    }
                    sb.append(']');
                } else {
                    sb.append("[]");
                }
        }

        return sb.toString();

    }

    /**
     * <p>Yields the date of final introduction of gregorian calendar. </p>
     *
     * @return  ISO-8601-date (gregorian)
     * @throws  UnsupportedOperationException if this history is proleptic
     * @see     #hasGregorianCutOverDate()
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert das Datum der letztlichen Einf&uuml;hrung des gregorianischen Kalenders. </p>
     *
     * @return  ISO-8601-date (gregorian)
     * @throws  UnsupportedOperationException if this history is proleptic
     * @see     #hasGregorianCutOverDate()
     * @since   3.0
     */
    public PlainDate getGregorianCutOverDate() {

        long start = this.events.get(this.events.size() - 1).start;

        if (start == Long.MIN_VALUE) {
            throw new UnsupportedOperationException("Proleptic history without any gregorian reform date.");
        }

        return PlainDate.of(start, EpochDays.MODIFIED_JULIAN_DATE);

    }

    /**
     * <p>Determines if this history has at least one gregorian calendar reform date. </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     * @see     #getGregorianCutOverDate()
     */
    /*[deutsch]
     * <p>Ermittelt, ob diese {@code ChronoHistory} wenigstens eine gregorianische Kalenderreform kennt. </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     * @see     #getGregorianCutOverDate()
     */
    public boolean hasGregorianCutOverDate() {

        return (this.events.get(this.events.size() - 1).start > Long.MIN_VALUE);

    }

    /**
     * <p>Determines the length of given historical year in days. </p>
     *
     * @param   era         historical era
     * @param   yearOfEra   historical year of era
     * @return  length of historical year in days or {@code -1} if the length cannot be determined
     * @since   3.0
     */
    /*[deutsch]
     * <p>Bestimmt die L&auml;nge des angegebenen historischen Jahres in Tagen. </p>
     *
     * @param   era         historical era
     * @param   yearOfEra   historical year of era
     * @return  length of historical year in days or {@code -1} if the length cannot be determined
     * @since   3.0
     */
    public int getLengthOfYear(
        HistoricEra era,
        int yearOfEra
    ) {

        try {
            HistoricDate min = HistoricDate.of(era, yearOfEra, 1, 1);
            HistoricDate max = HistoricDate.of(era, yearOfEra, 12, 31);
            return (int) (CalendarUnit.DAYS.between(this.convert(min), this.convert(max)) + 1);
        } catch (RuntimeException re) {
            return -1; // only in very exotic circumstances (for example if given year is out of range)
        }

    }

    /**
     * <p>Yields the historic julian leap year pattern if available. </p>
     *
     * @return  sequence of historic julian leap years
     * @throws  UnsupportedOperationException if this history has not defined any historic julian leap years
     * @see     #hasAncientJulianLeapYears()
     * @see     #with(AncientJulianLeapYears)
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die Sequenz von historischen julianischen Schaltjahren, wenn verf&uuml;gbar. </p>
     *
     * @return  sequence of historic julian leap years
     * @throws  UnsupportedOperationException if this history has not defined any historic julian leap years
     * @see     #hasAncientJulianLeapYears()
     * @see     #with(AncientJulianLeapYears)
     * @since   3.11/4.8
     */
    public AncientJulianLeapYears getAncientJulianLeapYears() {

        if (this.ajly == null){
            throw new UnsupportedOperationException("No historic julian leap years were defined.");
        }

        return this.ajly;

    }

    /**
     * <p>Determines if this history has defined any historic julian leap year sequence. </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     * @see     #with(AncientJulianLeapYears)
     * @see     #getAncientJulianLeapYears()
     */
    /*[deutsch]
     * <p>Ermittelt, ob diese {@code ChronoHistory} eine spezielle Sequenz von historischen
     * julianischen Schaltjahren kennt. </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     * @see     #with(AncientJulianLeapYears)
     * @see     #getAncientJulianLeapYears()
     */
    public boolean hasAncientJulianLeapYears() {

        return (this.ajly != null);

    }

    /**
     * <p>Creates a copy of this history with given historic julian leap year sequence. </p>
     *
     * <p>This method has no effect if applied on {@code ChronoHistory.PROLEPTIC_GREGORIAN}
     * or  {@code ChronoHistory.PROLEPTIC_JULIAN}. </p>
     *
     * @param   ancientJulianLeapYears      sequence of historic julian leap years
     * @return  new history which starts at first of January in year BC 45
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Instanz mit den angegebenen historischen julianischen Schaltjahren. </p>
     *
     * <p>Diese Methode hat keine Auswirkung, wenn angewandt auf {@code ChronoHistory.PROLEPTIC_GREGORIAN}
     * oder  {@code ChronoHistory.PROLEPTIC_JULIAN}. </p>
     *
     * @param   ancientJulianLeapYears      sequence of historic julian leap years
     * @return  new history which starts at first of January in year BC 45
     * @since   3.11/4.8
     */
    public ChronoHistory with(AncientJulianLeapYears ancientJulianLeapYears) {

        if (ancientJulianLeapYears == null) {
            throw new NullPointerException("Missing ancient julian leap years.");
        } else if (!this.hasGregorianCutOverDate()) {
            return this;
        }

        return new ChronoHistory(this.variant, this.events, ancientJulianLeapYears);

    }

    /**
     * <p>Defines the element for the historical era. </p>
     *
     * <p>This element is applicable on all chronological types which have registered the element
     * {@link PlainDate#COMPONENT}. </p>
     *
     * @return  era-related element
     * @since   3.0
     * @see     PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    /*[deutsch]
     * <p>Definiert das Element f&uuml;r die historische &Auml;ra. </p>
     *
     * <p>Dieses Element ist auf alle chronologischen Typen anwendbar, die das Element
     * {@link PlainDate#COMPONENT} registriert haben. </p>
     *
     * @return  era-related element
     * @since   3.0
     * @see     PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    public TextElement<HistoricEra> era() {

        return this.eraElement;

    }

    /**
     * <p>Defines the element for the year of a given historical era. </p>
     *
     * <p>This element is applicable on all chronological types which have registered the element
     * {@link PlainDate#COMPONENT}. </p>
     *
     * @return  year-of-era-related element
     * @since   3.0
     * @see     PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    /*[deutsch]
     * <p>Definiert das Element f&uuml;r das Jahr einer historischen &Auml;ra. </p>
     *
     * <p>Dieses Element ist auf alle chronologischen Typen anwendbar, die das Element
     * {@link PlainDate#COMPONENT} registriert haben. </p>
     *
     * @return  year-of-era-related element
     * @since   3.0
     * @see     PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    public TextElement<Integer> yearOfEra() {

        return this.yearOfEraElement;

    }

    /**
     * <p>Defines the element for the historical month. </p>
     *
     * <p>This element is applicable on all chronological types which have registered the element
     * {@link PlainDate#COMPONENT}. </p>
     *
     * @return  month-related element
     * @since   3.0
     * @see     PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    /*[deutsch]
     * <p>Definiert das Element f&uuml;r den historischen Monat. </p>
     *
     * <p>Dieses Element ist auf alle chronologischen Typen anwendbar, die das Element
     * {@link PlainDate#COMPONENT} registriert haben. </p>
     *
     * @return  month-related element
     * @since   3.0
     * @see     PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    public TextElement<Integer> month() {

        return this.monthElement;

    }

    /**
     * <p>Defines the element for the historical day of month. </p>
     *
     * <p>This element is applicable on all chronological types which have registered the element
     * {@link PlainDate#COMPONENT}. </p>
     *
     * @return  day-of-month-related element
     * @since   3.0
     * @see     PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    /*[deutsch]
     * <p>Definiert das Element f&uuml;r den historischen Tag des Monats. </p>
     *
     * <p>Dieses Element ist auf alle chronologischen Typen anwendbar, die das Element
     * {@link PlainDate#COMPONENT} registriert haben. </p>
     *
     * @return  day-of-month-related element
     * @since   3.0
     * @see     PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    public TextElement<Integer> dayOfMonth() {

        return this.dayOfMonthElement;

    }

    /**
     * <p>Yields all associated elements. </p>
     *
     * @return  unmodifiable set of historical elements
     * @since   3.1
     */
    /*[deutsch]
     * <p>Liefert alle zugeh&ouml;rigen historischen Elemente. </p>
     *
     * @return  unmodifiable set of historical elements
     * @since   3.1
     */
    public Set<ChronoElement<?>> getElements() {

        return this.elements;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ChronoHistory) {
            ChronoHistory that = (ChronoHistory) obj;

            if (this.variant == that.variant) {
                if (isEqual(this.ajly, that.ajly)) {
                    return (
                        (this.variant != HistoricVariant.SINGLE_CUTOVER_DATE)
                        || (this.events.get(0).start == that.events.get(0).start)
                    );
                }
            }
        }

        return false;

    }

    @Override
    public int hashCode() {

        if (this.variant == HistoricVariant.SINGLE_CUTOVER_DATE) {
            long h = this.events.get(0).start;
            return (int) (h ^ (h << 32));
        }

        return this.variant.hashCode();

    }

    public String toString() {

        String result;

        switch (this.variant) {
            case PROLEPTIC_GREGORIAN:
                result = "ChronoHistory[PROLEPTIC-GREGORIAN]";
                break;
            case PROLEPTIC_JULIAN:
                result = "ChronoHistory[PROLEPTIC-JULIAN]";
                break;
            case SWEDEN:
                result = "ChronoHistory[SWEDEN]";
                break;
            default:
                PlainDate date = this.getGregorianCutOverDate();
                result = "ChronoHistory[" + date + "]";
        }

        if (this.ajly != null) {
            result = result + " with ancient julian leap years: " + this.ajly;
        }

        return result;

    }

    /**
     * <p>Yields the proper calendar algorithm. </p>
     *
     * @param   date    historical date
     * @return  appropriate calendar algorithm or {@code null} if not found (in case of an invalid date)
     * @since   3.0
     */
    Calculus getAlgorithm(HistoricDate date) {

        for (int i = this.events.size() - 1; i >= 0; i--) {
            CutOverEvent event = this.events.get(i);

            if (date.compareTo(event.dateAtCutOver) >= 0) {
                return event.algorithm;
            } else if (date.compareTo(event.dateBeforeCutOver) > 0) {
                return null; // gap at cutover
            }
        }

        return this.getJulianAlgorithm();

    }

    /**
     * <p>Adjusts given historical date with respect to actual maximum of day-of-month if necessary. </p>
     *
     * @param   date    historical date
     * @return  adjusted date
     * @throws  IllegalArgumentException if argument is out of range
     * @since   3.0
     */
    HistoricDate adjustDayOfMonth(HistoricDate date) {

        Calculus algorithm = this.getAlgorithm(date);

        if (algorithm == null) {
            return date; // gap at cutover => let it be unchanged
        }

        int max = algorithm.getMaximumDayOfMonth(date);

        if (max < date.getDayOfMonth()) {
            return HistoricDate.of(date.getEra(), date.getYearOfEra(), date.getMonth(), max);
        } else {
            return date;
        }

    }

    /**
     * <p>Returns the list of all associated cutover events. </p>
     *
     * @return  unmodifiable list
     * @since   3.0
     */
    List<CutOverEvent> getEvents() {

        return this.events;

    }

    /**
     * Used in serialization.
     *
     * @return  enum
     */
    HistoricVariant getHistoricVariant() {

        return this.variant;

    }

    private Calculus getJulianAlgorithm() {

        if (this.ajly != null) {
            return this.ajly.getCalculus();
        }

        return CalendarAlgorithm.JULIAN;

    }

    private static boolean isEqual(
        Object a1,
        Object a2
    ) {

        return ((a1 == null) ? (a2 == null) : a1.equals(a2));

    }

    private static void check(long mjd) {

        if (mjd < EARLIEST_CUTOVER) {
            throw new IllegalArgumentException("Gregorian calendar did not exist before 1582-10-15");
        }

    }

    private static ChronoHistory ofGregorianReform(long mjd) {

        return new ChronoHistory(
            ((mjd == EARLIEST_CUTOVER)
                ? HistoricVariant.INTRODUCTION_ON_1582_10_15
                : HistoricVariant.SINGLE_CUTOVER_DATE),
            Collections.singletonList(
                new CutOverEvent(mjd, CalendarAlgorithm.JULIAN, CalendarAlgorithm.GREGORIAN)));

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. The first byte contains in the four
     *              most significant bits the type-ID {@code 2}. The following
     *              bits 4-7 contain the variant of history. The variant is usually
     *              zero, but for PROLEPTIC_GREGORIAN 1, for PROLEPTIC_JULIAN 2,
     *              for SWEDEN 4 and for the first gregorian reform 7. If the
     *              variant is zero then the cutover date in question will be
     *              written as long (modified julian date) into the stream. Finally
     *              the sequence of eventually set ancient julian leap years will be
     *              written as int-array (length followed by list of extended years).
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.VERSION_2);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
