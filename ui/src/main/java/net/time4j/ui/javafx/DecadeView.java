/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DecadeView.java) is part of project Time4J.
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
import net.time4j.format.CalendarText;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.range.CalendarYear;
import net.time4j.range.DateInterval;

import java.time.format.FormatStyle;
import java.util.Locale;


class DecadeView<T extends CalendarDate>
    extends TableView<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String CSS_CALENDAR_DECADE_VIEW = "calendar-decade-view";
    private static final String CSS_CALENDAR_CELL_INSIDE_RANGE = "calendar-cell-inside-range";
    private static final String CSS_CALENDAR_CELL_OUT_OF_RANGE = "calendar-cell-out-of-range";

    //~ Konstruktoren -----------------------------------------------------

    protected DecadeView(
        CalendarControl<T> control,
        FXCalendarSystem<T> calsys,
        boolean animationMode
    ) {
        super(control, calsys, animationMode);

        getStyleClass().add(CSS_CALENDAR_DECADE_VIEW);

        if (!this.isAnimationMode()) {
            // listen to arrow keys
            this.setEventHandler(
                KeyEvent.KEY_PRESSED,
                event -> {
                    KeyCode code = event.getCode();
                    if (code.isArrowKey()) {
                        int index = -1;
                        for (int i = 0; i < 12; i++) {
                            Node cell = getChildren().get(i);
                            if (cell.isFocused()) {
                                index = i;
                                break;
                            }
                        }
                        if (index != -1) {
                            int rowIndex = index / 4;
                            int colIndex = index % 4;
                            switch (code) {
                                case UP:
                                    rowIndex = Math.max(0, rowIndex - 1);
                                    break;
                                case RIGHT:
                                    colIndex = Math.min(3, colIndex + 1);
                                    break;
                                case DOWN:
                                    rowIndex = Math.min(2, rowIndex + 1);
                                    break;
                                case LEFT:
                                    colIndex = Math.max(0, colIndex - 1);
                                    break;
                                default:
                                    return;
                            }
                            int newIndex = rowIndex * 4 + colIndex;
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

        for (int i = 0; i < 12; i++) {
            Button button = new Button();

            if (i == 0 || i == 11) {
                button.getStyleClass().add(CSS_CALENDAR_CELL_OUT_OF_RANGE);
            } else {
                button.getStyleClass().add(CSS_CALENDAR_CELL_INSIDE_RANGE);
            }

            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(button, Priority.ALWAYS);
            GridPane.setHgrow(button, Priority.ALWAYS);

            button.setOnAction(
                actionEvent -> {
                    if (getControl().viewIndexProperty().get() == NavigationBar.BIRD_VIEW) {
                        T clickedDate = getControl().chronology().getChronoType().cast(button.getUserData());
                        getControl().pageDateProperty().setValue(clickedDate);
                        getControl().viewIndexProperty().set(NavigationBar.YEAR_VIEW);
                    }
                }
            );

            int colIndex = i % 4;
            int rowIndex = i / 4;
            add(button, colIndex, rowIndex);
        }

    }

    @Override
    protected void updateContent(T date) {

        Locale locale = this.getControl().localeProperty().get();

        if (locale == null) {
            locale = Locale.ROOT;
        }

        FXCalendarSystem<T> cs = this.getCalendarSystem();

        date = cs.withFirstDayOfYear(date); // should be successful
        int yearsAfterStartOfDecade = Math.floorMod(cs.getProlepticYear(date), 10);
        T min = this.getControl().minDateProperty().get();
        T max = this.getControl().maxDateProperty().get();

        String pattern = CalendarYear.chronology().getFormatPattern(FormatStyle.FULL, locale);

        if (!(date instanceof PlainDate)) {
            if (this.getControl().chronology().getFormatPattern(FormatStyle.MEDIUM, locale).endsWith("G")) {
                pattern = pattern + " G";
            } else {
                pattern = "G " + pattern;
            }
        }

        ChronoFormatter<T> yearFormat =
            ChronoFormatter.ofPattern(
                pattern,
                PatternType.CLDR,
                locale,
                this.getControl().chronology()
            );

        T startYear = null;
        T endYear = null;

        for (int i = 0; i < 12; i++) {
            Button button = (Button) getChildren().get(i);
            try {
                T btnDate = cs.addYears(date, i - 1 - yearsAfterStartOfDecade);
                if (
                    cs.withLastDayOfYear(btnDate).isBefore(min)
                    || cs.withFirstDayOfYear(btnDate).isAfter(max)
                ) {
                    button.setDisable(true);
                    button.setText(" ");
                    button.setUserData(null);
                } else {
                    if ((startYear == null) && (i >= 1)) {
                        startYear = btnDate;
                    }
                    if (i <= 10) {
                        endYear = btnDate;
                    }
                    button.setDisable(false);
                    button.setText(yearFormat.format(btnDate));
                    button.setUserData(btnDate);
                }
            } catch (ArithmeticException | IllegalArgumentException ex) {
                button.setDisable(true);
                button.setText(" ");
                button.setUserData(null);
            }
        }

        if (!this.isAnimationMode()) {
            this.titleProperty().setValue(this.getNavigationTitle(startYear, endYear, yearFormat, locale));
        }

    }

    @Override
    protected int getViewIndex() {

        return NavigationBar.BIRD_VIEW;

    }

    private String getNavigationTitle(
        T startYear,
        T endYear,
        ChronoFormatter<T> printer,
        Locale locale
    ) {

        T start;
        T end;

        try {
            start = this.getCalendarSystem().withFirstDayOfYear(startYear);
        } catch (IllegalArgumentException ex) {
            start = this.getCalendarSystem().getChronologicalMinimum();
        }

        try {
            end = this.getCalendarSystem().withLastDayOfYear(endYear);
        } catch (IllegalArgumentException ex) {
            end = this.getCalendarSystem().getChronologicalMaximum();
        }

        String intervalPattern = CalendarText.patternForInterval(locale);
        StringBuilder sb = new StringBuilder(16);
        int i = 0;
        int n = intervalPattern.length();

        while (i < n) {
            char c = intervalPattern.charAt(i);
            if ((c == '{') && (i + 2 < n) && (intervalPattern.charAt(i + 2) == '}')) {
                char next = intervalPattern.charAt(i + 1);
                if (next == '0') {
                    printer.print(start, sb);
                    i += 3;
                    continue;
                } else if (next == '1') {
                    printer.print(end, sb);
                    i += 3;
                    continue;
                }
            }
            sb.append(c);
            i++;
        }

        DateInterval range =
            DateInterval.between(
                PlainDate.of(start.getDaysSinceEpochUTC(), EpochDays.UTC),
                PlainDate.of(end.getDaysSinceEpochUTC(), EpochDays.UTC)
            );

        String prefix;

        if (start instanceof CalendarVariant) {
            prefix = CalendarVariant.class.cast(start).getVariant();
        } else {
            prefix = getCalendarSystem().getCalendarType();
        }

        this.infoProperty().setValue(prefix + ": " + range.toString());

        return sb.toString();

    }

}
