/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MultiFormatParser.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoEntity;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;


/**
 * <p>Serves for parsing of text input whose format is not yet known at compile time. </p>
 *
 * <p>User who only need to parse different formats for one locale only might consider the
 * simple alternative to concatenate all format pattern strings into one pattern with
 * the &quot;|&quot;-symbol as separator. </p>
 *
 * <p>Note: This class is immutable and can be used by multiple threads in parallel. </p>
 *
 * @param   <T> generic type of chronological entity
 * @author  Meno Hochschild
 * @since   3.14/4.11
 */
/*[deutsch]
 * <p>Dient der Interpretation von Texteingaben, deren Format zur Kompilierzeit noch unbekannt ist. </p>
 *
 * <p>Anwender, die nur f&uuml;r eine Sprache verschiedene Formate interpretieren m&uuml;ssen, k&ouml;nnen
 * als Alternative auch das Zusammenziehen von Formatmustern in ein einziges Formatmuster in Betracht
 * ziehen, indem die einzelnen Formatmuster mit dem &quot;|&quot;-Symbol getrennt werden. </p>
 *
 * <p>Hinweis: Diese Klasse ist <i>immutable</i> (unver&auml;nderlich) und kann von mehreren
 * Threads parallel verwendet werden. </p>
 *
 * @param   <T> generic type of chronological entity
 * @author  Meno Hochschild
 * @since   3.14/4.11
 */
