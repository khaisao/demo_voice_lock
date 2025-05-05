package pion.tech.pionbase.home.presetation

import com.piontech.core.base.BaseViewModel
import pion.tech.pionbase.main.domain.usecase.DataStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
) : BaseViewModel() {

    private val _countValue = MutableStateFlow(0)
    val countValue: StateFlow<Int> = _countValue

    fun plusValue() {
        _countValue.value += 1
    }

    suspend fun getIsPremiumValue(): Flow<Boolean> {
        return dataStoreUseCase.getIsPremium()
    }


}