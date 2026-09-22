package com.portocale.volunteer.config

import java.util.*


enum class LanguageApi {
    EN,
    RO,
    RU
}


fun Locale.toLanguageApi(): LanguageApi {
    return when (language.lowercase()) {
        "ro" -> LanguageApi.RO
        "en" -> LanguageApi.EN
        "ru" -> LanguageApi.RU
        else -> LanguageApi.RO
    }
}
