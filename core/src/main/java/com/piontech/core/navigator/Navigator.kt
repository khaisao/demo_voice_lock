package com.piontech.core.navigator

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination

interface Navigator {
    /**
     * Navigate to a specific route with optional arguments
     */
    fun navigateTo(actionId: Int, bundle: Bundle? = null)

    /**
     * Navigate to a specific route with animation
     */
    fun navigateTo(actionId: Int, bundle: Bundle? = null, enterAnim: Int, exitAnim: Int)

    /**
     * Navigate to a specific route and clear back stack
     */
    fun navigateTo(actionId: Int, bundle: Bundle? = null, clearBackStack: Boolean = false)

    /**
     * Navigate back to previous screen
     */
    fun navigateUp()

    /**
     * Add listener for navigation events
     */
    fun addOnDestinationChangedListener(listener: (NavController, NavDestination?, Bundle?) -> Unit)

    /**
     * Check if current screen was navigated from specific destination
     */
    fun isCameFrom(destinationId: Int): Boolean

    /**
     * Pop back stack to specific destination
     */
    fun popBackStack(destinationId: Int, inclusive: Boolean): Boolean
}
