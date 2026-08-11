package com.example.api

import com.example.engine.ThiruGanithaEngine
import com.example.model.CityLocation
import com.example.model.PanchangResult
import java.time.LocalDate

object PanchangApiService {

    data class ApiResponse<T>(
        val status: Int,
        val message: String,
        val timestamp: String,
        val endpoint: String,
        val parameters: Map<String, String>,
        val data: T
    )

    fun getFullPanchangJson(
        dateIso: String,
        cityId: String = "chennai",
        language: String = "ta"
    ): Pair<Int, String> {
        val date = try {
            LocalDate.parse(dateIso)
        } catch (e: Exception) {
            LocalDate.now()
        }
        val city = CityLocation.DEFAULT_CITIES.find { it.id.equals(cityId, ignoreCase = true) }
            ?: CityLocation.DEFAULT_CITIES[0]

        val result = ThiruGanithaEngine.calculatePanchang(date, city)

        val json = """
{
  "status": 200,
  "message": "Success - Thiru Ganitha Panchangam calculated successfully",
  "api_version": "v1.0.0",
  "engine": "Thiru Ganitha (Drik) Astronomical Calculation Engine",
  "query_parameters": {
    "date": "${result.dateIso}",
    "city": "${city.nameEnglish} (${city.nameTamil})",
    "latitude": ${city.latitude},
    "longitude": ${city.longitude},
    "timezone": "UTC+${city.timezoneOffsetHours}"
  },
  "panchangam": {
    "gregorian_date": "${result.dateIso}",
    "display_date_tamil": "${result.dateDisplayTamil}",
    "display_date_english": "${result.dateDisplayEnglish}",
    "tamil_year": {
      "ta": "${result.tamilYearTamil}",
      "en": "${result.tamilYearEnglish}"
    },
    "tamil_month": {
      "ta": "${result.tamilMonthTamil}",
      "en": "${result.tamilMonthEnglish}",
      "day": ${result.tamilDay}
    },
    "ayana": {
      "ta": "${result.ayanaTamil}",
      "en": "${result.ayanaEnglish}"
    },
    "pancha_angam": {
      "tithi": {
        "index": ${result.tithi.index},
        "name_ta": "${result.tithi.nameTamil}",
        "name_en": "${result.tithi.nameEnglish}",
        "paksha_ta": "${result.tithi.pakshaTamil}",
        "paksha_en": "${result.tithi.pakshaEnglish}",
        "end_time": "${result.tithi.endTime}",
        "deity": "${result.tithi.deityTamil} / ${result.tithi.deityEnglish}"
      },
      "nakshatra": {
        "index": ${result.nakshatra.index},
        "name_ta": "${result.nakshatra.nameTamil}",
        "name_en": "${result.nakshatra.nameEnglish}",
        "pada": ${result.nakshatra.pada},
        "rasi_ta": "${result.nakshatra.rasiTamil}",
        "rasi_en": "${result.nakshatra.rasiEnglish}",
        "ruling_planet": "${result.nakshatra.rulingPlanetTamil} / ${result.nakshatra.rulingPlanetEnglish}",
        "end_time": "${result.nakshatra.endTime}"
      },
      "yoga": {
        "index": ${result.yoga.index},
        "name_ta": "${result.yoga.nameTamil}",
        "name_en": "${result.yoga.nameEnglish}",
        "nature": "${result.yoga.natureTamil} / ${result.yoga.natureEnglish}",
        "end_time": "${result.yoga.endTime}"
      },
      "karana": {
        "index": ${result.karana.index},
        "name_ta": "${result.karana.nameTamil}",
        "name_en": "${result.karana.nameEnglish}",
        "type": "${result.karana.typeTamil} / ${result.karana.typeEnglish}"
      },
      "vaara": {
        "day_of_week": ${result.vaara.dayOfWeek},
        "name_ta": "${result.vaara.nameTamil}",
        "name_en": "${result.vaara.nameEnglish}",
        "ruling_planet": "${result.vaara.planetTamil} / ${result.vaara.planetEnglish}"
      }
    },
    "sun_and_moon": {
      "sunrise": "${result.sunrise}",
      "sunset": "${result.sunset}",
      "moonrise": "${result.moonrise}",
      "sun_sign": "${result.sunSignTamil} / ${result.sunSignEnglish}",
      "moon_sign": "${result.moonSignTamil} / ${result.moonSignEnglish}"
    },
    "auspicious_timings": {
      "nalla_neram": {
        "morning": "${result.nallaNeram.morning.formatted}",
        "evening": "${result.nallaNeram.evening.formatted}"
      },
      "gowri_nalla_neram": {
        "morning": "${result.gowriNallaNeram.morning.formatted}",
        "evening": "${result.gowriNallaNeram.evening.formatted}"
      }
    },
    "inauspicious_timings": {
      "rahu_kalam": "${result.rahuKalam.formatted}",
      "yamagandam": "${result.yamagandam.formatted}",
      "kuligai": "${result.kuligai.formatted}",
      "durmuhurtham": "${result.durmuhurtham.formatted}"
    },
    "astrological_notes": {
      "chandrashtama_star": "${result.chandrashtamaStarTamil} (${result.chandrashtamaStarEnglish})",
      "chandrashtama_rasi": "${result.chandrashtamaRasiTamil} (${result.chandrashtamaRasiEnglish})",
      "nethiram": ${result.nethiram},
      "jeevan": "${result.jeevan}"
    },
    "special_events": {
      "events_ta": ${result.specialEventsTamil.joinToString(prefix = "[\"", separator = "\", \"", postfix = "\"]")},
      "events_en": ${result.specialEventsEnglish.joinToString(prefix = "[\"", separator = "\", \"", postfix = "\"]")},
      "is_subha_muhurtham": ${result.isSubhaMuhurtham},
      "is_amavasya": ${result.isAmavasya},
      "is_purnima": ${result.isPurnima},
      "is_pradosham": ${result.isPradosham},
      "is_ekadashi": ${result.isEkadashi}
    }
  }
}
        """.trimIndent()
        return Pair(200, json)
    }

