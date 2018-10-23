Compatibility notes for v5.0 compared with previous version v4.38:
------------------------------------------------------------------

net.time4j.Duration:
	signature of method "Duration.comparator(T)" changed to: "<U extends IsoUnit, T extends TimePoint<U, T>> Comparator<Duration<? extends U>> comparator(T)

net.time4j.Moment:
	localFormatter(...)- and formatter(...)-methods removed => use factory methods of desired formatter directly

net.time4j.PlainDate:
	localFormatter(...)- and formatter(...)-methods removed => use factory methods of desired formatter directly
	method "axis(Converter<S, PlainDate>)" removed => use either new method "threeten()" or "new BridgeChronology<>(converter, PlainDate.axis())"

net.time4j.PlainTime:
	constant "ISO_HOUR" removed => use "HOUR_FROM_0_TO_24"
	localFormatter(...)- and formatter(...)-methods removed => use factory methods of desired formatter directly
	method "axis(Converter<S, PlainTime>)" removed => use either new method "threeten()" or "new BridgeChronology<>(converter, PlainTime.axis())"

net.time4j.PlainTimestamp:
	localFormatter(...)- and formatter(...)-methods removed => use factory methods of desired formatter directly
	method "axis(Converter<S, PlainTimestamp>)" removed => use either new method "threeten()" or "new BridgeChronology<>(converter, PlainTimestamp.axis())"

net.time4j.Platform:
	removed without replacement (use the alternative concept of "net.time4j.format.platform.SimpleFormatter")

net.time4j.ZonalClock:
	method "currentMoment()" removed => use "now().inStdTimezone()" instead (for better readability)

net.time4j.ZonalDateTime:
	method "parse(String, TemporalFormatter<Moment>)" now throws a ChronoException instead of a ParseException
	method "parse(String, TemporalFormatter<Moment>, ParsePosition)" removed

net.time4j.calendar.EthiopianCalendar:
	method "getYearOfEra()" removed => use "getYear()"

net.time4j.calendar.HebrewMonth.Order:
	constant "BIBILICAL" removed => use "BIBLICAL"

net.time4j.calendar.astro.SolarTime:
	method "sunrise(TZID)" removed => use other overloaded methods and then apply a timezone-based conversion
	method "sunset(TZID)" removed => use other overloaded methods and then apply a timezone-based conversion
	method "transitAtNoon(TZID)" removed => use other overloaded methods and then apply a timezone-based conversion
	method "transitAtMidnight(TZID)" removed => use other overloaded methods and then apply a timezone-based conversion

net.time4j.calendar.astro.SolarTime.Calculator:
	method "declination(double)" removed => use same method in class StdSolarCalculator
	constant "NOAA" removed => use enum constant in StdSolarCalculator
	constant "SIMPLE" removed => use enum constant in StdSolarCalculator

net.time4j.engine.CalendarEra (and all implementations):
	method "getValue()" removed without replacement because the underlying concept of temporal order of eras is often wrong

net.time4j.engine.ChronoMerger (and all implementations):
	method "createFrom(ChronoEntity<?>, AttributeQuery, boolean)" removed in favour of overloaded method with extra boolean parameter
	method "createFrom(TemporalAccessor, AttributeQuery)" removed => use bridge chronology instead

net.time4j.engine.Chronology:
	method "lookup(Class)" now works with an unspecified generic type T without upper bound

net.time4j.engine.RealTime:
	this interface has been removed without replacement, just use the implementation MachineTime in main package

net.time4j.engine.StartOfDay:
	signature of method "getDeviation(Calendrical<?, ?>, TZID)" changed to "getDeviation(CalendarDate, TZID)" => adjust subclasses

net.time4j.engine.TimeLine:
	getMinimum() and getMaximum() are no longer default methods and must be implemented

net.time4j.format.CalendarText:
	method "getFormatPatterns()" removed => use one of methods "patternForXYZ()" instead
	method "getTimestampPattern(DisplayMode, DisplayMode, Locale locale)" removed => use "patternForTimestamp(...)" instead
	method "getMeridiems(TextWidth)" removed => use "getMeridiems(TextWidth, OutputContext.FORMAT)" instead
	method "getGMTPrefix(Locale)" removed => use "ZonalOffset.UTC.getStdFormatPattern(Locale)"

net.time4j.format.ChronoPattern:
	removed without replacement

net.time4j.format.FormatEngine:
	removed without replacement

net.time4j.format.NumericalElement:
	the type parameter V is now constrained to enums

net.time4j.format.TemporalFormatter:
	new method "print(T)" added where old method "format(T)" becomes a synonym for new method
	method "formatToBuffer(T, Appendable)" removed => use similar print()-methods in concrete implementations
	method "parse(CharSequence, ParsePosition)" removed => use similar methods in concrete implementations (for example in ChronoFormatter with ParseLog)
	method "parse(CharSequence, ParsePosition, RawValues)" changed signature such that the ParsePosition-parameter was removed

