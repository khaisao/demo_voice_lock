package pion.tech.pionbase.language.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.language.domain.model.LanguageData

class GetLanguageUseCase {
    operator fun invoke(): Flow<List<LanguageData>> {
        val listLanguageData = listOf(
            LanguageData("https://flagcdn.com/w320/us.png", "English", "en"),
            LanguageData("https://flagcdn.com/w320/es.png", "Español", "es"),
            LanguageData("https://flagcdn.com/w320/sa.png", "عربي", "ar"),
            LanguageData("https://flagcdn.com/w320/pt.png", "Português", "pt"),
            LanguageData("https://flagcdn.com/w320/fr.png", "Français", "fr"),
            LanguageData("https://flagcdn.com/w320/de.png", "Deutsch", "de"),
            LanguageData("https://flagcdn.com/w320/cn.png", "中國人", "zh"),
            LanguageData("https://flagcdn.com/w320/kr.png", "한국인", "ko"),
            LanguageData("https://flagcdn.com/w320/jp.png", "日本人", "ja"),
            LanguageData("https://flagcdn.com/w320/ru.png", "Pусский", "ru"),
            LanguageData("https://flagcdn.com/w320/vn.png", "Việt Nam", "vi"),
            LanguageData("https://flagcdn.com/w320/th.png", "ไทย", "th"),
            LanguageData("https://flagcdn.com/w320/tr.png", "Türkçe", "tr"),
            LanguageData("https://flagcdn.com/w320/in.png", "हिंदी", "hi"),
            LanguageData("https://flagcdn.com/w320/uz.png", "O'zbek", "uz"),
            LanguageData("https://flagcdn.com/w320/it.png", "Italiano", "it"),
            LanguageData("https://flagcdn.com/w320/pl.png", "Polski", "pl"),
            LanguageData("https://flagcdn.com/w320/ir.png", "فارسی", "fa"),
            LanguageData("https://flagcdn.com/w320/ua.png", "Українська Мова", "uk"),
            LanguageData("https://flagcdn.com/w320/ro.png", "Română", "ro"),
            LanguageData("https://flagcdn.com/w320/nl.png", "Nederlands", "nl"),
            LanguageData("https://flagcdn.com/w320/hu.png", "Magyar", "hu"),
            LanguageData("https://flagcdn.com/w320/bg.png", "Български Език", "bg"),
            LanguageData("https://flagcdn.com/w320/gr.png", "Ελληνικά", "el")
        )
        return flow {
            emit(listLanguageData)
        }.catch {
            emit(emptyList())
        }.flowOn(Dispatchers.IO)
    }
}