public final class MultiFormatParser<T extends ChronoEntity<T>>
    implements ChronoParser<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoFormatter<T>[] parsers;

    //~ Konstruktoren -----------------------------------------------------

    private MultiFormatParser(ChronoFormatter<T>[] parsers) {
        super();

        this.parsers = parsers;

        for (ChronoFormatter<T> parser : this.parsers) {
            if (parser == null) {
                throw new NullPointerException("Null format cannot be set.");
            }
        }

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new multiple format parser. </p>
     *
     * @param   <T> generic type of chronological entity
     * @param   formats     array of multiple formats
     * @return  new immutable instance of MultiFormatParser
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen Multiformatinterpretierer. </p>
     *
     * @param   <T> generic type of chronological entity
     * @param   formats     array of multiple formats
     * @return  new immutable instance of MultiFormatParser
     * @since   3.14/4.11
     */
    @SafeVarargs
    public static <T extends ChronoEntity<T>> MultiFormatParser<T> of(ChronoFormatter<T>... formats) {

        ChronoFormatter<T>[] parsers = Arrays.copyOf(formats, formats.length);
        return new MultiFormatParser<T>(parsers);

    }

    /**
     * <p>Creates a new multiple format parser. </p>
     *
     * @param   <T> generic type of chronological entity
     * @param   formats     list of multiple formats
     * @return  new immutable instance of MultiFormatParser
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen Multiformatinterpretierer. </p>
     *
     * @param   <T> generic type of chronological entity
     * @param   formats     list of multiple formats
     * @return  new immutable instance of MultiFormatParser
     * @since   3.14/4.11
     */
    @SuppressWarnings("unchecked")
    public static <T extends ChronoEntity<T>> MultiFormatParser<T> of(List<ChronoFormatter<T>> formats) {

        ChronoFormatter<T>[] parsers =
            formats.toArray((ChronoFormatter<T>[]) Array.newInstance(ChronoFormatter.class, formats.size()));
        return new MultiFormatParser<T>(parsers);

    }

    /**
     * <p>Interpretes given text as chronological entity starting at the begin of text. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @see     #parse(CharSequence, ParseLog)
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text ab dem Anfang. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @see     #parse(CharSequence, ParseLog)
     * @since   3.14/4.11
     */
    public T parse(CharSequence text)
        throws ParseException {

        ParseLog status = new ParseLog();
        T result = this.parse(text, status);

        if (result == null) {
            throw new ParseException(
                status.getErrorMessage(),
                status.getErrorIndex()
            );
        }

        return result;

    }

    /**
     * <p>Interpretes given text as chronological entity starting
     * at the specified position in parse log. </p>
     *
     * <p>Following example demonstrates best coding practice if used in processing bulk data: </p>
     *
     * <pre>
     *     static final MultiFormatParser&lt;PlainDate&gt; MULTI_FORMAT_PARSER;
     *
     *     static {
     *         ChronoFormatter&lt;PlainDate&gt; germanStyle =
     *              ChronoFormatter.ofDatePattern(&quot;d. MMMM uuuu&quot;, PatternType.CLDR, Locale.GERMAN);
     *         ChronoFormatter&lt;PlainDate&gt; frenchStyle =
     *              ChronoFormatter.ofDatePattern(&quot;d. MMMM uuuu&quot;, PatternType.CLDR, Locale.FRENCH);
     *         ChronoFormatter&lt;PlainDate&gt; usStyle =
     *              ChronoFormatter.ofDatePattern(&quot;MM/dd/uuuu&quot;, PatternType.CLDR, Locale.US);
     *         MULTI_FORMAT_PARSER = MultiFormatParser.of(germanStyle, frenchStyle, usStyle);
     *     }
     *
     *     public Collection&lt;PlainDate&gt; parse(Collection&lt;String&gt; data) {
     *         Collection&lt;PlainDate&gt; parsedDates = new ArrayList&lt;&gt;();
     *         ParseLog plog = new ParseLog();
     *         int index = 0;
     *
     *         for (String text : data) {
     *             plog.reset(); // initialization
     *             PlainDate date = MULTI_FORMAT_PARSER.parse(text, plog);
     *             if ((date == null) || plog.isError()) {
     *                 // users are encouraged to use any good logging framework here
     *                 System.out.println(&quot;Wrong entry found: &quot; + text + &quot; at position &quot; + index);
     *             } else {
     *                 parsedDates.add(date);
     *             }
     *             index++;
     *         }
     *
     *         return Collections.unmodifiableCollection(parsedDates);
     *     }
     * </pre>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text ab der angegebenen Position im
     * Log. </p>
     *
     * <p>Folgendes Beispiel demonstriert eine sinnvolle Anwendung, wenn es um die Massenverarbeitung geht: </p>
     *
     * <pre>
     *     static final MultiFormatParser&lt;PlainDate&gt; MULTI_FORMAT_PARSER;
     *
     *     static {
     *         ChronoFormatter&lt;PlainDate&gt; germanStyle =
     *              ChronoFormatter.ofDatePattern(&quot;d. MMMM uuuu&quot;, PatternType.CLDR, Locale.GERMAN);
     *         ChronoFormatter&lt;PlainDate&gt; frenchStyle =
     *              ChronoFormatter.ofDatePattern(&quot;d. MMMM uuuu&quot;, PatternType.CLDR, Locale.FRENCH);
     *         ChronoFormatter&lt;PlainDate&gt; usStyle =
     *              ChronoFormatter.ofDatePattern(&quot;MM/dd/uuuu&quot;, PatternType.CLDR, Locale.US);
     *         MULTI_FORMAT_PARSER = MultiFormatParser.of(germanStyle, frenchStyle, usStyle);
     *     }
     *
     *     public Collection&lt;PlainDate&gt; parse(Collection&lt;String&gt; data) {
     *         Collection&lt;PlainDate&gt; parsedDates = new ArrayList&lt;&gt;();
     *         ParseLog plog = new ParseLog();
     *         int index = 0;
     *
     *         for (String text : data) {
     *             plog.reset(); // initialization
     *             PlainDate date = MULTI_FORMAT_PARSER.parse(text, plog);
     *             if ((date == null) || plog.isError()) {
     *                 // users are encouraged to use any good logging framework here
     *                 System.out.println(&quot;Wrong entry found: &quot; + text + &quot; at position &quot; + index);
     *             } else {
     *                 parsedDates.add(date);
     *             }
     *             index++;
     *         }
     *
     *         return Collections.unmodifiableCollection(parsedDates);
     *     }
     * </pre>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   3.14/4.11
     */
    public T parse(
        CharSequence text,
        ParseLog status
    ) {

        int start = status.getPosition();

        for (int i = 0; i < this.parsers.length; i++) {
            status.reset(); // initialization
            status.setPosition(start);

            // use the default global attributes of every single parser
            T parsed = this.parsers[i].parse(text, status);

            if ((parsed != null) && !status.isError()) {
                return parsed;
            }

        }

        status.setError(status.getErrorIndex(), "Not matched by any format: " + text);
        return null;

    }

    @Override
    public T parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes
    ) {

        int start = status.getPosition();

        for (int i = 0; i < this.parsers.length; i++) {
            status.reset(); // initialization
            status.setPosition(start);

            // use the default global attributes of every single parser,
            // possibly overridden by user-defined attributes
            T parsed = this.parsers[i].parse(text, status, attributes);

            if ((parsed != null) && !status.isError()) {
                return parsed;
            }

        }

        status.setError(status.getErrorIndex(), "Not matched by any format: " + text);
        return null;

    }

}
