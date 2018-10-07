/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalClock.java) is part of project Time4J.
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

import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.VariantSource;
import net.time4j.format.Attributes;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;


/**
 * <p>Represents a clock which yields the current local time according
 * to a timezone. </p>
 *
 * <p>This class is <i>immutable</i> as long as the underlying implementations
 * of time source and time zone are. </p>
 *
 * @author  Meno Hochschild
 * @see     SystemClock#inLocalView()
 * @see     SystemClock#inZonalView(TZID)
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Uhr, die die aktuelle lokale Zeit anzeigt. </p>
 *
 * <p>Diese Klasse ist solange <i>immutable</i> (unver&auml;nderlich), wie
 * die zugrundeliegenden Implementierungen der Zeitquelle und der Zeitzone
 * es sind. </p>
 *
 * @author  Meno Hochschild
 * @see     SystemClock#inLocalView()
 * @see     SystemClock#inZonalView(TZID)
 */
public final class ZonalClock {
    // --------------------
    // Implementation note:
    // --------------------
    // This class does not implement the interface TimeSource intentionally
    // because it is really designed for querying "zonal" times but not for
    // being injected into any test class or business object. Otherwise
    // zonal dependencies could be obfuscated from a user-perspective.
    // Instead users are strongly encouraged to use expressions like
    // SystemClock#inZonalView(TZID).

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ZonalClock SYSTEM = new ZonalClock();

    //~ Instanzvariablen --------------------------------------------------

    private final TimeSource<?> timeSource;
    private final Timezone timezone;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Constructs a new clock which can yield the current local time in
     * given timezone. </p>
     *
     * <p>Most users have no need to directly call this constructor. It is
     * mainly designed for being called by dedicated expressions like
     * {@code SystemClock.inZonalView(tzid)} etc. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Die meisten Anwender brauchen diesen Konstruktor nicht. Er ist
     * im wesentlichen f&uuml;r den Aufruf durch spezielle Ausdr&uuml;cke
     * wie {@code SystemClock.inZonalView(tzid)} etc. gedacht. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public ZonalClock(
        TimeSource<?> timeSource,
        TZID tzid
    ) {
        this(timeSource, Timezone.of(tzid));

    }

    /**
     * <p>Constructs a new clock which can yield the current local time in
     * given timezone. </p>
     *
     * <p>Most users have no need to directly call this constructor. It is
     * mainly designed for being called by dedicated expressions like
     * {@code SystemClock.inZonalView(tzid)} etc. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Die meisten Anwender brauchen diesen Konstruktor nicht. Er ist
     * im wesentlichen f&uuml;r den Aufruf durch spezielle Ausdr&uuml;cke
     * wie {@code SystemClock.inZonalView(tzid)} etc. gedacht. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public ZonalClock(
        TimeSource<?> timeSource,
        String tzid
    ) {
        this(timeSource, Timezone.of(tzid));

    }

    /**
     * <p>Constructs a new clock which can yield the current local time in
     * given timezone. </p>
     *
     * <p>Most users have no need to directly call this constructor. It is
     * mainly designed for being called by dedicated expressions like
     * {@code SystemClock.inLocalView()} etc. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tz          timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   3.22/4.18
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Die meisten Anwender brauchen diesen Konstruktor nicht. Er ist
     * im wesentlichen f&uuml;r den Aufruf durch spezielle Ausdr&uuml;cke
     * wie {@code SystemClock.inLocalView()} etc. gedacht. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tz          timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   3.22/4.18
     */
    public ZonalClock(
        TimeSource<?> timeSource,
        Timezone tz
    ) {

        if (timeSource == null) {
            throw new NullPointerException("Missing time source.");
        } else if (tz == null) {
            throw new NullPointerException("Missing timezone.");
        }

        this.timeSource = timeSource;
        this.timezone = tz;

    }

