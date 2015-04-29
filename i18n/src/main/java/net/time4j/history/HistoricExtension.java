/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricExtension.java) is part of project Time4J.
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
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoExtension;

import java.util.Locale;
import java.util.Set;


/**
 * <p>Defines a historic extension of {@code PlainDate}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 * @doctags.exclude
 */
/*[deutsch]
 * <p>Definiert eine historische Erweiterung von {@code PlainDate}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 * @doctags.exclude
 */
public class HistoricExtension
    implements ChronoExtension {

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean accept(Class<?> chronoType) {

        return (chronoType == PlainDate.class);

    }

    @Override
    public Set<ChronoElement<?>> getElements(
        Locale locale,
        AttributeQuery attributes
    ) {

        return getHistory(locale, attributes).getElements();

    }

    @Override
    public <T extends ChronoEntity<T>> T resolve(
        T entity,
        Locale locale,
        AttributeQuery attributes
    ) {

        ChronoHistory history = getHistory(locale, attributes);

        if (
            entity.contains(history.era())
            && entity.contains(history.yearOfEra())
            && entity.contains(history.month())
            && entity.contains(history.dayOfMonth())
        ) {
            HistoricEra era = entity.get(history.era());
            int yearOfEra = entity.get(history.yearOfEra());
            int month = entity.get(history.month());
            int dayOfMonth = entity.get(history.dayOfMonth());
            HistoricDate hd = HistoricDate.of(era, yearOfEra, month, dayOfMonth);
            PlainDate date = history.convert(hd);
            entity.with(history.era(), null);
            entity.with(history.yearOfEra(), null);
            entity.with(history.month(), null);
            entity.with(history.dayOfMonth(), null);
            return entity.with(PlainDate.COMPONENT, date);
        }

        return entity;

    }

    private static ChronoHistory getHistory(
        Locale locale,
        AttributeQuery attributes
    ) {

        return ChronoHistory.of(locale);

    }

}
