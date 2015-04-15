/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarType.java) is part of project Time4J.
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

package net.time4j.format;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>This {@code Annotation} can be used to mark all types of {@code ChronoEntity}
 * which need formatted representations and access to text resources dependent on
 * a calendar system. </p>
 *
 * @author  Meno Hochschild
 * @see     net.time4j.engine.ChronoEntity
 */
/*[deutsch]
 * <p>Mit dieser {@code Annotation} k&ouml;nnen alle {@code ChronoEntity}-Typen
 * ausgezeichnet werden, die formatierte Darstellungen erlauben und einen
 * Zugang zu chronologischen Textressourcen brauchen, die von einem
 * Kalendersystem abh&auml;ngig sind. </p>
 *
 * @author  Meno Hochschild
 * @see     net.time4j.engine.ChronoEntity
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CalendarType {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines the reference name for all text resources of a
     * calendar system. </p>
     *
     * <p>The name must not be unique, but has to be in agreement to the
     * conventions defined in CLDR if it exists there. The motivation of
     * the name is a localized access to calendar specific texts of eras,
     * months, quarters, weekdays and AM/PM. The CLDR file
     * <a href="http://unicode.org/repos/cldr/trunk/common/bcp47/calendar.xml"
     * target="_blank">calendar.xml</a> defines both names and aliases.
     * Alias names should be preferred in most cases. ISO systems usually
     * use the name &quot;iso8601&quot;. </p>
     *
     * @return  reference name for calendar specific text resources (usually
     *          a name according to CLDR standard)
     */
    /*[deutsch]
     * <p>Gibt einen Bezugsnamen f&uuml;r die Textressourcen eines
     * chronologischen Systems an. </p>
     *
     * <p>Der Name mu&szlig; nicht eindeutig sein, aber den CLDR-Konventionen
     * gen&uuml;gen, falls in CLDR definiert. Sinn und Zweck des Namens ist
     * der Zugang zu kalendertypischen lokalisierten Bezeichnungen von
     * &Auml;ras, Monaten, Quartalen, Wochentagen und AM/PM. Die CLDR-Datei
     * <a href="http://unicode.org/repos/cldr/trunk/common/bcp47/calendar.xml"
     * target="_blank">calendar.xml</a> definiert Namen wie auch Alias-Namen.
     * Letzteren ist der Vorzug zu geben. ISO-Systeme verwenden in der Regel
     * den Namen &quot;iso8601&quot;. </p>
     *
     * @return  reference name for calendar specific text resources (usually
     *          a name according to CLDR standard)
     */
    String value();

}
