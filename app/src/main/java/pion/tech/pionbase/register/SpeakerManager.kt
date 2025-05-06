package pion.tech.pionbase.register

import android.app.Application
import com.k2fsa.sherpa.onnx.SpeakerRecognition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pion.tech.pionbase.main.domain.repository.DataStoreRepository
import timber.log.Timber

private const val TAG = "SpeakerManager"

class SpeakerManager(
    private val dataStoreRepository: DataStoreRepository,
    private val application: Application
) {
    private val coroutineExceptionHandler = kotlinx.coroutines.CoroutineExceptionHandler { _, throwable ->
        Timber.tag(TAG).d("Error: ${throwable}")
    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    fun initialize() {
        SpeakerRecognition.initExtractor(application.assets)
        coroutineScope.launch {
            val speakerData = dataStoreRepository.getSpeakerEmbeddings().first()
            for ((name, embedding) in speakerData) {
                Timber.tag(TAG).d("name: ${name}")
                SpeakerRecognition.manager.add(name, embedding)
            }
        }
    }

    suspend fun saveSpeaker(name: String, embedding: Array<FloatArray>): Boolean {
        val ok = SpeakerRecognition.manager.add(name, embedding)
        Timber.tag(TAG).d("allSpeakerNames: ${allSpeakerNames()}")
        if (ok) {
            dataStoreRepository.addSpeakerEmbedding(name, embedding)
        }
        return ok
    }

    fun allSpeakerNames(): List<String> {
        return SpeakerRecognition.manager.allSpeakerNames().toList()
    }

    fun deleteAllSpeaker() {
        val allSpeakerNames = allSpeakerNames()
        allSpeakerNames.forEach {
            SpeakerRecognition.manager.remove(it)
        }
        coroutineScope.launch {
            dataStoreRepository.deleteAllSpeakerEmbedding()
        }

    }

}