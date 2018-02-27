/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EastAsianMonth.java) is part of project Time4J.
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

import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.NumberSystem;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Locale;
import java.util.Map;


/**
 * <p>Represents a month used in the East Asian countries China, Japan, Korea or Vietnam. </p>
 *
 * <p>This kind of month has its origins in the Chinese calendar. It is defined within the
 * lunisolar context and sometimes allows a leap month. Such a leap month happens about
 * every 2-3 years and has the same number as the preceding one. Therefore it is important
 * not to leave out the leap month flag before a month number if such a month has to be
 * printed. </p>
 *
 * <p>However, in Japan, the East Asian month is identical to the gregorian month since Meiji 6 (1873)
 * and does no longer make usage of any leap months. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den ostasiatischen Kalendermonat, der in den L&auml;ndern China, Japan, Korea und Vietnam
 * verwendet wird. </p>
 *
 * <p>Dieser Monatstyp hat seinen Ursprung in China, ist im lunisolaren Kontext definiert und erlaubt
 * manchmal einen Schaltmonat. Solch ein Schaltmonat geschieht ungef&auml;hr alle 2-3 Jahre und hat
 * die gleiche Nummer wie der vorangehende Monat. Deshalb ist es wichtig, in formatierten Monatsdarstellungen
 * das Schaltzeichen vor der Monatsnummer nicht wegzulassen, wenn es sich um einen Schaltmonat handelt. </p>
 *
 * <p>Hinweis: In Japan ist der ostasiatische Monat seit Meiji 6 (1873) identisch mit dem
 * gregorianischen Monat und benutzt keine Schaltmonate mehr. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
public final class EastAsianMonth
    implements Comparable<EastAsianMonth>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Format attribute which defines a symbol character for the leap month deviating from standard. </p>
     *
     * <p>This attribute is only relevant for the numeric formatting of leap months. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(AttributeKey, char)
     * @see     #LEAP_MONTH_IS_TRAILING
     */
    /*[deutsch]
     * <p>Formatattribut, das ein Symbolzeichen f&uuml;r den Schaltmonat abweichend vom Standard definiert. </p>
     *
     * <p>Dieses Attribut ist nur f&uuml;r die numerische Darstellung von Schaltmonaten von Interesse. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(AttributeKey, char)
     * @see     #LEAP_MONTH_IS_TRAILING
     */
    public static final AttributeKey<Character> LEAP_MONTH_INDICATOR =
        Attributes.createKey("LEAP_MONTH_INDICATOR", Character.class);

    /**
     * <p>Format attribute which defines if the symbol character for the leap month should be printed
     * after the month (default is {@code false} for most languages). </p>
     *
     * <p>This attribute is only relevant for the numeric formatting of leap months. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(AttributeKey, boolean)
     * @see     #LEAP_MONTH_INDICATOR
     * @since   3.40/4.35
     */
    /*[deutsch]
     * <p>Formatattribut, das angibt, ob das Symbolzeichen f&uuml;r den Schaltmonat nach dem Monat
     * angezeigt werden soll (Standard ist f&uuml;r die meisten Sprachen {@code false}). </p>
     *
     * <p>Dieses Attribut ist nur f&uuml;r die numerische Darstellung von Schaltmonaten von Interesse. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(AttributeKey, boolean)
     * @see     #LEAP_MONTH_INDICATOR
     * @since   3.40/4.35
     */
    public static final AttributeKey<Boolean> LEAP_MONTH_IS_TRAILING =
        Attributes.createKey("LEAP_MONTH_IS_TRAILING", Boolean.class);

    private static final EastAsianMonth[] CACHE;

    static {
        EastAsianMonth[] months = new EastAsianMonth[24];
        for (int i = 0; i < 12; i++) {
            months[i] = new EastAsianMonth(i, false);
            months[i + 12] = new EastAsianMonth(i, true);
        }
        CACHE = months;
    }

    private static final long serialVersionUID = 7544059597266533279L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  month index (0-11)
     */
    private final int index;

    /**
     * @serial  leap month flag
     */
    private final boolean leap;

    //~ Konstruktoren -----------------------------------------------------

    private EastAsianMonth(
        int index,
        boolean leap
    ) {
        super();

        this.index = index;
        this.leap = leap;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the standard East Asian month which corresponds to the given numerical value. </p>
     *
     * <p>Users have to invoke the method {@link #withLeap()} in order to obtain a leap month. </p>
     *
     * @param   month   standard month in the range [1-12]
     * @return  east asian month as wrapper around a number
     * @throws  IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Liefert den normalen ostasiatischen Monat mit dem angegebenen kalendarischen Integer-Wert. </p>
     *
     * <p>Um einen Schaltmonat zu erhalten, ist anschlie&szlig;end die Methode {@link #withLeap()} aufrufen. </p>
     *
     * @param   month   standard month in the range [1-12]
     * @return  east asian month as wrapper around a number
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static EastAsianMonth valueOf(int month) {

        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return CACHE[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value which is not necessarily unique due to a possible
     * leap month flag. </p>
     *
     * <p>Lunisolar leap months have the same number as the preceding month. </p>
     *
     * @return  number of month in the range [1-12]
     * @see     #isLeap()
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert,
     * der wegen m&ouml;glicher Schaltmonate nicht eindeutig sein mu&szlig;. </p>
     *
     * <p>Lunisolare Schaltmonate haben die gleiche Nummer wie der jeweils vorangehende Monat. </p>
     *
     * @return  number of month in the range [1-12]
     * @see     #isLeap()
     */
    public int getNumber() {

        return (this.index + 1);

    }

    /**
     * <p>Is this month a lunisolar leap month? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ist dieser Monat ein lunisolarer Schaltmonat? </p>
     *
     * @return  boolean
     */
    public boolean isLeap() {

        return this.leap;

    }

    /**
     * <p>Obtains the leap month version of this month. </p>
     *
     * @return  east asian leap month with the same number as this month
     */
    /*[deutsch]
     * <p>Liefert die geschaltete Version dieses Monats. </p>
     *
     * @return  east asian leap month with the same number as this month
     */
    public EastAsianMonth withLeap() {

        return CACHE[this.index + 12];

    }

    /**
     * <p>Gets the traditional Japanese month name. </p>
     *
     * <p>Note: The leap month flag is ignored. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert den traditionellen japanischen Monatsnamen. </p>
     *
     * <p>Hinweis: Diese Methode ignoriert den Umstand, ob dieser Monat ein Schaltmonat ist. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (never {@code null})
     */
    public String getOldJapaneseName(Locale locale) {

        Map<String, String> textForms = CalendarText.getInstance("japanese", locale).getTextForms();
        return textForms.get("t" + this.getNumber());

    }

    /**
     * <p>Obtains a mainly numeric representation of this month for display purposes. </p>
     *
     * <p>East Asian months are traditionally displayed in a numeric way. Example: </p>
     *
     * <pre>
     *      EastAsianMonth month = EastAsianMonth.valueOf(1).withLeap();
     *      String s = month.getDisplayName(Locale.ENGLISH, NumberSystem.ARABIC);
     *      System.out.println(s); // output with leap-indicator &quot;i&quot;: i1
     * </pre>
     *
     * <p>The leap indicator is locale-sensitive. Time4J uses the asterisk as default, but for the major
     * European languages with some affinity to Latin also the small letter &quot;i&quot; (intercalary).
     * The East Asian languages Chinese (閏), Japanese (閏), Korean (윤) and Vietnamese (n) have their own
     * special characters. </p>
     *
     * @param   locale      language setting
     * @param   numsys      number system
     * @return  descriptive text (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert eine weitgehend numerische Darstellung dieses Monats f&uuml;r Anzeigezwecke. </p>
     *
     * <p>Ostasiatische Monate werden traditionell in einer numerischen Darstellung pr&auml;sentiert. Beispiel: </p>
     *
     * <pre>
     *      EastAsianMonth month = EastAsianMonth.valueOf(1).withLeap();
     *      String s = month.getDisplayName(Locale.ENGLISH, NumberSystem.ARABIC);
     *      System.out.println(s); // Ausgabe mit dem Schalt-Sonderzeichen &quot;i&quot;: i1
     * </pre>
     *
     * <p>Das Schalt-Sonderzeichen h&auml;ngt von der verwendeten Sprache ab. Time4J benutzt das Sternchen
     * &quot;*&quot; als Standard, aber die wichtigsten europ&auml;ischen Sprachen mit einer gewissen N&auml;he
     * zu Lateinisch (inklusive Englisch und Deutsch) definieren den Kleinbuchstaben &quot;i&quot;. Die
     * ostasiatischen Sprachen Chinesisch (閏), Japanisch (閏), Koreanisch (윤) und Vietnamesisch (n) haben
     * ihre eigenen Sonderzeichen. </p>
     *
     * @param   locale      language setting
     * @param   numsys      number system
     * @return  descriptive text (never {@code null})
     */
    public String getDisplayName(
        Locale locale,
        NumberSystem numsys
    ) {

        String s = this.getDisplayName(locale, numsys, Attributes.empty());
        String lang = locale.getLanguage();

        if (lang.equals("zh")) {
            s = s + "月";
        } else if (lang.equals("ko")) {
            s = s + "월";
        } else if (lang.equals("ja")) {
            s = s + "月";
        }

        return s;

    }

    @Override
    public int compareTo(EastAsianMonth other) {

        if (this.index < other.index) {
            return -1;
        } else if (this.index > other.index) {
            return 1;
        } else if (this.leap) {
            return (other.leap ? 0 : 1);
        } else {
            return (other.leap ? -1 : 0);
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof EastAsianMonth) {
            EastAsianMonth that = (EastAsianMonth) obj;
            return ((this.index == that.index) && (this.leap == that.leap));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.index + (this.leap ? 12 : 0);

    }

    @Override
    public String toString() {

        String s = String.valueOf(this.index + 1);
        return (this.leap ? "*" + s : s);

    }

    // also called by EastAsianME
    String getDisplayName(
        Locale locale,
        NumberSystem numsys,
        AttributeQuery attributes
    ) {

        Map<String, String> textForms = CalendarText.getInstance("generic", locale).getTextForms();
        char zeroDigit = attributes.get(Attributes.ZERO_DIGIT, numsys.getDigits().charAt(0));
        String display = toNumeral(numsys, zeroDigit, this.getNumber());

        if (this.leap) {
            boolean trailing =
                attributes.get(LEAP_MONTH_IS_TRAILING, "R".equals(textForms.get("leap-alignment")));
            char indicator =
                attributes.get(LEAP_MONTH_INDICATOR, textForms.get("leap-indicator").charAt(0));
            display = (trailing ? display + indicator : indicator + display);
        }

        return display;

    }

    // also called by EastAsianME
    static String toNumeral(
        NumberSystem numsys,
        char zeroDigit,
        int number
    ) {

        if (numsys.isDecimal()) {
            int delta = zeroDigit - '0';
            String standard = Integer.toString(number);

            if (delta == 0) {
                return standard;
            }

            StringBuilder numeral = new StringBuilder();

            for (int i = 0, n = standard.length(); i < n; i++) {
                int codepoint = standard.charAt(i) + delta;
                numeral.append((char) codepoint);
            }

            return numeral.toString();
        } else {
            return numsys.toNumeral(number);
        }

    }

    /**
     * @serialData  Preserves the singleton semantic
     * @return      cached singleton
     * @throws      ObjectStreamException if deserializing is not possible
     */
    private Object readResolve() throws ObjectStreamException {

        try {
            return CACHE[this.index + (this.leap ? 12 : 0)];
        } catch (ArrayIndexOutOfBoundsException iooe) {
            throw new StreamCorruptedException();
        }

    }

}
