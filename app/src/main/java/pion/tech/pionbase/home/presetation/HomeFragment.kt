package pion.tech.pionbase.home.presetation

import android.view.View
import androidx.core.view.isVisible
import com.k2fsa.sherpa.onnx.SpeakerRecognition
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.main.presentation.CommonViewModel
import pion.tech.pionbase.main.presentation.GetAppCategoryUiState
import pion.tech.pionbase.main.presentation.GetTemplateUiState
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.home.presetation.adapter.DemoMultipleAdapter
import pion.tech.pionbase.home.presetation.dialog.DemoDialog
import pion.tech.pionbase.util.displayToast
import timber.log.Timber

private const val TAG = "HomeFragment"
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel, CommonViewModel>(
    FragmentHomeBinding::inflate,
    HomeViewModel::class.java,
    CommonViewModel::class.java
), DemoDialog.Listener {

    val adapter = DemoMultipleAdapter()

    override fun init(view: View) {
        logger.logScreen("home_show")
        logger.logEvent("home_view")
        initView()
        plusEvent()
        settingEvent()
        onBackEvent()
        clearDataEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.countValue.collectFlowOnView(viewLifecycleOwner) {
//            binding.tvCount.text = "$it"
        }
        viewModel.listSpeaker.collectFlowOnView(viewLifecycleOwner){
            Timber.tag(TAG).d("subscribeObserver: $it")
            binding.tvData.isVisible = it.isNotEmpty()
            binding.tvData.text = "Speaker exits: ${it.joinToString(", ")}"
        }

    }

    override fun onDialogPositiveClick() {
    }

    override fun onDialogNegativeClick() {
        displayToast("Hello")
    }

}
