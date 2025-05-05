package pion.tech.pionbase.language.presentation.adapter

import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import com.piontech.core.utils.loadImage
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemLanguageBinding
import pion.tech.pionbase.language.presentation.model.LanguageUIModel
import pion.tech.pionbase.util.setPreventDoubleClick

class LanguageAdapter : BaseListAdapter<LanguageUIModel, ItemLanguageBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem.localeCode == newItem.localeCode },
        areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {

    interface Listener {
        fun onClickLanguage(item: LanguageUIModel, position: Int)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getLayoutRes(viewType: Int): Int = R.layout.item_language
    override fun bindView(binding: ItemLanguageBinding, item: LanguageUIModel, position: Int) {
        fun loadBackground() {
            if (item.isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_language_selected)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_language_unselected)
            }
        }

        loadBackground()
        binding.tvName.text = item.nameCountry
        binding.ivFlag.loadImage(item.thumbnail)
        binding.root.setPreventDoubleClick {
            listener?.onClickLanguage(item, position)
        }
    }
}
