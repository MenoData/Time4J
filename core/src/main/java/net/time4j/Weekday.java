/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Weekday.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.engine.ChronoCondition;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * <p>Enumeration of weekdays. </p>
 *
 * <p>Several methods with a {@code Weekmodel}-parameter support other
 * week models, too. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Wochentagsaufz&auml;hlung. </p>
 *
 * <p>Verschiedene Methoden mit einem {@code Weekmodel}-Argument
 * unterst&uuml;tzen zus&auml;tzlich andere Wochenmodelle. </p>
 *
 * @author  Meno Hochschild
 */
public enum Weekday
    implements ChronoCondition<GregorianDate> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Monday with the numerical ISO-value {@code 1}. */
    /*[deutsch] Montag mit dem numerischen ISO-Wert {@code 1}. */
    MONDAY,

    /** Tuesday with the numerical ISO-value {@code 2}. */
    /*[deutsch] Dienstag mit dem numerischen ISO-Wert {@code 2}. */
    TUESDAY,

    /** Wednesday with the numerical ISO-value {@code 3}. */
    /*[deutsch] Mittwoch mit dem numerischen ISO-Wert {@code 3}. */
    WEDNESDAY,

    /** Thursday with the numerical ISO-value {@code 4}. */
    /*[deutsch] Donnerstag mit dem numerischen ISO-Wert {@code 4}. */
    THURSDAY,

    /** Friday with the numerical ISO-value {@code 5}. */
    /*[deutsch] Freitag mit dem numerischen ISO-Wert {@code 5}. */
    FRIDAY,

    /** Saturday with the numerical ISO-value {@code 6}. */
    /*[deutsch] Samstag mit dem numerischen ISO-Wert {@code 6}. */
    SATURDAY,

    /** Sunday with the numerical ISO-value {@code 7}. */
    /*[deutsch] Sonntag mit dem numerischen ISO-Wert {@code 7}. */
    SUNDAY;

    private static final Weekday[] ENUMS = Weekday.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the corresponding numerical ISO-value. </p>
     *
     * @return  {@code monday=1, tuesday=2, wednesday=3, thursday=4, friday=5, saturday=6, sunday=7}
     * @see     #valueOf(int)
     * @see     Weekmodel#ISO
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert
     * entsprechend der ISO-8601-Norm. </p>
     *
     * @return  {@code monday=1, tuesday=2, wednesday=3, thursday=4, friday=5, saturday=6, sunday=7}
     * @see     #valueOf(int)
     * @see     Weekmodel#ISO
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Gets the numerical value corresponding to the rule of given
     * week model on which day a week starts. </p>
     *
     * <p>In US, the rule is applied that weeks start with Sunday. If so then
     * this method will yield {@code 1} for {@code Weekmodel.of(Locale.US)}
     * (instead of the ISO-value {@code 7}). </p>
     *
     * @param   model       localized week model
     * @return  localized weekday number (1 - 7)
     * @see     Weekmodel#getFirstDayOfWeek()
     * @see     #values(Weekmodel)
     * @see     #valueOf(int, Weekmodel)
     */
    /*[deutsch]
     * <p>Liefert eine Wochentagsnummer passend zur im Modell enthaltenen
     * Regel, mit welchem Tag eine Woche beginnt. </p>
     *
     * <p>Wird z.B. die in den USA &uuml;bliche Regel angewandt, da&szlig;
     * der erste Tag einer Woche der Sonntag sein soll, dann hat der Sonntag
     * die Nummer 1 (statt 7 nach ISO-8601). </p>
     *
     * @param   model       localized week model
     * @return  localized weekday number (1 - 7)
     * @see     Weekmodel#getFirstDayOfWeek()
     * @see     #values(Weekmodel)
     * @see     #valueOf(int, Weekmodel)
     */
    public int getValue(Weekmodel model) {

        int shift = model.getFirstDayOfWeek().ordinal();
        return ((7 + this.ordinal() - shift) % 7) + 1;

    }

    /**
     * <p>Yields an array which is sorted corresponding to the rule of given
     * week model on which day a week starts. </p>
     *
     * <p>The alternative method generated by compiler without any parameters
     * creates an array sorted according to ISO-8601-standard. This method
     * is an overloaded variation where sorting is adjusted. </p>
     *
     * @param   model       localized week model
     * @return  new weekday array
     * @see     Weekmodel#getFirstDayOfWeek()
     * @see     #getValue(Weekmodel)
     * @see     #valueOf(int, Weekmodel)
     */
    /*[deutsch]
     * <p>Liefert ein Array, das passend zur im Model enthaltenen Regel
     * sortiert ist, mit welchem Tag eine Woche beginnt. </p>
     *
     * <p>Die vom Java-Compiler generierte {@code values()}-Methode ohne
     * Argument richtet sich nach dem ISO-8601-Wochenmodell. Diese Methode
     * ist die &uuml;berladene Variante, in der die Sortierung angepasst
     * ist. </p>
     *
     * @param   model       localized week model
     * @return  new weekday array
     * @see     Weekmodel#getFirstDayOfWeek()
     * @see     #getValue(Weekmodel)
     * @see     #valueOf(int, Weekmodel)
     */
    public static Weekday[] values(Weekmodel model) {

        Weekday[] enums = new Weekday[7];
        Weekday wd = model.getFirstDayOfWeek();

        for (int i = 0; i < 7; i++) {
            enums[i] = wd;
            wd = wd.next();
        }

        return enums;

    }

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical
     * value. </p>
     *
     * @param   dayOfWeek       (monday=1, tuesday=2, wednesday=3, thursday=4,
     *                          friday=5, saturday=6, sunday=7)
     * @return  weekday as enum
     * @throws  IllegalArgumentException if the argument is out of range
     * @see     #getValue()
     * @see     Weekmodel#ISO
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende
     * Enum-Konstante entsprechend der ISO-8601-Norm. </p>
     *
     * @param   dayOfWeek       (monday=1, tuesday=2, wednesday=3, thursday=4,
     *                          friday=5, saturday=6, sunday=7)
     * @return  weekday as enum
     * @throws  IllegalArgumentException if the argument is out of range
     * @see     #getValue()
     * @see     Weekmodel#ISO
     */
    public static Weekday valueOf(int dayOfWeek) {

        if ((dayOfWeek < 1) || (dayOfWeek > 7)) {
            throw new IllegalArgumentException("Out of range: " + dayOfWeek);
        }

        return ENUMS[dayOfWeek - 1];

    }

    /**
     * <p>Gets the enum-constant which corresponds to the given localized
     * numerical value taking into account given week model. </p>
     *
     * @param   dayOfWeek   localized weekday number (1 - 7)
     * @param   model       localized week model
     * @return  weekday as enum
     * @throws  IllegalArgumentException if the int-argument is out of range
     * @see     Weekmodel#getFirstDayOfWeek()
     * @see     #values(Weekmodel)
     * @see     #getValue(Weekmodel)
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende
     * Enum-Konstante passend zum angegebenen Wochenmodell. </p>
     *
     * @param   dayOfWeek   localized weekday number (1 - 7)
     * @param   model       localized week model
     * @return  weekday as enum
     * @throws  IllegalArgumentException if the int-argument is out of range
     * @see     Weekmodel#getFirstDayOfWeek()
     * @see     #values(Weekmodel)
     * @see     #getValue(Weekmodel)
     */
    public static Weekday valueOf(
        int dayOfWeek,
        Weekmodel model
    ) {

        if (
            (dayOfWeek < 1)
            || (dayOfWeek > 7)
        ) {
            throw new IllegalArgumentException(
                "Weekday out of range: " + dayOfWeek);
        }

        int shift = model.getFirstDayOfWeek().ordinal();
        return ENUMS[(dayOfWeek - 1 + shift) % 7];

    }

    /**
     * <p>Gets the weekday corresponding to given gregorian date. </p>
     *
     * <p>The proleptic gregorian calendar as defined in ISO-8601 is the
     * calculation basis. That means the current leap year rule is even
     * applied for dates before the introduction of gregorian calendar. </p>
     *
     * @param   year            proleptic iso year
     * @param   monthOfYear     gregorian month
     * @param   dayOfMonth      day of month (1 - 31)
     * @return  weekday
     * @throws  IllegalArgumentException if the day is out of range
     */
    /*[deutsch]
     * <p>Liefert den Wochentag zum angegebenen Datum. </p>
     *
     * <p>Grundlage ist der gregorianische Kalender proleptisch f&uuml;r
     * alle Zeiten ohne Kalenderwechsel angewandt. Es wird also so getan,
     * als ob der gregorianische Kalender schon vor dem 15. Oktober 1582
     * existiert h&auml;tte, so wie im ISO-8601-Format vorgesehen. </p>
     *
     * @param   year            proleptic iso year
     * @param   monthOfYear     gregorian month
     * @param   dayOfMonth      day of month (1 - 31)
     * @return  weekday
     * @throws  IllegalArgumentException if the day is out of range
     */
    public static Weekday valueOf(
        int year,
        Month monthOfYear,
        int dayOfMonth
    ) {

        return Weekday.valueOf(
            GregorianMath.getDayOfWeek(
                year,
                monthOfYear.getValue(),
                dayOfMonth
            )
        );

    }

    /**
     * <p>Rolls to the next day of week. </p>
     *
     * <p>The result is Monday if this method is applied on Sunday. </p>
     *
     * @return  next weekday
     */
    /*[deutsch]
     * <p>Ermittelt den n&auml;chsten Wochentag. </p>
     *
     * <p>Auf den Sonntag angewandt ist das Ergebnis der Montag. </p>
     *
     * @return  next weekday
     */
    public Weekday next() {

        return this.roll(1);

    }

    /**
     * <p>Rolls to the previous day of week. </p>
     *
     * <p>The result is Sunday if this method is applied on Monday. </p>
     *
     * @return  previous weekday
     */
    /*[deutsch]
     * <p>Ermittelt den vorherigen Wochentag. </p>
     *
     * <p>Auf den Montag angewandt ist das Ergebnis der Sonntag. </p>
     *
     * @return  previous weekday
     */
    public Weekday previous() {

        return this.roll(-1);

    }

    /**
     * <p>Rolls this day of week by given amount of days. </p>
     *
     * @param   days    count of days (maybe negative)
     * @return  result of rolling operation
     */
    /*[deutsch]
     * <p>Rollt um die angegebene Anzahl von Tagen vor oder zur&uuml;ck. </p>
     *
     * @param   days    count of days (maybe negative)
     * @return  result of rolling operation
     */
    public Weekday roll(int days) {

        return Weekday.valueOf((this.ordinal() + (days % 7 + 7)) % 7 + 1);

    }

    /**
     * <p>Equivalent to the expression
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}.
     * </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}.
     * </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(
            locale, TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Gets the description text dependent on the locale and style
     * parameters. </p>
     *
     * <p>The second argument controls the width of description while the
     * third argument is only relevant for languages which make a difference
     * between stand-alone forms and embedded text forms (does not matter in
     * English). </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   context     output context
     * @return  descriptive text for given locale and style (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>&Uuml;ber das zweite Argument kann gesteuert werden, ob eine kurze
     * oder eine lange Form des Beschreibungstexts ausgegeben werden soll. Das
     * ist besonders sinnvoll in Benutzeroberfl&auml;chen, wo zwischen der
     * Beschriftung und der detaillierten Erl&auml;uterung einer graphischen
     * Komponente unterschieden wird. Das dritte Argument ist in Sprachen von
     * Belang, die verschiedene grammatikalische Formen f&uuml;r die Ausgabe
     * als alleinstehend oder eingebettet in formatierten Text kennen. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   context     output context
     * @return  descriptive text for given locale and style (never {@code null})
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context
    ) {

        return CalendarText.getIsoInstance(locale).getWeekdays(width, context).print(this);

    }

    /**
     * <p>Tries to interprete given text as day-of-week. </p>
     *
     * @param   text    the text to be parsed
     * @param   locale  language setting
     * @param   width   expected text width
     * @param   context expected output context
     * @return  the parsed day of week if successful
     * @throws  ParseException if parsing fails
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Versucht, den angegebenen Text als Wochentag zu interpretieren. </p>
     *
     * @param   text    the text to be parsed
     * @param   locale  language setting
     * @param   width   expected text width
     * @param   context expected output context
     * @return  the parsed day of week if successful
     * @throws  ParseException if parsing fails
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.33/4.28
     */
    public static Weekday parse(
        CharSequence text,
        Locale locale,
        TextWidth width,
        OutputContext context
    ) throws ParseException {

        ParsePosition pp = new ParsePosition(0);
        Weekday wd = CalendarText.getIsoInstance(locale).getWeekdays(width, context).parse(text, pp, Weekday.class);

        if (wd == null) {
            throw new ParseException("Cannot parse: " + text, pp.getErrorIndex());
        } else {
            return wd;
        }

    }

    @Override
    public boolean test(GregorianDate context) {

        int y = context.getYear();
        int m = context.getMonth();
        int dom = context.getDayOfMonth();
        
        return (GregorianMath.getDayOfWeek(y, m, dom) == this.getValue());

    }

}
