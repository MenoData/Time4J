/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DecimalTimeElement.java) is part of project Time4J.
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

import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoDisplay;
import net.time4j.format.NumericalElement;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * <p>Ein dezimales Uhrzeitelement. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class DecimalTimeElement
    extends BasicElement<BigDecimal>
    implements NumericalElement<BigDecimal> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -4837430960549551204L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final BigDecimal defaultMax;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   name        name of element
     * @param   defaultMax  default maximum
     */
    DecimalTimeElement(
        String name,
        BigDecimal defaultMax
    ) {
        super(name);

        this.defaultMax = defaultMax;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<BigDecimal> getType() {

        return BigDecimal.class;

    }

    // TODO: Notwendigkeit prüfen, evtl. verzichtbar wg. direkter Typprüfung,
    //       oder in ChronoFormatter.Builder eine Methode addDecimal() einbauen!
    @Override
    public int numerical(BigDecimal value) {

        return value.setScale(9, RoundingMode.FLOOR).intValue();

    }

    @Override
    public BigDecimal getDefaultMinimum() {

        return BigDecimal.ZERO;

    }

    @Override
    public BigDecimal getDefaultMaximum() {

        return this.defaultMax;

    }

    @Override
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return true;

    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    private Object readResolve() throws ObjectStreamException {

        Object element = PlainTime.lookupElement(this.name());

        if (element == null) {
            throw new InvalidObjectException(this.name());
        } else {
            return element;
        }

    }

}
