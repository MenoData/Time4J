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

import net.time4j.PlainDate;
import net.time4j.engine.EpochDays;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * <p>Represents the chronological history of calendar reforms in a given region. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 * @doctags.concurrency <immutable>
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Geschichte der Kalenderreformen in einer gegebenen Region. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 * @doctags.concurrency <immutable>
 */
public final class ChronoHistory {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>The Swedish calendar has three cutover dates due to a failed experiment
     * when switching to gregorian calendar in the years 1700-1712 step by step. </p>
     */
    /*[deutsch]
     * <p>Der schwedische Kalender hat drei Umstellungszeitpunkte, weil ein Experiment in den
     * Jahren 1700-1712 zur schrittweisen Einf&uuml;hrung des gregorianischen Kalenders mi&szlig;lang. </p>
     */
    public static final ChronoHistory SWEDEN;

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

    static {
        List<CutOverEvent> events = new ArrayList<CutOverEvent>();
        events.add(new CutOverEvent(-57959, CalendarAlgorithm.JULIAN, CalendarAlgorithm.SWEDISH)); // 1700-03-01
        events.add(new CutOverEvent(-53575, CalendarAlgorithm.SWEDISH, CalendarAlgorithm.JULIAN)); // 1712-03-01
        events.add(new CutOverEvent(-38611, CalendarAlgorithm.JULIAN, CalendarAlgorithm.GREGORIAN)); // 1753-03-01
        SWEDEN = new ChronoHistory(Collections.unmodifiableList(events));

        PROLEPTIC_GREGORIAN =
            new ChronoHistory(
                Collections.singletonList(
                    new CutOverEvent(Long.MIN_VALUE, CalendarAlgorithm.GREGORIAN, CalendarAlgorithm.GREGORIAN)));

        PROLEPTIC_JULIAN =
            new ChronoHistory(
                Collections.singletonList(
                    new CutOverEvent(Long.MIN_VALUE, CalendarAlgorithm.JULIAN, CalendarAlgorithm.JULIAN)));

        EARLIEST_CUTOVER = PlainDate.of(1582, 10, 15).get(EpochDays.MODIFIED_JULIAN_DATE);
        INTRODUCTION_BY_POPE_GREGOR = ChronoHistory.ofGregorianReform(EARLIEST_CUTOVER);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final List<CutOverEvent> events;

    //~ Konstruktoren -----------------------------------------------------

    private ChronoHistory(List<CutOverEvent> events) {
        super();

        if (events.isEmpty()) {
            throw new IllegalArgumentException(
                "At least one cutover event must be present in chronological history.");
        }

        this.events = events;

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
     * @param   start   calendar date when the gregorian calendar was introduced
     * @return  new chronological history with only one cutover from julian to gregorian calendar
     * @throws  IllegalArgumentException if given date is before first introduction of gregorian calendar on 1582-10-15
     * @see     #ofFirstGregorianReform()
     * @since   3.0
     */
    /*[deutsch]
     * <p>Beschreibt die Umstellung vom julianischen zum gregorianischen Kalender am angegebenen Datum. </p>
     *
     * @param   start   calendar date when the gregorian calendar was introduced
     * @return  new chronological history with only one cutover from julian to gregorian calendar
     * @throws  IllegalArgumentException if given date is before first introduction of gregorian calendar on 1582-10-15
     * @see     #ofFirstGregorianReform()
     * @since   3.0
     */
    public static ChronoHistory ofGregorianReform(PlainDate start) {

        long mjd = start.get(EpochDays.MODIFIED_JULIAN_DATE);

        if (mjd < EARLIEST_CUTOVER) {
            throw new IllegalArgumentException("Gregorian calendar did not exist before 1582-10-15");
        } else if (mjd == EARLIEST_CUTOVER) {
            return INTRODUCTION_BY_POPE_GREGOR;
        }

        return ChronoHistory.ofGregorianReform(mjd);

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

        CalendarAlgorithm algorithm = null;

        for (int i = this.events.size() - 1; i >= 0; i--) {
            CutOverEvent event = this.events.get(i);
            algorithm = event.algorithm;
            if (date.compareTo(event.dateAtCutOver) >= 0) {
                return algorithm.isValid(date);
            } else if (date.compareTo(event.dateBeforeCutOver) > 0) {
                return false; // gap at cutover
            }
        }

        return CalendarAlgorithm.JULIAN.isValid(date);

    }

    /**
     * <p>Converts given historical date to an ISO-8601-date. </p>
     *
     * @param   date    historical calendar date
     * @return  ISO-8601-date (gregorian)
     * @since   3.0
     * @throws  IllegalArgumentException if given date is invalid
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene historische Datum zu einem ISO-8601-Datum. </p>
     *
     * @param   date    historical calendar date
     * @return  ISO-8601-date (gregorian)
     * @since   3.0
     * @throws  IllegalArgumentException if given date is invalid
     */
    public PlainDate convert(HistoricDate date) {

        CalendarAlgorithm algorithm = null;

        for (int i = this.events.size() - 1; i >= 0; i--) {
            CutOverEvent event = this.events.get(i);
            algorithm = event.algorithm;
            if (date.compareTo(event.dateAtCutOver) >= 0) {
                return PlainDate.of(algorithm.toMJD(date), EpochDays.MODIFIED_JULIAN_DATE);
            } else if (date.compareTo(event.dateBeforeCutOver) > 0) {
                throw new IllegalArgumentException("Invalid historical date: " + date);
            }
        }

        return PlainDate.of(CalendarAlgorithm.JULIAN.toMJD(date), EpochDays.MODIFIED_JULIAN_DATE);

    }

    /**
     * <p>Converts given ISO-8601-date to a historical date. </p>
     *
     * @param   date    ISO-8601-date (gregorian)
     * @return  historical calendar date
     * @since   3.0
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene ISO-8601-Datum zu einem historischen Datum. </p>
     *
     * @param   date    ISO-8601-date (gregorian)
     * @return  historical calendar date
     * @since   3.0
     */
    public HistoricDate convert(PlainDate date) {

        long mjd = date.get(EpochDays.MODIFIED_JULIAN_DATE);
        CalendarAlgorithm algorithm = null;

        for (int i = this.events.size() - 1; i >= 0; i--) {
            CutOverEvent event = this.events.get(i);
            algorithm = event.algorithm;
            if (mjd >= event.start) {
                return algorithm.fromMJD(mjd);
            }
        }

        return CalendarAlgorithm.JULIAN.fromMJD(mjd);

    }

    /**
     * <p>Yields the date of final introduction of gregorian calendar. </p>
     *
     * @return  ISO-8601-date (gregorian)
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert das Datum der letztlichen Einf&uuml;hrung des gregorianischen Kalenders. </p>
     *
     * @return  ISO-8601-date (gregorian)
     * @since   3.0
     */
    public PlainDate getGregorianCutOverDate() {

        return PlainDate.of(this.events.get(this.events.size() - 1).start, EpochDays.MODIFIED_JULIAN_DATE);

    }

    private static ChronoHistory ofGregorianReform(long mjd) {

        return new ChronoHistory(
            Collections.singletonList(
                new CutOverEvent(mjd, CalendarAlgorithm.JULIAN, CalendarAlgorithm.GREGORIAN)));

    }

}
