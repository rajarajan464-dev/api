package com.example.model

/**
 * Detailed Information about a Nakshatra (நட்சத்திரம்)
 * containing full lifecycle timing, pada distribution, planetary lords, and deity details.
 */
data class NakshatraDetail(
    val index: Int,                         // 1 to 27
    val nameTamil: String,                  // எ.கா: அஸ்வினி, பரணி, கார்த்திகை...
    val nameEnglish: String,                // Ashwini, Bharani, Krittika...
    val startDegree: Double,                // தொடக்க பாகை (எ.கா: 0.0°)
    val endDegree: Double,                  // முடிவு பாகை (எ.கா: 13° 20' = 13.3333°)
    val pada: Int,                          // பாதம் (1, 2, 3, 4)
    val rasiTamil: String,                  // ராசி (தமிழ்)
    val rasiEnglish: String,                // Rasi (English)
    val rulingPlanetTamil: String,          // அதிபதி கிரகம் (கேது, சுக்ரன், சூரியன்...)
    val rulingPlanetEnglish: String,        // Ketu, Venus, Sun...
    val deityTamil: String,                 // அதிதேவதை (அஸ்வினி குமாரர்கள்...)
    val deityEnglish: String,               // Deity
    val symbolTamil: String,                // சின்னம் (குதிரை முகம்...)
    val symbolEnglish: String,              // Symbol
    val ganaTamil: String,                  // கணம் (தேவ / மனித / ராட்சச)
    val ganaEnglish: String,                // Gana (Deva / Manushya / Rakshasa)
    val startTime: String,                  // அன்றைய ஆரம்ப நேரம் (எ.கா: "நேற்று இரவு 11:20 PM" அல்லது "காலை 06:00 AM")
    val endTime: String,                    // அன்றைய முடிவு நேரம் (எ.கா: "மாலை 04:58 PM வரை")
    val durationHours: Double,              // தோராய கால அளவு (மணி நேரங்கள்)
    val nextNakshatraTamil: String,         // அடுத்த நட்சத்திரம் (தமிழ்)
    val nextNakshatraEnglish: String,       // Next Nakshatra (English)
    val isFinished: Boolean = false,
    val currentLiveNameTamil: String = nameTamil,
    val currentLiveNameEnglish: String = nameEnglish,
    val currentLivePada: Int = pada,
    val currentLiveRasiTamil: String = rasiTamil,
    val currentLiveRasiEnglish: String = rasiEnglish,
    val currentLiveRulingPlanetTamil: String = rulingPlanetTamil,
    val currentLiveRulingPlanetEnglish: String = rulingPlanetEnglish
)
