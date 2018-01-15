/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EastAsianME.java) is part of project Time4J.
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
import net.time4j.format.CalendarText;
import net.time4j.format.TextElement;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * Represents a generic element for the East Asian month.
 *
 * @author  Meno Hochschild
 * @since   3.39/4.34
 */
class EastAsianME
    implements TextElement<EastAsianMonth>, Serializable {

    //~ Statische Felder/Initialisierungen ----------------------------

    static final EastAsianME SINGLETON_EA = new EastAsianME();

    private static final long serialVersionUID = -5874268477318061153L;

    //~ Methoden ------------------------------------------------------

    @Override
    public String name() {

        return "MONTH_OF_YEAR";

    }

    @Override
    public Class<EastAsianMonth> getType() {

        return EastAsianMonth.class;

    }

    @Override
    public char getSymbol() {

        return 'M';

    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public EastAsianMonth getDefaultMinimum() {

        return EastAsianMonth.valueOf(1);

    }

    @Override
    public EastAsianMonth getDefaultMaximum() {

        return EastAsianMonth.valueOf(12);

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

        String key = "L_month";
        String lname = CalendarText.getIsoInstance(language).getTextForms().get(key);
        return ((lname == null) ? this.name() : lname);

    }

    /**
     * @serialData  Preserves the singleton semantic
     * @return      singleton instance
     */
    protected Object readResolve() throws ObjectStreamException {

        return SINGLETON_EA;

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException, ChronoException {

        throw new AbstractMethodError("Not yet available.");

    }

    @Override
    public EastAsianMonth parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {

        throw new AbstractMethodError("Not yet available.");

    }

}
