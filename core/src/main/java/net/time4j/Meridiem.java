/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Meridiem.java) is part of project Time4J.
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

import net.time4j.base.WallTime;
import net.time4j.engine.ChronoCondition;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * <p>Represents the half day relative to noon. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert vor- oder nachmittags. </p>
 *
 * @author  Meno Hochschild
 */
public enum Meridiem
    implements ChronoCondition<WallTime> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Marks the wall time from midnight (at start of day) until
     * before noon (ante meridiem). </p>
     *
     * <p>The numerical value is {@code 0}. </p>
     */
    /*[deutsch]
     * <p>Bezeichnet die Uhrzeit ab Mitternacht bis vor dem Mittag
     * (ante meridiem). </p>
     *
     * <p>Der numerische Wert ist {@code 0}. </p>
     */
    AM,

    /**
     * <p>Marks the wall time at or after noon (post meridiem). </p>
     *
     * <p>The numerical value is {@code 1}. </p>
     */
    /*[deutsch]
     * <p>Bezeichnet die Uhrzeit nach oder gleich dem Mittag
     * (post meridiem). </p>
     *
     * <p>Der numerische Wert ist {@code 1}. </p>
     */
    PM;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the meridiem value dependent on given hour of day. </p>
     *
     * @param   hour    ISO-hour in the range {@code 0 <= hour <= 24}
     * @return  half of day (ante meridiem or post meridiem)
     * @throws  IllegalArgumentException if the hour is out of range
     * @see     PlainTime#AM_PM_OF_DAY
     */
    /*[deutsch]
     * <p>Ermittelt den Tagesabschnitt auf Basis der angegebenen
     * Tagesstunde. </p>
     *
     * @param   hour    ISO-hour in the range {@code 0 <= hour <= 24}
     * @return  half of day (ante meridiem or post meridiem)
     * @throws  IllegalArgumentException if the hour is out of range
     * @see     PlainTime#AM_PM_OF_DAY
     */
    public static Meridiem ofHour(int hour) {

        if ((hour >= 0) && (hour <= 24)) {
            return (((hour < 12) || (hour == 24)) ? AM : PM);
        } else {
            throw new IllegalArgumentException(
                "Hour of day out of range: " + hour);
        }

    }

    /**
     * <p>Equivalent to the expression
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale  language of text to be printed
     * @return  localized text in given language
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale  language of text to be printed
     * @return  localized text in given language
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT);

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
     * @since   3.35/4.30
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
     * @since   3.35/4.30
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context
    ) {

        return CalendarText.getIsoInstance(locale).getMeridiems(width, context).print(this);

    }

    /**
     * <p>Tries to interprete given text as AM/PM. </p>
     *
     * <p>The strings &quot;am&quot;, &quot;AM&quot;, &quot;pm&quot;, &quot;PM&quot; are always understood. </p>
     *
     * @param   text    the text to be parsed
     * @param   locale  language setting
     * @param   width   expected text width
     * @param   context expected output context
     * @return  the parsed meridiem if successful
     * @throws ParseException if parsing fails
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>Versucht, den angegebenen Text als AM/PM zu interpretieren. </p>
     *
     * <p>Die Ausdr&uuml;cke &quot;am&quot;, &quot;AM&quot;, &quot;pm&quot;, &quot;PM&quot;
     * werden immer verstanden. </p>
     *
     * @param   text    the text to be parsed
     * @param   locale  language setting
     * @param   width   expected text width
     * @param   context expected output context
     * @return  the parsed meridiem if successful
     * @throws  ParseException if parsing fails
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.35/4.30
     */
    public static Meridiem parse(
        CharSequence text,
        Locale locale,
        TextWidth width,
        OutputContext context
    ) throws ParseException {

        if (text.length() == 2) {
            char c2 = text.charAt(1);
            if (c2 == 'M' || c2 == 'm') {
                char c1 = text.charAt(0);
                if (c1 == 'A' || c1 == 'a') {
                    return Meridiem.AM;
                } else if (c1 == 'P' || c1 == 'p') {
                    return Meridiem.PM;
                }
            }
        }

        ParsePosition pp = new ParsePosition(0);
        Meridiem m = CalendarText.getIsoInstance(locale).getMeridiems(width, context).parse(text, pp, Meridiem.class);

        if (m == null) {
            throw new ParseException("Cannot parse: " + text, pp.getErrorIndex());
        } else {
            return m;
        }

    }

    @Override
    public boolean test(WallTime context) {

        int hour = context.getHour();

        return (
            (this == AM)
            ? ((hour < 12) || (hour == 24))
            : ((hour >= 12) && (hour < 24))
        );

    }

}
