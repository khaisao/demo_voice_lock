package pion.tech.pionbase.testSpeaker

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.k2fsa.sherpa.onnx.SpeakerRecognition
import com.permissionx.guolindev.PermissionX
import com.piontech.core.base.launchIO
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClick
import timber.log.Timber
import kotlin.math.sqrt

fun TestSpeakerFragment.startRecordingEvent() {

    binding.btnRecording.setPreventDoubleClick {
        PermissionX.init(this)
            .permissions(Manifest.permission.RECORD_AUDIO)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    if (viewModel.isRecording.value) {
                        stopRecord()
                    } else {
                        startRecord()
                    }
                }
            }

    }
}

@SuppressLint("MissingPermission")
fun TestSpeakerFragment.startRecord() {
    viewModel.nameDetected.value = ""
    viewModel.isRecording.value = true

    val audioSource = MediaRecorder.AudioSource.MIC
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val numBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)

    audioRecord = AudioRecord(
        audioSource,
        sampleRateInHz,
        channelConfig,
        audioFormat,
        numBytes * 2
    )

    sampleList = null

    launchIO {
        val interval = 0.1 // 100 ms
        val bufferSize = (interval * sampleRateInHz).toInt()
        val buffer = ShortArray(bufferSize)

        val silenceThreshold = 0.02f // Ngưỡng yên lặng
        val silenceDuration = 1500   // 1.5 giây
        var silenceStartTime: Long? = null
        var hasSpoken = false

        audioRecord?.let {
            it.startRecording()

            while (viewModel.isRecording.value) {
                val ret = it.read(buffer, 0, buffer.size)
                if (ret > 0) {
                    val samples = FloatArray(ret) { i -> buffer[i] / 32768.0f }
                    sampleList?.add(samples) ?: run { sampleList = mutableListOf(samples) }

                    val rms = sqrt(samples.map { s -> s * s }.average()).toFloat()

                    if (rms >= silenceThreshold) {
                        hasSpoken = true
                        silenceStartTime = null
                    } else if (hasSpoken) {
                        if (silenceStartTime == null) {
                            silenceStartTime = System.currentTimeMillis()
                        } else if (System.currentTimeMillis() - silenceStartTime!! > silenceDuration) {
                            // Im lặng sau khi đã nói → dừng ghi
                            viewModel.isRecording.value = false
                            stopRecord()
                        }
                    }
                }
            }
        }
    }
}

private const val TAG = "TestSpeakerFragmentEx"
fun TestSpeakerFragment.stopRecord() {
    audioRecord?.stop()
    audioRecord?.release()
    audioRecord = null
    viewModel.isRecording.value = false
    launchIO({
        displayToast("Có lỗi, hãy thử lại")
    }) {
        if (sampleList == null) return@launchIO
        val stream = SpeakerRecognition.extractor.createStream()
        for (samples in sampleList!!) {
            stream.acceptWaveform(samples = samples, sampleRate = sampleRateInHz)
        }
        stream.inputFinished()
        if (SpeakerRecognition.extractor.isReady(stream)) {
            val embedding = SpeakerRecognition.extractor.compute(stream)
            val detectedName = SpeakerRecognition.manager.search(
                embedding = embedding,
                threshold = 0.5f,
            )
            Timber.tag(TAG).d("detectedName: ${detectedName}")
            val formatName = detectedName.ifBlank {
                "Không nhận diện được"
            }
            viewModel.nameDetected.value = formatName
        }
    }

}

