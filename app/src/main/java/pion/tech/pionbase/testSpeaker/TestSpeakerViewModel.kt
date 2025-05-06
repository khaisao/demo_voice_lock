package pion.tech.pionbase.testSpeaker

import androidx.lifecycle.viewModelScope
import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TestSpeakerViewModel @Inject constructor(
) : BaseViewModel() {

    val isRecording = MutableStateFlow(false)
    val nameDetected = MutableStateFlow("")

}