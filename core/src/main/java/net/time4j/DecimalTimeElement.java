/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.engine.ChronoFunction;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.math.BigDecimal;


/**
 * <p>Ein dezimales Uhrzeitelement. </p>
 *
 * @author      Meno Hochschild
 */
final class DecimalTimeElement
    extends BasicElement<BigDecimal>
    implements ZonalElement<BigDecimal> {

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
    public ChronoFunction<Moment, BigDecimal> inStdTimezone() {

        return this.in(Timezone.ofSystem());

    }

    @Override
    public ChronoFunction<Moment, BigDecimal> inTimezone(TZID tzid) {

        return this.in(Timezone.of(tzid));

    }

    @Override
    public ChronoFunction<Moment, BigDecimal> in(Timezone tz) {

        return new ZonalQuery<BigDecimal>(this, tz);

    }

    @Override
    public ChronoFunction<Moment, BigDecimal> atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    @Override
    public ChronoFunction<Moment, BigDecimal> at(ZonalOffset offset) {

        return new ZonalQuery<BigDecimal>(this, offset);

    }

    @Override
    protected boolean isSingleton() {

        return true;

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
