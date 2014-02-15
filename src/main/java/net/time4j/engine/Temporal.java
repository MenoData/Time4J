/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Temporal.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.engine;


/**
 * <p>Repr&auml;sentiert ein Objekt, das auf einem Zeitstrahl rein zeitlich
 * angeordnet werden kann. </p>
 *
 * @param   <C> generic temporal type for pure temporal comparison purposes
 * @author  Meno Hochschild
 */
public interface Temporal<C> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liegt dieses Objekt zeitlich nach dem angegebenen Argument? </p>
     *
     * @param   temporal    object this instance is compared to
     * @return  {@code true} if this instance is temporally after
     *          {@code temporal} else {@code false}
     */
    boolean isAfter(C temporal);

    /**
     * <p>Liegt dieses Objekt zeitlich vor dem angegebenen Argument? </p>
     *
     * @param   temporal    object this instance is compared to
     * @return  {@code true} if this instance is temporally before
     *          {@code temporal} else {@code false}
     */
    boolean isBefore(C temporal);

    /**
     * <p>Sind dieses Objekt und das angegebene Argument zeitlich gleich? </p>
     *
     * <p>Entspricht {@code !isAfter(temporal) && !isBefore(temporal)}. Diese
     * Methode unterscheidet sich von der Objektmethode {@code equals()} darin,
     * da&szlig; erstens der Vergleichstyp ein temporaler sein mu&szlig; und
     * zweitens nur rein zeitliche Zustandsattribute verglichen werden. </p>
     *
     * @param   temporal    object this instance is compared to
     * @return  {@code true} if this instance is temporally equal
     *          to {@code temporal} else {@code false}
     */
    boolean isSimultaneous(C temporal);

}
