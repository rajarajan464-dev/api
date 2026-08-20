package com.example.engine

import com.example.model.CityLocation
import com.example.model.TithiDetail
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.sin

/**
 * திருக்கணித பஞ்சாங்க முறைப்படி ஒரு நாளின் திதி ஆரம்பம், முடிவு,
 * அதிதேவதை, தத்துவம், சுபகாரியங்கள், மற்றும் விரத விபரங்களைக் கணிக்கும் எஞ்சின்.
 */
object TithiEngine {

    data class StaticTithiData(
        val nameTa: String,
        val nameEn: String,
        val deityTa: String,
        val deityEn: String,
        val categoryTa: String,
        val categoryEn: String,
        val elementTa: String,
        val elementEn: String,
        val auspiciousTa: String,
        val auspiciousEn: String,
        val vrathamTa: String,
        val vrathamEn: String
    )

    private val TITHI_METADATA = listOf(
        // 1. பிரதமை (Prathama) - நந்தா
        StaticTithiData(
            nameTa = "பிரதமை", nameEn = "Prathama",
            deityTa = "அக்னி பகவான்", deityEn = "Lord Agni",
            categoryTa = "நந்தா (ஆனந்தம்)", categoryEn = "Nanda (Joyous)",
            elementTa = "பிருத்வி (பூமி)", elementEn = "Prithvi (Earth)",
            auspiciousTa = "மத வழிபாடுகள், வாஸ்து காரியங்கள், அக்னி ஹோமம் மற்றும் ஆன்மீக சாதனைகளுக்கு உகந்தது.",
            auspiciousEn = "Ideal for religious ceremonies, Vedic rituals, fire offerings, and spiritual beginnings.",
            vrathamTa = "அக்னி ஆராதனை, காரிய சித்தி தரும் வழிபாடு.",
            vrathamEn = "Fire worship and prayer for overcoming obstacles."
        ),
        // 2. துவிதியை (Dvitiya) - பத்ரா
        StaticTithiData(
            nameTa = "துவிதியை", nameEn = "Dvitiya",
            deityTa = "பிரம்மா / அஸ்வினி குமாரர்கள்", deityEn = "Lord Brahma / Ashvins",
            categoryTa = "பத்ரா (சுபம்)", categoryEn = "Bhadra (Auspicious)",
            elementTa = "அப்பு (நீர்)", elementEn = "Appu (Water)",
            auspiciousTa = "திருமணம், சீமந்தம், புதிய ஒப்பந்தங்கள், கல்வி தொடக்கம், மற்றும் அரசு சார்ந்த பணிகள் செய்ய மிகவும் உத்தமம்.",
            auspiciousEn = "Highly auspicious for weddings, contracts, foundation laying, education, and government tasks.",
            vrathamTa = "சந்திர தரிசனம், பிரம்ம ஞான வழிபாடு.",
            vrathamEn = "Chandra Darshan and prayers for wisdom."
        ),
        // 3. திருதியை (Tritiya) - ஜயா
        StaticTithiData(
            nameTa = "திருதியை", nameEn = "Tritiya",
            deityTa = "கௌரி தேவி", deityEn = "Goddess Gauri",
            categoryTa = "ஜயா (வெற்றி)", categoryEn = "Jaya (Victory)",
            elementTa = "தேயு (நெருப்பு)", elementEn = "Theyu (Fire)",
            auspiciousTa = "அட்சய திருதியை போன்ற மங்கள காரியங்கள், தங்கம் ஆபரணங்கள் வாங்குதல், புதிய தொழில் தொடங்குதல், இசை மற்றும் கலை பயில சிறந்தது.",
            auspiciousEn = "Excellent for auspicious beginnings, buying gold, new business ventures, and arts.",
            vrathamTa = "அட்சய திருதியை மற்றும் கௌரி விரதம். சகல ஐஸ்வர்யங்களும் பெருகும்.",
            vrathamEn = "Akshaya Tritiya and Gauri Vratham for prosperity and harmony."
        ),
        // 4. சதுர்த்தி (Chaturthi) - ரிக்தா
        StaticTithiData(
            nameTa = "சதுர்த்தி", nameEn = "Chaturthi",
            deityTa = "விநாயகப் பெருமான் / யமன்", deityEn = "Lord Ganesha / Yama",
            categoryTa = "ரிக்தா (விலக்கத்தக்கது)", categoryEn = "Rikta (Avoid New Starts)",
            elementTa = "வாயு (காற்று)", elementEn = "Vayu (Air)",
            auspiciousTa = "விநாயகர் வழிபாடு, கடன் தீர்த்தல், தீய பழக்கங்களை கைவிடுதல், மற்றும் எதிரிகளை வெற்றிகொள்ளும் காரியங்களுக்கு சிறந்தது.",
            auspiciousEn = "Favorable for debt clearance, spiritual purification, destroying negativities, and Ganesha worship.",
            vrathamTa = "சங்கடஹர சதுர்த்தி விரதம். சங்கடங்கள் யாவும் தூள்தூளாகும்.",
            vrathamEn = "Sankashti Chaturthi fasting to dissolve all hardships and hurdles."
        ),
        // 5. பஞ்சமி (Panchami) - பூர்ணா
        StaticTithiData(
            nameTa = "பஞ்சமி", nameEn = "Panchami",
            deityTa = "நாக தேவதை / சரஸ்வதி", deityEn = "Serpent Deities / Saraswati",
            categoryTa = "பூர்ணா (முழுமை)", categoryEn = "Purna (Complete)",
            elementTa = "ஆகாயம் (விண்)", elementEn = "Akasha (Ether)",
            auspiciousTa = "கிரகப்பிரவேசம், மருந்து உட்கொள்ளுதல், விவசாயப் பணிகள், கல்வி தொடக்கம், மற்றும் சுப நிகழ்வுகள்.",
            auspiciousEn = "Great for housewarming, taking medicines, agriculture, learning, and auspicious ceremonies.",
            vrathamTa = "நாக பஞ்சமி, வராகி அம்மன் பஞ்சமி விரதம். நாக தோஷம் நீங்கும்.",
            vrathamEn = "Naga Panchami and Varahi worship for clearing planetary afflictions."
        ),
        // 6. சஷ்டி (Sashti) - நந்தா
        StaticTithiData(
            nameTa = "சஷ்டி", nameEn = "Sashti",
            deityTa = "முருகப்பெருமான் (கந்தன்)", deityEn = "Lord Murugan (Kartikeya)",
            categoryTa = "நந்தா (ஆனந்தம்)", categoryEn = "Nanda (Joyous)",
            elementTa = "பிருத்வி (பூமி)", elementEn = "Prithvi (Earth)",
            auspiciousTa = "புதிய நண்பர்களை அடைதல், வாகனப் பயணம், போர்க்கலைகள், மற்றும் முருகப்பெருமான் தரிசனம்.",
            auspiciousEn = "Good for meeting friends, buying vehicles, defense, and visiting Murugan temples.",
            vrathamTa = "கந்த சஷ்டி விரதம். புத்திர பாக்கியம் மற்றும் பகை வெல்லும் அருள் கிட்டும்.",
            vrathamEn = "Skanda Sashti fasting for child boon and complete victory over obstacles."
        ),
        // 7. சப்தமி (Saptami) - பத்ரா
        StaticTithiData(
            nameTa = "சப்தமி", nameEn = "Saptami",
            deityTa = "சூரிய பகவான்", deityEn = "Surya Bhagavan (Sun God)",
            categoryTa = "பத்ரா (சுபம்)", categoryEn = "Bhadra (Auspicious)",
            elementTa = "அப்பு (நீர்)", elementEn = "Appu (Water)",
            auspiciousTa = "வாகனம் வாங்குதல், தூர தேசப் பயணம், ஆன்மீக யாத்திரை, மற்றும் தலைமைத்துவப் பொறுப்புகள் ஏற்க நன்று.",
            auspiciousEn = "Auspicious for buying vehicles, long journeys, pilgrimages, and leadership roles.",
            vrathamTa = "ரத சப்தமி விரதம், ஆதித்ய ஹிருதய ஸ்தோத்திர பாராயணம். ஆரோக்கியம் மற்றும் புகழ் ஓங்கும்.",
            vrathamEn = "Ratha Saptami fasting and Surya worship for radiant health and longevity."
        ),
        // 8. அஷ்டமி (Ashtami) - ஜயா
        StaticTithiData(
            nameTa = "அஷ்டமி", nameEn = "Ashtami",
            deityTa = "துர்க்கை அம்மன் / பைரவர் / ருத்ரன்", deityEn = "Goddess Durga / Bhairava / Rudra",
            categoryTa = "ஜயா (வெற்றி)", categoryEn = "Jaya (Victory)",
            elementTa = "தேயு (நெருப்பு)", elementEn = "Theyu (Fire)",
            auspiciousTa = "ஆன்மீக விரதம், பைரவர் ஆராதனை, கோட்டைக் காவல், பாதுகாப்பு ஏற்பாடுகள் (சுப சுபகாரியங்களைத் தவிர்க்கவும்).",
            auspiciousEn = "Spiritual fasting, Bhairava puja, defense tasks. Avoid conventional joyous events like marriages.",
            vrathamTa = "தேய்பிறை அஷ்டமி பைரவர் வழிபாடு / கோகுலாஷ்டமி / துர்க்காஷ்டமி விரதம்.",
            vrathamEn = "Kala Bhairava Ashtami and Durga Ashtami for protection against negative energies."
        ),
        // 9. நவமி (Navami) - ரிக்தா
        StaticTithiData(
            nameTa = "நவமி", nameEn = "Navami",
            deityTa = "துர்க்கை / சரஸ்வதி / ஸ்ரீ ராமர்", deityEn = "Goddess Durga / Saraswati / Sri Rama",
            categoryTa = "ரிக்தா (விலக்கத்தக்கது)", categoryEn = "Rikta (Avoid New Starts)",
            elementTa = "வாயு (காற்று)", elementEn = "Vayu (Air)",
            auspiciousTa = "ஆயுத பூஜை, பயிர்களைப் பாதுகாத்தல், தீமைகளை அழித்தல், ஆன்மீக ஜெபம் (சுபகாரியங்கள் தவிர்ப்பது உத்தமம்).",
            auspiciousEn = "Destroying negative forces, tool worship, mantra sadhana. Avoid weddings/housewarmings.",
            vrathamTa = "ஸ்ரீ ராம நவமி, மகாநவமி விரதம். அதர்மத்தை அழித்து நன்மையை நிலைநாட்டும்.",
            vrathamEn = "Sri Rama Navami and Maha Navami for righteous triumph and spiritual empowerment."
        ),
        // 10. தசமி (Dasami) - பூர்ணா
        StaticTithiData(
            nameTa = "தசமி", nameEn = "Dasami",
            deityTa = "யமதர்ம ராஜா / அஷ்ட திக் பாலர்கள்", deityEn = "Lord Yama / Dikpalas",
            categoryTa = "பூர்ணா (முழுமை)", categoryEn = "Purna (Complete)",
            elementTa = "ஆகாயம் (விண்)", elementEn = "Akasha (Ether)",
            auspiciousTa = "விஜயதசமி வெற்றி காரியங்கள், சுபமுகூர்த்தம், வீடு கட்டுதல், வாகனம் வாங்குதல், புதிய முயற்சிகள் வெற்றி தரும்.",
            auspiciousEn = "Vijayadasami endeavors, auspicious beginnings, construction, trade, and learning.",
            vrathamTa = "விஜயதசமி விரதம். தொட்ட காரியங்கள் யாவும் துலங்கும்.",
            vrathamEn = "Vijayadasami observances for boundless success in all new endeavors."
        ),
        // 11. ஏகாதசி (Ekadashi) - நந்தா
        StaticTithiData(
            nameTa = "ஏகாதசி", nameEn = "Ekadashi",
            deityTa = "மகாவிஷ்ணு (ஸ்ரீமன் நாராயணன்)", deityEn = "Lord Vishnu (Narayana)",
            categoryTa = "நந்தா (ஆனந்தம்)", categoryEn = "Nanda (Joyous)",
            elementTa = "பிருத்வி (பூமி)", elementEn = "Prithvi (Earth)",
            auspiciousTa = "மகாவிஷ்ணு வழிபாடு, துளசி பூஜை, உபவாசம், தான தர்மங்கள் செய்தல், ஆன்மீக முன்னேற்றம்.",
            auspiciousEn = "Fasting, Vishnu worship, Tulasi puja, charitable donations, spiritual introspection.",
            vrathamTa = "சர்வ ஏகாதசி விரதம். சகல பாவங்களையும் போக்கி வைகுண்ட பதவி தரும் மகா விரதம்.",
            vrathamEn = "Ekadashi Mahavrata for physical detoxification, mental clarity, and spiritual salvation."
        ),
        // 12. துவாதசி (Dvadasi) - பத்ரா
        StaticTithiData(
            nameTa = "துவாதசி", nameEn = "Dvadasi",
            deityTa = "விஷ்ணு / வாசுதேவன்", deityEn = "Lord Vasudeva",
            categoryTa = "பத்ரா (சுபம்)", categoryEn = "Bhadra (Auspicious)",
            elementTa = "அப்பு (நீர்)", elementEn = "Appu (Water)",
            auspiciousTa = "ஏகாதசி பாரணை, அன்னதானம், தான தர்மம், மங்களப் பொருட்கள் வாங்குதல், புண்ணிய தீர்த்த ஸ்நானம்.",
            auspiciousEn = "Breaking fast with Satvik food (Paranai), Annadanam, holy baths, and charity.",
            vrathamTa = "துவாதசி பாரணை மற்றும் துளசி கல்யாணம்.",
            vrathamEn = "Dvadasi Paranai and Tulasi Vivaha blessings."
        ),
        // 13. திரயோதசி (Trayodasi) - ஜயா
        StaticTithiData(
            nameTa = "திரயோதசி", nameEn = "Trayodasi",
            deityTa = "மன்மதன் / சிவபெருமான் (பிரதோஷம்)", deityEn = "Kamadeva / Lord Shiva (Pradosham)",
            categoryTa = "ஜயா (வெற்றி)", categoryEn = "Jaya (Victory)",
            elementTa = "தேயு (நெருப்பு)", elementEn = "Theyu (Fire)",
            auspiciousTa = "பிரதோஷ வழிபாடு, புதிய ஆடைகள் அணிதல், சிநேகம் வளர்த்தல், கலை மற்றும் இசை பயிற்சி.",
            auspiciousEn = "Pradosha Shiva puja, wearing new attire, deepening relationships, music, and art.",
            vrathamTa = "பிரதோஷ விரதம். மாலை வேளையில் நந்தி மற்றும் சிவ தரிசனம் செய்ய சகல தோஷங்களும் நீங்கும்.",
            vrathamEn = "Pradosham fasting at twilight to dissolve all karmic sins and sorrows."
        ),
        // 14. சதுர்த்தசி (Chaturdasi) - ரிக்தா
        StaticTithiData(
            nameTa = "சதுர்த்தசி", nameEn = "Chaturdasi",
            deityTa = "ருத்ரன் / காளி தேவி", deityEn = "Rudra / Goddess Kali",
            categoryTa = "ரிக்தா (விலக்கத்தக்கது)", categoryEn = "Rikta (Avoid New Starts)",
            elementTa = "வாயு (காற்று)", elementEn = "Vayu (Air)",
            auspiciousTa = "சிவராத்திரி வழிபாடு, நரசிம்மர் ஆராதனை, மந்திர ஜெபம், தீய சக்திகள் விரட்டுதல் (சுபகாரியங்களைத் தவிர்க்கவும்).",
            auspiciousEn = "Shivaratri prayers, Narasimha puja, mantra chanting. Avoid marriage and construction starts.",
            vrathamTa = "மாத சிவராத்திரி விரதம் / நரசிம்ம ஜெயந்தி விரதம்.",
            vrathamEn = "Masa Shivaratri and Narasimha Jayanti fasting for protection."
        ),
        // 15. பௌர்ணமி / அமாவாசை (Pournami / Amavasya) - பூர்ணா
        StaticTithiData(
            nameTa = "பௌர்ணமி / அமாவாசை", nameEn = "Pournami / Amavasya",
            deityTa = "சந்திரன் / விஸ்வேதேவர் / பித்ருக்கள்", deityEn = "Moon God / Pitrus (Ancestors)",
            categoryTa = "பூர்ணா (முழுமை)", categoryEn = "Purna (Complete)",
            elementTa = "ஆகாயம் (விண்)", elementEn = "Akasha (Ether)",
            auspiciousTa = "பௌர்ணமி: சத்தியநாராயண பூஜை, கிரிவலம், லட்சுமி பூஜை. அமாவாசை: பித்ரு தர்ப்பணம், முன்னோர் வழிபாடு.",
            auspiciousEn = "Pournami: Satyanarayana puja, Giri Valam, Lakshmi worship. Amavasya: Pitru Tarpanam and charity.",
            vrathamTa = "பௌர்ணமி விரதம் (அம்பாள் அருள்) மற்றும் அமாவாசை பித்ரு தர்ப்பணம் (முன்னோர் ஆசி).",
            vrathamEn = "Pournami fasting for Goddess Lakshmi & Amavasya ancestral blessings."
        )
    )

