## [v5.7] not yet released
### Fixed
- United Kingdom date format [#908]
- Package `net.time4j.calendar.hindu` not exported for jigsaw [#906]

## [v5.6] published on 2020-05-31
### Added
- Old Hindu calendar [#892]
- Local variant data for Hijri calendar [#888]
- Getter for observer zone id in `SolarTime` and `LunarTime` [#904]
- Support for Duration.in(Collection<? extends Unit>) [#899]
- Support for Swiss German (Schwyzerdütsch) [#895]
- Let TimeSource deliver current Instant [#884]
- Make PatternType.CLDR_DATE being applicable on PlainDate [#889]

### Deprecated
- `HijriCalendar.VARIANT_ICU4J` scheduled for future removal [#905]

### Fixed
- NPE in deserializing of SolarTime and LunarTime [#901]
- Build problem: More tolerant double comparisons in astro tests [#886]
- Incorrect sign in last term of calculation of mean lunar anomaly [#891]
- Documentation example in SolarTime outdated [#902]

## [v5.5] published on 2019-11-04
### Added
- Reversible time metric [#881]
- Coptic calendar for JavaFX-CalendarPicker [#882]

### Deprecated
- Rename the terms "standard offset" and "daylight-saving-offset" in related methods [#874]

### Fixed
- Document that Hebrew calendar starting at 18:00 is an approximation for sunset [#873]

## [v5.4] published on 2019-05-25
### Added
- Localized stream of weekdays [#871]
- Current calendar week with generic parameters [#870]
- Anomalistic month (apogee/perigee of moon) [#859]
- FULL_DAY-instance in ClockInterval [#867]
- Support for Kurdish (ku) and Somali (so) languages [#866]
- More translations for Badi calendar [#862]
- More translations for new Japanese era REIWA [#860]

### Changed
- Update to CLDR v35.1 [#863]

### Fixed
- Improved approximated normalization of durations [#869]
- Zero clock hour not tolerable in smart parsing [#868]
- Plural rules for Marathi and Nepali are wrong [#865]
- Smart parsing of protected space char [#864]
- Printing of weekdays in Bahai calendar broken [#861]

## [v5.3] published on 2019-04-16
### Added
- Badi calendar (Bahai) [#798]
- Documentation of calendar view customization in JavaFX-CalendarPicker [#857]

### Changed
- New Japanese Nengo "Reiwa" [#840]
- More flexible dynamic patterns [#854]

## [v5.2] published on 2018-12-21
### Added
- Easier truncation of durations with any arbitrary units [#850]
- Easier calculation of Chinese holidays [#844]
- Ethiopian calendar for JavaFX-CalendarPicker [#843]
- New static factory method for astronomical seasons based on a moment [#841]
- Custom duration separators in `PrettyTime` [#839]

### Fixed
- Handle Sindhi and Uyghur as right-to-left [#847]
- AstronomicalSeason fails for years like 999_999_999 [#842]

## [v5.1] published on 2018-11-20
### Added
- Support for languages Assamese, Sindhi and Tongan [#834]
- Generic calendar formatting with regional calendar preference [#833]
- Localized representation of ISO calendar week [#832]
- Formatting relative times like "last Monday" or "next Friday" [#733]

### Changed
- Make parsing month names slightly more tolerant in smart mode [#837]
- Update to CLDR 34 [#831]

## [v5.0] published on 2018-10-23
### Added
- Time arithmetic in class GeneralTimestamp [#810]
- Support tzdb-time-switches out of range T00:00/T24:00 [#825]
- New random()-methods in IsoInterval-subclasses [#827]
- New methods Temporal.isBeforeOrEqual() and Temporal.isAfterOrEqual() [#826]
- New interval class for calendar years, quarters, months or weeks [#822]
- Formatted representation of Years, Quarters, Months and weeks [#824]
- Make enums Month, Weekday and Quarter to operators for PlainDate [#819]
- Enhance duration comparators [#816]
- Partitioned streams in TimestampInterval [#814]
- Easy summing up a stream of durations [#812]
- Simplify bridge chronology accessors in basic types [#811]
- Platform-Timezone now delegates to `ZoneId.systemDefault()` [#805]
- Text elements formattable by help of string converter [#799]
- Extend formattability of extreme integer element values [#797]
- Resolve locale no-NO-NY to nynorsk [#788]

### Changed
- Simplify/Rename some interval factory methods [#821]
- Remove confusing method `ZonalClock.currentMoment()` [#817]
- Improved handling of negative DST-offsets [#742]
- Simplify generic formatter API [#813]
- Remove all deprecated stuff [#524]
- Improve conversion of Windows zones to IANA [#785]
- Move class `AnnualDate` to main package [#787]
- Move class `MachineTime` to main package [#609]
- Replace `ResourceBundle` in module environment [#786]
- Automatic module names (for Java-9) [#784]
- New modular structure [#525]
- `StartOfDay` now uses `CalendarDate` in abstract method [#655]
- Interface `ChronoPrinter` without checked exceptions [#526]
- Renaming of abstract method in `DayPartitionRule` [#815]

### Fixed
- Ensure that big year numbers with 10 digits can be printed [#792]
- SimpleInterval.Factory is wrong about open or closed interval boundaries [#823]
- CalendarWeek.of(GregorianMath.MAX_YEAR, 52) aborts [#820]
- Mismatch between getMinimumSinceUTC and transform for some historic calendars [#808]
- Fix for narrow era names which had been incomplete [#809]
- Fix for changing `JapaneseCalendar.MONTH_AS_ORDINAL` [#807]
- NPE-Fix for loading time zone based on system time zone identifier [#803]
- NPE-Fix for premature assignment of system time zone [#802]
- Wrong day-of-year in HebrewCalendar [#800]
- Rethrow undocumented ArithmeticException as IllegalArgumentException [#791]
- Wrong days-since-UTC after transform in Hijri adjustment [#789]

## [v4.38] published on 2018-05-18
### Added
- Support Kabyle language [#782]
- Show tomorrow and yesterday words in PrettyTime [#781]

### Fixed
- Arithmetic overflow of int-results in JulianCalendar.Unit.between(...) [#775]
- Incorrect translation of "M" pattern in JulianCalendar formatter [#776]
- Same exit and arrival of sun/moon in sign of Scorpius [#778]

## [v4.37] published on 2018-05-11
### Added
- Zodiac constellations and signs [#765]
- Empty date intervals [#771]
- Allow more temporal types for use with Duration.toTemporalAmount() [#769]
- New interface describing equatorial coordinates [#764]
- Static validation method for calendar weeks [#766]

### Changed
- Update some resources to CLDR 33 [#774]

### Deprecated
- Replace/Remove `SolarTime.Calculator.declination(double)` [#772]

### Fixed
- Right ascension of moon position should be in range 0-360 [#770]
- Use automatic fallback to worldwide locale in WindowsZone [#763]

## [v4.36] published on 2018-03-24
### Added
- Reverse engineering of windows zone names [#756]
- Normalization of timezone identifiers [#756]
- Support Asturian language (ISO-639: ast) [#757]
- Convenience week elements for Hebrew calendar [#758]
- Support of Hebrew calendar in JavaFX-CalendarPicker [#759]
- Support of Julian calendar in JavaFX-CalendarPicker [#760]

### Fixed
- Make astronomical calculations possible with leap seconds disabled [#761]

## [v4.35] published on 2018-03-07
### Added
- Traditional Chinese calendar [#396]
- Dangi calendar (Korean) [#722]
- Vietnamese lunar calendar [#641]
- Cyclic year used in East Asian calendars [#638]
- Juche calendar (North Korea) [#748]
- Stream-methods in MomentInterval [#743]
- Shadow length of objects with specified height [#754]
- Increase displayed precision of moon illumination [#747]
- Convenience constants for calendar-specific week elements [#753]
- Bounded calendar-week-elements in CommonElements [#738]
- Optimize chronological extensions during parsing [#749]
- Improved localization for French Rev. calendar [#741]

### Fixed
- Weekmodel.ISO.boundedWeekOfMonth().atFloor() not working as expected [#750]

## [v4.34] published on 2018-01-15
### Added
- Extra features for StdSolarCalculator.CC [#731]
- Search for moon phase at or after a moment [#730]
- Traditional Chinese now recognizes hant-script in locale [#728]

### Changed
- Allow negative DST-offsets [#735]
- Update leap second expiry date [#734]

### Fixed
- date.getMaximum(<week-related-element>) can crash near end of timeline [#732]

## [v4.33] published on 2017-12-18
### Added
- Moon rise/set [#704]
- Calculation for azimuth/elevation of Sun and Moon [#723]
- Positions of Sun and Moon in terms of right ascension and declination [#716]
- Make right ascension of sun accessible [#715]
- Determine min/max-range of possible lunations [#720]
- Convenience combination of operators atFloor() and newValue() [#721]

### Changed/Deprecated
- HebrewMonth.Order.BIBILICAL contains a typo [#714]
- Four methods in SolarTime loose the day information [#724]

### Fixed
- StdSolarCalculator.CC has integer-division-error [#725]
- Class SolarTime normally expects LMT-dates not zoned dates [#719]
- JulianDay misses definition of serialVersionUID [#727]

## [v4.32] published on 2017-11-26
### Added
- Hebrew calendar [#528]
- Hebrew time [#708]
- Hebrew birthdays and yahrzeit [#707]
- Constants of solar time for Jerusalem and Mecca [#711]
- Support unicode extensions fw and rg [#712]
- Add Odia (Oriya) language [#709]
- More flexible numberings of enums in formatting [#706]
- Update to CLDR v32 [#690]
- Conversions to/from old java.util.Calendar + java.util.TimeZone [#705]

### Changed
- Optimize ZoneNameProviderSPI [#713]

## [v4.31] published on 2017-10-20
### Added
- Moon phases [#676]
- Illumination of moon [#702]
- Historic calendar [#698]
- New SolarTime.Calculator based on Dershowitz/Reingold [#701]
- Static factory ChronoHistory.from(variantString) [#697]
- Extended support for always-intervals [#695]
- Generic calendar intervals [#675]
- TimeLine-enhancement [#675]
- Julian centuries with J2000-epoch [#693]
- Document which unicode-ca-extensions are supported [#699]

### Changed
- German names of French Republican calendar months [#692]
- Prevent calling some SolarTime.Builder-methods twice [#691]
- Refine altitude-dependent calculation of solar time [#689]

### Fixed
- Unicode-ca-support not available for HijriCalendar [#700]
- ChronoHistory.month()-annotation is incomplete [#696]

### Deprecated
- Constants in SolarTime.Calculator marked for removal [#701]
- Make surrounding()-method in MomentInterval consistently using APIs [#694]

## [v4.30] published on 2017-09-25
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
- Conversion of Time4J-Duration to temporal amount sometimes broken [#680]

### Deprecated
- Rename PlainTime.ISO_HOUR to HOUR_FROM_0_TO_24 [#685]

## [v4.29] published on 2017-09-10
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
- Conversion of Duration to Java-8 broken if fractional seconds occur [#669]
- French revolutionary calendar not serializable [#664]

## [v4.28] published on 2017-07-28
### Added
- French Revolutionary Calendar [#615]
- New formatter method 'getPattern()' [#662]
- Support for or-logic during printing [#661]
- Prevent escaping of Z-literal in format patterns [#658]
- Easy conversion between MachineTime and JSR-310 [#660]
- Conversion methods for Weekday and Month [#657]
- Direct parse-methods for Weekday, Quarter and Month [#656]
- Weekday-in-month in non-iso-calendars [#653]
- Alternative calculations for PersianCalendar [#634]
- Calculation of astronomical seasons [#628]
- Support for apparent solar time [#633]
- Support for Julian Day Number [#527]
- Implement timescales TT and UT1 [#93]

### Changed
- Redefine value space and epoch of TAI [#652]

### Deprecated
- Make PatternType-API fit for more calendars [#659]
- Prepare StartOfDay-change for next major release [#655]

## [v4.27.2] published on 2017-04-29
### Fixed
- Setting the number system via nu-extension in locale broken [#650]

## [v4.27] published on 2017-04-28
### Added
- Japanese imperial calendar [#560]
- Indian national calendar (Saka) [#642]
- Custom day adjustments on HijriCalendar [#649]
- Make pivot-year for two-digit-years calendar-specific [#643]
- Support for Japanese numbers [#639]
- Improve text resource handling [#626]
- Method nowInSystemTime() for `AnnualDate` and `EthiopianTime` [#646]
- Enable calendar lookup by names [#614]
- Make transformation between calendar dates easier [#648]

### Fixed
- DAY_OF_WEEK-element in some calendars inconsistent [#644]
- Clarify usage of Ethiopian eras [#636]
- TransitionHistory.getPreviousTransition() has improper description [#637]
- Roundtrip of print/parse during zone offset overlap fails [#635]
- Converting geo longitude to ZonalOffset crashes [#632]

## [v4.26] published on 2017-03-27
### Added
- Add ALWAYS-constant to DateInterval [#630]
- Assignment of values to intervals [#629]
- Make zone offset calculation for geo-longitude more precise [#622]
- Enable text-lookup for non-enum elements in formatting [#618]
- Support for Ewe language [#625]

### Fixed
- NPE in IntervalTree containing interval with infinite end [#627]
- ZonalDateTime.toString() should not contain UTC-literal [#623]

## [v4.25] published on 2017-02-22
### Added
- More convenient access to platform timezone data [#616]

### Fixed
- Improve and fix parsing of decimal elements [#617]

## [v4.24] published on 2017-02-06
### Added
- Stream support for fixed calendar intervals [#613]
- New HolidayModel-interface [#487]
- New streaming method for DateInterval with exclusion [#610]
- Support unicode-nu-extension in formatting and parsing [#611]
- Static from(GregorianDate)-factories for partial calendar classes [#600]
- New stream- and counting methods for class LeapSeconds [#608]
- Support different time-scales in formatting and parsing moments [#607]
- Duration conversion to clock units including days [#605]
- Make parsing of ambivalent zone informations easier [#603]

### Changed
- Adjust leap second expiry date [#601]

### Fixed
- Invalid CalendarWeek throws wrong exception [#612]
- equals-contract for ChronoFormatter including zone elements broken [#604]

## [v4.23] published on 2017-01-08
### Added
- Support for Breton (br), Faroese (fo), Western Frisian (fy), Scottish Gaelic (gd), Luxembourgish (lb) [#599]
- Support for generic timezone names [#491]
- Reduce buffer allocation when printing numbers [#598]

### Fixed
- ChronoFormatter.formatToBuffer() should be optimized [#597]

## [v4.22] published on 2017-01-03
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

## [v4.21] published on 2016-12-03
### Added
- Interval search tree [#575]
- Generic interval type for arbitrary foreign types [#578]
- Enable open start of intervals [#584]

### Changed
- Simplify parsing of trailing characters [#576]

### Fixed
- Fix for pattern sanity check [#583]
- Regression: Misleading error message [#581]
- Clarify documentation of AdjustableElement.atFloor() and .atCeiling() [#580]
- Ambivalence parsing check sometimes faulty [#577]
- Unicode-BIDI-chars should not be parsed in context of ISO [#574]

## [v4.20] published on 2016-11-01
### Added
- JavaFX-CalendarPicker [#561]
- Open ChronoFormatter for external types [#556]
- Add some convenient methods around day partition rules [#563]
- Week calculations for non-ISO calendars [#562]
- Add convenient methods to PrettyTime [#559]
- New method nowInSystemTime() also for CalendarXYZ-types [#557]
- Splitting of interval-collections [#571]
- New method `isDisjunct()` in class IntervalCollection [#572]
- New PatternType.DYNAMIC [#567]
- Support span of weekdays [#568]
- Bridge chronologies for AnnualDate, CalendarYear and CalendarMonth [#566]

### Fixed
- Years in far future should not require a positive sign [#569]
- Document pattern order in case of or-logic applied [#558]

### Changed
- Add simple sanity checks for cldr-format patterns [#565]
- Update resources to CLDR v30.0.2 [#570]

### Deprecated
- Specialized threeten-methods outdated due to a more general approach [#556]

## [v4.19] published on 2016-09-18
### Added
- Add historic centuries [#507]
- Add rounding support for MachineTime.dividedBy(long) [#546]
- Simple way needed to add MachineTime<SI> to a moment [#547]
- Add some alternative numbering systems [#550]
- Add active filter to DayPartitionBuilder [#552]
- Add convenient methods to determine current system time [#553]

### Fixed
- PUSH-FORWARD-strategy is not suitable for intervals [#545]
- NPE in Timezone.getDisplayName(...) [#548]
- Improved literal parsing in localized tz-offset [#549]
- Byte-order-marks in UTF8-resources not recognized [#551]

### Deprecated
- One division method in MachineTime without rounding parameter [#546]

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
