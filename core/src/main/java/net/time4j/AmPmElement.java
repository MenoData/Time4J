/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AmPmElement.java) is part of project Time4J.
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoFunction;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.GregorianTextElement;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * <p>Repr&auml;sentiert das Halbtagselement. </p>
 *
 * @author      Meno Hochschild
 */
enum AmPmElement
    implements ZonalElement<Meridiem>, GregorianTextElement<Meridiem> {

    //~ Statische Felder/Initialisierungen --------------------------------

    AM_PM_OF_DAY;

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Meridiem> getType() {

        return Meridiem.class;

    }

    @Override
    public char getSymbol() {

        return 'a';

    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public Meridiem getDefaultMinimum() {

        return Meridiem.AM;

    }

    @Override
    public Meridiem getDefaultMaximum() {

        return Meridiem.PM;

    }

    @Override
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return true;

    }

    @Override
    public boolean isLenient() {

        return false;

    }

    @Override
    public ChronoFunction<Moment, Meridiem> inStdTimezone() {

        return this.in(Timezone.ofSystem());

    }

    @Override
    public ChronoFunction<Moment, Meridiem> inTimezone(TZID tzid) {

        return this.in(Timezone.of(tzid));

    }

    @Override
    public ChronoFunction<Moment, Meridiem> in(Timezone tz) {

        return new ZonalQuery<Meridiem>(this, tz);

    }

    @Override
    public ChronoFunction<Moment, Meridiem> atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    @Override
    public ChronoFunction<Moment, Meridiem> at(ZonalOffset offset) {

        return new ZonalQuery<Meridiem>(this, offset);

    }

    @Override
    public String getDisplayName(Locale language) {

        String lname = CalendarText.getIsoInstance(language).getTextForms().get("L_dayperiod");
        return ((lname == null) ? this.name() : lname);

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException {

        buffer.append(this.accessor(attributes).print(context.get(this)));

    }

    @Override
    public Meridiem parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {

        Meridiem m = parseAmPm(text, status);

        if (m == null) {
            m = this.accessor(attributes).parse(text, status, this.getType(), attributes);
        }

        return m;

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        Locale language,
        TextWidth tw,
        OutputContext oc
    ) throws IOException, ChronoException {

        buffer.append(this.accessor(language, tw, oc).print(context.get(this)));

    }

    @Override
    public Meridiem parse(
        CharSequence text,
        ParsePosition status,
        Locale language,
        TextWidth tw,
        OutputContext oc,
        Leniency leniency
    ) {

        Meridiem m = parseAmPm(text, status);

        if (m == null) {
            m = this.accessor(language, tw, oc).parse(text, status, this.getType(), leniency);
        }

        return m;

    }

    private TextAccessor accessor(AttributeQuery attributes) {

        CalendarText cnames = CalendarText.getIsoInstance(attributes.get(Attributes.LANGUAGE, Locale.ROOT));
        TextWidth textWidth = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
        OutputContext outputContext = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
        return cnames.getMeridiems(textWidth, outputContext);

    }

    private TextAccessor accessor(
        Locale language,
        TextWidth textWidth,
        OutputContext outputContext
    ) {

        return CalendarText.getIsoInstance(language).getMeridiems(textWidth, outputContext);

    }

    static Meridiem parseAmPm(
        CharSequence text,
        ParsePosition pp
    ) {

        int offset = pp.getIndex();

        if (text.length() >= offset + 2) {
            char c2 = text.charAt(offset + 1);
            if (c2 == 'M' || c2 == 'm') {
                char c1 = text.charAt(offset);
                if (c1 == 'A' || c1 == 'a') {
                    pp.setIndex(offset + 2);
                    return Meridiem.AM;
                } else if (c1 == 'P' || c1 == 'p') {
                    pp.setIndex(offset + 2);
                    return Meridiem.PM;
                }
            }
        }

        return null;

    }

}
