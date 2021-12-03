/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HinduMonth.java) is part of project Time4J.
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

package net.time4j.calendar.hindu;

import net.time4j.calendar.IndianMonth;
import net.time4j.engine.AttributeKey;
import net.time4j.engine.ChronoCondition;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.io.Serializable;
import java.util.Locale;


/**
 * <p>The Hindu month varies in length and might also have a leap state when used in lunisolar context. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Die Hindumonate haben unterschiedliche L&auml;ngen und k&ouml;nnen im lunisolaren Kontext auch im
 * Schaltzustand vorliegen. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
public final class HinduMonth
    extends HinduPrimitive
    implements Comparable<HinduMonth>, ChronoCondition<HinduCalendar>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Format attribute which controls if Rasi names or traditional lunisolar names are used
     * for Hindu months in the solar calendar. </p>
     *
     * <p>The default is defined by the Hindu variant in question. For example, Kerala prefers Rasi names
     * while most other parts of India use the lunisolar forms. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(AttributeKey, boolean)
     */
    /*[deutsch]
     * <p>Formatattribut, das angibt, ob Rasi-Namen oder traditionelle lunisolare Monatsnamen
     * im Sonnenkalender verwendet werden sollen. </p>
     *
     * <p>Standard ist, was die jeweilige Hindu-Variante vorgibt. Kerala bevorzugt im Sonnenkalender
     * Rasi-Namen, w&auml;hrend in den meisten Teilen Indiens lunisolare Namen gebr&auml;uchlich sind. </p>
     *
     * @see     net.time4j.format.expert.ChronoFormatter#with(AttributeKey, boolean)
     */
    public static final AttributeKey<Boolean> RASI_NAMES =
        Attributes.createKey("RASI_NAMES", Boolean.class);

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  month of Indian national calendar
     */
    private final IndianMonth value;

    /**
     * @serial  leap month flag
     */
    private final boolean leap;

    //~ Konstruktoren -----------------------------------------------------

    private HinduMonth(
        IndianMonth value,
        boolean leap
    ) {
        super();

        if (value == null) {
            throw new NullPointerException("Missing Indian month.");
        }

        this.value = value;
        this.leap = leap;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the Hindu month which corresponds to the given Indian month. </p>
     *
     * <p>Users have to invoke the method {@link #withLeap()} in order to obtain a leap month
     * in lunisolar context. </p>
     *
     * @param   month   month of Indian national calendar
     * @return  associated Hindu month
     */
    /*[deutsch]
     * <p>Liefert den normalen Hindumonat basierend auf dem angegebenen indischen Standardmonat. </p>
     *
     * <p>Um einen Schaltmonat im lunisolaren Kontext zu erhalten, ist anschlie&szlig;end die Methode
     * {@link #withLeap()} aufrufen. </p>
     *
     * @param   month   month of Indian national calendar
     * @return  associated Hindu month
     */
    public static HinduMonth of(IndianMonth month) {
        return new HinduMonth(month, false);
    }

    /**
     * <p>Gets the Hindu month which corresponds to the given numerical value in lunisolar context. </p>
     *
     * <p>Users have to invoke the method {@link #withLeap()} in order to obtain a leap month
     * in lunisolar context. The first month is Chaitra. </p>
     *
     * @param   month   month value in the range [1-12]
     * @return  Hindu month as wrapper around a number
     * @throws  IllegalArgumentException if given argument is out of range 1-12
     */
    /*[deutsch]
     * <p>Liefert den normalen Hindumonat mit dem angegebenen kalendarischen Integer-Wert im lunisolaren Kalender. </p>
     *
     * <p>Um einen Schaltmonat im lunisolaren Kontext zu erhalten, ist anschlie&szlig;end die Methode
     * {@link #withLeap()} aufrufen. Der erste Monat ist Chaitra. </p>
     *
     * @param   month   month value in the range [1-12]
     * @return  Hindu month as wrapper around a number
     * @throws  IllegalArgumentException if given argument is out of range 1-12
     */
    public static HinduMonth ofLunisolar(int month) {
        return new HinduMonth(IndianMonth.valueOf(month), false);
    }

    /**
     * <p>Gets the Hindu month which corresponds to the given solar numerical value. </p>
     *
     * <p>Note: The first month of solar calendar is Mesa (or in lunisolar naming Vaishakha). </p>
     *
     * @param   month   month value in the range [1-12]
     * @return  Hindu month as wrapper around a number
     * @throws  IllegalArgumentException if given argument is out of range 1-12
     * @see     #getRasi()
     * @see     #getRasi(Locale)
     */
    /*[deutsch]
     * <p>Liefert den normalen Hindumonat mit dem angegebenen kalendarischen Integer-Wert im Sonnenkalender. </p>
     *
     * <p>Hinweis: Der erste Monat des Sonnenkalenders ist Mesa (oder in lunisolarer Benennung Vaishakha). </p>
     *
     * @param   month   month value in the range [1-12]
     * @return  Hindu month as wrapper around a number
     * @throws  IllegalArgumentException if given argument is out of range 1-12
     * @see     #getRasi()
     * @see     #getRasi(Locale)
     */
    public static HinduMonth ofSolar(int month) {
        int m = ((month == 12) ? 1 : month + 1);
        return new HinduMonth(IndianMonth.valueOf(m), false);
    }

    /**
     * <p>Obtains the corresponding Indian month. </p>
     *
     * <p>Important note: Hindu months in lunisolar context might be expunged which simply means that
     * there are gaps in the numbering of the months per year. And intercalated months have the same number. </p>
     *
     * @return  IndianMonth
     */
    /*[deutsch]
     * <p>Liefert den zugeh&ouml;rigen indischen Monat. </p>
     *
     * <p>Wichtiger Hinweis: Hindumonate im lunisolaren Kontext k&ouml;nnen L&uuml;cken in der Numerierung
     * haben. Und Schaltmonate haben dieselbe Nummer. </p>
     *
     * @return  IndianMonth
     */
    public IndianMonth getValue() {
        return this.value;
    }

    /**
     * <p>Obtains the value of solar month as the sun is going through the corresponding constellation (Rasi). </p>
     *
     * <p>The first solar month is VAISHAKHA. </p>
     *
     * @return  int
     * @see     #ofSolar(int)
     * @see     #getRasi(Locale)
     */
    /*[deutsch]
     * <p>Liefert den Wert des solaren Monats, so wie die Sonne durch die jeweilige Konstellation (Rasi) geht. </p>
     *
     * <p>Der erste solare Monat ist VAISHAKHA. </p>
     *
     * @return  int
     * @see     #ofSolar(int)
     * @see     #getRasi(Locale)
     */
    public int getRasi() {
        return ((this.value == IndianMonth.CHAITRA) ? 12 : this.value.getValue() - 1);
    }

    /**
     * <p>Obtains the localized text of solar month corresponding to the Hindu zodiac (Rasi). </p>
     *
     * <p>In many cases however, the lunisolar name is still used and can be obtained by
     * {@code getDisplayName(locale)}. If this month is in leap status then the localized word
     * for &quot;adhika&quot; will be inserted before the name. </p>
     *
     * @param   locale  localization parameter
     * @return  String
     * @see     #getRasi()
     * @see     #getDisplayName(Locale)
     */
    /*[deutsch]
     * <p>Liefert den lokalisierten Namen des solaren Monats entsprechend dem Hindu-Tierkreiszeichen (Rasi). </p>
     *
     * <p>In vielen F&auml;llen wird jedoch der lunisolare Name noch verwendet und kann mittels
     * {@code getDisplayName(locale)} ermittelt werden. Wenn dieser Monat ein eingeschobener Monat ist
     * (Schaltmonat), dann wird das sprachabh&auml;ngige Wort f&uuml;r &quot;adhika&quot; vor den Namen
     * gesetzt. </p>
     *
     * @param   locale  localization parameter
     * @return  String
     * @see     #getRasi()
     * @see     #getDisplayName(Locale)
     */
    public String getRasi(Locale locale) {
        CalendarText names = CalendarText.getInstance("hindu", locale);
        String rasi = names.getTextForms("R", IndianMonth.class).print(IndianMonth.valueOf(this.getRasi()));

        if (this.leap) { // should not happen because rasi is for solar calendars without leap months
            rasi = getAdhika(locale) + rasi;
        }

        return rasi;
    }

    /**
     * <p>Equivalent to the expression
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * <p>If this month is in leap status then the localized word
     * for &quot;adhika&quot; will be inserted before the name. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * <p>Wenn dieser Monat ein eingeschobener Monat ist (Schaltmonat), dann wird das
     * sprachabh&auml;ngige Wort f&uuml;r &quot;adhika&quot; vor den Namen gesetzt. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    public String getDisplayName(Locale locale) {
        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT);
    }

    /**
     * <p>Gets the description text dependent on the locale and style parameters. </p>
     *
     * <p>The second argument controls the width of description while the
     * third argument is only relevant for languages which make a difference
     * between stand-alone forms and embedded text forms (does not matter in
     * English). </p>
     *
     * <p>Note: Rasi names are not used by this method. </p>
     *
     * <p>If this month is in leap status then the localized word
     * for &quot;adhika&quot; will be inserted before the name. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   context     output context
     * @return  descriptive text for given locale and style (never {@code null})
     * @see     #getRasi(Locale)
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
     * <p>Hinweis: Rasi-Namen werden hier nicht verwendet. </p>
     *
     * <p>Wenn dieser Monat ein eingeschobener Monat ist (Schaltmonat), dann wird das
     * sprachabh&auml;ngige Wort f&uuml;r &quot;adhika&quot; vor den Namen gesetzt. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   context     output context
     * @return  descriptive text for given locale and style (never {@code null})
     * @see     #getRasi(Locale)
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context
    ) {
        String displayName = CalendarText.getInstance("indian", locale).getStdMonths(width, context).print(this.value);

        if (this.leap) {
            displayName = getAdhika(locale) + displayName;
        }

        return displayName;
    }

    /**
     * <p>Determines if this month is in leap state (intercalated month). </p>
     *
     * <p>A leap month is followed by a normal month with same number. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob dieser Monat ein Schaltmonat ist, also ein eingeschobener Monat. </p>
     *
     * <p>Ein Schaltmonat liegt direkt vor einem normalen Monat mit gleicher Nummer. </p>
     *
     * @return  boolean
     */
    @Override
    public boolean isLeap() {
        return this.leap;
    }

    /**
     * <p>Obtains the leap month version of this month. </p>
     *
     * <p>Leap months only exist in the lunisolar versions of the Hindu calendar. </p>
     *
     * @return  copy of this month but in leap state
     */
    /*[deutsch]
     * <p>Liefert die geschaltete Version dieses Monats. </p>
     *
     * <p>Schaltmonate existieren nur in den lunisolaren Varianten des Hindu-Kalenders. </p>
     *
     * @return  copy of this month but in leap state
     */
    public HinduMonth withLeap() {
        return (this.leap ? this : new HinduMonth(this.value, true));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof HinduMonth) {
            HinduMonth that = (HinduMonth) obj;
            return ((this.value == that.value) && (this.leap == that.leap));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.value.hashCode() + (this.leap ? 12 : 0);
    }

    @Override
    public String toString() {
        String s = this.value.toString();
        return (this.leap ? "*" + s : s);
    }

    /**
     * <p>Uses the comparing order of the lunisolar calendar. </p>
     *
     * <p>Leap months are sorted before months with same number. </p>
     *
     * @param   other   another month to be compared with
     * @return  comparing result
     */
    /*[deutsch]
     * <p>Verwendet die Anordnung der Monate im lunisolaren Kalender. </p>
     *
     * <p>Schaltmonate werden vor Monaten mit gleicher Nummer einsortiert. </p>
     *
     * @param   other   another month to be compared with
     * @return  comparing result
     */
    @Override
    public int compareTo(HinduMonth other) {
        int result = this.value.compareTo(other.value);

        if (result == 0) {
            if (this.leap) {
                result = (other.leap ? 0 : -1);
            } else {
                result = (other.leap ? 1 : 0);
            }
        }

        return result;
    }

    @Override
    public boolean test(HinduCalendar context) {
        return this.equals(context.getMonth());
    }

    private static String getAdhika(Locale locale) {
        return CalendarText.getInstance("hindu", locale).getTextForms().get("adhika") + " ";
    }

}
