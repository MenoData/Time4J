/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OverlapResolver.java) is part of project Time4J.
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

package net.time4j.tz;


/**
 * <p>Represents the component of a transition strategy how to handle overlaps
 * on the local timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     GapResolver
 * @see     TransitionStrategy
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Komponente einer &Uuml;bergangsstrategie, wie
 * &Uuml;berlappungen auf dem lokalen Zeitstrahl behandelt werden. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     GapResolver
 * @see     TransitionStrategy
 */
public enum OverlapResolver {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Strategy which selects the earlier offset before a transition
     * where the local time is ambivalent due to an overlap on the local
     * timeline. </p>
     */
    /*[deutsch]
     * <p>Strategie, die den fr&uuml;heren Offset vor einem &Uuml;bergang
     * w&auml;hlt, wenn eine lokale Zeit wegen einer &Uuml;berlappung auf dem
     * lokalen Zeitstrahl nicht eindeutig ist. </p>
     */
    EARLIER_OFFSET,

    /**
     * <p>Default strategy which selects the later offset after a transition
     * where the local time is ambivalent due to an overlap on the local
     * timeline. </p>
     */
    /*[deutsch]
     * <p>Standardstrategie, die den sp&auml;teren Offset nach einem
     * &Uuml;bergang w&auml;hlt, wenn eine lokale Zeit wegen einer
     * &Uuml;berlappung auf dem lokalen Zeitstrahl nicht eindeutig ist. </p>
     */
    LATER_OFFSET;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields a transition strategy as combination of given gap resolver
     * and this instance. </p>
     *
     * @param   gapResolver    strategy how to handle gaps on the local timeline
     * @return  transition strategy for handling gaps and overlaps
     * @since   2.2
     * @see     GapResolver#and(OverlapResolver)
     */
    /*[deutsch]
     * <p>Liefert eine &Uuml;bergangsstrategie als Kombination der angegebenen
     * L&uuml;ckenstrategie und dieser Instanz. </p>
     *
     * @param   gapResolver    strategy how to handle gaps on the local timeline
     * @return  transition strategy for handling gaps and overlaps
     * @since   2.2
     * @see     GapResolver#and(OverlapResolver)
     */
    public TransitionStrategy and(GapResolver gapResolver) {

        return gapResolver.and(this);

    }

}
