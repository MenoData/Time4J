/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Platform.java) is part of project Time4J.
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

import net.time4j.format.ChronoPattern;
import net.time4j.format.FormatEngine;


/**
 * <p>Defines the default format pattern of the actual platform. </p>
 *
 * <p>This pattern type is applicable on the four basic types of Time4J, namely
 * {@code PlainDate}, {@code PlainTime}, {@code PlainTimestamp} and {@code Moment}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Definiert das Standard-Formatmuster der aktuellen Plattform. </p>
 *
 * <p>Dieser Mustertyp ist auf die vier Basistypen von Time4J anwendbar, n&auml;mlich
 * {@code PlainDate}, {@code PlainTime}, {@code PlainTimestamp} und {@code Moment}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
public enum Platform
    implements ChronoPattern<Platform> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>The details of the platform pattern syntax are platform-dependent and will
     * follow the syntax described in the Java-6-version of {@code SimpleDateFormat}. </p>
     *
     * <p>Note that this configuration will not use the best available format engine. A
     * counter example are ISO-weekdates which are not supported in parsing in order to
     * preserve compatibility with Java-6. The performance cannot be the best, too. For
     * higher quality requirements users are strongly advised to use the alternative
     * {@code net.time4j.format.expert.PatternType} in i18n-module. </p>
     *
     * <p>Timezone conversions always rely on the best available timezone data
     * which are not necessarily those of the JDK. This is even true in context
     * of using {@code SimpleDateFormat}. </p>
     */
    /*[deutsch]
     * <p>Die Details der Plattform-Formatmuster-Syntax sind naturgem&auml;&szlig;
     * abh&auml;ngig von der aktuellen Plattform, folgen aber im wesentlichen der
     * Syntax beschrieben in der Java-6-Version von {@code SimpleDateFormat}. </p>
     *
     * <p>Hinweis: Diese Konfiguration wird nicht die bestm&ouml;gliche {@code FormatEngine}
     * nutzen. Zum Beispiel wird das ISO-Wochendatumsformat beim Parsen nicht unterst&uuml;tzt,
     * weil sonst die Kompatibilit&auml;t zu Java 6 nicht gegeben w&auml;re. Die Performance
     * ist auch nicht die beste. F&uuml;r h&ouml;here Anspr&uuml;che sollten Anwender dringend
     * {@code net.time4j.format.expert.PatternType} im i18n-Modul nutzen. </p>
     *
     * <p>Zeitzonenkonversionen beruhen immer auf den besten verf&uuml;gbaren
     * Zeitzonendaten, also nicht notwendig denen des JDK. Das gilt selbst dann,
     * wenn die interne {@code FormatEngine} auf {@code SimpleDateFormat} basiert. </p>
     */
    PATTERN;

    //~ Methoden ----------------------------------------------------------

    @Override
    public FormatEngine<Platform> getFormatEngine() {

        return SystemFormatEngine.INSTANCE;

    }

}
