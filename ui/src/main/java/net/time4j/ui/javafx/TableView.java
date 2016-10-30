/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TableView.java) is part of project Time4J.
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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.GridPane;
import net.time4j.engine.CalendarDate;


abstract class TableView<T extends CalendarDate>
    extends GridPane {

    //~ Instanzvariablen --------------------------------------------------

    private CalendarControl<T> control;
    private FXCalendarSystem<T> calsys;
    private StringProperty title;
    private StringProperty info;

    private final boolean animationMode;

    //~ Konstruktoren -----------------------------------------------------

    protected TableView(
        CalendarControl<T> control,
        FXCalendarSystem<T> calsys,
        boolean animationMode
    ) {
        super();

        this.setVgap(0);
        this.setFocusTraversable(true);

        this.control = control;
        this.calsys = calsys;
        this.title = new SimpleStringProperty();
        this.info = new SimpleStringProperty();
        this.animationMode = animationMode;

        this.rebuild();

        if (!animationMode) {
            control.localeProperty().addListener(
                observable -> {
                    updateContent(control.pageDateProperty().getValue());
                }
            );
            control.pageDateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    updateContent(newValue);
                }
            );
        }
    }

    //~ Methoden ----------------------------------------------------------

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty infoProperty() {
        return info;
    }

    protected abstract void buildContent();

    protected abstract void updateContent(T date);

    protected abstract int getViewIndex();

    protected final void rebuild() {
        getChildren().clear();
        buildContent();
        updateContent(control.pageDateProperty().getValue());
    }

    final CalendarControl<T> getControl() {
        return this.control;
    }

    final FXCalendarSystem<T> getCalendarSystem() {
        return this.calsys;
    }

    final boolean isAnimationMode() {
        return this.animationMode;
    }

}