    fun calculateDailyTithi(date: LocalDate, city: CityLocation): TithiDetail {
        val sunriseMinutes = 360.0 + (city.longitude - 80.0) * -2.0 + (date.monthValue % 3) * 3
        val jdSunrise = computeJulianDay(date.year, date.monthValue, date.dayOfMonth) + (sunriseMinutes - 330.0) / 1440.0

        val sunSidereal = calculateSunSidereal(jdSunrise)
        val moonSidereal = calculateMoonSidereal(jdSunrise)

        val diff = (moonSidereal - sunSidereal + 360.0) % 360.0
        val tithiIndex = ((diff / 12.0).toInt() + 1).coerceIn(1, 30)
        val isSukla = tithiIndex <= 15
        val pakshaTa = if (isSukla) "வளர்பிறை" else "தேய்பிறை"
        val pakshaEn = if (isSukla) "Sukla Paksha" else "Krishna Paksha"

        val metadataIndex = if (tithiIndex <= 15) tithiIndex - 1 else tithiIndex - 16
        val staticData = TITHI_METADATA[metadataIndex]

        val actualNameTa = if (tithiIndex == 15) "பௌர்ணமி" else if (tithiIndex == 30) "அமாவாசை" else staticData.nameTa
        val actualNameEn = if (tithiIndex == 15) "Pournami (Full Moon)" else if (tithiIndex == 30) "Amavasya (New Moon)" else staticData.nameEn

        // Find Start Time (searching backwards from Sunrise)
        val startMinutes = findTithiStartTimeMinutes(jdSunrise, sunriseMinutes.toInt(), tithiIndex)
        val startTimeStr = formatTransitionTime(startMinutes, isStartTime = true)

        // Find End Time (searching forwards from Sunrise)
        val endMinutes = findTithiEndTimeMinutes(jdSunrise, sunriseMinutes.toInt(), tithiIndex)
        val endTimeStr = formatTransitionTime(endMinutes, isStartTime = false)

        val durationHours = Math.round(((endMinutes - startMinutes) / 60.0) * 10.0) / 10.0

        // Next Tithi
        val nextTithiIndex = (tithiIndex % 30) + 1
        val nextIsSukla = nextTithiIndex <= 15
        val nextPakshaTa = if (nextIsSukla) "வளர்பிறை" else "தேய்பிறை"
        val nextPakshaEn = if (nextIsSukla) "Sukla Paksha" else "Krishna Paksha"
        val nextMetaIdx = if (nextTithiIndex <= 15) nextTithiIndex - 1 else nextTithiIndex - 16
        val nextStatic = TITHI_METADATA[nextMetaIdx]
        val nextNameTa = if (nextTithiIndex == 15) "பௌர்ணமி" else if (nextTithiIndex == 30) "அமாவாசை" else nextStatic.nameTa
        val nextNameEn = if (nextTithiIndex == 15) "Pournami" else if (nextTithiIndex == 30) "Amavasya" else nextStatic.nameEn

        // Real-time live check
        val isToday = date == LocalDate.now()
        val currentMinutesNow = if (isToday) {
            val now = LocalTime.now()
            now.hour * 60 + now.minute
        } else {
            sunriseMinutes.toInt()
        }

        val isFinished = isToday && currentMinutesNow >= endMinutes

        var liveNameTa = actualNameTa
        var liveNameEn = actualNameEn
        var livePakshaTa = pakshaTa
        var livePakshaEn = pakshaEn
        var liveDeityTa = staticData.deityTa
        var liveDeityEn = staticData.deityEn

        if (isFinished) {
            val jdNow = computeJulianDay(date.year, date.monthValue, date.dayOfMonth) + (currentMinutesNow - 330.0) / 1440.0
            val sunNow = calculateSunSidereal(jdNow)
            val moonNow = calculateMoonSidereal(jdNow)
            val diffNow = (moonNow - sunNow + 360.0) % 360.0
            val liveIdx = ((diffNow / 12.0).toInt() + 1).coerceIn(1, 30)
            val liveSukla = liveIdx <= 15
            val liveMetaIdx = if (liveIdx <= 15) liveIdx - 1 else liveIdx - 16
            val liveMeta = TITHI_METADATA[liveMetaIdx]
            liveNameTa = if (liveIdx == 15) "பௌர்ணமி" else if (liveIdx == 30) "அமாவாசை" else liveMeta.nameTa
            liveNameEn = if (liveIdx == 15) "Pournami" else if (liveIdx == 30) "Amavasya" else liveMeta.nameEn
            livePakshaTa = if (liveSukla) "வளர்பிறை" else "தேய்பிறை"
            livePakshaEn = if (liveSukla) "Sukla Paksha" else "Krishna Paksha"
            liveDeityTa = liveMeta.deityTa
            liveDeityEn = liveMeta.deityEn
        }

        return TithiDetail(
            index = tithiIndex,
            nameTamil = actualNameTa,
            nameEnglish = actualNameEn,
            pakshaTamil = pakshaTa,
            pakshaEnglish = pakshaEn,
            deityTamil = staticData.deityTa,
            deityEnglish = staticData.deityEn,
            categoryTamil = staticData.categoryTa,
            categoryEnglish = staticData.categoryEn,
            elementTamil = staticData.elementTa,
            elementEnglish = staticData.elementEn,
            startTime = startTimeStr,
            endTime = endTimeStr,
            durationHours = durationHours,
            auspiciousActivitiesTamil = staticData.auspiciousTa,
            auspiciousActivitiesEnglish = staticData.auspiciousEn,
            vrathamSignificanceTamil = staticData.vrathamTa,
            vrathamSignificanceEnglish = staticData.vrathamEn,
            nextTithiTamil = nextNameTa,
            nextTithiEnglish = nextNameEn,
            nextPakshaTamil = nextPakshaTa,
            nextPakshaEnglish = nextPakshaEn,
            isFinished = isFinished,
            currentLiveNameTamil = liveNameTa,
            currentLiveNameEnglish = liveNameEn,
            currentLivePakshaTamil = livePakshaTa,
            currentLivePakshaEnglish = livePakshaEn,
            currentLiveDeityTamil = liveDeityTa,
            currentLiveDeityEnglish = liveDeityEn
        )
    }

