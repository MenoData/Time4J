/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemBase.java) is part of project Time4J.
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

import net.time4j.engine.CalendarDays;
import net.time4j.engine.Calendrical;
import net.time4j.engine.TimeAxis;
import net.time4j.format.DisplayMode;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.expert.ChronoFormatter;

import java.util.Locale;


abstract class FXCalendarSystemBase<U, D extends Calendrical<U, D> & LocalizedPatternSupport>
    implements FXCalendarSystem<D> {

    //~ Methoden ----------------------------------------------------------

    @Override
    public D move(
        CalendarControl<D> control,
        int direction
    ) {
        D date = control.pageDateProperty().getValue();
        D result;

        switch (control.viewIndexProperty().get()) {
            case NavigationBar.MONTH_VIEW:
                result = date.plus(direction, this.getMonthsUnit());
                break;
            case NavigationBar.YEAR_VIEW:
                result = date.plus(direction, this.getYearsUnit());
                break;
            case NavigationBar.BIRD_VIEW:
                result = date.plus(Math.multiplyExact(direction, 10), this.getYearsUnit());
                break;
            default:
                throw new IllegalStateException("Invalid view: " + control.viewIndexProperty().getValue());
        }

        return result;
    }

    @Override
    public D navigateByDays(
        D date,
        int days
    ) {
        return date.plus(CalendarDays.of(days));
    }

    @Override
    public D getChronologicalMinimum() {
        return this.getChronology().getMinimum();
    }

    @Override
    public D getChronologicalMaximum() {
        return this.getChronology().getMaximum();
    }

    @Override
    public ChronoFormatter<D> createTooltipFormat(Locale locale) {
        return ChronoFormatter.ofStyle(DisplayMode.LONG, locale, this.getChronology());
    }

    @Override
    public D addYears(
        D date,
        int amount
    ) {
        return date.plus(amount, this.getYearsUnit());
    }

    @Override
    public int getProlepticYear(D date) {
        return this.getYear(date);
    }

    protected abstract U getMonthsUnit();

    protected abstract U getYearsUnit();

    protected abstract TimeAxis<U, D> getChronology();

}
