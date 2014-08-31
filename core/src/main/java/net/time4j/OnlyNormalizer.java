/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OnlyNormalizer.java) is part of project Time4J.
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

import net.time4j.engine.Normalizer;
import net.time4j.engine.TimeSpan;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;


/**
 * <p>Hilfsobjekt zur Normalisierung einer Uhrzeit-bezogenen Dauer. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
final class OnlyNormalizer
    implements Normalizer<ClockUnit> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<ClockUnit, OnlyNormalizer> MAP;

    static {
        Map<ClockUnit, OnlyNormalizer> m =
            new EnumMap<ClockUnit, OnlyNormalizer>(ClockUnit.class);
        for (ClockUnit unit : ClockUnit.values()) {
            m.put(unit, new OnlyNormalizer(unit));
        }
        MAP = Collections.unmodifiableMap(m);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final ClockUnit unit;

    //~ Konstruktoren -----------------------------------------------------

    private OnlyNormalizer(ClockUnit unit) {
        super();

        this.unit = unit;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert einen passenden Normalisierer. </p>
     *
     * @param   unit    clock unit
     * @return  unique normalizer instance
     */
    static OnlyNormalizer of(ClockUnit unit) {

        OnlyNormalizer ret = MAP.get(unit);

        if (ret == null) {
            throw new IllegalArgumentException(unit.name());
        }

        return ret;

    }

    @Override
    public Duration<ClockUnit> normalize(TimeSpan<? extends ClockUnit> dur) {

        return Duration.of(this.unit.convert(dur), this.unit);

    }

}
