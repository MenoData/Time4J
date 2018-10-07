/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Quarter.java) is part of project Time4J.
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
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoOperator;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * <p>Represents a quarter (in most cases of a year). </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein Quartal (meist eines Jahres). </p>
 *
 * @author  Meno Hochschild
 */
public enum Quarter
    implements ChronoCondition<GregorianDate>, ChronoOperator<PlainDate> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>First quarter with the numerical value {@code 1}. </p>
     */
    /*[deutsch]
     * <p>Erstes Quartal mit dem numerischen Wert {@code 1}. </p>
     */
    Q1,

    /**
     * <p>Second quarter with the numerical value {@code 2}. </p>
     */
    /*[deutsch]
     * <p>Zweites Quartal mit dem numerischen Wert {@code 2}. </p>
     */
    Q2,

    /**
     * <p>Third quarter with the numerical value {@code 3}. </p>
     */
    /*[deutsch]
     * <p>Drittes Quartal mit dem numerischen Wert {@code 3}. </p>
     */
    Q3,

    /**
     * <p>Last quarter with the numerical value {@code 4}. </p>
     */
    /*[deutsch]
     * <p>Letztes Quartal mit dem numerischen Wert {@code 4}. </p>
     */
    Q4;

    private static final Quarter[] ENUMS = Quarter.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical
     * value. </p>
     *
     * @param   quarter     value in the range [1-4]
     * @return  enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Liefert die zum chronologischen Integer-Wert passende
     * Enum-Konstante. </p>
     *
     * @param   quarter     value in the range [1-4]
     * @return  enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static Quarter valueOf(int quarter) {

        if ((quarter < 1) || (quarter > 4)) {
            throw new IllegalArgumentException("Out of range: " + quarter);
        }

        return ENUMS[quarter - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  int (Q1 = 1, Q2 = 2, Q3 = 3, Q4 = 4)
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden chronologischen Integer-Wert. </p>
     *
     * @return  int (Q1 = 1, Q2 = 2, Q3 = 3, Q4 = 4)
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Rolls to the next quarter. </p>
     *
     * <p>The result is {@code Q1} if this method is applied on {@code Q4}. </p>
     *
     * @return  next quarter rolling at last quarter
     */
    /*[deutsch]
     * <p>Ermittelt das n&auml;chste Quartal. </p>
     *
     * <p>Auf {@code Q4} angewandt ist das Ergebnis {@code Q1}. </p>
     *
     * @return  next quarter rolling at last quarter
     */
    public Quarter next() {

        return this.roll(1);

    }

    /**
     * <p>Rolls to the previous quarter. </p>
     *
     * <p>The result is {@code Q4} if this method is applied on {@code Q1}. </p>
     *
     * @return  previous quarter rolling at first quarter
     */
    /*[deutsch]
     * <p>Ermittelt das vorherige Quartal. </p>
     *
     * <p>Auf {@code Q1} angewandt ist das Ergebnis {@code Q4}. </p>
     *
     * @return  previous quarter rolling at first quarter
     */
    public Quarter previous() {

        return this.roll(-1);

    }

    /**
     * <p>Rolls by given amount of quarters. </p>
     *
     * @param   quarters    count of quarters (maybe negative)
     * @return  result of rolling operation
     */
    /*[deutsch]
     * <p>Rollt um die angegebene Anzahl von Quartalen vor oder
     * zur&uuml;ck. </p>
     *
     * @param   quarters    count of quarters (maybe negative)
     * @return  result of rolling operation
     */
    public Quarter roll(int quarters) {

        return Quarter.valueOf(
            (this.ordinal() + (quarters % 4 + 4)) % 4 + 1);

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

        return CalendarText.getIsoInstance(locale).getQuarters(width, context).print(this);

    }

    /**
     * <p>Tries to interprete given text as quarter of year. </p>
     *
     * @param   text    the text to be parsed
     * @param   locale  language setting
     * @param   width   expected text width
     * @param   context expected output context
     * @return  the parsed quarter of year if successful
     * @throws  ParseException if parsing fails
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Versucht, den angegebenen Text als Quartal zu interpretieren. </p>
     *
     * @param   text    the text to be parsed
     * @param   locale  language setting
     * @param   width   expected text width
     * @param   context expected output context
     * @return  the parsed quarter if successful
     * @throws  ParseException if parsing fails
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.33/4.28
     */
    public static Quarter parse(
        CharSequence text,
        Locale locale,
        TextWidth width,
        OutputContext context
    ) throws ParseException {

        ParsePosition pp = new ParsePosition(0);
        Quarter q = CalendarText.getIsoInstance(locale).getQuarters(width, context).parse(text, pp, Quarter.class);

        if (q == null) {
            throw new ParseException("Cannot parse: " + text, pp.getErrorIndex());
        } else {
            return q;
        }

    }

    @Override
    public boolean test(GregorianDate context) {

        int month = context.getMonth();
        return (this.getValue() == ((month - 1) / 3) + 1);

    }

    @Override
    public PlainDate apply(PlainDate date) {

        return date.with(PlainDate.QUARTER_OF_YEAR, this);

    }

}
