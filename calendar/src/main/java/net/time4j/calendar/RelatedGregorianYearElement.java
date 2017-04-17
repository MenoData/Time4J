/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RelatedGregorianYearElement.java) is part of project Time4J.
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

import net.time4j.base.GregorianMath;
import net.time4j.engine.BasicElement;

import java.io.ObjectStreamException;


/**
 * <p>Specific element for the related gregorian year. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
/*[deutsch]
 * <p>Spezialelement f&uuml;r das gregorianische Bezugsjahr. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
final class RelatedGregorianYearElement
    extends BasicElement<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final RelatedGregorianYearElement SINGLETON = new RelatedGregorianYearElement();

    private static final long serialVersionUID = -1117064522468823402L;

    //~ Konstruktoren -----------------------------------------------------

    private RelatedGregorianYearElement() {
        super("RELATED_GREGORIAN_YEAR");

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Integer> getType() {

        return Integer.class;

    }

    @Override
    public Integer getDefaultMinimum() {

        return Integer.valueOf(GregorianMath.MIN_YEAR);

    }

    @Override
    public Integer getDefaultMaximum() {

        return Integer.valueOf(GregorianMath.MAX_YEAR);

    }

    @Override
    public char getSymbol() {

        return 'r';

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

    /**
     * @serialData  preserves singleton semantic
     * @return      resolved singleton
     */
    protected Object readResolve() throws ObjectStreamException {

        return SINGLETON;

    }

}
