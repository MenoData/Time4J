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

package net.time4j.historic;

import net.time4j.PlainDate;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoDisplay;
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
 * @param       <V> generic enum type of element values
 * @author      Meno Hochschild
 * @doctags.concurrency <immutable>
 */
final class HistoricalEraElement<V extends Enum<V>>
    extends BasicElement<V>
    implements NumericalElement<V>, TextElement<V> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Element-Index. */
    static final int ERA = 1;

    //private static final long serialVersionUID = 1L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final Class<V> type;
    private transient final V dmin;
    private transient final V dmax;
    private transient final int index;
    private transient final char symbol;
    private transient final  ElementRule<?, V> rule;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert ein neues Element mit den angegebenen Details. </p>
     *
     * @param   name        name of element
     * @param   type        reified type of element values
     * @param   defaultMin  default minimum
     * @param   defaultMax  default maximum
     * @param   index       element index
     * @param   symbol      CLDR-symbol used in format patterns
     */
    HistoricalEraElement(
        String name,
        Class<V> type,
        V defaultMin,
        V defaultMax,
        int index,
        char symbol
    ) {
        this(name, type, defaultMin, defaultMax, index, symbol, null);

    }

    /**
     * <p>Konstruiert ein neues Element mit den angegebenen Details. </p>
     *
     * @param   name        name of element
     * @param   type        reified type of element values
     * @param   defaultMin  default minimum
     * @param   defaultMax  default maximum
     * @param   index       element index
     * @param   symbol      CLDR-symbol used in format patterns
     */
    HistoricalEraElement(
        String name,
        Class<V> type,
        V defaultMin,
        V defaultMax,
        int index,
        char symbol,
        ElementRule<?, V> rule
    ) {
        super(name);

        this.type = type;
        this.dmin = defaultMin;
        this.dmax = defaultMax;
        this.index = index;
        this.symbol = symbol;
        this.rule = rule;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<V> getType() {

        return this.type;

    }

    @Override
    public char getSymbol() {

        return this.symbol;

    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public V getDefaultMinimum() {

        return this.dmin;

    }

    @Override
    public V getDefaultMaximum() {

        return this.dmax;

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
    public int numerical(V value) {

        return (this.isEraElement() ? value.ordinal() : value.ordinal() + 1);

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
    public V parse(
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
    protected <T extends ChronoEntity<T>> ElementRule<T, V> derive(
        Chronology<T> chronology
    ) {

        if (
            this.isEraElement()
            && chronology.isRegistered(PlainDate.COMPONENT)
        ) {
            return (ElementRule<T, V>) this.rule;
        }

        return null;

    }

    /**
     * <p>Liefert einen Zugriffsindex zur Optimierung der Elementsuche. </p>
     *
     * @return  int
     */
    int getIndex() {

        return this.index;

    }

    private boolean isEraElement() {

        return (this.index == ERA);

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

}
