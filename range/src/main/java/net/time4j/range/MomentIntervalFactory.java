/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MomentIntervalFactory.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.Moment;


final class MomentIntervalFactory
    implements IntervalFactory<Moment> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final MomentIntervalFactory INSTANCE = new MomentIntervalFactory();

    //~ Konstruktoren -----------------------------------------------------

    private MomentIntervalFactory() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ChronoInterval<Moment> between(
        Boundary<Moment> start,
        Boundary<Moment> end
    ) {

        return new MomentInterval(start, end);

    }

}
