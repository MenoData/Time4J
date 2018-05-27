/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EastAsianST.java) is part of project Time4J.
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

import net.time4j.Moment;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ElementRule;
import net.time4j.format.Attributes;
import net.time4j.format.TextElement;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * Represents a generic element for the solar term of East Asian lunisolar calendars.
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
class EastAsianST<D extends EastAsianCalendar<?, D>>
    implements TextElement<SolarTerm>, ElementRule<D, SolarTerm>, Serializable {

    //~ Statische Felder/Initialisierungen ----------------------------

    private static final EastAsianST SINGLETON = new EastAsianST();

    private static final long serialVersionUID = 4572549754637955194L;

    //~ Methoden ------------------------------------------------------

    @SuppressWarnings("unchecked")
    static <D extends EastAsianCalendar<?, D>> EastAsianST<D> getInstance() {
        return SINGLETON;
    }

    @Override
    public String name() {
        return "SOLAR_TERM";
    }

    @Override
    public Class<SolarTerm> getType() {
        return SolarTerm.class;
    }

    @Override
    public char getSymbol() {
        return '\u0000';
    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {
        return o1.get(this).compareTo(o2.get(this));
    }

    @Override
    public SolarTerm getDefaultMinimum() {
        return SolarTerm.MINOR_01_LICHUN_315;
    }

    @Override
    public SolarTerm getDefaultMaximum() {
        return SolarTerm.MAJOR_12_DAHAN_300;
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
    public String getDisplayName(Locale locale) {
        String lang = locale.getLanguage();

        if (lang.equals("zh")) {
            return (locale.getCountry().equals("TW") || locale.getScript().equals("Hant")) ? "節氣" : "节气";
        } else if (lang.equals("ko")) {
            return "절기";
        } else if (lang.equals("vi")) {
            return "tiết khí";
        } else if (lang.equals("ja")) {
            return "節気";
        } else if (lang.isEmpty()) {
            return "jieqi";
        } else {
            return "jiéqì"; // pinyin
        }
    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException, ChronoException {
        Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
        SolarTerm st = context.get(this);
        buffer.append(st.getDisplayName(loc));
    }

    @Override
    public SolarTerm parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {
        Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
        int len = text.length();
        int start = status.getIndex();

        if (start >= len) {
            status.setErrorIndex(len);
            return null;
        }

        return SolarTerm.parse(text, loc, status);
    }

    @Override
    public SolarTerm getValue(D context) {
        Moment endOfDay = context.getCalendarSystem().midnight(context.getDaysSinceEpochUTC() + 1);
        return SolarTerm.of(endOfDay);
    }

    @Override
    public SolarTerm getMinimum(D context) {
        EastAsianCS<D> calsys = context.getCalendarSystem();
        long first = calsys.newYear(context.getCycle(), context.getYear().getNumber());
        return SolarTerm.of(calsys.midnight(first + 1));
    }

    @Override
    public SolarTerm getMaximum(D context) {
        EastAsianCS<D> calsys = context.getCalendarSystem();
        long first = calsys.newYear(context.getCycle(), context.getYear().getNumber());
        return SolarTerm.of(calsys.midnight(first + context.lengthOfYear()));
    }

    @Override
    public boolean isValid(
        D context,
        SolarTerm value
    ) {
        return (value != null);
    }

    @Override
    public D withValue(
        D context,
        SolarTerm value,
        boolean lenient
    ) {
        if (value == null) {
            throw new IllegalArgumentException("Missing solar term.");
        } else {
            long newYear = context.getCalendarSystem().newYear(context.getCycle(), context.getYear().getNumber());
            return value.onOrAfter(context.minus(CalendarDays.of(context.getDaysSinceEpochUTC() - newYear)));
        }
    }

    @Override
    public ChronoElement<?> getChildAtFloor(D context) {
        throw new AbstractMethodError(); // never called
    }

    @Override
    public ChronoElement<?> getChildAtCeiling(D context) {
        throw new AbstractMethodError(); // never called
    }

    /**
     * @serialData  Preserves the singleton semantic
     * @return      singleton instance
     */
    protected Object readResolve() throws ObjectStreamException {
        return SINGLETON;
    }

}
