## [v4.19] not yet released
### Fixed
- PUSH-FORWARD-strategy is not suitable for intervals [#545]

## [v4.18] published on 2016-08-28
### Added
- Stream support for ISO-intervals [#537]
- Recurrent intervals as defined in ISO-8601 [#488]
- Add localized display names for `ChronoElement` [#515]
- Support for annual dates [#514]
- Improved documentation of HijriCalendar [#518]
- Add general parse methods for dates in ISO-8601-format [#520]
- Iso8601Format....WALL_TIME now understands T17:45 [#521]
- Embedded formatter should know outer format attributes and defaults [#522]
- 4 new methods findXYZTransiton(...) to TransitionHistory [#523]
- ChronoFormatter.Builder should define default values and global attributes [#531]
- Add convenient methods to ChronoPrinter and ChronoParser [#532]
- Make printing of ISO-formats configurable [#533]
- New date-interval-method to convert to a moment interval [#534]
- Implement reduced ISO-formats for intervals [#535]
- Improve formatting of infinite intervals [#536]
- New interval boundary manipulations based on operators [#538]
- Determine first moment for given calendar date in time zone [#539]
- Add method to determine intersection interval [#542]
- Enable parsing of literals with leading digits after numerical elements [#544]

### Fixed
- Prescan phase of iso interval parser sometimes incorrect [#530]
- AM/PM-element not sensible for output context [#529]
- Inconsistent TimeSpan-behaviour of negative MachineTime [#540]
- Inconsistent exception handling of with()-methods [#541]
- Optimization of day-of-week-queries and date arithmetic in `PlainDate` [#543]

### Changed
- New leap second at end of year 2016 [#517]
- Make fractional second parser more tolerant [#519]

### Deprecated
- 1 method in TextProvider deprecated due to missing output context [#529]
- 1 methods in TransitionHistory deprecated and replaced by new better named method [#523]
- 1 method in ChronoPrinter deprecated and replaced by new methods [#532]
- 1 parse method in each of four iso-interval classes replaced by new one without ParseLog-parameter [#535]
- 1 print method in IsoInterval (replaced by a new one with extra infinity style arg) [#536]

## [v4.17] published on 2016-06-20
### Added
- Add conversion of net.time4j.Duration to TemporalAmount [#511]
- Enhance Temporal-interface by more comparison methods [#510]
- Introduce class for year-week-combination [#508]
- Introduce class for year-month-combination [#503]
- Introduce class for year-quarter-combination [#504]
- Model calendar year as date interval [#505]
- Add timespan classes Years, Quarters, Months and Weeks [#506]
- Better support for week-based durations and units [#509]

### Fixed
- Duration measured in millis only is broken [#513]
- Zone offset without sign - should be parseable in lax mode [#502]

## [v4.16] published on 2016-05-07
### Added
- Related gregorian year for non-gregorian calendars [#370]
- Make MachineTime comparable [#489]
- Modern-coverage of languages defined in CLDR [#367]
- Support for Uyghur [#496]
- Support for Malayalam [#493]
- Support for Basque, Galician and Welsh [#494]
- Support for Esperanto [#495]

### Fixed
- Make alias zone identifiers always parseable [#500]
- Improve performance of parsing timezone names [#499]
- Clarify format behaviour if zoneless types are combined with zone names [#490]

### Changed
- Split `ZoneProvider` into `ZoneModelProvider` and `ZoneNameProvider` [#498]
- Update to CLDR version 29 [#492]
 
## [v4.15] published on 2016-04-18
### Added
- Add convenient abuts-method to IsoInterval [#486]
- Add boolean query to determine if two intervals intersect [#485]
- Add Thai solar calendar (Suriyakati) [#478]
- Fine-tuning of historization [#479]
- Clarify that PlainDate is historizable [#448]

### Fixed
- OR-operator logic in ChronoFormatter sometimes broken [#482]
- Fix and improve documentation of era manipulations [#481]

### Deprecated
- Deprecate usage of CalendarEra.getValue() [#480]

## [v4.14] published on 2016-03-23
### Added
- Enable unparseable characters to be skipped when parsing [#476]
- Add ChronoFormatter.withDefaultSupplier(...) [#470]
- Move SQL-support to core-module [#471]
- Add SQL-support for type TIMESTAMP WITH TIMEZONE [#472]
- Historic year definition (example Easter style in France) [#473]
- More detailed error message when parsing ZonalDateTime [#467]
- Support for Kirghiz [#474]

### Fixed
- Make resolving zone names more robust [#469]
- Calculus Pisanus does not work in parsing [#466]

## [v4.13] published on 2016-03-06
### Added
- Make ZonalDateTime easier for creation and comparison [#462]
- Calculation of Easter (Computus) [#460]
- Support for Telugu language [#459]
- Add element support for historic day-of-year [#456]
 
### Fixed
- TimezoneRepositoryProviderSPI throws NPE [#464]
- Refine localized calendar history in Europe [#461]
- PlainTime.toString() does not display fraction of second as per spec [#465]
- Make Timezone.ofSystem() more robust against weird zone ids. [#463]
- Don't permit negative years for any historic era [#458]
- Clarify/correct exception behaviour of new year strategies [#457]
- Reduce array allocation during parsing [#455]

## [v4.12] published on 2016-02-14
### Added
- Proleptic Julian calendar [#444]
- Add Roman numerals [#443]

### Fixed
- Unit-between-arithmetic broken for Persian, Coptic and Ethiopian calendar [#453]
- Improve overall performance [#450]
- Text resources for am-pm are mismatched (in non-iso-calendars) [#452]
- Add equals/hashCode-support for platform formatter [#451]
- Prohibit use of Ethiopian hour with PlainTime [#449]
- Code example in documentation of MultiFormatParser is incomplete [#446]

## [v4.11] published on 2016-01-22
### Added
- Two new approximation normalizers based on max unit only [#442]
- Add pattern-based factory method to ChronoFormatter for any chronology [#441]
- Introduce or-operator in formatting via builder and pattern [#437}
- Add other historic eras [#436]
- Support for various historical New Year events [#434]
- Define convenient methods for creating intervals using JSR-310-types [#433]
- Date arithmetic on HijriCalendar [#429]
- New methods `withVariant(...)` on CalendarVariant [#428]
- Dual parsing of embedded or standalone formats [#427]
- Dedicated MultiFormatParser [#426]
- Support for Indian languages gu, kn, mr, pa, ta [#439]
- Support for Burmese (my - Myanmar) [#438]

### Fixed
- Unable to print java.time.chrono.MinguoDate with Time4J [#440]
- Setting proleptic julian history on formatter is ignored [#435]
- Semantic of changing historic era in ChronoHistory unclear [#430]

### Changed
- Update leap second expiry date to 2016-12-28 [#431]

## [v4.10] published on 2015-12-31
### Added
- Fixed and flexible day periods with appropriate translations [#369]
- Minguo calendar (Taiwan) [#390]
- Enable localized digits in timezone offsets [#411]
- New getters for day-of-week and day-of-year in PlainDate [#416]
- Query parsed raw data for any registered elements [#415]
- New method CalendarText.getIsoInstance(Locale) [#410]
- New methods CalendarText.patternForXYZ(...) [#410]
- Support for Nepali [#422]
- Support for Mongolian [#421]
- Support for Afrikaans and Zulu [#420]
- Support for the languages Khmer and Lao [#419]

### Fixed
- PersianCalendar in Farsi language should be with arabext numbers [#425]
- Bidi literals (LRM, RLM, ALM) should be ignored in parsing [#418]
- Formatting Ethiopian time with AM/PM-marker is not in western style [#413]
- Parsing of Ethiopic tabot names broken if not at end of text [#412]

### Deprecated
- EthiopianCalendar.getYearOfEra() deprecated and renamed to getYear() [#417]
- CalendarText.getGMTPrefix [#410]
- CalendarText.getFormatPatterns() [#410]
- CalendarText.getTimestampPattern [#410]

## [v4.9] published on 2015-11-30
### Added
- Support for Bengali [#409]
- Ethiopian time should support element PlainTime.COMPONENT [#408]

### Fixed
- HijriAlgorithm calendar variant broken [#407]
- Broken javadoc links to some serial forms [#406]
- British date format should be in order DMY [#403]

### Removed
- Tidy deprecated elements [#404]

## [v4.8] published on 2015-11-22
### Added
- Support for Sinhalese [#402]
- Print relative times for java.time.Instant [#392]
- Add Ethiopian calendar and time [#389]
- Enable formatting/parsing moments using another calendar [#399]
- GeneralTimestamp should implement ChronoDisplay [#401]
- Add Coptic calendar [#388]
- Support for triennal julian leap years [#393]

### Fixed
- Ensure that parsing raw data does not resolve anything [#400]
- Compile problem with text elements in ChronoFormatter.Builder [#398]
- plus/minus(CalendarDays) does not document any exception [#397]
- Arithmetic overflow in ChronoHistory [#391]

### Changed (updated/removed/deprecated)
- New method ChronoMerger.getDefaultStartOfDay() [#399]
- Refactor handling of two exotic format attributes [#395]
- Hide internal format attributes of ChronoHistory [#394]

## [v4.7] published on 2015-10-22
### Added
- New style-based factory methods for ChronoFormatter [#376], [#377]
- Repository for localized date-time-patterns [#100]
- Support for Amharic (am) [#387]
- Support for Swahili (sw) [#386]
- Support for Uzbek (uz), Turkmen (tk) and Kazakh (kk) [#385]
- Support for Armenian (hy), Azerbaijani (az) and Georgian (ka) [#384]
- Support for Filipino (Tagalog - fil or tl) [#380]
- Support for Icelandic (is) and Malta language (mt) [#375]

### Fixed
- Improved parsing of MSK timezone name [#381]
- Negative duration in Arabic sometimes without minus sign [#379]
- ChronoFormatter.format()-methods lack type safety [#378]

### Changed
- Better translations for various countries (mainly arabic and french) [#383]
- Better translations for Spanish locales in Latin America [#368]
- Simplify interface ChronoMerger by using Java-8 default methods [#382]
- Two ChronoFormatter.format()-methods renamed [#378]
- Low-level interface ChronoMerger with new method [#377]
- Review plural rules [#366]
- Update i18n-data to CLDR-28 [#374]
- Change ISO-8601-fallback-mechanism to CLDR standard [#373]
- FormatPatternProvider.getDateTimePattern() with 3 args [#100]

### Removed
- FormatPatternProvider.DEFAULT [#100]

## [v4.6] published on 2015-10-01
### Added
- Formatting/parsing of intervals based on custom interval patterns [#359]
- Canonicalization of interval boundaries [#365]
- New Persian calendar [#357]
- Support for Pashto language [#358]
- Support for Turkish islamic calendar (Diyanet) [#355]

### Fixed
- Non-overridden format attributes should be preserved [#364]
- Enable parsing of timestamps with 24:00 even in strict mode [#363]
- 2015-10-25T02:00+01:00[Europe/Berlin] cannot be parsed [#361]
- Wrong between-calculation with overflow units [#360]
- Pattern letter "L" causes exception for non-iso-dates [#354]

### Changed
- Behaviour of interval formatting/parsing changed [#359]
- Make serialization of HijriCalendar versionable [#356]

### Removed
- Interface ExtZoneProvider (no usage) [#371]

## [v4.5] published on 2015-09-15
### Added
- Support for Malay language [#353]
- Unify CalendarVariant and Calendrical by new interface CalendarDate [#350]
- New XOR-operator for interval collections [#347]
- Make PatternType.CLDR useable for non-iso-dates [#348]
- New calendar-related class GeneralTimestamp [#340]
- New intersect-operator for interval collections [#345]
- New method IntervalCollection.getRange() [#344]
- PrettyTime.printRelativeOrDate() using PlainDate [#343]
- Moment-class with a new precision element [#342]
- Add plus/minus-operators for IntervalCollection [#339]

### Fixed
- Disable parsing of ambivalent text forms [#351]

### Changed
- Rename unit pattern resource file names [#352]
- CalendarVariant and Calendrical changed Temporal-signature [#350]
- Two specialized now()-methods in ZonalClock changed the return type [#340]
- IntervalCollection.union() should create a merged collection [#338]

## [v4.4] published on 2015-08-31
### Added
- Support for algorithmic variants of islamic calendar [#334]
- Create direct option to determine length of Hijri month or year [#337]
- Add toDate() and toTime() in PlainTimestamp [#336]
- New method(s) for printing durations with 1 parameter only [#333]
- Create option to either print relative time or normal date-time [#328]
- Print relative times with abbreviations [#327]
- I18n-support for "yesterday", "today", "tomorrow" [#310]

### Fixed
- Printing relative time near a leap second incorrect [#332]
- Behaviour of timezone-offset-parser inconsistent with spec [#330]
- Time-axis-element not fully supported [#329]

### Changed
- ISO-formatters need to be strict by default [#331]

## [v4.3] published on 2015-08-12
### Added
- Umalqura calendar for Saudi-Arabia (experimental) [#313]
- Start-of-day-feature, relevant for current date in islamic calendar [#323]
- New pattern type for generic date chronologies [#324]
- New pattern type CLDR_24 with support for ISO-hour in range 0-24 [#314]
- Convenient method `printRelativeInStdTimezone(moment)` for `PrettyTime` [#311]
- New transformation methods for calendrical objects [#318]
- Introduce calendar families with day calculations [#315]

### Fixed
- Remove unnecessary warning message in ChronoFormatter [#312]

### Deprecated
- Interface `ExtZoneProvider` no longer used

### Changed
- Narrow value-type of BasicElement to Comparable [#321]

## [v4.2] published on 2015-06-26
### Added
- Generic clock-based creation of any `ChronoEntity` [#303]
- Add clock-methods `inPlatformView()` [#304]
- Register `PlainTime.PRECISION` in `PlainTimestamp` [#306]
- Improve adjacent digit parsing of 4-digit-years [#307]
- Language support for Irish (ga) [#309]

### Fixed
- Merger-factories for clock-based objects of basic types too lenient [#302]
- Don't cache system timezone in  SystemClock.inLocalView()  [#305]
- Threeten-pattern uu does not work [#308]

## [v4.1] published on 2015-06-11
### Added
- Monotonic clocks (main new feature) [#295]
- Add generic ordinal weekday-in-month [#297]
- Queries for dst+raw-offsets in `Timezone` [#288]
- Language support for Belorusian (be), Hebrew (he, iw), Bosnian (bs) [#290, #292, #294]

### Fixed
- `LeapSeconds.enhance(unixTime)` does not follow spec [#296]
- Version-compatibility-bug in tzdata-1.2-2015d [#298]

## [v4.0] published on 2015-05-25
- First release of version line v4.x, represents a fork of Time4J for the sake of interoperability with Java-8.
