/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemHebrew.java) is part of project Time4J.
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

import net.time4j.Weekmodel;
import net.time4j.calendar.HebrewCalendar;
import net.time4j.calendar.HebrewMonth;
import net.time4j.engine.TimeAxis;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.calendar.HebrewCalendar.*;


class FXCalendarSystemHebrew
    extends FXCalendarSystemBase<HebrewCalendar.Unit, HebrewCalendar> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return HebrewCalendar.getDefaultWeekmodel();
    }

    @Override
    protected HebrewCalendar.Unit getMonthsUnit() {
        return HebrewCalendar.Unit.MONTHS;
    }

    @Override
    protected HebrewCalendar.Unit getYearsUnit() {
        return HebrewCalendar.Unit.YEARS;
    }

    @Override
    protected TimeAxis<HebrewCalendar.Unit, HebrewCalendar> getChronology() {
        return HebrewCalendar.axis();
    }

    @Override
    public int getMonth(HebrewCalendar date) {
        return date.getMonth().getCivilValue(date.isLeapYear());
    }

    @Override
    public int getYear(HebrewCalendar date) {
        return date.getYear();
    }

    @Override
    public int getMaxCountOfMonths() {
        return 13;
    }

    @Override
    public String formatMonth(
        int month,
        Locale locale,
        HebrewCalendar date
    ) {
        boolean leapYear = date.isLeapYear();
        return HebrewMonth.valueOfCivil(month, leapYear)
            .getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE, leapYear);
    }

    @Override
    public HebrewCalendar withMonth(
        HebrewCalendar date,
        int month
    ) {
        return date.with(MONTH_OF_YEAR, HebrewMonth.valueOfCivil(month, date.isLeapYear()));
    }

    @Override
    public HebrewCalendar withFirstDayOfMonth(HebrewCalendar date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public HebrewCalendar withLastDayOfMonth(HebrewCalendar date) {
        return date.with(DAY_OF_MONTH.maximized());
    }

    @Override
    public HebrewCalendar withFirstDayOfYear(HebrewCalendar date) {
        return date.with(DAY_OF_YEAR, 1);
    }

    @Override
    public HebrewCalendar withLastDayOfYear(HebrewCalendar date) {
        return date.with(DAY_OF_YEAR.maximized());
    }

    @Override
    public int getProlepticYear(HebrewCalendar date) {
        return this.getYear(date);
    }

}
