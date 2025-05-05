package pion.tech.pionbase.onboard.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pion.tech.pionbase.databinding.LayoutIapBinding
import pion.tech.pionbase.databinding.PagerOnboard1Binding
import pion.tech.pionbase.databinding.PagerOnboard2Binding
import pion.tech.pionbase.databinding.PagerOnboard3Binding
import pion.tech.pionbase.databinding.PagerOnboard4Binding
import pion.tech.pionbase.onboard.presentation.OnboardFragment
import pion.tech.pionbase.onboard.presentation.viewpager.OnboardViewHolder1
import pion.tech.pionbase.onboard.presentation.viewpager.OnboardViewHolder2
import pion.tech.pionbase.onboard.presentation.viewpager.OnboardViewHolder3
import pion.tech.pionbase.onboard.presentation.viewpager.OnboardViewHolder4
import pion.tech.pionbase.onboard.presentation.viewpager.OnboardViewHolder5
import java.lang.ref.WeakReference

class OnboardAdapter : RecyclerView.Adapter<OnboardAdapter.OnboardViewHolder>() {

    private val listClassName = mutableListOf<String>()

    init {
        listClassName.add(OnboardViewHolder2::class.java.name)
//        if (AdsConstant.listConfigAds["onboardfull1.1"]?.isOn == true) listClassName.add(
//            OnboardViewHolder3::class.java.name
//        )
        listClassName.add(OnboardViewHolder3::class.java.name)
        listClassName.add(OnboardViewHolder4::class.java.name)
        listClassName.add(OnboardViewHolder5::class.java.name)
    }

    interface Listener {
        fun onDoneOnboard()
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun getListener() = listener

    private var onboardFragment: WeakReference<OnboardFragment>? = null

    fun setOnboardFragment(onboardFragment: OnboardFragment) {
        this.onboardFragment = WeakReference(onboardFragment)
    }

    abstract class OnboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (listClassName[viewType]) {
            OnboardViewHolder1::class.java.name -> {
                val binding = PagerOnboard1Binding.inflate(inflater, parent, false)
                OnboardViewHolder1(binding, this, onboardFragment?.get())
            }

            OnboardViewHolder2::class.java.name -> {
                val binding = PagerOnboard2Binding.inflate(inflater, parent, false)
                OnboardViewHolder2(binding, this, onboardFragment?.get())
            }

            OnboardViewHolder3::class.java.name -> {
                val binding = PagerOnboard3Binding.inflate(inflater, parent, false)
                OnboardViewHolder3(binding, this, onboardFragment?.get())
            }

            OnboardViewHolder4::class.java.name -> {
                val binding = PagerOnboard4Binding.inflate(inflater, parent, false)
                OnboardViewHolder4(binding, this, onboardFragment?.get())
            }

            else -> {
                val binding = LayoutIapBinding.inflate(inflater, parent, false)
                OnboardViewHolder5(binding, this, onboardFragment?.get())
            }
        }
    }

    override fun getItemCount(): Int = listClassName.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: OnboardViewHolder, position: Int) {
        holder.bind()
    }
}