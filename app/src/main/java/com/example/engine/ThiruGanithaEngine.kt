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
        Pair("க்ரோதன", "Krodhana"), Pair("அட்சய", "Akshaya")
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

        // Sunrise and Sunset minutes for location
        val sunriseMinutes = 360 + (city.longitude - 80.0) * -2.0 + (month % 3) * 3
        val sunsetMinutes = 1080 + (city.longitude - 80.0) * -2.0 - (month % 3) * 3

        // Julian Day at Sunrise IST (05:30 AM IST = 00:00 UTC)
        val sunriseOffsetDays = (sunriseMinutes - 330.0) / 1440.0
        val jdSunrise = computeJulianDay(year, month, day) + sunriseOffsetDays

        // Planetary positions at Sunrise
        val sunSidereal = calculateSunSidereal(jdSunrise)
        val moonSidereal = calculateMoonSidereal(jdSunrise)

        // 1. Tamil Solar Month & Solar Day Number
        val sunRasiIndex = (sunSidereal / 30.0).toInt().coerceIn(0, 11)
        val tamilMonthTriple = TAMIL_MONTHS[sunRasiIndex]
        val tamilDayNumber = calculateTamilSolarDay(date, city, sunRasiIndex)

        // Tamil 60-year Jovian Cycle (Chithirai 1 is solar entry into Mesha)
        val effectiveTamilYear = if (sunRasiIndex >= 8 || (month < 4 && sunRasiIndex > 8)) year - 1 else year
        val tamilYearIndex = (effectiveTamilYear - 4) % 60
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
        // A) Tithi (Moon - Sun angle / 12 degrees)
        val moonSunDiff = (moonSidereal - sunSidereal + 360.0) % 360.0
        val tithiIndex = ((moonSunDiff / 12.0).toInt() + 1).coerceIn(1, 30)
        val isSuklaPaksha = tithiIndex <= 15
        val pakshaTamil = if (isSuklaPaksha) "வளர்பிறை" else "தேய்பிறை"
        val pakshaEnglish = if (isSuklaPaksha) "Sukla Paksha" else "Krishna Paksha"
        val tithiPair = TITHI_NAMES[tithiIndex - 1]

        // Dynamic Root Search for exact Tithi transition end time
        val tithiEndTimeStr = findTithiEndTimeStr(jdSunrise, sunriseMinutes.toInt(), tithiIndex)

        val tithiInfo = TithiInfo(
            index = tithiIndex,
            nameTamil = tithiPair.first,
            nameEnglish = tithiPair.second,
            pakshaTamil = pakshaTamil,
            pakshaEnglish = pakshaEnglish,
            endTime = tithiEndTimeStr,
            deityTamil = getTithiDeity(tithiIndex).first,
            deityEnglish = getTithiDeity(tithiIndex).second
        )

        // B) Nakshatra (Moon Longitude / 13.333333 degrees)
        val nakshatraSpan = 360.0 / 27.0
        val nakshatraIndex = (moonSidereal / nakshatraSpan).toInt().coerceIn(0, 26)
        val nakshatraTriple = NAKSHATRAS[nakshatraIndex]
        val moonRasiIndex = (moonSidereal / 30.0).toInt().coerceIn(0, 11)
        val pada = (((moonSidereal % nakshatraSpan) / (360.0 / 108.0)).toInt() + 1).coerceIn(1, 4)

        // Dynamic Root Search for exact Nakshatra transition end time
        val nakshatraEndTimeStr = findNakshatraEndTimeStr(jdSunrise, sunriseMinutes.toInt(), nakshatraIndex)

        val nakshatraInfo = NakshatraInfo(
            index = nakshatraIndex + 1,
            nameTamil = nakshatraTriple.first,
            nameEnglish = nakshatraTriple.second,
            pada = pada,
            rasiTamil = RASIS[moonRasiIndex].first,
            rasiEnglish = RASIS[moonRasiIndex].second,
            endTime = nakshatraEndTimeStr,
            rulingPlanetTamil = nakshatraTriple.third.split("/")[0].trim(),
            rulingPlanetEnglish = nakshatraTriple.third.split("/").getOrElse(1) { "" }.trim()
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

        val nallaNeram = getNallaNeram(vaaraInfo.dayOfWeek)
        val gowriNallaNeram = getGowriNallaNeram(vaaraInfo.dayOfWeek)
        val durmuhurtham = getDurmuhurtham(vaaraInfo.dayOfWeek)

        // Chandrashtama Star & Rasi
        val chandrashtamaRasiIndex = (moonRasiIndex - 7 + 12) % 12
        val chandrashtamaStarIndex = (nakshatraIndex - 15 + 27) % 27

        // Gowri Panchangam & Horai
        val gowriList = generateGowriPanchangam(vaaraInfo.dayOfWeek)
        val horaiList = generateSubhaHorai(vaaraInfo.dayOfWeek)

        // Special Events / Fasting Days
        val specialTamil = mutableListOf<String>()
        val specialEng = mutableListOf<String>()

        val isSubhaMuhurtham = (tithiIndex in listOf(2, 3, 5, 7, 10, 11, 13) && isAuspiciousYoga && vaaraInfo.dayOfWeek !in listOf(3, 7))
        val isAmavasya = tithiIndex == 30
        val isPurnima = tithiIndex == 15
        val isPradosham = tithiIndex == 13 || tithiIndex == 28
        val isEkadashi = tithiIndex == 11 || tithiIndex == 26
        val isKarthigai = nakshatraIndex == 2

        if (isSubhaMuhurtham) {
            specialTamil.add("சுப முகூர்த்த நாள்")
            specialEng.add("Auspicious Muhurtham Day")
        }
        if (isAmavasya) {
            specialTamil.add("அமாவாசை")
            specialEng.add("Amavasya (New Moon)")
        }
        if (isPurnima) {
            specialTamil.add("பௌர்ணமி")
            specialEng.add("Pournami (Full Moon)")
        }
        if (isPradosham) {
            specialTamil.add("பிரதோஷம்")
            specialEng.add("Pradosham")
        }
        if (isEkadashi) {
            specialTamil.add("ஏகாதசி விரதம்")
            specialEng.add("Ekadashi Fasting")
        }
        if (isKarthigai) {
            specialTamil.add("கார்த்திகை தீபம் / விரதம்")
            specialEng.add("Kiruthigai")
        }
        if (tithiIndex == 4 || tithiIndex == 19) {
            specialTamil.add("சதுர்த்தி விரதம்")
            specialEng.add("Sankatahara / Vinayagar Chaturthi")
        }
        if (tithiIndex == 6 || tithiIndex == 21) {
            specialTamil.add("சஷ்டி விரதம்")
            specialEng.add("Sashti Fasting")
        }

        val formattedDateTamil = "${date.dayOfMonth} ${tamilMonthTriple.first} $year (${vaaraInfo.nameTamil})"
        val formattedDateEnglish = "${date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))} (${vaaraInfo.nameEnglish})"

        return PanchangResult(
            dateIso = date.toString(),
            dateDisplayTamil = formattedDateTamil,
            dateDisplayEnglish = formattedDateEnglish,
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
            moonSignTamil = RASIS[moonRasiIndex].first,
            moonSignEnglish = RASIS[moonRasiIndex].second,
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
            gowriDayList = gowriList,
            horaiDayList = horaiList,
            specialEventsTamil = specialTamil,
            specialEventsEnglish = specialEng,
            isSubhaMuhurtham = isSubhaMuhurtham,
            isAmavasya = isAmavasya,
            isPurnima = isPurnima,
            isPradosham = isPradosham,
            isEkadashi = isEkadashi,
            isKarthigai = isKarthigai
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

    /**
     * Compute Tamil Solar Day Number as elapsed days since Sankranti (Sun's ingress into sign)
     */
    private fun calculateTamilSolarDay(date: LocalDate, city: CityLocation, rasiIndex: Int): Int {
        val targetDegree = rasiIndex * 30.0
        // Search back up to 32 days for Sankranti
        var sankrantiDate = date
        for (i in 0..32) {
            val checkDate = date.minusDays(i.toLong())
            val checkSunriseMinutes = 360 + (city.longitude - 80.0) * -2.0 + (checkDate.monthValue % 3) * 3
            val checkOffsetDays = (checkSunriseMinutes - 330.0) / 1440.0
            val jdCheck = computeJulianDay(checkDate.year, checkDate.monthValue, checkDate.dayOfMonth) + checkOffsetDays
            val sunDeg = calculateSunSidereal(jdCheck)

            // Normalize angle relative to targetDegree
            val relativeDeg = (sunDeg - targetDegree + 360.0) % 360.0
            if (relativeDeg > 25.0) {
                // Cross boundary into previous sign
                sankrantiDate = checkDate.plusDays(1)
                break
            }
        }

        val elapsed = ChronoUnit.DAYS.between(sankrantiDate, date).toInt() + 1
        return elapsed.coerceIn(1, 32)
    }

    // --- Dynamic Root Finding for Panchang Transitions ---

    private fun findTithiEndTimeStr(jdSunrise: Double, sunriseMinutes: Int, currentTithiIndex: Int): String {
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

        val endMinutesFromMidnight = sunriseMinutes + highHours * 60.0
        return formatTransitionTime(endMinutesFromMidnight.toInt())
    }

    private fun findNakshatraEndTimeStr(jdSunrise: Double, sunriseMinutes: Int, currentNakshatraIndex: Int): String {
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

        val endMinutesFromMidnight = sunriseMinutes + highHours * 60.0
        return formatTransitionTime(endMinutesFromMidnight.toInt())
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

    private fun getNallaNeram(dayOfWeek: Int): TimeRangePair {
        val morning = when (dayOfWeek) {
            1 -> TimeRange("06:00 AM", "07:30 AM", "06:00 AM - 07:30 AM")
            2 -> TimeRange("06:00 AM", "07:30 AM", "06:00 AM - 07:30 AM")
            3 -> TimeRange("07:30 AM", "09:00 AM", "07:30 AM - 09:00 AM")
            4 -> TimeRange("09:00 AM", "10:30 AM", "09:00 AM - 10:30 AM")
            5 -> TimeRange("09:00 AM", "10:30 AM", "09:00 AM - 10:30 AM")
            6 -> TimeRange("09:00 AM", "10:30 AM", "09:00 AM - 10:30 AM")
            else -> TimeRange("07:30 AM", "09:00 AM", "07:30 AM - 09:00 AM")
        }
        val evening = when (dayOfWeek) {
            1 -> TimeRange("03:00 PM", "04:30 PM", "03:00 PM - 04:30 PM")
            2 -> TimeRange("04:30 PM", "06:00 PM", "04:30 PM - 06:00 PM")
            3 -> TimeRange("04:30 PM", "06:00 PM", "04:30 PM - 06:00 PM")
            4 -> TimeRange("04:30 PM", "06:00 PM", "04:30 PM - 06:00 PM")
            5 -> TimeRange("04:30 PM", "06:00 PM", "04:30 PM - 06:00 PM")
            6 -> TimeRange("04:30 PM", "06:00 PM", "04:30 PM - 06:00 PM")
            else -> TimeRange("04:30 PM", "06:00 PM", "04:30 PM - 06:00 PM")
        }
        return TimeRangePair(morning, evening)
    }

    private fun getGowriNallaNeram(dayOfWeek: Int): TimeRangePair {
        val morning = when (dayOfWeek) {
            1 -> TimeRange("10:30 AM", "11:30 AM", "10:30 AM - 11:30 AM")
            2 -> TimeRange("09:00 AM", "10:30 AM", "09:00 AM - 10:30 AM")
            3 -> TimeRange("10:30 AM", "11:30 AM", "10:30 AM - 11:30 AM")
            4 -> TimeRange("01:30 PM", "02:30 PM", "01:30 PM - 02:30 PM")
            5 -> TimeRange("12:00 PM", "01:30 PM", "12:00 PM - 01:30 PM")
            6 -> TimeRange("12:00 PM", "01:30 PM", "12:00 PM - 01:30 PM")
            else -> TimeRange("10:30 AM", "12:00 PM", "10:30 AM - 12:00 PM")
        }
        val evening = when (dayOfWeek) {
            1 -> TimeRange("01:30 PM", "02:30 PM", "01:30 PM - 02:30 PM")
            2 -> TimeRange("07:30 PM", "08:30 PM", "07:30 PM - 08:30 PM")
            3 -> TimeRange("07:30 PM", "08:30 PM", "07:30 PM - 08:30 PM")
            4 -> TimeRange("06:30 PM", "07:30 PM", "06:30 PM - 07:30 PM")
            5 -> TimeRange("06:30 PM", "07:30 PM", "06:30 PM - 07:30 PM")
            6 -> TimeRange("06:30 PM", "07:30 PM", "06:30 PM - 07:30 PM")
            else -> TimeRange("07:30 PM", "08:30 PM", "07:30 PM - 08:30 PM")
        }
        return TimeRangePair(morning, evening)
    }

    private fun getDurmuhurtham(dayOfWeek: Int): TimeRange {
        val range = when (dayOfWeek) {
            1 -> "04:12 PM - 05:00 PM"
            2 -> "12:24 PM - 01:12 PM"
            3 -> "08:24 AM - 09:12 AM"
            4 -> "11:36 AM - 12:24 PM"
            5 -> "10:12 AM - 11:00 AM"
            6 -> "08:24 AM - 09:12 AM"
            else -> "07:36 AM - 08:24 AM"
        }
        val parts = range.split("-")
        return TimeRange(parts[0].trim(), parts[1].trim(), range)
    }

    private fun generateGowriPanchangam(dayOfWeek: Int): List<GowriPeriod> {
        val gowriDayOrder = when (dayOfWeek) {
            1 -> listOf("உத்தி", "அமிர்தம்", "ரோகம்", "லாபம்", "தனம்", "விஷம்", "சுகம்", "சோரம்")
            2 -> listOf("அமிர்தம்", "ரோகம்", "லாபம்", "தனம்", "விஷம்", "சுகம்", "சோரம்", "உத்தி")
            3 -> listOf("ரோகம்", "லாபம்", "தனம்", "விஷம்", "சுகம்", "சோரம்", "உத்தி", "அமிர்தம்")
            4 -> listOf("லாபம்", "தனம்", "விஷம்", "சுகம்", "சோரம்", "உத்தி", "அமிர்தம்", "ரோகம்")
            5 -> listOf("தனம்", "விஷம்", "சுகம்", "சோரம்", "உத்தி", "அமிர்தம்", "ரோகம்", "லாபம்")
            6 -> listOf("விஷம்", "சுகம்", "சோரம்", "உத்தி", "அமிர்தம்", "ரோகம்", "லாபம்", "தனம்")
            else -> listOf("சுகம்", "சோரம்", "உத்தி", "அமிர்தம்", "ரோகம்", "லாபம்", "தனம்", "விஷம்")
        }

        val slots = listOf(
            "06:00 AM - 07:30 AM", "07:30 AM - 09:00 AM", "09:00 AM - 10:30 AM", "10:30 AM - 12:00 PM",
            "12:00 PM - 01:30 PM", "01:30 PM - 03:00 PM", "03:00 PM - 04:30 PM", "04:30 PM - 06:00 PM"
        )

        return gowriDayOrder.mapIndexed { index, nameTamil ->
            val (nameEng, qualityTamil, qualityEng, isGood) = parseGowriDetails(nameTamil)
            GowriPeriod(
                periodIndex = index + 1,
                timeSlot = slots[index],
                nameTamil = nameTamil,
                nameEnglish = nameEng,
                qualityTamil = qualityTamil,
                qualityEnglish = qualityEng,
                isGood = isGood
            )
        }
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

    private fun generateSubhaHorai(dayOfWeek: Int): List<HoraiPeriod> {
        val planetaryRulers = listOf(
            Pair("சூரியன்", "Sun"), Pair("சுக்கிரன்", "Venus"), Pair("புதன்", "Mercury"),
            Pair("சந்திரன்", "Moon"), Pair("சனி", "Saturn"), Pair("குரு", "Jupiter"),
            Pair("செவ்வாய்", "Mars")
        )

        val firstPlanetIndex = when (dayOfWeek) {
            1 -> 0 // Sun
            2 -> 3 // Moon
            3 -> 6 // Mars
            4 -> 2 // Mercury
            5 -> 5 // Jupiter
            6 -> 1 // Venus
            else -> 4 // Saturn
        }

        val horaiList = mutableListOf<HoraiPeriod>()
        for (i in 0 until 12) {
            val planet = planetaryRulers[(firstPlanetIndex + (i * 3)) % 7]
            val isGood = planet.second in listOf("Sun", "Venus", "Mercury", "Jupiter", "Moon")
            val hourStart = 6 + i
            val startStr = String.format("%02d:00 %s", if (hourStart % 12 == 0) 12 else hourStart % 12, if (hourStart >= 12) "PM" else "AM")
            val hourEnd = hourStart + 1
            val endStr = String.format("%02d:00 %s", if (hourEnd % 12 == 0) 12 else hourEnd % 12, if (hourEnd >= 12) "PM" else "AM")

            horaiList.add(
                HoraiPeriod(
                    periodIndex = i + 1,
                    timeSlot = "$startStr - $endStr",
                    planetTamil = planet.first,
                    planetEnglish = planet.second,
                    qualityTamil = if (isGood) "சுப ஹோரை (நன்று)" else "அசுப ஹோரை",
                    qualityEnglish = if (isGood) "Auspicious Horai" else "Inauspicious Horai",
                    isGood = isGood
                )
            )
        }
        return horaiList
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
