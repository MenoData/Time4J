/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DaylightSavingRule.java) is part of project Time4J.
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

package net.time4j.tz.model;

import net.time4j.ClockUnit;
import net.time4j.DayCycles;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.base.GregorianDate;
import net.time4j.format.CalendarType;


/**
 * <p>Defines a yearly pattern when and how there is a switch from winter
 * to summer time and vice versa. </p>
 *
 * <p>This rule describes when such a switch happens. It also determines
 * the DST-offset. For every rule instance, a {@code ZonalTransition} can
 * be created just by indicating the appropriate year and standard offset.
 * The change from winter to summer time and back is usually expressed
 * by two rule instances. </p>
 *
 * <p>Note: The term &quot;year&quot; denotes the year in any calendar which
 * is not necessarily the gregorian one. Subclasses need to define the
 * calendar type and some calendar-specific year conversions. If subclasses
 * also want to be serializable then they have to apply the
 * <i>serialization proxy pattern</i> described by Joshua Bloch. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
/*[deutsch]
 * <p>Definiert ein j&auml;hrliches Muster, wann und wie im Jahr eine Umstellung
 * von Winter- zu Sommerzeit oder zur&uuml;ck stattfindet. </p>
 *
 * <p>Dieses Muster beschreibt zum einen, wie ein solcher Umstellungszeitpunkt
 * festgelegt werden kann. Au&szlig;erdem wird ein DST-Offset festgelegt.
 * Somit kann nur mit der zus&auml;tzlichen Angabe eines Standard-Offsets pro
 * Jahr genau eine {@code ZonalTransition} erzeugt werden. Der Wechsel von
 * Winter- zu Sommerzeit und zur&uuml;ck wird im allgemeinen durch zwei
 * Instanzen dieser Klasse ausgedr&uuml;ckt. </p>
 *
 * <p>Hinweis: Der Begriff &quot;year&quot; zeigt das Jahr in irgendeinem
 * Kalender an, der nicht notwendig der gregorianische Kalender sein mu&szlig;.
 * Subklassen m&uuml;ssen den Kalendertyp und einige kalenderspezifische
 * Jahreskonversionen definieren. Wenn Subklassen auch serialisierbar sein
 * wollen, m&uuml;ssen sie das <i>serialization proxy pattern</i> realisieren,
 * das von Joshua Bloch beschrieben worden ist. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
public abstract class DaylightSavingRule {

    //~ Instanzvariablen --------------------------------------------------

    private transient final long dayOverflow;
    private transient final PlainTime timeOfDay;
    private transient final OffsetIndicator indicator;
    private transient final int savings;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>For non-standard subclasses only. </p>
     *
     * @param   timeOfDay   time of day in seconds after midnight when the rule switches the offset
     * @param   indicator   offset indicator
     * @param   savings     daylight saving offset in effect after this rule
     * @throws  IllegalArgumentException if the last argument is out of range
     */
    /*[deutsch]
     * <p>Nur f&uuml;r nicht-standardisierte Subklassen. </p>
     *
     * @param   timeOfDay   time of day in seconds after midnight when the rule switches the offset
     * @param   indicator   offset indicator
     * @param   savings     daylight saving offset in effect after this rule
     * @throws  IllegalArgumentException if the last argument is out of range
     */
    protected DaylightSavingRule(
        int timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super();

        if (indicator == null) {
            throw new NullPointerException("Missing offset indicator.");
        } else if ((savings != Integer.MAX_VALUE) && ((savings < -18 * 3600) || (savings > 18 * 3600))) {
            throw new IllegalArgumentException("DST out of range: " + savings);
        }

        if (timeOfDay == 86400) {
            this.dayOverflow = 0L;
            this.timeOfDay = PlainTime.midnightAtEndOfDay();
        } else {
            DayCycles cycles = PlainTime.midnightAtStartOfDay().roll(timeOfDay, ClockUnit.SECONDS);
            this.dayOverflow = cycles.getDayOverflow();
            this.timeOfDay = cycles.getWallTime();
        }

        this.indicator = indicator;
        this.savings = (savings == Integer.MAX_VALUE) ? 0 : savings; // for backwards compatibility

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines the date of time switch dependent on given year. </p>
     *
     * <p>The result must be interpreted by mean of {@link #getIndicator()}
     * in order to calculate the UTC date. </p>
     *
     * @param   year    reference year when a time switch happens
     * @return  calendar date of time switch
     * @throws  IllegalArgumentException if given year does not fit to this rule
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert das Datum der Zeitumstellung in Abh&auml;ngigkeit
     * vom angegebenen Jahr. </p>
     *
     * <p>Das Ergebnis ist mittels {@link #getIndicator()} geeignet
     * zu interpretieren, um das UTC-Datum zu bestimmen. </p>
     *
     * @param   year    Bezugsjahr, in dem eine Winter- oder
     *                  Sommerzeitumstellung stattfindet
     * @return  Datum der Umstellung
     * @throws  IllegalArgumentException wenn das Jahr nicht passt
     * @since   2.2
     */
    public abstract PlainDate getDate(int year);

    /**
     * <p>Determines the clock time when the switch from winter time to
     * summer time happens or vice versa. </p>
     *
     * <p>The result must be interpreted by mean of {@link #getIndicator()}
     * in order to calculate the UTC time. </p>
     *
     * @return  clock time of time switch in second precision
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert die Uhrzeit der Zeitumstellung. </p>
     *
     * <p>Das Ergebnis ist mittels {@link #getIndicator()} geeignet
     * zu interpretieren, um die UTC-Zeit zu bestimmen. </p>
     *
     * @return  Uhrzeit der Umstellung in second precision
     * @since   2.2
     */
    public final PlainTime getTimeOfDay() {

        return this.timeOfDay;

    }

    /**
     * <p>Yields the offset indicator which must be consulted when interpreting
     * the date and time of time switch in terms of UTC. </p>
     *
     * @return  OffsetIndicator
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert den Offset-Indikator, der zur Interpretation des Datums
     * und der Uhrzeit der Zeitumstellung im UTC-Kontext dient. </p>
     *
     * @return  OffsetIndicator
     * @since   2.2
     */
    public final OffsetIndicator getIndicator() {

        return this.indicator;

    }

    /**
     * <p>Yields the daylight saving amount after the time switch in seconds. </p>
     *
     * <p><strong>Important: </strong> This offset is not always positive but can also be zero or even negative. </p>
     *
     * @return  DST-Offset in seconds (without standard offset)
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert den DST-Offset nach der Umstellung in Sekunden. </p>
     *
     * <p><strong>Wichtig: </strong> Dieser Versatz ist nicht immer positiv, sondern kann auch null oder sogar
     * negativ sein. </p>
     *
     * @return  reiner DST-Offset in Sekunden (ohne Standard-Offset)
     * @since   2.2
     */
    public final int getSavings() {

        return this.savings;

    }

    /**
     * <p>Extracts the year from given epoch days. </p>
     *
     * @param   mjd     modified julian date
     * @return  year (maybe in a non-gregorian calendar)
     * @since   2.2
     */
    /*[deutsch]
     * <p>Ermittelt das kalenderspezifische Jahr auf Basis der angegebenen
     * Epochentage. </p>
     *
     * @param   mjd     modified julian date
     * @return  year (maybe in a non-gregorian calendar)
     * @since   2.2
     */
    protected abstract int toCalendarYear(long mjd);

    /**
     * <p>Extracts the year from given gregorian date. </p>
     *
     * @param   date    gregorian calendar date
     * @return  year (maybe in a non-gregorian calendar)
     * @since   2.2
     */
    /*[deutsch]
     * <p>Ermittelt das kalenderspezifische Jahr auf Basis des angegebenen
     * gregorianischen Kalenderdatums. </p>
     *
     * @param   date    gregorian calendar date
     * @return  year (maybe in a non-gregorian calendar)
     * @since   2.2
     */
    protected abstract int toCalendarYear(GregorianDate date);

    /**
     * <p>Determines the underlying calendar system. </p>
     *
     * @return  String describing the calendar
     * @throws  IllegalStateException if the subclass does not have any
     *          annotation of type {@link net.time4j.format.CalendarType}
     * @since   2.2
     */
    /*[deutsch]
     * <p>Bestimmt das zugrundeliegende Kalendersystem. </p>
     *
     * @return  String describing the calendar
     * @throws  IllegalStateException if the subclass does not have any
     *          annotation of type {@link net.time4j.format.CalendarType}
     * @since   2.2
     */
    protected String getCalendarType() {

        CalendarType ct = this.getClass().getAnnotation(CalendarType.class);

        if (ct == null) {
            throw new IllegalStateException(
                "Cannot find calendar type annotation: " + this.getClass());
        }

        return ct.value();

    }

    /**
     * Obtains the possible overflow in days when rolling the wall time.
     *
     * @return  day overflow
     * @since   5.0
     */
    /*[deutsch]
     * Liefert den m&ouml;glichen &Uuml;berlauf in Tagen an, wenn die Uhrzeit berechnet wird.
     *
     * @return  day overflow
     * @since   5.0
     */
    protected final long getDayOverflow() {

        return this.dayOverflow;

    }

    // benutzt in der Serialisierung
    int getType() {

        return 0; // default value for unknown type

    }

}
