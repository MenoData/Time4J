/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (KoreanExtension.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.calendar.KoreanCalendar;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoExtension;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;


/**
 * <p>Koreanische Datumserweiterung. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
public class KoreanExtension
    implements ChronoExtension {

    //~ Methoden ------------------------------------------------------

    @Override
    public boolean accept(Class<?> chronoType) {

        return (chronoType == PlainDate.class);

    }

    @Override
    public Set<ChronoElement<?>> getElements(
        Locale locale,
        AttributeQuery attributes
    ) {

        return Collections.emptySet();

    }

    @Override
    public ChronoEntity<?> resolve(
        ChronoEntity<?> entity,
        Locale locale,
        AttributeQuery attributes
    ) {

        if (entity.contains(KoreanCalendar.YEAR_OF_ERA)) {
            int dangi = entity.getInt(KoreanCalendar.YEAR_OF_ERA);
            entity = entity.with(PlainDate.YEAR, dangi - 2333);
        }

        return entity;

    }

    @Override
    public boolean canResolve(ChronoElement<?> element) {

        return (element == KoreanCalendar.YEAR_OF_ERA);

    }

}
