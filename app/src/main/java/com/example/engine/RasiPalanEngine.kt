package com.example.engine

import com.example.model.CityLocation
import com.example.model.PanchangResult
import com.example.model.RasiPalanItem
import java.time.LocalDate
import java.time.YearMonth

object RasiPalanEngine {

    private val RASI_BASE_DATA = listOf(
        Triple("மேஷம்", "Aries", "♈"),
        Triple("ரிஷபம்", "Taurus", "♉"),
        Triple("மிதுனம்", "Gemini", "♊"),
        Triple("கடகம்", "Cancer", "♋"),
        Triple("சிம்மம்", "Leo", "♌"),
        Triple("கன்னி", "Virgo", "♍"),
        Triple("துலாம்", "Libra", "♎"),
        Triple("விருச்சிகம்", "Scorpio", "♏"),
        Triple("தனுசு", "Sagittarius", "♐"),
        Triple("மகரம்", "Capricorn", "♑"),
        Triple("கும்பம்", "Aquarius", "♒"),
        Triple("மீனம்", "Pisces", "♓")
    )

    private val RASI_LORDS = listOf(
        Pair("செவ்வாய்", "Mars"),
        Pair("சுக்கிரன்", "Venus"),
        Pair("புதன்", "Mercury"),
        Pair("சந்திரன்", "Moon"),
        Pair("சூரியன்", "Sun"),
        Pair("புதன்", "Mercury"),
        Pair("சுக்கிரன்", "Venus"),
        Pair("செவ்வாய்", "Mars"),
        Pair("குரு", "Jupiter"),
        Pair("சனி", "Saturn"),
        Pair("சனி", "Saturn"),
        Pair("குரு", "Jupiter")
    )

    private val COLORS = listOf(
        Pair("சிவப்பு", "Red"),
        Pair("வெள்ளை", "White"),
        Pair("பச்சை", "Green"),
        Pair("வெள்ளி", "Silver"),
        Pair("மஞ்சள்", "Yellow"),
        Pair("பச்சை", "Green"),
        Pair("வெள்ளை / நீலம்", "White / Blue"),
        Pair("சிவப்பு / ஆரஞ்சு", "Red / Orange"),
        Pair("மஞ்சள்", "Yellow"),
        Pair("நீலம்", "Blue"),
        Pair("கருநீலம்", "Dark Blue"),
        Pair("மஞ்சள் / பொன் நிறம்", "Yellow / Gold")
    )

    private val REMEDIES = listOf(
        Pair("முருகப்பெருமானை வழிபாடு செய்யவும்.", "Worship Lord Murugan."),
        Pair("ஸ்ரீ மகாலட்சுமியைத் துதித்து வழிபடவும்.", "Pray to Goddess Mahalakshmi."),
        Pair("ஸ்ரீ விஷ்ணு சகஸ்ரநாமம் பாராயணம் செய்யவும்.", "Recite Sri Vishnu Sahasranamam."),
        Pair("ஸ்ரீ துர்க்கை அம்மனை வழிபட நன்மை உண்டாகும்.", "Worship Goddess Durga for auspiciousness."),
        Pair("சூரிய பகவானுக்கு ஆதித்ய ஹிருதயம் பாராயணம் செய்யவும்.", "Chant Aditya Hrudayam for Sun God."),
        Pair("ஸ்ரீ விநாயகப் பெருமானுக்கு அருகம்புல் சாற்றி வழிபடவும்.", "Offer Garika grass to Lord Ganesha."),
        Pair("ஸ்ரீ துர்க்கை அம்மனுக்கு நெய் தீபம் ஏற்றவும்.", "Light ghee lamp for Goddess Durga."),
        Pair("ஸ்ரீ அனுமான் சாலீசா பாராயணம் செய்யவும்.", "Recite Hanuman Chalisa."),
        Pair("ஸ்ரீ தட்சிணாமூர்த்திக்கு மஞ்சள் மலர் சாற்றி வழிபடவும்.", "Worship Lord Dakshinamurthy with yellow flowers."),
        Pair("ஸ்ரீ ஆஞ்சநேயருக்கு வெண்ணெய் சாற்றி வழிபடவும்.", "Offer butter to Lord Anjaneya."),
        Pair("ஸ்ரீ சனீஸ்வர பகவானுக்கு எள் தீபம் ஏற்றவும்.", "Light sesame oil lamp for Lord Shani."),
        Pair("ஸ்ரீ ராகவேந்திரர் / சீரடி பாபாவை தரிசிக்க நலம் பெருகும்.", "Seek blessings of Sri Raghavendra / Shirdi Baba.")
    )

