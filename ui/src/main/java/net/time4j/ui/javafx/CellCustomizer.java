/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CellCustomizer.java) is part of project Time4J.
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

package net.time4j.ui.javafx;

import javafx.scene.Node;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.CalendarDate;
import java.util.Optional;


/**
 * <p>Enables customizations of date cells in the month view. </p>
 *
 * <p>Example for disabling and painting (localized) weekend columns in another color: </p>
 *
 * <pre>
 *     CalendarPicker&lt;PlainDate&gt; picker = CalendarPicker.gregorianWithSystemDefaults();
 *
 *     picker.cellCustomizerProperty().set(
 *          (cell, column, row, model, date) -&gt; {
 *              if (CellCustomizer.isWeekend(column, model)) {
 *                  cell.setStyle("-fx-background-color: #FFE0E0;");
 *                  cell.setDisable(true);
 *              }
 *          }
 *     );
 * </pre>
 *
 * @param   <T> denotes the calendar system to be used
 * @author  Meno Hochschild
 * @since   4.20
 */
/*[deutsch]
 * <p>Erm&ouml;glicht benutzerdefinierte Anpassungen von Datumszellen in der Monatssicht. </p>
 *
 * <p>Beispiel zur Deaktivierung und Darstellung der (lokalisierten) Wochenendspalten in einer anderen Farbe: </p>
 *
 * <pre>
 *     CalendarPicker&lt;PlainDate&gt; picker = CalendarPicker.gregorianWithSystemDefaults();
 *
 *     picker.cellCustomizerProperty().set(
 *          (cell, column, row, model, date) -&gt; {
 *              if (CellCustomizer.isWeekend(column, model)) {
 *                  cell.setStyle("-fx-background-color: #FFE0E0;");
 *                  cell.setDisable(true);
 *              }
 *          }
 *     );
 * </pre>
 *
 * @param   <T> denotes the calendar system to be used
 * @author  Meno Hochschild
 * @since   4.20
 */
@FunctionalInterface
public interface CellCustomizer<T extends CalendarDate> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Performs any user-defined customization of given date cell. </p>
     *
     * @param   cell    the cell which contains a calendar date
     * @param   column  zero-based column index
     * @param   row     zero-based row index
     * @param   model   localized week model
     * @param   date    associated calendar date (not present if out of min-max-range)
     */
    /*[deutsch]
     * <p>F&uuml;hrt eine beliebige Anpassung der angegebenen Datumszelle aus. </p>
     *
     * @param   cell    the cell which contains a calendar date
     * @param   column  zero-based column index
     * @param   row     zero-based row index
     * @param   model   localized week model
     * @param   date    associated calendar date (not present if out of min-max-range)
     */
    void customize(
        Node cell,
        int column,
        int row,
        Weekmodel model,
        Optional<T> date
    );

    /**
     * <p>Convenient method to determine the localized weekend. </p>
     *
     * <p>Note that the weekend does not always match Saturday and Sunday dependent on current locale
     * and calendar system. </p>
     *
     * @param   column  zero-based column index
     * @param   model   localized week model
     * @return  {@code true} if given column matches a weekend else {@code false}
     */
    /*[deutsch]
     * <p>Bequeme Methode zur Bestimmung eines (lokalisierten) Wochenendes. </p>
     *
     * <p>Zu beachten: Das Wochenende f&auml;llt in manchen Regionen nicht immer auf Samstag und Sonntag.
     * Es ist au&szlig;dem vom Kalendersystem abh&auml;ngig. </p>
     *
     * @param   column  zero-based column index
     * @param   model   localized week model
     * @return  {@code true} if given column matches a weekend else {@code false}
     */
    static boolean isWeekend(
        int column,
        Weekmodel model
    ) {
        Weekday day = getDayOfWeek(column, model);
        return (day == model.getStartOfWeekend() || day == model.getEndOfWeekend());
    }

    /**
     * <p>Convenient method to determine the day-of-week in given column of calendar. </p>
     *
     * <p>Note that the order of weekdays depends on current locale and calendar system.
     * This method works even for empty calendar cells. </p>
     *
     * @param   column  zero-based column index
     * @param   model   localized week model
     * @return  associated day of week
     */
    /*[deutsch]
     * <p>Bequeme Methode zur Bestimmung des Wochentags in der angegebenen Kalenderspalte. </p>
     *
     * <p>Zu beachten: Die Reihenfolge der Spalten h&auml;ngt von der Region und dem Kalendersystem ab.
     * Diese Methode funktioniert sogar dann, wenn die aktuelle Zelle im Kalender leer ist. </p>
     *
     * @param   column  zero-based column index
     * @param   model   localized week model
     * @return  associated day of week
     */
    static Weekday getDayOfWeek(
        int column,
        Weekmodel model
    ) {
        return Weekday.valueOf(column + 1, model);
    }

}
