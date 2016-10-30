/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarFooter.java) is part of project Time4J.
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

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import net.time4j.PrettyTime;
import net.time4j.engine.CalendarDate;

import java.util.Locale;


class CalendarFooter<T extends CalendarDate>
    extends HBox {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String CSS_CALENDAR_FOOTER = "calendar-footer";
    private static final String CSS_CALENDAR_TODAY_BUTTON = "calendar-today-button";

    //~ Instanzvariablen --------------------------------------------------

    private CalendarControl<T> control;

    //~ Konstruktoren -----------------------------------------------------

    CalendarFooter(CalendarControl<T> control) {
        super();

        this.control = control;
        this.getStyleClass().add(CSS_CALENDAR_FOOTER);

        Button todayButton = new Button();
        todayButton.textProperty().bind(new TodayBinding());
        todayButton.getStyleClass().add(CSS_CALENDAR_TODAY_BUTTON);
        todayButton.setTooltip(new Tooltip(control.today().toString()));
        todayButton.setOnAction(
            actionEvent -> {
                control.pageDateProperty().setValue(control.today());
                control.viewIndexProperty().setValue(NavigationBar.MONTH_VIEW);
            }
        );
        todayButton.disableProperty().bind(new DisabledBinding());
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(todayButton);
    }

    //~ Innere Klassen ----------------------------------------------------

    private class TodayBinding
        extends StringBinding {

        //~ Konstruktoren -------------------------------------------------

        TodayBinding() {
            super();

            this.bind(control.localeProperty());

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected String computeValue() {

            Locale locale = control.localeProperty().get();
            if (locale == null) {
                locale = Locale.ROOT;
            }
            return PrettyTime.of(locale).printToday();

        }

    }

    private class DisabledBinding
        extends BooleanBinding {

        //~ Konstruktoren -------------------------------------------------

        DisabledBinding() {
            super();

            this.bind(
                control.minDateProperty(),
                control.maxDateProperty()
            );

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected boolean computeValue() {

            T date = control.today();

            return (
                date.isBefore(control.minDateProperty().get())
                || date.isAfter(control.maxDateProperty().get())
            );

        }

    }

}
