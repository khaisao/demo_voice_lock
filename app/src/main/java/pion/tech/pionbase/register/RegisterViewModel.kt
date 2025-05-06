package pion.tech.pionbase.register

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val speakerManager: SpeakerManager
) : BaseViewModel() {

    val isRecording = MutableStateFlow(false)

    val isModelTalking = MutableStateFlow<Boolean?>(null)

    private val _listEmbedded = MutableStateFlow<List<FloatArray>>(emptyList())
    val listEmbedded = _listEmbedded.asStateFlow()
    fun addEmbedded(embedded: FloatArray) {
        val currentList = _listEmbedded.value.toMutableList()
        currentList.add(embedded)
        _listEmbedded.value = currentList
    }

    val listContent = mutableListOf(
        "Thời tiết đang như thế nào",
        "Mở khóa điện thoại của tôi",
        "Tìm đường đến quán cà phê gần nhất",
        "Bật nhạc yêu thích của tôi lên",
        "Tăng độ sáng màn hình lên tối đa"
    )

    val currentIndexSpeak = MutableStateFlow<Int?>(null)
    fun increaseIndexSpeak() {
        if (currentIndexSpeak.value == null) {
            currentIndexSpeak.value = 0
        } else {
            if (currentIndexSpeak.value!! < listContent.size - 1) {
                currentIndexSpeak.value = currentIndexSpeak.value!! + 1
            }
        }
    }

    fun getContentSpeak(index:Int): String {
        return listContent[index]
    }

    val isRecordingDone = MutableStateFlow(false)

    fun saveSpeaker(name: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        launchIO({
            onFailure.invoke()
        }) {
           val success = speakerManager.saveSpeaker(name, listEmbedded.value.toTypedArray())
            if (success) {
                withContext(Dispatchers.Main){
                    onSuccess.invoke()
                }
            } else {
                withContext(Dispatchers.Main){
                    onFailure.invoke()
                }
            }
        }
    }

}