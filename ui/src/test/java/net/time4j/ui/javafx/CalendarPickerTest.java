package net.time4j.ui.javafx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.time4j.PlainDate;
import net.time4j.calendar.HijriCalendar;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.chrono.HijrahChronology;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class CalendarPickerTest
    extends Application {

    @Test
    public void open() {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("CalendarPicker-Test");

        CalendarPicker<PlainDate> picker = CalendarPicker.gregorianWithSystemDefaults();
        picker.setCellCustomizer(
            (cell, column, row, model, date) -> {
                if (CellCustomizer.isWeekend(column, model)) {
                    cell.setStyle("-fx-background-color: #FFE0E0;");
                    cell.setDisable(true);
                }
            }
        );

        picker.setPromptText("TT.MM.JJJJ");
        assertThat(picker.promptTextProperty().get(), is("TT.MM.JJJJ"));

        ChronoFormatter<PlainDate> dateFormat =
            ChronoFormatter.ofDatePattern("dd.MM.yyyy", PatternType.CLDR, Locale.GERMAN);
        picker.setDateFormat(dateFormat);
        assertThat(picker.dateFormatProperty().get(), is(dateFormat));

        picker.setMinDate(PlainDate.of(-12, 1));
        picker.setMaxDate(PlainDate.of(9999, 365));
        assertThat(picker.minDateProperty().get(), is(PlainDate.of(-12, 1)));
        assertThat(picker.maxDateProperty().get(), is(PlainDate.of(9999, 365)));

        picker.setValue(PlainDate.of(9999, 1, 20));
        picker.setLocale(new Locale("de"));
        Button button = new Button();
        button.setText("Show selected value");
        button.setOnAction(
            event -> {
                String error = picker.errorProperty().getValue();
                System.out.println(
                    error == null
                        ? picker.valueProperty().get()
                        : error);
                picker.setShowInfoLabel(!picker.showInfoLabelProperty().get());
            }
        );

        DatePicker datePicker = new DatePicker();
        datePicker.chronologyProperty().setValue(HijrahChronology.INSTANCE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.add(picker, 0, 0);
        gridPane.add(button, 0, 1);
        gridPane.add(datePicker, 0, 2);

        CalendarPicker<?> alternativeCalendarPicker;
        alternativeCalendarPicker = CalendarPicker.persianWithSystemDefaults();
        alternativeCalendarPicker.setLengthOfAnimations(Duration.seconds(0.7));
        assertThat(
            alternativeCalendarPicker.lengthOfAnimationsProperty().get(),
            is(Duration.seconds(0.7)));

        alternativeCalendarPicker = CalendarPicker.minguoWithSystemDefaults();
        alternativeCalendarPicker = CalendarPicker.thaiWithSystemDefaults();
        alternativeCalendarPicker = CalendarPicker.hijriWithSystemDefaults(() -> HijriCalendar.VARIANT_UMALQURA);
        alternativeCalendarPicker = CalendarPicker.persianWithSystemDefaults();
        alternativeCalendarPicker.setShowInfoLabel(true);
        alternativeCalendarPicker.setCellCustomizer(
            (cell, column, row, model, date) -> {
                if (CellCustomizer.isWeekend(column, model)) {
                    cell.setStyle("-fx-background-color: #FFE0E0;");
                    cell.setDisable(true);
                }
            }
        );
        alternativeCalendarPicker.setLocale(new Locale("fa", "IR"));
        alternativeCalendarPicker.setShowWeeks(true);
        gridPane.add(alternativeCalendarPicker, 0, 3);

        gridPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gridPane, 300, 200);
        stage.setScene(scene);
        stage.show();

    }

}