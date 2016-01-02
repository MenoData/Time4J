/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Weekmodel.java) is part of project Time4J.
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
import net.time4j.base.MathUtils;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.NumericalElement;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.WeekdataProvider;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.time4j.PlainDate.CALENDAR_DATE;
import static net.time4j.PlainDate.WEEKDAY_IN_MONTH;
import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Defines rules for the localized handling of weekdays and calendar weeks
 * on the base of a seven-day-week. </p>
 *
 * <ul>
 *  <li>1st rule: Which day of week is the first day of calendar week?</li>
 *  <li>2nd rule: What is the minimum count of days in the first calendar
 *  week of the year?</li>
 * </ul>
 *
 * <p>Furthermore, a {@code Weekmodel} contains some week-related elements
 * which can be used in all types containing an ISO-8601-date
 * ({@code PlainTimestamp} and {@code PlainDate}). </p>
 *
 * @author      Meno Hochschild
 * @see         WeekdataProvider
 */
/*[deutsch]
 * <p>Definiert Regeln f&uuml;r den lokalisierten Umgang mit Wochentagen
 * und Kalenderwochen auf einer 7-Tage-Wochenbasis. </p>
 *
 * <ul>
 *  <li>1. Regel: Welcher Wochentag ist der erste Tag der Woche?</li>
 *  <li>2. Regel: Was ist die minimale Anzahl der Tage der ersten Woche
 *  des Kalendarjahres?</li>
 * </ul>
 *
 * <p>Au&szlig;erdem werden einige wochenbezogene Elemente zur Verf&uuml;gung
 * gestellt, die mit allen Klassen umgehen k&ouml;nnen, die ein ISO-Datum
 * enthalten ({@code PlainTimestamp} und {@code PlainDate}). </p>
 *
 * @author      Meno Hochschild
 * @see         WeekdataProvider
 */
