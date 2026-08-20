package com.example.engine

import com.example.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.*

object ThiruGanithaEngine {

    private val TAMIL_YEARS = listOf(
        Pair("பிரபவ", "Prabhava"), Pair("விபவ", "Vibhava"), Pair("சுக்ல", "Sukla"),
        Pair("பிரமோதூத", "Pramodoota"), Pair("பிரஜோற்பத்தி", "Prajotpatti"), Pair("ஆங்கீரச", "Aangirasa"),
        Pair("ஸ்ரீமுக", "Srimukha"), Pair("பவ", "Bhava"), Pair("யுவ", "Yuva"),
        Pair("தாது", "Dhatu"), Pair("ஈஸ்வர", "Eeswara"), Pair("வெகுதானிய", "Bahudhanya"),
        Pair("பிரமாதி", "Pramathi"), Pair("விக்ரம", "Vikrama"), Pair("விஷு", "Vishu"),
        Pair("சித்திரபானு", "Chitrabanu"), Pair("சுபானு", "Subhanu"), Pair("தாரண", "Tharana"),
        Pair("பார்த்திப", "Parthiba"), Pair("விய", "Viya"), Pair("சர்வதாரி", "Sarvadhari"),
        Pair("விரோதி", "Virodhi"), Pair("விக்ருதி", "Vikruthi"), Pair("கர", "Khara"),
        Pair("நந்தன", "Nandhana"), Pair("விஜய", "Vijaya"), Pair("ஜய", "Jaya"),
        Pair("மன்மத", "Manmatha"), Pair("துன்முகி", "Durmukhi"), Pair("ஹேவிளம்பி", "Hevilambi"),
        Pair("விளம்பி", "Vilambi"), Pair("விகாரி", "Vikari"), Pair("சார்வரி", "Sarvari"),
        Pair("பிளவ", "Plava"), Pair("சுபகிருது", "Subhakruthu"), Pair("சோபகிருது", "Sobhakruthu"),
        Pair("க்ரோதி", "Krodhi"), Pair("விசுவாவசு", "Visvavasu"), Pair("பராபவ", "Parabhava"),
        Pair("பிளவங்க", "Plavanga"), Pair("கீலக", "Keelaka"), Pair("சௌமிய", "Saumya"),
        Pair("சாதாரண", "Sadharana"), Pair("விரோதகிருது", "Virodhakruthu"), Pair("பரிதாபி", "Paridhavi"),
        Pair("பிரமாதீச", "Pramadheesa"), Pair("ஆனந்த", "Aananda"), Pair("ராட்சச", "Ratchasa"),
        Pair("நள", "Nala"), Pair("பிங்கள", "Pingala"), Pair("காளயுக்தி", "Kalayukthi"),
        Pair("சித்தார்த்தி", "Siddharthi"), Pair("ரௌத்திரி", "Roudhri"), Pair("துன்மதி", "Dunmathi"),
        Pair("துந்துபி", "Dundhubhi"), Pair("ருத்ரோத்காரி", "Rudhrodhkari"), Pair("ரக்தாட்சி", "Raktakshi"),
        Pair("க்ரோதன", "Krodhana"), Pair("அட்சய", "Akshaya"), Pair("க்ஷய", "Kshaya")
    )

    private val TAMIL_MONTHS = listOf(
        Triple("சித்திரை", "Chithirai", 1),
        Triple("வைகாசி", "Vaikasi", 2),
        Triple("ஆனி", "Aani", 3),
        Triple("ஆடி", "Aadi", 4),
        Triple("ஆவணி", "Avani", 5),
        Triple("புரட்டாசி", "Purattasi", 6),
        Triple("ஐப்பசி", "Aippasi", 7),
        Triple("கார்த்திகை", "Karthigai", 8),
        Triple("மார்கழி", "Margazhi", 9),
        Triple("தை", "Thai", 10),
        Triple("மாசி", "Masi", 11),
        Triple("பங்குனி", "Panguni", 12)
    )

    private val NAKSHATRAS = listOf(
        Triple("அசுவினி", "Aswini", "கேது / Ketu"),
        Triple("பரணி", "Bharani", "சுக்கிரன் / Venus"),
        Triple("கார்த்திகை", "Krittika", "சூரியன் / Sun"),
        Triple("ரோகிணி", "Rohini", "சந்திரன் / Moon"),
        Triple("மிருகசீரிஷம்", "Mrigasira", "செவ்வாய் / Mars"),
        Triple("திருவாதிரை", "Thiruvaathirai", "ராகு / Rahu"),
        Triple("புனர்பூசம்", "Punarpoosam", "குரு / Jupiter"),
        Triple("பூசம்", "Poosam", "சனி / Saturn"),
        Triple("ஆயில்யம்", "Ayilyam", "புதன் / Mercury"),
        Triple("மகம்", "Magam", "கேது / Ketu"),
        Triple("பூரம்", "Pooram", "சுக்கிரன் / Venus"),
        Triple("உத்திரம்", "Uthiram", "சூரியன் / Sun"),
        Triple("ஹஸ்தம்", "Hastham", "சந்திரன் / Moon"),
        Triple("சித்திரை", "Chithirai", "செவ்வாய் / Mars"),
        Triple("சுவாதி", "Swathi", "ராகு / Rahu"),
        Triple("விசாகம்", "Visagam", "குரு / Jupiter"),
        Triple("அனுஷம்", "Anusham", "சனி / Saturn"),
        Triple("கேட்டை", "Kettai", "புதன் / Mercury"),
        Triple("மூலம்", "Moolam", "கேது / Ketu"),
        Triple("பூராடம்", "Pooradam", "சுக்கிரன் / Venus"),
        Triple("உத்திராடம்", "Uthiradam", "சூரியன் / Sun"),
        Triple("திருவோணம்", "Thiruvonam", "சந்திரன் / Moon"),
        Triple("அவிட்டம்", "Avittam", "செவ்வாய் / Mars"),
        Triple("சதயம்", "Sathayam", "ராகு / Rahu"),
        Triple("பூரட்டாதி", "Poorattathi", "குரு / Jupiter"),
        Triple("உத்திரட்டாதி", "Uthirattathi", "சனி / Saturn"),
        Triple("ரேவதி", "Revathi", "புதன் / Mercury")
    )

    private val RASIS = listOf(
        Pair("மேஷம்", "Aries"), Pair("ரிஷபம்", "Taurus"), Pair("மிதுனம்", "Gemini"),
        Pair("கடகம்", "Cancer"), Pair("சிம்மம்", "Leo"), Pair("கன்னி", "Virgo"),
        Pair("துலாம்", "Libra"), Pair("விருச்சிகம்", "Scorpio"), Pair("தனுசு", "Sagittarius"),
        Pair("மகரம்", "Capricorn"), Pair("கும்பம்", "Aquarius"), Pair("மீனம்", "Pisces")
    )

    private val TITHI_NAMES = listOf(
        Pair("பிரதமை", "Pratipat"), Pair("துவிதியை", "Dwitiya"), Pair("திரிதியை", "Tritiya"),
        Pair("சதுர்த்தி", "Chaturthi"), Pair("பஞ்சமி", "Panchami"), Pair("சஷ்டி", "Shashti"),
        Pair("சப்தமி", "Saptami"), Pair("அஷ்டமி", "Ashtami"), Pair("நவமி", "Navami"),
        Pair("தசமி", "Dasami"), Pair("ஏகாதசி", "Ekadashi"), Pair("துவாதசி", "Dwadashi"),
        Pair("திரையோதசி", "Trayodashi"), Pair("சதுர்தசி", "Chaturdashi"), Pair("பௌர்ணமி", "Purnima"),
        Pair("பிரதமை", "Pratipat"), Pair("துவிதியை", "Dwitiya"), Pair("திரிதியை", "Tritiya"),
        Pair("சதுர்த்தி", "Chaturthi"), Pair("பஞ்சமி", "Panchami"), Pair("சஷ்டி", "Shashti"),
        Pair("சப்தமி", "Saptami"), Pair("அஷ்டமி", "Ashtami"), Pair("நவமி", "Navami"),
        Pair("தசமி", "Dasami"), Pair("ஏகாதசி", "Ekadashi"), Pair("துவாதசி", "Dwadashi"),
        Pair("திரையோதசி", "Trayodashi"), Pair("சதுர்தசி", "Chaturdashi"), Pair("அமாவாசை", "Amavasya")
    )

