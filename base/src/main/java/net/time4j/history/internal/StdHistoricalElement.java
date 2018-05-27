/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdHistoricalElement.java) is part of project Time4J.
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

package net.time4j.history.internal;

import net.time4j.engine.ChronoElement;
import net.time4j.format.DisplayElement;

import java.io.ObjectStreamException;


/**
 * <p>Allgemeines verstellbares chronologisches Element auf Integer-Basis. </p>
 *
 * @author  Meno Hochschild
 * @since   3.16/4.13
 */
public class StdHistoricalElement
    extends DisplayElement<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Spezialelement zur Identifikation des angezeigten historischen Jahres. </p>
     */
    public static final ChronoElement<Integer> YEAR_OF_DISPLAY =
        new StdHistoricalElement("YEAR_OF_DISPLAY", '\u0000', 1, 9999);

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final char symbol;
    private transient final Integer defaultMin;
    private transient final Integer defaultMax;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Default constructor. </p>
     *
     * @param   name        element name
     * @param   symbol      format symbol
     * @param   defaultMin  default minimum value
     * @param   defaultMax  default maximum value
     */
    protected StdHistoricalElement(
        String name,
        char symbol,
        int defaultMin,
        int defaultMax
    ) {
        super(name);

        this.symbol = symbol;
        this.defaultMin = Integer.valueOf(defaultMin);
        this.defaultMax = Integer.valueOf(defaultMax);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public final Class<Integer> getType() {

        return Integer.class;

    }

    @Override
    public char getSymbol() {

        return this.symbol;

    }

    @Override
    public Integer getDefaultMinimum() {

        return this.defaultMin;

    }

    @Override
    public Integer getDefaultMaximum() {

        return this.defaultMax;

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
    protected boolean isSingleton() {

        return true;

    }

    private Object readResolve() throws ObjectStreamException {

        return YEAR_OF_DISPLAY;

    }

}
