package pion.tech.pionbase.language.presentation

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import pion.tech.pionbase.language.domain.usecase.GetLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pion.tech.pionbase.language.presentation.model.LanguageUIModel
import pion.tech.pionbase.language.presentation.model.toPresentation
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val getLanguageUseCase: GetLanguageUseCase
) : BaseViewModel() {

    private val _languageData = MutableStateFlow<List<LanguageUIModel>>(emptyList())
    val languageData = _languageData.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        launchIO {
            getLanguageUseCase.invoke().collect {
                _languageData.value = it.map { item -> item.toPresentation() }
            }
        }
    }

    fun selectLanguage(item: LanguageUIModel) {
        _languageData.value = _languageData.value.map { language ->
            language.copy(isSelected = language.localeCode == item.localeCode)
        }
    }

    fun getSelectedLanguage(): LanguageUIModel? {
        return _languageData.value.firstOrNull { it.isSelected }
    }
}