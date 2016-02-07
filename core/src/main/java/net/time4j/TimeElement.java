/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoFunction;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.ObjectStreamException;


/**
 * <p>Repr&auml;sentiert eine Uhrzeitkomponente. </p>
 *
 * @author      Meno Hochschild
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
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return true;

    }

	@Override
	public ElementOperator<?> setToNext(PlainTime v) {

		return new WallTimeOperator(ElementOperator.OP_NAV_NEXT, v);

	}

	@Override
	public ElementOperator<?> setToPrevious(PlainTime v) {

		return new WallTimeOperator(ElementOperator.OP_NAV_PREVIOUS, v);

	}

	@Override
	public ElementOperator<?> setToNextOrSame(PlainTime v) {

		return new WallTimeOperator(ElementOperator.OP_NAV_NEXT_OR_SAME, v);

	}

	@Override
	public ElementOperator<?> setToPreviousOrSame(PlainTime v) {

		return new WallTimeOperator(
            ElementOperator.OP_NAV_PREVIOUS_OR_SAME,
            v);

	}

    @Override
    public ElementOperator<PlainTime> roundedToFullHour() {

        return FullValueOperator.ROUNDING_FULL_HOUR;

    }

    @Override
    public ElementOperator<PlainTime> roundedToFullMinute() {

        return FullValueOperator.ROUNDING_FULL_MINUTE;

    }

    @Override
    public ElementOperator<PlainTime> setToNextFullHour() {

        return FullValueOperator.NEXT_FULL_HOUR;

    }

    @Override
    public ElementOperator<PlainTime> setToNextFullMinute() {

        return FullValueOperator.NEXT_FULL_MINUTE;

    }

    @Override
    public ChronoFunction<Moment, PlainTime> inStdTimezone() {

        return this.in(Timezone.ofSystem());

    }

    @Override
    public ChronoFunction<Moment, PlainTime> inTimezone(TZID tzid) {

        return this.in(Timezone.of(tzid));

    }

    @Override
    public ChronoFunction<Moment, PlainTime> in(Timezone tz) {

        return new ZonalQuery<PlainTime>(this, tz);

    }

    @Override
    public ChronoFunction<Moment, PlainTime> atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    @Override
    public ChronoFunction<Moment, PlainTime> at(ZonalOffset offset) {

        return new ZonalQuery<PlainTime>(this, offset);

    }

    @Override
    protected boolean isSingleton() {

        return true;

    }

    private Object readResolve() throws ObjectStreamException {

        return INSTANCE;

    }

}
