package com.piontech.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.piontech.core.firebaseAnalytics.FirebaseAnalyticsLogger
import com.piontech.core.navigator.Navigator
import com.piontech.core.navigator.NavigatorImpl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Inflate<Binding> = (LayoutInflater, ViewGroup?, Boolean) -> Binding


abstract class BaseFragment<Binding : ViewBinding, VM : ViewModel, CommonVM : ViewModel>(
    private val inflate: Inflate<Binding>,
    private val viewModelClass: Class<VM>,
    private val commonViewModelClass: Class<CommonVM>
) : Fragment() {

    @Inject
    lateinit var logger: FirebaseAnalyticsLogger

    private var _navigator: Navigator? = null
    val navigator: Navigator
        get() = checkNotNull(_navigator) {
            "Fragment $this navigator cannot be accessed before onCreateView() or after onDestroyView()"
        }

    private var _binding: Binding? = null

    val binding: Binding
        get() = checkNotNull(_binding) {
            "Fragment $this binding cannot be accessed before onCreateView() or after onDestroyView()"
        }

    val commonViewModel: CommonVM by lazy {
        ViewModelProvider(requireActivity())[commonViewModelClass]
    }

    val viewModel: VM by lazy {
        ViewModelProvider(this)[viewModelClass]
    }

    private var isInit = false
    private var saveView = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (saveView) {
            if (_binding == null) {
                isInit = true
                _binding = inflate.invoke(inflater, container, false)
            } else {
                isInit = false
            }
        } else {
            _binding = inflate.invoke(inflater, container, false)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentDestinationId = findNavController().currentDestination?.id ?: 0
        _navigator = NavigatorImpl(findNavController(), lifecycle, currentDestinationId)
        _navigator?.addOnDestinationChangedListener(listener = { controller: NavController, destination: NavDestination?, bundle: Bundle? ->
            showHideLoading(false)
        })
        init(view)
        subscribeObserver(view)
    }

    abstract fun init(view: View)

    abstract fun subscribeObserver(view: View)

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun showHideLoading(isShow: Boolean) {
        if (isShow) {
            LoadingDialog.getInstance().show(childFragmentManager)
        } else {
            LoadingDialog.getInstance().dismiss()
        }
    }

    fun onSystemBack(action: () -> Unit) {
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            action.invoke()
        }
    }

    companion object {
        private const val TAG = "BaseFragment"
    }
}

fun Fragment.doActionWhenResume(action: () -> Unit) {
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_RESUME) {
                action.invoke()
                lifecycle.removeObserver(this)
            }
        }
    })
}

fun Fragment.launchIO(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("${this::class.java.simpleName} error: $throwable")
        onError(throwable)
    }
    return lifecycleScope.launch(Dispatchers.IO + exceptionHandler, block = block)
}

fun Fragment.launchDefault(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("${this::class.java.simpleName} error: $throwable")
        onError(throwable)
    }
    return lifecycleScope.launch(Dispatchers.Default + exceptionHandler, block = block)
}

fun Fragment.launchMain(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("${this::class.java.simpleName} error: $throwable")
        onError(throwable)
    }
    return lifecycleScope.launch(Dispatchers.Main + exceptionHandler, block = block)
}