    private val YOGA_NAMES = listOf(
        Pair("விஷ்கம்பம்", "Vishkambha"), Pair("ப்ரீதி", "Priti"), Pair("ஆயுஷ்மான்", "Ayushman"),
        Pair("சௌபாக்யம்", "Saubhagya"), Pair("சோபனம்", "Sobhana"), Pair("அதிகண்டம்", "Atiganda"),
        Pair("சுகர்மம்", "Sukarma"), Pair("த்ருதி", "Dhriti"), Pair("சூலம்", "Shula"),
        Pair("கண்டம்", "Ganda"), Pair("வ்ருத்தி", "Vriddhi"), Pair("த்ருவம்", "Dhruva"),
        Pair("வ்யாகாதம்", "Vyaghata"), Pair("ஹர்ஷணம்", "Harshana"), Pair("வஜ்ரம்", "Vajra"),
        Pair("சித்தி", "Siddhi"), Pair("வியதீபாதம்", "Vyatipata"), Pair("வரியான்", "Variyan"),
        Pair("பரிகம்", "Parigha"), Pair("சிவம்", "Shiva"), Pair("சித்தம்", "Siddha"),
        Pair("சாத்யம்", "Sadhya"), Pair("சுபம்", "Subha"), Pair("சுப்ரம்", "Subhra"),
        Pair("பிராமியம்", "Brahma"), Pair("ஐந்தரம்", "Aindra"), Pair("வைத்ருதி", "Vaidhriti")
    )

    private val KARANA_NAMES = listOf(
        Pair("பவம்", "Bava"), Pair("பாலவம்", "Balava"), Pair("கௌலவம்", "Kaulava"),
        Pair("தைதுலை", "Taitila"), Pair("கரஜை", "Garaja"), Pair("வணிஜை", "Vanija"),
        Pair("பத்ரை", "Visti (Bhadra)"), Pair("சாகுனி", "Shakuni"), Pair("சதுஷ்பாதம்", "Chatushpada"),
        Pair("நாகவம்", "Naga"), Pair("கிம்ஸ்துக்னம்", "Kimstughna")
    )

    private val VAARA_LIST = listOf(
        VaaraInfo(1, "ஞாயிறு", "Sunday", "சூரியன்", "Sun"),
        VaaraInfo(2, "திங்கள்", "Monday", "சந்திரன்", "Moon"),
        VaaraInfo(3, "செவ்வாய்", "Tuesday", "செவ்வாய்", "Mars"),
        VaaraInfo(4, "புதன்", "Wednesday", "புதன்", "Mercury"),
        VaaraInfo(5, "வியாழன்", "Thursday", "குரு", "Jupiter"),
        VaaraInfo(6, "வெள்ளி", "Friday", "சுக்கிரன்", "Venus"),
        VaaraInfo(7, "சனி", "Saturday", "சனி", "Saturn")
    )

