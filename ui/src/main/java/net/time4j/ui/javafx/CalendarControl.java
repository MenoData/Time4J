/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarControl.java) is part of project Time4J.
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.Chronology;

import java.util.Locale;
import java.util.function.Supplier;


class CalendarControl<T extends CalendarDate> {

    //~ Instanzvariablen --------------------------------------------------

    private Supplier<T> todaySupplier;
    private Chronology<T> chronology;

    private ObjectProperty<T> pageDateProperty = new SimpleObjectProperty<>(this, "PAGE-DATE");
    private ObjectProperty<T> selectedDateProperty = new SimpleObjectProperty<>(this, "SELECTED-DATE");
    private IntegerProperty ongoingTransitionsProperty = new SimpleIntegerProperty(this, "TRANSITION-COUNT");
    private IntegerProperty viewIndexProperty = new SimpleIntegerProperty(this, "VIEW-INDEX");
    private StringProperty navigationTitleProperty = new SimpleStringProperty(this, "NAVIGATION-TITLE");
    private StringProperty navigationInfoProperty = new SimpleStringProperty(this, "NAVIGATION-INFO");
    private ObjectProperty<T> minDateProperty = new SimpleObjectProperty<>(this, "MIN-DATE");
    private ObjectProperty<T> maxDateProperty = new SimpleObjectProperty<>(this, "MAX-DATE");
    private ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(this, "LOCALE");
    private BooleanProperty showWeeksProperty = new SimpleBooleanProperty(this, "SHOW-WEEKS");
    private BooleanProperty showInfoLabelProperty = new SimpleBooleanProperty(this, "SHOW-INFO-LABEL");
    private ObjectProperty<CellCustomizer<T>> cellCustomizerProperty =
        new SimpleObjectProperty<>(this, "CELL-CUSTOMIZER");
    private ObjectProperty<Duration> lengthOfAnimationsProperty =
        new SimpleObjectProperty<>(this, "LENGTH-OF-ANIMATIONS");

    //~ Konstruktoren -----------------------------------------------------

    CalendarControl(
        Locale locale,
        Supplier<T> todaySupplier,
        Chronology<T> chronology,
        T minDate,
        T maxDate
    ) {
        super();

        if (locale == null || todaySupplier == null || chronology == null || minDate == null || maxDate == null) {
            throw new NullPointerException();
        }

        this.todaySupplier = todaySupplier;
        this.chronology = chronology;

        this.selectedDateProperty.setValue(null);
        this.pageDateProperty.setValue(todaySupplier.get());
        this.localeProperty.setValue(locale);
        this.ongoingTransitionsProperty.set(0);
        this.minDateProperty.setValue(minDate);
        this.maxDateProperty.setValue(maxDate);
        this.cellCustomizerProperty.setValue(null);

    }

    //~ Methoden ----------------------------------------------------------

    T today() {
        return this.todaySupplier.get();
    }

    Chronology<T> chronology() {
        return this.chronology;
    }

    ObjectProperty<Locale> localeProperty() {
        return this.localeProperty;
    }

    ObjectProperty<T> minDateProperty() {
        return this.minDateProperty;
    }

    ObjectProperty<T> maxDateProperty() {
        return this.maxDateProperty;
    }

    ObjectProperty<T> pageDateProperty() {
        return this.pageDateProperty;
    }

    ObjectProperty<T> selectedDateProperty() {
        return this.selectedDateProperty;
    }

    IntegerProperty ongoingTransitionsProperty() {
        return this.ongoingTransitionsProperty;
    }

    IntegerProperty viewIndexProperty() {
        return this.viewIndexProperty;
    }

    StringProperty navigationTitleProperty() {
        return this.navigationTitleProperty;
    }

    StringProperty navigationInfoProperty() {
        return this.navigationInfoProperty;
    }

    BooleanProperty showWeeksProperty() {
        return this.showWeeksProperty;
    }

    BooleanProperty showInfoLabelProperty() {
        return this.showInfoLabelProperty;
    }

    ObjectProperty<Duration> lengthOfAnimationsProperty() {
        return this.lengthOfAnimationsProperty;
    }

    ObjectProperty<CellCustomizer<T>> cellCustomizerProperty() {
        return this.cellCustomizerProperty;
    }

}