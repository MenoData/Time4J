/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarPicker.java) is part of project Time4J.
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

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import net.time4j.PlainDate;
import net.time4j.SystemClock;
import net.time4j.ZonalClock;
import net.time4j.calendar.HebrewCalendar;
import net.time4j.calendar.HijriCalendar;
import net.time4j.calendar.MinguoCalendar;
import net.time4j.calendar.PersianCalendar;
import net.time4j.calendar.ThaiSolarCalendar;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.Calendrical;
import net.time4j.engine.Chronology;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.VariantSource;
import net.time4j.format.Attributes;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ParseLog;
import net.time4j.format.expert.PatternType;

import java.util.Locale;
import java.util.function.Supplier;


/**
 * <p>Represents a combination of a text editor and a button which can open a calendar view for picking
 * any arbitrary calendar date. </p>
 *
 * @param   <T> denotes the calendar system to be used
 * @author  Meno Hochschild
 * @since   4.20
 * @doctags.concurrency {mutable}
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Kombination aus einem Texteditor und einer Schaltfl&auml;che, die
 * einen grafischen Kalender zur Auswahl eines beliebigen Datums &ouml;ffnen kann. </p>
 *
 * @param   <T> denotes the calendar system to be used
 * @author  Meno Hochschild
 * @since   4.20
 * @doctags.concurrency {mutable}
 */