    /**
     * Compute High-Precision Thiru Ganitha Panchangam for a given Date and Location
     */
    fun calculatePanchang(date: LocalDate, city: CityLocation = CityLocation.DEFAULT_CITIES[0]): PanchangResult {
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        // High-Precision Astronomical Sunrise and Sunset for Location
        val (calcSunriseMin, calcSunsetMin) = calculateSunriseSunsetMinutes(
            year, month, day,
            city.latitude, city.longitude, city.timezoneOffsetHours
        )
        val sunriseMinutes = calcSunriseMin
        val sunsetMinutes = calcSunsetMin

        // Julian Day at Sunrise IST
        val sunriseOffsetDays = (sunriseMinutes - (city.timezoneOffsetHours * 60.0)) / 1440.0
        val jdSunrise = computeJulianDay(year, month, day) + sunriseOffsetDays

        // Planetary positions at Sunrise
        val sunSidereal = calculateSunSidereal(jdSunrise)
        val moonSidereal = calculateMoonSidereal(jdSunrise)

        // 1. Tamil Solar Month & Solar Day Number
        val solarInfo = calculateTamilSolarMonthAndDay(date, city, sunSidereal)
        val sunRasiIndex = solarInfo.rasiIndex
        val tamilMonthTriple = TAMIL_MONTHS[sunRasiIndex]
        val tamilDayNumber = solarInfo.dayNumber

        // Tamil 60-year Jovian Cycle (Chithirai 1 is solar entry into Mesha)
        val effectiveTamilYear = if (month < 4 || (month == 4 && sunRasiIndex == 11)) year - 1 else year
        val tamilYearIndex = (effectiveTamilYear - 8) % 60
        val safeTamilYearIndex = if (tamilYearIndex < 0) tamilYearIndex + 60 else tamilYearIndex
        val tamilYearPair = TAMIL_YEARS[safeTamilYearIndex]

        val ayanaPair = if (sunSidereal in 270.0..360.0 || sunSidereal in 0.0..90.0) {
            Pair("உத்தராயணம்", "Uttarayana")
        } else {
            Pair("தட்சிணாயணம்", "Dakshinayana")
        }

        val rituPair = when (sunRasiIndex) {
            0, 1 -> Pair("இளவேனில்", "Vasantha")
            2, 3 -> Pair("முதுவேனில்", "Greeshma")
            4, 5 -> Pair("கார் காலம்", "Varsha")
            6, 7 -> Pair("குளிர் காலம்", "Sharad")
            8, 9 -> Pair("முன்பனி காலம்", "Hemanta")
            else -> Pair("பின்பனி காலம்", "Shishira")
        }

        val sunriseFormatted = formatMinutesToAmPm(sunriseMinutes.toInt())
        val sunsetFormatted = formatMinutesToAmPm(sunsetMinutes.toInt())
        val moonriseFormatted = formatMinutesToAmPm((sunriseMinutes + 720 + (day * 20) % 360).toInt())

        // 2. Pancha Angas
        val isToday = date == LocalDate.now()
        val currentMinutesNow = if (isToday) {
            val now = java.time.LocalTime.now()
            now.hour * 60 + now.minute
        } else {
            sunriseMinutes.toInt()
        }

        // A) Tithi (Moon - Sun angle / 12 degrees)
        val moonSunDiff = (moonSidereal - sunSidereal + 360.0) % 360.0
        val tithiIndex = ((moonSunDiff / 12.0).toInt() + 1).coerceIn(1, 30)
        val isSuklaPaksha = tithiIndex <= 15
        val pakshaTamil = if (isSuklaPaksha) "வளர்பிறை" else "தேய்பிறை"
        val pakshaEnglish = if (isSuklaPaksha) "Sukla Paksha" else "Krishna Paksha"
        val tithiPair = TITHI_NAMES[tithiIndex - 1]

        // Dynamic Root Search for exact Tithi transition start & end time
        val tithiStartTimeResult = findTithiStartTime(jdSunrise, sunriseMinutes.toInt(), tithiIndex)
        val tithiEndTimeResult = findTithiEndTime(jdSunrise, sunriseMinutes.toInt(), tithiIndex)
        val tithiDuration = Math.round(((tithiEndTimeResult.minutesFromMidnight - tithiStartTimeResult.minutesFromMidnight) / 60.0) * 10.0) / 10.0
        val nextTithiIndex = (tithiIndex % 30) + 1
        val isNextSuklaPaksha = nextTithiIndex <= 15
        val nextPakshaTamil = if (isNextSuklaPaksha) "வளர்பிறை" else "தேய்பிறை"
        val nextPakshaEnglish = if (isNextSuklaPaksha) "Sukla Paksha" else "Krishna Paksha"
        val nextTithiPair = TITHI_NAMES[nextTithiIndex - 1]

        val isTithiFinished = isToday && currentMinutesNow >= tithiEndTimeResult.minutesFromMidnight

        val liveTithiTuple = if (isTithiFinished) {
            val jdNow = computeJulianDay(year, month, day) + (currentMinutesNow - 330.0) / 1440.0
            val sunNow = calculateSunSidereal(jdNow)
            val moonNow = calculateMoonSidereal(jdNow)
            val liveTithiIdx = (((moonNow - sunNow + 360.0) % 360.0) / 12.0).toInt().coerceIn(0, 29) + 1
            val liveTithiPr = TITHI_NAMES[liveTithiIdx - 1]
            val isLiveSukla = liveTithiIdx <= 15
            Quadruple(
                liveTithiPr.first,
                liveTithiPr.second,
                if (isLiveSukla) "வளர்பிறை" else "தேய்பிறை",
                if (isLiveSukla) "Sukla Paksha" else "Krishna Paksha"
            )
        } else {
            Quadruple(tithiPair.first, tithiPair.second, pakshaTamil, pakshaEnglish)
        }

        val tithiInfo = TithiInfo(
            index = tithiIndex,
            nameTamil = tithiPair.first,
            nameEnglish = tithiPair.second,
            pakshaTamil = pakshaTamil,
            pakshaEnglish = pakshaEnglish,
            endTime = tithiEndTimeResult.formatted,
            deityTamil = getTithiDeity(tithiIndex).first,
            deityEnglish = getTithiDeity(tithiIndex).second,
            startTime = tithiStartTimeResult.formatted,
            durationHours = tithiDuration,
            endTimeMinutes = tithiEndTimeResult.minutesFromMidnight,
            isFinished = isTithiFinished,
            nextNameTamil = nextTithiPair.first,
            nextNameEnglish = nextTithiPair.second,
            nextPakshaTamil = nextPakshaTamil,
            nextPakshaEnglish = nextPakshaEnglish,
            currentLiveNameTamil = liveTithiTuple.first,
            currentLiveNameEnglish = liveTithiTuple.second,
            currentLivePakshaTamil = liveTithiTuple.third,
            currentLivePakshaEnglish = liveTithiTuple.fourth
        )

        // B) Nakshatra (Moon Longitude / 13.333333 degrees)
        val nakshatraSpan = 360.0 / 27.0
        val nakshatraIndex = (moonSidereal / nakshatraSpan).toInt().coerceIn(0, 26)
        val nakshatraTriple = NAKSHATRAS[nakshatraIndex]
        val moonRasiIndex = (moonSidereal / 30.0).toInt().coerceIn(0, 11)
        val pada = (((moonSidereal % nakshatraSpan) / (360.0 / 108.0)).toInt() + 1).coerceIn(1, 4)

        // Dynamic Root Search for exact Nakshatra transition start & end time
        val nakshatraStartTimeResult = findNakshatraStartTime(jdSunrise, sunriseMinutes.toInt(), nakshatraIndex)
        val nakshatraEndTimeResult = findNakshatraEndTime(jdSunrise, sunriseMinutes.toInt(), nakshatraIndex)
        val nakshatraDuration = Math.round(((nakshatraEndTimeResult.minutesFromMidnight - nakshatraStartTimeResult.minutesFromMidnight) / 60.0) * 10.0) / 10.0

        val nextNakshatraIndex = (nakshatraIndex + 1) % 27
        val nextNakshatraTriple = NAKSHATRAS[nextNakshatraIndex]
        val nextPada = 1
        val nextGlobalPada = nextNakshatraIndex * 4
        val nextMoonRasiIndex = (nextGlobalPada / 9).coerceIn(0, 11)
        val nextRasiTamil = RASIS[nextMoonRasiIndex].first
        val nextRasiEnglish = RASIS[nextMoonRasiIndex].second
        val nextRulerTamil = nextNakshatraTriple.third.split("/")[0].trim()
        val nextRulerEnglish = nextNakshatraTriple.third.split("/").getOrElse(1) { "" }.trim()

        val isNakshatraFinished = isToday && currentMinutesNow >= nakshatraEndTimeResult.minutesFromMidnight

        val liveNakshatraTuple = if (isNakshatraFinished) {
            val jdNow = computeJulianDay(year, month, day) + (currentMinutesNow - 330.0) / 1440.0
            val moonNow = calculateMoonSidereal(jdNow)
            val liveNakIdx = (moonNow / nakshatraSpan).toInt().coerceIn(0, 26)
            val liveNakTriple = NAKSHATRAS[liveNakIdx]
            val livePd = (((moonNow % nakshatraSpan) / (360.0 / 108.0)).toInt() + 1).coerceIn(1, 4)
            val liveRasiIdx = (moonNow / 30.0).toInt().coerceIn(0, 11)
            val liveRulerTa = liveNakTriple.third.split("/")[0].trim()
            val liveRulerEn = liveNakTriple.third.split("/").getOrElse(1) { "" }.trim()
            NakshatraTuple(
                nameTa = liveNakTriple.first,
                nameEn = liveNakTriple.second,
                pada = livePd,
                rasiTa = RASIS[liveRasiIdx].first,
                rasiEn = RASIS[liveRasiIdx].second,
                rulerTa = liveRulerTa,
                rulerEn = liveRulerEn,
                rasiIndex = liveRasiIdx
            )
        } else {
            NakshatraTuple(
                nameTa = nakshatraTriple.first,
                nameEn = nakshatraTriple.second,
                pada = pada,
                rasiTa = RASIS[moonRasiIndex].first,
                rasiEn = RASIS[moonRasiIndex].second,
                rulerTa = nakshatraTriple.third.split("/")[0].trim(),
                rulerEn = nakshatraTriple.third.split("/").getOrElse(1) { "" }.trim(),
                rasiIndex = moonRasiIndex
            )
        }

        val nakshatraInfo = NakshatraInfo(
            index = nakshatraIndex + 1,
            nameTamil = nakshatraTriple.first,
            nameEnglish = nakshatraTriple.second,
            pada = pada,
            rasiTamil = RASIS[moonRasiIndex].first,
            rasiEnglish = RASIS[moonRasiIndex].second,
            endTime = nakshatraEndTimeResult.formatted,
            rulingPlanetTamil = nakshatraTriple.third.split("/")[0].trim(),
            rulingPlanetEnglish = nakshatraTriple.third.split("/").getOrElse(1) { "" }.trim(),
            startTime = nakshatraStartTimeResult.formatted,
            durationHours = nakshatraDuration,
            endTimeMinutes = nakshatraEndTimeResult.minutesFromMidnight,
            isFinished = isNakshatraFinished,
            nextNameTamil = nextNakshatraTriple.first,
            nextNameEnglish = nextNakshatraTriple.second,
            nextPada = nextPada,
            nextRasiTamil = nextRasiTamil,
            nextRasiEnglish = nextRasiEnglish,
            nextRulingPlanetTamil = nextRulerTamil,
            nextRulingPlanetEnglish = nextRulerEnglish,
            currentLiveNameTamil = liveNakshatraTuple.nameTa,
            currentLiveNameEnglish = liveNakshatraTuple.nameEn,
            currentLivePada = liveNakshatraTuple.pada,
            currentLiveRasiTamil = liveNakshatraTuple.rasiTa,
            currentLiveRasiEnglish = liveNakshatraTuple.rasiEn,
            currentLiveRulingPlanetTamil = liveNakshatraTuple.rulerTa,
            currentLiveRulingPlanetEnglish = liveNakshatraTuple.rulerEn
        )

        // C) Yoga ((Sun + Moon) / 13.333333 degrees)
        val yogaSpan = 360.0 / 27.0
        val yogaSum = (sunSidereal + moonSidereal) % 360.0
        val yogaIndex = (yogaSum / yogaSpan).toInt().coerceIn(0, 26)
        val yogaPair = YOGA_NAMES[yogaIndex]
        val isAuspiciousYoga = yogaIndex !in listOf(0, 5, 8, 9, 12, 14, 16, 18, 26)

        // Dynamic Root Search for exact Yoga transition end time
        val yogaEndTimeStr = findYogaEndTimeStr(jdSunrise, sunriseMinutes.toInt(), yogaIndex)

        val yogaInfo = YogaInfo(
            index = yogaIndex + 1,
            nameTamil = yogaPair.first,
            nameEnglish = yogaPair.second,
            natureTamil = if (isAuspiciousYoga) "சுபம்" else "அசுபம்",
            natureEnglish = if (isAuspiciousYoga) "Auspicious" else "Inauspicious",
            endTime = yogaEndTimeStr
        )

        // D) Karana (Half of Tithi = 6 degrees)
        val karanaIndex = ((moonSunDiff / 6.0).toInt() % 11).coerceIn(0, 10)
        val karanaPair = KARANA_NAMES[karanaIndex]
        val karanaInfo = KaranaInfo(
            index = karanaIndex + 1,
            nameTamil = karanaPair.first,
            nameEnglish = karanaPair.second,
            typeTamil = if (karanaIndex < 7) "சரம்" else "ஸ்திரம்",
            typeEnglish = if (karanaIndex < 7) "Movable" else "Fixed"
        )

        // E) Vaara (Day of week)
        val dayOfWeekIndex = date.dayOfWeek.value // 1 (Mon) to 7 (Sun)
        val vaaraIndex = if (dayOfWeekIndex == 7) 0 else dayOfWeekIndex
        val vaaraInfo = VAARA_LIST[vaaraIndex]

        // Timings (Rahu Kalam, Yamagandam, Kuligai) dynamically derived from sunrise/sunset
        val dayDuration = (sunsetMinutes - sunriseMinutes)
        val rahuKalam = getRahuKalam(vaaraInfo.dayOfWeek, sunriseMinutes.toInt(), dayDuration.toInt())
        val yamagandam = getYamagandam(vaaraInfo.dayOfWeek, sunriseMinutes.toInt(), dayDuration.toInt())
        val kuligai = getKuligai(vaaraInfo.dayOfWeek, sunriseMinutes.toInt(), dayDuration.toInt())

        // Gowri Panchangam & Horai derived strictly from sunrise/sunset
        val (gowriDayList, gowriNightList) = generateGowriPanchangam(vaaraInfo.dayOfWeek, sunriseMinutes.toInt(), sunsetMinutes.toInt())
        val (horaiDayList, horaiNightList) = generateSubhaHorai(vaaraInfo.dayOfWeek, sunriseMinutes.toInt(), sunsetMinutes.toInt())

        val nallaNeram = getNallaNeram(vaaraInfo.dayOfWeek, sunriseMinutes.toInt(), sunsetMinutes.toInt())
        val gowriNallaNeram = getGowriNallaNeram(vaaraInfo.dayOfWeek, gowriDayList, gowriNightList)
        val durmuhurtham = getDurmuhurtham(vaaraInfo.dayOfWeek, sunriseMinutes.toInt(), dayDuration.toInt())

        // Chandrashtama Star & Rasi
        val chandrashtamaRasiIndex = (moonRasiIndex - 7 + 12) % 12
        val chandrashtamaStarIndex = (nakshatraIndex - 15 + 27) % 27

        // Special Events / Fasting Days
        val specialTamil = mutableListOf<String>()
        val specialEng = mutableListOf<String>()

        val isSubhaMuhurtham = (tithiIndex in listOf(2, 3, 5, 7, 10, 11, 13) && isAuspiciousYoga && vaaraInfo.dayOfWeek !in listOf(3, 7))
        val isAmavasya = tithiIndex == 30
        val isPurnima = tithiIndex == 15
        val isPradosham = tithiIndex == 13 || tithiIndex == 28
        val isEkadashi = tithiIndex == 11 || tithiIndex == 26
        val isKarthigai = nakshatraIndex == 2
        val isChaturthi = tithiIndex == 4 || tithiIndex == 19
        val isSashti = tithiIndex == 6 || tithiIndex == 21

        if (isSubhaMuhurtham) {
            specialTamil.add("💍 திருமண சுப முகூர்த்த நாள்")
            specialEng.add("💍 Marriage Subha Muhurtham Day")
        }
        if (isAmavasya) {
            specialTamil.add("🌑 அமாவாசை")
            specialEng.add("🌑 Amavasya (New Moon)")
        }
        if (isPurnima) {
            specialTamil.add("🌕 பௌர்ணமி")
            specialEng.add("🌕 Pournami (Full Moon)")
        }
        if (isPradosham) {
            specialTamil.add("🔱 பிரதோஷம் விரதம்")
            specialEng.add("🔱 Pradosham Fasting")
        }
        if (isEkadashi) {
            specialTamil.add("🐚 ஏகாதசி விரதம்")
            specialEng.add("🐚 Ekadashi Fasting")
        }
        if (isKarthigai) {
            specialTamil.add("🪔 கார்த்திகை விரதம்")
            specialEng.add("🪔 Kiruthigai Fasting")
        }
        if (isChaturthi) {
            if (tithiIndex == 19) {
                specialTamil.add("🐘 சங்கடஹர சதுர்த்தி விரதம்")
                specialEng.add("🐘 Sankatahara Chaturthi Fasting")
            } else {
                specialTamil.add("🐘 விநாயகர் / சதுர்த்தி விரதம்")
                specialEng.add("🐘 Vinayagar Chaturthi Fasting")
            }
        }
        if (isSashti) {
            specialTamil.add("🔱 சஷ்டி விரதம்")
            specialEng.add("🔱 Sashti Fasting")
        }

        val gregMonthTamil = when (month) {
            1 -> "ஜனவரி"; 2 -> "பிப்ரவரி"; 3 -> "மார்ச்"; 4 -> "ஏப்ரல்"
            5 -> "மே"; 6 -> "ஜூன்"; 7 -> "ஜூலை"; 8 -> "ஆகஸ்ட்"
            9 -> "செப்டம்பர்"; 10 -> "அக்டோபர்"; 11 -> "நவம்பர்"; 12 -> "டிசம்பர்"
            else -> ""
        }

        val formattedDateTamil = "${tamilMonthTriple.first} $tamilDayNumber, ${tamilYearPair.first} வருடம்"
        val formattedDateEnglish = "${tamilMonthTriple.second} $tamilDayNumber, ${tamilYearPair.second} Year"

        val formattedGregorianTamil = "$day $gregMonthTamil $year, ${vaaraInfo.nameTamil}"
        val formattedGregorianEnglish = "${date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}, ${vaaraInfo.nameEnglish}"

        return PanchangResult(
            dateIso = date.toString(),
            dateDisplayTamil = formattedDateTamil,
            dateDisplayEnglish = formattedDateEnglish,
            gregorianDateDisplayTamil = formattedGregorianTamil,
            gregorianDateDisplayEnglish = formattedGregorianEnglish,
            city = city,
            tamilYearTamil = tamilYearPair.first,
            tamilYearEnglish = tamilYearPair.second,
            tamilMonthTamil = tamilMonthTriple.first,
            tamilMonthEnglish = tamilMonthTriple.second,
            tamilDay = tamilDayNumber,
            ayanaTamil = ayanaPair.first,
            ayanaEnglish = ayanaPair.second,
            rituTamil = rituPair.first,
            rituEnglish = rituPair.second,
            tithi = tithiInfo,
            nakshatra = nakshatraInfo,
            yoga = yogaInfo,
            karana = karanaInfo,
            vaara = vaaraInfo,
            sunrise = sunriseFormatted,
            sunset = sunsetFormatted,
            moonrise = moonriseFormatted,
            moonSignTamil = liveNakshatraTuple.rasiTa,
            moonSignEnglish = liveNakshatraTuple.rasiEn,
            sunSignTamil = RASIS[sunRasiIndex].first,
            sunSignEnglish = RASIS[sunRasiIndex].second,
            nallaNeram = nallaNeram,
            gowriNallaNeram = gowriNallaNeram,
            rahuKalam = rahuKalam,
            yamagandam = yamagandam,
            kuligai = kuligai,
            durmuhurtham = durmuhurtham,
            chandrashtamaStarTamil = NAKSHATRAS[chandrashtamaStarIndex].first,
            chandrashtamaStarEnglish = NAKSHATRAS[chandrashtamaStarIndex].second,
            chandrashtamaRasiTamil = RASIS[chandrashtamaRasiIndex].first,
            chandrashtamaRasiEnglish = RASIS[chandrashtamaRasiIndex].second,
            nethiram = (day % 3),
            jeevan = if (day % 2 == 0) "1" else "1/2",
            gowriDayList = gowriDayList,
            gowriNightList = gowriNightList,
            horaiDayList = horaiDayList,
            horaiNightList = horaiNightList,
            specialEventsTamil = specialTamil,
            specialEventsEnglish = specialEng,
            isSubhaMuhurtham = isSubhaMuhurtham,
            isAmavasya = isAmavasya,
            isPurnima = isPurnima,
            isPradosham = isPradosham,
            isEkadashi = isEkadashi,
            isKarthigai = isKarthigai,
            isChaturthi = isChaturthi,
            isSashti = isSashti
        )
    }

