package pion.tech.pionbase.register

import com.piontech.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
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

    val listContent = mutableListOf<String>(
        "Thời tiết đang như thế nào",
        "Mở khóa điện thoại của tôi",
        "Khô heo cháy tỏi",
        "Được đóng gói và phân phối bởi",
        "Ăn liền hoặc chế biến các món khác"
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

}