public class CalendarPicker<T extends CalendarDate>
    extends HBox {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Duration STD_ANIMATION_TIME = Duration.seconds(0.5);
    private static final String CSS_CALENDAR_EDITOR_ERROR = "calendar-editor-error";
    private static final String CSS_CALENDAR_DIALOG_START = "calendar-dialog-start";

    //~ Instanzvariablen --------------------------------------------------

    private TextField textField;
    private Button popupButton;
    private Popup popupDialog;

    private CalendarControl<T> control;
    private FXCalendarSystem<T> calsys;

    private boolean committingText = false;
    private boolean textInChange = false;
    private boolean valueInChange = false;
    private boolean selectionInChange = false;

    private ObjectProperty<T> valuePropertyInt = new SimpleObjectProperty<>(this, "VALUE-INTERNAL");
    private ObjectProperty<T> valuePropertyExt = new SimpleObjectProperty<>(this, "VALUE");
    private StringProperty errorProperty = new SimpleStringProperty(this, "ERROR");
    private ObjectProperty<ChronoFormatter<T>> formatProperty = new SimpleObjectProperty<>(this, "DATE-FORMAT");
    private StringProperty promptProperty = new SimpleStringProperty(this, "PROMPT-TEXT");

    //~ Konstruktoren -----------------------------------------------------

    private CalendarPicker(
        FXCalendarSystem<T> calsys,
        Locale locale,
        Supplier<T> todaySupplier,
        Chronology<T> chronology,
        T minDate,
        T maxDate
    ) {
        super();

        this.control = new CalendarControl<>(locale, todaySupplier, chronology, minDate, maxDate);
        this.calsys = calsys;

        // initialization of properties
        this.control.selectedDateProperty().setValue(null);
        this.control.pageDateProperty().setValue(todaySupplier.get());
        this.valuePropertyInt.setValue(null);
        this.errorProperty.setValue(null);
        this.formatProperty.setValue(null);
        this.promptProperty.setValue(null);
        this.setShowWeeks(true);
        this.setLengthOfAnimations(STD_ANIMATION_TIME);

        // our components
        this.getStylesheets().add("/net/time4j/ui/javafx/calendar.css");
        this.textField = new TextField();
        this.popupButton = this.createPopupButton();
        this.popupDialog = null;

        this.getChildren().add(this.textField);
        this.getChildren().add(this.popupButton);

        HBox.setHgrow(this.textField, Priority.ALWAYS);

        // listeners and bindings
        this.control.selectedDateProperty().addListener(
            (observable, oldValue, newValue) -> {
                control.pageDateProperty().setValue((newValue == null) ? control.today() : newValue);
                if (!valueInChange) {
                    selectionInChange = true;
                    valuePropertyInt.setValue(newValue);
                    selectionInChange = false;
                    if (!committingText) {
                        hidePopup();
                    }
                }
            }
        );

        this.valuePropertyInt.addListener(
            (observable, oldValue, newValue) -> {
                valueInChange = true;
                if (!committingText) {
                    errorProperty.setValue(null);
                    if (!selectionInChange) {
                        control.selectedDateProperty().setValue(newValue);
                    }
                    updateTextField();
                }
                valuePropertyExt.setValue(newValue);
                valueInChange = false;
            }
        );

        this.valuePropertyExt.addListener(
            (observable, oldValue, newValue) -> {
                if (
                    (newValue != null)
                    && (newValue.isBefore(control.minDateProperty().get())
                        || newValue.isAfter(control.maxDateProperty().get()))
                ) {
                    throw new IllegalArgumentException("Out of range: " + newValue);
                } else if (!valueInChange) {
                    valuePropertyInt.setValue(newValue);
                }
            }
        );

        this.errorProperty.addListener(
            observable -> {
                if (errorProperty.getValue() == null) {
                    textField.getStyleClass().remove(CSS_CALENDAR_EDITOR_ERROR);
                    textField.setTooltip(null);
                } else {
                    if (!textField.getStyleClass().contains(CSS_CALENDAR_EDITOR_ERROR)) {
                        textField.getStyleClass().add(CSS_CALENDAR_EDITOR_ERROR);
                    }
                    textField.setTooltip(new Tooltip(errorProperty.getValue()));
                }
            }
        );

        this.textField.promptTextProperty().bind(new PromptBinding());
        this.textField.minHeightProperty().bind(this.minHeightProperty());
        this.textField.maxHeightProperty().bind(this.maxHeightProperty());

        this.textField.textProperty().addListener( // CLEAR
            (observable, oldValue, newValue) -> {
                if (!textInChange) {
                    if ((newValue == null) || newValue.isEmpty()) {
                        committingText = true;
                        control.selectedDateProperty().setValue(null);
                        errorProperty.setValue(null);
                        committingText = false;
                    } else {
                        textField.setTooltip(null);
                    }
                }
            }
        );

        this.textField.focusedProperty().addListener( // FOCUS-LOST
            observable -> {
                if (!textField.isFocused()) {
                    commitTextInput();
                }
            }
        );

        this.textField.setOnAction( // ENTER
            event -> {
                hidePopup();
                this.popupButton.requestFocus();
                if (this.textField.isFocused()) {
                    commitTextInput();
                }
            }
        );

        this.control.localeProperty().addListener(
            observable -> {
                updateTextField();
            }
        );

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new {@code CalendarPicker} for the gregorian calendar system using system defaults
     * for the locale and the current local time. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#today()
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r das gregorianische Kalendersystem
     * unter Benutzung von aus dem System abgeleiteten Standardwerten f&uuml;r die Sprach- und
     * L&auml;dereinstellung und die aktuelle Zonenzeit. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#today()
     */
    public static CalendarPicker<PlainDate> gregorianWithSystemDefaults() {

        return CalendarPicker.gregorian(
            Locale.getDefault(Locale.Category.FORMAT),
            () -> SystemClock.inLocalView().today()
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the gregorian calendar system. </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r das gregorianische Kalendersystem. </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    public static CalendarPicker<PlainDate> gregorian(
        Locale locale,
        Supplier<PlainDate> todaySupplier
    ) {

        return CalendarPicker.create(
            PlainDate.axis(),
            new FXCalendarSystemIso8601(),
            locale,
            todaySupplier
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the hebrew calendar system (jewish calendar) using system defaults
     * for the locale and the current local time. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r den hebr&auml;ischen (j&uuml;dischen) Kalender
     * unter Benutzung von aus dem System abgeleiteten Standardwerten f&uuml;r die Sprach- und
     * L&auml;dereinstellung und die aktuelle Zonenzeit. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(Chronology)
     */
    public static CalendarPicker<HebrewCalendar> hebrewWithSystemDefaults() {

        return CalendarPicker.hebrew(
            Locale.getDefault(Locale.Category.FORMAT),
            () -> SystemClock.inLocalView().now(HebrewCalendar.axis())
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the hebrew calendar system (jewish calendar). </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r den hebr&auml;ischen (j&uuml;dischen) Kalender. </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    public static CalendarPicker<HebrewCalendar> hebrew(
        Locale locale,
        Supplier<HebrewCalendar> todaySupplier
    ) {

        return CalendarPicker.create(
            HebrewCalendar.axis(),
            new FXCalendarSystemHebrew(),
            locale,
            todaySupplier
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the islamic calendar using system defaults
     * for the locale and the current local time. </p>
     *
     * <p>Following code selects the Umalqura variant of Saudi-Arabia: </p>
     *
     * <pre>
     *     CalendarPicker&lt;HijriCalendar&gt; picker =
     *          CalendarPicker.hijriWithSystemDefaults(() -> HijriCalendar.VARIANT_UMALQURA);
     * </pre>
     *
     * @param   variantSource   the variant of the underlying islamic calendar
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(CalendarFamily, VariantSource, StartOfDay)
     * @see     HijriCalendar#VARIANT_UMALQURA
     * @see     net.time4j.calendar.HijriAlgorithm
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r den islamischen Kalender
     * unter Benutzung von aus dem System abgeleiteten Standardwerten f&uuml;r die Sprach- und
     * L&auml;dereinstellung und die aktuelle Zonenzeit. </p>
     *
     * <p>Folgender Code w&auml;hlt die Umalqura-Variante von Saudi-Arabien: </p>
     *
     * <pre>
     *     CalendarPicker&lt;HijriCalendar&gt; picker =
     *          CalendarPicker.hijriWithSystemDefaults(() -> HijriCalendar.VARIANT_UMALQURA);
     * </pre>
     *
     * @param   variantSource   the variant of the underlying islamic calendar
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(CalendarFamily, VariantSource, StartOfDay)
     * @see     HijriCalendar#VARIANT_UMALQURA
     * @see     net.time4j.calendar.HijriAlgorithm
     */
    public static CalendarPicker<HijriCalendar> hijriWithSystemDefaults(VariantSource variantSource) {

        return CalendarPicker.hijri(
            variantSource,
            Locale.getDefault(Locale.Category.FORMAT),
            () -> SystemClock.inLocalView().now(HijriCalendar.family(), variantSource, StartOfDay.EVENING).toDate()
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the islamic calendar. </p>
     *
     * @param   variantSource   the variant of the underlying islamic calendar
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r den islamischen Kalender. </p>
     *
     * @param   variantSource   the variant of the underlying islamic calendar
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     * @return  CalendarPicker
     */
    public static CalendarPicker<HijriCalendar> hijri(
        VariantSource variantSource,
        Locale locale,
        Supplier<HijriCalendar> todaySupplier
    ) {

        return CalendarPicker.create(
            HijriCalendar.family(),
            new FXCalendarSystemHijri(variantSource.getVariant()),
            locale,
            todaySupplier
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the calendar system of Taiwan using system defaults
     * for the locale and the current local time. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r den Kalender auf Taiwan
     * unter Benutzung von aus dem System abgeleiteten Standardwerten f&uuml;r die Sprach- und
     * L&auml;dereinstellung und die aktuelle Zonenzeit. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(Chronology)
     */
    public static CalendarPicker<MinguoCalendar> minguoWithSystemDefaults() {

        return CalendarPicker.minguo(
            Locale.getDefault(Locale.Category.FORMAT),
            () -> SystemClock.inLocalView().now(MinguoCalendar.axis())
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the calendar system used in Taiwan. </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r den Kalender auf Taiwan. </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    public static CalendarPicker<MinguoCalendar> minguo(
        Locale locale,
        Supplier<MinguoCalendar> todaySupplier
    ) {

        return CalendarPicker.create(
            MinguoCalendar.axis(),
            new FXCalendarSystemMinguo(),
            locale,
            todaySupplier
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the persian calendar system using system defaults
     * for the locale and the current local time. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r das persische Kalendersystem
     * unter Benutzung von aus dem System abgeleiteten Standardwerten f&uuml;r die Sprach- und
     * L&auml;dereinstellung und die aktuelle Zonenzeit. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(Chronology)
     */
    public static CalendarPicker<PersianCalendar> persianWithSystemDefaults() {

        return CalendarPicker.persian(
            Locale.getDefault(Locale.Category.FORMAT),
            () -> SystemClock.inLocalView().now(PersianCalendar.axis())
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the persian calendar system (jalali). </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r das persische Kalendersystem (jalali). </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    public static CalendarPicker<PersianCalendar> persian(
        Locale locale,
        Supplier<PersianCalendar> todaySupplier
    ) {

        return CalendarPicker.create(
            PersianCalendar.axis(),
            new FXCalendarSystemPersian(),
            locale,
            todaySupplier
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the buddhist calendar system in Thailand using system defaults
     * for the locale and the current local time. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r das Thai-Kalendersystem
     * unter Benutzung von aus dem System abgeleiteten Standardwerten f&uuml;r die Sprach- und
     * L&auml;dereinstellung und die aktuelle Zonenzeit. </p>
     *
     * @return  CalendarPicker
     * @see     Locale#getDefault(Locale.Category) Locale.getDefault(Locale.Category.FORMAT)
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now(Chronology)
     */
    public static CalendarPicker<ThaiSolarCalendar> thaiWithSystemDefaults() {

        return CalendarPicker.thai(
            Locale.getDefault(Locale.Category.FORMAT),
            () -> SystemClock.inLocalView().now(ThaiSolarCalendar.axis())
        );

    }

    /**
     * <p>Creates a new {@code CalendarPicker} for the buddhist calendar system used in Thailand. </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen {@code CalendarPicker} f&uuml;r das Thai-Kalendersystem. </p>
     *
     * @param   locale          the language and country configuration
     * @param   todaySupplier   determines the current calendar date
     * @return  CalendarPicker
     */
    public static CalendarPicker<ThaiSolarCalendar> thai(
        Locale locale,
        Supplier<ThaiSolarCalendar> todaySupplier
    ) {

        return CalendarPicker.create(
            ThaiSolarCalendar.axis(),
            new FXCalendarSystemThai(),
            locale,
            todaySupplier
        );

    }

    /**
     * The current format locale.
     *
     * Note: If the locale is set to {@code null} (not recommended) then the locale in effect is {@code Locale.ROOT}.
     *
     * @return  read-write-property
     * @see     #setLocale(Locale)
     */
    /*[deutsch]
     * Die assoziierte Sprach- und L&auml;ndereinstellung f&uuml; Formatierungszwecke.
     *
     * Hinweis: Wenn die Sprache auf {@code null} gesetzt wird (nicht empfohlen), dann
     * wird effektiv {@code Locale.ROOT} angenommen.
     *
     * @return  read-write-property
     * @see     #setLocale(Locale)
     */
    public ObjectProperty<Locale> localeProperty() {
        return this.control.localeProperty();
    }

    /**
     * The current calendar date value associated with this component.
     *
     * @return  read-write-property
     * @see     #setValue(CalendarDate) setValue(T)
     */
    /*[deutsch]
     * Der assoziierte Datumswert.
     *
     * @return  read-write-property
     * @see     #setValue(CalendarDate) setValue(T)
     */
    public ObjectProperty<T> valueProperty() {
        return this.valuePropertyExt;
    }

    /**
     * The current error information associated with this component.
     *
     * @return  read-only-property
     */
    /*[deutsch]
     * Die assoziierte Fehlerinformation.
     *
     * @return  read-only-property
     */
    public ReadOnlyStringProperty errorProperty() {
        return this.errorProperty;
    }

    /**
     * The customized editor date format.
     *
     * @return  read-property
     * @see     #setDateFormat(ChronoFormatter)
     */
    /*[deutsch]
     * Das mit dem Texteditor assoziierte Datumsformat.
     *
     * @return  read-property
     * @see     #setDateFormat(ChronoFormatter)
     */
    public ReadOnlyObjectProperty<ChronoFormatter<T>> dateFormatProperty()  {
        return this.formatProperty;
    }

    /**
     * The customized editor prompt text.
     *
     * @return  read-property
     * @see     #setPromptText(String)
     */
    /*[deutsch]
     * Der mit dem Texteditor assoziierte Aufforderungstext.
     *
     * @return  read-property
     * @see     #setPromptText(String)
     */
    public ReadOnlyStringProperty promptTextProperty() {
        return this.promptProperty;
    }

    /**
     * The minimum value which can be selected.
     *
     * @return  read-property
     * @see     #setMinDate(CalendarDate) setMinDate(T)
     */
    /*[deutsch]
     * Der minimal ausw&auml;hlbare Wert.
     *
     * @return  read-property
     * @see     #setMinDate(CalendarDate) setMinDate(T)
     */
    public ReadOnlyObjectProperty<T> minDateProperty() {
        return this.control.minDateProperty();
    }

    /**
     * The maximum value which can be selected.
     *
     * @return  read-property
     * @see     #setMaxDate(CalendarDate) setMaxDate(T)
     */
    /*[deutsch]
     * Der maximal ausw&auml;hlbare Wert.
     *
     * @return  read-property
     * @see     #setMaxDate(CalendarDate) setMaxDate(T)
     */
    public ReadOnlyObjectProperty<T> maxDateProperty() {
        return this.control.maxDateProperty();
    }

    /**
     * Determines if the calendar shows week numbers.
     *
     * @return  boolean property
     * @see     #setShowWeeks(boolean)
     */
    /*[deutsch]
     * Legt fest, ob die Nummern von Kalenderwochen im Kalender angezeigt werden.
     *
     * @return  boolean property
     * @see     #setShowWeeks(boolean)
     */
    public BooleanProperty showWeeksProperty() {
        return this.control.showWeeksProperty();
    }

    /**
     * Determines if the header shows the chosen calendar range.
     *
     * @return  boolean property
     * @see     #setShowInfoLabel(boolean)
     */
    /*[deutsch]
     * Legt fest, ob der Kopf den gew&auml;hlten Tabellenbereich als ISO-Datumsintervall anzeigt.
     *
     * @return  boolean property
     * @see     #setShowInfoLabel(boolean)
     */
    public BooleanProperty showInfoLabelProperty() {
        return this.control.showInfoLabelProperty();
    }

    /**
     * Determines if and how long any animations will happen.
     *
     * If the duration is negative or zero then animations are switched off.
     *
     * @return  read-property
     * @see     #setLengthOfAnimations(Duration)
     */
    /*[deutsch]
     * Legt fest, ob und wie lange Animationen dauern.
     *
     * Ist die Dauer negativ oder gleich {@code ZERO}, dann sind Animationen abgeschaltet.
     *
     * @return  read-property
     * @see     #setLengthOfAnimations(Duration)
     */
    public ReadOnlyObjectProperty<Duration> lengthOfAnimationsProperty() {
        return this.control.lengthOfAnimationsProperty();
    }

    /**
     * <p>Allows user-defined customizations of date cells in the month view. </p>
     *
     * @return  read-write property (nullable)
     * @see     #setCellCustomizer(CellCustomizer)
     */
    /*[deutsch]
     * <p>Erlaubt benutzerdefinierte Anpassungen von Datumszellen in der Monatssicht. </p>
     *
     * @return  read-write property (nullable)
     * @see     #setCellCustomizer(CellCustomizer)
     */
    public ObjectProperty<CellCustomizer<T>> cellCustomizerProperty() {
        return this.control.cellCustomizerProperty();
    }

    public void setLocale(Locale locale){
        this.localeProperty().setValue(locale);
    }

    public void setValue(T value){
        this.valuePropertyExt.setValue(value);
    }

    public void setDateFormat(ChronoFormatter<T> dateFormat) {
        if (dateFormat == null) {
            throw new NullPointerException("Missing date format.");
        }
        this.formatProperty.setValue(dateFormat);
    }

    public void setPromptText(String promptText) {
        if (promptText == null) {
            throw new NullPointerException("Missing prompt text.");
        }
        this.promptProperty.setValue(promptText);
    }

    public void setMinDate(T minimum) {
        if (minimum == null) {
            throw new NullPointerException("Missing minimum date.");
        }
        this.control.minDateProperty().setValue(minimum);
    }

    public void setMaxDate(T maximum) {
        if (maximum == null) {
            throw new NullPointerException("Missing maximum date.");
        }
        this.control.maxDateProperty().setValue(maximum);
    }

    public void setShowWeeks(boolean showWeeks) {
        this.showWeeksProperty().set(showWeeks);
    }

    public void setShowInfoLabel(boolean showInfoLabel) {
        this.showInfoLabelProperty().set(showInfoLabel);
    }

    public void setLengthOfAnimations(Duration duration) {
        this.control.lengthOfAnimationsProperty().set((duration == null) ? Duration.ZERO : duration);
    }

    public void setCellCustomizer(CellCustomizer<T> customizer) {
        this.cellCustomizerProperty().set(customizer);
    }

    private static <D extends CalendarVariant<D>> CalendarPicker<D> create(
        CalendarFamily<D> family,
        FXCalendarSystem<D> calsys,
        Locale locale,
        Supplier<D> todaySupplier
    ) {
        CalendarPicker<D> picker =
            new CalendarPicker<>(
                calsys,
                locale,
                todaySupplier,
                family,
                calsys.getChronologicalMinimum(),
                calsys.getChronologicalMaximum());
        picker.setShowInfoLabel(true);
        return picker;
    }

    private static <U, T extends Calendrical<U, T>> CalendarPicker<T> create(
        TimeAxis<U, T> axis,
        FXCalendarSystem<T> calsys,
        Locale locale,
        Supplier<T> todaySupplier
    ) {
        CalendarPicker<T> picker =
            new CalendarPicker<>(calsys, locale, todaySupplier, axis, axis.getMinimum(), axis.getMaximum());
        picker.setShowInfoLabel(axis != PlainDate.axis());
        return picker;
    }

    private void commitTextInput() {

        try {
            this.committingText = true;
            ParseLog plog = new ParseLog();
            String input = this.textField.getText();

            if ((input == null) || input.trim().isEmpty()) {
                this.control.selectedDateProperty().setValue(null);
                this.errorProperty.setValue(null);
            } else {
                T date = this.getFormat().parse(input, plog);

                if (plog.isError()) {
                    this.control.selectedDateProperty().setValue(null);
                    this.errorProperty.setValue(
                        "[error-position=" + plog.getErrorIndex() + "] " + plog.getErrorMessage());
                } else if (
                    date.isBefore(this.control.minDateProperty().get())
                    || date.isAfter(this.control.maxDateProperty().get())
                ) {
                    this.control.selectedDateProperty().setValue(null);
                    this.errorProperty.setValue("[error] Out of range: " + date);
                } else {
                    this.control.selectedDateProperty().setValue(date);
                    this.errorProperty.setValue(null);
                }
            }
        } catch (RuntimeException ex) {
            this.control.selectedDateProperty().setValue(null);
            this.errorProperty.setValue("[error] " + ex.getMessage());
        } finally {
            this.updateTextField();
            this.committingText = false;
        }

    }

    private void updateTextField() {

        if (this.errorProperty.getValue() == null) {
            this.textInChange = true;
            T value = this.valuePropertyInt.getValue();
            if (value == null) {
                this.textField.setText("");
            } else {
                String s = this.getFormat().format(value);
                if (!this.textField.getText().equals(s)) {
                    this.textField.setText(s);
                }
            }
            this.textInChange = false;
        }

    }

    private ChronoFormatter<T> getFormat() {

        // we always use strict parsing in order to avoid problems with the print/parse-roundtrip of 2-digit-years
        ChronoFormatter<T> f;

        if (this.formatProperty.getValue() == null) {
            Locale locale = this.control.localeProperty().get();
            if (locale == null) {
                locale = Locale.ROOT;
            }
            String pattern = this.getStdFormatPattern(locale);
            f = ChronoFormatter.ofPattern(
                pattern,
                PatternType.CLDR,
                locale,
                this.control.chronology()
            ).with(Leniency.STRICT);
            if (this.calsys.getVariantSource().isPresent()) {
                f = f.withCalendarVariant(this.calsys.getVariantSource().get());
            }
        } else {
            f = this.formatProperty.getValue();
            if (!f.getAttributes().get(Attributes.LENIENCY, Leniency.SMART).isStrict()) {
                f = f.with(Leniency.STRICT);
            }
        }

        return f;

    }

    private String getStdFormatPattern(Locale locale) {

        String pattern = this.control.chronology().getFormatPattern(DisplayMode.SHORT, locale);

        if (pattern.contains("yy") && !pattern.contains("yyy")) {
            pattern = pattern.replace("yy", "yyyy"); // avoid two-digit-years if possible anyway
        }

        return pattern;

    }

    private void showPopup() {

        if (this.popupDialog == null) {
            Popup p = new AnimatedPopup();
            p.setAutoHide(true);
            p.setAutoFix(true);
            p.setHideOnEscape(true);
            CalendarContent<T> cc = new CalendarContent<>(this.control, this.calsys);
            cc.getStylesheets().setAll(this.getStylesheets());
            this.getStylesheets().addListener(
                (Observable observable) -> {
                    cc.getStylesheets().setAll(getStylesheets());
                }
            );
            p.getContent().add(cc);
            this.popupDialog = p;
        }

        Bounds cBounds = this.popupDialog.getContent().get(0).getBoundsInLocal();
        Bounds pBounds = this.localToScene(this.getBoundsInLocal());
        Scene scene = this.getScene();
        Window window = scene.getWindow();

        double x = cBounds.getMinX() + pBounds.getMinX() + scene.getX() + window.getX();
        double y = cBounds.getMinY() + pBounds.getHeight() + pBounds.getMinY() + scene.getY() + window.getY();

        this.popupDialog.show(this, x, y);

        // fix for issue reported by Dimitris Michaelides:
        // see https://bitbucket.org/controlsfx/controlsfx/issues/185/nullpointerexception-when-using-popover
        window.setOnCloseRequest(AnimatedPopup.class.cast(this.popupDialog).getClosingHandler());

    }

    private void hidePopup() {

        if (this.popupDialog != null) {
            this.popupDialog.hide();
        }

    }

    private Button createPopupButton() {

        Button button = new Button();
        button.getStyleClass().add(CSS_CALENDAR_DIALOG_START);
        ImageView image = new ImageView("/net/time4j/ui/javafx/calendar32.png");
        image.setFitHeight(16);
        image.setPreserveRatio(true);
        button.setGraphic(image);
        button.setOnAction(event -> showPopup());
        return button;

    }

    //~ Innere Klassen ----------------------------------------------------

    private class PromptBinding
        extends StringBinding {

        //~ Konstruktoren -------------------------------------------------

        PromptBinding() {
            super();

            this.bind(control.localeProperty(), formatProperty, promptProperty);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected String computeValue() {

            if (promptProperty.getValue() != null) {
                return promptProperty.getValue();
            } else if (formatProperty.getValue() == null) {
                Locale locale = control.localeProperty().get();
                if (locale == null) {
                    locale = Locale.ROOT;
                }
                return getStdFormatPattern(locale);
            }

            return "";

        }

    }

    private class AnimatedPopup extends Popup {

        //~ Instanzvariablen ----------------------------------------------

        private final FadeTransition hideFadeTransition;
        private final ScaleTransition hideScaleTransition;
        private final FadeTransition showFadeTransition;
        private final ScaleTransition showScaleTransition;

        private final EventHandler<WindowEvent> closingHandler;

        //~ Konstruktoren -------------------------------------------------

        AnimatedPopup() {
            super();

            Interpolator interpolator = new PopupInterpolator();

            showFadeTransition = new FadeTransition(Duration.seconds(0.2), getScene().getRoot());
            showFadeTransition.setFromValue(0);
            showFadeTransition.setToValue(1);
            showFadeTransition.setInterpolator(interpolator);

            showScaleTransition = new ScaleTransition(Duration.seconds(0.2), getScene().getRoot());
            showScaleTransition.setFromX(0.8);
            showScaleTransition.setFromY(0.8);
            showScaleTransition.setToY(1);
            showScaleTransition.setToX(1);
            showScaleTransition.setInterpolator(interpolator);

            hideFadeTransition = new FadeTransition(Duration.seconds(.3), getScene().getRoot());
            hideFadeTransition.setFromValue(1);
            hideFadeTransition.setToValue(0);
            hideFadeTransition.setInterpolator(interpolator);

            hideScaleTransition = new ScaleTransition(Duration.seconds(.3), getScene().getRoot());
            hideScaleTransition.setFromX(1);
            hideScaleTransition.setFromY(1);
            hideScaleTransition.setToY(0.8);
            hideScaleTransition.setToX(0.8);
            hideScaleTransition.setInterpolator(interpolator);

            hideScaleTransition.setOnFinished(
                actionEvent -> {
                    if (AnimatedPopup.super.isShowing()) {
                        AnimatedPopup.super.hide();
                    }
                }
            );

            this.closingHandler = (
                event -> {
                    final Popup p = popupDialog;

                    if (p != null) {
                        p.getOwnerWindow().removeEventFilter(
                            WindowEvent.WINDOW_CLOSE_REQUEST,
                            getClosingHandler());

                        if (p.isShowing()) {
                            // first closing request will only close the popup dialog but not the window
                            p.hide();
                            event.consume();
                        }

                        popupDialog = null;
                    }
                }
            );

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public void show() {

            super.show();

            if (showFadeTransition.getStatus() != Animation.Status.RUNNING) {
                showFadeTransition.playFromStart();
                showScaleTransition.playFromStart();
            }

        }

        @Override
        public void hide() {

            if (isShowing()) {
                if (!getOwnerWindow().isShowing()) {
                    hideFadeTransition.stop();
                    hideScaleTransition.stop();
                } else if (hideFadeTransition.getStatus() != Animation.Status.RUNNING) {
                    hideFadeTransition.playFromStart();
                    hideScaleTransition.playFromStart();
                }
            }

        }

        private EventHandler<WindowEvent> getClosingHandler() {

            return this.closingHandler;

        }

    }

    private static class PopupInterpolator
        extends Interpolator {

        //~ Methoden ------------------------------------------------------

        @Override
        protected double curve(double t) {

            double s = 1.70158;
            double v = 1 - t;
            return 1 - (v * v * ((s + 1) * v - s));

        }

    }

}