    /**
     * Calculates Daily Rasi Palan for all 12 Rasis on a given date.
     */
    fun calculateDailyRasiPalan(date: LocalDate, city: CityLocation = CityLocation.DEFAULT_CITIES[2]): List<RasiPalanItem> {
        val panchang = ThiruGanithaEngine.calculatePanchang(date, city)
        val daySeed = date.toEpochDay().toInt()
        val moonRasiName = panchang.moonSignTamil

        return RASI_BASE_DATA.mapIndexed { index, (nameTa, nameEn, symbol) ->
            val lord = RASI_LORDS[index]
            val color = COLORS[index]
            val remedy = REMEDIES[index]

            // Seed deterministic randomness based on Date + Rasi Index
            val hash = (daySeed * 31 + index * 17 + panchang.tithi.index * 7 + panchang.nakshatra.index) % 100
            val absHash = Math.abs(hash)

            // Chandrashtama detection for this rasi
            val isChandrashtama = panchang.chandrashtamaRasiTamil.contains(nameTa)

            val rating = when {
                isChandrashtama -> 2
                absHash % 5 == 0 -> 5
                absHash % 3 == 0 -> 4
                else -> 3
            }

            val luckyNum = ((absHash + index) % 9 + 1).toString()

            val generalTa: String
            val generalEn: String
            val financeTa: String
            val financeEn: String
            val familyHealthTa: String
            val familyHealthEn: String

            if (isChandrashtama) {
                generalTa = "இன்று சந்திராஷ்டமம் இருப்பதால் புதிய முயற்சிகளையும் முக்கிய முடிவுகளையும் தள்ளி வைப்பது நல்லது. நிதானத்துடன் செயல்படவும்."
                generalEn = "Chandrashtama today. Postpone major decisions and new ventures. Act with patience and caution."
                financeTa = "பணப் பரிவர்த்தனைகளில் எச்சரிக்கை தேவை. வீண் விரயங்கள் நேரலாம்."
                financeEn = "Exercise caution in financial transactions. Avoid unnecessary expenses."
                familyHealthTa = "வாகனப் பயணங்களில் கவனம் தேவை. உணவுப் பழக்கத்தில் நிதானம் காக்கவும்."
                familyHealthEn = "Be careful while driving. Maintain healthy dietary habits."
            } else {
                when (absHash % 4) {
                    0 -> {
                        generalTa = "இன்று உங்களின் நீண்ட நாள் விருப்பங்கள் நிறைவேறும் இனிய நாள். எடுத்த காரியங்களில் வெற்றி நிச்சயம்."
                        generalEn = "A favorable day where long-pending wishes come to fruition. Success assured in endeavors."
                        financeTa = "தொழில் மற்றும் வியாபாரத்தில் எதிர்பார்த்த லாபம் கிடைக்கும். நிதி நிலை உயரும்."
                        financeEn = "Expected profits in trade and business. Financial growth indicated."
                        familyHealthTa = "குடும்பத்தில் சுப நிகழ்ச்சிகள் பற்றிய பேச்சுகள் நல்லபடியாக முடியும். ஆரோக்கியம் சீராக இருக்கும்."
                        familyHealthEn = "Harmonious family environment. Health remains stable and energetic."
                    }
                    1 -> {
                        generalTa = "உத்தியோகத்தில் மேலதிகாரிகளின் பாராட்டைப் பெறுவீர்கள். புதிய வாய்ப்புகள் கதவைத் தட்டும்."
                        generalEn = "Recognition from superiors at workplace. New opportunities will knock your door."
                        financeTa = "பணவரவு திருப்திகரமாக இருக்கும். கடன் சுமைகள் குறைய வாய்ப்புள்ளது."
                        financeEn = "Satisfactory cash inflow. Good chances to reduce financial obligations."
                        familyHealthTa = "உறவினர்களின் ஆதரவு அதிகரிக்கும். மனமகிழ்ச்சியும் சுபிட்சமும் நிறைந்து காணப்படும்."
                        familyHealthEn = "Supportive relatives and friends. Joy and tranquility prevail."
                    }
                    2 -> {
                        generalTa = "சுறுசுறுப்புடன் செயல்பட்டு நிலுவையில் உள்ள வேலைகளை விரைந்து முடிப்பீர்கள்."
                        generalEn = "Energetic day to swiftly complete all pending tasks efficiently."
                        financeTa = "புதிய முதலீடுகள் செய்வதற்கு சாதகமான சூழல் நிலவுகிறது."
                        financeEn = "Favorable environment for making prospective investments."
                        familyHealthTa = "சகோதர வழியில் சுபச் செய்திகள் வந்து சேரும். உடல் நலம் சிறப்பாக இருக்கும்."
                        familyHealthEn = "Good news from siblings or close kin. Excellent physical wellbeing."
                    }
                    else -> {
                        generalTa = "நண்பர்கள் மற்றும் சுற்றத்தாரின் உதவி கிடைத்து மனநிம்மதி அடைவீர்கள்."
                        generalEn = "Mental peace and support from friends and relatives today."
                        financeTa = "வரவுக்கு ஏற்ற செலவுகள் ஏற்படும். ஆடம்பரச் செலவுகளைத் தவிர்ப்பது நல்லது."
                        financeEn = "Balanced income and expense flow. Avoid luxury spending."
                        familyHealthTa = "வாழ்க்கைத்துணையுடன் அன்பு அதிகரிக்கும். சுபச் செய்திகள் மகிழ்ச்சி தரும்."
                        familyHealthEn = "Warmth in matrimonial relation. Pleasant news will bring happiness."
                    }
                }
            }

            RasiPalanItem(
                rasiIndex = index,
                nameTamil = nameTa,
                nameEnglish = nameEn,
                symbol = symbol,
                lordTamil = lord.first,
                lordEnglish = lord.second,
                generalPredictionTa = generalTa,
                generalPredictionEn = generalEn,
                financeCareerTa = financeTa,
                financeCareerEn = financeEn,
                familyHealthTa = familyHealthTa,
                familyHealthEn = familyHealthEn,
                luckyNumber = luckyNum,
                luckyColorTa = color.first,
                luckyColorEn = color.second,
                remedyTa = remedy.first,
                remedyEn = remedy.second,
                isChandrashtama = isChandrashtama,
                ratingStars = rating
            )
        }
    }

