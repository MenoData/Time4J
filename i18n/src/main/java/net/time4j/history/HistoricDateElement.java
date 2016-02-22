/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricalDateElement.java) is part of project Time4J.
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

package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;

import java.io.ObjectStreamException;


/**
 * <p>Element f&uuml;r ein historisches Datumstupel. </p>
 *
 * @author  Meno Hochschild
 * @since   3.15/4.12
 */
final class HistoricalDateElement
    extends BasicElement<HistoricDate> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int YMAX = 999999999;
    private static final long serialVersionUID = -5386613740709845550L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  associated chronological history
     */
    private final ChronoHistory history;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert ein neues Element mit den angegebenen Details. </p>
     *
     * @param   history     associated chronological history
     */
    HistoricalDateElement(ChronoHistory history) {
        super("HISTORIC_DATE");

        this.history = history;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<HistoricDate> getType() {

        return HistoricDate.class;

    }

    @Override
    public HistoricDate getDefaultMinimum() {

        return HistoricDate.of(HistoricEra.BC, YMAX, 1, 1);

    }

    @Override
    public HistoricDate getDefaultMaximum() {

        return HistoricDate.of(HistoricEra.AD, YMAX, 12, 31);

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
    protected <T extends ChronoEntity<T>> ElementRule<T, HistoricDate> derive(Chronology<T> chronology) {

        if (chronology.isRegistered(PlainDate.COMPONENT)) {
            return new Rule<T>(this.history);
        }

        return null;

    }

    @Override
    protected boolean doEquals(BasicElement<?> obj) {

        return this.history.equals(((HistoricalDateElement) obj).history);

    }

    private Object readResolve() throws ObjectStreamException {

        return this.history.date();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Rule<C extends ChronoEntity<C>>
        implements ElementRule<C, HistoricDate> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoHistory history;

        //~ Konstruktoren -------------------------------------------------

        Rule(ChronoHistory history) {
            super();

            this.history = history;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HistoricDate getValue(C context) {

            try {
                return this.history.convert(context.get(PlainDate.COMPONENT));
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public HistoricDate getMinimum(C context) {

            try {
                return this.history.convert(PlainDate.axis().getMinimum());
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public HistoricDate getMaximum(C context) {

            try {
                return this.history.convert(PlainDate.axis().getMaximum());
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public boolean isValid(
            C context,
            HistoricDate value
        ) {

            return this.history.isValid(value);

        }

        @Override
        public C withValue(
            C context,
            HistoricDate value,
            boolean lenient
        ) {

            if (value != null) {
                PlainDate date = this.history.convert(value);
                return context.with(PlainDate.COMPONENT, date);
            }

            throw new IllegalArgumentException("Missing historic date.");

        }

        @Override
        public ChronoElement<?> getChildAtFloor(C context) {

            throw new UnsupportedOperationException("Never called.");

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(C context) {

            throw new UnsupportedOperationException("Never called.");

        }

    }

}
