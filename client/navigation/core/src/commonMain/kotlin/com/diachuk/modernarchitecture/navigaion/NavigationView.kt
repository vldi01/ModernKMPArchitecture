package com.diachuk.modernarchitecture.navigaion

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinInternalApi

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No navigator provided") }


@OptIn(KoinInternalApi::class)
@Composable
fun NavigationView(
    modifier: Modifier = Modifier
) {
    val navigator = koinInject<Navigator>()
    val screenInjectors = koinGetAll<ScreenInjector>()

    val fastEasing = FastOutLinearInEasing
    val slowEasing = LinearOutSlowInEasing
    val duration = 300

    CompositionLocalProvider(LocalNavigator provides navigator) {
        NavDisplay(
            modifier = modifier,
            backStack = navigator.backStack.collectAsState().value,
            onBack = { navigator.popBack() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                screenInjectors.forEach {
                    it.injectInto(this)
                }
            },
            transitionSpec = {
                // FORWARD: Shared X-Axis
                // Entering screen: slides in from right, scales up, fades in
                val enter = slideInHorizontally(
                    initialOffsetX = { it / 3 }, // Start 33% to the right
                    animationSpec = tween(duration, easing = slowEasing)
                ) + fadeIn(
                    animationSpec = tween(duration, easing = slowEasing)
                ) + scaleIn(
                    initialScale = 0.95f, // Start slightly small
                    animationSpec = tween(duration, easing = slowEasing)
                )

                // Exiting screen: slides out to left, scales down, fades out
                val exit = slideOutHorizontally(
                    targetOffsetX = { -it / 3 }, // Exit 33% to the left
                    animationSpec = tween(duration, easing = fastEasing)
                ) + fadeOut(
                    animationSpec = tween(duration, easing = fastEasing)
                ) + scaleOut(
                    targetScale = 0.95f, // End slightly small
                    animationSpec = tween(duration, easing = fastEasing)
                )

                enter togetherWith exit
            },
            popTransitionSpec = {
                // BACK: Shared X-Axis (Reversed)
                // Entering screen: slides in from left, scales up, fades in
                val enter = slideInHorizontally(
                    initialOffsetX = { -it / 3 }, // Start 33% to the left
                    animationSpec = tween(duration, easing = slowEasing)
                ) + fadeIn(
                    animationSpec = tween(duration, easing = slowEasing)
                ) + scaleIn(
                    initialScale = 0.95f, // Start slightly small
                    animationSpec = tween(duration, easing = slowEasing)
                )

                // Exiting screen: slides out to right, scales down, fades out
                val exit = slideOutHorizontally(
                    targetOffsetX = { it / 3 }, // Exit 33% to the right
                    animationSpec = tween(duration, easing = fastEasing)
                ) + fadeOut(
                    animationSpec = tween(duration, easing = fastEasing)
                ) + scaleOut(
                    targetScale = 0.95f, // End slightly small
                    animationSpec = tween(duration, easing = fastEasing)
                )

                enter togetherWith exit
            },
            predictivePopTransitionSpec = {
                // BACK: Shared X-Axis (Reversed)
                val enter = slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(duration, easing = slowEasing)
                ) + fadeIn(
                    animationSpec = tween(duration, easing = slowEasing)
                ) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(duration, easing = slowEasing)
                )

                val exit = slideOutHorizontally(
                    targetOffsetX = { it / 3 },
                    animationSpec = tween(duration / 2, easing = fastEasing)
                ) + fadeOut(
                    animationSpec = tween(duration / 2, easing = fastEasing)
                ) + scaleOut(
                    targetScale = 0.95f,
                    animationSpec = tween(duration / 2, easing = fastEasing)
                )

                enter togetherWith exit
            },
        )
    }
}