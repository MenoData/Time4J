/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PredefinedKey.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.AttributeKey;


/**
 * <p>Repr&auml;sentiert einen vordefinierten Attributschl&uuml;ssel. </p>
 *
 * @param   <A> generic type of associated attribute values
 * @author  Meno Hochschild
 */
final class PredefinedKey<A>
    implements AttributeKey<A> {

    //~ Instanzvariablen --------------------------------------------------

    private final String name;
    private final Class<A> type;

    //~ Konstruktoren -----------------------------------------------------

    private PredefinedKey(
        String name,
        Class<A> type
    ) {
        super();

        if (name == null) {
            throw new NullPointerException("Missing name of attribute key.");
        } else if (type == null) {
            throw new NullPointerException("Missing type of attribute.");
        }

        this.name = name;
        this.type = type;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erzeugt einen neuen vordefinierten Attributschl&uuml;ssel. </p>
     *
     * @param   <A> generic type of associated attribute values
     * @param   name    name of attribute key
     * @param   type    reified type of attribute values
     * @return  new instance
     */
    static <A> PredefinedKey<A> valueOf(
        String name,
        Class<A> type
    ) {

        return new PredefinedKey<A>(name, type);

    }

    @Override
    public String name() {

        return this.name;

    }

    @Override
    public Class<A> type() {

        return this.type;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof PredefinedKey) {
            PredefinedKey<?> that = (PredefinedKey) obj;
            return (this.name.equals(that.name) && this.type.equals(that.type));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.name.hashCode();

    }

    @Override
    public String toString() {

        return this.type.getName() + "@" + this.name;

    }

}
