/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemPersian.java) is part of project Time4J.
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
import net.time4j.calendar.PersianCalendar;
import net.time4j.calendar.PersianMonth;
import net.time4j.engine.TimeAxis;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.calendar.PersianCalendar.*;


class FXCalendarSystemPersian
    extends FXCalendarSystemBase<PersianCalendar.Unit, PersianCalendar> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return PersianCalendar.getDefaultWeekmodel();
    }

    @Override
    protected PersianCalendar.Unit getMonthsUnit() {
        return PersianCalendar.Unit.MONTHS;
    }

    @Override
    protected PersianCalendar.Unit getYearsUnit() {
        return PersianCalendar.Unit.YEARS;
    }

    @Override
    protected TimeAxis<PersianCalendar.Unit, PersianCalendar> getChronology() {
        return PersianCalendar.axis();
    }

    @Override
    public int getMonth(PersianCalendar date) {
        return date.getMonth().getValue();
    }

    @Override
    public int getYear(PersianCalendar date) {
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
        return PersianMonth.valueOf(month).getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE);
    }

    @Override
    public PersianCalendar withMonth(
        PersianCalendar date,
        int month
    ) {
        return date.with(MONTH_OF_YEAR, PersianMonth.valueOf(month));
    }

    @Override
    public PersianCalendar withFirstDayOfMonth(PersianCalendar date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public PersianCalendar withLastDayOfMonth(PersianCalendar date) {
        return date.with(DAY_OF_MONTH.maximized());
    }

    @Override
    public PersianCalendar withFirstDayOfYear(PersianCalendar date) {
        return date.with(DAY_OF_YEAR, 1);
    }

    @Override
    public PersianCalendar withLastDayOfYear(PersianCalendar date) {
        return date.with(DAY_OF_YEAR.maximized());
    }

}
