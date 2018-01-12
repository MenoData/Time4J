/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WeekdayRule.java) is part of project Time4J.
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
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ElementRule;


/**
 * Generic element rule for weekdays.
 *
 * @author  Meno Hochschild
 * @since   3.39/4.34
 */
class WeekdayRule<D extends CalendarDate>
    implements ElementRule<D, Weekday> {

    //~ Instanzvariablen --------------------------------------------------

    private final Weekmodel stdWeekmodel;
    private final ChronoFunction<D, CalendarSystem<D>> calsysFunc;

    //~ Konstruktoren -----------------------------------------------------

    WeekdayRule(
        Weekmodel stdWeekmodel,
        ChronoFunction<D, CalendarSystem<D>> calsysFunc
    ) {
        super();

        this.stdWeekmodel = stdWeekmodel;
        this.calsysFunc = calsysFunc;
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekday getValue(D context) {
        return getWeekday(context.getDaysSinceEpochUTC());
    }

    @Override
    public Weekday getMinimum(D context) {
        CalendarSystem<D> cs = this.calsysFunc.apply(context);
        int oldNum = this.getValue(context).getValue(this.stdWeekmodel);

        if (context.getDaysSinceEpochUTC() + 1 - oldNum < cs.getMinimumSinceUTC()) {
            return getWeekday(cs.getMinimumSinceUTC());
        }

        return this.stdWeekmodel.getFirstDayOfWeek();
    }

    @Override
    public Weekday getMaximum(D context) {
        CalendarSystem<D> cs = this.calsysFunc.apply(context);
        int oldNum = this.getValue(context).getValue(this.stdWeekmodel);

        if (context.getDaysSinceEpochUTC() + 7 - oldNum > cs.getMaximumSinceUTC()) {
            return getWeekday(cs.getMaximumSinceUTC());
        }

        return this.stdWeekmodel.getFirstDayOfWeek().roll(6);
    }

    @Override
    public boolean isValid(
        D context,
        Weekday value
    ) {
        if (value == null) {
            return false;
        }

        int oldValue = this.getValue(context).getValue(this.stdWeekmodel);
        int newValue = value.getValue(this.stdWeekmodel);
        long utcDays = context.getDaysSinceEpochUTC() + newValue - oldValue;
        CalendarSystem<D> cs = this.calsysFunc.apply(context);
        return (utcDays >= cs.getMinimumSinceUTC()) && (utcDays <= cs.getMaximumSinceUTC());
    }

    @Override
    public D withValue(
        D context,
        Weekday value,
        boolean lenient
    ) {
        if (value == null) {
            throw new IllegalArgumentException("Missing weekday.");
        }

        int oldValue = this.getValue(context).getValue(this.stdWeekmodel);
        int newValue = value.getValue(this.stdWeekmodel);
        long utcDays = context.getDaysSinceEpochUTC() + newValue - oldValue;
        CalendarSystem<D> cs = this.calsysFunc.apply(context);

        if ((utcDays >= cs.getMinimumSinceUTC()) && (utcDays <= cs.getMaximumSinceUTC())) {
            return cs.transform(utcDays);
        } else {
            throw new IllegalArgumentException("New day out of supported range.");
        }
    }

    @Override
    public ChronoElement<?> getChildAtFloor(D context) {
        return null;
    }

    @Override
    public ChronoElement<?> getChildAtCeiling(D context) {
        return null;
    }

    private static Weekday getWeekday(long utcDays) {
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);
    }

}
