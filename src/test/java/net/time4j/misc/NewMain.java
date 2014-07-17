/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.time4j.misc;

import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.Iso8601Format;
import net.time4j.IsoDateUnit;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PatternType;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.TemporalTypes;
import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.ParseLog;
import net.time4j.format.SignPolicy;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Locale;

import static net.time4j.PlainTime.NANO_OF_DAY;
import static net.time4j.tz.ZonalOffset.Sign.AHEAD_OF_UTC;

/**
 *
 * @author Administrator
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, IOException, ClassNotFoundException {

        PlainTime time = PlainTime.of(6); // T06:00
        System.out.println(
           time.get(NANO_OF_DAY.ratio()).multiply(BigDecimal.valueOf(100)).stripTrailingZeros()
           + "% of day are over.");

        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter.setUp(PlainTime.class, Locale.ROOT)
                .addLiteral('T')
                .addFixedInteger(PlainTime.ISO_HOUR, 2)
                .addLiteral(':')
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .build();
        System.out.println(formatter.format(PlainTime.midnightAtStartOfDay()));
        System.out.println(formatter.format(PlainTime.midnightAtEndOfDay()));

           int minDigits = 3;
           int maxDigits = 6;

           formatter =
               ChronoFormatter.setUp(PlainTime.class, Locale.ROOT)
               .addInteger(
                   PlainTime.MILLI_OF_SECOND,
                   minDigits,
                   maxDigits,
                   SignPolicy.SHOW_ALWAYS)
               .build();
           System.out.println(
               formatter.format(PlainTime.of(12, 0, 0, 12345678)));

           formatter =
               ChronoFormatter.setUp(PlainTime.class, Locale.ROOT)
               .addLiteral('T')
//               .startSection(Attributes.LENIENCY, Leniency.STRICT)
               .addInteger(PlainTime.ISO_HOUR, 2, 2)
               .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
//               .startOptionalSection()
               .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
               .build()
               .with(Attributes.LENIENCY, Leniency.LAX);
           System.out.println(
               formatter.format(PlainTime.of(13, 45, 8)));
           System.out.println(formatter.parse("T13458"));
           System.out.println(formatter.parse("T240009"));

           formatter =
               ChronoFormatter.setUp(PlainTime.class, Locale.GERMANY)
               .addInteger(PlainTime.CLOCK_HOUR_OF_AMPM, 1, 2)
               .addLiteral(' ')
               .addText(PlainTime.AM_PM_OF_DAY)
               .padPrevious(3)
               .padNext(4)
               .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
               .build()
               .with(Attributes.LENIENCY, Leniency.STRICT)
               .with(Attributes.TRAILING_CHARACTERS, true);
           System.out.println(formatter.format(PlainTime.of(17, 45)));
           ParseLog log = new ParseLog();
           System.out.println(formatter.parse("5 PM   45gy34567890x", log));
           System.out.println("Log: " + log);

         formatter =
               ChronoFormatter.setUp(PlainTime.class, Locale.ROOT)
               .addInteger(PlainTime.CLOCK_HOUR_OF_AMPM, 1, 2)
               .addLiteral(' ')
               .addText(PlainTime.AM_PM_OF_DAY)
               .padPrevious(3)
               .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
               .build()
               .with(Attributes.LENIENCY, Leniency.STRICT)
               .with(Attributes.TRAILING_CHARACTERS, true);
           System.out.println(formatter.parse("05 PM 45xyz"));

           ChronoFormatter<PlainDate> cf =
                   ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                   .addInteger(PlainDate.DAY_OF_MONTH, 2, 2)
                   .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                   .addTwoDigitYear(PlainDate.YEAR)
                   .build();
           System.out.println(cf.parse("101260"));

       PlainDate date = PlainDate.of(2014, Month.JANUARY, 2);
       IsoDateUnit unit =
            PlainDate.axis().getBaseUnit(PlainDate.YEAR_OF_WEEKDATE);
       System.out.println(date.plus(1, unit)); // Ausgabe: 2015-01-01

       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       new ObjectOutputStream(baos).writeObject(Moment.UNIX_EPOCH);
       Object obj =
        new ObjectInputStream(
            new ByteArrayInputStream(baos.toByteArray())).readObject();
       System.out.println("EQUALITY: " + (obj == Moment.UNIX_EPOCH));
       System.out.println(PlainDate.of(1972, Month.JANUARY, 1)
               .atStartOfDay());
       Moment moment =
               PlainDate.of(1972, Month.JANUARY, 1)
               .with(PlainDate.YEAR, 1975)
               .atStartOfDay()
               .atUTC()
               .plus(1, SI.SECONDS);
       System.out.println(moment);

       baos.reset();
       new ObjectOutputStream(baos).writeObject(moment);
       obj =
        new ObjectInputStream(
            new ByteArrayInputStream(baos.toByteArray())).readObject();
       System.out.println("EQUALITY: " + (obj.equals(moment)));
       System.out.println(obj);
//---------
       moment =
               PlainDate.of(2012, Month.JUNE, 30)
               .at(PlainTime.of(23).with(PlainTime.ISO_HOUR.atCeiling()))
               .atUTC()
               .plus(1, SI.SECONDS);
       System.out.println(moment);

       baos.reset();
       new ObjectOutputStream(baos).writeObject(moment);
       obj =
        new ObjectInputStream(
            new ByteArrayInputStream(baos.toByteArray())).readObject();
       System.out.println("EQUALITY: " + (obj.equals(moment)));
       System.out.println(obj);

       System.out.println("TS-TEST: ");

       PlainTimestamp start = PlainTimestamp.of(2014, 1, 2, 17, 0);
       PlainTimestamp end = PlainTimestamp.of(2014, 1, 1, 14, 0, 1);
       System.out.println(ClockUnit.HOURS.between(start, end));
       System.out.println(start.plus(-49, ClockUnit.HOURS));

        ChronoFormatter<Moment> utcFormat =
             ChronoFormatter.setUp(Moment.class, Locale.US)
             .addFixedInteger(PlainDate.YEAR, 4)
             .addLiteral('-')
             .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
             .addLiteral('-')
             .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
             .addLiteral('T')
             .addFixedInteger(PlainTime.ISO_HOUR, 2)
             .addLiteral(':')
             .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
             .addLiteral(':')
             .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
//             .startOptionalSection()
             .addFraction(PlainTime.NANO_OF_SECOND, 9, 9, true)
//             .endSection()
             .addTimezoneOffset(
                DisplayMode.LONG,
                true,
                Collections.singletonList("Z"))
             .build()
             .withTimezone(TZID.ASIA.TOKYO)
             .with(Attributes.LENIENCY, Leniency.STRICT);
        System.out.println(utcFormat.format(moment));
        ParseLog plog = new ParseLog();
        System.out.println(
            utcFormat.parse(
                "2012-07-01T08:59:60.500000000+09:00:00", plog));
        System.out.println(plog);

  System.out.println(
      PlainTime.of(18, 38).with(PlainTime.MINUTE_OF_HOUR.roundedDown(15)));
  System.out.println(
      PlainTime.of(18, 38).with(PlainTime.MINUTE_OF_HOUR.roundedUp(15)));
  System.out.println(
      PlainDate
          .of(2014, Month.FEBRUARY, 27)
          .with(PlainDate.DAY_OF_MONTH.roundedUp(5)));

         java.sql.Timestamp sqlValue = new java.sql.Timestamp(86401 * 1000);
       sqlValue.setNanos(1);
       PlainTimestamp ts = // 1970-01-02T00:00:01,000000001
           TemporalTypes.SQL_TIMESTAMP.transform(sqlValue);
       System.out.println("SQL: " +
           ts.get(TemporalTypes.SQL_TIMESTAMP).equals(sqlValue));
       // Ausgabe: true
        System.out.println(ts);

        System.out.println(moment.toString(TimeScale.POSIX));
        System.out.println(moment.toString(TimeScale.UTC));
        System.out.println(moment.toString(TimeScale.TAI));
        System.out.println(moment.toString(TimeScale.GPS));

        System.out.println(
            Moment.localFormatter("d. MMMM yyyy O", PatternType.CLDR)
                .withTimezone(ZonalOffset.ofHoursMinutes(AHEAD_OF_UTC, 2, 0))
                .format(moment));

        System.out.println("mesz=" +
            Moment.localFormatter("d. MMMM yyyy G HH:mm z", PatternType.CLDR)
                .with(Attributes.LENIENCY, Leniency.STRICT)
                .parse("1. Juli 2014 n. Chr. 14:00 MESZ"));

        System.out.println(
            Moment.localFormatter("d. MMMM yyyy z", PatternType.CLDR)
                .format(moment));

        plog = new ParseLog();
        System.out.println("europe/berlin=" +
            Moment.localFormatter(
                "d. MMMM yyyy HH:mm '['VV']'",
                PatternType.CLDR)
                .with(Attributes.LENIENCY, Leniency.STRICT)
                .parse("1. Juli 2014 14:00 [Europe/Berlin]", plog));
        System.out.println(plog.getRawValues().getTimezone().canonical());

        moment = moment.with(PlainTime.SECOND_OF_MINUTE, 59);
        System.out.println("ORIGINAL: " + moment);
        System.out.println("+1s (SI): " + moment.plus(1, SI.SECONDS));
        System.out.println(
            "+1s (clock): "
            + moment.with(
                Duration
                    .of(1, ClockUnit.SECONDS)
                    .later(Timezone.of(ZonalOffset.UTC))));

        PlainTime walltime = PlainTime.of(15, 20, 0, 123456789);
        System.out.println(
            Iso8601Format.EXTENDED_WALL_TIME.format(walltime));

        PlainDate d = PlainDate.of(-1, 4, 12);
        System.out.println(
            Moment.localFormatter("d. MMMM yyyy G", PatternType.CLDR)
                .format(d.atStartOfDay().atTimezone(TZID.EUROPE.BERLIN)));

    }
}