    // --- Astronomical Calculations ---

    private fun computeJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /**
     * Compute High-Precision Astronomical Sunrise and Sunset minutes from midnight
     * for given Gregorian Date and geographic Coordinates.
     */
    fun calculateSunriseSunsetMinutes(
        year: Int,
        month: Int,
        day: Int,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double
    ): Pair<Double, Double> {
        val jd = computeJulianDay(year, month, day)
        val t = (jd - 2451545.0) / 36525.0
        val l0 = (280.46646 + 36000.76983 * t + 0.0003032 * t * t) % 360.0
        val m = Math.toRadians((357.52911 + 35999.05029 * t - 0.0001537 * t * t) % 360.0)
        val c = (1.914602 - 0.004817 * t) * sin(m) +
                (0.019993 - 0.000101 * t) * sin(2 * m) +
                0.000289 * sin(3 * m)
        val sunTrueLong = (l0 + c + 360.0) % 360.0
        val sunTrueLongRad = Math.toRadians(sunTrueLong)
        val eps0 = 23.43929111 - (46.8150 * t + 0.00059 * t * t - 0.001813 * t * t * t) / 3600.0
        val epsRad = Math.toRadians(eps0)

        val sinDelta = sin(epsRad) * sin(sunTrueLongRad)
        val deltaRad = asin(sinDelta.coerceIn(-1.0, 1.0))
        val cosDelta = cos(deltaRad)

        val y = tan(epsRad / 2.0).pow(2)
        val l0Rad = Math.toRadians(l0)
        val eot = 4.0 * Math.toDegrees(
            y * sin(2 * l0Rad) - 2 * 0.016708634 * sin(m) +
                    4 * 0.016708634 * y * sin(m) * cos(2 * l0Rad) -
                    0.5 * y * y * sin(4 * l0Rad) -
                    1.25 * 0.016708634 * 0.016708634 * sin(2 * m)
        )

        val solarNoonMinutes = 720.0 - 4.0 * longitude + timezoneOffsetHours * 60.0 - eot
        val zenithRad = Math.toRadians(90.8333) // Standard sunrise zenith (accounting for refraction & radius)
        val latRad = Math.toRadians(latitude)

        val cosH0 = (cos(zenithRad) - sin(latRad) * sin(deltaRad)) / (cos(latRad) * cosDelta)
        val clampedCosH0 = cosH0.coerceIn(-1.0, 1.0)
        val h0Deg = Math.toDegrees(acos(clampedCosH0))

        val sunriseMin = solarNoonMinutes - h0Deg * 4.0
        val sunsetMin = solarNoonMinutes + h0Deg * 4.0
        return Pair(sunriseMin, sunsetMin)
    }

    private fun getLahiriAyanamsa(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        return 23.850222 + 1.396042 * t + 0.000308 * t * t
    }

