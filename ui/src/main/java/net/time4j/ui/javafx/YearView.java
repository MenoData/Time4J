/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (YearView.java) is part of project Time4J.
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

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import net.time4j.PlainDate;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.range.CalendarYear;
import net.time4j.range.DateInterval;

import java.time.format.FormatStyle;
import java.util.Locale;


class YearView<T extends CalendarDate>
    extends TableView<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String CSS_CALENDAR_YEAR_VIEW = "calendar-year-view";
    private static final String CSS_CALENDAR_CELL_INSIDE_RANGE = "calendar-cell-inside-range";
    private static final int NUM_OF_COLUMNS = 3;

    //~ Konstruktoren -----------------------------------------------------

    protected YearView(
        CalendarControl<T> control,
        FXCalendarSystem<T> calsys,
        boolean animationMode
    ) {
        super(control, calsys, animationMode);

        getStyleClass().add(CSS_CALENDAR_YEAR_VIEW);

        if (!this.isAnimationMode()) {
            // listen to arrow keys
            this.setEventHandler(
                KeyEvent.KEY_PRESSED,
                event -> {
                    KeyCode code = event.getCode();
                    if (code.isArrowKey()) {
                        int index = -1;
                        for (int i = 0, n = getChildren().size(); i < n; i++) {
                            Node cell = getChildren().get(i);
                            if (cell.isFocused()) {
                                index = i;
                                break;
                            }
                        }
                        if (index != -1) {
                            int rowIndex = index / NUM_OF_COLUMNS;
                            int colIndex = index % NUM_OF_COLUMNS;
                            switch (code) {
                                case UP:
                                    rowIndex = Math.max(0, rowIndex - 1);
                                    break;
                                case RIGHT:
                                    colIndex = Math.min(NUM_OF_COLUMNS - 1, colIndex + 1);
                                    break;
                                case DOWN:
                                    rowIndex = Math.min((getChildren().size() - 1) / NUM_OF_COLUMNS, rowIndex + 1);
                                    break;
                                case LEFT:
                                    colIndex = Math.max(0, colIndex - 1);
                                    break;
                                default:
                                    return;
                            }
                            int newIndex = Math.min(getChildren().size() - 1, rowIndex * NUM_OF_COLUMNS + colIndex);
                            Node cell = getChildren().get(newIndex);
                            if (!cell.isDisabled()) {
                                cell.requestFocus();
                            }
                        }
                        event.consume();
                    }
                }
            );
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    protected void buildContent() {

        for (int i = 0, n = this.getCalendarSystem().getMaxCountOfMonths(); i < n; i++) {
            Button button = new Button();
            button.getStyleClass().add(CSS_CALENDAR_CELL_INSIDE_RANGE);

            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(button, Priority.ALWAYS);
            GridPane.setHgrow(button, Priority.ALWAYS);

            button.setOnAction(
                actionEvent -> {
                    if (getControl().viewIndexProperty().get() == NavigationBar.YEAR_VIEW) {
                        T clickedDate = getControl().chronology().getChronoType().cast(button.getUserData());
                        getControl().pageDateProperty().setValue(clickedDate);
                        getControl().viewIndexProperty().set(NavigationBar.MONTH_VIEW);
                    }
                }
            );

            int colIndex = i % NUM_OF_COLUMNS;
            int rowIndex = i / NUM_OF_COLUMNS;
            add(button, colIndex, rowIndex);
        }

    }

    @Override
    protected void updateContent(T date) {

        Locale locale = this.getControl().localeProperty().get();

        if (locale == null) {
            locale = Locale.ROOT;
        }

        T min = null;
        T max = null;

        for (int i = 0, n = this.getCalendarSystem().getMaxCountOfMonths(); i < n; i++) {
            Button button = (Button) getChildren().get(i);
            try {
                T btnDate = this.getCalendarSystem().withMonth(date, i + 1);

                if (min == null) {
                    min = btnDate;
                } else {
                    max = btnDate;
                }

                if (
                    getCalendarSystem().withLastDayOfMonth(btnDate).isBefore(getControl().minDateProperty().get())
                    || getCalendarSystem().withFirstDayOfMonth(btnDate).isAfter(getControl().maxDateProperty().get())
                ) {
                    button.setDisable(true);
                    button.setText(" ");
                    button.setUserData(null);
                } else {
                    button.setDisable(false);
                    int month = this.getCalendarSystem().getMonth(btnDate);
                    button.setText(this.getCalendarSystem().formatMonth(month, locale, btnDate));
                    button.setUserData(btnDate);
                }
            } catch (ArithmeticException | IllegalArgumentException ex) {
                button.setDisable(true);
                button.setText(" ");
                button.setUserData(null);
            }
        }

        if (!this.isAnimationMode()) {
            String pattern = CalendarYear.chronology().getFormatPattern(FormatStyle.FULL, locale);

            if (!(date instanceof PlainDate)) {
                if (this.getControl().chronology().getFormatPattern(FormatStyle.MEDIUM, locale).endsWith("G")) {
                    pattern = pattern + " G";
                } else {
                    pattern = "G " + pattern;
                }
            }

            this.titleProperty().setValue(
                ChronoFormatter.ofPattern(pattern, PatternType.CLDR, locale, getControl().chronology()).format(date)
            );

            min = this.getCalendarSystem().withFirstDayOfMonth(min);
            max = this.getCalendarSystem().withLastDayOfMonth(max);

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

    }

    @Override
    protected int getViewIndex() {

        return NavigationBar.YEAR_VIEW;

    }

}
