/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZOM.java) is part of project Time4J.
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

import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;

import static net.time4j.format.Attributes.TIMEZONE_ID;


/**
 * <p>Spezialattribut zur Vermeidung der extra Objekterzeugung beim
 * Formatieren eines {@code ZonalMoment}. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
final class ZOM
    implements AttributeQuery, AttributeKey<ZonalMoment> {

    //~ Instanzvariablen --------------------------------------------------

    private final ZonalMoment zm;
    private final AttributeQuery query;

    //~ Konstruktoren -----------------------------------------------------

    ZOM(
        ZonalMoment zm,
        AttributeQuery query
    ) {
        super();

        this.zm = zm;
        this.query = query;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(AttributeKey<?> key) {

        return (
            (this == key)
            || TIMEZONE_ID.equals(key)
            || this.query.contains(key)
        );

    }

    @Override
    public <A> A get(AttributeKey<A> key) {

        if (this == key) {
            return key.type().cast(this.zm);
        } else if (TIMEZONE_ID.equals(key)) {
            return key.type().cast(this.zm.getTimezone());
        }

        return this.query.get(key);

    }

    @Override
    public <A> A get(
        AttributeKey<A> key,
        A defaultValue
    ) {

        if (this == key) {
            return key.type().cast(this.zm);
        } else if (TIMEZONE_ID.equals(key)) {
            return key.type().cast(this.zm.getTimezone());
        }

        return this.query.get(key, defaultValue);

    }

    @Override
    public Class<ZonalMoment> type() {

        return ZonalMoment.class;

    }

    @Override
    public String name() {

        return "ZOM(" + this.zm + ")";

    }

}