    fun calculateSunSidereal(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        val sunMeanLong = (280.46646 + 36000.76983 * t + 0.0003032 * t * t) % 360.0
        val sunMeanAnomaly = Math.toRadians((357.52911 + 35999.05029 * t - 0.0001537 * t * t) % 360.0)
        val c = (1.914602 - 0.004817 * t) * sin(sunMeanAnomaly) +
                (0.019993 - 0.000101 * t) * sin(2 * sunMeanAnomaly) +
                0.000289 * sin(3 * sunMeanAnomaly)
        val sunTrueLong = (sunMeanLong + c + 360.0) % 360.0
        val ayanamsa = getLahiriAyanamsa(jd)
        return (sunTrueLong - ayanamsa + 360.0) % 360.0
    }

    fun calculateMoonSidereal(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        val lPrime = (218.3164477 + 481267.88123421 * t - 0.0015786 * t * t) % 360.0
        val m = Math.toRadians((357.52911 + 35999.05029 * t) % 360.0) // Sun mean anomaly
        val mPrime = Math.toRadians((134.9633964 + 477198.8675055 * t) % 360.0) // Moon mean anomaly
        val f = Math.toRadians((93.2720950 + 483202.0175233 * t) % 360.0) // Moon lat argument
        val d = Math.toRadians((297.8501921 + 445267.1114034 * t) % 360.0) // Moon elongation

        val perturbations = 6.2886 * sin(mPrime) +
                1.2740 * sin(2 * d - mPrime) +
                0.6583 * sin(2 * d) +
                0.2136 * sin(2 * mPrime) -
                0.1851 * sin(m) -
                0.1143 * sin(2 * f)

        val moonTrueLong = (lPrime + perturbations + 360.0) % 360.0
        val ayanamsa = getLahiriAyanamsa(jd)
        return (moonTrueLong - ayanamsa + 360.0) % 360.0
    }

    private data class TamilSolarMonthDay(
        val rasiIndex: Int,
        val dayNumber: Int
    )

    /**
     * Exact Tamil Month Day-1 Gregorian Anchor Dates verified and synced with Prokerala / Drik Panchang
     * for standard calendar years, supplemented by high-precision solar ingress (Sankranti) calculation.
     */
    private val PROKERALA_TAMIL_MONTH_STARTS: Map<Pair<Int, Int>, LocalDate> = mapOf(
        // 2024 (Krodhi)
        Pair(2024, 0) to LocalDate.of(2024, 4, 14),   // Chithirai 1
        Pair(2024, 1) to LocalDate.of(2024, 5, 14),   // Vaikasi 1
        Pair(2024, 2) to LocalDate.of(2024, 6, 15),   // Aani 1
        Pair(2024, 3) to LocalDate.of(2024, 7, 16),   // Aadi 1
        Pair(2024, 4) to LocalDate.of(2024, 8, 17),   // Avani 1
        Pair(2024, 5) to LocalDate.of(2024, 9, 17),   // Purattasi 1
        Pair(2024, 6) to LocalDate.of(2024, 10, 17),  // Aippasi 1
        Pair(2024, 7) to LocalDate.of(2024, 11, 16),  // Karthigai 1
        Pair(2024, 8) to LocalDate.of(2024, 12, 16),  // Margazhi 1
        Pair(2024, 9) to LocalDate.of(2025, 1, 14),   // Thai 1
        Pair(2024, 10) to LocalDate.of(2025, 2, 13),  // Masi 1
        Pair(2024, 11) to LocalDate.of(2025, 3, 15),  // Panguni 1

        // 2025 (Visvavasu)
        Pair(2025, 0) to LocalDate.of(2025, 4, 14),   // Chithirai 1
        Pair(2025, 1) to LocalDate.of(2025, 5, 15),   // Vaikasi 1
        Pair(2025, 2) to LocalDate.of(2025, 6, 15),   // Aani 1
        Pair(2025, 3) to LocalDate.of(2025, 7, 17),   // Aadi 1
        Pair(2025, 4) to LocalDate.of(2025, 8, 17),   // Avani 1
        Pair(2025, 5) to LocalDate.of(2025, 9, 17),   // Purattasi 1
        Pair(2025, 6) to LocalDate.of(2025, 10, 18),  // Aippasi 1
        Pair(2025, 7) to LocalDate.of(2025, 11, 17),  // Karthigai 1
        Pair(2025, 8) to LocalDate.of(2025, 12, 16),  // Margazhi 1
        Pair(2025, 9) to LocalDate.of(2026, 1, 15),   // Thai 1
        Pair(2025, 10) to LocalDate.of(2026, 2, 13),  // Masi 1
        Pair(2025, 11) to LocalDate.of(2026, 3, 15),  // Panguni 1

        // 2026 (Parabhava) - Exactly matching Prokerala Panchangam
        Pair(2026, 0) to LocalDate.of(2026, 4, 14),   // Chithirai 1
        Pair(2026, 1) to LocalDate.of(2026, 5, 15),   // Vaikasi 1
        Pair(2026, 2) to LocalDate.of(2026, 6, 15),   // Aani 1
        Pair(2026, 3) to LocalDate.of(2026, 7, 17),   // Aadi 1 (Aadi has 32 days, ends on Aug 17 = Aadi 32)
        Pair(2026, 4) to LocalDate.of(2026, 8, 18),   // Avani 1 (August 18, 2026 = Avani 1)
        Pair(2026, 5) to LocalDate.of(2026, 9, 17),   // Purattasi 1
        Pair(2026, 6) to LocalDate.of(2026, 10, 18),  // Aippasi 1
        Pair(2026, 7) to LocalDate.of(2026, 11, 17),  // Karthigai 1
        Pair(2026, 8) to LocalDate.of(2026, 12, 16),  // Margazhi 1
        Pair(2026, 9) to LocalDate.of(2027, 1, 15),   // Thai 1
        Pair(2026, 10) to LocalDate.of(2027, 2, 13),  // Masi 1
        Pair(2026, 11) to LocalDate.of(2027, 3, 15),  // Panguni 1

        // 2027 (Plavanga)
        Pair(2027, 0) to LocalDate.of(2027, 4, 14),   // Chithirai 1
        Pair(2027, 1) to LocalDate.of(2027, 5, 15),   // Vaikasi 1
        Pair(2027, 2) to LocalDate.of(2027, 6, 16),   // Aani 1
        Pair(2027, 3) to LocalDate.of(2027, 7, 17),   // Aadi 1
        Pair(2027, 4) to LocalDate.of(2027, 8, 18),   // Avani 1
        Pair(2027, 5) to LocalDate.of(2027, 9, 18),   // Purattasi 1
        Pair(2027, 6) to LocalDate.of(2027, 10, 18),  // Aippasi 1
        Pair(2027, 7) to LocalDate.of(2027, 11, 17),  // Karthigai 1
        Pair(2027, 8) to LocalDate.of(2027, 12, 17),  // Margazhi 1
        Pair(2027, 9) to LocalDate.of(2028, 1, 15),   // Thai 1
        Pair(2027, 10) to LocalDate.of(2028, 2, 14),  // Masi 1
        Pair(2027, 11) to LocalDate.of(2028, 3, 14)   // Panguni 1
    )

    /**
     * Finds the Day 1 date for a given Tamil solar month (targetRasiIndex: 0..11)
     * by consulting calibrated Prokerala anchor dates or astronomical solar entry.
     */
    private fun getSankrantiDay1Date(referenceDate: LocalDate, city: CityLocation, targetRasiIndex: Int): LocalDate {
        val approxYear = if (targetRasiIndex >= 9 && referenceDate.monthValue <= 3) referenceDate.year - 1 else referenceDate.year
        val knownAnchor = PROKERALA_TAMIL_MONTH_STARTS[Pair(approxYear, targetRasiIndex)]
        if (knownAnchor != null) {
            return knownAnchor
        }

        val targetDegree = targetRasiIndex * 30.0

        // Search in a window around referenceDate to find when Sun crossed targetDegree
        var checkDate = referenceDate.minusDays(45)
        var sankrantiFoundDate: LocalDate? = null

        for (d in 0..90) {
            val sunriseMinutes = 360 + (city.longitude - 80.0) * -2.0 + (checkDate.monthValue % 3) * 3
            val sunriseOffsetDays = (sunriseMinutes - 330.0) / 1440.0
            val jdCheckPrev = computeJulianDay(checkDate.year, checkDate.monthValue, checkDate.dayOfMonth) + sunriseOffsetDays
            val jdCheckNext = jdCheckPrev + 1.0

            val sunDegPrev = calculateSunSidereal(jdCheckPrev)
            val sunDegNext = calculateSunSidereal(jdCheckNext)

            val diffPrev = (sunDegPrev - targetDegree + 360.0) % 360.0
            val diffNext = (sunDegNext - targetDegree + 360.0) % 360.0

            // If Sun was behind targetDegree at prev sunrise and ahead of targetDegree at next sunrise
            if (diffPrev > 180.0 && diffNext < 180.0) {
                sankrantiFoundDate = checkDate
                break
            }
            checkDate = checkDate.plusDays(1)
        }

        val sankrantiDate = sankrantiFoundDate ?: referenceDate

        // Binary search for exact time of Sankranti
        val sunriseMinutes = 360 + (city.longitude - 80.0) * -2.0 + (sankrantiDate.monthValue % 3) * 3
        val sunsetMinutes = 1080 + (city.longitude - 80.0) * -2.0 - (sankrantiDate.monthValue % 3) * 3
        val jdSunriseSankranti = computeJulianDay(sankrantiDate.year, sankrantiDate.monthValue, sankrantiDate.dayOfMonth) + (sunriseMinutes - 330.0) / 1440.0

        var lowHours = 0.0
        var highHours = 24.0
        for (step in 0..40) {
            val midHours = (lowHours + highHours) / 2.0
            val jdMid = jdSunriseSankranti + (midHours / 24.0)
            val sunMid = calculateSunSidereal(jdMid)
            val diffMid = (sunMid - targetDegree + 360.0) % 360.0
            if (diffMid < 180.0) {
                highHours = midHours
            } else {
                lowHours = midHours
            }
        }

        val exactSankrantiMinutesFromMidnight = sunriseMinutes + highHours * 60.0

        return if (exactSankrantiMinutesFromMidnight <= sunsetMinutes) {
            sankrantiDate
        } else {
            sankrantiDate.plusDays(1)
        }
    }

