/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarView.java) is part of project Time4J.
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

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import net.time4j.engine.CalendarDate;


class CalendarView<T extends CalendarDate>
    extends StackPane {

    //~ Instanzvariablen --------------------------------------------------

    private SlidingStackPane<T> monthView;
    private SlidingStackPane<T> yearView;
    private SlidingStackPane<T> birdView;

    //~ Konstruktoren -----------------------------------------------------

    CalendarView(
        CalendarControl<T> control,
        FXCalendarSystem<T> calsys,
        Button titleButton
    ) {
        super();

        this.monthView =
            new SlidingStackPane<>(
                new MonthView<>(control, calsys, true),
                new MonthView<>(control, calsys, false));
        this.yearView =
            new SlidingStackPane<>(
                new YearView<>(control, calsys, true),
                new YearView<>(control, calsys, false));
        getChildren().addAll(this.monthView, this.yearView);
        this.monthView.updateVisibility(true);
        this.yearView.updateVisibility(false);

        if (calsys.getMaxView() >= NavigationBar.BIRD_VIEW) {
            this.birdView =
                new SlidingStackPane<>(
                    calsys.getBirdView(control, true),
                    calsys.getBirdView(control, false));
            getChildren().add(this.birdView);
            this.birdView.updateVisibility(false);
        } else {
            this.birdView = null;
        }

        control.viewIndexProperty().set(NavigationBar.MONTH_VIEW);
        control.navigationTitleProperty().bind(monthView.getCurrentTitle());
        control.navigationInfoProperty().bind(monthView.getCurrentInfo());

        control.selectedDateProperty().addListener(
            observable -> {
                control.viewIndexProperty().set(NavigationBar.MONTH_VIEW);
            }
        );

        control.viewIndexProperty().addListener(
            (observable, oldIndex, newIndex) -> {
                control.navigationTitleProperty().unbind();
                control.navigationInfoProperty().unbind();

                switch (oldIndex.intValue()) {
                    case NavigationBar.MONTH_VIEW:
                        monthView.updateVisibility(false);
                        switch (newIndex.intValue()) {
                            case NavigationBar.YEAR_VIEW:
                                control.navigationTitleProperty().bind(yearView.getCurrentTitle());
                                control.navigationInfoProperty().bind(yearView.getCurrentInfo());
                                show(control, titleButton, yearView);
                                yearView.updateVisibility(true);
                                break;
                        }
                        break;
                    case NavigationBar.YEAR_VIEW:
                        yearView.updateVisibility(false);
                        switch (newIndex.intValue()) {
                            case NavigationBar.MONTH_VIEW:
                                hide(control, titleButton, yearView);
                                control.navigationTitleProperty().bind(monthView.getCurrentTitle());
                                control.navigationInfoProperty().bind(monthView.getCurrentInfo());
                                monthView.updateVisibility(true);
                                break;
                            case NavigationBar.BIRD_VIEW:
                                control.navigationTitleProperty().bind(birdView.getCurrentTitle());
                                control.navigationInfoProperty().bind(birdView.getCurrentInfo());
                                show(control, titleButton, birdView);
                                birdView.updateVisibility(true);
                                break;
                        }
                        break;
                    case NavigationBar.BIRD_VIEW:
                        birdView.updateVisibility(false);
                        switch (newIndex.intValue()) {
                            case NavigationBar.MONTH_VIEW:
                                yearView.updateVisibility(false);
                                hide(control, titleButton, birdView);
                                control.navigationTitleProperty().bind(monthView.getCurrentTitle());
                                control.navigationInfoProperty().bind(monthView.getCurrentInfo());
                                monthView.updateVisibility(true);
                                break;
                            case NavigationBar.YEAR_VIEW:
                                hide(control, titleButton, birdView);
                                control.navigationTitleProperty().bind(yearView.getCurrentTitle());
                                control.navigationInfoProperty().bind(yearView.getCurrentInfo());
                                yearView.updateVisibility(true);
                                break;
                        }
                        break;
                }

                titleButton.requestFocus();
            }
        );
    }

    //~ Methoden ----------------------------------------------------------

    private void show(
        CalendarControl<T> control,
        Button titleButton,
        SlidingStackPane stackPane
    ) {

        this.showOrHide(control, titleButton, stackPane, true);

    }

    private void hide(
        CalendarControl<T> control,
        Button titleButton,
        SlidingStackPane stackPane
    ) {

        this.showOrHide(control, titleButton, stackPane, false);

    }

    private void showOrHide(
        CalendarControl<T> control,
        Button titleButton,
        SlidingStackPane stackPane,
        boolean show
    ) {

        Duration duration = control.lengthOfAnimationsProperty().get();

        if (duration.lessThanOrEqualTo(Duration.ZERO)) {
            if (!show) {
                titleButton.requestFocus();
            }
            stackPane.updateVisibility(show);
            return;
        }

        stackPane.updateVisibility(true);
        control.ongoingTransitionsProperty().set(control.ongoingTransitionsProperty().get() + 1);

        TranslateTransition translateTransition = new TranslateTransition(duration, stackPane);
        FadeTransition fadeTransition = new FadeTransition(duration, stackPane);
        translateTransition.setInterpolator(Interpolator.EASE_OUT);
        fadeTransition.setInterpolator(Interpolator.EASE_OUT);

        stackPane.setCache(true);
        stackPane.setCacheHint(CacheHint.SPEED);

        if (show) {
            translateTransition.setFromY(-stackPane.getBoundsInLocal().getHeight());
            translateTransition.setToY(0);
            fadeTransition.setToValue(1);
            fadeTransition.setFromValue(0);
        } else {
            translateTransition.setToY(-stackPane.getBoundsInLocal().getHeight());
            translateTransition.setFromY(0);
            fadeTransition.setToValue(0);
            fadeTransition.setFromValue(1);
        }

        this.setClip(new Rectangle(stackPane.getBoundsInLocal().getWidth(), stackPane.getBoundsInLocal().getHeight()));

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().add(translateTransition);
        parallelTransition.getChildren().add(fadeTransition);
        parallelTransition.playFromStart();

        parallelTransition.setOnFinished(
            actionEvent -> {
                if (!show) {
                    titleButton.requestFocus();
                    stackPane.updateVisibility(false);
                }
                stackPane.setCache(false);
                control.ongoingTransitionsProperty().set(control.ongoingTransitionsProperty().get() - 1);
            }
        );

    }

}
