/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalQuery.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoFunction;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


class ZonalQuery<V>
    implements ChronoFunction<Moment, V> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<V> element;
    private final Timezone tz;
    private final ZonalOffset offset;

    //~ Konstruktoren -----------------------------------------------------

    ZonalQuery(
        ChronoElement<V> element,
        Timezone tz
    ) {
        super();

        if (tz == null) {
            throw new NullPointerException("Missing timezone.");
        }

        this.element = element;
        this.tz = tz;
        this.offset = null;

    }

    ZonalQuery(
        ChronoElement<V> element,
        ZonalOffset offset
    ) {
        super();

        if (offset == null) {
            throw new NullPointerException("Missing timezone offset.");
        }

        this.element = element;
        this.tz = null;
        this.offset = offset;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public V apply(Moment context) {

        ZonalOffset shift = (
            (this.offset == null)
            ? this.tz.getOffset(context)
            : this.offset);

        if (
            (this.element == PlainTime.SECOND_OF_MINUTE)
            && context.isLeapSecond()
            && (shift.getFractionalAmount() == 0)
            && ((shift.getAbsoluteSeconds() % 60) == 0)
        ) {
            return this.element.getType().cast(Integer.valueOf(60));
        }

        return PlainTimestamp.from(context, shift).get(this.element);

    }

}
