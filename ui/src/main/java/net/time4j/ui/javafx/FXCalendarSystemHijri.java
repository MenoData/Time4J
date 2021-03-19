/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystemHijri.java) is part of project Time4J.
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
import net.time4j.calendar.HijriCalendar;
import net.time4j.calendar.HijriMonth;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.VariantSource;
import net.time4j.format.expert.ChronoFormatter;

import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;

import static net.time4j.calendar.HijriCalendar.*;


class FXCalendarSystemHijri
    implements FXCalendarSystem<HijriCalendar> {

    //~ Instanzvariablen --------------------------------------------------

    private CalendarSystem<HijriCalendar> calsys;

    //~ Konstruktoren -----------------------------------------------------

    FXCalendarSystemHijri(String variant) {
        super();

        this.calsys = HijriCalendar.family().getCalendarSystem(variant);
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public HijriCalendar getChronologicalMinimum() {
        long utcDays = this.calsys.getMinimumSinceUTC();
        return this.calsys.transform(utcDays);
    }

    @Override
    public HijriCalendar getChronologicalMaximum() {
        long utcDays = this.calsys.getMaximumSinceUTC();
        return this.calsys.transform(utcDays);
    }

    @Override
    public Optional<VariantSource> getVariantSource() {
        return Optional.of(() -> getChronologicalMinimum().getVariant());
    }

    @Override
    public HijriCalendar move(
        CalendarControl<HijriCalendar> control,
        int direction
    ) {
        HijriCalendar date = control.pageDateProperty().getValue();
        HijriCalendar result;

        switch (control.viewIndexProperty().get()) {
            case NavigationBar.MONTH_VIEW:
                result = date.plus(direction, Unit.MONTHS);
                break;
            case NavigationBar.YEAR_VIEW:
                result = date.plus(direction, Unit.YEARS);
                break;
            case NavigationBar.BIRD_VIEW:
                result = date.plus(Math.multiplyExact(direction, 10), Unit.YEARS);
                break;
            default:
                throw new IllegalStateException("Invalid view: " + control.viewIndexProperty().getValue());
        }

        return result;
    }

    @Override
    public HijriCalendar navigateByDays(
        HijriCalendar date,
        int days
    ) {
        return date.plus(CalendarDays.of(days));
    }

    @Override
    public Weekmodel getDefaultWeekmodel() {
        return HijriCalendar.getDefaultWeekmodel();
    }

    @Override
    public ChronoFormatter<HijriCalendar> createTooltipFormat(Locale locale) {
        return ChronoFormatter.ofStyle(FormatStyle.LONG, locale, HijriCalendar.family());
    }

    @Override
    public int getMaxCountOfMonths() {
        return 12;
    }

    @Override
    public String formatMonth(
        int month,
        Locale locale,
        HijriCalendar date
    ) {
        return HijriMonth.valueOf(month).getDisplayName(locale);
    }

    @Override
    public HijriCalendar withMonth(
        HijriCalendar date,
        int month
    ) {
        return date.with(MONTH_OF_YEAR, HijriMonth.valueOf(month));
    }

    @Override
    public int getMonth(HijriCalendar date) {
        return date.getMonth().getValue();
    }

    @Override
    public int getProlepticYear(HijriCalendar date) {
        return date.getYear();
    }

    @Override
    public HijriCalendar withFirstDayOfYear(HijriCalendar date) {
        return date.with(DAY_OF_YEAR, 1);
    }

    @Override
    public HijriCalendar withLastDayOfYear(HijriCalendar date) {
        return date.with(DAY_OF_YEAR.maximized());
    }

    @Override
    public HijriCalendar withFirstDayOfMonth(HijriCalendar date) {
        return date.with(DAY_OF_MONTH, 1);
    }

    @Override
    public HijriCalendar withLastDayOfMonth(HijriCalendar date) {
        return date.with(DAY_OF_MONTH.maximized());
    }

    @Override
    public HijriCalendar addYears(
        HijriCalendar date,
        int amount
    ) {
        return date.plus(amount, Unit.YEARS);
    }

}
