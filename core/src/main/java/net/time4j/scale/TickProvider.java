/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TickProvider.java) is part of project Time4J.
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

package net.time4j.scale;


/**
 * <p>This <strong>SPI-interface</strong> describes how nanoseconds since an arbitrary
 * start time are generated. </p>
 *
 * <p>Will be evaluated during loading of the class {@code SystemClock}. The internal standard
 * implementation uses {@code System.nanoTime()}. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   3.2/4.1
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> beschreibt, wie Nanosekunden seit einem
 * beliebigen Startzeitpunkt generiert werden. </p>
 *
 * <p>Wird beim Laden der Klasse {@code net.time4j.SystemClock} ausgewertet. Die
 * interne Standardimplementierung basiert auf {@code System.nanoTime()}. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   3.2/4.1
 */
public interface TickProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines the name of the platform where this implementation should be used. </p>
     *
     * @return  name of suitable platform which is equivalent to the system property &quot;java.vm.name&quot;
     * @since   3.2/4.1
     */
    /*[deutsch]
     * <p>Gibt den Namen der Plattform an wo diese Implementierung genutzt werden darf. </p>
     *
     * @return  name of suitable platform which is equivalent to the system property &quot;java.vm.name&quot;
     * @since   3.2/4.1
     */
    String getPlatform();

    /**
     * <p>Generates a count of nanoseconds. </p>
     *
     * @return  count of nanosecond ticks since an arbitrary but fixed start time which is typically the boot time
     * @since   3.2/4.1
     */
    /*[deutsch]
     * <p>Generiert eine Anzahl von Nanosekunden. </p>
     *
     * @return  count of nanosecond ticks since an arbitrary but fixed start time which is typically the boot time
     * @since   3.2/4.1
     */
    long getNanos();

}
