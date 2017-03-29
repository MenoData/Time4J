/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (JapaneseMonth.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.Month;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Locale;
import java.util.Map;


/**
 * <p>Represents the Japanese month. </p>
 *
 * <p>The Japanese month is either in exact agreement with the gregorian month (since Meiji 6)
 * or is defined within the lunisolar period of the old Japanese calendar where leap months
 * are possible. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den japanischen Kalendermonat. </p>
 *
 * <p>Der japanische Monat ist seit Meiji 6 (1873) identisch mit dem gregorianischen Monat,
 * aber davor war er im lunisolaren Kontext definiert und erlaubte manchmal einen Schaltmonat. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
public final class JapaneseMonth
    implements Serializable { // ChronoCondition<JapaneseCalendar>

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final JapaneseMonth[] CACHE;

    static {
        JapaneseMonth[] months = new JapaneseMonth[36];
        for (int i = 0; i < 36; i++) {
            months[i] = new JapaneseMonth(i);
        }
        CACHE = months;
    }

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  month index (0-11 for gregorian, 12-23 for lunisolar std months, 24-35 for lunisolar leap months)
     */
    private final int index;

    //~ Konstruktoren -----------------------------------------------------

    private JapaneseMonth(int index) {
        super();

        this.index = index;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the equivalent of a gregorian month which corresponds to the given numerical value. </p>
     *
     * @param   month   gregorian month in the range [1-12]
     * @return  japanese month as wrapper around a gregorian month
     * @throws  IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Liefert das &Auml;quivalent eines gregorianischen Monats mit dem angegebenen kalendarischen Integer-Wert. </p>
     *
     * @param   month   gregorian month in the range [1-12]
     * @return  japanese month as wrapper around a gregorian month
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static JapaneseMonth ofGregorian(int month) {

        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return CACHE[month - 1];

    }

    /**
     * <p>Gets the standard lunisolar variant used before Meiji 6. </p>
     *
     * @param   num     number in the range [1-12]
     * @return  japanese month for the lunisolar period before Meiji 6 (=1873)
     * @throws  IllegalArgumentException if the number is out of range
     */
    /*[deutsch]
     * <p>Liefert die normale lunisolare Variante, die vor Meiji 6 in Gebrauch war. </p>
     *
     * @param   num     number in the range [1-12]
     * @return  japanese month for the lunisolar period before Meiji 6 (=1873)
     * @throws  IllegalArgumentException if the number is out of range
     */
    public static JapaneseMonth ofLunisolarStdType(int num) {

        if ((num < 1) || (num > 12)) {
            throw new IllegalArgumentException("Month number out of range: " + num);
        }

        return CACHE[num + 11];

    }

    /**
     * <p>Gets the lunisolar leap month used before Meiji 6. </p>
     *
     * @param   num     number in the range [1-12]
     * @return  japanese leap month for the lunisolar period before Meiji 6 (=1873)
     * @throws  IllegalArgumentException if the number is out of range
     */
    /*[deutsch]
     * <p>Liefert den lunisolaren Schaltmonat, der vor Meiji 6 in Gebrauch war. </p>
     *
     * @param   num     number in the range [1-12]
     * @return  japanese leap month for the lunisolar period before Meiji 6 (=1873)
     * @throws  IllegalArgumentException if the number is out of range
     */
    public static JapaneseMonth ofLunisolarLeapType(int num) {

        if ((num < 1) || (num > 12)) {
            throw new IllegalArgumentException("Month number out of range: " + num);
        }

        return CACHE[num + 23];

    }

    /**
     * <p>Gets the corresponding numerical value which is not necessarily unique before Meiji 6. </p>
     *
     * <p>Lunisolar leap months have the same number as the preceding month. </p>
     *
     * @return  number of month in the range [1-12]
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert,
     * der vor Meiji 6 nicht eindeutig sein mu&szlig;. </p>
     *
     * <p>Lunisolare Schaltmonate haben die gleiche Nummer wie der jeweils vorangehende Monat. </p>
     *
     * @return  number of month in the range [1-12]
     */
    public int getNumber() {

        if (this.index < 12) {
            return (this.index + 1);
        } else if (this.index < 24) {
            return (this.index - 11);
        } else {
            return (this.index - 23);
        }

    }

    /**
     * <p>Is this japanese month equivalent to a gregorian month? </p>
     *
     * <p>If yes then a gregorian month can be obtained by {@code Month.valueOf(this.getNumber())}. </p>
     *
     * @return  boolean
     * @see     net.time4j.Month
     * @see     #getNumber()
     */
    /*[deutsch]
     * <p>Ist dieser japanische Monat &auml;quivalent zu einem gregorianischen Monat? </p>
     *
     * <p>Wenn ja, kann ein gregorianischer Monat mittels des Ausdrucks {@code Month.valueOf(this.getNumber())}
     * ermittelt werden. </p>
     *
     * @return  boolean
     * @see     net.time4j.Month
     * @see     #getNumber()
     */
    public boolean isGregorian() {

        return (this.index < 12);

    }

    /**
     * <p>Is this japanese month a lunisolar leap month? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ist dieser japanische Monat ein lunisolarer Schaltmonat? </p>
     *
     * @return  boolean
     */
    public boolean isLeap() {

        return (this.index >= 24);

    }

    /**
     * <p>Equivalent to the expression
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
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
     * <p><strong>Special notes about lunisolar months:</strong> </p>
     *
     * <p>Lunisolar months will normally be printed in a numerical way, possibly with a preceding character
     * indicating a leap month. As special case, if the text width is {@code WIDE} and the context is
     * {@code STANDALONE} then this method will yield the traditional month name, not the numerical one.
     * Example: </p>
     *
     * <pre>
     *      JapaneseMonth i1 = JapaneseMonth.ofLunisolarLeapType(1);
     *      String s = i1.getDisplayName(Locale.ENGLISH, TextWidth.WIDE, OutputContext.STANDALONE);
     *      System.out.println(s); // output with leap-indicator &quot;*&quot;: *Mutsuki
     * </pre>
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
     * <p><strong>Spezieller Hinweis zu lunisolaren Monaten:</strong> </p>
     *
     * <p>Lunisolare Monate werden normalerweise numerisch formatiert, m&ouml;glicherweise nach einem
     * Sonderzeichen f&uuml;r einen Schaltmonat. Sonderfall: Wenn die Textbreite {@code WIDE} und der
     * Kontext {@code STANDALONE} sind, dann wird diese Methode den traditionellen Monatsnamen statt
     * der numerischen Form verwenden. Beispiel: </p>
     *
     * <pre>
     *      JapaneseMonth i1 = JapaneseMonth.ofLunisolarLeapType(1);
     *      String s = i1.getDisplayName(Locale.ENGLISH, TextWidth.WIDE, OutputContext.STANDALONE);
     *      System.out.println(s); // output with leap-indicator &quot;*&quot;: *Mutsuki
     * </pre>
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

        if (this.isGregorian()) {
            CalendarText names = CalendarText.getIsoInstance(locale);
            return names.getStdMonths(width, context).print(Month.valueOf(this.index + 1));
        }

        Map<String, String> textForms = CalendarText.getInstance("japanese", locale).getTextForms();
        int num = this.getNumber();

        if ((width == TextWidth.WIDE) && (context == OutputContext.STANDALONE)) {
            String traditional = textForms.get("t" + num);
            if (this.isLeap()) {
                return textForms.get("leap-month") + traditional;
            } else {
                return traditional;
            }
        } else {
            String stdName = textForms.get("m" + num);
            if (this.isLeap()) {
                return textForms.get("leap-month") + stdName;
            } else {
                return stdName;
            }
        }

    }

//    @Override
//    public boolean test(JapaneseCalendar context) {
//
//        return (context.getMonth() == this);
//
//    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof JapaneseMonth) {
            return (this.index == ((JapaneseMonth) obj).index);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.index;

    }

    @Override
    public String toString() {

        int number = this.getNumber();

        if (this.isGregorian()) {
            return Month.valueOf(number).toString();
        } else {
            String s = String.valueOf(number);
            return (this.isLeap() ? "i" + s : s);
        }

    }

    /**
     * @serialData  Preserves the singleton semantic
     * @return      cached singleton
     * @throws      ObjectStreamException if deserializing is not possible
     */
    private Object readResolve() throws ObjectStreamException {

        try {
            return CACHE[this.index];
        } catch (ArrayIndexOutOfBoundsException iooe) {
            throw new StreamCorruptedException();
        }

    }

}
