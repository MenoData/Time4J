/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CommonElements.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.MathUtils;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoExtension;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;
import net.time4j.engine.FormattableElement;

import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


/**
 * <p>Defines access to elements which can be used by all calendars defined in this package. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
/*[deutsch]
 * <p>Definiert einen Zugang zu Elementen, die von allen Kalendern in diesem Paket verwendet werden k&ouml;nnen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
public class CommonElements {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Represents the related gregorian year which corresponds to the start
     * of any given non-gregorian calendar year. </p>
     *
     * <p>The element is read-only. </p>
     *
     * @since   3.20/4.16
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das gregorianische Bezugsjahr des Beginns eines gegebenen Kalenderjahres. </p>
     *
     * <p>Dieses Element kann nur gelesen werden. </p>
     *
     * @since   3.20/4.16
     */
    @FormattableElement(format = "r")
    public static final ChronoElement<Integer> RELATED_GREGORIAN_YEAR = RelatedGregorianYearElement.SINGLETON;

    //~ Konstruktoren -----------------------------------------------------

    private CommonElements() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines an element for the weekday with a localized day number in
     * the value range {@code 1-7}. </p>
     *
     * <p>The given chronology must support a 7-day-week with an element of name &quot;DAY_OF_WEEK&quot;
     * otherwise an exception will be thrown. </p>
     *
     * <p>This element defines localized weekday numbers in numerical formatting
     * and also a localized sorting order of weekdays, but still manages values
     * of type {@code Weekday}. However, the value range with its minimum and
     * maximum is localized, too, i.e. the element defines as minium the value
     * {@code getFirstDayOfWeek()}. </p>
     *
     * @param   <T> chronological type
     * @param   chronology  the calendrical chronology
     * @param   model       the underlying week model
     * @return  day of week with localized order
     * @throws  IllegalArgumentException if the chronology does not support this element
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Liefert ein Element f&uuml;r den Wochentag mit einer lokalisierten
     * Wochentagsnummer im Wertebereich {@code 1-7} und kann auf alle Chronologien angewandt
     * werden, die eine 7-Tage-Woche mit einem Element namens &quot;DAY_OF_WEEK&quot; unterst&uuml;tzen. </p>
     *
     * <p>Dieses Element definiert lokalisierte Wochentagsnummern in der
     * numerischen Formatierung und demzufolge auch eine lokalisierte
     * Wochentagssortierung, verwaltet aber selbst immer noch Enums vom Typ
     * {@code Weekday} als Werte. Jedoch ist der Wertebereich mitsamt seinem
     * Minimum und Maximum ebenfalls lokalisiert, d.h., das Element definiert
     * als Minimum den Wert {@code model.getFirstDayOfWeek()}. </p>
     *
     * @param   <T> chronological type
     * @param   chronology  the calendrical chronology
     * @param   model       the underlying week model
     * @return  day of week with localized order
     * @throws  IllegalArgumentException if the chronology does not support this element
     * @since   3.24/4.20
     */
    @FormattableElement(format = "e", standalone = "c")
    public static <T extends ChronoEntity<T> & CalendarDate> StdCalendarElement<Weekday, T> localDayOfWeek(
        Chronology<T> chronology,
        Weekmodel model
    ) {

        checkSevenDayWeek(chronology);
        return new DayOfWeekElement<T>(chronology.getChronoType(), model);

    }

    /**
     * <p>Creates an integer element for the week of year in given chronology dependent on given week model. </p>
     *
     * <p>The given chronology must support a 7-day-week with elements of names &quot;DAY_OF_WEEK&quot;
     * and &quot;DAY_OF_YEAR&quot;, otherwise an exception will be thrown. </p>
     *
     * @param   <T> chronological type
     * @param   chronology  the calendrical chronology
     * @param   model       the underlying week model
     * @return  generic calendar element
     * @throws  IllegalArgumentException if the chronology does not support this element
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Erzeugt ein Integer-Element f&uuml;r die Kalenderwoche des Jahres zum angegebenen Kalendersystem
     * unter Benutzung des angegebenen Wochenmodells. </p>
     *
     * <p>Die angegebene Chronologie mu&szlig; eine 7-Tage-Woche mit Elementen namens &quot;DAY_OF_WEEK&quot;
     * und &quot;DAY_OF_YEAR&quot; unterst&uuml;tzen, sonst wird eine Ausnahme geworfen. </p>
     *
     * @param   <T> chronological type
     * @param   chronology  the calendrical chronology
     * @param   model       the underlying week model
     * @return  generic calendar element
     * @throws  IllegalArgumentException if the chronology does not support this element
     * @since   3.24/4.20
     */
    @FormattableElement(format = "w")
    public static <T extends ChronoEntity<T> & CalendarDate> StdCalendarElement<Integer, T> weekOfYear(
        Chronology<T> chronology,
        Weekmodel model
    ) {

        ChronoElement<Integer> e = findDayElement(chronology, "DAY_OF_YEAR");

        if (e == null) {
            throw new IllegalArgumentException("Cannot derive a rule for given chronology: " + chronology);
        }

        return new CalendarWeekElement<T>("WEEK_OF_YEAR", chronology.getChronoType(), 1, 52, 'w', model, e);

    }

    /**
     * <p>Creates an integer element for the week of month in given chronology dependent on given week model. </p>
     *
     * <p>The given chronology must support a 7-day-week with elements of names &quot;DAY_OF_WEEK&quot;
     * and &quot;DAY_OF_MONTH&quot;, otherwise an exception will be thrown. </p>
     *
     * @param   <T> chronological type
     * @param   chronology  the calendar chronology
     * @param   model       the underlying week model
     * @return  generic calendar element
     * @throws  IllegalArgumentException if the chronology does not support this element
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Erzeugt ein Integer-Element f&uuml;r die Kalenderwoche des Monats zum angegebenen Kalendersystem
     * unter Benutzung des angegebenen Wochenmodells. </p>
     *
     * <p>Die angegebene Chronologie mu&szlig; eine 7-Tage-Woche mit Elementen namens &quot;DAY_OF_WEEK&quot;
     * und &quot;DAY_OF_MONTH&quot; unterst&uuml;tzen, sonst wird eine Ausnahme geworfen. </p>
     *
     * @param   <T> chronological type
     * @param   chronology  the calendar chronology
     * @param   model       the underlying week model
     * @return  generic calendar element
     * @throws  IllegalArgumentException if the chronology does not support this element
     * @since   3.24/4.20
     */
    @FormattableElement(format = "W")
    public static <T extends ChronoEntity<T> & CalendarDate> StdCalendarElement<Integer, T> weekOfMonth(
        Chronology<T> chronology,
        Weekmodel model
    ) {

        ChronoElement<Integer> e = findDayElement(chronology, "DAY_OF_MONTH");

        if (e == null) {
            throw new IllegalArgumentException("Cannot derive a rule for given chronology: " + chronology);
        }

        return new CalendarWeekElement<T>("WEEK_OF_MONTH", chronology.getChronoType(), 1, 5, 'W', model, e);

    }

    private static <D extends ChronoEntity<D>> int getMax(
        ChronoElement<?> element,
        D context
    ) {

        return Integer.class.cast(context.getMaximum(element)).intValue();

    }

    private static Weekday getDayOfWeek(long utcDays) {

        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    private static void checkSevenDayWeek(Chronology<?> chronology) {

        if (CalendarDate.class.isAssignableFrom(chronology.getChronoType())) {
            for (ChronoElement<?> element : chronology.getRegisteredElements()) {
                if (element.name().equals("DAY_OF_WEEK")) {
                    Object[] enums = element.getType().getEnumConstants();
                    if ((enums != null) && (enums.length == 7)) {
                        return;
                    }
                }
            }
        }

        throw new IllegalArgumentException("No 7-day-week: " + chronology);

    }

    @SuppressWarnings("unchecked")
    private static <D extends ChronoEntity<D>> ChronoElement<Integer> findDayElement(
        Chronology<D> chronology,
        String searchName
    ) {

        checkSevenDayWeek(chronology);

        for (ChronoElement<?> e : chronology.getRegisteredElements()) {
            if (e.name().equals(searchName)) {
                if (e.getType() == Integer.class) {
                    return (ChronoElement<Integer>) e;
                } else {
                    break;
                }
            }
        }

        return null;

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Wochenelement-Erweiterung. </p>
     *
     * @author  Meno Hochschild
     */
    static class Weekengine
        implements ChronoExtension {

        //~ Instanzvariablen ----------------------------------------------

        private final Class<? extends ChronoEntity> chronoType;
        private final ChronoElement<Integer> dayOfMonthElement;
        private final ChronoElement<Integer> dayOfYearElement;
        private final Weekmodel defaultWeekmodel;

        //~ Konstruktoren -------------------------------------------------

        Weekengine(
            Class<? extends ChronoEntity> chronoType,
            ChronoElement<Integer> dayOfMonthElement,
            ChronoElement<Integer> dayOfYearElement,
            Weekmodel defaultWeekmodel
        ) {
            super();

            this.chronoType = chronoType;
            this.dayOfMonthElement = dayOfMonthElement;
            this.dayOfYearElement = dayOfYearElement;
            this.defaultWeekmodel = defaultWeekmodel;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean accept(Class<?> chronoType) {

            return this.chronoType.equals(chronoType);

        }

        @Override
        public Set<ChronoElement<?>> getElements(
            Locale locale,
            AttributeQuery attributes
        ) {

            Weekmodel model = (locale.getCountry().isEmpty() ? this.defaultWeekmodel : Weekmodel.of(locale));
            Set<ChronoElement<?>> set = new HashSet<ChronoElement<?>>();
            set.add(
                DayOfWeekElement.of(this.chronoType, model));
            set.add(
                CalendarWeekElement.of("WEEK_OF_MONTH", this.chronoType, 1, 5, 'W', model, this.dayOfMonthElement));
            set.add(
                CalendarWeekElement.of("WEEK_OF_YEAR", this.chronoType, 1, 52, 'w', model, this.dayOfYearElement));
            return Collections.unmodifiableSet(set);

        }

        @Override
        public ChronoEntity<?> resolve(
            ChronoEntity<?> entity,
            Locale locale,
            AttributeQuery attributes
        ) {

            return entity; // no-op

        }

    }

    private static class CalendarWeekElement<T extends ChronoEntity<T>>
        extends StdIntegerDateElement<T> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -7471192143785466686L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  the underlying week model
         */
        private final Weekmodel model;

        /**
         * @serial  reference day element
         */
        private final ChronoElement<Integer> dayElement;

        //~ Konstruktoren -------------------------------------------------

        private CalendarWeekElement(
            String name,
            Class<T> chrono,
            int min,
            int max,
            char symbol,
            Weekmodel model,
            ChronoElement<Integer> dayElement
        ) {
            super(name, chrono, min, max, symbol);

            if (model == null) {
                throw new NullPointerException("Missing week model.");
            }

            this.model = model;
            this.dayElement = dayElement;

        }

        //~ Methoden ------------------------------------------------------

        static <T extends ChronoEntity<T>> CalendarWeekElement<T> of(
            String name,
            Class<T> chrono,
            int min,
            int max,
            char symbol,
            Weekmodel model,
            ChronoElement<Integer> dayElement
        ) {

            return new CalendarWeekElement<T>(name, chrono, min, max, symbol, model, dayElement);

        }

        @Override
        public ChronoOperator<T> decremented() {

            return new DayOperator<T>(-7);

        }

        @Override
        public ChronoOperator<T> incremented() {

            return new DayOperator<T>(7);

        }

        @Override
        public boolean isLenient() {

            return true;

        }

        @Override
        protected boolean doEquals(BasicElement<?> obj) {

            if (super.doEquals(obj)) {
                CalendarWeekElement<?> that = CalendarWeekElement.class.cast(obj);
                return this.model.equals(that.model);
            }

            return false;

        }

        @Override
        protected <D extends ChronoEntity<D>> ElementRule<D, Integer> derive(Chronology<D> chronology) {

            if (this.getChronoType().equals(chronology.getChronoType())) {
                return new CWRule<D>(this);
            }

            return null;

        }

        @Override
        protected Object readResolve() throws ObjectStreamException {

            return this; // no singleton

        }

    }

    private static class CWRule<D extends ChronoEntity<D>>
        implements ElementRule<D, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final CalendarWeekElement<?> owner;

        //~ Konstruktoren -------------------------------------------------

        private CWRule(CalendarWeekElement<?> owner) {
            super();

            this.owner = owner;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(D context) {

            return Integer.valueOf(this.getCalendarWeek(context));

        }

        @Override
        public Integer getMinimum(D context) {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getMaximum(D context) {

            return Integer.valueOf(this.getMaxCalendarWeek(context));

        }

        @Override
        public boolean isValid(
            D context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();
            return ((v >= 1) && (v <= this.getMaxCalendarWeek(context)));

        }

        @Override
        public D withValue(
            D context,
            Integer value,
            boolean lenient
        ) {

            int v = value.intValue();

            if (!lenient && !this.isValid(context, value)) {
                throw new IllegalArgumentException(
                    "Invalid value: " + v + " (context=" + context + ")");
            }

            return this.setCalendarWeek(context, v);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(D context) {

            return this.getChild(context.getClass());

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(D context) {

            return this.getChild(context.getClass());

        }

        @SuppressWarnings("unchecked")
        private ChronoElement<?> getChild(Object obj) {

            Class<D> type = (Class<D>) obj;
            return new DayOfWeekElement<D>(type, this.owner.model);

        }

        // letzte Kalenderwoche im Jahr/Monat
        private int getMaxCalendarWeek(D context) {

            int scaledDay = context.getInt(this.owner.dayElement);
            int wCurrent = getFirstCalendarWeekAsDay(context, 0);

            if (wCurrent <= scaledDay) {
                int wNext =
                    getFirstCalendarWeekAsDay(context, 1) + getLengthOfYM(context, 0);
                if (wNext <= scaledDay) { // reference date points to next week cycle
                    try {
                        int wStart = getFirstCalendarWeekAsDay(context, 1);
                        D corrected = context.with(EpochDays.UTC, context.get(EpochDays.UTC).longValue() + 7);
                        wNext = getFirstCalendarWeekAsDay(corrected, 1) + getLengthOfYM(context, 1);
                        wCurrent = wStart;
                    } catch (RuntimeException re) {
                        wNext += 7; // rare edge case near the end of time axis
                    }
                }
                return (wNext - wCurrent) / 7;
            } else {
                int wPrevious = getFirstCalendarWeekAsDay(context, -1);
                wCurrent = wCurrent + getLengthOfYM(context, -1);
                return (wCurrent - wPrevious) / 7;
            }

        }

        // Ermittelt den Beginn der ersten Kalenderwoche eines Jahres/Monats
        // auf einer day-of-year/month-Skala (kann auch <= 0 sein).
        private int getFirstCalendarWeekAsDay(
            D context,
            int shift // -1 = Vorjahr/-monat, 0 = aktuell, +1 = Folgejahr/-monat
        ) {

            Weekday wd = this.getWeekdayStart(context, shift);
            Weekmodel model = this.owner.model;
            int dow = wd.getValue(model);

            return (
                (dow <= 8 - model.getMinimalDaysInFirstWeek())
                    ? 2 - dow
                    : 9 - dow
            );

        }

        // Wochentag des ersten Tags des Jahres/Monats
        private Weekday getWeekdayStart(
            D context,
            int shift // -1 = Vorjahr/-monat, 0 = aktuell, +1 = Folgejahr/-monat
        ) {

            int scaledDay = context.getInt(this.owner.dayElement);
            int lastDay;

            switch (shift) {
                case -1:
                    long utcDays = context.get(EpochDays.UTC) - scaledDay;
                    lastDay = context.with(EpochDays.UTC, utcDays).getInt(this.owner.dayElement);
                    return getDayOfWeek(utcDays - lastDay + 1);
                case 0:
                    return getDayOfWeek(context.get(EpochDays.UTC).longValue() - scaledDay + 1);
                case 1:
                    lastDay = getMax(this.owner.dayElement, context);
                    return getDayOfWeek(context.get(EpochDays.UTC).longValue() + lastDay + 1 - scaledDay);
                default:
                    throw new AssertionError("Unexpected: " + shift);
            }

        }

        // Länge eines Jahres/Monats in Tagen
        private int getLengthOfYM(
            D context,
            int shift // -1 = Vorjahr/-monat, 0 = aktuell, +1 = Folgejahr/-monat
        ) {

            int scaledDay = context.getInt(this.owner.dayElement);
            int lastDay;

            switch (shift) {
                case -1:
                    return getMax(
                        this.owner.dayElement,
                        context.with(EpochDays.UTC, context.get(EpochDays.UTC).longValue() - scaledDay));
                case 0:
                    return getMax(this.owner.dayElement, context);
                case 1:
                    lastDay = getMax(this.owner.dayElement, context);
                    return getMax(
                        this.owner.dayElement,
                        context.with(EpochDays.UTC, context.get(EpochDays.UTC).longValue() + lastDay + 1 - scaledDay));
                default:
                    throw new AssertionError("Unexpected: " + shift);
            }

        }

        private int getCalendarWeek(D context) {

            int scaledDay = context.getInt(this.owner.dayElement);
            int wCurrent = getFirstCalendarWeekAsDay(context, 0);

            if (wCurrent <= scaledDay) {
                int wNext = getFirstCalendarWeekAsDay(context, 1) + getLengthOfYM(context, 0);
                if (wNext <= scaledDay) {
                    return 1;
                } else {
                    return ((scaledDay - wCurrent) / 7) + 1;
                }
            } else {
                int wPrevious = getFirstCalendarWeekAsDay(context, -1);
                int dayCurrent = scaledDay + getLengthOfYM(context, -1);
                return ((dayCurrent - wPrevious) / 7) + 1;
            }

        }

        private D setCalendarWeek(
            D context,
            int value
        ) {

            int old = this.getCalendarWeek(context);

            if (value == old) {
                return context;
            } else {
                return context.with(EpochDays.UTC, context.get(EpochDays.UTC).longValue() + 7 * (value - old));
            }

        }

    }

    private static class DayOfWeekElement<T extends ChronoEntity<T>>
        extends StdEnumDateElement<Weekday, T> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 5613494586572932860L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  the underlying week model
         */
        private final Weekmodel model;

        //~ Konstruktoren -------------------------------------------------

        private DayOfWeekElement(
            Class<T> chronoType,
            Weekmodel model
        ) {
            super("LOCAL_DAY_OF_WEEK", chronoType, Weekday.class, 'e');

            this.model = model;

        }

        //~ Methoden ------------------------------------------------------

        static <T extends ChronoEntity<T>> DayOfWeekElement<T> of(
            Class<T> chronoType,
            Weekmodel model
        ) {

            return new DayOfWeekElement<T>(chronoType, model);

        }

        @Override
        public ChronoOperator<T> decremented() {

            return new DayOperator<T>(-1);

        }

        @Override
        public ChronoOperator<T> incremented() {

            return new DayOperator<T>(1);

        }

        @Override
        public int numerical(Weekday dayOfWeek) {

            return dayOfWeek.getValue(this.model);

        }

        @Override
        public Weekday getDefaultMinimum() {

            return this.model.getFirstDayOfWeek();

        }

        @Override
        public Weekday getDefaultMaximum() {

            return this.model.getFirstDayOfWeek().roll(6);

        }

        @Override
        public int compare(
            ChronoDisplay o1,
            ChronoDisplay o2
        ) {

            int i1 = o1.get(this).getValue(this.model);
            int i2 = o2.get(this).getValue(this.model);
            return ((i1 < i2) ? -1 : ((i1 == i2) ? 0 : 1));

        }

        @Override
        protected boolean doEquals(BasicElement<?> obj) {

            if (super.doEquals(obj)) {
                DayOfWeekElement<?> that = DayOfWeekElement.class.cast(obj);
                return this.model.equals(that.model);
            }

            return false;

        }

        @Override
        protected <D extends ChronoEntity<D>> ElementRule<D, Weekday> derive(Chronology<D> chronology) {

            if (this.getChronoType().equals(chronology.getChronoType())) {
                return new DRule<D>(this);
            }

            return null;

        }

        @Override
        protected boolean isWeekdayElement() {

            return true;

        }

        @Override
        protected Object readResolve() throws ObjectStreamException {

            return this; // no singleton

        }

    }

    private static class DRule<T extends ChronoEntity<T>>
        implements ElementRule<T, Weekday> {

        //~ Instanzvariablen ----------------------------------------------

        private final DayOfWeekElement<?> element;

        //~ Konstruktoren -------------------------------------------------

        private DRule(DayOfWeekElement<?> element) {
            super();

            this.element = element;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(T context) {

            return getDayOfWeek(context.get(EpochDays.UTC).longValue());

        }

        @Override
        public Weekday getMinimum(T context) {

            Chronology<?> c = Chronology.lookup(context.getClass());
            long min;

            if (context instanceof CalendarVariant) {
                min = c.getCalendarSystem(CalendarVariant.class.cast(context).getVariant()).getMinimumSinceUTC();
            } else {
                min = c.getCalendarSystem().getMinimumSinceUTC();
            }

            long utcDays = context.get(EpochDays.UTC).longValue();
            int oldNum = getDayOfWeek(utcDays).getValue(this.element.model);

            if (utcDays + 1 - oldNum < min) {
                return getDayOfWeek(min);
            }

            return this.element.getDefaultMinimum();

        }

        @Override
        public Weekday getMaximum(T context) {

            Chronology<?> c = Chronology.lookup(context.getClass());
            long max;

            if (context instanceof CalendarVariant) {
                max = c.getCalendarSystem(CalendarVariant.class.cast(context).getVariant()).getMaximumSinceUTC();
            } else {
                max = c.getCalendarSystem().getMaximumSinceUTC();
            }

            long utcDays = context.get(EpochDays.UTC).longValue();
            int oldNum = getDayOfWeek(utcDays).getValue(this.element.model);

            if (utcDays + 7 - oldNum > max) {
                return getDayOfWeek(max);
            }

            return this.element.getDefaultMaximum();

        }

        @Override
        public boolean isValid(
            T context,
            Weekday value
        ) {

            if (value == null) {
                return false;
            }

            try {
                this.withValue(context, value, false);
                return true;
            } catch (RuntimeException ex) {
                return false;
            }

        }

        @Override
        public T withValue(
            T context,
            Weekday value,
            boolean lenient
        ) {

            long utcDays = context.get(EpochDays.UTC).longValue();
            Weekday current = getDayOfWeek(utcDays);

            if (value == current) {
                return context;
            }

            int old = current.getValue(this.element.model);
            int neu = value.getValue(this.element.model);
            return context.with(EpochDays.UTC, utcDays + neu - old);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            return null;

        }

    }

    private static class DayOperator<T extends ChronoEntity<T>>
        implements ChronoOperator<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final int amount;

        //~ Konstruktoren -------------------------------------------------

        DayOperator(int amount) {
            super();

            this.amount = amount;

        }

        //~ Methoden ------------------------------------------------------

        public T apply(T entity) {

            long e = MathUtils.safeAdd(entity.get(EpochDays.UTC), this.amount);
            return entity.with(EpochDays.UTC, e);

        }

    }

}
