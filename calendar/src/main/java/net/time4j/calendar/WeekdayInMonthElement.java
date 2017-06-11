/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WeekdayInMonthElement.java) is part of project Time4J.
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
import net.time4j.base.MathUtils;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;
import net.time4j.engine.IntElementRule;


/**
 * <p>Das Element f&uuml;r den x-ten Wochentag im Monat. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
final class WeekdayInMonthElement<T extends ChronoEntity<T> & CalendarDate>
    extends StdIntegerDateElement<T>
    implements OrdinalWeekdayElement<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int LAST = Integer.MAX_VALUE;
    private static final long serialVersionUID = 4275169663905222176L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final ChronoElement<Integer> domElement;
    private transient final ChronoElement<Weekday> dowElement;

    //~ Konstruktoren -----------------------------------------------------

    WeekdayInMonthElement(
        Class<T> chrono,
        ChronoElement<Integer> domElement,
        ChronoElement<Weekday> dowElement
    ) {
        super(
            "WEEKDAY_IN_MONTH",
            chrono,
            1,
            domElement.getDefaultMaximum().intValue() / 7,
            'F',
            new WeekOperator<T>(true),
            new WeekOperator<T>(false));

        this.domElement = domElement;
        this.dowElement = dowElement;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ChronoOperator<T> setToFirst(Weekday dayOfWeek) {

        return this.setTo(1, dayOfWeek);

    }

    @Override
    public ChronoOperator<T> setToLast(Weekday dayOfWeek) {

        return this.setTo(LAST, dayOfWeek);

    }

    @Override
    public ChronoOperator<T> setTo(
        int ordinal,
        Weekday dayOfWeek
    ) {

        return new SetOperator<T>(this, ordinal, dayOfWeek);

    }

    static <T extends ChronoEntity<T> & CalendarDate> ElementRule<T, Integer> getRule(WeekdayInMonthElement<T> wim) {

        return new Rule<T>(wim);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Rule<T extends ChronoEntity<T> & CalendarDate>
        implements IntElementRule<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final WeekdayInMonthElement<T> wim;

        //~ Konstruktoren -------------------------------------------------

        Rule(WeekdayInMonthElement<T> wim) {
            super();

            this.wim = wim;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(T context) {

            return Integer.valueOf(this.getInt(context));

        }

        @Override
        public Integer getMinimum(T context) {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getMaximum(T context) {

            return Integer.valueOf(this.getMax(context));

        }

        @Override
        public boolean isValid(
            T context,
            Integer value
        ) {

            return ((value != null) && this.isValid(context, value.intValue()));

        }

        @Override
        public T withValue(
            T context,
            Integer value,
            boolean lenient
        ) {

            if (value != null) {
                return this.withValue(context, value.intValue(), lenient);
            } else {
                throw new IllegalArgumentException("Missing value.");
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            return null;

        }

        @Override
        public boolean isValid(
            T context,
            int value
        ) {

            return (value >= 1) && (value <= this.getMax(context));

        }

        @Override
        public T withValue(
            T context,
            int value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                Weekday dayOfWeek = context.get(this.wim.dowElement);
                return context.with(this.wim.setTo(value, dayOfWeek));
            } else {
                throw new IllegalArgumentException("Invalid value: " + value);
            }

        }

        @Override
        public int getInt(T context) {

            int dom = context.getInt(this.wim.domElement);
            return MathUtils.floorDivide(dom - 1, 7) + 1;

        }

        private int getMax(T context) {

            int dom = context.getInt(this.wim.domElement);
            int max = context.getMaximum(this.wim.domElement).intValue();

            while (dom + 7 <= max) {
                dom += 7;
            }

            return MathUtils.floorDivide(dom - 1, 7) + 1;

        }

    }

    private static class SetOperator<T extends ChronoEntity<T> & CalendarDate>
        implements ChronoOperator<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final WeekdayInMonthElement<T> wim;
        private final long ordinal;
        private final Weekday dayOfWeek;

        //~ Konstruktoren -------------------------------------------------

        SetOperator(
            WeekdayInMonthElement<T> wim,
            int ordinal,
            Weekday dayOfWeek
        ) {
            super();

            if (dayOfWeek == null) {
                throw new NullPointerException("Missing value.");
            }

            this.wim = wim;
            this.ordinal = ordinal;
            this.dayOfWeek = dayOfWeek;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public T apply(T entity) {

            Weekday current = entity.get(this.wim.dowElement);
            int dom = entity.getInt(this.wim.domElement);
            long days;

            if (this.ordinal == LAST) {
                int max = entity.getMaximum(this.wim.domElement).intValue();
                int wdLast = current.getValue() + ((max - dom) % 7);
                if (wdLast > 7) {
                    wdLast -= 7;
                }
                int delta = this.dayOfWeek.getValue() - wdLast;
                days = max - dom + delta;
                if (delta > 0) {
                    days -= 7;
                }
            } else {
                int delta = this.dayOfWeek.getValue() - current.getValue();
                days = (this.ordinal - (MathUtils.floorDivide(dom + delta - 1, 7) + 1)) * 7 + delta;
            }

            long utcDays = entity.getDaysSinceEpochUTC();
            return entity.with(EpochDays.UTC, utcDays + days);

        }

    }

    private static class WeekOperator<T extends ChronoEntity<T>>
        implements ChronoOperator<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean backwards;

        //~ Konstruktoren -------------------------------------------------

        WeekOperator(boolean backwards) {
            super();

            this.backwards = backwards;

        }

        //~ Methoden ------------------------------------------------------

        public T apply(T entity) {

            long e = entity.get(EpochDays.UTC);
            if (this.backwards) {
                e -= 7;
            } else {
                e += 7;
            }
            return entity.with(EpochDays.UTC, e);

        }

    }

}
