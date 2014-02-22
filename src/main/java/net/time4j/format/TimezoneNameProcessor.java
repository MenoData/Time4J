/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimezoneNameProcessor.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.base.UnixTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.tz.TZID;
import net.time4j.tz.TimeZone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Verarbeitet einen Zeitzonen-Namen. </p>
 *
 * @author  Meno Hochschild
 */
final class TimezoneNameProcessor
    implements FormatProcessor<TZID> {

    //~ Instanzvariablen --------------------------------------------------

    private final boolean abbreviated;
    private final Set<TZID> preferredZones;
    private final Set<Locale> preferredTerritories;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   abbreviated     abbreviations to be used?
     * @param   preferredZones  preferred time zone ids for resolving duplicates
     */
    TimezoneNameProcessor(
        boolean abbreviated,
        Set<TZID> preferredZones
    ) {
        super();

        this.abbreviated = abbreviated;
        this.preferredZones = preferredZones;
        this.preferredTerritories = Collections.emptySet();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoEntity<?> formattable,
        Appendable buffer,
        Attributes attributes,
        Set<ElementPosition> positions,
        FormatStep step
    ) throws IOException {

        int start = -1;
        int printed = 0;

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        TZID tzid = formattable.get(TimeZone.identifier());
        String name;

        if (tzid instanceof ZonalOffset) {
            ZonalOffset offset = (ZonalOffset) tzid;
            name = "GMT" + offset.toString(); // TODO: localized GMT format
        } else if (formattable instanceof UnixTime) {
            TimeZone zone = TimeZone.of(tzid);
            UnixTime ut = UnixTime.class.cast(formattable);
            name =
                zone.getDisplayName(
                    zone.isDaylightSaving(ut),
                    this.abbreviated,
                    step.getAttribute(
                        Attributes.LOCALE, attributes, Locale.ROOT));
        } else {
            throw new IllegalArgumentException(
                "Cannot extract time zone name from: " + formattable);
        }

        buffer.append(name);
        printed = name.length();

        if (
            (start != -1)
            && (printed > 0)
            && (positions != null)
        ) {
            positions.add(
                new ElementPosition(
                    ZonalElement.TIMEZONE_ID,
                    start,
                    start + printed));
        }

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        Attributes attributes,
        Map<ChronoElement<?>, Object> parsedResult,
        FormatStep step
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(start, "Missing time zone name.");
            return;
        }


    }

    @Override
    public ChronoElement<TZID> getElement() {

        return ZonalElement.TIMEZONE_ID;

    }

    @Override
    public FormatProcessor<TZID> withElement(ChronoElement<TZID> element) {

        return this;

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    private static ZonalOffset getOffset(
        ChronoEntity<?> formattable,
        FormatStep step,
        Attributes attributes
    ) {

        AttributeQuery aq = step.getQuery(attributes);

        if (aq.contains(Attributes.TIMEZONE_ID)) {
            TZID tzid = aq.get(Attributes.TIMEZONE_ID);

            if (tzid instanceof ZonalOffset) {
                return (ZonalOffset) tzid;
            }
        }

        throw new IllegalArgumentException(
            "Cannot extract time zone offset from format attributes for: "
            + formattable);

    }

}