public final class Weekmodel
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int CALENDAR_WEEK_OF_YEAR = 0;
    private static final int CALENDAR_WEEK_OF_MONTH = 1;
    private static final int BOUNDED_WEEK_OF_YEAR = 2;
    private static final int BOUNDED_WEEK_OF_MONTH = 3;

    private static final Map<Locale, Weekmodel> CACHE =
        new ConcurrentHashMap<Locale, Weekmodel>();

    /**
     * <p>Standard week rules as defined by ISO-8601. </p>
     *
     * <p>Monday is considered as first day of calendar week. And the first
     * calendar week of year must contain at least four days respective
     * contain the first Thursday of year. Saturday and Sunday are considered
     * as weekend. </p>
     */
    /*[deutsch]
     * <p>Standard-Wochenregeln f&uuml;r die ISO-8601-Norm. </p>
     *
     * <p>Nach der ISO-8601-Norm ist der Montag der erste Tag der Woche, und
     * die erste Kalenderwoche des Jahres mu&szlig; mindestens 4 Tage haben
     * bzw. den ersten Donnerstag des Jahres enthalten. Als Wochenende gelten
     * die Tage Samstag und Sonntag. </p>
     */
    public static final Weekmodel ISO =
        new Weekmodel(Weekday.MONDAY, 4, Weekday.SATURDAY, Weekday.SUNDAY);

    private static final WeekdataProvider LOCALIZED_WEEKDATA;

    static {
        WeekdataProvider tmp = null;

        for (WeekdataProvider p : ResourceLoader.getInstance().services(WeekdataProvider.class)) {
            tmp = p;
            break;
        }

        LOCALIZED_WEEKDATA = tmp;
    }

    private static final long serialVersionUID = 7794495882610436763L;

    //~ Instanzvariablen --------------------------------------------------

    // Zustand
    private transient final Weekday firstDayOfWeek;
    private transient final int minimalDaysInFirstWeek;
    private transient final Weekday startOfWeekend;
    private transient final Weekday endOfWeekend;

    // Elemente kompatibel zu PlainDate
    private transient final
        AdjustableElement<Integer, PlainDate> woyElement;
    private transient final
        AdjustableElement<Integer, PlainDate> womElement;
    private transient final
        AdjustableElement<Integer, PlainDate> boundWoyElement;
    private transient final
        AdjustableElement<Integer, PlainDate> boundWomElement;
    private transient final
        NavigableElement<Weekday> dayOfWeekElement;
    private transient final Set<ChronoElement<?>> elements;

    // Bedingungsausdruck
    private transient final ChronoCondition<GregorianDate> weekendCondition;

    //~ Konstruktoren -----------------------------------------------------

    private Weekmodel(
        Weekday firstDayOfWeek,
        int minimalDaysInFirstWeek,
        final Weekday startOfWeekend,
        final Weekday endOfWeekend
    ) {
        super();

        if (firstDayOfWeek == null) {
            throw new NullPointerException("Missing first day of week.");
        } else if (
            (minimalDaysInFirstWeek < 1)
            || (minimalDaysInFirstWeek > 7)
        ) {
            throw new IllegalArgumentException(
                "Minimal days in first week out of range: "
                + minimalDaysInFirstWeek);
        } else if (startOfWeekend == null) {
            throw new NullPointerException("Missing start of weekend.");
        } else if (endOfWeekend == null) {
            throw new NullPointerException("Missing end of weekend.");
        }

        this.firstDayOfWeek = firstDayOfWeek;
        this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
        this.startOfWeekend = startOfWeekend;
        this.endOfWeekend = endOfWeekend;

        this.woyElement =
            new CalendarWeekElement(
                "WEEK_OF_YEAR", CALENDAR_WEEK_OF_YEAR);
        this.womElement =
            new CalendarWeekElement(
                "WEEK_OF_MONTH", CALENDAR_WEEK_OF_MONTH);
        this.boundWoyElement =
            new CalendarWeekElement(
                "BOUNDED_WEEK_OF_YEAR", BOUNDED_WEEK_OF_YEAR);
        this.boundWomElement =
            new CalendarWeekElement(
                "BOUNDED_WEEK_OF_MONTH", BOUNDED_WEEK_OF_MONTH);
        this.dayOfWeekElement = new DayOfWeekElement();

        this.weekendCondition =
            new ChronoCondition<GregorianDate>() {
                @Override
                public boolean test(GregorianDate context) {
                    int y = context.getYear();
                    int m = context.getMonth();
                    int dom = context.getDayOfMonth();
                    Weekday wd =
                        Weekday.valueOf(GregorianMath.getDayOfWeek(y, m, dom));
                    return ((wd == startOfWeekend) || (wd == endOfWeekend));
                }
            };

        Set<ChronoElement<?>> set =
            new HashSet<ChronoElement<?>>();
        set.add(this.woyElement);
        set.add(this.womElement);
        set.add(this.dayOfWeekElement);
        set.add(this.boundWoyElement);
        set.add(this.boundWomElement);
        this.elements = Collections.unmodifiableSet(set);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new week model with the given rules and the
     * weekend-definition Saturday/Sunday. </p>
     *
     * @param   firstDayOfWeek          localized first day of week
     * @param   minimalDaysInFirstWeek  required minimum count of days for
     *                                  the first week of year in range (1-7)
     * @return  specific week model with weekend on saturday and sunday
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #of(Locale)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Wochenmodell mit den angegebenen Einstellungen
     * und der Wochenenddefinition Samstag/Sonntag. </p>
     *
     * @param   firstDayOfWeek          localized first day of week
     * @param   minimalDaysInFirstWeek  required minimum count of days for
     *                                  the first week of year in range (1-7)
     * @return  specific week model with weekend on saturday and sunday
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #of(Locale)
     */
    public static Weekmodel of(
        Weekday firstDayOfWeek,
        int minimalDaysInFirstWeek
    ) {

        return Weekmodel.of(
            firstDayOfWeek,
            minimalDaysInFirstWeek,
            Weekday.SATURDAY,
            Weekday.SUNDAY
        );

    }

    /**
     * <p>Creates a new week model with the given rules. </p>
     *
     * @param   firstDayOfWeek          localized first day of week
     * @param   minimalDaysInFirstWeek  required minimum count of days for
     *                                  the first week of year in range (1-7)
     * @param   startOfWeekend          first day of weekend
     * @param   endOfWeekend            last day of weekend
     * @return  specific week model
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #of(Locale)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Wochenmodell mit den angegebenen Einstellungen. </p>
     *
     * @param   firstDayOfWeek          localized first day of week
     * @param   minimalDaysInFirstWeek  required minimum count of days for
     *                                  the first week of year in range (1-7)
     * @param   startOfWeekend          first day of weekend
     * @param   endOfWeekend            last day of weekend
     * @return  specific week model
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #of(Locale)
     */
    public static Weekmodel of(
        Weekday firstDayOfWeek,
        int minimalDaysInFirstWeek,
        Weekday startOfWeekend,
        Weekday endOfWeekend
    ) {

        if (
            (firstDayOfWeek == Weekday.MONDAY)
            && (minimalDaysInFirstWeek == 4)
            && (startOfWeekend == Weekday.SATURDAY)
            && (endOfWeekend == Weekday.SUNDAY)
        ) {
            return Weekmodel.ISO;
        }

        return new Weekmodel(
            firstDayOfWeek,
            minimalDaysInFirstWeek,
            startOfWeekend,
            endOfWeekend
        );

    }

    /**
     * <p>Gets a suitable weekmodel for the default locale of system. </p>
     *
     * <p>Note: In order to get a weekend definition deviating from the
     * standard Saturday + Sunday, the i18n-module must be present in
     * classpath since v2.2. </p>
     *
     * @return  week model in system locale
     * @see     Locale#getDefault()
     */
    /*[deutsch]
     * <p>Ermittelt ein geeignetes Wochenmodell f&uuml;r die aktuelle
     * Landeseinstellung des Systems. </p>
     *
     * <p>Hinweis: Damit eine von Samstag und Sonntag abweichende
     * lokalisierte Wochenenddefinition erzeugt werden kann, mu&szlig;
     * seit Version v2.2 das i18n-Modul im Klassenpfad vorhanden sein. </p>
     *
     * @return  week model in system locale
     * @see     Locale#getDefault()
     */
    public static Weekmodel ofSystem() {

        return Weekmodel.of(Locale.getDefault());

    }

    /**
     * <p>Gets a suitable weekmodel for the given country. </p>
     *
     * <p>Note: In order to get a weekend definition deviating from the
     * standard Saturday + Sunday, the i18n-module must be present in
     * classpath since v2.2. If the country-part of given locale is missing
     * then this method will just return {@link #ISO}. </p>
     *
     * @param   locale      country setting
     * @return  localized week model
     */
    /*[deutsch]
     * <p>Ermittelt ein geeignetes Wochenmodell f&uuml;r das angegebene
     * Land. </p>
     *
     * <p>Hinweis: Damit eine von Samstag und Sonntag abweichende
     * lokalisierte Wochenenddefinition erzeugt werden kann, mu&szlig;
     * seit Version v2.2 das i18n-Modul im Klassenpfad vorhanden sein.
     * Falls die Landeskomponente des Arguments fehlt, wird diese Methode
     * lediglich {@link #ISO} liefern. </p>
     *
     * @param   locale      country setting
     * @return  localized week model
     */
    public static Weekmodel of(Locale locale) {

        if (locale.getCountry().isEmpty()) {
            return Weekmodel.ISO;
        }

        Weekmodel model = CACHE.get(locale);

        if (model != null) {
            return model;
        }

        WeekdataProvider p = LOCALIZED_WEEKDATA;

        if (p == null) { // fallback
            GregorianCalendar gc = new GregorianCalendar(locale);
            int fd = gc.getFirstDayOfWeek();
            int firstDayOfWeek = ((fd == 1) ? 7 : (fd - 1));
            return Weekmodel.of(
                Weekday.valueOf(firstDayOfWeek),
                gc.getMinimalDaysInFirstWeek());
        }

        model =
            new Weekmodel(
                Weekday.valueOf(p.getFirstDayOfWeek(locale)),
                p.getMinimalDaysInFirstWeek(locale),
                Weekday.valueOf(p.getStartOfWeekend(locale)),
                Weekday.valueOf(p.getEndOfWeekend(locale))
            );

        if (CACHE.size() > 150) {
            CACHE.clear(); // Größenbegrenzung
        }

        CACHE.put(locale, model);
        return model;

    }

    /**
     * <p>Defines the first day of the calendar week in this model. </p>
     *
     * <p>The first day of week is not required to be identical with the
     * first working day. It rather marks the the first column a graphical
     * localized calendar. However, in ISO-8601 the first day of week and
     * the first working day (equal to first day after weekend) are
     * identical. </p>
     *
     * @return  start of week
     * @see     #getFirstWorkday()
     */
    /*[deutsch]
     * <p>Definiert den ersten Tag einer Kalenderwoche. </p>
     *
     * <p>Der erste Tag der Woche mu&szlig; nicht mit dem ersten Arbeitstag
     * einer Woche identisch sein. Vielmehr bezeichnet der erste Tag der
     * Woche die erste Spalte in einer graphischen Kalenderdarstellung.
     * Im ISO-8601-Standard sind allerdings der erste Tag der Woche und
     * der erste Arbeitstag identisch. </p>
     *
     * @return  start of week
     * @see     #getFirstWorkday()
     */
    public Weekday getFirstDayOfWeek() {

        return this.firstDayOfWeek;

    }

    /**
     * <p>Defines the minimum count of days the first calendar week of year
     * (or month) must contain. </p>
     *
     * <p>If this method yields {@code 1} then the first calendar week of
     * year always contains the first of January. If the return value is
     * {@code 7} instead then only the first full seven-day-week is the
     * first calendar week of year. In ISO-8601 the value is {@code 4}. </p>
     *
     * @return  required count of days for first week of year in the range (1-7)
     */
    /*[deutsch]
     * <p>Definiert die minimale Anzahl von Tagen, die die erste Kalenderwoche
     * eines Jahres oder Monats enthalten mu&szlig;. </p>
     *
     * <p>Bei einem Wert von {@code 1} enth&auml;lt die erste Kalenderwoche
     * des Jahres den 1. Januar, bei einem Wert von {@code 7} ist nur die
     * erste volle 7-Tage-Woche die erste Kalenderwoche des Jahres. Im
     * ISO-8601-Standard ist der Wert {@code 4}. </p>
     *
     * @return  required count of days for first week of year in the range (1-7)
     */
    public int getMinimalDaysInFirstWeek() {

        return this.minimalDaysInFirstWeek;

    }

    /**
     * <p>Defines the first day of the weekend. </p>
     *
     * <p>In ISO-8601 Saturday is considered as start of weekend (note: not
     * explicitly mentioned in ISO-paper). </p>
     *
     * @return  start of weekend
     */
    /*[deutsch]
     * <p>Definiert den ersten Tag des Wochenendes. </p>
     *
     * <p>Im ISO-8601-Standard ist der Samstag der Beginn des Wochenendes
     * (zu beachten: nicht explizit im ISO-Papier erw&auml;hnt). </p>
     *
     * @return  start of weekend
     */
    public Weekday getStartOfWeekend() {

        return this.startOfWeekend;

    }

    /**
     * <p>Defines the last day of weekend. </p>
     *
     * <p>In ISO-8601 Sunday is considered as end of weekend (note: not
     * explicitly mentioned in ISO-paper). </p>
     *
     * @return  end of weekend
     */
    /*[deutsch]
     * <p>Definiert den letzten Tag des Wochenendes. </p>
     *
     * <p>Im ISO-8601-Standard ist der Sonntag das Ende des Wochenendes
     * (zu beachten: nicht explizit im ISO-Papier erw&auml;hnt). </p>
     *
     * @return  end of weekend
     */
    public Weekday getEndOfWeekend() {

        return this.endOfWeekend;

    }

    /**
     * <p>Gets the first working day as first day after end of weekend. </p>
     *
     * <p>Note: The last working day of week is not well defined however
     * and needs to be defined by the application itself. For example
     * Saturday is considered as start of weekend but also handled as legal
     * working day in most countries. </p>
     *
     * @return  first day after weekend
     */
    /*[deutsch]
     * <p>Ermittelt den ersten Arbeitstag als den Tag nach dem Ende des
     * Wochenendes. </p>
     *
     * <p>Hinweis: Der letzte Arbeitstag der Woche als Gegenst&uuml;ck
     * zu dieser Methode ist in der Regel nicht eindeutig und daher von
     * Anwendungen selbst festzulegen. Zum Beispiel gilt in vielen L&auml;ndern
     * der Samstag zwar als der Start des Wochenendes, wird aber trotzdem
     * gesetzlich als Werktag behandelt. </p>
     *
     * @return  first day after weekend
     */
    public Weekday getFirstWorkday() {

        return this.getEndOfWeekend().next();

    }

    /**
     * <p>Defines an element for the calendar week of year with a localized
     * week number. </p>
     *
     * <p>In ISO-8601 the value range is given by {@code 1-52/53}. Reference
     * year is the week-based year, not the calendar year. Therefore the
     * maximum of this element is equivalent to the last calendar week of the
     * week-based year. Examples: </p>
     *
     * <pre>
     *  PlainDate date1 = PlainDate.of(2012, 12, 31); // Monday
     *  System.out.println(date1.get(Weekmodel.ISO.weekOfYear()));
     *  // Output: 1 (first calendar week of year 2013)
     *
     *  PlainDate date2 = PlainDate.of(2000, 1, 2); // Sunday
     *  System.out.println(date2.get(Weekmodel.ISO.weekOfYear()));
     *  // Output: 52 (last calendar week of year 1999)
     * </pre>
     *
     * <p>Note: This element uses the lenient mode if new values are to be set
     * ({@code isLenient() == true}). </p>
     *
     * @return  localized week of year
     */
    /*[deutsch]
     * <p>Liefert ein Element f&uuml;r die Woche des Jahres mit einer
     * lokalisierten Wochennummer. </p>
     *
     * <p>Im ISO-Wochenmodell ist der Wertebereich {@code 1-52/53}. Bezugsjahr
     * ist das wochenbasierte Jahr, nicht das Kalenderjahr. Daher ist der
     * Maximalwert dieses Elements gleichbedeutend mit der letzten Kalenderwoche
     * des wochenbasierten Jahres. Beispiele: </p>
     *
     * <pre>
     *  PlainDate date1 = PlainDate.of(2012, 12, 31); // Montag
     *  System.out.println(date1.get(Weekmodel.ISO.weekOfYear()));
     *  // Ausgabe: 1 (erste Kalenderwoche des Jahres 2013)
     *
     *  PlainDate date2 = PlainDate.of(2000, 1, 2); // Sonntag
     *  System.out.println(date2.get(Weekmodel.ISO.weekOfYear()));
     *  // Ausgabe: 52 (letzte Kalenderwoche des Jahres 1999)
     * </pre>
     *
     * <p>Achtung: Dieses Element arbeitet beim Setzen von Werten fehlertolerant
     * im Nachsichtigkeitsmodus ({@code isLenient() == true}). </p>
     *
     * @return  localized week of year
     */
    @FormattableElement(format = "w")
    public AdjustableElement<Integer, PlainDate> weekOfYear() {

        return this.woyElement;

    }

    /**
     * <p>Defines an element for the calendar week of month with a localized
     * week number. </p>
     *
     * <p>In ISO-8601 the value range is given by {@code 1-4/5}. The behaviour
     * is fully conform to the week of year - like in CLDR standard. </p>
     *
     * <p>Note: This element uses the lenient mode if new values are to be set
     * ({@code isLenient() == true}). </p>
     *
     * @return  localized week of month
     * @see     #weekOfYear()
     */
    /*[deutsch]
     * <p>Liefert ein Element f&uuml;r die Woche des Monats mit einer
     * lokalisierten Wochennummer. </p>
     *
     * <p>Im ISO-Wochenmodell ist der Wertebereich {@code 1-4/5}. Das Verhalten
     * ist vollkommen analog zur Woche des Jahres - in &Uuml;bereinstimmung mit
     * der CLDR-Norm. </p>
     *
     * <p>Achtung: Dieses Element arbeitet beim Setzen von Werten fehlertolerant
     * im Nachsichtigkeitsmodus ({@code isLenient() == true}). </p>
     *
     * @return  localized week of month
     * @see     #weekOfYear()
     */
    @FormattableElement(format = "W")
    public AdjustableElement<Integer, PlainDate> weekOfMonth() {

        return this.womElement;

    }

    /**
     * <p>Defines an element for the weekday with a localized day number in
     * the value range {@code 1-7}. </p>
     *
     * <p>This element defines localized weekday numbers in numerical formatting
     * and also a localized sorting order of weekdays, but still manages values
     * of type {@code Weekday}. However, the value range with its minimum and
     * maximum is localized, too, i.e. the element defines as minium the value
     * {@code getFirstDayOfWeek()}. </p>
     *
     * <p>In contrast the element {@link PlainDate#DAY_OF_WEEK} defines a
     * strict ISO-8601-conforming order and ISO-weekday-numbers. </p>
     *
     * @return  day of week with localized order
     */
    /*[deutsch]
     * <p>Liefert ein Element f&uuml;r den Wochentag mit einer lokalisierten
     * Wochentagsnummer im Wertebereich {@code 1-7}. </p>
     *
     * <p>Dieses Element definiert lokalisierte Wochentagsnummern in der
     * numerischen Formatierung und demzufolge auch eine lokalisierte
     * Wochentagssortierung, verwaltet aber selbst immer noch Enums vom Typ
     * {@code Weekday} als Werte. Jedoch ist der Wertebereich mitsamt seinem
     * Minimum und Maximum ebenfalls lokalisiert, d.h., das Element definiert
     * als Minimum den Wert {@code getFirstDayOfWeek()}. </p>
     *
     * <p>Im Gegensatz hierzu definiert das Element
     * {@link PlainDate#DAY_OF_WEEK} eine streng ISO-konforme Sortierung
     * nebst rein ISO-konformen Wochentagsnummern in der Formatierung. </p>
     *
     * @return  day of week with localized order
     */
    @FormattableElement(format = "e", standalone = "c")
    public NavigableElement<Weekday> localDayOfWeek() {

        return this.dayOfWeekElement;

    }

    /**
     * <p>Defines an element for the week of year with a localized week number,
     * constrained to the current calendar year. </p>
     *
     * <p>In ISO-8601-calendars the value range is {@code 0/1-52/53}, in other
     * weekmodels the maximum value can also be {@code 54}. In contrast to
     * {@link #weekOfYear()} this week can be shortened (less than seven days)
     * at the start or end of a calendar year. If the week normally belongs
     * to the previous year or to the following year then the bounded week
     * gets the value {@code 0} resp. for the end of year the incremented
     * maximum value. This behaviour is a simplifying deviation from
     * CLDR-standard. </p>
     *
     * <p>Note: This element uses the lenient mode if new values are to be set
     * ({@code isLenient() == true}). </p>
     *
     * @return  week of year within the limits of calendar year
     */
    /*[deutsch]
     * <p>Liefert ein Element f&uuml;r die Woche des Jahres mit einer
     * lokalisierten Wochennummer, begrenzt auf das aktuelle Jahr. </p>
     *
     * <p>In ISO-konformen Kalendersystemem ist der Wertebereich
     * {@code 0/1-52/53}, in anderen Wochendefinitionen kann der Maximalwert
     * auch {@code 54} sein. Im Unterschied zu {@link #weekOfYear()} kann
     * diese Woche am Anfang oder Ende eines Jahres verk&uuml;rzt sein,
     * weil kein Wochenumbruch stattfindet. Falls die Woche am Anfang
     * eines Jahres eigentlich in das Vorjahr bzw. die Woche am Ende
     * eines Jahres eigentlich in das Folgejahr geh&ouml;rt, bekommt
     * die Woche f&uuml;r den Jahresanfang den Wert {@code 0} bzw. f&uuml;r
     * das Jahresende den hochgez&auml;hlten Maximalwert. Dieses Verhalten
     * ist eine vereinfachende Abweichung vom CLDR-Standard. </p>
     *
     * <p>Achtung: Dieses Element arbeitet beim Setzen von Werten fehlertolerant
     * im Nachsichtigkeitsmodus ({@code isLenient() == true}). </p>
     *
     * @return  week of year within the limits of calendar year
     */
    public AdjustableElement<Integer, PlainDate> boundedWeekOfYear() {

        return this.boundWoyElement;

    }

    /**
     * <p>Defines an element for the week of month with a localized week number,
     * constrained to the current calendar month. </p>
     *
     * <p>In ISO-8601-calendars the value range is {@code 0/1-4/5}, in other
     * weekmodels the maximum value can also be {@code 6}. In contrast to
     * {@link #weekOfMonth()} this week can be shortened (less than seven days)
     * at the start or end of a calendar month. If the week normally belongs
     * to the previous month or to the following month then the bounded week
     * gets the value {@code 0} resp. for the end of month the incremented
     * maximum value. This behaviour is a simplifying deviation from
     * CLDR-standard but is the same as defined in the JDK. </p>
     *
     * <p>Note: This element uses the lenient mode if new values are to be set
     * ({@code isLenient() == true}). </p>
     *
     * @return  week of month within the limits of calendar month
     * @see     #boundedWeekOfYear()
     */
    /*[deutsch]
     * <p>Liefert ein Element f&uuml;r die Woche des Monats mit einer
     * lokalisierten Wochennummer, begrenzt auf den aktuellen Monat. </p>
     *
     * <p>In ISO-konformen Kalendersystemem ist der Wertebereich
     * {@code 0/1-4/5}, in anderen Wochendefinitionen kann der Maximalwert
     * auch {@code 6} sein. Im Unterschied zu {@link #weekOfMonth()} kann
     * diese Woche am Anfang oder Ende eines Monats verk&uuml;rzt sein,
     * weil kein Wochenumbruch stattfindet. Falls die Woche am Anfang
     * eines Monats eigentlich in den Vormonat bzw. die Woche am Ende
     * eines Monats eigentlich in den Folgemonat geh&ouml;rt, bekommt
     * die Woche f&uuml;r den Monatsanfang den Wert {@code 0} bzw. f&uuml;r
     * das Monatsende den hochgez&auml;hlten Maximalwert. Dieses Verhalten
     * entspricht der Woche des Monats in den traditionellen Kalenderklassen
     * des JDK, ist aber eine vereinfachende Abweichung vom CLDR-Standard. </p>
     *
     * <p>Achtung: Dieses Element arbeitet beim Setzen von Werten fehlertolerant
     * im Nachsichtigkeitsmodus ({@code isLenient() == true}). </p>
     *
     * @return  week of month within the limits of calendar month
     * @see     #boundedWeekOfYear()
     */
    public AdjustableElement<Integer, PlainDate> boundedWeekOfMonth() {

        return this.boundWomElement;

    }

    /**
     * <p>Defines a chronological condition if a date matches a weekend. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  PlainDate date = new PlainDate(2013, 3, 30); // Saturday
     *  System.out.println(date.matches(Weekmodel.ISO.weekend()));
     *  // Output: true
     *
     *  Locale yemen = new Locale("ar", "YE");
     *  System.out.println(date.matches(Weekmodel.of(yemen).weekend()));
     *  // Output: false (in Yemen the weekend matches Thursday and Friday)
     * </pre>
     *
     * @return  test for weekend
     */
    /*[deutsch]
     * <p>Definiert eine Bedingung, ob ein Datum am Wochenende liegt. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  PlainDate date = new PlainDate(2013, 3, 30); // Samstag
     *  System.out.println(date.matches(Weekmodel.ISO.weekend()));
     *  // Ausgabe: true
     *
     *  Locale yemen = new Locale("ar", "YE");
     *  System.out.println(date.matches(Weekmodel.of(yemen).weekend()));
     *  // Ausgabe: false (im Jemen ist das Wochenende Donnerstag und Freitag)
     * </pre>
     *
     * @return  test for weekend
     */
    public ChronoCondition<GregorianDate> weekend() {

        return this.weekendCondition;

    }

    /**
     * <p>Compares on the base of internal week rules. </p>
     */
    /*[deutsch]
     * <p>Vergleicht auf Basis der internen Wochenregeln. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof Weekmodel) {
            Weekmodel that = (Weekmodel) obj;
            return (
                (this.firstDayOfWeek == that.firstDayOfWeek)
                && (this.minimalDaysInFirstWeek == that.minimalDaysInFirstWeek)
                && (this.startOfWeekend == that.startOfWeekend)
                && (this.endOfWeekend == that.endOfWeekend)
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Defines the hash value. </p>
     */
    /*[deutsch]
     * <p>Liefert den Hash-Code. </p>
     */
    @Override
    public int hashCode() {

        return (
            17 * this.firstDayOfWeek.name().hashCode()
            + 37 * this.minimalDaysInFirstWeek
        );

    }

    /**
     * <p>Debugging-support. </p>
     */
    /*[deutsch]
     * <p>Debugging-Unterst&uuml;tzung. </p>
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[firstDayOfWeek=");
        sb.append(this.firstDayOfWeek);
        sb.append(",minimalDaysInFirstWeek=");
        sb.append(this.minimalDaysInFirstWeek);
        sb.append(",startOfWeekend=");
        sb.append(this.startOfWeekend);
        sb.append(",endOfWeekend=");
        sb.append(this.endOfWeekend);
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Liefert alle definierten chronologischen Elemente. </p>
     *
     * @return  unmodifiable set
     */
    Set<ChronoElement<?>> getElements() {

        return this.elements;

    }

    /**
     * <p>Ermittelt den Wochentag. </p>
     *
     * @param   utcDays   count of days relative to [1972-01-01]
     * @return  day of week as enum
     */
    static Weekday getDayOfWeek(long utcDays) {

        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. Two data bytes are used, sometimes
     *              also three. The first byte contains in the four most
     *              significant bits the type-ID {@code 3}. If the weekend
     *              is not saturday and sunday then the four least significant
     *              bits will be set to {@code 1}. The second byte has in the
     *              four most significant bits the first day of week, in the
     *              other four bits the minimum days of first calendar week.
     *              If there is no standard weekend then a third byte follows
     *              which contains in the four most significant bits the start
     *              and the four least significant bits the end of weekend.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  boolean isoWeekend = (
     *      (getStartOfWeekend() == Weekday.SATURDAY)
     *      &amp;&amp; (getEndOfWeekend() == Weekday.SUNDAY)
     *  );
     *
     *  int header = 3;
     *  header &lt;&lt;= 4;
     *  if (!isoWeekend) {
     *      header |= 1;
     *  }
     *  out.writeByte(header);
     *
     *  int state = getFirstDayOfWeek().getValue();
     *  state &lt;&lt;= 4;
     *  state |= getMinimalDaysInFirstWeek();
     *  out.writeByte(state);
     *
     *  if (!isoWeekend) {
     *      state = getStartOfWeekend().getValue();
     *      state &lt;&lt;= 4;
     *      state |= getEndOfWeekend().getValue();
     *      out.writeByte(state);
     *  }
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.WEEKMODEL_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    private class DayOfWeekElement
        extends AbstractDateElement<Weekday>
        implements NavigableElement<Weekday>,
                   NumericalElement<Weekday>,
                   TextElement<Weekday> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 1945670789283677398L;

        //~ Konstruktoren -------------------------------------------------

        DayOfWeekElement() {
            super("LOCAL_DAY_OF_WEEK");

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<Weekday> getType() {

            return Weekday.class;

        }

        @Override
        public char getSymbol() {

            return 'e';

        }

        @Override
        public int numerical(Weekday dayOfWeek) {

            return Integer.valueOf(dayOfWeek.getValue(Weekmodel.this));

        }

        @Override
        public int compare(
            ChronoDisplay o1,
            ChronoDisplay o2
        ) {

            int i1 = o1.get(this).getValue(Weekmodel.this);
            int i2 = o2.get(this).getValue(Weekmodel.this);
            return ((i1 < i2) ? -1 : ((i1 == i2) ? 0 : 1));

        }

        @Override
        public Weekday getDefaultMinimum() {

            return Weekmodel.this.getFirstDayOfWeek();

        }

        @Override
        public Weekday getDefaultMaximum() {

            return Weekmodel.this.getFirstDayOfWeek().roll(6);

        }

        @Override
        public boolean isDateElement() {

            return true;

        }

        @Override
        public boolean isTimeElement() {

            return false;

        }

        @Override
        public ElementOperator<PlainDate> setToNext(Weekday value) {

            return new NavigationOperator<Weekday>(
                this,
                ElementOperator.OP_NAV_NEXT,
                value
            );

        }

        @Override
        public ElementOperator<PlainDate> setToPrevious(Weekday value) {

            return new NavigationOperator<Weekday>(
                this,
                ElementOperator.OP_NAV_PREVIOUS,
                value
            );

        }

        @Override
        public ElementOperator<PlainDate> setToNextOrSame(Weekday value) {

            return new NavigationOperator<Weekday>(
                this,
                ElementOperator.OP_NAV_NEXT_OR_SAME,
                value
            );

        }

        @Override
        public ElementOperator<PlainDate> setToPreviousOrSame(Weekday value) {

            return new NavigationOperator<Weekday>(
                this,
                ElementOperator.OP_NAV_PREVIOUS_OR_SAME,
                value
            );

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException {

            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            buffer.append(this.accessor(attributes, oc).print(context.get(this)));

        }

        @Override
        public Weekday parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            int index = status.getIndex();
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            Weekday result = this.accessor(attributes, oc).parse(text, status, this.getType(), attributes);

            if ((result == null) && attributes.get(Attributes.PARSE_MULTIPLE_CONTEXT, Boolean.TRUE)) {
                status.setErrorIndex(-1);
                status.setIndex(index);
                oc = ((oc == OutputContext.FORMAT) ? OutputContext.STANDALONE : OutputContext.FORMAT);
                result = this.accessor(attributes, oc).parse(text, status, this.getType(), attributes);
            }

            return result;

        }

        @Override
        public boolean equals(Object obj) {

            return (
                super.equals(obj)
                && this.getModel().equals(((DayOfWeekElement) obj).getModel())
            );

        }

        @Override
        public int hashCode() {

            return 31 * super.hashCode() + 37 * this.getModel().hashCode();

        }

        @Override
        protected <T extends ChronoEntity<T>> ElementRule<T, Weekday> derive(Chronology<T> chronology) {

            if (chronology.isRegistered(CALENDAR_DATE)) {
                return new DRule<T>(this);
            } else {
                return null;
            }

        }

        @Override
        protected ChronoElement<?> getParent() {

            return PlainDate.DAY_OF_WEEK;

        }

        private TextAccessor accessor(
            AttributeQuery attributes,
            OutputContext oc
        ) {

            CalendarText cnames =
                CalendarText.getInstance(
                    attributes.get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE),
                    attributes.get(Attributes.LANGUAGE, Locale.ROOT));

            return cnames.getWeekdays(
                attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE),
                oc);

        }

        private Weekmodel getModel() {

            return Weekmodel.this;

        }

        private Object readResolve() throws ObjectStreamException {

            return Weekmodel.this.localDayOfWeek();

        }

    }

    private static class DRule<T extends ChronoEntity<T>>
        implements ElementRule<T, Weekday> {

        //~ Instanzvariablen ----------------------------------------------

        final DayOfWeekElement element;

        //~ Konstruktoren -------------------------------------------------

        private DRule(DayOfWeekElement element) {
            super();

            this.element = element;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            T context,
            Weekday value
        ) {

            return (value != null);

        }

        @Override
        public Weekday getMinimum(T context) {

            return this.element.getDefaultMinimum();

        }

        @Override
        public Weekday getMaximum(T context) {

            return this.element.getDefaultMaximum();

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            return this.getChild(context);

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            return this.getChild(context);

        }

        private ChronoElement<?> getChild(T context) {

            if (context.contains(PlainTime.WALL_TIME)) {
                return PlainTime.WALL_TIME;
            } else {
                return null;
            }

        }

        @Override
        public Weekday getValue(T context) {

            return getDayOfWeek(
                context.get(CALENDAR_DATE).getDaysSinceUTC());

        }

        @Override
        public T withValue(
            T context,
            Weekday value,
            boolean lenient
        ) {

            PlainDate date = context.get(CALENDAR_DATE);
            long utcDays = date.getDaysSinceUTC();
            Weekday current = getDayOfWeek(utcDays);

            if (value == current) {
                return context;
            }

            int old = current.getValue(this.element.getModel());
            int neu = value.getValue(this.element.getModel());
            date = date.withDaysSinceUTC(utcDays + neu - old);
            return context.with(CALENDAR_DATE, date);

        }

    }

    private class CalendarWeekElement
        extends AbstractDateElement<Integer>
        implements NumericalElement<Integer> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -5936254509996557266L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  0 = CALENDAR_WEEK_OF_YEAR, 1 = CALENDAR_WEEK_OF_MONTH,
         *          2 = BOUNDED_WEEK_OF_YEAR, 3 = BOUNDED_WEEK_OF_MONTH
         */
        private final int category;

        //~ Konstruktoren -------------------------------------------------

        CalendarWeekElement(
            String name,
            int category
        ) {
            super(name);

            this.category = category;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public char getSymbol() {

            switch (this.category) {
                case CALENDAR_WEEK_OF_YEAR:
                    return 'w';
                case CALENDAR_WEEK_OF_MONTH:
                    return 'W';
                default:
                    return super.getSymbol();
            }

        }

        @Override
        public Class<Integer> getType() {

            return Integer.class;

        }

        @Override
        public int numerical(Integer value) {

            return value.intValue();

        }

        @Override
        public Integer getDefaultMinimum() {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getDefaultMaximum() {

            return Integer.valueOf(this.isYearRelated() ? 52 : 5);

        }

        @Override
        public boolean isDateElement() {

            return true;

        }

        @Override
        public boolean isTimeElement() {

            return false;

        }

        @Override
        public boolean isLenient() {

            return true;

        }

        @Override
        public boolean equals(Object obj) {

            return (
                super.equals(obj)
                && this.getModel().equals(
                    ((CalendarWeekElement) obj).getModel())
            );

        }

        @Override
        public int hashCode() {

            return 31 * super.hashCode() + 37 * this.getModel().hashCode();

        }
        @Override
        protected ChronoElement<?> getParent() {

            return WEEKDAY_IN_MONTH; // Basiseinheit Wochen!

        }

        @Override
        protected <T extends ChronoEntity<T>>
            ElementRule<T, Integer> derive(Chronology<T> chronology) {

            if (chronology.isRegistered(CALENDAR_DATE)) {
                if (this.isBounded()) {
                    return new BWRule<T>(this);
                } else {
                    return new CWRule<T>(this);
                }
            }

            return null;

        }

        private Object readResolve() throws ObjectStreamException {

            Weekmodel model = this.getModel();

            switch (this.category) {
                case CALENDAR_WEEK_OF_YEAR:
                    return model.weekOfYear();
                case CALENDAR_WEEK_OF_MONTH:
                    return model.weekOfMonth();
                case BOUNDED_WEEK_OF_YEAR:
                    return model.boundedWeekOfYear();
                case BOUNDED_WEEK_OF_MONTH:
                    return model.boundedWeekOfMonth();
                default:
                    throw new InvalidObjectException(
                        "Unknown category: " + this.category);
            }

        }

        private Weekmodel getModel() {

            return Weekmodel.this;

        }

        private boolean isYearRelated() {

            return ((this.category % 2) == 0);

        }

        private boolean isBounded() {

            return (this.category >= 2);

        }

    }

    private static class CWRule<T extends ChronoEntity<T>>
        implements ElementRule<T, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final CalendarWeekElement owner;

        //~ Konstruktoren -------------------------------------------------

        private CWRule(CalendarWeekElement owner) {
            super();

            this.owner = owner;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getMinimum(T context) {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getMaximum(T context) {

            PlainDate date = context.get(CALENDAR_DATE);
            return Integer.valueOf(this.getMaxCalendarWeek(date));

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            return this.getChild();

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            return this.getChild();

        }

        private ChronoElement<?> getChild() {

            return this.owner.getModel().localDayOfWeek();

        }

        @Override
        public Integer getValue(T context) {

            PlainDate date = context.get(CALENDAR_DATE);
            return Integer.valueOf(this.getCalendarWeek(date));

        }

        @Override
        public boolean isValid(
            T context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();

            if (
                this.owner.isYearRelated()
                && (v >= 1)
                && (v <= 52)
            ) {
                return true;
            }

            if (!this.owner.isYearRelated() || (v == 53)) {
                PlainDate date = context.get(CALENDAR_DATE);
                return ((v >= 1) && (v <= this.getMaxCalendarWeek(date)));
            } else {
                return false;
            }

        }

        @Override
        public T withValue(
            T context,
            Integer value,
            boolean lenient
        ) {

            int v = value.intValue();
            PlainDate date = context.get(CALENDAR_DATE);

            if (
                !lenient
                && !this.isValid(context, value)
            ) {
                throw new IllegalArgumentException(
                    "Invalid value: " + v + " (context=" + context + ")");
            }

            return context.with(
                CALENDAR_DATE,
                this.setCalendarWeek(date, v)
            );


        }

        // letzte Kalenderwoche im Jahr/Monat
        private int getMaxCalendarWeek(PlainDate date) {

            int scaledDay = (
                this.owner.isYearRelated()
                ? date.getDayOfYear()
                : date.getDayOfMonth());
            int wCurrent = getFirstCalendarWeekAsDay(date, 0);

            if (wCurrent <= scaledDay) {
                int wNext =
                    getFirstCalendarWeekAsDay(date, 1)
                    + getLengthOfYM(date, 0);
                return (wNext - wCurrent) / 7;
            } else {
                int wPrevious = getFirstCalendarWeekAsDay(date, -1);
                wCurrent = wCurrent + getLengthOfYM(date, -1);
                return (wCurrent - wPrevious) / 7;
            }

        }

        // Ermittelt den Beginn der ersten Kalenderwoche eines Jahres/Monats
        // auf einer day-of-year/month-Skala (kann auch <= 0 sein).
        private int getFirstCalendarWeekAsDay(
            PlainDate date,
            int shift // -1 = Vorjahr/-monat, 0 = aktuell, +1 = Folgejahr/-monat
        ) {

            Weekday wd = this.getWeekdayStart(date, shift);
            Weekmodel model = this.owner.getModel();
            int dow = wd.getValue(model);

            return (
                (dow <= 8 - model.getMinimalDaysInFirstWeek())
                ? 2 - dow
                : 9 - dow
            );

        }

        // Wochentag des ersten Tags des Jahres/Monats
        private Weekday getWeekdayStart(
            PlainDate date,
            int shift // -1 = Vorjahr/-monat, 0 = aktuell, +1 = Folgejahr/-monat
        ) {

            if (this.owner.isYearRelated()) {
                return Weekday.valueOf(
                    GregorianMath.getDayOfWeek(date.getYear() + shift, 1, 1));
            } else {
                int year = date.getYear();
                int month = date.getMonth() + shift;

                if (month == 0) {
                    month = 12;
                    year--;
                } else if (month == 13) {
                    month = 1;
                    year++;
                }

                return Weekday.valueOf(
                    GregorianMath.getDayOfWeek(year, month, 1));
            }

        }

        // Länge eines Jahres/Monats in Tagen
        private int getLengthOfYM(
            PlainDate date,
            int shift // -1 = Vorjahr/-monat, 0 = aktuell, +1 = Folgejahr/-monat
        ) {

            if (this.owner.isYearRelated()) {
                return (
                    GregorianMath.isLeapYear(date.getYear() + shift)
                    ? 366
                    : 365
                );
            } else {
                int year = date.getYear();
                int month = date.getMonth() + shift;

                if (month == 0) {
                    month = 12;
                    year--;
                } else if (month == 13) {
                    month = 1;
                    year++;
                }

                return GregorianMath.getLengthOfMonth(year, month);
            }

        }

        private int getCalendarWeek(PlainDate date) {

            int scaledDay = (
                this.owner.isYearRelated()
                ? date.getDayOfYear()
                : date.getDayOfMonth());
            int wCurrent = getFirstCalendarWeekAsDay(date, 0);

            if (wCurrent <= scaledDay) {
                int result = ((scaledDay - wCurrent) / 7) + 1;

                if (
                    (result >= 53)
                    || (!this.owner.isYearRelated() && (result >= 5))
                ) {
                    int wNext =
                        getFirstCalendarWeekAsDay(date, 1)
                        + getLengthOfYM(date, 0);
                    if (wNext <= scaledDay) {
                        result = 1;
                    }
                }

                return result;
            } else {
                int wPrevious = getFirstCalendarWeekAsDay(date, -1);
                int dayCurrent = scaledDay + getLengthOfYM(date, -1);
                return ((dayCurrent - wPrevious) / 7) + 1;
            }

        }

        private PlainDate setCalendarWeek(
            PlainDate date,
            int value
        ) {

            int old = this.getCalendarWeek(date);

            if (value == old) {
                return date;
            } else {
                int delta = 7 * (value - old);
                return date.withDaysSinceUTC(date.getDaysSinceUTC() + delta);
            }

        }

    }

    private static class BWRule<T extends ChronoEntity<T>>
        implements ElementRule<T, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final CalendarWeekElement owner;

        //~ Konstruktoren -------------------------------------------------

        private BWRule(CalendarWeekElement owner) {
            super();

            this.owner = owner;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(T context) {

            PlainDate date = context.get(CALENDAR_DATE);
            return Integer.valueOf(this.getWeek(date));

        }

        @Override
        public Integer getMinimum(T context) {

            PlainDate date = context.get(CALENDAR_DATE);
            return Integer.valueOf(this.getMinWeek(date));

        }

        @Override
        public Integer getMaximum(T context) {

            PlainDate date = context.get(CALENDAR_DATE);
            return Integer.valueOf(this.getMaxWeek(date));

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            return this.getChild(context, false);

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            return this.getChild(context, true);

        }

        private ChronoElement<?> getChild(
            T context,
            boolean ceiling
        ) {

            PlainDate date = context.get(CALENDAR_DATE);
            ChronoElement<Weekday> dow =
                this.owner.getModel().localDayOfWeek();
            int weeknum = this.getValue(context).intValue();

            if (ceiling) {
                if (weeknum >= (this.owner.isYearRelated() ? 52 : 4)) {
                    PlainDate max = date.with(dow, context.getMaximum(dow));
                    if (this.owner.isYearRelated()) {
                        if (max.getDayOfYear() < date.getDayOfYear()) {
                            return PlainDate.DAY_OF_YEAR;
                        }
                    } else if (max.getDayOfMonth() < date.getDayOfMonth()) {
                        return PlainDate.DAY_OF_MONTH;
                    }
                }
            } else if (weeknum == 0) {
                PlainDate min = date.with(dow, context.getMinimum(dow));
                if (this.owner.isYearRelated()) {
                    if (min.getDayOfYear() > date.getDayOfYear()) {
                        return PlainDate.DAY_OF_YEAR;
                    }
                } else if (min.getDayOfMonth() > date.getDayOfMonth()) {
                    return PlainDate.DAY_OF_MONTH;
                }
            }

            return dow;

        }

        @Override
        public boolean isValid(
            T context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();
            PlainDate date = context.get(CALENDAR_DATE);

            return (
                (v >= this.getMinWeek(date))
                && (v <= this.getMaxWeek(date))
            );

        }

        @Override
        public T withValue(
            T context,
            Integer value,
            boolean lenient
        ) {

            int v = value.intValue();
            PlainDate date = context.get(CALENDAR_DATE);

            if (
                !lenient
                && !this.isValid(context, value)
            ) {
                throw new IllegalArgumentException(
                    "Invalid value: " + v + " (context=" + context + ")");
            }

            return context.with(
                CALENDAR_DATE,
                this.setWeek(date, v)
            );

        }

        private int getWeek(PlainDate date) {

            return this.getWeek(date, 0);

        }

        private int getMinWeek(PlainDate date) {

            return this.getWeek(date, -1);

        }

        private int getMaxWeek(PlainDate date) {

            return this.getWeek(date, 1);

        }

        private int getWeek(
            PlainDate date,
            int mode // -1 = Jahres-/Monatsanfang, 0 = aktueller Tag, 1 = Ende
        ) {

            int scaledDay = (
                this.owner.isYearRelated()
                ? date.getDayOfYear()
                : date.getDayOfMonth());
            Weekday wd = getDayOfWeek(date.getDaysSinceUTC() - scaledDay + 1);
            int dow = wd.getValue(this.owner.getModel());

            int wstart = (
                (dow <= 8 - this.owner.getModel().getMinimalDaysInFirstWeek())
                ? 2 - dow
                : 9 - dow
            );

            int refday;

            switch (mode) {
                case -1:
                    refday = 1;
                    break;
                case 0:
                    refday = scaledDay;
                    break;
                case 1:
                    refday = this.getLengthOfYM(date);
                    break;
                default:
                    throw new AssertionError("Unexpected: " + mode);
            }

            return MathUtils.floorDivide((refday - wstart), 7) + 1;

        }

        private PlainDate setWeek(
            PlainDate date,
            int value
        ) {

            int old = this.getWeek(date);

            if (value == old) {
                return date;
            } else {
                int delta = 7 * (value - old);
                return date.withDaysSinceUTC(date.getDaysSinceUTC() + delta);
            }

        }

        // Länge eines Jahres/Monats in Tagen
        private int getLengthOfYM(PlainDate date) {

            if (this.owner.isYearRelated()) {
                return (GregorianMath.isLeapYear(date.getYear()) ? 366 : 365);
            } else {
                return GregorianMath.getLengthOfMonth(
                    date.getYear(),
                    date.getMonth()
                );
            }

        }

    }

}
