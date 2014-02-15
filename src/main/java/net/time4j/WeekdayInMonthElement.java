/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WeekdayInMonthElement.java) is part of project Time4J.
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

import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoOperator;
import net.time4j.format.NumericalElement;
import net.time4j.tz.TZID;
import net.time4j.tz.TransitionStrategy;

import java.io.ObjectStreamException;


/**
 * <p>Das Element f&uuml;r den x-ten Wochentag im Monat. </p>
 *
 * <p>Eine Instanz ist erh&auml;ltlich &uuml;ber den Ausdruck
 * {@link PlainDate#WEEKDAY_IN_MONTH}. Diese Klasse bietet neben
 * den vom Interface {@code AdjustableElement} geerbten Methoden
 * weitere Spezialmethoden zum Setzen des Wochentags im Monat. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class WeekdayInMonthElement
    extends AbstractValueElement<Integer, PlainDate>
    implements OrdinalWeekdayElement<PlainDate>,
               NumericalElement<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Singleton. </p>
     */
    static final WeekdayInMonthElement INSTANCE =
        new WeekdayInMonthElement();

    private static final int LAST = 5;
    private static final long serialVersionUID = -2378018589067147278L;

    //~ Konstruktoren -----------------------------------------------------

    private WeekdayInMonthElement() {
        super("WEEKDAY_IN_MONTH");

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Integer> getType() {

        return Integer.class;

    }

    @Override
    public char getSymbol() {

        return 'F';

    }

    @Override
    public int numerical(Integer value) {

        return value.intValue();

    }

    /**
     * Definiert das Standardminimum.
     *
     * @return  {@code 1}
     */
    @Override
    public Integer getDefaultMinimum() {

        return Integer.valueOf(1);

    }

    /**
     * Definiert das Standardmaximum.
     *
     * @return  {@code 5}
     */
    @Override
    public Integer getDefaultMaximum() {

        return Integer.valueOf(LAST);

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
    public ZonalOperator<PlainDate> setToFirst(Weekday dayOfWeek) {

        return this.setTo(1, dayOfWeek);

    }

    @Override
    public ZonalOperator<PlainDate> setToSecond(Weekday dayOfWeek) {

        return this.setTo(2, dayOfWeek);

    }

    @Override
    public ZonalOperator<PlainDate> setToThird(Weekday dayOfWeek) {

        return this.setTo(3, dayOfWeek);

    }

    @Override
    public ZonalOperator<PlainDate> setToFourth(Weekday dayOfWeek) {

        return this.setTo(4, dayOfWeek);

    }

    @Override
    public ZonalOperator<PlainDate> setToLast(Weekday dayOfWeek) {

        return this.setTo(LAST, dayOfWeek);

    }

    private ZonalOperator<PlainDate> setTo(
        final int ordinal,
        final Weekday dayOfWeek
    ) {

        return new SpecialOperator<PlainDate>(ordinal, dayOfWeek);

    }

    private Object readResolve() throws ObjectStreamException {

        return INSTANCE;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class SpecialOperator<T extends ChronoEntity<T>>
        implements ZonalOperator<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final int ordinal;
        private final Weekday dayOfWeek;

        //~ Konstruktoren -------------------------------------------------

        SpecialOperator(
            int ordinal,
            Weekday dayOfWeek
        ) {
            super();

            if (dayOfWeek == null) {
                throw new NullPointerException("Missing value.");
            }

            this.ordinal = ordinal;
            this.dayOfWeek = dayOfWeek;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public T apply(T entity) {

            if (entity.contains(PlainDate.CALENDAR_DATE)) {
                PlainDate date = entity.get(PlainDate.CALENDAR_DATE);
                Weekday current = date.get(PlainDate.DAY_OF_WEEK);
                int delta = this.dayOfWeek.getValue() - current.getValue();
                int dom = date.getDayOfMonth() + delta;
                int days =
                    (this.ordinal - (MathUtils.floorDivide(dom - 1, 7) + 1)) * 7
                    + delta;
                if (this.ordinal == LAST) {
                    int max =
                        GregorianMath.getLengthOfMonth(
                            date.getYear(),
                            date.getMonth());
                    if (date.getDayOfMonth() + days > max) {
                        days -= 7;
                    }
                }
                date = date.plus(days, CalendarUnit.DAYS);
                return entity.with(PlainDate.CALENDAR_DATE, date);
            } else {
                throw new ChronoException(
                    "Rule not found for ordinal day of week in month: "
                    + entity);
            }

        }

        @Override
        public ChronoOperator<Moment> inSystemTimezone() {

            return new Moment.Operator(
                this.onTimestamp(),
                WeekdayInMonthElement.INSTANCE,
                OperatorType.WIM
            );

        }

        @Override
        public ChronoOperator<Moment> inTimezone(
            TZID tzid,
            TransitionStrategy strategy
        ) {

            return new Moment.Operator(
                this.onTimestamp(),
                tzid,
                strategy,
                WeekdayInMonthElement.INSTANCE,
                OperatorType.WIM
            );

        }

        @Override
        public ChronoOperator<PlainTimestamp> onTimestamp() {

            return new SpecialOperator<PlainTimestamp>(
                this.ordinal,
                this.dayOfWeek
            );
        }

    }

}
