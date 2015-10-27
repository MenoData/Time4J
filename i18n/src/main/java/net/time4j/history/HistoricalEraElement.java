/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricalEraElement.java) is part of project Time4J.
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

package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.NumericalElement;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.text.ParsePosition;
import java.util.Locale;

import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Allgemeines verstellbares chronologisches Element auf enum-Basis. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class HistoricalEraElement
    extends BasicElement<HistoricEra>
    implements NumericalElement<HistoricEra>, TextElement<HistoricEra> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 5200533417265981438L;
    private static final Locale LATIN = new Locale("la");

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  associated chronological history
     */
    private final ChronoHistory history;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert ein neues Element mit den angegebenen Details. </p>
     *
     * @param   history     associated chronological history
     */
    HistoricalEraElement(ChronoHistory history) {
        super("ERA");

        this.history = history;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<HistoricEra> getType() {

        return HistoricEra.class;

    }

    @Override
    public char getSymbol() {

        return 'G';

    }

    @Override
    public HistoricEra getDefaultMinimum() {

        return HistoricEra.BC;

    }

    @Override
    public HistoricEra getDefaultMaximum() {

        return HistoricEra.AD;

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
    public int numerical(HistoricEra value) {

        return value.getValue();

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException {

        buffer.append(this.accessor(attributes).print(context.get(this)));

    }

    @Override
    public HistoricEra parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {

        return this.accessor(attributes).parse(
            text,
            status,
            this.getType(),
            attributes
        );

    }

    @Override
    protected <T extends ChronoEntity<T>> ElementRule<T, HistoricEra> derive(Chronology<T> chronology) {

        if (chronology.isRegistered(PlainDate.COMPONENT)) {
            return new Rule<T>(this.history);
        }

        return null;

    }

    private TextAccessor accessor(AttributeQuery attributes) {

        TextWidth textWidth = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);

        if (attributes.get(ChronoHistory.ATTRIBUTE_LATIN_ERA, Boolean.FALSE).booleanValue()) {
            // NARROW and SHORT like ABBREVIATED
            CalendarText cnames = CalendarText.getInstance("historic", LATIN);
            return cnames.getTextForms(this, ((textWidth == TextWidth.WIDE) ? "w" : "a"));
        }

        CalendarText cnames =
            CalendarText.getInstance(
                attributes.get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE),
                attributes.get(Attributes.LANGUAGE, Locale.ROOT));

        if (attributes.get(ChronoHistory.ATTRIBUTE_COMMON_ERA, Boolean.FALSE).booleanValue()) {
            // NARROW and SHORT like ABBREVIATED
            return cnames.getTextForms(this, ((textWidth == TextWidth.WIDE) ? "w" : "a"), "alt");
        }

        return cnames.getEras(textWidth);

    }

    private Object readResolve() throws ObjectStreamException {

        return this.history.era();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Rule<C extends ChronoEntity<C>>
        implements ElementRule<C, HistoricEra> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoHistory history;

        //~ Konstruktoren -------------------------------------------------

        Rule(ChronoHistory history) {
            super();

            this.history = history;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HistoricEra getValue(C context) {

            try {
                return this.history.convert(context.get(PlainDate.COMPONENT)).getEra();
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public HistoricEra getMinimum(C context) {

            return HistoricEra.BC;

        }

        @Override
        public HistoricEra getMaximum(C context) {

            return HistoricEra.AD;

        }

        @Override
        public boolean isValid(
            C context,
            HistoricEra value
        ) {

            if (value == null) {
                return false;
            }

            try {
                HistoricDate hd = this.history.convert(context.get(PlainDate.COMPONENT));
                HistoricDate newHD = HistoricDate.of(value, hd.getYearOfEra(), hd.getMonth(), hd.getDayOfMonth());
                newHD = this.history.adjustDayOfMonth(newHD);
                return this.history.isValid(newHD);
            } catch (IllegalArgumentException iae) {
                return false;
            }

        }

        @Override
        public C withValue(
            C context,
            HistoricEra value,
            boolean lenient
        ) {

            HistoricDate hd = this.history.convert(context.get(PlainDate.COMPONENT));
            HistoricDate newHD = HistoricDate.of(value, hd.getYearOfEra(), hd.getMonth(), hd.getDayOfMonth());
            newHD = this.history.adjustDayOfMonth(newHD);
            return context.with(PlainDate.COMPONENT, this.history.convert(newHD));

        }

        @Override
        public ChronoElement<?> getChildAtFloor(C context) {

            throw new UnsupportedOperationException("Never called.");

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(C context) {

            throw new UnsupportedOperationException("Never called.");

        }

    }

}
