package pion.tech.pionbase.register

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.k2fsa.sherpa.onnx.SpeakerRecognition
import com.permissionx.guolindev.PermissionX
import com.piontech.core.base.launchIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClick
import timber.log.Timber
import java.util.*

private const val TAG = "RegisterFragmentEx"

fun RegisterFragment.initTextToSpeech() {
    showHideLoading(true)
    textToSpeech = TextToSpeech(requireContext()) { status ->
        showHideLoading(false)
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale("vi")) // ngôn ngữ tiếng Việt

            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Bắt đầu nói
                    Timber.tag(TAG).d("onStart:")
                    viewModel.isModelTalking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    // Kết thúc nói
                    Timber.tag(TAG).d("onDone:")
                    viewModel.isModelTalking.value = false

                }

                override fun onError(utteranceId: String?) {
                    Timber.tag(TAG).d("onError:")
                    viewModel.isModelTalking.value = false
                }
            })

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                displayToast("Ngôn ngữ không được hỗ trợ!")
            } else {
                viewModel.increaseIndexSpeak()
            }
        } else {
            displayToast("Khởi tạo TextToSpeech thất bại!")
        }
    }
}

fun RegisterFragment.initView() {
    Timber.tag(TAG).d("initView: ")
}

fun RegisterFragment.speakText(text: String) {
    val utteranceId = "utteranceId_${System.currentTimeMillis()}"
    val bundle = Bundle()
    bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
    textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, utteranceId)
}

@SuppressLint("MissingPermission")
fun RegisterFragment.startTalking() {
    binding.tvStart.setPreventDoubleClick {
        val audioSource = MediaRecorder.AudioSource.MIC
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val numBytes =
            AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)

        PermissionX.init(this)
            .permissions(Manifest.permission.RECORD_AUDIO)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    audioRecord = AudioRecord(
                        audioSource,
                        sampleRateInHz,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        numBytes * 2 // a sample has two bytes as we are using 16-bit PCM
                    )
                    if (viewModel.isModelTalking.value == false) {
                        if (!viewModel.isRecording.value) {
                            viewModel.isRecording.value = true
                            launchIO({
                                displayToast("Có lỗi, hãy thử lại")
                            }) {

                                val interval = 0.1 // i.e., 100 ms
                                val bufferSize = (interval * sampleRateInHz).toInt() // in samples
                                val buffer = ShortArray(bufferSize)
                                audioRecord?.let {
                                    it.startRecording()

                                    while (viewModel.isRecording.value) {
                                        val ret = audioRecord?.read(buffer, 0, buffer.size)
                                        ret?.let { n ->
                                            val samples = FloatArray(n) { buffer[it] / 32768.0f }
                                            if (sampleList == null) {
                                                sampleList = mutableListOf(samples)
                                            } else {
                                                sampleList?.add(samples)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            launchIO({
                                displayToast("Có lỗi, hãy thử lại")
                            }) {
                                audioRecord?.stop()
                                audioRecord?.release()
                                audioRecord = null
                                if (sampleList == null) return@launchIO
                                val stream = SpeakerRecognition.extractor.createStream()
                                for (samples in sampleList!!) {
                                    stream.acceptWaveform(
                                        samples = samples,
                                        sampleRate = sampleRateInHz
                                    )
                                }
                                stream.inputFinished()
                                if (SpeakerRecognition.extractor.isReady(stream)) {
                                    val embedding = SpeakerRecognition.extractor.compute(stream)
                                    viewModel.addEmbedded(embedding)
                                    if ((viewModel.currentIndexSpeak.value
                                            ?: 0) < viewModel.listContent.size
                                    ) {
                                        viewModel.increaseIndexSpeak()
                                    }
                                }
                                viewModel.isRecording.value = false
                            }


                        }
                    }
                }
            }

    }
}

fun RegisterFragment.registerEvent() {
    binding.tvRegister.setPreventDoubleClick {
        val speakerName = binding.btnName.text.toString().trim()
        if (speakerName.isEmpty() || speakerName.isBlank()) {
            displayToast("Please input a speaker name")
            return@setPreventDoubleClick
        }
        if (SpeakerRecognition.manager.contains(speakerName)) {
            displayToast("A speaker with $speakerName already exists. Please choose a new name")
            return@setPreventDoubleClick
        }
        viewModel.saveSpeaker(name = speakerName, onSuccess = {
            displayToast("Đã lưu thành công")
            findNavController().navigateUp()
        }, onFailure = {
            displayToast("Lưu thất bại")
        })

    }
}
