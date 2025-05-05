package com.piontech.core.navigator

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions

class NavigatorImpl(
    private val navController: NavController,
    private val lifecycle: Lifecycle,
    private val currentDestinationId: Int
) : Navigator {

    private var navObserver: LifecycleEventObserver? = null

    private fun isAtCurrentDestination(): Boolean {
        return navController.currentDestination?.id == currentDestinationId
    }

    private fun safeNav(actionId: Int, bundle: Bundle? = null, navOptions: NavOptions? = null) {
        if (!isAtCurrentDestination()) return
        runCatching {
            navObserver = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_RESUME) {
                        lifecycle.removeObserver(this)
                        runCatching {
                            if (navController.currentDestination?.id == currentDestinationId) {
                                navController.navigate(actionId, bundle, navOptions)
                            }
                        }
                    }
                }
            }
            lifecycle.addObserver(navObserver!!)

            navController.addOnDestinationChangedListener(object :
                NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    if (destination.id != currentDestinationId) {
                        navController.removeOnDestinationChangedListener(this)
                        lifecycle.removeObserver(navObserver as LifecycleEventObserver)
                    }
                }
            })

            if (navController.currentDestination?.id == currentDestinationId) {
                navController.navigate(actionId, bundle, navOptions)
            }
        }
    }

    override fun navigateTo(actionId: Int, bundle: Bundle?) {
        safeNav(actionId, bundle)
    }

    override fun navigateTo(actionId: Int, bundle: Bundle?, enterAnim: Int, exitAnim: Int) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .build()
        safeNav(actionId, bundle, navOptions)
    }

    override fun navigateTo(actionId: Int, bundle: Bundle?, clearBackStack: Boolean) {
        val navOptions = if (clearBackStack) {
            NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, true)
                .build()
        } else null
        safeNav(actionId, bundle, navOptions)
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun addOnDestinationChangedListener(listener: (NavController, NavDestination?, Bundle?) -> Unit) {
        navController.addOnDestinationChangedListener(listener)
    }

    override fun isCameFrom(destinationId: Int): Boolean {
        return navController.previousBackStackEntry?.destination?.id == destinationId
    }

    override fun popBackStack(destinationId: Int, inclusive: Boolean): Boolean {
        return navController.popBackStack(destinationId, inclusive)
    }
}
