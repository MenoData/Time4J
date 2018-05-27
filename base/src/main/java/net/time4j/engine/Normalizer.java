/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Normalizer.java) is part of project Time4J.
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


/**
 * <p>Normalizes a time span. </p>
 *
 * @param   <U> generic type of time unit compatible to {@link ChronoUnit}
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Normalisiert eine Zeitspanne. </p>
 *
 * @param   <U> generic type of time unit compatible to {@link ChronoUnit}
 * @author  Meno Hochschild
 */
public interface Normalizer<U> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Normalizes given time span such that the amounts of associated
     * time units are converted in a specific way. </p>
     *
     * @param   timespan    time span to be normalized
     * @return  normalized copy of argument which itself remains unaffected
     * @see     net.time4j.Duration#with(Normalizer)
     */
    /*[deutsch]
     * <p>Normalisiert die angegebene Zeitspanne so, da&szlig; die Betr&auml;ge
     * der enthaltenen Zeiteinheiten ineinander nach einem spezifischen
     * Verfahren umgerechnet werden. </p>
     *
     * @param   timespan    time span to be normalized
     * @return  normalized copy of argument which itself remains unaffected
     * @see     net.time4j.Duration#with(Normalizer)
     */
    TimeSpan<U> normalize(TimeSpan<? extends U> timespan);

}
