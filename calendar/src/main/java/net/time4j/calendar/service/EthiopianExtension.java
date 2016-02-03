/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EthiopianExtension.java) is part of project Time4J.
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

package net.time4j.calendar.service;

import net.time4j.PlainTime;
import net.time4j.calendar.EthiopianTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


/**
 * <p>&Auml;thiopische Uhrzeit-Erweiterung. </p>
 *
 * @author  Meno Hochschild
 */
public class EthiopianExtension
    implements ChronoExtension {

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean accept(Class<?> chronoType) {

        return false; // never called

    }

    @Override
    public Set<ChronoElement<?>> getElements(
        Locale locale,
        AttributeQuery attributes
    ) {

        Set<ChronoElement<?>> set = new HashSet<ChronoElement<?>>();
        set.add(EthiopianTime.ETHIOPIAN_HOUR);
        return Collections.unmodifiableSet(set);

    }

    @Override
    public <T extends ChronoEntity<T>> T resolve(
        T entity,
        Locale locale,
        AttributeQuery attributes
    ) {

        if (entity.contains(EthiopianTime.ETHIOPIAN_HOUR)) {
            int h = entity.get(EthiopianTime.ETHIOPIAN_HOUR).intValue();
            if (h == 12) {
                h = 0;
            }
            h += 6;
            if (h >= 12) {
                h -= 12;
            }
            entity = entity.with(PlainTime.DIGITAL_HOUR_OF_AMPM, h);
        }

        return entity;

    }

}