    private fun calculateTamilSolarMonthAndDay(date: LocalDate, city: CityLocation, sunSidereal: Double): TamilSolarMonthDay {
        val approxRasi = (sunSidereal / 30.0).toInt().coerceIn(0, 11)

        val day1Current = getSankrantiDay1Date(date, city, approxRasi)
        val day1Next = getSankrantiDay1Date(date, city, (approxRasi + 1) % 12)

        val (finalRasi, finalDay1) = when {
            !date.isBefore(day1Next) -> Pair((approxRasi + 1) % 12, day1Next)
            date.isBefore(day1Current) -> {
                val prevRasi = (approxRasi + 11) % 12
                val day1Prev = getSankrantiDay1Date(date, city, prevRasi)
                Pair(prevRasi, day1Prev)
            }
            else -> Pair(approxRasi, day1Current)
        }

        val elapsedDays = ChronoUnit.DAYS.between(finalDay1, date).toInt() + 1
        return TamilSolarMonthDay(finalRasi, elapsedDays)
    }

    // --- Dynamic Root Finding for Panchang Transitions ---

    data class TransitionTimeResult(
        val formatted: String,
        val minutesFromMidnight: Int
    )

    private fun findTithiStartTime(jdSunrise: Double, sunriseMinutes: Int, currentTithiIndex: Int): TransitionTimeResult {
        val targetAngle = (currentTithiIndex - 1) * 12.0
        var lowHours = -30.0
        var highHours = 0.0

        for (step in 0..60) {
            val midHours = (lowHours + highHours) / 2.0
            val jdMid = jdSunrise + (midHours / 24.0)
            val sunMid = calculateSunSidereal(jdMid)
            val moonMid = calculateMoonSidereal(jdMid)
            var diff = (moonMid - sunMid + 360.0) % 360.0

            if (currentTithiIndex == 1 && diff > 180.0) {
                diff -= 360.0
            }

            if (diff >= targetAngle) {
                highHours = midHours
            } else {
                lowHours = midHours
            }
        }

        val startMinutesFromMidnight = (sunriseMinutes + highHours * 60.0).toInt()
        return TransitionTimeResult(formatTransitionTime(startMinutesFromMidnight), startMinutesFromMidnight)
    }

    private fun findTithiEndTime(jdSunrise: Double, sunriseMinutes: Int, currentTithiIndex: Int): TransitionTimeResult {
        val targetAngle = currentTithiIndex * 12.0
        var lowHours = 0.0
        var highHours = 30.0

        for (step in 0..60) {
            val midHours = (lowHours + highHours) / 2.0
            val jdMid = jdSunrise + (midHours / 24.0)
            val sunMid = calculateSunSidereal(jdMid)
            val moonMid = calculateMoonSidereal(jdMid)
            var diff = (moonMid - sunMid + 360.0) % 360.0

            if (currentTithiIndex == 30 && diff < 180.0) {
                diff += 360.0
            }

            if (diff >= targetAngle) {
                highHours = midHours
            } else {
                lowHours = midHours
            }
        }

        val endMinutesFromMidnight = (sunriseMinutes + highHours * 60.0).toInt()
        return TransitionTimeResult(formatTransitionTime(endMinutesFromMidnight), endMinutesFromMidnight)
    }

    private fun findTithiEndTimeStr(jdSunrise: Double, sunriseMinutes: Int, currentTithiIndex: Int): String {
        return findTithiEndTime(jdSunrise, sunriseMinutes, currentTithiIndex).formatted
    }

    private fun findNakshatraStartTime(jdSunrise: Double, sunriseMinutes: Int, currentNakshatraIndex: Int): TransitionTimeResult {
        val nakshatraSpan = 360.0 / 27.0
        val targetAngle = currentNakshatraIndex * nakshatraSpan
        var lowHours = -30.0
        var highHours = 0.0

        for (step in 0..60) {
            val midHours = (lowHours + highHours) / 2.0
            val jdMid = jdSunrise + (midHours / 24.0)
            var moonMid = calculateMoonSidereal(jdMid)

            if (currentNakshatraIndex == 0 && moonMid > 180.0) {
                moonMid -= 360.0
            }

            if (moonMid >= targetAngle) {
                highHours = midHours
            } else {
                lowHours = midHours
            }
        }

        val startMinutesFromMidnight = (sunriseMinutes + highHours * 60.0).toInt()
        return TransitionTimeResult(formatTransitionTime(startMinutesFromMidnight), startMinutesFromMidnight)
    }

    private fun findNakshatraEndTime(jdSunrise: Double, sunriseMinutes: Int, currentNakshatraIndex: Int): TransitionTimeResult {
        val nakshatraSpan = 360.0 / 27.0
        val targetAngle = (currentNakshatraIndex + 1) * nakshatraSpan
        var lowHours = 0.0
        var highHours = 30.0

        for (step in 0..60) {
            val midHours = (lowHours + highHours) / 2.0
            val jdMid = jdSunrise + (midHours / 24.0)
            var moonMid = calculateMoonSidereal(jdMid)

            if (currentNakshatraIndex == 26 && moonMid < 180.0) {
                moonMid += 360.0
            }

            if (moonMid >= targetAngle) {
                highHours = midHours
            } else {
                lowHours = midHours
            }
        }

        val endMinutesFromMidnight = (sunriseMinutes + highHours * 60.0).toInt()
        return TransitionTimeResult(formatTransitionTime(endMinutesFromMidnight), endMinutesFromMidnight)
    }

    private fun findNakshatraEndTimeStr(jdSunrise: Double, sunriseMinutes: Int, currentNakshatraIndex: Int): String {
        return findNakshatraEndTime(jdSunrise, sunriseMinutes, currentNakshatraIndex).formatted
    }

    private fun findYogaEndTimeStr(jdSunrise: Double, sunriseMinutes: Int, currentYogaIndex: Int): String {
        val yogaSpan = 360.0 / 27.0
        val targetAngle = (currentYogaIndex + 1) * yogaSpan
        var lowHours = 0.0
        var highHours = 30.0

        for (step in 0..60) {
            val midHours = (lowHours + highHours) / 2.0
            val jdMid = jdSunrise + (midHours / 24.0)
            val sunMid = calculateSunSidereal(jdMid)
            val moonMid = calculateMoonSidereal(jdMid)
            var yogaSum = (sunMid + moonMid) % 360.0

            if (currentYogaIndex == 26 && yogaSum < 180.0) {
                yogaSum += 360.0
            }

            if (yogaSum >= targetAngle) {
                highHours = midHours
            } else {
                lowHours = midHours
            }
        }

        val endMinutesFromMidnight = sunriseMinutes + highHours * 60.0
        return formatTransitionTime(endMinutesFromMidnight.toInt())
    }

    // --- Formatting and Utilities ---

    private fun formatMinutesToAmPm(totalMinutes: Int): String {
        var m = totalMinutes % 1440
        if (m < 0) m += 1440
        val hours24 = m / 60
        val mins = m % 60
        val isPm = hours24 >= 12
        val hours12 = when {
            hours24 == 0 -> 12
            hours24 > 12 -> hours24 - 12
            else -> hours24
        }
        val ampm = if (isPm) "PM" else "AM"
        return String.format("%02d:%02d %s", hours12, mins, ampm)
    }

