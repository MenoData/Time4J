/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (YearParser.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.engine.AttributeQuery;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ParseLog;


enum YearParser
    implements ChronoParser<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    YEAR,

    YEAR_OF_WEEKDATE;

    //~ Methoden ----------------------------------------------------------

    @Override
    public Integer parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes
    ) {

        Leniency leniency = attributes.get(Attributes.LENIENCY, Leniency.SMART);

        int effectiveMin = 1;
        int effectiveMax = 9;

        if (!leniency.isLax()) {
            effectiveMin = 4;
        }

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        int protectedChars =
            attributes.get(Attributes.PROTECTED_CHARACTERS, 0).intValue();

        if (protectedChars > 0) {
            len -= protectedChars;
        }

        if (pos >= len) {
            status.setError(start, "Missing digits for: " + this.name());
            status.setWarning();
            return null;
        }

        boolean negative = false;
        char sign = text.charAt(pos);

        if (
            (sign == '-')
            || (sign == '+')
        ) {
            negative = (sign == '-');
            pos++;
            start++;
        }

        if (pos >= len) {
            status.setError(start, "Missing digits for: " + this.name());
            return null;
        }

        int minPos = pos + effectiveMin;
        int maxPos = Math.min(len, pos + effectiveMax);
        int total = 0;
        boolean first = true;

        while (pos < maxPos) {
            int digit = text.charAt(pos) - '0';

            if ((digit >= 0) && (digit <= 9)) {
                total = total * 10 + digit;
                pos++;
                first = false;
            } else if (first) {
                status.setError(start, "Digit expected.");
                return null;
            } else {
                break;
            }
        }

        if (
            (pos < minPos)
            && (first || !leniency.isLax())
        ) {
            status.setError(
                start,
                "Not enough digits found for " + this.name());
            return null;
        }

        if (negative) {
            if (
                (total == 0)
                && leniency.isStrict()
            ) {
                status.setError(start - 1, "Negative zero is not allowed.");
                return null;
            }

            total = -total;
        } else if (leniency.isStrict()) {
            if (
                (sign == '+')
                && (pos <= minPos)
            ) {
                status.setError(
                    start - 1,
                    "Positive sign only allowed for big number.");
                return null;
            } else if (
                (sign != '+')
                && (pos > minPos)
            ) {
                status.setError(
                    start,
                    "Positive sign must be present for big number.");
                return null;
            }
        }

        if (pos >= len) {
            status.setError(pos, "Missing literal '-' after: " + this.name());
            return null;
        } else if (text.charAt(pos) != '-') {
            status.setError(pos, "Literal '-' expected after: " + this.name());
            return null;
        } else {
            pos++;
        }

        status.setPosition(pos);
        return Integer.valueOf(total);

    }

}
