Time4J
======

Advanced date and time library for Java

Motivation:
-----------

Time4J is thought as a complete and high-end replacement for old java classes around java.util.Date, java.util.Calendar and java.text.SimpleDateFormat. This project is also intended as first-class alternative to the popular libraries JodaTime and its successor JSR-310 (Threeten) since the target audience of Time4J will not only be business Java developers, but also developers with a more scientific background (for example extended time scale support including leap seconds or - in a future release - historically accurate dates).

Although the new JSR-310 (built in Java 8) is certainly a very useful library for many business developers it also has some severe and intentional limitations. Time4J intends to fill all gaps in the future so justifying its co-existence.

Current state and introduction:
-------------------------------

On 2014-11-16 the version v2.0 of Time4J was finished and released. The older version line v1.x is no longer recommended (due to several backward incompatibilities) and has reached end-of-life. Time4J is organized in modules. The core is always necessary. Other modules are optional and include:

- time4j-olson which contains some predefined timezone identifiers as enums and enables parsing of localized timezone names. 
- time4j-i18n for enhanced localization support (especially for formatting of durations)
- time4j-range for handling intervals

Standard use cases will be covered by the main package "net.time4j". It offers four basic temporal types.

- PlainDate = calendar date strictly following ISO-8601
- PlainTime = wall time (on an analogous clock) including 24:00-support
- PlainTimestamp = local timestamp as composition of calendar date and wall time
- Moment = global timestamp which refers to true UTC standard including leapsecond-support

Here some examples as a flavour of how Time4J-code looks like (shown code valid for v2.0):

```java
import net.time4j.*;
import net.time4j.format.TextWidth;
import net.time4j.tz.olson.*;

import java.util.Locale;

import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.PlainDate.DAY_OF_MONTH;
import static net.time4j.PlainDate.DAY_OF_WEEK;
import static net.time4j.PlainTime.MINUTE_OF_HOUR;
import static net.time4j.Weekday.WEDNESDAY;

public class Demo {
  public static void main(String... args) {
	// What is the last day of overnext month?
	System.out.println(
		SystemClock.inLocalView().today().plus(2, MONTHS).with(DAY_OF_MONTH.maximized()));

	// When is next wednesday?
	PlainDate today = SystemClock.inLocalView().today();
	PlainDate nextWednesday = today.with(DAY_OF_WEEK.setToNext(WEDNESDAY));
	System.out.println(nextWednesday);

	// What is the current wall time rounded down to multiples of 5 minutes?
	PlainTimestamp currentLocalTimestamp = SystemClock.inZonalView(EUROPE.BERLIN).now();
	PlainTime roundedTime =
		currentLocalTimestamp.getWallTime() // T22:06:52,688
		.with(MINUTE_OF_HOUR.atFloor()) // T22:06
		.with(MINUTE_OF_HOUR.roundedDown(5)); // T22:05
	System.out.println("Rounded wall time: " + roundedTime);

	// How does last UTC-leapsecond look like in Japan?
	Moment leapsecondUTC =
		PlainDate.of(2012, Month.JUNE, 30)
		.at(PlainTime.midnightAtEndOfDay()) // 2012-06-30T24 => 2012-07-01T00
		.atUTC().minus(1, SI.SECONDS);
	System.out.println(leapsecondUTC); // 2012-06-30T23:59:60,000000000Z

	System.out.println(
		"Japan-Time: "
		+ Moment.localFormatter("uuuu-MM-dd'T'HH:mm:ssXX", PatternType.CLDR)
			.withTimezone(ASIA.TOKYO)
			.format(leapsecondUTC)
	); // Japan-Time: 2012-07-01T08:59:60+0900

	// duration in seconds normalized to hours, minutes and seconds
	Duration<?> dur = Duration.of(337540, ClockUnit.SECONDS).with(Duration.STD_CLOCK_PERIOD);

	// custom duration format => hh:mm:ss
	String s1 = Duration.Formatter.ofPattern("hh:mm:ss").format(dur);
	System.out.println(s1); // output: 93:45:40

	// localized duration format for french
	String s2 = PrettyTime.of(Locale.FRANCE).print(dur, TextWidth.WIDE);
	System.out.println(s2); // output: 93 heures, 45 minutes et 40 secondes
  }
}
```

