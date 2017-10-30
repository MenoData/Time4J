/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EnumElement.java) is part of project Time4J.
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.format.NumericalElement;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.GregorianTextElement;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * <p>Allgemeines verstellbares chronologisches Element auf enum-Basis. </p>
 *
 * @param       <V> generic enum type of element values
 * @author      Meno Hochschild
 */
final class EnumElement<V extends Enum<V>>
    extends AbstractDateElement<V>
    implements NavigableElement<V>, NumericalElement<V>, GregorianTextElement<V> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Element-Index. */
    static final int MONTH = 101;
    /** Element-Index. */
    static final int DAY_OF_WEEK = 102;
    /** Element-Index. */
    static final int QUARTER_OF_YEAR = 103;

    private static final long serialVersionUID = 2055272540517425102L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final Class<V> type;
    private transient final V dmin;
    private transient final V dmax;
    private transient final int index;
    private transient final char symbol;

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
    EnumElement(
        String name,
        Class<V> type,
        V defaultMin,
        V defaultMax,
        int index,
        char symbol
    ) {
        super(name);

        this.type = type;
        this.dmin = defaultMin;
        this.dmax = defaultMax;
        this.index = index;
        this.symbol = symbol;

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
    public ElementOperator<PlainDate> setToNext(V value) {

        return new NavigationOperator<V>(
            this, ElementOperator.OP_NAV_NEXT, value);

    }

    @Override
    public ElementOperator<PlainDate> setToPrevious(V value) {

        return new NavigationOperator<V>(
            this, ElementOperator.OP_NAV_PREVIOUS, value);

    }

    @Override
    public ElementOperator<PlainDate> setToNextOrSame(V value) {

        return new NavigationOperator<V>(
            this, ElementOperator.OP_NAV_NEXT_OR_SAME, value);

    }

    @Override
    public ElementOperator<PlainDate> setToPreviousOrSame(V value) {

        return new NavigationOperator<V>(
            this, ElementOperator.OP_NAV_PREVIOUS_OR_SAME, value);

    }

    @Override
    public int numerical(V value) {

        return value.ordinal() + 1;

    }

    @Override
    public int printToInt(
        V value,
        ChronoDisplay context,
        AttributeQuery attributes
    ) {

        return value.ordinal() + 1;

    }

    @Override
    public boolean parseFromInt(
        ChronoEntity<?> entity,
        int value
    ) {

        for (V v : this.getType().getEnumConstants()) {
            if (this.numerical(v) == value) {
                entity.with(this, v);
                return true;
            }
        }

        return false;

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException {

        Locale language = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
        TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
        OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
        buffer.append(this.accessor(language, tw, oc).print(context.get(this)));

    }

    @Override
    public V parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {

        int index = status.getIndex();
        Locale language = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
        TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
        OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
        V result = this.accessor(language, tw, oc).parse(text, status, this.getType(), attributes);

        if ((result == null) && attributes.get(Attributes.PARSE_MULTIPLE_CONTEXT, Boolean.TRUE)) {
            status.setErrorIndex(-1);
            status.setIndex(index);
            oc = ((oc == OutputContext.FORMAT) ? OutputContext.STANDALONE : OutputContext.FORMAT);
            result = this.accessor(language, tw, oc).parse(text, status, this.getType(), attributes);
        }

        return result;

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        Locale language,
        TextWidth tw,
        OutputContext oc
    ) throws IOException, ChronoException {

        buffer.append(this.accessor(language, tw, oc).print(context.get(this)));

    }

    @Override
    public V parse(
        CharSequence text,
        ParsePosition status,
        Locale language,
        TextWidth tw,
        OutputContext oc,
        Leniency leniency
    ) {

        int index = status.getIndex();
        V result = this.accessor(language, tw, oc).parse(text, status, this.getType(), leniency);

        if ((result == null) && !leniency.isStrict()) {
            status.setErrorIndex(-1);
            status.setIndex(index);
            oc = ((oc == OutputContext.FORMAT) ? OutputContext.STANDALONE : OutputContext.FORMAT);
            result = this.accessor(language, tw, oc).parse(text, status, this.getType(), leniency);
        }

        return result;

    }

    @Override
    protected boolean isSingleton() {

        return true; // exists only once per name in PlainDate

    }

    /**
     * <p>Liefert einen Zugriffsindex zur Optimierung der Elementsuche. </p>
     *
     * @return  int
     */
    int getIndex() {

        return this.index;

    }

    private TextAccessor accessor(
        Locale language,
        TextWidth tw,
        OutputContext oc
    ) {

        switch (this.index) {
            case MONTH:
                return CalendarText.getIsoInstance(language).getStdMonths(tw, oc);
            case DAY_OF_WEEK:
                return CalendarText.getIsoInstance(language).getWeekdays(tw, oc);
            case QUARTER_OF_YEAR:
                return CalendarText.getIsoInstance(language).getQuarters(tw, oc);
            default:
                throw new UnsupportedOperationException(this.name());
        }

    }

    private Object readResolve() throws ObjectStreamException {

        Object element = PlainDate.lookupElement(this.name());

        if (element == null) {
            throw new InvalidObjectException(this.name());
        } else {
            return element;
        }

    }

}
