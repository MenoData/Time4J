/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarContent.java) is part of project Time4J.
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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.time4j.engine.CalendarDate;


class CalendarContent<T extends CalendarDate>
    extends BorderPane {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String CSS_CALENDAR = "calendar";
    private static final String CSS_CALENDAR_HEADER = "calendar-header";
    private static final String CSS_CALENDAR_NAVIGATION_INFO = "calendar-navigation-info";

    //~ Instanzvariablen --------------------------------------------------

    private VBox header;

    //~ Konstruktoren -----------------------------------------------------

    CalendarContent(
        CalendarControl<T> control,
        FXCalendarSystem<T> calsys
    ) {
        super();

        this.setEffect(new DropShadow());
        this.getStyleClass().add(CSS_CALENDAR);
        this.setMaxWidth(Control.USE_PREF_SIZE);

        NavigationBar<T> navigationBar = new NavigationBar<>(control, calsys);
        this.header = createHeader(control, navigationBar);
        CalendarView view = new CalendarView<>(control, calsys, navigationBar.getTitleButton());

        this.setTop(this.header);
        this.setCenter(view);
        this.setBottom(new CalendarFooter<>(control));

        control.showInfoLabelProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean infoVisible = (header.getChildren().size() == 2);
                if (newValue != infoVisible) {
                    if (newValue) {
                        header.getChildren().add(createLabel(control));
                    } else {
                        header.getChildren().remove(1);
                    }
                }
            }
        );

    }

    private static VBox createHeader(
        CalendarControl<?> control,
        NavigationBar<?> navigationBar
    ) {

        VBox header = new VBox();
        header.getStyleClass().add(CSS_CALENDAR_HEADER);
        header.getChildren().add(navigationBar);

        if (control.showInfoLabelProperty().get()) {
            header.getChildren().add(createLabel(control));
        }

        return header;

    }

    private static Node createLabel(CalendarControl<?> control) {

        Label label = new Label();
        label.textProperty().bind(control.navigationInfoProperty());
        HBox bottom = new HBox();
        bottom.getChildren().add(label);
        bottom.setAlignment(Pos.CENTER);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.getStyleClass().add(CSS_CALENDAR_NAVIGATION_INFO);
        return bottom;

    }

}