    fun getTithiJson(dateIso: String): Pair<Int, String> {
        val date = try { LocalDate.parse(dateIso) } catch (e: Exception) { LocalDate.now() }
        val result = ThiruGanithaEngine.calculatePanchang(date)
        val json = """
{
  "status": 200,
  "endpoint": "/api/v1/tithi",
  "date": "$dateIso",
  "tithi": {
    "number": ${result.tithi.index},
    "name_tamil": "${result.tithi.nameTamil}",
    "name_english": "${result.tithi.nameEnglish}",
    "paksha_tamil": "${result.tithi.pakshaTamil}",
    "paksha_english": "${result.tithi.pakshaEnglish}",
    "deity": "${result.tithi.deityTamil} / ${result.tithi.deityEnglish}",
    "end_time": "${result.tithi.endTime}"
  }
}
        """.trimIndent()
        return Pair(200, json)
    }

    fun getNakshatraJson(dateIso: String): Pair<Int, String> {
        val date = try { LocalDate.parse(dateIso) } catch (e: Exception) { LocalDate.now() }
        val result = ThiruGanithaEngine.calculatePanchang(date)
        val json = """
{
  "status": 200,
  "endpoint": "/api/v1/nakshatra",
  "date": "$dateIso",
  "nakshatra": {
    "number": ${result.nakshatra.index},
    "name_tamil": "${result.nakshatra.nameTamil}",
    "name_english": "${result.nakshatra.nameEnglish}",
    "pada": ${result.nakshatra.pada},
    "rasi_tamil": "${result.nakshatra.rasiTamil}",
    "rasi_english": "${result.nakshatra.rasiEnglish}",
    "ruling_planet": "${result.nakshatra.rulingPlanetTamil} / ${result.nakshatra.rulingPlanetEnglish}",
    "end_time": "${result.nakshatra.endTime}"
  }
}
        """.trimIndent()
        return Pair(200, json)
    }

    fun getGowriJson(dateIso: String): Pair<Int, String> {
        val date = try { LocalDate.parse(dateIso) } catch (e: Exception) { LocalDate.now() }
        val result = ThiruGanithaEngine.calculatePanchang(date)
        val gowriArray = result.gowriDayList.joinToString(separator = ",\n    ") { g ->
            """{"period": ${g.periodIndex}, "time": "${g.timeSlot}", "gowri_ta": "${g.nameTamil}", "gowri_en": "${g.nameEnglish}", "quality": "${g.qualityTamil}", "is_good": ${g.isGood}}"""
        }
        val json = """
{
  "status": 200,
  "endpoint": "/api/v1/gowri",
  "date": "$dateIso",
  "gowri_panchangam": [
    $gowriArray
  ]
}
        """.trimIndent()
        return Pair(200, json)
    }

    fun getHoraiJson(dateIso: String): Pair<Int, String> {
        val date = try { LocalDate.parse(dateIso) } catch (e: Exception) { LocalDate.now() }
        val result = ThiruGanithaEngine.calculatePanchang(date)
        val horaiArray = result.horaiDayList.joinToString(separator = ",\n    ") { h ->
            """{"period": ${h.periodIndex}, "time": "${h.timeSlot}", "planet_ta": "${h.planetTamil}", "planet_en": "${h.planetEnglish}", "quality": "${h.qualityTamil}", "is_good": ${h.isGood}}"""
        }
        val json = """
{
  "status": 200,
  "endpoint": "/api/v1/horai",
  "date": "$dateIso",
  "subha_horai": [
    $horaiArray
  ]
}
        """.trimIndent()
        return Pair(200, json)
    }
}
