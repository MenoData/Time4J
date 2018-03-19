/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemJulian.java) is part of project Time4J.
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

import net.time4j.Month;
import net.time4j.Weekmodel;
import net.time4j.calendar.JulianCalendar;
import net.time4j.engine.TimeAxis;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import net.time4j.history.HistoricEra;

import java.util.Locale;

import static net.time4j.calendar.JulianCalendar.*;


class FXCalendarSystemJulian
    extends FXCalendarSystemBase<JulianCalendar.Unit, JulianCalendar> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return JulianCalendar.getDefaultWeekmodel();
    }

    @Override
    protected JulianCalendar.Unit getMonthsUnit() {
        return JulianCalendar.Unit.MONTHS;
    }

    @Override
    protected JulianCalendar.Unit getYearsUnit() {
        return JulianCalendar.Unit.YEARS;
    }

    @Override
    protected TimeAxis<JulianCalendar.Unit, JulianCalendar> getChronology() {
        return JulianCalendar.axis();
    }

    @Override
    public int getMonth(JulianCalendar date) {
        return date.getMonth().getValue();
    }

    @Override
    public int getProlepticYear(JulianCalendar date) {
        int yoe = date.getYear();
        if (date.getEra() == HistoricEra.BC) {
            yoe = 1 - yoe;
        }
        return yoe;
    }

    @Override
    public int getMaxCountOfMonths() {
        return 12;
    }

    @Override
    public String formatMonth(
        int month,
        Locale locale,
        JulianCalendar date
    ) {
        return Month.valueOf(month).getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE);
    }

    @Override
    public JulianCalendar withMonth(
        JulianCalendar date,
        int month
    ) {
        return date.with(MONTH_OF_YEAR, month);
    }

    @Override
    public JulianCalendar withFirstDayOfMonth(JulianCalendar date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public JulianCalendar withLastDayOfMonth(JulianCalendar date) {
        int max = date.getMaximum(DAY_OF_MONTH);
        return date.with(DAY_OF_MONTH, max);
    }

    @Override
    public JulianCalendar withFirstDayOfYear(JulianCalendar date) {
        return date.with(DAY_OF_YEAR, 1);
    }

    @Override
    public JulianCalendar withLastDayOfYear(JulianCalendar date) {
        int max = date.getMaximum(DAY_OF_YEAR);
        return date.with(DAY_OF_YEAR, max);
    }

}
