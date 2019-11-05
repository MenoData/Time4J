/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2019 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemCoptic.java) is part of project Time4J.
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
import net.time4j.calendar.CopticCalendar;
import net.time4j.calendar.CopticMonth;
import net.time4j.engine.TimeAxis;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.calendar.CopticCalendar.*;


class FXCalendarSystemCoptic
    extends FXCalendarSystemBase<Unit, CopticCalendar> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return CopticCalendar.getDefaultWeekmodel();
    }

    @Override
    protected Unit getMonthsUnit() {
        return Unit.MONTHS;
    }

    @Override
    protected Unit getYearsUnit() {
        return Unit.YEARS;
    }

    @Override
    protected TimeAxis<Unit, CopticCalendar> getChronology() {
        return CopticCalendar.axis();
    }

    @Override
    public int getMonth(CopticCalendar date) {
        return date.getMonth().getValue();
    }

    @Override
    public int getProlepticYear(CopticCalendar date) {
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
        CopticCalendar date
    ) {
        return CopticMonth.valueOf(month)
            .getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE);
    }

    @Override
    public CopticCalendar withMonth(
        CopticCalendar date,
        int month
    ) {
        return date.with(MONTH_OF_YEAR, CopticMonth.valueOf(month));
    }

    @Override
    public CopticCalendar withFirstDayOfMonth(CopticCalendar date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public CopticCalendar withLastDayOfMonth(CopticCalendar date) {
        return date.with(DAY_OF_MONTH.maximized());
    }

    @Override
    public CopticCalendar withFirstDayOfYear(CopticCalendar date) {
        return date.with(DAY_OF_YEAR, 1);
    }

    @Override
    public CopticCalendar withLastDayOfYear(CopticCalendar date) {
        return date.with(DAY_OF_YEAR.maximized());
    }

}