Design remarks:

a) Safety: Although Time4J is strongly generified users will not really use any generics in their application code as demonstrated in example code, but are more or less type-safe at compile-time. For example, it is impossible to add clock units to a calendar date. This is in contrast to JSR-310 which heavily relies on runtime exceptions. Otherwise Time4J shares the advantages like immutability and non-tolerant null-handling.

b) In contrast to most other libraries Time4J does not like implicit defaults. Users have to explicitly specify what locale or time zone they want. And even if they want the default then they spell it so in methods like: "inLocalView()" or "localFormatter(...)" or "inStdTimezone()". This philosophy is also the reason why the class "PlainDate" is missing a static method like "today()". This method instead exists in the class "ZonalClock" making clear that you cannot achieve the current local date and time without specifying the time zone.

c) Time4J offers a lot of manipulations of date and time by an element-centric approach. Every basic type like 
"PlainTime" registers some elements (similar to fields in other libraries) which serve as access key to chronological partial data. These elements like "MINUTE_OF_HOUR" offer many different manipulation methods, called operators using the strategy pattern idea. With this design it is possible to manipulate a "PlainTime" in more than 170 different ways. Another advantage of this design: Despite the size of features the count of methods in most classes is still not too big, "PlainTime" has less than 45 methods including the inherited methods from super classes.

d) Another way of manipulation is date/time-arithmetic along a time axis. All four basic types have their time axis. For example roughly spoken "Moment" is defined as an elapsed count of SI-seconds since UTC epoch while a calendar date (here: "PlainDate") maps dates to julian days - another kind of time axis. The essentials of this time arithmetic are realized via the abstract super class "TimePoint". So all four basic types inherit methods like "plus(n, units)", "until(...)" etc for supporting adding, subtracting and evaluating durations.

e) Time4J rejects the design idea of JSR-310 to separate between "machine time" and "human time". This is considered as artificial. So all four basic types offer both aspects in one. For example a calendar date is simultaneously a human time consisting of several meaningful elements like year, month etc. and also a kind of machine or technical time counter because you can define a single incrementing number represented by julian days. In a similar way a UTC-moment has both a technical counter (the number of SI-seconds since UTC-epoch) AND a human representation visible in its canonical output produced by toString()-method (example: 2014-04-21T19:45:30Z). However, Time4J emphasizes the
difference between local and global types. Conversion between these types always require a timezone or an offset.

Plans for next releases:
----------------------------------

There are no fixed predictions when some features will introduced in which release. However, you can follow the milestone page to get a rough estimation - see https://github.com/MenoData/Time4J/issues/milestones.

While the main focus of the next releases are standard business use cases, you can expect later more exciting features like other calendar systems, support for historical dates and astronomically related calendar issues. Time4J will be a long-term running project. A smooth integration with JSR-310 and Joda-Time is also planned.


Downloads and Requirements:
---------------------------

You can find any downloads on the release page. Time4J will run at least under Java 6 or later. If you want to build it yourself from sources then you need a Java7-compiler (not 6!) with options "-source 1.6 -target 1.6". This is necessary to ensure that generified code will correctly compile.

Feedback:
---------

Feedback is welcome. You can best use following page for feedback: https://github.com/MenoData/Time4J/issues

Tutorials:
----------

English tutorials and code examples will soon be presented on the website "http://www.time4j.net/".

Blog:
-----

If you are capable of German language you can also read my blog "http://www.menodata.de/blog/" where I sometimes post my views about Java and Time, for example my review of JSR-310.