    private fun findTithiStartTimeMinutes(jdSunrise: Double, sunriseMinutes: Int, currentTithiIndex: Int): Int {
        val targetAngle = (currentTithiIndex - 1) * 12.0
        var lowHours = -30.0
        var highHours = 0.0

        for (step in 0..50) {
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

        return (sunriseMinutes + highHours * 60.0).toInt()
    }

    private fun findTithiEndTimeMinutes(jdSunrise: Double, sunriseMinutes: Int, currentTithiIndex: Int): Int {
        val targetAngle = currentTithiIndex * 12.0
        var lowHours = 0.0
        var highHours = 30.0

        for (step in 0..50) {
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

        return (sunriseMinutes + highHours * 60.0).toInt()
    }

    private fun formatTransitionTime(minutesFromMidnight: Int, isStartTime: Boolean): String {
        val isPastDay = minutesFromMidnight < 0
        val isNextDay = minutesFromMidnight >= 1440
        var normalizedMin = minutesFromMidnight % 1440
        if (normalizedMin < 0) normalizedMin += 1440

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
            isPastDay -> "நேற்று இரவு"
            isNextDay -> "நாளை அதிகாலை"
            hours24 in 4..11 -> "காலை"
            hours24 in 12..15 -> "மதியம்"
            hours24 in 16..18 -> "மாலை"
            else -> "இரவு"
        }

        return if (isStartTime) {
            "$periodTamil $timeFormatted முதல்"
        } else {
            val suffix = if (isNextDay) " (மறுநாள்)" else ""
            "$periodTamil $timeFormatted வரை$suffix"
        }
    }

    private fun calculateSunSidereal(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        var l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t * t
        var m = 357.52911 + 35999.05029 * t - 0.0001537 * t * t
        l0 %= 360.0; if (l0 < 0) l0 += 360.0
        m %= 360.0; if (m < 0) m += 360.0
        val mRad = Math.toRadians(m)
        val c = (1.914602 - 0.004817 * t) * sin(mRad) + (0.019993 - 0.000101 * t) * sin(2 * mRad)
        val sunTropical = (l0 + c) % 360.0
        val ayanamsa = calculateLahiriAyanamsa(jd)
        var sunSidereal = (sunTropical - ayanamsa) % 360.0
        if (sunSidereal < 0) sunSidereal += 360.0
        return sunSidereal
    }

    private fun calculateMoonSidereal(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        var lp = 218.3164477 + 481267.88128 * t
        var d = 297.8501921 + 445267.11140 * t
        var m = 357.5291092 + 35999.05029 * t
        var mp = 134.9633964 + 477198.86750 * t
        var f = 93.2720950 + 483202.01752 * t
        lp %= 360.0; if (lp < 0) lp += 360.0
        d %= 360.0; if (d < 0) d += 360.0
        m %= 360.0; if (m < 0) m += 360.0
        mp %= 360.0; if (mp < 0) mp += 360.0
        f %= 360.0; if (f < 0) f += 360.0

        val moonTropical = lp + 6.288774 * sin(Math.toRadians(mp)) +
                1.274027 * sin(Math.toRadians(2 * d - mp)) +
                0.658309 * sin(Math.toRadians(2 * d)) +
                0.213618 * sin(Math.toRadians(2 * mp)) -
                0.185116 * sin(Math.toRadians(m)) -
                0.114332 * sin(Math.toRadians(2 * f))

        val ayanamsa = calculateLahiriAyanamsa(jd)
        var moonSidereal = (moonTropical - ayanamsa) % 360.0
        if (moonSidereal < 0) moonSidereal += 360.0
        return moonSidereal
    }

    private fun calculateLahiriAyanamsa(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        return 23.85 + (50.29 / 3600.0) * (t * 100.0)
    }

    private fun computeJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + (a / 4)
        return (365.25 * (y + 4716)).toInt() + (30.6001 * (m + 1)).toInt() + day + b - 1524.5
    }
}
