/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Weekmodel.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.NumericalElement;
import net.time4j.format.OutputContext;
import net.time4j.format.ParseLog;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.WeekdataProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.time4j.PlainDate.CALENDAR_DATE;
import static net.time4j.PlainDate.WEEKDAY_IN_MONTH;
import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Definiert Regeln f&uuml;r den lokalisierten Umgang mit Wochentagen
 * und Kalenderwochen auf einer 7-Tage-Wochenbasis. </p>
 *
 * <ul>
 *  <li>1. Regel: Welcher Wochentag ist der erste Tag der Woche?</li>
 *  <li>2. Regel: Was ist die minimale Anzahl der Tage der ersten Woche?</li>
 * </ul>
 *
 * <p>Au&szlig;erdem werden einige wochenbezogene Elemente zur Verf&uuml;gung
 * gestellt, die mit allen Klassen umgehen k&ouml;nnen, die ein ISO-Datum
 * enthalten ({@code Timestamp}-Klassen und {@code PlainDate}). </p>
 *
 * @author      Meno Hochschild
 * @see         WeekdataProvider
 * @concurrency <immutable>
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

        for (WeekdataProvider p : ServiceLoader.load(WeekdataProvider.class)) {
            tmp = p;
            break;
        }

        if (tmp == null) {
            tmp = new DefaultWeekdataProvider();
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
        AdjustableElement<Integer, DateOperator> woyElement;
    private transient final
        AdjustableElement<Integer, DateOperator> womElement;
    private transient final
        AdjustableElement<Integer, DateOperator> boundWoyElement;
    private transient final
        AdjustableElement<Integer, DateOperator> boundWomElement;
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
                    Weekday wd =
                        Weekday.valueOf(GregorianMath.getDayOfWeek(context));
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
     * <p>Ermittelt ein geeignetes Wochenmodell f&uuml;r die aktuelle
     * Landeseinstellung des Systems. </p>
     *
     * @return  week model in system locale
     * @see     Locale#getDefault()
     */
    public static Weekmodel ofSystem() {

        return Weekmodel.of(Locale.getDefault());

    }

    /**
     * <p>Ermittelt ein geeignetes Wochenmodell f&uuml;r das angegebene
     * Land. </p>
     *
     * @param   locale      country setting
     * @return  localized week model
     */
    public static Weekmodel of(Locale locale) {

        if (
            locale.getLanguage().isEmpty()
            && locale.getCountry().isEmpty()
        ) {
            return Weekmodel.ISO;
        }

        Weekmodel model = CACHE.get(locale);

        if (model != null) {
            return model;
        }

        WeekdataProvider p = LOCALIZED_WEEKDATA;

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
     * <p>Definiert den ersten Tag des Wochenendes. </p>
     *
     * <p>Im ISO-8601-Standard ist der Samstag der Beginn des Wochenendes. </p>
     *
     * @return  start of weekend
     */
    public Weekday getStartOfWeekend() {

        return this.startOfWeekend;

    }

    /**
     * <p>Definiert den letzten Tag des Wochenendes. </p>
     *
     * <p>Im ISO-8601-Standard ist der Sonntag das Ende des Wochenendes. </p>
     *
     * @return  end of weekend
     */
    public Weekday getEndOfWeekend() {

        return this.endOfWeekend;

    }

    /**
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
    public AdjustableElement<Integer, DateOperator> weekOfYear() {

        return this.woyElement;

    }

    /**
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
    public AdjustableElement<Integer, DateOperator> weekOfMonth() {

        return this.womElement;

    }

    /**
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
    public AdjustableElement<Integer, DateOperator> boundedWeekOfYear() {

        return this.boundWoyElement;

    }

    /**
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
    public AdjustableElement<Integer, DateOperator> boundedWeekOfMonth() {

        return this.boundWomElement;

    }

    /**
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
     *      && (getEndOfWeekend() == Weekday.SUNDAY)
     *  );
     *
     *  int header = 3;
     *  header <<= 4;
     *  if (!isoWeekend) {
     *      header |= 1;
     *  }
     *  out.writeByte(header);
     *
     *  int state = getFirstDayOfWeek().getValue();
     *  state <<= 4;
     *  state |= getMinimalDaysInFirstWeek();
     *  out.writeByte(state);
     *
     *  if (!isoWeekend) {
     *      state = getStartOfWeekend().getValue();
     *      state <<= 4;
     *      state |= getEndOfWeekend().getValue();
     *      out.writeByte(state);
     *  }
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.WEEKMODEL_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

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
            ChronoEntity<?> o1,
            ChronoEntity<?> o2
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
        public DateOperator setToNext(Weekday value) {

            return new NavigationOperator<Weekday>(
                this,
                ElementOperator.OP_NAV_NEXT,
                value
            );

        }

        @Override
        public DateOperator setToPrevious(Weekday value) {

            return new NavigationOperator<Weekday>(
                this,
                ElementOperator.OP_NAV_PREVIOUS,
                value
            );

        }

        @Override
        public DateOperator setToNextOrSame(Weekday value) {

            return new NavigationOperator<Weekday>(
                this,
                ElementOperator.OP_NAV_NEXT_OR_SAME,
                value
            );

        }

        @Override
        public DateOperator setToPreviousOrSame(Weekday value) {

            return new NavigationOperator<Weekday>(
                this,
                ElementOperator.OP_NAV_PREVIOUS_OR_SAME,
                value
            );

        }

        @Override
        public void print(
            ChronoEntity<?> context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException {

            buffer.append(this.accessor(attributes).print(context.get(this)));

        }

        @Override
        public Weekday parse(
            CharSequence text,
            ParseLog status,
            AttributeQuery attributes
        ) {

            boolean caseInsensitive =
                attributes
                    .get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE)
                    .booleanValue();
            boolean partialCompare =
                attributes
                    .get(Attributes.PARSE_PARTIAL_COMPARE, Boolean.FALSE)
                    .booleanValue();

            return this.accessor(attributes).parse(
                text,
                status,
                this.getType(),
                caseInsensitive,
                partialCompare
            );

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
        protected <T extends ChronoEntity<T>>
        ElementRule<T, Weekday> derive(Chronology<T> chronology) {

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

        private CalendarText.Accessor accessor(AttributeQuery attributes) {

            CalendarText cnames =
                CalendarText.getInstance(
                    attributes.get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE),
                    attributes.get(Attributes.LANGUAGE, Locale.ROOT));

            return cnames.getWeekdays(
                attributes.get(
                    Attributes.TEXT_WIDTH,
                    TextWidth.WIDE),
                attributes.get(
                    Attributes.OUTPUT_CONTEXT,
                    OutputContext.FORMAT));

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
        implements AdjustableElement<Integer, DateOperator>,
                   NumericalElement<Integer> {

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

            return this.getChild(context);

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            return this.getChild(context);

        }

        private ChronoElement<?> getChild(T context) {

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

    private static class DefaultWeekdataProvider
        implements WeekdataProvider {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final Map<String, Weekday> START_OF_WEEKEND;
        static final Map<String, Weekday> END_OF_WEEKEND;

        // Daten aus CLDR 23
        static {
            Map<String, Weekday> tmp = new HashMap<String, Weekday>(28);
            tmp.put("AF", Weekday.THURSDAY);
            tmp.put("DZ", Weekday.THURSDAY);
            tmp.put("IR", Weekday.THURSDAY);
            tmp.put("OM", Weekday.THURSDAY);
            tmp.put("SA", Weekday.THURSDAY);
            tmp.put("YE", Weekday.THURSDAY);
            tmp.put("AE", Weekday.FRIDAY);
            tmp.put("BH", Weekday.FRIDAY);
            tmp.put("EG", Weekday.FRIDAY);
            tmp.put("IL", Weekday.FRIDAY);
            tmp.put("IQ", Weekday.FRIDAY);
            tmp.put("JO", Weekday.FRIDAY);
            tmp.put("KW", Weekday.FRIDAY);
            tmp.put("LY", Weekday.FRIDAY);
            tmp.put("MA", Weekday.FRIDAY);
            tmp.put("QA", Weekday.FRIDAY);
            tmp.put("SD", Weekday.FRIDAY);
            tmp.put("SY", Weekday.FRIDAY);
            tmp.put("TN", Weekday.FRIDAY);
            tmp.put("IN", Weekday.SUNDAY);
            START_OF_WEEKEND = Collections.unmodifiableMap(tmp);

            tmp = new HashMap<String, Weekday>(25);
            tmp.put("AF", Weekday.FRIDAY);
            tmp.put("DZ", Weekday.FRIDAY);
            tmp.put("IR", Weekday.FRIDAY);
            tmp.put("OM", Weekday.FRIDAY);
            tmp.put("SA", Weekday.FRIDAY);
            tmp.put("YE", Weekday.FRIDAY);
            tmp.put("AE", Weekday.SATURDAY);
            tmp.put("BH", Weekday.SATURDAY);
            tmp.put("EG", Weekday.SATURDAY);
            tmp.put("IL", Weekday.SATURDAY);
            tmp.put("IQ", Weekday.SATURDAY);
            tmp.put("JO", Weekday.SATURDAY);
            tmp.put("KW", Weekday.SATURDAY);
            tmp.put("LY", Weekday.SATURDAY);
            tmp.put("MA", Weekday.SATURDAY);
            tmp.put("QA", Weekday.SATURDAY);
            tmp.put("SD", Weekday.SATURDAY);
            tmp.put("SY", Weekday.SATURDAY);
            tmp.put("TN", Weekday.SATURDAY);
            END_OF_WEEKEND = Collections.unmodifiableMap(tmp);
        }

        //~ Instanzvariablen ----------------------------------------------

        private final String source;
        private final Map<String, Weekday> startOfWeekend;
        private final Map<String, Weekday> endOfWeekend;

        //~ Konstruktoren -------------------------------------------------

        DefaultWeekdataProvider() {
            super();

            InputStream is = null;
            String name = "data/weekend.data";
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            if (cl != null) {
                is = cl.getResourceAsStream(name);
            }

            if (is == null) {
                cl = Weekmodel.class.getClassLoader();
                is = cl.getResourceAsStream(name);
            }

            if (is != null) {

                this.source = "@" + cl.getResource(name).toString();
                Map<String, Weekday> tmpStart =
                    new HashMap<String, Weekday>(START_OF_WEEKEND.size());
                Map<String, Weekday> tmpEnd =
                    new HashMap<String, Weekday>(END_OF_WEEKEND.size());

                try {

                    BufferedReader br =
                        new BufferedReader(
                            new InputStreamReader(is, "US-ASCII"));

                    String line;

                    while ((line = br.readLine()) != null) {

                        if (line.startsWith("#")) {
                            continue; // Kommentarzeile überspringen
                        }

                        int equal = line.indexOf('=');
                        String prefix = line.substring(0, equal).trim();
                        String[] list = line.substring(equal + 1).split(" ");
                        String wd = "";
                        Weekday weekday;
                        Map<String, Weekday> map;

                        if (prefix.startsWith("start-")) {
                            wd = prefix.substring(6);
                            weekday = Weekday.SATURDAY;
                            map = tmpStart;
                        } else if (prefix.startsWith("end-")) {
                            wd = prefix.substring(4);
                            weekday = Weekday.SUNDAY;
                            map = tmpEnd;
                        } else {
                            throw new IllegalStateException(
                                "Unexpected format: " + this.source);
                        }

                        if (wd.equals("sun")) {
                            weekday = Weekday.SUNDAY;
                        } else if (wd.equals("sat")) {
                            weekday = Weekday.SATURDAY;
                        } else if (wd.equals("fri")) {
                            weekday = Weekday.FRIDAY;
                        } else if (wd.equals("thu")) {
                            weekday = Weekday.THURSDAY;
                        } else if (wd.equals("wed")) {
                            weekday = Weekday.WEDNESDAY;
                        } else if (wd.equals("tue")) {
                            weekday = Weekday.TUESDAY;
                        } else if (wd.equals("mon")) {
                            weekday = Weekday.MONDAY;
                        }

                        for (String country : list) {
                            String key = country.trim().toUpperCase(Locale.US);

                            if (!key.isEmpty()) {
                                map.put(key, weekday);
                            }
                        }

                    }

                    this.startOfWeekend = Collections.unmodifiableMap(tmpStart);
                    this.endOfWeekend = Collections.unmodifiableMap(tmpEnd);

                } catch (UnsupportedEncodingException uee) {
                    throw new AssertionError(uee);
                } catch (Exception ex) {
                    throw new IllegalStateException(
                        "Unexpected format: " + this.source, ex);
                } finally {
                    try {
                        is.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace(System.err);
                    }
                }

            } else {
                this.source = "@STATIC";
                this.startOfWeekend = START_OF_WEEKEND;
                this.endOfWeekend = END_OF_WEEKEND;

                System.out.println("Warning: File \"" + name + "\" not found.");
            }

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int getFirstDayOfWeek(Locale country) {

            GregorianCalendar gc = new GregorianCalendar(country);
            int fd = gc.getFirstDayOfWeek();
            return ((fd == 1) ? 7 : (fd - 1));

        }

        @Override
        public int getMinimalDaysInFirstWeek(Locale country) {

            GregorianCalendar gc = new GregorianCalendar(country);
            return gc.getMinimalDaysInFirstWeek();

        }

        @Override
        public int getStartOfWeekend(Locale country) {

            String key = country.getCountry();
            Weekday start = Weekday.SATURDAY;

            if (this.startOfWeekend.containsKey(key)) {
                start = this.startOfWeekend.get(key);
            }

            return start.getValue();

        }

        @Override
        public int getEndOfWeekend(Locale country) {

            String key = country.getCountry();
            Weekday end = Weekday.SUNDAY;

            if (this.endOfWeekend.containsKey(key)) {
                end = this.endOfWeekend.get(key);
            }

            return end.getValue();

        }

        @Override
        public String toString() {

            return this.getClass().getName() + this.source;

        }

    }

}
