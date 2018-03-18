package net.time4j.ui.javafx;

import com.sun.prism.paint.Color;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.time4j.PlainDate;
import net.time4j.calendar.HebrewCalendar;
import net.time4j.calendar.HebrewMonth;
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
        gridPane.setBorder(
            new Border(
                new BorderStroke(
                    Paint.valueOf("transparent"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));

        Label labelISO = new Label("Gregorian");
        labelISO.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelISO, 0, 0);
        gridPane.add(picker, 1, 0);
        gridPane.add(button, 1, 1);

        Label labelJDK = new Label("JavaFX (Hijrah)");
        labelJDK.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelJDK, 0, 2);
        gridPane.add(datePicker, 1, 2);

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
        Label labelPersian = new Label("Persian");
        labelPersian.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelPersian, 0, 3);
        gridPane.add(alternativeCalendarPicker, 1, 3);
        gridPane.setAlignment(Pos.CENTER);

        Label labelHebrew = new Label("Hebrew");
        labelHebrew.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelHebrew, 0, 4);
        CalendarPicker<HebrewCalendar> hebrewPicker = CalendarPicker.hebrewWithSystemDefaults();
        hebrewPicker.setLocale(Locale.GERMAN);
        gridPane.add(hebrewPicker, 1, 4);
        gridPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gridPane, 300, 200);
        stage.setScene(scene);
        stage.show();

    }

}