package com.example.model

/**
 * திருக்கணித பஞ்சாங்க முறைப்படி ஒரு நாளின் திதி பற்றிய முழுமையான
 * வானியல் & ஜோதிட விபரங்கள் (Tithi Full Details Model)
 */
data class TithiDetail(
    val index: Int,                         // திதி எண் (1 முதல் 30 வரை)
    val nameTamil: String,                  // திதி பெயர் (தமிழ்) - எ.கா: "ஏகாதசி"
    val nameEnglish: String,                // Tithi Name (English) - e.g. "Ekadashi"
    val pakshaTamil: String,                // பக்ஷம் (தமிழ்) - "வளர்பிறை" / "தேய்பிறை"
    val pakshaEnglish: String,              // Paksha (English) - "Sukla Paksha" / "Krishna Paksha"
    val deityTamil: String,                 // அதிதேவதை (தமிழ்)
    val deityEnglish: String,               // Presiding Deity (English)
    val categoryTamil: String,              // நந்தா / பத்ரா / ஜயா / ரிக்தா / பூர்ணா
    val categoryEnglish: String,            // Nanda / Bhadra / Jaya / Rikta / Purna
    val elementTamil: String,               // தத்துவம் (பிருத்வி/அப்பு/தேயு/வாயு/ஆகாயம்)
    val elementEnglish: String,             // Element (Earth/Water/Fire/Air/Ether)
    val startTime: String,                  // தொடங்கிய நேரம் (எ.கா: "நேற்று இரவு 09:12 PM முதல்")
    val endTime: String,                    // முடிவு நேரம் (எ.கா: "இன்று மாலை 05:48 PM வரை")
    val durationHours: Double,              // தோராய கால அளவு (மணி நேரங்கள்)
    val auspiciousActivitiesTamil: String,  // செய்யத்தக்க சுபகாரியங்கள்
    val auspiciousActivitiesEnglish: String,// Auspicious activities
    val vrathamSignificanceTamil: String,   // விரத விசேஷம் & வழிபாட்டு பலன்கள்
    val vrathamSignificanceEnglish: String, // Vratham and Spiritual significance
    val nextTithiTamil: String,             // அடுத்த திதி (தமிழ்)
    val nextTithiEnglish: String,           // Next Tithi (English)
    val nextPakshaTamil: String,            // அடுத்த பக்ஷம்
    val nextPakshaEnglish: String,
    val isFinished: Boolean = false,        // தற்போதைய நேரத்திற்கு இந்த திதி முடிந்துவிட்டதா
    val currentLiveNameTamil: String = nameTamil,       // தற்போது இயங்கும் நேரலை திதி
    val currentLiveNameEnglish: String = nameEnglish,
    val currentLivePakshaTamil: String = pakshaTamil,
    val currentLivePakshaEnglish: String = pakshaEnglish,
    val currentLiveDeityTamil: String = deityTamil,
    val currentLiveDeityEnglish: String = deityEnglish
)
