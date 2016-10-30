/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FadingStackPane.java) is part of project Time4J.
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

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import net.time4j.engine.CalendarDate;


class SlidingStackPane<T extends CalendarDate>
    extends StackPane {

    //~ Instanzvariablen --------------------------------------------------

    private TableView<T> animatedPane;
    private TableView<T> currentPane;
    private ParallelTransition slideTransition;

    //~ Konstruktoren -----------------------------------------------------

    SlidingStackPane(
        TableView<T> animatedPane,
        TableView<T> currentPane
    ) {
        super();

        this.animatedPane = animatedPane;
        this.currentPane = currentPane;

        animatedPane.setVisible(false);
        animatedPane.setFocusTraversable(false);

        getChildren().add(animatedPane);
        getChildren().add(currentPane);

        CalendarControl<T> control = currentPane.getControl();

        control.pageDateProperty().addListener(
            (observableValue, oldDate, newDate) -> {
                if (
                    (getWidth() > 0)
                    && (control.ongoingTransitionsProperty().get() == 0)
                    && (control.viewIndexProperty().get() == currentPane.getViewIndex())
                ) {
                    int direction =
                        currentPane.getCalendarSystem().getDirection(currentPane.getViewIndex(), oldDate, newDate);
                    if (direction != 0) {
                        slide(direction, oldDate, control.lengthOfAnimationsProperty().get());
                    }
                }
            }
        );

        layoutBoundsProperty().addListener(
            (observable, oldValue, newValue) -> {
                setClip(new Rectangle(newValue.getWidth(), newValue.getHeight()));
            }
        );
    }

    //~ Methoden ----------------------------------------------------------

    void updateVisibility(boolean visible) {

        this.setVisible(visible);
        this.currentPane.setVisible(visible); // impacts focusability

    }

    StringProperty getCurrentTitle() {

        return this.currentPane.titleProperty();

    }

    StringProperty getCurrentInfo() {

        return this.currentPane.infoProperty();

    }

    private void slide(
        int direction,
        T oldDate,
        Duration slidingTime
    ) {

        if (slideTransition != null) {
            slideTransition.stop();
        }

        if (slidingTime.lessThanOrEqualTo(Duration.ZERO)) {
            return;
        }

        TranslateTransition transition1 = new TranslateTransition(slidingTime, animatedPane);
        TranslateTransition transition2 = new TranslateTransition(slidingTime, currentPane);
        transition1.setInterpolator(Interpolator.EASE_OUT);
        transition2.setInterpolator(Interpolator.EASE_OUT);

        animatedPane.setVisible(true);
        animatedPane.updateContent(oldDate);
        animatedPane.setCache(true);
        currentPane.setCache(true);

        double width = this.getLayoutBounds().getWidth();
        transition1.setFromX(0);
        transition1.setToX(width * direction);
        transition2.setFromX(-1 * width * direction);
        transition2.setToX(0);

        slideTransition = new ParallelTransition();
        slideTransition.getChildren().addAll(transition1, transition2);
        slideTransition.playFromStart();

        slideTransition.setOnFinished(
            actionEvent -> {
                animatedPane.setVisible(false);
                animatedPane.setCache(false);
                currentPane.setCache(false);
            }
        );

    }

}
