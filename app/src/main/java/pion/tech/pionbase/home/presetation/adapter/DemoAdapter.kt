package pion.tech.pionbase.home.presetation.adapter

import com.piontech.core.base.BaseListAdapter
import com.piontech.core.base.createDiffCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemDummyBinding

class DemoAdapter : BaseListAdapter<String, ItemDummyBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
        areContentsTheSame = { oldItem, newItem -> true }
    )
) {
    override fun getLayoutRes(viewType: Int): Int = R.layout.item_dummy

    override fun bindView(binding: ItemDummyBinding, item: String, position: Int) {
        binding.name.text = item
    }

}
