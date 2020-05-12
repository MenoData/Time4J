/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GeneralTimestamp.java) is part of project Time4J.
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

import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoException;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.VariantSource;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Represents a general timestamp consisting of a general date and a 24-hour-clock time. </p>
 *
 * <p>Note: This class is only relevant for non-gregorian use-cases. Most users will use {@link PlainTimestamp}
 * instead. </p>
 *
 * @param   <C> generic type of date component
 * @author  Meno Hochschild
 * @since   3.8/4.5
 */
/*[deutsch]
 * <p>Repr&auml;sentiert einen allgemeinen Zeitstempel, der aus einem allgemeinen Datum und einer 24-Stunden-Uhrzeit
 * zusammengesetzt ist. </p>
 *
 * <p>Hinweis: Diese Klasse ist nur f&uuml;r nicht-gregorianische Anwendungsf&auuml;lle von Belang. Die meisten
 * Anwender werden stattdessen {@link PlainTimestamp} nutzen. </p>
 *
 * @param   <C> generic type of date component
 * @author  Meno Hochschild
 * @since   3.8/4.5
 */
public final class GeneralTimestamp<C>
    implements ChronoDisplay, VariantSource {

    //~ Instanzvariablen --------------------------------------------------

    private final CalendarVariant<?> cv; // optional
    private final Calendrical<?, ?> ca; // optional
    private final PlainTime time;

    //~ Konstruktoren -----------------------------------------------------

    private GeneralTimestamp(
        CalendarVariant<?> cv,
        Calendrical<?, ?> ca,
        PlainTime time
    ) {
        super();

        if (time.getHour() == 24) { // NPE-check
            if (cv == null) {
                this.cv = null;
                this.ca = ca.plus(CalendarDays.of(1));
            } else {
                this.cv = cv.plus(CalendarDays.of(1));
                this.ca = null;
            }
            this.time = PlainTime.midnightAtStartOfDay();
        } else {
            this.cv = cv;
            this.ca = ca;
            this.time = time;
        }

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new timestamp consisting of a calendar variant and a time component. </p>
     *
     * @param <C>             generic type of date component
     * @param calendarVariant date component
     * @param time            time component
     * @return general timestamp
     * @since 3.8/4.5
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen Zeitstempel, der aus einer Kalendervariante und einer Uhrzeitkomponente besteht. </p>
     *
     * @param   <C> generic type of date component
     * @param   calendarVariant     date component
     * @param   time                time component
     * @return  general timestamp
     * @since   3.8/4.5
     */
    public static <C extends CalendarVariant<C>> GeneralTimestamp<C> of(
        C calendarVariant,
        PlainTime time
    ) {

        if (calendarVariant == null) {
            throw new NullPointerException("Missing date component.");
        }

        return new GeneralTimestamp<>(calendarVariant, null, time);

    }

    /**
     * <p>Creates a new timestamp consisting of a general date and a time component. </p>
     *
     * @param <C>         generic type of date component
     * @param calendrical date component
     * @param time        time component
     * @return general timestamp
     * @since 3.8/4.5
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen Zeitstempel, der aus einem allgemeinen Datum und einer Uhrzeitkomponente besteht. </p>
     *
     * @param   <C> generic type of date component
     * @param   calendrical         date component
     * @param   time                time component
     * @return  general timestamp
     * @since   3.8/4.5
     */
    public static <C extends Calendrical<?, C>> GeneralTimestamp<C> of(
        C calendrical,
        PlainTime time
    ) {

        if (calendrical == null) {
            throw new NullPointerException("Missing date component.");
        }

        return new GeneralTimestamp<>(null, calendrical, time);

    }

    /**
     * <p>Yields the date component. </p>
     *
     * @return date component
     * @since 3.8/4.5
     */
    /*[deutsch]
     * <p>Liefert die Datumskomponente. </p>
     *
     * @return  date component
     * @since   3.8/4.5
     */
    @SuppressWarnings("unchecked")
    public C toDate() {

        Object date = ((this.cv == null) ? this.ca : this.cv);
        return (C) date;

    }

    /**
     * <p>Yields the time component. </p>
     *
     * @return time component
     * @since 3.8/4.5
     */
    /*[deutsch]
     * <p>Liefert die Uhrzeitkomponente. </p>
     *
     * @return  time component
     * @since   3.8/4.5
     */
    public PlainTime toTime() {

        return this.time;

    }

    /**
     * <p>Subtracts given count of days from this general timestamp and return the result. </p>
     *
     * @param   days    the duration in days to be subtracted
     * @return  result of subtraction as changed copy, this instance remains unaffected
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.0
     */
    /*[deutsch]
     * <p>Zieht die angegebene Anzahl von Tagen von diesem allgemeinen Zeitstempel ab
     * und liefert das Subtraktionsergebnis. </p>
     *
     * @param   days    the duration in days to be subtracted
     * @return  result of subtraction as changed copy, this instance remains unaffected
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.0
     */
    public GeneralTimestamp<C> minus(CalendarDays days) {

        return this.plus(days.inverse());

    }

    /**
     * <p>Subtracts given count of days from this general timestamp and return the result. </p>
     *
     * <p><strong>Warning:</strong> If users also wish to take into account time zones then a conversion
     * of this general timestamp to a {@code Moment} must be done first before applying any time arithmetic. </p>
     *
     * @param   amount  the amount of units to be subtracted
     * @param   unit    the clock unit to be applied
     * @return  result of subtraction as changed copy, this instance remains unaffected
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #at(ZonalOffset, StartOfDay)
     * @see     #in(Timezone, StartOfDay)
     * @see     Moment#minus(long, Object) Moment.minus(long, TimeUnit)
     * @since   5.0
     */
    /*[deutsch]
     * <p>Zieht die angegebene Anzahl von Tagen von diesem allgemeinen Zeitstempel ab
     * und liefert das Subtraktionsergebnis. </p>
     *
     * <p><strong>Warnung:</strong> Wenn Anwender auch Zeitzoneneffekte ber&uuml;cksichtigen wollen, dann
     * mu&szlig; zuerst eine Umwandlung dieses allgemeinen Zeitstempels zu einem {@code Moment} geschehen,
     * bevor irgendeine Zeitarithmetik versucht wird. </p>
     *
     * @param   amount  the amount of units to be subtracted
     * @param   unit    the clock unit to be applied
     * @return  result of subtraction as changed copy, this instance remains unaffected
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #at(ZonalOffset, StartOfDay)
     * @see     #in(Timezone, StartOfDay)
     * @see     Moment#minus(long, Object) Moment.minus(long, TimeUnit)
     * @since   5.0
     */
    public GeneralTimestamp<C> minus(long amount, ClockUnit unit) {

        return this.plus(Math.negateExact(amount), unit);

    }

    /**
     * <p>Adds given count of days to this general timestamp and return the result. </p>
     *
     * @param   days    the duration in days to be added
     * @return  result of addition as changed copy, this instance remains unaffected
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.0
     */
    /*[deutsch]
     * <p>F&uuml;gt die angegebene Anzahl von Tagen zu diesem allgemeinen Zeitstempel hinzu
     * und liefert das Additionsergebnis. </p>
     *
     * @param   days    the duration in days to be added
     * @return  result of addition as changed copy, this instance remains unaffected
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.0
     */
    public GeneralTimestamp<C> plus(CalendarDays days) {

        CalendarVariant<?> ncv = (this.cv == null) ? null : this.cv.plus(days);
        Calendrical<?, ?> nca = (this.ca == null) ? null : this.ca.plus(days);
        return new GeneralTimestamp<>(ncv, nca, this.time);

    }

    /**
     * <p>Adds given count of clock units to this general timestamp and return the result. </p>
     *
     * <p><strong>Warning:</strong> If users also wish to take into account time zones then a conversion
     * of this general timestamp to a {@code Moment} must be done first before applying any time arithmetic. </p>
     *
     * @param   amount  the amount of units to be added
     * @param   unit    the clock unit to be applied
     * @return  result of addition as changed copy, this instance remains unaffected
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #at(ZonalOffset, StartOfDay)
     * @see     #in(Timezone, StartOfDay)
     * @see     Moment#plus(long, Object) Moment.plus(long, TimeUnit)
     * @since   5.0
     */
    /*[deutsch]
     * <p>F&uuml;gt die angegebene Anzahl von Uhrzeiteinheiten zu diesem allgemeinen Zeitstempel hinzu
     * und liefert das Additionsergebnis. </p>
     *
     * <p><strong>Warnung:</strong> Wenn Anwender auch Zeitzoneneffekte ber&uuml;cksichtigen wollen, dann
     * mu&szlig; zuerst eine Umwandlung dieses allgemeinen Zeitstempels zu einem {@code Moment} geschehen,
     * bevor irgendeine Zeitarithmetik versucht wird. </p>
     *
     * @param   amount  the amount of units to be added
     * @param   unit    the clock unit to be applied
     * @return  result of addition as changed copy, this instance remains unaffected
     * @throws  ArithmeticException in case of numerical overflow
     * @see     #at(ZonalOffset, StartOfDay)
     * @see     #in(Timezone, StartOfDay)
     * @see     Moment#plus(long, Object) Moment.plus(long, TimeUnit)
     * @since   5.0
     */
    public GeneralTimestamp<C> plus(long amount, ClockUnit unit) {

        DayCycles cycles = this.time.roll(amount, unit);
        CalendarDays days = CalendarDays.of(cycles.getDayOverflow());
        CalendarVariant<?> ncv = (this.cv == null) ? null : this.cv.plus(days);
        Calendrical<?, ?> nca = (this.ca == null) ? null : this.ca.plus(days);
        return new GeneralTimestamp<>(ncv, nca, cycles.getWallTime());

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof GeneralTimestamp) {
            GeneralTimestamp<?> that = GeneralTimestamp.class.cast(obj);
            if (!this.time.equals(that.time)) {
                return false;
            } else if (this.cv == null) {
                return ((that.cv == null) && this.ca.equals(that.ca));
            } else {
                return ((that.ca == null) && this.cv.equals(that.cv));
            }
        }

        return false;

    }

    @Override
    public int hashCode() {

        int h = (
            (this.cv == null)
            ? this.ca.hashCode()
            : this.cv.hashCode());
        return h + this.time.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (this.cv == null) {
            sb.append(this.ca);
        } else {
            sb.append(this.cv);
        }
        sb.append(this.time);
        return sb.toString();

    }

    /**
     * <p>Combines this general timestamp with given timezone offset to a global UTC-moment. </p>
     *
     * @param   offset      timezone offset
     * @param   startOfDay  start of day
     * @return  global UTC-moment based on this general timestamp interpreted in given timezone
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Kombiniert diesen allgemeinen Zeitstempel mit dem angegebenen Zeitzonen-Offset zu einem UTC-Moment. </p>
     *
     * @param   offset      timezone offset
     * @param   startOfDay  start of day
     * @return  global UTC-moment based on this general timestamp interpreted in given timezone
     * @since   3.8/4.5
     */
    public Moment at(
        ZonalOffset offset,
        StartOfDay startOfDay
    ) {

        PlainTimestamp tsp = (
            (this.cv == null)
            ? this.ca.transform(PlainDate.class).at(this.time)
            : this.cv.transform(PlainDate.class).at(this.time));

        // assuming that deviation will not change much by one day difference (approximation)
        int deviation = startOfDay.getDeviation(tsp.getCalendarDate(), offset);
        int comp = this.time.get(PlainTime.SECOND_OF_DAY).intValue() - deviation;

        if (comp >= 86400) { // happens if sunset is the start of day
            tsp = tsp.minus(1, CalendarUnit.DAYS);
        } else if (comp < 0) { // happens if sunrise is the start of day
            tsp = tsp.plus(1, CalendarUnit.DAYS);
        }

        return tsp.at(offset);

    }

    /**
     * <p>Combines this general timestamp with given timezone to a global UTC-moment. </p>
     *
     * @param   tz          timezone
     * @param   startOfDay  start of day
     * @return  global UTC-moment based on this general timestamp interpreted in given timezone
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Kombiniert diesen allgemeinen Zeitstempel mit der angegebenen Zeitzone zu einem UTC-Moment. </p>
     *
     * @param   tz          timezone
     * @param   startOfDay  start of day
     * @return  global UTC-moment based on this general timestamp interpreted in given timezone
     * @since   3.8/4.5
     */
    public Moment in(
        Timezone tz,
        StartOfDay startOfDay
    ) {

        PlainTimestamp tsp = (
            (this.cv == null)
            ? this.ca.transform(PlainDate.class).at(this.time)
            : this.cv.transform(PlainDate.class).at(this.time));

        // assuming that deviation will not change much by one day difference (approximation)
        int deviation = startOfDay.getDeviation(tsp.getCalendarDate(), tz.getID());
        int comp = this.time.get(PlainTime.SECOND_OF_DAY).intValue() - deviation;

        if (comp >= 86400) { // happens if sunset is the start of day
            tsp = tsp.minus(1, CalendarUnit.DAYS);
        } else if (comp < 0) { // happens if sunrise is the start of day
            tsp = tsp.plus(1, CalendarUnit.DAYS);
        }

        return tsp.in(tz);

    }

    @Override
    public boolean contains(ChronoElement<?> element) {

        return (element.isDateElement() ? this.toDate0().contains(element) : this.time.contains(element));

    }

    @Override
    public <V> V get(ChronoElement<V> element) {

        return (element.isDateElement() ? this.toDate0().get(element) : this.time.get(element));

    }

    @Override
    public int getInt(ChronoElement<Integer> element) {

        return (element.isDateElement() ? this.toDate0().getInt(element) : this.time.getInt(element));

    }

    @Override
    public <V> V getMinimum(ChronoElement<V> element) {

        return (element.isDateElement() ? this.toDate0().getMinimum(element) : this.time.getMinimum(element));

    }

    @Override
    public <V> V getMaximum(ChronoElement<V> element) {

        return (element.isDateElement() ? this.toDate0().getMaximum(element) : this.time.getMaximum(element));

    }

    @Override
    public boolean hasTimezone() {

        return false;

    }

    @Override
    public TZID getTimezone() {

        throw new ChronoException("Timezone not available: " + this);

    }

    @Override
    public String getVariant() {

        return ((this.cv == null) ? "" : this.cv.getVariant());

    }

    private ChronoDisplay toDate0() {

        Object date = ((this.cv == null) ? this.ca : this.cv);
        return (ChronoDisplay) date;

    }

}