/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NavigationOperator.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.MathUtils;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.EpochDays;
import net.time4j.tz.TZID;
import net.time4j.tz.TransitionStrategy;


/**
 * <p>Spezialoperator zum Navigieren zu bestimmten Elementwerten. </p>
 *
 * @param       <V> generic enum type of element values
 * @param       <T> generic target type for a {@code ChronoOperator}
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class NavigationOperator<V extends Enum<V>, T extends ChronoEntity<T>>
    implements ZonalOperator<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<V> element;
    private final OperatorType mode;
    private final V value;
    private final int len;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element     reference element
     * @param   mode        navigation mode
     * @param   value       target value of navigation
     */
    NavigationOperator(
        ChronoElement<V> element,
        OperatorType mode,
        V value
    ) {
        super();

        if (value == null) {
            throw new NullPointerException("Missing value.");
        }

        this.element = element;
        this.mode = mode;
        this.value = value;
        this.len = element.getType().getEnumConstants().length;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public T apply(T entity) {

        if (entity.contains(PlainDate.CALENDAR_DATE)) {
            PlainDate date = entity.get(PlainDate.CALENDAR_DATE);
            int oldOrdinal = date.get(this.element).ordinal();
            int newOrdinal = this.delta(oldOrdinal);

            if (newOrdinal == oldOrdinal) {
                return entity;
            } else {
                return entity.with(
                    PlainDate.CALENDAR_DATE,
                    date.plus(
                        (newOrdinal - oldOrdinal),
                        date.getChronology().getBaseUnit(this.element))
                );
            }
        } else if (
            (entity instanceof Calendrical)
            && this.element.name().equals("LOCAL_DAY_OF_WEEK")
        ) {
            // Spezialmodus für Weekmodel#localDayOfWeek()
            Calendrical<?, ?> date = Calendrical.class.cast(entity);
            Calendrical<?, ?> result =  this.adjustDate(date);

            if (result != null) {
                return entity.getChronology().getChronoType().cast(result);
            }
        }

        String navigation;

        switch (this.mode) {
            case NAV_NEXT:
                navigation = "setToNext";
                break;
            case NAV_PREVIOUS:
                navigation = "setToPrevious";
                break;
            case NAV_NEXT_OR_SAME:
                navigation = "setToNextOrSame";
                break;
            case NAV_PREVIOUS_OR_SAME:
                navigation = "setToPreviousOrSame";
                break;
            default:
                throw new AssertionError("Unknown: " + this.mode);
        }

        throw new ChronoException(
            navigation
            + "()-operation not supported on: "
            + this.element.name());

    }

    @Override
    public ChronoOperator<Moment> inSystemTimezone() {

        return new Moment.Operator(this.onTimestamp(), this.element, this.mode);

    }

    @Override
    public ChronoOperator<Moment> inTimezone(
        TZID tzid,
        TransitionStrategy strategy
    ) {

        return new Moment.Operator(
            this.onTimestamp(),
            tzid,
            strategy,
            this.element,
            this.mode
        );

    }

    // TODO: cachen?
    @Override
    public ChronoOperator<PlainTimestamp> onTimestamp() {

        return new NavigationOperator<V, PlainTimestamp>(
            this.element,
            this.mode,
            this.value
        );

    }

    private int delta(int oldOrdinal) {

        int newOrdinal = this.value.ordinal();

        switch (this.mode) {
            case NAV_NEXT:
                if (newOrdinal <= oldOrdinal) {
                    newOrdinal += this.len;
                }
                break;
            case NAV_PREVIOUS:
                if (newOrdinal >= oldOrdinal) {
                    newOrdinal -= this.len;
                }
                break;
            case NAV_NEXT_OR_SAME:
                if (newOrdinal < oldOrdinal) {
                    newOrdinal += this.len;
                }
                break;
            case NAV_PREVIOUS_OR_SAME:
                if (newOrdinal > oldOrdinal) {
                    newOrdinal -= this.len;
                }
                break;
            default:
                throw new AssertionError("Unknown: " + this.mode);
        }

        return newOrdinal;

    }

    private Calendrical<?, ?> adjustDate(Calendrical<?, ?> date) {

        if (Weekmodel.hasSevenDayWeek(date.getChronology())) {
            long utcDays = date.get(EpochDays.UTC);
            int oldValue = Weekmodel.getDayOfWeek(utcDays).getValue();
            int newValue = this.delta(oldValue);

            if (newValue == oldValue) {
                return date;
            } else {
                return date.with(
                    EpochDays.UTC,
                    MathUtils.safeAdd(utcDays, newValue - oldValue)
                );
            }
        } else {
            return null;
        }

    }

}