net.time4j.format.TextProvider:
	method "getControl()" removed => no replacement (does not work on module path in Java 9 or later)
	method "meridiems(String, Locale, TextWidth)" removed => use overloaded method with OutputContext-parameter

net.time4j.format.expert.ChronoFormatter:
	public static <T extends LocalizedPatternSupport> ChronoFormatter<T> ofStyle(...) uses DisplayMode-parameter instead of DisplayStyle
	method "formatThreeten(TemporalAccessor)" removed => use static factory methods and/or bridge chronology instead
	method "printThreeten(TemporalAccessor, Appendable)" removed => use static factory methods and/or bridge chronology instead
	method "print(T, Appendable, AttributeQuery, ChronoFunction<ChronoDisplay, R>)" removed in favour of simplified signature

net.time4j.format.expert.ChronoPrinter:
	method "format(T)" renamed to "print(T)"
	method "print(T, Appendable, AttributeQuery, ChronoFunction<ChronoDisplay, R>)" removed in favour of simplified signature

net.time4j.format.expert.PatternType:
	constant "NON_ISO_DATE" removed in favour of "CLDR_DATE"

net.time4j.range.ClockInterval:
	method "parse(CharSequence, ChronoParser<PlainTime>, BracketPolicy, ParseLog)" removed in favour of other parse-methods

net.time4j.range.DateInterval:
	method "in(Timezone)" removed => use method "inTimezone(TZID)" instead
	method "move(long, CalendarUnit)" removed in favour of "move(long, IsoDateUnit)"
	method "parse(CharSequence, ChronoParser<PlainDate>, BracketPolicy, ParseLog)" removed in favour of other parse-methods

net.time4j.range.DayPartitionRule:
	method "getPartition(PlainDate)" renamed to "getPartitions(PlainDate)"

net.time4j.range.IntervalCollection:
	inherits from java.util.AbstractCollection
	method "contains(T)" removed => use method "encloses(T)" instead
	method "plus(List<? extends ChronoInterval<T>>)" removed => use overloaded method with Collection-parameter
	method "minus(List<? extends ChronoInterval<T>>)" removed => use overloaded method with Collection-parameter
	method `onTimeLine(TimeLine)` renamed to `on(TimeLine)`

net.time4j.range.IntervalTree:
	method `onTimeLine(TimeLine, Collection)` renamed to `on(TimeLine, Collection)`

net.time4j.range.IsoInterval:
	method "print(ChronoPrinter<T>, char, ChronoPrinter<T>, BracketPolicy, Appendable)" removed => use overloaded method with InfinityStyle.SYMBOL

net.time4j.range.MachineTime:
	class moved to main package "net.time4j" => change import statement
	method "dividedBy(long)" removed => use overloaded method with parameter RoundingMode.HALF_UP

net.time4j.range.MomentInterval:
	method "parse(CharSequence, ChronoParser<Moment>, BracketPolicy, ParseLog)" removed in favour of other parse-methods
	method "surrounding(Instant, MachineTime<TimeUnit>, double)" removed in favour of other surrounding-methods

net.time4j.range.SimpleInterval:
	method `on(TimeAxis)` removed and method `onTimeLine(TimeLine)` renamed to `on(TimeLine)`
	method "on(CalendarFamily, String)" removed => use "CalendarPeriod.on(CalendarFamily, String)" instead

net.time4j.range.TimestampInterval:
	method "in(Timezone)" removed => use method "inTimezone(TZID)" instead
	method "parse(CharSequence, ChronoParser<PlainTimestamp>, BracketPolicy, ParseLog)" removed in favour of other parse-methods

net.time4j.tz.TransitionHistory:
	method "getNextTransition(UnixTime)" removed in favour of "findNextTransition(UnixTime)"

net.time4j.tz.ZonalOffset:
	method "atLongitude(OffsetSign, int, int, int)" removed in favour of overloaded method with last double-parameter

net.time4j.tz.ZonalTransition:
	method "isDaylightSaving()" removed without replacement => use Timezone.isDaylightSaving(UnixTime moment) instead

net.time4j.tz.ZoneProvider:
	no replacement, use the super interfaces ZoneNameProvider or ZoneModelProvider instead

net.time4j.tz.model.DaylightSavingRule:
	method "isSaving()" removed without replacement

net.time4j.tz.other.WindowsZone:
	resolveSmart() now uses an optional result instead of sometimes null-result

net.time4j.xml.AnnualDate:
	moved to main package "net.time4j" => change import statement

--- Time4A ---

net.time4j.android.ApplicationStarter:
	method "prepareResources(Application)" removed => use "prepareAssets(application, null)" instead
