package com.diachuk.modernarchitecture.features.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.diachuk.modernarchitecture.features.auth.api.LoginDestination
import com.diachuk.modernarchitecture.features.auth.api.RegisterDestination
import com.diachuk.modernarchitecture.features.auth.logic.LoginViewModel
import com.diachuk.modernarchitecture.features.auth.logic.RegisterViewModel
import com.diachuk.modernarchitecture.features.auth.ui.LoginScreen
import com.diachuk.modernarchitecture.features.auth.ui.RegisterScreen
import com.diachuk.modernarchitecture.navigaion.Destination
import com.diachuk.modernarchitecture.navigaion.ScreenInjector
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.Single


@Single
class AuthScreenInjector : ScreenInjector {
    override fun injectInto(scope: EntryProviderScope<Destination>) {
        scope.entry<LoginDestination> {
            LoginScreen(koinViewModel<LoginViewModel>())
        }
        scope.entry<RegisterDestination> {
            RegisterScreen(koinViewModel<RegisterViewModel>())
        }
    }
}