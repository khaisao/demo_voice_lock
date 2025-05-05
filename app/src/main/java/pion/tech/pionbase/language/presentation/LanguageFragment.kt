package pion.tech.pionbase.language.presentation

import android.view.View
import androidx.core.view.isVisible
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.main.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentLanguageBinding
import pion.tech.pionbase.language.presentation.model.LanguageUIModel
import pion.tech.pionbase.language.presentation.adapter.LanguageAdapter

@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding, LanguageViewModel,CommonViewModel>(
    FragmentLanguageBinding::inflate,
    LanguageViewModel::class.java,
    CommonViewModel::class.java,
), LanguageAdapter.Listener {

    val adapter = LanguageAdapter()

    override fun init(view: View) {
        initView()
        applyEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.languageData.collectFlowOnView(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onClickLanguage(item: LanguageUIModel, position: Int) {
        binding.ivDone.isVisible = true
        viewModel.selectLanguage(item)
    }

}