    private ZonalClock() {
        super();

        this.timeSource = SystemClock.INSTANCE;
        this.timezone = null;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the current date in the associated timezone. </p>
     *
     * <p>The result dynamically depends on the associated timezone meaning if and only if the underlying
     * timezone is the system timezone. </p>
     *
     * @return  calendar date representing today
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Datum in der assoziierten Zeitzone. </p>
     *
     * <p>Das Ergebnis h&auml;ngt genau dann dynamisch von der assoziierten Zeitzone ab, wenn die
     * System-Zeitzone vorliegt. </p>
     *
     * @return  calendar date representing today
     */
    public PlainDate today() {

        final UnixTime ut = this.timeSource.currentTime();
        Timezone tz = (this.timezone == null) ? Timezone.ofSystem() : this.timezone;
        return PlainDate.from(ut, tz.getOffset(ut));

    }

    /**
     * <p>Gets the current timestamp in the associated timezone. </p>
     *
     * <p>The result dynamically depends on the associated timezone meaning if and only if the underlying
     * timezone is the system timezone. </p>
     *
     * @return  current local timestamp
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle Zeit in der assoziierten Zeitzone. </p>
     *
     * <p>Das Ergebnis h&auml;ngt genau dann dynamisch von der assoziierten Zeitzone ab, wenn die
     * System-Zeitzone vorliegt. </p>
     *
     * @return  current local timestamp
     */
    public PlainTimestamp now() {

        final UnixTime ut = this.timeSource.currentTime();
        Timezone tz = (this.timezone == null) ? Timezone.ofSystem() : this.timezone;
        return PlainTimestamp.from(ut, tz.getOffset(ut));

    }

    /**
     * <p>Gets the current timestamp in the associated timezone and given chronology. </p>
     *
     * <p>The result always dynamically depends on the associated timezone meaning if the underlying
     * timezone data change then the result will also change by next call. </p>
     *
     * <p>Code example: </p>
     *
     * <pre>
     *     System.out.println(SystemClock.inLocalView().now(PlainTime.axis())); // local wall time
     * </pre>
     *
     * @param   <T> generic type of chronology
     * @param   chronology  chronology to be used
     * @return  current local timestamp or date in given chronology
     * @throws  IllegalArgumentException if given chronology requires a calendar variant
     * @since   3.3/4.2
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle Zeit in der assoziierten Zeitzone und angegebenen Chronologie. </p>
     *
     * <p>Das Ergebnis h&auml;ngt immer dynamisch von der assoziierten Zeitzone ab. Wenn deren Daten sich
     * &auml;ndern, dann wird diese Methode beim n&auml;chsten Aufruf ein angepasstes Ergebnis liefern. </p>
     *
     * <p>Code-Beispiel: </p>
     *
     * <pre>
     *     System.out.println(SystemClock.inLocalView().now(PlainTime.axis())); // lokale Uhrzeit
     * </pre>
     *
     * @param   <T> generic type of chronology
     * @param   chronology  chronology to be used
     * @return  current local timestamp or date in given chronology
     * @throws  IllegalArgumentException if given chronology requires a calendar variant
     * @since   3.3/4.2
     */
    public <T extends ChronoEntity<T>> T now(Chronology<T> chronology) {

        Timezone tz = (this.timezone == null) ? Timezone.ofSystem() : this.timezone;
        Attributes attrs = new Attributes.Builder().setTimezone(tz.getID()).build();
        T result = chronology.createFrom(this.timeSource, attrs);

        if (result == null) {
            Class<?> type = chronology.getChronoType();
            if (CalendarVariant.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Calendar variant required: " + type.getName());
            } else {
                throw new IllegalArgumentException("Insufficient data: " + type.getName());
            }
        } else {
            return result;
        }

    }

    /**
     * <p>Gets the current timestamp in the associated timezone and given chronology taking into account
     * given calendar variant and start of day. </p>
     *
     * <p>The result always dynamically depends on the associated timezone meaning if the underlying
     * timezone data change then the result will also change by next call. </p>
     *
     * <p>Code example: </p>
     *
     * <pre>
     *     HijriCalendar hijriDate =
     *      CLOCK.now(
     *          HijriCalendar.family(),
     *          HijriCalendar.VARIANT_UMALQURA,
     *          StartOfDay.EVENING)
     *      .toDate();
     *     System.out.println(hijriDate); // AH-1436-10-02[islamic-umalqura]
     * </pre>
     *
     * <p>Note that this example is even true if the current timestamp is 2015-07-17T18:00 which would
     * normally map to AH-1436-10-01 (if the clock time is not considered). Reason is that the islamic
     * day starts on the evening of the previous day. </p>
     *
     * @param   <C> generic type of chronology
     * @param   family      calendar family to be used
     * @param   variant     calendar variant
     * @param   startOfDay  start of calendar day
     * @return  current general timestamp in given chronology
     * @throws  IllegalArgumentException if given variant is not supported
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle Zeit in der assoziierten Zeitzone und angegebenen Chronologie unter
     * Ber&uuml;cksichtigung von Kalendervariante und Start des Kalendertages. </p>
     *
     * <p>Das Ergebnis h&auml;ngt immer dynamisch von der assoziierten Zeitzone ab. Wenn deren Daten sich
     * &auml;ndern, dann wird diese Methode beim n&auml;chsten Aufruf ein angepasstes Ergebnis liefern. </p>
     *
     * <p>Code-Beispiel: </p>
     *
     * <pre>
     *     HijriCalendar hijriDate =
     *      CLOCK.now(
     *          HijriCalendar.family(),
     *          HijriCalendar.VARIANT_UMALQURA,
     *          StartOfDay.EVENING)
     *      .toDate();
     *     System.out.println(hijriDate); // AH-1436-10-02[islamic-umalqura]
     * </pre>
     *
     * <p>Zu beachten: Dieses Beispiel stimmt sogar dann, wenn der aktuelle Zeitstempel 2015-07-17T18:00 ist,
     * welcher normalerweise auf das islamische Datum AH-1436-10-01 abgebildet wird (wenn die Uhrzeit nicht
     * betrachtet wird), denn der islamische Tag beginnt am Abend des Vortags. </p>
     *
     * @param   <C> generic type of chronology
     * @param   family      calendar family to be used
     * @param   variant     calendar variant
     * @param   startOfDay  start of calendar day
     * @return  current general timestamp in given chronology
     * @throws  IllegalArgumentException if given variant is not supported
     * @since   3.8/4.5
     */
    public <C extends CalendarVariant<C>> GeneralTimestamp<C> now(
        CalendarFamily<C> family,
        String variant,
        StartOfDay startOfDay
    ) {

        Timezone tz = (this.timezone == null) ? Timezone.ofSystem() : this.timezone;
        Moment moment = Moment.from(this.timeSource.currentTime());
        return moment.toGeneralTimestamp(family, variant, tz.getID(), startOfDay);

    }

    /**
     * <p>Equivalent to {@code now(chronology, variantSource.getVariant(), startOfDay)}. </p>
     *
     * @param   <C> generic type of chronology
     * @param   family          calendar family to be used
     * @param   variantSource   source of calendar variant
     * @param   startOfDay      start of calendar day
     * @return  current local timestamp or date in given chronology
     * @throws  IllegalArgumentException if given variant is not supported
     * @see     #now(CalendarFamily, String, StartOfDay)
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>&Auml;quivalent to {@code now(chronology, variantSource.getVariant(), startOfDay)}. </p>
     *
     * @param   <C> generic type of chronology
     * @param   family          calendar family to be used
     * @param   variantSource   source of calendar variant
     * @param   startOfDay      start of calendar day
     * @return  current local timestamp or date in given chronology
     * @throws  IllegalArgumentException if given variant is not supported
     * @see     #now(CalendarFamily, String, StartOfDay)
     * @since   3.8/4.5
     */
    public <C extends CalendarVariant<C>> GeneralTimestamp<C> now(
        CalendarFamily<C> family,
        VariantSource variantSource,
        StartOfDay startOfDay
    ) {

        return this.now(family, variantSource.getVariant(), startOfDay);

    }

    /**
     * <p>Gets the associated clock. </p>
     *
     * @return  time source
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Uhr. </p>
     *
     * @return  Zeitquelle
     */
    public TimeSource<?> getSource() {

        return this.timeSource;

    }

    /**
     * <p>Gets the associated timezone. </p>
     *
     * @return  timezone id
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Zeitzone. </p>
     *
     * @return  timezone id
     */
    public TZID getTimezone() {

        Timezone tz = (this.timezone == null) ? Timezone.ofSystem() : this.timezone;
        return tz.getID();

    }

    /**
     * <p>Zonale Uhr basierend auf den Systemeinstellungen beim Laden
     * dieser Klasse. </p>
     *
     * @return  local clock in default system timezone
     */
    static ZonalClock ofSystem() {

        return SYSTEM;

    }

}
