/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (KoreanEra.java) is part of project Time4J.
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

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;
import net.time4j.base.GregorianMath;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.DisplayElement;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * <p>The Korean calendar supports the danki-system which is now only historic. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * <p>Der koreanische Kalender unterst&uuml;tzt das Danki-System,
 * das heute nur noch historisch ist. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
public enum KoreanEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Called after the legendary founder Dangun of the first Korean kingdom Gojoseon in year BC 2333.
     *
     * <p>It was used in South Korea from 1952 until 1961, in North Korea still before 1997. </p>
     */
    /*[deutsch]
     * Benannt nach dem mythischen Gr&uuml;nder Dangun des ersten koreanischen K&ouml;nigreiches Gojoseon
     * im Jahre BC 2333.
     *
     * <p>In S&uuml;dkorea wurde es von 1952 bis 1961 verwendet, in Nordkorea noch bis vor 1997. </p>
     */
    DANGI;

    //~ Instanzvariablen ----------------------------------------------

    private transient final ChronoElement<KoreanEra> eraElement = new EraElement();
    private transient final ChronoElement<Integer> yearOfEraElement = new YearOfEraElement();

    //~ Methoden ----------------------------------------------------------

    @Override
    @Deprecated
    public int getValue() {

        return 1;

    }

    /**
     * <p>Equivalent to the expression {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE);

    }

    /**
     * <p>Gets the description text dependent on the locale and style parameters. </p>
     *
     * <p>The second argument controls the width of description. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and style (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>&Uuml;ber das zweite Argument kann gesteuert werden, ob eine kurze
     * oder eine lange Form des Beschreibungstexts ausgegeben werden soll. Das
     * ist besonders sinnvoll in Benutzeroberfl&auml;chen, wo zwischen der
     * Beschriftung und der detaillierten Erl&auml;uterung einer graphischen
     * Komponente unterschieden wird. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and style (never {@code null})
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width
    ) {

        CalendarText names = CalendarText.getInstance("dangi", locale);
        return names.getEras(width).print(this);

    }

    /**
     * <p>Represents the Korean era as element. </p>
     *
     * <p>This element is effectively read-only and directly related to this instance hence not static.
     * Its value cannot be changed in a direct and meaningful way. The dangi era can also be used in
     * conjunction with {@code PlainDate}. </p>
     *
     * @see     #yearOfEra()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die koreanische &Auml;ra als Element. </p>
     *
     * <p>Dieses Element ist effektiv nur zur Anzeige und direkt auf diese Instanz bezogen, daher nicht
     * statisch. Sein Wert kann nicht direkt und sinnvoll ge&auml;ndert werden. Die Dangi-&Auml;ra kann
     * auch in Verbindung mit {@code PlainDate} verwendet werden. </p>
     *
     * @see     #yearOfEra()
     */
    @FormattableElement(format = "G")
    ChronoElement<KoreanEra> era() {

        return this.eraElement;

    }

    /**
     * <p>Represents the Korean year related to this era. </p>
     *
     * @see     #era()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das koreanische Jahr bezogen auf diese &Auml;ra. </p>
     *
     * @see     #era()
     */
    @FormattableElement(format = "y")
    ChronoElement<Integer> yearOfEra() {

        return this.yearOfEraElement;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class EraElement
        extends DisplayElement<KoreanEra>
        implements TextElement<KoreanEra> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -5179188137244162427L;

        //~ Konstruktoren -------------------------------------------------

        private EraElement() {
            super("ERA");

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<KoreanEra> getType() {
            return KoreanEra.class;
        }

        @Override
        public char getSymbol() {
            return 'G';
        }

        @Override
        public KoreanEra getDefaultMinimum() {
            return DANGI;
        }

        @Override
        public KoreanEra getDefaultMaximum() {
            return DANGI;
        }

        @Override
        public boolean isDateElement() {
            return true;
        }

        @Override
        public boolean isTimeElement() {
            return false;
        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {
            Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            String name = DANGI.getDisplayName(locale, width);
            buffer.append(name);
        }

        @Override
        public KoreanEra parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {
            Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            boolean caseInsensitive = attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue();
            boolean partialCompare = attributes.get(Attributes.PARSE_PARTIAL_COMPARE, Boolean.FALSE).booleanValue();
            TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            int offset = status.getIndex();
            String name = DANGI.getDisplayName(locale, width);
            int end = Math.max(Math.min(offset + name.length(), text.length()), offset);

            if (end > offset) {
                String test = text.subSequence(offset, end).toString();
                if (caseInsensitive) {
                    name = name.toLowerCase(locale);
                    test = test.toLowerCase(locale);
                }
                if (name.equals(test) || (partialCompare && name.startsWith(test))) {
                    status.setIndex(end);
                    return DANGI;
                }
            }

            status.setErrorIndex(offset);
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <T extends ChronoEntity<T>> ElementRule<T, KoreanEra> derive(Chronology<T> chronology) {
            if (chronology.isRegistered(PlainDate.COMPONENT)) {
                ElementRule<?, KoreanEra> rule = new EraRule();
                return (ElementRule<T, KoreanEra>) rule; // based on type erasure
            }
            return null;
        }

        @Override
        protected boolean isSingleton() {
            return true;
        }

        private Object readResolve() throws ObjectStreamException {
            return DANGI.era();
        }

    }

    private static class EraRule
        implements ElementRule<ChronoEntity<?>, KoreanEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public KoreanEra getValue(ChronoEntity<?> context) {
            return DANGI;
        }

        @Override
        public KoreanEra getMinimum(ChronoEntity<?> context) {
            return DANGI;
        }

        @Override
        public KoreanEra getMaximum(ChronoEntity<?> context) {
            return DANGI;
        }

        @Override
        public boolean isValid(
            ChronoEntity<?> context,
            KoreanEra value
        ) {
            return (value == DANGI);
        }

        @Override
        public ChronoEntity<?> withValue(
            ChronoEntity<?> context,
            KoreanEra value,
            boolean lenient
        ) {
            if (this.isValid(context, value)) {
                return context;
            } else {
                throw new IllegalArgumentException("Invalid Korean era: " + value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(ChronoEntity<?> context) {
            throw new AbstractMethodError("Never called.");
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(ChronoEntity<?> context) {
            throw new AbstractMethodError("Never called.");
        }

    }

    private static class YearOfEraElement
        extends DisplayElement<Integer> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -7864513245908399367L;

        //~ Konstruktoren -------------------------------------------------

        private YearOfEraElement() {
            super("YEAR_OF_ERA");

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public char getSymbol() {
            return 'y';
        }

        @Override
        public Integer getDefaultMinimum() {
            return Integer.valueOf(1645 + 2333);
        }

        @Override
        public Integer getDefaultMaximum() {
            return Integer.valueOf(2999 + 2333);
        }

        @Override
        public boolean isDateElement() {
            return true;
        }

        @Override
        public boolean isTimeElement() {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <T extends ChronoEntity<T>> ElementRule<T, Integer> derive(Chronology<T> chronology) {
            ElementRule<?, Integer> rule = null;
            if (chronology.isRegistered(PlainDate.COMPONENT)) {
                rule = new GregorianYearOfEraRule();
            }
            return (ElementRule<T, Integer>) rule;
        }

        @Override
        protected boolean isSingleton() {
            return true;
        }

        private Object readResolve() throws ObjectStreamException {
            return DANGI.yearOfEra();
        }

    }

    private static class GregorianYearOfEraRule
        implements ElementRule<ChronoEntity<?>, Integer> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(ChronoEntity<?> context) {
            return Integer.valueOf(this.getInt(context));
        }

        @Override
        public Integer getMinimum(ChronoEntity<?> context) {
            int r = GregorianMath.MIN_YEAR + 2333;
            return Integer.valueOf(r);
        }

        @Override
        public Integer getMaximum(ChronoEntity<?> context) {
            int r = GregorianMath.MAX_YEAR + 2333;
            return Integer.valueOf(r);
        }

        @Override
        public boolean isValid(
            ChronoEntity<?> context,
            Integer value
        ) {
            if (value == null) {
                return false;
            }
            int min = this.getMinimum(context).intValue();
            int max = this.getMaximum(context).intValue();
            return ((value >= min) && (value <= max));
        }

        @Override
        public ChronoEntity<?> withValue(
            ChronoEntity<?> context,
            Integer value,
            boolean lenient
        ) {
            if (value == null) {
                throw new IllegalArgumentException("Missing year of era.");
            } else if (this.isValid(context, value)) {
                int yoe = this.getInt(context);
                PlainDate date = context.get(PlainDate.COMPONENT);
                date = date.plus(value - yoe, CalendarUnit.YEARS);
                return context.with(PlainDate.COMPONENT, date);
            } else {
                throw new IllegalArgumentException("Invalid year of era: " + value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(ChronoEntity<?> context) {
            throw new AbstractMethodError("Never called.");
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(ChronoEntity<?> context) {
            throw new AbstractMethodError("Never called.");
        }

        private int getInt(ChronoEntity<?> context) {
            return context.get(PlainDate.COMPONENT).getYear() + 2333;
        }

    }

}
