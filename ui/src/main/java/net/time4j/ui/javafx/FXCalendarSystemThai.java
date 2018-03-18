/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemThai.java) is part of project Time4J.
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
import net.time4j.PlainDate;
import net.time4j.Weekmodel;
import net.time4j.calendar.ThaiSolarCalendar;
import net.time4j.engine.TimeAxis;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.calendar.ThaiSolarCalendar.*;


class FXCalendarSystemThai
    extends FXCalendarSystemBase<CalendarUnit, ThaiSolarCalendar> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public ThaiSolarCalendar move(
        CalendarControl<ThaiSolarCalendar> control,
        int direction
    ) {
        ThaiSolarCalendar date = control.pageDateProperty().getValue();
        ThaiSolarCalendar result;

        switch (control.viewIndexProperty().get()) {
            case NavigationBar.MONTH_VIEW:
                result = date.plus(direction, CalendarUnit.MONTHS);
                break;
            case NavigationBar.YEAR_VIEW:
                // don't miss 2483 BE under certain circumstances
                if (date.getMonth().getValue() <= 3) {
                    if (date.getYear() == 2482 && direction > 0) {
                        date = date.with(DAY_OF_YEAR, 1);
                    } else if (date.getYear() == 2484 && direction < 0) {
                        date = date.with(DAY_OF_YEAR.maximized());
                    }
                }
                result = date.plus(direction, CalendarUnit.YEARS);
                break;
            case NavigationBar.BIRD_VIEW:
                result = date.plus(Math.multiplyExact(direction, 10), CalendarUnit.YEARS);
                break;
            default:
                throw new IllegalStateException("Invalid view: " + control.viewIndexProperty().getValue());
        }

        return result;
    }

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return ThaiSolarCalendar.getDefaultWeekmodel();
    }

    @Override
    public int getMonth(ThaiSolarCalendar date) {
        return date.getMonth().getValue();
    }

    @Override
    public int getYear(ThaiSolarCalendar date) {
        return date.getYear();
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
    protected TimeAxis<CalendarUnit, ThaiSolarCalendar> getChronology() {
        return ThaiSolarCalendar.axis();
    }

    @Override
    public int getMaxCountOfMonths() {
        return 12;
    }

    @Override
    public String formatMonth(
        int month,
        Locale locale,
        ThaiSolarCalendar date
    ) {
        return Month.valueOf(month).getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE);
    }

    @Override
    public ThaiSolarCalendar withMonth(
        ThaiSolarCalendar date,
        int month
    ) {
        // ensure right month order in year view before 1941 when April was the first month
        if (date.transform(PlainDate.class).getYear() <= 1940) {
            month += 3;
            if (month > 12) {
                month -= 12;
            }
        }
        return date.with(MONTH_OF_YEAR, Month.valueOf(month));
    }

    @Override
    public ThaiSolarCalendar withFirstDayOfMonth(ThaiSolarCalendar date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public ThaiSolarCalendar withLastDayOfMonth(ThaiSolarCalendar date) {
        return date.with(DAY_OF_MONTH.maximized());
    }

    @Override
    public ThaiSolarCalendar withFirstDayOfYear(ThaiSolarCalendar date) {
        return date.with(DAY_OF_YEAR, 1);
    }

    @Override
    public ThaiSolarCalendar withLastDayOfYear(ThaiSolarCalendar date) {
        return date.with(DAY_OF_YEAR.maximized());
    }

    @Override
    public ThaiSolarCalendar addYears(
        ThaiSolarCalendar date,
        int amount
    ) {
        int yoe = date.getYear() + amount;
        if (yoe <= 2483) {
            date = date.with(MONTH_OF_YEAR, Month.APRIL);
        }
        return date.with(YEAR_OF_ERA, yoe);
    }

}
