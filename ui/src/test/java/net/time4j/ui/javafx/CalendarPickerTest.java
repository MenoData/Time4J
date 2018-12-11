package net.time4j.ui.javafx;

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
import net.time4j.calendar.EthiopianCalendar;
import net.time4j.calendar.HebrewCalendar;
import net.time4j.calendar.HijriCalendar;
import net.time4j.calendar.JulianCalendar;
import net.time4j.calendar.MinguoCalendar;
import net.time4j.calendar.ThaiSolarCalendar;
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
    public void start(Stage stage) {
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

        int row = 2;

        Label labelJDK = new Label("JavaFX (Hijrah)");
        labelJDK.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelJDK, 0, row);
        gridPane.add(datePicker, 1, row);

        row++;
        Label labelEthiopian = new Label("Ethiopian");
        labelEthiopian.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelEthiopian, 0, row);
        CalendarPicker<EthiopianCalendar> ethiopianPicker = CalendarPicker.ethiopianWithSystemDefaults();
        ethiopianPicker.setLocale(Locale.ENGLISH);
        gridPane.add(ethiopianPicker, 1, row);

        row++;
        Label labelHebrew = new Label("Hebrew");
        labelHebrew.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelHebrew, 0, row);
        CalendarPicker<HebrewCalendar> hebrewPicker = CalendarPicker.hebrewWithSystemDefaults();
        hebrewPicker.setLocale(Locale.ENGLISH);
        gridPane.add(hebrewPicker, 1, row);

        row++;
        Label labelHijri = new Label("Islamic-Umalqura");
        labelHijri.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelHijri, 0, row);
        CalendarPicker<HijriCalendar> hijriPicker =
            CalendarPicker.hijriWithSystemDefaults(() -> HijriCalendar.VARIANT_UMALQURA);
        hijriPicker.setLocale(Locale.ENGLISH);
        gridPane.add(hijriPicker, 1, row);

        row++;
        Label labelJulian = new Label("Julian");
        labelJulian.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelJulian, 0, row);
        CalendarPicker<JulianCalendar> julianPicker = CalendarPicker.julianWithSystemDefaults();
        julianPicker.setLocale(Locale.ENGLISH);
        gridPane.add(julianPicker, 1, row);

        row++;
        Label labelMinguo = new Label("Minguo");
        labelMinguo.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelMinguo, 0, row);
        CalendarPicker<MinguoCalendar> minguoPicker = CalendarPicker.minguoWithSystemDefaults();
        minguoPicker.setLocale(Locale.ENGLISH);
        gridPane.add(minguoPicker, 1, row);

        row++;
        CalendarPicker<?> persianCalendarPicker = CalendarPicker.persianWithSystemDefaults();
        persianCalendarPicker.setLengthOfAnimations(Duration.seconds(0.7));
        assertThat(
            persianCalendarPicker.lengthOfAnimationsProperty().get(),
            is(Duration.seconds(0.7)));
        persianCalendarPicker = CalendarPicker.persianWithSystemDefaults();
        persianCalendarPicker.setShowInfoLabel(true);
        persianCalendarPicker.setCellCustomizer(
            (cell, columnIndex, rowIndex, model, date) -> {
                if (CellCustomizer.isWeekend(columnIndex, model)) {
                    cell.setStyle("-fx-background-color: #FFE0E0;");
                    cell.setDisable(true);
                }
            }
        );
        persianCalendarPicker.setLocale(new Locale("fa", "IR"));
        persianCalendarPicker.setShowWeeks(true);
        Label labelPersian = new Label("Persian");
        labelPersian.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelPersian, 0, row);
        gridPane.add(persianCalendarPicker, 1, row);

        row++;
        Label labelThaisolar = new Label("Thai (Buddhist)");
        labelThaisolar.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(labelThaisolar, 0, row);
        CalendarPicker<ThaiSolarCalendar> thaisolarPicker = CalendarPicker.thaiWithSystemDefaults();
        thaisolarPicker.setLocale(Locale.ENGLISH);
        gridPane.add(thaisolarPicker, 1, row);

        gridPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gridPane, 305, 320);
        stage.setScene(scene);
        stage.show();

    }

}