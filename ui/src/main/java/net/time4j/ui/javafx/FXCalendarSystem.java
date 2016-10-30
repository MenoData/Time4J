/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FXCalendarSystem.java) is part of project Time4J.
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
import net.time4j.engine.CalendarDate;
import net.time4j.engine.VariantSource;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.expert.ChronoFormatter;

import java.util.Locale;
import java.util.Optional;


interface FXCalendarSystem<T extends CalendarDate> {

    //~ Methoden ----------------------------------------------------------

    default TableView<T> getBirdView(
        CalendarControl<T> control,
        boolean animationMode
    ) {
        return new DecadeView<>(control, this, animationMode);
    }

    default int getDirection(
        int viewIndex,
        T oldDate,
        T newDate
    ) {
        int oldYear = getYear(oldDate);
        int newYear = getYear(newDate);

        if (viewIndex >= NavigationBar.YEAR_VIEW) {
            return (oldYear == newYear) ? 0 : (oldYear > newYear ? 1 : -1);
        } else {
            int oldMonth = getMonth(oldDate);
            int newMonth = getMonth(newDate);

            if (newYear > oldYear || (newYear == oldYear && newMonth > oldMonth)) {
                return -1;
            } else if (newYear < oldYear || (newYear == oldYear && newMonth < oldMonth)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    T getChronologicalMinimum();

    T getChronologicalMaximum();

    default String getCalendarType() {
        CalendarType ft = getChronologicalMinimum().getClass().getAnnotation(CalendarType.class);
        return ((ft == null) ? CalendarText.ISO_CALENDAR_TYPE : ft.value());
    }

    default Optional<VariantSource> getVariantSource() {
        return Optional.empty();
    }

    //~ navigation bar ----------------------------------------------------

    // moves left or right based on view-index
    T move(CalendarControl<T> control, int direction);

    // maximum of supported view-index (should never beyond year-view if there is more than one era)
    default int getMaxView() {
        return NavigationBar.BIRD_VIEW;
    }

    //~ month, year or decade view ----------------------------------------

    T navigateByDays(T date, int days);

    Weekmodel getDefaultWeekmodel();

    ChronoFormatter<T> createTooltipFormat(Locale locale);

    int getCountOfMonths();

    String formatMonth(
        int month,
        Locale locale
    );

    T withMonth(
        T date,
        int month
    );

    int getMonth(T date);

    int getYear(T date);

    T withFirstDayOfYear(T date);

    T withLastDayOfYear(T date);

    T withFirstDayOfMonth(T date);

    T withLastDayOfMonth(T date);

    T addYears(
        T date,
        int amount
    );

    int getProlepticYear(T date);

}
