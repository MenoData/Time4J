/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BridgeChronology.java) is part of project Time4J.
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

import net.time4j.base.TimeSource;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * <p>Represents a foreign chronology which delegates formatting and parsing to a chronology in Time4J. </p>
 *
 * <p>A {@code ChronoFormatter} can be created by help of this chronology such that the formatter can be
 * adjusted to any foreign type without external conversion. </p>
 *
 * <p>Important to know: This chronology does not register any elements and is also not registered itself
 * so {@code Chronology.lookup(...)} will inevitably fail to find this chronology. </p>
 *
 * @param   <S> generic type of foreign temporal type
 * @param   <T> generic type compatible to {@link ChronoEntity}
 * @author  Meno Hochschild
 * @see     net.time4j.TemporalType
 * @since   3.24/4.20
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein fremdes chronologisches System, das alle formatbezogenen Aufgaben an
 * eine Time4J-Chronologie delegieren kann. </p>
 *
 * <p>Mit Hilfe dieser Chronologie kann ein {@code ChronoFormatter} so erzeugt werden, da&szlig; er direkt
 * an einen fremden Zeittyp ohne externe Konversion angepasst werden kann. </p>
 *
 * <p>Wichtig zu wissen: Diese Chronologie registriert keine chronologischen Elemente und ist selbst auch nicht
 * registriert, so da&szlig; die statische Methode {@code Chronology.lookup(...)} diese Chronologie niemals
 * finden kann. </p>
 *
 * @param   <S> generic type of foreign temporal type
 * @param   <T> generic type compatible to {@link ChronoEntity}
 * @author  Meno Hochschild
 * @see     net.time4j.TemporalType
 * @since   3.24/4.20
 */
public final class BridgeChronology<S, T extends ChronoEntity<T>>
    extends Chronology<S> {

    //~ Instanzvariablen --------------------------------------------------

    private final Converter<S, T> converter;
    private final Chronology<T> delegate;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Constructs a new instance. </p>
     *
     * @param   converter       used in any type conversion, should be stateless
     * @param   delegate        delegate chronology in Time4J
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   converter       used in any type conversion, should be stateless
     * @param   delegate        delegate chronology in Time4J
     */
    public BridgeChronology(
        Converter<S, T> converter,
        Chronology<T> delegate
    ) {
        super(converter.getSourceType());

        if (!ChronoEntity.class.isAssignableFrom(delegate.getChronoType())) {
            throw new IllegalArgumentException("Target chronology not compatible with ChronoEntity.");
        }

        this.converter = converter;
        this.delegate = delegate;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public S createFrom(
        TimeSource<?> clock,
        AttributeQuery attributes
    ) {

        T temporal = this.delegate.createFrom(clock, attributes);
        return ((temporal == null) ? null : this.converter.from(temporal));

    }

    @Override
    @Deprecated
    public S createFrom(
        ChronoEntity<?> entity,
        AttributeQuery attributes,
        boolean preparsing
    ) {

        T temporal = this.delegate.createFrom(entity, attributes, preparsing);
        return ((temporal == null) ? null : this.converter.from(temporal));

    }

    @Override
    public S createFrom(
        ChronoEntity<?> entity,
        AttributeQuery attributes,
        boolean lenient,
        boolean preparsing
    ) {

        T temporal;

        if (this.delegate.getChronoType().isInstance(entity)) {
            temporal = this.delegate.getChronoType().cast(entity);
        } else {
            temporal = this.delegate.createFrom(entity, attributes, lenient, preparsing);
        }

        return ((temporal == null) ? null : this.converter.from(temporal));

    }

    @Override
    public ChronoDisplay preformat(
        S context,
        AttributeQuery attributes
    ) {

        T temporal = this.converter.translate(context);
        return this.delegate.preformat(temporal, attributes);

    }

    @Override
    public Chronology<?> preparser() {

        return this.delegate;

    }

    /**
     * <p>Not supported for foreign types. </p>
     *
     * @param   style   format style
     * @param   locale  language and country setting
     * @return  nothing
     * @throws  UnsupportedOperationException always
     */
    /*[deutsch]
     * <p>F&uuml;r Fremdtypen nicht unterst&uuml;tzt. </p>
     *
     * @param   style   format style
     * @param   locale  language and country setting
     * @return  nothing
     * @throws  UnsupportedOperationException always
     */
    @Override
    public String getFormatPattern(
        DisplayStyle style,
        Locale locale
    ) {

        throw new UnsupportedOperationException("Localized format patterns are not available for foreign types.");

    }

    @Override
    public StartOfDay getDefaultStartOfDay() {

        return this.delegate.getDefaultStartOfDay();

    }

    @Override
    public boolean hasCalendarSystem() {

        return this.delegate.hasCalendarSystem();

    }

    @Override
    public CalendarSystem<S> getCalendarSystem() {

        CalendarSystem<T> calsys = this.delegate.getCalendarSystem();
        return new CalendarSystemProxy<S, T>(this.converter, calsys);

    }

    @Override
    public CalendarSystem<S> getCalendarSystem(String variant) {

        CalendarSystem<T> calsys = this.delegate.getCalendarSystem(variant);
        return new CalendarSystemProxy<S, T>(this.converter, calsys);

    }

    /**
     * <p>This chronology does not itself support any elements. </p>
     *
     * <p>Only its delegate chronology might support given element. </p>
     *
     * @param   element     element to be asked (optional)
     * @return  {@code false}
     */
    /*[deutsch]
     * <p>Diese Chronologie unterst&uuml;tzt selber kein chronologisches Element. </p>
     *
     * <p>Eventuell wird das angegebene Element von der zugrundeliegenden Delegationschronologie unterst&uuml;tzt. </p>
     *
     * @param   element     element to be asked (optional)
     * @return  {@code false}
     */
    @Override
    public boolean isSupported(ChronoElement<?> element) {

        return false;

    }

    @Override
    public Set<ChronoElement<?>> getRegisteredElements() {

        return Collections.emptySet();

    }

    @Override
    public List<ChronoExtension> getExtensions() {

        return Collections.emptyList();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class CalendarSystemProxy<S, T>
        implements CalendarSystem<S> {

        //~ Instanzvariablen ----------------------------------------------

        private final Converter<S, T> converter;
        private final CalendarSystem<T> calsys;

        //~ Konstruktoren -------------------------------------------------

        CalendarSystemProxy(
            Converter<S, T> converter,
            CalendarSystem<T> calsys
        ) {
            super();

            this.converter = converter;
            this.calsys = calsys;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public S transform(long utcDays) {
            return this.converter.from(this.calsys.transform(utcDays));
        }

        @Override
        public long transform(S date) {
            return this.calsys.transform(this.converter.translate(date));
        }

        @Override
        public long getMinimumSinceUTC() {
            return this.calsys.getMinimumSinceUTC();
        }

        @Override
        public long getMaximumSinceUTC() {
            return this.calsys.getMaximumSinceUTC();
        }

        @Override
        public List<CalendarEra> getEras() {
            return this.calsys.getEras();
        }

    }

}
