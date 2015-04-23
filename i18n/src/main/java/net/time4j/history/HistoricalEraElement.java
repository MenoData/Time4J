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
 * @author      Meno Hochschild
 * @doctags.concurrency <immutable>
 */
final class HistoricalEraElement
    extends BasicElement<HistoricEra>
    implements NumericalElement<HistoricEra>, TextElement<HistoricEra> {

    //~ Statische Felder/Initialisierungen --------------------------------

    //private static final long serialVersionUID = 1L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final ElementRule<?, HistoricEra> rule;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert ein neues Element mit den angegebenen Details. </p>
     *
     * @param   rule    element rule
     */
    HistoricalEraElement(ElementRule<?, HistoricEra> rule) {
        super("ERA");

        this.rule = rule;

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
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

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
    @SuppressWarnings("unchecked")
    protected <T extends ChronoEntity<T>> ElementRule<T, HistoricEra> derive(Chronology<T> chronology) {

        if (chronology.isRegistered(PlainDate.COMPONENT)) {
            return (ElementRule<T, HistoricEra>) this.rule;
        }

        return null;

    }

    private TextAccessor accessor(AttributeQuery attributes) {

        CalendarText cnames =
            CalendarText.getInstance(
                attributes.get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE),
                attributes.get(Attributes.LANGUAGE, Locale.ROOT));

        TextWidth textWidth =
            attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);

        return cnames.getEras(textWidth);

    }

    private Object readResolve() throws ObjectStreamException {

        return this; // TODO: improve

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class EraRule<T extends ChronoEntity<T>>
        implements ElementRule<T, HistoricEra> {

        //~ Instanzvariablen ----------------------------------------------

        //~ Konstruktoren -------------------------------------------------

        EraRule() {
            super();

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HistoricEra getValue(T context) {

            PlainDate date = context.get(PlainDate.COMPONENT);
            return ((date.getYear() >= 1) ? HistoricEra.AD : HistoricEra.BC);

        }

        @Override
        public HistoricEra getMinimum(T context) {

            return HistoricEra.BC;

        }

        @Override
        public HistoricEra getMaximum(T context) {

            return HistoricEra.AD;

        }

        @Override
        public boolean isValid(
            T context,
            HistoricEra value
        ) {

            return (value != null);

        }

        @Override
        public T withValue(
            T context,
            HistoricEra value,
            boolean lenient
        ) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            throw new UnsupportedOperationException("Never called.");

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            throw new UnsupportedOperationException("Never called.");

        }

    }

}