    /**
     * Calculates Monthly Rasi Palan for all 12 Rasis for a given YearMonth.
     */
    fun calculateMonthlyRasiPalan(yearMonth: YearMonth, city: CityLocation = CityLocation.DEFAULT_CITIES[2]): List<RasiPalanItem> {
        val monthSeed = yearMonth.year * 12 + yearMonth.monthValue

        return RASI_BASE_DATA.mapIndexed { index, (nameTa, nameEn, symbol) ->
            val lord = RASI_LORDS[index]
            val color = COLORS[index]
            val remedy = REMEDIES[index]

            val hash = (monthSeed * 29 + index * 13) % 100
            val absHash = Math.abs(hash)

            val luckyNum = ((absHash + index) % 9 + 1).toString()
            val rating = 3 + (absHash % 3)

            val generalTa: String
            val generalEn: String
            val financeTa: String
            val financeEn: String
            val familyHealthTa: String
            val familyHealthEn: String

            when (absHash % 3) {
                0 -> {
                    generalTa = "இந்த மாதம் உங்களின் ராசிக்கு கிரகங்களின் நற்பலன்களால் தொட்டதெல்லாம் துலங்கும் யோகமான மாதமாக அமையும்."
                    generalEn = "This month brings highly favorable planetary alignments leading to prosperity and success in all tasks."
                    financeTa = "தொழிலில் விரிவாக்கம் நிகழும். புதிய வாடிக்கையாளர்கள் கிடைப்பார்கள். எதிர்பாராத தனலாபம் உண்டு."
                    financeEn = "Business expansion and new client prospects. Unexpected monetary gains expected."
                    familyHealthTa = "குடும்பத்தில் சுபகாரியங்கள் தடையின்றி நடைபெறும். குடும்பத்தாரின் ஆரோக்கியம் மேம்படும்."
                    familyHealthEn = "Auspicious ceremonies in family will progress smoothly. Good overall family health."
                }
                1 -> {
                    generalTa = "இந்த மாதம் கடின உழைப்பிற்கு ஏற்ற தகுந்த பலன்கள் கிடைக்கும். தடைகள் அகன்று காரிய வெற்றி உண்டாகும்."
                    generalEn = "Hard work yields deserved rewards this month. Obstacles vanish paving way for accomplishments."
                    financeTa = "உத்தியோகத்தில் ஊதிய உயர்வு மற்றும் பதவி உயர்வு கிடைக்க வாய்ப்புள்ளது. பொருளாதாரத்தில் முன்னேற்றம் ஏற்படும்."
                    financeEn = "Chances of salary hike and promotion at work. Steadfast financial improvement."
                    familyHealthTa = "பிள்ளைகளின் வழியில் பெருமை சேர்க்கும் செய்திகள் கிடைக்கும். ஆன்மீகப் பயணங்கள் மேற்கொள்வீர்கள்."
                    familyHealthEn = "Proud accomplishments through children. Spiritual travels and pilgrimage indicated."
                }
                else -> {
                    generalTa = "இந்த மாதம் திட்டமிட்டு செயல்படுவதன் மூலம் அனைத்து சவால்களையும் எளிதில் வெல்வீர்கள்."
                    generalEn = "Strategic planning and patience will help overcome all challenges effortlessly this month."
                    financeTa = "நிதி நிலைமை திருப்திகரமாக இருக்கும். புதிய சொத்து அல்லது வாகனம் வாங்கும் யோகம் உண்டு."
                    financeEn = "Satisfactory finances. Possibility of purchasing new property or vehicle."
                    familyHealthTa = "உறவினர்களிடையே நிலவிய கருத்து வேறுபாடுகள் நீங்கி ஒற்றுமை பலப்படும்."
                    familyHealthEn = "Resolution of misunderstandings among relatives, fostering family unity."
                }
            }

            RasiPalanItem(
                rasiIndex = index,
                nameTamil = nameTa,
                nameEnglish = nameEn,
                symbol = symbol,
                lordTamil = lord.first,
                lordEnglish = lord.second,
                generalPredictionTa = generalTa,
                generalPredictionEn = generalEn,
                financeCareerTa = financeTa,
                financeCareerEn = financeEn,
                familyHealthTa = familyHealthTa,
                familyHealthEn = familyHealthEn,
                luckyNumber = luckyNum,
                luckyColorTa = color.first,
                luckyColorEn = color.second,
                remedyTa = remedy.first,
                remedyEn = remedy.second,
                isChandrashtama = false,
                ratingStars = rating
            )
        }
    }
}
