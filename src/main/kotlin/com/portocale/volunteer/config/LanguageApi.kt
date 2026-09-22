package com.portocale.volunteer.config

import java.util.*


enum class LanguageApi(val value: String) {
    EN("en"),
    RO("ro"),
    RU("ru")
}


fun Locale.toLanguageApi(): LanguageApi {
    return when (language.lowercase()) {
        "ro" -> LanguageApi.RO
        "en" -> LanguageApi.EN
        "ru" -> LanguageApi.RU
        else -> LanguageApi.RO
    }
}
