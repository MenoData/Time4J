/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeElement.java) is part of project Time4J.
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

import net.time4j.base.UnixTime;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;

import java.io.ObjectStreamException;


/**
 * <p>Repr&auml;sentiert eine Uhrzeitkomponente. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class TimeElement
    extends BasicElement<PlainTime>
	implements WallTimeElement {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Singleton-Instanz.
     */
    static final TimeElement INSTANCE = new TimeElement();

    private static final long serialVersionUID = -3712256393866098916L;

    //~ Konstruktoren -----------------------------------------------------

    private TimeElement() {
        super("WALL_TIME");

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<PlainTime> getType() {

        return PlainTime.class;

    }

    @Override
    public PlainTime getDefaultMinimum() {

        return PlainTime.MIN;

    }

    @Override
    public PlainTime getDefaultMaximum() {

        return PlainTime.of(23, 59, 59, 999999999);

    }

    @Override
    public int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return true;

    }

	@Override
	public ElementOperator<PlainTimestamp> setToNext(PlainTime value) {
		
		return new WallTimeOperator(ElementOperator.OP_NAV_NEXT, value);
		
	}

	@Override
	public ElementOperator<PlainTimestamp> setToPrevious(PlainTime value) {
		
		return new WallTimeOperator(ElementOperator.OP_NAV_PREVIOUS, value);
		
	}

	@Override
	public ElementOperator<PlainTimestamp> setToNextOrSame(PlainTime value) {
		
		return new WallTimeOperator(ElementOperator.OP_NAV_NEXT_OR_SAME, value);
		
	}

	@Override
	public ElementOperator<PlainTimestamp> setToPreviousOrSame(PlainTime value) {
		
		return new WallTimeOperator(ElementOperator.OP_NAV_PREVIOUS_OR_SAME, value);
		
	}

    @Override
    protected String getVeto(Chronology<?> chronology) {

        if (UnixTime.class.isAssignableFrom(chronology.getChronoType())) {
            return "Accessing the wall time component from a global type "
                   + "requires a timezone. Try to first convert the global "
                   + "type to a PlainTimestamp: \"moment.inZonalView(...)\".";
        }

        return null;

    }

    private Object readResolve() throws ObjectStreamException {

        return INSTANCE;

    }

}
