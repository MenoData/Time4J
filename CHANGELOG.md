## [v3.41] published on 2018-03-24  (final version of level-6)
### Added
- Reverse engineering of windows zone names [#756]
- Normalization of timezone identifiers [#756]
- Support Asturian language (ISO-639: ast) [#757]
- Convenience week elements for Hebrew calendar [#758]

### Fixed
- Make astronomical calculations possible with leap seconds disabled [#761]

## [v3.40] published on 2018-03-05
### Added
- Traditional Chinese calendar [#396]
- Dangi calendar (Korean) [#722]
- Vietnamese lunar calendar [#641]
- Cyclic year used in East Asian calendars [#638]
- Juche calendar (North Korea) [#748]
- Shadow length of objects with specified height [#754]
- Increase displayed precision of moon illumination [#747]
- Convenience constants for calendar-specific week elements [#753]
- Bounded calendar-week-elements in CommonElements [#738]
- Optimize chronological extensions during parsing [#749]
- Improved localization for French Rev. calendar [#741]

### Fixed
- Weekmodel.ISO.boundedWeekOfMonth().atFloor() not working as expected [#750]

## [v3.39] published on 2018-01-15
### Added
- Extra features for StdSolarCalculator.CC [#731]
- Search for moon phase at or after a moment [#730]

### Changed
- Allow negative DST-offsets [#735]
- Update leap second expiry date [#734]

### Fixed
- date.getMaximum(<week-related-element>) can crash near end of timeline [#732]

## [v3.38] published on 2017-12-18
### Added
- Moon rise/set [#704]
- Calculation for azimuth/elevation of Sun and Moon [#723]
- Positions of Sun and Moon in terms of right ascension and declination [#716]
- Make right ascension of sun accessible [#715]
- Determine min/max-range of possible lunations [#720]

### Changed/Deprecated
- HebrewMonth.Order.BIBILICAL contains a typo [#714]
- Four methods in SolarTime loose the day information [#724]

### Fixed
- StdSolarCalculator.CC has integer-division-error [#725]
- Class SolarTime normally expects LMT-dates not zoned dates [#719]
- JulianDay misses definition of serialVersionUID [#727]

## [v3.37] published on 2017-11-26
### Added
- Hebrew calendar [#528]
- Hebrew time [#708]
- Hebrew birthdays and yahrzeit [#707]
- Constants of solar time for Jerusalem and Mecca [#711]
- Add Odia (Oriya) language [#709]
- More flexible numberings of enums in formatting [#706]
- Update to CLDR v32 [#690]
- Conversions to/from old java.util.Calendar + java.util.TimeZone [#705]

### Changed
- Optimize ZoneNameProviderSPI [#713]

## [v3.36] published on 2017-10-20
### Added
- Moon phases [#676]
- Illumination of moon [#702]
- Historic calendar [#698]
- New SolarTime.Calculator based on Dershowitz/Reingold [#701]
- Static factory ChronoHistory.from(variantString) [#697]
- Extended support for always-intervals [#695]
- Generic calendar intervals [#675]
- Julian centuries with J2000-epoch [#693]

### Changed
- TimeLine-enhancement [#675]
- German names of French Republican calendar months [#692]
- Prevent calling some SolarTime.Builder-methods twice [#691]
- Refine altitude-dependent calculation of solar time [#689]

### Fixed
- ChronoHistory.month()-annotation is incomplete [#696]

## [v3.35] published on 2017-09-25
### Added
- New hemisphere-related methods in astronomical classes [#688]
- Unit simulating Joda-behaviour for month-based durations [#687]

### Changed
- Parsing of "AM" or "PM" for all locales [#684]
- Changes to CalendarUnit.keepingEndOfMonth() and atEndOfMonth() [#679]
- Clarify meaning of IntervalCollection [#677]
- Empty intervals should not be added to interval collection [#678]

### Fixed
- Zone name parsing should use string-prefix-matching [#686]
- Duration parser tolerates trailing chars [#682]

### Deprecated
- Rename PlainTime.ISO_HOUR to HOUR_FROM_0_TO_24 [#685]

## [v3.34] published on 2017-09-10
### Added
- Support for sunrise / sunset - calculations [#663]
- Static validation methods for calendars [#666]
- Twilight definition [#667]
- Sunset as start-of-day (for islamic calendar etc) [#668]
- Simplified version of JulianDay [#670]
- Easy conversions from general intervals to ISO-intervals [#671]
- Intervals surrounding any moment [#672]

### Changed
- Improve calculation of equation of time [#665]

### Fixed
- French revolutionary calendar not serializable [#664]

## [v3.33] published on 2017-07-28
### Added
- French Revolutionary Calendar [#615]
- New formatter method 'getPattern()' [#662]
- Support for or-logic during printing [#661]
- Prevent escaping of Z-literal in format patterns [#658]
- Direct parse-methods for Weekday, Quarter and Month [#656]
- Weekday-in-month in non-iso-calendars [#653]
- Alternative calculations for PersianCalendar [#634]
- Calculation of astronomical seasons [#628]
- Support for apparent solar time [#633]
- Support for Julian Day Number [#527]
- Implement timescales TT and UT1 [#93]
- New dynamic pattern type [#659]

### Changed
- Redefine value space and epoch of TAI [#652]

### Deprecated
- Make PatternType-API fit for more calendars [#659]
- Prepare StartOfDay-change for next major release [#655]

## [v3.32] published on 2017-04-28
### Added
- Japanese imperial calendar [#560]
- Indian national calendar (Saka) [#642]
- Custom day adjustments on HijriCalendar [#649]
- Make pivot-year for two-digit-years calendar-specific [#643]
- Support for Japanese numbers [#639]
- Improve text resource handling [#626]
- Method nowInSystemTime() for `AnnualDate` and `EthiopianTime` [#646]

### Fixed
- DAY_OF_WEEK-element in some calendars inconsistent [#644]
- Clarify usage of Ethiopian eras [#636]
- Roundtrip of print/parse during zone offset overlap fails [#635]
- Converting geo longitude to ZonalOffset crashes [#632]

## [v3.31] published on 2017-03-27
### Added
- Add ALWAYS-constant to DateInterval [#630]
- Assignment of values to intervals [#629]

### Fixed
- NPE in IntervalTree containing interval with infinite end [#627]

## [v3.30] published on 2017-03-19
### Added
- Make zone offset calculation for geo-longitude more precise [#622]
- Enable text-lookup for non-enum elements in formatting [#618]
- Support for Ewe language [#625]

### Fixed
- ZonalDateTime.toString() should not contain UTC-literal [#623]

## [v3.29] published on 2017-02-22
### Added
- More convenient access to platform timezone data [#616]

### Fixed
- Improve and fix parsing of decimal elements [#617]

## [v3.28] published on 2017-02-06
### Added
- Static from(GregorianDate)-factories for partial calendar classes [#600]
- New counting method for class LeapSeconds [#608]
- Support different time-scales in formatting and parsing moments [#607]
- Duration conversion to clock units including days [#605]
- Make parsing of ambivalent zone informations easier [#603]

### Changed
- Adjust leap second expiry date [#601]

### Fixed
- Invalid CalendarWeek throws wrong exception [#612]
- equals-contract for ChronoFormatter including zone elements broken [#604]

## [v3.27] published on 2017-01-08
### Added
- Support for Breton (br), Faroese (fo), Western Frisian (fy), Scottish Gaelic (gd), Luxembourgish (lb) [#599]
- Reduce buffer allocation when printing numbers [#598]

### Fixed
- ChronoFormatter.formatToBuffer() should be optimized [#597]

## [v3.26] published on 2017-01-03
### Added
- Add formatting/parsing to class MachineTime [#593]
- Enable or-logic when parsing durations [#592]
- Ensure 4 digit-years in date style patterns [#591]
- Dozenal numbering system [#596]
- Document how to handle wrong platform tz-data on Android [#589]
- Make styled formatters sensible for change of locale [#586]

### Fixed
- Max-calculation for week-of-year sometimes broken [#595]
- RFC-1123-formatter must support northamerican zones [#594]
- MultiFormatParser tolerates trailing characters [#590]
- BridgeChronology: Some format patterns are not runnable [#588]
- BridgeChronology: Some formatters cannot be built [#587]

## [v3.25] published on 2016-12-03
### Added
- Interval search tree [#575]
- Generic interval type for arbitrary foreign types [#578]

### Changed
- Simplify parsing of trailing characters [#576]

### Fixed
- Fix for pattern sanity check [#583]
- Regression: Misleading error message [#581]
- Clarify documentation of AdjustableElement.atFloor() and .atCeiling() [#580]
- Ambivalence parsing check sometimes faulty [#577]
- Unicode-BIDI-chars should not be parsed in context of ISO [#574]

## [v3.24] published on 2016-11-01
### Added
- Open ChronoFormatter for external types [#556]
- Week calculations for non-ISO calendars [#562]
- Add convenient methods to PrettyTime [#559]
- New method nowInSystemTime() also for CalendarXYZ-types [#557]
- Splitting of interval-collections [#571]
- New method `isDisjunct()` in class IntervalCollection [#572]

### Fixed
- Years in far future should not require a positive sign [#569]
- Document pattern order in case of or-logic applied [#558]

### Changed
- Add simple sanity checks for cldr-format patterns [#565]
- Update resources to CLDR v30.0.2 [#570]

## [v3.23] published on 2016-09-18
### Added
- Add historic centuries [#507]
- Add rounding support for MachineTime.dividedBy(long) [#546]
- Simple way needed to add MachineTime<SI> to a moment [#547]
- Add some alternative numbering systems [#550]
- Add convenient methods to determine current system time [#553]

### Fixed
- PUSH-FORWARD-strategy is not suitable for intervals [#545]
- NPE in Timezone.getDisplayName(...) [#548]
- Improved literal parsing in localized tz-offset [#549]
- Byte-order-marks in UTF8-resources not recognized [#551]

### Changed
- New method in ZoneNameProvider-interface [#549]

### Deprecated
- One division method in MachineTime without rounding parameter [#546]

## [v3.22] published on 2016-08-28
### Added
- Recurrent intervals as defined in ISO-8601 [#488]
- Add localized display names for `ChronoElement` [#515]
- Support for annual dates [#514]
- Improved documentation of HijriCalendar [#518]
- Add general parse methods for dates in ISO-8601-format [#520]
- Iso8601Format....WALL_TIME now understands T17:45 [#521]
- Embedded formatter should know outer format attributes and defaults [#522]
- ChronoFormatter.Builder should define default values and global attributes [#531]
- New date-interval-method to convert to a moment interval [#534]
- New interval boundary manipulations based on operators [#538]
- Determine first moment for given calendar date in time zone [#539]
- Enable parsing of literals with leading digits after numerical elements [#544]

### Fixed
- Prescan phase of iso interval parser sometimes incorrect [#530]
- AM/PM-element not sensible for output context [#529]
- Inconsistent TimeSpan-behaviour of negative MachineTime [#540]
- Inconsistent exception handling of with()-methods [#541]

### Changed
- New leap second at end of year 2016 [#517]
- Make fractional second parser more tolerant [#519]
- TextProvider-interface and TransitionHistory-interface with new methods [#523]+[#529]

### Deprecated
- 1 method in TextProvider deprecated due to missing output context [#529]
- 1 method in TransitionHistory marked as deprecated (for future migration) [#523]

## [v3.21] published on 2016-06-20
### Added
- Introduce class for year-week-combination [#508]
- Introduce class for year-month-combination [#503]
- Introduce class for year-quarter-combination [#504]
- Model calendar year as date interval [#505]
- Add timespan classes Years, Quarters, Months and Weeks [#506]
- Better support for week-based durations and units [#509]

### Fixed
- Duration measured in millis only is broken [#513]
- Zone offset without sign - should be parseable in lax mode [#502]

## [v3.20] published on 2016-05-07
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

## [v3.19] published on 2016-04-18
### Added
- Add convenient abuts-method to IsoInterval [#486]
- Add boolean query to determine if two intervals intersect [#485]
- Add Thai solar calendar (Suriyakati) [#478]
- Fine-tuning of historization [#479]
- Document that PlainDate is historizable [#448]

### Fixed
- OR-operator logic in ChronoFormatter sometimes broken [#482]
- Fix and improve documentation of era manipulations [#481]

### Deprecated
- Deprecate usage of CalendarEra.getValue() [#480]

## [v3.18] published on 2016-03-23
### Added
- Enable unparseable characters to be skipped when parsing [#476]
- Move SQL-support to core-module [#471]
- Add SQL-support for type TIMESTAMP WITH TIMEZONE [#472]
- Historic year definition (example Easter style in France) [#473]
- Support for Kirghiz [#474]

## [v3.17] published on 2016-03-13
### Added
- More detailed error message when parsing ZonalDateTime [#467]

### Fixed
- Make resolving zone names more robust [#469]
- Calculus Pisanus does not work in parsing [#466]

## [v3.16] published on 2016-03-06
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

## [v3.15] published on 2016-02-14
### Added
- Proleptic Julian calendar [#444]
- Add Roman numerals [#443]

### Fixed
- Unit-between-arithmetic broken for Persian, Coptic and Ethiopian calendar [#453]
- Improve overall performance including two new methods in low-level interfaces [#450]
- Text resources for am-pm are mismatched (in non-iso-calendars) [#452]
- Add equals/hashCode-support for platform formatter [#451]
- Prohibit use of Ethiopian hour with PlainTime [#449]
- Code example in documentation of MultiFormatParser is incomplete [#446]

## [v3.14] published on 2016-01-22
### Added
- Two new approximation normalizers based on max unit only [#442]
- Add pattern-based factory method to ChronoFormatter for any chronology [#441]
- Introduce or-operator in formatting via builder and pattern [#437}
- Add other historic eras [#436]
- Support for various historical New Year events [#434]
- Date arithmetic on HijriCalendar [#429]
- New methods `withVariant(...)` on CalendarVariant [#428]
- Dual parsing of embedded or standalone formats [#427]
- Dedicated MultiFormatParser [#426]
- Support for Indian languages gu, kn, mr, pa, ta [#439]
- Support for Burmese (my - Myanmar) [#438]

### Fixed
- Setting proleptic julian history on formatter is ignored [#435]
- Semantic of changing historic era in ChronoHistory unclear [#430]

### Changed
- Update leap second expiry date to 2016-12-28 [#431]

## [v3.13] published on 2015-12-31
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

## [v3.12] published on 2015-11-30
### Added
- Support for Bengali [#409]
- Ethiopian time should support element PlainTime.COMPONENT [#408]

### Fixed
- HijriAlgorithm calendar variant broken [#407]
- Broken javadoc links to some serial forms [#406]
- British date format should be in order DMY [#403]

### Removed
- Tidy deprecated elements [#404]

## [v3.11] published on 2015-11-22
### Added
- Support for Sinhalese [#402]
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

## [v3.10] published on 2015-10-22
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

### Changed
- Better translations for various countries (mainly arabic and french) [#383]
- Better translations for Spanish locales in Latin America [#368]
- Low-level interface ChronoMerger with new method [#377]
- Review plural rules [#366]
- Update i18n-data to CLDR-28 [#374]
- Change ISO-8601-fallback-mechanism to CLDR standard [#373]
- FormatPatternProvider.getDateTimePattern() with 3 args [#100]

### Removed
- FormatPatternProvider.DEFAULT [#100]

## [v3.9] published on 2015-10-01
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

## [v3.8] published on 2015-09-15
### Added
- Support for Malay language [#353]
- Unify CalendarVariant and Calendrical by new interface CalendarDate [#350]
- New XOR-operator for interval collections [#347]
- Make PatternType.CLDR useable for non-iso-dates [#348]
- New calendar-related class GeneralTimestamp [#340]
- New intersect-operator for interval collections [#345]

### Fixed
- Disable parsing of ambivalent text forms [#351]

### Changed
- Rename unit pattern resource file names [#352]
- CalendarVariant and Calendrical changed Temporal signature [#350]
- Two specialized now()-methods in ZonalClock changed the return type [#340]

## [v3.7] published on 2015-09-03
### Added
- New method IntervalCollection.getRange() [#344]
- PrettyTime.printRelativeOrDate() using PlainDate [#343]
- Moment-class with a new precision element [#342]
- Add plus/minus-operators for IntervalCollection [#339]

### Fixed
- Fix for generics-related problem in PatternType [#346]

### Changed
- IntervalCollection.union() should create a merged collection [#338]

## [v3.6] published on 2015-08-31
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

## [v3.5] published on 2015-08-18
### Added
- Umalqura calendar for Saudi-Arabia (experimental) [#313]
- Start-of-day-feature, relevant for current date in islamic calendar [#323]
- New pattern type for generic date chronologies [#324]
- New transformation methods for calendrical objects [#318]

### Deprecated
- Interface `ExtZoneProvider` no longer used

### Changed
- Narrow value-type of BasicElement to Comparable [#321]

### Removed
- Android-module dropped in favour of new stand-alone project Time4A [#326]

## [v3.4] published on 2015-07-06
### Added
- New pattern type CLDR_24 with support for ISO-hour in range 0-24 [#314]
- Convenient method `printRelativeInStdTimezone(moment)` for `PrettyTime` [#311]
- Introduce calendar families with day calculations [#315]

### Fixed
- Using PrettyTime on Android - null pointer exception [#316]
- Remove unnecessary warning message in ChronoFormatter [#312]

## [v3.3] published on 2015-06-26
### Added
- Generic clock-based creation of any `ChronoEntity` [#303]
- Add clock-methods `inPlatformView()` [#304]
- Register `PlainTime.PRECISION` in `PlainTimestamp` [#306]
- Improve adjacent digit parsing of 4-digit-years [#307]
- Language support for Irish (ga) [#309]

### Fixed
- Merger-factories for clock-based objects of basic types too lenient [#302]
- Don't cache system timezone in  SystemClock.inLocalView()  [#305]

## [v3.2] published on 2015-06-15
### Added
- Add Timezone-Change-Receiver for Android [#289]
- Monotonic clocks including support for real-time on Android [#295]
- Queries for dst+raw-offsets in `Timezone` [#288]
- Language support for Belorusian (be), Hebrew (he, iw), Bosnian (bs) [#290, #292, #294]

### Fixed
- Android does not accept `ResourceBundle.Control.getFormats()` [#291]
- `LeapSeconds.enhance(unixTime)` does not follow spec [#296]
- Version-compatibility-bug in tzdata-1.2-2015d [#298]

## [v3.1] published on 2015-05-25
### Added
- Add pattern-based factory methods to `ChronoFormatter` [#273]
- Handle min/max-date for gregorian cutover better [#274]
- Enhance `ZonalOffset.parse(...)` [#275]
- Add option for parsing an alternative literal char [#278]
- Add serialization feature to `ZonalDateTime` [#285]
- Complete localized era name support [#286]
- Support latin era names in non-English languages [#287]

### Fixed
- Sectional format attributes should always be explicit [#264]
- `ChronoFormatter.toFormat()` broken with some `FieldPosition`-args [#265]
- Javadoc-Fix for `ChronoFormatter.toFormat()` [#267]
- Define and document appropriate types for `TemporalFormatter` [#271]
- `Duration` cannot process -1sec + 999_999_999ns = -1ns [#272]
- Inner/nested formatters should accept trailing characters in parsing [#279]
- Ensure change of language without side effects [#280]
- Allow 5 symbols for narrow form of quarter-of-year [#281]
- Parsing timezone-IDs is sometimes broken [#284]

### Changed
- Hidden support for `ZonalDateTime` in `Platform.PATTERN` dropped [#271]

## [v3.0] published on 2015-04-30
- First release of version line v3.x, contains a pluggable format engine and historization features.
- The older version lines v1.x and v2.x are no longer supported and have reached end of life.