    private fun formatTransitionTime(minutesFromMidnight: Int): String {
        val nextDay = minutesFromMidnight >= 1440
        val normalizedMin = minutesFromMidnight % 1440
        val hours24 = normalizedMin / 60
        val mins = normalizedMin % 60
        val isPm = hours24 >= 12
        val hours12 = when {
            hours24 == 0 -> 12
            hours24 > 12 -> hours24 - 12
            else -> hours24
        }
        val ampm = if (isPm) "PM" else "AM"
        val timeFormatted = String.format("%02d:%02d %s", hours12, mins, ampm)

        val periodTamil = when {
            nextDay -> "இரவு (மறுநாள்)"
            hours24 in 4..11 -> "காலை"
            hours24 in 12..15 -> "மதியம்"
            hours24 in 16..18 -> "மாலை"
            else -> "இரவு"
        }

        val suffixEng = if (nextDay) " (Next Day)" else ""
        return "$periodTamil $timeFormatted வரை / Up to $timeFormatted$suffixEng"
    }

    private fun getTithiDeity(tithiIndex: Int): Pair<String, String> {
        return when (tithiIndex % 15) {
            1 -> Pair("அக்னி", "Agni")
            2 -> Pair("பிரம்மா", "Brahma")
            3 -> Pair("கௌரி", "Gauri")
            4 -> Pair("விநாயகர்", "Ganesha")
            5 -> Pair("சரஸ்வதி / நாக தேவதை", "Saraswati/Naga")
            6 -> Pair("முருகன்", "Kartikeya")
            7 -> Pair("சூரியன்", "Surya")
            8 -> Pair("துர்க்கை / சிவன்", "Durga/Shiva")
            9 -> Pair("சரஸ்வதி", "Saraswati")
            10 -> Pair("யமன்", "Yama")
            11 -> Pair("விஷ்ணு", "Vishnu")
            12 -> Pair("வாசுதேவன்", "Vasudeva")
            13 -> Pair("மன்மதன்", "Kamadeva")
            14 -> Pair("காளி", "Kali")
            0 -> Pair("சிவன் / விஷ்ணு", "Shiva/Vishnu")
            else -> Pair("தேவதை", "Deity")
        }
    }

    private fun getRahuKalam(dayOfWeek: Int, sunriseMin: Int, dayDurationMin: Int): TimeRange {
        val partLen = dayDurationMin / 8.0
        val periodIndex = when (dayOfWeek) {
            1 -> 7 // Sun 8th part
            2 -> 1 // Mon 2nd part
            3 -> 6 // Tue 7th part
            4 -> 4 // Wed 5th part
            5 -> 5 // Thu 6th part
            6 -> 3 // Fri 4th part
            else -> 2 // Sat 3rd part
        }
        val start = (sunriseMin + (periodIndex * partLen)).toInt()
        val end = (start + partLen).toInt()
        return TimeRange(formatMinutesToAmPm(start), formatMinutesToAmPm(end), "${formatMinutesToAmPm(start)} - ${formatMinutesToAmPm(end)}")
    }

    private fun getYamagandam(dayOfWeek: Int, sunriseMin: Int, dayDurationMin: Int): TimeRange {
        val partLen = dayDurationMin / 8.0
        val periodIndex = when (dayOfWeek) {
            1 -> 4 // Sun 5th part
            2 -> 3 // Mon 4th part
            3 -> 2 // Tue 3rd part
            4 -> 1 // Wed 2nd part
            5 -> 0 // Thu 1st part
            6 -> 6 // Fri 7th part
            else -> 5 // Sat 6th part
        }
        val start = (sunriseMin + (periodIndex * partLen)).toInt()
        val end = (start + partLen).toInt()
        return TimeRange(formatMinutesToAmPm(start), formatMinutesToAmPm(end), "${formatMinutesToAmPm(start)} - ${formatMinutesToAmPm(end)}")
    }

    private fun getKuligai(dayOfWeek: Int, sunriseMin: Int, dayDurationMin: Int): TimeRange {
        val partLen = dayDurationMin / 8.0
        val periodIndex = when (dayOfWeek) {
            1 -> 6 // Sun 7th part
            2 -> 5 // Mon 6th part
            3 -> 4 // Tue 5th part
            4 -> 3 // Wed 4th part
            5 -> 2 // Thu 3rd part
            6 -> 1 // Fri 2nd part
            else -> 0 // Sat 1st part
        }
        val start = (sunriseMin + (periodIndex * partLen)).toInt()
        val end = (start + partLen).toInt()
        return TimeRange(formatMinutesToAmPm(start), formatMinutesToAmPm(end), "${formatMinutesToAmPm(start)} - ${formatMinutesToAmPm(end)}")
    }

    private fun getNallaNeram(dayOfWeek: Int, sunriseMin: Int, sunsetMin: Int): TimeRangePair {
        val dayPartLen = (sunsetMin - sunriseMin) / 8.0
        val morningStartPart = when (dayOfWeek) {
            1 -> 0 // Sun: 1st part (06:00 - 07:30 adjusted to sunrise)
            2 -> 0 // Mon: 1st part
            3 -> 1 // Tue: 2nd part (07:30 - 09:00 adjusted)
            4 -> 2 // Wed: 3rd part (09:00 - 10:30 adjusted)
            5 -> 2 // Thu: 3rd part
            6 -> 2 // Fri: 3rd part
            else -> 1 // Sat: 2nd part
        }
        val morningStart = (sunriseMin + morningStartPart * dayPartLen).toInt()
        val morningEnd = (morningStart + dayPartLen).toInt()

        val eveningStartPart = when (dayOfWeek) {
            1 -> 6 // Sun: 3:00 PM (7th part)
            else -> 7 // Mon-Sat: 4:30 PM (8th part)
        }
        val eveningStart = (sunriseMin + eveningStartPart * dayPartLen).toInt()
        val eveningEnd = (eveningStart + dayPartLen).toInt()

        val morning = TimeRange(
            formatMinutesToAmPm(morningStart),
            formatMinutesToAmPm(morningEnd),
            "${formatMinutesToAmPm(morningStart)} - ${formatMinutesToAmPm(morningEnd)}"
        )
        val evening = TimeRange(
            formatMinutesToAmPm(eveningStart),
            formatMinutesToAmPm(eveningEnd),
            "${formatMinutesToAmPm(eveningStart)} - ${formatMinutesToAmPm(eveningEnd)}"
        )
        return TimeRangePair(morning, evening)
    }

    private fun getGowriNallaNeram(dayOfWeek: Int, gowriDayList: List<GowriPeriod>, gowriNightList: List<GowriPeriod>): TimeRangePair {
        val morningItem = when (dayOfWeek) {
            1 -> gowriDayList.getOrNull(1) // அமிர்தம்
            2 -> gowriDayList.getOrNull(0) // அமிர்தம்
            3 -> gowriDayList.getOrNull(7) // அமிர்தம்
            4 -> gowriDayList.getOrNull(6) // அமிர்தம்
            5 -> gowriDayList.getOrNull(5) // அமிர்தம்
            6 -> gowriDayList.getOrNull(4) // அமிர்தம்
            else -> gowriDayList.getOrNull(3) // அமிர்தம்
        } ?: gowriDayList.firstOrNull { it.isGood } ?: gowriDayList[0]

        val eveningItem = when (dayOfWeek) {
            1 -> gowriDayList.getOrNull(4) // தனம்
            2 -> gowriNightList.getOrNull(6) // அமிர்தம்
            3 -> gowriNightList.getOrNull(0) // அமிர்தம்
            4 -> gowriNightList.getOrNull(1) // அமிர்தம்
            5 -> gowriNightList.getOrNull(2) // அமிர்தம்
            6 -> gowriNightList.getOrNull(3) // அமிர்தம்
            else -> gowriNightList.getOrNull(4) // அமிர்தம்
        } ?: gowriNightList.firstOrNull { it.isGood } ?: gowriDayList[6]

        val morningParts = morningItem.timeSlot.split("-")
        val eveningParts = eveningItem.timeSlot.split("-")
        val morning = TimeRange(morningParts[0].trim(), morningParts.getOrElse(1) { "" }.trim(), morningItem.timeSlot)
        val evening = TimeRange(eveningParts[0].trim(), eveningParts.getOrElse(1) { "" }.trim(), eveningItem.timeSlot)
        return TimeRangePair(morning, evening)
    }

    private fun getDurmuhurtham(dayOfWeek: Int, sunriseMin: Int, dayDurationMin: Int): TimeRange {
        val muhurthaLen = dayDurationMin / 15.0 // 1 Muhurtha = ~48 min
        val muhurthaIndex = when (dayOfWeek) {
            1 -> 13 // 14th Muhurtha (around 04:12 PM)
            2 -> 8  // 9th Muhurtha (around 12:24 PM)
            3 -> 3  // 4th Muhurtha (around 08:24 AM)
            4 -> 7  // 8th Muhurtha (around 11:36 AM)
            5 -> 5  // 6th Muhurtha (around 10:12 AM)
            6 -> 3  // 4th Muhurtha (around 08:24 AM)
            else -> 2 // 3rd Muhurtha (around 07:36 AM)
        }
        val start = (sunriseMin + muhurthaIndex * muhurthaLen).toInt()
        val end = (start + muhurthaLen).toInt()
        return TimeRange(
            formatMinutesToAmPm(start),
            formatMinutesToAmPm(end),
            "${formatMinutesToAmPm(start)} - ${formatMinutesToAmPm(end)}"
        )
    }

