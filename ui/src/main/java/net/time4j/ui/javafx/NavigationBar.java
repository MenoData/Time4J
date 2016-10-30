/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NavigationBar.java) is part of project Time4J.
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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.time4j.engine.CalendarDate;


class NavigationBar<T extends CalendarDate>
    extends HBox {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String CSS_CALENDAR_NAVIGATION_ARROW = "calendar-navigation-arrow";
    private static final String CSS_CALENDAR_NAVIGATION_TITLE = "calendar-navigation-title";

    private static final int LEFT = -1;
    private static final int RIGHT = 1;

    static final int MONTH_VIEW = 0;
    static final int YEAR_VIEW = 1;
    static final int BIRD_VIEW = 2;

    //~ Instanzvariablen --------------------------------------------------

    protected CalendarControl<T> control;
    private FXCalendarSystem<T> calsys;
    private Button titleButton;

    //~ Konstruktoren -----------------------------------------------------

    NavigationBar(
        CalendarControl<T> control,
        FXCalendarSystem<T> calsys
    ) {
        super();

        this.control = control;
        this.calsys = calsys;
        this.titleButton = this.createTitleButton();

        this.setFocusTraversable(true);

        HBox center = new HBox();
        center.getChildren().add(this.titleButton);
        center.setAlignment(Pos.CENTER);
        HBox.setHgrow(center, Priority.ALWAYS);

        Button extraLeft = this.createNavigationButton(10 * LEFT);
        Button extraRight = this.createNavigationButton(10 * RIGHT);
        extraLeft.setVisible(false);
        extraRight.setVisible(false);

        getChildren().add(extraLeft);
        getChildren().add(this.createNavigationButton(LEFT));
        getChildren().add(center);
        getChildren().add(this.createNavigationButton(RIGHT));
        getChildren().add(extraRight);

        control.viewIndexProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean birdView = (newValue.intValue() == NavigationBar.BIRD_VIEW);
                extraLeft.setVisible(birdView);
                extraRight.setVisible(birdView);
            }
        );

        // suppress arrow keys
        this.setEventHandler(
            KeyEvent.KEY_PRESSED,
            event -> {
                KeyCode code = event.getCode();
                if (code.isArrowKey()) {
                    event.consume();
                }
            }
        );

    }

    //~ Methoden ----------------------------------------------------------

    Button getTitleButton() {

        return this.titleButton;

    }

    private T move(int direction) {

        return this.calsys.move(this.control, direction);

    }

    private int getMaxView() {

        return this.calsys.getMaxView();

    }

    private Button createTitleButton() {

        Button titleButton = new Button();
        titleButton.getStyleClass().add(CSS_CALENDAR_NAVIGATION_TITLE);
        titleButton.textProperty().bind(control.navigationTitleProperty());

        titleButton.setOnAction(
            actionEvent -> {
                switch (control.viewIndexProperty().get()) {
                    case MONTH_VIEW:
                        control.viewIndexProperty().set(YEAR_VIEW);
                        break;
                    case YEAR_VIEW:
                        control.viewIndexProperty().set(BIRD_VIEW);
                        break;
                    default:
                        // no-op
                }
            }
        );

        titleButton.disableProperty().bind(new DisabledTitleBinding());
        return titleButton;

    }

    private Button createNavigationButton(int direction) {

        Button button = new Button();

        button.setOnAction(
            actionEvent -> {
                T date = move(direction);
                control.pageDateProperty().setValue(date);
            }
        );

        switch (direction) {
            case 10 * LEFT:
                button.setText("<<");
                break;
            case LEFT:
                button.setText("<");
                break;
            case RIGHT:
                button.setText(">");
                break;
            case 10 * RIGHT:
                button.setText(">>");
                break;
            default:
                button.setText("");
        }

        button.getStyleClass().add(CSS_CALENDAR_NAVIGATION_ARROW);
        button.disableProperty().bind(new DisabledArrowBinding(direction));
        return button;

    }

    //~ Innere Klassen ----------------------------------------------------

    private class DisabledTitleBinding
        extends BooleanBinding {

        //~ Konstruktoren -------------------------------------------------

        DisabledTitleBinding() {
            super();

            this.bind(control.ongoingTransitionsProperty(), control.viewIndexProperty());

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected boolean computeValue() {

            return (
                (control.viewIndexProperty().get() >= getMaxView())
                || (control.ongoingTransitionsProperty().get() > 0)
            );

        }

    }

    private class DisabledArrowBinding
        extends BooleanBinding {

        //~ Instanzvariablen ----------------------------------------------

        private final int direction;

        //~ Konstruktoren -------------------------------------------------

        DisabledArrowBinding(int direction) {
            super();

            this.direction = direction;

            this.bind(
                control.minDateProperty(),
                control.maxDateProperty(),
                control.viewIndexProperty(),
                control.pageDateProperty()
            );

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected boolean computeValue() {

            try {
                T date = move(this.direction);

                return (
                    date.isBefore(control.minDateProperty().get())
                    || date.isAfter(control.maxDateProperty().get())
                );
            } catch (ArithmeticException ex) {
                return true;
            }

        }

    }

}
