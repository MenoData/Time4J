/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RelatedGregorianYearElement.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.base.GregorianMath;
import net.time4j.engine.BasicElement;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;


/**
 * <p>Specific element for the related gregorian year. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
/*[deutsch]
 * <p>Spezialelement f&uuml;r das gregorianische Bezugsjahr. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
abstract class RelatedGregorianYearElement<T extends ChronoEntity<T>>
    extends BasicElement<Integer>
    implements ElementRule<T, Integer> {

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  associated chronological type which has registered this element
     */
    /*[deutsch]
     * @serial  assoziierter chronologischer Typ, der dieses Element registriert hat
     */
    private final Class<T> chrono;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>For subclasses. </p>
     *
     * @param   chrono      chronological type which registers this element
     */
    /*[deutsch]
     * <p>F&uuml;r Subklassen. </p>
     *
     * @param   chrono      chronological type which registers this element
     */
    public RelatedGregorianYearElement(Class<T> chrono) {
        super("RELATED_GREGORIAN_YEAR");

        this.chrono = chrono;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Integer> getType() {

        return Integer.class;

    }

    @Override
    public Integer getDefaultMinimum() {

        return Integer.valueOf(GregorianMath.MIN_YEAR);

    }

    @Override
    public Integer getDefaultMaximum() {

        return Integer.valueOf(GregorianMath.MAX_YEAR);

    }

    @Override
    public char getSymbol() {

        return 'r';

    }

    @Override
    public boolean isDateElement() {

        return true;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    @Override
    public Integer getValue(T context) {

        CalendarSystem<T> calsys = this.getCalendarSystem(context);
        T start = this.firstDayOfYear(context);
        return Integer.valueOf(PlainDate.of(calsys.transform(start), EpochDays.UTC).getYear());

    }

    @Override
    public Integer getMinimum(T context) {

        CalendarSystem<T> calsys = this.getCalendarSystem(context);
        long utc = calsys.getMinimumSinceUTC();
        return this.getValue(calsys.transform(utc));

    }

    @Override
    public Integer getMaximum(T context) {

        CalendarSystem<T> calsys = this.getCalendarSystem(context);
        long utc = calsys.getMaximumSinceUTC();
        return this.getValue(calsys.transform(utc));

    }

    @Override
    public boolean isValid(
        T context,
        Integer value
    ) {

        return this.getValue(context).equals(value);

    }

    @Override
    public T withValue(
        T context,
        Integer value,
        boolean lenient
    ) {

        if (this.isValid(context, value)) {
            return context;
        } else {
            throw new IllegalArgumentException("The related gregorian year is read-only.");
        }

    }

    @Override
    public ChronoElement<?> getChildAtFloor(T context) {

        return null;

    }

    @Override
    public ChronoElement<?> getChildAtCeiling(T context) {

        return null;

    }

    /**
     * <p>Determines the chronology-dependent start of year in given context. </p>
     *
     * @param   context     the calendar date whose begin is to be determined
     * @return  the start of contextual calendar year
     * @since   3.20/4.16
     */
    protected abstract T firstDayOfYear(T context);

    @Override
    protected boolean doEquals(BasicElement<?> obj) {

        RelatedGregorianYearElement<?> that = (RelatedGregorianYearElement<?>) obj;
        return (this.chrono == that.chrono);

    }

    /**
     * @serialData  preserves singleton semantic
     * @return      resolved singleton
     * @throws      ObjectStreamException if resolving fails
     */
    protected Object readResolve() throws ObjectStreamException {

        String comp = this.name();

        for (ChronoElement<?> element : Chronology.lookup(this.chrono).getRegisteredElements()) {
            if (element.name().equals(comp)) {
                return element;
            }
        }

        throw new InvalidObjectException(comp);

    }

    private CalendarSystem<T> getCalendarSystem(T context) {

        Chronology<T> chronology = Chronology.lookup(this.chrono);

        if (Calendrical.class.isAssignableFrom(this.chrono)) {
            return chronology.getCalendarSystem();
        } else {
            String variant = CalendarVariant.class.cast(context).getVariant();
            return chronology.getCalendarSystem(variant);
        }

    }

}
