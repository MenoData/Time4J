/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MergedAttributes.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;


/**
 * <p>Represents a merged set of format attributes where the outer attributes
 * have precedence over the inner attributes. </p>
 *
 * @since   3.22/4.18
 */
final class MergedAttributes
    implements AttributeQuery {

    //~ Instanzvariablen --------------------------------------------------

    private final AttributeQuery outer;
    private final AttributeQuery inner;

    //~ Konstruktoren -----------------------------------------------------

    MergedAttributes(
        AttributeQuery outer,
        AttributeQuery inner
    ) {
        super();

        this.outer = outer;
        this.inner = inner;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(AttributeKey<?> key) {

        return (this.outer.contains(key) || this.inner.contains(key));

    }

    @Override
    public <A> A get(AttributeKey<A> key) {

        if (this.outer.contains(key)) {
            return this.outer.get(key);
        }

        return this.inner.get(key);

    }

    @Override
    public <A> A get(
        AttributeKey<A> key,
        A defaultValue
    ) {

        if (this.outer.contains(key)) {
            return this.outer.get(key);
        }

        return this.inner.get(key, defaultValue);

    }

}
