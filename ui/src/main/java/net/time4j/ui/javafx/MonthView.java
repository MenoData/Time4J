/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MonthView.java) is part of project Time4J.
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

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.Chronology;
import net.time4j.engine.EpochDays;
import net.time4j.format.DisplayMode;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.format.internal.FormatUtils;
import net.time4j.range.CalendarMonth;
import net.time4j.range.DateInterval;

import java.util.Locale;
import java.util.Optional;


class MonthView<T extends CalendarDate>
    extends TableView<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String CSS_CALENDAR_MONTH_VIEW = "calendar-month-view";
    private static final String CSS_CALENDAR_CELL_INSIDE_RANGE = "calendar-cell-inside-range";
    private static final String CSS_CALENDAR_CELL_OUT_OF_RANGE = "calendar-cell-out-of-range";
    private static final String CSS_CALENDAR_TODAY = "calendar-cell-today";
    private static final String CSS_CALENDAR_SELECTED = "calendar-cell-selected";
    private static final String CSS_CALENDAR_WEEKDAY_HEADER = "calendar-weekday-header";
    private static final String CSS_CALENDAR_WEEK_OF_YEAR = "calendar-week-of-year";

    //~ Konstruktoren -----------------------------------------------------

    protected MonthView(
        CalendarControl<T> control,
        FXCalendarSystem<T> calsys,
        boolean animationMode
    ) {
        super(control, calsys, animationMode);

        getStyleClass().add(CSS_CALENDAR_MONTH_VIEW);

        // listen to layout-change
        control.showWeeksProperty().addListener(observable -> rebuild());

        if (!this.isAnimationMode()) {
            // repaint today-cell if necessary
            control.selectedDateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if ((newValue == null) || (newValue.equals(control.today()))) {
                        updateContent(control.pageDateProperty().getValue());
                    }
                }
            );
            // listen to arrow keys
            this.setEventHandler(
                KeyEvent.KEY_PRESSED,
                event -> {
                    KeyCode code = event.getCode();
                    if (code.isArrowKey()) {
                        int shift;
                        switch (code) {
                            case UP:
                                shift = -7;
                                break;
                            case RIGHT:
                                shift = 1;
                                break;
                            case DOWN:
                                shift = 7;
                                break;
                            case LEFT:
                                shift = -1;
                                break;
                            default:
                                return;
                        }
                        event.consume();
                        setFocusedDate(shift);
                    }
                }
            );
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    protected void buildContent() {

        boolean showWeeks = this.getControl().showWeeksProperty().get();
        int extra = (showWeeks ? 1 : 0);

        // table header with seven weekday names
        for (int colIndex = 0; colIndex < 7 + extra; colIndex++) {
            Label label = new Label();
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            if (showWeeks && colIndex == 0) {
                label.getStyleClass().add(CSS_CALENDAR_WEEK_OF_YEAR);
                add(label, 0, 0);
            } else {
                label.getStyleClass().add(CSS_CALENDAR_WEEKDAY_HEADER);
                add(label, colIndex, 0);
            }
        }

        // we use fixed count of rows (always 6) in order to avoid sudden changes in height when browsing
        for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
            if (showWeeks) {
                Label label = new Label();
                label.setMaxWidth(USE_PREF_SIZE);
                label.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(label, Priority.ALWAYS);
                GridPane.setHgrow(label, Priority.NEVER);
                GridPane.setHalignment(label, HPos.RIGHT);
                label.getStyleClass().add(CSS_CALENDAR_WEEK_OF_YEAR);
                add(label, 0, rowIndex + 1);
            }
            for (int colIndex = 0; colIndex < 7; colIndex++) {
                Button button = new Button();
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);

                GridPane.setVgrow(button, Priority.ALWAYS);
                GridPane.setHgrow(button, Priority.ALWAYS);

                button.setOnAction(
                    actionEvent -> {
                        T selected = getControl().chronology().getChronoType().cast(button.getUserData());
                        T oldValue = getControl().selectedDateProperty().get();
                        if ((oldValue == null) || !oldValue.equals(selected)) {
                            getControl().selectedDateProperty().setValue(selected);
                        }
                    }
                );

                add(button, colIndex + extra, rowIndex + 1);
            }
        }

    }

    @Override
    protected void updateContent(T date) {

        Locale locale = this.getControl().localeProperty().get();

        if (locale == null) {
            locale = Locale.ROOT;
        }

        this.updateCells(locale, date);
        this.updateColumnHeaders(locale);

        if (!this.isAnimationMode()) {
            this.updateNavigationTitle(locale, date);
        }

    }

    @Override
    protected int getViewIndex() {

        return NavigationBar.MONTH_VIEW;

    }

    private void setFocusedDate(int shift) {

        T date = null;
        int extra = (this.getControl().showWeeksProperty().get() ? 1 : 0);

        // a) search for focused button
        for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
            int index = (rowIndex + 1) * (7 + extra);

            for (int colIndex = 0; colIndex < 7; colIndex++) {
                Button button = (Button) this.getChildren().get(index + colIndex + extra);
                if (button.isFocused()) {
                    date = getControl().chronology().getChronoType().cast(button.getUserData());
                    break;
                }
            }

            if (date != null) {
                break;
            }
        }

        if (date == null) {
            return;
        }

        // b) request focus on button with relevant date
        date = getCalendarSystem().navigateByDays(date, shift);

        for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
            int index = (rowIndex + 1) * (7 + extra);

            for (int colIndex = 0; colIndex < 7; colIndex++) {
                Button button = (Button) this.getChildren().get(index + colIndex + extra);
                T test = getControl().chronology().getChronoType().cast(button.getUserData());

                if (date.equals(test)) {
                    if (!button.isDisabled()) {
                        button.requestFocus();
                    }
                    return;
                }
            }
        }

    }

    private void updateCells(
        Locale locale,
        T pageDate
    ) {

        boolean showWeeks = this.getControl().showWeeksProperty().get();
        int extra = (showWeeks ? 1 : 0);
        T selected = this.getControl().selectedDateProperty().getValue();
        T today = this.getControl().today();
        Weekmodel model = this.getWeekmodel(locale);

        int btnCount = 0;
        int firstValidDay;
        T current;

        try {
            current = this.getCalendarSystem().withFirstDayOfMonth(pageDate);
            int localDayOfWeek = this.getDayOfWeek(current).getValue(model);
            if (localDayOfWeek == 1) {
                current = this.getCalendarSystem().navigateByDays(current, -7);
            } else {
                current = this.getCalendarSystem().navigateByDays(current, 1 - localDayOfWeek);
            }
            firstValidDay = 1;
        } catch (ArithmeticException | IllegalArgumentException ex) {
            current = null;
            Weekday wd = this.getDayOfWeek(this.getCalendarSystem().getChronologicalMinimum());
            firstValidDay = wd.getValue(model);
        }

        Chronology<T> chronology = this.getControl().chronology();
        ChronoFormatter<T> woyFormat = ChronoFormatter.ofPattern("w", PatternType.CLDR, locale, chronology);
        ChronoFormatter<T> cellFormat = ChronoFormatter.ofPattern("d", PatternType.CLDR, locale, chronology);
        ChronoFormatter<T> tooltipFormat = this.getCalendarSystem().createTooltipFormat(locale);
        CellCustomizer<T> cellCustomizer = this.getControl().cellCustomizerProperty().get();

        for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
            int index = (rowIndex + 1) * (7 + extra);
            T anyCellDate = null;
            for (int colIndex = 0; colIndex < 7; colIndex++) {
                Button button = (Button) this.getChildren().get(index + colIndex + extra);
                button.getStyleClass().remove(CSS_CALENDAR_CELL_INSIDE_RANGE);
                button.getStyleClass().remove(CSS_CALENDAR_CELL_OUT_OF_RANGE);
                button.getStyleClass().remove(CSS_CALENDAR_TODAY);
                button.getStyleClass().remove(CSS_CALENDAR_SELECTED);

                btnCount++;

                if ((current == null) && (btnCount == firstValidDay)) {
                    current = this.getCalendarSystem().getChronologicalMinimum();
                }

                boolean disabled = (
                    (current == null)
                    || current.isBefore(this.getControl().minDateProperty().get())
                    || current.isAfter(this.getControl().maxDateProperty().get())
                );

                button.setDisable(disabled);

                if (disabled) {
                    button.setOpacity(0); // make button transparent
                    button.setText(" ");
                    button.setTooltip(null);
                } else {
                    // resets opacity, too (see calendar.css)
                    if (this.getCalendarSystem().getMonth(current) == this.getCalendarSystem().getMonth(pageDate)) {
                        button.getStyleClass().add(CSS_CALENDAR_CELL_INSIDE_RANGE);
                    } else {
                        button.getStyleClass().add(CSS_CALENDAR_CELL_OUT_OF_RANGE);
                    }

                    if (current.equals(today)) {
                        button.getStyleClass().add(CSS_CALENDAR_TODAY);
                    }

                    if (current.equals(selected)) {
                        button.getStyleClass().add(CSS_CALENDAR_SELECTED);
                    }

                    anyCellDate = current;
                    button.setText(cellFormat.format(current));

                    String tooltip = tooltipFormat.format(current);
                    if (!(current instanceof PlainDate)) {
                        tooltip = // using LRM-marker shows ISO-date in right order even in RTL-languages
                            tooltip + " (\u200E" + PlainDate.of(current.getDaysSinceEpochUTC(), EpochDays.UTC) + ")";
                    }
                    button.setTooltip(new Tooltip(tooltip));
                }

                if (cellCustomizer != null) {
                    Optional<T> dateRef = (disabled ? Optional.<T>empty() : Optional.of(current));
                    cellCustomizer.customize(button, colIndex, rowIndex, model, dateRef);

                    if (disabled && !button.isDisabled()) {
                        button.setDisable(true); // prevents customization of disabled-status if appropriate
                    }
                }

                button.setUserData(disabled ? null : current);

                try {
                    if (current != null) {
                        current = this.getCalendarSystem().navigateByDays(current, 1);
                    }
                } catch (ArithmeticException ex) {
                    current = null;
                }
            }

            if (showWeeks) {
                Label label = (Label) this.getChildren().get(index);
                if (anyCellDate == null) {
                    label.setText("  ");
                } else {
                    String woy = woyFormat.format(anyCellDate);
                    if (woy.length() == 1) {
                        woy = " " + woy;
                    }
                    label.setText(woy);
                }
            }
        }

    }

    private void updateColumnHeaders(Locale locale) {

        Weekday wd = this.getWeekmodel(locale).getFirstDayOfWeek();
        boolean showWeeks = this.getControl().showWeeksProperty().get();
        int extra = (showWeeks ? 1 : 0);

        for (int colIndex = 0; colIndex < 7; colIndex++) {
            Label label = (Label) this.getChildren().get(colIndex + extra);
            label.setText(wd.getDisplayName(locale, TextWidth.SHORT, OutputContext.STANDALONE));
            wd = wd.next();
        }

    }

    private void updateNavigationTitle(
        Locale locale,
        T date
    ) {

        String pattern = CalendarMonth.chronology().getFormatPattern(DisplayMode.FULL, locale);

        if (!(date instanceof PlainDate)) {
            if (this.getControl().chronology().getFormatPattern(DisplayMode.MEDIUM, locale).endsWith("G")) {
                pattern = pattern + " G";
            } else {
                pattern = "G " + pattern;
            }
        }

        this.titleProperty().setValue(
            ChronoFormatter.ofPattern(pattern, PatternType.CLDR, locale, getControl().chronology()).format(date)
        );

        T min = this.getCalendarSystem().withFirstDayOfMonth(date);
        T max = this.getCalendarSystem().withLastDayOfMonth(min);

        DateInterval range =
            DateInterval.between(
                PlainDate.of(min.getDaysSinceEpochUTC(), EpochDays.UTC),
                PlainDate.of(max.getDaysSinceEpochUTC(), EpochDays.UTC)
            );

        String prefix;

        if (min instanceof CalendarVariant) {
            prefix = CalendarVariant.class.cast(min).getVariant();
        } else {
            prefix = getCalendarSystem().getCalendarType();
        }

        this.infoProperty().setValue(prefix + ": " + range.toString());

    }

    private Weekmodel getWeekmodel(Locale locale) {

        if (FormatUtils.useDefaultWeekmodel(locale)) {
            return this.getCalendarSystem().getDefaultWeekmodel();
        } else {
            return Weekmodel.of(locale);
        }

    }

    private Weekday getDayOfWeek(T date) {

        return Weekday.valueOf((int) (Math.floorMod(date.getDaysSinceEpochUTC() + 5, 7) + 1));

    }

}
