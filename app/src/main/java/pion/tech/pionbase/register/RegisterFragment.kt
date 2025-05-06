package pion.tech.pionbase.register

import android.media.AudioRecord
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentRegisterBinding
import pion.tech.pionbase.main.presentation.CommonViewModel
import timber.log.Timber
import java.util.Locale

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel, CommonViewModel>(
    FragmentRegisterBinding::inflate,
    RegisterViewModel::class.java,
    CommonViewModel::class.java,
) {

    var textToSpeech: TextToSpeech? = null

    var audioRecord: AudioRecord? = null
    val sampleRateInHz = 16000
    var sampleList: MutableList<FloatArray>? = null

    override fun init(view: View) {
        initTextToSpeech()
        initView()
        startTalking()
        registerEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.isRecording.collectFlowOnView(viewLifecycleOwner) {
            if (it) {
                binding.tvStart.text = "Dừng"
            } else {
                binding.tvStart.text = "Bắt đầu"
            }
        }

        viewModel.currentIndexSpeak.collectFlowOnView(viewLifecycleOwner) {
            if (it == null) return@collectFlowOnView
            val content = "Nói: ${viewModel.getContentSpeak(it).toLowerCase(Locale.ROOT)}"
            speakText(content)
            binding.tvContent.text = content

        }

        viewModel.listEmbedded.collectFlowOnView(viewLifecycleOwner) {
            binding.tvSize.text = "Số lượng bản ghi: ${it.size}"
            viewModel.isRecordingDone.value = it.size == viewModel.listContent.size
        }

        viewModel.isRecordingDone.collectFlowOnView(viewLifecycleOwner) {
            Timber.tag(TAG).d("isRecordingDone: ${it}")
            if(it){
                binding.tvRegister.isVisible = true
                binding.tvStart.isVisible = false
            }else{
                binding.tvRegister.isVisible = false
                binding.tvStart.isVisible = true
            }
        }
        viewModel.isModelTalking.collectFlowOnView(viewLifecycleOwner){
            binding.tvStart.isVisible = it == false
        }

    }


}
