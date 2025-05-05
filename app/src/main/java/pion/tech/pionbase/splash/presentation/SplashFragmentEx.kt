package pion.tech.pionbase.splash.presentation

import android.animation.ValueAnimator
import pion.tech.pionbase.R


fun SplashFragment.backEvent() {
    onSystemBack {
        onBackPressed()
    }
//    binding.btnBack.setPreventDoubleClickScaleView {
//        onBackPressed()
//    }
}

fun SplashFragment.onBackPressed() {
//    findNavController().popBackStack()
}

fun SplashFragment.releaseAnimation() {
    progressAnimator?.cancel()
    progressAnimator = null
}

fun SplashFragment.startAnimation() {
    binding.progressBar.apply {
        isIndeterminate = false
        progress = 0
    }

    progressAnimator = ValueAnimator.ofInt(0, 100).apply {
        duration = 15000

        addUpdateListener { animation ->
            runCatching {
                binding.progressBar.progress = animation.animatedValue as Int
            }
        }
        start()
    }
}

fun SplashFragment.goToLanguageScreen() {
    navigator.navigateTo(R.id.action_splashFragment_to_languageFragment)
}