/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemIso8601.java) is part of project Time4J.
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
import net.time4j.IsoDateUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.Weekmodel;
import net.time4j.engine.TimeAxis;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.PlainDate.*;


class FXCalendarSystemIso8601
    extends FXCalendarSystemBase<IsoDateUnit, PlainDate> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return Weekmodel.ISO;
    }

    @Override
    protected IsoDateUnit getMonthsUnit() {
        return CalendarUnit.MONTHS;
    }

    @Override
    protected IsoDateUnit getYearsUnit() {
        return CalendarUnit.YEARS;
    }

    @Override
    protected TimeAxis<IsoDateUnit, PlainDate> getChronology() {
        return PlainDate.axis();
    }

    @Override
    public int getMonth(PlainDate date) {
        return date.getMonth();
    }

    @Override
    public int getYear(PlainDate date) {
        return date.getYear();
    }

    @Override
    public int getMaxCountOfMonths() {
        return 12;
    }

    @Override
    public String formatMonth(
        int month,
        Locale locale,
        PlainDate date
    ) {
        return Month.valueOf(month).getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE);
    }

    @Override
    public PlainDate withMonth(
        PlainDate date,
        int month
    ) {
        return date.with(MONTH_AS_NUMBER, month);
    }

    @Override
    public PlainDate withFirstDayOfMonth(PlainDate date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public PlainDate withLastDayOfMonth(PlainDate date) {
        return date.with(DAY_OF_MONTH.maximized());
    }

    @Override
    public PlainDate withFirstDayOfYear(PlainDate date) {
        return PlainDate.of(date.getYear(), 1, 1);
    }

    @Override
    public PlainDate withLastDayOfYear(PlainDate date) {
        return date.with(DAY_OF_YEAR.maximized());
    }

}
