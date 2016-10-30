/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemMinguo.java) is part of project Time4J.
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

package net.time4j.ui.javafx;

import net.time4j.CalendarUnit;
import net.time4j.Month;
import net.time4j.Weekmodel;
import net.time4j.calendar.MinguoCalendar;
import net.time4j.calendar.MinguoEra;
import net.time4j.engine.TimeAxis;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.calendar.MinguoCalendar.*;


class FXCalendarSystemMinguo
    extends FXCalendarSystemBase<CalendarUnit, MinguoCalendar> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return MinguoCalendar.getDefaultWeekmodel();
    }

    @Override
    protected CalendarUnit getMonthsUnit() {
        return CalendarUnit.MONTHS;
    }

    @Override
    protected CalendarUnit getYearsUnit() {
        return CalendarUnit.YEARS;
    }

    @Override
    protected TimeAxis<CalendarUnit, MinguoCalendar> getChronology() {
        return MinguoCalendar.axis();
    }

    @Override
    public int getMonth(MinguoCalendar date) {
        return date.getMonth().getValue();
    }

    @Override
    public int getYear(MinguoCalendar date) {
        return date.getYear();
    }

    @Override
    public int getCountOfMonths() {
        return 12;
    }

    @Override
    public String formatMonth(
        int month,
        Locale locale
    ) {
        return Month.valueOf(month).getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE);
    }

    @Override
    public MinguoCalendar withMonth(
        MinguoCalendar date,
        int month
    ) {
        return date.with(MONTH_OF_YEAR, Month.valueOf(month));
    }

    @Override
    public MinguoCalendar withFirstDayOfMonth(MinguoCalendar date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public MinguoCalendar withLastDayOfMonth(MinguoCalendar date) {
        return date.with(DAY_OF_MONTH.maximized());
    }

    @Override
    public MinguoCalendar withFirstDayOfYear(MinguoCalendar date) {
        return date.with(DAY_OF_YEAR, 1);
    }

    @Override
    public MinguoCalendar withLastDayOfYear(MinguoCalendar date) {
        return date.with(DAY_OF_YEAR.maximized());
    }

    @Override
    public int getProlepticYear(MinguoCalendar date) {
        int yoe = this.getYear(date);
        if (date.getEra() == MinguoEra.BEFORE_ROC) {
            yoe = 1 - yoe;
        }
        return yoe;
    }

}