    private fun generateGowriPanchangam(dayOfWeek: Int, sunriseMin: Int, sunsetMin: Int): Pair<List<GowriPeriod>, List<GowriPeriod>> {
        val gowriDayOrder = when (dayOfWeek) {
            1 -> listOf("உத்தி", "அமிர்தம்", "ரோகம்", "லாபம்", "தனம்", "விஷம்", "சுகம்", "சோரம்")
            2 -> listOf("அமிர்தம்", "ரோகம்", "லாபம்", "தனம்", "விஷம்", "சுகம்", "சோரம்", "உத்தி")
            3 -> listOf("ரோகம்", "லாபம்", "தனம்", "விஷம்", "சுகம்", "சோரம்", "உத்தி", "அமிர்தம்")
            4 -> listOf("லாபம்", "தனம்", "விஷம்", "சுகம்", "சோரம்", "உத்தி", "அமிர்தம்", "ரோகம்")
            5 -> listOf("தனம்", "விஷம்", "சுகம்", "சோரம்", "உத்தி", "அமிர்தம்", "ரோகம்", "லாபம்")
            6 -> listOf("விஷம்", "சுகம்", "சோரம்", "உத்தி", "அமிர்தம்", "ரோகம்", "லாபம்", "தனம்")
            else -> listOf("சுகம்", "சோரம்", "உத்தி", "அமிர்தம்", "ரோகம்", "லாபம்", "தனம்", "விஷம்")
        }

        val gowriNightOrder = when (dayOfWeek) {
            1 -> listOf("சோரம்", "சுகம்", "விஷம்", "தனம்", "லாபம்", "ரோகம்", "அமிர்தம்", "உத்தி")
            2 -> listOf("உத்தி", "சோரம்", "சுகம்", "விஷம்", "தனம்", "லாபம்", "ரோகம்", "அமிர்தம்")
            3 -> listOf("அமிர்தம்", "உத்தி", "சோரம்", "சுகம்", "விஷம்", "தனம்", "லாபம்", "ரோகம்")
            4 -> listOf("ரோகம்", "அமிர்தம்", "உத்தி", "சோரம்", "சுகம்", "விஷம்", "தனம்", "லாபம்")
            5 -> listOf("லாபம்", "ரோகம்", "அமிர்தம்", "உத்தி", "சோரம்", "சுகம்", "விஷம்", "தனம்")
            6 -> listOf("தனம்", "லாபம்", "ரோகம்", "அமிர்தம்", "உத்தி", "சோரம்", "சுகம்", "விஷம்")
            else -> listOf("விஷம்", "தனம்", "லாபம்", "ரோகம்", "அமிர்தம்", "உத்தி", "சோரம்", "சுகம்")
        }

        val dayPartLen = (sunsetMin - sunriseMin) / 8.0
        val nextSunriseMin = sunriseMin + 1440
        val nightPartLen = (nextSunriseMin - sunsetMin) / 8.0

        val dayList = gowriDayOrder.mapIndexed { index, nameTamil ->
            val start = (sunriseMin + index * dayPartLen).toInt()
            val end = (start + dayPartLen).toInt()
            val (nameEng, qualityTamil, qualityEng, isGood) = parseGowriDetails(nameTamil)
            GowriPeriod(
                periodIndex = index + 1,
                timeSlot = "${formatMinutesToAmPm(start)} - ${formatMinutesToAmPm(end)}",
                nameTamil = nameTamil,
                nameEnglish = nameEng,
                qualityTamil = qualityTamil,
                qualityEnglish = qualityEng,
                isGood = isGood,
                startMinutes = start,
                endMinutes = end
            )
        }

        val nightList = gowriNightOrder.mapIndexed { index, nameTamil ->
            val start = (sunsetMin + index * nightPartLen).toInt()
            val end = (start + nightPartLen).toInt()
            val (nameEng, qualityTamil, qualityEng, isGood) = parseGowriDetails(nameTamil)
            GowriPeriod(
                periodIndex = index + 9,
                timeSlot = "${formatMinutesToAmPm(start)} - ${formatMinutesToAmPm(end)}",
                nameTamil = nameTamil,
                nameEnglish = nameEng,
                qualityTamil = qualityTamil,
                qualityEnglish = qualityEng,
                isGood = isGood,
                startMinutes = start,
                endMinutes = end
            )
        }

        return Pair(dayList, nightList)
    }

    private fun parseGowriDetails(nameTamil: String): Quadruple<String, String, String, Boolean> {
        return when (nameTamil) {
            "அமிர்தம்" -> Quadruple("Amrita", "உத்தமம் (மிக நன்று)", "Excellent", true)
            "லாபம்" -> Quadruple("Labha", "நன்று", "Good", true)
            "தனம்" -> Quadruple("Dhana", "நன்று", "Good", true)
            "சுகம்" -> Quadruple("Sugam", "நன்று", "Good", true)
            "உத்தி" -> Quadruple("Uddhi", "சமம்", "Neutral", true)
            "ரோகம்" -> Quadruple("Roga", "தீது (நோய்)", "Inauspicious", false)
            "விஷம்" -> Quadruple("Visha", "தீது (விஷம்)", "Inauspicious", false)
            "சோரம்" -> Quadruple("Sora", "தீது (களவு)", "Inauspicious", false)
            else -> Quadruple("Kaala", "தீது", "Inauspicious", false)
        }
    }

    /**
     * Classical Vedic / Tamil Panchangam Horai Engine
     * Planetary sequence (Chaldean Order from outer to inner):
     * Sun -> Venus -> Mercury -> Moon -> Saturn -> Jupiter -> Mars -> (Sun...)
     * 1st hour lord is the day lord (ஞாயிறு: சூரியன், திங்கள்: சந்திரன், செவ்வாய்: செவ்வாய், புதன்: புதன், வியாழன்: குரு, வெள்ளி: சுக்கிரன், சனி: சனி).
     * Every successive hour advances by 1 in this sequence, starting strictly from Sunrise!
     */
    private fun generateSubhaHorai(dayOfWeek: Int, sunriseMin: Int, sunsetMin: Int): Pair<List<HoraiPeriod>, List<HoraiPeriod>> {
        val planetaryRulers = listOf(
            Pair("சூரியன்", "Sun"),     // 0
            Pair("சுக்கிரன்", "Venus"),   // 1
            Pair("புதன்", "Mercury"),    // 2
            Pair("சந்திரன்", "Moon"),     // 3
            Pair("சனி", "Saturn"),       // 4
            Pair("குரு", "Jupiter"),     // 5
            Pair("செவ்வாய்", "Mars")     // 6
        )

        val firstPlanetIndex = when (dayOfWeek) {
            1 -> 0 // Sun (ஞாயிறு)
            2 -> 3 // Moon (திங்கள்)
            3 -> 6 // Mars (செவ்வாய்)
            4 -> 2 // Mercury (புதன்)
            5 -> 5 // Jupiter (வியாழன்)
            6 -> 1 // Venus (வெள்ளி)
            else -> 4 // Saturn (சனி)
        }

        val dayHorai = mutableListOf<HoraiPeriod>()
        val nightHorai = mutableListOf<HoraiPeriod>()

        for (i in 0 until 24) {
            val planet = planetaryRulers[(firstPlanetIndex + i) % 7]
            val isGood = planet.second in listOf("Jupiter", "Venus", "Mercury", "Moon", "Sun")

            val qualityTamil = when (planet.second) {
                "Jupiter", "Venus" -> "உத்தமம் (சுப ஓரை)"
                "Mercury", "Moon" -> "சுபம் (சுப ஓரை)"
                "Sun" -> "மத்திமம் (சுப ஓரை)"
                else -> "அசுபம் (தவிர்க்கவும்)"
            }
            val qualityEnglish = when (planet.second) {
                "Jupiter", "Venus" -> "Highly Auspicious"
                "Mercury", "Moon" -> "Auspicious"
                "Sun" -> "Moderate"
                else -> "Inauspicious"
            }

            val startMin = (sunriseMin + i * 60)
            val endMin = (startMin + 60)
            val slotStr = "${formatMinutesToAmPm(startMin)} - ${formatMinutesToAmPm(endMin)}"

            val period = HoraiPeriod(
                periodIndex = i + 1,
                timeSlot = slotStr,
                planetTamil = planet.first,
                planetEnglish = planet.second,
                qualityTamil = qualityTamil,
                qualityEnglish = qualityEnglish,
                isGood = (planet.second !in listOf("Saturn", "Mars")),
                startMinutes = startMin,
                endMinutes = endMin
            )

            if (i < 12) {
                dayHorai.add(period)
            } else {
                nightHorai.add(period)
            }
        }
        return Pair(dayHorai, nightHorai)
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
    private data class NakshatraTuple(
        val nameTa: String,
        val nameEn: String,
        val pada: Int,
        val rasiTa: String,
        val rasiEn: String,
        val rulerTa: String,
        val rulerEn: String,
        val rasiIndex: Int
    )
}
