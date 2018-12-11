/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemEthiopian.java) is part of project Time4J.
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
import net.time4j.calendar.EthiopianCalendar;
import net.time4j.calendar.EthiopianEra;
import net.time4j.calendar.EthiopianMonth;
import net.time4j.engine.TimeAxis;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.calendar.EthiopianCalendar.*;


class FXCalendarSystemEthiopian
    extends FXCalendarSystemBase<Unit, EthiopianCalendar> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return EthiopianCalendar.getDefaultWeekmodel();
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
    protected TimeAxis<Unit, EthiopianCalendar> getChronology() {
        return EthiopianCalendar.axis();
    }

    @Override
    public int getMonth(EthiopianCalendar date) {
        return date.getMonth().getValue();
    }

    @Override
    public int getProlepticYear(EthiopianCalendar date) {
        return date.getYear() + ((date.getEra() == EthiopianEra.AMETE_MIHRET) ? 5500 : 0); // always amete-alem-year
    }

    @Override
    public int getMaxCountOfMonths() {
        return 13;
    }

    @Override
    public String formatMonth(
        int month,
        Locale locale,
        EthiopianCalendar date
    ) {
        return EthiopianMonth.valueOf(month)
            .getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE);
    }

    @Override
    public EthiopianCalendar withMonth(
        EthiopianCalendar date,
        int month
    ) {
        return date.with(MONTH_OF_YEAR, EthiopianMonth.valueOf(month));
    }

    @Override
    public EthiopianCalendar withFirstDayOfMonth(EthiopianCalendar date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public EthiopianCalendar withLastDayOfMonth(EthiopianCalendar date) {
        return date.with(DAY_OF_MONTH.maximized());
    }

    @Override
    public EthiopianCalendar withFirstDayOfYear(EthiopianCalendar date) {
        return date.with(DAY_OF_YEAR, 1);
    }

    @Override
    public EthiopianCalendar withLastDayOfYear(EthiopianCalendar date) {
        return date.with(DAY_OF_YEAR.maximized());
    }

}
