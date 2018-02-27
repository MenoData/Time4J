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

import net.time4j.Month;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoException;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.NumberSystem;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.DualFormatElement;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;


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

        Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
        EastAsianMonth eam = context.get(this);

        if (attributes.contains(DualFormatElement.COUNT_OF_PATTERN_SYMBOLS)) { // numeric case
            NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            buffer.append(eam.getDisplayName(loc, numsys, attributes));
        } else {
            TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            TextAccessor ta =
                eam.isLeap()
                    ? CalendarText.getInstance("chinese", loc).getLeapMonths(tw, oc)
                    : CalendarText.getInstance("chinese", loc).getStdMonths(tw, oc);
            buffer.append(ta.print(Month.valueOf(eam.getNumber()))); // uses the iso-month only as index for text forms
        }

    }

    @Override
    public EastAsianMonth parse(
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

        boolean leap = false;
        EastAsianMonth eam;

        if (attributes.contains(DualFormatElement.COUNT_OF_PATTERN_SYMBOLS)) { // numeric case
            Map<String, String> textForms = CalendarText.getInstance("generic", loc).getTextForms();
            NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            int pos = start;
            char zeroDigit = attributes.get(Attributes.ZERO_DIGIT, numsys.getDigits().charAt(0));

            boolean trailing =
                attributes.get(EastAsianMonth.LEAP_MONTH_IS_TRAILING, "R".equals(textForms.get("leap-alignment")));
            char indicator =
                attributes.get(EastAsianMonth.LEAP_MONTH_INDICATOR, textForms.get("leap-indicator").charAt(0));

            if (!trailing && (text.charAt(pos) == indicator)) {
                pos++;
                leap = true;
            }

            if (numsys.isDecimal()) {
                // ignore possible padding
                while ((pos < len) && (text.charAt(pos) == zeroDigit)) {
                    pos++;
                }
            }

            int m = 0;

            for (int num = 12; (num >= 1) && (m == 0); num--) {
                String display = EastAsianMonth.toNumeral(numsys, zeroDigit, num);
                int numlen = display.length();
                for (int i = 0; ; i++) {
                    if ((len > pos + i) && (text.charAt(pos + i) != display.charAt(i))) {
                        break;
                    } else if (i + 1 == numlen) {
                        m = num;
                        pos += numlen;
                        break;
                    }
                }
            }

            if (m == 0) {
                status.setErrorIndex(start);
                return null;
            }

            if (trailing && (len > pos) && (text.charAt(pos) == indicator)) {
                pos++;
                leap = true;
            }

            eam = EastAsianMonth.valueOf(m);
            status.setIndex(pos);
        } else {
            TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            TextAccessor taStd = CalendarText.getInstance("chinese", loc).getStdMonths(tw, oc);

            // uses the iso-month only as index for text forms
            Month m = taStd.parse(text, status, Month.class, attributes);

            if (m == null) {
                status.setErrorIndex(-1);
                status.setIndex(start);
                TextAccessor taLeap = CalendarText.getInstance("chinese", loc).getLeapMonths(tw, oc);
                m = taLeap.parse(text, status, Month.class, attributes);
                if (m != null) {
                    leap = true;
                }
            }

            if (m == null) {
                status.setErrorIndex(start);
                return null;
            }

            eam = EastAsianMonth.valueOf(m.getValue());
        }

        if (leap) {
            eam = eam.withLeap();
        }

        return eam;

    }

}
