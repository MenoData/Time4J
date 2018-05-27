/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EastAsianCY.java) is part of project Time4J.
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoException;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.format.TextElement;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * Represents a generic element for the cyclic year.
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
class EastAsianCY
    implements TextElement<CyclicYear>, Serializable {

    //~ Statische Felder/Initialisierungen ----------------------------

    static final EastAsianCY SINGLETON = new EastAsianCY();

    private static final long serialVersionUID = -4211396220263977858L;

    //~ Methoden ------------------------------------------------------

    @Override
    public String name() {

        return "CYCLIC_YEAR";

    }

    @Override
    public Class<CyclicYear> getType() {

        return CyclicYear.class;

    }

    @Override
    public char getSymbol() {

        return 'U';

    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public CyclicYear getDefaultMinimum() {

        return CyclicYear.of(1);

    }

    @Override
    public CyclicYear getDefaultMaximum() {

        return CyclicYear.of(60);

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
    public boolean isLenient() {

        return false;

    }

    @Override
    public String getDisplayName(Locale language) {

        String key = "L_year";
        String lname = CalendarText.getIsoInstance(language).getTextForms().get(key);
        return ((lname == null) ? this.name() : lname);

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException, ChronoException {

        Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
        buffer.append(context.get(this).getDisplayName(locale));

    }

    @Override
    public CyclicYear parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {

        Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
        boolean lenient = !attributes.get(Attributes.LENIENCY, Leniency.SMART).isStrict();
        return CyclicYear.parse(text, status, locale, lenient);

    }

    /**
     * @serialData  Preserves the singleton semantic
     * @return      singleton instance
     */
    protected Object readResolve() throws ObjectStreamException {

        return SINGLETON;

    }

}
