/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ThreetenAdapter.java) is part of project Time4J.
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

package net.time4j.engine;

import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;


/**
 * <p>Defines the implementing chronological entity as {@code TemporalAccessor}. </p>
 *
 * @author  Meno Hochschild
 * @since   4.0
 */
/*[deutsch]
 * <p>Definiert die implementierende chronologische Entit&auml;t als {@code TemporalAccessor}. </p>
 *
 * @author  Meno Hochschild
 * @since   4.0
 */
public interface ThreetenAdapter
    extends TemporalAccessor {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Converts this object to a {@code TemporalAccessor}. </p>
     *
     * <p>Any implementation is required to return a new object with a different concrete type,
     * not this instance. </p>
     *
     * @return  converted Threeten-object (always as new object)
     * @since   4.0
     */
    /*[deutsch]
     * <p>Konvertiert dieses Objekt zu einem {@code TemporalAccessor}. </p>
     *
     * <p>Jedwede Implementierung ist angehalten, ein neues Objekt mit einem anderen Typ
     * statt dieser Instanz zur&uuml;ckzugeben. </p>
     *
     * @return  converted Threeten-object
     * @since   4.0
     */
    TemporalAccessor toTemporalAccessor();

    @Override
    default boolean isSupported(TemporalField field) {
        return this.toTemporalAccessor().isSupported(field);
    }

    @Override
    default ValueRange range(TemporalField field) {
        return this.toTemporalAccessor().range(field);
    }

    @Override
    default int get(TemporalField field) {
        return this.toTemporalAccessor().get(field);
    }

    @Override
    default long getLong(TemporalField field) {
        return this.toTemporalAccessor().getLong(field);
    }

    @Override
    default <R> R query(TemporalQuery<R> query) {
        return this.toTemporalAccessor().query(query);
    }

}
