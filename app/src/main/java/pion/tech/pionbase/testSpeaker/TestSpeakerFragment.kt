package pion.tech.pionbase.testSpeaker

import android.media.AudioRecord
import android.view.View
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentTestSpeakerBinding
import pion.tech.pionbase.main.presentation.CommonViewModel

@AndroidEntryPoint
class TestSpeakerFragment :
    BaseFragment<FragmentTestSpeakerBinding, TestSpeakerViewModel, CommonViewModel>(
        FragmentTestSpeakerBinding::inflate,
        TestSpeakerViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    val sampleRateInHz = 16000
    var audioRecord: AudioRecord? = null
     var sampleList: MutableList<FloatArray>? = null

    override fun init(view: View) {
        startRecordingEvent()

    }

    override fun subscribeObserver(view: View) {
        viewModel.isRecording.collectFlowOnView(viewLifecycleOwner) {
            if (it) {
                binding.btnRecording.text = "Dừng"
            } else {
                binding.btnRecording.text = "Bắt đầu"
            }
        }

        viewModel.nameDetected.collectFlowOnView(viewLifecycleOwner){
            binding.tvResult.text = it
        }

    }


}