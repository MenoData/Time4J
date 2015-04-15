/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FixedNumParser.java) is part of project Time4J.
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
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ParseLog;


enum FixedNumParser
    implements ChronoParser<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    BASIC_WEEK_OF_YEAR,

    EXTENDED_WEEK_OF_YEAR,

    CALENDAR_MONTH;

    //~ Methoden ----------------------------------------------------------

    @Override
    public Integer parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        int protectedChars =
            attributes.get(Attributes.PROTECTED_CHARACTERS, 0).intValue();

        if (protectedChars > 0) {
            len -= protectedChars;
        }

        if (pos >= len) {
            status.setError(pos, "Missing component: " + this.name());
            status.setWarning();
            return null;
        }

        if (
            (this == BASIC_WEEK_OF_YEAR)
            || (this == EXTENDED_WEEK_OF_YEAR)
        ) {
            if (text.charAt(pos) == 'W') {
                pos++;
                start++;
                if (pos >= len) {
                    status.setError(pos, "Missing digits for: " + this.name());
                    return null;
                }
            } else {
                status.setError(pos, "Literal 'W' expected.");
                return null;
            }
        }

        char sign = text.charAt(pos);

        if (
            (sign == '-')
            || (sign == '+')
        ) {
            status.setError(pos, "Sign not allowed due to sign policy.");
            return null;
        }

        int minPos = pos + 2;
        int maxPos = Math.min(len, pos + 2);
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

        if (pos < minPos) {
            status.setError(
                start,
                "Not enough digits found for: " + this.name());
            return null;
        }

        if (this != BASIC_WEEK_OF_YEAR) {
            if (pos >= len) {
                status.setError(
                    pos,
                    "Missing literal '-' after: " + this.name());
                return null;
            }

            char sep = text.charAt(pos);

            if (sep != '-') {
                status.setError(
                    pos,
                    "Found "
                        + sep
                        + ", but expected literal '-' after: "
                        + this.name());
                return null;
            } else {
                pos++;
            }
        }

        status.setPosition(pos);
        return Integer.valueOf(total);

    }

}
